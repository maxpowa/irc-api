package com.ircclouds.irc.api;

import com.ircclouds.irc.api.commands.interfaces.ICommand;
import com.ircclouds.irc.api.comms.IConnection;
import com.ircclouds.irc.api.comms.SSLSocketChannelConnection;
import com.ircclouds.irc.api.comms.SocketChannelConnection;
import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.domain.IRCServerOptions;
import com.ircclouds.irc.api.domain.SecureIRCServer;
import com.ircclouds.irc.api.domain.messages.ClientErrorMessage;
import com.ircclouds.irc.api.interfaces.Callback;
import com.ircclouds.irc.api.interfaces.ICommandServer;
import com.ircclouds.irc.api.interfaces.IIRCSession;
import com.ircclouds.irc.api.interfaces.IMessageReader;
import com.ircclouds.irc.api.state.IIRCState;

import net.engio.mbassy.bus.MBassador;

import java.io.IOException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

public abstract class AbstractIRCSession implements IIRCSession
{
	private final MBassador eventBus = new MBassador();

	private final ICommandServer cmdServ;
	private final IMessageReader reader;
	private final AbstractApiDaemon daemon;
	private IConnection conn;
	private Callback<IIRCState> callback;

	public AbstractIRCSession()
	{
		cmdServ = new AbstractCommandServer()
		{
			@Override
			public IConnection getConnection()
			{
				return conn;
			}
		};
		
		reader = new AbstractMessageReader()
		{
			@Override
			protected IRCServerOptions getIRCServerOptions()
			{
				return AbstractIRCSession.this.getIRCServerOptions();
			}

			@Override
			public IConnection getConnection()
			{
				return conn;
			}
		};
		
		daemon = new AbstractApiDaemon(reader, eventBus)
		{
			@Override
			protected void onExit()
			{
				try
				{
					close();
				}
				catch (IOException aExc)
				{
					throw new RuntimeException(aExc);
				}
			}

			@Override
			protected void signalExceptionToApi(Exception aExc)
			{
				callback.onFailure(aExc);
			}
		};
	}

	public void execute(ICommand aCommand) throws IOException
	{
		cmdServ.execute(aCommand);
	}

	@Override
	public void register(Object... aListeners)
	{
		for (Object _listener : aListeners)
		{
			eventBus.subscribe(_listener);
		}
	}
	
	@Override
	public ICommandServer getCommandServer()
	{
		return cmdServ;
	}

	@Override
	public MBassador getEventBus() {
		return eventBus;
	}

	@Override
	public void unregister(Object aListener)
	{
		eventBus.unsubscribe(aListener);
	}

	@Override
	public boolean open(IRCServer aServer, Callback<IIRCState> aCallback) throws IOException
	{
		callback = aCallback;
		
		if (!aServer.isSSL())
		{
			conn = new SocketChannelConnection();
		}
		else
		{
			conn = new SSLSocketChannelConnection();
		}
		
		SSLContext _ctx = null;
		if (aServer instanceof SecureIRCServer)
		{
			_ctx = ((SecureIRCServer) aServer).getSSLContext();
		}
		
		if (conn.open(aServer.getHostname(), aServer.getPort(), _ctx, aServer.getProxy(), aServer.isResolveByProxy()))
		{
			if (!daemon.isAlive())
			{
				daemon.start();	
			}
			
			return true;
		}

		return false;
	}

	@Override
	public void close() throws IOException
	{
		conn.close();
		
		reader.reset();
	}

	protected abstract IRCServerOptions getIRCServerOptions();

	@Override
	public void dispatchClientError(final Exception e)
	{
		final MBassador currentDispatcher = this.eventBus;
		new Thread()
		{

			@Override
			public void run()
			{
				final ClientErrorMessage errorMsg = new ClientErrorMessage(e);
				currentDispatcher.post(errorMsg).asynchronously();
			}
		}.start();
	}
	
	@Override
	public void secureConnection(final SSLContext aContext, final String aHostname, final int aPort) throws SSLException
	{
		if (!(this.conn instanceof SocketChannelConnection))
		{
			throw new IllegalArgumentException("unsupported connection type in use");
		}
		this.conn = new SSLSocketChannelConnection((SocketChannelConnection) this.conn, aContext, aHostname, aPort);
	}
}

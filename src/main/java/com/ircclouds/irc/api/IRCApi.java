package com.ircclouds.irc.api;

import com.ircclouds.irc.api.commands.*;
import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.commands.interfaces.ICommand;
import com.ircclouds.irc.api.dcc.DCCManager;
import com.ircclouds.irc.api.dcc.interfaces.IDCCManager;
import com.ircclouds.irc.api.dcc.interfaces.IDCCReceiveCallback;
import com.ircclouds.irc.api.dcc.interfaces.IDCCSendCallback;
import com.ircclouds.irc.api.domain.IRCChannel;
import com.ircclouds.irc.api.domain.IRCServerOptions;
import com.ircclouds.irc.api.domain.WritableIRCChannel;
import com.ircclouds.irc.api.interfaces.*;
import com.ircclouds.irc.api.listeners.AbstractExecuteCommandListener;
import com.ircclouds.irc.api.listeners.ExecuteCommandListenerImpl;
import com.ircclouds.irc.api.listeners.PingVersionListenerImpl;
import com.ircclouds.irc.api.negotiators.CapabilityNegotiator;
import com.ircclouds.irc.api.state.*;
import com.ircclouds.irc.api.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.ircclouds.irc.api.dcc.DCCManager.DCC_SEND_TIMEOUT;

/**
 * The main implementation of {@link IIRCApi}. It offers the ability to save the
 * IRC state, and allows for extensibility through {@link #getCommandServer()}.
 * 
 * This implementation provides logging via slf4j.
 * 
 * @author miguel@lebane.se
 * 
 */
public class IRCApi implements IIRCApi
{
	private static final Logger LOG = LoggerFactory.getLogger(IRCApi.class);

	private final IIRCSession session;
	private final AbstractExecuteCommandListener executeCmdListener;
	private IIRCState state;
	private int asyncId = 0;

	private final DCCManager dccManager;

	/**
	 * 
	 * @param aSaveIRCState
	 *            A flag to allow saving the IRC state that will be obtained by
	 *            {@link #connect(IServerParameters, Callback)}
	 */
	public IRCApi(Boolean aSaveIRCState)
	{
		state = new DisconnectedIRCState();
		session = new AbstractIRCSession()
		{
			@Override
			protected IRCServerOptions getIRCServerOptions()
			{
				return state.getServerOptions();
			}
		};

		session.register(
				executeCmdListener = new ExecuteCommandListenerImpl(session, getStateUpdater(aSaveIRCState)),
				new PingVersionListenerImpl(session)
		);

		dccManager = new DCCManager(this);
	}

	@Override
	public void connect(final IServerParameters aServerParameters, final Callback<IIRCState> aCallback)
	{
		connect(aServerParameters, aCallback, null);
	}

	@Override
	public void connect(final IServerParameters aServerParameters, final Callback<IIRCState> aCallback, final CapabilityNegotiator negotiator)
	{
		if (state.isConnected())
		{
			aCallback.onFailure(new ApiException("Already connected!"));
			return;
		}

		Dirty _d = new Dirty();
		Callback<IIRCState> _connectCallback = newConnectCallback(aCallback, _d);
		executeCmdListener.submitConnectCallback(_connectCallback, aServerParameters);

		boolean _isOpen = false;
		try
		{
			if (_isOpen = session.open(aServerParameters.getServer(), _connectCallback))
			{
				final ICapCmd initCmd;
				if (negotiator == null)
				{
					initCmd = null;
				}
				else
				{
					session.register(negotiator);
					initCmd = negotiator.initiate(this);
				}
				executeAsync(new ConnectCmd(aServerParameters, initCmd), aCallback, _d);
			}
			else
			{
				aCallback.onFailure(new ApiException("Failed to open connection to [" + aServerParameters.getServer().toString() + "]"));
			}
		}
		catch (IOException aExc)
		{
			_isOpen = true;
			LOG.error("Error opening session", aExc);

			tryInvokeCallback(aCallback, _d, aExc);
		}
		finally
		{
			if (!_isOpen)
			{
				closeSession(aCallback, _d);
			}
		}
	}

	@Override
	public void disconnect(String aQuitMessage)
	{
		checkConnected();

		execute(new QuitCmd(aQuitMessage));
	}

	@Override
	public void disconnect()
	{
		checkConnected();

		execute(new QuitCmd());
	}

	@Override
	public void joinChannel(String aChannelName)
	{
		joinChannel(aChannelName, "");
	}

	@Override
	public void joinChannel(String aChannelName, Callback<IRCChannel> aCallback)
	{
		joinChannel(aChannelName, "", aCallback);
	}

	@Override
	public void joinChannel(String aChannelName, String aKey)
	{
		checkConnected();

		execute(new JoinChanCmd(prependChanType(aChannelName), aKey));
	}

	@Override
	public void joinChannel(String aChannelName, String aKey, final Callback<IRCChannel> aCallback)
	{
		if (!state.isConnected())
		{
			aCallback.onFailure(new ApiException("Not connected!"));
			return;
		}

		aChannelName = prependChanType(aChannelName).toLowerCase();

		Dirty _d = new Dirty();
		executeCmdListener.submitJoinChannelCallback(aChannelName, getDirtyCallback(aCallback, _d));
		executeAsync(new JoinChanCmd(aChannelName, aKey), aCallback, _d);
	}

	@Override
	public void leaveChannel(String aChannelName)
	{
		leaveChannel(aChannelName, "");
	}

	@Override
	public void leaveChannel(String aChannelName, Callback<String> aCallback)
	{
		leaveChannel(aChannelName, "", aCallback);
	}

	@Override
	public void leaveChannel(String aChannelName, String aPartMessage)
	{
		checkConnected();

		execute(new PartChanCmd(aChannelName, aPartMessage));
	}

	@Override
	public void leaveChannel(String aChannelName, String aPartMessage, Callback<String> aCallback)
	{
		if (!state.isConnected())
		{
			aCallback.onFailure(new ApiException("Not connected!"));
			return;
		}

		aChannelName = prependChanType(aChannelName).toLowerCase();

		Dirty _d = new Dirty();
		executeCmdListener.submitPartChannelCallback(aChannelName, getDirtyCallback(aCallback, _d));
		executeAsync(new PartChanCmd(aChannelName, aPartMessage), aCallback, _d);
	}

	@Override
	public void message(String aTarget, String aMessage)
	{
		checkConnected();

		execute(new SendPrivateMessage(aTarget, aMessage));
	}

	@Override
	public void message(String aTarget, String aMessage, Callback<String> aCallback)
	{
		if (!state.isConnected())
		{
			aCallback.onFailure(new ApiException("Not connected!"));
			return;
		}

		Dirty _d = new Dirty();
		executeCmdListener.submitSendMessageCallback(asyncId, getDirtyCallback(aCallback, _d));

		executeAsync(new SendPrivateMessage(aTarget, aMessage, asyncId++), aCallback, _d);
	}

	@Override
	public void act(String aTarget, String aActionMessage)
	{
		checkConnected();

		execute(new SendActionMessage(aTarget, aActionMessage));
	}

	@Override
	public void act(String aTarget, String aActionMessage, Callback<String> aCallback)
	{
		if (!state.isConnected())
		{
			aCallback.onFailure(new ApiException("Not connected!"));
			return;
		}

		Dirty _d = new Dirty();
		executeCmdListener.submitSendMessageCallback(asyncId, getDirtyCallback(aCallback, _d));

		executeAsync(new SendActionMessage(aTarget, aActionMessage, asyncId++), aCallback, _d);
	}

	@Override
	public void notice(String aTarget, String aText)
	{
		checkConnected();

		execute(new SendNoticeMessage(aTarget, aText));
	}

	@Override
	public void notice(String aTarget, String aText, Callback<String> aCallback)
	{
		if (!state.isConnected())
		{
			aCallback.onFailure(new ApiException("Not connected!"));
			return;
		}

		Dirty _d = new Dirty();
		executeCmdListener.submitSendMessageCallback(asyncId, getDirtyCallback(aCallback, _d));

		executeAsync(new SendNoticeMessage(aTarget, aText, asyncId++), aCallback, _d);
	}

	@Override
	public void kick(String aChannel, String aNick)
	{
		kick(aChannel, aNick, "");
	}

	@Override
	public void kick(String aChannel, String aNick, Callback<String> aCallback)
	{
		kick(aChannel, aNick, "", aCallback);
	}

	@Override
	public void kick(String aChannel, String aNick, String aKickMessage)
	{
		checkConnected();

		execute(new KickUserCmd(aChannel, aNick, aKickMessage));
	}

	@Override
	public void kick(String aChannel, String aNick, String aKickMessage, Callback<String> aCallback)
	{
		if (!state.isConnected())
		{
			aCallback.onFailure(new ApiException("Not connected!"));
			return;
		}

		Dirty _d = new Dirty();
		executeCmdListener.submitKickUserCallback(aChannel, getDirtyCallback(aCallback, _d));

		executeAsync(new KickUserCmd(aChannel, aNick, aKickMessage), aCallback, _d);
	}

	@Override
	public void changeNick(String aNewNick)
	{
		checkConnected();

		execute(new ChangeNickCmd(aNewNick));
	}

	@Override
	public void changeNick(String aNewNickname, final Callback<String> aCallback)
	{
		if (!state.isConnected())
		{
			aCallback.onFailure(new ApiException("Not connected!"));
			return;
		}

		Dirty _d = new Dirty();
		executeCmdListener.submitChangeNickCallback(aNewNickname, getDirtyCallback(aCallback, _d));

		executeAsync(new ChangeNickCmd(aNewNickname), aCallback, _d);
	}

	@Override
	public void changeTopic(final String aChannel, final String aSuggestedTopic)
	{
		checkConnected();

		execute(new ChangeTopicCmd(aChannel, aSuggestedTopic));
	}

	@Override
	public void changeMode(String aModeString)
	{
		checkConnected();

		execute(new ChangeModeCmd(aModeString));
	}

	@Override
	public void rawMessage(String aMessage)
	{
		execute(new SendRawMessage(aMessage));
	}

	@Override
	public void rawMessage(ICommand aMessage) {
		execute(new SendRawMessage(aMessage.toString()));
	}

	@Override
	public void register(Object aListener)
	{
		session.register(aListener);
	}

	@Override
	public void unregister(Object aListener)
	{
		session.unregister(aListener);
	}

	@Override
	public void dccSend(final String aNick, final File aFile, IDCCSendCallback aCallback) throws UnknownHostException
	{
		dccSend(aNick, aFile, NetUtils.getLocalAddressRepresentation(), NetUtils.getRandDccPort(), DCC_SEND_TIMEOUT, aCallback);
	}

	@Override
	public void dccSend(String aNick, String aListeningAddress, Integer aListeningPort, File aFile, IDCCSendCallback aCallback)
	{
		dccSend(aNick, aFile, aListeningAddress, aListeningPort, DCC_SEND_TIMEOUT, aCallback);
	}

	@Override
	public void dccSend(String aNick, File aFile, Integer aTimeout, IDCCSendCallback aCallback)
	{
		dccSend(aNick, aFile, aTimeout, aCallback);
	}

	@Override
	public void dccSend(String aNick, File aFile, String aListeningAddress, Integer aListeningPort, Integer aTimeout, IDCCSendCallback aCallback)
	{
		dccManager.dccSend(aNick, aFile, aListeningAddress, aListeningPort, aTimeout, aCallback);
	}

	@Override
	public void dccAccept(String aNick, File aFile, Integer aPort, Long aResumePosition, IDCCSendCallback aCallback)
	{
		dccAccept(aNick, aFile, aPort, aResumePosition, DCC_SEND_TIMEOUT, aCallback);
	}

	@Override
	public void dccAccept(String aNick, File aFile, Integer aPort, Long aResumePosition, Integer aTimeout, IDCCSendCallback aCallback)
	{
		dccManager.dccAccept(aNick, aFile, aPort, aResumePosition, aTimeout, aCallback);
	}

	@Override
	public void dccReceive(File aFile, Long aSize, SocketAddress aAddress, IDCCReceiveCallback aCallback)
	{
		dccResume(aFile, 0L, aSize, aAddress, aCallback);
	}

	@Override
	public void dccReceive(File aFile, Long aSize, SocketAddress aAddress, IDCCReceiveCallback aCallback, Proxy aProxy)
	{
		dccResume(aFile, 0L, aSize, aAddress, aCallback, aProxy);
	}

	@Override
	public void dccResume(File aFile, Long aResumePosition, Long aSize, SocketAddress aAddress, IDCCReceiveCallback aCallback)
	{
		dccManager.dccResume(aFile, aResumePosition, aSize, aAddress, aCallback);
	}

	@Override
	public void dccResume(File aFile, Long aResumePosition, Long aSize, SocketAddress aAddress, IDCCReceiveCallback aCallback, Proxy aProxy)
	{
		dccManager.dccResume(aFile, aResumePosition, aSize, aAddress, aCallback, aProxy);
	}

	@Override
	public IDCCManager getDCCManager()
	{
		return dccManager;
	}

	/**
	 * Returns the interface responsible for executing IRC commands
	 * 
	 * @return {@link ICommandServer}
	 */
	protected ICommandServer getCommandServer()
	{
		return session.getCommandServer();
	}

	private String prependChanType(String aChannelName)
	{
		for (Character _c : state.getServerOptions().getChanTypes())
		{
			if (_c.equals(aChannelName.charAt(0)))
			{
				return aChannelName;
			}
		}

		return state.getServerOptions().getChanTypes().iterator().next() + aChannelName;
	}

	private void closeSession(Callback<IIRCState> aCallback, Dirty _d)
	{
		try
		{
			session.close();
		}
		catch (IOException aExc)
		{
			tryInvokeCallback(aCallback, _d, aExc);
			LOG.error("Error Closing Session.", aExc);
		}
	}

	private void checkConnected()
	{
		if (!state.isConnected())
		{
			throw new ApiException("Not connected!");
		}
	}

	private Callback<IIRCState> newConnectCallback(final Callback<IIRCState> aCallback, final Dirty aDirty)
	{
		return new Callback<IIRCState>()
		{
			@Override
			public void onSuccess(IIRCState aConnectedState)
			{
				state = aConnectedState;

				((IRCState) (state)).setConnected(true);

				aCallback.onSuccess(aConnectedState);
			}

			@Override
			public void onFailure(Exception aExc)
			{
				tryInvokeCallback(aCallback, aDirty, aExc);
			}
		};
	}

	private void tryInvokeCallback(final Callback<?> aCallback, Dirty aDirty, Exception aExc)
	{
		synchronized (aDirty)
		{
			if (!aDirty.isDirty())
			{
				aCallback.onFailure(aExc);
				aDirty.setDirty();
			}
		}
	}

	private void execute(ICommand aCommand)
	{
		try
		{
			getCommandServer().execute(aCommand);
		}
		catch (SocketException aExc)
		{
			((IRCState) this.state).setConnected(false);
			dispatchError(aExc);
			throw new RuntimeException(aExc);
		}
		catch (IOException aExc)
		{
			LOG.error("Error executing command", aExc);
			throw new RuntimeException(aExc);
		}
	}

	private IStateAccessor getStateUpdater(Boolean aSaveIRCState)
	{
		if (aSaveIRCState)
		{
			AbstractIRCStateUpdater _stateUpdater = new AbstractIRCStateUpdater()
			{
				@Override
				public IIRCState getIRCState()
				{
					return state;
				}
			};

			session.register(_stateUpdater);

			return _stateUpdater;
		}
		else
		{
			return new IStateAccessor()
			{
				@Override
				public void saveChan(WritableIRCChannel aChannel)
				{
					// NOP
				}

				@Override
				public IIRCState getIRCState()
				{
					return state;
				}

				@Override
				public void deleteChan(String aChannelName)
				{
					// NOP
				}

				@Override
				public void updateNick(String aNewNick)
				{
					// NOP
				}

				@Override
				public void deleteNickFromChan(String aChan, String aNick)
				{
					// NOP
				}
			};
		}
	}

	private void executeAsync(ICommand aCommand, Callback<?> aCallback, Dirty aDirty)
	{
		try
		{
			getCommandServer().execute(aCommand);
		}
		catch (SocketException aExc)
		{
			((IRCState) this.state).setConnected(false);
			dispatchError(aExc);
			tryInvokeCallback(aCallback, aDirty, aExc);
		}
		catch (IOException aExc)
		{
			LOG.error("Error executing command", aExc);

			tryInvokeCallback(aCallback, aDirty, aExc);
		}
	}

	private void dispatchError(final Exception aExc)
	{
		LOG.debug("Connectivity error: socket exception are non-recoverable, dispatching error message.", aExc);
		this.session.dispatchClientError(aExc);
	}

	private <R> Callback<R> getDirtyCallback(final Callback<R> aCallback, final Dirty aDirty)
	{
		return new Callback<R>()
		{
			@Override
			public void onSuccess(R aObject)
			{
				aCallback.onSuccess(aObject);
			}

			@Override
			public void onFailure(Exception aExc)
			{
				LOG.info("", aExc);

				tryInvokeCallback(aCallback, aDirty, aExc);
			}
		};
	}

	private class Dirty
	{
		boolean dirty;

		void setDirty()
		{
			dirty = true;
		}

		boolean isDirty()
		{
			return dirty == true;
		}
	}
}

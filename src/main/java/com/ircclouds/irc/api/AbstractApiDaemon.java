package com.ircclouds.irc.api;

import com.ircclouds.irc.api.comms.IConnection.EndOfStreamException;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ClientErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractApiDaemon extends Thread
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractApiDaemon.class);

	private final IMessageReader reader;
	private final IMessageDispatcher dispatcher;

	public AbstractApiDaemon(IMessageReader aReader, IMessageDispatcher aDispatcher)
	{
		super("ApiDaemon");

		reader = aReader;
		dispatcher = aDispatcher;
	}

	@Override
	public void run()
	{
		try
		{
			while (reader.available())
			{
				AbstractMessage _msg = reader.readMessage();
				if (_msg != null)
				{
					dispatcher.dispatchToPrivateListeners(_msg);
					dispatcher.dispatch(_msg);
				}
			}
		}
		catch (EndOfStreamException aExc)
		{
			LOG.debug("Received end of stream, closing connection", aExc);
			// Signaling the exception to the api is necessary, since there is a
			// chance that the connection is abruptly cut off before connection
			// (or rather IRC registration process) is finished. Otherwise,
			// there would be no feedback of the connection failure.
			signalExceptionToApi(aExc);
			dispatcher.dispatch(new ClientErrorMessage(aExc));
		}
		catch (IOException aExc)
		{
			LOG.error(this.getName(), aExc);
			signalExceptionToApi(aExc);
			dispatcher.dispatch(new ClientErrorMessage(aExc));
		}
		finally
		{
			LOG.debug("ApiDaemon Exiting..");

			onExit();
		}
	}

	protected abstract void signalExceptionToApi(Exception aExc);

	protected abstract void onExit();
}

package com.ircclouds.irc.api;

import com.ircclouds.irc.api.comms.IConnection.EndOfStreamException;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ClientErrorMessage;
import com.ircclouds.irc.api.interfaces.IMessageReader;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractApiDaemon extends Thread
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractApiDaemon.class);

	private final IMessageReader reader;
	private final MBassador eventBus;

	public AbstractApiDaemon(IMessageReader aReader, MBassador aDispatcher)
	{
		super("IRCApiDaemon");

		reader = aReader;
		eventBus = aDispatcher;
	}

	@Override
	public void run() {
		try {
			while (reader.available()) {
				AbstractMessage _msg = reader.readMessage();

				if (_msg != null) {
					LOG.trace("<< " + _msg.asRaw());
					eventBus.post(_msg).now();
				}
			}
		} catch (EndOfStreamException aExc) {
			LOG.debug("Received end of stream, closing connection", aExc);
			// Signaling the exception to the api is necessary, since there is a
			// chance that the connection is abruptly cut off before connection
			// (or rather IRC registration process) is finished. Otherwise,
			// there would be no feedback of the connection failure.
			signalExceptionToApi(aExc);
			eventBus.post(new ClientErrorMessage(aExc)).asynchronously();
		} catch (IOException aExc) {
			LOG.error(this.getName(), aExc);
			signalExceptionToApi(aExc);
			eventBus.post(new ClientErrorMessage(aExc)).asynchronously();
		} finally {
			LOG.debug("IRCApiDaemon Exiting..");

			onExit();
		}
	}

	protected abstract void signalExceptionToApi(Exception aExc);

	protected abstract void onExit();
}

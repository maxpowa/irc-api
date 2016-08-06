package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.messages.interfaces.IMessage;
import com.ircclouds.irc.api.domain.messages.interfaces.ISource;

import java.util.ArrayList;
import java.util.List;

/**
 * A client error message. The IRC server manages the clients. Therefore almost
 * every error will arrive via the IRC server. A clear exception to this case is
 * when the connection is interrupted unexpectedly. Because of connection
 * interruption, they will not receive any messages from the IRC server anymore.
 * For this use case listeners should be informed of this error as well.
 *
 * @author Danny van Heumen
 */
public class ClientErrorMessage implements IMessage
{
	private final Exception exception;

	public ClientErrorMessage(final Exception aExc)
	{
		this.exception = aExc;
	}

	public List<String> getParams() {
		return new ArrayList<String>();
	}

	/**
	 * This is a client error message, hence the source will be null.
	 *
	 * @return returns null
	 */
	@Override
	public ISource getSource()
	{
		return null;
	}

	@Override
	public String asRaw()
	{
		return this.exception.toString();
	}

	public Exception getException()
	{
		return this.exception;
	}
}

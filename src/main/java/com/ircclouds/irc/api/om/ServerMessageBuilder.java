package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public class ServerMessageBuilder implements IBuilder<ServerNumericMessage>
{
	public ServerNumericMessage build(Message aMessage)
	{
		return new ServerNumericMessage(getNumberFrom(aMessage.command), aMessage.params.get(0), aMessage.params.toArray(new String[0]), new IRCServer(aMessage.prefix));
	}

	private Integer getNumberFrom(String aString)
	{
		try
		{
			return Integer.parseInt(aString);
		}
		catch (NumberFormatException aExc)
		{
			return 0;
		}
	}

}

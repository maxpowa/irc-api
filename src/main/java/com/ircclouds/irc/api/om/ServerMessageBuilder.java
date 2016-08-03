package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class ServerMessageBuilder implements IBuilder<ServerNumeric>
{
	public ServerNumeric build(AbstractMessage aMessage)
	{
		return new ServerNumeric(aMessage);
	}

}

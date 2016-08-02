package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;

public class ServerMessageBuilder implements IBuilder<ServerNumeric>
{
	public ServerNumeric build(Message aMessage)
	{
		return new ServerNumeric(aMessage);
	}

}

package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class ServerPingMessageBuilder implements IBuilder<ServerPing>
{
	public ServerPing build(Message aMessage)
	{
        return new ServerPing(aMessage);
    }
}

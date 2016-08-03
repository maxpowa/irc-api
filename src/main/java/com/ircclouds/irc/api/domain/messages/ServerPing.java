package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ServerPing extends AbstractMessage
{
	public ServerPing(AbstractMessage message) {
		super(message);
	}

	@Override
	public IRCServer getSource()
	{
		return null;
	}
}

package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ServerNotice extends AbstractNotice
{
	public ServerNotice(AbstractMessage message)
	{
		super(message);
	}

	@Override
	public IRCServer getSource()
	{
		return (IRCServer)super.getSource();
	}
}
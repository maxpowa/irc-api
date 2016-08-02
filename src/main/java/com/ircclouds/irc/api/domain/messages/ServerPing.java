package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ServerPing extends Message implements IHasText, IServerMessage
{
	public ServerPing(Message message) {
		super(message);
	}

	@Override
	public IRCServer getSource()
	{
		return null;
	}

	@Override
	public String asRaw()
	{
		return new StringBuffer().append("PING :").append(this.getText()).toString();
	}
}

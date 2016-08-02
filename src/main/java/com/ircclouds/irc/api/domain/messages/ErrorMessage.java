package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ErrorMessage extends Message implements IServerMessage, IHasText {
	public ErrorMessage(Message message)
	{
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
		return new StringBuffer().append("ERROR :").append(this.getText()).toString();
	}
}

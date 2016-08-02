package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.messages.interfaces.*;

public abstract class AbstractNotice extends Message implements IHasText
{
	public AbstractNotice(Message message) {
		super(message);
	}

	public AbstractNotice(String aText)
	{
		super(aText);
	}
}

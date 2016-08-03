package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.utils.ParseUtils;

public class NickMessage extends AbstractUserMessage
{
	public NickMessage(AbstractMessage message) {
		super(message);
	}
	
	public String getNewNick()
	{
		return this.params.get(0);
	}
}

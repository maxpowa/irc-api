package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.ParseUtils;

public class NickMessage extends Message implements IUserMessage
{
	public NickMessage(Message message) {
		super(message);
	}
	
	public String getNewNick()
	{
		return this.getText();
	}

	@Override
	public IRCUser getSource()
	{
		return ParseUtils.getUser(this.prefix);
	}
	
	@Override
	public String asRaw()
	{
		return new StringBuffer(":").append(this.getSource()).append(" NICK :").append(this.getNewNick()).toString();
	}	
}

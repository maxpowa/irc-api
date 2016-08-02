package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.ParseUtils;

/**
 * 
 * @author
 * 
 */
public abstract class AbstractPrivMsg extends Message implements IUserMessage, IHasText
{
	public AbstractPrivMsg(String aText)
	{
		super(aText);
	}

	public AbstractPrivMsg(Message message) {
		super(message);
	}
	
	public IRCUser getSource()
	{
		return ParseUtils.getUser(this.prefix);
	}
}

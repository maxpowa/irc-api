package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.ParseUtils;

/**
 * 
 * @author
 * 
 */
public class UserNotice extends AbstractNotice implements IUserMessage
{
	public UserNotice(Message message) {
		super(message);
	}

	public IRCUser getSource()
	{
		return ParseUtils.getUser(this.prefix);
	}
	
	@Override
	public String asRaw()
	{
		return new StringBuffer().append(":").append(this.getSource()).append(" NOTICE ").append(":").append(this.getText()).toString();
	}
}

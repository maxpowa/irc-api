package com.ircclouds.irc.api.filters;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;

public class MessageFilterResult
{
	public static final MessageFilterResult HALT_MSG_RESULT = new MessageFilterResult(null, FilterStatus.HALT);
	
	private AbstractMessage message;
	private FilterStatus status;
			
	public MessageFilterResult(AbstractMessage aMessage, FilterStatus aStatus)
	{
		message = aMessage;
		status = aStatus;
	}
	
	public AbstractMessage getFilteredMessage()
	{
		return message;
	}
	
	public FilterStatus getFilterStatus()
	{
		return status;
	}
}
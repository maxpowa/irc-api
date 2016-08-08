package com.ircclouds.irc.api.filters;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;

/**
 * The message filter interface that can be used via {@link IRCApi}.
 * 
 * It allows filtering of {@link AbstractMessage} and redirecting them to interested {@link TargetListeners}.
 * 
 * @author miguel@lebane.se
 *
 */
public interface IMessageFilter
{
	MessageFilterResult filter(AbstractMessage aMessage);
	
	TargetListeners getTargetListeners();
}

package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;

/**
 * The IRC message listener interface that can be registered via {@link IRCApi}.
 * 
 * @author miguel@lebane.se
 *
 */
public interface IMessageListener
{
	void onMessage(AbstractMessage aMessage);
}

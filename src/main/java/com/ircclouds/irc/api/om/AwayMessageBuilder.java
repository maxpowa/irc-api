package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.UserAwayMessage;

/**
 * Away message builder.
 *
 * AbstractMessage builder for IRCv3 away-notify capability notification messages.
 *
 * @author Danny van Heumen
 */
public class AwayMessageBuilder implements IBuilder<UserAwayMessage>
{

	@Override
	public UserAwayMessage build(AbstractMessage aMessage)
	{
        return new UserAwayMessage(aMessage);
    }
}

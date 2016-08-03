package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.AwayMessage;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;

/**
 * Away message builder.
 *
 * AbstractMessage builder for IRCv3 away-notify capability notification messages.
 *
 * @author Danny van Heumen
 */
public class AwayMessageBuilder implements IBuilder<AwayMessage>
{

	@Override
	public AwayMessage build(AbstractMessage aMessage)
	{
        return new AwayMessage(aMessage);
    }
}

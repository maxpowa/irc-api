package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.AwayMessage;
import com.ircclouds.irc.api.domain.messages.Message;

/**
 * Away message builder.
 *
 * Message builder for IRCv3 away-notify capability notification messages.
 *
 * @author Danny van Heumen
 */
public class AwayMessageBuilder implements IBuilder<AwayMessage>
{

	@Override
	public AwayMessage build(Message aMessage)
	{
        return new AwayMessage(aMessage);
    }
}

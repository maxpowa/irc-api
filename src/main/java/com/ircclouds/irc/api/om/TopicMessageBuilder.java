package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

/**
 * 
 * @author didry
 * 
 */
public class TopicMessageBuilder implements IBuilder<ChannelTopic>
{
	@Override
    public ChannelTopic build(AbstractMessage aMessage) {
        return new ChannelTopic(aMessage);
    }
}

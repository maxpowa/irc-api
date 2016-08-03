package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class ChanPartBuilder implements IBuilder<ChannelPart>
{
	// //:aae!aaf@bot.lebane.se PART #botcode :aSS

    public ChannelPart build(AbstractMessage aMessage) {
        return new ChannelPart(aMessage);
    }
}

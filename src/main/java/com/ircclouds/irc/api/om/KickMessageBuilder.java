package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class KickMessageBuilder implements IBuilder<ChannelKick>
{
	@Override
	public ChannelKick build(AbstractMessage aMessage)
	{
		return new ChannelKick(aMessage);
	}
}

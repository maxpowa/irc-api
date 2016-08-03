package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class ChanJoinBuilder implements IBuilder<ChannelJoin>
{
	public ChannelJoin build(AbstractMessage aMessage)
	{
		return new ChannelJoin(aMessage);
	}
}

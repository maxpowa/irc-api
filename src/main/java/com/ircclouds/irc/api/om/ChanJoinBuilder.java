package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public class ChanJoinBuilder implements IBuilder<ChannelJoin>
{
	public ChannelJoin build(Message aMessage)
	{
		return new ChannelJoin(aMessage);
	}
}

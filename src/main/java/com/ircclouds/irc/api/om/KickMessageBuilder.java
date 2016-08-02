package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public class KickMessageBuilder implements IBuilder<ChannelKick>
{
	@Override
	public ChannelKick build(Message aMessage)
	{
		return new ChannelKick(aMessage);
	}
}

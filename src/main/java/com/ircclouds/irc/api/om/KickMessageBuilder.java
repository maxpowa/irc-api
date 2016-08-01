package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public class KickMessageBuilder implements IBuilder<ChannelKick>
{
	@Override
	public ChannelKick build(Message aMessage)
	{
		String[] _cmpnts = (String[]) aMessage.params.toArray();

		return new ChannelKick(ParseUtils.getUser(_cmpnts[0]), aMessage.getText(), _cmpnts[2], _cmpnts[3]);
	}
}

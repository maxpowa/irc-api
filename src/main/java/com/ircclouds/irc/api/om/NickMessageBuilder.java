package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class NickMessageBuilder implements IBuilder<NickMessage>
{
	@Override
	public NickMessage build(AbstractMessage aMessage)
	{
		return new NickMessage(aMessage);
	}
}

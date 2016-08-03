package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class NickMessageBuilder implements IBuilder<UserNickMessage>
{
	@Override
	public UserNickMessage build(AbstractMessage aMessage)
	{
		return new UserNickMessage(aMessage);
	}
}

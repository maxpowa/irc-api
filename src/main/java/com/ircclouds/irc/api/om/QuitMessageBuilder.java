package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class QuitMessageBuilder implements IBuilder<UserQuitMessage>
{
	public UserQuitMessage build(AbstractMessage aMsg)
	{
		return new UserQuitMessage(aMsg);
	}
}

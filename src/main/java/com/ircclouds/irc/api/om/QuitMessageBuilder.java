package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class QuitMessageBuilder implements IBuilder<QuitMessage>
{
	public QuitMessage build(AbstractMessage aMsg)
	{
		return new QuitMessage(aMsg);
	}
}

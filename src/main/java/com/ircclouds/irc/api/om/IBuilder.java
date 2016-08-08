package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;

public interface IBuilder<T extends AbstractMessage>
{
	T build(AbstractMessage aMessage);
}

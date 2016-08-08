package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.ChannelModes;
import com.ircclouds.irc.api.domain.IRCServerOptions;
import com.ircclouds.irc.api.domain.IRCUserStatuses;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ChannelMode;

public abstract class AbstractChanModeBuilder implements IBuilder<ChannelMode>
{
	public ChannelMode build(AbstractMessage aMessage)
	{
		// TODO: Move this logic into the channel mode message.

		return new ChannelMode(aMessage, getChannelModes(), getUserStatuses());
	}

	protected abstract IRCServerOptions getIRCServerOptions();

	private IRCUserStatuses getUserStatuses()
	{
		return getIRCServerOptions().getUserChanStatuses();
	}

	private ChannelModes getChannelModes()
	{
		return getIRCServerOptions().getChannelModes();
	}
}
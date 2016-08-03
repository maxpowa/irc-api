package com.ircclouds.irc.api.domain.messages;

public class ChannelNotice extends AbstractChannelMessage
{
	public ChannelNotice(AbstractMessage message)
	{
		super(message);
	}

	@Override
	public String asRaw() {
		return this.raw;
	}
}

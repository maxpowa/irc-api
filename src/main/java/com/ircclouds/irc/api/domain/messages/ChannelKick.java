package com.ircclouds.irc.api.domain.messages;

public class ChannelKick extends AbstractChannelMessage
{
	public ChannelKick(AbstractMessage message) {
		super(message);
	}

	public String getKickedNickname()
	{
		return this.params.get(1);
	}
	
	@Override
	public String asRaw()
	{
		return this.raw;
	}	
}

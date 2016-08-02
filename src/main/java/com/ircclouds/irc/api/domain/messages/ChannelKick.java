package com.ircclouds.irc.api.domain.messages;

public class ChannelKick extends ChannelPrivMsg
{
	public ChannelKick(Message message) {
		super(message);
	}

	public String getKickedNickname()
	{
		return this.params.get(1);
	}
	
	@Override
	public String asRaw()
	{
		return new StringBuffer().append(":").append(this.getSource()).append(" KICK ").append(this.getChannelName())
				.append(" ").append(this.getKickedNickname()).append(" :").append(this.getText()).toString();
	}	
}

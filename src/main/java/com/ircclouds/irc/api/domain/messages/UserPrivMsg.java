package com.ircclouds.irc.api.domain.messages;

public class UserPrivMsg extends AbstractUserMessage
{
	public UserPrivMsg(AbstractMessage message) {
		super(message);
	}

	public String getTarget() {
		return this.params.get(0);
	}
}
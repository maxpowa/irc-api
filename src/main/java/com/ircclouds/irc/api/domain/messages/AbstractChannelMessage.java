package com.ircclouds.irc.api.domain.messages;

public abstract class AbstractChannelMessage extends AbstractUserMessage {

	public AbstractChannelMessage(AbstractMessage message) {
		super(message);
	}

	public String getChannelName() {
		return this.params.get(0);
	}
}

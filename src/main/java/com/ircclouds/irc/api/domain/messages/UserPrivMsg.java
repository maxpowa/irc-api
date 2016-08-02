package com.ircclouds.irc.api.domain.messages;

public class UserPrivMsg extends AbstractPrivMsg
{
	public UserPrivMsg(Message message) {
		super(message);
	}

	public String getTarget() {
		return this.params.get(0);
	}

	@Override
	public String asRaw() {
		return new StringBuffer().append(":").append(this.getSource()).append(" PRIVMSG ").append(this.getTarget()).append(" :").append(this.getText()).toString();
	}
}
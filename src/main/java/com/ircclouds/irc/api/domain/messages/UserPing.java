package com.ircclouds.irc.api.domain.messages;

public class UserPing extends UserCTCP
{
	public UserPing(AbstractMessage message) {
		super(message);
	}

	@Override
	public String getText() {
		return super.getText().replaceFirst("(?i)PING", "").trim();
	}
}

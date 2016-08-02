package com.ircclouds.irc.api.domain.messages;

/**
 * 
 * @author
 * 
 */
public class UserVersion extends UserCTCP
{
	public UserVersion(Message message) {
		super(message);
	}

	@Override
	public String getText() {
		return super.getText().replaceFirst("(?i)VERSION", "").trim();
	}
}

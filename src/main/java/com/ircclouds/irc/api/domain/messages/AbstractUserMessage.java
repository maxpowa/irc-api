package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.utils.ParseUtils;

public abstract class AbstractUserMessage extends AbstractMessage {

	public AbstractUserMessage(AbstractMessage message) {
		super(message);
	}

	@Override
	public IRCUser getSource() {
		return ParseUtils.getUser(this.prefix);
	}
}

package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.ParseUtils;

import java.util.Date;

public class ChannelTopic extends AbstractChannelMessage
{
	private IRCTopic topic;

	public ChannelTopic(AbstractMessage message) {
		super(message);

		topic = new WritableIRCTopic(this.getSource().toString(), new Date(), this.getText());
	}

	public IRCTopic getTopic() {
		return topic;
	}
}

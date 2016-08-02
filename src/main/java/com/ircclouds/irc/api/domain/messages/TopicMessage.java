package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.ParseUtils;

import java.util.Date;

public class TopicMessage extends Message implements IChannelMessage, IUserMessage
{
	private IRCTopic topic;

	public TopicMessage(Message message) {
		super(message);

		topic = new WritableIRCTopic(this.getSource().toString(), new Date(), this.getText());
	}

	public IRCTopic getTopic() {
		return topic;
	}

	@Override
	public String asRaw() {
		return new StringBuffer().append(":").append(this.getSource()).append(" TOPIC ").append(this.getChannelName()).append(" :").append(topic.getValue()).toString();
	}

	@Override
	public String getChannelName() {
		return this.params.get(0);
	}

	@Override
	public IRCUser getSource() {
		return ParseUtils.getUser(this.prefix);
	}
}

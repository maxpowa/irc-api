package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ChannelNotice extends UserNotice implements IChannelMessage
{
	public ChannelNotice(Message message)
	{
		super(message);
	}

	@Override
	public String getChannelName()
	{
		return this.params.get(0);
	}

	@Override
	public String asRaw() {
		return new StringBuffer().append(":").append(this.getSource()).append(" NOTICE ").append(this.params.get(0)).append(" :").append(this.getText()).toString();
	}
}

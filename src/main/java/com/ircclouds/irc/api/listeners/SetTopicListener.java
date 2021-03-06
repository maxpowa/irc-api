package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.IRCException;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ChannelTopic;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.interfaces.Callback;

import net.engio.mbassy.listener.Handler;

public class SetTopicListener
{
	private Callback<String> callback;

	public SetTopicListener(Callback<String> aCallback)
	{
		callback = aCallback;
	}

	@Handler
	public void onMessage(AbstractMessage aMessage)
	{
		if (aMessage instanceof ChannelTopic)
		{
			ChannelTopic _topicMsg = (ChannelTopic) aMessage;

			callback.onSuccess(_topicMsg.getTopic().getValue());
		} else if (aMessage instanceof ServerNumeric) {
			if (((ServerNumeric) aMessage).getNumericCode().equals(IRCNumerics.ERR_CHANOPRIVSNEEDED)) {
				callback.onFailure(new IRCException(((ServerNumeric) aMessage).getText()));
			}
		}
	}
}

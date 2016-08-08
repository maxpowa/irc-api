package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.Callback;
import com.ircclouds.irc.api.IRCException;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ChannelTopic;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;

public class SetTopicListener implements IMessageListener
{
	private Callback<String> callback;

	public SetTopicListener(Callback<String> aCallback)
	{
		callback = aCallback;
	}

	@Override
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

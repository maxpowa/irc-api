package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.*;
import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class SetTopicListener implements IMessageListener
{
	private Callback<String> callback;

	public SetTopicListener(Callback<String> aCallback)
	{
		callback = aCallback;
	}

	@Override
	public void onMessage(IMessage aMessage)
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

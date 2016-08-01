package com.ircclouds.irc.api.om;

import java.util.*;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.*;

public abstract class AbstractNoticeBuilder implements IBuilder<IMessage>
{
	public IMessage build(Message aMessage)
	{
		if (aMessage.prefix == null) {
			return new ServerNotice(aMessage.getText(), null);
		} else if (!aMessage.prefix.contains("@")) {
			return new ServerNotice(aMessage.getText(), new IRCServer(aMessage.prefix));
		}

		WritableIRCUser _user = ParseUtils.getUser(aMessage.prefix);

		UserNotice _msg;

		if (getChannelTypes().contains(aMessage.params.get(0).charAt(0)))
		{
			_msg = new ChannelNotice(_user, aMessage.getText(), aMessage.params.get(0));
		}
		else
		{
			_msg = new UserNotice(_user, aMessage.getText(), aMessage.params.get(0));
		}

		return _msg;
	}
	
	protected abstract Set<Character> getChannelTypes();
}

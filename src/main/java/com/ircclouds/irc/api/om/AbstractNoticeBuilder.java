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
        if (aMessage.prefix == null || !aMessage.prefix.contains("@")) {
            return new ServerNotice(aMessage);
        }

        UserNotice _msg = new UserNotice(aMessage);
        if (getChannelTypes().contains(aMessage.params.get(0).charAt(0))) {
            _msg = new ChannelNotice(aMessage);
        }

		return _msg;
	}
	
	protected abstract Set<Character> getChannelTypes();
}

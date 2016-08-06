package com.ircclouds.irc.api.om;

import java.util.*;

import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public abstract class AbstractNoticeBuilder implements IBuilder<AbstractMessage>
{
	public AbstractMessage build(AbstractMessage aMessage)
	{
        if (aMessage.prefix == null || !aMessage.prefix.contains("@")) {
            return new ServerNotice(aMessage);
        }

        if (getChannelTypes().contains(aMessage.params.get(0).charAt(0))) {
            return new ChannelNotice(aMessage);
        }

		return new UserNotice(aMessage);
	}
	
	protected abstract Set<Character> getChannelTypes();
}

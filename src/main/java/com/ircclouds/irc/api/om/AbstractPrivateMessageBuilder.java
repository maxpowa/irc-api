package com.ircclouds.irc.api.om;

import java.util.*;

import com.ircclouds.irc.api.domain.messages.*;

public abstract class AbstractPrivateMessageBuilder implements IBuilder<AbstractUserMessage>
{
	private static final char NUL = '\001';
	private static final String PING = "PING";
	private static final String VERSION = "VERSION";
	private static final String ACTION = "ACTION";

	@Override
	public AbstractUserMessage build(AbstractMessage aMessage)
	{
		String _m = aMessage.getText();

		if (!aMessage.params.get(0).isEmpty() && getChannelTypes().contains(aMessage.params.get(0).charAt(0)))
		{
			if (_m.length() >= 2 && _m.charAt(0) == NUL && _m.charAt(_m.length() - 1) == NUL)
			{
				String _type = aMessage.getText().substring(1, aMessage.getText().length() - 1);
				if (_type.startsWith(VERSION))
				{
					return new ChannelVersion(aMessage);
				} else if (_type.startsWith(PING))
				{
					return new ChannelPing(aMessage);
				}
				else if (_type.startsWith(ACTION))
				{
					return new ChannelAction(aMessage);
				}
				else
				{
					return new ChannelCTCP(aMessage);
				}
			}
			else
			{
				return new ChannelPrivMsg(aMessage);
			}
		}
		else
		{
			// user msg
			if (_m.length() >= 2 && _m.charAt(0) == NUL && _m.charAt(_m.length() - 1) == NUL)
			{
				String _type = aMessage.getText().substring(1, aMessage.getText().length() - 1);
				if (_type.startsWith(VERSION))
				{
					return new UserVersion(aMessage);
				} else if (_type.startsWith(PING))
				{
					return new UserPing(aMessage);
				}
				else if (_type.startsWith(ACTION))
				{
					return new UserAction(aMessage);
				}
				else
				{
					return new UserCTCP(aMessage);
				}
			}
			else
			{
				return new UserPrivMsg(aMessage);
			}
		}
	}
	
	protected abstract Set<Character> getChannelTypes();
}

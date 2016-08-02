package com.ircclouds.irc.api.om;

import java.util.*;

import com.ircclouds.irc.api.domain.messages.*;

public abstract class AbstractPrivateMessageBuilder implements IBuilder<AbstractPrivMsg>
{
	private static final char NUL = '\001';
	private static final String PING = "PING";
	private static final String VERSION = "VERSION";
	private static final String ACTION = "ACTION";

	@Override
	public AbstractPrivMsg build(Message aMessage)
	{
		final AbstractPrivMsg _msg;
		String _m = aMessage.getText();

		if (!aMessage.params.get(0).isEmpty() && getChannelTypes().contains(aMessage.params.get(0).charAt(0)))
		{
			if (_m.length() >= 2 && _m.charAt(0) == NUL && _m.charAt(_m.length() - 1) == NUL)
			{
				String _type = aMessage.getText().substring(1, aMessage.getText().length() - 1);
				if (_type.startsWith(VERSION))
				{
					_msg = new ChannelVersion(aMessage);
				} else if (_type.startsWith(PING))
				{
					_msg = new ChannelPing(aMessage);
				}
				else if (_type.startsWith(ACTION))
				{
					_msg = new ChannelAction(aMessage);
				}
				else
				{
					_msg = new ChannelCTCP(aMessage);
				}
			}
			else
			{
				_msg = new ChannelPrivMsg(aMessage);
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
					_msg = new UserVersion(aMessage);
				} else if (_type.startsWith(PING))
				{
					_msg = new UserPing(aMessage);
				}
				else if (_type.startsWith(ACTION))
				{
					_msg = new UserAction(aMessage);
				}
				else
				{
					_msg = new UserCTCP(aMessage);
				}
			}
			else
			{
				_msg = new UserPrivMsg(aMessage);
			}
		}

		return _msg;
	}
	
	protected abstract Set<Character> getChannelTypes();
}

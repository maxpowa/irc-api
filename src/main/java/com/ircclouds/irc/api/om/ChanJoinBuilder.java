package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public class ChanJoinBuilder implements IBuilder<ChanJoinMessage>
{
	public ChanJoinMessage build(Message aMessage)
	{
		String _chanName = aMessage.params.get(0);
		if (_chanName.startsWith(":"))
		{
			_chanName = _chanName.substring(1);
		}

		return new ChanJoinMessage(ParseUtils.getUser(aMessage.prefix), _chanName.toLowerCase());
	}
}

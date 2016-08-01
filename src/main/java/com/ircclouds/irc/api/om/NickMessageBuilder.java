package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public class NickMessageBuilder implements IBuilder<NickMessage>
{
	@Override
	public NickMessage build(Message aMessage)
	{
		return new NickMessage(ParseUtils.getUser(aMessage.prefix), aMessage.getText());
	}
}

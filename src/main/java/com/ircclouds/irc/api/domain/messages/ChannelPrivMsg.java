package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ChannelPrivMsg extends AbstractPrivMsg implements IChannelMessage
{
	public ChannelPrivMsg(Message message)
	{
		super(message);
	}
	
	public String getChannelName()
	{
		return this.params.get(0);
	}
	
	@Override
	public String asRaw()
	{
		return new StringBuffer().append(":").append(this.getSource()).append(" PRIVMSG ").append(this.getChannelName()).append(" :").append(this.getText()).toString();
	}
}

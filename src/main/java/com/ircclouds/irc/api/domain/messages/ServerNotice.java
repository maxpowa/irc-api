package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ServerNotice extends AbstractNotice implements IServerMessage
{
	private IRCServer server;

	public ServerNotice(Message message)
	{
		super(message);

		server = this.prefix != null ? new IRCServer(this.prefix) : null;
	}

	@Override
	public IRCServer getSource()
	{
		return server;
	}

	@Override
	public String asRaw()
	{
		StringBuffer sb = new StringBuffer();
		if (this.getSource() != null) {
			sb.append(":").append(this.getSource()).append(" ");
		}
		return sb.append("NOTICE :").append(this.getText()).toString();
	}
}
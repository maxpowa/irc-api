package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class ServerPingMessageBuilder implements IBuilder<ServerPing>
{
	public ServerPing build(Message aMessage)
	{
		String _cmpnt[] = aMessage.raw.split(":");

		ServerPing _pMsg = new ServerPing();
		_pMsg.setText(_cmpnt[1]);

		return _pMsg;
	}
}

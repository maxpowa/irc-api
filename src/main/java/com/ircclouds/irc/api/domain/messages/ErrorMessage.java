package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

public class ErrorMessage extends AbstractMessage {
	public ErrorMessage(AbstractMessage message)
	{
		super(message);
	}
}

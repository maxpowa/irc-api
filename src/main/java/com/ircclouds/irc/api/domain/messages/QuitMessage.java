package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.utils.ParseUtils;

/**
 * 
 * @author
 * 
 */
public class QuitMessage extends AbstractUserMessage
{

	public QuitMessage(AbstractMessage message) {
		super(message);
	}
	
	@Override
	public String asRaw()
	{
		return this.raw;
	}
}
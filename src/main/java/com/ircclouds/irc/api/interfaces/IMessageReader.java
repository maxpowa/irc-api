package com.ircclouds.irc.api.interfaces;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;

import java.io.IOException;

public interface IMessageReader
{
	boolean available() throws IOException;

	AbstractMessage readMessage();
	
	void reset();
}

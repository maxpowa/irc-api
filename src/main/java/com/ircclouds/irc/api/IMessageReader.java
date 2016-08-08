package com.ircclouds.irc.api;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;

import java.io.IOException;

public interface IMessageReader
{
	boolean available() throws IOException;

	AbstractMessage readMessage();
	
	void reset();
}

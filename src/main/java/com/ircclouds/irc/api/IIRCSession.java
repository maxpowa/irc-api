package com.ircclouds.irc.api;

import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.listeners.IMessageListener;
import com.ircclouds.irc.api.listeners.Visibility;
import com.ircclouds.irc.api.state.IIRCState;

import java.io.IOException;

public interface IIRCSession
{
	ICommandServer getCommandServer();

	void addListeners(Visibility aLevel, IMessageListener... aListener);
	
	void removeListener(IMessageListener aListener);
	
	boolean open(IRCServer aServer, Callback<IIRCState> aCallback) throws IOException;
	
	void close() throws IOException;

	void dispatchClientError(Exception e);
}

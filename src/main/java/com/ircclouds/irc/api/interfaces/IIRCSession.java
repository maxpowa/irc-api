package com.ircclouds.irc.api.interfaces;

import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.state.IIRCState;

import net.engio.mbassy.bus.MBassador;

import java.io.IOException;

public interface IIRCSession
{
	ICommandServer getCommandServer();

	MBassador getEventBus();

	void register(Object... aListener);
	
	void unregister(Object aListener);
	
	boolean open(IRCServer aServer, Callback<IIRCState> aCallback) throws IOException;
	
	void close() throws IOException;

	void dispatchClientError(Exception e);
}

package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.interfaces.IIRCSession;

public class PingVersionListenerImpl extends AbstractPingVersionListener
{
	private IIRCSession session;

	public PingVersionListenerImpl(IIRCSession aSession)
	{
		session = aSession;
	}

	@Override
	protected IIRCSession getSession()
	{
		return session;
	}
}

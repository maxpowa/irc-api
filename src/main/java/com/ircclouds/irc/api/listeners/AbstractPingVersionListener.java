package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.commands.PongCommand;
import com.ircclouds.irc.api.domain.messages.ServerPing;
import com.ircclouds.irc.api.interfaces.IIRCSession;

import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractPingVersionListener
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractPingVersionListener.class);

	@Handler
	public void onServerPing(ServerPing aMsg) throws IOException {
		getSession().getCommandServer().execute(new PongCommand(aMsg.getText()));
    }

	protected abstract IIRCSession getSession();
}

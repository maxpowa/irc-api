package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.IIRCSession;
import com.ircclouds.irc.api.commands.SendServerPingReplyCmd;
import com.ircclouds.irc.api.commands.interfaces.ICommand;
import com.ircclouds.irc.api.domain.messages.ServerPing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractPingVersionListener extends VariousMessageListenerAdapter
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractPingVersionListener.class);

	@Override
	public void onServerPing(ServerPing aMsg)
	{
		execute(new SendServerPingReplyCmd(aMsg.getText()));
	}

	protected abstract IIRCSession getSession();
	
	private void execute(ICommand aCmd)
	{
		try
		{
			getSession().getCommandServer().execute(aCmd);
		}
		catch (IOException aExc)
		{
			LOG.error("Error Executing Command [" + aCmd + "]", aExc);
		}
	}
}

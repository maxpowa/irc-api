package com.ircclouds.irc.api;

import com.ircclouds.irc.api.commands.ICommand;
import com.ircclouds.irc.api.comms.INeedsConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractCommandServerImpl implements ICommandServer, INeedsConnection
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractCommandServerImpl.class);

	public void execute(ICommand aCommand) throws IOException
	{
		LOG.debug("Executing Command: " + aCommand);

		String _str = aCommand + "\r\n";
		int _written = getConnection().write(_str);
		if (_str.length() > _written)
		{
			LOG.error("Expected to write " + _str.length() + " bytes, but wrote " + _written);
		}
	}
}

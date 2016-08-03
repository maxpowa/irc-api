package com.ircclouds.irc.api;

import com.ircclouds.irc.api.comms.INeedsConnection;
import com.ircclouds.irc.api.domain.IRCServerOptions;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.interfaces.IMessage;
import com.ircclouds.irc.api.om.AbstractMessageFactory;
import com.ircclouds.irc.api.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author miguel
 * 
 */

public abstract class AbstractMessageReader implements IMessageReader, INeedsConnection
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageReader.class);
	
	private String crlf = "";
	private AbstractMessageFactory msgFactory;
	private StringBuilder ircData = new StringBuilder();
	private Queue<String> ircMessages = new LinkedList<String>();
	private boolean canRead = true;
	
	public AbstractMessageReader()
	{
		msgFactory = new AbstractMessageFactory()
		{
			@Override
			protected IRCServerOptions getIRCServerOptions()
			{
				return AbstractMessageReader.this.getIRCServerOptions();
			}
		};
	}

	public boolean available() throws IOException
	{
		if (canRead)
		{
			ircData.append(getConnection().read());				
			canRead = false;
			
			trySetNewLine();
			fetchNextBatch();
		}

		return true;
	}

	public IMessage readMessage()
	{
		IMessage _msg = null;
		
		if (ircMessages.peek() != null) {
			try {
				_msg = msgFactory.build(ircMessages.poll());
			} catch (AbstractMessage.ParseError aExc) {
				LOG.error("Parse error: " + aExc.getMessage());
			}
		}
		
		canRead = ircMessages.isEmpty();
		
		return _msg;
	}

	@Override
	public void reset()
	{
		ircMessages.clear();
		ircData.setLength(0);
		canRead = true;
	}
	
	protected abstract IRCServerOptions getIRCServerOptions();

	private void trySetNewLine()
	{
		if (!StringUtils.isEmpty(crlf))
		{
			return;
		}
		else if (ircData.indexOf("\r\n") != -1)
		{
			crlf = "\r\n";
		}
		else if (ircData.indexOf("\n") != -1)
		{
			crlf = "\n";
		}
	}	
	
	private void fetchNextBatch()
	{
		if (ircData.indexOf(crlf) != -1)
		{
			String _tempMsg = ircData.toString();
			if (ircData.lastIndexOf(crlf) != ircData.length() - crlf.length() - 1)
			{
				int _i = ircData.lastIndexOf(crlf);
				_tempMsg = ircData.substring(0, _i);
				ircData = new StringBuilder(ircData.substring(_i + crlf.length()));
			}
			else
			{
				ircData.setLength(0);
			}

			ircMessages.addAll(Arrays.asList(_tempMsg.split(crlf)));
		}
	}
}
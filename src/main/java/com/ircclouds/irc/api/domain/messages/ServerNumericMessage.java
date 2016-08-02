package com.ircclouds.irc.api.domain.messages;

import java.text.*;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

/**
 * 
 * @author
 * 
 */
public class ServerNumericMessage implements IServerMessage, IHasText, IHasNumericCode
{
	private final static NumberFormat FORMATTER = new DecimalFormat("000");

	private int numericCode;
	private String text;
	private IRCServer server;
	private String target;

	public ServerNumericMessage(Integer aNumericCode, String aTarget, String[] aText, IRCServer aServer)
	{
		numericCode = aNumericCode;
		target = aTarget;
		text = strJoin(aText, " ");
		server = aServer;
	}

	public String getText()
	{
		return text;
	}

	public Integer getNumericCode()
	{
		return numericCode;
	}

	public String getTarget()
	{
		return target;
	}

	public static String strJoin(String[] aArr, String sSep) {
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0)
				sbStr.append(sSep);
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}

	@Override
	public IRCServer getSource()
	{
		return server;
	}

	@Override
	public String asRaw()
	{
		return new StringBuffer().append(":").append(server.getHostname()).append(" ").append(FORMATTER.format(numericCode)).append(" ").append(target).append(" ").append(text).toString();
	}
}

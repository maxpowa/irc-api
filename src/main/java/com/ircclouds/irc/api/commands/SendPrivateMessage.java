package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

public class SendPrivateMessage implements ICommand
{
	private static final String PRIVMSG = "PRIVMSG ";

	private String target;
	private String msg;
	private Integer asyncRandConstant;
	
	public SendPrivateMessage(String aChannel, String aText)
	{
		this(aChannel, aText, null);
	}

	public SendPrivateMessage(String aTarget, String aText, Integer aAsyncRandConstant)
	{
		target = aTarget;
		msg = aText;
		asyncRandConstant = aAsyncRandConstant;
	}
	
	@Override
	public String toString()
	{
		if (asyncRandConstant == null)
		{
			return PRIVMSG + target + " :" + msg + CRNL;
		}
		else
		{
			return PRIVMSG + target + "," + asyncRandConstant + " :" + msg + CRNL;
		}
	}
}
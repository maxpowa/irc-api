package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

public class SendActionMessage implements ICommand
{	
	private static final char NUL = '\001';

	private String target;
	private String msg;
	private Integer asyncRandConstant;

	public SendActionMessage(String aTarget, String aText)
	{
		this(aTarget, aText, null);
	}

	public SendActionMessage(String aTarget, String aText, Integer aAsyncRandConstant)
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
			return "PRIVMSG " + target + " :" + NUL + "ACTION " + msg + NUL + CRNL;
		}
		else
		{
			return "PRIVMSG " + target + "," + asyncRandConstant + " :" + NUL + "ACTION " + msg + NUL + CRNL;
		}
	}
}

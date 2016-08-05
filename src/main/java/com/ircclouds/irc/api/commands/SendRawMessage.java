package com.ircclouds.irc.api.commands;


import com.ircclouds.irc.api.commands.interfaces.ICommand;

public class SendRawMessage implements ICommand
{
	private String text;

	public SendRawMessage(String aText)
	{
		text = aText;
	}

	public String toString()
	{
		return text + CRNL;
	}
}

package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

public class ChangeNickCmd implements ICommand
{
	private String newNick;
	
	public ChangeNickCmd(String aNewNick)
	{
		newNick = aNewNick;
	}
	
	@Override
	public String toString()
	{
		return "NICK :" + newNick + CRNL;
	}
}

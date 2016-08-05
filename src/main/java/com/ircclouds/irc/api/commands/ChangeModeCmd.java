package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

public class ChangeModeCmd implements ICommand
{
	private String changeModesStr;
	
	public ChangeModeCmd(String aModeStr)
	{
		changeModesStr = aModeStr;
	}
	
	@Override
	public String toString()
	{
		return "MODE " + changeModesStr + CRNL;
	}
}

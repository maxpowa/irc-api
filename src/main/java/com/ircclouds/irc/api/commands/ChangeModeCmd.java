package com.ircclouds.irc.api.commands;

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

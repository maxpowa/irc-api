package com.ircclouds.irc.api.commands;


public class QuitCmd implements ICommand
{
	public static String DEFAULT_QUIT_MESSAGE = "Leaving";

	private String quitMsg;

	public QuitCmd()
	{
		quitMsg = DEFAULT_QUIT_MESSAGE;
	}	
	
	public QuitCmd(String aQuitMsg)
	{
		quitMsg = aQuitMsg;
	}

	@Override
	public String toString()
	{
		return "QUIT :" + quitMsg + CRNL;
	}
}

package com.ircclouds.irc.api.commands;


import com.ircclouds.irc.api.commands.interfaces.ICommand;

public class PartChanCmd implements ICommand
{
	private static final String PART_ID = "PART";

	private String channel;
	private String partMsg;

	public PartChanCmd(String aChannel, String aPartMsg)
	{
		channel = aChannel;
		partMsg = aPartMsg;
	}

	@Override
	public String toString()
	{
		return "PART " + channel + " :" + partMsg + CRNL;
	}

}

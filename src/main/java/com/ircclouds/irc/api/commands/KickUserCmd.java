package com.ircclouds.irc.api.commands;

public class KickUserCmd implements ICommand
{
	private String channel;
	private String user;
	private String kickMsg;

	public KickUserCmd(String aChannel, String aNick, String aKickMessage)
	{
		user = aNick;
		channel = aChannel;
		kickMsg = aKickMessage;
	}

	public String toString()
	{
		return "KICK " + channel + " " + user + " :" + kickMsg + CRNL;
	}
}

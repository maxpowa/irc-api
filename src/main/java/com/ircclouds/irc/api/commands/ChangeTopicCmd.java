package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

/**
 * 
 * @author didry
 * 
 */
public class ChangeTopicCmd implements ICommand
{
	private String channel;
	private String topic;

	public ChangeTopicCmd(String aChannel, String aTopic)
	{
		channel = aChannel;
		topic = aTopic;
	}

	@Override
	public String toString()
	{
		return "TOPIC " + channel + " :" + topic + CRNL;
	}

}

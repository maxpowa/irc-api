package com.ircclouds.irc.api.commands;

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

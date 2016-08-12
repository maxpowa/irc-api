package com.ircclouds.irc.api.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WritableIRCChannel extends IRCChannel
{
	public WritableIRCChannel(String aName)
	{
		super(aName);
	}

	public void setTopic(WritableIRCTopic aTopic)
	{
		topic = aTopic;
	}

	public void setModes(Set<ChannelMode> aModes)
	{
		chanModes = aModes;
	}

	public void setName(String aName)
	{
		name = aName;
	}

	public void addUser(IRCUser aUser)
	{
		addUser(aUser, Collections.synchronizedSet(new HashSet<IRCUserStatus>()));
	}

	public void addUser(IRCUser aUser, Set<IRCUserStatus> aStatus)
	{
		users.add(aUser);
		usersStatuses.put(aUser, aStatus);
	}

	public Set<IRCUserStatus> removeUser(IRCUser aUser)
	{
		users.remove(aUser);
		return usersStatuses.remove(aUser);
	}
}
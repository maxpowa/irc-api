package com.ircclouds.irc.api.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IRCChannel implements Serializable
{
	/**
	 * The IRC channel object that will be returned when an asynchronous
	 * joinChannel succeeds.
	 * 
	 * The object stores the channel name, topic, channel modes, and a mapping
	 * of all channel users and their statuses.
	 * 
	 * @author miguel@lebane.se
	 * 
	 */

	String name;
	IRCTopic topic;
	Set<ChannelMode> chanModes = Collections.synchronizedSet(new HashSet<ChannelMode>());
	List<IRCUser> users = Collections.synchronizedList(new ArrayList<IRCUser>());
	Map<IRCUser, Set<IRCUserStatus>> usersStatuses = new LinkedHashMap<IRCUser, Set<IRCUserStatus>>();
	
	public IRCChannel(String aName)
	{
		this(aName, new WritableIRCTopic(""));
	}

	public IRCChannel(String aName, IRCTopic aTopic)
	{
		name = aName;
		topic = aTopic;
	}

	public String getName()
	{
		return name;
	}

	public IRCTopic getTopic()
	{
		return topic;
	}

	public Set<ChannelMode> getModes()
	{
		return chanModes;
	}
	
	public boolean contains(String aNick)
	{
		return contains(new IRCUser(aNick));
	}
	
	public boolean contains(IRCUser aUser)
	{
		return users.contains(aUser);
	}

	public Set<IRCUserStatus> getStatusesForUser(IRCUser aUser)
	{
		return usersStatuses.get(aUser);
	}

	public List<IRCUser> getUsers()
	{
		return users;
	}

	@Override
	public boolean equals(Object aObject)
	{
		if (aObject instanceof IRCChannel)
		{
			return name.equals(((IRCChannel) aObject).getName());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}

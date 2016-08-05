package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.utils.StringUtils;

/**
 * 
 * @author
 * 
 */
public class JoinChanCmd implements ICommand
{
	private String chanName;
	private String key;

	public JoinChanCmd(String aChanName)
	{
		this(aChanName, "");
	}

	public JoinChanCmd(String aChanName, String aKey)
	{
		chanName = aChanName;
		key = aKey;
	}

	public String toString() {
		return "JOIN " + chanName + getKey() + CRNL;
	}

	private String getKey()
	{
		if (StringUtils.isEmpty(key)) return "";
		return " :" + key;
	}
}

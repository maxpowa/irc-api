package com.ircclouds.irc.api.utils;

public final class StringUtils
{
	public static boolean isEmpty(String aString)
	{
		return aString == null || "".equals(aString);
	}
}
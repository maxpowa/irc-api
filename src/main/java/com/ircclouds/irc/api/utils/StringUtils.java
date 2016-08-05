package com.ircclouds.irc.api.utils;

import com.ircclouds.irc.api.domain.ChannelMode;

import java.util.List;

/**
 * 
 * @author
 * 
 */
public final class StringUtils
{
	public static String join(List<ChannelMode> aList)
	{
		StringBuilder _sb = new StringBuilder();
		for (ChannelMode _s : aList)
		{
			_sb.append(_s.getChannelModeType());
		}
		
		return _sb.toString();
	}

	public static boolean isEmpty(String aString)
	{
		return aString == null || "".equals(aString);
	}
}
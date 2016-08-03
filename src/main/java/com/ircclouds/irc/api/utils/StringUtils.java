package com.ircclouds.irc.api.utils;

import java.util.*;

import com.ircclouds.irc.api.domain.*;

/**
 * 
 * @author
 * 
 */
public final class StringUtils
{
	public static String join(List<ChannelMode> aList)
	{
		StringBuffer _sb = new StringBuffer();
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
package com.ircclouds.irc.api.domain;

import java.io.*;

import com.ircclouds.irc.api.domain.messages.interfaces.*;

/**
 * 
 * @author
 * 
 */
public class WritableIRCUser implements ISource, Serializable
{
	private String nick;
	private String hostname;
	private String ident;

	public WritableIRCUser()
	{
		this("");
	}

	public WritableIRCUser(String aNick)
	{
		nick = aNick;
	}

	public String getNick()
	{
		return nick;
	}

	public void setNick(String aNick)
	{
		nick = aNick;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String aHostname)
	{
		hostname = aHostname;
	}

	public String getIdent()
	{
		return ident;
	}

	public void setIdent(String aIdent)
	{
		ident = aIdent;
	}

	@Override
	public boolean equals(Object aObject)
	{
		if (aObject != null)
		{
			if (aObject instanceof WritableIRCUser)
			{
				return ((WritableIRCUser) aObject).getNick().equals(nick);
			}
			
			return aObject.equals(nick);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return nick.hashCode();
	}
	
	public String toString()
	{
		return nick + "!" + ident + "@" + hostname;
	}
}
package com.ircclouds.irc.api.commands;

/**
 * CAP END. Command to end capability negotiation.
 *
 * @author Danny van Heumen
 */
public class CapEndCmd extends CapCmd
{

	@Override
	public String toString()
	{
		return "CAP END" + CRNL;
	}
}

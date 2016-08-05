package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICapCmd;

/**
 * CAP END. Command to end capability negotiation.
 *
 * @author Danny van Heumen
 */
public class CapEndCmd implements ICapCmd
{

	@Override
	public String toString()
	{
		return "CAP END" + CRNL;
	}
}

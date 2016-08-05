package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICapCmd;

/**
 * CAP LS. Command to query available capabilities.
 *
 * @author Danny van Heumen
 */
public class CapLsCmd implements ICapCmd
{

	@Override
	public String toString()
	{
		return "CAP LS" + CRNL;
	}
}

package com.ircclouds.irc.api.negotiators;

import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.interfaces.IIRCApi;

/**
 * IRC v3.1 capability negotiation.
 *
 * <p>
 * See https://github.com/ircv3/ircv3-specifications for IRC v3 specifications.
 * </p>
 *
 * @author Danny van Heumen
 */
public interface CapabilityNegotiator
{

	/**
	 * Initiate the negotiator with the provided irc instance.
	 *
	 * <p>
	 * Initiate the negotiator and return the initial CAP negotiation message
	 * that should be sent back to the server.
	 * </p>
	 *
	 * <p>
	 * The IIRCApi instance should be stored for later use during negotiations in
	 * order to respond to incoming CAP negoation messages.
	 * </p>
	 *
	 * @param irc the current IIRCApi instance
	 * @return returns initialization command for CAP negotiation
	 */
	ICapCmd initiate(IIRCApi irc);
}

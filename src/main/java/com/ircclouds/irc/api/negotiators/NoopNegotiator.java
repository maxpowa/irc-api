package com.ircclouds.irc.api.negotiators;

import com.ircclouds.irc.api.commands.CapEndCmd;
import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.interfaces.IIRCApi;

import net.engio.mbassy.listener.Handler;

/**
 * NOOP negotiator. This negotiator does not actually enable any of the
 * available extensions. It does however, signal the IRC server that CAP
 * negotiation is supported by the client.
 *
 * @author Danny van Heumen
 */
public class NoopNegotiator implements CapabilityNegotiator
{

	/**
	 * The CAP negotiation initialization command immediately ends negotiation.
	 */
	@Override
	public ICapCmd initiate(final IIRCApi irc)
	{
		return new CapEndCmd();
	}

	@Handler
	public void onMessage(final AbstractMessage aMessage)
	{
	}
}

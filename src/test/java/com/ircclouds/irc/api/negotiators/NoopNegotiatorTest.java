package com.ircclouds.irc.api.negotiators;

import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.interfaces.IIRCApi;

import org.junit.Test;

import mockit.Mocked;
import mockit.Tested;

import static org.junit.Assert.assertEquals;

/**
 * @author Danny van Heumen
 */
public class NoopNegotiatorTest
{

	@Tested
	private NoopNegotiator neg;

	public NoopNegotiatorTest()
	{
	}

	@Test
	public void testInitiate(@Mocked IIRCApi irc)
	{
		ICapCmd cmd = neg.initiate(irc);
		assertEquals("CAP END\r\n", cmd.toString());
	}

	@Test
	public void testOnMessage(@Mocked AbstractMessage msg)
	{
		neg.onMessage(msg);
	}
}

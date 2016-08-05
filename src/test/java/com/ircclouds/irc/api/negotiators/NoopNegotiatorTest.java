package com.ircclouds.irc.api.negotiators;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.commands.CapCmd;
import com.ircclouds.irc.api.domain.messages.interfaces.IMessage;

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
	public void testInitiate(@Mocked IRCApi irc)
	{
		CapCmd cmd = neg.initiate(irc);
		assertEquals("CAP END\r\n", cmd.toString());
	}

	@Test
	public void testOnMessage(@Mocked IMessage msg)
	{
		neg.onMessage(msg);
	}
}

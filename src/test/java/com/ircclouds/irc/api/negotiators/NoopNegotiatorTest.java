package com.ircclouds.irc.api.negotiators;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.domain.messages.interfaces.IMessage;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Test;

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
		ICapCmd cmd = neg.initiate(irc);
		assertEquals("CAP END\r\n", cmd.toString());
	}

	@Test
	public void testOnMessage(@Mocked IMessage msg)
	{
		neg.onMessage(msg);
	}
}

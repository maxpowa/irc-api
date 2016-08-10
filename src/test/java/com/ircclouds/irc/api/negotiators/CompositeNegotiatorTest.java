package com.ircclouds.irc.api.negotiators;

import com.ircclouds.irc.api.commands.CapLsCmd;
import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.GenericMessage;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.interfaces.IIRCApi;
import com.ircclouds.irc.api.negotiators.CompositeNegotiator.Capability;
import com.ircclouds.irc.api.negotiators.CompositeNegotiator.Host;
import com.ircclouds.irc.api.negotiators.api.Relay;
import com.ircclouds.irc.api.negotiators.capabilities.SimpleCapability;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mockit.Mocked;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for composite negotiator.
 * @author Danny van Heumen
 */
public class CompositeNegotiatorTest
{
	
	public CompositeNegotiatorTest()
	{
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructorNullCapabilityList()
	{
		new CompositeNegotiator(null, new Host() {

			@Override
			public void acknowledge(CompositeNegotiator.Capability cap)
			{
			}

			@Override
			public void reject(CompositeNegotiator.Capability cap)
			{
			}
		});
	}

	@Test
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructorNullHost() {
		new CompositeNegotiator(Collections.<CompositeNegotiator.Capability>emptyList(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructWithNullCapabilityId() {
		LinkedList<Capability> negotiators = new LinkedList<Capability>();
		negotiators.add(new Capability() {

			@Override
			public String getId() { return null; }

			@Override
			public boolean enable() { return true; }

			@Override
			public boolean converse(Relay relay, AbstractMessage msg) { return false; }
		});
		new CompositeNegotiator(negotiators, null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructWithEmptyCapabilityId() {
		LinkedList<Capability> negotiators = new LinkedList<Capability>();
		negotiators.add(new Capability() {

			@Override
			public String getId()
			{
				return "";
			}

			@Override
			public boolean enable()
			{
				return true;
			}

			@Override
			public boolean converse(Relay relay, AbstractMessage msg) { return false; }
		});
		new CompositeNegotiator(negotiators, null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructWithOneBadCapability() {
		LinkedList<Capability> negotiators = new LinkedList<Capability>();
		negotiators.add(new Capability() {

			@Override
			public String getId() { return "away-notify"; }

			@Override
			public boolean enable() { return false; }

			@Override
			public boolean converse(Relay relay, AbstractMessage msg) { return false; }
		});
		negotiators.add(new Capability() {

			@Override
			public String getId() { return "sasl"; }

			@Override
			public boolean enable() { return false; }

			@Override
			public boolean converse(Relay relay, AbstractMessage msg) { return false; }
		});
		negotiators.add(new Capability() {

			@Override
			public String getId() { return ""; }

			@Override
			public boolean enable() { return true; }

			@Override
			public boolean converse(Relay relay, AbstractMessage msg) { return false; }
		});
		new CompositeNegotiator(negotiators, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitiateNull() {
		Capability cap = getCapability("away-notify");
		CompositeNegotiator negotiator = new CompositeNegotiator(Collections.singletonList(cap), null);
		ICapCmd cmd = negotiator.initiate(null);
	}

	@Test
	public void testInitiate(@Mocked IIRCApi ircapi) {
		Capability cap = getCapability("away-notify");
		CompositeNegotiator negotiator = new CompositeNegotiator(Collections.singletonList(cap), null);
		ICapCmd cmd = negotiator.initiate(ircapi);
		assertNotNull(cmd);
		assertEquals(CapLsCmd.class, cmd.getClass());
	}

	@Test(expected = NullPointerException.class)
	public void testParseResponseCapNull() {
		CompositeNegotiator.parseResponseCaps(null);
	}

	@Test
	public void testParseResponseCapEmpty() {
		assertEquals(Collections.emptyList(), CompositeNegotiator.parseResponseCaps(""));
	}

	@Test
	public void testParseResponseCapManyWhitespace() {
		assertEquals(Collections.emptyList(), CompositeNegotiator.parseResponseCaps("               "));
	}

	@Test
	public void testParseResponseCapSimple() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("sasl");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertTrue(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertFalse(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapMultiple() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("-=~sasl");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertFalse(parsed.get(0).isEnabled());
		assertTrue(parsed.get(0).isRquiresAck());
		assertTrue(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapMultiCapWithModifiers() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("=sasl -away-notify ~account-notify");
		assertEquals(3, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertTrue(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertTrue(parsed.get(0).isMandatory());
		assertEquals("away-notify", parsed.get(1).getId());
		assertFalse(parsed.get(1).isEnabled());
		assertFalse(parsed.get(1).isRquiresAck());
		assertFalse(parsed.get(1).isMandatory());
		assertEquals("account-notify", parsed.get(2).getId());
		assertTrue(parsed.get(2).isEnabled());
		assertTrue(parsed.get(2).isRquiresAck());
		assertFalse(parsed.get(2).isMandatory());
	}

	@Test
	public void testParseResponseCapConfirm() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("~sasl");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertTrue(parsed.get(0).isEnabled());
		assertTrue(parsed.get(0).isRquiresAck());
		assertFalse(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapSticky() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("=sasl");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertTrue(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertTrue(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapDisabled() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("-sasl");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertFalse(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertFalse(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapSurroundedWhitespace() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("   sasl         ");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertTrue(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertFalse(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapSurroundedWhitespaceConfirm() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("   ~sasl         ");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertTrue(parsed.get(0).isEnabled());
		assertTrue(parsed.get(0).isRquiresAck());
		assertFalse(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapSurroundedWhitespaceSticky() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("   =sasl         ");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertTrue(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertTrue(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapSurroundedWhitespaceDisabled() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("   -sasl         ");
		assertEquals(1, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertFalse(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertFalse(parsed.get(0).isMandatory());
	}

	@Test
	public void testParseResponseCapMultiSurroundedWhitespaceDisabled() {
		LinkedList<CompositeNegotiator.Cap> parsed = CompositeNegotiator.parseResponseCaps("   -sasl    =account-notify     ");
		assertEquals(2, parsed.size());
		assertEquals("sasl", parsed.get(0).getId());
		assertFalse(parsed.get(0).isEnabled());
		assertFalse(parsed.get(0).isRquiresAck());
		assertFalse(parsed.get(0).isMandatory());
		assertEquals("account-notify", parsed.get(1).getId());
		assertTrue(parsed.get(1).isEnabled());
		assertFalse(parsed.get(1).isRquiresAck());
		assertTrue(parsed.get(1).isMandatory());
	}

	@Test
	public void testCapabilityConversation(@Mocked IIRCApi ircapi) {
		List<Capability> caps = new ArrayList<Capability>();
		caps.add(new SimpleCapability("away-notify", true));
		caps.add(new SimpleCapability("multi-prefix", false));
		caps.add(new SimpleCapability("random.cap/nak", true));
		CompositeNegotiator negotiator = new CompositeNegotiator(caps, null);
		ICapCmd cmd = negotiator.initiate(ircapi);
		assertEquals("CAP LS\r\n", cmd.toString());
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP * LS :away-notify multi-prefix random.cap/nak"));
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP * ACK :~away-notify -multi-prefix"));
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP * NAK :random.cap/nak"));
	}

	@Test
	public void testCapabilityConversationAbortEarly(@Mocked IIRCApi ircapi) {
		List<Capability> caps = new ArrayList<Capability>();
		caps.add(new SimpleCapability("away-notify", true));
		CompositeNegotiator negotiator = new CompositeNegotiator(caps, null);
		ICapCmd cmd = negotiator.initiate(ircapi);
		assertEquals("CAP LS\r\n", cmd.toString());
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP *")); // Malformed message
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP * LS :away-notify"));
		negotiator.onMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 002 :funky out of order message"))); // Out of order message
		negotiator.onMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 001 :welcome to fake server")));
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP * ACK :away-notify")); // Out of order should be dropped
	}

	private Capability getCapability(final String id) {
		return new Capability() {

			@Override
			public String getId()
			{
				return id;
			}

			@Override
			public boolean enable()
			{
				return true;
			}

			@Override
			public boolean converse(Relay relay, AbstractMessage msg)
			{
				return false;
			}
		};
	}
}

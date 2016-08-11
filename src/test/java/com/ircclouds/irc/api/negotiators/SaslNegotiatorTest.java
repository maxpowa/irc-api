/*
 * Copyright 2015 danny.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ircclouds.irc.api.negotiators;

import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.domain.messages.GenericMessage;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.interfaces.IIRCApi;

import org.junit.Before;
import org.junit.Test;

import mockit.Mocked;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author danny
 */
public class SaslNegotiatorTest {

	public SaslNegotiatorTest() {
	}

	@Before
	public void setUp() {
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructNullUser() {
		new SaslNegotiator(null, "password", "role");
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructNullPassword() {
		new SaslNegotiator("user", null, "role");
	}

	@Test
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructNullRole() {
		new SaslNegotiator("user", "password", null);
	}

	@Test
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testConstructWithAllParams() {
		new SaslNegotiator("User", "pass", "role");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitiateNull() {
		SaslNegotiator neg = new SaslNegotiator("user", "pass", "role");
		neg.initiate(null);
	}

	@Test
	public void testInitiateValid(@Mocked IIRCApi irc) {
		SaslNegotiator neg = new SaslNegotiator("user", "pass", "role");
		ICapCmd cmd = neg.initiate(irc);
		assertNotNull(cmd);
		assertEquals("CAP REQ :sasl", cmd.toString().trim());
	}

	@Test
	public void testCapabilityConversation(@Mocked IIRCApi ircapi) {
		SaslNegotiator negotiator = new SaslNegotiator("nick", "pass", "authzid");
		ICapCmd cmd = negotiator.initiate(ircapi);
		assertEquals("CAP REQ :sasl", cmd.toString().trim());
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP * ACK :sasl"));
		negotiator.onMessage(new GenericMessage("AUTHENTICATE +"));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 900 nick nick!user@host authzid :You're now logged in as authzid")));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 903 nick :SASL success")));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 902 nick :You must use a nick assigned to you")));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 904 nick :SASL fail")));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 905 nick :SASL too long")));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 906 nick :SASL aborted")));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 907 nick :Already authenticated via SASL")));
		negotiator.onServerNumericMessage(new ServerNumeric(new GenericMessage(":irc.serv.er 908 nick PLAIN EXTERNAL :are available mechanisms")));
	}

	@Test
	public void testCapabilityConversationAbortEarly(@Mocked IIRCApi ircapi) {
		SaslNegotiator negotiator = new SaslNegotiator("user", "pass", "authzid");
		ICapCmd cmd = negotiator.initiate(ircapi);
		assertEquals("CAP REQ :sasl", cmd.toString().trim());
		negotiator.onMessage(new GenericMessage(":irc.serv.er CAP * NAK :sasl"));
	}
}

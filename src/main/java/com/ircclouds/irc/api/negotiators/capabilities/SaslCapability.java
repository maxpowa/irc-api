package com.ircclouds.irc.api.negotiators.capabilities;

import com.ircclouds.irc.api.domain.messages.Message;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter;
import com.ircclouds.irc.api.negotiators.CompositeNegotiator;
import com.ircclouds.irc.api.negotiators.SaslContext;
import com.ircclouds.irc.api.negotiators.api.Relay;
import com.ircclouds.irc.api.om.ServerMessageBuilder;
import com.ircclouds.irc.api.utils.RawMessageUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SASL capability.
 *
 * The implementation of SASL capability using IRC server conversation in order
 * to do actual SASL authentication after the capability has been confirmed.
 *
 * TODO introduce exception (should be defined in CompositeNegotiator) that will
 * be thrown in case SASL authentication fails and registration is not allowed
 * to continue after failed SASL.
 *
 * @author Danny van Heumen
 */
public class SaslCapability extends VariousMessageListenerAdapter
		implements CompositeNegotiator.Capability
{
	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SaslCapability.class);

	/**
	 * Capability ID for SASL.
	 */
	private static final String CAP_ID = "sasl";

	/**
	 * Pattern for authentication mechanism confirmation.
	 */
	private static final Pattern AUTHENTICATE_CONFIRMATION = Pattern.compile("AUTHENTICATE\\s+(\\+)\\s*$", 0);

	// AUTHENTICATE numeric replies
	private static final int RPL_LOGGEDIN = 900;
	private static final int RPL_LOGGEDOUT = 901;
	private static final int ERR_NICKLOCKED = 902;
	private static final int RPL_SASLSUCCESS = 903;
	private static final int ERR_SASLFAIL = 904;
	private static final int ERR_SASLTOOLONG = 905;
	private static final int ERR_SASLABORTED = 906;
	private static final int ERR_SASLALREADY = 907;
	private static final int RPL_SASLMECHS = 908;

	/**
	 * Server Numeric Message builder.
	 */
	private static final ServerMessageBuilder SERVER_MSG_BUILDER = new ServerMessageBuilder();

	/**
	 * Negotiated capability state.
	 */
	private final boolean enable;

	/**
	 * Authorization role id.
	 */
	private final String authzId;

	/**
	 * User name.
	 */
	private final String user;

	/**
	 * Password.
	 */
	private final String pass;

	/**
	 * The SASL protocol state.
	 */
	private SaslContext state;

	/**
	 * Constructor.
	 *
	 * @param enable <tt>true</tt> to negotiate enabling, <tt>false</tt> to
	 * negotiate disabling
	 * @param authzid (optional) authorization role id
	 * @param user username
	 * @param pass password
	 */
	public SaslCapability(final boolean enable, final String authzid, final String user, final String pass)
	{
		this.enable = enable;
		this.authzId = authzid;
		if (this.enable && user == null)
		{
			throw new NullPointerException("user");
		}
		this.user = user;
		if (this.enable && pass == null)
		{
			throw new NullPointerException("pass");
		}
		this.pass = pass;
	}

	@Override
	public String getId()
	{
		return CAP_ID;
	}

	@Override
	public boolean enable()
	{
		return this.enable;
	}

	@Override
	public boolean converse(Relay relay, String msg)
	{
		if (!this.enable)
		{
			// Nothing to do, since we're negotiating disabling sasl. By now it
			// is acknowledged that it is disabled, so we're done here.
			return false;
		}
		if (msg == null)
		{
			// start of conversation with IRC server
			this.state = new SaslContext(relay);
			this.state.init();
			return true;
		}
		final Matcher confirmation = AUTHENTICATE_CONFIRMATION.matcher(msg);
		if (confirmation.find())
		{
			this.state.confirm(confirmation.group(1), this.authzId, this.user, this.pass);
			return true;
		}
		else if (RawMessageUtils.isServerNumericMessage(msg))
		{
            final ServerNumeric numMsg = SERVER_MSG_BUILDER.build(new Message(msg));
            switch (numMsg.getNumericCode()) {
                case RPL_LOGGEDIN:
                    this.state.loggedIn();
				return true;
			case RPL_SASLSUCCESS:
				this.state.success();
				return false;
			case ERR_SASLFAIL:
				this.state.fail();
				return true;
			case ERR_NICKLOCKED:
				LOG.error("SASL account locked. Failing authentication procedure.");
				this.state.fail();
				return true;
			case ERR_SASLABORTED:
			case ERR_SASLALREADY:
				// No multiple tries. In case of abort, don't restart conversation.
				return false;
			case ERR_SASLTOOLONG:
				LOG.error("We have sent a bad message. Message was longer than 400 chars. This is a bug in the SASL capability implementation. Aborting SASL authentication.");
				this.state.abort();
				return true;
			default:
				LOG.warn("Unsupported numeric message. Aborting SASL authentication. ({})", msg);
				return false;
			}
		}
		else
		{
			// Case of an unknown message. Assuming that the message was not
			// intended for SASL capability. Hoping for another message that is
			// part of conversation.
			LOG.warn("Unknown message, not handling: " + msg);
			return true;
		}
	}
}

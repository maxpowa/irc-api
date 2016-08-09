package com.ircclouds.irc.api.negotiators.capabilities;

import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.negotiators.CompositeNegotiator;
import com.ircclouds.irc.api.negotiators.SaslContext;
import com.ircclouds.irc.api.negotiators.api.Relay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SASL capability.
 * <p>
 * The implementation of SASL capability using IRC server conversation in order
 * to do actual SASL authentication after the capability has been confirmed.
 * <p>
 * TODO introduce exception (should be defined in CompositeNegotiator) that will
 * be thrown in case SASL authentication fails and registration is not allowed
 * to continue after failed SASL.
 *
 * @author Danny van Heumen
 */
public class SaslCapability implements CompositeNegotiator.Capability {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SaslCapability.class);

    /**
     * Capability ID for SASL.
     */
    private static final String CAP_ID = "sasl";

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
     * @param enable  <tt>true</tt> to negotiate enabling, <tt>false</tt> to
     *                negotiate disabling
     * @param authzid (optional) authorization role id
     * @param user    username
     * @param pass    password
     */
    public SaslCapability(final boolean enable, final String authzid, final String user, final String pass) {
        this.enable = enable;
        this.authzId = authzid;
        if (this.enable && user == null) {
            throw new NullPointerException("user");
        }
        this.user = user;
        if (this.enable && pass == null) {
            throw new NullPointerException("pass");
        }
        this.pass = pass;
    }

    @Override
    public String getId() {
        return CAP_ID;
    }

    @Override
    public boolean enable() {
        return this.enable;
    }

    @Override
    public boolean converse(Relay relay, AbstractMessage msg) {
        if (!this.enable) {
            // Nothing to do, since we're negotiating disabling sasl. By now it
            // is acknowledged that it is disabled, so we're done here.
            return false;
        }
        if (msg == null) {
            // start of conversation with IRC server
            this.state = new SaslContext(relay);
            this.state.init();
            return true;
        }
        if (msg.command.equalsIgnoreCase("AUTHENTICATE")) {
            if (msg.getParams().size() >= 1) {
                this.state.confirm(msg.getParams().get(0), this.authzId, this.user, this.pass);
            }
            return true;
        } else if (msg instanceof ServerNumeric) {
            final ServerNumeric numMsg = (ServerNumeric) msg;
            switch (numMsg.getNumericCode()) {
                case IRCNumerics.RPL_LOGGEDIN:
                    this.state.loggedIn();
                    return true;
                case IRCNumerics.RPL_SASLSUCCESS:
                    this.state.success();
                    return false;
                case IRCNumerics.RPL_SASLMECHS:
                    LOG.warn("SASL mechanisms \"{}\" are available.", msg.getParams().get(1));
                    this.state.init();
                    return true;
                case IRCNumerics.ERR_SASLFAIL:
                    this.state.fail();
                    return true;
                case IRCNumerics.ERR_NICKLOCKED:
                    LOG.error("SASL account locked. Failing authentication procedure.");
                    this.state.fail();
                    return true;
                case IRCNumerics.ERR_SASLABORTED:
                case IRCNumerics.ERR_SASLALREADY:
                    // No multiple tries. In case of abort, don't restart conversation.
                    return false;
                case IRCNumerics.ERR_SASLTOOLONG:
                    LOG.error("Received ERR_SASLTOOLONG, this is a bug in the SASL capability implementation. Aborting SASL authentication.");
                    this.state.abort();
                    return true;
                default:
                    LOG.warn("Unsupported numeric message. Aborting SASL authentication. ({})", msg);
                    return false;
            }
        } else {
            // Case of an unknown message. Assuming that the message was not
            // intended for SASL capability. Hoping for another message that is
            // part of conversation.
            LOG.warn("Unknown message, not handling: " + msg);
            return true;
        }
    }
}

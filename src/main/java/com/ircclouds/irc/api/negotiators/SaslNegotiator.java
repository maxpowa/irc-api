package com.ircclouds.irc.api.negotiators;


import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.commands.CapEndCmd;
import com.ircclouds.irc.api.commands.CapReqCmd;
import com.ircclouds.irc.api.commands.interfaces.ICapCmd;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter;
import com.ircclouds.irc.api.negotiators.api.Relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of a SASL negotiator. This negotiator will negotiate for the
 * 'sasl' extension. If the extension is acknowledged by the IRC server, it will
 * start the authentication procedure and authenticate the user with the
 * credentials provided in the constructor.
 *
 * @author Danny van Heumen
 */
public class SaslNegotiator extends VariousMessageListenerAdapter implements CapabilityNegotiator {
    private static final Logger LOG = LoggerFactory.getLogger(SaslNegotiator.class);

    private static final String SASL_CAPABILITY_ID = "sasl";

    private final String user;
    private final String pass;
    private final String authzid;

    private SaslContext state;
    private IRCApi irc;

    // TODO How to handle time-outs? (though not crucial since IRC server will also time-out)
    public SaslNegotiator(final String user, final String pass, final String authzid) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        this.user = user;
        if (pass == null) {
            throw new IllegalArgumentException("pass cannot be null");
        }
        this.pass = pass;
        this.authzid = authzid;
    }

    @Override
    public ICapCmd initiate(final IRCApi irc) {
        if (irc == null) {
            throw new IllegalArgumentException("irc instance is required");
        }
        this.irc = irc;
        this.state = new SaslContext(new Relay() {

            @Override
            public void send(final String msg) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("CLIENT: {}", msg);
                }
                SaslNegotiator.this.irc.rawMessage(msg);
            }
        });
        return new CapReqCmd(SASL_CAPABILITY_ID);
    }

    @Override
    public void onMessage(AbstractMessage msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SERVER: {}", msg.asRaw());
        }
        List<String> params = msg.getParams();
        String subCommand = "";
        if (params.size() > 1) {
            subCommand = params.get(1);
        }
        try {
            if (subCommand.equalsIgnoreCase("ACK") && params.size() > 1 && saslAcknowledged(params.get(params.size() - 1))) {
                this.state.init();
            } else if (subCommand.equalsIgnoreCase("NAK")) {
                this.irc.rawMessage(new CapEndCmd());
            } else if (msg.getCommand().equalsIgnoreCase("AUTHENTICATE")) {
                if (msg.getParams().size() >= 1) {
                    this.state.confirm(params.get(0), this.authzid, this.user, this.pass);
                }
            } else {
                // IGNORING, currently ...
            }
        } catch (RuntimeException e) {
            LOG.error("Error occurred during CAP negotiation. Prematurely ending CAP negotiation phase and continuing IRC registration as is.", e);
            this.irc.rawMessage(new CapEndCmd());
        }
    }

    private boolean saslAcknowledged(final String acknowledged) {
        final String[] caps = acknowledged.split("\\s+");
        for (String cap : caps) {
            if (SASL_CAPABILITY_ID.equals(cap)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onServerNumericMessage(ServerNumeric msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SERVER: {}", msg.asRaw());
        }
        try {
            switch (msg.getNumericCode()) {
                case IRCNumerics.RPL_LOGGEDIN:
                    this.state.loggedIn();
                    break;
                case IRCNumerics.RPL_SASLSUCCESS:
                    this.state.success();
                    this.irc.rawMessage(new CapEndCmd());
                    break;
                case IRCNumerics.ERR_SASLFAIL:
                    this.state.fail();
                    break;
                case IRCNumerics.ERR_NICKLOCKED:
                    LOG.error("SASL account locked. Aborting authentication procedure.");
                    this.state.abort();
                    this.irc.rawMessage(new CapEndCmd());
                    break;
                case IRCNumerics.ERR_SASLTOOLONG:
                    this.state.abort();
                    break;
                case IRCNumerics.ERR_SASLABORTED:
                case IRCNumerics.ERR_SASLALREADY:
                    this.irc.rawMessage(new CapEndCmd());
                    break;
                default:
                    break;
            }
        } catch (RuntimeException e) {
            LOG.error("Error occurred during CAP negotiation. Ending CAP negotiation phase and continuing registration as is.", e);
            this.irc.rawMessage(new CapEndCmd());
        }
    }
}

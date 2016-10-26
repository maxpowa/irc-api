package com.ircclouds.irc.api.negotiators.capabilities;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.negotiators.CompositeNegotiator.Capability;
import com.ircclouds.irc.api.negotiators.api.Relay;
import com.ircclouds.irc.api.om.ServerMessageBuilder;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TLS capability.
 *
 * @author Danny van Heumen
 */
public class TlsCapability implements Capability {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TlsCapability.class);

    // FIXME probably not the prettiest solution for our needs
    private final IRCApi irc;

    public TlsCapability(final IRCApi aIrc) {
        if (aIrc == null) {
            throw new NullPointerException("IRC api instance must be provided.");
        }
        this.irc = aIrc;
    }

    @Override
    public String getId() {
        return "tls";
    }

    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public boolean converse(Relay relay, AbstractMessage msg) {
        if (msg == null) {
            relay.send("STARTTLS");
            // FIXME also ensure that original message readers stops reading after 670 response
            return true;
        }
        if (!(msg instanceof ServerNumeric)) {
            LOG.error("Unexpected message encountered. Aborting securing connection with TLS.");
            return false;
        }
        ServerNumeric numMsg = (ServerNumeric) msg;
        switch (numMsg.getNumericCode()) {
            case IRCNumerics.RPL_STARTTLS:
                // FIXME call back to user code for starting secure connection
                try {
                    this.irc.secureConnection(null, ((IRCServer) numMsg.getSource()).getHostname(), ((IRCServer) numMsg.getSource()).getPort());
                } catch (SSLException e) {
                    LOG.error("error starting handshake", e);
                }
                return false;
            case IRCNumerics.ERR_STARTTLS:
                // FIXME abort connection with exception
                return false;
            default:
                LOG.error("Unsupported numeric message from server: {}", numMsg.asRaw());
                return false;
        }
    }
}

package com.ircclouds.irc.api.domain.messages;

import java.text.*;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

/**
 * @author
 */
public class ServerNumeric extends Message implements IServerMessage, IHasText, IHasNumericCode {
    private IRCServer server;

    public ServerNumeric(Message message) {
        super(message);

        this.server = this.prefix != null ? new IRCServer(this.prefix) : null;
    }

    private Integer getNumberFrom(String aString) {
        try {
            return Integer.parseInt(aString);
        } catch (NumberFormatException aExc) {
            return 0;
        }
    }

    public Integer getNumericCode() {
        return getNumberFrom(this.command);
    }

    @Override
    public IRCServer getSource() {
        return server;
    }

    @Override
    public String asRaw() {
        return new StringBuffer().append(":").append(server.getHostname()).append(" ").append(this.command).append(" ").append(this.params.get(0)).append(" ").append(this.getText()).toString();
    }
}

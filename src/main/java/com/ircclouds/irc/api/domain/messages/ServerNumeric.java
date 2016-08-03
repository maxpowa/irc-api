package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

/**
 * @author
 */
public class ServerNumeric extends AbstractMessage {

    public ServerNumeric(AbstractMessage message) {
        super(message);
    }

    public Integer getNumericCode() {
        try {
            return Integer.parseInt(this.command);
        } catch (NumberFormatException aExc) {
            return 0;
        }
    }
}

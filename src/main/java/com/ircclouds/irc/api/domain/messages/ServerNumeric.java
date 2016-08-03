package com.ircclouds.irc.api.domain.messages;

public class ServerNumeric extends AbstractMessage {

    private int numeric;

    public ServerNumeric(AbstractMessage message) {
        super(message);
        this.parseCommand();
    }

    private void parseCommand() {
        try {
            numeric = Integer.parseInt(this.command);
        } catch (NumberFormatException aExc) {
            throw new ParseError("Expected command to be numeric");
        }
    }

    public Integer getNumericCode() {
        return numeric;
    }
}

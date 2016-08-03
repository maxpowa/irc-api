package com.ircclouds.irc.api.domain.messages;

public class ServerPong extends AbstractMessage {
    private static final String PONG = "PONG";
    private static final String SPACE = " ";
    private static final String COLUMN = ":";

    public ServerPong(AbstractMessage message) {
        super(message);
    }

    public String toString() {
        return PONG + SPACE + COLUMN + this.getText();
    }
}

package com.ircclouds.irc.api.domain.messages;

/**
 * @author
 */
public class ChannelCTCP extends ChannelPrivMsg {
    public ChannelCTCP(AbstractMessage message) {
        super(message);
    }

    public String getText() {
        // Get the text between the NULs
        return super.getText().substring(1, super.getText().length() - 1);
    }
}

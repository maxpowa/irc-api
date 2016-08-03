package com.ircclouds.irc.api.domain.messages;

/**
 * @author
 */
public class ChannelPing extends ChannelCTCP {
    public ChannelPing(AbstractMessage message) {
        super(message);
    }

    @Override
    public String getText() {
        return super.getText().replaceFirst("(?i)PING", "").trim();
    }
}

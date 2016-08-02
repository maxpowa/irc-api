package com.ircclouds.irc.api.domain.messages;

/**
 * @author
 */
public class ChannelVersion extends ChannelCTCP {
    public ChannelVersion(Message message) {
        super(message);
    }

    @Override
    public String getText() {
        return super.getText().replaceFirst("(?i)VERSION", "").trim();
    }
}

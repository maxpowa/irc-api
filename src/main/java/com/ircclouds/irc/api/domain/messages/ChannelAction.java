package com.ircclouds.irc.api.domain.messages;

public class ChannelAction extends ChannelCTCP {
    public ChannelAction(AbstractMessage message) {
        super(message);
    }

    @Override
    public String getText() {
        return super.getText().replaceFirst("(?i)ACTION", "").trim();
    }
}

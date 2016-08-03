package com.ircclouds.irc.api.domain.messages;

public class UserAction extends UserCTCP {
    public UserAction(AbstractMessage message) {
        super(message);
    }

    @Override
    public String getText() {
        return super.getText().replaceFirst("(?i)ACTION", "").trim();
    }
}

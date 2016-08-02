package com.ircclouds.irc.api.domain.messages;

public class UserAction extends UserPrivMsg {
    public UserAction(Message message) {
        super(message);
    }

    @Override
    public String getText() {
        return super.getText().replaceFirst("(?i)ACTION", "").trim();
    }
}

package com.ircclouds.irc.api.domain.messages;

/**
 * @author
 */
public class UserCTCP extends UserPrivMsg {
    public UserCTCP(Message message) {
        super(message);
    }

    public String getText() {
        // Get the text between the NULs
        return super.getText().substring(1, super.getText().length() - 1);
    }
}

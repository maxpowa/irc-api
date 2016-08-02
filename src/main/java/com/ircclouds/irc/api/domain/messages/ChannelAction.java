package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;

public class ChannelAction extends ChannelPrivMsg {
    public ChannelAction(Message message) {
        super(message);
    }

    @Override
    public String getText() {
        return super.getText().replaceFirst("(?i)ACTION", "").trim();
    }
}

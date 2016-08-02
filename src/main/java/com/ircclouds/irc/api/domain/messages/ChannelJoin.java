package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.ParseUtils;

/**
 * @author
 */
public class ChannelJoin extends Message implements IChannelMessage, IUserMessage {
    public ChannelJoin(Message message) {
        super(message);
    }

    public String getChannelName() {
        return this.params.get(0);
    }

    public IRCUser getSource() {
        return ParseUtils.getUser(this.prefix);
    }

    @Override
    public String asRaw() {
        return new StringBuffer(":").append(this.getSource()).append(" JOIN ").append(this.getChannelName()).toString();
    }
}

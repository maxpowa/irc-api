package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.ParseUtils;

/**
 * @author
 */
public class ChannelPart extends Message implements IChannelMessage, IUserMessage {

    public ChannelPart(Message message) {
        super(message);
    }

    public String getText() {
        return this.params.size() > 1 ? super.getText() : null;
    }

    public String getChannelName() {
        return this.params.get(0);
    }

    public IRCUser getSource() {
        return ParseUtils.getUser(this.prefix);
    }

    @Override
    public String asRaw() {
        return new StringBuffer(":").append(this.getSource()).append(" PART ").append(this.getChannelName())
                .append(this.getText() == null ? "" : " :" + this.getText()).toString();
    }
}

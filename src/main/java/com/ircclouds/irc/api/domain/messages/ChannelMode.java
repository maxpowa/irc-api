package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.messages.interfaces.IMessage;
import com.ircclouds.irc.api.domain.messages.interfaces.ISource;
import com.ircclouds.irc.api.utils.StringUtils;

import java.util.List;

/**
 * @author
 */
public class ChannelMode implements IMessage {
    private ISource user;
    private String channel;
    private String modeStr;
    private List<com.ircclouds.irc.api.domain.ChannelMode> addedModes;
    private List<com.ircclouds.irc.api.domain.ChannelMode> removedModes;

    public ChannelMode(ISource aUser, String aChanName, String aModeStr, List<com.ircclouds.irc.api.domain.ChannelMode> aAddedModes, List<com.ircclouds.irc.api.domain.ChannelMode> aRemModes) {
        user = aUser;
        channel = aChanName;
        modeStr = aModeStr;
        addedModes = aAddedModes;
        removedModes = aRemModes;
    }

    public String getChannelName() {
        return channel;
    }

    public ISource getSource() {
        return user;
    }

    public List<com.ircclouds.irc.api.domain.ChannelMode> getAddedModes() {
        return addedModes;
    }

    public List<com.ircclouds.irc.api.domain.ChannelMode> getRemovedModes() {
        return removedModes;
    }

    public String getModeStr() {
        return modeStr;
    }

    @Override
    public String asRaw() {
        // TODO: Account for modes with parameters
        StringBuilder sb = new StringBuilder(":").append(user).append(" MODE ").append(channel).append(" ");
        if (addedModes.size() > 0) {
            sb.append("+").append(StringUtils.join(addedModes));
        }
        if (removedModes.size() > 0) {
            sb.append("-").append(StringUtils.join(removedModes));
        }
        return sb.toString();
    }
}

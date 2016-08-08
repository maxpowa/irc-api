package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ChannelAction;
import com.ircclouds.irc.api.domain.messages.ChannelJoin;
import com.ircclouds.irc.api.domain.messages.ChannelKick;
import com.ircclouds.irc.api.domain.messages.ChannelMode;
import com.ircclouds.irc.api.domain.messages.ChannelNotice;
import com.ircclouds.irc.api.domain.messages.ChannelPart;
import com.ircclouds.irc.api.domain.messages.ChannelPrivMsg;
import com.ircclouds.irc.api.domain.messages.ChannelTopic;
import com.ircclouds.irc.api.domain.messages.ClientErrorMessage;
import com.ircclouds.irc.api.domain.messages.ServerError;
import com.ircclouds.irc.api.domain.messages.ServerNotice;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.domain.messages.ServerPing;
import com.ircclouds.irc.api.domain.messages.UserAction;
import com.ircclouds.irc.api.domain.messages.UserAwayMessage;
import com.ircclouds.irc.api.domain.messages.UserNickMessage;
import com.ircclouds.irc.api.domain.messages.UserNotice;
import com.ircclouds.irc.api.domain.messages.UserPing;
import com.ircclouds.irc.api.domain.messages.UserPrivMsg;
import com.ircclouds.irc.api.domain.messages.UserQuitMessage;
import com.ircclouds.irc.api.domain.messages.UserVersion;

public class VariousMessageListenerAdapter implements IVariousMessageListener
{
	@Override
	public void onUserPing(UserPing aMsg)
	{
	}

	@Override
	public void onUserVersion(UserVersion aMsg)
	{
	}

	@Override
	public void onServerPing(ServerPing aMsg)
	{
	}

	@Override
	public void onMessage(AbstractMessage aMessage)
	{
	}

	@Override
	public void onChannelMessage(ChannelPrivMsg aMsg)
	{
	}

	@Override
    public void onChannelJoin(ChannelJoin aMsg) {
    }

	@Override
    public void onChannelPart(ChannelPart aMsg) {
    }

	@Override
	public void onChannelNotice(ChannelNotice aMsg)
	{
	}

	@Override
    public void onChannelAction(ChannelAction aMsg) {
    }

	@Override
	public void onChannelKick(ChannelKick aMsg)
	{
	}

	@Override
	public void onTopicChange(ChannelTopic aMsg)
	{
	}

	@Override
	public void onUserPrivMessage(UserPrivMsg aMsg)
	{
	}

	@Override
	public void onUserNotice(UserNotice aMsg)
	{
	}

	@Override
    public void onUserAction(UserAction aMsg) {
    }

	@Override
    public void onServerNumericMessage(ServerNumeric aMsg) {
    }

	@Override
	public void onServerNotice(ServerNotice aMsg)
	{
	}

	@Override
	public void onNickChange(UserNickMessage aMsg)
	{
	}

	@Override
	public void onUserQuit(UserQuitMessage aMsg)
	{
	}

	@Override
	public void onError(ServerError aMsg)
	{
	}

	@Override
	public void onClientError(ClientErrorMessage aMsg)
	{
	}

	@Override
    public void onChannelMode(ChannelMode aMsg) {
    }

	@Override
	public void onUserAway(UserAwayMessage aMsg)
	{
	}
}

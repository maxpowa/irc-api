package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;

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
	public void onMessage(IMessage aMessage)
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

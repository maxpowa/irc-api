package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.domain.messages.*;

public interface IVariousMessageListener extends IMessageListener
{
	void onChannelMessage(ChannelPrivMsg aMsg);

    void onChannelJoin(ChannelJoin aMsg);

    void onChannelPart(ChannelPart aMsg);

    void onChannelNotice(ChannelNotice aMsg);

    void onChannelAction(ChannelAction aMsg);

    void onChannelKick(ChannelKick aMsg);

    void onTopicChange(ChannelTopic aMsg);

    void onUserPrivMessage(UserPrivMsg aMsg);

    void onUserNotice(UserNotice aMsg);

    void onUserAction(UserAction aMsg);

    void onServerNumericMessage(ServerNumeric aMsg);

    void onServerNotice(ServerNotice aMsg);

    void onNickChange(UserNickMessage aMsg);

    void onUserQuit(UserQuitMessage aMsg);

    void onError(ServerError aMsg);

	void onClientError(ClientErrorMessage aMsg);

    void onChannelMode(ChannelMode aMsg);

    void onUserPing(UserPing aMsg);

    void onUserVersion(UserVersion aMsg);

    void onServerPing(ServerPing aMsg);

	/**
	 * Event for user away change notifications. (capability: away-notify)
	 *
	 * This event signals a change in the away status of a channel member. If an
	 * away message is set this means that a channel member is away. If no away
	 * message is set, this means that the user just got back. This event will
	 * only be used if capability away-notify (IRCv3) is enabled.
	 *
	 * @param aMsg the away message
	 */
	void onUserAway(UserAwayMessage aMsg);
}

package com.ircclouds.irc.api.listeners;

import java.util.*;

import com.ircclouds.irc.api.*;
import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChannelJoinListener
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractChannelJoinListener.class);

	private final Map<String, Callback<IRCChannel>> callbacks = new HashMap<String, Callback<IRCChannel>>();

	private WritableIRCChannel channel;
	private WritableIRCTopic topic;

	public void submit(String aChannelName, Callback<IRCChannel> aCallback)
	{
		callbacks.put(aChannelName, aCallback);
	}

    public void onChanJoinMessage(ChannelJoin aMsg) {
        saveChannel(channel = new WritableIRCChannel(aMsg.getChannelName()));
    }

    public void onServerMessage(ServerNumeric aServerMessage) {
        int _numcode = aServerMessage.getNumericCode();
        if (_numcode == IRCServerNumerics.CHANNEL_FORWARD || _numcode == IRCServerNumerics.TOPIC_USER_DATE || _numcode == IRCServerNumerics.CHANNEL_NICKS_LIST
                || _numcode == IRCServerNumerics.CHANNEL_TOPIC || _numcode == IRCServerNumerics.CHANNEL_NICKS_END_OF_LIST
				|| _numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_INVITE || _numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_KEYED
				|| _numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_FULL || _numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_BANNED)
		{
			if (channel != null)
			{
				if (_numcode == IRCServerNumerics.CHANNEL_NICKS_LIST)
				{
                    String _nicks[] = aServerMessage.getText().split(" "); // reviewed
                    for (String _nick : _nicks) {
                        add(_nick);
                    }
				}
				else if (_numcode == IRCServerNumerics.CHANNEL_TOPIC)
				{
					topic = new WritableIRCTopic(getTopic(aServerMessage));
				}
				else if (_numcode == IRCServerNumerics.TOPIC_USER_DATE)
				{
                    topic.setSetBy(aServerMessage.params.get(2));
                    topic.setDate(new Date(Long.parseLong(aServerMessage.params.get(3) + "000")));
                    channel.setTopic(topic);
                } else if (_numcode == IRCServerNumerics.CHANNEL_NICKS_END_OF_LIST) {
					Callback<IRCChannel> _chanCallback = callbacks.remove(channel.getName());
					if (_chanCallback != null)
					{
						_chanCallback.onSuccess(channel);
					}
					channel = null;
					topic = null;
				}
            } else if (callbacks.containsKey(aServerMessage.params.get(0))) {
                if (_numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_INVITE) {
                    callbacks.remove(aServerMessage.params.get(0)).onFailure(new IRCException(aServerMessage.getText()));
                } else if (_numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_KEYED) {
                    callbacks.remove(aServerMessage.params.get(0)).onFailure(new IRCException(aServerMessage.getText()));
                } else if (_numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_BANNED) {
                    callbacks.remove(aServerMessage.params.get(0)).onFailure(new IRCException(aServerMessage.getText()));
                } else if (_numcode == IRCServerNumerics.CHANNEL_CANNOT_JOIN_FULL) {
                    callbacks.remove(aServerMessage.params.get(0)).onFailure(new IRCException(aServerMessage.getText()));
                } else if (_numcode == IRCServerNumerics.CHANNEL_FORWARD) {
                    Callback<IRCChannel> callback = callbacks.remove(aServerMessage.params.get(1));
                    if (callback != null) {
                        callbacks.put(aServerMessage.params.get(1), callback);
                    }
                }
            }
        }
	}

	protected abstract void saveChannel(WritableIRCChannel aChannel);

	protected abstract IRCUserStatuses getIRCUserStatuses();

	private void add(String aNick)
	{
		final Map<Character, IRCUserStatus> statuses = mapPrefixes(getIRCUserStatuses());
		final HashSet<IRCUserStatus> active = new HashSet<IRCUserStatus>();
		IRCUser user = null;
		for (int i = 0; i < aNick.length(); i++)
		{
			char p = aNick.charAt(i);
			if (!statuses.containsKey(p))
			{
				user = new WritableIRCUser(aNick.substring(i));
				break;
			}
			active.add(statuses.get(p));
		}
		if (user == null)
		{
			LOG.debug("Skipping user {}: not able to extract a valid nick name.", aNick);
			return;
		}
		channel.addUser(user, new SynchronizedUnmodifiableSet<IRCUserStatus>(active));
	}

    private String getTopic(ServerNumeric aServMsg) {
        return aServMsg.getText().substring(aServMsg.getText().indexOf(":") + 1);
    }

	private static Map<Character, IRCUserStatus> mapPrefixes(final IRCUserStatuses statuses) {
		final HashMap<Character, IRCUserStatus> map = new HashMap<Character, IRCUserStatus>();
		for (IRCUserStatus status : statuses)
		{
			map.put(status.getPrefix(), status);
		}
		return map;
	}
}

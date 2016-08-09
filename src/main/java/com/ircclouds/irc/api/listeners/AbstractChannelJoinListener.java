package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.IRCException;
import com.ircclouds.irc.api.domain.IRCChannel;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.IRCUser;
import com.ircclouds.irc.api.domain.IRCUserStatus;
import com.ircclouds.irc.api.domain.IRCUserStatuses;
import com.ircclouds.irc.api.domain.WritableIRCChannel;
import com.ircclouds.irc.api.domain.WritableIRCTopic;
import com.ircclouds.irc.api.domain.WritableIRCUser;
import com.ircclouds.irc.api.domain.messages.ChannelJoin;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.interfaces.Callback;
import com.ircclouds.irc.api.utils.SynchronizedUnmodifiableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class AbstractChannelJoinListener
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractChannelJoinListener.class);

    private static List<Integer> NUMERIC_FILTER = Arrays.asList(
            IRCNumerics.ERR_LINKCHANNEL,
            IRCNumerics.RPL_TOPICWHOTIME,
            IRCNumerics.RPL_NAMREPLY,
            IRCNumerics.RPL_TOPIC,
            IRCNumerics.RPL_ENDOFNAMES,
            IRCNumerics.ERR_INVITEONLYCHAN,
            IRCNumerics.ERR_BADCHANNELKEY,
            IRCNumerics.ERR_CHANNELISFULL,
            IRCNumerics.ERR_BANNEDFROMCHAN
    );

	private final Map<String, Callback<IRCChannel>> callbacks = new HashMap<String, Callback<IRCChannel>>();

	private WritableIRCChannel channel;
	private WritableIRCTopic topic;

	public void submit(String aChannelName, Callback<IRCChannel> aCallback)
	{
		callbacks.put(aChannelName.toLowerCase(), aCallback);
	}

    public void onChanJoinMessage(ChannelJoin aMsg) {
        saveChannel(channel = new WritableIRCChannel(aMsg.getChannelName()));
    }

    public void onServerMessage(ServerNumeric aServerMessage) {
        int _numcode = aServerMessage.getNumericCode();

        // If the filter doesn't have the numcode in this message, we're just gonna return.
        if (!NUMERIC_FILTER.contains(_numcode)) return;

        // Are we in the process of joining a channel?
        if (channel != null) {
            switch (_numcode) {
                case IRCNumerics.RPL_NAMREPLY: {
                    String _nicks[] = aServerMessage.getText().split(" "); // reviewed
                    for (String _nick : _nicks) {
                        add(_nick);
                    }
                    break;
                }
                case IRCNumerics.RPL_TOPIC: {
                    topic = new WritableIRCTopic(aServerMessage.getText());
                    break;
                }
                case IRCNumerics.RPL_TOPICWHOTIME: {
                    topic.setSetBy(aServerMessage.params.get(2));
                    topic.setDate(new Date(Long.parseLong(aServerMessage.params.get(3) + "000")));
                    channel.setTopic(topic);
                    break;
                }
                case IRCNumerics.RPL_ENDOFNAMES: {
                    Callback<IRCChannel> _chanCallback = callbacks.remove(channel.getName().toLowerCase());
                    if (_chanCallback != null)
                    {
                        _chanCallback.onSuccess(channel);
                    }
                    channel = null;
                    topic = null;
                    break;
                }
            }
        } else if (_numcode == IRCNumerics.ERR_LINKCHANNEL) {
            // return if channel we were forwarded from isn't in callbacks
            if (!callbacks.containsKey(aServerMessage.params.get(1).toLowerCase())) return;

            Callback<IRCChannel> callback = callbacks.remove(aServerMessage.params.get(1).toLowerCase());
            if (callback != null) {
                callbacks.put(aServerMessage.params.get(2).toLowerCase(), callback);
            }
        } else if (callbacks.containsKey(aServerMessage.params.get(0).toLowerCase())) {

            String channelName = aServerMessage.params.get(0).toLowerCase();
            switch (_numcode) {
                case IRCNumerics.ERR_CHANNELISFULL:
                case IRCNumerics.ERR_BANNEDFROMCHAN:
                case IRCNumerics.ERR_BADCHANNELKEY:
                case IRCNumerics.ERR_INVITEONLYCHAN:
                    callbacks.remove(channelName).onFailure(new IRCException(aServerMessage.getText()));
                    break;

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

	private static Map<Character, IRCUserStatus> mapPrefixes(final IRCUserStatuses statuses) {
		final HashMap<Character, IRCUserStatus> map = new HashMap<Character, IRCUserStatus>();
		for (IRCUserStatus status : statuses)
		{
			map.put(status.getPrefix(), status);
		}
		return map;
	}
}

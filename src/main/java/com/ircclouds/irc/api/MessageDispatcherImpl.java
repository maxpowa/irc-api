package com.ircclouds.irc.api;

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
import com.ircclouds.irc.api.filters.HowMany;
import com.ircclouds.irc.api.filters.TargetListeners;
import com.ircclouds.irc.api.listeners.IMessageListener;
import com.ircclouds.irc.api.listeners.IVariousMessageListener;
import com.ircclouds.irc.api.listeners.Visibility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MessageDispatcherImpl implements IMessageDispatcher
{
	private static final Logger LOG = LoggerFactory.getLogger(MessageDispatcherImpl.class);
	
	private final Map<Visibility, List<IMessageListener>> listenersMap = new EnumMap<Visibility, List<IMessageListener>>(Visibility.class);
	
	public MessageDispatcherImpl()
	{
		listenersMap.put(Visibility.PRIVATE, new ArrayList<IMessageListener>());
		listenersMap.put(Visibility.PUBLIC, new ArrayList<IMessageListener>());
	}

	@Override
	public void dispatch(AbstractMessage aMessage, TargetListeners aTargetListeners)
	{
		if (aTargetListeners.getHowMany().equals(HowMany.ALL))
		{
			dispatchTo(aMessage, new ArrayList<IMessageListener>(listenersMap.get(Visibility.PUBLIC)));
		}
		else
		{
			dispatchTo(aMessage, aTargetListeners.getListeners());
		}
	}

	@Override
	public void dispatchToPrivateListeners(AbstractMessage aMessage)
	{
		dispatchTo(aMessage, new ArrayList<IMessageListener>(listenersMap.get(Visibility.PRIVATE)));
	}	
	
	@Override
	public void register(IMessageListener aListener, Visibility aVisibility)
	{
		listenersMap.get(aVisibility).add(aListener);
	}

	@Override
	public void unregister(IMessageListener aListener)
	{
		listenersMap.get(Visibility.PRIVATE).remove(aListener);
		listenersMap.get(Visibility.PUBLIC).remove(aListener);
	}
	
	private void dispatchTo(AbstractMessage aMessage, List<IMessageListener> aListeners)
	{
		for (IMessageListener _listener : aListeners)
		{
			//LOG.debug("Dispatching " + aMessage.getClass().getSimpleName() + " to " +  _listener.getClass().getSimpleName());
			try
			{
				if (_listener instanceof IVariousMessageListener)
				{
					dispatchVarious((IVariousMessageListener) _listener, aMessage);
				}
				else
				{
					_listener.onMessage(aMessage);
				}
			}
			catch (Exception aExc)
			{
				LOG.error("", aExc);
			}
		}
	}

	private void dispatchVarious(IVariousMessageListener aListener, AbstractMessage aMessage)
	{
        if (aMessage instanceof ChannelJoin) {
            aListener.onChannelJoin((ChannelJoin) aMessage);
        } else if (aMessage instanceof ChannelPart) {
            aListener.onChannelPart((ChannelPart) aMessage);
        } else if (aMessage instanceof ChannelNotice) {
            aListener.onChannelNotice((ChannelNotice) aMessage);
        } else if (aMessage instanceof ChannelAction) {
            aListener.onChannelAction((ChannelAction) aMessage);
        } else if (aMessage instanceof ChannelKick) {
            aListener.onChannelKick((ChannelKick) aMessage);
		}
		else if (aMessage instanceof ChannelPrivMsg)
		{
			aListener.onChannelMessage((ChannelPrivMsg) aMessage);
		}
		else if (aMessage instanceof ChannelTopic)
		{
			aListener.onTopicChange((ChannelTopic) aMessage);
		}
		else if (aMessage instanceof UserPrivMsg)
		{
			if (aMessage instanceof UserVersion)
			{
				aListener.onUserVersion((UserVersion) aMessage);
			}
			else if (aMessage instanceof UserPing)
			{
				aListener.onUserPing((UserPing) aMessage);
            } else if (aMessage instanceof UserAction) {
                aListener.onUserAction((UserAction) aMessage);
            } else {
                aListener.onUserPrivMessage((UserPrivMsg) aMessage);
			}
		}
		else if (aMessage instanceof UserNotice)
		{
			aListener.onUserNotice((UserNotice) aMessage);
        } else if (aMessage instanceof ServerNumeric) {
            aListener.onServerNumericMessage((ServerNumeric) aMessage);
        } else if (aMessage instanceof ServerNotice) {
            aListener.onServerNotice((ServerNotice) aMessage);
		}
		else if (aMessage instanceof UserNickMessage)
		{
			aListener.onNickChange((UserNickMessage) aMessage);
		}
		else if (aMessage instanceof UserQuitMessage)
		{
			aListener.onUserQuit((UserQuitMessage) aMessage);
		}
		else if (aMessage instanceof ServerError)
		{
			aListener.onError((ServerError) aMessage);
		}
		else if (aMessage instanceof ClientErrorMessage)
		{
			aListener.onClientError((ClientErrorMessage) aMessage);
        } else if (aMessage instanceof ChannelMode) {
            aListener.onChannelMode((ChannelMode) aMessage);
        } else if (aMessage instanceof ServerPing) {
            aListener.onServerPing((ServerPing) aMessage);
		}
		else if (aMessage instanceof UserAwayMessage)
		{
			aListener.onUserAway((UserAwayMessage) aMessage);
		}
		else
		{
			aListener.onMessage(aMessage);
		}
	}
}

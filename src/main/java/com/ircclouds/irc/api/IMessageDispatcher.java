package com.ircclouds.irc.api;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.listeners.IMessageListener;
import com.ircclouds.irc.api.listeners.Visibility;

public interface IMessageDispatcher
{
	void dispatch(AbstractMessage aMessage);

	void dispatchToPrivateListeners(AbstractMessage aMessage);
	
	void register(IMessageListener aListener, Visibility aVisibility);

	void unregister(IMessageListener aListener);
}

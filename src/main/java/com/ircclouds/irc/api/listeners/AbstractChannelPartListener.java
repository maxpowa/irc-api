package com.ircclouds.irc.api.listeners;

import java.util.*;

import com.ircclouds.irc.api.*;
import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;

public abstract class AbstractChannelPartListener
{
	private Map<String, Callback<String>> callbacks = new HashMap<String, Callback<String>>();
		
	public void submit(String aChannelName, Callback<String> aCallback)
	{
		callbacks.put(aChannelName, aCallback);
	}

    public void onChannelPart(ChannelPart aMsg) {
        Callback<String> _callback = callbacks.remove(aMsg.getChannelName());
        if (_callback != null) {
			_callback.onSuccess(aMsg.getChannelName());
		}
		
		deleteChannel(aMsg.getChannelName());
	}

    public void onServerMessage(ServerNumeric aServerMessage) {
        if (aServerMessage.getNumericCode() == IRCNumerics.ERR_NOSUCHCHANNEL) {
            String _chan = aServerMessage.params.get(0);
            if (callbacks.containsKey(_chan)) {
                callbacks.remove(_chan).onFailure(new IRCException(aServerMessage.getText()));
            }
		}
	}
	
	protected abstract void deleteChannel(String aChannelName);
}

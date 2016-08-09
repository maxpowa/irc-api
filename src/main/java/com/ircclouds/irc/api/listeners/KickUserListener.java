package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.IRCException;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.messages.ChannelKick;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.interfaces.Callback;

import java.util.HashMap;
import java.util.Map;

public abstract class KickUserListener
{
	private Map<String, Callback<String>> callbacks = new HashMap<String, Callback<String>>();
	
	public void onChannelKick(ChannelKick aChanKick)
	{
		if (callbacks.containsKey(aChanKick))
		{
			callbacks.get(aChanKick).onSuccess("");
			delChanUser(aChanKick.getChannelName(), aChanKick.getKickedNickname());
		}
	}

    public void onServerMessage(ServerNumeric aServerMessage) {
        switch (aServerMessage.getNumericCode()) {
            case IRCNumerics.ERR_NOSUCHCHANNEL:
            case IRCNumerics.ERR_CHANOPRIVSNEEDED:
                String _chan = aServerMessage.params.get(0);
                if (callbacks.containsKey(_chan)) {
                    callbacks.remove(_chan).onFailure(new IRCException(aServerMessage.getText()));
                }
		}
	}

	public void submit(String aChannel, Callback<String> aCallback)
	{
		callbacks.put(aChannel, aCallback);
	}
	
	protected abstract void delChanUser(String aChan, String aUser);
}

package com.ircclouds.irc.api.listeners;

import java.util.*;

import com.ircclouds.irc.api.*;
import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;

public abstract class AbstractNickChangeListener
{
	private Map<String, Callback<String>> callbacks = new HashMap<String, Callback<String>>();
	
	protected abstract void changeNick(String aNewNick);

	public void submit(String aNewNick, Callback<String> aCallback)
	{
		callbacks.put(aNewNick, aCallback);
	}

	public void onNickChange(UserNickMessage aMsg)
	{
		Callback<String> _callback = callbacks.get(aMsg.getNewNick());
		if (_callback != null) {
			_callback.onSuccess(aMsg.getNewNick());
		} else {
			changeNick(aMsg.getNewNick());
		}
	}

	public void onServerMessage(ServerNumeric aServerMessage) {
		Callback<String> _callback = callbacks.remove(aServerMessage.params.get(0));
		if (_callback != null) {
			switch (aServerMessage.getNumericCode()) {
				case IRCNumerics.ERR_NICKNAMEINUSE:
				case IRCNumerics.ERR_ERRONEUSNICKNAME:
				case IRCNumerics.ERR_NICKTOOFAST:
					_callback.onFailure(new IRCException(aServerMessage.getText()));
					break;

			}
		}
	}
}

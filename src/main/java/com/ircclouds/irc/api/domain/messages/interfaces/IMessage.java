package com.ircclouds.irc.api.domain.messages.interfaces;

import java.io.Serializable;
import java.util.List;

@Deprecated
public interface IMessage extends Serializable
{	
	ISource getSource();

	List<String> getParams();

    String getCommand();

    String asRaw();
}

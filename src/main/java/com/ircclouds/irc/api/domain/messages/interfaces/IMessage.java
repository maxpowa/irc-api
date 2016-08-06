package com.ircclouds.irc.api.domain.messages.interfaces;

import java.io.*;
import java.util.List;

public interface IMessage extends Serializable
{	
	ISource getSource();

	List<String> getParams();
	
	String asRaw();
}

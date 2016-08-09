package com.ircclouds.irc.api.state;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.domain.IRCChannel;
import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.domain.IRCServerOptions;

import java.util.List;

/**
 * This interface represents a view about the currently established IRC connection state, and allows retrieval 
 * of various IRC server options like channels modes, user statuses, and more, once
 * {@link IRCApi#IRCApi(Boolean)} is set to true.
 * 
 * @author miguel@lebane.se
 *
 */
public interface IIRCState
{
	String getNickname();

	List<String> getAltNicks();

	String getRealname();

	String getIdent();

	List<IRCChannel> getChannels();

	IRCChannel getChannelByName(String aChannelName);

	IRCServer getServer();

	IRCServerOptions getServerOptions();
	
	boolean isConnected();
	
	IIRCState getPrevious();
}

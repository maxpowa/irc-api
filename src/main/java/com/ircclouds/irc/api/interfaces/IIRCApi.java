package com.ircclouds.irc.api.interfaces;

import com.ircclouds.irc.api.commands.interfaces.ICommand;
import com.ircclouds.irc.api.dcc.DCCManager;
import com.ircclouds.irc.api.domain.IRCChannel;
import com.ircclouds.irc.api.negotiators.CapabilityNegotiator;
import com.ircclouds.irc.api.state.IIRCState;

import net.engio.mbassy.bus.MBassador;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.File;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/** 
 * The main interface of IRC-API, where all IRC methods are defined.
 * 
 * There are 2 types of IRC methods available, synchronous and asynchronous.  When the type is asynchronous, a callback should be provided.
 * 
 * Moreover, this interface accepts IRC message listeners/filters, and offers a useful set of DCC Commands.
 * 
 *  @author miguel@lebane.se  
 */
@SuppressWarnings("unused")
public interface IIRCApi
{
	/**
	 * Asynchronous connect
	 *
	 * Connect to an IRC server without enabling IRCv3.
	 *
	 * @param aServerParameters
	 *            The IRC Server connection parameters
	 * @param aCallback
	 *            A callback that will be invoked when the connection is
	 *            established, and will return an {@link IIRCState} on success,
	 *            or an {@link Exception} in case of failure
	 */
	void connect(IServerParameters aServerParameters, Callback<IIRCState> aCallback);

	/**
	 * Asynchronous connect
	 *
	 * Connect to an IRC server with ability to enable IRCv3 and (optionally)
	 * negotiate for capabilities.
	 *
	 * @param aServerParameters
	 *            The IRC Server connection parameters
	 * @param aCallback
	 *            A callback that will be invoked when the connection is
	 *            established, and will return an {@link IIRCState} on success,
	 *            or an {@link Exception} in case of failure
	 * @param negotiator
	 *            CAP negotiator instance used when establishing the IRC
	 *            connection. If <code>null</code> instance is provided, then
	 *            capability negotiation is not started and IRCv3 will not be
	 *            available.<br/>
	 *            Please refer to available negotiators in package
	 *            {@link com.ircclouds.irc.api.negotiators} for various options.
	 *            The {@link com.ircclouds.irc.api.negotiators.NoopNegotiator}
	 *            is useful for case where you want to let the IRC server know
	 *            that IRCv3 support is available, but no capabilities need to
	 *            be negotiated. The {@link com.ircclouds.irc.api.negotiators.CompositeNegotiator}
	 *            is most likely the negotiator to use for your use case.<br/>
	 *            <b>Note</b> that the negotiator is expected to
	 *            take over (transparently, irc-api will not signal the
	 *            instance) when CAP negotiation has started. For this purpose,
	 *            the provided negotiator instance is added to the private
	 *            listeners collection such that it will receive communications.
	 *            After CAP END has been sent, as by spec the server will resume
	 *            the normal registration process starting with message 001 and
	 *            irc-api will then continue its standard procedure.
	 */
	void connect(IServerParameters aServerParameters, Callback<IIRCState> aCallback, CapabilityNegotiator negotiator);

	/**
	 * Synchronous disconnect
	 */
	void disconnect();

	/**
	 * Synchronous disconnect
	 * 
	 * @param aQuitMessage The Quit message
	 */
	void disconnect(String aQuitMessage);

	/**
	 * Synchronous channel join
	 * 
	 * @param aChannelName A channel name
	 */
	void joinChannel(String aChannelName);

	/**
	 * Asynchronous channel join
	 * 
	 * @param aChannelName A Channel name
	 * @param aCallback A callback that will return an {@link IRCChannel} on success, or an {@link Exception} in case of failure
	 */
	void joinChannel(String aChannelName, Callback<IRCChannel> aCallback);

	/**
	 * Synchronous channel join
	 * 
	 * @param aChannelName A Channel name
	 * @param aKey A channel key
	 */
	void joinChannel(String aChannelName, String aKey);

	/**
	 * Asynchronous channel join
	 * 
	 * @param aChannelName A Channel name
	 * @param aKey A channel key
	 * @param aCallback A callback that will return an {@link IRCChannel} on success, or an {@link Exception} in case of failure
	 */
	void joinChannel(String aChannelName, String aKey, Callback<IRCChannel> aCallback);

	/**
	 * Synchronous channel leave
	 * @param aChannelName A Channel name
	 */
	void leaveChannel(String aChannelName);

	/**
	 * Asynchronous channel leave
	 * 
	 * @param aChannelName A Channel name
	 * @param aCallback A callback that will return the left channel name in case of success, or an {@link Exception} in case of failure
	 */
	void leaveChannel(String aChannelName, Callback<String> aCallback);

	/**
	 * Synchronous channel leave
	 * 
	 * @param aChannelName A Channel name
	 * @param aPartMessage A part message
	 */
	void leaveChannel(String aChannelName, String aPartMessage);

	/**
	 * Asynchronous channel leave
	 * 
	 * @param aChannelName A channel name
	 * @param aPartMessage A part message
	 * @param aCallback A callback that will return the left channel name in case of success, or an {@link Exception} in case of failure
	 */
	void leaveChannel(String aChannelName, String aPartMessage, Callback<String> aCallback);

	/**
	 * Synchronous nick change
	 * 
	 * @param aNewNick A new nickname
	 */
	void changeNick(String aNewNick);

	/**
	 * Asynchronous nick change
	 * 
	 * @param aNewNick A new nickname
	 * @param aCallback A callback that returns the new nick on success, or an {@link Exception} in case of failure
	 */
	void changeNick(String aNewNick, Callback<String> aCallback);	

	/**
	 * Synchronous Private message
	 * 
	 * @param aTarget Can be a channel or a nickname
	 * @param aMessage A message 
	 */
	void message(String aTarget, String aMessage);

	/**
	 * Asynchronous Private message
	 * 
	 * @param aTarget Can be a channel or a nickname
	 * @param aMessage A message 
	 * @param aCallback A callback that will return the sent message on success, or an {@link Exception} in case of failure
	 */
	void message(String aTarget, String aMessage, Callback<String> aCallback);

	/**
	 * Synchronous Action message
	 * 
	 * @param aTarget Can be a channel or a nickname
	 * @param aMessage A message
	 */
	void act(String aTarget, String aMessage);

	/**
	 * Asynchronous Action message
	 * 
	 * @param aTarget Can be a channel or a nickname
	 * @param aMessage A message 
	 * @param aCallback A callback that will return the sent action message on success, or an {@link Exception} in case of failure
	 */
	void act(String aTarget, String aMessage, Callback<String> aCallback);

	/**
	 * Synchronous Notice message
	 * 
	 * @param aTarget Can be a channel or a nickname
	 * @param aMessage A message 
	 */
	void notice(String aTarget, String aMessage);

	/**
	 * Asynchronous Notice message
	 * 
	 * @param aTarget Can be a channel or a nickname
	 * @param aMessage A message 
	 * @param aCallback A callback that will return the sent notice message on success, or an {@link Exception} in case of failure
	 */
	void notice(String aTarget, String aMessage, Callback<String> aCallback);

	/**
	 * Synchronous kick message
	 * 
	 * @param aChannel A channel name
	 * @param aNick A nick to be kicked
	 */
	void kick(String aChannel, String aNick);

	/**
	 * Synchronous kick message
	 * 
	 * @param aChannel A channel name
	 * @param aNick A nick to be kicked
	 * @param aKickMessage A kick message
	 */
	void kick(String aChannel, String aNick, String aKickMessage);

	/**
	 * Asynchronous kick message
	 * 
	 * @param aChannel A channel name
	 * @param aNick A nick to be kicked
	 * @param aCallback A callback that will return an empty message on success, or an {@link Exception} in case of failure
	 */
	void kick(String aChannel, String aNick, Callback<String> aCallback);

	/**
	 * Asynchronous kick message
	 * 
	 * @param aChannel A channel name
	 * @param aNick A nick to be kicked
	 * @param aKickMessage A kick message
	 * @param aCallback A callback that will return an empty message on success, or an {@link Exception} in case of failure
	 */
	void kick(String aChannel, String aNick, String aKickMessage, Callback<String> aCallback);

	/**
	 * Synchronous change topic
	 * 
	 * @param aChannel A channel name
	 * @param aTopic A new topic
	 */
	void changeTopic(String aChannel, String aTopic);

	/**
	 * Synchronous change mode
	 * 
	 * @param aModeString This will basically execute a 'mode ' + aModeString
	 */
	void changeMode(String aModeString);

	/**
	 * Synchronous raw message
	 *
	 * @param aMessage A raw text message to be sent to the IRC server
	 */
	void rawMessage(String aMessage);

	/**
	 * Synchronous raw message
	 *
	 * @param aMessage A raw text message to be sent to the IRC server, will be stringified
	 */
	void rawMessage(ICommand aMessage);

	/**
	 * 
	 * @param aNick A nick to send the file to
	 * @param aFile A file resource
	 * @throws UnknownHostException Throws because we have to get the host from the system, which may or may not be nothing.
	 */
	void dccSend(String aNick, File aFile) throws UnknownHostException;

	/**
	 * @param aNick A nick to send the file to
	 * @param aFile A file resource to send
	 * @param aTimeout A timeout in milliseconds for destination to reply
	 */
	void dccSend(String aNick, File aFile, Integer aTimeout);

	/**
	 * @param aNick A nick to send the file to
	 * @param aListeningPort A port to listen on
	 * @param aFile A file resource to send
	 */
	void dccSend(String aNick, String aListeningAddress, Integer aListeningPort, File aFile);

	/**
	 * 
	 * @param aNick A nick to send the file to
	 * @param aFile A file resource to send
	 * @param aListeningPort A port to listen on for incoming DCC connections
	 * @param aTimeout A timeout in milliseconds for destination to reply
	 */
	void dccSend(String aNick, File aFile, String aListeningAddress, Integer aListeningPort, Integer aTimeout);

	/**
	 * 
	 * @param aNick A nick to accept the file from
	 * @param aFile A file resource to receive to
	 * @param aPort A port to advertise and to listen on for DCC senders to send us to the file
	 * @param aResumePosition A file resume position in bytes
	 */
	void dccAccept(String aNick, File aFile, Integer aPort, Long aResumePosition);

	/**
	 * 
	 * @param aNick A nick to accept the file from
	 * @param aFile A file resource to receive to
	 * @param aPort A port to advertise and to listen on for DCC senders to send us to the file
	 * @param aResumePosition A file resume position in bytes
	 * @param aTimeout A timeout in milliseconds for destination to reply
	 */
	void dccAccept(String aNick, File aFile, Integer aPort, Long aResumePosition, Integer aTimeout);

	/**
	 * 
	 * @param aFile A file resource
	 * @param aSize A file size.  Used to denote how much to receive to file
	 * @param aAddress A socket address to connect to and get the file
	 */
	void dccReceive(File aFile, Long aSize, SocketAddress aAddress);

	/**
	 * 
	 * @param aFile A file resource
	 * @param aSize A file size.  Used to denote how much to receive to file
	 * @param aAddress A socket address to connect to and get the file
	 * @param aProxy A SOCKS proxy
	 */
	void dccReceive(File aFile, Long aSize, SocketAddress aAddress, Proxy aProxy);

	/**
	 *
	 * @param aFile A file resource
	 * @param aResumePosition A resume position in bytes
	 * @param aSize A size in bytes.  Used to denote how much to receive to file
	 * @param aAddress A socket address to connect to and get the file
	 */
	void dccResume(File aFile, Long aResumePosition, Long aSize, SocketAddress aAddress);

	/**
	 *
	 * @param aFile A file resource
	 * @param aResumePosition A resume position in bytes
	 * @param aSize A size in bytes.  Used to denote how much to receive to file
	 * @param aAddress A socket address to connect to and get the file
	 * @param aProxy The proxy server to use for connecting.
	 */
	void dccResume(File aFile, Long aResumePosition, Long aSize, SocketAddress aAddress, Proxy aProxy);

	/**
	 * Returns the DCC manager
	 * 
	 * @return {@link DCCManager}
	 */
	DCCManager getDCCManager();

	MBassador getEventBus();

	/**
	 * Adds a message listener
	 * 
	 * @param aListener A message listener
	 */
	void register(Object aListener);

	/**
	 * Deletes a message listener
	 * 
	 * @param aListener A message listener
	 */
	void unregister(Object aListener);

	/**
	 * Convert a plain text connection into an SSL/TLS secured connection.
	 *
	 * @param aContext An SSL context, optional, if null will fall back to
	 * default SSLContext of IRCApi.
	 * @param aHostname The IRC server host name.
	 * @param aPort The IRC server port.
	 * @throws SSLException Throws SSLException in case the SSL/TLS
	 * initiation fails, specifically the beginning of the handshake.
	 */
	void secureConnection(SSLContext aContext, String aHostname, int aPort) throws SSLException;
}
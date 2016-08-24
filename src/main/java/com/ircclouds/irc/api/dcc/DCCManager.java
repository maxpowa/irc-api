package com.ircclouds.irc.api.dcc;

import com.ircclouds.irc.api.interfaces.IIRCApi;

import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.dannyvanheumen.nio.ProxiedSocketChannel;

public class DCCManager {
	private static final Logger LOG = LoggerFactory.getLogger(DCCManager.class);

	public static final int DCC_SEND_TIMEOUT = 10000;

	private IIRCApi api;

	private Map<Integer, DCCSender> sendersMap = new HashMap<Integer, DCCSender>();
	private List<DCCReceiver> dccReceivers = new ArrayList<DCCReceiver>();

	public DCCManager(IIRCApi aApi) {
		api = aApi;
		api.getEventBus().subscribe(new DCCEventListener());
	}

	public void sendFile(String aNick, File aFile, String aListeningAddress, Integer aListeningPort, Integer aTimeout)
	{
		DCCSender _dccSender = new DCCSender(aFile, aListeningPort, aTimeout, api.getEventBus());

		sendersMap.put(aListeningPort, _dccSender);

		_dccSender.send();

		api.message(aNick, '\001' + "DCC SEND " + aFile.getName() + " " + aListeningAddress + " " + aListeningPort + " " + aFile.length() + '\001');
	}

	public void acceptFile(String aNick, File aFile, Integer aPort, Long aResumePosition, Integer aTimeout)
	{

		if (isWaitingForConnection(aPort)) {
			sendersMap.get(aPort).setResumePosition(aResumePosition);
		} else {
			DCCSender _dccSender = new DCCSender(aFile, aTimeout, aPort, aResumePosition, api.getEventBus());

			sendersMap.put(aPort, _dccSender);

			_dccSender.send();
		}

		api.message(aNick, '\001' + "DCC ACCEPT " + aFile.getName() + " " + aPort + " " + aResumePosition + '\001');
	}

	public void resumeFile(File aFile, Long aResumePosition, Long aSize, SocketAddress aAddress)
	{
		resumeFile(aFile, aResumePosition, aSize, aAddress, null);
	}

	public void resumeFile(File aFile, Long aResumePosition, Long aSize, SocketAddress aAddress, Proxy aProxy) {
		DCCReceiver _dccReceiver = new DCCReceiver(aFile, aResumePosition, aSize, aAddress, api.getEventBus(), aProxy);

		dccReceivers.add(_dccReceiver);

		_dccReceiver.receive();
	}

	public int activeDCCSendsCount()
	{
		return sendersMap.size();
	}

	public int activeDCCReceivesCount()
	{
		return dccReceivers.size();
	}

	private boolean isWaitingForConnection(Integer aPort)
	{
		return sendersMap.containsKey(aPort);
	}

	// Listen for success or failure and drop the senders/receivers.
	public class DCCEventListener {
		@Handler(priority = 100)
		public void handleReceiver(DCCReceiver.Success event) {
			dccReceivers.remove(event.getReceiver());
		}
		@Handler(priority = 100)
		public void handleReceiver(DCCReceiver.Failure event) {
			dccReceivers.remove(event.getReceiver());
		}
		@Handler(priority = 100)
		public void handleSender(DCCSender.Success event) {
			sendersMap.remove(event.getPort());
		}
		@Handler(priority = 100)
		public void handleSender(DCCSender.Failure event) {
			sendersMap.remove(event.getPort());
		}
	}

	public static ServerSocketChannel getServerSocketChannel(Integer listeningPort) throws IOException {
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.socket().bind(new InetSocketAddress(listeningPort));
		return ssc;
	}

	public static SocketChannel getSocketChannel(SocketAddress address) throws IOException {
		return getSocketChannel(address, null);
	}

	public static SocketChannel getSocketChannel(SocketAddress address, Proxy proxy) throws IOException {
		if (proxy != null) {
			SocketChannel sc = new ProxiedSocketChannel(proxy);
			sc.connect(address);
			return sc;
		}
		return SocketChannel.open(address);
	}
}

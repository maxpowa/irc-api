package com.ircclouds.irc.api.dcc;

import com.ircclouds.irc.api.ApiException;
import com.ircclouds.irc.api.dcc.interfaces.IDCCManager;
import com.ircclouds.irc.api.dcc.interfaces.IDCCReceiveCallback;
import com.ircclouds.irc.api.dcc.interfaces.IDCCReceiveProgressCallback;
import com.ircclouds.irc.api.dcc.interfaces.IDCCReceiveResult;
import com.ircclouds.irc.api.dcc.interfaces.IDCCSendCallback;
import com.ircclouds.irc.api.dcc.interfaces.IDCCSendProgressCallback;
import com.ircclouds.irc.api.dcc.interfaces.IDCCSendResult;
import com.ircclouds.irc.api.interfaces.IIRCApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCCManager implements IDCCManager
{
	private static final Logger LOG = LoggerFactory.getLogger(DCCManager.class);

	public static final int DCC_SEND_TIMEOUT = 10000;

	private IIRCApi api;

	private Map<Integer, DCCSender> sendersMap = new HashMap<Integer, DCCSender>();
	private List<DCCReceiver> dccReceivers = new ArrayList<DCCReceiver>();
	
	public DCCManager(IIRCApi aApi)
	{
		api = aApi;
	}

	public void dccSend(String aNick, File aFile, Integer aListeningPort, Integer aTimeout, IDCCSendCallback aCallback)
	{
		DCCSender _dccSender = new DCCSender(aListeningPort, aTimeout, addManagerDCCSendCallback(aCallback, aListeningPort));

		registerSender(aListeningPort, _dccSender);

		_dccSender.send(aFile);

		api.message(aNick, '\001' + "DCC SEND " + aFile.getName() + " " + getLocalAddressRepresentation() + " " + aListeningPort + " " + aFile.length() + '\001');
	}

	public void dccAccept(String aNick, File aFile, Integer aPort, Integer aResumePosition, Integer aTimeout, IDCCSendCallback aCallback)
	{
		DCCSender _dccSender = new DCCSender(aTimeout, aPort, aResumePosition, addManagerDCCSendCallback(aCallback, aPort));

		if (isWaitingForConnection(aPort))
		{
			sendersMap.get(aPort).setResumePosition(aResumePosition);
		}
		else
		{
			registerSender(aPort, _dccSender);
			_dccSender.send(aFile);
		}

		api.message(aNick, '\001' + "DCC ACCEPT " + aFile.getName() + " " + aPort + " " + aResumePosition + '\001');
	}

	public void dccResume(File aFile, Integer aResumePosition, Integer aSize, SocketAddress aAddress, IDCCReceiveCallback aCallback)
	{
		DCCReceiver _dccReceiver = new DCCReceiver(addManagerDCCReceiveCallback(aCallback), null);
		
		registerReceiver(_dccReceiver);
		
		_dccReceiver.receive(aFile, aResumePosition, aSize, aAddress);
	}

	public void dccResume(File aFile, Integer aResumePosition, Integer aSize, SocketAddress aAddress, IDCCReceiveCallback aCallback, Proxy aProxy)
	{
		DCCReceiver _dccReceiver = new DCCReceiver(addManagerDCCReceiveCallback(aCallback), aProxy);

		registerReceiver(_dccReceiver);

		_dccReceiver.receive(aFile, aResumePosition, aSize, aAddress);
	}

	@Override
	public int activeDCCSendsCount()
	{
		return sendersMap.size();
	}

	@Override
	public int activeDCCReceivesCount()
	{
		return dccReceivers.size();
	}
	
	private String getLocalAddressRepresentation()
	{
		try
		{
			InetAddress _localHost = InetAddress.getLocalHost();
			byte[] _address = _localHost.getAddress();
			if (_address.length == 4)
			{
				return new BigInteger(1, _address).toString();
			}
			else
			{
				return _localHost.getHostAddress();
			}
		}
		catch (UnknownHostException aExc)
		{
			LOG.error("", aExc);
			throw new ApiException(aExc);
		}
	}

	private IDCCReceiveCallback addManagerDCCReceiveCallback(final IDCCReceiveCallback aCallback)
	{
		if (aCallback instanceof IDCCReceiveProgressCallback)
		{
			return new IDCCReceiveProgressCallback()
			{				
				@Override
				public void onSuccess(IDCCReceiveResult aU)
				{
					dccReceivers.remove(aU);
					aCallback.onSuccess(aU);
				}
				
				@Override
				public void onFailure(DCCReceiveException aV)
				{
					dccReceivers.remove(aV);
					aCallback.onFailure(aV);
				}
				
				@Override
				public void onProgress(int aBytesTransferred)
				{
					((IDCCReceiveProgressCallback) aCallback).onProgress(aBytesTransferred);
				}
			};
		}
		
		return new IDCCReceiveCallback()
		{			
			@Override
			public void onSuccess(IDCCReceiveResult aU)
			{
				dccReceivers.remove(aU);
				aCallback.onSuccess(aU);
			}
			
			@Override
			public void onFailure(DCCReceiveException aV)
			{
				dccReceivers.remove(aV);
				aCallback.onFailure(aV);
			}
		};
	}
	
	private IDCCSendCallback addManagerDCCSendCallback(final IDCCSendCallback aCallback, final int aPort)
	{
		if (aCallback instanceof IDCCSendProgressCallback)
		{
			return new IDCCSendProgressCallback()
			{				
				@Override
				public void onSuccess(IDCCSendResult aU)
				{					
					sendersMap.remove(aPort);
					
					aCallback.onSuccess(aU);					
				}
				
				@Override
				public void onFailure(DCCSendException aV)
				{					
					sendersMap.remove(aPort);
					
					aCallback.onFailure(aV);
				}
				
				@Override
				public void onProgress(int aBytesTransferred)
				{
					((IDCCSendProgressCallback) aCallback).onProgress(aBytesTransferred);
				}
			};
		}
		
		return new IDCCSendCallback()
		{
			@Override
			public void onSuccess(IDCCSendResult aU)
			{
				sendersMap.remove(aPort);
			
				aCallback.onSuccess(aU);				
			}
			
			@Override
			public void onFailure(DCCSendException aV)
			{
				sendersMap.remove(aPort);
				
				aCallback.onFailure(aV);
			}
		};
	}
	
	private void registerSender(Integer aListeningPort, DCCSender _dccSender)
	{
		sendersMap.put(aListeningPort, _dccSender);
	}

	private void registerReceiver(DCCReceiver aDCCReceiver)
	{
		dccReceivers.add(aDCCReceiver);
	}
	
	private boolean isWaitingForConnection(Integer aPort)
	{
		return sendersMap.containsKey(aPort);
	}
}

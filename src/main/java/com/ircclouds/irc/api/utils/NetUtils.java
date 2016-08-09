package com.ircclouds.irc.api.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Random;

public class NetUtils
{
	/**
	 * Set the maximum port to be randomly generated by getRandDccPort()
	 */
	public static Integer MAX_DCC_PORT = 5000;
	/**
	 * Set the minimum port to be randomly generated by getRandDccPort()
	 */
	public static Integer MIN_DCC_PORT = 1024;

	public static int getRandDccPort()
	{
		return getRandPortIn(MIN_DCC_PORT, MAX_DCC_PORT);
	}
	
	public static int getRandPortIn(int aMin, int aMax)
	{
		if (aMin >= aMax)
		{
			throw new RuntimeException("Please provide a valid [Min-Max] range.");
		}
		
		Random _rand = new Random();
		int _port = _rand.nextInt(aMax - aMin) + aMin;
		while (!NetUtils.available(_port))
		{
			_port = _rand.nextInt(aMax - aMin) + aMin;
		}
		
		return _port;
	}
	
	public static boolean available(int aPort)
	{
		ServerSocket _ss = null;
		try
		{
			_ss = new ServerSocket(aPort);
			_ss.setReuseAddress(true);
			
			return true;
		}
		catch (IOException aExc)
		{
		}
		finally
		{
			if (_ss != null)
			{
				try
				{
					_ss.close();
				}
				catch (IOException aExc)
				{
				}
			}
		}

		return false;
	}

	public static String getLocalAddressRepresentation() throws UnknownHostException {
		InetAddress _localHost = InetAddress.getLocalHost();
		byte[] _address = _localHost.getAddress();
		if (_address.length == 4) {
			return new BigInteger(1, _address).toString();
		} else {
			return _localHost.getHostAddress();
		}
	}
}

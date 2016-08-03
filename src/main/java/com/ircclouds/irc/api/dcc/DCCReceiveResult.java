package com.ircclouds.irc.api.dcc;

public interface DCCReceiveResult
{
	int getNumberOfAcksSent();
	
	int totalBytesReceived();
	
	long totalTime();
}

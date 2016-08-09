package com.ircclouds.irc.api.dcc.interfaces;

public interface IDCCReceiveResult
{
	int getNumberOfAcksSent();
	
	int totalBytesReceived();
	
	long totalTime();
}

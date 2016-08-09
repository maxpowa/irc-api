package com.ircclouds.irc.api.dcc.interfaces;

public interface IDCCReceiveResult
{
	int getNumberOfAcksSent();
	
	long totalBytesReceived();
	
	long totalTime();
}

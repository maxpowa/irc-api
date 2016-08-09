package com.ircclouds.irc.api.dcc.interfaces;

public interface IDCCSendResult
{
	int getNumberOfAcksReceived();

	long totalBytesSent();
	
	long totalTime();
}
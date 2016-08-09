package com.ircclouds.irc.api.dcc.interfaces;

public interface IDCCSendResult
{
	int getNumberOfAcksReceived();
	
	int totalBytesSent();
	
	long totalTime();
}
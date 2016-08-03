package com.ircclouds.irc.api.dcc;

public interface DCCSendResult
{
	int getNumberOfAcksReceived();
	
	int totalBytesSent();
	
	long totalTime();
}
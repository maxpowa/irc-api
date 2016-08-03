package com.ircclouds.irc.api.dcc;

public interface DCCReceiveProgressCallback extends DCCReceiveCallback
{
	void onProgress(int aBytesTransferred);
}

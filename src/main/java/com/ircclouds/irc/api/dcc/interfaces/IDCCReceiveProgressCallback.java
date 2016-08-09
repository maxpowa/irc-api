package com.ircclouds.irc.api.dcc.interfaces;

public interface IDCCReceiveProgressCallback extends IDCCReceiveCallback
{
	void onProgress(int aBytesTransferred);
}

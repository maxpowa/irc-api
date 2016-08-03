package com.ircclouds.irc.api.dcc;

public interface DCCSendProgressCallback extends DCCSendCallback
{
	void onProgress(int aBytesTransferred);
}

package com.ircclouds.irc.api.dcc.interfaces;

public interface IDCCSendProgressCallback extends IDCCSendCallback
{
	void onProgress(int aBytesTransferred);
}

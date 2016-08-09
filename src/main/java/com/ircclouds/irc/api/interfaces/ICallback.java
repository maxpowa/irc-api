package com.ircclouds.irc.api.interfaces;

public interface ICallback<U, V>
{
	void onSuccess(U aU);
	
	void onFailure(V aV);
}

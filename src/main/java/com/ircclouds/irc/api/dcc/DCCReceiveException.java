package com.ircclouds.irc.api.dcc;

import com.ircclouds.irc.api.ApiException;
import com.ircclouds.irc.api.dcc.interfaces.IDCCReceiveResult;

import java.io.IOException;

public class DCCReceiveException extends ApiException
{
	private IDCCReceiveResult result;
	
	private IOException exc;
	
	public DCCReceiveException(IDCCReceiveResult aResult, IOException aException)
	{
		super(DCCReceiveException.class.getName());
		
		result = aResult;
		exc = aException;
	}
	
	public IOException getException()
	{
		return exc;
	}
	
	public IDCCReceiveResult getDCCReceiveResult()
	{
		return result;
	}
}

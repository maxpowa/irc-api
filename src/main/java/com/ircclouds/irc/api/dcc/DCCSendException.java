package com.ircclouds.irc.api.dcc;

import com.ircclouds.irc.api.ApiException;
import com.ircclouds.irc.api.dcc.interfaces.IDCCSendResult;

public class DCCSendException extends ApiException
{
	private IDCCSendResult result;
	
	private Exception readerExc;
	private Exception writerExc;
	
	public DCCSendException(IDCCSendResult aResult, Exception aReaderException, Exception aWriterException)
	{
		super(DCCSendException.class.getName());
		
		result = aResult;
		
		readerExc = aReaderException;
		writerExc = aWriterException;
	}
	
	public IDCCSendResult getDCCSendResult()
	{
		return result;
	}
	
	public Exception getReaderException()
	{
		return readerExc;
	}
	
	public Exception getWriterException()
	{
		return writerExc;
	}
}

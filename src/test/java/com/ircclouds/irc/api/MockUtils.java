package com.ircclouds.irc.api;

import com.ircclouds.irc.api.comms.IConnection;
import com.ircclouds.irc.api.interfaces.Callback;
import com.ircclouds.irc.api.interfaces.IIRCApi;
import com.ircclouds.irc.api.interfaces.IServerParameters;
import com.ircclouds.irc.api.state.IIRCState;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockUtils
{
	private static Thread readerThread;
	private static ConnectedApi connectedApi;

	public static ConnectedApi newConnectedApi(IConnection aConnection, IServerParameters aServerParams, Integer aWaitingDuration) throws Exception
	{
		final CountDownLatch _cdl = new CountDownLatch(1);

		final IIRCApi api = new IRCApi(false);
		api.connect(aServerParams, new Callback<IIRCState>()
		{
			@Override
			public void onSuccess(final IIRCState aObject)
			{
				setConnectedApi(api, aObject);

				readerThread = Thread.currentThread();

				_cdl.countDown();
			}

			@Override
			public void onFailure(Exception aExc)
			{
				readerThread = Thread.currentThread();

				_cdl.countDown();
			}
		}, null);

		_cdl.await(aWaitingDuration, TimeUnit.SECONDS);
		if (readerThread != null)
		{
			readerThread.join();
		}

		return connectedApi;
	}

	protected interface ConnectedApi
	{
		IIRCApi getIRCApi();

		IIRCState getConnectedState();
	}

	private static void setConnectedApi(final IIRCApi aApi, final IIRCState aConnectedState)
	{
		connectedApi = new ConnectedApi()
		{

			@Override
			public IIRCApi getIRCApi()
			{
				return aApi;
			}

			@Override
			public IIRCState getConnectedState()
			{
				return aConnectedState;
			}
		};
	}
}

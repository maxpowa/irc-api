package com.ircclouds.irc.api.listeners;

import java.util.*;

import com.ircclouds.irc.api.*;
import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;

public class AsyncMessageListener
{
	private static final List<Integer> NUMERICS = Arrays.asList(IRCServerNumerics.NO_SUCH_NICK_CHANNEL, IRCServerNumerics.NO_EXTERNAL_CHANNEL_MESSAGES);
	
	private Queue<AsyncTriple> myQueue = new LinkedList<AsyncTriple>();

    public void onServerMsg(ServerNumeric aMsg) {
        if (NUMERICS.contains(aMsg.getNumericCode())) {
            String cmpnts[] = aMsg.params.toArray(new String[0]);

			AsyncTriple _aTrip = myQueue.peek();
			if (_aTrip != null)
			{
				if (_aTrip.asyncId.equals(cmpnts[0]))
				{
					_aTrip = myQueue.poll();
					if (!_aTrip.flag)
					{
						_aTrip.callback.onSuccess("OK");
					}
				}
				else
				{
                    _aTrip.callback.onFailure(new IRCException(aMsg.getText()));
                    _aTrip.flag = true;
                }
            }
        }
	}

	public void submit(int aAsyncId, Callback<String> aCallback)
	{
		myQueue.add(new AsyncTriple(aAsyncId + "", aCallback));
	}

	private class AsyncTriple
	{
		private String asyncId;
		private Callback<String> callback;
		private boolean flag;

		public AsyncTriple(String aSyncId, Callback<String> aCb)
		{
			asyncId = aSyncId;
			callback = aCb;
		}
	}
}

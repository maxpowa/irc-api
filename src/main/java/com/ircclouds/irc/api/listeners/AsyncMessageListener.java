package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.Callback;
import com.ircclouds.irc.api.IRCException;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import net.engio.mbassy.listener.Handler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AsyncMessageListener
{
	private static final List<Integer> NUMERIC_FILTER = Arrays.asList(IRCNumerics.ERR_NOSUCHNICK, IRCNumerics.ERR_CANNOTSENDTOCHAN);
	
	private Queue<AsyncTriple> myQueue = new LinkedList<AsyncTriple>();

	@Handler
    public void onServerMsg(ServerNumeric aMsg) {
        if (NUMERIC_FILTER.contains(aMsg.getNumericCode())) {
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

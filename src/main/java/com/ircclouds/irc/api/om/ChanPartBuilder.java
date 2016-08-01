package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public class ChanPartBuilder implements IBuilder<ChanPartMessage>
{
	// //:aae!aaf@bot.lebane.se PART #botcode :aSS

	public ChanPartMessage build(Message aMessage)
	{
		String[] _cmpnts = aMessage.raw.split(" ");

		WritableIRCUser _info = ParseUtils.getUser(aMessage.prefix);
		String _chanName = aMessage.params.get(0);
		if (_chanName.startsWith(":"))
		{
			_chanName = _chanName.substring(1);
		}

		ChanPartMessage _msg = null;
		if (_cmpnts.length > 3)
		{
			_msg = new ChanPartMessage(_chanName, _info, aMessage.raw.substring(aMessage.raw.indexOf(" :") + 2));
		}
		else
		{
			 _msg = new ChanPartMessage(_chanName, _info);
		}

		return _msg;
	}
}

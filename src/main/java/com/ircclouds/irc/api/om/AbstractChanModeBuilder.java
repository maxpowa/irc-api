package com.ircclouds.irc.api.om;

import java.util.*;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

public abstract class AbstractChanModeBuilder implements IBuilder<ChannelModeMessage>
{
	public ChannelModeMessage build(Message aMessage)
	{
		Stack<String> _params = new Stack<String>();
		_params.addAll(aMessage.params.subList(2, aMessage.params.size()));
		
		List<ChannelMode> _addedModes = new ArrayList<ChannelMode>();
		List<ChannelMode> _removedModes = new ArrayList<ChannelMode>();

		String _modesStr = aMessage.params.get(1);
		for (int _i = 0; _i < _modesStr.length(); _i++)
		{
			int _plusIndex = _modesStr.indexOf("+", _i + 1);
			int _minusIndex = _modesStr.indexOf("-", _i + 1);
			int _end = 0;
			if (_plusIndex < _minusIndex)
			{
				if (_plusIndex != -1)
				{
					_end = _plusIndex;
				}
				else
				{
					_end = _minusIndex;
				}
			}
			else if (_minusIndex < _plusIndex)
			{
				if (_minusIndex != -1)
				{
					_end = _minusIndex;
				}
				else
				{
					_end = _plusIndex;
				}
			}
			else
			{
				_end = _modesStr.length();
			}
			
			char _m = _modesStr.charAt(_i);
			String _mode = _modesStr.substring(_i+1, _end);
			_i = _end -1;
			if (_m == '+')
			{
				parseModes(_params, _addedModes, _mode, true);
			}
			else
			{
				parseModes(_params, _removedModes, _mode, false);
			}
		}

		return new ChannelModeMessage(ParseUtils.getSource(aMessage.prefix), aMessage.params.get(0), getModeStr(aMessage.params), _addedModes, _removedModes);
	}

	protected abstract IRCServerOptions getIRCServerOptions();

	private String getModeStr(List<String> aCmpnts)
	{
		StringBuilder _sb = new StringBuilder();

		int _i = aCmpnts.size();
		for (int _j = 1; _j < _i; _j++)
		{
			_sb.append(aCmpnts.get(_j)).append(" ");
		}
		
		return _sb.substring(0, _sb.length() -1).toString();
	}

	private void parseModes(Stack<String> aParams, List<ChannelMode> aModes, String aModesStr, boolean aAddFlag)
	{
		for (int _i = 0; _i < aModesStr.length(); _i++)
		{
			char _mode = aModesStr.charAt(_i);

			if (getChannelModes().isOfTypeA(_mode))
			{
				aModes.add(new ChannelModeA(_mode, aParams.pop()));
			}
			else if (getChannelModes().isOfTypeB(_mode))
			{
				aModes.add(new ChannelModeB(_mode, aParams.pop()));
			}
			else if (getChannelModes().isOfTypeC(_mode))
			{
				if (aAddFlag)
				{
					aModes.add(new ChannelModeC(_mode, aParams.pop()));
				}
				else
				{
					aModes.add(new ChannelModeC(_mode));
				}
			}
			else if (getChannelModes().isOfTypeD(_mode))
			{
				aModes.add(new ChannelModeD(_mode));
			}
			else if (getUserStatuses().contains(_mode))
			{
				aModes.add(new IRCUserStatusMode(getUserStatuses().getUserStatus(_mode), aParams.pop()));
			}
		}
	}

	private IRCUserStatuses getUserStatuses()
	{
		return getIRCServerOptions().getUserChanStatuses();
	}

	private ChannelModes getChannelModes()
	{
		return getIRCServerOptions().getChannelModes();
	}
}
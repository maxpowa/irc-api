package com.ircclouds.irc.api.filters;

import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;

import java.util.ArrayList;
import java.util.List;

public class ApiMessageFilter implements IMessageFilter
{
	private List<String> nextValues = new ArrayList<String>();

	public ApiMessageFilter()
	{
		this(0);
	}
	
	public ApiMessageFilter(Integer aNextInt)
	{
		nextValues.add(aNextInt+"");
	}
	
	@Override
	public MessageFilterResult filter(AbstractMessage aMsg)
	{
        if (aMsg instanceof ServerNumeric) {
            if (nextValues.remove(((ServerNumeric) aMsg).params.get(0))) {
                return new MessageFilterResult(null, FilterStatus.HALT);
			}
		}
		
		return new MessageFilterResult(aMsg, FilterStatus.PASS);
	}

	public void addValue(Integer aNextInt)
	{
		nextValues.add(aNextInt+"");
	}

	@Override
	public TargetListeners getTargetListeners()
	{
		return TargetListeners.ALL;
	}
}

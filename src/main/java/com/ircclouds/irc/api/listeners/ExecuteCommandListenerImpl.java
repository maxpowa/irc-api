package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.domain.WritableIRCChannel;
import com.ircclouds.irc.api.interfaces.IIRCSession;
import com.ircclouds.irc.api.state.IIRCState;
import com.ircclouds.irc.api.state.IStateAccessor;

public class ExecuteCommandListenerImpl extends AbstractExecuteCommandListener
{
	private IStateAccessor updater;
	
	public ExecuteCommandListenerImpl(IIRCSession aSession, IStateAccessor aUpdater)
	{
		super(aSession);
		
		updater = aUpdater;
	}

	@Override
	public void saveChan(WritableIRCChannel aChannel)
	{
		updater.saveChan(aChannel);
	}

	@Override
	public void deleteChan(String aChannelName)
	{
		updater.deleteChan(aChannelName);
	}

	@Override
	public IIRCState getIRCState()
	{
		return updater.getIRCState();
	}

	@Override
	public void updateNick(String aNewNick)
	{
		updater.updateNick(aNewNick);
	}

	@Override
	public void deleteNickFromChan(String aChan, String aNick)
	{
		updater.deleteNickFromChan(aChan, aNick);
	}
}

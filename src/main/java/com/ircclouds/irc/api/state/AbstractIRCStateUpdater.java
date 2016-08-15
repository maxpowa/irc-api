package com.ircclouds.irc.api.state;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.domain.messages.ChannelMode;
import com.ircclouds.irc.api.utils.StateUtils;
import net.engio.mbassy.listener.Handler;

import java.util.Set;

public abstract class AbstractIRCStateUpdater implements IStateAccessor
{
	@Handler
    public void onChannelJoin(ChannelJoin aMsg) {
        if (!isForMe(aMsg)) {
            IRCUser _user = aMsg.getSource();
			WritableIRCChannel _chan = getIRCStateImpl().getWritableChannelByName(aMsg.getChannelName());
			
			savedOldState(_chan);
			
			_chan.addUser(_user);
		}
	}

	@Handler
    public void onChannelPart(ChannelPart aMsg) {
        if (!isForMe(aMsg)) {
            WritableIRCChannel _chan = getIRCStateImpl().getWritableChannelByName(aMsg.getChannelName());
			
			savedOldState(_chan);

			_chan.removeUser(aMsg.getSource());
		}
	}

	@Handler
	public void onNickChange(UserNickMessage aMsg)
	{
		WritableIRCUser _old = new WritableIRCUser(aMsg.getSource().getNick());
		WritableIRCUser _new = new WritableIRCUser(aMsg.getNewNick());
		
		for (WritableIRCChannel _chan : getIRCStateImpl().getChannelsMutable())
		{
			savedOldState(_chan);
			
			if (_chan.contains(_old))
			{
				_chan.addUser(_new, _chan.removeUser(_old));
			}
		}
	}

	@Handler
	public void onUserQuit(UserQuitMessage aMsg)
	{
		for (WritableIRCChannel _chan : getIRCStateImpl().getChannelsMutable())
		{
			savedOldState(_chan);
			
			_chan.removeUser(aMsg.getSource());			
		}
	}

	@Handler
	public void onTopicChange(ChannelTopic aMsg)
	{		
		WritableIRCChannel _chan = getIRCStateImpl().getWritableChannelByName(aMsg.getChannelName());
		
		savedOldState(_chan);
		
		WritableIRCTopic _wit = (WritableIRCTopic) _chan.getTopic();
		_wit.setDate(aMsg.getTopic().getDate());
		_wit.setSetBy(aMsg.getTopic().getSetBy());
		_wit.setValue(aMsg.getTopic().getValue());
	}

	@Handler
	public void onChannelKick(ChannelKick aMsg)
	{
		WritableIRCChannel _chan = getIRCStateImpl().getWritableChannelByName(aMsg.getChannelName());
		
		savedOldState(_chan);
		
		_chan.removeUser(new WritableIRCUser(aMsg.getKickedNickname()));
	}

	@Handler
    public void onChannelMode(ChannelMode aMsg) {
        String _chanName = aMsg.getChannelName();
        WritableIRCChannel _chan = getIRCStateImpl().getWritableChannelByName(_chanName);

        savedOldState(_chan);

        for (com.ircclouds.irc.api.domain.ChannelMode _mode : aMsg.getAddedModes()) {
            if (_mode instanceof IRCUserStatusMode) {
                if (aMsg.getRemovedModes().contains(_mode))
				{
					aMsg.getRemovedModes().remove(_mode);
				}
				else
				{
					IRCUserStatusMode _usm = (IRCUserStatusMode) _mode;
					IRCUserStatus _us = getAvailableUserStatuses().getUserStatus(_usm.getChannelModeType());
					if (_us != null)
					{
						Set<IRCUserStatus> _uStatuses = _chan.getStatusesForUser(new WritableIRCUser(_usm.getUser()));
						_uStatuses.add(_us);
					}
				}
			}
		}
        for (com.ircclouds.irc.api.domain.ChannelMode _mode : aMsg.getRemovedModes()) {
            if (_mode instanceof IRCUserStatusMode) {
                IRCUserStatusMode _usm = (IRCUserStatusMode) _mode;
				IRCUserStatus _us = getAvailableUserStatuses().getUserStatus(_usm.getChannelModeType());
				if (_us != null)
				{
					Set<IRCUserStatus> _uStatuses = _chan.getStatusesForUser(new WritableIRCUser(_usm.getUser()));
					_uStatuses.remove(_us);
				}
			}
		}
	}

	private IRCState getIRCStateImpl()
	{
		return (IRCState) getIRCState();
	}
	
	private IRCState getPreviousIRCStateImpl()
	{
		return (IRCState) getIRCStateImpl().getPrevious();
	}
	
	private IRCUserStatuses getAvailableUserStatuses()
	{
		return getIRCState().getServerOptions().getUserChanStatuses();
	}
	
	private boolean isForMe(AbstractUserMessage aMsg)
	{
		return getIRCState().getNickname().equals(aMsg.getSource().getNick());
	}

	@Override
	public void saveChan(WritableIRCChannel aChannel)
	{
		getIRCStateImpl().getChannelsMutable().add(aChannel);
	}

	@Override
	public void deleteChan(String aChannelName)
	{
		getIRCStateImpl().getChannelsMutable().remove(aChannelName);
	}
	
	@Override
	public void updateNick(String aNewNick)
	{
		getIRCStateImpl().updateNick(aNewNick);
	}
	
	@Override
	public void deleteNickFromChan(String aChannel, String aNick)
	{
		for (WritableIRCChannel _chan : getIRCStateImpl().getChannelsMutable())
		{
			if (_chan.getName().equals(aChannel))
			{
				_chan.getUsers().remove(new WritableIRCUser(aNick));
				break;
			}
		}
	}
	
	private void savedOldState(WritableIRCChannel aChan)
	{
		getPreviousIRCStateImpl().getChannelsMutable().remove(aChan);
		getPreviousIRCStateImpl().getChannelsMutable().add(StateUtils.cloneChannel(aChan));
	}
}
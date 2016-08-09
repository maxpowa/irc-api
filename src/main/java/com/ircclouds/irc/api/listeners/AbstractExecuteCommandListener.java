package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.domain.IRCChannel;
import com.ircclouds.irc.api.domain.IRCUserStatuses;
import com.ircclouds.irc.api.domain.WritableIRCChannel;
import com.ircclouds.irc.api.domain.messages.AbstractUserMessage;
import com.ircclouds.irc.api.domain.messages.ChannelJoin;
import com.ircclouds.irc.api.domain.messages.ChannelKick;
import com.ircclouds.irc.api.domain.messages.ChannelPart;
import com.ircclouds.irc.api.domain.messages.ServerError;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.domain.messages.UserNickMessage;
import com.ircclouds.irc.api.interfaces.Callback;
import com.ircclouds.irc.api.interfaces.IIRCSession;
import com.ircclouds.irc.api.interfaces.IServerParameters;
import com.ircclouds.irc.api.state.IIRCState;
import com.ircclouds.irc.api.state.IRCStateImpl;
import com.ircclouds.irc.api.state.IStateAccessor;

import net.engio.mbassy.listener.Handler;

public abstract class AbstractExecuteCommandListener implements IStateAccessor
{
	private AbstractChannelJoinListener chanJoinListener;
	private AbstractChannelPartListener chanPartListener;
	private ConnectCmdListener connectListener;
	private AbstractNickChangeListener nickChangeListener;
	private KickUserListener kickUserListener;
	private AsyncMessageListener messsageListener;
	
	public AbstractExecuteCommandListener(IIRCSession aSession)
	{
		chanJoinListener = new AbstractChannelJoinListener()
		{
			@Override
			public void saveChannel(WritableIRCChannel aChannel)
			{
				saveChan(aChannel);
			}

			@Override
			protected IRCUserStatuses getIRCUserStatuses()
			{
				return getIRCState().getServerOptions().getUserChanStatuses();
			}
		};
		chanPartListener = new AbstractChannelPartListener()
		{
			@Override
			protected void deleteChannel(String aChannelName)
			{
				deleteChan(aChannelName);
			}
		};	
		connectListener = new ConnectCmdListener(aSession);
		nickChangeListener = new AbstractNickChangeListener()
		{
			@Override
			protected void changeNick(String aNewNick)
			{
				updateNick(aNewNick);
			}
		};
		kickUserListener = new KickUserListener()
		{
			@Override
			protected void delChanUser(String aChan, String aUser)
			{
				deleteNickFromChan(aChan, aUser);
			}			
		};
		messsageListener = new AsyncMessageListener();
	}

	@Handler
    public void onChannelJoin(ChannelJoin aMsg) {
        if (isForMe(aMsg)) {
            chanJoinListener.onChanJoinMessage(aMsg);
		}
	}

	@Handler
    public void onChannelPart(ChannelPart aMsg) {
        if (isForMe(aMsg)) {
            chanPartListener.onChannelPart(aMsg);
		}
	}

	@Handler
	public void onChannelKick(ChannelKick aChanKick)
	{
		if (isForMe(aChanKick))
		{
			kickUserListener.onChannelKick(aChanKick);
		}
	}

	@Handler
    public void onServerNumericMessage(ServerNumeric aMsg) {
        chanJoinListener.onServerMessage(aMsg);
        chanPartListener.onServerMessage(aMsg);
        if (!getIRCState().isConnected())
		{
			connectListener.onServerMessage(aMsg);
		}
		nickChangeListener.onServerMessage(aMsg);
		messsageListener.onServerMsg(aMsg);
		kickUserListener.onServerMessage(aMsg);
	}

	/**
	 * (Server-side) error message handling.
	 *
	 * NOTE: There exists 'onClientError'. In this execute listener we are
	 * solely interested in error handling <em>after</em> the command has been
	 * executed. In case an exception is thrown during the initial execution,
	 * then it will be discovered immediately.
	 *
	 * @param aMsg the error message
	 */
	@Handler
	public void onError(ServerError aMsg)
	{
		if (!getIRCState().isConnected())
		{
			connectListener.onError(aMsg);
		}

		if (getIRCState() instanceof IRCStateImpl)
		{
			((IRCStateImpl) (getIRCState())).setConnected(false);
		}
	}

	@Handler
	public void onNickChange(UserNickMessage aMsg)
	{
		if (isForMe(aMsg))
		{
			nickChangeListener.onNickChange(aMsg);
			
			updateNick(aMsg.getNewNick());
		}
	}
	
	public void submitConnectCallback(Callback<IIRCState> aCallback, IServerParameters aServerParameters)
	{
		connectListener.setCallback(aCallback, aServerParameters);
	}

	public void submitJoinChannelCallback(String aChanName, final Callback<IRCChannel> aCallback)
	{
		chanJoinListener.submit(aChanName, aCallback);
	}

	public void submitPartChannelCallback(String aChanName, Callback<String> aCallback)
	{
		chanPartListener.submit(aChanName, aCallback);
	}

	public void submitChangeNickCallback(String aNewNickname, Callback<String> aCallback)
	{
		nickChangeListener.submit(aNewNickname, aCallback);
	}

	public void submitSendMessageCallback(int aAsyncId, Callback<String> aCallback)
	{
		messsageListener.submit(aAsyncId, aCallback);
	}
	
	public void submitKickUserCallback(String aChannel, Callback<String> aCallback)
	{
		kickUserListener.submit(aChannel, aCallback);
	}
	
	private boolean isForMe(AbstractUserMessage aMsg)
	{
		return getIRCState().getNickname().equals((aMsg.getSource()).getNick());
	}
}
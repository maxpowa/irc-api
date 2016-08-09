package com.ircclouds.irc.api.listeners;

import com.ircclouds.irc.api.IRCException;
import com.ircclouds.irc.api.commands.SendRawMessage;
import com.ircclouds.irc.api.domain.IRCNumerics;
import com.ircclouds.irc.api.domain.IRCServerOptions;
import com.ircclouds.irc.api.domain.messages.ServerError;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.interfaces.Callback;
import com.ircclouds.irc.api.interfaces.IIRCSession;
import com.ircclouds.irc.api.interfaces.IServerParameters;
import com.ircclouds.irc.api.state.IIRCState;
import com.ircclouds.irc.api.state.IRCStateImpl;

import java.io.IOException;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ConnectCmdListener
{
	private String nick;
	private IServerParameters params;

	private Properties properties = new Properties();

	private IIRCSession session;
	private Queue<String> altNicks;

	private Callback<IIRCState> callback;

	public ConnectCmdListener(IIRCSession aSession)
	{
		session = aSession;
	}

	public void setCallback(Callback<IIRCState> aCallback, IServerParameters aServerParameters)
	{
		callback = aCallback;
		params = aServerParameters;
		altNicks = new ArrayBlockingQueue<String>(aServerParameters.getAlternativeNicknames().size(), true, aServerParameters.getAlternativeNicknames());
		properties = new Properties();
	}

    public void onServerMessage(ServerNumeric aServMsg) {
        switch (aServMsg.getNumericCode()) {
			case IRCNumerics.ERR_NICKNAMEINUSE:
				String _altNick = null;
				if (!altNicks.isEmpty()) {
					_altNick = altNicks.poll();
				} else {
					throw new RuntimeException("Found no more altnicks!");
				}

				try {
					session.getCommandServer().execute(new SendRawMessage("NICK " + _altNick + "\r\n"));
				} catch (IOException aExc) {
					throw new RuntimeException(aExc);
				}
				break;
			case IRCNumerics.ERR_ERRONEUSNICKNAME:
				throw new RuntimeException("Errorneus nickname");
			case IRCNumerics.RPL_WELCOME:
				nick = aServMsg.getParams().get(0);
				break;
			case IRCNumerics.RPL_ISUPPORT:
				for (String _opt : aServMsg.params) {
					if (_opt.contains("=")) {
						String _kv[] = _opt.split("=");
						properties.put(_kv[0], _kv[1]);
					}
				}
				break;
			case IRCNumerics.RPL_ENDOFMOTD:
			case IRCNumerics.ERR_NOMOTD:
				callback.onSuccess(new IRCStateImpl(
						nick, params.getIdent(), params.getRealname(), params.getAlternativeNicknames(),
						params.getServer(), new IRCServerOptions(properties))
				);
				break;
		}
	}

	public void onError(ServerError aMsg)
	{
		callback.onFailure(new IRCException(aMsg.getText()));
	}
}

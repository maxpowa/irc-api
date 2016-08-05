package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.IServerParameters;
import com.ircclouds.irc.api.utils.StringUtils;

public class ConnectCmd implements ICommand
{
	private final String nick;
	private final String ident;
	private final String realname;
	private final String password;
	
	private final CapCmd capInitCmd;

	public ConnectCmd(final IServerParameters aServerParameters,
			final CapCmd capInitCmd)
	{
		nick = aServerParameters.getNickname();
		ident = aServerParameters.getIdent();
		realname = aServerParameters.getRealname();
		password = aServerParameters.getServer().getPassword();
		this.capInitCmd = capInitCmd;
	}

	@Override
	public String toString()
	{
		return getInitLine() +
				getPasswordLine() +
				"NICK " + nick + CRNL +
				"USER " + ident + " 0 * :" + realname + CRNL;
	}

	private String getInitLine() {
		if (capInitCmd == null) return "";
		// CAP commands have CRNL already appended.
		return capInitCmd.toString();
	}

	private String getPasswordLine() {
		if (StringUtils.isEmpty(password)) return "";
		return "PASS " + password + CRNL;

	}
}

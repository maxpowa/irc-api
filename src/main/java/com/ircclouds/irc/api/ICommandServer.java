package com.ircclouds.irc.api;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

import java.io.IOException;

public interface ICommandServer
{
	void execute(ICommand aCommand) throws IOException;
}

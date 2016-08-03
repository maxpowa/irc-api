package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class ErrorMessageBuilder implements IBuilder<ServerError>
{
	@Override
	public ServerError build(AbstractMessage aMessage)
	{
        return new ServerError(aMessage);
    }
}

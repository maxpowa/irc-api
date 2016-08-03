package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.messages.*;

public class ErrorMessageBuilder implements IBuilder<ErrorMessage>
{
	@Override
	public ErrorMessage build(AbstractMessage aMessage)
	{
        return new ErrorMessage(aMessage);
    }
}

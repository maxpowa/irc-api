package com.ircclouds.irc.api.om;

import java.util.*;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.utils.*;

/**
 * 
 * @author didry
 * 
 */
public class TopicMessageBuilder implements IBuilder<TopicMessage>
{
	@Override
    public TopicMessage build(Message aMessage) {
        return new TopicMessage(aMessage);
    }
}

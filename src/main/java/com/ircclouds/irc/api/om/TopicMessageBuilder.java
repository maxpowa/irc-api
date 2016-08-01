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
	public TopicMessage build(Message aMessage)
	{
		// user TOPIC #channel :topic
		int idx1 = aMessage.raw.indexOf(' ');
		int idx2 = aMessage.raw.indexOf(' ', idx1 + 1);
		int idx3 = aMessage.raw.indexOf(' ', idx2 + 1);

		String _user = aMessage.raw.substring(1, idx1);
		String _chan = aMessage.raw.substring(idx2 + 1, idx3);
		String _topic = aMessage.raw.substring(idx3 + 1, aMessage.raw.length()).substring(1);

		return new TopicMessage(ParseUtils.getUser(aMessage.prefix), _chan, new WritableIRCTopic(_user, new Date(), _topic));
	}
}

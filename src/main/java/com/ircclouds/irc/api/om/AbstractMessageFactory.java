package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.IRCServerOptions;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.GenericMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 
 * @author miguel
 * 
 */

public abstract class AbstractMessageFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageFactory.class);
	
	private static final String PING_KEY = "PING";
	private static final String NOTICE_KEY = "NOTICE";
	private static final String PRIVATE_MESSAGE_KEY = "PRIVMSG";
	private static final String JOIN_KEY = "JOIN";
	private static final String PART_KEY = "PART";
	private static final String QUIT_MESSAGE_KEY = "QUIT";
	private static final String TOPIC_KEY = "TOPIC";
	private static final String NICK_KEY = "NICK";
	private static final String KICK_KEY = "KICK";
	private static final String MODE_KEY = "MODE";
	private static final String ERROR_KEY = "ERROR";
	private static final String AWAY_KEY = "AWAY";
	
	private static final ServerMessageBuilder SERVER_MESSAGE_BUILDER = new ServerMessageBuilder();
	private static final TopicMessageBuilder TOPIC_MESSAGE_BUILDER = new TopicMessageBuilder();
	private static final NickMessageBuilder NICK_MESSAGE_BUILDER = new NickMessageBuilder();
	private static final KickMessageBuilder KICK_MESSAGE_BUILDER = new KickMessageBuilder();
	private static final ServerPingMessageBuilder SERVER_PING_MESSAGE_BUILDER = new ServerPingMessageBuilder();
	private static final ChanJoinBuilder CHAN_JOIN_BUILDER = new ChanJoinBuilder();
	private static final ChanPartBuilder CHAN_PART_BUILDER = new ChanPartBuilder();
	private static final QuitMessageBuilder QUIT_MESSAGE_BUILDER = new QuitMessageBuilder();
	private static final ErrorMessageBuilder ERROR_MESSAGE_BUILDER = new ErrorMessageBuilder();
	private static final AwayMessageBuilder AWAY_MESSAGE_BUILDER = new AwayMessageBuilder();
	
	private final AbstractPrivateMessageBuilder PRIVATE_MESSAGE_BUILDER;
	private final AbstractNoticeBuilder NOTICE_BUILDER;
	private final AbstractChanModeBuilder CHAN_MODE_BUILDER;
			
	public AbstractMessageFactory()
	{
		PRIVATE_MESSAGE_BUILDER = new AbstractPrivateMessageBuilder()
		{
			@Override
			protected Set<Character> getChannelTypes()
			{
				return AbstractMessageFactory.this.getIRCServerOptions().getChanTypes();
			}
		};
		NOTICE_BUILDER = new AbstractNoticeBuilder()
		{
			@Override
			protected Set<Character> getChannelTypes()
			{
				return AbstractMessageFactory.this.getIRCServerOptions().getChanTypes();
			}		
		};
		CHAN_MODE_BUILDER = new AbstractChanModeBuilder()
		{
			@Override
			protected IRCServerOptions getIRCServerOptions()
			{
				return AbstractMessageFactory.this.getIRCServerOptions();
			}
		};
	}

	public AbstractMessage build(String aMsg)
	{
		LOG.debug(aMsg);

		GenericMessage msg = new GenericMessage(aMsg);
		if (PING_KEY.equals(msg.command)) {
			return SERVER_PING_MESSAGE_BUILDER.build(msg);
		} else if (PRIVATE_MESSAGE_KEY.equals(msg.command)) {
			return PRIVATE_MESSAGE_BUILDER.build(msg);
		} else if (NOTICE_KEY.equals(msg.command)) {
			return NOTICE_BUILDER.build(msg);
		} else if (JOIN_KEY.equals(msg.command)) {
			return CHAN_JOIN_BUILDER.build(msg);
		} else if (PART_KEY.equals(msg.command)) {
			return CHAN_PART_BUILDER.build(msg);
		} else if (QUIT_MESSAGE_KEY.equals(msg.command)) {
			return QUIT_MESSAGE_BUILDER.build(msg);
		} else if (TOPIC_KEY.equals(msg.command)) {
			return TOPIC_MESSAGE_BUILDER.build(msg);
		} else if (NICK_KEY.equals(msg.command)) {
			return NICK_MESSAGE_BUILDER.build(msg);
		} else if (KICK_KEY.equals(msg.command)) {
			return KICK_MESSAGE_BUILDER.build(msg);
		} else if (MODE_KEY.equals(msg.command) && getIRCServerOptions().getChanTypes().contains(msg.params.get(0).charAt(0))) {
			return CHAN_MODE_BUILDER.build(msg);
		} else if (AWAY_KEY.equals(msg.command)) {
			return AWAY_MESSAGE_BUILDER.build(msg);
		} else if (isNumeric(msg.command)) {
			return SERVER_MESSAGE_BUILDER.build(msg);
		} else if (ERROR_KEY.equals(msg.command)) {
			return ERROR_MESSAGE_BUILDER.build(msg);
		}

		// If all else fails just return the plain old generic message.
		return msg;
	}
	
	protected abstract IRCServerOptions getIRCServerOptions();
	
	private static boolean isNumeric(String aMsgType)
	{
		try
		{
			Integer.parseInt(aMsgType);
		}
		catch (NumberFormatException aExc)
		{
			return false;
		}

		return true;
	}
}

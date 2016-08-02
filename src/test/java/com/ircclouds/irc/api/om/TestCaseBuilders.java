package com.ircclouds.irc.api.om;

import java.util.*;

import junit.framework.*;

import com.ircclouds.irc.api.domain.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.domain.messages.interfaces.*;
import com.ircclouds.irc.api.utils.*;

/**
 * Oh yes first tests :O
 * @author yoann
 *
 */
public class TestCaseBuilders extends TestCase
{

	private static final String TEST_CHANNEL = "#math";
	private static final WritableIRCUser   TEST_USER;
	
	static 
	{
	    TEST_USER = new WritableIRCUser("soka", "k", "dyoann.dyndns.org");
	}
	
	private static final String USER_STRING  = ":"+TEST_USER.getNick()+"!"+TEST_USER.getIdent()+"@"+TEST_USER.getHostname();
	
	/**
	 * Test against dummy values
	 * @param aUser
	 * @param aChannel
	 */
	private void checkChannelAndUser(IRCUser aUser, String aChannel, boolean removeDash)
	{
		if(removeDash)
		{
			assertEquals(TEST_CHANNEL,aChannel);	
		}
		else
		{
			assertEquals(TEST_CHANNEL,aChannel);
		}
		
		assertEquals(TEST_USER, aUser);
	}
	
	/**
	 * Test to build a JOIN MESSAGE
	 */
	public void testChanJoinBuiler()
	{
		ChanJoinBuilder _builder = new ChanJoinBuilder();
		ChanJoinMessage _msg = _builder.build(new Message(USER_STRING + " JOIN " + TEST_CHANNEL));
		checkChannelAndUser(_msg.getSource(),_msg.getChannelName(),true);
	}
	
	/**
	 * Test to build a PART MESSAGE
	 */
	public void testChannelPartBuilder()
	{
		ChanJoinBuilder _builder = new ChanJoinBuilder();
		ChanJoinMessage _msg = _builder.build(new Message(USER_STRING + " PART " + TEST_CHANNEL));
		checkChannelAndUser(_msg.getSource(), _msg.getChannelName(), true);
	}
	
	/**
	 * Test to build a PART MESSAGE
	 */
	public void testNoticeBuilder()
	{
		AbstractNoticeBuilder _builder = new AbstractNoticeBuilder()
		{
			@Override
			protected Set<Character> getChannelTypes()
			{
				return new LinkedHashSet<Character>() {{ add('#'); }};
			}
		};
		IMessage _msg = _builder.build(new Message("NOTICE :Server Shit"));
		assertEquals(ServerNotice.class, _msg.getClass());
		assertEquals("Server Shit", ((ServerNotice) _msg).getText());

		_msg = _builder.build(new Message(USER_STRING + " NOTICE :Something To An User"));
		assertEquals(UserNotice.class, _msg.getClass());
		assertEquals(TEST_USER,((UserNotice)_msg).getSource());
		assertEquals("Something To An User", ((UserNotice) _msg).getText());

		_msg = _builder.build(new Message(USER_STRING + " NOTICE " + TEST_CHANNEL + " :Something To a Chan"));
		assertEquals(ChannelNotice.class, _msg.getClass());
		checkChannelAndUser(((ChannelNotice) _msg).getSource(), ((ChannelNotice) _msg).getChannelName(), false);
		assertEquals("Something To a Chan", ((ChannelNotice) _msg).getText());
	}
	
	/**
	 * Test to build a PING MESSAGE
	 */
	public void testPingBuilder()
	{
		ServerPingMessageBuilder _builder = new ServerPingMessageBuilder();
		ServerPing _msg = _builder.build(new Message("PING miguel :1234"));
		assertEquals("1234", _msg.getText());
	}

	
	/**
	 * Test to build a PRIVMSG MESSAGE
	 */
	public void testQuitMessageBuilder()
	{
		QuitMessageBuilder _builder = new QuitMessageBuilder();
		QuitMessage _msg = _builder.build(new Message(USER_STRING + " QUIT :stfu message"));
		assertEquals("stfu message",_msg.getQuitMsg());
		assertEquals(TEST_USER,_msg.getSource());
	}
	
	
	/**
	 * Test to build a TOPIC MESSAGE
	 */
	public void testTopicMessageBuilder()
	{
		TopicMessageBuilder _builder = new TopicMessageBuilder();
		TopicMessage _msg = _builder.build(new Message(USER_STRING + " TOPIC " + TEST_CHANNEL + " :let's set that topic :D"));
		checkChannelAndUser(ParseUtils.getUser(_msg.getTopic().getSetBy()), _msg.getChannelName(), false);
		assertEquals(_msg.getChannelName(), TEST_CHANNEL);
		assertEquals(_msg.getTopic().getValue(),"let's set that topic :D");
		
	}
	
	/**
	 * Test to build a PRIVMSG MESSAGE
	 */
	public void testPrivateMessageBuilder()
	{
		AbstractPrivateMessageBuilder _builder = new AbstractPrivateMessageBuilder() {
			@Override
			protected Set<Character> getChannelTypes() {
				return new LinkedHashSet<Character>() {{
					add('#');
				}};
			}
		};
		IMessage _msg = _builder.build(new Message(USER_STRING + " PRIVMSG :Something To An User"));
		assertEquals(UserPrivMsg.class, _msg.getClass());
		assertEquals(TEST_USER, ((UserPrivMsg) _msg).getSource());
		assertEquals("Something To An User", ((UserPrivMsg) _msg).getText());

		_msg = _builder.build(new Message(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :Something To a Chan"));
		assertEquals(ChannelPrivMsg.class, _msg.getClass());
		checkChannelAndUser(((ChannelPrivMsg) _msg).getSource(), ((ChannelPrivMsg) _msg).getChannelName(), false);
		assertEquals("Something To a Chan", ((ChannelPrivMsg) _msg).getText());
	}
	
	/**
	 * Test to build a SERVER MESSAGE
	 */
	public void testServerMessageBuilder()
	{
		ServerMessageBuilder _builder = new ServerMessageBuilder();
		ServerNumericMessage _msg = _builder.build(new Message("421 WTF :Unknown command"));
		assertEquals(421, (int) _msg.getNumericCode());
		//assertEquals(TEST_USER, _msg.getTarget());

		_msg = _builder.build(new Message(":chaos.esper.net 005 Nickname SAFELIST ELIST=CTU CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLPQTcgimnprstz CHANLIMIT=#:50 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=EsperNet KNOCK :are supported by this server"));
		assertEquals(5, (int) _msg.getNumericCode());
	}
	
}

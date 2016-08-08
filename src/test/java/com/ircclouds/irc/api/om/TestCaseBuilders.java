package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.domain.IRCUser;
import com.ircclouds.irc.api.domain.WritableIRCUser;
import com.ircclouds.irc.api.domain.messages.AbstractMessage;
import com.ircclouds.irc.api.domain.messages.ChannelAction;
import com.ircclouds.irc.api.domain.messages.ChannelCTCP;
import com.ircclouds.irc.api.domain.messages.ChannelJoin;
import com.ircclouds.irc.api.domain.messages.ChannelKick;
import com.ircclouds.irc.api.domain.messages.ChannelNotice;
import com.ircclouds.irc.api.domain.messages.ChannelPart;
import com.ircclouds.irc.api.domain.messages.ChannelPing;
import com.ircclouds.irc.api.domain.messages.ChannelPrivMsg;
import com.ircclouds.irc.api.domain.messages.ChannelTopic;
import com.ircclouds.irc.api.domain.messages.ChannelVersion;
import com.ircclouds.irc.api.domain.messages.ClientErrorMessage;
import com.ircclouds.irc.api.domain.messages.GenericMessage;
import com.ircclouds.irc.api.domain.messages.ServerError;
import com.ircclouds.irc.api.domain.messages.ServerNotice;
import com.ircclouds.irc.api.domain.messages.ServerNumeric;
import com.ircclouds.irc.api.domain.messages.ServerPing;
import com.ircclouds.irc.api.domain.messages.UserAction;
import com.ircclouds.irc.api.domain.messages.UserAwayMessage;
import com.ircclouds.irc.api.domain.messages.UserCTCP;
import com.ircclouds.irc.api.domain.messages.UserNickMessage;
import com.ircclouds.irc.api.domain.messages.UserNotice;
import com.ircclouds.irc.api.domain.messages.UserPing;
import com.ircclouds.irc.api.domain.messages.UserPrivMsg;
import com.ircclouds.irc.api.domain.messages.UserQuitMessage;
import com.ircclouds.irc.api.domain.messages.UserVersion;
import com.ircclouds.irc.api.utils.ParseUtils;

import junit.framework.TestCase;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Oh yes first tests :O
 *
 * @author yoann
 */
public class TestCaseBuilders extends TestCase {

    private static final String TEST_CHANNEL = "#math";
    private static final WritableIRCUser TEST_USER = new WritableIRCUser("soka", "k", "dyoann.dyndns.org");
    private static final IRCServer TEST_SERVER = new IRCServer("chaos.esper.net");

    private static final String USER_STRING = ":" + TEST_USER.getNick() + "!" + TEST_USER.getIdent() + "@" + TEST_USER.getHostname();

    /**
     * Test against dummy values
     *
     * @param aUser
     * @param aChannel
     */
    private void checkChannelAndUser(IRCUser aUser, String aChannel, boolean removeDash) {
        if (removeDash) {
            assertEquals(TEST_CHANNEL, aChannel);
        } else {
            assertEquals(TEST_CHANNEL, aChannel);
        }

        assertEquals(TEST_USER, aUser);
    }

    /**
     * Test to build a JOIN MESSAGE
     */
    public void testChannelJoinBuilder() {
        ChanJoinBuilder _builder = new ChanJoinBuilder();
        GenericMessage input = new GenericMessage(USER_STRING + " JOIN " + TEST_CHANNEL);
        ChannelJoin _msg = _builder.build(input);
        checkChannelAndUser(_msg.getSource(), _msg.getChannelName(), true);
    }

    /**
     * Test to build a PART MESSAGE
     */
    public void testChannelPartBuilder() {
        ChanPartBuilder _builder = new ChanPartBuilder();
        GenericMessage input = new GenericMessage(USER_STRING + " PART " + TEST_CHANNEL);
        ChannelPart _msg = _builder.build(input);
        checkChannelAndUser(_msg.getSource(), _msg.getChannelName(), true);
    }

    /**
     * Test to build a PART MESSAGE
     */
    public void testNoticeBuilder() {
        AbstractNoticeBuilder _builder = new AbstractNoticeBuilder() {
            @Override
            protected Set<Character> getChannelTypes() {
                return new LinkedHashSet<Character>() {{
                    add('#');
                }};
            }
        };
        GenericMessage input = new GenericMessage("NOTICE :Server Shit");
        AbstractMessage _msg = _builder.build(input);
        assertEquals(ServerNotice.class, _msg.getClass());
        assertNull(_msg.getSource());
        assertEquals("Server Shit", ((ServerNotice) _msg).getText());

        input = new GenericMessage(USER_STRING + " NOTICE :Something To An User");
        _msg = _builder.build(input);
        assertEquals(UserNotice.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserNotice) _msg).getSource());
        assertEquals("Something To An User", ((UserNotice) _msg).getText());

        input = new GenericMessage(USER_STRING + " NOTICE " + TEST_CHANNEL + " :Something To a Chan");
        _msg = _builder.build(input);
        assertEquals(ChannelNotice.class, _msg.getClass());
        checkChannelAndUser(((ChannelNotice) _msg).getSource(), ((ChannelNotice) _msg).getChannelName(), false);
        assertEquals("Something To a Chan", ((ChannelNotice) _msg).getText());
    }

    /**
     * Test to build a PING MESSAGE
     */
    public void testPingBuilder() {
        ServerPingMessageBuilder _builder = new ServerPingMessageBuilder();
        GenericMessage input = new GenericMessage("PING :" + TEST_SERVER);
        ServerPing _msg = _builder.build(input);
        assertNull(_msg.getSource());
        assertEquals(TEST_SERVER.toString(), _msg.getText());
    }

    public void testErrorBuilder() {
        ErrorMessageBuilder _builder = new ErrorMessageBuilder();
        GenericMessage input = new GenericMessage("ERROR :Unfortunately, something broke");
        ServerError _msg = _builder.build(input);
        assertEquals("Unfortunately, something broke", _msg.getText());

        input = new GenericMessage(":" + TEST_SERVER + " ERROR :Unfortunately, something broke");
        _msg = _builder.build(input);
        assertEquals(TEST_SERVER.toString(), _msg.getSource().toString());
        assertEquals("Unfortunately, something broke", _msg.getText());
    }


    /**
     * Test to build a PRIVMSG MESSAGE
     */
    public void testQuitMessageBuilder() {
        QuitMessageBuilder _builder = new QuitMessageBuilder();
        UserQuitMessage _msg = _builder.build(new GenericMessage(USER_STRING + " QUIT :stfu message"));
        assertEquals("stfu message", _msg.getText());
        assertEquals(TEST_USER, _msg.getSource());
    }

    public void testAwayMessageBuilder() {
        AwayMessageBuilder _builder = new AwayMessageBuilder();
        GenericMessage input = new GenericMessage(USER_STRING + " AWAY :user is away");
        UserAwayMessage _msg = _builder.build(input);
        assertEquals(true, _msg.isAway());
        assertEquals("user is away", _msg.getText());
        assertEquals(TEST_USER, _msg.getSource());

        input = new GenericMessage(USER_STRING + " AWAY");
        _msg = _builder.build(input);
        assertEquals(false, _msg.isAway());
        assertEquals(null, _msg.getText());
        assertEquals(TEST_USER, _msg.getSource());
    }

    public void testNickMessageBuilder() {
        NickMessageBuilder _builder = new NickMessageBuilder();
        GenericMessage input = new GenericMessage(USER_STRING + " NICK :soka|away");
        UserNickMessage _msg = _builder.build(input);
        assertEquals("soka|away", _msg.getNewNick());
        assertEquals(TEST_USER, _msg.getSource());
    }

    public void testKickMessageBuilder() {
        KickMessageBuilder _builder = new KickMessageBuilder();
        GenericMessage input = new GenericMessage(USER_STRING + " KICK " + TEST_CHANNEL + " akos :reason");
        ChannelKick _msg = _builder.build(input);
        assertEquals(TEST_CHANNEL, _msg.getChannelName());
        assertEquals("reason", _msg.getText());
        assertEquals("akos", _msg.getKickedNickname());
        assertEquals(TEST_USER, _msg.getSource());
    }

    /**
     * Test to build a TOPIC MESSAGE
     */
    public void testTopicMessageBuilder() {
        TopicMessageBuilder _builder = new TopicMessageBuilder();
        ChannelTopic _msg = _builder.build(new GenericMessage(USER_STRING + " TOPIC " + TEST_CHANNEL + " :let's set that topic :D"));
        checkChannelAndUser(ParseUtils.getUser(_msg.getTopic().getSetBy()), _msg.getChannelName(), false);
        assertEquals(_msg.getChannelName(), TEST_CHANNEL);
        assertEquals(_msg.getTopic().getValue(), "let's set that topic :D");

    }

    /**
     * Test to build a PRIVMSG MESSAGE
     */
    public void testPrivateMessageBuilder() {
        AbstractPrivateMessageBuilder _builder = new AbstractPrivateMessageBuilder() {
            @Override
            protected Set<Character> getChannelTypes() {
                return new LinkedHashSet<Character>() {{
                    add('#');
                }};
            }
        };

        AbstractMessage _msg = _builder.build(new GenericMessage("@tag=value " + USER_STRING + " PRIVMSG User :Something To An User"));
        assertEquals(UserPrivMsg.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserPrivMsg) _msg).getSource());
        assertEquals("value", ((UserPrivMsg) _msg).getTags().get("tag"));
        assertEquals("Something To An User", ((UserPrivMsg) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG User :Something To An User"));
        assertEquals(UserPrivMsg.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserPrivMsg) _msg).getSource());
        assertEquals("Something To An User", ((UserPrivMsg) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG User :\001generic CTCP to a user\001"));
        assertEquals(UserCTCP.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserCTCP) _msg).getSource());
        assertEquals("generic CTCP to a user", ((UserCTCP) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG User :\001PING CTCP to a user\001"));
        assertEquals(UserPing.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserPing) _msg).getSource());
        assertEquals("CTCP to a user", ((UserPing) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG User :\001VERSION CTCP to a user\001"));
        assertEquals(UserVersion.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserVersion) _msg).getSource());
        assertEquals("CTCP to a user", ((UserVersion) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG User :\001ACTION CTCP to a user\001"));
        assertEquals(UserAction.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserAction) _msg).getSource());
        assertEquals("User", ((UserAction) _msg).getTarget());
        assertEquals("CTCP to a user", ((UserAction) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :Something To a Chan"));
        assertEquals(ChannelPrivMsg.class, _msg.getClass());
        checkChannelAndUser(((ChannelPrivMsg) _msg).getSource(), ((ChannelPrivMsg) _msg).getChannelName(), false);
        assertEquals("Something To a Chan", ((ChannelPrivMsg) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001generic CTCP to a chan\001"));
        assertEquals(ChannelCTCP.class, _msg.getClass());
        checkChannelAndUser(((ChannelCTCP) _msg).getSource(), ((ChannelCTCP) _msg).getChannelName(), false);
        assertEquals("generic CTCP to a chan", ((ChannelCTCP) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001PING CTCP to a chan\001"));
        assertEquals(ChannelPing.class, _msg.getClass());
        checkChannelAndUser(((ChannelPing) _msg).getSource(), ((ChannelPing) _msg).getChannelName(), false);
        assertEquals("CTCP to a chan", ((ChannelPing) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001VERSION CTCP to a chan\001"));
        assertEquals(ChannelVersion.class, _msg.getClass());
        checkChannelAndUser(((ChannelVersion) _msg).getSource(), ((ChannelVersion) _msg).getChannelName(), false);
        assertEquals("CTCP to a chan", ((ChannelVersion) _msg).getText());

        _msg = _builder.build(new GenericMessage(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001ACTION CTCP to a chan\001"));
        assertEquals(ChannelAction.class, _msg.getClass());
        checkChannelAndUser(((ChannelAction) _msg).getSource(), ((ChannelAction) _msg).getChannelName(), false);
        assertEquals("CTCP to a chan", ((ChannelAction) _msg).getText());
    }

    /**
     * Test to build a SERVER MESSAGE
     */
    public void testServerMessageBuilder() {
        ServerMessageBuilder _builder = new ServerMessageBuilder();
        ServerNumeric _msg = _builder.build(new GenericMessage("421 WTF :Unknown command"));
        assertEquals(421, (int) _msg.getNumericCode());
        assertNull(_msg.getSource());
        //assertEquals(TEST_USER, _msg.getTarget());

        _msg = _builder.build(new GenericMessage(":chaos.esper.net 005 Nickname SAFELIST ELIST=CTU CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLPQTcgimnprstz CHANLIMIT=#:50 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=EsperNet KNOCK :are supported by this server"));
        assertEquals(5, (int) _msg.getNumericCode());
    }

    public void testClientErrorMessage() {
        Exception tmp = new Exception("Test exception");
        ClientErrorMessage _msg = new ClientErrorMessage(tmp);
        assertEquals("java.lang.Exception: Test exception", _msg.asRaw());
        assertEquals(tmp, _msg.getException());
        assertNull(_msg.getSource());
    }

    public void testNumericParseError() {
        try {
            new ServerNumeric(new GenericMessage("PASS :should throw a ParseError"));
            fail("ServerNumeric didn't throw exception when created with non-numeric command");
        } catch (AbstractMessage.ParseError err) {
            // pass
        }
    }

}

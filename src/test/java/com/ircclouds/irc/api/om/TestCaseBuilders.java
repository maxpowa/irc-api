package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.domain.IRCUser;
import com.ircclouds.irc.api.domain.WritableIRCUser;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.domain.messages.interfaces.IMessage;
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
        Message input = new Message(USER_STRING + " JOIN " + TEST_CHANNEL);
        ChannelJoin _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
        checkChannelAndUser(_msg.getSource(), _msg.getChannelName(), true);
    }

    /**
     * Test to build a PART MESSAGE
     */
    public void testChannelPartBuilder() {
        ChanPartBuilder _builder = new ChanPartBuilder();
        Message input = new Message(USER_STRING + " PART " + TEST_CHANNEL);
        ChannelPart _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
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
        Message input = new Message("NOTICE :Server Shit");
        IMessage _msg = _builder.build(input);
        assertEquals(ServerNotice.class, _msg.getClass());
        assertEquals(input.raw, _msg.asRaw());
        assertEquals("Server Shit", ((ServerNotice) _msg).getText());

        input = new Message(USER_STRING + " NOTICE :Something To An User");
        _msg = _builder.build(input);
        assertEquals(UserNotice.class, _msg.getClass());
        assertEquals(input.raw, _msg.asRaw());
        assertEquals(TEST_USER, ((UserNotice) _msg).getSource());
        assertEquals("Something To An User", ((UserNotice) _msg).getText());

        input = new Message(USER_STRING + " NOTICE " + TEST_CHANNEL + " :Something To a Chan");
        _msg = _builder.build(input);
        assertEquals(ChannelNotice.class, _msg.getClass());
        assertEquals(input.raw, _msg.asRaw());
        checkChannelAndUser(((ChannelNotice) _msg).getSource(), ((ChannelNotice) _msg).getChannelName(), false);
        assertEquals("Something To a Chan", ((ChannelNotice) _msg).getText());
    }

    /**
     * Test to build a PING MESSAGE
     */
    public void testPingBuilder() {
        ServerPingMessageBuilder _builder = new ServerPingMessageBuilder();
        Message input = new Message("PING :" + TEST_SERVER);
        ServerPing _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
        assertEquals(TEST_SERVER.toString(), _msg.getText());
    }

    public void testErrorBuilder() {
        ErrorMessageBuilder _builder = new ErrorMessageBuilder();
        Message input = new Message("ERROR :Unfortunately, something broke");
        ErrorMessage _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
        assertEquals("Unfortunately, something broke", _msg.getText());
    }


    /**
     * Test to build a PRIVMSG MESSAGE
     */
    public void testQuitMessageBuilder() {
        QuitMessageBuilder _builder = new QuitMessageBuilder();
        QuitMessage _msg = _builder.build(new Message(USER_STRING + " QUIT :stfu message"));
        assertEquals("stfu message", _msg.getQuitMsg());
        assertEquals(TEST_USER, _msg.getSource());
    }

    public void testAwayMessageBuilder() {
        AwayMessageBuilder _builder = new AwayMessageBuilder();
        Message input = new Message(USER_STRING + " AWAY :user is away");
        AwayMessage _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
        assertEquals(true, _msg.isAway());
        assertEquals("user is away", _msg.getText());
        assertEquals(TEST_USER, _msg.getSource());

        input = new Message(USER_STRING + " AWAY");
        _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
        assertEquals(false, _msg.isAway());
        assertEquals(null, _msg.getText());
        assertEquals(TEST_USER, _msg.getSource());
    }

    public void testNickMessageBuilder() {
        NickMessageBuilder _builder = new NickMessageBuilder();
        Message input = new Message(USER_STRING + " NICK :soka|away");
        NickMessage _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
        assertEquals("soka|away", _msg.getNewNick());
        assertEquals(TEST_USER, _msg.getSource());
    }

    public void testKickMessageBuilder() {
        KickMessageBuilder _builder = new KickMessageBuilder();
        Message input = new Message(USER_STRING + " KICK " + TEST_CHANNEL + " akos :reason");
        ChannelKick _msg = _builder.build(input);
        assertEquals(input.raw, _msg.asRaw());
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
        TopicMessage _msg = _builder.build(new Message(USER_STRING + " TOPIC " + TEST_CHANNEL + " :let's set that topic :D"));
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
        IMessage _msg = _builder.build(new Message(USER_STRING + " PRIVMSG :Something To An User"));
        assertEquals(UserPrivMsg.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserPrivMsg) _msg).getSource());
        assertEquals("Something To An User", ((UserPrivMsg) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG :\001generic CTCP to a user\001"));
        assertEquals(UserCTCP.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserCTCP) _msg).getSource());
        assertEquals("generic CTCP to a user", ((UserCTCP) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG :\001PING CTCP to a user\001"));
        assertEquals(UserPing.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserPing) _msg).getSource());
        assertEquals("CTCP to a user", ((UserPing) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG :\001VERSION CTCP to a user\001"));
        assertEquals(UserVersion.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserVersion) _msg).getSource());
        assertEquals("CTCP to a user", ((UserVersion) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG :\001ACTION CTCP to a user\001"));
        assertEquals(UserAction.class, _msg.getClass());
        assertEquals(TEST_USER, ((UserAction) _msg).getSource());
        assertEquals("CTCP to a user", ((UserAction) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :Something To a Chan"));
        assertEquals(ChannelPrivMsg.class, _msg.getClass());
        checkChannelAndUser(((ChannelPrivMsg) _msg).getSource(), ((ChannelPrivMsg) _msg).getChannelName(), false);
        assertEquals("Something To a Chan", ((ChannelPrivMsg) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001generic CTCP to a chan\001"));
        assertEquals(ChannelCTCP.class, _msg.getClass());
        checkChannelAndUser(((ChannelCTCP) _msg).getSource(), ((ChannelCTCP) _msg).getChannelName(), false);
        assertEquals("generic CTCP to a chan", ((ChannelCTCP) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001PING CTCP to a chan\001"));
        assertEquals(ChannelPing.class, _msg.getClass());
        checkChannelAndUser(((ChannelPing) _msg).getSource(), ((ChannelPing) _msg).getChannelName(), false);
        assertEquals("CTCP to a chan", ((ChannelPing) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001VERSION CTCP to a chan\001"));
        assertEquals(ChannelVersion.class, _msg.getClass());
        checkChannelAndUser(((ChannelVersion) _msg).getSource(), ((ChannelVersion) _msg).getChannelName(), false);
        assertEquals("CTCP to a chan", ((ChannelVersion) _msg).getText());

        _msg = _builder.build(new Message(USER_STRING + " PRIVMSG " + TEST_CHANNEL + " :\001ACTION CTCP to a chan\001"));
        assertEquals(ChannelAction.class, _msg.getClass());
        checkChannelAndUser(((ChannelAction) _msg).getSource(), ((ChannelAction) _msg).getChannelName(), false);
        assertEquals("CTCP to a chan", ((ChannelAction) _msg).getText());
    }

    /**
     * Test to build a SERVER MESSAGE
     */
    public void testServerMessageBuilder() {
        ServerMessageBuilder _builder = new ServerMessageBuilder();
        ServerNumeric _msg = _builder.build(new Message("421 WTF :Unknown command"));
        assertEquals(421, (int) _msg.getNumericCode());
        //assertEquals(TEST_USER, _msg.getTarget());

        _msg = _builder.build(new Message(":chaos.esper.net 005 Nickname SAFELIST ELIST=CTU CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLPQTcgimnprstz CHANLIMIT=#:50 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=EsperNet KNOCK :are supported by this server"));
        assertEquals(5, (int) _msg.getNumericCode());
    }

}

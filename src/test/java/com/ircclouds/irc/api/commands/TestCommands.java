package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.IServerParameters;
import com.ircclouds.irc.api.domain.IRCServer;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Created by maxgurela on 8/4/16.
 */
public class TestCommands extends TestCase {

    private static IServerParameters getServerParams(final String aNickname, final List<String> aAlternativeNicks, final String aRealname, final String aIdent,
                                                     final String aServerName, final String aServerPassword) {
        return new IServerParameters() {
            @Override
            public IRCServer getServer() {
                return new IRCServer(aServerName, aServerPassword);
            }

            @Override
            public String getRealname() {
                return aRealname;
            }

            @Override
            public String getNickname() {
                return aNickname;
            }

            @Override
            public String getIdent() {
                return aIdent;
            }

            @Override
            public List<String> getAlternativeNicknames() {
                return aAlternativeNicks;
            }
        };
    }

    private static void assertStringEquals(Object one, Object two) {
        assertEquals(one.toString(), two.toString());
    }

    public void testCapCommands() {
        assertStringEquals("CAP LS\r\n", new CapLsCmd());
        assertStringEquals("CAP REQ :away-notify sasl\r\n", new CapReqCmd("away-notify", "sasl"));
        assertStringEquals("CAP END\r\n", new CapEndCmd());
    }

    public void testModeCommand() {
        assertStringEquals("MODE +i\r\n", new ChangeModeCmd("+i"));
    }

    public void testNickCommand() {
        assertStringEquals("NICK :dave2\r\n", new ChangeNickCmd("dave2"));
    }

    public void testTopicCommand() {
        assertStringEquals("TOPIC #chan :This is the new topic\r\n", new ChangeTopicCmd("#chan", "This is the new topic"));
    }

    public void testConnectCommand() {
        IServerParameters params = getServerParams("nick", Arrays.asList("altNick1", "altNick2"), "IRC Api", "ident", "irc.esper.net", "password");
        assertStringEquals("PASS password\r\nNICK nick\r\nUSER ident 0 * :IRC Api\r\n", new ConnectCmd(params, null));

        params = getServerParams("nick", Arrays.asList("altNick1", "altNick2"), "IRC Api", "ident", "irc.esper.net", null);
        assertStringEquals("NICK nick\r\nUSER ident 0 * :IRC Api\r\n", new ConnectCmd(params, null));
        assertStringEquals("CAP LS\r\nNICK nick\r\nUSER ident 0 * :IRC Api\r\n", new ConnectCmd(params, new CapLsCmd()));
    }

    public void testJoinCommand() {
        assertStringEquals("JOIN #channel\r\n", new JoinChanCmd("#channel"));
        assertStringEquals("JOIN #channel :passphrase\r\n", new JoinChanCmd("#channel", "passphrase"));
    }

    public void testKickCommand() {
        assertStringEquals("KICK #channel nickname :reason\r\n", new KickUserCmd("#channel", "nickname", "reason"));
    }

    public void testPartCommand() {
        assertStringEquals("PART #channel :reason\r\n", new PartChanCmd("#channel", "reason"));
    }

    public void testQuitCommand() {
        assertStringEquals("QUIT :Leaving\r\n", new QuitCmd());
        assertStringEquals("QUIT :reason\r\n", new QuitCmd("reason"));
    }

    public void testActionCommand() {
        assertStringEquals("PRIVMSG #channel :\001ACTION does stuff\001\r\n", new SendActionMessage("#channel", "does stuff"));
        assertStringEquals("PRIVMSG #channel,1 :\001ACTION does stuff\001\r\n", new SendActionMessage("#channel", "does stuff", 1));
    }

    public void testNoticeCommand() {
        assertStringEquals("NOTICE #channel :message\r\n", new SendNoticeMessage("#channel", "message"));
        assertStringEquals("NOTICE #channel,1 :message\r\n", new SendNoticeMessage("#channel", "message", 1));
    }

    public void testPrivateMessageCommand() {
        assertStringEquals("PRIVMSG #channel :message\r\n", new SendPrivateMessage("#channel", "message"));
        assertStringEquals("PRIVMSG #channel,1 :message\r\n", new SendPrivateMessage("#channel", "message", 1));
    }

    public void testRawMessageCommand() {
        assertStringEquals("MESSAGE :goes here\r\n", new SendRawMessage("MESSAGE :goes here"));
    }

    public void testPingReplyCommand() {
        assertStringEquals("PONG :data identifier\r\n", new SendServerPingReplyCmd("data identifier"));
    }
}

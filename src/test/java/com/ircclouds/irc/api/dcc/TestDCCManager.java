package com.ircclouds.irc.api.dcc;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.dcc.interfaces.IDCCReceiveCallback;
import com.ircclouds.irc.api.dcc.interfaces.IDCCReceiveResult;
import com.ircclouds.irc.api.dcc.interfaces.IDCCSendCallback;
import com.ircclouds.irc.api.dcc.interfaces.IDCCSendResult;
import com.ircclouds.irc.api.interfaces.Callback;
import com.ircclouds.irc.api.interfaces.IIRCApi;
import mockit.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestDCCManager {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public IIRCApi getApiWithExpectedResponses(final String expectedTarget, final String expectedMessage) {
        return new IRCApi(false) {
            @Mock
            public void message(String aTarget, String aMessage) {
                assertEquals(expectedTarget, aTarget);
                assertEquals(expectedMessage, aMessage);
            }

            @Mock
            public void message(String aTarget, String aMessage, Callback<String> aCallback) {
                message(aTarget, aMessage);
                assertNull(aCallback);
            }

        };
    }

    @Test
    public void testDCCAccept() throws IOException {
        String nick = "ExpectedNick";
        String response = "\001DCC ACCEPT cool_file.doc 1337 0\001";

        File tmpFile = folder.newFile("cool_file.doc");

        DCCManager manager = new DCCManager(getApiWithExpectedResponses(nick, response));

        manager.dccAccept(nick, tmpFile, 1337, 0L, 5, new IDCCSendCallback() {
            @Override
            public void onSuccess(IDCCSendResult aU) {
                // cool
            }

            @Override
            public void onFailure(DCCSendException aV) {
                // aww
            }
        });

        assertEquals(1, manager.activeDCCSendsCount());
    }

    @Test
    public void testDCCSend() throws IOException {
        File tmpFile = folder.newFile("cool_file.doc");

        String nick = "ExpectedNick";
        String response = "\001DCC SEND cool_file.doc localhost 1337 " + tmpFile.length() + "\001";

        DCCManager manager = new DCCManager(getApiWithExpectedResponses(nick, response));

        manager.dccSend(nick, tmpFile, "localhost", 1337, 5, new IDCCSendCallback() {
            @Override
            public void onSuccess(IDCCSendResult aU) {
                // cool
            }

            @Override
            public void onFailure(DCCSendException aV) {
                // aww
            }
        });

        assertEquals(1, manager.activeDCCSendsCount());
    }

    @Test
    public void testDCCResume() throws IOException {
        File tmpFile = folder.newFile("cool_file.doc");

        String nick = "ExpectedNick";
        String response = "\001DCC SEND cool_file.doc localhost 1337 " + tmpFile.length() + "\001";

        DCCManager manager = new DCCManager(getApiWithExpectedResponses(nick, response));

        manager.dccResume(tmpFile, 0L, tmpFile.length(), new InetSocketAddress(1337), new IDCCReceiveCallback() {
            @Override
            public void onSuccess(IDCCReceiveResult aU) {
                // cool
            }

            @Override
            public void onFailure(DCCReceiveException aV) {
                // aww
            }
        });

        assertEquals(1, manager.activeDCCReceivesCount());
    }

}

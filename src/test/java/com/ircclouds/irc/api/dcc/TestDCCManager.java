package com.ircclouds.irc.api.dcc;

import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.interfaces.Callback;
import com.ircclouds.irc.api.interfaces.IIRCApi;

import net.engio.mbassy.bus.common.DeadMessage;
import net.engio.mbassy.listener.Enveloped;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.subscription.MessageEnvelope;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import mockit.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestDCCManager {

    private static final Logger LOG = LoggerFactory.getLogger(TestDCCManager.class);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public File saveResource(File output, String name) throws IOException {
        // Step 1:
        InputStream resource = this.getClass().getResourceAsStream(name);
        if (resource == null)
            throw new FileNotFoundException(name + " (resource not found)");
        // Step 2 and automatic step 4
        try(InputStream in = resource;
            OutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(output))) {
            // Step 3
            byte[] buffer = new byte[1024 * 4];
            int length;
            while((length = in.read(buffer)) >= 0) {
                writer.write(buffer, 0, length);
            }
        }
        return output;
    }


    public IIRCApi getApiWithExpectedResponses(final String expectedTarget, final String expectedMessage, final CountDownLatch lock) {
        IIRCApi _api = new IRCApi(false) {
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

        _api.register(new Object() {
            @Handler
            public void showDeadMessage(DeadMessage dm) {
                LOG.warn("DEAD MESSAGE! -> " + dm.getClass());
            }

            @Handler
            public void handleReceiverState(DCCReceiver.Event event) {
                LOG.info("receiver event! -> " + event.getClass());
                LOG.info("time taken: " + event.getTimeTaken());
            }

            @Handler
            public void handleSenderState(DCCSender.Event event) {
                LOG.info("sender event! -> " + event.getClass());
                LOG.info("time taken: " + event.getTimeTaken());
            }

            @Handler
            public void handleReceiverProgress(DCCReceiver.Progress event) {
                LOG.info("receiver progress -> " + event.getDeltaBytes());
            }

            @Handler
            public void handleSenderProgress(DCCSender.Progress event) {
                LOG.info("Sender progress -> " + event.getDeltaBytes());
            }

            @Handler
            @Enveloped(messages = {DCCReceiver.Success.class, DCCReceiver.Failure.class, DCCSender.Success.class, DCCSender.Failure.class})
            public void handleDone(MessageEnvelope event) {
                lock.countDown();
            }
        });

        return _api;
    }

    @Test
    public void testDCCAccept() throws IOException, InterruptedException {
        String nick = "ExpectedNick";
        String response = "\001DCC ACCEPT cool_file.doc 1337 0\001";

        File tmpFile = folder.newFile("cool_file.doc");

        CountDownLatch lock = new CountDownLatch(1);

        DCCManager manager = new DCCManager(getApiWithExpectedResponses(nick, response, lock));

        manager.acceptFile(nick, tmpFile, 1337, 0L, 5);

        assertEquals(1, manager.activeDCCSendsCount());

        lock.await(1, TimeUnit.SECONDS);

        assertEquals(0, manager.activeDCCSendsCount());
    }

    @Test
    public void testDCCSend() throws IOException, InterruptedException {
        File tmpFile = folder.newFile("cool_file.doc");

        String nick = "ExpectedNick";
        String response = "\001DCC SEND cool_file.doc localhost 1337 " + tmpFile.length() + "\001";

        CountDownLatch lock = new CountDownLatch(1);

        DCCManager manager = new DCCManager(getApiWithExpectedResponses(nick, response, lock));

        manager.sendFile(nick, tmpFile, "localhost", 1337, 5);

        assertEquals(1, manager.activeDCCSendsCount());

        lock.await(1, TimeUnit.SECONDS);

        assertEquals(0, manager.activeDCCSendsCount());
    }

    @Test
    public void testDCCReceive() throws IOException, InterruptedException {
        File tmpFile = folder.newFile("cool_file.doc");
        File tmpFile1 = folder.newFile("cool_file1.doc");

        saveResource(tmpFile, "test_file.txt");

        LOG.info("LENGTH: " + tmpFile.length());

        String nick = "ExpectedNick";
        String response = "\001DCC SEND cool_file.doc localhost 1337 " + tmpFile.length() + "\001";

        CountDownLatch lock = new CountDownLatch(1);

        DCCManager manager = new DCCManager(getApiWithExpectedResponses(nick, response, lock));

        // Start sender on localhost
        manager.sendFile(nick, tmpFile, "localhost", 1337, 5000);

        // Start download from above sender
        manager.resumeFile(tmpFile1, 0L, tmpFile.length(), new InetSocketAddress("127.0.0.1", 1337));

        assertEquals(1, manager.activeDCCReceivesCount());

        lock.await(1, TimeUnit.SECONDS);

        assertEquals(0, manager.activeDCCReceivesCount());
    }

}

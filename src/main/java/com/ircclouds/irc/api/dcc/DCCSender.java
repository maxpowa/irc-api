package com.ircclouds.irc.api.dcc;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class DCCSender implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DCCSender.class);
    private static final int READ_BUFFER_SIZE = 1024;
    private final File file;

    private Integer timeout;
    private Integer listeningPort;
    private Long resumePos;

    private final MBassador eventBus;
    private long totalBytesTransferred;
    private int totalAcksRead;

    private Exception readerExc;
    private Exception writerExc;
    private long timeTaken;
    private long timeBefore;

    public DCCSender(File aFile, Integer aPort, Integer aTimeout, MBassador aEventBus) {
        this(aFile, aTimeout, aPort, 0L, aEventBus);
    }

    public DCCSender(File aFile, int aTimeout, Integer aPort, Long aResumePosition, MBassador aEventBus) {
        timeout = aTimeout;
        listeningPort = aPort;
        resumePos = aResumePosition;
        eventBus = aEventBus;
        file = aFile;
    }

    public void setResumePosition(Long aResumePosition) {
        resumePos = aResumePosition;
    }

    @Override
    public void run() {
        ServerSocketChannel _ssc = null;
        SocketChannel _sc = null;

        timeBefore = System.currentTimeMillis();

        try {
            _ssc = DCCManager.getServerSocketChannel(listeningPort);

            Selector _selector = Selector.open();
            _ssc.register(_selector, SelectionKey.OP_ACCEPT);

            if (_selector.select(timeout) > 0 && _selector.selectedKeys().iterator().next().isAcceptable()) {
                _sc = _ssc.accept();

                Thread _ar = getACKsReader(_sc);
                _ar.start();

                if (_sc != null) {
                    writeFileToChannel(file, _sc);
                }

                _ar.join();
            }
        } catch (Exception aExc) {
            LOG.error("Error Transmitting File", aExc);

            writerExc = aExc;
        } finally {
            if (_ssc != null)
                close(_ssc);
            if (_sc != null)
                close(_sc);

            timeTaken = System.currentTimeMillis() - timeBefore;
            if (writerExc == null && readerExc == null && totalBytesTransferred == file.length()) {
                eventBus.post(new Success()).asynchronously();
            } else {
                eventBus.post(new Failure(readerExc, writerExc)).asynchronously();
            }
        }
    }

    public void send() {
        new Thread(this, "DCCSender").start();
    }

    private int getPort() {
        return listeningPort;
    }

    private File getFile() {
        return file;
    }

    private long getTotalSentBytes() {
        return totalBytesTransferred;
    }

    private int getTotalAcks() {
        return totalAcksRead;
    }

    private long getTimeTaken() {
        return timeTaken != 0L ? timeTaken : System.currentTimeMillis() - timeBefore;
    }

    private void writeFileToChannel(File aFile, SocketChannel aSocketChannel) throws IOException {
        FileInputStream _fis = new FileInputStream(aFile);
        FileChannel _fc = _fis.getChannel();

        long _size = aFile.length();
        long _position = resumePos;

        while (_position < _size) {
            _position += _fc.transferTo(_position, _size - _position, aSocketChannel);
        }

        if (_fis != null)
            close(_fis);
        if (_fc != null)
            close(_fc);
    }

    private Thread getACKsReader(final SocketChannel aSocketChannel) {
        return new Thread(new Runnable() {
            public void run() {
                ByteBuffer _bb = ByteBuffer.allocate(READ_BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);

                boolean _hasReadData = false;
                boolean _cleared = false;
                try {

                    int _readCount = 0;
                    totalAcksRead = 0;

                    while ((_readCount = aSocketChannel.read(_bb)) > 0) {
                        totalAcksRead += _readCount / 4;

                        _bb.position(_bb.position() - _readCount);
                        for (int _i = 0; _i < _readCount / 4; _i++) {
                            eventBus.post(new Progress(_bb.getInt())).asynchronously();
                        }

                        _hasReadData = true;
                        _cleared = false;

                        if (!_bb.hasRemaining()) {
                            _bb.clear();
                            _cleared = true;
                        }
                    }

                    if (_hasReadData) {
                        if (!_cleared) {
                            _bb.flip();
                            if (_bb.limit() >= 4)
                                _bb.position(_bb.limit() - 4);
                        } else {
                            _bb.position(READ_BUFFER_SIZE - 4);
                        }

                        totalBytesTransferred = _bb.getInt();
                    }
                } catch (IOException aExc) {
                    LOG.error("Error Reading Acks", aExc);
                    readerExc = aExc;
                }
            }

        }, "DCCACKsReader");
    }

    private void close(Closeable aCloseable) {
        try {
            aCloseable.close();
        } catch (IOException aExc) {
            LOG.error("", aExc);
        }
    }

    public class Event {

        public File getFile() {
            return DCCSender.this.getFile();
        }

        public long getTimeTaken() {
            return DCCSender.this.getTimeTaken();
        }

        public long getTotalSentBytes() {
            return DCCSender.this.getTotalSentBytes();
        }

        public long getTotalAcks() {
            return DCCSender.this.getTotalAcks();
        }

        public int getPort() {
            return DCCSender.this.getPort();
        }

        public DCCSender getSender() {
            return DCCSender.this;
        }
    }

    public class Success extends Event {

        public Success() {}

    }

    public class Failure extends Event {

        private final Exception readerExc;
        private final Exception writerExc;

        public Failure(Exception aReaderExc, Exception aWriterExc) {
            readerExc = aReaderExc;
            writerExc = aWriterExc;
        }

        public Exception getReaderException() {
            return readerExc;
        }

        public Exception getWriterException() {
            return writerExc;
        }

    }

    public class Progress extends Event {

        private final int deltaBytes;

        public Progress(int aCount) {
            deltaBytes = aCount;
        }

        public int getDeltaBytes() {
            return deltaBytes;
        }

    }
}
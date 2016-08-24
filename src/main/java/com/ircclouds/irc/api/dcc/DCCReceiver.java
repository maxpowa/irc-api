package com.ircclouds.irc.api.dcc;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class DCCReceiver implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(DCCReceiver.class);

	private final ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

	private final MBassador eventBus;

	private final Proxy proxy;
	private final SocketAddress address;
	private final Long resumePos;
	private final File file;

	private long totalBytesReceived;
	private long timeTaken = 0L;
	private long timeBefore = 0L;

	private long expectedSize = 0L;

	private IOException exc;

	public DCCReceiver(File aFile, long aResumePos, long aSize, SocketAddress aAddress, MBassador aEventBus, Proxy aProxy) {
		file = aFile;
		resumePos = aResumePos;
		expectedSize = aSize;
		address = aAddress;
		eventBus = aEventBus;
		proxy = aProxy;
	}

	@Override
	public void run() {
		SocketChannel _sc = null;
		FileChannel _fc = null;
		FileOutputStream _fos = null;

		timeBefore = System.currentTimeMillis();
		totalBytesReceived = 0L;

		try {
			_sc = DCCManager.getSocketChannel(address, proxy);
			_fos = new FileOutputStream(file);
			_fc = _fos.getChannel();

			long _read = resumePos;
			while (_read < expectedSize) {
				_read += _fc.transferFrom(_sc, resumePos + _read, expectedSize);
				writeTotalBytesReceived(_sc, (int) _read);
			}
		} catch (IOException aExc) {
			exc = aExc;
			LOG.error("", aExc);
		} finally {
			if (_sc != null)
				close(_sc);
			if (_fos != null)
				close(_fos);
			if (_fc != null)
				close(_fc);

			timeTaken = System.currentTimeMillis() - timeBefore;
			if (exc == null && totalBytesReceived == expectedSize) {
				eventBus.post(new Success()).asynchronously();
			} else {
				eventBus.post(new Failure(exc)).asynchronously();
			}
		}
	}

	public void receive() {
		new Thread(this, "DCCReceiver").start();
	}

	private long getTimeTaken() {
		return timeTaken != 0L ? timeTaken : System.currentTimeMillis() - timeBefore;
	}

	private long getExpectedSize() {
		return expectedSize;
	}

	private long getTotalBytes() {
		return totalBytesReceived;
	}

	private void writeTotalBytesReceived(SocketChannel aSocketChannel, int aCount) throws IOException {
		bb.clear();
		bb.putInt(aCount);

		aSocketChannel.write(bb);

		totalBytesReceived += aCount;
		eventBus.post(new Progress(aCount)).asynchronously();
	}

	private void close(Closeable aCloseable) {
		try {
			aCloseable.close();
		} catch (IOException aExc) {
			LOG.error("", aExc);
		}
	}

	public class Event {

		public long getExpectedSize() {
			return DCCReceiver.this.getExpectedSize();
		}

		public long getTimeTaken() {
			return DCCReceiver.this.getTimeTaken();
		}

		public long getTotalBytes() {
			return DCCReceiver.this.getTotalBytes();
		}

		public DCCReceiver getReceiver() {
			return DCCReceiver.this;
		}

	}

	public class Success extends Event {

		public Success() {}

	}

	public class Failure extends Event {

		private final IOException exception;

		public Failure(IOException aExc) {
			exception = aExc;
		}

		public IOException getException() {
			return exception;
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

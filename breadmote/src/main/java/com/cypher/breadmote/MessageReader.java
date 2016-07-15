package com.cypher.breadmote;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by cypher1 on 1/23/16.
 */
class MessageReader implements Runnable {

    private static final int HEADER_WAIT = 100;
    private final InputStream inputStream;
    private final Connection connection;
    private final Manager manager;
    private MessageHeader messageHeader;
    private boolean firstMessage;

    MessageReader(InputStream inputStream, Connection connection, Manager manager) {
        this.inputStream = inputStream;
        this.connection = connection;
        this.manager = manager;
        resetMessageHeader();
    }

    private void resetMessageHeader() {
        this.messageHeader = MessageHeader.DEFAULT_HEADER;
        this.firstMessage = true;
    }

    public void setMessageHeader(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }

    @Override
    public void run() {
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        while (true) {
            try {
                if (messageHeader == null) {
                    //wait for updated header
                    try {
                        Thread.sleep(HEADER_WAIT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    readMessage(mainThreadHandler);
                    if (firstMessage) {
                        firstMessage = false;
                        messageHeader = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        manager.disconnect();
                    }
                });
                break;
            }
        }
    }

    private byte readByte() throws IOException {
        return (byte) inputStream.read();
    }

    private void readMessage(Handler mainThreadHandler) throws IOException {
        final byte type = readByte();
        int length = readLength();
        final byte[] value = readValue(length);

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                connection.onMessageRecieved(type, value);
            }
        });
    }

    private int readLength() throws IOException {
        if (messageHeader.getSizeOfInt() == 4) {
            return readInt();
        } else {
            return readShort();
        }
    }

    private int readInt() throws IOException {
        byte[] bytes = new byte[]{readByte(), readByte(), readByte(), readByte()};
        return ByteBuffer.wrap(bytes).order(messageHeader.getByteOrder()).getInt();
    }

    private short readShort() throws IOException {
        byte[] bytes = new byte[]{readByte(), readByte()};
        return ByteBuffer.wrap(bytes).order(messageHeader.getByteOrder()).getShort();
    }

    private byte[] readValue(int length) throws IOException {
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            bytes[i] = readByte();
        }

        return bytes;
    }
}
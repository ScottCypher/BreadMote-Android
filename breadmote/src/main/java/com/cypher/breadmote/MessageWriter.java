package com.cypher.breadmote;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cypher1 on 1/23/16.
 */
class MessageWriter implements Runnable {
    private final BlockingQueue<byte[]> componentBlockingQueue;
    private final OutputStream outputStream;
    private final Manager manager;
    private MessageHeader messageHeader;

    MessageWriter(OutputStream outputStream, Manager manager) {
        this.componentBlockingQueue = new LinkedBlockingQueue<>();
        this.outputStream = outputStream;
        this.messageHeader = MessageHeader.DEFAULT_HEADER;
        this.manager = manager;
    }

    public void setMessageHeader(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }

    public void putComponent(TLVCompatible tlvCompatible) throws InterruptedException {
        byte[] writeBytes = tlvCompatible.getTLV(messageHeader);
        componentBlockingQueue.put(writeBytes);
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] bytes = componentBlockingQueue.take();
                outputStream.write(bytes);
            } catch (InterruptedException | IOException e) {
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
}

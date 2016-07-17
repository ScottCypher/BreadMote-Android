package com.cypher.breadmote;

import java.nio.ByteBuffer;

/**
 * Created by scypher on 7/6/16.
 */
class ByteConverter {
    private ByteConverter() {

    }

    //TODO writing a message thats too big (e.g. writing something that should be an int to a short device)
    public static byte[] toBytes(int x, MessageHeader messageHeader) {
        if (messageHeader.getSizeOfInt() == 2) {
            return toBytes((short) x, messageHeader);
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(4).order(messageHeader.getByteOrder());
            buffer.putInt(x);
            return buffer.array();
        }
    }

    public static byte[] toBytes(short s, MessageHeader messageHeader) {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(messageHeader.getByteOrder());
        buffer.putShort(s);
        return buffer.array();
    }

    public static byte[] toBytes(boolean b) {
        return new byte[]{(byte) (b ? 1 : 0)};
    }

    public static byte[] toBytes(String s) {
        String terminatingStr = s + "\0";
        return terminatingStr.getBytes();
    }

    public static byte[] toBytes(byte[]... allBytes) {
        int size = 0;
        for (byte[] bytes : allBytes) {
            size += bytes.length;
        }

        byte[] endBytes = new byte[size];
        int index = 0;
        for (byte[] bytes : allBytes) {
            for (byte b : bytes) {
                endBytes[index++] = b;
            }
        }
        return endBytes;
    }
}

package com.cypher.breadmote;

import java.nio.ByteOrder;

/**
 * Created by cypher1 on 1/27/16.
 */
class MessageHeader {
    public static final MessageHeader DEFAULT_HEADER = new MessageHeader(new byte[]{0, 0, 2, 2});
    private static final int INDEX_PROTOCOL = 0, INDEX_ENDIAN = 1, INDEX_INT_SIZE = 2, INDEX_FUNC_SIZE = 3;
    private final byte protocolVersion;
    private final boolean isBigEndian;
    private final byte sizeOfInt;
    private final int sizeOfFunc;

    public MessageHeader(byte[] header) {
        this.protocolVersion = header[INDEX_PROTOCOL];
        this.isBigEndian = header[INDEX_ENDIAN] > 0;
        this.sizeOfInt = header[INDEX_INT_SIZE];
        this.sizeOfFunc = header[INDEX_FUNC_SIZE];
    }

    public ByteOrder getByteOrder() {
        return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public byte getSizeOfInt() {
        return sizeOfInt;
    }

    public int getSizeOfFunc() {
        return sizeOfFunc;
    }

    public boolean isBigEndian() {
        return isBigEndian;
    }

    public int getVersion() {
        return protocolVersion;
    }
}

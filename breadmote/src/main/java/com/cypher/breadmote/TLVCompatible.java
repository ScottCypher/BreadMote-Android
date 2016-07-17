package com.cypher.breadmote;

/**
 * Created by scypher on 7/13/16.
 */
abstract class TLVCompatible {
    private final byte type;

    public TLVCompatible(byte type) {
        this.type = type;
    }

    protected abstract byte[] getValue(MessageHeader messageHeader);

    byte[] getTLV(MessageHeader messageHeader) {
        byte[] type = new byte[]{this.type};
        byte[] valueBytes = getValue(messageHeader);
        byte[] lengthBytes = ByteConverter.toBytes((short) (valueBytes.length), messageHeader);
        return ByteConverter.toBytes(type, lengthBytes, valueBytes);
    }
}

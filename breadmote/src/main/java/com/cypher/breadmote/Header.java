package com.cypher.breadmote;

/**
 * Created by scypher on 4/19/16.
 */
class Header extends TLVCompatible {
    Header() {
        super(Connection.TYPE_HEADER);
    }

    @Override
    protected byte[] getValue(MessageHeader messageHeader) {
        return ByteConverter.toBytes((short) 0, messageHeader);
    }

}

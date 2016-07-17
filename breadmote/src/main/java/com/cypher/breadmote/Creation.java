package com.cypher.breadmote;

/**
 * Created by cypher1 on 1/30/16.
 */
class Creation extends TLVCompatible {
    private final boolean isCreating;

    Creation(boolean isCreating) {
        super(Connection.TYPE_CREATE);
        this.isCreating = isCreating;
    }

    boolean isCreating() {
        return isCreating;
    }

    @Override
    protected byte[] getValue(MessageHeader messageHeader) {
        return new byte[]{(byte) (isCreating ? 1 : 0)};
    }

}

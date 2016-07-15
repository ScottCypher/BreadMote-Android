package com.cypher.breadmote;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by scypher on 2/21/16.
 */
interface IOConnectionListener {
    void onConnect(InputStream inputStream, OutputStream outputStream);

    void onDisconnect();
}

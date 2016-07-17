package com.cypher.breadmote_example.connect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


/**
 * Created by scypher on 4/17/16.
 */
class WiFiConnectionDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Connection.db";

    WiFiConnectionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConnectionContract.SQL_CREATE_WIFI_CONNECTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ConnectionContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    void addConnectionInfo(String mac, String host, int port, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = ConnectionContract.WifiConnectionEntry.getContentValues(mac, host, port, password);
        db.insertWithOnConflict(ConnectionContract.WifiConnectionEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.releaseReference();
    }

    @Nullable
    SavedConnectionInfo getConnectionInfo(String deviceAddress) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {ConnectionContract.WifiConnectionEntry.COLUMN_NAME_HOST,
                ConnectionContract.WifiConnectionEntry.COLUMN_NAME_PORT,
                ConnectionContract.WifiConnectionEntry.COLUMN_NAME_PASSWORD};

        Cursor c = db.query(ConnectionContract.WifiConnectionEntry.TABLE_NAME,
                projection,
                ConnectionContract.WifiConnectionEntry.COLUMN_NAME_MAC + "=?",
                new String[]{deviceAddress},
                null,
                null,
                null);

        SavedConnectionInfo wifiConnectionInfo;
        if (c.getCount() > 0) {
            c.moveToFirst();
            String host = c.getString(c.getColumnIndex(ConnectionContract.WifiConnectionEntry.COLUMN_NAME_HOST));
            int port = c.getInt(c.getColumnIndex(ConnectionContract.WifiConnectionEntry.COLUMN_NAME_PORT));
            String password = c.getString(c.getColumnIndex(ConnectionContract.WifiConnectionEntry.COLUMN_NAME_PASSWORD));
            wifiConnectionInfo = new SavedConnectionInfo(deviceAddress, host, port, password);
        } else {
            wifiConnectionInfo = null;
        }
        c.close();

        db.releaseReference();

        return wifiConnectionInfo;
    }

    static class SavedConnectionInfo {
        final String deviceAddress;
        final String host;
        final String password;
        final int port;

        public SavedConnectionInfo(String deviceAddress, String host, int port, String password) {
            this.deviceAddress = deviceAddress;
            this.host = host;
            this.password = password;
            this.port = port;
        }

        public String getDeviceAddress() {
            return deviceAddress;
        }

        public String getHost() {
            return host;
        }

        public String getPassword() {
            return password;
        }

        public int getPort() {
            return port;
        }
    }

    /**
     * Created by scypher on 4/17/16.
     */
    private static final class ConnectionContract {
        static final String SQL_CREATE_WIFI_CONNECTIONS =
                "CREATE TABLE " + WifiConnectionEntry.TABLE_NAME + " (" +
                        WifiConnectionEntry.COLUMN_NAME_MAC + " TEXT PRIMARY KEY, " +
                        WifiConnectionEntry.COLUMN_NAME_HOST + " TEXT, " +
                        WifiConnectionEntry.COLUMN_NAME_PORT + " INT, " +
                        WifiConnectionEntry.COLUMN_NAME_PASSWORD + " TEXT)";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + WifiConnectionEntry.TABLE_NAME;

        static abstract class WifiConnectionEntry {
            static final String TABLE_NAME = "wifi_connection_info";
            static final String COLUMN_NAME_MAC = "mac";
            static final String COLUMN_NAME_HOST = "host";
            static final String COLUMN_NAME_PORT = "port";
            static final String COLUMN_NAME_PASSWORD = "password";

            static ContentValues getContentValues(String mac, String host, int port, String password) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_MAC, mac);
                values.put(COLUMN_NAME_HOST, host);
                values.put(COLUMN_NAME_PORT, port);
                values.put(COLUMN_NAME_PASSWORD, password);
                return values;
            }
        }
    }
}

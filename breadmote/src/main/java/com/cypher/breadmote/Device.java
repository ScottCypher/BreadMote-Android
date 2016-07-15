package com.cypher.breadmote;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a connectable device returned during a scan. A device is not necessarily BreadMote
 * compatible.
 *
 * @see BreadMote#scan(ConnectionType)
 * @see ScanListener
 */
public class Device implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
    private final String address;
    private final String name;

    Device(String address, String name) {
        this.address = address;
        this.name = name;
    }

    private Device(Parcel in) {
        address = in.readString();
        name = in.readString();
    }

    /**
     *
     * @return The unique address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
    }
}

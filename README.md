This is the SDK / example app corresponding to the BreadMote hardware library. The SDK and library are designed to simplify communication between your hardware and Android device. Be sure to checkout http://www.breadmote.com for more information about this project.

* [API](http://scottcypher.github.io/BreadMote/android/index.html)
* [Hardware library](https://www.breadmote.com)

## Install BreadMote

The Android SDK is published to JCenter. To install the library, add the following dependency to your `build.gradle` file:

    compile 'com.cypher.breadmote:breadmote:0.1.3'

##Required Permissions

Depending on how you plan to use BreadMote, different permissions are needed.

#### Bluetooth

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission-sdk-23 android:name="android.permission.ACCESS_COARSE_LOCATION" />

#### WiFi
If you plan to connect over Wi-Fi and need to allow the user to change networks from in app (such as connecting directly to an access point), the following permissions are needed: 

	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission-sdk-23 android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
If you plan to connect over an internet connection only, you will simply need:

    <uses-permission android:name="android.permission.INTERNET" />

*Note: ACCESS_COARSE_LOCATION is needed for devices running Android 6+ [in order to get details about nearby devices](https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id)*


## Setup BreadMote

The BreadMote library should initialized once with a Context. This can be done from the application object:

    @Override
    public void onCreate() {
        //...
        BreadMote.initialize(this);
    }


## Scanning for devices

Scanning gives you a list of nearby devices. To scan, call 

	BreadMote.startScan(ConnectionType connectiontype);

Scans will automatically complete on their own. Scan results and the current scan state can observed by adding a scan listener:

	BreadMote.addListener(ScanListener scanListener);


## Connecting to a device

Different connection types require different method calls for connecting.

#### Bluetooth

	BreadMote.connectBluetooth(Device device); //Devices are returned during scans

#### WiFi

To connect to a device using the current network:

	BreadMote.connectWiFi(String host, int port);

To connect to a device on a different network (e.g. if your target device is an access point):

	BreadMote.connectWiFi(Device device, String password, String host int port);

#### Observing connections
To observe device disconnects and connects, add a listener with

	BreadMote.addListener(ConnectionListener connectionListener);

The Connection object returned in the ConnectionListener callback will be needed to interact with the device.

## Interacting with a device

Once a BreadMote supported device has been connected to, you can get its list of components/controls by adding a listener with:

	connection.addListener(ComponentListener componentListener);

Components can be modified by calling `component.setValue`. Different components require different arguments. For example, to edit a `CheckBoxComponent`, you could call

	checkBoxComponent.setValue(false); //This would represent a user setting the checkbox to off. 

Once you have changed the value of a Component, you can notify the connected device of an update by calling

	connection.sendComponentChange(Component component);


## Errors

A BreadMote device may cause errors based on user defined behavior or when performing an incorrect action (e.g. enabling a non-existent component). To observe these errors, add a listener with

	connection.addListener(ErrorListener errorListener)

The error log can be edited by calling

	connection.clearErrors();
	//or
	connection.removeError(int index);


## Cleanup

Bluetooth and WiFi operations can be expensive. Care should be taken to cleanup resources when possible.  When you no longer wish to be connected to a specific device, call

	connection.disconnect();

When you are completely done scanning, connecting, or interacting, call

	BreadMote.terminate();


## Limitations

This library is still a work in progress. Feel free to add a pull request if you think you know of a good fix!

1. **Your hardware can go silent if your main loop or baud rate is too slow**. This is due to buffer overflow in your hardware's radio module. This problem is more evident in Bluetooth based connections. A solution to this could involve the app throttling messages based on the baud rate and/or confirmation messages sent from the hardware when a message is received.
2. **Only basic components are supported**. Video, picture, or audio streaming and additional components would enhance this library greatly!
3. **You can only connect to one device at a time**. Being able to connect to multiple devices through Wi-Fi and Bluetooth may be beneficial for some use cases. If you plan to tackle this issue, be sure to consider your Android device's connection limitations, especially with Bluetooth!

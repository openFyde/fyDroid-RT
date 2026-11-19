package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface BluetoothConnectSocketClient extends Interface {
    public static final Interface.Manager<BluetoothConnectSocketClient, Proxy> MANAGER = BluetoothConnectSocketClient_Internal.MANAGER;

    public interface Proxy extends BluetoothConnectSocketClient, Interface.Proxy {
    }

    void onConnectFailed();

    void onConnected(BluetoothSocketConnection bluetoothSocketConnection);
}

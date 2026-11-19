package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface BluetoothListenSocketClient extends Interface {
    public static final Interface.Manager<BluetoothListenSocketClient, Proxy> MANAGER = BluetoothListenSocketClient_Internal.MANAGER;

    public interface Proxy extends BluetoothListenSocketClient, Interface.Proxy {
    }

    void onAccepted(BluetoothSocketConnection bluetoothSocketConnection);
}

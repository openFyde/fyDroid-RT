package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface RfcommConnectingSocketClient extends Interface {
    public static final Interface.Manager<RfcommConnectingSocketClient, Proxy> MANAGER = RfcommConnectingSocketClient_Internal.MANAGER;

    public interface Proxy extends RfcommConnectingSocketClient, Interface.Proxy {
    }

    void onConnectFailed();

    void onConnected(BluetoothRfcommConnection bluetoothRfcommConnection);
}

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface RfcommListeningSocketClient extends Interface {
    public static final Interface.Manager<RfcommListeningSocketClient, Proxy> MANAGER = RfcommListeningSocketClient_Internal.MANAGER;

    public interface Proxy extends RfcommListeningSocketClient, Interface.Proxy {
    }

    void onAccepted(BluetoothRfcommConnection bluetoothRfcommConnection);
}

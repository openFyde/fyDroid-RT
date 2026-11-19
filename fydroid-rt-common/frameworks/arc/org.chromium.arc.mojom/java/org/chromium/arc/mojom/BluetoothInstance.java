package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface BluetoothInstance extends Interface {
    public static final Interface.Manager<BluetoothInstance, Proxy> MANAGER = BluetoothInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends BluetoothInstance, Interface.Proxy {
    }

    public interface RequestGattExecuteWriteResponse extends Callbacks.Callback1<Integer> {
    }

    public interface RequestGattReadResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface RequestGattWriteResponse extends Callbacks.Callback1<Integer> {
    }

    void init(BluetoothHost bluetoothHost, InitResponse initResponse);

    void onAdapterProperties(int i, BluetoothProperty[] bluetoothPropertyArr);

    void onBondStateChanged(int i, BluetoothAddress bluetoothAddress, int i2);

    void onConnectionStateChanged(BluetoothAddress bluetoothAddress, int i, boolean z);

    void onDeviceFound(BluetoothProperty[] bluetoothPropertyArr);

    void onDevicePropertiesChanged(BluetoothAddress bluetoothAddress, BluetoothProperty[] bluetoothPropertyArr);

    void onDiscoveryStateChanged(int i);

    void onGattNotify(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, boolean z, byte[] bArr);

    void onGetGattDb(BluetoothAddress bluetoothAddress, BluetoothGattDbElement[] bluetoothGattDbElementArr);

    void onGetSdpRecords(int i, BluetoothAddress bluetoothAddress, BluetoothUuid bluetoothUuid, BluetoothSdpRecord[] bluetoothSdpRecordArr);

    void onLeConnectionStateChange(BluetoothAddress bluetoothAddress, boolean z);

    void onLeDeviceAddressChange(BluetoothAddress bluetoothAddress, BluetoothAddress bluetoothAddress2);

    void onLeDeviceFound(BluetoothAddress bluetoothAddress, int i, byte[] bArr);

    void onMtuReceived(BluetoothAddress bluetoothAddress, short s);

    void onSearchComplete(BluetoothAddress bluetoothAddress, int i);

    void requestGattExecuteWrite(BluetoothAddress bluetoothAddress, boolean z, RequestGattExecuteWriteResponse requestGattExecuteWriteResponse);

    void requestGattRead(BluetoothAddress bluetoothAddress, int i, int i2, boolean z, int i3, RequestGattReadResponse requestGattReadResponse);

    void requestGattWrite(BluetoothAddress bluetoothAddress, int i, int i2, byte[] bArr, int i3, boolean z, RequestGattWriteResponse requestGattWriteResponse);
}

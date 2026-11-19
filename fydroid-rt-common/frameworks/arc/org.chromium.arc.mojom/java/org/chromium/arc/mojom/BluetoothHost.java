package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface BluetoothHost extends Interface {
    public static final Interface.Manager<BluetoothHost, Proxy> MANAGER = BluetoothHost_Internal.MANAGER;

    public interface AddCharacteristicResponse extends Callbacks.Callback1<Integer> {
    }

    public interface AddDescriptorResponse extends Callbacks.Callback1<Integer> {
    }

    public interface AddServiceResponse extends Callbacks.Callback1<Integer> {
    }

    public interface BluetoothSocketConnectResponse extends Callbacks.Callback2<Integer, InterfaceRequest<BluetoothConnectSocketClient>> {
    }

    public interface BluetoothSocketListenResponse extends Callbacks.Callback3<Integer, Integer, InterfaceRequest<BluetoothListenSocketClient>> {
    }

    public interface CreateSdpRecordResponse extends Callbacks.Callback1<BluetoothCreateSdpRecordResult> {
    }

    public interface DeleteServiceResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DeregisterForGattNotificationResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DisableAdapterResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DisableAdvertisementResponse extends Callbacks.Callback1<Integer> {
    }

    public interface EnableAdapterResponse extends Callbacks.Callback1<Integer> {
    }

    public interface EnableAdvertisementResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ExecuteWriteResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetConnectionStateResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends BluetoothHost, Interface.Proxy {
    }

    public interface ReadGattCharacteristicResponse extends Callbacks.Callback1<BluetoothGattValue> {
    }

    public interface ReadGattDescriptorResponse extends Callbacks.Callback1<BluetoothGattValue> {
    }

    public interface ReadRemoteRssiResponse extends Callbacks.Callback1<Integer> {
    }

    public interface RegisterForGattNotificationResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ReleaseAdvertisementHandleResponse extends Callbacks.Callback1<Integer> {
    }

    public interface RemoveSdpRecordResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ReserveAdvertisementHandleResponse extends Callbacks.Callback2<Integer, Integer> {
    }

    public interface SendIndicationResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StartServiceResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StopServiceResponse extends Callbacks.Callback1<Integer> {
    }

    public interface WriteGattCharacteristicResponse extends Callbacks.Callback1<Integer> {
    }

    public interface WriteGattDescriptorResponse extends Callbacks.Callback1<Integer> {
    }

    void addCharacteristic(int i, BluetoothUuid bluetoothUuid, int i2, int i3, AddCharacteristicResponse addCharacteristicResponse);

    void addDescriptor(int i, BluetoothUuid bluetoothUuid, int i2, AddDescriptorResponse addDescriptorResponse);

    void addService(BluetoothGattServiceId bluetoothGattServiceId, int i, AddServiceResponse addServiceResponse);

    void bluetoothSocketConnect(int i, BluetoothSocketFlags bluetoothSocketFlags, BluetoothAddress bluetoothAddress, int i2, BluetoothSocketConnectResponse bluetoothSocketConnectResponse);

    void bluetoothSocketListen(int i, BluetoothSocketFlags bluetoothSocketFlags, int i2, BluetoothSocketListenResponse bluetoothSocketListenResponse);

    void cancelBond(BluetoothAddress bluetoothAddress);

    void cancelDiscovery();

    void connectLeDevice(BluetoothAddress bluetoothAddress);

    void createBond(BluetoothAddress bluetoothAddress, int i);

    void createSdpRecord(BluetoothSdpRecord bluetoothSdpRecord, CreateSdpRecordResponse createSdpRecordResponse);

    void deleteService(int i, DeleteServiceResponse deleteServiceResponse);

    void deregisterForGattNotification(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, DeregisterForGattNotificationResponse deregisterForGattNotificationResponse);

    void disableAdapter(DisableAdapterResponse disableAdapterResponse);

    void disableAdvertisement(int i, DisableAdvertisementResponse disableAdvertisementResponse);

    void disconnectLeDevice(BluetoothAddress bluetoothAddress);

    void enableAdapter(EnableAdapterResponse enableAdapterResponse);

    void enableAdvertisement(int i, BluetoothAdvertisement bluetoothAdvertisement, EnableAdvertisementResponse enableAdvertisementResponse);

    void executeWrite(BluetoothAddress bluetoothAddress, boolean z, ExecuteWriteResponse executeWriteResponse);

    void getAdapterProperty(int i);

    void getConnectionState(BluetoothAddress bluetoothAddress, GetConnectionStateResponse getConnectionStateResponse);

    void getGattDb(BluetoothAddress bluetoothAddress);

    void getSdpRecords(BluetoothAddress bluetoothAddress, BluetoothUuid bluetoothUuid);

    void readGattCharacteristic(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, ReadGattCharacteristicResponse readGattCharacteristicResponse);

    void readGattDescriptor(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothGattId bluetoothGattId2, ReadGattDescriptorResponse readGattDescriptorResponse);

    void readRemoteRssi(BluetoothAddress bluetoothAddress, ReadRemoteRssiResponse readRemoteRssiResponse);

    void registerForGattNotification(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, RegisterForGattNotificationResponse registerForGattNotificationResponse);

    void releaseAdvertisementHandle(int i, ReleaseAdvertisementHandleResponse releaseAdvertisementHandleResponse);

    void removeBond(BluetoothAddress bluetoothAddress);

    void removeSdpRecord(int i, RemoveSdpRecordResponse removeSdpRecordResponse);

    void reserveAdvertisementHandle(ReserveAdvertisementHandleResponse reserveAdvertisementHandleResponse);

    void searchService(BluetoothAddress bluetoothAddress);

    void sendIndication(int i, BluetoothAddress bluetoothAddress, boolean z, byte[] bArr, SendIndicationResponse sendIndicationResponse);

    void setAdapterProperty(BluetoothProperty bluetoothProperty);

    void startDiscovery();

    void startLeScan();

    void startService(int i, StartServiceResponse startServiceResponse);

    void stopLeScan();

    void stopService(int i, StopServiceResponse stopServiceResponse);

    void writeGattCharacteristic(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothGattValue bluetoothGattValue, boolean z, WriteGattCharacteristicResponse writeGattCharacteristicResponse);

    void writeGattDescriptor(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothGattId bluetoothGattId2, BluetoothGattValue bluetoothGattValue, WriteGattDescriptorResponse writeGattDescriptorResponse);
}

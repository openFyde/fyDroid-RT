package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class BluetoothProperty extends Union {
    private int mAdapterScanMode;
    private BluetoothAddress mBdaddr;
    private String mBdname;
    private BluetoothAddress[] mBondedDevices;
    private int mDeviceClass;
    private int mDeviceType;
    private int mDiscoveryTimeout;
    private BluetoothLocalLeFeatures mLocalLeFeatures;
    private String mRemoteFriendlyName;
    private int mRemoteRssi;
    private BluetoothRemoteVersion mRemoteVersion;
    private BluetoothServiceRecord mServiceRecord;
    private BluetoothUuid[] mUuids;

    public static final class Tag {
        public static final int AdapterScanMode = 6;
        public static final int Bdaddr = 1;
        public static final int Bdname = 0;
        public static final int BondedDevices = 7;
        public static final int DeviceClass = 3;
        public static final int DeviceType = 4;
        public static final int DiscoveryTimeout = 8;
        public static final int LocalLeFeatures = 12;
        public static final int RemoteFriendlyName = 9;
        public static final int RemoteRssi = 10;
        public static final int RemoteVersion = 11;
        public static final int ServiceRecord = 5;
        public static final int Uuids = 2;
    }

    public static final BluetoothProperty decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        BluetoothProperty bluetoothProperty = new BluetoothProperty();
        switch (dataHeaderForUnion.elementsOrVersion) {
            case 0:
                bluetoothProperty.mBdname = decoder.readString(i + 8, false);
                bluetoothProperty.mTag = 0;
                return bluetoothProperty;
            case 1:
                bluetoothProperty.mBdaddr = BluetoothAddress.decode(decoder.readPointer(i + 8, false));
                bluetoothProperty.mTag = 1;
                return bluetoothProperty;
            case 2:
                Decoder pointer = decoder.readPointer(i + 8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                bluetoothProperty.mUuids = new BluetoothUuid[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    bluetoothProperty.mUuids[i2] = BluetoothUuid.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                bluetoothProperty.mTag = 2;
                return bluetoothProperty;
            case 3:
                bluetoothProperty.mDeviceClass = decoder.readInt(i + 8);
                bluetoothProperty.mTag = 3;
                return bluetoothProperty;
            case 4:
                int i3 = decoder.readInt(i + 8);
                bluetoothProperty.mDeviceType = i3;
                BluetoothDeviceType.validate(i3);
                bluetoothProperty.mTag = 4;
                return bluetoothProperty;
            case 5:
                bluetoothProperty.mServiceRecord = BluetoothServiceRecord.decode(decoder.readPointer(i + 8, false));
                bluetoothProperty.mTag = 5;
                return bluetoothProperty;
            case 6:
                int i4 = decoder.readInt(i + 8);
                bluetoothProperty.mAdapterScanMode = i4;
                BluetoothScanMode.validate(i4);
                bluetoothProperty.mTag = 6;
                return bluetoothProperty;
            case 7:
                Decoder pointer2 = decoder.readPointer(i + 8, false);
                DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                bluetoothProperty.mBondedDevices = new BluetoothAddress[dataHeaderForPointerArray2.elementsOrVersion];
                for (int i5 = 0; i5 < dataHeaderForPointerArray2.elementsOrVersion; i5++) {
                    bluetoothProperty.mBondedDevices[i5] = BluetoothAddress.decode(GeneratedOutlineSupport.outline2(i5, 8, 8, pointer2, false));
                }
                bluetoothProperty.mTag = 7;
                return bluetoothProperty;
            case 8:
                bluetoothProperty.mDiscoveryTimeout = decoder.readInt(i + 8);
                bluetoothProperty.mTag = 8;
                return bluetoothProperty;
            case 9:
                bluetoothProperty.mRemoteFriendlyName = decoder.readString(i + 8, false);
                bluetoothProperty.mTag = 9;
                return bluetoothProperty;
            case 10:
                bluetoothProperty.mRemoteRssi = decoder.readInt(i + 8);
                bluetoothProperty.mTag = 10;
                return bluetoothProperty;
            case 11:
                bluetoothProperty.mRemoteVersion = BluetoothRemoteVersion.decode(decoder.readPointer(i + 8, false));
                bluetoothProperty.mTag = 11;
                return bluetoothProperty;
            case 12:
                bluetoothProperty.mLocalLeFeatures = BluetoothLocalLeFeatures.decode(decoder.readPointer(i + 8, false));
                bluetoothProperty.mTag = 12;
                return bluetoothProperty;
            default:
                return bluetoothProperty;
        }
    }

    public static BluetoothProperty deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        switch (this.mTag) {
            case 0:
                encoder.encode(this.mBdname, i + 8, false);
                break;
            case 1:
                encoder.encode((Struct) this.mBdaddr, i + 8, false);
                break;
            case 2:
                BluetoothUuid[] bluetoothUuidArr = this.mUuids;
                if (bluetoothUuidArr != null) {
                    Encoder encoderEncodePointerArray = encoder.encodePointerArray(bluetoothUuidArr.length, i + 8, -1);
                    int i2 = 0;
                    while (true) {
                        BluetoothUuid[] bluetoothUuidArr2 = this.mUuids;
                        if (i2 < bluetoothUuidArr2.length) {
                            encoderEncodePointerArray.encode((Struct) bluetoothUuidArr2[i2], (i2 * 8) + 8, false);
                            i2++;
                        }
                    }
                } else {
                    encoder.encodeNullPointer(i + 8, false);
                }
                break;
            case 3:
                encoder.encode(this.mDeviceClass, i + 8);
                break;
            case 4:
                encoder.encode(this.mDeviceType, i + 8);
                break;
            case 5:
                encoder.encode((Struct) this.mServiceRecord, i + 8, false);
                break;
            case 6:
                encoder.encode(this.mAdapterScanMode, i + 8);
                break;
            case 7:
                BluetoothAddress[] bluetoothAddressArr = this.mBondedDevices;
                if (bluetoothAddressArr != null) {
                    Encoder encoderEncodePointerArray2 = encoder.encodePointerArray(bluetoothAddressArr.length, i + 8, -1);
                    int i3 = 0;
                    while (true) {
                        BluetoothAddress[] bluetoothAddressArr2 = this.mBondedDevices;
                        if (i3 < bluetoothAddressArr2.length) {
                            encoderEncodePointerArray2.encode((Struct) bluetoothAddressArr2[i3], (i3 * 8) + 8, false);
                            i3++;
                        }
                    }
                } else {
                    encoder.encodeNullPointer(i + 8, false);
                }
                break;
            case 8:
                encoder.encode(this.mDiscoveryTimeout, i + 8);
                break;
            case 9:
                encoder.encode(this.mRemoteFriendlyName, i + 8, false);
                break;
            case 10:
                encoder.encode(this.mRemoteRssi, i + 8);
                break;
            case 11:
                encoder.encode((Struct) this.mRemoteVersion, i + 8, false);
                break;
            case 12:
                encoder.encode((Struct) this.mLocalLeFeatures, i + 8, false);
                break;
        }
    }

    public int getAdapterScanMode() {
        return this.mAdapterScanMode;
    }

    public BluetoothAddress getBdaddr() {
        return this.mBdaddr;
    }

    public String getBdname() {
        return this.mBdname;
    }

    public BluetoothAddress[] getBondedDevices() {
        return this.mBondedDevices;
    }

    public int getDeviceClass() {
        return this.mDeviceClass;
    }

    public int getDeviceType() {
        return this.mDeviceType;
    }

    public int getDiscoveryTimeout() {
        return this.mDiscoveryTimeout;
    }

    public BluetoothLocalLeFeatures getLocalLeFeatures() {
        return this.mLocalLeFeatures;
    }

    public String getRemoteFriendlyName() {
        return this.mRemoteFriendlyName;
    }

    public int getRemoteRssi() {
        return this.mRemoteRssi;
    }

    public BluetoothRemoteVersion getRemoteVersion() {
        return this.mRemoteVersion;
    }

    public BluetoothServiceRecord getServiceRecord() {
        return this.mServiceRecord;
    }

    public BluetoothUuid[] getUuids() {
        return this.mUuids;
    }

    public void setAdapterScanMode(int i) {
        this.mTag = 6;
        this.mAdapterScanMode = i;
    }

    public void setBdaddr(BluetoothAddress bluetoothAddress) {
        this.mTag = 1;
        this.mBdaddr = bluetoothAddress;
    }

    public void setBdname(String str) {
        this.mTag = 0;
        this.mBdname = str;
    }

    public void setBondedDevices(BluetoothAddress[] bluetoothAddressArr) {
        this.mTag = 7;
        this.mBondedDevices = bluetoothAddressArr;
    }

    public void setDeviceClass(int i) {
        this.mTag = 3;
        this.mDeviceClass = i;
    }

    public void setDeviceType(int i) {
        this.mTag = 4;
        this.mDeviceType = i;
    }

    public void setDiscoveryTimeout(int i) {
        this.mTag = 8;
        this.mDiscoveryTimeout = i;
    }

    public void setLocalLeFeatures(BluetoothLocalLeFeatures bluetoothLocalLeFeatures) {
        this.mTag = 12;
        this.mLocalLeFeatures = bluetoothLocalLeFeatures;
    }

    public void setRemoteFriendlyName(String str) {
        this.mTag = 9;
        this.mRemoteFriendlyName = str;
    }

    public void setRemoteRssi(int i) {
        this.mTag = 10;
        this.mRemoteRssi = i;
    }

    public void setRemoteVersion(BluetoothRemoteVersion bluetoothRemoteVersion) {
        this.mTag = 11;
        this.mRemoteVersion = bluetoothRemoteVersion;
    }

    public void setServiceRecord(BluetoothServiceRecord bluetoothServiceRecord) {
        this.mTag = 5;
        this.mServiceRecord = bluetoothServiceRecord;
    }

    public void setUuids(BluetoothUuid[] bluetoothUuidArr) {
        this.mTag = 2;
        this.mUuids = bluetoothUuidArr;
    }
}

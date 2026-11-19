// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class BluetoothAdvertisingData extends Union {
    private byte mFlags;
    private String mLocalName;
    private byte[] mManufacturerData;
    private byte[] mOtherData;
    private BluetoothServiceData mServiceData;
    private BluetoothUuid[] mServiceUuids;
    private short[] mServiceUuids16;
    private byte mTxPowerLevel;

    public static final class Tag {
        public static final int Flags = 0;
        public static final int LocalName = 3;
        public static final int ManufacturerData = 6;
        public static final int OtherData = 7;
        public static final int ServiceData = 5;
        public static final int ServiceUuids = 2;
        public static final int ServiceUuids16 = 1;
        public static final int TxPowerLevel = 4;
    }

    public static final BluetoothAdvertisingData decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        BluetoothAdvertisingData bluetoothAdvertisingData = new BluetoothAdvertisingData();
        switch (dataHeaderForUnion.elementsOrVersion) {
            case 0:
                bluetoothAdvertisingData.mFlags = decoder.readByte(i + 8);
                bluetoothAdvertisingData.mTag = 0;
                return bluetoothAdvertisingData;
            case 1:
                bluetoothAdvertisingData.mServiceUuids16 = decoder.readShorts(i + 8, 0, -1);
                bluetoothAdvertisingData.mTag = 1;
                return bluetoothAdvertisingData;
            case 2:
                Decoder pointer = decoder.readPointer(i + 8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                bluetoothAdvertisingData.mServiceUuids = new BluetoothUuid[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    bluetoothAdvertisingData.mServiceUuids[i2] = BluetoothUuid.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                bluetoothAdvertisingData.mTag = 2;
                return bluetoothAdvertisingData;
            case 3:
                bluetoothAdvertisingData.mLocalName = decoder.readString(i + 8, false);
                bluetoothAdvertisingData.mTag = 3;
                return bluetoothAdvertisingData;
            case 4:
                bluetoothAdvertisingData.mTxPowerLevel = decoder.readByte(i + 8);
                bluetoothAdvertisingData.mTag = 4;
                return bluetoothAdvertisingData;
            case 5:
                bluetoothAdvertisingData.mServiceData = BluetoothServiceData.decode(decoder.readPointer(i + 8, false));
                bluetoothAdvertisingData.mTag = 5;
                return bluetoothAdvertisingData;
            case 6:
                bluetoothAdvertisingData.mManufacturerData = decoder.readBytes(i + 8, 0, -1);
                bluetoothAdvertisingData.mTag = 6;
                return bluetoothAdvertisingData;
            case 7:
                bluetoothAdvertisingData.mOtherData = decoder.readBytes(i + 8, 0, -1);
                bluetoothAdvertisingData.mTag = 7;
                return bluetoothAdvertisingData;
            default:
                return bluetoothAdvertisingData;
        }
    }

    public static BluetoothAdvertisingData deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        switch (this.mTag) {
            case 0:
                encoder.encode(this.mFlags, i + 8);
                break;
            case 1:
                encoder.encode(this.mServiceUuids16, i + 8, 0, -1);
                break;
            case 2:
                BluetoothUuid[] bluetoothUuidArr = this.mServiceUuids;
                if (bluetoothUuidArr != null) {
                    Encoder encoderEncodePointerArray = encoder.encodePointerArray(bluetoothUuidArr.length, i + 8, -1);
                    int i2 = 0;
                    while (true) {
                        BluetoothUuid[] bluetoothUuidArr2 = this.mServiceUuids;
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
                encoder.encode(this.mLocalName, i + 8, false);
                break;
            case 4:
                encoder.encode(this.mTxPowerLevel, i + 8);
                break;
            case 5:
                encoder.encode((Struct) this.mServiceData, i + 8, false);
                break;
            case 6:
                encoder.encode(this.mManufacturerData, i + 8, 0, -1);
                break;
            case 7:
                encoder.encode(this.mOtherData, i + 8, 0, -1);
                break;
        }
    }

    public byte getFlags() {
        return this.mFlags;
    }

    public String getLocalName() {
        return this.mLocalName;
    }

    public byte[] getManufacturerData() {
        return this.mManufacturerData;
    }

    public byte[] getOtherData() {
        return this.mOtherData;
    }

    public BluetoothServiceData getServiceData() {
        return this.mServiceData;
    }

    public BluetoothUuid[] getServiceUuids() {
        return this.mServiceUuids;
    }

    public short[] getServiceUuids16() {
        return this.mServiceUuids16;
    }

    public byte getTxPowerLevel() {
        return this.mTxPowerLevel;
    }

    public void setFlags(byte b) {
        this.mTag = 0;
        this.mFlags = b;
    }

    public void setLocalName(String str) {
        this.mTag = 3;
        this.mLocalName = str;
    }

    public void setManufacturerData(byte[] bArr) {
        this.mTag = 6;
        this.mManufacturerData = bArr;
    }

    public void setOtherData(byte[] bArr) {
        this.mTag = 7;
        this.mOtherData = bArr;
    }

    public void setServiceData(BluetoothServiceData bluetoothServiceData) {
        this.mTag = 5;
        this.mServiceData = bluetoothServiceData;
    }

    public void setServiceUuids(BluetoothUuid[] bluetoothUuidArr) {
        this.mTag = 2;
        this.mServiceUuids = bluetoothUuidArr;
    }

    public void setServiceUuids16(short[] sArr) {
        this.mTag = 1;
        this.mServiceUuids16 = sArr;
    }

    public void setTxPowerLevel(byte b) {
        this.mTag = 4;
        this.mTxPowerLevel = b;
    }
}

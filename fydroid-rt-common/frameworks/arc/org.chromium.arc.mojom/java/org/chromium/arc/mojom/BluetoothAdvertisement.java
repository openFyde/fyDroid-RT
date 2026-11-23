// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class BluetoothAdvertisement extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public BluetoothAdvertisingData[] data;
    public boolean includeTxPower;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothAdvertisement() {
        super(24, 0);
    }

    public static BluetoothAdvertisement decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothAdvertisement bluetoothAdvertisement = new BluetoothAdvertisement(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            bluetoothAdvertisement.type = i;
            BluetoothAdvertisementType.validate(i);
            bluetoothAdvertisement.includeTxPower = decoder.readBoolean(12, 0);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            bluetoothAdvertisement.data = new BluetoothAdvertisingData[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                bluetoothAdvertisement.data[i2] = BluetoothAdvertisingData.decode(pointer, (i2 * 16) + 8);
            }
            return bluetoothAdvertisement;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothAdvertisement deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.type, 8);
        encoderAtDataOffset.encode(this.includeTxPower, 12, 0);
        BluetoothAdvertisingData[] bluetoothAdvertisingDataArr = this.data;
        if (bluetoothAdvertisingDataArr == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
            return;
        }
        Encoder encoderEncodeUnionArray = encoderAtDataOffset.encodeUnionArray(bluetoothAdvertisingDataArr.length, 16, -1);
        int i = 0;
        while (true) {
            BluetoothAdvertisingData[] bluetoothAdvertisingDataArr2 = this.data;
            if (i >= bluetoothAdvertisingDataArr2.length) {
                return;
            }
            encoderEncodeUnionArray.encode((Union) bluetoothAdvertisingDataArr2[i], (i * 16) + 8, false);
            i++;
        }
    }

    private BluetoothAdvertisement(int i) {
        super(24, i);
    }

    public static BluetoothAdvertisement deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

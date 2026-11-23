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

public final class BluetoothGattDbElement extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public short attributeHandle;
    public byte deprecatedId;
    public short elementId;
    public short endHandle;
    public byte properties;
    public short startHandle;
    public int type;
    public BluetoothUuid uuid;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(32, 19)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public BluetoothGattDbElement() {
        super(32, 19);
    }

    public static BluetoothGattDbElement decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            BluetoothGattDbElement bluetoothGattDbElement = new BluetoothGattDbElement(i);
            bluetoothGattDbElement.deprecatedId = decoder.readByte(8);
            bluetoothGattDbElement.properties = decoder.readByte(9);
            bluetoothGattDbElement.attributeHandle = decoder.readShort(10);
            int i2 = decoder.readInt(12);
            bluetoothGattDbElement.type = i2;
            BluetoothGattDbAttributeType.validate(i2);
            bluetoothGattDbElement.uuid = BluetoothUuid.decode(decoder.readPointer(16, false));
            bluetoothGattDbElement.startHandle = decoder.readShort(24);
            bluetoothGattDbElement.endHandle = decoder.readShort(26);
            if (i >= 19) {
                bluetoothGattDbElement.elementId = decoder.readShort(28);
            }
            return bluetoothGattDbElement;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothGattDbElement deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.deprecatedId, 8);
        encoderAtDataOffset.encode(this.properties, 9);
        encoderAtDataOffset.encode(this.attributeHandle, 10);
        encoderAtDataOffset.encode(this.type, 12);
        encoderAtDataOffset.encode((Struct) this.uuid, 16, false);
        encoderAtDataOffset.encode(this.startHandle, 24);
        encoderAtDataOffset.encode(this.endHandle, 26);
        encoderAtDataOffset.encode(this.elementId, 28);
    }

    private BluetoothGattDbElement(int i) {
        super(32, i);
    }

    public static BluetoothGattDbElement deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

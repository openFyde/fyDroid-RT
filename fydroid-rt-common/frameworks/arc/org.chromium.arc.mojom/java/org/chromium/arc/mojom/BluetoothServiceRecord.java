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

public final class BluetoothServiceRecord extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public short channel;
    public String name;
    public BluetoothUuid uuid;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothServiceRecord() {
        super(32, 0);
    }

    public static BluetoothServiceRecord decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothServiceRecord bluetoothServiceRecord = new BluetoothServiceRecord(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothServiceRecord.uuid = BluetoothUuid.decode(decoder.readPointer(8, false));
            bluetoothServiceRecord.channel = decoder.readShort(16);
            bluetoothServiceRecord.name = decoder.readString(24, false);
            return bluetoothServiceRecord;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothServiceRecord deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.uuid, 8, false);
        encoderAtDataOffset.encode(this.channel, 16);
        encoderAtDataOffset.encode(this.name, 24, false);
    }

    private BluetoothServiceRecord(int i) {
        super(32, i);
    }

    public static BluetoothServiceRecord deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

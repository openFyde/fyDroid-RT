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

public final class BluetoothRemoteVersion extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int manufacturer;
    public int subVer;
    public int version;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothRemoteVersion() {
        super(24, 0);
    }

    public static BluetoothRemoteVersion decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothRemoteVersion bluetoothRemoteVersion = new BluetoothRemoteVersion(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothRemoteVersion.version = decoder.readInt(8);
            bluetoothRemoteVersion.subVer = decoder.readInt(12);
            bluetoothRemoteVersion.manufacturer = decoder.readInt(16);
            return bluetoothRemoteVersion;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothRemoteVersion deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.version, 8);
        encoderAtDataOffset.encode(this.subVer, 12);
        encoderAtDataOffset.encode(this.manufacturer, 16);
    }

    private BluetoothRemoteVersion(int i) {
        super(24, i);
    }

    public static BluetoothRemoteVersion deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

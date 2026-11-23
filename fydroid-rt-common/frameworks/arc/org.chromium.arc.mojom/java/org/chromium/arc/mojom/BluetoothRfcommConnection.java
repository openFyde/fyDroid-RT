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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

public final class BluetoothRfcommConnection extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public BluetoothAddress addr;
    public int channel;
    public UntypedHandle sock;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothRfcommConnection() {
        super(24, 0);
        this.sock = InvalidHandle.INSTANCE;
    }

    public static BluetoothRfcommConnection decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothRfcommConnection bluetoothRfcommConnection = new BluetoothRfcommConnection(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothRfcommConnection.sock = decoder.readUntypedHandle(8, false);
            bluetoothRfcommConnection.channel = decoder.readInt(12);
            bluetoothRfcommConnection.addr = BluetoothAddress.decode(decoder.readPointer(16, false));
            return bluetoothRfcommConnection;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothRfcommConnection deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Handle) this.sock, 8, false);
        encoderAtDataOffset.encode(this.channel, 12);
        encoderAtDataOffset.encode((Struct) this.addr, 16, false);
    }

    public static BluetoothRfcommConnection deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private BluetoothRfcommConnection(int i) {
        super(24, i);
        this.sock = InvalidHandle.INSTANCE;
    }
}

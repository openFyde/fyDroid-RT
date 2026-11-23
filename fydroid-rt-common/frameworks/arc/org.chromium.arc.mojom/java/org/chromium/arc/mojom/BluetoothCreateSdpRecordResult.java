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

public final class BluetoothCreateSdpRecordResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int serviceHandle;
    public int status;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothCreateSdpRecordResult() {
        super(16, 0);
    }

    public static BluetoothCreateSdpRecordResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothCreateSdpRecordResult bluetoothCreateSdpRecordResult = new BluetoothCreateSdpRecordResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            bluetoothCreateSdpRecordResult.status = i;
            BluetoothStatus.validate(i);
            bluetoothCreateSdpRecordResult.serviceHandle = decoder.readInt(12);
            return bluetoothCreateSdpRecordResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothCreateSdpRecordResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.status, 8);
        encoderAtDataOffset.encode(this.serviceHandle, 12);
    }

    private BluetoothCreateSdpRecordResult(int i) {
        super(16, i);
    }

    public static BluetoothCreateSdpRecordResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

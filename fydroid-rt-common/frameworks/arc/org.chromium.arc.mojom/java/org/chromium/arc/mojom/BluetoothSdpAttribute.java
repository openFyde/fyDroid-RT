// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo_base.mojom.Value;

public final class BluetoothSdpAttribute extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String deprecatedJsonValue;
    public BluetoothSdpAttribute[] sequence;
    public int type;
    public int typeSize;
    public Value value;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(48, 14)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public BluetoothSdpAttribute() {
        super(48, 14);
    }

    public static BluetoothSdpAttribute decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            BluetoothSdpAttribute bluetoothSdpAttribute = new BluetoothSdpAttribute(i);
            int i2 = decoder.readInt(8);
            bluetoothSdpAttribute.type = i2;
            BluetoothSdpAttributeType.validate(i2);
            bluetoothSdpAttribute.typeSize = decoder.readInt(12);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            bluetoothSdpAttribute.sequence = new BluetoothSdpAttribute[dataHeaderForPointerArray.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                bluetoothSdpAttribute.sequence[i3] = decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer, false));
            }
            bluetoothSdpAttribute.deprecatedJsonValue = decoder.readString(24, true);
            if (i >= 14) {
                bluetoothSdpAttribute.value = Value.decode(decoder, 32);
            }
            return bluetoothSdpAttribute;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothSdpAttribute deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.type, 8);
        encoderAtDataOffset.encode(this.typeSize, 12);
        BluetoothSdpAttribute[] bluetoothSdpAttributeArr = this.sequence;
        if (bluetoothSdpAttributeArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(bluetoothSdpAttributeArr.length, 16, -1);
            int i = 0;
            while (true) {
                BluetoothSdpAttribute[] bluetoothSdpAttributeArr2 = this.sequence;
                if (i >= bluetoothSdpAttributeArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) bluetoothSdpAttributeArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode(this.deprecatedJsonValue, 24, true);
        encoderAtDataOffset.encode((Union) this.value, 32, true);
    }

    private BluetoothSdpAttribute(int i) {
        super(48, i);
    }

    public static BluetoothSdpAttribute deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

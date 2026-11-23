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

public final class KeyEventData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean isAltDown;
    public boolean isAltGrDown;
    public boolean isCapslockOn;
    public boolean isControlDown;
    public boolean isRepeat;
    public boolean isShiftDown;
    public int keyCode;
    public boolean pressed;
    public int scanCode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 16), new DataHeader(24, 20), new DataHeader(24, 23)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[3];
    }

    public KeyEventData() {
        super(24, 23);
    }

    public static KeyEventData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            KeyEventData keyEventData = new KeyEventData(i);
            keyEventData.pressed = decoder.readBoolean(8, 0);
            keyEventData.isShiftDown = decoder.readBoolean(8, 1);
            keyEventData.isControlDown = decoder.readBoolean(8, 2);
            keyEventData.isAltDown = decoder.readBoolean(8, 3);
            keyEventData.isCapslockOn = decoder.readBoolean(8, 4);
            if (i >= 20) {
                keyEventData.isAltGrDown = decoder.readBoolean(8, 5);
            }
            if (i >= 23) {
                keyEventData.isRepeat = decoder.readBoolean(8, 6);
            }
            keyEventData.keyCode = decoder.readInt(12);
            if (i >= 16) {
                keyEventData.scanCode = decoder.readInt(16);
            }
            return keyEventData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static KeyEventData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.pressed, 8, 0);
        encoderAtDataOffset.encode(this.isShiftDown, 8, 1);
        encoderAtDataOffset.encode(this.isControlDown, 8, 2);
        encoderAtDataOffset.encode(this.isAltDown, 8, 3);
        encoderAtDataOffset.encode(this.isCapslockOn, 8, 4);
        encoderAtDataOffset.encode(this.isAltGrDown, 8, 5);
        encoderAtDataOffset.encode(this.isRepeat, 8, 6);
        encoderAtDataOffset.encode(this.keyCode, 12);
        encoderAtDataOffset.encode(this.scanCode, 16);
    }

    private KeyEventData(int i) {
        super(24, i);
    }

    public static KeyEventData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

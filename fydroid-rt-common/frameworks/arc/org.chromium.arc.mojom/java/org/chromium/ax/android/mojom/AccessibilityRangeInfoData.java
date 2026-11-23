// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AccessibilityRangeInfoData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public float current;
    public float max;
    public float min;
    public int rangeType;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AccessibilityRangeInfoData() {
        super(24, 0);
    }

    public static AccessibilityRangeInfoData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AccessibilityRangeInfoData accessibilityRangeInfoData = new AccessibilityRangeInfoData(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            accessibilityRangeInfoData.rangeType = i;
            AccessibilityRangeType.validate(i);
            accessibilityRangeInfoData.min = decoder.readFloat(12);
            accessibilityRangeInfoData.max = decoder.readFloat(16);
            accessibilityRangeInfoData.current = decoder.readFloat(20);
            return accessibilityRangeInfoData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityRangeInfoData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.rangeType, 8);
        encoderAtDataOffset.encode(this.min, 12);
        encoderAtDataOffset.encode(this.max, 16);
        encoderAtDataOffset.encode(this.current, 20);
    }

    private AccessibilityRangeInfoData(int i) {
        super(24, i);
    }

    public static AccessibilityRangeInfoData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

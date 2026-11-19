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

public final class AccessibilityCollectionInfoData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int columnCount;
    public boolean isHierarchical;
    public int rowCount;
    public int selectionMode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AccessibilityCollectionInfoData() {
        super(24, 0);
    }

    public static AccessibilityCollectionInfoData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AccessibilityCollectionInfoData accessibilityCollectionInfoData = new AccessibilityCollectionInfoData(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            accessibilityCollectionInfoData.rowCount = decoder.readInt(8);
            accessibilityCollectionInfoData.columnCount = decoder.readInt(12);
            accessibilityCollectionInfoData.isHierarchical = decoder.readBoolean(16, 0);
            int i = decoder.readInt(20);
            accessibilityCollectionInfoData.selectionMode = i;
            AccessibilitySelectionMode.validate(i);
            return accessibilityCollectionInfoData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityCollectionInfoData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.rowCount, 8);
        encoderAtDataOffset.encode(this.columnCount, 12);
        encoderAtDataOffset.encode(this.isHierarchical, 16, 0);
        encoderAtDataOffset.encode(this.selectionMode, 20);
    }

    private AccessibilityCollectionInfoData(int i) {
        super(24, i);
    }

    public static AccessibilityCollectionInfoData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

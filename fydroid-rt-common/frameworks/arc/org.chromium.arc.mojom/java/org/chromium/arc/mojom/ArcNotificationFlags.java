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

public final class ArcNotificationFlags extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    public static final int SUPPORT_SNOOZE = 1;
    private static final DataHeader[] VERSION_ARRAY;
    public int value;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcNotificationFlags() {
        super(16, 0);
    }

    public static ArcNotificationFlags decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcNotificationFlags arcNotificationFlags = new ArcNotificationFlags(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcNotificationFlags.value = decoder.readInt(8);
            return arcNotificationFlags;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcNotificationFlags deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.value, 8);
    }

    private ArcNotificationFlags(int i) {
        super(16, i);
    }

    public static ArcNotificationFlags deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

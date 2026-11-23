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

public final class ArcNotificationButton extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String buttonPlaceholder;
    public String label;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 31)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public ArcNotificationButton() {
        super(24, 31);
    }

    public static ArcNotificationButton decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            ArcNotificationButton arcNotificationButton = new ArcNotificationButton(i);
            arcNotificationButton.label = decoder.readString(8, false);
            if (i >= 31) {
                arcNotificationButton.buttonPlaceholder = decoder.readString(16, true);
            }
            return arcNotificationButton;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcNotificationButton deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.label, 8, false);
        encoderAtDataOffset.encode(this.buttonPlaceholder, 16, true);
    }

    private ArcNotificationButton(int i) {
        super(24, i);
    }

    public static ArcNotificationButton deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

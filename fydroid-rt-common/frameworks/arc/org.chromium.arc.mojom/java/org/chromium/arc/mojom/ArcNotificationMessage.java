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

public final class ArcNotificationMessage extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String message;
    public ArcBitmap senderIcon;
    public String senderName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcNotificationMessage() {
        super(32, 0);
    }

    public static ArcNotificationMessage decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcNotificationMessage arcNotificationMessage = new ArcNotificationMessage(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcNotificationMessage.message = decoder.readString(8, true);
            arcNotificationMessage.senderName = decoder.readString(16, true);
            arcNotificationMessage.senderIcon = ArcBitmap.decode(decoder.readPointer(24, true));
            return arcNotificationMessage;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcNotificationMessage deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.message, 8, true);
        encoderAtDataOffset.encode(this.senderName, 16, true);
        encoderAtDataOffset.encode((Struct) this.senderIcon, 24, true);
    }

    private ArcNotificationMessage(int i) {
        super(32, i);
    }

    public static ArcNotificationMessage deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

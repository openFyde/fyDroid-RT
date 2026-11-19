// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class MediaSessionDebugInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String name;
    public String owner;
    public String state;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public MediaSessionDebugInfo() {
        super(32, 0);
    }

    public static MediaSessionDebugInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            MediaSessionDebugInfo mediaSessionDebugInfo = new MediaSessionDebugInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            mediaSessionDebugInfo.name = decoder.readString(8, false);
            mediaSessionDebugInfo.owner = decoder.readString(16, false);
            mediaSessionDebugInfo.state = decoder.readString(24, false);
            return mediaSessionDebugInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MediaSessionDebugInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.name, 8, false);
        encoderAtDataOffset.encode(this.owner, 16, false);
        encoderAtDataOffset.encode(this.state, 24, false);
    }

    private MediaSessionDebugInfo(int i) {
        super(32, i);
    }

    public static MediaSessionDebugInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

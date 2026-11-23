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

public final class LinkSpeed extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int downlinkSpeedKbps;
    public int uplinkSpeedKbps;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public LinkSpeed() {
        super(16, 0);
    }

    public static LinkSpeed decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            LinkSpeed linkSpeed = new LinkSpeed(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            linkSpeed.uplinkSpeedKbps = decoder.readInt(8);
            linkSpeed.downlinkSpeedKbps = decoder.readInt(12);
            return linkSpeed;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static LinkSpeed deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.uplinkSpeedKbps, 8);
        encoderAtDataOffset.encode(this.downlinkSpeedKbps, 12);
    }

    private LinkSpeed(int i) {
        super(16, i);
    }

    public static LinkSpeed deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

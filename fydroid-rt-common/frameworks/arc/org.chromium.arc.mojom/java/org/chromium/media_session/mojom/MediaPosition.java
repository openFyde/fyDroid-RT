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
import org.chromium.mojo_base.mojom.TimeDelta;
import org.chromium.mojo_base.mojom.TimeTicks;

public final class MediaPosition extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public TimeDelta duration;
    public boolean endOfMedia;
    public TimeTicks lastUpdatedTime;
    public double playbackRate;
    public TimeDelta position;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 13)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public MediaPosition() {
        super(48, 13);
    }

    public static MediaPosition decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            MediaPosition mediaPosition = new MediaPosition(i);
            mediaPosition.playbackRate = decoder.readDouble(8);
            mediaPosition.duration = TimeDelta.decode(decoder.readPointer(16, false));
            mediaPosition.position = TimeDelta.decode(decoder.readPointer(24, false));
            mediaPosition.lastUpdatedTime = TimeTicks.decode(decoder.readPointer(32, false));
            if (i >= 13) {
                mediaPosition.endOfMedia = decoder.readBoolean(40, 0);
            }
            return mediaPosition;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MediaPosition deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.playbackRate, 8);
        encoderAtDataOffset.encode((Struct) this.duration, 16, false);
        encoderAtDataOffset.encode((Struct) this.position, 24, false);
        encoderAtDataOffset.encode((Struct) this.lastUpdatedTime, 32, false);
        encoderAtDataOffset.encode(this.endOfMedia, 40, 0);
    }

    private MediaPosition(int i) {
        super(48, i);
    }

    public static MediaPosition deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

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

public final class VideoEncodeProfile extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int maxFramerateDenominator;
    public int maxFramerateNumerator;
    public Size maxResolution;
    public int profile;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public VideoEncodeProfile() {
        super(32, 0);
    }

    public static VideoEncodeProfile decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            VideoEncodeProfile videoEncodeProfile = new VideoEncodeProfile(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            videoEncodeProfile.profile = i;
            VideoCodecProfile.validate(i);
            videoEncodeProfile.maxFramerateNumerator = decoder.readInt(12);
            videoEncodeProfile.maxResolution = Size.decode(decoder.readPointer(16, false));
            videoEncodeProfile.maxFramerateDenominator = decoder.readInt(24);
            return videoEncodeProfile;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VideoEncodeProfile deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.profile, 8);
        encoderAtDataOffset.encode(this.maxFramerateNumerator, 12);
        encoderAtDataOffset.encode((Struct) this.maxResolution, 16, false);
        encoderAtDataOffset.encode(this.maxFramerateDenominator, 24);
    }

    private VideoEncodeProfile(int i) {
        super(32, i);
    }

    public static VideoEncodeProfile deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

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

public final class VideoEncodeAcceleratorConfig extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte h264OutputLevel;
    public boolean hasH264OutputLevel;
    public boolean hasInitialFramerate;
    public int initialBitrate;
    public int initialFramerate;
    public int inputFormat;
    public Size inputVisibleSize;
    public int outputProfile;
    public int storageType;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(40, 1)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public VideoEncodeAcceleratorConfig() {
        super(40, 1);
    }

    public static VideoEncodeAcceleratorConfig decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            VideoEncodeAcceleratorConfig videoEncodeAcceleratorConfig = new VideoEncodeAcceleratorConfig(i);
            int i2 = decoder.readInt(8);
            videoEncodeAcceleratorConfig.inputFormat = i2;
            VideoPixelFormat.validate(i2);
            int i3 = decoder.readInt(12);
            videoEncodeAcceleratorConfig.outputProfile = i3;
            VideoCodecProfile.validate(i3);
            videoEncodeAcceleratorConfig.inputVisibleSize = Size.decode(decoder.readPointer(16, false));
            videoEncodeAcceleratorConfig.initialBitrate = decoder.readInt(24);
            videoEncodeAcceleratorConfig.initialFramerate = decoder.readInt(28);
            videoEncodeAcceleratorConfig.hasInitialFramerate = decoder.readBoolean(32, 0);
            videoEncodeAcceleratorConfig.hasH264OutputLevel = decoder.readBoolean(32, 1);
            videoEncodeAcceleratorConfig.h264OutputLevel = decoder.readByte(33);
            if (i >= 1) {
                int i4 = decoder.readInt(36);
                videoEncodeAcceleratorConfig.storageType = i4;
                VideoFrameStorageType.validate(i4);
            }
            return videoEncodeAcceleratorConfig;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VideoEncodeAcceleratorConfig deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.inputFormat, 8);
        encoderAtDataOffset.encode(this.outputProfile, 12);
        encoderAtDataOffset.encode((Struct) this.inputVisibleSize, 16, false);
        encoderAtDataOffset.encode(this.initialBitrate, 24);
        encoderAtDataOffset.encode(this.initialFramerate, 28);
        encoderAtDataOffset.encode(this.hasInitialFramerate, 32, 0);
        encoderAtDataOffset.encode(this.hasH264OutputLevel, 32, 1);
        encoderAtDataOffset.encode(this.h264OutputLevel, 33);
        encoderAtDataOffset.encode(this.storageType, 36);
    }

    private VideoEncodeAcceleratorConfig(int i) {
        super(40, i);
    }

    public static VideoEncodeAcceleratorConfig deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

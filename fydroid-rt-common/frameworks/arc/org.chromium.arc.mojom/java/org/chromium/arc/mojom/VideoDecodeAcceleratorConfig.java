package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class VideoDecodeAcceleratorConfig extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int profile;
    public boolean secureMode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public VideoDecodeAcceleratorConfig() {
        super(16, 0);
    }

    public static VideoDecodeAcceleratorConfig decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            VideoDecodeAcceleratorConfig videoDecodeAcceleratorConfig = new VideoDecodeAcceleratorConfig(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            videoDecodeAcceleratorConfig.profile = i;
            VideoCodecProfile.validate(i);
            videoDecodeAcceleratorConfig.secureMode = decoder.readBoolean(12, 0);
            return videoDecodeAcceleratorConfig;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VideoDecodeAcceleratorConfig deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.profile, 8);
        encoderAtDataOffset.encode(this.secureMode, 12, 0);
    }

    private VideoDecodeAcceleratorConfig(int i) {
        super(16, i);
    }

    public static VideoDecodeAcceleratorConfig deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

package org.chromium.media_session.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo_base.mojom.UnguessableToken;

public final class AudioFocusRequestState extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int audioFocusType;
    public UnguessableToken requestId;
    public MediaSessionInfo sessionInfo;
    public String sourceName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 2), new DataHeader(40, 3)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public AudioFocusRequestState() {
        super(40, 3);
    }

    public static AudioFocusRequestState decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AudioFocusRequestState audioFocusRequestState = new AudioFocusRequestState(i);
            audioFocusRequestState.sessionInfo = MediaSessionInfo.decode(decoder.readPointer(8, false));
            int i2 = decoder.readInt(16);
            audioFocusRequestState.audioFocusType = i2;
            AudioFocusType.validate(i2);
            if (i >= 2) {
                audioFocusRequestState.sourceName = decoder.readString(24, true);
            }
            if (i >= 3) {
                audioFocusRequestState.requestId = UnguessableToken.decode(decoder.readPointer(32, true));
            }
            return audioFocusRequestState;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AudioFocusRequestState deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.sessionInfo, 8, false);
        encoderAtDataOffset.encode(this.audioFocusType, 16);
        encoderAtDataOffset.encode(this.sourceName, 24, true);
        encoderAtDataOffset.encode((Struct) this.requestId, 32, true);
    }

    private AudioFocusRequestState(int i) {
        super(40, i);
    }

    public static AudioFocusRequestState deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

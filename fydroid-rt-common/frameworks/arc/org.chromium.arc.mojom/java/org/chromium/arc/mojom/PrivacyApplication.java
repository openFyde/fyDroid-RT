package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PrivacyApplication extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String packageName;
    public int uid;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrivacyApplication() {
        super(24, 0);
    }

    public static PrivacyApplication decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrivacyApplication privacyApplication = new PrivacyApplication(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            privacyApplication.packageName = decoder.readString(8, false);
            privacyApplication.uid = decoder.readInt(16);
            return privacyApplication;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrivacyApplication deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.packageName, 8, false);
        encoderAtDataOffset.encode(this.uid, 16);
    }

    private PrivacyApplication(int i) {
        super(24, i);
    }

    public static PrivacyApplication deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

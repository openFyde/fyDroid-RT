package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class QuotaSpaces extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long[] curspacesForGids;
    public long[] curspacesForProjectIds;
    public long[] curspacesForUids;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public QuotaSpaces() {
        super(32, 0);
    }

    public static QuotaSpaces decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            QuotaSpaces quotaSpaces = new QuotaSpaces(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            quotaSpaces.curspacesForUids = decoder.readLongs(8, 0, -1);
            quotaSpaces.curspacesForGids = decoder.readLongs(16, 0, -1);
            quotaSpaces.curspacesForProjectIds = decoder.readLongs(24, 0, -1);
            return quotaSpaces;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static QuotaSpaces deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.curspacesForUids, 8, 0, -1);
        encoderAtDataOffset.encode(this.curspacesForGids, 16, 0, -1);
        encoderAtDataOffset.encode(this.curspacesForProjectIds, 24, 0, -1);
    }

    private QuotaSpaces(int i) {
        super(32, i);
    }

    public static QuotaSpaces deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ExportKeyRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] appData;
    public byte[] clientId;
    public byte[] keyBlob;
    public int keyFormat;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ExportKeyRequest() {
        super(40, 0);
    }

    public static ExportKeyRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ExportKeyRequest exportKeyRequest = new ExportKeyRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            exportKeyRequest.keyFormat = i;
            KeyFormat.validate(i);
            exportKeyRequest.keyBlob = decoder.readBytes(16, 0, -1);
            exportKeyRequest.clientId = decoder.readBytes(24, 0, -1);
            exportKeyRequest.appData = decoder.readBytes(32, 0, -1);
            return exportKeyRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ExportKeyRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyFormat, 8);
        encoderAtDataOffset.encode(this.keyBlob, 16, 0, -1);
        encoderAtDataOffset.encode(this.clientId, 24, 0, -1);
        encoderAtDataOffset.encode(this.appData, 32, 0, -1);
    }

    private ExportKeyRequest(int i) {
        super(40, i);
    }

    public static ExportKeyRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

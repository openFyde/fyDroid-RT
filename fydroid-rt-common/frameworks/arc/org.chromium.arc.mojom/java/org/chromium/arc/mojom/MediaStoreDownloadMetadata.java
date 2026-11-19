package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo_base.mojom.FilePath;

public final class MediaStoreDownloadMetadata extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String displayName;
    public String ownerPackageName;
    public FilePath relativePath;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public MediaStoreDownloadMetadata() {
        super(32, 0);
    }

    public static MediaStoreDownloadMetadata decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            MediaStoreDownloadMetadata mediaStoreDownloadMetadata = new MediaStoreDownloadMetadata(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            mediaStoreDownloadMetadata.displayName = decoder.readString(8, false);
            mediaStoreDownloadMetadata.ownerPackageName = decoder.readString(16, false);
            mediaStoreDownloadMetadata.relativePath = FilePath.decode(decoder.readPointer(24, false));
            return mediaStoreDownloadMetadata;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MediaStoreDownloadMetadata deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.displayName, 8, false);
        encoderAtDataOffset.encode(this.ownerPackageName, 16, false);
        encoderAtDataOffset.encode((Struct) this.relativePath, 24, false);
    }

    private MediaStoreDownloadMetadata(int i) {
        super(32, i);
    }

    public static MediaStoreDownloadMetadata deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

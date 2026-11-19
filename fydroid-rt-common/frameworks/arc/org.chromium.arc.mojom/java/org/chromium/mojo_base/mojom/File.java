package org.chromium.mojo_base.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

public final class File extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean async;
    public UntypedHandle fd;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public File() {
        super(16, 0);
        this.fd = InvalidHandle.INSTANCE;
    }

    public static File decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            File file = new File(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            file.fd = decoder.readUntypedHandle(8, false);
            file.async = decoder.readBoolean(12, 0);
            return file;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static File deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Handle) this.fd, 8, false);
        encoderAtDataOffset.encode(this.async, 12, 0);
    }

    public static File deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private File(int i) {
        super(16, i);
        this.fd = InvalidHandle.INSTANCE;
    }
}

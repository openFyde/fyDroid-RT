package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Root extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String authority;
    public String documentId;
    public ArcBitmap icon;
    public String[] mimeTypes;
    public String rootId;
    public String summary;
    public boolean supportsCreate;
    public String title;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(56, 0), new DataHeader(72, 12)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public Root() {
        super(72, 12);
    }

    public static Root decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            Root root = new Root(i);
            root.authority = decoder.readString(8, false);
            root.rootId = decoder.readString(16, false);
            root.documentId = decoder.readString(24, false);
            root.title = decoder.readString(32, false);
            root.summary = decoder.readString(40, true);
            root.icon = ArcBitmap.decode(decoder.readPointer(48, true));
            if (i >= 12) {
                root.supportsCreate = decoder.readBoolean(56, 0);
                Decoder pointer = decoder.readPointer(64, true);
                if (pointer == null) {
                    root.mimeTypes = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    root.mimeTypes = new String[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                        root.mimeTypes[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
                    }
                }
            }
            return root;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Root deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.authority, 8, false);
        encoderAtDataOffset.encode(this.rootId, 16, false);
        encoderAtDataOffset.encode(this.documentId, 24, false);
        encoderAtDataOffset.encode(this.title, 32, false);
        encoderAtDataOffset.encode(this.summary, 40, true);
        encoderAtDataOffset.encode((Struct) this.icon, 48, true);
        encoderAtDataOffset.encode(this.supportsCreate, 56, 0);
        String[] strArr = this.mimeTypes;
        if (strArr == null) {
            encoderAtDataOffset.encodeNullPointer(64, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 64, -1);
        int iOutline0 = 0;
        while (true) {
            String[] strArr2 = this.mimeTypes;
            if (iOutline0 >= strArr2.length) {
                return;
            }
            iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
        }
    }

    private Root(int i) {
        super(72, i);
    }

    public static Root deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

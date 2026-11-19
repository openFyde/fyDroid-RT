package org.chromium.media_session.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.gfx.mojom.Size;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo_base.mojom.String16;
import org.chromium.url.mojom.Url;

public final class MediaImage extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Size[] sizes;
    public Url src;
    public String16 type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public MediaImage() {
        super(32, 0);
    }

    public static MediaImage decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            MediaImage mediaImage = new MediaImage(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            mediaImage.src = Url.decode(decoder.readPointer(8, false));
            mediaImage.type = String16.decode(decoder.readPointer(16, false));
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            mediaImage.sizes = new Size[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                mediaImage.sizes[i] = Size.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            return mediaImage;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MediaImage deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.src, 8, false);
        encoderAtDataOffset.encode((Struct) this.type, 16, false);
        Size[] sizeArr = this.sizes;
        if (sizeArr == null) {
            encoderAtDataOffset.encodeNullPointer(24, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(sizeArr.length, 24, -1);
        int i = 0;
        while (true) {
            Size[] sizeArr2 = this.sizes;
            if (i >= sizeArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) sizeArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private MediaImage(int i) {
        super(32, i);
    }

    public static MediaImage deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

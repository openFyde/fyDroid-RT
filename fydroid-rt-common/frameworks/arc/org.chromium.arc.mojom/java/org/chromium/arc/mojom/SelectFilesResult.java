package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.url.mojom.Url;

public final class SelectFilesResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String pickerActivity;
    public Url[] urls;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SelectFilesResult() {
        super(24, 0);
    }

    public static SelectFilesResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SelectFilesResult selectFilesResult = new SelectFilesResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            selectFilesResult.urls = new Url[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                selectFilesResult.urls[i] = Url.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            selectFilesResult.pickerActivity = decoder.readString(16, true);
            return selectFilesResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SelectFilesResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        Url[] urlArr = this.urls;
        if (urlArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(urlArr.length, 8, -1);
            int i = 0;
            while (true) {
                Url[] urlArr2 = this.urls;
                if (i >= urlArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) urlArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        encoderAtDataOffset.encode(this.pickerActivity, 16, true);
    }

    private SelectFilesResult(int i) {
        super(24, i);
    }

    public static SelectFilesResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

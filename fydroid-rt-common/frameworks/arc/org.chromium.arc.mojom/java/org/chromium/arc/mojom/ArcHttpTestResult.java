package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ArcHttpTestResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long durationMs;
    public boolean isSuccessful;
    public Map<String, String> responseHeaderFields;
    public int statusCode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcHttpTestResult() {
        super(32, 0);
    }

    public static ArcHttpTestResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcHttpTestResult arcHttpTestResult = new ArcHttpTestResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcHttpTestResult.isSuccessful = decoder.readBoolean(8, 0);
            arcHttpTestResult.statusCode = decoder.readInt(12);
            Decoder pointer = decoder.readPointer(16, false);
            pointer.readDataHeaderForMap();
            Decoder pointer2 = pointer.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(-1);
            int i = dataHeaderForPointerArray.elementsOrVersion;
            String[] strArr = new String[i];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                strArr[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer2, false);
            }
            Decoder pointer3 = pointer.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer3.readDataHeaderForPointerArray(i);
            String[] strArr2 = new String[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                strArr2[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer3, false);
            }
            arcHttpTestResult.responseHeaderFields = new HashMap();
            for (int i4 = 0; i4 < i; i4++) {
                arcHttpTestResult.responseHeaderFields.put(strArr[i4], strArr2[i4]);
            }
            arcHttpTestResult.durationMs = decoder.readLong(24);
            return arcHttpTestResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcHttpTestResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        boolean z = false;
        encoderAtDataOffset.encode(this.isSuccessful, 8, 0);
        encoderAtDataOffset.encode(this.statusCode, 12);
        if (this.responseHeaderFields == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(16);
            int size = this.responseHeaderFields.size();
            String[] strArr = new String[size];
            String[] strArr2 = new String[size];
            int i = 0;
            for (Map.Entry<String, String> entry : this.responseHeaderFields.entrySet()) {
                strArr[i] = entry.getKey();
                strArr2[i] = entry.getValue();
                i++;
            }
            int i2 = -1;
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 8, -1);
            int iOutline0 = 0;
            while (iOutline0 < size) {
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr[iOutline0], z, iOutline0, 1);
                i2 = i2;
                strArr2 = strArr2;
                z = false;
            }
            String[] strArr3 = strArr2;
            Encoder encoderEncodePointerArray2 = encoderEncoderForMap.encodePointerArray(size, 16, i2);
            for (int iOutline02 = 0; iOutline02 < size; iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr3[iOutline02], false, iOutline02, 1)) {
            }
        }
        encoderAtDataOffset.encode(this.durationMs, 24);
    }

    private ArcHttpTestResult(int i) {
        super(32, i);
    }

    public static ArcHttpTestResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

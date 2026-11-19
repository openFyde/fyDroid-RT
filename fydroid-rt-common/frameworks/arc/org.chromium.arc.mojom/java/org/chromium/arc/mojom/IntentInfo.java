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

public final class IntentInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String action;
    public String[] categories;
    public String clipDataUri;
    public String data;
    public Map<String, String> extras;
    public String type;
    public boolean uiBypassed;
    public UriComponents uriComponents;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 13), new DataHeader(56, 20), new DataHeader(64, 22), new DataHeader(72, 30)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[4];
    }

    public IntentInfo() {
        super(72, 30);
    }

    public static IntentInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            IntentInfo intentInfo = new IntentInfo(i);
            intentInfo.action = decoder.readString(8, false);
            Decoder pointer = decoder.readPointer(16, true);
            if (pointer == null) {
                intentInfo.categories = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentInfo.categories = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    intentInfo.categories[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
                }
            }
            intentInfo.data = decoder.readString(24, true);
            intentInfo.type = decoder.readString(32, true);
            if (i >= 13) {
                intentInfo.clipDataUri = decoder.readString(40, true);
            }
            if (i >= 20) {
                intentInfo.uiBypassed = decoder.readBoolean(48, 0);
            }
            if (i >= 22) {
                Decoder pointer2 = decoder.readPointer(56, true);
                if (pointer2 == null) {
                    intentInfo.extras = null;
                } else {
                    pointer2.readDataHeaderForMap();
                    Decoder pointer3 = pointer2.readPointer(8, false);
                    DataHeader dataHeaderForPointerArray2 = pointer3.readDataHeaderForPointerArray(-1);
                    int i3 = dataHeaderForPointerArray2.elementsOrVersion;
                    String[] strArr = new String[i3];
                    for (int i4 = 0; i4 < dataHeaderForPointerArray2.elementsOrVersion; i4++) {
                        strArr[i4] = GeneratedOutlineSupport.outline1(i4, 8, 8, pointer3, false);
                    }
                    Decoder pointer4 = pointer2.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray3 = pointer4.readDataHeaderForPointerArray(i3);
                    String[] strArr2 = new String[dataHeaderForPointerArray3.elementsOrVersion];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray3.elementsOrVersion; i5++) {
                        strArr2[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer4, false);
                    }
                    intentInfo.extras = new HashMap();
                    for (int i6 = 0; i6 < i3; i6++) {
                        intentInfo.extras.put(strArr[i6], strArr2[i6]);
                    }
                }
            }
            if (i >= 30) {
                intentInfo.uriComponents = UriComponents.decode(decoder.readPointer(64, true));
            }
            return intentInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static IntentInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.action, 8, false);
        String[] strArr = this.categories;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.categories;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, true);
        }
        encoderAtDataOffset.encode(this.data, 24, true);
        encoderAtDataOffset.encode(this.type, 32, true);
        encoderAtDataOffset.encode(this.clipDataUri, 40, true);
        encoderAtDataOffset.encode(this.uiBypassed, 48, 0);
        if (this.extras == null) {
            encoderAtDataOffset.encodeNullPointer(56, true);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(56);
            int size = this.extras.size();
            String[] strArr3 = new String[size];
            String[] strArr4 = new String[size];
            int i = 0;
            for (Map.Entry<String, String> entry : this.extras.entrySet()) {
                strArr3[i] = entry.getKey();
                strArr4[i] = entry.getValue();
                i++;
            }
            Encoder encoderEncodePointerArray2 = encoderEncoderForMap.encodePointerArray(size, 8, -1);
            int iOutline02 = 0;
            while (iOutline02 < size) {
                iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr3[iOutline02], false, iOutline02, 1);
                size = size;
                strArr4 = strArr4;
                strArr3 = strArr3;
            }
            String[] strArr5 = strArr4;
            int i2 = size;
            Encoder encoderEncodePointerArray3 = encoderEncoderForMap.encodePointerArray(i2, 16, -1);
            for (int iOutline03 = 0; iOutline03 < i2; iOutline03 = GeneratedOutlineSupport.outline0(iOutline03, 8, 8, encoderEncodePointerArray3, strArr5[iOutline03], false, iOutline03, 1)) {
            }
        }
        encoderAtDataOffset.encode((Struct) this.uriComponents, 64, true);
    }

    private IntentInfo(int i) {
        super(72, i);
    }

    public static IntentInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

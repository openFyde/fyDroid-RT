package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class SupportedLinksPackage extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public IntentFilter[] deprecatedFilters;
    public String packageName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SupportedLinksPackage() {
        super(24, 0);
    }

    public static SupportedLinksPackage decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SupportedLinksPackage supportedLinksPackage = new SupportedLinksPackage(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            supportedLinksPackage.packageName = decoder.readString(8, false);
            Decoder pointer = decoder.readPointer(16, true);
            if (pointer == null) {
                supportedLinksPackage.deprecatedFilters = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                supportedLinksPackage.deprecatedFilters = new IntentFilter[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    supportedLinksPackage.deprecatedFilters[i] = IntentFilter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
            }
            return supportedLinksPackage;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SupportedLinksPackage deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.packageName, 8, false);
        IntentFilter[] intentFilterArr = this.deprecatedFilters;
        if (intentFilterArr == null) {
            encoderAtDataOffset.encodeNullPointer(16, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(intentFilterArr.length, 16, -1);
        int i = 0;
        while (true) {
            IntentFilter[] intentFilterArr2 = this.deprecatedFilters;
            if (i >= intentFilterArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) intentFilterArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private SupportedLinksPackage(int i) {
        super(24, i);
    }

    public static SupportedLinksPackage deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

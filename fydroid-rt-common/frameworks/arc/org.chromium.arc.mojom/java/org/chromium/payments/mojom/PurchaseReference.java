package org.chromium.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PurchaseReference extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String itemId;
    public String purchaseToken;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PurchaseReference() {
        super(24, 0);
    }

    public static PurchaseReference decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PurchaseReference purchaseReference = new PurchaseReference(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            purchaseReference.itemId = decoder.readString(8, false);
            purchaseReference.purchaseToken = decoder.readString(16, false);
            return purchaseReference;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PurchaseReference deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.itemId, 8, false);
        encoderAtDataOffset.encode(this.purchaseToken, 16, false);
    }

    private PurchaseReference(int i) {
        super(24, i);
    }

    public static PurchaseReference deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

package org.chromium.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PayerErrors extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String email;
    public String name;
    public String phone;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PayerErrors() {
        super(32, 0);
    }

    public static PayerErrors decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PayerErrors payerErrors = new PayerErrors(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            payerErrors.email = decoder.readString(8, false);
            payerErrors.name = decoder.readString(16, false);
            payerErrors.phone = decoder.readString(24, false);
            return payerErrors;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PayerErrors deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.email, 8, false);
        encoderAtDataOffset.encode(this.name, 16, false);
        encoderAtDataOffset.encode(this.phone, 24, false);
    }

    private PayerErrors(int i) {
        super(32, i);
    }

    public static PayerErrors deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

package org.chromium.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PaymentCurrencyAmount extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String currency;
    public String value;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PaymentCurrencyAmount() {
        super(24, 0);
    }

    public static PaymentCurrencyAmount decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PaymentCurrencyAmount paymentCurrencyAmount = new PaymentCurrencyAmount(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            paymentCurrencyAmount.currency = decoder.readString(8, false);
            paymentCurrencyAmount.value = decoder.readString(16, false);
            return paymentCurrencyAmount;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PaymentCurrencyAmount deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.currency, 8, false);
        encoderAtDataOffset.encode(this.value, 16, false);
    }

    private PaymentCurrencyAmount(int i) {
        super(24, i);
    }

    public static PaymentCurrencyAmount deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

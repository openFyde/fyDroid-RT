package org.chromium.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PaymentValidationErrors extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String error;
    public PayerErrors payer;
    public AddressErrors shippingAddress;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PaymentValidationErrors() {
        super(32, 0);
    }

    public static PaymentValidationErrors decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PaymentValidationErrors paymentValidationErrors = new PaymentValidationErrors(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            paymentValidationErrors.error = decoder.readString(8, false);
            paymentValidationErrors.payer = PayerErrors.decode(decoder.readPointer(16, false));
            paymentValidationErrors.shippingAddress = AddressErrors.decode(decoder.readPointer(24, false));
            return paymentValidationErrors;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PaymentValidationErrors deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.error, 8, false);
        encoderAtDataOffset.encode((Struct) this.payer, 16, false);
        encoderAtDataOffset.encode((Struct) this.shippingAddress, 24, false);
    }

    private PaymentValidationErrors(int i) {
        super(32, i);
    }

    public static PaymentValidationErrors deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

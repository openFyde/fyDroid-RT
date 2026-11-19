package org.chromium.chromeos.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PaymentParameters extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String activityOrServiceName;
    public String packageName;
    public String paymentRequestId;
    public String paymentRequestOrigin;
    public String requestToken;
    public String stringifiedMethodData;
    public String topLevelOrigin;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0), new DataHeader(56, 2), new DataHeader(64, 3)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public PaymentParameters() {
        super(64, 3);
    }

    public static PaymentParameters decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            PaymentParameters paymentParameters = new PaymentParameters(i);
            paymentParameters.packageName = decoder.readString(8, false);
            paymentParameters.activityOrServiceName = decoder.readString(16, false);
            paymentParameters.stringifiedMethodData = decoder.readString(24, false);
            paymentParameters.topLevelOrigin = decoder.readString(32, false);
            paymentParameters.paymentRequestOrigin = decoder.readString(40, false);
            if (i >= 2) {
                paymentParameters.paymentRequestId = decoder.readString(48, true);
            }
            if (i >= 3) {
                paymentParameters.requestToken = decoder.readString(56, true);
            }
            return paymentParameters;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PaymentParameters deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.packageName, 8, false);
        encoderAtDataOffset.encode(this.activityOrServiceName, 16, false);
        encoderAtDataOffset.encode(this.stringifiedMethodData, 24, false);
        encoderAtDataOffset.encode(this.topLevelOrigin, 32, false);
        encoderAtDataOffset.encode(this.paymentRequestOrigin, 40, false);
        encoderAtDataOffset.encode(this.paymentRequestId, 48, true);
        encoderAtDataOffset.encode(this.requestToken, 56, true);
    }

    private PaymentParameters(int i) {
        super(64, i);
    }

    public static PaymentParameters deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

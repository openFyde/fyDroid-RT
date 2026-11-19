package org.chromium.chromeos.payments.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class InvokePaymentAppResult extends Union {
    private String mError;
    private InvokePaymentAppValidResult mValid;

    public static final class Tag {
        public static final int Error = 1;
        public static final int Valid = 0;
    }

    public static final InvokePaymentAppResult decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        InvokePaymentAppResult invokePaymentAppResult = new InvokePaymentAppResult();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            invokePaymentAppResult.mValid = InvokePaymentAppValidResult.decode(decoder.readPointer(i + 8, false));
            invokePaymentAppResult.mTag = 0;
        } else if (i2 == 1) {
            invokePaymentAppResult.mError = decoder.readString(i + 8, false);
            invokePaymentAppResult.mTag = 1;
        }
        return invokePaymentAppResult;
    }

    public static InvokePaymentAppResult deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode((Struct) this.mValid, i + 8, false);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode(this.mError, i + 8, false);
        }
    }

    public String getError() {
        return this.mError;
    }

    public InvokePaymentAppValidResult getValid() {
        return this.mValid;
    }

    public void setError(String str) {
        this.mTag = 1;
        this.mError = str;
    }

    public void setValid(InvokePaymentAppValidResult invokePaymentAppValidResult) {
        this.mTag = 0;
        this.mValid = invokePaymentAppValidResult;
    }
}

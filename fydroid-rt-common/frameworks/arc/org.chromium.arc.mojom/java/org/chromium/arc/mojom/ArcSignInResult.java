package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class ArcSignInResult extends Union {
    private ArcSignInError mError;
    private int mSuccess;

    public static final class Tag {
        public static final int Error = 1;
        public static final int Success = 0;
    }

    public static final ArcSignInResult decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        ArcSignInResult arcSignInResult = new ArcSignInResult();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            int i3 = decoder.readInt(i + 8);
            arcSignInResult.mSuccess = i3;
            ArcSignInSuccess.validate(i3);
            arcSignInResult.mTag = 0;
        } else if (i2 == 1) {
            Decoder pointer = decoder.readPointer(i + 8, true);
            if (pointer != null) {
                arcSignInResult.mError = ArcSignInError.decode(pointer, 0);
            }
            arcSignInResult.mTag = 1;
        }
        return arcSignInResult;
    }

    public static ArcSignInResult deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mSuccess, i + 8);
        } else {
            if (i2 != 1) {
                return;
            }
            if (this.mError == null) {
                encoder.encodeNullPointer(i + 8, true);
            } else {
                encoder.encoderForUnionPointer(i + 8).encode((Union) this.mError, 0, true);
            }
        }
    }

    public ArcSignInError getError() {
        return this.mError;
    }

    public int getSuccess() {
        return this.mSuccess;
    }

    public void setError(ArcSignInError arcSignInError) {
        this.mTag = 1;
        this.mError = arcSignInError;
    }

    public void setSuccess(int i) {
        this.mTag = 0;
        this.mSuccess = i;
    }
}

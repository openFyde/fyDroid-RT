package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class IntegerKeyParam extends Union {
    private byte[] mBlob;
    private boolean mBooleanValue;
    private long mDateTime;
    private int mInteger;
    private long mLongInteger;

    public static final class Tag {
        public static final int Blob = 4;
        public static final int BooleanValue = 0;
        public static final int DateTime = 3;
        public static final int Integer = 1;
        public static final int LongInteger = 2;
    }

    public static final IntegerKeyParam decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        IntegerKeyParam integerKeyParam = new IntegerKeyParam();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            integerKeyParam.mBooleanValue = decoder.readBoolean(i + 8, 0);
            integerKeyParam.mTag = 0;
        } else if (i2 == 1) {
            integerKeyParam.mInteger = decoder.readInt(i + 8);
            integerKeyParam.mTag = 1;
        } else if (i2 == 2) {
            integerKeyParam.mLongInteger = decoder.readLong(i + 8);
            integerKeyParam.mTag = 2;
        } else if (i2 == 3) {
            integerKeyParam.mDateTime = decoder.readLong(i + 8);
            integerKeyParam.mTag = 3;
        } else if (i2 == 4) {
            integerKeyParam.mBlob = decoder.readBytes(i + 8, 0, -1);
            integerKeyParam.mTag = 4;
        }
        return integerKeyParam;
    }

    public static IntegerKeyParam deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mBooleanValue, i + 8, 0);
            return;
        }
        if (i2 == 1) {
            encoder.encode(this.mInteger, i + 8);
            return;
        }
        if (i2 == 2) {
            encoder.encode(this.mLongInteger, i + 8);
        } else if (i2 == 3) {
            encoder.encode(this.mDateTime, i + 8);
        } else {
            if (i2 != 4) {
                return;
            }
            encoder.encode(this.mBlob, i + 8, 0, -1);
        }
    }

    public byte[] getBlob() {
        return this.mBlob;
    }

    public boolean getBooleanValue() {
        return this.mBooleanValue;
    }

    public long getDateTime() {
        return this.mDateTime;
    }

    public int getInteger() {
        return this.mInteger;
    }

    public long getLongInteger() {
        return this.mLongInteger;
    }

    public void setBlob(byte[] bArr) {
        this.mTag = 4;
        this.mBlob = bArr;
    }

    public void setBooleanValue(boolean z) {
        this.mTag = 0;
        this.mBooleanValue = z;
    }

    public void setDateTime(long j) {
        this.mTag = 3;
        this.mDateTime = j;
    }

    public void setInteger(int i) {
        this.mTag = 1;
        this.mInteger = i;
    }

    public void setLongInteger(long j) {
        this.mTag = 2;
        this.mLongInteger = j;
    }
}

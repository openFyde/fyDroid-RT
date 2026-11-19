package org.chromium.mojo_base.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class Value extends Union {
    private byte[] mBinaryValue;
    private boolean mBoolValue;
    private DictionaryValue mDictionaryValue;
    private double mDoubleValue;
    private int mIntValue;
    private ListValue mListValue;
    private byte mNullValue;
    private String mStringValue;

    public static final class Tag {
        public static final int BinaryValue = 5;
        public static final int BoolValue = 1;
        public static final int DictionaryValue = 6;
        public static final int DoubleValue = 3;
        public static final int IntValue = 2;
        public static final int ListValue = 7;
        public static final int NullValue = 0;
        public static final int StringValue = 4;
    }

    public static final Value decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        Value value = new Value();
        switch (dataHeaderForUnion.elementsOrVersion) {
            case 0:
                value.mNullValue = decoder.readByte(i + 8);
                value.mTag = 0;
                return value;
            case 1:
                value.mBoolValue = decoder.readBoolean(i + 8, 0);
                value.mTag = 1;
                return value;
            case 2:
                value.mIntValue = decoder.readInt(i + 8);
                value.mTag = 2;
                return value;
            case 3:
                value.mDoubleValue = decoder.readDouble(i + 8);
                value.mTag = 3;
                return value;
            case 4:
                value.mStringValue = decoder.readString(i + 8, false);
                value.mTag = 4;
                return value;
            case 5:
                value.mBinaryValue = decoder.readBytes(i + 8, 0, -1);
                value.mTag = 5;
                return value;
            case 6:
                value.mDictionaryValue = DictionaryValue.decode(decoder.readPointer(i + 8, false));
                value.mTag = 6;
                return value;
            case 7:
                value.mListValue = ListValue.decode(decoder.readPointer(i + 8, false));
                value.mTag = 7;
                return value;
            default:
                return value;
        }
    }

    public static Value deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        switch (this.mTag) {
            case 0:
                encoder.encode(this.mNullValue, i + 8);
                break;
            case 1:
                encoder.encode(this.mBoolValue, i + 8, 0);
                break;
            case 2:
                encoder.encode(this.mIntValue, i + 8);
                break;
            case 3:
                encoder.encode(this.mDoubleValue, i + 8);
                break;
            case 4:
                encoder.encode(this.mStringValue, i + 8, false);
                break;
            case 5:
                encoder.encode(this.mBinaryValue, i + 8, 0, -1);
                break;
            case 6:
                encoder.encode((Struct) this.mDictionaryValue, i + 8, false);
                break;
            case 7:
                encoder.encode((Struct) this.mListValue, i + 8, false);
                break;
        }
    }

    public byte[] getBinaryValue() {
        return this.mBinaryValue;
    }

    public boolean getBoolValue() {
        return this.mBoolValue;
    }

    public DictionaryValue getDictionaryValue() {
        return this.mDictionaryValue;
    }

    public double getDoubleValue() {
        return this.mDoubleValue;
    }

    public int getIntValue() {
        return this.mIntValue;
    }

    public ListValue getListValue() {
        return this.mListValue;
    }

    public byte getNullValue() {
        return this.mNullValue;
    }

    public String getStringValue() {
        return this.mStringValue;
    }

    public void setBinaryValue(byte[] bArr) {
        this.mTag = 5;
        this.mBinaryValue = bArr;
    }

    public void setBoolValue(boolean z) {
        this.mTag = 1;
        this.mBoolValue = z;
    }

    public void setDictionaryValue(DictionaryValue dictionaryValue) {
        this.mTag = 6;
        this.mDictionaryValue = dictionaryValue;
    }

    public void setDoubleValue(double d) {
        this.mTag = 3;
        this.mDoubleValue = d;
    }

    public void setIntValue(int i) {
        this.mTag = 2;
        this.mIntValue = i;
    }

    public void setListValue(ListValue listValue) {
        this.mTag = 7;
        this.mListValue = listValue;
    }

    public void setNullValue(byte b) {
        this.mTag = 0;
        this.mNullValue = b;
    }

    public void setStringValue(String str) {
        this.mTag = 4;
        this.mStringValue = str;
    }
}

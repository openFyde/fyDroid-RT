package org.chromium.mojo_base.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class ListValue extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Value[] storage;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ListValue() {
        super(16, 0);
    }

    public static ListValue decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ListValue listValue = new ListValue(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            listValue.storage = new Value[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                listValue.storage[i] = Value.decode(pointer, (i * 16) + 8);
            }
            return listValue;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ListValue deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        Value[] valueArr = this.storage;
        if (valueArr == null) {
            encoderAtDataOffset.encodeNullPointer(8, false);
            return;
        }
        Encoder encoderEncodeUnionArray = encoderAtDataOffset.encodeUnionArray(valueArr.length, 8, -1);
        int i = 0;
        while (true) {
            Value[] valueArr2 = this.storage;
            if (i >= valueArr2.length) {
                return;
            }
            encoderEncodeUnionArray.encode((Union) valueArr2[i], (i * 16) + 8, false);
            i++;
        }
    }

    private ListValue(int i) {
        super(16, i);
    }

    public static ListValue deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

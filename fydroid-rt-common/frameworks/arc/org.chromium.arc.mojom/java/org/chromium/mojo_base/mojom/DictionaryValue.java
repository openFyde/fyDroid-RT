// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.mojo_base.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class DictionaryValue extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<String, Value> storage;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public DictionaryValue() {
        super(16, 0);
    }

    public static DictionaryValue decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            DictionaryValue dictionaryValue = new DictionaryValue(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            pointer.readDataHeaderForMap();
            Decoder pointer2 = pointer.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(-1);
            int i = dataHeaderForPointerArray.elementsOrVersion;
            String[] strArr = new String[i];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                strArr[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer2, false);
            }
            Decoder pointer3 = pointer.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer3.readDataHeaderForPointerArray(i);
            Value[] valueArr = new Value[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                valueArr[i3] = Value.decode(pointer3, (i3 * 16) + 8);
            }
            dictionaryValue.storage = new HashMap();
            for (int i4 = 0; i4 < i; i4++) {
                dictionaryValue.storage.put(strArr[i4], valueArr[i4]);
            }
            return dictionaryValue;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static DictionaryValue deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        if (this.storage == null) {
            encoderAtDataOffset.encodeNullPointer(8, false);
            return;
        }
        Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(8);
        int size = this.storage.size();
        String[] strArr = new String[size];
        Value[] valueArr = new Value[size];
        int i = 0;
        for (Map.Entry<String, Value> entry : this.storage.entrySet()) {
            strArr[i] = entry.getKey();
            valueArr[i] = entry.getValue();
            i++;
        }
        Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 8, -1);
        for (int iOutline0 = 0; iOutline0 < size; iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr[iOutline0], false, iOutline0, 1)) {
        }
        Encoder encoderEncodeUnionArray = encoderEncoderForMap.encodeUnionArray(size, 16, -1);
        for (int i2 = 0; i2 < size; i2++) {
            encoderEncodeUnionArray.encode((Union) valueArr[i2], (i2 * 16) + 8, false);
        }
    }

    private DictionaryValue(int i) {
        super(16, i);
    }

    public static DictionaryValue deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

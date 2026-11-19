// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UpdateResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int error;
    public int inputConsumed;
    public KeyParameter[] outParams;
    public byte[] output;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UpdateResult() {
        super(32, 0);
    }

    public static UpdateResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UpdateResult updateResult = new UpdateResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            updateResult.inputConsumed = decoder.readInt(8);
            updateResult.error = decoder.readInt(12);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            updateResult.outParams = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                updateResult.outParams[i] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            updateResult.output = decoder.readBytes(24, 0, -1);
            return updateResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UpdateResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.inputConsumed, 8);
        encoderAtDataOffset.encode(this.error, 12);
        KeyParameter[] keyParameterArr = this.outParams;
        if (keyParameterArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 16, -1);
            int i = 0;
            while (true) {
                KeyParameter[] keyParameterArr2 = this.outParams;
                if (i >= keyParameterArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode(this.output, 24, 0, -1);
    }

    private UpdateResult(int i) {
        super(32, i);
    }

    public static UpdateResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

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

public final class AttestKeyRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public KeyParameter[] attestParams;
    public byte[] keyToAttest;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AttestKeyRequest() {
        super(24, 0);
    }

    public static AttestKeyRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AttestKeyRequest attestKeyRequest = new AttestKeyRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            attestKeyRequest.keyToAttest = decoder.readBytes(8, 0, -1);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            attestKeyRequest.attestParams = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                attestKeyRequest.attestParams[i] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            return attestKeyRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AttestKeyRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyToAttest, 8, 0, -1);
        KeyParameter[] keyParameterArr = this.attestParams;
        if (keyParameterArr == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 16, -1);
        int i = 0;
        while (true) {
            KeyParameter[] keyParameterArr2 = this.attestParams;
            if (i >= keyParameterArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private AttestKeyRequest(int i) {
        super(24, i);
    }

    public static AttestKeyRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

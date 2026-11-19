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

public final class UpgradeKeyRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] keyBlobToUpgrade;
    public KeyParameter[] upgradeParams;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UpgradeKeyRequest() {
        super(24, 0);
    }

    public static UpgradeKeyRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UpgradeKeyRequest upgradeKeyRequest = new UpgradeKeyRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            upgradeKeyRequest.keyBlobToUpgrade = decoder.readBytes(8, 0, -1);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            upgradeKeyRequest.upgradeParams = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                upgradeKeyRequest.upgradeParams[i] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            return upgradeKeyRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UpgradeKeyRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyBlobToUpgrade, 8, 0, -1);
        KeyParameter[] keyParameterArr = this.upgradeParams;
        if (keyParameterArr == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 16, -1);
        int i = 0;
        while (true) {
            KeyParameter[] keyParameterArr2 = this.upgradeParams;
            if (i >= keyParameterArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private UpgradeKeyRequest(int i) {
        super(24, i);
    }

    public static UpgradeKeyRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

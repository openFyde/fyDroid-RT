// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.payments.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class IsPaymentImplementedValidResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] activityNames;
    public String[] serviceNames;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public IsPaymentImplementedValidResult() {
        super(24, 0);
    }

    public static IsPaymentImplementedValidResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            IsPaymentImplementedValidResult isPaymentImplementedValidResult = new IsPaymentImplementedValidResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            isPaymentImplementedValidResult.activityNames = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                isPaymentImplementedValidResult.activityNames[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
            }
            Decoder pointer2 = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            isPaymentImplementedValidResult.serviceNames = new String[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray2.elementsOrVersion; i2++) {
                isPaymentImplementedValidResult.serviceNames[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer2, false);
            }
            return isPaymentImplementedValidResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static IsPaymentImplementedValidResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        String[] strArr = this.activityNames;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.activityNames;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        String[] strArr3 = this.serviceNames;
        if (strArr3 == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
            return;
        }
        Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr3.length, 16, -1);
        int iOutline02 = 0;
        while (true) {
            String[] strArr4 = this.serviceNames;
            if (iOutline02 >= strArr4.length) {
                return;
            }
            iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr4[iOutline02], false, iOutline02, 1);
        }
    }

    private IsPaymentImplementedValidResult(int i) {
        super(24, i);
    }

    public static IsPaymentImplementedValidResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.payments.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PaymentAddress extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] addressLine;
    public String city;
    public String country;
    public String dependentLocality;
    public String organization;
    public String phone;
    public String postalCode;
    public String recipient;
    public String region;
    public String sortingCode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(88, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PaymentAddress() {
        super(88, 0);
    }

    public static PaymentAddress decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PaymentAddress paymentAddress = new PaymentAddress(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            paymentAddress.country = decoder.readString(8, false);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            paymentAddress.addressLine = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                paymentAddress.addressLine[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
            }
            paymentAddress.region = decoder.readString(24, false);
            paymentAddress.city = decoder.readString(32, false);
            paymentAddress.dependentLocality = decoder.readString(40, false);
            paymentAddress.postalCode = decoder.readString(48, false);
            paymentAddress.sortingCode = decoder.readString(56, false);
            paymentAddress.organization = decoder.readString(64, false);
            paymentAddress.recipient = decoder.readString(72, false);
            paymentAddress.phone = decoder.readString(80, false);
            return paymentAddress;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PaymentAddress deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.country, 8, false);
        String[] strArr = this.addressLine;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.addressLine;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode(this.region, 24, false);
        encoderAtDataOffset.encode(this.city, 32, false);
        encoderAtDataOffset.encode(this.dependentLocality, 40, false);
        encoderAtDataOffset.encode(this.postalCode, 48, false);
        encoderAtDataOffset.encode(this.sortingCode, 56, false);
        encoderAtDataOffset.encode(this.organization, 64, false);
        encoderAtDataOffset.encode(this.recipient, 72, false);
        encoderAtDataOffset.encode(this.phone, 80, false);
    }

    private PaymentAddress(int i) {
        super(88, i);
    }

    public static PaymentAddress deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

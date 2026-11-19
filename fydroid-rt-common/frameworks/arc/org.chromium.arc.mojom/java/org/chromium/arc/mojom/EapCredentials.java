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

public final class EapCredentials extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String anonymousIdentity;
    public String[] caCertificatePem;
    public String clientCertificateKey;
    public String[] clientCertificatePem;
    public String[] domainSuffixMatchList;
    public String identity;
    public int keyManagement;
    public int method;
    public String password;
    public int phase2Method;
    public String[] subjectAlternativeNameMatchList;
    public String subjectMatch;
    public String tlsVersionMax;
    public boolean useLoginPassword;
    public boolean useProactiveKeyCaching;
    public boolean useSystemCas;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(104, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public EapCredentials() {
        super(104, 0);
    }

    public static EapCredentials decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            EapCredentials eapCredentials = new EapCredentials(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            eapCredentials.method = i;
            EapMethod.validate(i);
            int i2 = decoder.readInt(12);
            eapCredentials.phase2Method = i2;
            EapPhase2Method.validate(i2);
            eapCredentials.anonymousIdentity = decoder.readString(16, true);
            eapCredentials.identity = decoder.readString(24, true);
            eapCredentials.password = decoder.readString(32, true);
            int i3 = decoder.readInt(40);
            eapCredentials.keyManagement = i3;
            KeyManagement.validate(i3);
            eapCredentials.useSystemCas = decoder.readBoolean(44, 0);
            eapCredentials.useProactiveKeyCaching = decoder.readBoolean(44, 1);
            eapCredentials.useLoginPassword = decoder.readBoolean(44, 2);
            Decoder pointer = decoder.readPointer(48, true);
            if (pointer == null) {
                eapCredentials.caCertificatePem = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                eapCredentials.caCertificatePem = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i4 = 0; i4 < dataHeaderForPointerArray.elementsOrVersion; i4++) {
                    eapCredentials.caCertificatePem[i4] = GeneratedOutlineSupport.outline1(i4, 8, 8, pointer, false);
                }
            }
            Decoder pointer2 = decoder.readPointer(56, true);
            if (pointer2 == null) {
                eapCredentials.clientCertificatePem = null;
            } else {
                DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                eapCredentials.clientCertificatePem = new String[dataHeaderForPointerArray2.elementsOrVersion];
                for (int i5 = 0; i5 < dataHeaderForPointerArray2.elementsOrVersion; i5++) {
                    eapCredentials.clientCertificatePem[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer2, false);
                }
            }
            eapCredentials.clientCertificateKey = decoder.readString(64, true);
            eapCredentials.subjectMatch = decoder.readString(72, true);
            Decoder pointer3 = decoder.readPointer(80, true);
            if (pointer3 == null) {
                eapCredentials.subjectAlternativeNameMatchList = null;
            } else {
                DataHeader dataHeaderForPointerArray3 = pointer3.readDataHeaderForPointerArray(-1);
                eapCredentials.subjectAlternativeNameMatchList = new String[dataHeaderForPointerArray3.elementsOrVersion];
                for (int i6 = 0; i6 < dataHeaderForPointerArray3.elementsOrVersion; i6++) {
                    eapCredentials.subjectAlternativeNameMatchList[i6] = GeneratedOutlineSupport.outline1(i6, 8, 8, pointer3, false);
                }
            }
            Decoder pointer4 = decoder.readPointer(88, true);
            if (pointer4 == null) {
                eapCredentials.domainSuffixMatchList = null;
            } else {
                DataHeader dataHeaderForPointerArray4 = pointer4.readDataHeaderForPointerArray(-1);
                eapCredentials.domainSuffixMatchList = new String[dataHeaderForPointerArray4.elementsOrVersion];
                for (int i7 = 0; i7 < dataHeaderForPointerArray4.elementsOrVersion; i7++) {
                    eapCredentials.domainSuffixMatchList[i7] = GeneratedOutlineSupport.outline1(i7, 8, 8, pointer4, false);
                }
            }
            eapCredentials.tlsVersionMax = decoder.readString(96, true);
            return eapCredentials;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static EapCredentials deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.method, 8);
        encoderAtDataOffset.encode(this.phase2Method, 12);
        encoderAtDataOffset.encode(this.anonymousIdentity, 16, true);
        encoderAtDataOffset.encode(this.identity, 24, true);
        encoderAtDataOffset.encode(this.password, 32, true);
        encoderAtDataOffset.encode(this.keyManagement, 40);
        encoderAtDataOffset.encode(this.useSystemCas, 44, 0);
        encoderAtDataOffset.encode(this.useProactiveKeyCaching, 44, 1);
        encoderAtDataOffset.encode(this.useLoginPassword, 44, 2);
        String[] strArr = this.caCertificatePem;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 48, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.caCertificatePem;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(48, true);
        }
        String[] strArr3 = this.clientCertificatePem;
        if (strArr3 != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr3.length, 56, -1);
            int iOutline02 = 0;
            while (true) {
                String[] strArr4 = this.clientCertificatePem;
                if (iOutline02 >= strArr4.length) {
                    break;
                }
                iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr4[iOutline02], false, iOutline02, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(56, true);
        }
        encoderAtDataOffset.encode(this.clientCertificateKey, 64, true);
        encoderAtDataOffset.encode(this.subjectMatch, 72, true);
        String[] strArr5 = this.subjectAlternativeNameMatchList;
        if (strArr5 != null) {
            Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(strArr5.length, 80, -1);
            int iOutline03 = 0;
            while (true) {
                String[] strArr6 = this.subjectAlternativeNameMatchList;
                if (iOutline03 >= strArr6.length) {
                    break;
                }
                iOutline03 = GeneratedOutlineSupport.outline0(iOutline03, 8, 8, encoderEncodePointerArray3, strArr6[iOutline03], false, iOutline03, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(80, true);
        }
        String[] strArr7 = this.domainSuffixMatchList;
        if (strArr7 != null) {
            Encoder encoderEncodePointerArray4 = encoderAtDataOffset.encodePointerArray(strArr7.length, 88, -1);
            int iOutline04 = 0;
            while (true) {
                String[] strArr8 = this.domainSuffixMatchList;
                if (iOutline04 >= strArr8.length) {
                    break;
                }
                iOutline04 = GeneratedOutlineSupport.outline0(iOutline04, 8, 8, encoderEncodePointerArray4, strArr8[iOutline04], false, iOutline04, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(88, true);
        }
        encoderAtDataOffset.encode(this.tlsVersionMax, 96, true);
    }

    private EapCredentials(int i) {
        super(104, i);
    }

    public static EapCredentials deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

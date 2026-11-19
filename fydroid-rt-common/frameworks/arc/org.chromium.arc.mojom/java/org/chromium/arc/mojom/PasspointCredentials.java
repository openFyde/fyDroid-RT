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

public final class PasspointCredentials extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] domains;
    public EapCredentials eap;
    public String friendlyName;
    public long[] homeOis;
    public boolean metered;
    public String packageName;
    public String realm;
    public long[] requiredHomeOis;
    public long[] roamingConsortiumOis;
    public long subscriptionExpirationTimeMs;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(72, 0), new DataHeader(88, 22)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public PasspointCredentials() {
        super(88, 22);
    }

    public static PasspointCredentials decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            PasspointCredentials passpointCredentials = new PasspointCredentials(i);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            passpointCredentials.domains = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                passpointCredentials.domains[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
            }
            passpointCredentials.realm = decoder.readString(16, false);
            passpointCredentials.homeOis = decoder.readLongs(24, 0, -1);
            passpointCredentials.requiredHomeOis = decoder.readLongs(32, 0, -1);
            passpointCredentials.roamingConsortiumOis = decoder.readLongs(40, 0, -1);
            passpointCredentials.eap = EapCredentials.decode(decoder.readPointer(48, false));
            passpointCredentials.metered = decoder.readBoolean(56, 0);
            passpointCredentials.packageName = decoder.readString(64, false);
            if (i >= 22) {
                passpointCredentials.friendlyName = decoder.readString(72, true);
                passpointCredentials.subscriptionExpirationTimeMs = decoder.readLong(80);
            }
            return passpointCredentials;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PasspointCredentials deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        String[] strArr = this.domains;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.domains;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        encoderAtDataOffset.encode(this.realm, 16, false);
        encoderAtDataOffset.encode(this.homeOis, 24, 0, -1);
        encoderAtDataOffset.encode(this.requiredHomeOis, 32, 0, -1);
        encoderAtDataOffset.encode(this.roamingConsortiumOis, 40, 0, -1);
        encoderAtDataOffset.encode((Struct) this.eap, 48, false);
        encoderAtDataOffset.encode(this.metered, 56, 0);
        encoderAtDataOffset.encode(this.packageName, 64, false);
        encoderAtDataOffset.encode(this.friendlyName, 72, true);
        encoderAtDataOffset.encode(this.subscriptionExpirationTimeMs, 80);
    }

    private PasspointCredentials(int i) {
        super(88, i);
    }

    public static PasspointCredentials deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

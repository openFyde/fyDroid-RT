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

public final class ManualProxyConfig extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] exclusionList;
    public String host;
    public int port;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ManualProxyConfig() {
        super(32, 0);
    }

    public static ManualProxyConfig decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ManualProxyConfig manualProxyConfig = new ManualProxyConfig(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            manualProxyConfig.host = decoder.readString(8, false);
            manualProxyConfig.port = decoder.readInt(16);
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            manualProxyConfig.exclusionList = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                manualProxyConfig.exclusionList[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
            }
            return manualProxyConfig;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ManualProxyConfig deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.host, 8, false);
        encoderAtDataOffset.encode(this.port, 16);
        String[] strArr = this.exclusionList;
        if (strArr == null) {
            encoderAtDataOffset.encodeNullPointer(24, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 24, -1);
        int iOutline0 = 0;
        while (true) {
            String[] strArr2 = this.exclusionList;
            if (iOutline0 >= strArr2.length) {
                return;
            }
            iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
        }
    }

    private ManualProxyConfig(int i) {
        super(32, i);
    }

    public static ManualProxyConfig deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

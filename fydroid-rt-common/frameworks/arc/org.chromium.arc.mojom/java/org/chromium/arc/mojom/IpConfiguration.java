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

public final class IpConfiguration extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String gateway;
    public String ipAddress;
    public String[] nameServers;
    public int routingPrefix;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public IpConfiguration() {
        super(40, 0);
    }

    public static IpConfiguration decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            IpConfiguration ipConfiguration = new IpConfiguration(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            ipConfiguration.gateway = decoder.readString(8, false);
            ipConfiguration.ipAddress = decoder.readString(16, false);
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            ipConfiguration.nameServers = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                ipConfiguration.nameServers[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
            }
            ipConfiguration.routingPrefix = decoder.readInt(32);
            int i2 = decoder.readInt(36);
            ipConfiguration.type = i2;
            IpAddressType.validate(i2);
            return ipConfiguration;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static IpConfiguration deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.gateway, 8, false);
        encoderAtDataOffset.encode(this.ipAddress, 16, false);
        String[] strArr = this.nameServers;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 24, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.nameServers;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, false);
        }
        encoderAtDataOffset.encode(this.routingPrefix, 32);
        encoderAtDataOffset.encode(this.type, 36);
    }

    private IpConfiguration(int i) {
        super(40, i);
    }

    public static IpConfiguration deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

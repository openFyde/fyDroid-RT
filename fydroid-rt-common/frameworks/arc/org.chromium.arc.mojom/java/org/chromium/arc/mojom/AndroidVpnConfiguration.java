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
import org.chromium.mojo.bindings.Union;

public final class AndroidVpnConfiguration extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String appLabel;
    public String appName;
    public String[] domains;
    public ArcProxyInfo httpProxy;
    public String ipv4Gateway;
    public int mtu;
    public String[] nameservers;
    public String sessionName;
    public String[] splitExclude;
    public String[] splitInclude;
    public boolean tunnelChromeTraffic;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(80, 0), new DataHeader(96, 21), new DataHeader(96, 38)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public AndroidVpnConfiguration() {
        super(96, 38);
    }

    public static AndroidVpnConfiguration decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AndroidVpnConfiguration androidVpnConfiguration = new AndroidVpnConfiguration(i);
            androidVpnConfiguration.appName = decoder.readString(8, false);
            androidVpnConfiguration.appLabel = decoder.readString(16, false);
            androidVpnConfiguration.sessionName = decoder.readString(24, false);
            androidVpnConfiguration.tunnelChromeTraffic = decoder.readBoolean(32, 0);
            if (i >= 38) {
                androidVpnConfiguration.mtu = decoder.readInt(36);
            }
            androidVpnConfiguration.ipv4Gateway = decoder.readString(40, false);
            Decoder pointer = decoder.readPointer(48, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            androidVpnConfiguration.splitInclude = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                androidVpnConfiguration.splitInclude[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
            }
            Decoder pointer2 = decoder.readPointer(56, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            androidVpnConfiguration.splitExclude = new String[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                androidVpnConfiguration.splitExclude[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer2, false);
            }
            Decoder pointer3 = decoder.readPointer(64, false);
            DataHeader dataHeaderForPointerArray3 = pointer3.readDataHeaderForPointerArray(-1);
            androidVpnConfiguration.nameservers = new String[dataHeaderForPointerArray3.elementsOrVersion];
            for (int i4 = 0; i4 < dataHeaderForPointerArray3.elementsOrVersion; i4++) {
                androidVpnConfiguration.nameservers[i4] = GeneratedOutlineSupport.outline1(i4, 8, 8, pointer3, false);
            }
            Decoder pointer4 = decoder.readPointer(72, false);
            DataHeader dataHeaderForPointerArray4 = pointer4.readDataHeaderForPointerArray(-1);
            androidVpnConfiguration.domains = new String[dataHeaderForPointerArray4.elementsOrVersion];
            for (int i5 = 0; i5 < dataHeaderForPointerArray4.elementsOrVersion; i5++) {
                androidVpnConfiguration.domains[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer4, false);
            }
            if (i >= 21) {
                androidVpnConfiguration.httpProxy = ArcProxyInfo.decode(decoder, 80);
            }
            return androidVpnConfiguration;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AndroidVpnConfiguration deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.appName, 8, false);
        encoderAtDataOffset.encode(this.appLabel, 16, false);
        encoderAtDataOffset.encode(this.sessionName, 24, false);
        encoderAtDataOffset.encode(this.tunnelChromeTraffic, 32, 0);
        encoderAtDataOffset.encode(this.mtu, 36);
        encoderAtDataOffset.encode(this.ipv4Gateway, 40, false);
        String[] strArr = this.splitInclude;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 48, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.splitInclude;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(48, false);
        }
        String[] strArr3 = this.splitExclude;
        if (strArr3 != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr3.length, 56, -1);
            int iOutline02 = 0;
            while (true) {
                String[] strArr4 = this.splitExclude;
                if (iOutline02 >= strArr4.length) {
                    break;
                }
                iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr4[iOutline02], false, iOutline02, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(56, false);
        }
        String[] strArr5 = this.nameservers;
        if (strArr5 != null) {
            Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(strArr5.length, 64, -1);
            int iOutline03 = 0;
            while (true) {
                String[] strArr6 = this.nameservers;
                if (iOutline03 >= strArr6.length) {
                    break;
                }
                iOutline03 = GeneratedOutlineSupport.outline0(iOutline03, 8, 8, encoderEncodePointerArray3, strArr6[iOutline03], false, iOutline03, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(64, false);
        }
        String[] strArr7 = this.domains;
        if (strArr7 != null) {
            Encoder encoderEncodePointerArray4 = encoderAtDataOffset.encodePointerArray(strArr7.length, 72, -1);
            int iOutline04 = 0;
            while (true) {
                String[] strArr8 = this.domains;
                if (iOutline04 >= strArr8.length) {
                    break;
                }
                iOutline04 = GeneratedOutlineSupport.outline0(iOutline04, 8, 8, encoderEncodePointerArray4, strArr8[iOutline04], false, iOutline04, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(72, false);
        }
        encoderAtDataOffset.encode((Union) this.httpProxy, 80, true);
    }

    private AndroidVpnConfiguration(int i) {
        super(96, i);
    }

    public static AndroidVpnConfiguration deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

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

public final class WebShareTargetInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String action;
    public String enctype;
    public String[][] fileAccepts;
    public String[] fileNames;
    public String method;
    public String paramText;
    public String paramTitle;
    public String paramUrl;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(72, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public WebShareTargetInfo() {
        super(72, 0);
    }

    public static WebShareTargetInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            WebShareTargetInfo webShareTargetInfo = new WebShareTargetInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            webShareTargetInfo.action = decoder.readString(8, true);
            webShareTargetInfo.method = decoder.readString(16, true);
            webShareTargetInfo.enctype = decoder.readString(24, true);
            webShareTargetInfo.paramTitle = decoder.readString(32, true);
            webShareTargetInfo.paramText = decoder.readString(40, true);
            webShareTargetInfo.paramUrl = decoder.readString(48, true);
            Decoder pointer = decoder.readPointer(56, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            webShareTargetInfo.fileNames = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                webShareTargetInfo.fileNames[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
            }
            Decoder pointer2 = decoder.readPointer(64, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            webShareTargetInfo.fileAccepts = new String[dataHeaderForPointerArray2.elementsOrVersion][];
            for (int i2 = 0; i2 < dataHeaderForPointerArray2.elementsOrVersion; i2++) {
                Decoder decoderOutline2 = GeneratedOutlineSupport.outline2(i2, 8, 8, pointer2, false);
                DataHeader dataHeaderForPointerArray3 = decoderOutline2.readDataHeaderForPointerArray(-1);
                webShareTargetInfo.fileAccepts[i2] = new String[dataHeaderForPointerArray3.elementsOrVersion];
                for (int i3 = 0; i3 < dataHeaderForPointerArray3.elementsOrVersion; i3++) {
                    webShareTargetInfo.fileAccepts[i2][i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, decoderOutline2, false);
                }
            }
            return webShareTargetInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WebShareTargetInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.action, 8, true);
        encoderAtDataOffset.encode(this.method, 16, true);
        encoderAtDataOffset.encode(this.enctype, 24, true);
        encoderAtDataOffset.encode(this.paramTitle, 32, true);
        encoderAtDataOffset.encode(this.paramText, 40, true);
        encoderAtDataOffset.encode(this.paramUrl, 48, true);
        String[] strArr = this.fileNames;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 56, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.fileNames;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(56, false);
        }
        String[][] strArr3 = this.fileAccepts;
        if (strArr3 == null) {
            encoderAtDataOffset.encodeNullPointer(64, false);
            return;
        }
        Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr3.length, 64, -1);
        int i = 0;
        while (true) {
            String[][] strArr4 = this.fileAccepts;
            if (i >= strArr4.length) {
                return;
            }
            if (strArr4[i] == null) {
                encoderEncodePointerArray2.encodeNullPointer((i * 8) + 8, false);
            } else {
                Encoder encoderEncodePointerArray3 = encoderEncodePointerArray2.encodePointerArray(strArr4[i].length, (i * 8) + 8, -1);
                int iOutline02 = 0;
                while (true) {
                    String[][] strArr5 = this.fileAccepts;
                    if (iOutline02 < strArr5[i].length) {
                        iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray3, strArr5[i][iOutline02], false, iOutline02, 1);
                    }
                }
            }
            i++;
        }
    }

    private WebShareTargetInfo(int i) {
        super(72, i);
    }

    public static WebShareTargetInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

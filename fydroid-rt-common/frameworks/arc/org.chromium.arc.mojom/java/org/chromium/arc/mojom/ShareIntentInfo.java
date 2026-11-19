// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ShareIntentInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<String, String> extras;
    public FileInfo[] files;
    public String mimeType;
    public String title;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ShareIntentInfo() {
        super(40, 0);
    }

    public static ShareIntentInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ShareIntentInfo shareIntentInfo = new ShareIntentInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            shareIntentInfo.title = decoder.readString(8, false);
            shareIntentInfo.mimeType = decoder.readString(16, false);
            Decoder pointer = decoder.readPointer(24, true);
            if (pointer == null) {
                shareIntentInfo.extras = null;
            } else {
                pointer.readDataHeaderForMap();
                Decoder pointer2 = pointer.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(-1);
                int i = dataHeaderForPointerArray.elementsOrVersion;
                String[] strArr = new String[i];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    strArr[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer2, false);
                }
                Decoder pointer3 = pointer.readPointer(16, false);
                DataHeader dataHeaderForPointerArray2 = pointer3.readDataHeaderForPointerArray(i);
                String[] strArr2 = new String[dataHeaderForPointerArray2.elementsOrVersion];
                for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                    strArr2[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer3, false);
                }
                shareIntentInfo.extras = new HashMap();
                for (int i4 = 0; i4 < i; i4++) {
                    shareIntentInfo.extras.put(strArr[i4], strArr2[i4]);
                }
            }
            Decoder pointer4 = decoder.readPointer(32, true);
            if (pointer4 == null) {
                shareIntentInfo.files = null;
            } else {
                DataHeader dataHeaderForPointerArray3 = pointer4.readDataHeaderForPointerArray(-1);
                shareIntentInfo.files = new FileInfo[dataHeaderForPointerArray3.elementsOrVersion];
                for (int i5 = 0; i5 < dataHeaderForPointerArray3.elementsOrVersion; i5++) {
                    shareIntentInfo.files[i5] = FileInfo.decode(GeneratedOutlineSupport.outline2(i5, 8, 8, pointer4, false));
                }
            }
            return shareIntentInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ShareIntentInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.title, 8, false);
        encoderAtDataOffset.encode(this.mimeType, 16, false);
        if (this.extras == null) {
            encoderAtDataOffset.encodeNullPointer(24, true);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(24);
            int size = this.extras.size();
            String[] strArr = new String[size];
            String[] strArr2 = new String[size];
            int i = 0;
            for (Map.Entry<String, String> entry : this.extras.entrySet()) {
                strArr[i] = entry.getKey();
                strArr2[i] = entry.getValue();
                i++;
            }
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 8, -1);
            int iOutline0 = 0;
            while (iOutline0 < size) {
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr[iOutline0], false, iOutline0, 1);
                size = size;
                strArr2 = strArr2;
                strArr = strArr;
            }
            String[] strArr3 = strArr2;
            int i2 = size;
            Encoder encoderEncodePointerArray2 = encoderEncoderForMap.encodePointerArray(i2, 16, -1);
            for (int iOutline02 = 0; iOutline02 < i2; iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr3[iOutline02], false, iOutline02, 1)) {
            }
        }
        FileInfo[] fileInfoArr = this.files;
        if (fileInfoArr == null) {
            encoderAtDataOffset.encodeNullPointer(32, true);
            return;
        }
        Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(fileInfoArr.length, 32, -1);
        int i3 = 0;
        while (true) {
            FileInfo[] fileInfoArr2 = this.files;
            if (i3 >= fileInfoArr2.length) {
                return;
            }
            encoderEncodePointerArray3.encode((Struct) fileInfoArr2[i3], (i3 * 8) + 8, false);
            i3++;
        }
    }

    private ShareIntentInfo(int i) {
        super(40, i);
    }

    public static ShareIntentInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

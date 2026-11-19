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

public final class OpenUrlsRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int actionType;
    public ActivityName activityName;
    public Map<String, String> extras;
    public ContentUrlWithMimeType[] urls;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 17)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public OpenUrlsRequest() {
        super(40, 17);
    }

    public static OpenUrlsRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            OpenUrlsRequest openUrlsRequest = new OpenUrlsRequest(i);
            int i2 = decoder.readInt(8);
            openUrlsRequest.actionType = i2;
            ActionType.validate(i2);
            openUrlsRequest.activityName = ActivityName.decode(decoder.readPointer(16, false));
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            openUrlsRequest.urls = new ContentUrlWithMimeType[dataHeaderForPointerArray.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                openUrlsRequest.urls[i3] = ContentUrlWithMimeType.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer, false));
            }
            if (i >= 17) {
                Decoder pointer2 = decoder.readPointer(32, true);
                if (pointer2 == null) {
                    openUrlsRequest.extras = null;
                } else {
                    pointer2.readDataHeaderForMap();
                    Decoder pointer3 = pointer2.readPointer(8, false);
                    DataHeader dataHeaderForPointerArray2 = pointer3.readDataHeaderForPointerArray(-1);
                    int i4 = dataHeaderForPointerArray2.elementsOrVersion;
                    String[] strArr = new String[i4];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray2.elementsOrVersion; i5++) {
                        strArr[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer3, false);
                    }
                    Decoder pointer4 = pointer2.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray3 = pointer4.readDataHeaderForPointerArray(i4);
                    String[] strArr2 = new String[dataHeaderForPointerArray3.elementsOrVersion];
                    for (int i6 = 0; i6 < dataHeaderForPointerArray3.elementsOrVersion; i6++) {
                        strArr2[i6] = GeneratedOutlineSupport.outline1(i6, 8, 8, pointer4, false);
                    }
                    openUrlsRequest.extras = new HashMap();
                    for (int i7 = 0; i7 < i4; i7++) {
                        openUrlsRequest.extras.put(strArr[i7], strArr2[i7]);
                    }
                }
            }
            return openUrlsRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OpenUrlsRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.actionType, 8);
        encoderAtDataOffset.encode((Struct) this.activityName, 16, false);
        ContentUrlWithMimeType[] contentUrlWithMimeTypeArr = this.urls;
        if (contentUrlWithMimeTypeArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(contentUrlWithMimeTypeArr.length, 24, -1);
            int i = 0;
            while (true) {
                ContentUrlWithMimeType[] contentUrlWithMimeTypeArr2 = this.urls;
                if (i >= contentUrlWithMimeTypeArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) contentUrlWithMimeTypeArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, false);
        }
        if (this.extras == null) {
            encoderAtDataOffset.encodeNullPointer(32, true);
            return;
        }
        Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(32);
        int size = this.extras.size();
        String[] strArr = new String[size];
        String[] strArr2 = new String[size];
        int i2 = 0;
        for (Map.Entry<String, String> entry : this.extras.entrySet()) {
            strArr[i2] = entry.getKey();
            strArr2[i2] = entry.getValue();
            i2++;
        }
        Encoder encoderEncodePointerArray2 = encoderEncoderForMap.encodePointerArray(size, 8, -1);
        int iOutline0 = 0;
        while (iOutline0 < size) {
            iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray2, strArr[iOutline0], false, iOutline0, 1);
            strArr2 = strArr2;
        }
        String[] strArr3 = strArr2;
        Encoder encoderEncodePointerArray3 = encoderEncoderForMap.encodePointerArray(size, 16, -1);
        for (int iOutline02 = 0; iOutline02 < size; iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray3, strArr3[iOutline02], false, iOutline02, 1)) {
        }
    }

    private OpenUrlsRequest(int i) {
        super(40, i);
    }

    public static OpenUrlsRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

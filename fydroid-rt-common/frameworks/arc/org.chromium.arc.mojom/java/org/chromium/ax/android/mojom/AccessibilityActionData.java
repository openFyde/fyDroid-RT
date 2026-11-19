// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

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

public final class AccessibilityActionData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int actionType;
    public int customActionId;
    public int endIndex;
    public Map<Integer, Float> floatParameters;
    public Map<Integer, Integer> intParameters;
    public int nodeId;
    public int startIndex;
    public Map<Integer, String> stringParameters;
    public int windowId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(24, 6), new DataHeader(32, 13), new DataHeader(56, 25)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[3];
    }

    public AccessibilityActionData() {
        super(56, 25);
    }

    public static AccessibilityActionData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AccessibilityActionData accessibilityActionData = new AccessibilityActionData(i);
            accessibilityActionData.nodeId = decoder.readInt(8);
            int i2 = decoder.readInt(12);
            accessibilityActionData.actionType = i2;
            AccessibilityActionType.validate(i2);
            accessibilityActionData.customActionId = decoder.readInt(16);
            if (i >= 6) {
                accessibilityActionData.windowId = decoder.readInt(20);
            }
            if (i >= 13) {
                accessibilityActionData.startIndex = decoder.readInt(24);
                accessibilityActionData.endIndex = decoder.readInt(28);
            }
            if (i >= 25) {
                Decoder pointer = decoder.readPointer(32, true);
                if (pointer == null) {
                    accessibilityActionData.intParameters = null;
                } else {
                    pointer.readDataHeaderForMap();
                    int[] ints = pointer.readInts(8, 0, -1);
                    for (int i3 : ints) {
                        ActionIntArgumentType.validate(i3);
                    }
                    int[] ints2 = pointer.readInts(16, 0, ints.length);
                    accessibilityActionData.intParameters = new HashMap();
                    for (int i4 = 0; i4 < ints.length; i4++) {
                        accessibilityActionData.intParameters.put(Integer.valueOf(ints[i4]), Integer.valueOf(ints2[i4]));
                    }
                }
                Decoder pointer2 = decoder.readPointer(40, true);
                if (pointer2 == null) {
                    accessibilityActionData.stringParameters = null;
                } else {
                    pointer2.readDataHeaderForMap();
                    int[] ints3 = pointer2.readInts(8, 0, -1);
                    for (int i5 : ints3) {
                        ActionStringArgumentType.validate(i5);
                    }
                    Decoder pointer3 = pointer2.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray = pointer3.readDataHeaderForPointerArray(ints3.length);
                    String[] strArr = new String[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i6 = 0; i6 < dataHeaderForPointerArray.elementsOrVersion; i6++) {
                        strArr[i6] = GeneratedOutlineSupport.outline1(i6, 8, 8, pointer3, false);
                    }
                    accessibilityActionData.stringParameters = new HashMap();
                    for (int i7 = 0; i7 < ints3.length; i7++) {
                        accessibilityActionData.stringParameters.put(Integer.valueOf(ints3[i7]), strArr[i7]);
                    }
                }
                Decoder pointer4 = decoder.readPointer(48, true);
                if (pointer4 == null) {
                    accessibilityActionData.floatParameters = null;
                } else {
                    pointer4.readDataHeaderForMap();
                    int[] ints4 = pointer4.readInts(8, 0, -1);
                    for (int i8 : ints4) {
                        ActionFloatArgumentType.validate(i8);
                    }
                    float[] floats = pointer4.readFloats(16, 0, ints4.length);
                    accessibilityActionData.floatParameters = new HashMap();
                    for (int i9 = 0; i9 < ints4.length; i9++) {
                        accessibilityActionData.floatParameters.put(Integer.valueOf(ints4[i9]), Float.valueOf(floats[i9]));
                    }
                }
            }
            return accessibilityActionData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityActionData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.nodeId, 8);
        encoderAtDataOffset.encode(this.actionType, 12);
        encoderAtDataOffset.encode(this.customActionId, 16);
        encoderAtDataOffset.encode(this.windowId, 20);
        encoderAtDataOffset.encode(this.startIndex, 24);
        encoderAtDataOffset.encode(this.endIndex, 28);
        if (this.intParameters == null) {
            encoderAtDataOffset.encodeNullPointer(32, true);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(32);
            int size = this.intParameters.size();
            int[] iArr = new int[size];
            int[] iArr2 = new int[size];
            int i = 0;
            for (Map.Entry<Integer, Integer> entry : this.intParameters.entrySet()) {
                iArr[i] = entry.getKey().intValue();
                iArr2[i] = entry.getValue().intValue();
                i++;
            }
            encoderEncoderForMap.encode(iArr, 8, 0, -1);
            encoderEncoderForMap.encode(iArr2, 16, 0, -1);
        }
        if (this.stringParameters == null) {
            encoderAtDataOffset.encodeNullPointer(40, true);
        } else {
            Encoder encoderEncoderForMap2 = encoderAtDataOffset.encoderForMap(40);
            int size2 = this.stringParameters.size();
            int[] iArr3 = new int[size2];
            String[] strArr = new String[size2];
            int i2 = 0;
            for (Map.Entry<Integer, String> entry2 : this.stringParameters.entrySet()) {
                iArr3[i2] = entry2.getKey().intValue();
                strArr[i2] = entry2.getValue();
                i2++;
            }
            encoderEncoderForMap2.encode(iArr3, 8, 0, -1);
            Encoder encoderEncodePointerArray = encoderEncoderForMap2.encodePointerArray(size2, 16, -1);
            int iOutline0 = 0;
            while (iOutline0 < size2) {
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr[iOutline0], false, iOutline0, 1);
                size2 = size2;
                strArr = strArr;
            }
        }
        if (this.floatParameters == null) {
            encoderAtDataOffset.encodeNullPointer(48, true);
            return;
        }
        Encoder encoderEncoderForMap3 = encoderAtDataOffset.encoderForMap(48);
        int size3 = this.floatParameters.size();
        int[] iArr4 = new int[size3];
        float[] fArr = new float[size3];
        int i3 = 0;
        for (Map.Entry<Integer, Float> entry3 : this.floatParameters.entrySet()) {
            iArr4[i3] = entry3.getKey().intValue();
            fArr[i3] = entry3.getValue().floatValue();
            i3++;
        }
        encoderEncoderForMap3.encode(iArr4, 8, 0, -1);
        encoderEncoderForMap3.encode(fArr, 16, 0, -1);
    }

    private AccessibilityActionData(int i) {
        super(56, i);
    }

    public static AccessibilityActionData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

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

public final class AccessibilityWindowInfoData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<Integer, Boolean> booleanProperties;
    public Rect boundsInScreen;
    public Map<Integer, int[]> intListProperties;
    public Map<Integer, Integer> intProperties;
    public int rootNodeId;
    public Map<Integer, String> stringProperties;
    public int windowId;
    public int windowType;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(64, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AccessibilityWindowInfoData() {
        super(64, 0);
    }

    public static AccessibilityWindowInfoData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AccessibilityWindowInfoData accessibilityWindowInfoData = new AccessibilityWindowInfoData(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            accessibilityWindowInfoData.windowId = decoder.readInt(8);
            accessibilityWindowInfoData.rootNodeId = decoder.readInt(12);
            accessibilityWindowInfoData.boundsInScreen = Rect.decode(decoder.readPointer(16, false));
            int i = decoder.readInt(24);
            accessibilityWindowInfoData.windowType = i;
            AccessibilityWindowType.validate(i);
            Decoder pointer = decoder.readPointer(32, true);
            if (pointer == null) {
                accessibilityWindowInfoData.booleanProperties = null;
            } else {
                pointer.readDataHeaderForMap();
                int[] ints = pointer.readInts(8, 0, -1);
                for (int i2 : ints) {
                    AccessibilityWindowBooleanProperty.validate(i2);
                }
                boolean[] booleans = pointer.readBooleans(16, 0, ints.length);
                accessibilityWindowInfoData.booleanProperties = new HashMap();
                for (int i3 = 0; i3 < ints.length; i3++) {
                    accessibilityWindowInfoData.booleanProperties.put(Integer.valueOf(ints[i3]), Boolean.valueOf(booleans[i3]));
                }
            }
            Decoder pointer2 = decoder.readPointer(40, true);
            if (pointer2 == null) {
                accessibilityWindowInfoData.stringProperties = null;
            } else {
                pointer2.readDataHeaderForMap();
                int[] ints2 = pointer2.readInts(8, 0, -1);
                for (int i4 : ints2) {
                    AccessibilityWindowStringProperty.validate(i4);
                }
                Decoder pointer3 = pointer2.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer3.readDataHeaderForPointerArray(ints2.length);
                String[] strArr = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i5 = 0; i5 < dataHeaderForPointerArray.elementsOrVersion; i5++) {
                    strArr[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer3, false);
                }
                accessibilityWindowInfoData.stringProperties = new HashMap();
                for (int i6 = 0; i6 < ints2.length; i6++) {
                    accessibilityWindowInfoData.stringProperties.put(Integer.valueOf(ints2[i6]), strArr[i6]);
                }
            }
            Decoder pointer4 = decoder.readPointer(48, true);
            if (pointer4 == null) {
                accessibilityWindowInfoData.intProperties = null;
            } else {
                pointer4.readDataHeaderForMap();
                int[] ints3 = pointer4.readInts(8, 0, -1);
                for (int i7 : ints3) {
                    AccessibilityWindowIntProperty.validate(i7);
                }
                int[] ints4 = pointer4.readInts(16, 0, ints3.length);
                accessibilityWindowInfoData.intProperties = new HashMap();
                for (int i8 = 0; i8 < ints3.length; i8++) {
                    accessibilityWindowInfoData.intProperties.put(Integer.valueOf(ints3[i8]), Integer.valueOf(ints4[i8]));
                }
            }
            Decoder pointer5 = decoder.readPointer(56, true);
            if (pointer5 == null) {
                accessibilityWindowInfoData.intListProperties = null;
            } else {
                pointer5.readDataHeaderForMap();
                int[] ints5 = pointer5.readInts(8, 0, -1);
                for (int i9 : ints5) {
                    AccessibilityWindowIntListProperty.validate(i9);
                }
                Decoder pointer6 = pointer5.readPointer(16, false);
                DataHeader dataHeaderForPointerArray2 = pointer6.readDataHeaderForPointerArray(ints5.length);
                int[][] iArr = new int[dataHeaderForPointerArray2.elementsOrVersion][];
                for (int i10 = 0; i10 < dataHeaderForPointerArray2.elementsOrVersion; i10++) {
                    iArr[i10] = pointer6.readInts((i10 * 8) + 8, 0, -1);
                }
                accessibilityWindowInfoData.intListProperties = new HashMap();
                for (int i11 = 0; i11 < ints5.length; i11++) {
                    accessibilityWindowInfoData.intListProperties.put(Integer.valueOf(ints5[i11]), iArr[i11]);
                }
            }
            return accessibilityWindowInfoData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityWindowInfoData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.windowId, 8);
        encoderAtDataOffset.encode(this.rootNodeId, 12);
        encoderAtDataOffset.encode((Struct) this.boundsInScreen, 16, false);
        encoderAtDataOffset.encode(this.windowType, 24);
        if (this.booleanProperties == null) {
            encoderAtDataOffset.encodeNullPointer(32, true);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(32);
            int size = this.booleanProperties.size();
            int[] iArr = new int[size];
            boolean[] zArr = new boolean[size];
            int i = 0;
            for (Map.Entry<Integer, Boolean> entry : this.booleanProperties.entrySet()) {
                iArr[i] = entry.getKey().intValue();
                zArr[i] = entry.getValue().booleanValue();
                i++;
            }
            encoderEncoderForMap.encode(iArr, 8, 0, -1);
            encoderEncoderForMap.encode(zArr, 16, 0, -1);
        }
        if (this.stringProperties == null) {
            encoderAtDataOffset.encodeNullPointer(40, true);
        } else {
            Encoder encoderEncoderForMap2 = encoderAtDataOffset.encoderForMap(40);
            int size2 = this.stringProperties.size();
            int[] iArr2 = new int[size2];
            String[] strArr = new String[size2];
            int i2 = 0;
            for (Map.Entry<Integer, String> entry2 : this.stringProperties.entrySet()) {
                iArr2[i2] = entry2.getKey().intValue();
                strArr[i2] = entry2.getValue();
                i2++;
            }
            encoderEncoderForMap2.encode(iArr2, 8, 0, -1);
            Encoder encoderEncodePointerArray = encoderEncoderForMap2.encodePointerArray(size2, 16, -1);
            int iOutline0 = 0;
            while (iOutline0 < size2) {
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr[iOutline0], false, iOutline0, 1);
                size2 = size2;
                strArr = strArr;
            }
        }
        if (this.intProperties == null) {
            encoderAtDataOffset.encodeNullPointer(48, true);
        } else {
            Encoder encoderEncoderForMap3 = encoderAtDataOffset.encoderForMap(48);
            int size3 = this.intProperties.size();
            int[] iArr3 = new int[size3];
            int[] iArr4 = new int[size3];
            int i3 = 0;
            for (Map.Entry<Integer, Integer> entry3 : this.intProperties.entrySet()) {
                iArr3[i3] = entry3.getKey().intValue();
                iArr4[i3] = entry3.getValue().intValue();
                i3++;
            }
            encoderEncoderForMap3.encode(iArr3, 8, 0, -1);
            encoderEncoderForMap3.encode(iArr4, 16, 0, -1);
        }
        if (this.intListProperties == null) {
            encoderAtDataOffset.encodeNullPointer(56, true);
            return;
        }
        Encoder encoderEncoderForMap4 = encoderAtDataOffset.encoderForMap(56);
        int size4 = this.intListProperties.size();
        int[] iArr5 = new int[size4];
        int[][] iArr6 = new int[size4][];
        int i4 = 0;
        for (Map.Entry<Integer, int[]> entry4 : this.intListProperties.entrySet()) {
            iArr5[i4] = entry4.getKey().intValue();
            iArr6[i4] = entry4.getValue();
            i4++;
        }
        encoderEncoderForMap4.encode(iArr5, 8, 0, -1);
        Encoder encoderEncodePointerArray2 = encoderEncoderForMap4.encodePointerArray(size4, 16, -1);
        for (int i5 = 0; i5 < size4; i5++) {
            encoderEncodePointerArray2.encode(iArr6[i5], (i5 * 8) + 8, 0, -1);
        }
    }

    private AccessibilityWindowInfoData(int i) {
        super(64, i);
    }

    public static AccessibilityWindowInfoData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

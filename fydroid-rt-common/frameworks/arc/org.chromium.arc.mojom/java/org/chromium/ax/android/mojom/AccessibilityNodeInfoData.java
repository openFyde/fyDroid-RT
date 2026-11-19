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

public final class AccessibilityNodeInfoData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<Integer, Boolean> booleanProperties;
    public Rect boundsInScreen;
    public AccessibilityCollectionInfoData collectionInfo;
    public AccessibilityCollectionItemInfoData collectionItemInfo;
    public AccessibilityActionInAndroid[] customActions;
    public int id;
    public Map<Integer, int[]> intListProperties;
    public Map<Integer, Integer> intProperties;
    public boolean isVirtualNode;
    public AccessibilityRangeInfoData rangeInfo;
    public Map<Integer, SpanEntry[]> spannableStringProperties;
    public AccessibilityActionInAndroid[] standardActions;
    public Map<Integer, String[]> stringListProperties;
    public Map<Integer, String> stringProperties;
    public int windowId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(56, 1), new DataHeader(64, 3), new DataHeader(96, 5), new DataHeader(96, 12), new DataHeader(104, 20), new DataHeader(120, 24)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[6];
    }

    public AccessibilityNodeInfoData() {
        super(120, 24);
    }

    public static AccessibilityNodeInfoData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AccessibilityNodeInfoData accessibilityNodeInfoData = new AccessibilityNodeInfoData(i);
            int i2 = 8;
            accessibilityNodeInfoData.boundsInScreen = Rect.decode(decoder.readPointer(8, false));
            if (i >= 1) {
                accessibilityNodeInfoData.id = decoder.readInt(16);
            }
            if (i >= 12) {
                accessibilityNodeInfoData.windowId = decoder.readInt(20);
            }
            int i3 = -1;
            if (i >= 1) {
                Decoder pointer = decoder.readPointer(24, true);
                if (pointer == null) {
                    accessibilityNodeInfoData.booleanProperties = null;
                } else {
                    pointer.readDataHeaderForMap();
                    int[] ints = pointer.readInts(8, 0, -1);
                    for (int i4 : ints) {
                        AccessibilityBooleanProperty.validate(i4);
                    }
                    boolean[] booleans = pointer.readBooleans(16, 0, ints.length);
                    accessibilityNodeInfoData.booleanProperties = new HashMap();
                    for (int i5 = 0; i5 < ints.length; i5++) {
                        accessibilityNodeInfoData.booleanProperties.put(Integer.valueOf(ints[i5]), Boolean.valueOf(booleans[i5]));
                    }
                }
                Decoder pointer2 = decoder.readPointer(32, true);
                if (pointer2 == null) {
                    accessibilityNodeInfoData.stringProperties = null;
                } else {
                    pointer2.readDataHeaderForMap();
                    int[] ints2 = pointer2.readInts(8, 0, -1);
                    for (int i6 : ints2) {
                        AccessibilityStringProperty.validate(i6);
                    }
                    Decoder pointer3 = pointer2.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray = pointer3.readDataHeaderForPointerArray(ints2.length);
                    String[] strArr = new String[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i7 = 0; i7 < dataHeaderForPointerArray.elementsOrVersion; i7++) {
                        strArr[i7] = GeneratedOutlineSupport.outline1(i7, 8, 8, pointer3, false);
                    }
                    accessibilityNodeInfoData.stringProperties = new HashMap();
                    for (int i8 = 0; i8 < ints2.length; i8++) {
                        accessibilityNodeInfoData.stringProperties.put(Integer.valueOf(ints2[i8]), strArr[i8]);
                    }
                }
                Decoder pointer4 = decoder.readPointer(40, true);
                if (pointer4 == null) {
                    accessibilityNodeInfoData.intProperties = null;
                } else {
                    pointer4.readDataHeaderForMap();
                    int[] ints3 = pointer4.readInts(8, 0, -1);
                    for (int i9 : ints3) {
                        AccessibilityIntProperty.validate(i9);
                    }
                    int[] ints4 = pointer4.readInts(16, 0, ints3.length);
                    accessibilityNodeInfoData.intProperties = new HashMap();
                    for (int i10 = 0; i10 < ints3.length; i10++) {
                        accessibilityNodeInfoData.intProperties.put(Integer.valueOf(ints3[i10]), Integer.valueOf(ints4[i10]));
                    }
                }
                Decoder pointer5 = decoder.readPointer(48, true);
                if (pointer5 == null) {
                    accessibilityNodeInfoData.intListProperties = null;
                } else {
                    pointer5.readDataHeaderForMap();
                    int[] ints5 = pointer5.readInts(8, 0, -1);
                    for (int i11 : ints5) {
                        AccessibilityIntListProperty.validate(i11);
                    }
                    Decoder pointer6 = pointer5.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray2 = pointer6.readDataHeaderForPointerArray(ints5.length);
                    int[][] iArr = new int[dataHeaderForPointerArray2.elementsOrVersion][];
                    for (int i12 = 0; i12 < dataHeaderForPointerArray2.elementsOrVersion; i12++) {
                        iArr[i12] = pointer6.readInts((i12 * 8) + 8, 0, -1);
                    }
                    accessibilityNodeInfoData.intListProperties = new HashMap();
                    for (int i13 = 0; i13 < ints5.length; i13++) {
                        accessibilityNodeInfoData.intListProperties.put(Integer.valueOf(ints5[i13]), iArr[i13]);
                    }
                }
            }
            if (i >= 3) {
                Decoder pointer7 = decoder.readPointer(56, true);
                if (pointer7 == null) {
                    accessibilityNodeInfoData.stringListProperties = null;
                } else {
                    pointer7.readDataHeaderForMap();
                    int[] ints6 = pointer7.readInts(8, 0, -1);
                    for (int i14 : ints6) {
                        AccessibilityStringListProperty.validate(i14);
                    }
                    Decoder pointer8 = pointer7.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray3 = pointer8.readDataHeaderForPointerArray(ints6.length);
                    String[][] strArr2 = new String[dataHeaderForPointerArray3.elementsOrVersion][];
                    int i15 = 0;
                    while (i15 < dataHeaderForPointerArray3.elementsOrVersion) {
                        Decoder decoderOutline2 = GeneratedOutlineSupport.outline2(i15, 8, i2, pointer8, false);
                        DataHeader dataHeaderForPointerArray4 = decoderOutline2.readDataHeaderForPointerArray(i3);
                        strArr2[i15] = new String[dataHeaderForPointerArray4.elementsOrVersion];
                        for (int i16 = 0; i16 < dataHeaderForPointerArray4.elementsOrVersion; i16++) {
                            strArr2[i15][i16] = GeneratedOutlineSupport.outline1(i16, 8, 8, decoderOutline2, false);
                        }
                        i15++;
                        i3 = -1;
                        i2 = 8;
                    }
                    accessibilityNodeInfoData.stringListProperties = new HashMap();
                    for (int i17 = 0; i17 < ints6.length; i17++) {
                        accessibilityNodeInfoData.stringListProperties.put(Integer.valueOf(ints6[i17]), strArr2[i17]);
                    }
                }
            }
            if (i >= 5) {
                Decoder pointer9 = decoder.readPointer(64, true);
                if (pointer9 == null) {
                    accessibilityNodeInfoData.spannableStringProperties = null;
                } else {
                    pointer9.readDataHeaderForMap();
                    int[] ints7 = pointer9.readInts(8, 0, -1);
                    for (int i18 : ints7) {
                        AccessibilityStringProperty.validate(i18);
                    }
                    Decoder pointer10 = pointer9.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray5 = pointer10.readDataHeaderForPointerArray(ints7.length);
                    SpanEntry[][] spanEntryArr = new SpanEntry[dataHeaderForPointerArray5.elementsOrVersion][];
                    for (int i19 = 0; i19 < dataHeaderForPointerArray5.elementsOrVersion; i19++) {
                        Decoder decoderOutline22 = GeneratedOutlineSupport.outline2(i19, 8, 8, pointer10, false);
                        DataHeader dataHeaderForPointerArray6 = decoderOutline22.readDataHeaderForPointerArray(-1);
                        spanEntryArr[i19] = new SpanEntry[dataHeaderForPointerArray6.elementsOrVersion];
                        for (int i20 = 0; i20 < dataHeaderForPointerArray6.elementsOrVersion; i20++) {
                            spanEntryArr[i19][i20] = SpanEntry.decode(GeneratedOutlineSupport.outline2(i20, 8, 8, decoderOutline22, false));
                        }
                    }
                    accessibilityNodeInfoData.spannableStringProperties = new HashMap();
                    for (int i21 = 0; i21 < ints7.length; i21++) {
                        accessibilityNodeInfoData.spannableStringProperties.put(Integer.valueOf(ints7[i21]), spanEntryArr[i21]);
                    }
                }
                accessibilityNodeInfoData.collectionInfo = AccessibilityCollectionInfoData.decode(decoder.readPointer(72, true));
                accessibilityNodeInfoData.collectionItemInfo = AccessibilityCollectionItemInfoData.decode(decoder.readPointer(80, true));
                accessibilityNodeInfoData.rangeInfo = AccessibilityRangeInfoData.decode(decoder.readPointer(88, true));
            }
            if (i >= 20) {
                accessibilityNodeInfoData.isVirtualNode = decoder.readBoolean(96, 0);
            }
            if (i >= 24) {
                Decoder pointer11 = decoder.readPointer(104, true);
                if (pointer11 == null) {
                    accessibilityNodeInfoData.standardActions = null;
                } else {
                    DataHeader dataHeaderForPointerArray7 = pointer11.readDataHeaderForPointerArray(-1);
                    accessibilityNodeInfoData.standardActions = new AccessibilityActionInAndroid[dataHeaderForPointerArray7.elementsOrVersion];
                    for (int i22 = 0; i22 < dataHeaderForPointerArray7.elementsOrVersion; i22++) {
                        accessibilityNodeInfoData.standardActions[i22] = AccessibilityActionInAndroid.decode(GeneratedOutlineSupport.outline2(i22, 8, 8, pointer11, false));
                    }
                }
                Decoder pointer12 = decoder.readPointer(112, true);
                if (pointer12 == null) {
                    accessibilityNodeInfoData.customActions = null;
                } else {
                    DataHeader dataHeaderForPointerArray8 = pointer12.readDataHeaderForPointerArray(-1);
                    accessibilityNodeInfoData.customActions = new AccessibilityActionInAndroid[dataHeaderForPointerArray8.elementsOrVersion];
                    for (int i23 = 0; i23 < dataHeaderForPointerArray8.elementsOrVersion; i23++) {
                        accessibilityNodeInfoData.customActions[i23] = AccessibilityActionInAndroid.decode(GeneratedOutlineSupport.outline2(i23, 8, 8, pointer12, false));
                    }
                }
            }
            return accessibilityNodeInfoData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityNodeInfoData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.boundsInScreen, 8, false);
        encoderAtDataOffset.encode(this.id, 16);
        encoderAtDataOffset.encode(this.windowId, 20);
        if (this.booleanProperties == null) {
            encoderAtDataOffset.encodeNullPointer(24, true);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(24);
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
            encoderAtDataOffset.encodeNullPointer(32, true);
        } else {
            Encoder encoderEncoderForMap2 = encoderAtDataOffset.encoderForMap(32);
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
            encoderAtDataOffset.encodeNullPointer(40, true);
        } else {
            Encoder encoderEncoderForMap3 = encoderAtDataOffset.encoderForMap(40);
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
            encoderAtDataOffset.encodeNullPointer(48, true);
        } else {
            Encoder encoderEncoderForMap4 = encoderAtDataOffset.encoderForMap(48);
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
        if (this.stringListProperties == null) {
            encoderAtDataOffset.encodeNullPointer(56, true);
        } else {
            Encoder encoderEncoderForMap5 = encoderAtDataOffset.encoderForMap(56);
            int size5 = this.stringListProperties.size();
            int[] iArr7 = new int[size5];
            String[][] strArr2 = new String[size5][];
            int i6 = 0;
            for (Map.Entry<Integer, String[]> entry5 : this.stringListProperties.entrySet()) {
                iArr7[i6] = entry5.getKey().intValue();
                strArr2[i6] = entry5.getValue();
                i6++;
            }
            encoderEncoderForMap5.encode(iArr7, 8, 0, -1);
            Encoder encoderEncodePointerArray3 = encoderEncoderForMap5.encodePointerArray(size5, 16, -1);
            int i7 = 0;
            while (i7 < size5) {
                if (strArr2[i7] == null) {
                    encoderEncodePointerArray3.encodeNullPointer((i7 * 8) + 8, false);
                } else {
                    Encoder encoderEncodePointerArray4 = encoderEncodePointerArray3.encodePointerArray(strArr2[i7].length, (i7 * 8) + 8, -1);
                    int iOutline02 = 0;
                    while (iOutline02 < strArr2[i7].length) {
                        iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray4, strArr2[i7][iOutline02], false, iOutline02, 1);
                        i7 = i7;
                        size5 = size5;
                        strArr2 = strArr2;
                    }
                }
                i7++;
                size5 = size5;
                strArr2 = strArr2;
            }
        }
        if (this.spannableStringProperties == null) {
            encoderAtDataOffset.encodeNullPointer(64, true);
        } else {
            Encoder encoderEncoderForMap6 = encoderAtDataOffset.encoderForMap(64);
            int size6 = this.spannableStringProperties.size();
            int[] iArr8 = new int[size6];
            SpanEntry[][] spanEntryArr = new SpanEntry[size6][];
            int i8 = 0;
            for (Map.Entry<Integer, SpanEntry[]> entry6 : this.spannableStringProperties.entrySet()) {
                iArr8[i8] = entry6.getKey().intValue();
                spanEntryArr[i8] = entry6.getValue();
                i8++;
            }
            encoderEncoderForMap6.encode(iArr8, 8, 0, -1);
            Encoder encoderEncodePointerArray5 = encoderEncoderForMap6.encodePointerArray(size6, 16, -1);
            for (int i9 = 0; i9 < size6; i9++) {
                if (spanEntryArr[i9] == null) {
                    encoderEncodePointerArray5.encodeNullPointer((i9 * 8) + 8, false);
                } else {
                    Encoder encoderEncodePointerArray6 = encoderEncodePointerArray5.encodePointerArray(spanEntryArr[i9].length, (i9 * 8) + 8, -1);
                    for (int i10 = 0; i10 < spanEntryArr[i9].length; i10++) {
                        encoderEncodePointerArray6.encode((Struct) spanEntryArr[i9][i10], (i10 * 8) + 8, false);
                    }
                }
            }
        }
        encoderAtDataOffset.encode((Struct) this.collectionInfo, 72, true);
        encoderAtDataOffset.encode((Struct) this.collectionItemInfo, 80, true);
        encoderAtDataOffset.encode((Struct) this.rangeInfo, 88, true);
        encoderAtDataOffset.encode(this.isVirtualNode, 96, 0);
        AccessibilityActionInAndroid[] accessibilityActionInAndroidArr = this.standardActions;
        if (accessibilityActionInAndroidArr != null) {
            Encoder encoderEncodePointerArray7 = encoderAtDataOffset.encodePointerArray(accessibilityActionInAndroidArr.length, 104, -1);
            int i11 = 0;
            while (true) {
                AccessibilityActionInAndroid[] accessibilityActionInAndroidArr2 = this.standardActions;
                if (i11 >= accessibilityActionInAndroidArr2.length) {
                    break;
                }
                encoderEncodePointerArray7.encode((Struct) accessibilityActionInAndroidArr2[i11], (i11 * 8) + 8, false);
                i11++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(104, true);
        }
        AccessibilityActionInAndroid[] accessibilityActionInAndroidArr3 = this.customActions;
        if (accessibilityActionInAndroidArr3 == null) {
            encoderAtDataOffset.encodeNullPointer(112, true);
            return;
        }
        Encoder encoderEncodePointerArray8 = encoderAtDataOffset.encodePointerArray(accessibilityActionInAndroidArr3.length, 112, -1);
        int i12 = 0;
        while (true) {
            AccessibilityActionInAndroid[] accessibilityActionInAndroidArr4 = this.customActions;
            if (i12 >= accessibilityActionInAndroidArr4.length) {
                return;
            }
            encoderEncodePointerArray8.encode((Struct) accessibilityActionInAndroidArr4[i12], (i12 * 8) + 8, false);
            i12++;
        }
    }

    private AccessibilityNodeInfoData(int i) {
        super(120, i);
    }

    public static AccessibilityNodeInfoData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

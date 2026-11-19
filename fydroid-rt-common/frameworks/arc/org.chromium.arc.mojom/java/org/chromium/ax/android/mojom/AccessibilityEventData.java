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

public final class AccessibilityEventData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] eventText;
    public int eventType;
    public Map<Integer, int[]> intListProperties;
    public Map<Integer, Integer> intProperties;
    public boolean isInputMethodWindow;
    public AccessibilityNodeInfoData[] nodeData;
    public String notificationKey;
    public int sourceId;
    public Map<Integer, String> stringProperties;
    public int taskId;
    public AccessibilityWindowInfoData[] windowData;
    public int windowId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(40, 6), new DataHeader(40, 8), new DataHeader(48, 12), new DataHeader(56, 14), new DataHeader(64, 15), new DataHeader(80, 21), new DataHeader(88, 23)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[7];
    }

    public AccessibilityEventData() {
        super(88, 23);
    }

    public static AccessibilityEventData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AccessibilityEventData accessibilityEventData = new AccessibilityEventData(i);
            int i2 = decoder.readInt(8);
            accessibilityEventData.eventType = i2;
            AccessibilityEventType.validate(i2);
            accessibilityEventData.sourceId = decoder.readInt(12);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            accessibilityEventData.nodeData = new AccessibilityNodeInfoData[dataHeaderForPointerArray.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                accessibilityEventData.nodeData[i3] = AccessibilityNodeInfoData.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer, false));
            }
            if (i >= 6) {
                accessibilityEventData.notificationKey = decoder.readString(24, true);
                accessibilityEventData.windowId = decoder.readInt(32);
            }
            if (i >= 8) {
                accessibilityEventData.taskId = decoder.readInt(36);
            }
            if (i >= 12) {
                Decoder pointer2 = decoder.readPointer(40, true);
                if (pointer2 == null) {
                    accessibilityEventData.windowData = null;
                } else {
                    DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                    accessibilityEventData.windowData = new AccessibilityWindowInfoData[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i4 = 0; i4 < dataHeaderForPointerArray2.elementsOrVersion; i4++) {
                        accessibilityEventData.windowData[i4] = AccessibilityWindowInfoData.decode(GeneratedOutlineSupport.outline2(i4, 8, 8, pointer2, false));
                    }
                }
            }
            if (i >= 14) {
                accessibilityEventData.isInputMethodWindow = decoder.readBoolean(48, 0);
            }
            if (i >= 15) {
                Decoder pointer3 = decoder.readPointer(56, true);
                if (pointer3 == null) {
                    accessibilityEventData.eventText = null;
                } else {
                    DataHeader dataHeaderForPointerArray3 = pointer3.readDataHeaderForPointerArray(-1);
                    accessibilityEventData.eventText = new String[dataHeaderForPointerArray3.elementsOrVersion];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray3.elementsOrVersion; i5++) {
                        accessibilityEventData.eventText[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer3, false);
                    }
                }
            }
            if (i >= 21) {
                Decoder pointer4 = decoder.readPointer(64, true);
                if (pointer4 == null) {
                    accessibilityEventData.intProperties = null;
                } else {
                    pointer4.readDataHeaderForMap();
                    int[] ints = pointer4.readInts(8, 0, -1);
                    for (int i6 : ints) {
                        AccessibilityEventIntProperty.validate(i6);
                    }
                    int[] ints2 = pointer4.readInts(16, 0, ints.length);
                    accessibilityEventData.intProperties = new HashMap();
                    for (int i7 = 0; i7 < ints.length; i7++) {
                        accessibilityEventData.intProperties.put(Integer.valueOf(ints[i7]), Integer.valueOf(ints2[i7]));
                    }
                }
                Decoder pointer5 = decoder.readPointer(72, true);
                if (pointer5 == null) {
                    accessibilityEventData.stringProperties = null;
                } else {
                    pointer5.readDataHeaderForMap();
                    int[] ints3 = pointer5.readInts(8, 0, -1);
                    for (int i8 : ints3) {
                        AccessibilityEventStringProperty.validate(i8);
                    }
                    Decoder pointer6 = pointer5.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray4 = pointer6.readDataHeaderForPointerArray(ints3.length);
                    String[] strArr = new String[dataHeaderForPointerArray4.elementsOrVersion];
                    for (int i9 = 0; i9 < dataHeaderForPointerArray4.elementsOrVersion; i9++) {
                        strArr[i9] = GeneratedOutlineSupport.outline1(i9, 8, 8, pointer6, false);
                    }
                    accessibilityEventData.stringProperties = new HashMap();
                    for (int i10 = 0; i10 < ints3.length; i10++) {
                        accessibilityEventData.stringProperties.put(Integer.valueOf(ints3[i10]), strArr[i10]);
                    }
                }
            }
            if (i >= 23) {
                Decoder pointer7 = decoder.readPointer(80, true);
                if (pointer7 == null) {
                    accessibilityEventData.intListProperties = null;
                } else {
                    pointer7.readDataHeaderForMap();
                    int[] ints4 = pointer7.readInts(8, 0, -1);
                    for (int i11 : ints4) {
                        AccessibilityEventIntListProperty.validate(i11);
                    }
                    Decoder pointer8 = pointer7.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray5 = pointer8.readDataHeaderForPointerArray(ints4.length);
                    int[][] iArr = new int[dataHeaderForPointerArray5.elementsOrVersion][];
                    for (int i12 = 0; i12 < dataHeaderForPointerArray5.elementsOrVersion; i12++) {
                        iArr[i12] = pointer8.readInts((i12 * 8) + 8, 0, -1);
                    }
                    accessibilityEventData.intListProperties = new HashMap();
                    for (int i13 = 0; i13 < ints4.length; i13++) {
                        accessibilityEventData.intListProperties.put(Integer.valueOf(ints4[i13]), iArr[i13]);
                    }
                }
            }
            return accessibilityEventData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityEventData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.eventType, 8);
        encoderAtDataOffset.encode(this.sourceId, 12);
        AccessibilityNodeInfoData[] accessibilityNodeInfoDataArr = this.nodeData;
        if (accessibilityNodeInfoDataArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(accessibilityNodeInfoDataArr.length, 16, -1);
            int i = 0;
            while (true) {
                AccessibilityNodeInfoData[] accessibilityNodeInfoDataArr2 = this.nodeData;
                if (i >= accessibilityNodeInfoDataArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) accessibilityNodeInfoDataArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode(this.notificationKey, 24, true);
        encoderAtDataOffset.encode(this.windowId, 32);
        encoderAtDataOffset.encode(this.taskId, 36);
        AccessibilityWindowInfoData[] accessibilityWindowInfoDataArr = this.windowData;
        if (accessibilityWindowInfoDataArr != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(accessibilityWindowInfoDataArr.length, 40, -1);
            int i2 = 0;
            while (true) {
                AccessibilityWindowInfoData[] accessibilityWindowInfoDataArr2 = this.windowData;
                if (i2 >= accessibilityWindowInfoDataArr2.length) {
                    break;
                }
                encoderEncodePointerArray2.encode((Struct) accessibilityWindowInfoDataArr2[i2], (i2 * 8) + 8, false);
                i2++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(40, true);
        }
        encoderAtDataOffset.encode(this.isInputMethodWindow, 48, 0);
        String[] strArr = this.eventText;
        if (strArr != null) {
            Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(strArr.length, 56, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.eventText;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray3, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(56, true);
        }
        if (this.intProperties == null) {
            encoderAtDataOffset.encodeNullPointer(64, true);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(64);
            int size = this.intProperties.size();
            int[] iArr = new int[size];
            int[] iArr2 = new int[size];
            int i3 = 0;
            for (Map.Entry<Integer, Integer> entry : this.intProperties.entrySet()) {
                iArr[i3] = entry.getKey().intValue();
                iArr2[i3] = entry.getValue().intValue();
                i3++;
            }
            encoderEncoderForMap.encode(iArr, 8, 0, -1);
            encoderEncoderForMap.encode(iArr2, 16, 0, -1);
        }
        if (this.stringProperties == null) {
            encoderAtDataOffset.encodeNullPointer(72, true);
        } else {
            Encoder encoderEncoderForMap2 = encoderAtDataOffset.encoderForMap(72);
            int size2 = this.stringProperties.size();
            int[] iArr3 = new int[size2];
            String[] strArr3 = new String[size2];
            int i4 = 0;
            for (Map.Entry<Integer, String> entry2 : this.stringProperties.entrySet()) {
                iArr3[i4] = entry2.getKey().intValue();
                strArr3[i4] = entry2.getValue();
                i4++;
            }
            encoderEncoderForMap2.encode(iArr3, 8, 0, -1);
            Encoder encoderEncodePointerArray4 = encoderEncoderForMap2.encodePointerArray(size2, 16, -1);
            int iOutline02 = 0;
            while (iOutline02 < size2) {
                iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray4, strArr3[iOutline02], false, iOutline02, 1);
                size2 = size2;
                strArr3 = strArr3;
            }
        }
        if (this.intListProperties == null) {
            encoderAtDataOffset.encodeNullPointer(80, true);
            return;
        }
        Encoder encoderEncoderForMap3 = encoderAtDataOffset.encoderForMap(80);
        int size3 = this.intListProperties.size();
        int[] iArr4 = new int[size3];
        int[][] iArr5 = new int[size3][];
        int i5 = 0;
        for (Map.Entry<Integer, int[]> entry3 : this.intListProperties.entrySet()) {
            iArr4[i5] = entry3.getKey().intValue();
            iArr5[i5] = entry3.getValue();
            i5++;
        }
        encoderEncoderForMap3.encode(iArr4, 8, 0, -1);
        Encoder encoderEncodePointerArray5 = encoderEncoderForMap3.encodePointerArray(size3, 16, -1);
        for (int i6 = 0; i6 < size3; i6++) {
            encoderEncodePointerArray5.encode(iArr5[i6], (i6 * 8) + 8, 0, -1);
        }
    }

    private AccessibilityEventData(int i) {
        super(88, i);
    }

    public static AccessibilityEventData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

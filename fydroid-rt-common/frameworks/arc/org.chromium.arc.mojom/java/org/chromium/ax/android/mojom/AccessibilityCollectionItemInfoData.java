package org.chromium.ax.android.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AccessibilityCollectionItemInfoData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int columnIndex;
    public int columnSpan;
    public boolean isHeading;
    public boolean isSelected;
    public int rowIndex;
    public int rowSpan;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AccessibilityCollectionItemInfoData() {
        super(32, 0);
    }

    public static AccessibilityCollectionItemInfoData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AccessibilityCollectionItemInfoData accessibilityCollectionItemInfoData = new AccessibilityCollectionItemInfoData(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            accessibilityCollectionItemInfoData.rowIndex = decoder.readInt(8);
            accessibilityCollectionItemInfoData.columnIndex = decoder.readInt(12);
            accessibilityCollectionItemInfoData.rowSpan = decoder.readInt(16);
            accessibilityCollectionItemInfoData.columnSpan = decoder.readInt(20);
            accessibilityCollectionItemInfoData.isHeading = decoder.readBoolean(24, 0);
            accessibilityCollectionItemInfoData.isSelected = decoder.readBoolean(24, 1);
            return accessibilityCollectionItemInfoData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityCollectionItemInfoData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.rowIndex, 8);
        encoderAtDataOffset.encode(this.columnIndex, 12);
        encoderAtDataOffset.encode(this.rowSpan, 16);
        encoderAtDataOffset.encode(this.columnSpan, 20);
        encoderAtDataOffset.encode(this.isHeading, 24, 0);
        encoderAtDataOffset.encode(this.isSelected, 24, 1);
    }

    private AccessibilityCollectionItemInfoData(int i) {
        super(32, i);
    }

    public static AccessibilityCollectionItemInfoData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

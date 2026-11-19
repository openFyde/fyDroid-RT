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

public final class BluetoothSdpRecord extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<Short, BluetoothSdpAttribute> attrs;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothSdpRecord() {
        super(16, 0);
    }

    public static BluetoothSdpRecord decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothSdpRecord bluetoothSdpRecord = new BluetoothSdpRecord(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            pointer.readDataHeaderForMap();
            short[] shorts = pointer.readShorts(8, 0, -1);
            Decoder pointer2 = pointer.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(shorts.length);
            BluetoothSdpAttribute[] bluetoothSdpAttributeArr = new BluetoothSdpAttribute[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                bluetoothSdpAttributeArr[i] = BluetoothSdpAttribute.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer2, false));
            }
            bluetoothSdpRecord.attrs = new HashMap();
            for (int i2 = 0; i2 < shorts.length; i2++) {
                bluetoothSdpRecord.attrs.put(Short.valueOf(shorts[i2]), bluetoothSdpAttributeArr[i2]);
            }
            return bluetoothSdpRecord;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothSdpRecord deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        if (this.attrs == null) {
            encoderAtDataOffset.encodeNullPointer(8, false);
            return;
        }
        Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(8);
        int size = this.attrs.size();
        short[] sArr = new short[size];
        BluetoothSdpAttribute[] bluetoothSdpAttributeArr = new BluetoothSdpAttribute[size];
        int i = 0;
        for (Map.Entry<Short, BluetoothSdpAttribute> entry : this.attrs.entrySet()) {
            sArr[i] = entry.getKey().shortValue();
            bluetoothSdpAttributeArr[i] = entry.getValue();
            i++;
        }
        encoderEncoderForMap.encode(sArr, 8, 0, -1);
        Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 16, -1);
        for (int i2 = 0; i2 < size; i2++) {
            encoderEncodePointerArray.encode((Struct) bluetoothSdpAttributeArr[i2], (i2 * 8) + 8, false);
        }
    }

    private BluetoothSdpRecord(int i) {
        super(16, i);
    }

    public static BluetoothSdpRecord deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

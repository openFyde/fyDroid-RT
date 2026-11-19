package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BluetoothGattId extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte deprecatedInstId;
    public short instanceId;
    public BluetoothUuid uuid;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(24, 19)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public BluetoothGattId() {
        super(24, 19);
    }

    public static BluetoothGattId decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            BluetoothGattId bluetoothGattId = new BluetoothGattId(i);
            bluetoothGattId.uuid = BluetoothUuid.decode(decoder.readPointer(8, false));
            bluetoothGattId.deprecatedInstId = decoder.readByte(16);
            if (i >= 19) {
                bluetoothGattId.instanceId = decoder.readShort(18);
            }
            return bluetoothGattId;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothGattId deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.uuid, 8, false);
        encoderAtDataOffset.encode(this.deprecatedInstId, 16);
        encoderAtDataOffset.encode(this.instanceId, 18);
    }

    private BluetoothGattId(int i) {
        super(24, i);
    }

    public static BluetoothGattId deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

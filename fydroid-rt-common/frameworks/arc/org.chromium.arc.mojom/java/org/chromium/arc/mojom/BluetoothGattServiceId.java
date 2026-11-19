package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BluetoothGattServiceId extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public BluetoothGattId id;
    public byte isPrimary;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothGattServiceId() {
        super(24, 0);
    }

    public static BluetoothGattServiceId decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothGattServiceId bluetoothGattServiceId = new BluetoothGattServiceId(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothGattServiceId.id = BluetoothGattId.decode(decoder.readPointer(8, false));
            bluetoothGattServiceId.isPrimary = decoder.readByte(16);
            return bluetoothGattServiceId;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothGattServiceId deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.id, 8, false);
        encoderAtDataOffset.encode(this.isPrimary, 16);
    }

    private BluetoothGattServiceId(int i) {
        super(24, i);
    }

    public static BluetoothGattServiceId deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

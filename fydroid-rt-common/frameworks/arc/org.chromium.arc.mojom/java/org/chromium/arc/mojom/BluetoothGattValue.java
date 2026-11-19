package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BluetoothGattValue extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int status;
    public byte[] value;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothGattValue() {
        super(24, 0);
    }

    public static BluetoothGattValue decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothGattValue bluetoothGattValue = new BluetoothGattValue(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            bluetoothGattValue.status = i;
            BluetoothGattStatus.validate(i);
            bluetoothGattValue.value = decoder.readBytes(16, 0, -1);
            return bluetoothGattValue;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothGattValue deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.status, 8);
        encoderAtDataOffset.encode(this.value, 16, 0, -1);
    }

    private BluetoothGattValue(int i) {
        super(24, i);
    }

    public static BluetoothGattValue deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

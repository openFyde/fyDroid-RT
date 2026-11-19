package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BluetoothServiceData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] data;
    public short uuid16bit;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothServiceData() {
        super(24, 0);
    }

    public static BluetoothServiceData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothServiceData bluetoothServiceData = new BluetoothServiceData(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothServiceData.uuid16bit = decoder.readShort(8);
            bluetoothServiceData.data = decoder.readBytes(16, 0, -1);
            return bluetoothServiceData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothServiceData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.uuid16bit, 8);
        encoderAtDataOffset.encode(this.data, 16, 0, -1);
    }

    private BluetoothServiceData(int i) {
        super(24, i);
    }

    public static BluetoothServiceData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

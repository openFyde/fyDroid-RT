package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BluetoothUuid extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] uuid;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothUuid() {
        super(16, 0);
    }

    public static BluetoothUuid decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothUuid bluetoothUuid = new BluetoothUuid(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothUuid.uuid = decoder.readBytes(8, 0, 16);
            return bluetoothUuid;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothUuid deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.uuid, 8, 0, 16);
    }

    private BluetoothUuid(int i) {
        super(16, i);
    }

    public static BluetoothUuid deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

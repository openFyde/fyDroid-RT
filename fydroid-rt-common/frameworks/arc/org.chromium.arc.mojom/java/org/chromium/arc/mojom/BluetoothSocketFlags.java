package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BluetoothSocketFlags extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean auth;
    public boolean auth16Digit;
    public boolean authMitm;
    public boolean encrypt;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothSocketFlags() {
        super(16, 0);
    }

    public static BluetoothSocketFlags decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothSocketFlags bluetoothSocketFlags = new BluetoothSocketFlags(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothSocketFlags.encrypt = decoder.readBoolean(8, 0);
            bluetoothSocketFlags.auth = decoder.readBoolean(8, 1);
            bluetoothSocketFlags.authMitm = decoder.readBoolean(8, 2);
            bluetoothSocketFlags.auth16Digit = decoder.readBoolean(8, 3);
            return bluetoothSocketFlags;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothSocketFlags deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.encrypt, 8, 0);
        encoderAtDataOffset.encode(this.auth, 8, 1);
        encoderAtDataOffset.encode(this.authMitm, 8, 2);
        encoderAtDataOffset.encode(this.auth16Digit, 8, 3);
    }

    private BluetoothSocketFlags(int i) {
        super(16, i);
    }

    public static BluetoothSocketFlags deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

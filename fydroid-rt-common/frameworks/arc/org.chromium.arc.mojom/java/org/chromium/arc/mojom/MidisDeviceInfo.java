package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class MidisDeviceInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int card;
    public int deviceNum;
    public int flags;
    public String manufacturer;
    public String name;
    public int numSubdevices;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public MidisDeviceInfo() {
        super(40, 0);
    }

    public static MidisDeviceInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            MidisDeviceInfo midisDeviceInfo = new MidisDeviceInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            midisDeviceInfo.card = decoder.readInt(8);
            midisDeviceInfo.deviceNum = decoder.readInt(12);
            midisDeviceInfo.numSubdevices = decoder.readInt(16);
            midisDeviceInfo.flags = decoder.readInt(20);
            midisDeviceInfo.name = decoder.readString(24, false);
            midisDeviceInfo.manufacturer = decoder.readString(32, false);
            return midisDeviceInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MidisDeviceInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.card, 8);
        encoderAtDataOffset.encode(this.deviceNum, 12);
        encoderAtDataOffset.encode(this.numSubdevices, 16);
        encoderAtDataOffset.encode(this.flags, 20);
        encoderAtDataOffset.encode(this.name, 24, false);
        encoderAtDataOffset.encode(this.manufacturer, 32, false);
    }

    private MidisDeviceInfo(int i) {
        super(40, i);
    }

    public static MidisDeviceInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

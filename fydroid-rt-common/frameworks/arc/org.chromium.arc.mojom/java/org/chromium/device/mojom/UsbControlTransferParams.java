package org.chromium.device.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UsbControlTransferParams extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public short index;
    public int recipient;
    public byte request;
    public int type;
    public short value;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UsbControlTransferParams() {
        super(24, 0);
    }

    public static UsbControlTransferParams decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UsbControlTransferParams usbControlTransferParams = new UsbControlTransferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            usbControlTransferParams.type = i;
            UsbControlTransferType.validate(i);
            int i2 = decoder.readInt(12);
            usbControlTransferParams.recipient = i2;
            UsbControlTransferRecipient.validate(i2);
            usbControlTransferParams.request = decoder.readByte(16);
            usbControlTransferParams.value = decoder.readShort(18);
            usbControlTransferParams.index = decoder.readShort(20);
            return usbControlTransferParams;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UsbControlTransferParams deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.type, 8);
        encoderAtDataOffset.encode(this.recipient, 12);
        encoderAtDataOffset.encode(this.request, 16);
        encoderAtDataOffset.encode(this.value, 18);
        encoderAtDataOffset.encode(this.index, 20);
    }

    private UsbControlTransferParams(int i) {
        super(24, i);
    }

    public static UsbControlTransferParams deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

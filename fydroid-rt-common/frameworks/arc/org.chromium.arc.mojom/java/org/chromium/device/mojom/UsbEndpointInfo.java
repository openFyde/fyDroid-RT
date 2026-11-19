package org.chromium.device.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UsbEndpointInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int direction;
    public byte endpointNumber;
    public byte[] extraData;
    public int packetSize;
    public byte pollingInterval;
    public int synchronizationType;
    public int type;
    public int usageType;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UsbEndpointInfo() {
        super(40, 0);
    }

    public static UsbEndpointInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UsbEndpointInfo usbEndpointInfo = new UsbEndpointInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            usbEndpointInfo.endpointNumber = decoder.readByte(8);
            usbEndpointInfo.pollingInterval = decoder.readByte(9);
            int i = decoder.readInt(12);
            usbEndpointInfo.direction = i;
            UsbTransferDirection.validate(i);
            int i2 = decoder.readInt(16);
            usbEndpointInfo.type = i2;
            UsbTransferType.validate(i2);
            usbEndpointInfo.packetSize = decoder.readInt(20);
            int i3 = decoder.readInt(24);
            usbEndpointInfo.synchronizationType = i3;
            UsbSynchronizationType.validate(i3);
            int i4 = decoder.readInt(28);
            usbEndpointInfo.usageType = i4;
            UsbUsageType.validate(i4);
            usbEndpointInfo.extraData = decoder.readBytes(32, 0, -1);
            return usbEndpointInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UsbEndpointInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.endpointNumber, 8);
        encoderAtDataOffset.encode(this.pollingInterval, 9);
        encoderAtDataOffset.encode(this.direction, 12);
        encoderAtDataOffset.encode(this.type, 16);
        encoderAtDataOffset.encode(this.packetSize, 20);
        encoderAtDataOffset.encode(this.synchronizationType, 24);
        encoderAtDataOffset.encode(this.usageType, 28);
        encoderAtDataOffset.encode(this.extraData, 32, 0, -1);
    }

    private UsbEndpointInfo(int i) {
        super(40, i);
    }

    public static UsbEndpointInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

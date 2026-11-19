package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.network.mojom.IpAddress;

public final class SocketConnectionEvent extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int appCategory;
    public IpAddress dstAddr;
    public int dstPort;
    public int event;
    public int proto;
    public int qosCategory;
    public IpAddress srcAddr;
    public int srcPort;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SocketConnectionEvent() {
        super(48, 0);
    }

    public static SocketConnectionEvent decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SocketConnectionEvent socketConnectionEvent = new SocketConnectionEvent(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            socketConnectionEvent.srcAddr = IpAddress.decode(decoder.readPointer(8, false));
            socketConnectionEvent.dstAddr = IpAddress.decode(decoder.readPointer(16, false));
            socketConnectionEvent.srcPort = decoder.readInt(24);
            socketConnectionEvent.dstPort = decoder.readInt(28);
            int i = decoder.readInt(32);
            socketConnectionEvent.proto = i;
            IpProtocol.validate(i);
            int i2 = decoder.readInt(36);
            socketConnectionEvent.event = i2;
            SocketEvent.validate(i2);
            int i3 = decoder.readInt(40);
            socketConnectionEvent.appCategory = i3;
            AppCategory.validate(i3);
            int i4 = decoder.readInt(44);
            socketConnectionEvent.qosCategory = i4;
            QosCategory.validate(i4);
            return socketConnectionEvent;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SocketConnectionEvent deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.srcAddr, 8, false);
        encoderAtDataOffset.encode((Struct) this.dstAddr, 16, false);
        encoderAtDataOffset.encode(this.srcPort, 24);
        encoderAtDataOffset.encode(this.dstPort, 28);
        encoderAtDataOffset.encode(this.proto, 32);
        encoderAtDataOffset.encode(this.event, 36);
        encoderAtDataOffset.encode(this.appCategory, 40);
        encoderAtDataOffset.encode(this.qosCategory, 44);
    }

    private SocketConnectionEvent(int i) {
        super(48, i);
    }

    public static SocketConnectionEvent deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

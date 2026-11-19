package org.chromium.chromeos.mojo_service_manager.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ServiceEvent extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public ProcessIdentity dispatcher;
    public String serviceName;
    public int type;

    public static final class Type {
        public static final int REGISTERED = 1;
        public static final int UNKNOWN = 0;
        public static final int UN_REGISTERED = 2;

        private Type() {
        }

        public static boolean isKnownValue(int i) {
            return i == 0 || i == 1 || i == 2;
        }

        public static void validate(int i) {
        }
    }

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ServiceEvent() {
        super(32, 0);
    }

    public static ServiceEvent decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ServiceEvent serviceEvent = new ServiceEvent(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            serviceEvent.type = i;
            Type.validate(i);
            serviceEvent.serviceName = decoder.readString(16, false);
            serviceEvent.dispatcher = ProcessIdentity.decode(decoder.readPointer(24, false));
            return serviceEvent;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ServiceEvent deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.type, 8);
        encoderAtDataOffset.encode(this.serviceName, 16, false);
        encoderAtDataOffset.encode((Struct) this.dispatcher, 24, false);
    }

    private ServiceEvent(int i) {
        super(32, i);
    }

    public static ServiceEvent deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

package org.chromium.chromeos.mojo_service_manager.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RegisteredServiceState extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public ProcessIdentity owner;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RegisteredServiceState() {
        super(16, 0);
    }

    public static RegisteredServiceState decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RegisteredServiceState registeredServiceState = new RegisteredServiceState(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            registeredServiceState.owner = ProcessIdentity.decode(decoder.readPointer(8, false));
            return registeredServiceState;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RegisteredServiceState deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.owner, 8, false);
    }

    private RegisteredServiceState(int i) {
        super(16, i);
    }

    public static RegisteredServiceState deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

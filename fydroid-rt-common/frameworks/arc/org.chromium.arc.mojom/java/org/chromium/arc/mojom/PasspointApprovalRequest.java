package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PasspointApprovalRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String appName;
    public String friendlyName;
    public String packageName;
    public long subscriptionExpirationTimeMs;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(40, 31)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public PasspointApprovalRequest() {
        super(40, 31);
    }

    public static PasspointApprovalRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            PasspointApprovalRequest passpointApprovalRequest = new PasspointApprovalRequest(i);
            passpointApprovalRequest.packageName = decoder.readString(8, false);
            passpointApprovalRequest.appName = decoder.readString(16, false);
            if (i >= 31) {
                passpointApprovalRequest.friendlyName = decoder.readString(24, true);
                passpointApprovalRequest.subscriptionExpirationTimeMs = decoder.readLong(32);
            }
            return passpointApprovalRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PasspointApprovalRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.packageName, 8, false);
        encoderAtDataOffset.encode(this.appName, 16, false);
        encoderAtDataOffset.encode(this.friendlyName, 24, true);
        encoderAtDataOffset.encode(this.subscriptionExpirationTimeMs, 32);
    }

    private PasspointApprovalRequest(int i) {
        super(40, i);
    }

    public static PasspointApprovalRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

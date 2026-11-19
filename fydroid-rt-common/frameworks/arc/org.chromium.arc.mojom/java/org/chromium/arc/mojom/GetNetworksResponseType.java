package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class GetNetworksResponseType extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public NetworkConfiguration[] networks;
    public int status;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public GetNetworksResponseType() {
        super(24, 0);
    }

    public static GetNetworksResponseType decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            GetNetworksResponseType getNetworksResponseType = new GetNetworksResponseType(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            getNetworksResponseType.status = i;
            NetworkResult.validate(i);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            getNetworksResponseType.networks = new NetworkConfiguration[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                getNetworksResponseType.networks[i2] = NetworkConfiguration.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            return getNetworksResponseType;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static GetNetworksResponseType deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.status, 8);
        NetworkConfiguration[] networkConfigurationArr = this.networks;
        if (networkConfigurationArr == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(networkConfigurationArr.length, 16, -1);
        int i = 0;
        while (true) {
            NetworkConfiguration[] networkConfigurationArr2 = this.networks;
            if (i >= networkConfigurationArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) networkConfigurationArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private GetNetworksResponseType(int i) {
        super(24, i);
    }

    public static GetNetworksResponseType deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

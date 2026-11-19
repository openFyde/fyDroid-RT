package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class WifiScanResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String bssid;
    public int frequency;
    public String hexSsid;
    public int rssi;
    public int security;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public WifiScanResult() {
        super(40, 0);
    }

    public static WifiScanResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            WifiScanResult wifiScanResult = new WifiScanResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            wifiScanResult.hexSsid = decoder.readString(8, false);
            wifiScanResult.bssid = decoder.readString(16, false);
            wifiScanResult.frequency = decoder.readInt(24);
            int i = decoder.readInt(28);
            wifiScanResult.security = i;
            SecurityType.validate(i);
            wifiScanResult.rssi = decoder.readInt(32);
            return wifiScanResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WifiScanResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.hexSsid, 8, false);
        encoderAtDataOffset.encode(this.bssid, 16, false);
        encoderAtDataOffset.encode(this.frequency, 24);
        encoderAtDataOffset.encode(this.security, 28);
        encoderAtDataOffset.encode(this.rssi, 32);
    }

    private WifiScanResult(int i) {
        super(40, i);
    }

    public static WifiScanResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

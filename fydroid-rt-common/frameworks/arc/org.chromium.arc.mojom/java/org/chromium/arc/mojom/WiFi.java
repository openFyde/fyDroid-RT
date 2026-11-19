package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class WiFi extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String bssid;
    public String fqdn;
    public int frequency;
    public String hexSsid;
    public boolean hiddenSsid;
    public boolean isPasspoint;
    public short rssi;
    public int security;
    public int signalStrength;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 16), new DataHeader(48, 23)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public WiFi() {
        super(48, 23);
    }

    public static WiFi decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            WiFi wiFi = new WiFi(i);
            wiFi.bssid = decoder.readString(8, false);
            wiFi.frequency = decoder.readInt(16);
            wiFi.hiddenSsid = decoder.readBoolean(20, 0);
            if (i >= 16) {
                wiFi.isPasspoint = decoder.readBoolean(20, 1);
            }
            if (i >= 23) {
                wiFi.rssi = decoder.readShort(22);
            }
            wiFi.hexSsid = decoder.readString(24, false);
            int i2 = decoder.readInt(32);
            wiFi.security = i2;
            SecurityType.validate(i2);
            wiFi.signalStrength = decoder.readInt(36);
            if (i >= 16) {
                wiFi.fqdn = decoder.readString(40, true);
            }
            return wiFi;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WiFi deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.bssid, 8, false);
        encoderAtDataOffset.encode(this.frequency, 16);
        encoderAtDataOffset.encode(this.hiddenSsid, 20, 0);
        encoderAtDataOffset.encode(this.isPasspoint, 20, 1);
        encoderAtDataOffset.encode(this.rssi, 22);
        encoderAtDataOffset.encode(this.hexSsid, 24, false);
        encoderAtDataOffset.encode(this.security, 32);
        encoderAtDataOffset.encode(this.signalStrength, 36);
        encoderAtDataOffset.encode(this.fqdn, 40, true);
    }

    private WiFi(int i) {
        super(48, i);
    }

    public static WiFi deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

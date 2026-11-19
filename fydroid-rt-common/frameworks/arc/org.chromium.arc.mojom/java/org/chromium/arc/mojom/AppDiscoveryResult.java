package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AppDiscoveryResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String formattedPrice;
    public RawIconPngData icon;
    public byte[] iconPngData;
    public String installIntentUri;
    public boolean isInstantApp;
    public boolean isRecent;
    public String label;
    public String launchIntentUri;
    public String packageName;
    public String publisherName;
    public float reviewScore;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(64, 0), new DataHeader(72, 22), new DataHeader(80, 46)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public AppDiscoveryResult() {
        super(80, 46);
    }

    public static AppDiscoveryResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AppDiscoveryResult appDiscoveryResult = new AppDiscoveryResult(i);
            appDiscoveryResult.launchIntentUri = decoder.readString(8, true);
            appDiscoveryResult.installIntentUri = decoder.readString(16, true);
            appDiscoveryResult.label = decoder.readString(24, true);
            appDiscoveryResult.isInstantApp = decoder.readBoolean(32, 0);
            appDiscoveryResult.isRecent = decoder.readBoolean(32, 1);
            appDiscoveryResult.reviewScore = decoder.readFloat(36);
            appDiscoveryResult.publisherName = decoder.readString(40, true);
            appDiscoveryResult.formattedPrice = decoder.readString(48, true);
            appDiscoveryResult.iconPngData = decoder.readBytes(56, 0, -1);
            if (i >= 22) {
                appDiscoveryResult.packageName = decoder.readString(64, true);
            }
            if (i >= 46) {
                appDiscoveryResult.icon = RawIconPngData.decode(decoder.readPointer(72, true));
            }
            return appDiscoveryResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AppDiscoveryResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.launchIntentUri, 8, true);
        encoderAtDataOffset.encode(this.installIntentUri, 16, true);
        encoderAtDataOffset.encode(this.label, 24, true);
        encoderAtDataOffset.encode(this.isInstantApp, 32, 0);
        encoderAtDataOffset.encode(this.isRecent, 32, 1);
        encoderAtDataOffset.encode(this.reviewScore, 36);
        encoderAtDataOffset.encode(this.publisherName, 40, true);
        encoderAtDataOffset.encode(this.formattedPrice, 48, true);
        encoderAtDataOffset.encode(this.iconPngData, 56, 0, -1);
        encoderAtDataOffset.encode(this.packageName, 64, true);
        encoderAtDataOffset.encode((Struct) this.icon, 72, true);
    }

    private AppDiscoveryResult(int i) {
        super(80, i);
    }

    public static AppDiscoveryResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

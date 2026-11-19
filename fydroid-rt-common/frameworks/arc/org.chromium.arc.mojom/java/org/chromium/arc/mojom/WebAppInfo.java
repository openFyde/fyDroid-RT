package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class WebAppInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String certificateSha256Fingerprint;
    public boolean isWebOnlyTwa;
    public String scopeUrl;
    public String startUrl;
    public long themeColor;
    public String title;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 44), new DataHeader(56, 45)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public WebAppInfo() {
        super(56, 45);
    }

    public static WebAppInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            WebAppInfo webAppInfo = new WebAppInfo(i);
            webAppInfo.title = decoder.readString(8, false);
            webAppInfo.startUrl = decoder.readString(16, false);
            webAppInfo.scopeUrl = decoder.readString(24, false);
            webAppInfo.themeColor = decoder.readLong(32);
            if (i >= 44) {
                webAppInfo.isWebOnlyTwa = decoder.readBoolean(40, 0);
            }
            if (i >= 45) {
                webAppInfo.certificateSha256Fingerprint = decoder.readString(48, true);
            }
            return webAppInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WebAppInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.title, 8, false);
        encoderAtDataOffset.encode(this.startUrl, 16, false);
        encoderAtDataOffset.encode(this.scopeUrl, 24, false);
        encoderAtDataOffset.encode(this.themeColor, 32);
        encoderAtDataOffset.encode(this.isWebOnlyTwa, 40, 0);
        encoderAtDataOffset.encode(this.certificateSha256Fingerprint, 48, true);
    }

    private WebAppInfo(int i) {
        super(56, i);
    }

    public static WebAppInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

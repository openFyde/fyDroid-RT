package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class ArcProxyInfo extends Union {
    private ManualProxyConfig mManualProxy;
    private PacUrlProxyConfig mPacUrlProxy;

    public static final class Tag {
        public static final int ManualProxy = 0;
        public static final int PacUrlProxy = 1;
    }

    public static final ArcProxyInfo decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        ArcProxyInfo arcProxyInfo = new ArcProxyInfo();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            arcProxyInfo.mManualProxy = ManualProxyConfig.decode(decoder.readPointer(i + 8, false));
            arcProxyInfo.mTag = 0;
        } else if (i2 == 1) {
            arcProxyInfo.mPacUrlProxy = PacUrlProxyConfig.decode(decoder.readPointer(i + 8, false));
            arcProxyInfo.mTag = 1;
        }
        return arcProxyInfo;
    }

    public static ArcProxyInfo deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode((Struct) this.mManualProxy, i + 8, false);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode((Struct) this.mPacUrlProxy, i + 8, false);
        }
    }

    public ManualProxyConfig getManualProxy() {
        return this.mManualProxy;
    }

    public PacUrlProxyConfig getPacUrlProxy() {
        return this.mPacUrlProxy;
    }

    public void setManualProxy(ManualProxyConfig manualProxyConfig) {
        this.mTag = 0;
        this.mManualProxy = manualProxyConfig;
    }

    public void setPacUrlProxy(PacUrlProxyConfig pacUrlProxyConfig) {
        this.mTag = 1;
        this.mPacUrlProxy = pacUrlProxyConfig;
    }
}

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class Camera3NotifyMsgMessage extends Union {
    private Camera3ErrorMsg mError;
    private byte[] mGeneric;
    private Camera3ShutterMsg mShutter;

    public static final class Tag {
        public static final int Error = 0;
        public static final int Generic = 2;
        public static final int Shutter = 1;
    }

    public static final Camera3NotifyMsgMessage decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        Camera3NotifyMsgMessage camera3NotifyMsgMessage = new Camera3NotifyMsgMessage();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            camera3NotifyMsgMessage.mError = Camera3ErrorMsg.decode(decoder.readPointer(i + 8, false));
            camera3NotifyMsgMessage.mTag = 0;
        } else if (i2 == 1) {
            camera3NotifyMsgMessage.mShutter = Camera3ShutterMsg.decode(decoder.readPointer(i + 8, false));
            camera3NotifyMsgMessage.mTag = 1;
        } else if (i2 == 2) {
            camera3NotifyMsgMessage.mGeneric = decoder.readBytes(i + 8, 0, -1);
            camera3NotifyMsgMessage.mTag = 2;
        }
        return camera3NotifyMsgMessage;
    }

    public static Camera3NotifyMsgMessage deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode((Struct) this.mError, i + 8, false);
        } else if (i2 == 1) {
            encoder.encode((Struct) this.mShutter, i + 8, false);
        } else {
            if (i2 != 2) {
                return;
            }
            encoder.encode(this.mGeneric, i + 8, 0, -1);
        }
    }

    public Camera3ErrorMsg getError() {
        return this.mError;
    }

    public byte[] getGeneric() {
        return this.mGeneric;
    }

    public Camera3ShutterMsg getShutter() {
        return this.mShutter;
    }

    public void setError(Camera3ErrorMsg camera3ErrorMsg) {
        this.mTag = 0;
        this.mError = camera3ErrorMsg;
    }

    public void setGeneric(byte[] bArr) {
        this.mTag = 2;
        this.mGeneric = bArr;
    }

    public void setShutter(Camera3ShutterMsg camera3ShutterMsg) {
        this.mTag = 1;
        this.mShutter = camera3ShutterMsg;
    }
}

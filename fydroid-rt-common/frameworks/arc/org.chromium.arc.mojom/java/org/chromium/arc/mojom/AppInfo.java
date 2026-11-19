package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AppInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String activity;
    public int appCategory;
    public AppStorage appStorage;
    public WindowLayout initialLayout;
    public String name;
    public boolean needFixup;
    public boolean notificationsEnabled;
    public String packageName;
    public int resizeLockState;
    public boolean sticky;
    public boolean suspended;
    public String versionName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 2), new DataHeader(40, 7), new DataHeader(40, 35), new DataHeader(40, 47), new DataHeader(48, 50), new DataHeader(56, 51), new DataHeader(64, 52), new DataHeader(64, 54), new DataHeader(72, 57)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[9];
    }

    public AppInfo() {
        super(72, 57);
    }

    public static AppInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AppInfo appInfo = new AppInfo(i);
            appInfo.name = decoder.readString(8, false);
            appInfo.packageName = decoder.readString(16, false);
            appInfo.activity = decoder.readString(24, false);
            if (i >= 2) {
                appInfo.sticky = decoder.readBoolean(32, 0);
            }
            if (i >= 7) {
                appInfo.notificationsEnabled = decoder.readBoolean(32, 1);
            }
            if (i >= 35) {
                appInfo.suspended = decoder.readBoolean(32, 2);
            }
            if (i >= 54) {
                appInfo.needFixup = decoder.readBoolean(32, 3);
            }
            if (i >= 47) {
                int i2 = decoder.readInt(36);
                appInfo.resizeLockState = i2;
                ArcResizeLockState.validate(i2);
            }
            if (i >= 50) {
                appInfo.initialLayout = WindowLayout.decode(decoder.readPointer(40, true));
            }
            if (i >= 51) {
                appInfo.versionName = decoder.readString(48, true);
            }
            if (i >= 52) {
                appInfo.appStorage = AppStorage.decode(decoder.readPointer(56, true));
            }
            if (i >= 57) {
                int i3 = decoder.readInt(64);
                appInfo.appCategory = i3;
                AppCategory.validate(i3);
            }
            return appInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AppInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.name, 8, false);
        encoderAtDataOffset.encode(this.packageName, 16, false);
        encoderAtDataOffset.encode(this.activity, 24, false);
        encoderAtDataOffset.encode(this.sticky, 32, 0);
        encoderAtDataOffset.encode(this.notificationsEnabled, 32, 1);
        encoderAtDataOffset.encode(this.suspended, 32, 2);
        encoderAtDataOffset.encode(this.needFixup, 32, 3);
        encoderAtDataOffset.encode(this.resizeLockState, 36);
        encoderAtDataOffset.encode((Struct) this.initialLayout, 40, true);
        encoderAtDataOffset.encode(this.versionName, 48, true);
        encoderAtDataOffset.encode((Struct) this.appStorage, 56, true);
        encoderAtDataOffset.encode(this.appCategory, 64);
    }

    private AppInfo(int i) {
        super(72, i);
    }

    public static AppInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

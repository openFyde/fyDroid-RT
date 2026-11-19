package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AppShortcutItem extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public RawIconPngData icon;
    public byte[] iconPng;
    public String packageName;
    public int rank;
    public String shortLabel;
    public String shortcutId;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 31), new DataHeader(48, 32), new DataHeader(56, 46)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[3];
    }

    public AppShortcutItem() {
        super(56, 46);
    }

    public static AppShortcutItem decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AppShortcutItem appShortcutItem = new AppShortcutItem(i);
            appShortcutItem.shortcutId = decoder.readString(8, false);
            appShortcutItem.shortLabel = decoder.readString(16, false);
            appShortcutItem.iconPng = decoder.readBytes(24, 0, -1);
            if (i >= 31) {
                appShortcutItem.packageName = decoder.readString(32, true);
            }
            if (i >= 32) {
                int i2 = decoder.readInt(40);
                appShortcutItem.type = i2;
                AppShortcutItemType.validate(i2);
                appShortcutItem.rank = decoder.readInt(44);
            }
            if (i >= 46) {
                appShortcutItem.icon = RawIconPngData.decode(decoder.readPointer(48, true));
            }
            return appShortcutItem;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AppShortcutItem deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.shortcutId, 8, false);
        encoderAtDataOffset.encode(this.shortLabel, 16, false);
        encoderAtDataOffset.encode(this.iconPng, 24, 0, -1);
        encoderAtDataOffset.encode(this.packageName, 32, true);
        encoderAtDataOffset.encode(this.type, 40);
        encoderAtDataOffset.encode(this.rank, 44);
        encoderAtDataOffset.encode((Struct) this.icon, 48, true);
    }

    private AppShortcutItem(int i) {
        super(56, i);
    }

    public static AppShortcutItem deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

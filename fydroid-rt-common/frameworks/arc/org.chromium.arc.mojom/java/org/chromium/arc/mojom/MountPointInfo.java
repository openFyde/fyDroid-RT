// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class MountPointInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int deviceType;
    public String fsUuid;
    public String label;
    public int mountEvent;
    public String mountPath;
    public String sourcePath;
    public boolean visible;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0), new DataHeader(56, 2)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public MountPointInfo() {
        super(56, 2);
    }

    public static MountPointInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            MountPointInfo mountPointInfo = new MountPointInfo(i);
            int i2 = decoder.readInt(8);
            mountPointInfo.mountEvent = i2;
            MountEvent.validate(i2);
            int i3 = decoder.readInt(12);
            mountPointInfo.deviceType = i3;
            DeviceType.validate(i3);
            mountPointInfo.sourcePath = decoder.readString(16, false);
            mountPointInfo.mountPath = decoder.readString(24, false);
            mountPointInfo.fsUuid = decoder.readString(32, false);
            mountPointInfo.label = decoder.readString(40, false);
            if (i >= 2) {
                mountPointInfo.visible = decoder.readBoolean(48, 0);
            }
            return mountPointInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MountPointInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.mountEvent, 8);
        encoderAtDataOffset.encode(this.deviceType, 12);
        encoderAtDataOffset.encode(this.sourcePath, 16, false);
        encoderAtDataOffset.encode(this.mountPath, 24, false);
        encoderAtDataOffset.encode(this.fsUuid, 32, false);
        encoderAtDataOffset.encode(this.label, 40, false);
        encoderAtDataOffset.encode(this.visible, 48, 0);
    }

    private MountPointInfo(int i) {
        super(56, i);
    }

    public static MountPointInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

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

public final class PermissionState extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String details;
    public boolean granted;
    public boolean managed;
    public boolean oneTime;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 1), new DataHeader(24, 2)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public PermissionState() {
        super(24, 2);
    }

    public static PermissionState decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            PermissionState permissionState = new PermissionState(i);
            permissionState.granted = decoder.readBoolean(8, 0);
            permissionState.managed = decoder.readBoolean(8, 1);
            if (i >= 2) {
                permissionState.oneTime = decoder.readBoolean(8, 2);
            }
            if (i >= 1) {
                permissionState.details = decoder.readString(16, true);
            }
            return permissionState;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PermissionState deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.granted, 8, 0);
        encoderAtDataOffset.encode(this.managed, 8, 1);
        encoderAtDataOffset.encode(this.oneTime, 8, 2);
        encoderAtDataOffset.encode(this.details, 16, true);
    }

    private PermissionState(int i) {
        super(24, i);
    }

    public static PermissionState deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

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

public final class InstallationResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean isLaunchableApp;
    public String packageName;
    public boolean success;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(24, 62)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public InstallationResult() {
        super(24, 62);
    }

    public static InstallationResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            InstallationResult installationResult = new InstallationResult(i);
            installationResult.packageName = decoder.readString(8, false);
            installationResult.success = decoder.readBoolean(16, 0);
            if (i >= 62) {
                installationResult.isLaunchableApp = decoder.readBoolean(16, 1);
            }
            return installationResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static InstallationResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.packageName, 8, false);
        encoderAtDataOffset.encode(this.success, 16, 0);
        encoderAtDataOffset.encode(this.isLaunchableApp, 16, 1);
    }

    private InstallationResult(int i) {
        super(24, i);
    }

    public static InstallationResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

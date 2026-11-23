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

public final class WebApkInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String apkVersion;
    public String iconHash;
    public String manifestUrl;
    public String name;
    public String packageName;
    public String scope;
    public WebShareTargetInfo shareInfo;
    public String shellApkVersion;
    public String startUrl;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(80, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public WebApkInfo() {
        super(80, 0);
    }

    public static WebApkInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            WebApkInfo webApkInfo = new WebApkInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            webApkInfo.packageName = decoder.readString(8, false);
            webApkInfo.apkVersion = decoder.readString(16, false);
            webApkInfo.shellApkVersion = decoder.readString(24, false);
            webApkInfo.manifestUrl = decoder.readString(32, false);
            webApkInfo.name = decoder.readString(40, false);
            webApkInfo.startUrl = decoder.readString(48, false);
            webApkInfo.scope = decoder.readString(56, false);
            webApkInfo.iconHash = decoder.readString(64, false);
            webApkInfo.shareInfo = WebShareTargetInfo.decode(decoder.readPointer(72, true));
            return webApkInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WebApkInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.packageName, 8, false);
        encoderAtDataOffset.encode(this.apkVersion, 16, false);
        encoderAtDataOffset.encode(this.shellApkVersion, 24, false);
        encoderAtDataOffset.encode(this.manifestUrl, 32, false);
        encoderAtDataOffset.encode(this.name, 40, false);
        encoderAtDataOffset.encode(this.startUrl, 48, false);
        encoderAtDataOffset.encode(this.scope, 56, false);
        encoderAtDataOffset.encode(this.iconHash, 64, false);
        encoderAtDataOffset.encode((Struct) this.shareInfo, 72, true);
    }

    private WebApkInfo(int i) {
        super(80, i);
    }

    public static WebApkInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

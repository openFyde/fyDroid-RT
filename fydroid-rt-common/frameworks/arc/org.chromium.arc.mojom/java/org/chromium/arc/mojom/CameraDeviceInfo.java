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

public final class CameraDeviceInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String devicePath;
    public int framesToSkipAfterStreamon;
    public float horizontalViewAngle169;
    public float horizontalViewAngle43;
    public int lensFacing;
    public float[] lensInfoAvailableFocalLengths;
    public float lensInfoMinimumFocusDistance;
    public float lensInfoOptimalFocusDistance;
    public int sensorOrientation;
    public String usbPid;
    public String usbVid;
    public float verticalViewAngle169;
    public float verticalViewAngle43;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(80, 1)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public CameraDeviceInfo() {
        super(80, 1);
    }

    public static CameraDeviceInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            CameraDeviceInfo cameraDeviceInfo = new CameraDeviceInfo(i);
            cameraDeviceInfo.devicePath = decoder.readString(8, false);
            cameraDeviceInfo.usbVid = decoder.readString(16, false);
            cameraDeviceInfo.usbPid = decoder.readString(24, false);
            if (i >= 1) {
                cameraDeviceInfo.lensFacing = decoder.readInt(32);
                cameraDeviceInfo.sensorOrientation = decoder.readInt(36);
                cameraDeviceInfo.framesToSkipAfterStreamon = decoder.readInt(40);
                cameraDeviceInfo.horizontalViewAngle169 = decoder.readFloat(44);
                cameraDeviceInfo.horizontalViewAngle43 = decoder.readFloat(48);
                cameraDeviceInfo.lensInfoMinimumFocusDistance = decoder.readFloat(52);
                cameraDeviceInfo.lensInfoAvailableFocalLengths = decoder.readFloats(56, 1, -1);
                cameraDeviceInfo.lensInfoOptimalFocusDistance = decoder.readFloat(64);
                cameraDeviceInfo.verticalViewAngle169 = decoder.readFloat(68);
                cameraDeviceInfo.verticalViewAngle43 = decoder.readFloat(72);
            }
            return cameraDeviceInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CameraDeviceInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.devicePath, 8, false);
        encoderAtDataOffset.encode(this.usbVid, 16, false);
        encoderAtDataOffset.encode(this.usbPid, 24, false);
        encoderAtDataOffset.encode(this.lensFacing, 32);
        encoderAtDataOffset.encode(this.sensorOrientation, 36);
        encoderAtDataOffset.encode(this.framesToSkipAfterStreamon, 40);
        encoderAtDataOffset.encode(this.horizontalViewAngle169, 44);
        encoderAtDataOffset.encode(this.horizontalViewAngle43, 48);
        encoderAtDataOffset.encode(this.lensInfoMinimumFocusDistance, 52);
        encoderAtDataOffset.encode(this.lensInfoAvailableFocalLengths, 56, 1, -1);
        encoderAtDataOffset.encode(this.lensInfoOptimalFocusDistance, 64);
        encoderAtDataOffset.encode(this.verticalViewAngle169, 68);
        encoderAtDataOffset.encode(this.verticalViewAngle43, 72);
    }

    private CameraDeviceInfo(int i) {
        super(80, i);
    }

    public static CameraDeviceInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

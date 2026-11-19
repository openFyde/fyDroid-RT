// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ArcPackageInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<Integer, Boolean> deprecatedPermissions;
    public boolean deprecatedSystem;
    public boolean gameControlsOptOut;
    public long lastBackupAndroidId;
    public long lastBackupTime;
    public PackageLocaleInfo localeInfo;
    public String packageName;
    public int packageVersion;
    public Map<Integer, PermissionState> permissionStates;
    public boolean preinstalled;
    public int priority;
    public boolean sync;
    public String versionName;
    public boolean vpnProvider;
    public WebAppInfo webAppInfo;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(40, 11), new DataHeader(40, 25), new DataHeader(48, 38), new DataHeader(56, 41), new DataHeader(64, 43), new DataHeader(72, 51), new DataHeader(72, 55), new DataHeader(80, 56), new DataHeader(88, 60), new DataHeader(88, 61)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[10];
    }

    public ArcPackageInfo() {
        super(88, 61);
    }

    public static ArcPackageInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            ArcPackageInfo arcPackageInfo = new ArcPackageInfo(i);
            arcPackageInfo.packageName = decoder.readString(8, false);
            arcPackageInfo.packageVersion = decoder.readInt(16);
            arcPackageInfo.sync = decoder.readBoolean(20, 0);
            if (i >= 11) {
                arcPackageInfo.deprecatedSystem = decoder.readBoolean(20, 1);
            }
            if (i >= 25) {
                arcPackageInfo.vpnProvider = decoder.readBoolean(20, 2);
            }
            if (i >= 55) {
                arcPackageInfo.preinstalled = decoder.readBoolean(20, 3);
            }
            if (i >= 61) {
                arcPackageInfo.gameControlsOptOut = decoder.readBoolean(20, 4);
            }
            arcPackageInfo.lastBackupAndroidId = decoder.readLong(24);
            arcPackageInfo.lastBackupTime = decoder.readLong(32);
            if (i >= 38) {
                arcPackageInfo.webAppInfo = WebAppInfo.decode(decoder.readPointer(40, true));
            }
            if (i >= 41) {
                Decoder pointer = decoder.readPointer(48, true);
                if (pointer == null) {
                    arcPackageInfo.deprecatedPermissions = null;
                } else {
                    pointer.readDataHeaderForMap();
                    int[] ints = pointer.readInts(8, 0, -1);
                    for (int i2 : ints) {
                        AppPermission.validate(i2);
                    }
                    boolean[] booleans = pointer.readBooleans(16, 0, ints.length);
                    arcPackageInfo.deprecatedPermissions = new HashMap();
                    for (int i3 = 0; i3 < ints.length; i3++) {
                        arcPackageInfo.deprecatedPermissions.put(Integer.valueOf(ints[i3]), Boolean.valueOf(booleans[i3]));
                    }
                }
            }
            if (i >= 43) {
                Decoder pointer2 = decoder.readPointer(56, true);
                if (pointer2 == null) {
                    arcPackageInfo.permissionStates = null;
                } else {
                    pointer2.readDataHeaderForMap();
                    int[] ints2 = pointer2.readInts(8, 0, -1);
                    for (int i4 : ints2) {
                        AppPermission.validate(i4);
                    }
                    Decoder pointer3 = pointer2.readPointer(16, false);
                    DataHeader dataHeaderForPointerArray = pointer3.readDataHeaderForPointerArray(ints2.length);
                    PermissionState[] permissionStateArr = new PermissionState[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray.elementsOrVersion; i5++) {
                        permissionStateArr[i5] = PermissionState.decode(GeneratedOutlineSupport.outline2(i5, 8, 8, pointer3, false));
                    }
                    arcPackageInfo.permissionStates = new HashMap();
                    for (int i6 = 0; i6 < ints2.length; i6++) {
                        arcPackageInfo.permissionStates.put(Integer.valueOf(ints2[i6]), permissionStateArr[i6]);
                    }
                }
            }
            if (i >= 51) {
                arcPackageInfo.versionName = decoder.readString(64, true);
            }
            if (i >= 56) {
                int i7 = decoder.readInt(72);
                arcPackageInfo.priority = i7;
                InstallPriority.validate(i7);
            }
            if (i >= 60) {
                arcPackageInfo.localeInfo = PackageLocaleInfo.decode(decoder.readPointer(80, true));
            }
            return arcPackageInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcPackageInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.packageName, 8, false);
        encoderAtDataOffset.encode(this.packageVersion, 16);
        encoderAtDataOffset.encode(this.sync, 20, 0);
        encoderAtDataOffset.encode(this.deprecatedSystem, 20, 1);
        encoderAtDataOffset.encode(this.vpnProvider, 20, 2);
        encoderAtDataOffset.encode(this.preinstalled, 20, 3);
        encoderAtDataOffset.encode(this.gameControlsOptOut, 20, 4);
        encoderAtDataOffset.encode(this.lastBackupAndroidId, 24);
        encoderAtDataOffset.encode(this.lastBackupTime, 32);
        encoderAtDataOffset.encode((Struct) this.webAppInfo, 40, true);
        if (this.deprecatedPermissions == null) {
            encoderAtDataOffset.encodeNullPointer(48, true);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(48);
            int size = this.deprecatedPermissions.size();
            int[] iArr = new int[size];
            boolean[] zArr = new boolean[size];
            int i = 0;
            for (Map.Entry<Integer, Boolean> entry : this.deprecatedPermissions.entrySet()) {
                iArr[i] = entry.getKey().intValue();
                zArr[i] = entry.getValue().booleanValue();
                i++;
            }
            encoderEncoderForMap.encode(iArr, 8, 0, -1);
            encoderEncoderForMap.encode(zArr, 16, 0, -1);
        }
        if (this.permissionStates == null) {
            encoderAtDataOffset.encodeNullPointer(56, true);
        } else {
            Encoder encoderEncoderForMap2 = encoderAtDataOffset.encoderForMap(56);
            int size2 = this.permissionStates.size();
            int[] iArr2 = new int[size2];
            PermissionState[] permissionStateArr = new PermissionState[size2];
            int i2 = 0;
            for (Map.Entry<Integer, PermissionState> entry2 : this.permissionStates.entrySet()) {
                iArr2[i2] = entry2.getKey().intValue();
                permissionStateArr[i2] = entry2.getValue();
                i2++;
            }
            encoderEncoderForMap2.encode(iArr2, 8, 0, -1);
            Encoder encoderEncodePointerArray = encoderEncoderForMap2.encodePointerArray(size2, 16, -1);
            for (int i3 = 0; i3 < size2; i3++) {
                encoderEncodePointerArray.encode((Struct) permissionStateArr[i3], (i3 * 8) + 8, false);
            }
        }
        encoderAtDataOffset.encode(this.versionName, 64, true);
        encoderAtDataOffset.encode(this.priority, 72);
        encoderAtDataOffset.encode((Struct) this.localeInfo, 80, true);
    }

    private ArcPackageInfo(int i) {
        super(88, i);
    }

    public static ArcPackageInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

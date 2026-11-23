// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.keymint.KeyMintInstance;
import org.chromium.ax.android.mojom.AccessibilityHelperInstance;
import org.chromium.chromeos.payments.mojom.PaymentAppInstance;
import org.chromium.cros.mojom.AndroidControlSceneMode;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class ArcBridgeHost_Internal {
    public static final Interface.Manager<ArcBridgeHost, ArcBridgeHost.Proxy> MANAGER = new Interface.Manager<ArcBridgeHost, ArcBridgeHost.Proxy>() { // from class: org.chromium.arc.mojom.ArcBridgeHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ArcBridgeHost[] buildArray(int i) {
            return new ArcBridgeHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ArcBridgeHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ArcBridgeHost> buildStub(Core core, ArcBridgeHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ArcBridgeHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 67;
        }
    };

    static final class ArcBridgeHostOnAccessibilityHelperInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccessibilityHelperInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnAccessibilityHelperInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnAccessibilityHelperInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnAccessibilityHelperInstanceReadyParams arcBridgeHostOnAccessibilityHelperInstanceReadyParams = new ArcBridgeHostOnAccessibilityHelperInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnAccessibilityHelperInstanceReadyParams.instanceRemote = (AccessibilityHelperInstance) decoder.readServiceInterface(8, false, AccessibilityHelperInstance.MANAGER);
                return arcBridgeHostOnAccessibilityHelperInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<AccessibilityHelperInstance, ?>) AccessibilityHelperInstance.MANAGER);
        }

        private ArcBridgeHostOnAccessibilityHelperInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnAdbdMonitorInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AdbdMonitorInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnAdbdMonitorInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnAdbdMonitorInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnAdbdMonitorInstanceReadyParams arcBridgeHostOnAdbdMonitorInstanceReadyParams = new ArcBridgeHostOnAdbdMonitorInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnAdbdMonitorInstanceReadyParams.instanceRemote = (AdbdMonitorInstance) decoder.readServiceInterface(8, false, AdbdMonitorInstance.MANAGER);
                return arcBridgeHostOnAdbdMonitorInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<AdbdMonitorInstance, ?>) AdbdMonitorInstance.MANAGER);
        }

        private ArcBridgeHostOnAdbdMonitorInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnAppInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnAppInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnAppInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnAppInstanceReadyParams arcBridgeHostOnAppInstanceReadyParams = new ArcBridgeHostOnAppInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnAppInstanceReadyParams.instanceRemote = (AppInstance) decoder.readServiceInterface(8, false, AppInstance.MANAGER);
                return arcBridgeHostOnAppInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<AppInstance, ?>) AppInstance.MANAGER);
        }

        private ArcBridgeHostOnAppInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnAppPermissionsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppPermissionsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnAppPermissionsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnAppPermissionsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnAppPermissionsInstanceReadyParams arcBridgeHostOnAppPermissionsInstanceReadyParams = new ArcBridgeHostOnAppPermissionsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnAppPermissionsInstanceReadyParams.instanceRemote = (AppPermissionsInstance) decoder.readServiceInterface(8, false, AppPermissionsInstance.MANAGER);
                return arcBridgeHostOnAppPermissionsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<AppPermissionsInstance, ?>) AppPermissionsInstance.MANAGER);
        }

        private ArcBridgeHostOnAppPermissionsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnAppfuseInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppfuseInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnAppfuseInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnAppfuseInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnAppfuseInstanceReadyParams arcBridgeHostOnAppfuseInstanceReadyParams = new ArcBridgeHostOnAppfuseInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnAppfuseInstanceReadyParams.instanceRemote = (AppfuseInstance) decoder.readServiceInterface(8, false, AppfuseInstance.MANAGER);
                return arcBridgeHostOnAppfuseInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<AppfuseInstance, ?>) AppfuseInstance.MANAGER);
        }

        private ArcBridgeHostOnAppfuseInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnArcWifiInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcWifiInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnArcWifiInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnArcWifiInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnArcWifiInstanceReadyParams arcBridgeHostOnArcWifiInstanceReadyParams = new ArcBridgeHostOnArcWifiInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnArcWifiInstanceReadyParams.instanceRemote = (ArcWifiInstance) decoder.readServiceInterface(8, false, ArcWifiInstance.MANAGER);
                return arcBridgeHostOnArcWifiInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<ArcWifiInstance, ?>) ArcWifiInstance.MANAGER);
        }

        private ArcBridgeHostOnArcWifiInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnAudioInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AudioInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnAudioInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnAudioInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnAudioInstanceReadyParams arcBridgeHostOnAudioInstanceReadyParams = new ArcBridgeHostOnAudioInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnAudioInstanceReadyParams.instanceRemote = (AudioInstance) decoder.readServiceInterface(8, false, AudioInstance.MANAGER);
                return arcBridgeHostOnAudioInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<AudioInstance, ?>) AudioInstance.MANAGER);
        }

        private ArcBridgeHostOnAudioInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnAuthInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AuthInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnAuthInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnAuthInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnAuthInstanceReadyParams arcBridgeHostOnAuthInstanceReadyParams = new ArcBridgeHostOnAuthInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnAuthInstanceReadyParams.instanceRemote = (AuthInstance) decoder.readServiceInterface(8, false, AuthInstance.MANAGER);
                return arcBridgeHostOnAuthInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<AuthInstance, ?>) AuthInstance.MANAGER);
        }

        private ArcBridgeHostOnAuthInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnBackupSettingsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BackupSettingsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnBackupSettingsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnBackupSettingsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnBackupSettingsInstanceReadyParams arcBridgeHostOnBackupSettingsInstanceReadyParams = new ArcBridgeHostOnBackupSettingsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnBackupSettingsInstanceReadyParams.instanceRemote = (BackupSettingsInstance) decoder.readServiceInterface(8, false, BackupSettingsInstance.MANAGER);
                return arcBridgeHostOnBackupSettingsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<BackupSettingsInstance, ?>) BackupSettingsInstance.MANAGER);
        }

        private ArcBridgeHostOnBackupSettingsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnBluetoothInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnBluetoothInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnBluetoothInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnBluetoothInstanceReadyParams arcBridgeHostOnBluetoothInstanceReadyParams = new ArcBridgeHostOnBluetoothInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnBluetoothInstanceReadyParams.instanceRemote = (BluetoothInstance) decoder.readServiceInterface(8, false, BluetoothInstance.MANAGER);
                return arcBridgeHostOnBluetoothInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<BluetoothInstance, ?>) BluetoothInstance.MANAGER);
        }

        private ArcBridgeHostOnBluetoothInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BootPhaseMonitorInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams arcBridgeHostOnBootPhaseMonitorInstanceReadyParams = new ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnBootPhaseMonitorInstanceReadyParams.instanceRemote = (BootPhaseMonitorInstance) decoder.readServiceInterface(8, false, BootPhaseMonitorInstance.MANAGER);
                return arcBridgeHostOnBootPhaseMonitorInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<BootPhaseMonitorInstance, ?>) BootPhaseMonitorInstance.MANAGER);
        }

        private ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnCameraInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnCameraInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnCameraInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnCameraInstanceReadyParams arcBridgeHostOnCameraInstanceReadyParams = new ArcBridgeHostOnCameraInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnCameraInstanceReadyParams.instanceRemote = (CameraInstance) decoder.readServiceInterface(8, false, CameraInstance.MANAGER);
                return arcBridgeHostOnCameraInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<CameraInstance, ?>) CameraInstance.MANAGER);
        }

        private ArcBridgeHostOnCameraInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ChromeFeatureFlagsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams arcBridgeHostOnChromeFeatureFlagsInstanceReadyParams = new ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnChromeFeatureFlagsInstanceReadyParams.instanceRemote = (ChromeFeatureFlagsInstance) decoder.readServiceInterface(8, false, ChromeFeatureFlagsInstance.MANAGER);
                return arcBridgeHostOnChromeFeatureFlagsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<ChromeFeatureFlagsInstance, ?>) ChromeFeatureFlagsInstance.MANAGER);
        }

        private ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnCompatibilityModeInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CompatibilityModeInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnCompatibilityModeInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnCompatibilityModeInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnCompatibilityModeInstanceReadyParams arcBridgeHostOnCompatibilityModeInstanceReadyParams = new ArcBridgeHostOnCompatibilityModeInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnCompatibilityModeInstanceReadyParams.instanceRemote = (CompatibilityModeInstance) decoder.readServiceInterface(8, false, CompatibilityModeInstance.MANAGER);
                return arcBridgeHostOnCompatibilityModeInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<CompatibilityModeInstance, ?>) CompatibilityModeInstance.MANAGER);
        }

        private ArcBridgeHostOnCompatibilityModeInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnCrashCollectorInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CrashCollectorInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnCrashCollectorInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnCrashCollectorInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnCrashCollectorInstanceReadyParams arcBridgeHostOnCrashCollectorInstanceReadyParams = new ArcBridgeHostOnCrashCollectorInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnCrashCollectorInstanceReadyParams.instanceRemote = (CrashCollectorInstance) decoder.readServiceInterface(8, false, CrashCollectorInstance.MANAGER);
                return arcBridgeHostOnCrashCollectorInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<CrashCollectorInstance, ?>) CrashCollectorInstance.MANAGER);
        }

        private ArcBridgeHostOnCrashCollectorInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnDigitalGoodsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public DigitalGoodsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnDigitalGoodsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnDigitalGoodsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnDigitalGoodsInstanceReadyParams arcBridgeHostOnDigitalGoodsInstanceReadyParams = new ArcBridgeHostOnDigitalGoodsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnDigitalGoodsInstanceReadyParams.instanceRemote = (DigitalGoodsInstance) decoder.readServiceInterface(8, false, DigitalGoodsInstance.MANAGER);
                return arcBridgeHostOnDigitalGoodsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<DigitalGoodsInstance, ?>) DigitalGoodsInstance.MANAGER);
        }

        private ArcBridgeHostOnDigitalGoodsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnDiskSpaceInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public DiskSpaceInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnDiskSpaceInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnDiskSpaceInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnDiskSpaceInstanceReadyParams arcBridgeHostOnDiskSpaceInstanceReadyParams = new ArcBridgeHostOnDiskSpaceInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnDiskSpaceInstanceReadyParams.instanceRemote = (DiskSpaceInstance) decoder.readServiceInterface(8, false, DiskSpaceInstance.MANAGER);
                return arcBridgeHostOnDiskSpaceInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<DiskSpaceInstance, ?>) DiskSpaceInstance.MANAGER);
        }

        private ArcBridgeHostOnDiskSpaceInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnEnterpriseReportingInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public EnterpriseReportingInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnEnterpriseReportingInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnEnterpriseReportingInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnEnterpriseReportingInstanceReadyParams arcBridgeHostOnEnterpriseReportingInstanceReadyParams = new ArcBridgeHostOnEnterpriseReportingInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnEnterpriseReportingInstanceReadyParams.instanceRemote = (EnterpriseReportingInstance) decoder.readServiceInterface(8, false, EnterpriseReportingInstance.MANAGER);
                return arcBridgeHostOnEnterpriseReportingInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<EnterpriseReportingInstance, ?>) EnterpriseReportingInstance.MANAGER);
        }

        private ArcBridgeHostOnEnterpriseReportingInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnErrorNotificationInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ErrorNotificationInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnErrorNotificationInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnErrorNotificationInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnErrorNotificationInstanceReadyParams arcBridgeHostOnErrorNotificationInstanceReadyParams = new ArcBridgeHostOnErrorNotificationInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnErrorNotificationInstanceReadyParams.instanceRemote = (ErrorNotificationInstance) decoder.readServiceInterface(8, false, ErrorNotificationInstance.MANAGER);
                return arcBridgeHostOnErrorNotificationInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<ErrorNotificationInstance, ?>) ErrorNotificationInstance.MANAGER);
        }

        private ArcBridgeHostOnErrorNotificationInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnFileSystemInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FileSystemInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnFileSystemInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnFileSystemInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnFileSystemInstanceReadyParams arcBridgeHostOnFileSystemInstanceReadyParams = new ArcBridgeHostOnFileSystemInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnFileSystemInstanceReadyParams.instanceRemote = (FileSystemInstance) decoder.readServiceInterface(8, false, FileSystemInstance.MANAGER);
                return arcBridgeHostOnFileSystemInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<FileSystemInstance, ?>) FileSystemInstance.MANAGER);
        }

        private ArcBridgeHostOnFileSystemInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnIioSensorInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IioSensorInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnIioSensorInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnIioSensorInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnIioSensorInstanceReadyParams arcBridgeHostOnIioSensorInstanceReadyParams = new ArcBridgeHostOnIioSensorInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnIioSensorInstanceReadyParams.instanceRemote = (IioSensorInstance) decoder.readServiceInterface(8, false, IioSensorInstance.MANAGER);
                return arcBridgeHostOnIioSensorInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<IioSensorInstance, ?>) IioSensorInstance.MANAGER);
        }

        private ArcBridgeHostOnIioSensorInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnImeInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ImeInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnImeInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnImeInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnImeInstanceReadyParams arcBridgeHostOnImeInstanceReadyParams = new ArcBridgeHostOnImeInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnImeInstanceReadyParams.instanceRemote = (ImeInstance) decoder.readServiceInterface(8, false, ImeInstance.MANAGER);
                return arcBridgeHostOnImeInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<ImeInstance, ?>) ImeInstance.MANAGER);
        }

        private ArcBridgeHostOnImeInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnInputMethodManagerInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InputMethodManagerInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnInputMethodManagerInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnInputMethodManagerInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnInputMethodManagerInstanceReadyParams arcBridgeHostOnInputMethodManagerInstanceReadyParams = new ArcBridgeHostOnInputMethodManagerInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnInputMethodManagerInstanceReadyParams.instanceRemote = (InputMethodManagerInstance) decoder.readServiceInterface(8, false, InputMethodManagerInstance.MANAGER);
                return arcBridgeHostOnInputMethodManagerInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<InputMethodManagerInstance, ?>) InputMethodManagerInstance.MANAGER);
        }

        private ArcBridgeHostOnInputMethodManagerInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnIntentHelperInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentHelperInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnIntentHelperInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnIntentHelperInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnIntentHelperInstanceReadyParams arcBridgeHostOnIntentHelperInstanceReadyParams = new ArcBridgeHostOnIntentHelperInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnIntentHelperInstanceReadyParams.instanceRemote = (IntentHelperInstance) decoder.readServiceInterface(8, false, IntentHelperInstance.MANAGER);
                return arcBridgeHostOnIntentHelperInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<IntentHelperInstance, ?>) IntentHelperInstance.MANAGER);
        }

        private ArcBridgeHostOnIntentHelperInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnKeyMintInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeyMintInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnKeyMintInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnKeyMintInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnKeyMintInstanceReadyParams arcBridgeHostOnKeyMintInstanceReadyParams = new ArcBridgeHostOnKeyMintInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnKeyMintInstanceReadyParams.instanceRemote = (KeyMintInstance) decoder.readServiceInterface(8, false, KeyMintInstance.MANAGER);
                return arcBridgeHostOnKeyMintInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<KeyMintInstance, ?>) KeyMintInstance.MANAGER);
        }

        private ArcBridgeHostOnKeyMintInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnKeymasterInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeymasterInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnKeymasterInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnKeymasterInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnKeymasterInstanceReadyParams arcBridgeHostOnKeymasterInstanceReadyParams = new ArcBridgeHostOnKeymasterInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnKeymasterInstanceReadyParams.instanceRemote = (KeymasterInstance) decoder.readServiceInterface(8, false, KeymasterInstance.MANAGER);
                return arcBridgeHostOnKeymasterInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<KeymasterInstance, ?>) KeymasterInstance.MANAGER);
        }

        private ArcBridgeHostOnKeymasterInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnMediaSessionInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnMediaSessionInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnMediaSessionInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnMediaSessionInstanceReadyParams arcBridgeHostOnMediaSessionInstanceReadyParams = new ArcBridgeHostOnMediaSessionInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnMediaSessionInstanceReadyParams.instanceRemote = (MediaSessionInstance) decoder.readServiceInterface(8, false, MediaSessionInstance.MANAGER);
                return arcBridgeHostOnMediaSessionInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<MediaSessionInstance, ?>) MediaSessionInstance.MANAGER);
        }

        private ArcBridgeHostOnMediaSessionInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnMemoryInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MemoryInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnMemoryInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnMemoryInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnMemoryInstanceReadyParams arcBridgeHostOnMemoryInstanceReadyParams = new ArcBridgeHostOnMemoryInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnMemoryInstanceReadyParams.instanceRemote = (MemoryInstance) decoder.readServiceInterface(8, false, MemoryInstance.MANAGER);
                return arcBridgeHostOnMemoryInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<MemoryInstance, ?>) MemoryInstance.MANAGER);
        }

        private ArcBridgeHostOnMemoryInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnMetricsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MetricsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnMetricsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnMetricsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnMetricsInstanceReadyParams arcBridgeHostOnMetricsInstanceReadyParams = new ArcBridgeHostOnMetricsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnMetricsInstanceReadyParams.instanceRemote = (MetricsInstance) decoder.readServiceInterface(8, false, MetricsInstance.MANAGER);
                return arcBridgeHostOnMetricsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<MetricsInstance, ?>) MetricsInstance.MANAGER);
        }

        private ArcBridgeHostOnMetricsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnMidisInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MidisInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnMidisInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnMidisInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnMidisInstanceReadyParams arcBridgeHostOnMidisInstanceReadyParams = new ArcBridgeHostOnMidisInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnMidisInstanceReadyParams.instanceRemote = (MidisInstance) decoder.readServiceInterface(8, false, MidisInstance.MANAGER);
                return arcBridgeHostOnMidisInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<MidisInstance, ?>) MidisInstance.MANAGER);
        }

        private ArcBridgeHostOnMidisInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnNearbyShareInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NearbyShareInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnNearbyShareInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnNearbyShareInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnNearbyShareInstanceReadyParams arcBridgeHostOnNearbyShareInstanceReadyParams = new ArcBridgeHostOnNearbyShareInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnNearbyShareInstanceReadyParams.instanceRemote = (NearbyShareInstance) decoder.readServiceInterface(8, false, NearbyShareInstance.MANAGER);
                return arcBridgeHostOnNearbyShareInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<NearbyShareInstance, ?>) NearbyShareInstance.MANAGER);
        }

        private ArcBridgeHostOnNearbyShareInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnNetInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NetInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnNetInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnNetInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnNetInstanceReadyParams arcBridgeHostOnNetInstanceReadyParams = new ArcBridgeHostOnNetInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnNetInstanceReadyParams.instanceRemote = (NetInstance) decoder.readServiceInterface(8, false, NetInstance.MANAGER);
                return arcBridgeHostOnNetInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<NetInstance, ?>) NetInstance.MANAGER);
        }

        private ArcBridgeHostOnNetInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnNotificationsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NotificationsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnNotificationsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnNotificationsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnNotificationsInstanceReadyParams arcBridgeHostOnNotificationsInstanceReadyParams = new ArcBridgeHostOnNotificationsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnNotificationsInstanceReadyParams.instanceRemote = (NotificationsInstance) decoder.readServiceInterface(8, false, NotificationsInstance.MANAGER);
                return arcBridgeHostOnNotificationsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<NotificationsInstance, ?>) NotificationsInstance.MANAGER);
        }

        private ArcBridgeHostOnNotificationsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnObbMounterInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ObbMounterInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnObbMounterInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnObbMounterInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnObbMounterInstanceReadyParams arcBridgeHostOnObbMounterInstanceReadyParams = new ArcBridgeHostOnObbMounterInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnObbMounterInstanceReadyParams.instanceRemote = (ObbMounterInstance) decoder.readServiceInterface(8, false, ObbMounterInstance.MANAGER);
                return arcBridgeHostOnObbMounterInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<ObbMounterInstance, ?>) ObbMounterInstance.MANAGER);
        }

        private ArcBridgeHostOnObbMounterInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnOemCryptoInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OemCryptoInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnOemCryptoInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnOemCryptoInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnOemCryptoInstanceReadyParams arcBridgeHostOnOemCryptoInstanceReadyParams = new ArcBridgeHostOnOemCryptoInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnOemCryptoInstanceReadyParams.instanceRemote = (OemCryptoInstance) decoder.readServiceInterface(8, false, OemCryptoInstance.MANAGER);
                return arcBridgeHostOnOemCryptoInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<OemCryptoInstance, ?>) OemCryptoInstance.MANAGER);
        }

        private ArcBridgeHostOnOemCryptoInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnPaymentAppInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PaymentAppInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnPaymentAppInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnPaymentAppInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnPaymentAppInstanceReadyParams arcBridgeHostOnPaymentAppInstanceReadyParams = new ArcBridgeHostOnPaymentAppInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnPaymentAppInstanceReadyParams.instanceRemote = (PaymentAppInstance) decoder.readServiceInterface(8, false, PaymentAppInstance.MANAGER);
                return arcBridgeHostOnPaymentAppInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<PaymentAppInstance, ?>) PaymentAppInstance.MANAGER);
        }

        private ArcBridgeHostOnPaymentAppInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnPipInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PipInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnPipInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnPipInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnPipInstanceReadyParams arcBridgeHostOnPipInstanceReadyParams = new ArcBridgeHostOnPipInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnPipInstanceReadyParams.instanceRemote = (PipInstance) decoder.readServiceInterface(8, false, PipInstance.MANAGER);
                return arcBridgeHostOnPipInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<PipInstance, ?>) PipInstance.MANAGER);
        }

        private ArcBridgeHostOnPipInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnPolicyInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PolicyInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnPolicyInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnPolicyInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnPolicyInstanceReadyParams arcBridgeHostOnPolicyInstanceReadyParams = new ArcBridgeHostOnPolicyInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnPolicyInstanceReadyParams.instanceRemote = (PolicyInstance) decoder.readServiceInterface(8, false, PolicyInstance.MANAGER);
                return arcBridgeHostOnPolicyInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<PolicyInstance, ?>) PolicyInstance.MANAGER);
        }

        private ArcBridgeHostOnPolicyInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnPowerInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PowerInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnPowerInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnPowerInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnPowerInstanceReadyParams arcBridgeHostOnPowerInstanceReadyParams = new ArcBridgeHostOnPowerInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnPowerInstanceReadyParams.instanceRemote = (PowerInstance) decoder.readServiceInterface(8, false, PowerInstance.MANAGER);
                return arcBridgeHostOnPowerInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<PowerInstance, ?>) PowerInstance.MANAGER);
        }

        private ArcBridgeHostOnPowerInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnPrintSpoolerInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PrintSpoolerInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnPrintSpoolerInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnPrintSpoolerInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnPrintSpoolerInstanceReadyParams arcBridgeHostOnPrintSpoolerInstanceReadyParams = new ArcBridgeHostOnPrintSpoolerInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnPrintSpoolerInstanceReadyParams.instanceRemote = (PrintSpoolerInstance) decoder.readServiceInterface(8, false, PrintSpoolerInstance.MANAGER);
                return arcBridgeHostOnPrintSpoolerInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<PrintSpoolerInstance, ?>) PrintSpoolerInstance.MANAGER);
        }

        private ArcBridgeHostOnPrintSpoolerInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnPrivacyItemsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PrivacyItemsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnPrivacyItemsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnPrivacyItemsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnPrivacyItemsInstanceReadyParams arcBridgeHostOnPrivacyItemsInstanceReadyParams = new ArcBridgeHostOnPrivacyItemsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnPrivacyItemsInstanceReadyParams.instanceRemote = (PrivacyItemsInstance) decoder.readServiceInterface(8, false, PrivacyItemsInstance.MANAGER);
                return arcBridgeHostOnPrivacyItemsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<PrivacyItemsInstance, ?>) PrivacyItemsInstance.MANAGER);
        }

        private ArcBridgeHostOnPrivacyItemsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnProcessInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ProcessInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnProcessInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnProcessInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnProcessInstanceReadyParams arcBridgeHostOnProcessInstanceReadyParams = new ArcBridgeHostOnProcessInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnProcessInstanceReadyParams.instanceRemote = (ProcessInstance) decoder.readServiceInterface(8, false, ProcessInstance.MANAGER);
                return arcBridgeHostOnProcessInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<ProcessInstance, ?>) ProcessInstance.MANAGER);
        }

        private ArcBridgeHostOnProcessInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnScreenCaptureInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ScreenCaptureInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnScreenCaptureInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnScreenCaptureInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnScreenCaptureInstanceReadyParams arcBridgeHostOnScreenCaptureInstanceReadyParams = new ArcBridgeHostOnScreenCaptureInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnScreenCaptureInstanceReadyParams.instanceRemote = (ScreenCaptureInstance) decoder.readServiceInterface(8, false, ScreenCaptureInstance.MANAGER);
                return arcBridgeHostOnScreenCaptureInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<ScreenCaptureInstance, ?>) ScreenCaptureInstance.MANAGER);
        }

        private ArcBridgeHostOnScreenCaptureInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnSharesheetInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SharesheetInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnSharesheetInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnSharesheetInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnSharesheetInstanceReadyParams arcBridgeHostOnSharesheetInstanceReadyParams = new ArcBridgeHostOnSharesheetInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnSharesheetInstanceReadyParams.instanceRemote = (SharesheetInstance) decoder.readServiceInterface(8, false, SharesheetInstance.MANAGER);
                return arcBridgeHostOnSharesheetInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<SharesheetInstance, ?>) SharesheetInstance.MANAGER);
        }

        private ArcBridgeHostOnSharesheetInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnSystemStateInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SystemStateInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnSystemStateInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnSystemStateInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnSystemStateInstanceReadyParams arcBridgeHostOnSystemStateInstanceReadyParams = new ArcBridgeHostOnSystemStateInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnSystemStateInstanceReadyParams.instanceRemote = (SystemStateInstance) decoder.readServiceInterface(8, false, SystemStateInstance.MANAGER);
                return arcBridgeHostOnSystemStateInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<SystemStateInstance, ?>) SystemStateInstance.MANAGER);
        }

        private ArcBridgeHostOnSystemStateInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnSystemUiInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SystemUiInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnSystemUiInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnSystemUiInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnSystemUiInstanceReadyParams arcBridgeHostOnSystemUiInstanceReadyParams = new ArcBridgeHostOnSystemUiInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnSystemUiInstanceReadyParams.instanceRemote = (SystemUiInstance) decoder.readServiceInterface(8, false, SystemUiInstance.MANAGER);
                return arcBridgeHostOnSystemUiInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<SystemUiInstance, ?>) SystemUiInstance.MANAGER);
        }

        private ArcBridgeHostOnSystemUiInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnTimerInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimerInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnTimerInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnTimerInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnTimerInstanceReadyParams arcBridgeHostOnTimerInstanceReadyParams = new ArcBridgeHostOnTimerInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnTimerInstanceReadyParams.instanceRemote = (TimerInstance) decoder.readServiceInterface(8, false, TimerInstance.MANAGER);
                return arcBridgeHostOnTimerInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<TimerInstance, ?>) TimerInstance.MANAGER);
        }

        private ArcBridgeHostOnTimerInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnTracingInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TracingInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnTracingInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnTracingInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnTracingInstanceReadyParams arcBridgeHostOnTracingInstanceReadyParams = new ArcBridgeHostOnTracingInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnTracingInstanceReadyParams.instanceRemote = (TracingInstance) decoder.readServiceInterface(8, false, TracingInstance.MANAGER);
                return arcBridgeHostOnTracingInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<TracingInstance, ?>) TracingInstance.MANAGER);
        }

        private ArcBridgeHostOnTracingInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnTtsInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TtsInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnTtsInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnTtsInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnTtsInstanceReadyParams arcBridgeHostOnTtsInstanceReadyParams = new ArcBridgeHostOnTtsInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnTtsInstanceReadyParams.instanceRemote = (TtsInstance) decoder.readServiceInterface(8, false, TtsInstance.MANAGER);
                return arcBridgeHostOnTtsInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<TtsInstance, ?>) TtsInstance.MANAGER);
        }

        private ArcBridgeHostOnTtsInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnUsbHostInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UsbHostInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnUsbHostInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnUsbHostInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnUsbHostInstanceReadyParams arcBridgeHostOnUsbHostInstanceReadyParams = new ArcBridgeHostOnUsbHostInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnUsbHostInstanceReadyParams.instanceRemote = (UsbHostInstance) decoder.readServiceInterface(8, false, UsbHostInstance.MANAGER);
                return arcBridgeHostOnUsbHostInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<UsbHostInstance, ?>) UsbHostInstance.MANAGER);
        }

        private ArcBridgeHostOnUsbHostInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnVideoInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnVideoInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnVideoInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnVideoInstanceReadyParams arcBridgeHostOnVideoInstanceReadyParams = new ArcBridgeHostOnVideoInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnVideoInstanceReadyParams.instanceRemote = (VideoInstance) decoder.readServiceInterface(8, false, VideoInstance.MANAGER);
                return arcBridgeHostOnVideoInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<VideoInstance, ?>) VideoInstance.MANAGER);
        }

        private ArcBridgeHostOnVideoInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnVolumeMounterInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VolumeMounterInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnVolumeMounterInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnVolumeMounterInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnVolumeMounterInstanceReadyParams arcBridgeHostOnVolumeMounterInstanceReadyParams = new ArcBridgeHostOnVolumeMounterInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnVolumeMounterInstanceReadyParams.instanceRemote = (VolumeMounterInstance) decoder.readServiceInterface(8, false, VolumeMounterInstance.MANAGER);
                return arcBridgeHostOnVolumeMounterInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<VolumeMounterInstance, ?>) VolumeMounterInstance.MANAGER);
        }

        private ArcBridgeHostOnVolumeMounterInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnWakeLockInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WakeLockInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnWakeLockInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnWakeLockInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnWakeLockInstanceReadyParams arcBridgeHostOnWakeLockInstanceReadyParams = new ArcBridgeHostOnWakeLockInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnWakeLockInstanceReadyParams.instanceRemote = (WakeLockInstance) decoder.readServiceInterface(8, false, WakeLockInstance.MANAGER);
                return arcBridgeHostOnWakeLockInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<WakeLockInstance, ?>) WakeLockInstance.MANAGER);
        }

        private ArcBridgeHostOnWakeLockInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnWallpaperInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WallpaperInstance instanceRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnWallpaperInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnWallpaperInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnWallpaperInstanceReadyParams arcBridgeHostOnWallpaperInstanceReadyParams = new ArcBridgeHostOnWallpaperInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnWallpaperInstanceReadyParams.instanceRemote = (WallpaperInstance) decoder.readServiceInterface(8, false, WallpaperInstance.MANAGER);
                return arcBridgeHostOnWallpaperInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instanceRemote, 8, false, (Interface.Manager<WallpaperInstance, ?>) WallpaperInstance.MANAGER);
        }

        private ArcBridgeHostOnWallpaperInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class ArcBridgeHostOnWebApkInstanceReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WebApkInstance instancePtr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcBridgeHostOnWebApkInstanceReadyParams() {
            super(16, 0);
        }

        public static ArcBridgeHostOnWebApkInstanceReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcBridgeHostOnWebApkInstanceReadyParams arcBridgeHostOnWebApkInstanceReadyParams = new ArcBridgeHostOnWebApkInstanceReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcBridgeHostOnWebApkInstanceReadyParams.instancePtr = (WebApkInstance) decoder.readServiceInterface(8, false, WebApkInstance.MANAGER);
                return arcBridgeHostOnWebApkInstanceReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.instancePtr, 8, false, (Interface.Manager<WebApkInstance, ?>) WebApkInstance.MANAGER);
        }

        private ArcBridgeHostOnWebApkInstanceReadyParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ArcBridgeHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onAccessibilityHelperInstanceReady(AccessibilityHelperInstance accessibilityHelperInstance) {
            ArcBridgeHostOnAccessibilityHelperInstanceReadyParams arcBridgeHostOnAccessibilityHelperInstanceReadyParams = new ArcBridgeHostOnAccessibilityHelperInstanceReadyParams();
            arcBridgeHostOnAccessibilityHelperInstanceReadyParams.instanceRemote = accessibilityHelperInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnAccessibilityHelperInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(AndroidControlSceneMode.ANDROID_CONTROL_SCENE_MODE_DEVICE_CUSTOM_END)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onAdbdMonitorInstanceReady(AdbdMonitorInstance adbdMonitorInstance) {
            ArcBridgeHostOnAdbdMonitorInstanceReadyParams arcBridgeHostOnAdbdMonitorInstanceReadyParams = new ArcBridgeHostOnAdbdMonitorInstanceReadyParams();
            arcBridgeHostOnAdbdMonitorInstanceReadyParams.instanceRemote = adbdMonitorInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnAdbdMonitorInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(158)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onAppInstanceReady(AppInstance appInstance) {
            ArcBridgeHostOnAppInstanceReadyParams arcBridgeHostOnAppInstanceReadyParams = new ArcBridgeHostOnAppInstanceReadyParams();
            arcBridgeHostOnAppInstanceReadyParams.instanceRemote = appInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnAppInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(100)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onAppPermissionsInstanceReady(AppPermissionsInstance appPermissionsInstance) {
            ArcBridgeHostOnAppPermissionsInstanceReadyParams arcBridgeHostOnAppPermissionsInstanceReadyParams = new ArcBridgeHostOnAppPermissionsInstanceReadyParams();
            arcBridgeHostOnAppPermissionsInstanceReadyParams.instanceRemote = appPermissionsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnAppPermissionsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(149)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onAppfuseInstanceReady(AppfuseInstance appfuseInstance) {
            ArcBridgeHostOnAppfuseInstanceReadyParams arcBridgeHostOnAppfuseInstanceReadyParams = new ArcBridgeHostOnAppfuseInstanceReadyParams();
            arcBridgeHostOnAppfuseInstanceReadyParams.instanceRemote = appfuseInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnAppfuseInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(145)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onArcWifiInstanceReady(ArcWifiInstance arcWifiInstance) {
            ArcBridgeHostOnArcWifiInstanceReadyParams arcBridgeHostOnArcWifiInstanceReadyParams = new ArcBridgeHostOnArcWifiInstanceReadyParams();
            arcBridgeHostOnArcWifiInstanceReadyParams.instanceRemote = arcWifiInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnArcWifiInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(171)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onAudioInstanceReady(AudioInstance audioInstance) {
            ArcBridgeHostOnAudioInstanceReadyParams arcBridgeHostOnAudioInstanceReadyParams = new ArcBridgeHostOnAudioInstanceReadyParams();
            arcBridgeHostOnAudioInstanceReadyParams.instanceRemote = audioInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnAudioInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(115)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onAuthInstanceReady(AuthInstance authInstance) {
            ArcBridgeHostOnAuthInstanceReadyParams arcBridgeHostOnAuthInstanceReadyParams = new ArcBridgeHostOnAuthInstanceReadyParams();
            arcBridgeHostOnAuthInstanceReadyParams.instanceRemote = authInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnAuthInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(106)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onBackupSettingsInstanceReady(BackupSettingsInstance backupSettingsInstance) {
            ArcBridgeHostOnBackupSettingsInstanceReadyParams arcBridgeHostOnBackupSettingsInstanceReadyParams = new ArcBridgeHostOnBackupSettingsInstanceReadyParams();
            arcBridgeHostOnBackupSettingsInstanceReadyParams.instanceRemote = backupSettingsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnBackupSettingsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(138)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onBluetoothInstanceReady(BluetoothInstance bluetoothInstance) {
            ArcBridgeHostOnBluetoothInstanceReadyParams arcBridgeHostOnBluetoothInstanceReadyParams = new ArcBridgeHostOnBluetoothInstanceReadyParams();
            arcBridgeHostOnBluetoothInstanceReadyParams.instanceRemote = bluetoothInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnBluetoothInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(113)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onBootPhaseMonitorInstanceReady(BootPhaseMonitorInstance bootPhaseMonitorInstance) {
            ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams arcBridgeHostOnBootPhaseMonitorInstanceReadyParams = new ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams();
            arcBridgeHostOnBootPhaseMonitorInstanceReadyParams.instanceRemote = bootPhaseMonitorInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnBootPhaseMonitorInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(125)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onCameraInstanceReady(CameraInstance cameraInstance) {
            ArcBridgeHostOnCameraInstanceReadyParams arcBridgeHostOnCameraInstanceReadyParams = new ArcBridgeHostOnCameraInstanceReadyParams();
            arcBridgeHostOnCameraInstanceReadyParams.instanceRemote = cameraInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnCameraInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(151)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onChromeFeatureFlagsInstanceReady(ChromeFeatureFlagsInstance chromeFeatureFlagsInstance) {
            ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams arcBridgeHostOnChromeFeatureFlagsInstanceReadyParams = new ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams();
            arcBridgeHostOnChromeFeatureFlagsInstanceReadyParams.instanceRemote = chromeFeatureFlagsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnChromeFeatureFlagsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(170)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onCompatibilityModeInstanceReady(CompatibilityModeInstance compatibilityModeInstance) {
            ArcBridgeHostOnCompatibilityModeInstanceReadyParams arcBridgeHostOnCompatibilityModeInstanceReadyParams = new ArcBridgeHostOnCompatibilityModeInstanceReadyParams();
            arcBridgeHostOnCompatibilityModeInstanceReadyParams.instanceRemote = compatibilityModeInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnCompatibilityModeInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(161)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onCrashCollectorInstanceReady(CrashCollectorInstance crashCollectorInstance) {
            ArcBridgeHostOnCrashCollectorInstanceReadyParams arcBridgeHostOnCrashCollectorInstanceReadyParams = new ArcBridgeHostOnCrashCollectorInstanceReadyParams();
            arcBridgeHostOnCrashCollectorInstanceReadyParams.instanceRemote = crashCollectorInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnCrashCollectorInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(112)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onDigitalGoodsInstanceReady(DigitalGoodsInstance digitalGoodsInstance) {
            ArcBridgeHostOnDigitalGoodsInstanceReadyParams arcBridgeHostOnDigitalGoodsInstanceReadyParams = new ArcBridgeHostOnDigitalGoodsInstanceReadyParams();
            arcBridgeHostOnDigitalGoodsInstanceReadyParams.instanceRemote = digitalGoodsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnDigitalGoodsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(156)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onDiskSpaceInstanceReady(DiskSpaceInstance diskSpaceInstance) {
            ArcBridgeHostOnDiskSpaceInstanceReadyParams arcBridgeHostOnDiskSpaceInstanceReadyParams = new ArcBridgeHostOnDiskSpaceInstanceReadyParams();
            arcBridgeHostOnDiskSpaceInstanceReadyParams.instanceRemote = diskSpaceInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnDiskSpaceInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(144)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onEnterpriseReportingInstanceReady(EnterpriseReportingInstance enterpriseReportingInstance) {
            ArcBridgeHostOnEnterpriseReportingInstanceReadyParams arcBridgeHostOnEnterpriseReportingInstanceReadyParams = new ArcBridgeHostOnEnterpriseReportingInstanceReadyParams();
            arcBridgeHostOnEnterpriseReportingInstanceReadyParams.instanceRemote = enterpriseReportingInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnEnterpriseReportingInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(122)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onErrorNotificationInstanceReady(ErrorNotificationInstance errorNotificationInstance) {
            ArcBridgeHostOnErrorNotificationInstanceReadyParams arcBridgeHostOnErrorNotificationInstanceReadyParams = new ArcBridgeHostOnErrorNotificationInstanceReadyParams();
            arcBridgeHostOnErrorNotificationInstanceReadyParams.instanceRemote = errorNotificationInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnErrorNotificationInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(172)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onFileSystemInstanceReady(FileSystemInstance fileSystemInstance) {
            ArcBridgeHostOnFileSystemInstanceReadyParams arcBridgeHostOnFileSystemInstanceReadyParams = new ArcBridgeHostOnFileSystemInstanceReadyParams();
            arcBridgeHostOnFileSystemInstanceReadyParams.instanceRemote = fileSystemInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnFileSystemInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(119)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onIioSensorInstanceReady(IioSensorInstance iioSensorInstance) {
            ArcBridgeHostOnIioSensorInstanceReadyParams arcBridgeHostOnIioSensorInstanceReadyParams = new ArcBridgeHostOnIioSensorInstanceReadyParams();
            arcBridgeHostOnIioSensorInstanceReadyParams.instanceRemote = iioSensorInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnIioSensorInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(159)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onImeInstanceReady(ImeInstance imeInstance) {
            ArcBridgeHostOnImeInstanceReadyParams arcBridgeHostOnImeInstanceReadyParams = new ArcBridgeHostOnImeInstanceReadyParams();
            arcBridgeHostOnImeInstanceReadyParams.instanceRemote = imeInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnImeInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(110)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onInputMethodManagerInstanceReady(InputMethodManagerInstance inputMethodManagerInstance) {
            ArcBridgeHostOnInputMethodManagerInstanceReadyParams arcBridgeHostOnInputMethodManagerInstanceReadyParams = new ArcBridgeHostOnInputMethodManagerInstanceReadyParams();
            arcBridgeHostOnInputMethodManagerInstanceReadyParams.instanceRemote = inputMethodManagerInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnInputMethodManagerInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(BluetoothGattStatus.GATT_CONNECTION_CONGESTED)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onIntentHelperInstanceReady(IntentHelperInstance intentHelperInstance) {
            ArcBridgeHostOnIntentHelperInstanceReadyParams arcBridgeHostOnIntentHelperInstanceReadyParams = new ArcBridgeHostOnIntentHelperInstanceReadyParams();
            arcBridgeHostOnIntentHelperInstanceReadyParams.instanceRemote = intentHelperInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnIntentHelperInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(111)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onKeyMintInstanceReady(KeyMintInstance keyMintInstance) {
            ArcBridgeHostOnKeyMintInstanceReadyParams arcBridgeHostOnKeyMintInstanceReadyParams = new ArcBridgeHostOnKeyMintInstanceReadyParams();
            arcBridgeHostOnKeyMintInstanceReadyParams.instanceRemote = keyMintInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnKeyMintInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(168)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onKeymasterInstanceReady(KeymasterInstance keymasterInstance) {
            ArcBridgeHostOnKeymasterInstanceReadyParams arcBridgeHostOnKeymasterInstanceReadyParams = new ArcBridgeHostOnKeymasterInstanceReadyParams();
            arcBridgeHostOnKeymasterInstanceReadyParams.instanceRemote = keymasterInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnKeymasterInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(152)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onMediaSessionInstanceReady(MediaSessionInstance mediaSessionInstance) {
            ArcBridgeHostOnMediaSessionInstanceReadyParams arcBridgeHostOnMediaSessionInstanceReadyParams = new ArcBridgeHostOnMediaSessionInstanceReadyParams();
            arcBridgeHostOnMediaSessionInstanceReadyParams.instanceRemote = mediaSessionInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnMediaSessionInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(148)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onMemoryInstanceReady(MemoryInstance memoryInstance) {
            ArcBridgeHostOnMemoryInstanceReadyParams arcBridgeHostOnMemoryInstanceReadyParams = new ArcBridgeHostOnMemoryInstanceReadyParams();
            arcBridgeHostOnMemoryInstanceReadyParams.instanceRemote = memoryInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnMemoryInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(164)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onMetricsInstanceReady(MetricsInstance metricsInstance) {
            ArcBridgeHostOnMetricsInstanceReadyParams arcBridgeHostOnMetricsInstanceReadyParams = new ArcBridgeHostOnMetricsInstanceReadyParams();
            arcBridgeHostOnMetricsInstanceReadyParams.instanceRemote = metricsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnMetricsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(116)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onMidisInstanceReady(MidisInstance midisInstance) {
            ArcBridgeHostOnMidisInstanceReadyParams arcBridgeHostOnMidisInstanceReadyParams = new ArcBridgeHostOnMidisInstanceReadyParams();
            arcBridgeHostOnMidisInstanceReadyParams.instanceRemote = midisInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnMidisInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(135)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onNearbyShareInstanceReady(NearbyShareInstance nearbyShareInstance) {
            ArcBridgeHostOnNearbyShareInstanceReadyParams arcBridgeHostOnNearbyShareInstanceReadyParams = new ArcBridgeHostOnNearbyShareInstanceReadyParams();
            arcBridgeHostOnNearbyShareInstanceReadyParams.instanceRemote = nearbyShareInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnNearbyShareInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(163)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onNetInstanceReady(NetInstance netInstance) {
            ArcBridgeHostOnNetInstanceReadyParams arcBridgeHostOnNetInstanceReadyParams = new ArcBridgeHostOnNetInstanceReadyParams();
            arcBridgeHostOnNetInstanceReadyParams.instanceRemote = netInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnNetInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(108)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onNotificationsInstanceReady(NotificationsInstance notificationsInstance) {
            ArcBridgeHostOnNotificationsInstanceReadyParams arcBridgeHostOnNotificationsInstanceReadyParams = new ArcBridgeHostOnNotificationsInstanceReadyParams();
            arcBridgeHostOnNotificationsInstanceReadyParams.instanceRemote = notificationsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnNotificationsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(102)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onObbMounterInstanceReady(ObbMounterInstance obbMounterInstance) {
            ArcBridgeHostOnObbMounterInstanceReadyParams arcBridgeHostOnObbMounterInstanceReadyParams = new ArcBridgeHostOnObbMounterInstanceReadyParams();
            arcBridgeHostOnObbMounterInstanceReadyParams.instanceRemote = obbMounterInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnObbMounterInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(120)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onOemCryptoInstanceReady(OemCryptoInstance oemCryptoInstance) {
            ArcBridgeHostOnOemCryptoInstanceReadyParams arcBridgeHostOnOemCryptoInstanceReadyParams = new ArcBridgeHostOnOemCryptoInstanceReadyParams();
            arcBridgeHostOnOemCryptoInstanceReadyParams.instanceRemote = oemCryptoInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnOemCryptoInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(133)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onPaymentAppInstanceReady(PaymentAppInstance paymentAppInstance) {
            ArcBridgeHostOnPaymentAppInstanceReadyParams arcBridgeHostOnPaymentAppInstanceReadyParams = new ArcBridgeHostOnPaymentAppInstanceReadyParams();
            arcBridgeHostOnPaymentAppInstanceReadyParams.instanceRemote = paymentAppInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnPaymentAppInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(155)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onPipInstanceReady(PipInstance pipInstance) {
            ArcBridgeHostOnPipInstanceReadyParams arcBridgeHostOnPipInstanceReadyParams = new ArcBridgeHostOnPipInstanceReadyParams();
            arcBridgeHostOnPipInstanceReadyParams.instanceRemote = pipInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnPipInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(146)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onPolicyInstanceReady(PolicyInstance policyInstance) {
            ArcBridgeHostOnPolicyInstanceReadyParams arcBridgeHostOnPolicyInstanceReadyParams = new ArcBridgeHostOnPolicyInstanceReadyParams();
            arcBridgeHostOnPolicyInstanceReadyParams.instanceRemote = policyInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnPolicyInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(114)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onPowerInstanceReady(PowerInstance powerInstance) {
            ArcBridgeHostOnPowerInstanceReadyParams arcBridgeHostOnPowerInstanceReadyParams = new ArcBridgeHostOnPowerInstanceReadyParams();
            arcBridgeHostOnPowerInstanceReadyParams.instanceRemote = powerInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnPowerInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(103)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onPrintSpoolerInstanceReady(PrintSpoolerInstance printSpoolerInstance) {
            ArcBridgeHostOnPrintSpoolerInstanceReadyParams arcBridgeHostOnPrintSpoolerInstanceReadyParams = new ArcBridgeHostOnPrintSpoolerInstanceReadyParams();
            arcBridgeHostOnPrintSpoolerInstanceReadyParams.instanceRemote = printSpoolerInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnPrintSpoolerInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(150)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onPrivacyItemsInstanceReady(PrivacyItemsInstance privacyItemsInstance) {
            ArcBridgeHostOnPrivacyItemsInstanceReadyParams arcBridgeHostOnPrivacyItemsInstanceReadyParams = new ArcBridgeHostOnPrivacyItemsInstanceReadyParams();
            arcBridgeHostOnPrivacyItemsInstanceReadyParams.instanceRemote = privacyItemsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnPrivacyItemsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(166)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onProcessInstanceReady(ProcessInstance processInstance) {
            ArcBridgeHostOnProcessInstanceReadyParams arcBridgeHostOnProcessInstanceReadyParams = new ArcBridgeHostOnProcessInstanceReadyParams();
            arcBridgeHostOnProcessInstanceReadyParams.instanceRemote = processInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnProcessInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(104)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onScreenCaptureInstanceReady(ScreenCaptureInstance screenCaptureInstance) {
            ArcBridgeHostOnScreenCaptureInstanceReadyParams arcBridgeHostOnScreenCaptureInstanceReadyParams = new ArcBridgeHostOnScreenCaptureInstanceReadyParams();
            arcBridgeHostOnScreenCaptureInstanceReadyParams.instanceRemote = screenCaptureInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnScreenCaptureInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(140)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onSharesheetInstanceReady(SharesheetInstance sharesheetInstance) {
            ArcBridgeHostOnSharesheetInstanceReadyParams arcBridgeHostOnSharesheetInstanceReadyParams = new ArcBridgeHostOnSharesheetInstanceReadyParams();
            arcBridgeHostOnSharesheetInstanceReadyParams.instanceRemote = sharesheetInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnSharesheetInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(157)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onSystemStateInstanceReady(SystemStateInstance systemStateInstance) {
            ArcBridgeHostOnSystemStateInstanceReadyParams arcBridgeHostOnSystemStateInstanceReadyParams = new ArcBridgeHostOnSystemStateInstanceReadyParams();
            arcBridgeHostOnSystemStateInstanceReadyParams.instanceRemote = systemStateInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnSystemStateInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(169)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onSystemUiInstanceReady(SystemUiInstance systemUiInstance) {
            ArcBridgeHostOnSystemUiInstanceReadyParams arcBridgeHostOnSystemUiInstanceReadyParams = new ArcBridgeHostOnSystemUiInstanceReadyParams();
            arcBridgeHostOnSystemUiInstanceReadyParams.instanceRemote = systemUiInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnSystemUiInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(167)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onTimerInstanceReady(TimerInstance timerInstance) {
            ArcBridgeHostOnTimerInstanceReadyParams arcBridgeHostOnTimerInstanceReadyParams = new ArcBridgeHostOnTimerInstanceReadyParams();
            arcBridgeHostOnTimerInstanceReadyParams.instanceRemote = timerInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnTimerInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(141)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onTracingInstanceReady(TracingInstance tracingInstance) {
            ArcBridgeHostOnTracingInstanceReadyParams arcBridgeHostOnTracingInstanceReadyParams = new ArcBridgeHostOnTracingInstanceReadyParams();
            arcBridgeHostOnTracingInstanceReadyParams.instanceRemote = tracingInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnTracingInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(128)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onTtsInstanceReady(TtsInstance ttsInstance) {
            ArcBridgeHostOnTtsInstanceReadyParams arcBridgeHostOnTtsInstanceReadyParams = new ArcBridgeHostOnTtsInstanceReadyParams();
            arcBridgeHostOnTtsInstanceReadyParams.instanceRemote = ttsInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnTtsInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(123)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onUsbHostInstanceReady(UsbHostInstance usbHostInstance) {
            ArcBridgeHostOnUsbHostInstanceReadyParams arcBridgeHostOnUsbHostInstanceReadyParams = new ArcBridgeHostOnUsbHostInstanceReadyParams();
            arcBridgeHostOnUsbHostInstanceReadyParams.instanceRemote = usbHostInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnUsbHostInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(139)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onVideoInstanceReady(VideoInstance videoInstance) {
            ArcBridgeHostOnVideoInstanceReadyParams arcBridgeHostOnVideoInstanceReadyParams = new ArcBridgeHostOnVideoInstanceReadyParams();
            arcBridgeHostOnVideoInstanceReadyParams.instanceRemote = videoInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnVideoInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(107)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onVolumeMounterInstanceReady(VolumeMounterInstance volumeMounterInstance) {
            ArcBridgeHostOnVolumeMounterInstanceReadyParams arcBridgeHostOnVolumeMounterInstanceReadyParams = new ArcBridgeHostOnVolumeMounterInstanceReadyParams();
            arcBridgeHostOnVolumeMounterInstanceReadyParams.instanceRemote = volumeMounterInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnVolumeMounterInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(131)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onWakeLockInstanceReady(WakeLockInstance wakeLockInstance) {
            ArcBridgeHostOnWakeLockInstanceReadyParams arcBridgeHostOnWakeLockInstanceReadyParams = new ArcBridgeHostOnWakeLockInstanceReadyParams();
            arcBridgeHostOnWakeLockInstanceReadyParams.instanceRemote = wakeLockInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnWakeLockInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(142)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onWallpaperInstanceReady(WallpaperInstance wallpaperInstance) {
            ArcBridgeHostOnWallpaperInstanceReadyParams arcBridgeHostOnWallpaperInstanceReadyParams = new ArcBridgeHostOnWallpaperInstanceReadyParams();
            arcBridgeHostOnWallpaperInstanceReadyParams.instanceRemote = wallpaperInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnWallpaperInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(124)));
        }

        @Override // org.chromium.arc.mojom.ArcBridgeHost
        public void onWebApkInstanceReady(WebApkInstance webApkInstance) {
            ArcBridgeHostOnWebApkInstanceReadyParams arcBridgeHostOnWebApkInstanceReadyParams = new ArcBridgeHostOnWebApkInstanceReadyParams();
            arcBridgeHostOnWebApkInstanceReadyParams.instancePtr = webApkInstance;
            getProxyHandler().getMessageReceiver().accept(arcBridgeHostOnWebApkInstanceReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(162)));
        }
    }

    static final class Stub extends Interface.Stub<ArcBridgeHost> {
        Stub(Core core, ArcBridgeHost arcBridgeHost) {
            super(core, arcBridgeHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (!header.validateHeader(0)) {
                    return false;
                }
                int type = header.getType();
                if (type == 119) {
                    getImpl().onFileSystemInstanceReady(ArcBridgeHostOnFileSystemInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                    return true;
                }
                if (type == 120) {
                    getImpl().onObbMounterInstanceReady(ArcBridgeHostOnObbMounterInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                    return true;
                }
                if (type == 127) {
                    getImpl().onAccessibilityHelperInstanceReady(ArcBridgeHostOnAccessibilityHelperInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                    return true;
                }
                if (type == 128) {
                    getImpl().onTracingInstanceReady(ArcBridgeHostOnTracingInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                    return true;
                }
                switch (type) {
                    case -2:
                        return true;
                    case AndroidControlSceneMode.ANDROID_CONTROL_SCENE_MODE_DEVICE_CUSTOM_START /* 100 */:
                        getImpl().onAppInstanceReady(ArcBridgeHostOnAppInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 131:
                        getImpl().onVolumeMounterInstanceReady(ArcBridgeHostOnVolumeMounterInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 133:
                        getImpl().onOemCryptoInstanceReady(ArcBridgeHostOnOemCryptoInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 135:
                        getImpl().onMidisInstanceReady(ArcBridgeHostOnMidisInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 138:
                        getImpl().onBackupSettingsInstanceReady(ArcBridgeHostOnBackupSettingsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 139:
                        getImpl().onUsbHostInstanceReady(ArcBridgeHostOnUsbHostInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 140:
                        getImpl().onScreenCaptureInstanceReady(ArcBridgeHostOnScreenCaptureInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 141:
                        getImpl().onTimerInstanceReady(ArcBridgeHostOnTimerInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 142:
                        getImpl().onWakeLockInstanceReady(ArcBridgeHostOnWakeLockInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case BluetoothGattStatus.GATT_CONNECTION_CONGESTED /* 143 */:
                        getImpl().onInputMethodManagerInstanceReady(ArcBridgeHostOnInputMethodManagerInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 144:
                        getImpl().onDiskSpaceInstanceReady(ArcBridgeHostOnDiskSpaceInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 145:
                        getImpl().onAppfuseInstanceReady(ArcBridgeHostOnAppfuseInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 146:
                        getImpl().onPipInstanceReady(ArcBridgeHostOnPipInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 166:
                        getImpl().onPrivacyItemsInstanceReady(ArcBridgeHostOnPrivacyItemsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 167:
                        getImpl().onSystemUiInstanceReady(ArcBridgeHostOnSystemUiInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 168:
                        getImpl().onKeyMintInstanceReady(ArcBridgeHostOnKeyMintInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 169:
                        getImpl().onSystemStateInstanceReady(ArcBridgeHostOnSystemStateInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 170:
                        getImpl().onChromeFeatureFlagsInstanceReady(ArcBridgeHostOnChromeFeatureFlagsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 171:
                        getImpl().onArcWifiInstanceReady(ArcBridgeHostOnArcWifiInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    case 172:
                        getImpl().onErrorNotificationInstanceReady(ArcBridgeHostOnErrorNotificationInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                        return true;
                    default:
                        switch (type) {
                            case 102:
                                getImpl().onNotificationsInstanceReady(ArcBridgeHostOnNotificationsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                break;
                            case 103:
                                getImpl().onPowerInstanceReady(ArcBridgeHostOnPowerInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                break;
                            case 104:
                                getImpl().onProcessInstanceReady(ArcBridgeHostOnProcessInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                break;
                            default:
                                switch (type) {
                                    case 106:
                                        getImpl().onAuthInstanceReady(ArcBridgeHostOnAuthInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                        break;
                                    case 107:
                                        getImpl().onVideoInstanceReady(ArcBridgeHostOnVideoInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                        break;
                                    case 108:
                                        getImpl().onNetInstanceReady(ArcBridgeHostOnNetInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                        break;
                                    default:
                                        switch (type) {
                                            case 110:
                                                getImpl().onImeInstanceReady(ArcBridgeHostOnImeInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                break;
                                            case 111:
                                                getImpl().onIntentHelperInstanceReady(ArcBridgeHostOnIntentHelperInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                break;
                                            case 112:
                                                getImpl().onCrashCollectorInstanceReady(ArcBridgeHostOnCrashCollectorInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                break;
                                            case 113:
                                                getImpl().onBluetoothInstanceReady(ArcBridgeHostOnBluetoothInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                break;
                                            case 114:
                                                getImpl().onPolicyInstanceReady(ArcBridgeHostOnPolicyInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                break;
                                            case 115:
                                                getImpl().onAudioInstanceReady(ArcBridgeHostOnAudioInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                break;
                                            case 116:
                                                getImpl().onMetricsInstanceReady(ArcBridgeHostOnMetricsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                break;
                                            default:
                                                switch (type) {
                                                    case 122:
                                                        getImpl().onEnterpriseReportingInstanceReady(ArcBridgeHostOnEnterpriseReportingInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                        break;
                                                    case 123:
                                                        getImpl().onTtsInstanceReady(ArcBridgeHostOnTtsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                        break;
                                                    case 124:
                                                        getImpl().onWallpaperInstanceReady(ArcBridgeHostOnWallpaperInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                        break;
                                                    case 125:
                                                        getImpl().onBootPhaseMonitorInstanceReady(ArcBridgeHostOnBootPhaseMonitorInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                        break;
                                                    default:
                                                        switch (type) {
                                                            case 148:
                                                                getImpl().onMediaSessionInstanceReady(ArcBridgeHostOnMediaSessionInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                break;
                                                            case 149:
                                                                getImpl().onAppPermissionsInstanceReady(ArcBridgeHostOnAppPermissionsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                break;
                                                            case 150:
                                                                getImpl().onPrintSpoolerInstanceReady(ArcBridgeHostOnPrintSpoolerInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                break;
                                                            case 151:
                                                                getImpl().onCameraInstanceReady(ArcBridgeHostOnCameraInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                break;
                                                            case 152:
                                                                getImpl().onKeymasterInstanceReady(ArcBridgeHostOnKeymasterInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                break;
                                                            default:
                                                                switch (type) {
                                                                    case 155:
                                                                        getImpl().onPaymentAppInstanceReady(ArcBridgeHostOnPaymentAppInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                        break;
                                                                    case 156:
                                                                        getImpl().onDigitalGoodsInstanceReady(ArcBridgeHostOnDigitalGoodsInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                        break;
                                                                    case 157:
                                                                        getImpl().onSharesheetInstanceReady(ArcBridgeHostOnSharesheetInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                        break;
                                                                    case 158:
                                                                        getImpl().onAdbdMonitorInstanceReady(ArcBridgeHostOnAdbdMonitorInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                        break;
                                                                    case 159:
                                                                        getImpl().onIioSensorInstanceReady(ArcBridgeHostOnIioSensorInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                        break;
                                                                    default:
                                                                        switch (type) {
                                                                            case 161:
                                                                                getImpl().onCompatibilityModeInstanceReady(ArcBridgeHostOnCompatibilityModeInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                                break;
                                                                            case 162:
                                                                                getImpl().onWebApkInstanceReady(ArcBridgeHostOnWebApkInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instancePtr);
                                                                                break;
                                                                            case 163:
                                                                                getImpl().onNearbyShareInstanceReady(ArcBridgeHostOnNearbyShareInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                                break;
                                                                            case 164:
                                                                                getImpl().onMemoryInstanceReady(ArcBridgeHostOnMemoryInstanceReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).instanceRemote);
                                                                                break;
                                                                        }
                                                                        break;
                                                                }
                                                                break;
                                                        }
                                                        break;
                                                }
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                        return true;
                }
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }

        @Override // org.chromium.mojo.bindings.MessageReceiverWithResponder
        public boolean acceptWithResponder(Message message, MessageReceiver messageReceiver) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ArcBridgeHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.AppHost;
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

class AppHost_Internal {
    public static final Interface.Manager<AppHost, AppHost.Proxy> MANAGER = new Interface.Manager<AppHost, AppHost.Proxy>() { // from class: org.chromium.arc.mojom.AppHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppHost[] buildArray(int i) {
            return new AppHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AppHost> buildStub(Core core, AppHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AppHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 58;
        }
    };

    static final class AppHostOnAppAddedDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppInfo app;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnAppAddedDeprecatedParams() {
            super(16, 0);
        }

        public static AppHostOnAppAddedDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnAppAddedDeprecatedParams appHostOnAppAddedDeprecatedParams = new AppHostOnAppAddedDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnAppAddedDeprecatedParams.app = AppInfo.decode(decoder.readPointer(8, false));
                return appHostOnAppAddedDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.app, 8, false);
        }

        private AppHostOnAppAddedDeprecatedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnAppListRefreshedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppInfo[] apps;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnAppListRefreshedParams() {
            super(16, 0);
        }

        public static AppHostOnAppListRefreshedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AppHostOnAppListRefreshedParams appHostOnAppListRefreshedParams = new AppHostOnAppListRefreshedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                appHostOnAppListRefreshedParams.apps = new AppInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    appHostOnAppListRefreshedParams.apps[i] = AppInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return appHostOnAppListRefreshedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            AppInfo[] appInfoArr = this.apps;
            if (appInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(appInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                AppInfo[] appInfoArr2 = this.apps;
                if (i >= appInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) appInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AppHostOnAppListRefreshedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnInstallShortcutParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ShortcutInfo shortcut;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnInstallShortcutParams() {
            super(16, 0);
        }

        public static AppHostOnInstallShortcutParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnInstallShortcutParams appHostOnInstallShortcutParams = new AppHostOnInstallShortcutParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnInstallShortcutParams.shortcut = ShortcutInfo.decode(decoder.readPointer(8, false));
                return appHostOnInstallShortcutParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.shortcut, 8, false);
        }

        private AppHostOnInstallShortcutParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnInstallationActiveChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean active;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnInstallationActiveChangedParams() {
            super(24, 0);
        }

        public static AppHostOnInstallationActiveChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnInstallationActiveChangedParams appHostOnInstallationActiveChangedParams = new AppHostOnInstallationActiveChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnInstallationActiveChangedParams.packageName = decoder.readString(8, false);
                appHostOnInstallationActiveChangedParams.active = decoder.readBoolean(16, 0);
                return appHostOnInstallationActiveChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.active, 16, 0);
        }

        private AppHostOnInstallationActiveChangedParams(int i) {
            super(24, i);
        }
    }

    static final class AppHostOnInstallationFinishedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InstallationResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0), new DataHeader(16, 17)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public AppHostOnInstallationFinishedParams() {
            super(16, 17);
        }

        public static AppHostOnInstallationFinishedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                AppHostOnInstallationFinishedParams appHostOnInstallationFinishedParams = new AppHostOnInstallationFinishedParams(i);
                if (i >= 17) {
                    appHostOnInstallationFinishedParams.result = InstallationResult.decode(decoder.readPointer(8, true));
                }
                return appHostOnInstallationFinishedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, true);
        }

        private AppHostOnInstallationFinishedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnInstallationProgressChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public float progress;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnInstallationProgressChangedParams() {
            super(24, 0);
        }

        public static AppHostOnInstallationProgressChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnInstallationProgressChangedParams appHostOnInstallationProgressChangedParams = new AppHostOnInstallationProgressChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnInstallationProgressChangedParams.packageName = decoder.readString(8, false);
                appHostOnInstallationProgressChangedParams.progress = decoder.readFloat(16);
                return appHostOnInstallationProgressChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.progress, 16);
        }

        private AppHostOnInstallationProgressChangedParams(int i) {
            super(24, i);
        }
    }

    static final class AppHostOnInstallationStartedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0), new DataHeader(16, 17)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public AppHostOnInstallationStartedParams() {
            super(16, 17);
        }

        public static AppHostOnInstallationStartedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                AppHostOnInstallationStartedParams appHostOnInstallationStartedParams = new AppHostOnInstallationStartedParams(i);
                if (i >= 17) {
                    appHostOnInstallationStartedParams.packageName = decoder.readString(8, true);
                }
                return appHostOnInstallationStartedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, true);
        }

        private AppHostOnInstallationStartedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnNotificationsEnabledChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnNotificationsEnabledChangedParams() {
            super(24, 0);
        }

        public static AppHostOnNotificationsEnabledChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnNotificationsEnabledChangedParams appHostOnNotificationsEnabledChangedParams = new AppHostOnNotificationsEnabledChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnNotificationsEnabledChangedParams.packageName = decoder.readString(8, false);
                appHostOnNotificationsEnabledChangedParams.enabled = decoder.readBoolean(16, 0);
                return appHostOnNotificationsEnabledChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.enabled, 16, 0);
        }

        private AppHostOnNotificationsEnabledChangedParams(int i) {
            super(24, i);
        }
    }

    static final class AppHostOnPackageAddedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcPackageInfo arcPackageInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnPackageAddedParams() {
            super(16, 0);
        }

        public static AppHostOnPackageAddedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnPackageAddedParams appHostOnPackageAddedParams = new AppHostOnPackageAddedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnPackageAddedParams.arcPackageInfo = ArcPackageInfo.decode(decoder.readPointer(8, false));
                return appHostOnPackageAddedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.arcPackageInfo, 8, false);
        }

        private AppHostOnPackageAddedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnPackageAppListRefreshedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppInfo[] apps;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnPackageAppListRefreshedParams() {
            super(24, 0);
        }

        public static AppHostOnPackageAppListRefreshedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AppHostOnPackageAppListRefreshedParams appHostOnPackageAppListRefreshedParams = new AppHostOnPackageAppListRefreshedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnPackageAppListRefreshedParams.packageName = decoderOutline3.readString(8, false);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                appHostOnPackageAppListRefreshedParams.apps = new AppInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    appHostOnPackageAppListRefreshedParams.apps[i] = AppInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return appHostOnPackageAppListRefreshedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            AppInfo[] appInfoArr = this.apps;
            if (appInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(appInfoArr.length, 16, -1);
            int i = 0;
            while (true) {
                AppInfo[] appInfoArr2 = this.apps;
                if (i >= appInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) appInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AppHostOnPackageAppListRefreshedParams(int i) {
            super(24, i);
        }
    }

    static final class AppHostOnPackageListRefreshedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcPackageInfo[] packages;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnPackageListRefreshedParams() {
            super(16, 0);
        }

        public static AppHostOnPackageListRefreshedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AppHostOnPackageListRefreshedParams appHostOnPackageListRefreshedParams = new AppHostOnPackageListRefreshedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                appHostOnPackageListRefreshedParams.packages = new ArcPackageInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    appHostOnPackageListRefreshedParams.packages[i] = ArcPackageInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return appHostOnPackageListRefreshedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ArcPackageInfo[] arcPackageInfoArr = this.packages;
            if (arcPackageInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(arcPackageInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                ArcPackageInfo[] arcPackageInfoArr2 = this.packages;
                if (i >= arcPackageInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) arcPackageInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AppHostOnPackageListRefreshedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnPackageModifiedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcPackageInfo arcPackageInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnPackageModifiedParams() {
            super(16, 0);
        }

        public static AppHostOnPackageModifiedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnPackageModifiedParams appHostOnPackageModifiedParams = new AppHostOnPackageModifiedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnPackageModifiedParams.arcPackageInfo = ArcPackageInfo.decode(decoder.readPointer(8, false));
                return appHostOnPackageModifiedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.arcPackageInfo, 8, false);
        }

        private AppHostOnPackageModifiedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnPackageRemovedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnPackageRemovedParams() {
            super(16, 0);
        }

        public static AppHostOnPackageRemovedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnPackageRemovedParams appHostOnPackageRemovedParams = new AppHostOnPackageRemovedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnPackageRemovedParams.packageName = decoder.readString(8, false);
                return appHostOnPackageRemovedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private AppHostOnPackageRemovedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnTaskCreatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String activity;
        public String intent;
        public String name;
        public String packageName;
        public int sessionId;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 13), new DataHeader(48, 15), new DataHeader(48, 48)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[3];
        }

        public AppHostOnTaskCreatedParams() {
            super(48, 48);
        }

        public static AppHostOnTaskCreatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                AppHostOnTaskCreatedParams appHostOnTaskCreatedParams = new AppHostOnTaskCreatedParams(i);
                appHostOnTaskCreatedParams.taskId = decoder.readInt(8);
                if (i >= 48) {
                    appHostOnTaskCreatedParams.sessionId = decoder.readInt(12);
                }
                appHostOnTaskCreatedParams.packageName = decoder.readString(16, false);
                appHostOnTaskCreatedParams.activity = decoder.readString(24, false);
                if (i >= 13) {
                    appHostOnTaskCreatedParams.name = decoder.readString(32, true);
                }
                if (i >= 15) {
                    appHostOnTaskCreatedParams.intent = decoder.readString(40, true);
                }
                return appHostOnTaskCreatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.taskId, 8);
            encoderAtDataOffset.encode(this.sessionId, 12);
            encoderAtDataOffset.encode(this.packageName, 16, false);
            encoderAtDataOffset.encode(this.activity, 24, false);
            encoderAtDataOffset.encode(this.name, 32, true);
            encoderAtDataOffset.encode(this.intent, 40, true);
        }

        private AppHostOnTaskCreatedParams(int i) {
            super(48, i);
        }
    }

    static final class AppHostOnTaskDescriptionChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public RawIconPngData icon;
        public String label;
        public int primaryColor;
        public int statusBarColor;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 49)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public AppHostOnTaskDescriptionChangedParams() {
            super(40, 49);
        }

        public static AppHostOnTaskDescriptionChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                AppHostOnTaskDescriptionChangedParams appHostOnTaskDescriptionChangedParams = new AppHostOnTaskDescriptionChangedParams(i);
                appHostOnTaskDescriptionChangedParams.taskId = decoder.readInt(8);
                if (i >= 49) {
                    appHostOnTaskDescriptionChangedParams.primaryColor = decoder.readInt(12);
                }
                appHostOnTaskDescriptionChangedParams.label = decoder.readString(16, false);
                appHostOnTaskDescriptionChangedParams.icon = RawIconPngData.decode(decoder.readPointer(24, false));
                if (i >= 49) {
                    appHostOnTaskDescriptionChangedParams.statusBarColor = decoder.readInt(32);
                }
                return appHostOnTaskDescriptionChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.taskId, 8);
            encoderAtDataOffset.encode(this.primaryColor, 12);
            encoderAtDataOffset.encode(this.label, 16, false);
            encoderAtDataOffset.encode((Struct) this.icon, 24, false);
            encoderAtDataOffset.encode(this.statusBarColor, 32);
        }

        private AppHostOnTaskDescriptionChangedParams(int i) {
            super(40, i);
        }
    }

    static final class AppHostOnTaskDescriptionUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] iconPngData;
        public String label;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnTaskDescriptionUpdatedParams() {
            super(32, 0);
        }

        public static AppHostOnTaskDescriptionUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnTaskDescriptionUpdatedParams appHostOnTaskDescriptionUpdatedParams = new AppHostOnTaskDescriptionUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnTaskDescriptionUpdatedParams.taskId = decoder.readInt(8);
                appHostOnTaskDescriptionUpdatedParams.label = decoder.readString(16, false);
                appHostOnTaskDescriptionUpdatedParams.iconPngData = decoder.readBytes(24, 0, -1);
                return appHostOnTaskDescriptionUpdatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.taskId, 8);
            encoderAtDataOffset.encode(this.label, 16, false);
            encoderAtDataOffset.encode(this.iconPngData, 24, 0, -1);
        }

        private AppHostOnTaskDescriptionUpdatedParams(int i) {
            super(32, i);
        }
    }

    static final class AppHostOnTaskDestroyedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnTaskDestroyedParams() {
            super(16, 0);
        }

        public static AppHostOnTaskDestroyedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnTaskDestroyedParams appHostOnTaskDestroyedParams = new AppHostOnTaskDestroyedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnTaskDestroyedParams.taskId = decoder.readInt(8);
                return appHostOnTaskDestroyedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.taskId, 8);
        }

        private AppHostOnTaskDestroyedParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnTaskSetActiveParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnTaskSetActiveParams() {
            super(16, 0);
        }

        public static AppHostOnTaskSetActiveParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnTaskSetActiveParams appHostOnTaskSetActiveParams = new AppHostOnTaskSetActiveParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnTaskSetActiveParams.taskId = decoder.readInt(8);
                return appHostOnTaskSetActiveParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.taskId, 8);
        }

        private AppHostOnTaskSetActiveParams(int i) {
            super(16, i);
        }
    }

    static final class AppHostOnUninstallShortcutParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String intentUri;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppHostOnUninstallShortcutParams() {
            super(24, 0);
        }

        public static AppHostOnUninstallShortcutParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppHostOnUninstallShortcutParams appHostOnUninstallShortcutParams = new AppHostOnUninstallShortcutParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appHostOnUninstallShortcutParams.packageName = decoder.readString(8, false);
                appHostOnUninstallShortcutParams.intentUri = decoder.readString(16, false);
                return appHostOnUninstallShortcutParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.intentUri, 16, false);
        }

        private AppHostOnUninstallShortcutParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AppHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onAppAddedDeprecated(AppInfo appInfo) {
            AppHostOnAppAddedDeprecatedParams appHostOnAppAddedDeprecatedParams = new AppHostOnAppAddedDeprecatedParams();
            appHostOnAppAddedDeprecatedParams.app = appInfo;
            getProxyHandler().getMessageReceiver().accept(appHostOnAppAddedDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onAppListRefreshed(AppInfo[] appInfoArr) {
            AppHostOnAppListRefreshedParams appHostOnAppListRefreshedParams = new AppHostOnAppListRefreshedParams();
            appHostOnAppListRefreshedParams.apps = appInfoArr;
            getProxyHandler().getMessageReceiver().accept(appHostOnAppListRefreshedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onInstallShortcut(ShortcutInfo shortcutInfo) {
            AppHostOnInstallShortcutParams appHostOnInstallShortcutParams = new AppHostOnInstallShortcutParams();
            appHostOnInstallShortcutParams.shortcut = shortcutInfo;
            getProxyHandler().getMessageReceiver().accept(appHostOnInstallShortcutParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onInstallationActiveChanged(String str, boolean z) {
            AppHostOnInstallationActiveChangedParams appHostOnInstallationActiveChangedParams = new AppHostOnInstallationActiveChangedParams();
            appHostOnInstallationActiveChangedParams.packageName = str;
            appHostOnInstallationActiveChangedParams.active = z;
            getProxyHandler().getMessageReceiver().accept(appHostOnInstallationActiveChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onInstallationFinished(InstallationResult installationResult) {
            AppHostOnInstallationFinishedParams appHostOnInstallationFinishedParams = new AppHostOnInstallationFinishedParams();
            appHostOnInstallationFinishedParams.result = installationResult;
            getProxyHandler().getMessageReceiver().accept(appHostOnInstallationFinishedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onInstallationProgressChanged(String str, float f) {
            AppHostOnInstallationProgressChangedParams appHostOnInstallationProgressChangedParams = new AppHostOnInstallationProgressChangedParams();
            appHostOnInstallationProgressChangedParams.packageName = str;
            appHostOnInstallationProgressChangedParams.progress = f;
            getProxyHandler().getMessageReceiver().accept(appHostOnInstallationProgressChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onInstallationStarted(String str) {
            AppHostOnInstallationStartedParams appHostOnInstallationStartedParams = new AppHostOnInstallationStartedParams();
            appHostOnInstallationStartedParams.packageName = str;
            getProxyHandler().getMessageReceiver().accept(appHostOnInstallationStartedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onNotificationsEnabledChanged(String str, boolean z) {
            AppHostOnNotificationsEnabledChangedParams appHostOnNotificationsEnabledChangedParams = new AppHostOnNotificationsEnabledChangedParams();
            appHostOnNotificationsEnabledChangedParams.packageName = str;
            appHostOnNotificationsEnabledChangedParams.enabled = z;
            getProxyHandler().getMessageReceiver().accept(appHostOnNotificationsEnabledChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onPackageAdded(ArcPackageInfo arcPackageInfo) {
            AppHostOnPackageAddedParams appHostOnPackageAddedParams = new AppHostOnPackageAddedParams();
            appHostOnPackageAddedParams.arcPackageInfo = arcPackageInfo;
            getProxyHandler().getMessageReceiver().accept(appHostOnPackageAddedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onPackageAppListRefreshed(String str, AppInfo[] appInfoArr) {
            AppHostOnPackageAppListRefreshedParams appHostOnPackageAppListRefreshedParams = new AppHostOnPackageAppListRefreshedParams();
            appHostOnPackageAppListRefreshedParams.packageName = str;
            appHostOnPackageAppListRefreshedParams.apps = appInfoArr;
            getProxyHandler().getMessageReceiver().accept(appHostOnPackageAppListRefreshedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onPackageListRefreshed(ArcPackageInfo[] arcPackageInfoArr) {
            AppHostOnPackageListRefreshedParams appHostOnPackageListRefreshedParams = new AppHostOnPackageListRefreshedParams();
            appHostOnPackageListRefreshedParams.packages = arcPackageInfoArr;
            getProxyHandler().getMessageReceiver().accept(appHostOnPackageListRefreshedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onPackageModified(ArcPackageInfo arcPackageInfo) {
            AppHostOnPackageModifiedParams appHostOnPackageModifiedParams = new AppHostOnPackageModifiedParams();
            appHostOnPackageModifiedParams.arcPackageInfo = arcPackageInfo;
            getProxyHandler().getMessageReceiver().accept(appHostOnPackageModifiedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onPackageRemoved(String str) {
            AppHostOnPackageRemovedParams appHostOnPackageRemovedParams = new AppHostOnPackageRemovedParams();
            appHostOnPackageRemovedParams.packageName = str;
            getProxyHandler().getMessageReceiver().accept(appHostOnPackageRemovedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onTaskCreated(int i, String str, String str2, String str3, String str4, int i2) {
            AppHostOnTaskCreatedParams appHostOnTaskCreatedParams = new AppHostOnTaskCreatedParams();
            appHostOnTaskCreatedParams.taskId = i;
            appHostOnTaskCreatedParams.packageName = str;
            appHostOnTaskCreatedParams.activity = str2;
            appHostOnTaskCreatedParams.name = str3;
            appHostOnTaskCreatedParams.intent = str4;
            appHostOnTaskCreatedParams.sessionId = i2;
            getProxyHandler().getMessageReceiver().accept(appHostOnTaskCreatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onTaskDescriptionChanged(int i, String str, RawIconPngData rawIconPngData, int i2, int i3) {
            AppHostOnTaskDescriptionChangedParams appHostOnTaskDescriptionChangedParams = new AppHostOnTaskDescriptionChangedParams();
            appHostOnTaskDescriptionChangedParams.taskId = i;
            appHostOnTaskDescriptionChangedParams.label = str;
            appHostOnTaskDescriptionChangedParams.icon = rawIconPngData;
            appHostOnTaskDescriptionChangedParams.primaryColor = i2;
            appHostOnTaskDescriptionChangedParams.statusBarColor = i3;
            getProxyHandler().getMessageReceiver().accept(appHostOnTaskDescriptionChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onTaskDescriptionUpdated(int i, String str, byte[] bArr) {
            AppHostOnTaskDescriptionUpdatedParams appHostOnTaskDescriptionUpdatedParams = new AppHostOnTaskDescriptionUpdatedParams();
            appHostOnTaskDescriptionUpdatedParams.taskId = i;
            appHostOnTaskDescriptionUpdatedParams.label = str;
            appHostOnTaskDescriptionUpdatedParams.iconPngData = bArr;
            getProxyHandler().getMessageReceiver().accept(appHostOnTaskDescriptionUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onTaskDestroyed(int i) {
            AppHostOnTaskDestroyedParams appHostOnTaskDestroyedParams = new AppHostOnTaskDestroyedParams();
            appHostOnTaskDestroyedParams.taskId = i;
            getProxyHandler().getMessageReceiver().accept(appHostOnTaskDestroyedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onTaskSetActive(int i) {
            AppHostOnTaskSetActiveParams appHostOnTaskSetActiveParams = new AppHostOnTaskSetActiveParams();
            appHostOnTaskSetActiveParams.taskId = i;
            getProxyHandler().getMessageReceiver().accept(appHostOnTaskSetActiveParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.AppHost
        public void onUninstallShortcut(String str, String str2) {
            AppHostOnUninstallShortcutParams appHostOnUninstallShortcutParams = new AppHostOnUninstallShortcutParams();
            appHostOnUninstallShortcutParams.packageName = str;
            appHostOnUninstallShortcutParams.intentUri = str2;
            getProxyHandler().getMessageReceiver().accept(appHostOnUninstallShortcutParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16)));
        }
    }

    static final class Stub extends Interface.Stub<AppHost> {
        Stub(Core core, AppHost appHost) {
            super(core, appHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (!header.validateHeader(0)) {
                    return false;
                }
                switch (header.getType()) {
                    case 0:
                        getImpl().onAppListRefreshed(AppHostOnAppListRefreshedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).apps);
                        return true;
                    case 2:
                        getImpl().onAppAddedDeprecated(AppHostOnAppAddedDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).app);
                        return true;
                    case 3:
                        getImpl().onPackageRemoved(AppHostOnPackageRemovedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName);
                        return true;
                    case 4:
                        AppHostOnTaskCreatedParams appHostOnTaskCreatedParamsDeserialize = AppHostOnTaskCreatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onTaskCreated(appHostOnTaskCreatedParamsDeserialize.taskId, appHostOnTaskCreatedParamsDeserialize.packageName, appHostOnTaskCreatedParamsDeserialize.activity, appHostOnTaskCreatedParamsDeserialize.name, appHostOnTaskCreatedParamsDeserialize.intent, appHostOnTaskCreatedParamsDeserialize.sessionId);
                        return true;
                    case 5:
                        getImpl().onTaskDestroyed(AppHostOnTaskDestroyedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).taskId);
                        return true;
                    case 6:
                        getImpl().onTaskSetActive(AppHostOnTaskSetActiveParams.deserialize(serviceMessageAsServiceMessage.getPayload()).taskId);
                        return true;
                    case 7:
                        AppHostOnNotificationsEnabledChangedParams appHostOnNotificationsEnabledChangedParamsDeserialize = AppHostOnNotificationsEnabledChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onNotificationsEnabledChanged(appHostOnNotificationsEnabledChangedParamsDeserialize.packageName, appHostOnNotificationsEnabledChangedParamsDeserialize.enabled);
                        return true;
                    case 8:
                        getImpl().onPackageAdded(AppHostOnPackageAddedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).arcPackageInfo);
                        return true;
                    case 9:
                        getImpl().onPackageListRefreshed(AppHostOnPackageListRefreshedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packages);
                        return true;
                    case 10:
                        getImpl().onPackageModified(AppHostOnPackageModifiedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).arcPackageInfo);
                        return true;
                    case 11:
                        getImpl().onInstallShortcut(AppHostOnInstallShortcutParams.deserialize(serviceMessageAsServiceMessage.getPayload()).shortcut);
                        return true;
                    case 13:
                        AppHostOnPackageAppListRefreshedParams appHostOnPackageAppListRefreshedParamsDeserialize = AppHostOnPackageAppListRefreshedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onPackageAppListRefreshed(appHostOnPackageAppListRefreshedParamsDeserialize.packageName, appHostOnPackageAppListRefreshedParamsDeserialize.apps);
                        return true;
                    case 14:
                        getImpl().onInstallationStarted(AppHostOnInstallationStartedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName);
                        return true;
                    case 15:
                        getImpl().onInstallationFinished(AppHostOnInstallationFinishedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                        return true;
                    case 16:
                        AppHostOnUninstallShortcutParams appHostOnUninstallShortcutParamsDeserialize = AppHostOnUninstallShortcutParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onUninstallShortcut(appHostOnUninstallShortcutParamsDeserialize.packageName, appHostOnUninstallShortcutParamsDeserialize.intentUri);
                        return true;
                    case 17:
                        AppHostOnTaskDescriptionUpdatedParams appHostOnTaskDescriptionUpdatedParamsDeserialize = AppHostOnTaskDescriptionUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onTaskDescriptionUpdated(appHostOnTaskDescriptionUpdatedParamsDeserialize.taskId, appHostOnTaskDescriptionUpdatedParamsDeserialize.label, appHostOnTaskDescriptionUpdatedParamsDeserialize.iconPngData);
                        return true;
                    case 18:
                        AppHostOnTaskDescriptionChangedParams appHostOnTaskDescriptionChangedParamsDeserialize = AppHostOnTaskDescriptionChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onTaskDescriptionChanged(appHostOnTaskDescriptionChangedParamsDeserialize.taskId, appHostOnTaskDescriptionChangedParamsDeserialize.label, appHostOnTaskDescriptionChangedParamsDeserialize.icon, appHostOnTaskDescriptionChangedParamsDeserialize.primaryColor, appHostOnTaskDescriptionChangedParamsDeserialize.statusBarColor);
                        return true;
                    case 19:
                        AppHostOnInstallationProgressChangedParams appHostOnInstallationProgressChangedParamsDeserialize = AppHostOnInstallationProgressChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onInstallationProgressChanged(appHostOnInstallationProgressChangedParamsDeserialize.packageName, appHostOnInstallationProgressChangedParamsDeserialize.progress);
                        return true;
                    case 20:
                        AppHostOnInstallationActiveChangedParams appHostOnInstallationActiveChangedParamsDeserialize = AppHostOnInstallationActiveChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onInstallationActiveChanged(appHostOnInstallationActiveChangedParamsDeserialize.packageName, appHostOnInstallationActiveChangedParamsDeserialize.active);
                        return true;
                }
                    return false;
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AppHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.IntentHelperHost;
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
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.url.mojom.Url;

class IntentHelperHost_Internal {
    public static final Interface.Manager<IntentHelperHost, IntentHelperHost.Proxy> MANAGER = new Interface.Manager<IntentHelperHost, IntentHelperHost.Proxy>() { // from class: org.chromium.arc.mojom.IntentHelperHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IntentHelperHost[] buildArray(int i) {
            return new IntentHelperHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IntentHelperHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<IntentHelperHost> buildStub(Core core, IntentHelperHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.IntentHelperHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 50;
        }
    };

    static final class IntentHelperHostCloseCameraAppParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostCloseCameraAppParams() {
            super(8, 0);
        }

        public static IntentHelperHostCloseCameraAppParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperHostCloseCameraAppParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperHostCloseCameraAppParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperHostFactoryResetArcParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostFactoryResetArcParams() {
            super(8, 0);
        }

        public static IntentHelperHostFactoryResetArcParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperHostFactoryResetArcParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperHostFactoryResetArcParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperHostIsChromeAppEnabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int app;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostIsChromeAppEnabledParams() {
            super(16, 0);
        }

        public static IntentHelperHostIsChromeAppEnabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostIsChromeAppEnabledParams intentHelperHostIsChromeAppEnabledParams = new IntentHelperHostIsChromeAppEnabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                intentHelperHostIsChromeAppEnabledParams.app = i;
                ChromeApp.validate(i);
                return intentHelperHostIsChromeAppEnabledParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.app, 8);
        }

        private IntentHelperHostIsChromeAppEnabledParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperHostIsChromeAppEnabledResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isEnabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostIsChromeAppEnabledResponseParams() {
            super(16, 0);
        }

        public static IntentHelperHostIsChromeAppEnabledResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostIsChromeAppEnabledResponseParams intentHelperHostIsChromeAppEnabledResponseParams = new IntentHelperHostIsChromeAppEnabledResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostIsChromeAppEnabledResponseParams.isEnabled = decoder.readBoolean(8, 0);
                return intentHelperHostIsChromeAppEnabledResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isEnabled, 8, 0);
        }

        private IntentHelperHostIsChromeAppEnabledResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperHostIsChromeAppEnabledResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperHost.IsChromeAppEnabledResponse mCallback;

        IntentHelperHostIsChromeAppEnabledResponseParamsForwardToCallback(IntentHelperHost.IsChromeAppEnabledResponse isChromeAppEnabledResponse) {
            this.mCallback = isChromeAppEnabledResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(15, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(IntentHelperHostIsChromeAppEnabledResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isEnabled));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperHostIsChromeAppEnabledResponseParamsProxyToResponder implements IntentHelperHost.IsChromeAppEnabledResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperHostIsChromeAppEnabledResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            IntentHelperHostIsChromeAppEnabledResponseParams intentHelperHostIsChromeAppEnabledResponseParams = new IntentHelperHostIsChromeAppEnabledResponseParams();
            intentHelperHostIsChromeAppEnabledResponseParams.isEnabled = bool.booleanValue();
            this.mMessageReceiver.accept(intentHelperHostIsChromeAppEnabledResponseParams.serializeWithHeader(this.mCore, new MessageHeader(15, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperHostLaunchCameraAppParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int intentId;
        public boolean isSecure;
        public int mode;
        public boolean shouldDownScale;
        public boolean shouldHandleResult;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(24, 36)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public IntentHelperHostLaunchCameraAppParams() {
            super(24, 36);
        }

        public static IntentHelperHostLaunchCameraAppParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                IntentHelperHostLaunchCameraAppParams intentHelperHostLaunchCameraAppParams = new IntentHelperHostLaunchCameraAppParams(i);
                intentHelperHostLaunchCameraAppParams.intentId = decoder.readInt(8);
                int i2 = decoder.readInt(12);
                intentHelperHostLaunchCameraAppParams.mode = i2;
                CameraIntentMode.validate(i2);
                intentHelperHostLaunchCameraAppParams.shouldHandleResult = decoder.readBoolean(16, 0);
                intentHelperHostLaunchCameraAppParams.shouldDownScale = decoder.readBoolean(16, 1);
                intentHelperHostLaunchCameraAppParams.isSecure = decoder.readBoolean(16, 2);
                if (i >= 36) {
                    intentHelperHostLaunchCameraAppParams.taskId = decoder.readInt(20);
                }
                return intentHelperHostLaunchCameraAppParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.intentId, 8);
            encoderAtDataOffset.encode(this.mode, 12);
            encoderAtDataOffset.encode(this.shouldHandleResult, 16, 0);
            encoderAtDataOffset.encode(this.shouldDownScale, 16, 1);
            encoderAtDataOffset.encode(this.isSecure, 16, 2);
            encoderAtDataOffset.encode(this.taskId, 20);
        }

        private IntentHelperHostLaunchCameraAppParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperHostOnAndroidSettingChangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isEnabled;
        public int setting;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnAndroidSettingChangeParams() {
            super(16, 0);
        }

        public static IntentHelperHostOnAndroidSettingChangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnAndroidSettingChangeParams intentHelperHostOnAndroidSettingChangeParams = new IntentHelperHostOnAndroidSettingChangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                intentHelperHostOnAndroidSettingChangeParams.setting = i;
                AndroidSetting.validate(i);
                intentHelperHostOnAndroidSettingChangeParams.isEnabled = decoder.readBoolean(12, 0);
                return intentHelperHostOnAndroidSettingChangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.setting, 8);
            encoderAtDataOffset.encode(this.isEnabled, 12, 0);
        }

        private IntentHelperHostOnAndroidSettingChangeParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperHostOnCloseSystemDialogsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnCloseSystemDialogsParams() {
            super(8, 0);
        }

        public static IntentHelperHostOnCloseSystemDialogsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperHostOnCloseSystemDialogsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperHostOnCloseSystemDialogsParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperHostOnDownloadAddedDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String ownerPackageName;
        public String relativePath;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnDownloadAddedDeprecatedParams() {
            super(24, 0);
        }

        public static IntentHelperHostOnDownloadAddedDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnDownloadAddedDeprecatedParams intentHelperHostOnDownloadAddedDeprecatedParams = new IntentHelperHostOnDownloadAddedDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnDownloadAddedDeprecatedParams.relativePath = decoder.readString(8, false);
                intentHelperHostOnDownloadAddedDeprecatedParams.ownerPackageName = decoder.readString(16, false);
                return intentHelperHostOnDownloadAddedDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.relativePath, 8, false);
            encoderAtDataOffset.encode(this.ownerPackageName, 16, false);
        }

        private IntentHelperHostOnDownloadAddedDeprecatedParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperHostOnIconInvalidatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnIconInvalidatedParams() {
            super(16, 0);
        }

        public static IntentHelperHostOnIconInvalidatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnIconInvalidatedParams intentHelperHostOnIconInvalidatedParams = new IntentHelperHostOnIconInvalidatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnIconInvalidatedParams.packageName = decoder.readString(8, false);
                return intentHelperHostOnIconInvalidatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private IntentHelperHostOnIconInvalidatedParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperHostOnIntentFiltersUpdatedForPackageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentFilter[] intentFilters;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnIntentFiltersUpdatedForPackageParams() {
            super(24, 0);
        }

        public static IntentHelperHostOnIntentFiltersUpdatedForPackageParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperHostOnIntentFiltersUpdatedForPackageParams intentHelperHostOnIntentFiltersUpdatedForPackageParams = new IntentHelperHostOnIntentFiltersUpdatedForPackageParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnIntentFiltersUpdatedForPackageParams.packageName = decoderOutline3.readString(8, false);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperHostOnIntentFiltersUpdatedForPackageParams.intentFilters = new IntentFilter[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperHostOnIntentFiltersUpdatedForPackageParams.intentFilters[i] = IntentFilter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperHostOnIntentFiltersUpdatedForPackageParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            IntentFilter[] intentFilterArr = this.intentFilters;
            if (intentFilterArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(intentFilterArr.length, 16, -1);
            int i = 0;
            while (true) {
                IntentFilter[] intentFilterArr2 = this.intentFilters;
                if (i >= intentFilterArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) intentFilterArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperHostOnIntentFiltersUpdatedForPackageParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperHostOnIntentFiltersUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentFilter[] intentFilters;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnIntentFiltersUpdatedParams() {
            super(16, 0);
        }

        public static IntentHelperHostOnIntentFiltersUpdatedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperHostOnIntentFiltersUpdatedParams intentHelperHostOnIntentFiltersUpdatedParams = new IntentHelperHostOnIntentFiltersUpdatedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperHostOnIntentFiltersUpdatedParams.intentFilters = new IntentFilter[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperHostOnIntentFiltersUpdatedParams.intentFilters[i] = IntentFilter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperHostOnIntentFiltersUpdatedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            IntentFilter[] intentFilterArr = this.intentFilters;
            if (intentFilterArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(intentFilterArr.length, 8, -1);
            int i = 0;
            while (true) {
                IntentFilter[] intentFilterArr2 = this.intentFilters;
                if (i >= intentFilterArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) intentFilterArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperHostOnIntentFiltersUpdatedParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperHostOnOpenAppWithIntentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public LaunchIntent intent;
        public Url startUrl;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnOpenAppWithIntentParams() {
            super(24, 0);
        }

        public static IntentHelperHostOnOpenAppWithIntentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnOpenAppWithIntentParams intentHelperHostOnOpenAppWithIntentParams = new IntentHelperHostOnOpenAppWithIntentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnOpenAppWithIntentParams.startUrl = Url.decode(decoder.readPointer(8, false));
                intentHelperHostOnOpenAppWithIntentParams.intent = LaunchIntent.decode(decoder.readPointer(16, false));
                return intentHelperHostOnOpenAppWithIntentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.startUrl, 8, false);
            encoderAtDataOffset.encode((Struct) this.intent, 16, false);
        }

        private IntentHelperHostOnOpenAppWithIntentParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperHostOnOpenChromePageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int page;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0), new DataHeader(16, 18)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public IntentHelperHostOnOpenChromePageParams() {
            super(16, 18);
        }

        public static IntentHelperHostOnOpenChromePageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                IntentHelperHostOnOpenChromePageParams intentHelperHostOnOpenChromePageParams = new IntentHelperHostOnOpenChromePageParams(i);
                if (i >= 18) {
                    int i2 = decoder.readInt(8);
                    intentHelperHostOnOpenChromePageParams.page = i2;
                    ChromePage.validate(i2);
                }
                return intentHelperHostOnOpenChromePageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.page, 8);
        }

        private IntentHelperHostOnOpenChromePageParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperHostOnOpenCustomTabParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int taskId;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnOpenCustomTabParams() {
            super(24, 0);
        }

        public static IntentHelperHostOnOpenCustomTabParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnOpenCustomTabParams intentHelperHostOnOpenCustomTabParams = new IntentHelperHostOnOpenCustomTabParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnOpenCustomTabParams.url = decoder.readString(8, false);
                intentHelperHostOnOpenCustomTabParams.taskId = decoder.readInt(16);
                return intentHelperHostOnOpenCustomTabParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.url, 8, false);
            encoderAtDataOffset.encode(this.taskId, 16);
        }

        private IntentHelperHostOnOpenCustomTabParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperHostOnOpenCustomTabResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CustomTabSession session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnOpenCustomTabResponseParams() {
            super(16, 0);
        }

        public static IntentHelperHostOnOpenCustomTabResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnOpenCustomTabResponseParams intentHelperHostOnOpenCustomTabResponseParams = new IntentHelperHostOnOpenCustomTabResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnOpenCustomTabResponseParams.session = (CustomTabSession) decoder.readServiceInterface(8, true, CustomTabSession.MANAGER);
                return intentHelperHostOnOpenCustomTabResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.session, 8, true, (Interface.Manager<CustomTabSession, ?>) CustomTabSession.MANAGER);
        }

        private IntentHelperHostOnOpenCustomTabResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperHostOnOpenCustomTabResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperHost.OnOpenCustomTabResponse mCallback;

        IntentHelperHostOnOpenCustomTabResponseParamsForwardToCallback(IntentHelperHost.OnOpenCustomTabResponse onOpenCustomTabResponse) {
            this.mCallback = onOpenCustomTabResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(17, 2)) {
                    return false;
                }
                this.mCallback.call(IntentHelperHostOnOpenCustomTabResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).session);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperHostOnOpenCustomTabResponseParamsProxyToResponder implements IntentHelperHost.OnOpenCustomTabResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperHostOnOpenCustomTabResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(CustomTabSession customTabSession) {
            IntentHelperHostOnOpenCustomTabResponseParams intentHelperHostOnOpenCustomTabResponseParams = new IntentHelperHostOnOpenCustomTabResponseParams();
            intentHelperHostOnOpenCustomTabResponseParams.session = customTabSession;
            this.mMessageReceiver.accept(intentHelperHostOnOpenCustomTabResponseParams.serializeWithHeader(this.mCore, new MessageHeader(17, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperHostOnOpenDownloadsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnOpenDownloadsParams() {
            super(8, 0);
        }

        public static IntentHelperHostOnOpenDownloadsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperHostOnOpenDownloadsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperHostOnOpenDownloadsParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperHostOnOpenGlobalActionsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnOpenGlobalActionsParams() {
            super(8, 0);
        }

        public static IntentHelperHostOnOpenGlobalActionsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperHostOnOpenGlobalActionsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperHostOnOpenGlobalActionsParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperHostOnOpenUrlParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnOpenUrlParams() {
            super(16, 0);
        }

        public static IntentHelperHostOnOpenUrlParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnOpenUrlParams intentHelperHostOnOpenUrlParams = new IntentHelperHostOnOpenUrlParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnOpenUrlParams.url = decoder.readString(8, false);
                return intentHelperHostOnOpenUrlParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private IntentHelperHostOnOpenUrlParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperHostOnOpenWebAppParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOnOpenWebAppParams() {
            super(16, 0);
        }

        public static IntentHelperHostOnOpenWebAppParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostOnOpenWebAppParams intentHelperHostOnOpenWebAppParams = new IntentHelperHostOnOpenWebAppParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperHostOnOpenWebAppParams.url = decoder.readString(8, false);
                return intentHelperHostOnOpenWebAppParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private IntentHelperHostOnOpenWebAppParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperHostOnSupportedLinksChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SupportedLinksPackage[] addedPackages;
        public SupportedLinksPackage[] removedPackages;
        public int source;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 48)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public IntentHelperHostOnSupportedLinksChangedParams() {
            super(32, 48);
        }

        public static IntentHelperHostOnSupportedLinksChangedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                int i = decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                IntentHelperHostOnSupportedLinksChangedParams intentHelperHostOnSupportedLinksChangedParams = new IntentHelperHostOnSupportedLinksChangedParams(i);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperHostOnSupportedLinksChangedParams.addedPackages = new SupportedLinksPackage[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    intentHelperHostOnSupportedLinksChangedParams.addedPackages[i2] = SupportedLinksPackage.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                Decoder pointer2 = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                intentHelperHostOnSupportedLinksChangedParams.removedPackages = new SupportedLinksPackage[dataHeaderForPointerArray2.elementsOrVersion];
                for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                    intentHelperHostOnSupportedLinksChangedParams.removedPackages[i3] = SupportedLinksPackage.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer2, false));
                }
                if (i >= 48) {
                    int i4 = decoderOutline3.readInt(24);
                    intentHelperHostOnSupportedLinksChangedParams.source = i4;
                    SupportedLinkChangeSource.validate(i4);
                }
                return intentHelperHostOnSupportedLinksChangedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            SupportedLinksPackage[] supportedLinksPackageArr = this.addedPackages;
            if (supportedLinksPackageArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(supportedLinksPackageArr.length, 8, -1);
                int i = 0;
                while (true) {
                    SupportedLinksPackage[] supportedLinksPackageArr2 = this.addedPackages;
                    if (i >= supportedLinksPackageArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) supportedLinksPackageArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(8, false);
            }
            SupportedLinksPackage[] supportedLinksPackageArr3 = this.removedPackages;
            if (supportedLinksPackageArr3 != null) {
                Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(supportedLinksPackageArr3.length, 16, -1);
                int i2 = 0;
                while (true) {
                    SupportedLinksPackage[] supportedLinksPackageArr4 = this.removedPackages;
                    if (i2 >= supportedLinksPackageArr4.length) {
                        break;
                    }
                    encoderEncodePointerArray2.encode((Struct) supportedLinksPackageArr4[i2], (i2 * 8) + 8, false);
                    i2++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(16, false);
            }
            encoderAtDataOffset.encode(this.source, 24);
        }

        private IntentHelperHostOnSupportedLinksChangedParams(int i) {
            super(32, i);
        }
    }

    static final class IntentHelperHostOpenVolumeControlParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOpenVolumeControlParams() {
            super(8, 0);
        }

        public static IntentHelperHostOpenVolumeControlParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperHostOpenVolumeControlParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperHostOpenVolumeControlParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperHostOpenWallpaperPickerParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostOpenWallpaperPickerParams() {
            super(8, 0);
        }

        public static IntentHelperHostOpenWallpaperPickerParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperHostOpenWallpaperPickerParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperHostOpenWallpaperPickerParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperHostRecordShareFilesMetricsDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int flag;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperHostRecordShareFilesMetricsDeprecatedParams() {
            super(16, 0);
        }

        public static IntentHelperHostRecordShareFilesMetricsDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperHostRecordShareFilesMetricsDeprecatedParams intentHelperHostRecordShareFilesMetricsDeprecatedParams = new IntentHelperHostRecordShareFilesMetricsDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                intentHelperHostRecordShareFilesMetricsDeprecatedParams.flag = i;
                ShareFiles.validate(i);
                return intentHelperHostRecordShareFilesMetricsDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.flag, 8);
        }

        private IntentHelperHostRecordShareFilesMetricsDeprecatedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements IntentHelperHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void closeCameraApp() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperHostCloseCameraAppParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void factoryResetArc() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperHostFactoryResetArcParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void isChromeAppEnabled(int i, IntentHelperHost.IsChromeAppEnabledResponse isChromeAppEnabledResponse) {
            IntentHelperHostIsChromeAppEnabledParams intentHelperHostIsChromeAppEnabledParams = new IntentHelperHostIsChromeAppEnabledParams();
            intentHelperHostIsChromeAppEnabledParams.app = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperHostIsChromeAppEnabledParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15, 1, 0L)), new IntentHelperHostIsChromeAppEnabledResponseParamsForwardToCallback(isChromeAppEnabledResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void launchCameraApp(int i, int i2, boolean z, boolean z2, boolean z3, int i3) {
            IntentHelperHostLaunchCameraAppParams intentHelperHostLaunchCameraAppParams = new IntentHelperHostLaunchCameraAppParams();
            intentHelperHostLaunchCameraAppParams.intentId = i;
            intentHelperHostLaunchCameraAppParams.mode = i2;
            intentHelperHostLaunchCameraAppParams.shouldHandleResult = z;
            intentHelperHostLaunchCameraAppParams.shouldDownScale = z2;
            intentHelperHostLaunchCameraAppParams.isSecure = z3;
            intentHelperHostLaunchCameraAppParams.taskId = i3;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostLaunchCameraAppParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onAndroidSettingChange(int i, boolean z) {
            IntentHelperHostOnAndroidSettingChangeParams intentHelperHostOnAndroidSettingChangeParams = new IntentHelperHostOnAndroidSettingChangeParams();
            intentHelperHostOnAndroidSettingChangeParams.setting = i;
            intentHelperHostOnAndroidSettingChangeParams.isEnabled = z;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnAndroidSettingChangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onCloseSystemDialogs() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperHostOnCloseSystemDialogsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onDownloadAddedDeprecated(String str, String str2) {
            IntentHelperHostOnDownloadAddedDeprecatedParams intentHelperHostOnDownloadAddedDeprecatedParams = new IntentHelperHostOnDownloadAddedDeprecatedParams();
            intentHelperHostOnDownloadAddedDeprecatedParams.relativePath = str;
            intentHelperHostOnDownloadAddedDeprecatedParams.ownerPackageName = str2;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnDownloadAddedDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onIconInvalidated(String str) {
            IntentHelperHostOnIconInvalidatedParams intentHelperHostOnIconInvalidatedParams = new IntentHelperHostOnIconInvalidatedParams();
            intentHelperHostOnIconInvalidatedParams.packageName = str;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnIconInvalidatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onIntentFiltersUpdated(IntentFilter[] intentFilterArr) {
            IntentHelperHostOnIntentFiltersUpdatedParams intentHelperHostOnIntentFiltersUpdatedParams = new IntentHelperHostOnIntentFiltersUpdatedParams();
            intentHelperHostOnIntentFiltersUpdatedParams.intentFilters = intentFilterArr;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnIntentFiltersUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onIntentFiltersUpdatedForPackage(String str, IntentFilter[] intentFilterArr) {
            IntentHelperHostOnIntentFiltersUpdatedForPackageParams intentHelperHostOnIntentFiltersUpdatedForPackageParams = new IntentHelperHostOnIntentFiltersUpdatedForPackageParams();
            intentHelperHostOnIntentFiltersUpdatedForPackageParams.packageName = str;
            intentHelperHostOnIntentFiltersUpdatedForPackageParams.intentFilters = intentFilterArr;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnIntentFiltersUpdatedForPackageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onOpenAppWithIntent(Url url, LaunchIntent launchIntent) {
            IntentHelperHostOnOpenAppWithIntentParams intentHelperHostOnOpenAppWithIntentParams = new IntentHelperHostOnOpenAppWithIntentParams();
            intentHelperHostOnOpenAppWithIntentParams.startUrl = url;
            intentHelperHostOnOpenAppWithIntentParams.intent = launchIntent;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnOpenAppWithIntentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onOpenChromePage(int i) {
            IntentHelperHostOnOpenChromePageParams intentHelperHostOnOpenChromePageParams = new IntentHelperHostOnOpenChromePageParams();
            intentHelperHostOnOpenChromePageParams.page = i;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnOpenChromePageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onOpenCustomTab(String str, int i, IntentHelperHost.OnOpenCustomTabResponse onOpenCustomTabResponse) {
            IntentHelperHostOnOpenCustomTabParams intentHelperHostOnOpenCustomTabParams = new IntentHelperHostOnOpenCustomTabParams();
            intentHelperHostOnOpenCustomTabParams.url = str;
            intentHelperHostOnOpenCustomTabParams.taskId = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperHostOnOpenCustomTabParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17, 1, 0L)), new IntentHelperHostOnOpenCustomTabResponseParamsForwardToCallback(onOpenCustomTabResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onOpenDownloads() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperHostOnOpenDownloadsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onOpenGlobalActions() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperHostOnOpenGlobalActionsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onOpenUrl(String str) {
            IntentHelperHostOnOpenUrlParams intentHelperHostOnOpenUrlParams = new IntentHelperHostOnOpenUrlParams();
            intentHelperHostOnOpenUrlParams.url = str;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnOpenUrlParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onOpenWebApp(String str) {
            IntentHelperHostOnOpenWebAppParams intentHelperHostOnOpenWebAppParams = new IntentHelperHostOnOpenWebAppParams();
            intentHelperHostOnOpenWebAppParams.url = str;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnOpenWebAppParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void onSupportedLinksChanged(SupportedLinksPackage[] supportedLinksPackageArr, SupportedLinksPackage[] supportedLinksPackageArr2, int i) {
            IntentHelperHostOnSupportedLinksChangedParams intentHelperHostOnSupportedLinksChangedParams = new IntentHelperHostOnSupportedLinksChangedParams();
            intentHelperHostOnSupportedLinksChangedParams.addedPackages = supportedLinksPackageArr;
            intentHelperHostOnSupportedLinksChangedParams.removedPackages = supportedLinksPackageArr2;
            intentHelperHostOnSupportedLinksChangedParams.source = i;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostOnSupportedLinksChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void openVolumeControl() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperHostOpenVolumeControlParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void openWallpaperPicker() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperHostOpenWallpaperPickerParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperHost
        public void recordShareFilesMetricsDeprecated(int i) {
            IntentHelperHostRecordShareFilesMetricsDeprecatedParams intentHelperHostRecordShareFilesMetricsDeprecatedParams = new IntentHelperHostRecordShareFilesMetricsDeprecatedParams();
            intentHelperHostRecordShareFilesMetricsDeprecatedParams.flag = i;
            getProxyHandler().getMessageReceiver().accept(intentHelperHostRecordShareFilesMetricsDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }
    }

    static final class Stub extends Interface.Stub<IntentHelperHost> {
        Stub(Core core, IntentHelperHost intentHelperHost) {
            super(core, intentHelperHost);
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
                        getImpl().onOpenUrl(IntentHelperHostOnOpenUrlParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url);
                        return true;
                    case 1:
                        getImpl().onIconInvalidated(IntentHelperHostOnIconInvalidatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName);
                        return true;
                    case 2:
                        IntentHelperHostOnOpenDownloadsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onOpenDownloads();
                        return true;
                    case 3:
                        IntentHelperHostOpenWallpaperPickerParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().openWallpaperPicker();
                        return true;
                    case 5:
                        getImpl().onIntentFiltersUpdated(IntentHelperHostOnIntentFiltersUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).intentFilters);
                        return true;
                    case 6:
                        IntentHelperHostOpenVolumeControlParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().openVolumeControl();
                        return true;
                    case 7:
                        getImpl().onOpenChromePage(IntentHelperHostOnOpenChromePageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).page);
                        return true;
                    case 8:
                        getImpl().onOpenWebApp(IntentHelperHostOnOpenWebAppParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url);
                        return true;
                    case 9:
                        getImpl().recordShareFilesMetricsDeprecated(IntentHelperHostRecordShareFilesMetricsDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).flag);
                        return true;
                    case 11:
                        IntentHelperHostFactoryResetArcParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().factoryResetArc();
                        return true;
                    case 12:
                        IntentHelperHostLaunchCameraAppParams intentHelperHostLaunchCameraAppParamsDeserialize = IntentHelperHostLaunchCameraAppParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().launchCameraApp(intentHelperHostLaunchCameraAppParamsDeserialize.intentId, intentHelperHostLaunchCameraAppParamsDeserialize.mode, intentHelperHostLaunchCameraAppParamsDeserialize.shouldHandleResult, intentHelperHostLaunchCameraAppParamsDeserialize.shouldDownScale, intentHelperHostLaunchCameraAppParamsDeserialize.isSecure, intentHelperHostLaunchCameraAppParamsDeserialize.taskId);
                        return true;
                    case 13:
                        IntentHelperHostOnIntentFiltersUpdatedForPackageParams intentHelperHostOnIntentFiltersUpdatedForPackageParamsDeserialize = IntentHelperHostOnIntentFiltersUpdatedForPackageParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onIntentFiltersUpdatedForPackage(intentHelperHostOnIntentFiltersUpdatedForPackageParamsDeserialize.packageName, intentHelperHostOnIntentFiltersUpdatedForPackageParamsDeserialize.intentFilters);
                        return true;
                    case 14:
                        IntentHelperHostCloseCameraAppParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().closeCameraApp();
                        return true;
                    case 18:
                        IntentHelperHostOnDownloadAddedDeprecatedParams intentHelperHostOnDownloadAddedDeprecatedParamsDeserialize = IntentHelperHostOnDownloadAddedDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onDownloadAddedDeprecated(intentHelperHostOnDownloadAddedDeprecatedParamsDeserialize.relativePath, intentHelperHostOnDownloadAddedDeprecatedParamsDeserialize.ownerPackageName);
                        return true;
                    case 19:
                        IntentHelperHostOnOpenAppWithIntentParams intentHelperHostOnOpenAppWithIntentParamsDeserialize = IntentHelperHostOnOpenAppWithIntentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onOpenAppWithIntent(intentHelperHostOnOpenAppWithIntentParamsDeserialize.startUrl, intentHelperHostOnOpenAppWithIntentParamsDeserialize.intent);
                        return true;
                    case 20:
                        IntentHelperHostOnSupportedLinksChangedParams intentHelperHostOnSupportedLinksChangedParamsDeserialize = IntentHelperHostOnSupportedLinksChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onSupportedLinksChanged(intentHelperHostOnSupportedLinksChangedParamsDeserialize.addedPackages, intentHelperHostOnSupportedLinksChangedParamsDeserialize.removedPackages, intentHelperHostOnSupportedLinksChangedParamsDeserialize.source);
                        return true;
                    case 21:
                        IntentHelperHostOnOpenGlobalActionsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onOpenGlobalActions();
                        return true;
                    case 22:
                        IntentHelperHostOnCloseSystemDialogsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onCloseSystemDialogs();
                        return true;
                    case 23:
                        IntentHelperHostOnAndroidSettingChangeParams intentHelperHostOnAndroidSettingChangeParamsDeserialize = IntentHelperHostOnAndroidSettingChangeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onAndroidSettingChange(intentHelperHostOnAndroidSettingChangeParamsDeserialize.setting, intentHelperHostOnAndroidSettingChangeParamsDeserialize.isEnabled);
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
                if (!header.validateHeader(1)) {
                    return false;
                }
                int type = header.getType();
                if (type == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), IntentHelperHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 15) {
                    getImpl().isChromeAppEnabled(IntentHelperHostIsChromeAppEnabledParams.deserialize(serviceMessageAsServiceMessage.getPayload()).app, new IntentHelperHostIsChromeAppEnabledResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 17) {
                    return false;
                }
                IntentHelperHostOnOpenCustomTabParams intentHelperHostOnOpenCustomTabParamsDeserialize = IntentHelperHostOnOpenCustomTabParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onOpenCustomTab(intentHelperHostOnOpenCustomTabParamsDeserialize.url, intentHelperHostOnOpenCustomTabParamsDeserialize.taskId, new IntentHelperHostOnOpenCustomTabResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

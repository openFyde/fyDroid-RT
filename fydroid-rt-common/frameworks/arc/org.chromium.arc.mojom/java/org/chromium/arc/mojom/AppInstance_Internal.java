package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.AppInstance;
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

class AppInstance_Internal {
    public static final Interface.Manager<AppInstance, AppInstance.Proxy> MANAGER = new Interface.Manager<AppInstance, AppInstance.Proxy>() { // from class: org.chromium.arc.mojom.AppInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppInstance[] buildArray(int i) {
            return new AppInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AppInstance> buildStub(Core core, AppInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AppInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 63;
        }
    };

    static final class AppInstanceCloseTaskParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceCloseTaskParams() {
            super(16, 0);
        }

        public static AppInstanceCloseTaskParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceCloseTaskParams appInstanceCloseTaskParams = new AppInstanceCloseTaskParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceCloseTaskParams.taskId = decoder.readInt(8);
                return appInstanceCloseTaskParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.taskId, 8);
        }

        private AppInstanceCloseTaskParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceGetAndroidIdParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAndroidIdParams() {
            super(8, 0);
        }

        public static AppInstanceGetAndroidIdParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AppInstanceGetAndroidIdParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AppInstanceGetAndroidIdParams(int i) {
            super(8, i);
        }
    }

    static final class AppInstanceGetAndroidIdResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long androidId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAndroidIdResponseParams() {
            super(16, 0);
        }

        public static AppInstanceGetAndroidIdResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAndroidIdResponseParams appInstanceGetAndroidIdResponseParams = new AppInstanceGetAndroidIdResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAndroidIdResponseParams.androidId = decoder.readLong(8);
                return appInstanceGetAndroidIdResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.androidId, 8);
        }

        private AppInstanceGetAndroidIdResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceGetAndroidIdResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetAndroidIdResponse mCallback;

        AppInstanceGetAndroidIdResponseParamsForwardToCallback(AppInstance.GetAndroidIdResponse getAndroidIdResponse) {
            this.mCallback = getAndroidIdResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(33, 2)) {
                    return false;
                }
                this.mCallback.call(Long.valueOf(AppInstanceGetAndroidIdResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).androidId));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetAndroidIdResponseParamsProxyToResponder implements AppInstance.GetAndroidIdResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetAndroidIdResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Long l) {
            AppInstanceGetAndroidIdResponseParams appInstanceGetAndroidIdResponseParams = new AppInstanceGetAndroidIdResponseParams();
            appInstanceGetAndroidIdResponseParams.androidId = l.longValue();
            this.mMessageReceiver.accept(appInstanceGetAndroidIdResponseParams.serializeWithHeader(this.mCore, new MessageHeader(33, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceGetAppCategoryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppCategoryParams() {
            super(16, 0);
        }

        public static AppInstanceGetAppCategoryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppCategoryParams appInstanceGetAppCategoryParams = new AppInstanceGetAppCategoryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAppCategoryParams.packageName = decoder.readString(8, false);
                return appInstanceGetAppCategoryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private AppInstanceGetAppCategoryParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceGetAppCategoryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int category;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppCategoryResponseParams() {
            super(16, 0);
        }

        public static AppInstanceGetAppCategoryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppCategoryResponseParams appInstanceGetAppCategoryResponseParams = new AppInstanceGetAppCategoryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                appInstanceGetAppCategoryResponseParams.category = i;
                AppCategory.validate(i);
                return appInstanceGetAppCategoryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.category, 8);
        }

        private AppInstanceGetAppCategoryResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceGetAppCategoryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetAppCategoryResponse mCallback;

        AppInstanceGetAppCategoryResponseParamsForwardToCallback(AppInstance.GetAppCategoryResponse getAppCategoryResponse) {
            this.mCallback = getAppCategoryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(41, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(AppInstanceGetAppCategoryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).category));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetAppCategoryResponseParamsProxyToResponder implements AppInstance.GetAppCategoryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetAppCategoryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            AppInstanceGetAppCategoryResponseParams appInstanceGetAppCategoryResponseParams = new AppInstanceGetAppCategoryResponseParams();
            appInstanceGetAppCategoryResponseParams.category = num.intValue();
            this.mMessageReceiver.accept(appInstanceGetAppCategoryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(41, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceGetAppIconParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String activity;
        public String packageName;
        public int pixelSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppIconParams() {
            super(32, 0);
        }

        public static AppInstanceGetAppIconParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppIconParams appInstanceGetAppIconParams = new AppInstanceGetAppIconParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAppIconParams.packageName = decoder.readString(8, false);
                appInstanceGetAppIconParams.activity = decoder.readString(16, false);
                appInstanceGetAppIconParams.pixelSize = decoder.readInt(24);
                return appInstanceGetAppIconParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.activity, 16, false);
            encoderAtDataOffset.encode(this.pixelSize, 24);
        }

        private AppInstanceGetAppIconParams(int i) {
            super(32, i);
        }
    }

    static final class AppInstanceGetAppIconResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public RawIconPngData icon;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppIconResponseParams() {
            super(16, 0);
        }

        public static AppInstanceGetAppIconResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppIconResponseParams appInstanceGetAppIconResponseParams = new AppInstanceGetAppIconResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAppIconResponseParams.icon = RawIconPngData.decode(decoder.readPointer(8, false));
                return appInstanceGetAppIconResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.icon, 8, false);
        }

        private AppInstanceGetAppIconResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceGetAppIconResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetAppIconResponse mCallback;

        AppInstanceGetAppIconResponseParamsForwardToCallback(AppInstance.GetAppIconResponse getAppIconResponse) {
            this.mCallback = getAppIconResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(35, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceGetAppIconResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).icon);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetAppIconResponseParamsProxyToResponder implements AppInstance.GetAppIconResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetAppIconResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(RawIconPngData rawIconPngData) {
            AppInstanceGetAppIconResponseParams appInstanceGetAppIconResponseParams = new AppInstanceGetAppIconResponseParams();
            appInstanceGetAppIconResponseParams.icon = rawIconPngData;
            this.mMessageReceiver.accept(appInstanceGetAppIconResponseParams.serializeWithHeader(this.mCore, new MessageHeader(35, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceGetAppShortcutGlobalQueryItemsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int maxResults;
        public String query;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppShortcutGlobalQueryItemsParams() {
            super(24, 0);
        }

        public static AppInstanceGetAppShortcutGlobalQueryItemsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppShortcutGlobalQueryItemsParams appInstanceGetAppShortcutGlobalQueryItemsParams = new AppInstanceGetAppShortcutGlobalQueryItemsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAppShortcutGlobalQueryItemsParams.query = decoder.readString(8, false);
                appInstanceGetAppShortcutGlobalQueryItemsParams.maxResults = decoder.readInt(16);
                return appInstanceGetAppShortcutGlobalQueryItemsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.query, 8, false);
            encoderAtDataOffset.encode(this.maxResults, 16);
        }

        private AppInstanceGetAppShortcutGlobalQueryItemsParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceGetAppShortcutGlobalQueryItemsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppShortcutItem[] shortcutItems;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppShortcutGlobalQueryItemsResponseParams() {
            super(16, 0);
        }

        public static AppInstanceGetAppShortcutGlobalQueryItemsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AppInstanceGetAppShortcutGlobalQueryItemsResponseParams appInstanceGetAppShortcutGlobalQueryItemsResponseParams = new AppInstanceGetAppShortcutGlobalQueryItemsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                appInstanceGetAppShortcutGlobalQueryItemsResponseParams.shortcutItems = new AppShortcutItem[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    appInstanceGetAppShortcutGlobalQueryItemsResponseParams.shortcutItems[i] = AppShortcutItem.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return appInstanceGetAppShortcutGlobalQueryItemsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            AppShortcutItem[] appShortcutItemArr = this.shortcutItems;
            if (appShortcutItemArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(appShortcutItemArr.length, 8, -1);
            int i = 0;
            while (true) {
                AppShortcutItem[] appShortcutItemArr2 = this.shortcutItems;
                if (i >= appShortcutItemArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) appShortcutItemArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AppInstanceGetAppShortcutGlobalQueryItemsResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceGetAppShortcutGlobalQueryItemsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetAppShortcutGlobalQueryItemsResponse mCallback;

        AppInstanceGetAppShortcutGlobalQueryItemsResponseParamsForwardToCallback(AppInstance.GetAppShortcutGlobalQueryItemsResponse getAppShortcutGlobalQueryItemsResponse) {
            this.mCallback = getAppShortcutGlobalQueryItemsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(26, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceGetAppShortcutGlobalQueryItemsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).shortcutItems);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetAppShortcutGlobalQueryItemsResponseParamsProxyToResponder implements AppInstance.GetAppShortcutGlobalQueryItemsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetAppShortcutGlobalQueryItemsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(AppShortcutItem[] appShortcutItemArr) {
            AppInstanceGetAppShortcutGlobalQueryItemsResponseParams appInstanceGetAppShortcutGlobalQueryItemsResponseParams = new AppInstanceGetAppShortcutGlobalQueryItemsResponseParams();
            appInstanceGetAppShortcutGlobalQueryItemsResponseParams.shortcutItems = appShortcutItemArr;
            this.mMessageReceiver.accept(appInstanceGetAppShortcutGlobalQueryItemsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(26, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceGetAppShortcutIconParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String iconResourceId;
        public int pixelSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppShortcutIconParams() {
            super(24, 0);
        }

        public static AppInstanceGetAppShortcutIconParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppShortcutIconParams appInstanceGetAppShortcutIconParams = new AppInstanceGetAppShortcutIconParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAppShortcutIconParams.iconResourceId = decoder.readString(8, false);
                appInstanceGetAppShortcutIconParams.pixelSize = decoder.readInt(16);
                return appInstanceGetAppShortcutIconParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.iconResourceId, 8, false);
            encoderAtDataOffset.encode(this.pixelSize, 16);
        }

        private AppInstanceGetAppShortcutIconParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceGetAppShortcutIconResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public RawIconPngData icon;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppShortcutIconResponseParams() {
            super(16, 0);
        }

        public static AppInstanceGetAppShortcutIconResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppShortcutIconResponseParams appInstanceGetAppShortcutIconResponseParams = new AppInstanceGetAppShortcutIconResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAppShortcutIconResponseParams.icon = RawIconPngData.decode(decoder.readPointer(8, false));
                return appInstanceGetAppShortcutIconResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.icon, 8, false);
        }

        private AppInstanceGetAppShortcutIconResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceGetAppShortcutIconResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetAppShortcutIconResponse mCallback;

        AppInstanceGetAppShortcutIconResponseParamsForwardToCallback(AppInstance.GetAppShortcutIconResponse getAppShortcutIconResponse) {
            this.mCallback = getAppShortcutIconResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(36, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceGetAppShortcutIconResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).icon);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetAppShortcutIconResponseParamsProxyToResponder implements AppInstance.GetAppShortcutIconResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetAppShortcutIconResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(RawIconPngData rawIconPngData) {
            AppInstanceGetAppShortcutIconResponseParams appInstanceGetAppShortcutIconResponseParams = new AppInstanceGetAppShortcutIconResponseParams();
            appInstanceGetAppShortcutIconResponseParams.icon = rawIconPngData;
            this.mMessageReceiver.accept(appInstanceGetAppShortcutIconResponseParams.serializeWithHeader(this.mCore, new MessageHeader(36, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceGetAppShortcutItemsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppShortcutItemsParams() {
            super(16, 0);
        }

        public static AppInstanceGetAppShortcutItemsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetAppShortcutItemsParams appInstanceGetAppShortcutItemsParams = new AppInstanceGetAppShortcutItemsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetAppShortcutItemsParams.packageName = decoder.readString(8, false);
                return appInstanceGetAppShortcutItemsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private AppInstanceGetAppShortcutItemsParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceGetAppShortcutItemsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppShortcutItem[] shortcutItems;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetAppShortcutItemsResponseParams() {
            super(16, 0);
        }

        public static AppInstanceGetAppShortcutItemsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AppInstanceGetAppShortcutItemsResponseParams appInstanceGetAppShortcutItemsResponseParams = new AppInstanceGetAppShortcutItemsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                appInstanceGetAppShortcutItemsResponseParams.shortcutItems = new AppShortcutItem[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    appInstanceGetAppShortcutItemsResponseParams.shortcutItems[i] = AppShortcutItem.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return appInstanceGetAppShortcutItemsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            AppShortcutItem[] appShortcutItemArr = this.shortcutItems;
            if (appShortcutItemArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(appShortcutItemArr.length, 8, -1);
            int i = 0;
            while (true) {
                AppShortcutItem[] appShortcutItemArr2 = this.shortcutItems;
                if (i >= appShortcutItemArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) appShortcutItemArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AppInstanceGetAppShortcutItemsResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceGetAppShortcutItemsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetAppShortcutItemsResponse mCallback;

        AppInstanceGetAppShortcutItemsResponseParamsForwardToCallback(AppInstance.GetAppShortcutItemsResponse getAppShortcutItemsResponse) {
            this.mCallback = getAppShortcutItemsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(23, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceGetAppShortcutItemsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).shortcutItems);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetAppShortcutItemsResponseParamsProxyToResponder implements AppInstance.GetAppShortcutItemsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetAppShortcutItemsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(AppShortcutItem[] appShortcutItemArr) {
            AppInstanceGetAppShortcutItemsResponseParams appInstanceGetAppShortcutItemsResponseParams = new AppInstanceGetAppShortcutItemsResponseParams();
            appInstanceGetAppShortcutItemsResponseParams.shortcutItems = appShortcutItemArr;
            this.mMessageReceiver.accept(appInstanceGetAppShortcutItemsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(23, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceGetPackageIconParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean normalize;
        public String packageName;
        public int pixelSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetPackageIconParams() {
            super(24, 0);
        }

        public static AppInstanceGetPackageIconParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetPackageIconParams appInstanceGetPackageIconParams = new AppInstanceGetPackageIconParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetPackageIconParams.packageName = decoder.readString(8, false);
                appInstanceGetPackageIconParams.pixelSize = decoder.readInt(16);
                appInstanceGetPackageIconParams.normalize = decoder.readBoolean(20, 0);
                return appInstanceGetPackageIconParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.pixelSize, 16);
            encoderAtDataOffset.encode(this.normalize, 20, 0);
        }

        private AppInstanceGetPackageIconParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceGetPackageIconResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public RawIconPngData icon;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetPackageIconResponseParams() {
            super(16, 0);
        }

        public static AppInstanceGetPackageIconResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetPackageIconResponseParams appInstanceGetPackageIconResponseParams = new AppInstanceGetPackageIconResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetPackageIconResponseParams.icon = RawIconPngData.decode(decoder.readPointer(8, false));
                return appInstanceGetPackageIconResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.icon, 8, false);
        }

        private AppInstanceGetPackageIconResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceGetPackageIconResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetPackageIconResponse mCallback;

        AppInstanceGetPackageIconResponseParamsForwardToCallback(AppInstance.GetPackageIconResponse getPackageIconResponse) {
            this.mCallback = getPackageIconResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(37, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceGetPackageIconResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).icon);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetPackageIconResponseParamsProxyToResponder implements AppInstance.GetPackageIconResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetPackageIconResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(RawIconPngData rawIconPngData) {
            AppInstanceGetPackageIconResponseParams appInstanceGetPackageIconResponseParams = new AppInstanceGetPackageIconResponseParams();
            appInstanceGetPackageIconResponseParams.icon = rawIconPngData;
            this.mMessageReceiver.accept(appInstanceGetPackageIconResponseParams.serializeWithHeader(this.mCore, new MessageHeader(37, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int maxResults;
        public String query;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams() {
            super(24, 0);
        }

        public static AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams = new AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams.query = decoder.readString(8, false);
                appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams.maxResults = decoder.readInt(16);
                return appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.query, 8, false);
            encoderAtDataOffset.encode(this.maxResults, 16);
        }

        private AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppDiscoveryResult[] results;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams() {
            super(24, 0);
        }

        public static AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams = new AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams.results = new AppDiscoveryResult[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams.results[i] = AppDiscoveryResult.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                int i2 = decoderOutline3.readInt(16);
                appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams.state = i2;
                AppDiscoveryRequestState.validate(i2);
                return appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            AppDiscoveryResult[] appDiscoveryResultArr = this.results;
            if (appDiscoveryResultArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(appDiscoveryResultArr.length, 8, -1);
                int i = 0;
                while (true) {
                    AppDiscoveryResult[] appDiscoveryResultArr2 = this.results;
                    if (i >= appDiscoveryResultArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) appDiscoveryResultArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(8, false);
            }
            encoderAtDataOffset.encode(this.state, 16);
        }

        private AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams(int i) {
            super(24, i);
        }
    }

    static class AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.GetRecentAndSuggestedAppsFromPlayStoreResponse mCallback;

        AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsForwardToCallback(AppInstance.GetRecentAndSuggestedAppsFromPlayStoreResponse getRecentAndSuggestedAppsFromPlayStoreResponse) {
            this.mCallback = getRecentAndSuggestedAppsFromPlayStoreResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(16, 2)) {
                    return false;
                }
                AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsDeserialize = AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsDeserialize.state), appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsDeserialize.results);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsProxyToResponder implements AppInstance.GetRecentAndSuggestedAppsFromPlayStoreResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, AppDiscoveryResult[] appDiscoveryResultArr) {
            AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams = new AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams();
            appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams.state = num.intValue();
            appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams.results = appDiscoveryResultArr;
            this.mMessageReceiver.accept(appInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParams.serializeWithHeader(this.mCore, new MessageHeader(16, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceInitParams() {
            super(16, 0);
        }

        public static AppInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceInitParams appInstanceInitParams = new AppInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceInitParams.hostRemote = (AppHost) decoder.readServiceInterface(8, false, AppHost.MANAGER);
                return appInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<AppHost, ?>) AppHost.MANAGER);
        }

        private AppInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class AppInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.InitResponse mCallback;

        AppInstanceInitResponseParamsForwardToCallback(AppInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(21, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceInitResponseParamsProxyToResponder implements AppInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new AppInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(21, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceInstallPackageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcPackageInfo arcPackageInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceInstallPackageParams() {
            super(16, 0);
        }

        public static AppInstanceInstallPackageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceInstallPackageParams appInstanceInstallPackageParams = new AppInstanceInstallPackageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceInstallPackageParams.arcPackageInfo = ArcPackageInfo.decode(decoder.readPointer(8, false));
                return appInstanceInstallPackageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.arcPackageInfo, 8, false);
        }

        private AppInstanceInstallPackageParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceIsInstallableParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceIsInstallableParams() {
            super(16, 0);
        }

        public static AppInstanceIsInstallableParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceIsInstallableParams appInstanceIsInstallableParams = new AppInstanceIsInstallableParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceIsInstallableParams.packageName = decoder.readString(8, false);
                return appInstanceIsInstallableParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private AppInstanceIsInstallableParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceIsInstallableResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isInstallable;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceIsInstallableResponseParams() {
            super(16, 0);
        }

        public static AppInstanceIsInstallableResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceIsInstallableResponseParams appInstanceIsInstallableResponseParams = new AppInstanceIsInstallableResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceIsInstallableResponseParams.isInstallable = decoder.readBoolean(8, 0);
                return appInstanceIsInstallableResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isInstallable, 8, 0);
        }

        private AppInstanceIsInstallableResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceIsInstallableResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.IsInstallableResponse mCallback;

        AppInstanceIsInstallableResponseParamsForwardToCallback(AppInstance.IsInstallableResponse isInstallableResponse) {
            this.mCallback = isInstallableResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(34, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(AppInstanceIsInstallableResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isInstallable));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceIsInstallableResponseParamsProxyToResponder implements AppInstance.IsInstallableResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceIsInstallableResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            AppInstanceIsInstallableResponseParams appInstanceIsInstallableResponseParams = new AppInstanceIsInstallableResponseParams();
            appInstanceIsInstallableResponseParams.isInstallable = bool.booleanValue();
            this.mMessageReceiver.accept(appInstanceIsInstallableResponseParams.serializeWithHeader(this.mCore, new MessageHeader(34, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceLaunchAppShortcutItemParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long displayId;
        public String packageName;
        public String shortcutId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceLaunchAppShortcutItemParams() {
            super(32, 0);
        }

        public static AppInstanceLaunchAppShortcutItemParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceLaunchAppShortcutItemParams appInstanceLaunchAppShortcutItemParams = new AppInstanceLaunchAppShortcutItemParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceLaunchAppShortcutItemParams.packageName = decoder.readString(8, false);
                appInstanceLaunchAppShortcutItemParams.shortcutId = decoder.readString(16, false);
                appInstanceLaunchAppShortcutItemParams.displayId = decoder.readLong(24);
                return appInstanceLaunchAppShortcutItemParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.shortcutId, 16, false);
            encoderAtDataOffset.encode(this.displayId, 24);
        }

        private AppInstanceLaunchAppShortcutItemParams(int i) {
            super(32, i);
        }
    }

    static final class AppInstanceLaunchAppWithWindowInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String activity;
        public String packageName;
        public WindowInfo windowInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceLaunchAppWithWindowInfoParams() {
            super(32, 0);
        }

        public static AppInstanceLaunchAppWithWindowInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceLaunchAppWithWindowInfoParams appInstanceLaunchAppWithWindowInfoParams = new AppInstanceLaunchAppWithWindowInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceLaunchAppWithWindowInfoParams.packageName = decoder.readString(8, false);
                appInstanceLaunchAppWithWindowInfoParams.activity = decoder.readString(16, false);
                appInstanceLaunchAppWithWindowInfoParams.windowInfo = WindowInfo.decode(decoder.readPointer(24, false));
                return appInstanceLaunchAppWithWindowInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.activity, 16, false);
            encoderAtDataOffset.encode((Struct) this.windowInfo, 24, false);
        }

        private AppInstanceLaunchAppWithWindowInfoParams(int i) {
            super(32, i);
        }
    }

    static final class AppInstanceLaunchIntentWithWindowInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String intentUri;
        public WindowInfo windowInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceLaunchIntentWithWindowInfoParams() {
            super(24, 0);
        }

        public static AppInstanceLaunchIntentWithWindowInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceLaunchIntentWithWindowInfoParams appInstanceLaunchIntentWithWindowInfoParams = new AppInstanceLaunchIntentWithWindowInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceLaunchIntentWithWindowInfoParams.intentUri = decoder.readString(8, false);
                appInstanceLaunchIntentWithWindowInfoParams.windowInfo = WindowInfo.decode(decoder.readPointer(16, false));
                return appInstanceLaunchIntentWithWindowInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.intentUri, 8, false);
            encoderAtDataOffset.encode((Struct) this.windowInfo, 16, false);
        }

        private AppInstanceLaunchIntentWithWindowInfoParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceRemoveCachedIconParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String iconResourceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceRemoveCachedIconParams() {
            super(16, 0);
        }

        public static AppInstanceRemoveCachedIconParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceRemoveCachedIconParams appInstanceRemoveCachedIconParams = new AppInstanceRemoveCachedIconParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceRemoveCachedIconParams.iconResourceId = decoder.readString(8, false);
                return appInstanceRemoveCachedIconParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.iconResourceId, 8, false);
        }

        private AppInstanceRemoveCachedIconParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceRequestAppIconParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String activity;
        public String packageName;
        public int pixelSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceRequestAppIconParams() {
            super(32, 0);
        }

        public static AppInstanceRequestAppIconParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceRequestAppIconParams appInstanceRequestAppIconParams = new AppInstanceRequestAppIconParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceRequestAppIconParams.packageName = decoder.readString(8, false);
                appInstanceRequestAppIconParams.activity = decoder.readString(16, false);
                appInstanceRequestAppIconParams.pixelSize = decoder.readInt(24);
                return appInstanceRequestAppIconParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.activity, 16, false);
            encoderAtDataOffset.encode(this.pixelSize, 24);
        }

        private AppInstanceRequestAppIconParams(int i) {
            super(32, i);
        }
    }

    static final class AppInstanceRequestAppIconResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] iconPngData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceRequestAppIconResponseParams() {
            super(16, 0);
        }

        public static AppInstanceRequestAppIconResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceRequestAppIconResponseParams appInstanceRequestAppIconResponseParams = new AppInstanceRequestAppIconResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceRequestAppIconResponseParams.iconPngData = decoder.readBytes(8, 0, -1);
                return appInstanceRequestAppIconResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.iconPngData, 8, 0, -1);
        }

        private AppInstanceRequestAppIconResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceRequestAppIconResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.RequestAppIconResponse mCallback;

        AppInstanceRequestAppIconResponseParamsForwardToCallback(AppInstance.RequestAppIconResponse requestAppIconResponse) {
            this.mCallback = requestAppIconResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(27, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceRequestAppIconResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iconPngData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceRequestAppIconResponseParamsProxyToResponder implements AppInstance.RequestAppIconResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceRequestAppIconResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(byte[] bArr) {
            AppInstanceRequestAppIconResponseParams appInstanceRequestAppIconResponseParams = new AppInstanceRequestAppIconResponseParams();
            appInstanceRequestAppIconResponseParams.iconPngData = bArr;
            this.mMessageReceiver.accept(appInstanceRequestAppIconResponseParams.serializeWithHeader(this.mCore, new MessageHeader(27, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceRequestPackageIconParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean normalize;
        public String packageName;
        public int pixelSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceRequestPackageIconParams() {
            super(24, 0);
        }

        public static AppInstanceRequestPackageIconParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceRequestPackageIconParams appInstanceRequestPackageIconParams = new AppInstanceRequestPackageIconParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceRequestPackageIconParams.packageName = decoder.readString(8, false);
                appInstanceRequestPackageIconParams.pixelSize = decoder.readInt(16);
                appInstanceRequestPackageIconParams.normalize = decoder.readBoolean(20, 0);
                return appInstanceRequestPackageIconParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.pixelSize, 16);
            encoderAtDataOffset.encode(this.normalize, 20, 0);
        }

        private AppInstanceRequestPackageIconParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceRequestPackageIconResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] iconPngData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceRequestPackageIconResponseParams() {
            super(16, 0);
        }

        public static AppInstanceRequestPackageIconResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceRequestPackageIconResponseParams appInstanceRequestPackageIconResponseParams = new AppInstanceRequestPackageIconResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceRequestPackageIconResponseParams.iconPngData = decoder.readBytes(8, 0, -1);
                return appInstanceRequestPackageIconResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.iconPngData, 8, 0, -1);
        }

        private AppInstanceRequestPackageIconResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceRequestPackageIconResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.RequestPackageIconResponse mCallback;

        AppInstanceRequestPackageIconResponseParamsForwardToCallback(AppInstance.RequestPackageIconResponse requestPackageIconResponse) {
            this.mCallback = requestPackageIconResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(30, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceRequestPackageIconResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iconPngData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceRequestPackageIconResponseParamsProxyToResponder implements AppInstance.RequestPackageIconResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceRequestPackageIconResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(byte[] bArr) {
            AppInstanceRequestPackageIconResponseParams appInstanceRequestPackageIconResponseParams = new AppInstanceRequestPackageIconResponseParams();
            appInstanceRequestPackageIconResponseParams.iconPngData = bArr;
            this.mMessageReceiver.accept(appInstanceRequestPackageIconResponseParams.serializeWithHeader(this.mCore, new MessageHeader(30, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceRequestShortcutIconParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String iconResourceId;
        public int pixelSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceRequestShortcutIconParams() {
            super(24, 0);
        }

        public static AppInstanceRequestShortcutIconParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceRequestShortcutIconParams appInstanceRequestShortcutIconParams = new AppInstanceRequestShortcutIconParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceRequestShortcutIconParams.iconResourceId = decoder.readString(8, false);
                appInstanceRequestShortcutIconParams.pixelSize = decoder.readInt(16);
                return appInstanceRequestShortcutIconParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.iconResourceId, 8, false);
            encoderAtDataOffset.encode(this.pixelSize, 16);
        }

        private AppInstanceRequestShortcutIconParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceRequestShortcutIconResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] iconPngData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceRequestShortcutIconResponseParams() {
            super(16, 0);
        }

        public static AppInstanceRequestShortcutIconResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceRequestShortcutIconResponseParams appInstanceRequestShortcutIconResponseParams = new AppInstanceRequestShortcutIconResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceRequestShortcutIconResponseParams.iconPngData = decoder.readBytes(8, 0, -1);
                return appInstanceRequestShortcutIconResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.iconPngData, 8, 0, -1);
        }

        private AppInstanceRequestShortcutIconResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceRequestShortcutIconResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.RequestShortcutIconResponse mCallback;

        AppInstanceRequestShortcutIconResponseParamsForwardToCallback(AppInstance.RequestShortcutIconResponse requestShortcutIconResponse) {
            this.mCallback = requestShortcutIconResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(28, 2)) {
                    return false;
                }
                this.mCallback.call(AppInstanceRequestShortcutIconResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iconPngData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceRequestShortcutIconResponseParamsProxyToResponder implements AppInstance.RequestShortcutIconResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceRequestShortcutIconResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(byte[] bArr) {
            AppInstanceRequestShortcutIconResponseParams appInstanceRequestShortcutIconResponseParams = new AppInstanceRequestShortcutIconResponseParams();
            appInstanceRequestShortcutIconResponseParams.iconPngData = bArr;
            this.mMessageReceiver.accept(appInstanceRequestShortcutIconResponseParams.serializeWithHeader(this.mCore, new MessageHeader(28, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceSetAppLocaleParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String localeTag;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceSetAppLocaleParams() {
            super(24, 0);
        }

        public static AppInstanceSetAppLocaleParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceSetAppLocaleParams appInstanceSetAppLocaleParams = new AppInstanceSetAppLocaleParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceSetAppLocaleParams.packageName = decoder.readString(8, false);
                appInstanceSetAppLocaleParams.localeTag = decoder.readString(16, false);
                return appInstanceSetAppLocaleParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.localeTag, 16, false);
        }

        private AppInstanceSetAppLocaleParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceSetNotificationsEnabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceSetNotificationsEnabledParams() {
            super(24, 0);
        }

        public static AppInstanceSetNotificationsEnabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceSetNotificationsEnabledParams appInstanceSetNotificationsEnabledParams = new AppInstanceSetNotificationsEnabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceSetNotificationsEnabledParams.packageName = decoder.readString(8, false);
                appInstanceSetNotificationsEnabledParams.enabled = decoder.readBoolean(16, 0);
                return appInstanceSetNotificationsEnabledParams;
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

        private AppInstanceSetNotificationsEnabledParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceSetTaskActiveParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceSetTaskActiveParams() {
            super(16, 0);
        }

        public static AppInstanceSetTaskActiveParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceSetTaskActiveParams appInstanceSetTaskActiveParams = new AppInstanceSetTaskActiveParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceSetTaskActiveParams.taskId = decoder.readInt(8);
                return appInstanceSetTaskActiveParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.taskId, 8);
        }

        private AppInstanceSetTaskActiveParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceShowPackageInfoDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Rect dimensionOnScreen;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceShowPackageInfoDeprecatedParams() {
            super(24, 0);
        }

        public static AppInstanceShowPackageInfoDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceShowPackageInfoDeprecatedParams appInstanceShowPackageInfoDeprecatedParams = new AppInstanceShowPackageInfoDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceShowPackageInfoDeprecatedParams.packageName = decoder.readString(8, false);
                appInstanceShowPackageInfoDeprecatedParams.dimensionOnScreen = Rect.decode(decoder.readPointer(16, false));
                return appInstanceShowPackageInfoDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode((Struct) this.dimensionOnScreen, 16, false);
        }

        private AppInstanceShowPackageInfoDeprecatedParams(int i) {
            super(24, i);
        }
    }

    static final class AppInstanceShowPackageInfoOnPageDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Rect dimensionOnScreen;
        public String packageName;
        public int page;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceShowPackageInfoOnPageDeprecatedParams() {
            super(32, 0);
        }

        public static AppInstanceShowPackageInfoOnPageDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceShowPackageInfoOnPageDeprecatedParams appInstanceShowPackageInfoOnPageDeprecatedParams = new AppInstanceShowPackageInfoOnPageDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceShowPackageInfoOnPageDeprecatedParams.packageName = decoder.readString(8, false);
                int i = decoder.readInt(16);
                appInstanceShowPackageInfoOnPageDeprecatedParams.page = i;
                ShowPackageInfoPage.validate(i);
                appInstanceShowPackageInfoOnPageDeprecatedParams.dimensionOnScreen = Rect.decode(decoder.readPointer(24, false));
                return appInstanceShowPackageInfoOnPageDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.page, 16);
            encoderAtDataOffset.encode((Struct) this.dimensionOnScreen, 24, false);
        }

        private AppInstanceShowPackageInfoOnPageDeprecatedParams(int i) {
            super(32, i);
        }
    }

    static final class AppInstanceShowPackageInfoOnPageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long displayId;
        public String packageName;
        public int page;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceShowPackageInfoOnPageParams() {
            super(32, 0);
        }

        public static AppInstanceShowPackageInfoOnPageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceShowPackageInfoOnPageParams appInstanceShowPackageInfoOnPageParams = new AppInstanceShowPackageInfoOnPageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceShowPackageInfoOnPageParams.packageName = decoder.readString(8, false);
                int i = decoder.readInt(16);
                appInstanceShowPackageInfoOnPageParams.page = i;
                ShowPackageInfoPage.validate(i);
                appInstanceShowPackageInfoOnPageParams.displayId = decoder.readLong(24);
                return appInstanceShowPackageInfoOnPageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.page, 16);
            encoderAtDataOffset.encode(this.displayId, 24);
        }

        private AppInstanceShowPackageInfoOnPageParams(int i) {
            super(32, i);
        }
    }

    static final class AppInstanceStartFastAppReinstallFlowParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] arcPackageNames;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceStartFastAppReinstallFlowParams() {
            super(16, 0);
        }

        public static AppInstanceStartFastAppReinstallFlowParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AppInstanceStartFastAppReinstallFlowParams appInstanceStartFastAppReinstallFlowParams = new AppInstanceStartFastAppReinstallFlowParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                appInstanceStartFastAppReinstallFlowParams.arcPackageNames = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    appInstanceStartFastAppReinstallFlowParams.arcPackageNames[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return appInstanceStartFastAppReinstallFlowParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.arcPackageNames;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.arcPackageNames;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private AppInstanceStartFastAppReinstallFlowParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceStartPaiFlowParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceStartPaiFlowParams() {
            super(8, 0);
        }

        public static AppInstanceStartPaiFlowParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AppInstanceStartPaiFlowParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AppInstanceStartPaiFlowParams(int i) {
            super(8, i);
        }
    }

    static final class AppInstanceStartPaiFlowResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceStartPaiFlowResponseParams() {
            super(16, 0);
        }

        public static AppInstanceStartPaiFlowResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceStartPaiFlowResponseParams appInstanceStartPaiFlowResponseParams = new AppInstanceStartPaiFlowResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                appInstanceStartPaiFlowResponseParams.state = i;
                PaiFlowState.validate(i);
                return appInstanceStartPaiFlowResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private AppInstanceStartPaiFlowResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppInstanceStartPaiFlowResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppInstance.StartPaiFlowResponse mCallback;

        AppInstanceStartPaiFlowResponseParamsForwardToCallback(AppInstance.StartPaiFlowResponse startPaiFlowResponse) {
            this.mCallback = startPaiFlowResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(32, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(AppInstanceStartPaiFlowResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppInstanceStartPaiFlowResponseParamsProxyToResponder implements AppInstance.StartPaiFlowResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppInstanceStartPaiFlowResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            AppInstanceStartPaiFlowResponseParams appInstanceStartPaiFlowResponseParams = new AppInstanceStartPaiFlowResponseParams();
            appInstanceStartPaiFlowResponseParams.state = num.intValue();
            this.mMessageReceiver.accept(appInstanceStartPaiFlowResponseParams.serializeWithHeader(this.mCore, new MessageHeader(32, 2, this.mRequestId)));
        }
    }

    static final class AppInstanceUninstallPackageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceUninstallPackageParams() {
            super(16, 0);
        }

        public static AppInstanceUninstallPackageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceUninstallPackageParams appInstanceUninstallPackageParams = new AppInstanceUninstallPackageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceUninstallPackageParams.packageName = decoder.readString(8, false);
                return appInstanceUninstallPackageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private AppInstanceUninstallPackageParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceUpdateAppDetailsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceUpdateAppDetailsParams() {
            super(16, 0);
        }

        public static AppInstanceUpdateAppDetailsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceUpdateAppDetailsParams appInstanceUpdateAppDetailsParams = new AppInstanceUpdateAppDetailsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceUpdateAppDetailsParams.packageName = decoder.readString(8, false);
                return appInstanceUpdateAppDetailsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private AppInstanceUpdateAppDetailsParams(int i) {
            super(16, i);
        }
    }

    static final class AppInstanceUpdateWindowInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WindowInfo windowInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppInstanceUpdateWindowInfoParams() {
            super(16, 0);
        }

        public static AppInstanceUpdateWindowInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppInstanceUpdateWindowInfoParams appInstanceUpdateWindowInfoParams = new AppInstanceUpdateWindowInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appInstanceUpdateWindowInfoParams.windowInfo = WindowInfo.decode(decoder.readPointer(8, false));
                return appInstanceUpdateWindowInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.windowInfo, 8, false);
        }

        private AppInstanceUpdateWindowInfoParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AppInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void closeTask(int i) {
            AppInstanceCloseTaskParams appInstanceCloseTaskParams = new AppInstanceCloseTaskParams();
            appInstanceCloseTaskParams.taskId = i;
            getProxyHandler().getMessageReceiver().accept(appInstanceCloseTaskParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getAndroidId(AppInstance.GetAndroidIdResponse getAndroidIdResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AppInstanceGetAndroidIdParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(33, 1, 0L)), new AppInstanceGetAndroidIdResponseParamsForwardToCallback(getAndroidIdResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getAppCategory(String str, AppInstance.GetAppCategoryResponse getAppCategoryResponse) {
            AppInstanceGetAppCategoryParams appInstanceGetAppCategoryParams = new AppInstanceGetAppCategoryParams();
            appInstanceGetAppCategoryParams.packageName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceGetAppCategoryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(41, 1, 0L)), new AppInstanceGetAppCategoryResponseParamsForwardToCallback(getAppCategoryResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getAppIcon(String str, String str2, int i, AppInstance.GetAppIconResponse getAppIconResponse) {
            AppInstanceGetAppIconParams appInstanceGetAppIconParams = new AppInstanceGetAppIconParams();
            appInstanceGetAppIconParams.packageName = str;
            appInstanceGetAppIconParams.activity = str2;
            appInstanceGetAppIconParams.pixelSize = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceGetAppIconParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(35, 1, 0L)), new AppInstanceGetAppIconResponseParamsForwardToCallback(getAppIconResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getAppShortcutGlobalQueryItems(String str, int i, AppInstance.GetAppShortcutGlobalQueryItemsResponse getAppShortcutGlobalQueryItemsResponse) {
            AppInstanceGetAppShortcutGlobalQueryItemsParams appInstanceGetAppShortcutGlobalQueryItemsParams = new AppInstanceGetAppShortcutGlobalQueryItemsParams();
            appInstanceGetAppShortcutGlobalQueryItemsParams.query = str;
            appInstanceGetAppShortcutGlobalQueryItemsParams.maxResults = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceGetAppShortcutGlobalQueryItemsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(26, 1, 0L)), new AppInstanceGetAppShortcutGlobalQueryItemsResponseParamsForwardToCallback(getAppShortcutGlobalQueryItemsResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getAppShortcutIcon(String str, int i, AppInstance.GetAppShortcutIconResponse getAppShortcutIconResponse) {
            AppInstanceGetAppShortcutIconParams appInstanceGetAppShortcutIconParams = new AppInstanceGetAppShortcutIconParams();
            appInstanceGetAppShortcutIconParams.iconResourceId = str;
            appInstanceGetAppShortcutIconParams.pixelSize = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceGetAppShortcutIconParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(36, 1, 0L)), new AppInstanceGetAppShortcutIconResponseParamsForwardToCallback(getAppShortcutIconResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getAppShortcutItems(String str, AppInstance.GetAppShortcutItemsResponse getAppShortcutItemsResponse) {
            AppInstanceGetAppShortcutItemsParams appInstanceGetAppShortcutItemsParams = new AppInstanceGetAppShortcutItemsParams();
            appInstanceGetAppShortcutItemsParams.packageName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceGetAppShortcutItemsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23, 1, 0L)), new AppInstanceGetAppShortcutItemsResponseParamsForwardToCallback(getAppShortcutItemsResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getPackageIcon(String str, int i, boolean z, AppInstance.GetPackageIconResponse getPackageIconResponse) {
            AppInstanceGetPackageIconParams appInstanceGetPackageIconParams = new AppInstanceGetPackageIconParams();
            appInstanceGetPackageIconParams.packageName = str;
            appInstanceGetPackageIconParams.pixelSize = i;
            appInstanceGetPackageIconParams.normalize = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceGetPackageIconParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(37, 1, 0L)), new AppInstanceGetPackageIconResponseParamsForwardToCallback(getPackageIconResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void getRecentAndSuggestedAppsFromPlayStore(String str, int i, AppInstance.GetRecentAndSuggestedAppsFromPlayStoreResponse getRecentAndSuggestedAppsFromPlayStoreResponse) {
            AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams = new AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams();
            appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams.query = str;
            appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams.maxResults = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceGetRecentAndSuggestedAppsFromPlayStoreParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16, 1, 0L)), new AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsForwardToCallback(getRecentAndSuggestedAppsFromPlayStoreResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void init(AppHost appHost, AppInstance.InitResponse initResponse) {
            AppInstanceInitParams appInstanceInitParams = new AppInstanceInitParams();
            appInstanceInitParams.hostRemote = appHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21, 1, 0L)), new AppInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void installPackage(ArcPackageInfo arcPackageInfo) {
            AppInstanceInstallPackageParams appInstanceInstallPackageParams = new AppInstanceInstallPackageParams();
            appInstanceInstallPackageParams.arcPackageInfo = arcPackageInfo;
            getProxyHandler().getMessageReceiver().accept(appInstanceInstallPackageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void isInstallable(String str, AppInstance.IsInstallableResponse isInstallableResponse) {
            AppInstanceIsInstallableParams appInstanceIsInstallableParams = new AppInstanceIsInstallableParams();
            appInstanceIsInstallableParams.packageName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceIsInstallableParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(34, 1, 0L)), new AppInstanceIsInstallableResponseParamsForwardToCallback(isInstallableResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void launchAppShortcutItem(String str, String str2, long j) {
            AppInstanceLaunchAppShortcutItemParams appInstanceLaunchAppShortcutItemParams = new AppInstanceLaunchAppShortcutItemParams();
            appInstanceLaunchAppShortcutItemParams.packageName = str;
            appInstanceLaunchAppShortcutItemParams.shortcutId = str2;
            appInstanceLaunchAppShortcutItemParams.displayId = j;
            getProxyHandler().getMessageReceiver().accept(appInstanceLaunchAppShortcutItemParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(24)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void launchAppWithWindowInfo(String str, String str2, WindowInfo windowInfo) {
            AppInstanceLaunchAppWithWindowInfoParams appInstanceLaunchAppWithWindowInfoParams = new AppInstanceLaunchAppWithWindowInfoParams();
            appInstanceLaunchAppWithWindowInfoParams.packageName = str;
            appInstanceLaunchAppWithWindowInfoParams.activity = str2;
            appInstanceLaunchAppWithWindowInfoParams.windowInfo = windowInfo;
            getProxyHandler().getMessageReceiver().accept(appInstanceLaunchAppWithWindowInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(38)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void launchIntentWithWindowInfo(String str, WindowInfo windowInfo) {
            AppInstanceLaunchIntentWithWindowInfoParams appInstanceLaunchIntentWithWindowInfoParams = new AppInstanceLaunchIntentWithWindowInfoParams();
            appInstanceLaunchIntentWithWindowInfoParams.intentUri = str;
            appInstanceLaunchIntentWithWindowInfoParams.windowInfo = windowInfo;
            getProxyHandler().getMessageReceiver().accept(appInstanceLaunchIntentWithWindowInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(39)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void removeCachedIcon(String str) {
            AppInstanceRemoveCachedIconParams appInstanceRemoveCachedIconParams = new AppInstanceRemoveCachedIconParams();
            appInstanceRemoveCachedIconParams.iconResourceId = str;
            getProxyHandler().getMessageReceiver().accept(appInstanceRemoveCachedIconParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void requestAppIcon(String str, String str2, int i, AppInstance.RequestAppIconResponse requestAppIconResponse) {
            AppInstanceRequestAppIconParams appInstanceRequestAppIconParams = new AppInstanceRequestAppIconParams();
            appInstanceRequestAppIconParams.packageName = str;
            appInstanceRequestAppIconParams.activity = str2;
            appInstanceRequestAppIconParams.pixelSize = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceRequestAppIconParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(27, 1, 0L)), new AppInstanceRequestAppIconResponseParamsForwardToCallback(requestAppIconResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void requestPackageIcon(String str, int i, boolean z, AppInstance.RequestPackageIconResponse requestPackageIconResponse) {
            AppInstanceRequestPackageIconParams appInstanceRequestPackageIconParams = new AppInstanceRequestPackageIconParams();
            appInstanceRequestPackageIconParams.packageName = str;
            appInstanceRequestPackageIconParams.pixelSize = i;
            appInstanceRequestPackageIconParams.normalize = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceRequestPackageIconParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(30, 1, 0L)), new AppInstanceRequestPackageIconResponseParamsForwardToCallback(requestPackageIconResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void requestShortcutIcon(String str, int i, AppInstance.RequestShortcutIconResponse requestShortcutIconResponse) {
            AppInstanceRequestShortcutIconParams appInstanceRequestShortcutIconParams = new AppInstanceRequestShortcutIconParams();
            appInstanceRequestShortcutIconParams.iconResourceId = str;
            appInstanceRequestShortcutIconParams.pixelSize = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appInstanceRequestShortcutIconParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(28, 1, 0L)), new AppInstanceRequestShortcutIconResponseParamsForwardToCallback(requestShortcutIconResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void setAppLocale(String str, String str2) {
            AppInstanceSetAppLocaleParams appInstanceSetAppLocaleParams = new AppInstanceSetAppLocaleParams();
            appInstanceSetAppLocaleParams.packageName = str;
            appInstanceSetAppLocaleParams.localeTag = str2;
            getProxyHandler().getMessageReceiver().accept(appInstanceSetAppLocaleParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(43)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void setNotificationsEnabled(String str, boolean z) {
            AppInstanceSetNotificationsEnabledParams appInstanceSetNotificationsEnabledParams = new AppInstanceSetNotificationsEnabledParams();
            appInstanceSetNotificationsEnabledParams.packageName = str;
            appInstanceSetNotificationsEnabledParams.enabled = z;
            getProxyHandler().getMessageReceiver().accept(appInstanceSetNotificationsEnabledParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void setTaskActive(int i) {
            AppInstanceSetTaskActiveParams appInstanceSetTaskActiveParams = new AppInstanceSetTaskActiveParams();
            appInstanceSetTaskActiveParams.taskId = i;
            getProxyHandler().getMessageReceiver().accept(appInstanceSetTaskActiveParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void showPackageInfoDeprecated(String str, Rect rect) {
            AppInstanceShowPackageInfoDeprecatedParams appInstanceShowPackageInfoDeprecatedParams = new AppInstanceShowPackageInfoDeprecatedParams();
            appInstanceShowPackageInfoDeprecatedParams.packageName = str;
            appInstanceShowPackageInfoDeprecatedParams.dimensionOnScreen = rect;
            getProxyHandler().getMessageReceiver().accept(appInstanceShowPackageInfoDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void showPackageInfoOnPage(String str, int i, long j) {
            AppInstanceShowPackageInfoOnPageParams appInstanceShowPackageInfoOnPageParams = new AppInstanceShowPackageInfoOnPageParams();
            appInstanceShowPackageInfoOnPageParams.packageName = str;
            appInstanceShowPackageInfoOnPageParams.page = i;
            appInstanceShowPackageInfoOnPageParams.displayId = j;
            getProxyHandler().getMessageReceiver().accept(appInstanceShowPackageInfoOnPageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void showPackageInfoOnPageDeprecated(String str, int i, Rect rect) {
            AppInstanceShowPackageInfoOnPageDeprecatedParams appInstanceShowPackageInfoOnPageDeprecatedParams = new AppInstanceShowPackageInfoOnPageDeprecatedParams();
            appInstanceShowPackageInfoOnPageDeprecatedParams.packageName = str;
            appInstanceShowPackageInfoOnPageDeprecatedParams.page = i;
            appInstanceShowPackageInfoOnPageDeprecatedParams.dimensionOnScreen = rect;
            getProxyHandler().getMessageReceiver().accept(appInstanceShowPackageInfoOnPageDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void startFastAppReinstallFlow(String[] strArr) {
            AppInstanceStartFastAppReinstallFlowParams appInstanceStartFastAppReinstallFlowParams = new AppInstanceStartFastAppReinstallFlowParams();
            appInstanceStartFastAppReinstallFlowParams.arcPackageNames = strArr;
            getProxyHandler().getMessageReceiver().accept(appInstanceStartFastAppReinstallFlowParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(25)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void startPaiFlow(AppInstance.StartPaiFlowResponse startPaiFlowResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AppInstanceStartPaiFlowParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(32, 1, 0L)), new AppInstanceStartPaiFlowResponseParamsForwardToCallback(startPaiFlowResponse));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void uninstallPackage(String str) {
            AppInstanceUninstallPackageParams appInstanceUninstallPackageParams = new AppInstanceUninstallPackageParams();
            appInstanceUninstallPackageParams.packageName = str;
            getProxyHandler().getMessageReceiver().accept(appInstanceUninstallPackageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void updateAppDetails(String str) {
            AppInstanceUpdateAppDetailsParams appInstanceUpdateAppDetailsParams = new AppInstanceUpdateAppDetailsParams();
            appInstanceUpdateAppDetailsParams.packageName = str;
            getProxyHandler().getMessageReceiver().accept(appInstanceUpdateAppDetailsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(42)));
        }

        @Override // org.chromium.arc.mojom.AppInstance
        public void updateWindowInfo(WindowInfo windowInfo) {
            AppInstanceUpdateWindowInfoParams appInstanceUpdateWindowInfoParams = new AppInstanceUpdateWindowInfoParams();
            appInstanceUpdateWindowInfoParams.windowInfo = windowInfo;
            getProxyHandler().getMessageReceiver().accept(appInstanceUpdateWindowInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(40)));
        }
    }

    static final class Stub extends Interface.Stub<AppInstance> {
        Stub(Core core, AppInstance appInstance) {
            super(core, appInstance);
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
                if (type == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AppInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 5) {
                    getImpl().uninstallPackage(AppInstanceUninstallPackageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName);
                    return true;
                }
                if (type == 20) {
                    AppInstanceShowPackageInfoOnPageParams appInstanceShowPackageInfoOnPageParamsDeserialize = AppInstanceShowPackageInfoOnPageParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().showPackageInfoOnPage(appInstanceShowPackageInfoOnPageParamsDeserialize.packageName, appInstanceShowPackageInfoOnPageParamsDeserialize.page, appInstanceShowPackageInfoOnPageParamsDeserialize.displayId);
                    return true;
                }
                if (type == 14) {
                    getImpl().removeCachedIcon(AppInstanceRemoveCachedIconParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iconResourceId);
                    return true;
                }
                if (type == 15) {
                    AppInstanceShowPackageInfoOnPageDeprecatedParams appInstanceShowPackageInfoOnPageDeprecatedParamsDeserialize = AppInstanceShowPackageInfoOnPageDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().showPackageInfoOnPageDeprecated(appInstanceShowPackageInfoOnPageDeprecatedParamsDeserialize.packageName, appInstanceShowPackageInfoOnPageDeprecatedParamsDeserialize.page, appInstanceShowPackageInfoOnPageDeprecatedParamsDeserialize.dimensionOnScreen);
                    return true;
                }
                if (type == 24) {
                    AppInstanceLaunchAppShortcutItemParams appInstanceLaunchAppShortcutItemParamsDeserialize = AppInstanceLaunchAppShortcutItemParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().launchAppShortcutItem(appInstanceLaunchAppShortcutItemParamsDeserialize.packageName, appInstanceLaunchAppShortcutItemParamsDeserialize.shortcutId, appInstanceLaunchAppShortcutItemParamsDeserialize.displayId);
                    return true;
                }
                if (type == 25) {
                    getImpl().startFastAppReinstallFlow(AppInstanceStartFastAppReinstallFlowParams.deserialize(serviceMessageAsServiceMessage.getPayload()).arcPackageNames);
                    return true;
                }
                if (type == 42) {
                    getImpl().updateAppDetails(AppInstanceUpdateAppDetailsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName);
                    return true;
                }
                if (type == 43) {
                    AppInstanceSetAppLocaleParams appInstanceSetAppLocaleParamsDeserialize = AppInstanceSetAppLocaleParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setAppLocale(appInstanceSetAppLocaleParamsDeserialize.packageName, appInstanceSetAppLocaleParamsDeserialize.localeTag);
                    return true;
                }
                switch (type) {
                    case 7:
                        getImpl().setTaskActive(AppInstanceSetTaskActiveParams.deserialize(serviceMessageAsServiceMessage.getPayload()).taskId);
                        return true;
                    case 8:
                        getImpl().closeTask(AppInstanceCloseTaskParams.deserialize(serviceMessageAsServiceMessage.getPayload()).taskId);
                        return true;
                    case 9:
                        AppInstanceShowPackageInfoDeprecatedParams appInstanceShowPackageInfoDeprecatedParamsDeserialize = AppInstanceShowPackageInfoDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().showPackageInfoDeprecated(appInstanceShowPackageInfoDeprecatedParamsDeserialize.packageName, appInstanceShowPackageInfoDeprecatedParamsDeserialize.dimensionOnScreen);
                        return true;
                    case 10:
                        AppInstanceSetNotificationsEnabledParams appInstanceSetNotificationsEnabledParamsDeserialize = AppInstanceSetNotificationsEnabledParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().setNotificationsEnabled(appInstanceSetNotificationsEnabledParamsDeserialize.packageName, appInstanceSetNotificationsEnabledParamsDeserialize.enabled);
                        return true;
                    case 11:
                        getImpl().installPackage(AppInstanceInstallPackageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).arcPackageInfo);
                        return true;
                    default:
                        switch (type) {
                            case 38:
                                AppInstanceLaunchAppWithWindowInfoParams appInstanceLaunchAppWithWindowInfoParamsDeserialize = AppInstanceLaunchAppWithWindowInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().launchAppWithWindowInfo(appInstanceLaunchAppWithWindowInfoParamsDeserialize.packageName, appInstanceLaunchAppWithWindowInfoParamsDeserialize.activity, appInstanceLaunchAppWithWindowInfoParamsDeserialize.windowInfo);
                                break;
                            case 39:
                                AppInstanceLaunchIntentWithWindowInfoParams appInstanceLaunchIntentWithWindowInfoParamsDeserialize = AppInstanceLaunchIntentWithWindowInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().launchIntentWithWindowInfo(appInstanceLaunchIntentWithWindowInfoParamsDeserialize.intentUri, appInstanceLaunchIntentWithWindowInfoParamsDeserialize.windowInfo);
                                break;
                            case 40:
                                getImpl().updateWindowInfo(AppInstanceUpdateWindowInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload()).windowInfo);
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
                if (!header.validateHeader(1)) {
                    return false;
                }
                int type = header.getType();
                if (type == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AppInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 16) {
                    AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams appInstanceGetRecentAndSuggestedAppsFromPlayStoreParamsDeserialize = AppInstanceGetRecentAndSuggestedAppsFromPlayStoreParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getRecentAndSuggestedAppsFromPlayStore(appInstanceGetRecentAndSuggestedAppsFromPlayStoreParamsDeserialize.query, appInstanceGetRecentAndSuggestedAppsFromPlayStoreParamsDeserialize.maxResults, new AppInstanceGetRecentAndSuggestedAppsFromPlayStoreResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 21) {
                    getImpl().init(AppInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new AppInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 23) {
                    getImpl().getAppShortcutItems(AppInstanceGetAppShortcutItemsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName, new AppInstanceGetAppShortcutItemsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 30) {
                    AppInstanceRequestPackageIconParams appInstanceRequestPackageIconParamsDeserialize = AppInstanceRequestPackageIconParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestPackageIcon(appInstanceRequestPackageIconParamsDeserialize.packageName, appInstanceRequestPackageIconParamsDeserialize.pixelSize, appInstanceRequestPackageIconParamsDeserialize.normalize, new AppInstanceRequestPackageIconResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 41) {
                    getImpl().getAppCategory(AppInstanceGetAppCategoryParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName, new AppInstanceGetAppCategoryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                switch (type) {
                    case 26:
                        AppInstanceGetAppShortcutGlobalQueryItemsParams appInstanceGetAppShortcutGlobalQueryItemsParamsDeserialize = AppInstanceGetAppShortcutGlobalQueryItemsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getAppShortcutGlobalQueryItems(appInstanceGetAppShortcutGlobalQueryItemsParamsDeserialize.query, appInstanceGetAppShortcutGlobalQueryItemsParamsDeserialize.maxResults, new AppInstanceGetAppShortcutGlobalQueryItemsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 27:
                        AppInstanceRequestAppIconParams appInstanceRequestAppIconParamsDeserialize = AppInstanceRequestAppIconParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().requestAppIcon(appInstanceRequestAppIconParamsDeserialize.packageName, appInstanceRequestAppIconParamsDeserialize.activity, appInstanceRequestAppIconParamsDeserialize.pixelSize, new AppInstanceRequestAppIconResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 28:
                        AppInstanceRequestShortcutIconParams appInstanceRequestShortcutIconParamsDeserialize = AppInstanceRequestShortcutIconParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().requestShortcutIcon(appInstanceRequestShortcutIconParamsDeserialize.iconResourceId, appInstanceRequestShortcutIconParamsDeserialize.pixelSize, new AppInstanceRequestShortcutIconResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    default:
                        switch (type) {
                            case 32:
                                AppInstanceStartPaiFlowParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().startPaiFlow(new AppInstanceStartPaiFlowResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 33:
                                AppInstanceGetAndroidIdParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().getAndroidId(new AppInstanceGetAndroidIdResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 34:
                                getImpl().isInstallable(AppInstanceIsInstallableParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName, new AppInstanceIsInstallableResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 35:
                                AppInstanceGetAppIconParams appInstanceGetAppIconParamsDeserialize = AppInstanceGetAppIconParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().getAppIcon(appInstanceGetAppIconParamsDeserialize.packageName, appInstanceGetAppIconParamsDeserialize.activity, appInstanceGetAppIconParamsDeserialize.pixelSize, new AppInstanceGetAppIconResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 36:
                                AppInstanceGetAppShortcutIconParams appInstanceGetAppShortcutIconParamsDeserialize = AppInstanceGetAppShortcutIconParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().getAppShortcutIcon(appInstanceGetAppShortcutIconParamsDeserialize.iconResourceId, appInstanceGetAppShortcutIconParamsDeserialize.pixelSize, new AppInstanceGetAppShortcutIconResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 37:
                                AppInstanceGetPackageIconParams appInstanceGetPackageIconParamsDeserialize = AppInstanceGetPackageIconParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().getPackageIcon(appInstanceGetPackageIconParamsDeserialize.packageName, appInstanceGetPackageIconParamsDeserialize.pixelSize, appInstanceGetPackageIconParamsDeserialize.normalize, new AppInstanceGetPackageIconResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                        }
                        return true;
                }
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

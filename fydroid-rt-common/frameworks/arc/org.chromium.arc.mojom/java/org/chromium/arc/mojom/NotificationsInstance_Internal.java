// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.NotificationsInstance;
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

class NotificationsInstance_Internal {
    public static final Interface.Manager<NotificationsInstance, NotificationsInstance.Proxy> MANAGER = new Interface.Manager<NotificationsInstance, NotificationsInstance.Proxy>() { // from class: org.chromium.arc.mojom.NotificationsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NotificationsInstance[] buildArray(int i) {
            return new NotificationsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NotificationsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<NotificationsInstance> buildStub(Core core, NotificationsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.NotificationsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 36;
        }
    };

    static final class NotificationsInstanceCancelDeferredUserActionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int actionId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceCancelDeferredUserActionParams() {
            super(16, 0);
        }

        public static NotificationsInstanceCancelDeferredUserActionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceCancelDeferredUserActionParams notificationsInstanceCancelDeferredUserActionParams = new NotificationsInstanceCancelDeferredUserActionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceCancelDeferredUserActionParams.actionId = decoder.readInt(8);
                return notificationsInstanceCancelDeferredUserActionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.actionId, 8);
        }

        private NotificationsInstanceCancelDeferredUserActionParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceCancelPressParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceCancelPressParams() {
            super(16, 0);
        }

        public static NotificationsInstanceCancelPressParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceCancelPressParams notificationsInstanceCancelPressParams = new NotificationsInstanceCancelPressParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceCancelPressParams.key = decoder.readString(8, false);
                return notificationsInstanceCancelPressParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsInstanceCancelPressParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceCloseNotificationWindowParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceCloseNotificationWindowParams() {
            super(16, 0);
        }

        public static NotificationsInstanceCloseNotificationWindowParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceCloseNotificationWindowParams notificationsInstanceCloseNotificationWindowParams = new NotificationsInstanceCloseNotificationWindowParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceCloseNotificationWindowParams.key = decoder.readString(8, false);
                return notificationsInstanceCloseNotificationWindowParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsInstanceCloseNotificationWindowParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceCreateNotificationWindowParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceCreateNotificationWindowParams() {
            super(16, 0);
        }

        public static NotificationsInstanceCreateNotificationWindowParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceCreateNotificationWindowParams notificationsInstanceCreateNotificationWindowParams = new NotificationsInstanceCreateNotificationWindowParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceCreateNotificationWindowParams.key = decoder.readString(8, false);
                return notificationsInstanceCreateNotificationWindowParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsInstanceCreateNotificationWindowParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NotificationsHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceInitParams() {
            super(16, 0);
        }

        public static NotificationsInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceInitParams notificationsInstanceInitParams = new NotificationsInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceInitParams.hostRemote = (NotificationsHost) decoder.readServiceInterface(8, false, NotificationsHost.MANAGER);
                return notificationsInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<NotificationsHost, ?>) NotificationsHost.MANAGER);
        }

        private NotificationsInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class NotificationsInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NotificationsInstance.InitResponse mCallback;

        NotificationsInstanceInitResponseParamsForwardToCallback(NotificationsInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NotificationsInstanceInitResponseParamsProxyToResponder implements NotificationsInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NotificationsInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new NotificationsInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class NotificationsInstanceOnMessageCenterVisibilityChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int visibility;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceOnMessageCenterVisibilityChangedParams() {
            super(16, 0);
        }

        public static NotificationsInstanceOnMessageCenterVisibilityChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceOnMessageCenterVisibilityChangedParams notificationsInstanceOnMessageCenterVisibilityChangedParams = new NotificationsInstanceOnMessageCenterVisibilityChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                notificationsInstanceOnMessageCenterVisibilityChangedParams.visibility = i;
                MessageCenterVisibility.validate(i);
                return notificationsInstanceOnMessageCenterVisibilityChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.visibility, 8);
        }

        private NotificationsInstanceOnMessageCenterVisibilityChangedParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceOpenNotificationSettingsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceOpenNotificationSettingsParams() {
            super(16, 0);
        }

        public static NotificationsInstanceOpenNotificationSettingsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceOpenNotificationSettingsParams notificationsInstanceOpenNotificationSettingsParams = new NotificationsInstanceOpenNotificationSettingsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceOpenNotificationSettingsParams.key = decoder.readString(8, false);
                return notificationsInstanceOpenNotificationSettingsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsInstanceOpenNotificationSettingsParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceOpenNotificationSnoozeSettingsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceOpenNotificationSnoozeSettingsParams() {
            super(16, 0);
        }

        public static NotificationsInstanceOpenNotificationSnoozeSettingsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceOpenNotificationSnoozeSettingsParams notificationsInstanceOpenNotificationSnoozeSettingsParams = new NotificationsInstanceOpenNotificationSnoozeSettingsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceOpenNotificationSnoozeSettingsParams.key = decoder.readString(8, false);
                return notificationsInstanceOpenNotificationSnoozeSettingsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsInstanceOpenNotificationSnoozeSettingsParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstancePerformDeferredUserActionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int actionId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstancePerformDeferredUserActionParams() {
            super(16, 0);
        }

        public static NotificationsInstancePerformDeferredUserActionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstancePerformDeferredUserActionParams notificationsInstancePerformDeferredUserActionParams = new NotificationsInstancePerformDeferredUserActionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstancePerformDeferredUserActionParams.actionId = decoder.readInt(8);
                return notificationsInstancePerformDeferredUserActionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.actionId, 8);
        }

        private NotificationsInstancePerformDeferredUserActionParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstancePopUpAppNotificationSettingsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstancePopUpAppNotificationSettingsParams() {
            super(16, 0);
        }

        public static NotificationsInstancePopUpAppNotificationSettingsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstancePopUpAppNotificationSettingsParams notificationsInstancePopUpAppNotificationSettingsParams = new NotificationsInstancePopUpAppNotificationSettingsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstancePopUpAppNotificationSettingsParams.key = decoder.readString(8, false);
                return notificationsInstancePopUpAppNotificationSettingsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsInstancePopUpAppNotificationSettingsParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceSendNotificationButtonClickToAndroidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int actionButtonIndex;
        public String input;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceSendNotificationButtonClickToAndroidParams() {
            super(32, 0);
        }

        public static NotificationsInstanceSendNotificationButtonClickToAndroidParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceSendNotificationButtonClickToAndroidParams notificationsInstanceSendNotificationButtonClickToAndroidParams = new NotificationsInstanceSendNotificationButtonClickToAndroidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceSendNotificationButtonClickToAndroidParams.key = decoder.readString(8, false);
                notificationsInstanceSendNotificationButtonClickToAndroidParams.actionButtonIndex = decoder.readInt(16);
                notificationsInstanceSendNotificationButtonClickToAndroidParams.input = decoder.readString(24, false);
                return notificationsInstanceSendNotificationButtonClickToAndroidParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.key, 8, false);
            encoderAtDataOffset.encode(this.actionButtonIndex, 16);
            encoderAtDataOffset.encode(this.input, 24, false);
        }

        private NotificationsInstanceSendNotificationButtonClickToAndroidParams(int i) {
            super(32, i);
        }
    }

    static final class NotificationsInstanceSendNotificationEventToAndroidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int event;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceSendNotificationEventToAndroidParams() {
            super(24, 0);
        }

        public static NotificationsInstanceSendNotificationEventToAndroidParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceSendNotificationEventToAndroidParams notificationsInstanceSendNotificationEventToAndroidParams = new NotificationsInstanceSendNotificationEventToAndroidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceSendNotificationEventToAndroidParams.key = decoder.readString(8, false);
                int i = decoder.readInt(16);
                notificationsInstanceSendNotificationEventToAndroidParams.event = i;
                ArcNotificationEvent.validate(i);
                return notificationsInstanceSendNotificationEventToAndroidParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.key, 8, false);
            encoderAtDataOffset.encode(this.event, 16);
        }

        private NotificationsInstanceSendNotificationEventToAndroidParams(int i) {
            super(24, i);
        }
    }

    static final class NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcDoNotDisturbStatus status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams() {
            super(16, 0);
        }

        public static NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams notificationsInstanceSetDoNotDisturbStatusOnAndroidParams = new NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceSetDoNotDisturbStatusOnAndroidParams.status = ArcDoNotDisturbStatus.decode(decoder.readPointer(8, false));
                return notificationsInstanceSetDoNotDisturbStatusOnAndroidParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.status, 8, false);
        }

        private NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceSetLockScreenSettingOnAndroidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcLockScreenNotificationSetting setting;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceSetLockScreenSettingOnAndroidParams() {
            super(16, 0);
        }

        public static NotificationsInstanceSetLockScreenSettingOnAndroidParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceSetLockScreenSettingOnAndroidParams notificationsInstanceSetLockScreenSettingOnAndroidParams = new NotificationsInstanceSetLockScreenSettingOnAndroidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceSetLockScreenSettingOnAndroidParams.setting = ArcLockScreenNotificationSetting.decode(decoder.readPointer(8, false));
                return notificationsInstanceSetLockScreenSettingOnAndroidParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.setting, 8, false);
        }

        private NotificationsInstanceSetLockScreenSettingOnAndroidParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsInstanceSetNotificationConfigurationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NotificationConfiguration configuration;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsInstanceSetNotificationConfigurationParams() {
            super(16, 0);
        }

        public static NotificationsInstanceSetNotificationConfigurationParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsInstanceSetNotificationConfigurationParams notificationsInstanceSetNotificationConfigurationParams = new NotificationsInstanceSetNotificationConfigurationParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsInstanceSetNotificationConfigurationParams.configuration = NotificationConfiguration.decode(decoder.readPointer(8, false));
                return notificationsInstanceSetNotificationConfigurationParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.configuration, 8, false);
        }

        private NotificationsInstanceSetNotificationConfigurationParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements NotificationsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void cancelDeferredUserAction(int i) {
            NotificationsInstanceCancelDeferredUserActionParams notificationsInstanceCancelDeferredUserActionParams = new NotificationsInstanceCancelDeferredUserActionParams();
            notificationsInstanceCancelDeferredUserActionParams.actionId = i;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceCancelDeferredUserActionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void cancelPress(String str) {
            NotificationsInstanceCancelPressParams notificationsInstanceCancelPressParams = new NotificationsInstanceCancelPressParams();
            notificationsInstanceCancelPressParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceCancelPressParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void closeNotificationWindow(String str) {
            NotificationsInstanceCloseNotificationWindowParams notificationsInstanceCloseNotificationWindowParams = new NotificationsInstanceCloseNotificationWindowParams();
            notificationsInstanceCloseNotificationWindowParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceCloseNotificationWindowParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void createNotificationWindow(String str) {
            NotificationsInstanceCreateNotificationWindowParams notificationsInstanceCreateNotificationWindowParams = new NotificationsInstanceCreateNotificationWindowParams();
            notificationsInstanceCreateNotificationWindowParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceCreateNotificationWindowParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void init(NotificationsHost notificationsHost, NotificationsInstance.InitResponse initResponse) {
            NotificationsInstanceInitParams notificationsInstanceInitParams = new NotificationsInstanceInitParams();
            notificationsInstanceInitParams.hostRemote = notificationsHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(notificationsInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new NotificationsInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void onMessageCenterVisibilityChanged(int i) {
            NotificationsInstanceOnMessageCenterVisibilityChangedParams notificationsInstanceOnMessageCenterVisibilityChangedParams = new NotificationsInstanceOnMessageCenterVisibilityChangedParams();
            notificationsInstanceOnMessageCenterVisibilityChangedParams.visibility = i;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceOnMessageCenterVisibilityChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void openNotificationSettings(String str) {
            NotificationsInstanceOpenNotificationSettingsParams notificationsInstanceOpenNotificationSettingsParams = new NotificationsInstanceOpenNotificationSettingsParams();
            notificationsInstanceOpenNotificationSettingsParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceOpenNotificationSettingsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void openNotificationSnoozeSettings(String str) {
            NotificationsInstanceOpenNotificationSnoozeSettingsParams notificationsInstanceOpenNotificationSnoozeSettingsParams = new NotificationsInstanceOpenNotificationSnoozeSettingsParams();
            notificationsInstanceOpenNotificationSnoozeSettingsParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceOpenNotificationSnoozeSettingsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void performDeferredUserAction(int i) {
            NotificationsInstancePerformDeferredUserActionParams notificationsInstancePerformDeferredUserActionParams = new NotificationsInstancePerformDeferredUserActionParams();
            notificationsInstancePerformDeferredUserActionParams.actionId = i;
            getProxyHandler().getMessageReceiver().accept(notificationsInstancePerformDeferredUserActionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void popUpAppNotificationSettings(String str) {
            NotificationsInstancePopUpAppNotificationSettingsParams notificationsInstancePopUpAppNotificationSettingsParams = new NotificationsInstancePopUpAppNotificationSettingsParams();
            notificationsInstancePopUpAppNotificationSettingsParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsInstancePopUpAppNotificationSettingsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void sendNotificationButtonClickToAndroid(String str, int i, String str2) {
            NotificationsInstanceSendNotificationButtonClickToAndroidParams notificationsInstanceSendNotificationButtonClickToAndroidParams = new NotificationsInstanceSendNotificationButtonClickToAndroidParams();
            notificationsInstanceSendNotificationButtonClickToAndroidParams.key = str;
            notificationsInstanceSendNotificationButtonClickToAndroidParams.actionButtonIndex = i;
            notificationsInstanceSendNotificationButtonClickToAndroidParams.input = str2;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceSendNotificationButtonClickToAndroidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void sendNotificationEventToAndroid(String str, int i) {
            NotificationsInstanceSendNotificationEventToAndroidParams notificationsInstanceSendNotificationEventToAndroidParams = new NotificationsInstanceSendNotificationEventToAndroidParams();
            notificationsInstanceSendNotificationEventToAndroidParams.key = str;
            notificationsInstanceSendNotificationEventToAndroidParams.event = i;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceSendNotificationEventToAndroidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void setDoNotDisturbStatusOnAndroid(ArcDoNotDisturbStatus arcDoNotDisturbStatus) {
            NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams notificationsInstanceSetDoNotDisturbStatusOnAndroidParams = new NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams();
            notificationsInstanceSetDoNotDisturbStatusOnAndroidParams.status = arcDoNotDisturbStatus;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceSetDoNotDisturbStatusOnAndroidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void setLockScreenSettingOnAndroid(ArcLockScreenNotificationSetting arcLockScreenNotificationSetting) {
            NotificationsInstanceSetLockScreenSettingOnAndroidParams notificationsInstanceSetLockScreenSettingOnAndroidParams = new NotificationsInstanceSetLockScreenSettingOnAndroidParams();
            notificationsInstanceSetLockScreenSettingOnAndroidParams.setting = arcLockScreenNotificationSetting;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceSetLockScreenSettingOnAndroidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.NotificationsInstance
        public void setNotificationConfiguration(NotificationConfiguration notificationConfiguration) {
            NotificationsInstanceSetNotificationConfigurationParams notificationsInstanceSetNotificationConfigurationParams = new NotificationsInstanceSetNotificationConfigurationParams();
            notificationsInstanceSetNotificationConfigurationParams.configuration = notificationConfiguration;
            getProxyHandler().getMessageReceiver().accept(notificationsInstanceSetNotificationConfigurationParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12)));
        }
    }

    static final class Stub extends Interface.Stub<NotificationsInstance> {
        Stub(Core core, NotificationsInstance notificationsInstance) {
            super(core, notificationsInstance);
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
                    case 1:
                        NotificationsInstanceSendNotificationEventToAndroidParams notificationsInstanceSendNotificationEventToAndroidParamsDeserialize = NotificationsInstanceSendNotificationEventToAndroidParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().sendNotificationEventToAndroid(notificationsInstanceSendNotificationEventToAndroidParamsDeserialize.key, notificationsInstanceSendNotificationEventToAndroidParamsDeserialize.event);
                        return true;
                    case 2:
                        getImpl().createNotificationWindow(NotificationsInstanceCreateNotificationWindowParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
                        return true;
                    case 3:
                        getImpl().closeNotificationWindow(NotificationsInstanceCloseNotificationWindowParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
                        return true;
                    case 4:
                        getImpl().openNotificationSettings(NotificationsInstanceOpenNotificationSettingsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
                        return true;
                    case 6:
                        getImpl().openNotificationSnoozeSettings(NotificationsInstanceOpenNotificationSnoozeSettingsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
                        return true;
                    case 7:
                        getImpl().setDoNotDisturbStatusOnAndroid(NotificationsInstanceSetDoNotDisturbStatusOnAndroidParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status);
                        return true;
                    case 8:
                        getImpl().cancelPress(NotificationsInstanceCancelPressParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
                        return true;
                    case 9:
                        getImpl().performDeferredUserAction(NotificationsInstancePerformDeferredUserActionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).actionId);
                        return true;
                    case 10:
                        getImpl().cancelDeferredUserAction(NotificationsInstanceCancelDeferredUserActionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).actionId);
                        return true;
                    case 11:
                        getImpl().setLockScreenSettingOnAndroid(NotificationsInstanceSetLockScreenSettingOnAndroidParams.deserialize(serviceMessageAsServiceMessage.getPayload()).setting);
                        return true;
                    case 12:
                        getImpl().setNotificationConfiguration(NotificationsInstanceSetNotificationConfigurationParams.deserialize(serviceMessageAsServiceMessage.getPayload()).configuration);
                        return true;
                    case 13:
                        getImpl().onMessageCenterVisibilityChanged(NotificationsInstanceOnMessageCenterVisibilityChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).visibility);
                        return true;
                    case 14:
                        NotificationsInstanceSendNotificationButtonClickToAndroidParams notificationsInstanceSendNotificationButtonClickToAndroidParamsDeserialize = NotificationsInstanceSendNotificationButtonClickToAndroidParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().sendNotificationButtonClickToAndroid(notificationsInstanceSendNotificationButtonClickToAndroidParamsDeserialize.key, notificationsInstanceSendNotificationButtonClickToAndroidParamsDeserialize.actionButtonIndex, notificationsInstanceSendNotificationButtonClickToAndroidParamsDeserialize.input);
                        return true;
                    case 15:
                        getImpl().popUpAppNotificationSettings(NotificationsInstancePopUpAppNotificationSettingsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), NotificationsInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 5) {
                    return false;
                }
                getImpl().init(NotificationsInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new NotificationsInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

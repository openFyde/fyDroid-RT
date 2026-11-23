// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.NotificationsHost;
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

class NotificationsHost_Internal {
    public static final Interface.Manager<NotificationsHost, NotificationsHost.Proxy> MANAGER = new Interface.Manager<NotificationsHost, NotificationsHost.Proxy>() { // from class: org.chromium.arc.mojom.NotificationsHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NotificationsHost[] buildArray(int i) {
            return new NotificationsHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NotificationsHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<NotificationsHost> buildStub(Core core, NotificationsHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.NotificationsHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 27;
        }
    };

    static final class NotificationsHostCloseMessageCenterParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostCloseMessageCenterParams() {
            super(8, 0);
        }

        public static NotificationsHostCloseMessageCenterParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NotificationsHostCloseMessageCenterParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NotificationsHostCloseMessageCenterParams(int i) {
            super(8, i);
        }
    }

    static final class NotificationsHostLogInlineReplySentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostLogInlineReplySentParams() {
            super(16, 0);
        }

        public static NotificationsHostLogInlineReplySentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsHostLogInlineReplySentParams notificationsHostLogInlineReplySentParams = new NotificationsHostLogInlineReplySentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsHostLogInlineReplySentParams.key = decoder.readString(8, false);
                return notificationsHostLogInlineReplySentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsHostLogInlineReplySentParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsHostOnDoNotDisturbStatusUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcDoNotDisturbStatus status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostOnDoNotDisturbStatusUpdatedParams() {
            super(16, 0);
        }

        public static NotificationsHostOnDoNotDisturbStatusUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsHostOnDoNotDisturbStatusUpdatedParams notificationsHostOnDoNotDisturbStatusUpdatedParams = new NotificationsHostOnDoNotDisturbStatusUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsHostOnDoNotDisturbStatusUpdatedParams.status = ArcDoNotDisturbStatus.decode(decoder.readPointer(8, false));
                return notificationsHostOnDoNotDisturbStatusUpdatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.status, 8, false);
        }

        private NotificationsHostOnDoNotDisturbStatusUpdatedParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsHostOnLockScreenSettingUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcLockScreenNotificationSetting setting;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostOnLockScreenSettingUpdatedParams() {
            super(16, 0);
        }

        public static NotificationsHostOnLockScreenSettingUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsHostOnLockScreenSettingUpdatedParams notificationsHostOnLockScreenSettingUpdatedParams = new NotificationsHostOnLockScreenSettingUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsHostOnLockScreenSettingUpdatedParams.setting = ArcLockScreenNotificationSetting.decode(decoder.readPointer(8, false));
                return notificationsHostOnLockScreenSettingUpdatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.setting, 8, false);
        }

        private NotificationsHostOnLockScreenSettingUpdatedParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsHostOnNotificationPostedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcNotificationData notificationData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostOnNotificationPostedParams() {
            super(16, 0);
        }

        public static NotificationsHostOnNotificationPostedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsHostOnNotificationPostedParams notificationsHostOnNotificationPostedParams = new NotificationsHostOnNotificationPostedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsHostOnNotificationPostedParams.notificationData = ArcNotificationData.decode(decoder.readPointer(8, false));
                return notificationsHostOnNotificationPostedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.notificationData, 8, false);
        }

        private NotificationsHostOnNotificationPostedParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsHostOnNotificationRemovedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String key;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostOnNotificationRemovedParams() {
            super(16, 0);
        }

        public static NotificationsHostOnNotificationRemovedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsHostOnNotificationRemovedParams notificationsHostOnNotificationRemovedParams = new NotificationsHostOnNotificationRemovedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsHostOnNotificationRemovedParams.key = decoder.readString(8, false);
                return notificationsHostOnNotificationRemovedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.key, 8, false);
        }

        private NotificationsHostOnNotificationRemovedParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsHostOnNotificationUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcNotificationData notificationData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostOnNotificationUpdatedParams() {
            super(16, 0);
        }

        public static NotificationsHostOnNotificationUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsHostOnNotificationUpdatedParams notificationsHostOnNotificationUpdatedParams = new NotificationsHostOnNotificationUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsHostOnNotificationUpdatedParams.notificationData = ArcNotificationData.decode(decoder.readPointer(8, false));
                return notificationsHostOnNotificationUpdatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.notificationData, 8, false);
        }

        private NotificationsHostOnNotificationUpdatedParams(int i) {
            super(16, i);
        }
    }

    static final class NotificationsHostOpenMessageCenterParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostOpenMessageCenterParams() {
            super(8, 0);
        }

        public static NotificationsHostOpenMessageCenterParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NotificationsHostOpenMessageCenterParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NotificationsHostOpenMessageCenterParams(int i) {
            super(8, i);
        }
    }

    static final class NotificationsHostProcessUserActionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcNotificationUserActionData data;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NotificationsHostProcessUserActionParams() {
            super(16, 0);
        }

        public static NotificationsHostProcessUserActionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NotificationsHostProcessUserActionParams notificationsHostProcessUserActionParams = new NotificationsHostProcessUserActionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                notificationsHostProcessUserActionParams.data = ArcNotificationUserActionData.decode(decoder.readPointer(8, false));
                return notificationsHostProcessUserActionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.data, 8, false);
        }

        private NotificationsHostProcessUserActionParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements NotificationsHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void closeMessageCenter() {
            getProxyHandler().getMessageReceiver().accept(new NotificationsHostCloseMessageCenterParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void logInlineReplySent(String str) {
            NotificationsHostLogInlineReplySentParams notificationsHostLogInlineReplySentParams = new NotificationsHostLogInlineReplySentParams();
            notificationsHostLogInlineReplySentParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsHostLogInlineReplySentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void onDoNotDisturbStatusUpdated(ArcDoNotDisturbStatus arcDoNotDisturbStatus) {
            NotificationsHostOnDoNotDisturbStatusUpdatedParams notificationsHostOnDoNotDisturbStatusUpdatedParams = new NotificationsHostOnDoNotDisturbStatusUpdatedParams();
            notificationsHostOnDoNotDisturbStatusUpdatedParams.status = arcDoNotDisturbStatus;
            getProxyHandler().getMessageReceiver().accept(notificationsHostOnDoNotDisturbStatusUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void onLockScreenSettingUpdated(ArcLockScreenNotificationSetting arcLockScreenNotificationSetting) {
            NotificationsHostOnLockScreenSettingUpdatedParams notificationsHostOnLockScreenSettingUpdatedParams = new NotificationsHostOnLockScreenSettingUpdatedParams();
            notificationsHostOnLockScreenSettingUpdatedParams.setting = arcLockScreenNotificationSetting;
            getProxyHandler().getMessageReceiver().accept(notificationsHostOnLockScreenSettingUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void onNotificationPosted(ArcNotificationData arcNotificationData) {
            NotificationsHostOnNotificationPostedParams notificationsHostOnNotificationPostedParams = new NotificationsHostOnNotificationPostedParams();
            notificationsHostOnNotificationPostedParams.notificationData = arcNotificationData;
            getProxyHandler().getMessageReceiver().accept(notificationsHostOnNotificationPostedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void onNotificationRemoved(String str) {
            NotificationsHostOnNotificationRemovedParams notificationsHostOnNotificationRemovedParams = new NotificationsHostOnNotificationRemovedParams();
            notificationsHostOnNotificationRemovedParams.key = str;
            getProxyHandler().getMessageReceiver().accept(notificationsHostOnNotificationRemovedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void onNotificationUpdated(ArcNotificationData arcNotificationData) {
            NotificationsHostOnNotificationUpdatedParams notificationsHostOnNotificationUpdatedParams = new NotificationsHostOnNotificationUpdatedParams();
            notificationsHostOnNotificationUpdatedParams.notificationData = arcNotificationData;
            getProxyHandler().getMessageReceiver().accept(notificationsHostOnNotificationUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void openMessageCenter() {
            getProxyHandler().getMessageReceiver().accept(new NotificationsHostOpenMessageCenterParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.NotificationsHost
        public void processUserAction(ArcNotificationUserActionData arcNotificationUserActionData) {
            NotificationsHostProcessUserActionParams notificationsHostProcessUserActionParams = new NotificationsHostProcessUserActionParams();
            notificationsHostProcessUserActionParams.data = arcNotificationUserActionData;
            getProxyHandler().getMessageReceiver().accept(notificationsHostProcessUserActionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }
    }

    static final class Stub extends Interface.Stub<NotificationsHost> {
        Stub(Core core, NotificationsHost notificationsHost) {
            super(core, notificationsHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(NotificationsHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onNotificationPosted(NotificationsHostOnNotificationPostedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).notificationData);
                    return true;
                }
                if (type == 1) {
                    getImpl().onNotificationRemoved(NotificationsHostOnNotificationRemovedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
                    return true;
                }
                switch (type) {
                    case 5:
                        getImpl().onNotificationUpdated(NotificationsHostOnNotificationUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).notificationData);
                        return true;
                    case 6:
                        NotificationsHostOpenMessageCenterParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().openMessageCenter();
                        return true;
                    case 7:
                        getImpl().onDoNotDisturbStatusUpdated(NotificationsHostOnDoNotDisturbStatusUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status);
                        return true;
                    case 8:
                        NotificationsHostCloseMessageCenterParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().closeMessageCenter();
                        return true;
                    case 9:
                        getImpl().processUserAction(NotificationsHostProcessUserActionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).data);
                        return true;
                    case 10:
                        getImpl().onLockScreenSettingUpdated(NotificationsHostOnLockScreenSettingUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).setting);
                        return true;
                    case 11:
                        getImpl().logInlineReplySent(NotificationsHostLogInlineReplySentParams.deserialize(serviceMessageAsServiceMessage.getPayload()).key);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), NotificationsHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

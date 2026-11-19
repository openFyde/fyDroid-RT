// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

import org.chromium.ax.android.mojom.AccessibilityHelperHost;
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

class AccessibilityHelperHost_Internal {
    public static final Interface.Manager<AccessibilityHelperHost, AccessibilityHelperHost.Proxy> MANAGER = new Interface.Manager<AccessibilityHelperHost, AccessibilityHelperHost.Proxy>() { // from class: org.chromium.ax.android.mojom.AccessibilityHelperHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AccessibilityHelperHost[] buildArray(int i) {
            return new AccessibilityHelperHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AccessibilityHelperHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AccessibilityHelperHost> buildStub(Core core, AccessibilityHelperHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "ax.android.mojom.AccessibilityHelperHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 18;
        }
    };

    static final class AccessibilityHelperHostOnAccessibilityEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccessibilityEventData eventData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperHostOnAccessibilityEventParams() {
            super(16, 0);
        }

        public static AccessibilityHelperHostOnAccessibilityEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperHostOnAccessibilityEventParams accessibilityHelperHostOnAccessibilityEventParams = new AccessibilityHelperHostOnAccessibilityEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperHostOnAccessibilityEventParams.eventData = AccessibilityEventData.decode(decoder.readPointer(8, false));
                return accessibilityHelperHostOnAccessibilityEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.eventData, 8, false);
        }

        private AccessibilityHelperHostOnAccessibilityEventParams(int i) {
            super(16, i);
        }
    }

    static final class AccessibilityHelperHostOnNotificationStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String notificationKey;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperHostOnNotificationStateChangedParams() {
            super(24, 0);
        }

        public static AccessibilityHelperHostOnNotificationStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperHostOnNotificationStateChangedParams accessibilityHelperHostOnNotificationStateChangedParams = new AccessibilityHelperHostOnNotificationStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperHostOnNotificationStateChangedParams.notificationKey = decoder.readString(8, false);
                int i = decoder.readInt(16);
                accessibilityHelperHostOnNotificationStateChangedParams.state = i;
                AccessibilityNotificationStateType.validate(i);
                return accessibilityHelperHostOnNotificationStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.notificationKey, 8, false);
            encoderAtDataOffset.encode(this.state, 16);
        }

        private AccessibilityHelperHostOnNotificationStateChangedParams(int i) {
            super(24, i);
        }
    }

    static final class AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams() {
            super(16, 0);
        }

        public static AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams accessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams = new AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams.enabled = decoder.readBoolean(8, 0);
                return accessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AccessibilityHelperHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperHost
        public void onAccessibilityEvent(AccessibilityEventData accessibilityEventData) {
            AccessibilityHelperHostOnAccessibilityEventParams accessibilityHelperHostOnAccessibilityEventParams = new AccessibilityHelperHostOnAccessibilityEventParams();
            accessibilityHelperHostOnAccessibilityEventParams.eventData = accessibilityEventData;
            getProxyHandler().getMessageReceiver().accept(accessibilityHelperHostOnAccessibilityEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperHost
        public void onNotificationStateChanged(String str, int i) {
            AccessibilityHelperHostOnNotificationStateChangedParams accessibilityHelperHostOnNotificationStateChangedParams = new AccessibilityHelperHostOnNotificationStateChangedParams();
            accessibilityHelperHostOnNotificationStateChangedParams.notificationKey = str;
            accessibilityHelperHostOnNotificationStateChangedParams.state = i;
            getProxyHandler().getMessageReceiver().accept(accessibilityHelperHostOnNotificationStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperHost
        public void onToggleNativeChromeVoxArcSupport(boolean z) {
            AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams accessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams = new AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams();
            accessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams.enabled = z;
            getProxyHandler().getMessageReceiver().accept(accessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }
    }

    static final class Stub extends Interface.Stub<AccessibilityHelperHost> {
        Stub(Core core, AccessibilityHelperHost accessibilityHelperHost) {
            super(core, accessibilityHelperHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AccessibilityHelperHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    getImpl().onAccessibilityEvent(AccessibilityHelperHostOnAccessibilityEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).eventData);
                    return true;
                }
                if (type == 2) {
                    AccessibilityHelperHostOnNotificationStateChangedParams accessibilityHelperHostOnNotificationStateChangedParamsDeserialize = AccessibilityHelperHostOnNotificationStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onNotificationStateChanged(accessibilityHelperHostOnNotificationStateChangedParamsDeserialize.notificationKey, accessibilityHelperHostOnNotificationStateChangedParamsDeserialize.state);
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().onToggleNativeChromeVoxArcSupport(AccessibilityHelperHostOnToggleNativeChromeVoxArcSupportParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled);
                return true;
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AccessibilityHelperHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

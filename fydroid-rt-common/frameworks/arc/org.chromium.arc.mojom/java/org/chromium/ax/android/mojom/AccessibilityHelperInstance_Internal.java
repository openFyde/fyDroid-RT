// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

import org.chromium.ax.android.mojom.AccessibilityHelperInstance;
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
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.system.Core;

class AccessibilityHelperInstance_Internal {
    public static final Interface.Manager<AccessibilityHelperInstance, AccessibilityHelperInstance.Proxy> MANAGER = new Interface.Manager<AccessibilityHelperInstance, AccessibilityHelperInstance.Proxy>() { // from class: org.chromium.ax.android.mojom.AccessibilityHelperInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AccessibilityHelperInstance[] buildArray(int i) {
            return new AccessibilityHelperInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AccessibilityHelperInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AccessibilityHelperInstance> buildStub(Core core, AccessibilityHelperInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "ax.android.mojom.AccessibilityHelperInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 24;
        }
    };

    static final class AccessibilityHelperInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccessibilityHelperHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceInitParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceInitParams accessibilityHelperInstanceInitParams = new AccessibilityHelperInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstanceInitParams.hostRemote = (AccessibilityHelperHost) decoder.readServiceInterface(8, false, AccessibilityHelperHost.MANAGER);
                return accessibilityHelperInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<AccessibilityHelperHost, ?>) AccessibilityHelperHost.MANAGER);
        }

        private AccessibilityHelperInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class AccessibilityHelperInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class AccessibilityHelperInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AccessibilityHelperInstance.InitResponse mCallback;

        AccessibilityHelperInstanceInitResponseParamsForwardToCallback(AccessibilityHelperInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AccessibilityHelperInstanceInitResponseParamsProxyToResponder implements AccessibilityHelperInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AccessibilityHelperInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new AccessibilityHelperInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class AccessibilityHelperInstancePerformActionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccessibilityActionData actionData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstancePerformActionParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstancePerformActionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstancePerformActionParams accessibilityHelperInstancePerformActionParams = new AccessibilityHelperInstancePerformActionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstancePerformActionParams.actionData = AccessibilityActionData.decode(decoder.readPointer(8, false));
                return accessibilityHelperInstancePerformActionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.actionData, 8, false);
        }

        private AccessibilityHelperInstancePerformActionParams(int i) {
            super(16, i);
        }
    }

    static final class AccessibilityHelperInstancePerformActionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstancePerformActionResponseParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstancePerformActionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstancePerformActionResponseParams accessibilityHelperInstancePerformActionResponseParams = new AccessibilityHelperInstancePerformActionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstancePerformActionResponseParams.result = decoder.readBoolean(8, 0);
                return accessibilityHelperInstancePerformActionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private AccessibilityHelperInstancePerformActionResponseParams(int i) {
            super(16, i);
        }
    }

    static class AccessibilityHelperInstancePerformActionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AccessibilityHelperInstance.PerformActionResponse mCallback;

        AccessibilityHelperInstancePerformActionResponseParamsForwardToCallback(AccessibilityHelperInstance.PerformActionResponse performActionResponse) {
            this.mCallback = performActionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(AccessibilityHelperInstancePerformActionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AccessibilityHelperInstancePerformActionResponseParamsProxyToResponder implements AccessibilityHelperInstance.PerformActionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AccessibilityHelperInstancePerformActionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            AccessibilityHelperInstancePerformActionResponseParams accessibilityHelperInstancePerformActionResponseParams = new AccessibilityHelperInstancePerformActionResponseParams();
            accessibilityHelperInstancePerformActionResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(accessibilityHelperInstancePerformActionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class AccessibilityHelperInstanceRefreshWithExtraDataParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccessibilityActionData refreshData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceRefreshWithExtraDataParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstanceRefreshWithExtraDataParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceRefreshWithExtraDataParams accessibilityHelperInstanceRefreshWithExtraDataParams = new AccessibilityHelperInstanceRefreshWithExtraDataParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstanceRefreshWithExtraDataParams.refreshData = AccessibilityActionData.decode(decoder.readPointer(8, false));
                return accessibilityHelperInstanceRefreshWithExtraDataParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.refreshData, 8, false);
        }

        private AccessibilityHelperInstanceRefreshWithExtraDataParams(int i) {
            super(16, i);
        }
    }

    static final class AccessibilityHelperInstanceRefreshWithExtraDataResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Rect textLocation;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceRefreshWithExtraDataResponseParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstanceRefreshWithExtraDataResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceRefreshWithExtraDataResponseParams accessibilityHelperInstanceRefreshWithExtraDataResponseParams = new AccessibilityHelperInstanceRefreshWithExtraDataResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstanceRefreshWithExtraDataResponseParams.textLocation = Rect.decode(decoder.readPointer(8, true));
                return accessibilityHelperInstanceRefreshWithExtraDataResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.textLocation, 8, true);
        }

        private AccessibilityHelperInstanceRefreshWithExtraDataResponseParams(int i) {
            super(16, i);
        }
    }

    static class AccessibilityHelperInstanceRefreshWithExtraDataResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AccessibilityHelperInstance.RefreshWithExtraDataResponse mCallback;

        AccessibilityHelperInstanceRefreshWithExtraDataResponseParamsForwardToCallback(AccessibilityHelperInstance.RefreshWithExtraDataResponse refreshWithExtraDataResponse) {
            this.mCallback = refreshWithExtraDataResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(AccessibilityHelperInstanceRefreshWithExtraDataResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).textLocation);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AccessibilityHelperInstanceRefreshWithExtraDataResponseParamsProxyToResponder implements AccessibilityHelperInstance.RefreshWithExtraDataResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AccessibilityHelperInstanceRefreshWithExtraDataResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Rect rect) {
            AccessibilityHelperInstanceRefreshWithExtraDataResponseParams accessibilityHelperInstanceRefreshWithExtraDataResponseParams = new AccessibilityHelperInstanceRefreshWithExtraDataResponseParams();
            accessibilityHelperInstanceRefreshWithExtraDataResponseParams.textLocation = rect;
            this.mMessageReceiver.accept(accessibilityHelperInstanceRefreshWithExtraDataResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class AccessibilityHelperInstanceRequestSendAccessibilityTreeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccessibilityWindowKey window;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceRequestSendAccessibilityTreeParams() {
            super(24, 0);
        }

        public static AccessibilityHelperInstanceRequestSendAccessibilityTreeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceRequestSendAccessibilityTreeParams accessibilityHelperInstanceRequestSendAccessibilityTreeParams = new AccessibilityHelperInstanceRequestSendAccessibilityTreeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstanceRequestSendAccessibilityTreeParams.window = AccessibilityWindowKey.decode(decoder, 8);
                return accessibilityHelperInstanceRequestSendAccessibilityTreeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Union) this.window, 8, false);
        }

        private AccessibilityHelperInstanceRequestSendAccessibilityTreeParams(int i) {
            super(24, i);
        }
    }

    static final class AccessibilityHelperInstanceSetExploreByTouchEnabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceSetExploreByTouchEnabledParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstanceSetExploreByTouchEnabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceSetExploreByTouchEnabledParams accessibilityHelperInstanceSetExploreByTouchEnabledParams = new AccessibilityHelperInstanceSetExploreByTouchEnabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstanceSetExploreByTouchEnabledParams.enabled = decoder.readBoolean(8, 0);
                return accessibilityHelperInstanceSetExploreByTouchEnabledParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private AccessibilityHelperInstanceSetExploreByTouchEnabledParams(int i) {
            super(16, i);
        }
    }

    static final class AccessibilityHelperInstanceSetFilterParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int filterType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceSetFilterParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstanceSetFilterParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceSetFilterParams accessibilityHelperInstanceSetFilterParams = new AccessibilityHelperInstanceSetFilterParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                accessibilityHelperInstanceSetFilterParams.filterType = i;
                AccessibilityFilterType.validate(i);
                return accessibilityHelperInstanceSetFilterParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.filterType, 8);
        }

        private AccessibilityHelperInstanceSetFilterParams(int i) {
            super(16, i);
        }
    }

    static final class AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams = new AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams.enabled = decoder.readBoolean(8, 0);
                return accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams(int i) {
            super(16, i);
        }
    }

    static final class AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams() {
            super(16, 0);
        }

        public static AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams = new AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams.response = i;
                SetNativeChromeVoxResponse.validate(i);
                return accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.response, 8);
        }

        private AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams(int i) {
            super(16, i);
        }
    }

    static class AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AccessibilityHelperInstance.SetNativeChromeVoxArcSupportForFocusedWindowResponse mCallback;

        AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParamsForwardToCallback(AccessibilityHelperInstance.SetNativeChromeVoxArcSupportForFocusedWindowResponse setNativeChromeVoxArcSupportForFocusedWindowResponse) {
            this.mCallback = setNativeChromeVoxArcSupportForFocusedWindowResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParamsProxyToResponder implements AccessibilityHelperInstance.SetNativeChromeVoxArcSupportForFocusedWindowResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams = new AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams();
            accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams.response = num.intValue();
            this.mMessageReceiver.accept(accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AccessibilityHelperInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperInstance
        public void init(AccessibilityHelperHost accessibilityHelperHost, AccessibilityHelperInstance.InitResponse initResponse) {
            AccessibilityHelperInstanceInitParams accessibilityHelperInstanceInitParams = new AccessibilityHelperInstanceInitParams();
            accessibilityHelperInstanceInitParams.hostRemote = accessibilityHelperHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(accessibilityHelperInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new AccessibilityHelperInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperInstance
        public void performAction(AccessibilityActionData accessibilityActionData, AccessibilityHelperInstance.PerformActionResponse performActionResponse) {
            AccessibilityHelperInstancePerformActionParams accessibilityHelperInstancePerformActionParams = new AccessibilityHelperInstancePerformActionParams();
            accessibilityHelperInstancePerformActionParams.actionData = accessibilityActionData;
            getProxyHandler().getMessageReceiver().acceptWithResponder(accessibilityHelperInstancePerformActionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new AccessibilityHelperInstancePerformActionResponseParamsForwardToCallback(performActionResponse));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperInstance
        public void refreshWithExtraData(AccessibilityActionData accessibilityActionData, AccessibilityHelperInstance.RefreshWithExtraDataResponse refreshWithExtraDataResponse) {
            AccessibilityHelperInstanceRefreshWithExtraDataParams accessibilityHelperInstanceRefreshWithExtraDataParams = new AccessibilityHelperInstanceRefreshWithExtraDataParams();
            accessibilityHelperInstanceRefreshWithExtraDataParams.refreshData = accessibilityActionData;
            getProxyHandler().getMessageReceiver().acceptWithResponder(accessibilityHelperInstanceRefreshWithExtraDataParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new AccessibilityHelperInstanceRefreshWithExtraDataResponseParamsForwardToCallback(refreshWithExtraDataResponse));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperInstance
        public void requestSendAccessibilityTree(AccessibilityWindowKey accessibilityWindowKey) {
            AccessibilityHelperInstanceRequestSendAccessibilityTreeParams accessibilityHelperInstanceRequestSendAccessibilityTreeParams = new AccessibilityHelperInstanceRequestSendAccessibilityTreeParams();
            accessibilityHelperInstanceRequestSendAccessibilityTreeParams.window = accessibilityWindowKey;
            getProxyHandler().getMessageReceiver().accept(accessibilityHelperInstanceRequestSendAccessibilityTreeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperInstance
        public void setExploreByTouchEnabled(boolean z) {
            AccessibilityHelperInstanceSetExploreByTouchEnabledParams accessibilityHelperInstanceSetExploreByTouchEnabledParams = new AccessibilityHelperInstanceSetExploreByTouchEnabledParams();
            accessibilityHelperInstanceSetExploreByTouchEnabledParams.enabled = z;
            getProxyHandler().getMessageReceiver().accept(accessibilityHelperInstanceSetExploreByTouchEnabledParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperInstance
        public void setFilter(int i) {
            AccessibilityHelperInstanceSetFilterParams accessibilityHelperInstanceSetFilterParams = new AccessibilityHelperInstanceSetFilterParams();
            accessibilityHelperInstanceSetFilterParams.filterType = i;
            getProxyHandler().getMessageReceiver().accept(accessibilityHelperInstanceSetFilterParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.ax.android.mojom.AccessibilityHelperInstance
        public void setNativeChromeVoxArcSupportForFocusedWindow(boolean z, AccessibilityHelperInstance.SetNativeChromeVoxArcSupportForFocusedWindowResponse setNativeChromeVoxArcSupportForFocusedWindowResponse) {
            AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams = new AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams();
            accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams.enabled = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(accessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParamsForwardToCallback(setNativeChromeVoxArcSupportForFocusedWindowResponse));
        }
    }

    static final class Stub extends Interface.Stub<AccessibilityHelperInstance> {
        Stub(Core core, AccessibilityHelperInstance accessibilityHelperInstance) {
            super(core, accessibilityHelperInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AccessibilityHelperInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 2) {
                    getImpl().setFilter(AccessibilityHelperInstanceSetFilterParams.deserialize(serviceMessageAsServiceMessage.getPayload()).filterType);
                    return true;
                }
                if (type == 8) {
                    getImpl().setExploreByTouchEnabled(AccessibilityHelperInstanceSetExploreByTouchEnabledParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled);
                    return true;
                }
                if (type != 11) {
                    return false;
                }
                getImpl().requestSendAccessibilityTree(AccessibilityHelperInstanceRequestSendAccessibilityTreeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).window);
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
                if (!header.validateHeader(1)) {
                    return false;
                }
                int type = header.getType();
                if (type == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AccessibilityHelperInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 4) {
                    getImpl().performAction(AccessibilityHelperInstancePerformActionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).actionData, new AccessibilityHelperInstancePerformActionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 7) {
                    getImpl().init(AccessibilityHelperInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new AccessibilityHelperInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 9) {
                    getImpl().refreshWithExtraData(AccessibilityHelperInstanceRefreshWithExtraDataParams.deserialize(serviceMessageAsServiceMessage.getPayload()).refreshData, new AccessibilityHelperInstanceRefreshWithExtraDataResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 12) {
                    return false;
                }
                getImpl().setNativeChromeVoxArcSupportForFocusedWindow(AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled, new AccessibilityHelperInstanceSetNativeChromeVoxArcSupportForFocusedWindowResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ScreenCaptureHost;
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

class ScreenCaptureHost_Internal {
    public static final Interface.Manager<ScreenCaptureHost, ScreenCaptureHost.Proxy> MANAGER = new Interface.Manager<ScreenCaptureHost, ScreenCaptureHost.Proxy>() { // from class: org.chromium.arc.mojom.ScreenCaptureHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ScreenCaptureHost[] buildArray(int i) {
            return new ScreenCaptureHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ScreenCaptureHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ScreenCaptureHost> buildStub(Core core, ScreenCaptureHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ScreenCaptureHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements ScreenCaptureHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ScreenCaptureHost
        public void openSession(ScreenCaptureSessionNotifier screenCaptureSessionNotifier, String str, Size size, ScreenCaptureHost.OpenSessionResponse openSessionResponse) {
            ScreenCaptureHostOpenSessionParams screenCaptureHostOpenSessionParams = new ScreenCaptureHostOpenSessionParams();
            screenCaptureHostOpenSessionParams.notifier = screenCaptureSessionNotifier;
            screenCaptureHostOpenSessionParams.packageName = str;
            screenCaptureHostOpenSessionParams.size = size;
            getProxyHandler().getMessageReceiver().acceptWithResponder(screenCaptureHostOpenSessionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new ScreenCaptureHostOpenSessionResponseParamsForwardToCallback(openSessionResponse));
        }

        @Override // org.chromium.arc.mojom.ScreenCaptureHost
        public void requestPermission(String str, String str2, ScreenCaptureHost.RequestPermissionResponse requestPermissionResponse) {
            ScreenCaptureHostRequestPermissionParams screenCaptureHostRequestPermissionParams = new ScreenCaptureHostRequestPermissionParams();
            screenCaptureHostRequestPermissionParams.displayName = str;
            screenCaptureHostRequestPermissionParams.packageName = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(screenCaptureHostRequestPermissionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new ScreenCaptureHostRequestPermissionResponseParamsForwardToCallback(requestPermissionResponse));
        }

        @Override // org.chromium.arc.mojom.ScreenCaptureHost
        public void testModeAcceptPermission(String str) {
            ScreenCaptureHostTestModeAcceptPermissionParams screenCaptureHostTestModeAcceptPermissionParams = new ScreenCaptureHostTestModeAcceptPermissionParams();
            screenCaptureHostTestModeAcceptPermissionParams.packageName = str;
            getProxyHandler().getMessageReceiver().accept(screenCaptureHostTestModeAcceptPermissionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }
    }

    static final class ScreenCaptureHostOpenSessionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ScreenCaptureSessionNotifier notifier;
        public String packageName;
        public Size size;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureHostOpenSessionParams() {
            super(32, 0);
        }

        public static ScreenCaptureHostOpenSessionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ScreenCaptureHostOpenSessionParams screenCaptureHostOpenSessionParams = new ScreenCaptureHostOpenSessionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                screenCaptureHostOpenSessionParams.notifier = (ScreenCaptureSessionNotifier) decoder.readServiceInterface(8, false, ScreenCaptureSessionNotifier.MANAGER);
                screenCaptureHostOpenSessionParams.packageName = decoder.readString(16, false);
                screenCaptureHostOpenSessionParams.size = Size.decode(decoder.readPointer(24, false));
                return screenCaptureHostOpenSessionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.notifier, 8, false, (Interface.Manager<ScreenCaptureSessionNotifier, ?>) ScreenCaptureSessionNotifier.MANAGER);
            encoderAtDataOffset.encode(this.packageName, 16, false);
            encoderAtDataOffset.encode((Struct) this.size, 24, false);
        }

        private ScreenCaptureHostOpenSessionParams(int i) {
            super(32, i);
        }
    }

    static final class ScreenCaptureHostOpenSessionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ScreenCaptureSession session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureHostOpenSessionResponseParams() {
            super(16, 0);
        }

        public static ScreenCaptureHostOpenSessionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ScreenCaptureHostOpenSessionResponseParams screenCaptureHostOpenSessionResponseParams = new ScreenCaptureHostOpenSessionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                screenCaptureHostOpenSessionResponseParams.session = (ScreenCaptureSession) decoder.readServiceInterface(8, true, ScreenCaptureSession.MANAGER);
                return screenCaptureHostOpenSessionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.session, 8, true, (Interface.Manager<ScreenCaptureSession, ?>) ScreenCaptureSession.MANAGER);
        }

        private ScreenCaptureHostOpenSessionResponseParams(int i) {
            super(16, i);
        }
    }

    static class ScreenCaptureHostOpenSessionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ScreenCaptureHost.OpenSessionResponse mCallback;

        ScreenCaptureHostOpenSessionResponseParamsForwardToCallback(ScreenCaptureHost.OpenSessionResponse openSessionResponse) {
            this.mCallback = openSessionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(ScreenCaptureHostOpenSessionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).session);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ScreenCaptureHostOpenSessionResponseParamsProxyToResponder implements ScreenCaptureHost.OpenSessionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ScreenCaptureHostOpenSessionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ScreenCaptureSession screenCaptureSession) {
            ScreenCaptureHostOpenSessionResponseParams screenCaptureHostOpenSessionResponseParams = new ScreenCaptureHostOpenSessionResponseParams();
            screenCaptureHostOpenSessionResponseParams.session = screenCaptureSession;
            this.mMessageReceiver.accept(screenCaptureHostOpenSessionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class ScreenCaptureHostRequestPermissionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String displayName;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureHostRequestPermissionParams() {
            super(24, 0);
        }

        public static ScreenCaptureHostRequestPermissionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ScreenCaptureHostRequestPermissionParams screenCaptureHostRequestPermissionParams = new ScreenCaptureHostRequestPermissionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                screenCaptureHostRequestPermissionParams.displayName = decoder.readString(8, false);
                screenCaptureHostRequestPermissionParams.packageName = decoder.readString(16, false);
                return screenCaptureHostRequestPermissionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.displayName, 8, false);
            encoderAtDataOffset.encode(this.packageName, 16, false);
        }

        private ScreenCaptureHostRequestPermissionParams(int i) {
            super(24, i);
        }
    }

    static final class ScreenCaptureHostRequestPermissionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean granted;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureHostRequestPermissionResponseParams() {
            super(16, 0);
        }

        public static ScreenCaptureHostRequestPermissionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ScreenCaptureHostRequestPermissionResponseParams screenCaptureHostRequestPermissionResponseParams = new ScreenCaptureHostRequestPermissionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                screenCaptureHostRequestPermissionResponseParams.granted = decoder.readBoolean(8, 0);
                return screenCaptureHostRequestPermissionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.granted, 8, 0);
        }

        private ScreenCaptureHostRequestPermissionResponseParams(int i) {
            super(16, i);
        }
    }

    static class ScreenCaptureHostRequestPermissionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ScreenCaptureHost.RequestPermissionResponse mCallback;

        ScreenCaptureHostRequestPermissionResponseParamsForwardToCallback(ScreenCaptureHost.RequestPermissionResponse requestPermissionResponse) {
            this.mCallback = requestPermissionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(ScreenCaptureHostRequestPermissionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).granted));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ScreenCaptureHostRequestPermissionResponseParamsProxyToResponder implements ScreenCaptureHost.RequestPermissionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ScreenCaptureHostRequestPermissionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            ScreenCaptureHostRequestPermissionResponseParams screenCaptureHostRequestPermissionResponseParams = new ScreenCaptureHostRequestPermissionResponseParams();
            screenCaptureHostRequestPermissionResponseParams.granted = bool.booleanValue();
            this.mMessageReceiver.accept(screenCaptureHostRequestPermissionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class ScreenCaptureHostTestModeAcceptPermissionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureHostTestModeAcceptPermissionParams() {
            super(16, 0);
        }

        public static ScreenCaptureHostTestModeAcceptPermissionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ScreenCaptureHostTestModeAcceptPermissionParams screenCaptureHostTestModeAcceptPermissionParams = new ScreenCaptureHostTestModeAcceptPermissionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                screenCaptureHostTestModeAcceptPermissionParams.packageName = decoder.readString(8, false);
                return screenCaptureHostTestModeAcceptPermissionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private ScreenCaptureHostTestModeAcceptPermissionParams(int i) {
            super(16, i);
        }
    }

    static final class Stub extends Interface.Stub<ScreenCaptureHost> {
        Stub(Core core, ScreenCaptureHost screenCaptureHost) {
            super(core, screenCaptureHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ScreenCaptureHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 2) {
                    return false;
                }
                getImpl().testModeAcceptPermission(ScreenCaptureHostTestModeAcceptPermissionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ScreenCaptureHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    ScreenCaptureHostRequestPermissionParams screenCaptureHostRequestPermissionParamsDeserialize = ScreenCaptureHostRequestPermissionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestPermission(screenCaptureHostRequestPermissionParamsDeserialize.displayName, screenCaptureHostRequestPermissionParamsDeserialize.packageName, new ScreenCaptureHostRequestPermissionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                ScreenCaptureHostOpenSessionParams screenCaptureHostOpenSessionParamsDeserialize = ScreenCaptureHostOpenSessionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().openSession(screenCaptureHostOpenSessionParamsDeserialize.notifier, screenCaptureHostOpenSessionParamsDeserialize.packageName, screenCaptureHostOpenSessionParamsDeserialize.size, new ScreenCaptureHostOpenSessionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

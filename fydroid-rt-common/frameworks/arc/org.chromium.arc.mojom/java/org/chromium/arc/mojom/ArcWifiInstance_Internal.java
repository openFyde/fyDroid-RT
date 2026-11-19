// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ArcWifiInstance;
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

class ArcWifiInstance_Internal {
    public static final Interface.Manager<ArcWifiInstance, ArcWifiInstance.Proxy> MANAGER = new Interface.Manager<ArcWifiInstance, ArcWifiInstance.Proxy>() { // from class: org.chromium.arc.mojom.ArcWifiInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ArcWifiInstance[] buildArray(int i) {
            return new ArcWifiInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ArcWifiInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ArcWifiInstance> buildStub(Core core, ArcWifiInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ArcWifiInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ArcWifiInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcWifiHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiInstanceInitParams() {
            super(16, 0);
        }

        public static ArcWifiInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcWifiInstanceInitParams arcWifiInstanceInitParams = new ArcWifiInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcWifiInstanceInitParams.hostRemote = (ArcWifiHost) decoder.readServiceInterface(8, false, ArcWifiHost.MANAGER);
                return arcWifiInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<ArcWifiHost, ?>) ArcWifiHost.MANAGER);
        }

        private ArcWifiInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class ArcWifiInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class ArcWifiInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ArcWifiInstance.InitResponse mCallback;

        ArcWifiInstanceInitResponseParamsForwardToCallback(ArcWifiInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ArcWifiInstanceInitResponseParamsProxyToResponder implements ArcWifiInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ArcWifiInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new ArcWifiInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class ArcWifiInstanceScanCompletedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiInstanceScanCompletedParams() {
            super(8, 0);
        }

        public static ArcWifiInstanceScanCompletedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ArcWifiInstanceScanCompletedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ArcWifiInstanceScanCompletedParams(int i) {
            super(8, i);
        }
    }

    static final class ArcWifiInstanceWifiEnabledStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiInstanceWifiEnabledStateChangedParams() {
            super(16, 0);
        }

        public static ArcWifiInstanceWifiEnabledStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcWifiInstanceWifiEnabledStateChangedParams arcWifiInstanceWifiEnabledStateChangedParams = new ArcWifiInstanceWifiEnabledStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcWifiInstanceWifiEnabledStateChangedParams.enabled = decoder.readBoolean(8, 0);
                return arcWifiInstanceWifiEnabledStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private ArcWifiInstanceWifiEnabledStateChangedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ArcWifiInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ArcWifiInstance
        public void init(ArcWifiHost arcWifiHost, ArcWifiInstance.InitResponse initResponse) {
            ArcWifiInstanceInitParams arcWifiInstanceInitParams = new ArcWifiInstanceInitParams();
            arcWifiInstanceInitParams.hostRemote = arcWifiHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(arcWifiInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new ArcWifiInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.ArcWifiInstance
        public void scanCompleted() {
            getProxyHandler().getMessageReceiver().accept(new ArcWifiInstanceScanCompletedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.ArcWifiInstance
        public void wifiEnabledStateChanged(boolean z) {
            ArcWifiInstanceWifiEnabledStateChangedParams arcWifiInstanceWifiEnabledStateChangedParams = new ArcWifiInstanceWifiEnabledStateChangedParams();
            arcWifiInstanceWifiEnabledStateChangedParams.enabled = z;
            getProxyHandler().getMessageReceiver().accept(arcWifiInstanceWifiEnabledStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }
    }

    static final class Stub extends Interface.Stub<ArcWifiInstance> {
        Stub(Core core, ArcWifiInstance arcWifiInstance) {
            super(core, arcWifiInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ArcWifiInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 2) {
                    getImpl().wifiEnabledStateChanged(ArcWifiInstanceWifiEnabledStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled);
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                ArcWifiInstanceScanCompletedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().scanCompleted();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ArcWifiInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().init(ArcWifiInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new ArcWifiInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

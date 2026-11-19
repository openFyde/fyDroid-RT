// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.TtsInstance;
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

class TtsInstance_Internal {
    public static final Interface.Manager<TtsInstance, TtsInstance.Proxy> MANAGER = new Interface.Manager<TtsInstance, TtsInstance.Proxy>() { // from class: org.chromium.arc.mojom.TtsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TtsInstance[] buildArray(int i) {
            return new TtsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TtsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<TtsInstance> buildStub(Core core, TtsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.TtsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 4;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements TtsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.TtsInstance
        public void init(TtsHost ttsHost, TtsInstance.InitResponse initResponse) {
            TtsInstanceInitParams ttsInstanceInitParams = new TtsInstanceInitParams();
            ttsInstanceInitParams.hostRemote = ttsHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(ttsInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new TtsInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.TtsInstance
        public void refreshVoices() {
            getProxyHandler().getMessageReceiver().accept(new TtsInstanceRefreshVoicesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.TtsInstance
        public void speak(TtsUtterance ttsUtterance) {
            TtsInstanceSpeakParams ttsInstanceSpeakParams = new TtsInstanceSpeakParams();
            ttsInstanceSpeakParams.utterance = ttsUtterance;
            getProxyHandler().getMessageReceiver().accept(ttsInstanceSpeakParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.TtsInstance
        public void stop() {
            getProxyHandler().getMessageReceiver().accept(new TtsInstanceStopParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }
    }

    static final class Stub extends Interface.Stub<TtsInstance> {
        Stub(Core core, TtsInstance ttsInstance) {
            super(core, ttsInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(TtsInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 4) {
                    TtsInstanceRefreshVoicesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().refreshVoices();
                    return true;
                }
                if (type == 1) {
                    getImpl().speak(TtsInstanceSpeakParams.deserialize(serviceMessageAsServiceMessage.getPayload()).utterance);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                TtsInstanceStopParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().stop();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), TtsInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 3) {
                    return false;
                }
                getImpl().init(TtsInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new TtsInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class TtsInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TtsHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TtsInstanceInitParams() {
            super(16, 0);
        }

        public static TtsInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TtsInstanceInitParams ttsInstanceInitParams = new TtsInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                ttsInstanceInitParams.hostRemote = (TtsHost) decoder.readServiceInterface(8, false, TtsHost.MANAGER);
                return ttsInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<TtsHost, ?>) TtsHost.MANAGER);
        }

        private TtsInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class TtsInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TtsInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class TtsInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final TtsInstance.InitResponse mCallback;

        TtsInstanceInitResponseParamsForwardToCallback(TtsInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class TtsInstanceInitResponseParamsProxyToResponder implements TtsInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TtsInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new TtsInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class TtsInstanceRefreshVoicesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TtsInstanceRefreshVoicesParams() {
            super(8, 0);
        }

        public static TtsInstanceRefreshVoicesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new TtsInstanceRefreshVoicesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private TtsInstanceRefreshVoicesParams(int i) {
            super(8, i);
        }
    }

    static final class TtsInstanceSpeakParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TtsUtterance utterance;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TtsInstanceSpeakParams() {
            super(16, 0);
        }

        public static TtsInstanceSpeakParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TtsInstanceSpeakParams ttsInstanceSpeakParams = new TtsInstanceSpeakParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                ttsInstanceSpeakParams.utterance = TtsUtterance.decode(decoder.readPointer(8, false));
                return ttsInstanceSpeakParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.utterance, 8, false);
        }

        private TtsInstanceSpeakParams(int i) {
            super(16, i);
        }
    }

    static final class TtsInstanceStopParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TtsInstanceStopParams() {
            super(8, 0);
        }

        public static TtsInstanceStopParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new TtsInstanceStopParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private TtsInstanceStopParams(int i) {
            super(8, i);
        }
    }
}

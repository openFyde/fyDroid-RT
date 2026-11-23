// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom.keymint;

import org.chromium.arc.mojom.keymint.KeyMintInstance;
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

class KeyMintInstance_Internal {
    public static final Interface.Manager<KeyMintInstance, KeyMintInstance.Proxy> MANAGER = new Interface.Manager<KeyMintInstance, KeyMintInstance.Proxy>() { // from class: org.chromium.arc.mojom.keymint.KeyMintInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public KeyMintInstance[] buildArray(int i) {
            return new KeyMintInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public KeyMintInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<KeyMintInstance> buildStub(Core core, KeyMintInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.keymint.KeyMintInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class KeyMintInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeyMintHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeyMintInstanceInitParams() {
            super(16, 0);
        }

        public static KeyMintInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeyMintInstanceInitParams keyMintInstanceInitParams = new KeyMintInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keyMintInstanceInitParams.hostRemote = (KeyMintHost) decoder.readServiceInterface(8, false, KeyMintHost.MANAGER);
                return keyMintInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<KeyMintHost, ?>) KeyMintHost.MANAGER);
        }

        private KeyMintInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class KeyMintInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeyMintInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class KeyMintInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeyMintInstance.InitResponse mCallback;

        KeyMintInstanceInitResponseParamsForwardToCallback(KeyMintInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeyMintInstanceInitResponseParamsProxyToResponder implements KeyMintInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeyMintInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new KeyMintInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements KeyMintInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.keymint.KeyMintInstance
        public void init(KeyMintHost keyMintHost, KeyMintInstance.InitResponse initResponse) {
            KeyMintInstanceInitParams keyMintInstanceInitParams = new KeyMintInstanceInitParams();
            keyMintInstanceInitParams.hostRemote = keyMintHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keyMintInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new KeyMintInstanceInitResponseParamsForwardToCallback(initResponse));
        }
    }

    static final class Stub extends Interface.Stub<KeyMintInstance> {
        Stub(Core core, KeyMintInstance keyMintInstance) {
            super(core, keyMintInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(KeyMintInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), KeyMintInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().init(KeyMintInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new KeyMintInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

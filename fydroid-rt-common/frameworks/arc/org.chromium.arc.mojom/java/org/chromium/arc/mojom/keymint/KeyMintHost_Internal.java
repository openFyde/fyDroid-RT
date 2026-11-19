// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom.keymint;

import org.chromium.arc.mojom.keymint.KeyMintHost;
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

class KeyMintHost_Internal {
    public static final Interface.Manager<KeyMintHost, KeyMintHost.Proxy> MANAGER = new Interface.Manager<KeyMintHost, KeyMintHost.Proxy>() { // from class: org.chromium.arc.mojom.keymint.KeyMintHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public KeyMintHost[] buildArray(int i) {
            return new KeyMintHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public KeyMintHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<KeyMintHost> buildStub(Core core, KeyMintHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.keymint.KeyMintHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class KeyMintHostGetServerParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeyMintHostGetServerParams() {
            super(8, 0);
        }

        public static KeyMintHostGetServerParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new KeyMintHostGetServerParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private KeyMintHostGetServerParams(int i) {
            super(8, i);
        }
    }

    static final class KeyMintHostGetServerResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeyMintServer serverRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeyMintHostGetServerResponseParams() {
            super(16, 0);
        }

        public static KeyMintHostGetServerResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeyMintHostGetServerResponseParams keyMintHostGetServerResponseParams = new KeyMintHostGetServerResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keyMintHostGetServerResponseParams.serverRemote = (KeyMintServer) decoder.readServiceInterface(8, true, KeyMintServer.MANAGER);
                return keyMintHostGetServerResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.serverRemote, 8, true, (Interface.Manager<KeyMintServer, ?>) KeyMintServer.MANAGER);
        }

        private KeyMintHostGetServerResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeyMintHostGetServerResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeyMintHost.GetServerResponse mCallback;

        KeyMintHostGetServerResponseParamsForwardToCallback(KeyMintHost.GetServerResponse getServerResponse) {
            this.mCallback = getServerResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(KeyMintHostGetServerResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).serverRemote);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeyMintHostGetServerResponseParamsProxyToResponder implements KeyMintHost.GetServerResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeyMintHostGetServerResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(KeyMintServer keyMintServer) {
            KeyMintHostGetServerResponseParams keyMintHostGetServerResponseParams = new KeyMintHostGetServerResponseParams();
            keyMintHostGetServerResponseParams.serverRemote = keyMintServer;
            this.mMessageReceiver.accept(keyMintHostGetServerResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements KeyMintHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.keymint.KeyMintHost
        public void getServer(KeyMintHost.GetServerResponse getServerResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new KeyMintHostGetServerParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new KeyMintHostGetServerResponseParamsForwardToCallback(getServerResponse));
        }
    }

    static final class Stub extends Interface.Stub<KeyMintHost> {
        Stub(Core core, KeyMintHost keyMintHost) {
            super(core, keyMintHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(KeyMintHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), KeyMintHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                KeyMintHostGetServerParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getServer(new KeyMintHostGetServerResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

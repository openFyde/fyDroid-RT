// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ObbMounterInstance;
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

class ObbMounterInstance_Internal {
    public static final Interface.Manager<ObbMounterInstance, ObbMounterInstance.Proxy> MANAGER = new Interface.Manager<ObbMounterInstance, ObbMounterInstance.Proxy>() { // from class: org.chromium.arc.mojom.ObbMounterInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ObbMounterInstance[] buildArray(int i) {
            return new ObbMounterInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ObbMounterInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ObbMounterInstance> buildStub(Core core, ObbMounterInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ObbMounterInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class ObbMounterInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ObbMounterHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ObbMounterInstanceInitParams() {
            super(16, 0);
        }

        public static ObbMounterInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ObbMounterInstanceInitParams obbMounterInstanceInitParams = new ObbMounterInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                obbMounterInstanceInitParams.hostRemote = (ObbMounterHost) decoder.readServiceInterface(8, false, ObbMounterHost.MANAGER);
                return obbMounterInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<ObbMounterHost, ?>) ObbMounterHost.MANAGER);
        }

        private ObbMounterInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class ObbMounterInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ObbMounterInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class ObbMounterInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ObbMounterInstance.InitResponse mCallback;

        ObbMounterInstanceInitResponseParamsForwardToCallback(ObbMounterInstance.InitResponse initResponse) {
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

    static class ObbMounterInstanceInitResponseParamsProxyToResponder implements ObbMounterInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ObbMounterInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new ObbMounterInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ObbMounterInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ObbMounterInstance
        public void init(ObbMounterHost obbMounterHost, ObbMounterInstance.InitResponse initResponse) {
            ObbMounterInstanceInitParams obbMounterInstanceInitParams = new ObbMounterInstanceInitParams();
            obbMounterInstanceInitParams.hostRemote = obbMounterHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(obbMounterInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new ObbMounterInstanceInitResponseParamsForwardToCallback(initResponse));
        }
    }

    static final class Stub extends Interface.Stub<ObbMounterInstance> {
        Stub(Core core, ObbMounterInstance obbMounterInstance) {
            super(core, obbMounterInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ObbMounterInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ObbMounterInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().init(ObbMounterInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new ObbMounterInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

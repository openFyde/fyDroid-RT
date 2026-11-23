// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.NearbyShareHost;
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

class NearbyShareHost_Internal {
    public static final Interface.Manager<NearbyShareHost, NearbyShareHost.Proxy> MANAGER = new Interface.Manager<NearbyShareHost, NearbyShareHost.Proxy>() { // from class: org.chromium.arc.mojom.NearbyShareHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NearbyShareHost[] buildArray(int i) {
            return new NearbyShareHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NearbyShareHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<NearbyShareHost> buildStub(Core core, NearbyShareHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.NearbyShareHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class NearbyShareHostStartNearbyShareParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ShareIntentInfo info;
        public NearbyShareSessionInstance instance;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NearbyShareHostStartNearbyShareParams() {
            super(32, 0);
        }

        public static NearbyShareHostStartNearbyShareParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NearbyShareHostStartNearbyShareParams nearbyShareHostStartNearbyShareParams = new NearbyShareHostStartNearbyShareParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                nearbyShareHostStartNearbyShareParams.taskId = decoder.readInt(8);
                nearbyShareHostStartNearbyShareParams.info = ShareIntentInfo.decode(decoder.readPointer(16, false));
                nearbyShareHostStartNearbyShareParams.instance = (NearbyShareSessionInstance) decoder.readServiceInterface(24, false, NearbyShareSessionInstance.MANAGER);
                return nearbyShareHostStartNearbyShareParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.taskId, 8);
            encoderAtDataOffset.encode((Struct) this.info, 16, false);
            encoderAtDataOffset.encode(this.instance, 24, false, (Interface.Manager<NearbyShareSessionInstance, ?>) NearbyShareSessionInstance.MANAGER);
        }

        private NearbyShareHostStartNearbyShareParams(int i) {
            super(32, i);
        }
    }

    static final class NearbyShareHostStartNearbyShareResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NearbyShareSessionHost host;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NearbyShareHostStartNearbyShareResponseParams() {
            super(16, 0);
        }

        public static NearbyShareHostStartNearbyShareResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NearbyShareHostStartNearbyShareResponseParams nearbyShareHostStartNearbyShareResponseParams = new NearbyShareHostStartNearbyShareResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                nearbyShareHostStartNearbyShareResponseParams.host = (NearbyShareSessionHost) decoder.readServiceInterface(8, false, NearbyShareSessionHost.MANAGER);
                return nearbyShareHostStartNearbyShareResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.host, 8, false, (Interface.Manager<NearbyShareSessionHost, ?>) NearbyShareSessionHost.MANAGER);
        }

        private NearbyShareHostStartNearbyShareResponseParams(int i) {
            super(16, i);
        }
    }

    static class NearbyShareHostStartNearbyShareResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NearbyShareHost.StartNearbyShareResponse mCallback;

        NearbyShareHostStartNearbyShareResponseParamsForwardToCallback(NearbyShareHost.StartNearbyShareResponse startNearbyShareResponse) {
            this.mCallback = startNearbyShareResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(NearbyShareHostStartNearbyShareResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).host);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NearbyShareHostStartNearbyShareResponseParamsProxyToResponder implements NearbyShareHost.StartNearbyShareResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NearbyShareHostStartNearbyShareResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(NearbyShareSessionHost nearbyShareSessionHost) {
            NearbyShareHostStartNearbyShareResponseParams nearbyShareHostStartNearbyShareResponseParams = new NearbyShareHostStartNearbyShareResponseParams();
            nearbyShareHostStartNearbyShareResponseParams.host = nearbyShareSessionHost;
            this.mMessageReceiver.accept(nearbyShareHostStartNearbyShareResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements NearbyShareHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.NearbyShareHost
        public void startNearbyShare(int i, ShareIntentInfo shareIntentInfo, NearbyShareSessionInstance nearbyShareSessionInstance, NearbyShareHost.StartNearbyShareResponse startNearbyShareResponse) {
            NearbyShareHostStartNearbyShareParams nearbyShareHostStartNearbyShareParams = new NearbyShareHostStartNearbyShareParams();
            nearbyShareHostStartNearbyShareParams.taskId = i;
            nearbyShareHostStartNearbyShareParams.info = shareIntentInfo;
            nearbyShareHostStartNearbyShareParams.instance = nearbyShareSessionInstance;
            getProxyHandler().getMessageReceiver().acceptWithResponder(nearbyShareHostStartNearbyShareParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new NearbyShareHostStartNearbyShareResponseParamsForwardToCallback(startNearbyShareResponse));
        }
    }

    static final class Stub extends Interface.Stub<NearbyShareHost> {
        Stub(Core core, NearbyShareHost nearbyShareHost) {
            super(core, nearbyShareHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(NearbyShareHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), NearbyShareHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                NearbyShareHostStartNearbyShareParams nearbyShareHostStartNearbyShareParamsDeserialize = NearbyShareHostStartNearbyShareParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().startNearbyShare(nearbyShareHostStartNearbyShareParamsDeserialize.taskId, nearbyShareHostStartNearbyShareParamsDeserialize.info, nearbyShareHostStartNearbyShareParamsDeserialize.instance, new NearbyShareHostStartNearbyShareResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

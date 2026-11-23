// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.MemoryInstance;
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

class MemoryInstance_Internal {
    public static final Interface.Manager<MemoryInstance, MemoryInstance.Proxy> MANAGER = new Interface.Manager<MemoryInstance, MemoryInstance.Proxy>() { // from class: org.chromium.arc.mojom.MemoryInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MemoryInstance[] buildArray(int i) {
            return new MemoryInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MemoryInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MemoryInstance> buildStub(Core core, MemoryInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.MemoryInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class MemoryInstanceDropCachesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MemoryInstanceDropCachesParams() {
            super(8, 0);
        }

        public static MemoryInstanceDropCachesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MemoryInstanceDropCachesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MemoryInstanceDropCachesParams(int i) {
            super(8, i);
        }
    }

    static final class MemoryInstanceDropCachesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MemoryInstanceDropCachesResponseParams() {
            super(16, 0);
        }

        public static MemoryInstanceDropCachesResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MemoryInstanceDropCachesResponseParams memoryInstanceDropCachesResponseParams = new MemoryInstanceDropCachesResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                memoryInstanceDropCachesResponseParams.result = decoder.readBoolean(8, 0);
                return memoryInstanceDropCachesResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private MemoryInstanceDropCachesResponseParams(int i) {
            super(16, i);
        }
    }

    static class MemoryInstanceDropCachesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MemoryInstance.DropCachesResponse mCallback;

        MemoryInstanceDropCachesResponseParamsForwardToCallback(MemoryInstance.DropCachesResponse dropCachesResponse) {
            this.mCallback = dropCachesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(MemoryInstanceDropCachesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MemoryInstanceDropCachesResponseParamsProxyToResponder implements MemoryInstance.DropCachesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MemoryInstanceDropCachesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            MemoryInstanceDropCachesResponseParams memoryInstanceDropCachesResponseParams = new MemoryInstanceDropCachesResponseParams();
            memoryInstanceDropCachesResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(memoryInstanceDropCachesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class MemoryInstanceReclaimParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ReclaimRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MemoryInstanceReclaimParams() {
            super(16, 0);
        }

        public static MemoryInstanceReclaimParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MemoryInstanceReclaimParams memoryInstanceReclaimParams = new MemoryInstanceReclaimParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                memoryInstanceReclaimParams.request = ReclaimRequest.decode(decoder.readPointer(8, false));
                return memoryInstanceReclaimParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private MemoryInstanceReclaimParams(int i) {
            super(16, i);
        }
    }

    static final class MemoryInstanceReclaimResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ReclaimResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MemoryInstanceReclaimResponseParams() {
            super(16, 0);
        }

        public static MemoryInstanceReclaimResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MemoryInstanceReclaimResponseParams memoryInstanceReclaimResponseParams = new MemoryInstanceReclaimResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                memoryInstanceReclaimResponseParams.result = ReclaimResult.decode(decoder.readPointer(8, false));
                return memoryInstanceReclaimResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private MemoryInstanceReclaimResponseParams(int i) {
            super(16, i);
        }
    }

    static class MemoryInstanceReclaimResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MemoryInstance.ReclaimResponse mCallback;

        MemoryInstanceReclaimResponseParamsForwardToCallback(MemoryInstance.ReclaimResponse reclaimResponse) {
            this.mCallback = reclaimResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(MemoryInstanceReclaimResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MemoryInstanceReclaimResponseParamsProxyToResponder implements MemoryInstance.ReclaimResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MemoryInstanceReclaimResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ReclaimResult reclaimResult) {
            MemoryInstanceReclaimResponseParams memoryInstanceReclaimResponseParams = new MemoryInstanceReclaimResponseParams();
            memoryInstanceReclaimResponseParams.result = reclaimResult;
            this.mMessageReceiver.accept(memoryInstanceReclaimResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MemoryInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.MemoryInstance
        public void dropCaches(MemoryInstance.DropCachesResponse dropCachesResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new MemoryInstanceDropCachesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new MemoryInstanceDropCachesResponseParamsForwardToCallback(dropCachesResponse));
        }

        @Override // org.chromium.arc.mojom.MemoryInstance
        public void reclaim(ReclaimRequest reclaimRequest, MemoryInstance.ReclaimResponse reclaimResponse) {
            MemoryInstanceReclaimParams memoryInstanceReclaimParams = new MemoryInstanceReclaimParams();
            memoryInstanceReclaimParams.request = reclaimRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(memoryInstanceReclaimParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new MemoryInstanceReclaimResponseParamsForwardToCallback(reclaimResponse));
        }
    }

    static final class Stub extends Interface.Stub<MemoryInstance> {
        Stub(Core core, MemoryInstance memoryInstance) {
            super(core, memoryInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(MemoryInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MemoryInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    MemoryInstanceDropCachesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().dropCaches(new MemoryInstanceDropCachesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().reclaim(MemoryInstanceReclaimParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new MemoryInstanceReclaimResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

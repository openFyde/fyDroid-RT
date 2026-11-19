// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.DiskSpaceInstance;
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

class DiskSpaceInstance_Internal {
    public static final Interface.Manager<DiskSpaceInstance, DiskSpaceInstance.Proxy> MANAGER = new Interface.Manager<DiskSpaceInstance, DiskSpaceInstance.Proxy>() { // from class: org.chromium.arc.mojom.DiskSpaceInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DiskSpaceInstance[] buildArray(int i) {
            return new DiskSpaceInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DiskSpaceInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<DiskSpaceInstance> buildStub(Core core, DiskSpaceInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.DiskSpaceInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 4;
        }
    };

    static final class DiskSpaceInstanceGetApplicationsSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceInstanceGetApplicationsSizeParams() {
            super(8, 0);
        }

        public static DiskSpaceInstanceGetApplicationsSizeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new DiskSpaceInstanceGetApplicationsSizeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private DiskSpaceInstanceGetApplicationsSizeParams(int i) {
            super(8, i);
        }
    }

    static final class DiskSpaceInstanceGetApplicationsSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ApplicationsSize applicationsSize;
        public boolean succeeded;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceInstanceGetApplicationsSizeResponseParams() {
            super(24, 0);
        }

        public static DiskSpaceInstanceGetApplicationsSizeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceInstanceGetApplicationsSizeResponseParams diskSpaceInstanceGetApplicationsSizeResponseParams = new DiskSpaceInstanceGetApplicationsSizeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceInstanceGetApplicationsSizeResponseParams.succeeded = decoder.readBoolean(8, 0);
                diskSpaceInstanceGetApplicationsSizeResponseParams.applicationsSize = ApplicationsSize.decode(decoder.readPointer(16, true));
                return diskSpaceInstanceGetApplicationsSizeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.succeeded, 8, 0);
            encoderAtDataOffset.encode((Struct) this.applicationsSize, 16, true);
        }

        private DiskSpaceInstanceGetApplicationsSizeResponseParams(int i) {
            super(24, i);
        }
    }

    static class DiskSpaceInstanceGetApplicationsSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceInstance.GetApplicationsSizeResponse mCallback;

        DiskSpaceInstanceGetApplicationsSizeResponseParamsForwardToCallback(DiskSpaceInstance.GetApplicationsSizeResponse getApplicationsSizeResponse) {
            this.mCallback = getApplicationsSizeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                DiskSpaceInstanceGetApplicationsSizeResponseParams diskSpaceInstanceGetApplicationsSizeResponseParamsDeserialize = DiskSpaceInstanceGetApplicationsSizeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Boolean.valueOf(diskSpaceInstanceGetApplicationsSizeResponseParamsDeserialize.succeeded), diskSpaceInstanceGetApplicationsSizeResponseParamsDeserialize.applicationsSize);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DiskSpaceInstanceGetApplicationsSizeResponseParamsProxyToResponder implements DiskSpaceInstance.GetApplicationsSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceInstanceGetApplicationsSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Boolean bool, ApplicationsSize applicationsSize) {
            DiskSpaceInstanceGetApplicationsSizeResponseParams diskSpaceInstanceGetApplicationsSizeResponseParams = new DiskSpaceInstanceGetApplicationsSizeResponseParams();
            diskSpaceInstanceGetApplicationsSizeResponseParams.succeeded = bool.booleanValue();
            diskSpaceInstanceGetApplicationsSizeResponseParams.applicationsSize = applicationsSize;
            this.mMessageReceiver.accept(diskSpaceInstanceGetApplicationsSizeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class DiskSpaceInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public DiskSpaceHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceInstanceInitParams() {
            super(16, 0);
        }

        public static DiskSpaceInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceInstanceInitParams diskSpaceInstanceInitParams = new DiskSpaceInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceInstanceInitParams.hostRemote = (DiskSpaceHost) decoder.readServiceInterface(8, false, DiskSpaceHost.MANAGER);
                return diskSpaceInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<DiskSpaceHost, ?>) DiskSpaceHost.MANAGER);
        }

        private DiskSpaceInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class DiskSpaceInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class DiskSpaceInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceInstance.InitResponse mCallback;

        DiskSpaceInstanceInitResponseParamsForwardToCallback(DiskSpaceInstance.InitResponse initResponse) {
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

    static class DiskSpaceInstanceInitResponseParamsProxyToResponder implements DiskSpaceInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new DiskSpaceInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class DiskSpaceInstanceResizeStorageBalloonParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long freeSpaceBytes;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceInstanceResizeStorageBalloonParams() {
            super(16, 0);
        }

        public static DiskSpaceInstanceResizeStorageBalloonParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceInstanceResizeStorageBalloonParams diskSpaceInstanceResizeStorageBalloonParams = new DiskSpaceInstanceResizeStorageBalloonParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceInstanceResizeStorageBalloonParams.freeSpaceBytes = decoder.readLong(8);
                return diskSpaceInstanceResizeStorageBalloonParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.freeSpaceBytes, 8);
        }

        private DiskSpaceInstanceResizeStorageBalloonParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements DiskSpaceInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.DiskSpaceInstance
        public void getApplicationsSize(DiskSpaceInstance.GetApplicationsSizeResponse getApplicationsSizeResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new DiskSpaceInstanceGetApplicationsSizeParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new DiskSpaceInstanceGetApplicationsSizeResponseParamsForwardToCallback(getApplicationsSizeResponse));
        }

        @Override // org.chromium.arc.mojom.DiskSpaceInstance
        public void init(DiskSpaceHost diskSpaceHost, DiskSpaceInstance.InitResponse initResponse) {
            DiskSpaceInstanceInitParams diskSpaceInstanceInitParams = new DiskSpaceInstanceInitParams();
            diskSpaceInstanceInitParams.hostRemote = diskSpaceHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(diskSpaceInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new DiskSpaceInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.DiskSpaceInstance
        public void resizeStorageBalloon(long j) {
            DiskSpaceInstanceResizeStorageBalloonParams diskSpaceInstanceResizeStorageBalloonParams = new DiskSpaceInstanceResizeStorageBalloonParams();
            diskSpaceInstanceResizeStorageBalloonParams.freeSpaceBytes = j;
            getProxyHandler().getMessageReceiver().accept(diskSpaceInstanceResizeStorageBalloonParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }
    }

    static final class Stub extends Interface.Stub<DiskSpaceInstance> {
        Stub(Core core, DiskSpaceInstance diskSpaceInstance) {
            super(core, diskSpaceInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(DiskSpaceInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 2) {
                    return false;
                }
                getImpl().resizeStorageBalloon(DiskSpaceInstanceResizeStorageBalloonParams.deserialize(serviceMessageAsServiceMessage.getPayload()).freeSpaceBytes);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), DiskSpaceInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().init(DiskSpaceInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new DiskSpaceInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                DiskSpaceInstanceGetApplicationsSizeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getApplicationsSize(new DiskSpaceInstanceGetApplicationsSizeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

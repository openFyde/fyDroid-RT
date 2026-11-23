// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VolumeMounterInstance;
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
import org.chromium.mojo_base.mojom.FilePath;

class VolumeMounterInstance_Internal {
    public static final Interface.Manager<VolumeMounterInstance, VolumeMounterInstance.Proxy> MANAGER = new Interface.Manager<VolumeMounterInstance, VolumeMounterInstance.Proxy>() { // from class: org.chromium.arc.mojom.VolumeMounterInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VolumeMounterInstance[] buildArray(int i) {
            return new VolumeMounterInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VolumeMounterInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VolumeMounterInstance> buildStub(Core core, VolumeMounterInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VolumeMounterInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VolumeMounterInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VolumeMounterInstance
        public void init(VolumeMounterHost volumeMounterHost, VolumeMounterInstance.InitResponse initResponse) {
            VolumeMounterInstanceInitParams volumeMounterInstanceInitParams = new VolumeMounterInstanceInitParams();
            volumeMounterInstanceInitParams.hostRemote = volumeMounterHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(volumeMounterInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new VolumeMounterInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.VolumeMounterInstance
        public void onMountEvent(MountPointInfo mountPointInfo) {
            VolumeMounterInstanceOnMountEventParams volumeMounterInstanceOnMountEventParams = new VolumeMounterInstanceOnMountEventParams();
            volumeMounterInstanceOnMountEventParams.mountPointInfo = mountPointInfo;
            getProxyHandler().getMessageReceiver().accept(volumeMounterInstanceOnMountEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.VolumeMounterInstance
        public void prepareForRemovableMediaUnmount(FilePath filePath, VolumeMounterInstance.PrepareForRemovableMediaUnmountResponse prepareForRemovableMediaUnmountResponse) {
            VolumeMounterInstancePrepareForRemovableMediaUnmountParams volumeMounterInstancePrepareForRemovableMediaUnmountParams = new VolumeMounterInstancePrepareForRemovableMediaUnmountParams();
            volumeMounterInstancePrepareForRemovableMediaUnmountParams.path = filePath;
            getProxyHandler().getMessageReceiver().acceptWithResponder(volumeMounterInstancePrepareForRemovableMediaUnmountParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParamsForwardToCallback(prepareForRemovableMediaUnmountResponse));
        }
    }

    static final class Stub extends Interface.Stub<VolumeMounterInstance> {
        Stub(Core core, VolumeMounterInstance volumeMounterInstance) {
            super(core, volumeMounterInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VolumeMounterInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onMountEvent(VolumeMounterInstanceOnMountEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).mountPointInfo);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VolumeMounterInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().init(VolumeMounterInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new VolumeMounterInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().prepareForRemovableMediaUnmount(VolumeMounterInstancePrepareForRemovableMediaUnmountParams.deserialize(serviceMessageAsServiceMessage.getPayload()).path, new VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VolumeMounterInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VolumeMounterHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterInstanceInitParams() {
            super(16, 0);
        }

        public static VolumeMounterInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VolumeMounterInstanceInitParams volumeMounterInstanceInitParams = new VolumeMounterInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                volumeMounterInstanceInitParams.hostRemote = (VolumeMounterHost) decoder.readServiceInterface(8, false, VolumeMounterHost.MANAGER);
                return volumeMounterInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<VolumeMounterHost, ?>) VolumeMounterHost.MANAGER);
        }

        private VolumeMounterInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class VolumeMounterInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class VolumeMounterInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VolumeMounterInstance.InitResponse mCallback;

        VolumeMounterInstanceInitResponseParamsForwardToCallback(VolumeMounterInstance.InitResponse initResponse) {
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

    static class VolumeMounterInstanceInitResponseParamsProxyToResponder implements VolumeMounterInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VolumeMounterInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new VolumeMounterInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class VolumeMounterInstanceOnMountEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MountPointInfo mountPointInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterInstanceOnMountEventParams() {
            super(16, 0);
        }

        public static VolumeMounterInstanceOnMountEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VolumeMounterInstanceOnMountEventParams volumeMounterInstanceOnMountEventParams = new VolumeMounterInstanceOnMountEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                volumeMounterInstanceOnMountEventParams.mountPointInfo = MountPointInfo.decode(decoder.readPointer(8, false));
                return volumeMounterInstanceOnMountEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.mountPointInfo, 8, false);
        }

        private VolumeMounterInstanceOnMountEventParams(int i) {
            super(16, i);
        }
    }

    static final class VolumeMounterInstancePrepareForRemovableMediaUnmountParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FilePath path;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterInstancePrepareForRemovableMediaUnmountParams() {
            super(16, 0);
        }

        public static VolumeMounterInstancePrepareForRemovableMediaUnmountParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VolumeMounterInstancePrepareForRemovableMediaUnmountParams volumeMounterInstancePrepareForRemovableMediaUnmountParams = new VolumeMounterInstancePrepareForRemovableMediaUnmountParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                volumeMounterInstancePrepareForRemovableMediaUnmountParams.path = FilePath.decode(decoder.readPointer(8, false));
                return volumeMounterInstancePrepareForRemovableMediaUnmountParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.path, 8, false);
        }

        private VolumeMounterInstancePrepareForRemovableMediaUnmountParams(int i) {
            super(16, i);
        }
    }

    static final class VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams() {
            super(16, 0);
        }

        public static VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams volumeMounterInstancePrepareForRemovableMediaUnmountResponseParams = new VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                volumeMounterInstancePrepareForRemovableMediaUnmountResponseParams.success = decoder.readBoolean(8, 0);
                return volumeMounterInstancePrepareForRemovableMediaUnmountResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams(int i) {
            super(16, i);
        }
    }

    static class VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VolumeMounterInstance.PrepareForRemovableMediaUnmountResponse mCallback;

        VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParamsForwardToCallback(VolumeMounterInstance.PrepareForRemovableMediaUnmountResponse prepareForRemovableMediaUnmountResponse) {
            this.mCallback = prepareForRemovableMediaUnmountResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParamsProxyToResponder implements VolumeMounterInstance.PrepareForRemovableMediaUnmountResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams volumeMounterInstancePrepareForRemovableMediaUnmountResponseParams = new VolumeMounterInstancePrepareForRemovableMediaUnmountResponseParams();
            volumeMounterInstancePrepareForRemovableMediaUnmountResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(volumeMounterInstancePrepareForRemovableMediaUnmountResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }
}

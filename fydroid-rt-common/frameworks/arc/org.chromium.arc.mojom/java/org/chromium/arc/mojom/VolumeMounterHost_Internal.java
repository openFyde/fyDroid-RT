// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VolumeMounterHost;
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

class VolumeMounterHost_Internal {
    public static final Interface.Manager<VolumeMounterHost, VolumeMounterHost.Proxy> MANAGER = new Interface.Manager<VolumeMounterHost, VolumeMounterHost.Proxy>() { // from class: org.chromium.arc.mojom.VolumeMounterHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VolumeMounterHost[] buildArray(int i) {
            return new VolumeMounterHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VolumeMounterHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VolumeMounterHost> buildStub(Core core, VolumeMounterHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VolumeMounterHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 4;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VolumeMounterHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VolumeMounterHost
        public void requestAllMountPoints() {
            getProxyHandler().getMessageReceiver().accept(new VolumeMounterHostRequestAllMountPointsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.VolumeMounterHost
        public void setUpExternalStorageMountPoints(int i, VolumeMounterHost.SetUpExternalStorageMountPointsResponse setUpExternalStorageMountPointsResponse) {
            VolumeMounterHostSetUpExternalStorageMountPointsParams volumeMounterHostSetUpExternalStorageMountPointsParams = new VolumeMounterHostSetUpExternalStorageMountPointsParams();
            volumeMounterHostSetUpExternalStorageMountPointsParams.mediaProviderUid = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(volumeMounterHostSetUpExternalStorageMountPointsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new VolumeMounterHostSetUpExternalStorageMountPointsResponseParamsForwardToCallback(setUpExternalStorageMountPointsResponse));
        }
    }

    static final class Stub extends Interface.Stub<VolumeMounterHost> {
        Stub(Core core, VolumeMounterHost volumeMounterHost) {
            super(core, volumeMounterHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VolumeMounterHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                VolumeMounterHostRequestAllMountPointsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().requestAllMountPoints();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VolumeMounterHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 3) {
                    return false;
                }
                getImpl().setUpExternalStorageMountPoints(VolumeMounterHostSetUpExternalStorageMountPointsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).mediaProviderUid, new VolumeMounterHostSetUpExternalStorageMountPointsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VolumeMounterHostRequestAllMountPointsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterHostRequestAllMountPointsParams() {
            super(8, 0);
        }

        public static VolumeMounterHostRequestAllMountPointsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VolumeMounterHostRequestAllMountPointsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VolumeMounterHostRequestAllMountPointsParams(int i) {
            super(8, i);
        }
    }

    static final class VolumeMounterHostSetUpExternalStorageMountPointsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int mediaProviderUid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterHostSetUpExternalStorageMountPointsParams() {
            super(16, 0);
        }

        public static VolumeMounterHostSetUpExternalStorageMountPointsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VolumeMounterHostSetUpExternalStorageMountPointsParams volumeMounterHostSetUpExternalStorageMountPointsParams = new VolumeMounterHostSetUpExternalStorageMountPointsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                volumeMounterHostSetUpExternalStorageMountPointsParams.mediaProviderUid = decoder.readInt(8);
                return volumeMounterHostSetUpExternalStorageMountPointsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.mediaProviderUid, 8);
        }

        private VolumeMounterHostSetUpExternalStorageMountPointsParams(int i) {
            super(16, i);
        }
    }

    static final class VolumeMounterHostSetUpExternalStorageMountPointsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VolumeMounterHostSetUpExternalStorageMountPointsResponseParams() {
            super(16, 0);
        }

        public static VolumeMounterHostSetUpExternalStorageMountPointsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VolumeMounterHostSetUpExternalStorageMountPointsResponseParams volumeMounterHostSetUpExternalStorageMountPointsResponseParams = new VolumeMounterHostSetUpExternalStorageMountPointsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                volumeMounterHostSetUpExternalStorageMountPointsResponseParams.success = decoder.readBoolean(8, 0);
                return volumeMounterHostSetUpExternalStorageMountPointsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private VolumeMounterHostSetUpExternalStorageMountPointsResponseParams(int i) {
            super(16, i);
        }
    }

    static class VolumeMounterHostSetUpExternalStorageMountPointsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VolumeMounterHost.SetUpExternalStorageMountPointsResponse mCallback;

        VolumeMounterHostSetUpExternalStorageMountPointsResponseParamsForwardToCallback(VolumeMounterHost.SetUpExternalStorageMountPointsResponse setUpExternalStorageMountPointsResponse) {
            this.mCallback = setUpExternalStorageMountPointsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(VolumeMounterHostSetUpExternalStorageMountPointsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VolumeMounterHostSetUpExternalStorageMountPointsResponseParamsProxyToResponder implements VolumeMounterHost.SetUpExternalStorageMountPointsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VolumeMounterHostSetUpExternalStorageMountPointsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            VolumeMounterHostSetUpExternalStorageMountPointsResponseParams volumeMounterHostSetUpExternalStorageMountPointsResponseParams = new VolumeMounterHostSetUpExternalStorageMountPointsResponseParams();
            volumeMounterHostSetUpExternalStorageMountPointsResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(volumeMounterHostSetUpExternalStorageMountPointsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }
}

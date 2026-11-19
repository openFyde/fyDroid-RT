// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.cros.mojom.CameraModuleCallbacks;
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
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class CameraModuleCallbacks_Internal {
    public static final Interface.Manager<CameraModuleCallbacks, CameraModuleCallbacks.Proxy> MANAGER = new Interface.Manager<CameraModuleCallbacks, CameraModuleCallbacks.Proxy>() { // from class: org.chromium.cros.mojom.CameraModuleCallbacks_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraModuleCallbacks[] buildArray(int i) {
            return new CameraModuleCallbacks[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraModuleCallbacks.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<CameraModuleCallbacks> buildStub(Core core, CameraModuleCallbacks r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "cros.mojom.CameraModuleCallbacks";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class CameraModuleCallbacksCameraDeviceStatusChangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int cameraId;
        public int newStatus;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleCallbacksCameraDeviceStatusChangeParams() {
            super(16, 0);
        }

        public static CameraModuleCallbacksCameraDeviceStatusChangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleCallbacksCameraDeviceStatusChangeParams cameraModuleCallbacksCameraDeviceStatusChangeParams = new CameraModuleCallbacksCameraDeviceStatusChangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleCallbacksCameraDeviceStatusChangeParams.cameraId = decoder.readInt(8);
                int i = decoder.readInt(12);
                cameraModuleCallbacksCameraDeviceStatusChangeParams.newStatus = i;
                CameraDeviceStatus.validate(i);
                return cameraModuleCallbacksCameraDeviceStatusChangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.cameraId, 8);
            encoderAtDataOffset.encode(this.newStatus, 12);
        }

        private CameraModuleCallbacksCameraDeviceStatusChangeParams(int i) {
            super(16, i);
        }
    }

    static final class CameraModuleCallbacksTorchModeStatusChangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int cameraId;
        public int newStatus;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleCallbacksTorchModeStatusChangeParams() {
            super(16, 0);
        }

        public static CameraModuleCallbacksTorchModeStatusChangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleCallbacksTorchModeStatusChangeParams cameraModuleCallbacksTorchModeStatusChangeParams = new CameraModuleCallbacksTorchModeStatusChangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleCallbacksTorchModeStatusChangeParams.cameraId = decoder.readInt(8);
                int i = decoder.readInt(12);
                cameraModuleCallbacksTorchModeStatusChangeParams.newStatus = i;
                TorchModeStatus.validate(i);
                return cameraModuleCallbacksTorchModeStatusChangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.cameraId, 8);
            encoderAtDataOffset.encode(this.newStatus, 12);
        }

        private CameraModuleCallbacksTorchModeStatusChangeParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements CameraModuleCallbacks.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.cros.mojom.CameraModuleCallbacks
        public void cameraDeviceStatusChange(int i, int i2) {
            CameraModuleCallbacksCameraDeviceStatusChangeParams cameraModuleCallbacksCameraDeviceStatusChangeParams = new CameraModuleCallbacksCameraDeviceStatusChangeParams();
            cameraModuleCallbacksCameraDeviceStatusChangeParams.cameraId = i;
            cameraModuleCallbacksCameraDeviceStatusChangeParams.newStatus = i2;
            getProxyHandler().getMessageReceiver().accept(cameraModuleCallbacksCameraDeviceStatusChangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.cros.mojom.CameraModuleCallbacks
        public void torchModeStatusChange(int i, int i2) {
            CameraModuleCallbacksTorchModeStatusChangeParams cameraModuleCallbacksTorchModeStatusChangeParams = new CameraModuleCallbacksTorchModeStatusChangeParams();
            cameraModuleCallbacksTorchModeStatusChangeParams.cameraId = i;
            cameraModuleCallbacksTorchModeStatusChangeParams.newStatus = i2;
            getProxyHandler().getMessageReceiver().accept(cameraModuleCallbacksTorchModeStatusChangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<CameraModuleCallbacks> {
        Stub(Core core, CameraModuleCallbacks cameraModuleCallbacks) {
            super(core, cameraModuleCallbacks);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(CameraModuleCallbacks_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    CameraModuleCallbacksCameraDeviceStatusChangeParams cameraModuleCallbacksCameraDeviceStatusChangeParamsDeserialize = CameraModuleCallbacksCameraDeviceStatusChangeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().cameraDeviceStatusChange(cameraModuleCallbacksCameraDeviceStatusChangeParamsDeserialize.cameraId, cameraModuleCallbacksCameraDeviceStatusChangeParamsDeserialize.newStatus);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                CameraModuleCallbacksTorchModeStatusChangeParams cameraModuleCallbacksTorchModeStatusChangeParamsDeserialize = CameraModuleCallbacksTorchModeStatusChangeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().torchModeStatusChange(cameraModuleCallbacksTorchModeStatusChangeParamsDeserialize.cameraId, cameraModuleCallbacksTorchModeStatusChangeParamsDeserialize.newStatus);
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
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), CameraModuleCallbacks_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

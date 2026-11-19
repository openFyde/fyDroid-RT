package org.chromium.cros.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.cros.mojom.Camera3CallbackOps;
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

class Camera3CallbackOps_Internal {
    public static final Interface.Manager<Camera3CallbackOps, Camera3CallbackOps.Proxy> MANAGER = new Interface.Manager<Camera3CallbackOps, Camera3CallbackOps.Proxy>() { // from class: org.chromium.cros.mojom.Camera3CallbackOps_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Camera3CallbackOps[] buildArray(int i) {
            return new Camera3CallbackOps[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Camera3CallbackOps.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<Camera3CallbackOps> buildStub(Core core, Camera3CallbackOps r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "cros.mojom.Camera3CallbackOps";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class Camera3CallbackOpsNotifyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3NotifyMsg msg;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3CallbackOpsNotifyParams() {
            super(16, 0);
        }

        public static Camera3CallbackOpsNotifyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3CallbackOpsNotifyParams camera3CallbackOpsNotifyParams = new Camera3CallbackOpsNotifyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3CallbackOpsNotifyParams.msg = Camera3NotifyMsg.decode(decoder.readPointer(8, false));
                return camera3CallbackOpsNotifyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.msg, 8, false);
        }

        private Camera3CallbackOpsNotifyParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3CallbackOpsProcessCaptureResultParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3CaptureResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3CallbackOpsProcessCaptureResultParams() {
            super(16, 0);
        }

        public static Camera3CallbackOpsProcessCaptureResultParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3CallbackOpsProcessCaptureResultParams camera3CallbackOpsProcessCaptureResultParams = new Camera3CallbackOpsProcessCaptureResultParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3CallbackOpsProcessCaptureResultParams.result = Camera3CaptureResult.decode(decoder.readPointer(8, false));
                return camera3CallbackOpsProcessCaptureResultParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private Camera3CallbackOpsProcessCaptureResultParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3CallbackOpsRequestStreamBuffersParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3BufferRequest[] bufferReqs;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3CallbackOpsRequestStreamBuffersParams() {
            super(16, 0);
        }

        public static Camera3CallbackOpsRequestStreamBuffersParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                Camera3CallbackOpsRequestStreamBuffersParams camera3CallbackOpsRequestStreamBuffersParams = new Camera3CallbackOpsRequestStreamBuffersParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                camera3CallbackOpsRequestStreamBuffersParams.bufferReqs = new Camera3BufferRequest[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    camera3CallbackOpsRequestStreamBuffersParams.bufferReqs[i] = Camera3BufferRequest.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return camera3CallbackOpsRequestStreamBuffersParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            Camera3BufferRequest[] camera3BufferRequestArr = this.bufferReqs;
            if (camera3BufferRequestArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(camera3BufferRequestArr.length, 8, -1);
            int i = 0;
            while (true) {
                Camera3BufferRequest[] camera3BufferRequestArr2 = this.bufferReqs;
                if (i >= camera3BufferRequestArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) camera3BufferRequestArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private Camera3CallbackOpsRequestStreamBuffersParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3CallbackOpsRequestStreamBuffersResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public Camera3StreamBufferRet[] returnedBufReqs;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3CallbackOpsRequestStreamBuffersResponseParams() {
            super(24, 0);
        }

        public static Camera3CallbackOpsRequestStreamBuffersResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                Camera3CallbackOpsRequestStreamBuffersResponseParams camera3CallbackOpsRequestStreamBuffersResponseParams = new Camera3CallbackOpsRequestStreamBuffersResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                camera3CallbackOpsRequestStreamBuffersResponseParams.result = i;
                Camera3BufferRequestStatus.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                camera3CallbackOpsRequestStreamBuffersResponseParams.returnedBufReqs = new Camera3StreamBufferRet[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    camera3CallbackOpsRequestStreamBuffersResponseParams.returnedBufReqs[i2] = Camera3StreamBufferRet.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return camera3CallbackOpsRequestStreamBuffersResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            Camera3StreamBufferRet[] camera3StreamBufferRetArr = this.returnedBufReqs;
            if (camera3StreamBufferRetArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(camera3StreamBufferRetArr.length, 16, -1);
            int i = 0;
            while (true) {
                Camera3StreamBufferRet[] camera3StreamBufferRetArr2 = this.returnedBufReqs;
                if (i >= camera3StreamBufferRetArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) camera3StreamBufferRetArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private Camera3CallbackOpsRequestStreamBuffersResponseParams(int i) {
            super(24, i);
        }
    }

    static class Camera3CallbackOpsRequestStreamBuffersResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3CallbackOps.RequestStreamBuffersResponse mCallback;

        Camera3CallbackOpsRequestStreamBuffersResponseParamsForwardToCallback(Camera3CallbackOps.RequestStreamBuffersResponse requestStreamBuffersResponse) {
            this.mCallback = requestStreamBuffersResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                Camera3CallbackOpsRequestStreamBuffersResponseParams camera3CallbackOpsRequestStreamBuffersResponseParamsDeserialize = Camera3CallbackOpsRequestStreamBuffersResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(camera3CallbackOpsRequestStreamBuffersResponseParamsDeserialize.result), camera3CallbackOpsRequestStreamBuffersResponseParamsDeserialize.returnedBufReqs);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3CallbackOpsRequestStreamBuffersResponseParamsProxyToResponder implements Camera3CallbackOps.RequestStreamBuffersResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3CallbackOpsRequestStreamBuffersResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Camera3StreamBufferRet[] camera3StreamBufferRetArr) {
            Camera3CallbackOpsRequestStreamBuffersResponseParams camera3CallbackOpsRequestStreamBuffersResponseParams = new Camera3CallbackOpsRequestStreamBuffersResponseParams();
            camera3CallbackOpsRequestStreamBuffersResponseParams.result = num.intValue();
            camera3CallbackOpsRequestStreamBuffersResponseParams.returnedBufReqs = camera3StreamBufferRetArr;
            this.mMessageReceiver.accept(camera3CallbackOpsRequestStreamBuffersResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class Camera3CallbackOpsReturnStreamBuffersParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3StreamBuffer[] buffers;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3CallbackOpsReturnStreamBuffersParams() {
            super(16, 0);
        }

        public static Camera3CallbackOpsReturnStreamBuffersParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                Camera3CallbackOpsReturnStreamBuffersParams camera3CallbackOpsReturnStreamBuffersParams = new Camera3CallbackOpsReturnStreamBuffersParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                camera3CallbackOpsReturnStreamBuffersParams.buffers = new Camera3StreamBuffer[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    camera3CallbackOpsReturnStreamBuffersParams.buffers[i] = Camera3StreamBuffer.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return camera3CallbackOpsReturnStreamBuffersParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            Camera3StreamBuffer[] camera3StreamBufferArr = this.buffers;
            if (camera3StreamBufferArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(camera3StreamBufferArr.length, 8, -1);
            int i = 0;
            while (true) {
                Camera3StreamBuffer[] camera3StreamBufferArr2 = this.buffers;
                if (i >= camera3StreamBufferArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) camera3StreamBufferArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private Camera3CallbackOpsReturnStreamBuffersParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements Camera3CallbackOps.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.cros.mojom.Camera3CallbackOps
        public void notify(Camera3NotifyMsg camera3NotifyMsg) {
            Camera3CallbackOpsNotifyParams camera3CallbackOpsNotifyParams = new Camera3CallbackOpsNotifyParams();
            camera3CallbackOpsNotifyParams.msg = camera3NotifyMsg;
            getProxyHandler().getMessageReceiver().accept(camera3CallbackOpsNotifyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.cros.mojom.Camera3CallbackOps
        public void processCaptureResult(Camera3CaptureResult camera3CaptureResult) {
            Camera3CallbackOpsProcessCaptureResultParams camera3CallbackOpsProcessCaptureResultParams = new Camera3CallbackOpsProcessCaptureResultParams();
            camera3CallbackOpsProcessCaptureResultParams.result = camera3CaptureResult;
            getProxyHandler().getMessageReceiver().accept(camera3CallbackOpsProcessCaptureResultParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.cros.mojom.Camera3CallbackOps
        public void requestStreamBuffers(Camera3BufferRequest[] camera3BufferRequestArr, Camera3CallbackOps.RequestStreamBuffersResponse requestStreamBuffersResponse) {
            Camera3CallbackOpsRequestStreamBuffersParams camera3CallbackOpsRequestStreamBuffersParams = new Camera3CallbackOpsRequestStreamBuffersParams();
            camera3CallbackOpsRequestStreamBuffersParams.bufferReqs = camera3BufferRequestArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(camera3CallbackOpsRequestStreamBuffersParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new Camera3CallbackOpsRequestStreamBuffersResponseParamsForwardToCallback(requestStreamBuffersResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3CallbackOps
        public void returnStreamBuffers(Camera3StreamBuffer[] camera3StreamBufferArr) {
            Camera3CallbackOpsReturnStreamBuffersParams camera3CallbackOpsReturnStreamBuffersParams = new Camera3CallbackOpsReturnStreamBuffersParams();
            camera3CallbackOpsReturnStreamBuffersParams.buffers = camera3StreamBufferArr;
            getProxyHandler().getMessageReceiver().accept(camera3CallbackOpsReturnStreamBuffersParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }
    }

    static final class Stub extends Interface.Stub<Camera3CallbackOps> {
        Stub(Core core, Camera3CallbackOps camera3CallbackOps) {
            super(core, camera3CallbackOps);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(Camera3CallbackOps_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 3) {
                    getImpl().returnStreamBuffers(Camera3CallbackOpsReturnStreamBuffersParams.deserialize(serviceMessageAsServiceMessage.getPayload()).buffers);
                    return true;
                }
                if (type == 0) {
                    getImpl().processCaptureResult(Camera3CallbackOpsProcessCaptureResultParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().notify(Camera3CallbackOpsNotifyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).msg);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), Camera3CallbackOps_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 2) {
                    return false;
                }
                getImpl().requestStreamBuffers(Camera3CallbackOpsRequestStreamBuffersParams.deserialize(serviceMessageAsServiceMessage.getPayload()).bufferReqs, new Camera3CallbackOpsRequestStreamBuffersResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

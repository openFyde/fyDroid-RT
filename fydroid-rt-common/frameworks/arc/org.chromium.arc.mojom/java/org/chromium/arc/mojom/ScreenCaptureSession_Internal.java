package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ScreenCaptureSession;
import org.chromium.gfx.mojom.BufferFormat;
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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

class ScreenCaptureSession_Internal {
    public static final Interface.Manager<ScreenCaptureSession, ScreenCaptureSession.Proxy> MANAGER = new Interface.Manager<ScreenCaptureSession, ScreenCaptureSession.Proxy>() { // from class: org.chromium.arc.mojom.ScreenCaptureSession_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ScreenCaptureSession[] buildArray(int i) {
            return new ScreenCaptureSession[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ScreenCaptureSession.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ScreenCaptureSession> buildStub(Core core, ScreenCaptureSession r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ScreenCaptureSession";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements ScreenCaptureSession.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ScreenCaptureSession
        public void setOutputBuffer(UntypedHandle untypedHandle, int i, long j, int i2, ScreenCaptureSession.SetOutputBufferResponse setOutputBufferResponse) {
            ScreenCaptureSessionSetOutputBufferParams screenCaptureSessionSetOutputBufferParams = new ScreenCaptureSessionSetOutputBufferParams();
            screenCaptureSessionSetOutputBufferParams.graphicsBuffer = untypedHandle;
            screenCaptureSessionSetOutputBufferParams.bufferFormat = i;
            screenCaptureSessionSetOutputBufferParams.bufferFormatModifier = j;
            screenCaptureSessionSetOutputBufferParams.stride = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(screenCaptureSessionSetOutputBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new ScreenCaptureSessionSetOutputBufferResponseParamsForwardToCallback(setOutputBufferResponse));
        }

        @Override // org.chromium.arc.mojom.ScreenCaptureSession
        public void setOutputBufferDeprecated(UntypedHandle untypedHandle, int i, ScreenCaptureSession.SetOutputBufferDeprecatedResponse setOutputBufferDeprecatedResponse) {
            ScreenCaptureSessionSetOutputBufferDeprecatedParams screenCaptureSessionSetOutputBufferDeprecatedParams = new ScreenCaptureSessionSetOutputBufferDeprecatedParams();
            screenCaptureSessionSetOutputBufferDeprecatedParams.graphicsBuffer = untypedHandle;
            screenCaptureSessionSetOutputBufferDeprecatedParams.stride = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(screenCaptureSessionSetOutputBufferDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new ScreenCaptureSessionSetOutputBufferDeprecatedResponseParamsForwardToCallback(setOutputBufferDeprecatedResponse));
        }
    }

    static final class ScreenCaptureSessionSetOutputBufferDeprecatedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureSessionSetOutputBufferDeprecatedResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class ScreenCaptureSessionSetOutputBufferDeprecatedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ScreenCaptureSession.SetOutputBufferDeprecatedResponse mCallback;

        ScreenCaptureSessionSetOutputBufferDeprecatedResponseParamsForwardToCallback(ScreenCaptureSession.SetOutputBufferDeprecatedResponse setOutputBufferDeprecatedResponse) {
            this.mCallback = setOutputBufferDeprecatedResponse;
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

    static class ScreenCaptureSessionSetOutputBufferDeprecatedResponseParamsProxyToResponder implements ScreenCaptureSession.SetOutputBufferDeprecatedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ScreenCaptureSessionSetOutputBufferDeprecatedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new ScreenCaptureSessionSetOutputBufferDeprecatedResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class ScreenCaptureSessionSetOutputBufferResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureSessionSetOutputBufferResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class ScreenCaptureSessionSetOutputBufferResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ScreenCaptureSession.SetOutputBufferResponse mCallback;

        ScreenCaptureSessionSetOutputBufferResponseParamsForwardToCallback(ScreenCaptureSession.SetOutputBufferResponse setOutputBufferResponse) {
            this.mCallback = setOutputBufferResponse;
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

    static class ScreenCaptureSessionSetOutputBufferResponseParamsProxyToResponder implements ScreenCaptureSession.SetOutputBufferResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ScreenCaptureSessionSetOutputBufferResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new ScreenCaptureSessionSetOutputBufferResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Stub extends Interface.Stub<ScreenCaptureSession> {
        Stub(Core core, ScreenCaptureSession screenCaptureSession) {
            super(core, screenCaptureSession);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ScreenCaptureSession_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ScreenCaptureSession_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    ScreenCaptureSessionSetOutputBufferDeprecatedParams screenCaptureSessionSetOutputBufferDeprecatedParamsDeserialize = ScreenCaptureSessionSetOutputBufferDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setOutputBufferDeprecated(screenCaptureSessionSetOutputBufferDeprecatedParamsDeserialize.graphicsBuffer, screenCaptureSessionSetOutputBufferDeprecatedParamsDeserialize.stride, new ScreenCaptureSessionSetOutputBufferDeprecatedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                ScreenCaptureSessionSetOutputBufferParams screenCaptureSessionSetOutputBufferParamsDeserialize = ScreenCaptureSessionSetOutputBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().setOutputBuffer(screenCaptureSessionSetOutputBufferParamsDeserialize.graphicsBuffer, screenCaptureSessionSetOutputBufferParamsDeserialize.bufferFormat, screenCaptureSessionSetOutputBufferParamsDeserialize.bufferFormatModifier, screenCaptureSessionSetOutputBufferParamsDeserialize.stride, new ScreenCaptureSessionSetOutputBufferResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class ScreenCaptureSessionSetOutputBufferDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle graphicsBuffer;
        public int stride;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureSessionSetOutputBufferDeprecatedParams() {
            super(16, 0);
            this.graphicsBuffer = InvalidHandle.INSTANCE;
        }

        public static ScreenCaptureSessionSetOutputBufferDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ScreenCaptureSessionSetOutputBufferDeprecatedParams screenCaptureSessionSetOutputBufferDeprecatedParams = new ScreenCaptureSessionSetOutputBufferDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                screenCaptureSessionSetOutputBufferDeprecatedParams.graphicsBuffer = decoder.readUntypedHandle(8, false);
                screenCaptureSessionSetOutputBufferDeprecatedParams.stride = decoder.readInt(12);
                return screenCaptureSessionSetOutputBufferDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.graphicsBuffer, 8, false);
            encoderAtDataOffset.encode(this.stride, 12);
        }

        private ScreenCaptureSessionSetOutputBufferDeprecatedParams(int i) {
            super(16, i);
            this.graphicsBuffer = InvalidHandle.INSTANCE;
        }
    }

    static final class ScreenCaptureSessionSetOutputBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int bufferFormat;
        public long bufferFormatModifier;
        public UntypedHandle graphicsBuffer;
        public int stride;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ScreenCaptureSessionSetOutputBufferParams() {
            super(32, 0);
            this.graphicsBuffer = InvalidHandle.INSTANCE;
        }

        public static ScreenCaptureSessionSetOutputBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ScreenCaptureSessionSetOutputBufferParams screenCaptureSessionSetOutputBufferParams = new ScreenCaptureSessionSetOutputBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                screenCaptureSessionSetOutputBufferParams.graphicsBuffer = decoder.readUntypedHandle(8, false);
                int i = decoder.readInt(12);
                screenCaptureSessionSetOutputBufferParams.bufferFormat = i;
                BufferFormat.validate(i);
                screenCaptureSessionSetOutputBufferParams.bufferFormatModifier = decoder.readLong(16);
                screenCaptureSessionSetOutputBufferParams.stride = decoder.readInt(24);
                return screenCaptureSessionSetOutputBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.graphicsBuffer, 8, false);
            encoderAtDataOffset.encode(this.bufferFormat, 12);
            encoderAtDataOffset.encode(this.bufferFormatModifier, 16);
            encoderAtDataOffset.encode(this.stride, 24);
        }

        private ScreenCaptureSessionSetOutputBufferParams(int i) {
            super(32, i);
            this.graphicsBuffer = InvalidHandle.INSTANCE;
        }
    }
}

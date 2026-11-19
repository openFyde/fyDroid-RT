package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoFramePool;
import org.chromium.mojo.bindings.AssociatedInterfaceNotSupported;
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

class VideoFramePool_Internal {
    public static final Interface.Manager<VideoFramePool, VideoFramePool.Proxy> MANAGER = new Interface.Manager<VideoFramePool, VideoFramePool.Proxy>() { // from class: org.chromium.arc.mojom.VideoFramePool_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoFramePool[] buildArray(int i) {
            return new VideoFramePool[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoFramePool.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoFramePool> buildStub(Core core, VideoFramePool r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoFramePool";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoFramePool.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoFramePool
        public void addVideoFrame(VideoFrame videoFrame, VideoFramePool.AddVideoFrameResponse addVideoFrameResponse) {
            VideoFramePoolAddVideoFrameParams videoFramePoolAddVideoFrameParams = new VideoFramePoolAddVideoFrameParams();
            videoFramePoolAddVideoFrameParams.videoFrame = videoFrame;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoFramePoolAddVideoFrameParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new VideoFramePoolAddVideoFrameResponseParamsForwardToCallback(addVideoFrameResponse));
        }

        @Override // org.chromium.arc.mojom.VideoFramePool
        public void initialize(AssociatedInterfaceNotSupported associatedInterfaceNotSupported) {
            VideoFramePoolInitializeParams videoFramePoolInitializeParams = new VideoFramePoolInitializeParams();
            videoFramePoolInitializeParams.client = associatedInterfaceNotSupported;
            getProxyHandler().getMessageReceiver().accept(videoFramePoolInitializeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<VideoFramePool> {
        Stub(Core core, VideoFramePool videoFramePool) {
            super(core, videoFramePool);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoFramePool_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().initialize(VideoFramePoolInitializeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).client);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoFramePool_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().addVideoFrame(VideoFramePoolAddVideoFrameParams.deserialize(serviceMessageAsServiceMessage.getPayload()).videoFrame, new VideoFramePoolAddVideoFrameResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoFramePoolAddVideoFrameParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoFrame videoFrame;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoFramePoolAddVideoFrameParams() {
            super(16, 0);
        }

        public static VideoFramePoolAddVideoFrameParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoFramePoolAddVideoFrameParams videoFramePoolAddVideoFrameParams = new VideoFramePoolAddVideoFrameParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoFramePoolAddVideoFrameParams.videoFrame = VideoFrame.decode(decoder.readPointer(8, false));
                return videoFramePoolAddVideoFrameParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.videoFrame, 8, false);
        }

        private VideoFramePoolAddVideoFrameParams(int i) {
            super(16, i);
        }
    }

    static final class VideoFramePoolAddVideoFrameResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoFramePoolAddVideoFrameResponseParams() {
            super(16, 0);
        }

        public static VideoFramePoolAddVideoFrameResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoFramePoolAddVideoFrameResponseParams videoFramePoolAddVideoFrameResponseParams = new VideoFramePoolAddVideoFrameResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoFramePoolAddVideoFrameResponseParams.result = decoder.readBoolean(8, 0);
                return videoFramePoolAddVideoFrameResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private VideoFramePoolAddVideoFrameResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoFramePoolAddVideoFrameResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoFramePool.AddVideoFrameResponse mCallback;

        VideoFramePoolAddVideoFrameResponseParamsForwardToCallback(VideoFramePool.AddVideoFrameResponse addVideoFrameResponse) {
            this.mCallback = addVideoFrameResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(VideoFramePoolAddVideoFrameResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoFramePoolAddVideoFrameResponseParamsProxyToResponder implements VideoFramePool.AddVideoFrameResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoFramePoolAddVideoFrameResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            VideoFramePoolAddVideoFrameResponseParams videoFramePoolAddVideoFrameResponseParams = new VideoFramePoolAddVideoFrameResponseParams();
            videoFramePoolAddVideoFrameResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(videoFramePoolAddVideoFrameResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class VideoFramePoolInitializeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AssociatedInterfaceNotSupported client;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoFramePoolInitializeParams() {
            super(16, 0);
        }

        public static VideoFramePoolInitializeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoFramePoolInitializeParams videoFramePoolInitializeParams = new VideoFramePoolInitializeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoFramePoolInitializeParams.client = decoder.readAssociatedServiceInterfaceNotSupported(8, false);
                return videoFramePoolInitializeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.client, 8, false);
        }

        private VideoFramePoolInitializeParams(int i) {
            super(16, i);
        }
    }
}

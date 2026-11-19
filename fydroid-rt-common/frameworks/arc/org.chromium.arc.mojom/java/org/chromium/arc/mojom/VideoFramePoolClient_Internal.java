package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoFramePoolClient;
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

class VideoFramePoolClient_Internal {
    public static final Interface.Manager<VideoFramePoolClient, VideoFramePoolClient.Proxy> MANAGER = new Interface.Manager<VideoFramePoolClient, VideoFramePoolClient.Proxy>() { // from class: org.chromium.arc.mojom.VideoFramePoolClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoFramePoolClient[] buildArray(int i) {
            return new VideoFramePoolClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoFramePoolClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoFramePoolClient> buildStub(Core core, VideoFramePoolClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoFramePoolClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoFramePoolClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoFramePoolClient
        public void requestVideoFrames(int i, Size size, Rect rect, int i2, VideoFramePoolClient.RequestVideoFramesResponse requestVideoFramesResponse) {
            VideoFramePoolClientRequestVideoFramesParams videoFramePoolClientRequestVideoFramesParams = new VideoFramePoolClientRequestVideoFramesParams();
            videoFramePoolClientRequestVideoFramesParams.format = i;
            videoFramePoolClientRequestVideoFramesParams.codedSize = size;
            videoFramePoolClientRequestVideoFramesParams.visibleRect = rect;
            videoFramePoolClientRequestVideoFramesParams.numFrames = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoFramePoolClientRequestVideoFramesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new VideoFramePoolClientRequestVideoFramesResponseParamsForwardToCallback(requestVideoFramesResponse));
        }
    }

    static final class Stub extends Interface.Stub<VideoFramePoolClient> {
        Stub(Core core, VideoFramePoolClient videoFramePoolClient) {
            super(core, videoFramePoolClient);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoFramePoolClient_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoFramePoolClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 1) {
                    return false;
                }
                VideoFramePoolClientRequestVideoFramesParams videoFramePoolClientRequestVideoFramesParamsDeserialize = VideoFramePoolClientRequestVideoFramesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().requestVideoFrames(videoFramePoolClientRequestVideoFramesParamsDeserialize.format, videoFramePoolClientRequestVideoFramesParamsDeserialize.codedSize, videoFramePoolClientRequestVideoFramesParamsDeserialize.visibleRect, videoFramePoolClientRequestVideoFramesParamsDeserialize.numFrames, new VideoFramePoolClientRequestVideoFramesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoFramePoolClientRequestVideoFramesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Size codedSize;
        public int format;
        public int numFrames;
        public Rect visibleRect;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoFramePoolClientRequestVideoFramesParams() {
            super(32, 0);
        }

        public static VideoFramePoolClientRequestVideoFramesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoFramePoolClientRequestVideoFramesParams videoFramePoolClientRequestVideoFramesParams = new VideoFramePoolClientRequestVideoFramesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoFramePoolClientRequestVideoFramesParams.format = i;
                VideoPixelFormat.validate(i);
                videoFramePoolClientRequestVideoFramesParams.numFrames = decoder.readInt(12);
                videoFramePoolClientRequestVideoFramesParams.codedSize = Size.decode(decoder.readPointer(16, false));
                videoFramePoolClientRequestVideoFramesParams.visibleRect = Rect.decode(decoder.readPointer(24, false));
                return videoFramePoolClientRequestVideoFramesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.format, 8);
            encoderAtDataOffset.encode(this.numFrames, 12);
            encoderAtDataOffset.encode((Struct) this.codedSize, 16, false);
            encoderAtDataOffset.encode((Struct) this.visibleRect, 24, false);
        }

        private VideoFramePoolClientRequestVideoFramesParams(int i) {
            super(32, i);
        }
    }

    static final class VideoFramePoolClientRequestVideoFramesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoFramePoolClientRequestVideoFramesResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class VideoFramePoolClientRequestVideoFramesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoFramePoolClient.RequestVideoFramesResponse mCallback;

        VideoFramePoolClientRequestVideoFramesResponseParamsForwardToCallback(VideoFramePoolClient.RequestVideoFramesResponse requestVideoFramesResponse) {
            this.mCallback = requestVideoFramesResponse;
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

    static class VideoFramePoolClientRequestVideoFramesResponseParamsProxyToResponder implements VideoFramePoolClient.RequestVideoFramesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoFramePoolClientRequestVideoFramesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new VideoFramePoolClientRequestVideoFramesResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }
}

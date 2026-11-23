// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoDecoder;
import org.chromium.mojo.bindings.AssociatedInterfaceRequestNotSupported;
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
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.system.Core;

class VideoDecoder_Internal {
    public static final Interface.Manager<VideoDecoder, VideoDecoder.Proxy> MANAGER = new Interface.Manager<VideoDecoder, VideoDecoder.Proxy>() { // from class: org.chromium.arc.mojom.VideoDecoder_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecoder[] buildArray(int i) {
            return new VideoDecoder[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecoder.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoDecoder> buildStub(Core core, VideoDecoder r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoDecoder";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoDecoder.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoDecoder
        public void decode(DecoderBuffer decoderBuffer, VideoDecoder.DecodeResponse decodeResponse) {
            VideoDecoderDecodeParams videoDecoderDecodeParams = new VideoDecoderDecodeParams();
            videoDecoderDecodeParams.buffer = decoderBuffer;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoDecoderDecodeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new VideoDecoderDecodeResponseParamsForwardToCallback(decodeResponse));
        }

        @Override // org.chromium.arc.mojom.VideoDecoder
        public void initialize(VideoDecoderConfig videoDecoderConfig, VideoDecoderClient videoDecoderClient, AssociatedInterfaceRequestNotSupported associatedInterfaceRequestNotSupported, VideoDecoder.InitializeResponse initializeResponse) {
            VideoDecoderInitializeParams videoDecoderInitializeParams = new VideoDecoderInitializeParams();
            videoDecoderInitializeParams.config = videoDecoderConfig;
            videoDecoderInitializeParams.client = videoDecoderClient;
            videoDecoderInitializeParams.videoFramePool = associatedInterfaceRequestNotSupported;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoDecoderInitializeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new VideoDecoderInitializeResponseParamsForwardToCallback(initializeResponse));
        }

        @Override // org.chromium.arc.mojom.VideoDecoder
        public void releaseVideoFrame(int i) {
            VideoDecoderReleaseVideoFrameParams videoDecoderReleaseVideoFrameParams = new VideoDecoderReleaseVideoFrameParams();
            videoDecoderReleaseVideoFrameParams.videoFrameId = i;
            getProxyHandler().getMessageReceiver().accept(videoDecoderReleaseVideoFrameParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.VideoDecoder
        public void reset(VideoDecoder.ResetResponse resetResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VideoDecoderResetParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new VideoDecoderResetResponseParamsForwardToCallback(resetResponse));
        }
    }

    static final class Stub extends Interface.Stub<VideoDecoder> {
        Stub(Core core, VideoDecoder videoDecoder) {
            super(core, videoDecoder);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoDecoder_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 3) {
                    return false;
                }
                getImpl().releaseVideoFrame(VideoDecoderReleaseVideoFrameParams.deserialize(serviceMessageAsServiceMessage.getPayload()).videoFrameId);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoDecoder_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    VideoDecoderInitializeParams videoDecoderInitializeParamsDeserialize = VideoDecoderInitializeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().initialize(videoDecoderInitializeParamsDeserialize.config, videoDecoderInitializeParamsDeserialize.client, videoDecoderInitializeParamsDeserialize.videoFramePool, new VideoDecoderInitializeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    getImpl().decode(VideoDecoderDecodeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).buffer, new VideoDecoderDecodeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                VideoDecoderResetParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().reset(new VideoDecoderResetResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoDecoderDecodeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public DecoderBuffer buffer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderDecodeParams() {
            super(24, 0);
        }

        public static VideoDecoderDecodeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecoderDecodeParams videoDecoderDecodeParams = new VideoDecoderDecodeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecoderDecodeParams.buffer = DecoderBuffer.decode(decoder, 8);
                return videoDecoderDecodeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Union) this.buffer, 8, false);
        }

        private VideoDecoderDecodeParams(int i) {
            super(24, i);
        }
    }

    static final class VideoDecoderDecodeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderDecodeResponseParams() {
            super(16, 0);
        }

        public static VideoDecoderDecodeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecoderDecodeResponseParams videoDecoderDecodeResponseParams = new VideoDecoderDecodeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoDecoderDecodeResponseParams.status = i;
                DecoderStatus.validate(i);
                return videoDecoderDecodeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private VideoDecoderDecodeResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoDecoderDecodeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoDecoder.DecodeResponse mCallback;

        VideoDecoderDecodeResponseParamsForwardToCallback(VideoDecoder.DecodeResponse decodeResponse) {
            this.mCallback = decodeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VideoDecoderDecodeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoDecoderDecodeResponseParamsProxyToResponder implements VideoDecoder.DecodeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoDecoderDecodeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VideoDecoderDecodeResponseParams videoDecoderDecodeResponseParams = new VideoDecoderDecodeResponseParams();
            videoDecoderDecodeResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(videoDecoderDecodeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class VideoDecoderInitializeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoDecoderClient client;
        public VideoDecoderConfig config;
        public AssociatedInterfaceRequestNotSupported videoFramePool;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderInitializeParams() {
            super(32, 0);
        }

        public static VideoDecoderInitializeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecoderInitializeParams videoDecoderInitializeParams = new VideoDecoderInitializeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecoderInitializeParams.config = VideoDecoderConfig.decode(decoder.readPointer(8, false));
                videoDecoderInitializeParams.client = (VideoDecoderClient) decoder.readServiceInterface(16, false, VideoDecoderClient.MANAGER);
                videoDecoderInitializeParams.videoFramePool = decoder.readAssociatedInterfaceRequestNotSupported(24, false);
                return videoDecoderInitializeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.config, 8, false);
            encoderAtDataOffset.encode(this.client, 16, false, (Interface.Manager<VideoDecoderClient, ?>) VideoDecoderClient.MANAGER);
            encoderAtDataOffset.encode(this.videoFramePool, 24, false);
        }

        private VideoDecoderInitializeParams(int i) {
            super(32, i);
        }
    }

    static final class VideoDecoderInitializeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderInitializeResponseParams() {
            super(16, 0);
        }

        public static VideoDecoderInitializeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecoderInitializeResponseParams videoDecoderInitializeResponseParams = new VideoDecoderInitializeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoDecoderInitializeResponseParams.status = i;
                DecoderStatus.validate(i);
                return videoDecoderInitializeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private VideoDecoderInitializeResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoDecoderInitializeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoDecoder.InitializeResponse mCallback;

        VideoDecoderInitializeResponseParamsForwardToCallback(VideoDecoder.InitializeResponse initializeResponse) {
            this.mCallback = initializeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VideoDecoderInitializeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoDecoderInitializeResponseParamsProxyToResponder implements VideoDecoder.InitializeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoDecoderInitializeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VideoDecoderInitializeResponseParams videoDecoderInitializeResponseParams = new VideoDecoderInitializeResponseParams();
            videoDecoderInitializeResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(videoDecoderInitializeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class VideoDecoderReleaseVideoFrameParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int videoFrameId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderReleaseVideoFrameParams() {
            super(16, 0);
        }

        public static VideoDecoderReleaseVideoFrameParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecoderReleaseVideoFrameParams videoDecoderReleaseVideoFrameParams = new VideoDecoderReleaseVideoFrameParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecoderReleaseVideoFrameParams.videoFrameId = decoder.readInt(8);
                return videoDecoderReleaseVideoFrameParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.videoFrameId, 8);
        }

        private VideoDecoderReleaseVideoFrameParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecoderResetParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderResetParams() {
            super(8, 0);
        }

        public static VideoDecoderResetParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VideoDecoderResetParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VideoDecoderResetParams(int i) {
            super(8, i);
        }
    }

    static final class VideoDecoderResetResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderResetResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class VideoDecoderResetResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoDecoder.ResetResponse mCallback;

        VideoDecoderResetResponseParamsForwardToCallback(VideoDecoder.ResetResponse resetResponse) {
            this.mCallback = resetResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoDecoderResetResponseParamsProxyToResponder implements VideoDecoder.ResetResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoDecoderResetResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new VideoDecoderResetResponseParams().serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }
}

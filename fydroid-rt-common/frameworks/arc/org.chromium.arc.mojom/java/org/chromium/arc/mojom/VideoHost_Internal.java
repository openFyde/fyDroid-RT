// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoHost;
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

class VideoHost_Internal {
    public static final Interface.Manager<VideoHost, VideoHost.Proxy> MANAGER = new Interface.Manager<VideoHost, VideoHost.Proxy>() { // from class: org.chromium.arc.mojom.VideoHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoHost[] buildArray(int i) {
            return new VideoHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoHost> buildStub(Core core, VideoHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoHost
        public void createVideoAcceleratorFactory(VideoHost.CreateVideoAcceleratorFactoryResponse createVideoAcceleratorFactoryResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VideoHostCreateVideoAcceleratorFactoryParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new VideoHostCreateVideoAcceleratorFactoryResponseParamsForwardToCallback(createVideoAcceleratorFactoryResponse));
        }

        @Override // org.chromium.arc.mojom.VideoHost
        public void onBootstrapVideoAcceleratorFactory(VideoHost.OnBootstrapVideoAcceleratorFactoryResponse onBootstrapVideoAcceleratorFactoryResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VideoHostOnBootstrapVideoAcceleratorFactoryParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new VideoHostOnBootstrapVideoAcceleratorFactoryResponseParamsForwardToCallback(onBootstrapVideoAcceleratorFactoryResponse));
        }
    }

    static final class Stub extends Interface.Stub<VideoHost> {
        Stub(Core core, VideoHost videoHost) {
            super(core, videoHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 1) {
                    VideoHostOnBootstrapVideoAcceleratorFactoryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onBootstrapVideoAcceleratorFactory(new VideoHostOnBootstrapVideoAcceleratorFactoryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                VideoHostCreateVideoAcceleratorFactoryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().createVideoAcceleratorFactory(new VideoHostCreateVideoAcceleratorFactoryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoHostCreateVideoAcceleratorFactoryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoHostCreateVideoAcceleratorFactoryParams() {
            super(8, 0);
        }

        public static VideoHostCreateVideoAcceleratorFactoryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VideoHostCreateVideoAcceleratorFactoryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VideoHostCreateVideoAcceleratorFactoryParams(int i) {
            super(8, i);
        }
    }

    static final class VideoHostCreateVideoAcceleratorFactoryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoAcceleratorFactory remote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoHostCreateVideoAcceleratorFactoryResponseParams() {
            super(16, 0);
        }

        public static VideoHostCreateVideoAcceleratorFactoryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoHostCreateVideoAcceleratorFactoryResponseParams videoHostCreateVideoAcceleratorFactoryResponseParams = new VideoHostCreateVideoAcceleratorFactoryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoHostCreateVideoAcceleratorFactoryResponseParams.remote = (VideoAcceleratorFactory) decoder.readServiceInterface(8, false, VideoAcceleratorFactory.MANAGER);
                return videoHostCreateVideoAcceleratorFactoryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.remote, 8, false, (Interface.Manager<VideoAcceleratorFactory, ?>) VideoAcceleratorFactory.MANAGER);
        }

        private VideoHostCreateVideoAcceleratorFactoryResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoHostCreateVideoAcceleratorFactoryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoHost.CreateVideoAcceleratorFactoryResponse mCallback;

        VideoHostCreateVideoAcceleratorFactoryResponseParamsForwardToCallback(VideoHost.CreateVideoAcceleratorFactoryResponse createVideoAcceleratorFactoryResponse) {
            this.mCallback = createVideoAcceleratorFactoryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(VideoHostCreateVideoAcceleratorFactoryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).remote);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoHostCreateVideoAcceleratorFactoryResponseParamsProxyToResponder implements VideoHost.CreateVideoAcceleratorFactoryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoHostCreateVideoAcceleratorFactoryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(VideoAcceleratorFactory videoAcceleratorFactory) {
            VideoHostCreateVideoAcceleratorFactoryResponseParams videoHostCreateVideoAcceleratorFactoryResponseParams = new VideoHostCreateVideoAcceleratorFactoryResponseParams();
            videoHostCreateVideoAcceleratorFactoryResponseParams.remote = videoAcceleratorFactory;
            this.mMessageReceiver.accept(videoHostCreateVideoAcceleratorFactoryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class VideoHostOnBootstrapVideoAcceleratorFactoryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoHostOnBootstrapVideoAcceleratorFactoryParams() {
            super(8, 0);
        }

        public static VideoHostOnBootstrapVideoAcceleratorFactoryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VideoHostOnBootstrapVideoAcceleratorFactoryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VideoHostOnBootstrapVideoAcceleratorFactoryParams(int i) {
            super(8, i);
        }
    }

    static class VideoHostOnBootstrapVideoAcceleratorFactoryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoHost.OnBootstrapVideoAcceleratorFactoryResponse mCallback;

        VideoHostOnBootstrapVideoAcceleratorFactoryResponseParamsForwardToCallback(VideoHost.OnBootstrapVideoAcceleratorFactoryResponse onBootstrapVideoAcceleratorFactoryResponse) {
            this.mCallback = onBootstrapVideoAcceleratorFactoryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams videoHostOnBootstrapVideoAcceleratorFactoryResponseParamsDeserialize = VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(videoHostOnBootstrapVideoAcceleratorFactoryResponseParamsDeserialize.channelHandle, videoHostOnBootstrapVideoAcceleratorFactoryResponseParamsDeserialize.token);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoHostOnBootstrapVideoAcceleratorFactoryResponseParamsProxyToResponder implements VideoHost.OnBootstrapVideoAcceleratorFactoryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoHostOnBootstrapVideoAcceleratorFactoryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(UntypedHandle untypedHandle, String str) {
            VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams videoHostOnBootstrapVideoAcceleratorFactoryResponseParams = new VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams();
            videoHostOnBootstrapVideoAcceleratorFactoryResponseParams.channelHandle = untypedHandle;
            videoHostOnBootstrapVideoAcceleratorFactoryResponseParams.token = str;
            this.mMessageReceiver.accept(videoHostOnBootstrapVideoAcceleratorFactoryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle channelHandle;
        public String token;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams() {
            super(24, 0);
            this.channelHandle = InvalidHandle.INSTANCE;
        }

        public static VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams videoHostOnBootstrapVideoAcceleratorFactoryResponseParams = new VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoHostOnBootstrapVideoAcceleratorFactoryResponseParams.channelHandle = decoder.readUntypedHandle(8, false);
                videoHostOnBootstrapVideoAcceleratorFactoryResponseParams.token = decoder.readString(16, false);
                return videoHostOnBootstrapVideoAcceleratorFactoryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.channelHandle, 8, false);
            encoderAtDataOffset.encode(this.token, 16, false);
        }

        private VideoHostOnBootstrapVideoAcceleratorFactoryResponseParams(int i) {
            super(24, i);
            this.channelHandle = InvalidHandle.INSTANCE;
        }
    }
}

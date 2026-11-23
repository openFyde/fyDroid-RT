// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoProtectedBufferAllocator;
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

class VideoProtectedBufferAllocator_Internal {
    public static final Interface.Manager<VideoProtectedBufferAllocator, VideoProtectedBufferAllocator.Proxy> MANAGER = new Interface.Manager<VideoProtectedBufferAllocator, VideoProtectedBufferAllocator.Proxy>() { // from class: org.chromium.arc.mojom.VideoProtectedBufferAllocator_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoProtectedBufferAllocator[] buildArray(int i) {
            return new VideoProtectedBufferAllocator[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoProtectedBufferAllocator.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoProtectedBufferAllocator> buildStub(Core core, VideoProtectedBufferAllocator r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoProtectedBufferAllocator";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoProtectedBufferAllocator.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoProtectedBufferAllocator
        public void allocateProtectedNativePixmap(UntypedHandle untypedHandle, int i, Size size, VideoProtectedBufferAllocator.AllocateProtectedNativePixmapResponse allocateProtectedNativePixmapResponse) {
            VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams = new VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams();
            videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.handleFd = untypedHandle;
            videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.pixelFormat = i;
            videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.pictureSize = size;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParamsForwardToCallback(allocateProtectedNativePixmapResponse));
        }

        @Override // org.chromium.arc.mojom.VideoProtectedBufferAllocator
        public void allocateProtectedSharedMemory(UntypedHandle untypedHandle, long j, VideoProtectedBufferAllocator.AllocateProtectedSharedMemoryResponse allocateProtectedSharedMemoryResponse) {
            VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams = new VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams();
            videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams.handleFd = untypedHandle;
            videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams.size = j;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParamsForwardToCallback(allocateProtectedSharedMemoryResponse));
        }

        @Override // org.chromium.arc.mojom.VideoProtectedBufferAllocator
        public void releaseProtectedBuffer(UntypedHandle untypedHandle) {
            VideoProtectedBufferAllocatorReleaseProtectedBufferParams videoProtectedBufferAllocatorReleaseProtectedBufferParams = new VideoProtectedBufferAllocatorReleaseProtectedBufferParams();
            videoProtectedBufferAllocatorReleaseProtectedBufferParams.handleFd = untypedHandle;
            getProxyHandler().getMessageReceiver().accept(videoProtectedBufferAllocatorReleaseProtectedBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }
    }

    static final class Stub extends Interface.Stub<VideoProtectedBufferAllocator> {
        Stub(Core core, VideoProtectedBufferAllocator videoProtectedBufferAllocator) {
            super(core, videoProtectedBufferAllocator);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoProtectedBufferAllocator_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 2) {
                    return false;
                }
                getImpl().releaseProtectedBuffer(VideoProtectedBufferAllocatorReleaseProtectedBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload()).handleFd);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoProtectedBufferAllocator_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParamsDeserialize = VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().allocateProtectedSharedMemory(videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParamsDeserialize.handleFd, videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParamsDeserialize.size, new VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams videoProtectedBufferAllocatorAllocateProtectedNativePixmapParamsDeserialize = VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().allocateProtectedNativePixmap(videoProtectedBufferAllocatorAllocateProtectedNativePixmapParamsDeserialize.handleFd, videoProtectedBufferAllocatorAllocateProtectedNativePixmapParamsDeserialize.pixelFormat, videoProtectedBufferAllocatorAllocateProtectedNativePixmapParamsDeserialize.pictureSize, new VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams() {
            super(16, 0);
        }

        public static VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams videoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams = new VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams.result = decoder.readBoolean(8, 0);
                return videoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoProtectedBufferAllocator.AllocateProtectedNativePixmapResponse mCallback;

        VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParamsForwardToCallback(VideoProtectedBufferAllocator.AllocateProtectedNativePixmapResponse allocateProtectedNativePixmapResponse) {
            this.mCallback = allocateProtectedNativePixmapResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParamsProxyToResponder implements VideoProtectedBufferAllocator.AllocateProtectedNativePixmapResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams videoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams = new VideoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams();
            videoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(videoProtectedBufferAllocatorAllocateProtectedNativePixmapResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams() {
            super(16, 0);
        }

        public static VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams videoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams = new VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams.result = decoder.readBoolean(8, 0);
                return videoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoProtectedBufferAllocator.AllocateProtectedSharedMemoryResponse mCallback;

        VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParamsForwardToCallback(VideoProtectedBufferAllocator.AllocateProtectedSharedMemoryResponse allocateProtectedSharedMemoryResponse) {
            this.mCallback = allocateProtectedSharedMemoryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParamsProxyToResponder implements VideoProtectedBufferAllocator.AllocateProtectedSharedMemoryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams videoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams = new VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams();
            videoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(videoProtectedBufferAllocatorAllocateProtectedSharedMemoryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle handleFd;
        public Size pictureSize;
        public int pixelFormat;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams() {
            super(24, 0);
            this.handleFd = InvalidHandle.INSTANCE;
        }

        public static VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams = new VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.handleFd = decoder.readUntypedHandle(8, false);
                int i = decoder.readInt(12);
                videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.pixelFormat = i;
                HalPixelFormat.validate(i);
                videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams.pictureSize = Size.decode(decoder.readPointer(16, false));
                return videoProtectedBufferAllocatorAllocateProtectedNativePixmapParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.handleFd, 8, false);
            encoderAtDataOffset.encode(this.pixelFormat, 12);
            encoderAtDataOffset.encode((Struct) this.pictureSize, 16, false);
        }

        private VideoProtectedBufferAllocatorAllocateProtectedNativePixmapParams(int i) {
            super(24, i);
            this.handleFd = InvalidHandle.INSTANCE;
        }
    }

    static final class VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle handleFd;
        public long size;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams() {
            super(24, 0);
            this.handleFd = InvalidHandle.INSTANCE;
        }

        public static VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams = new VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams.handleFd = decoder.readUntypedHandle(8, false);
                videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams.size = decoder.readLong(16);
                return videoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.handleFd, 8, false);
            encoderAtDataOffset.encode(this.size, 16);
        }

        private VideoProtectedBufferAllocatorAllocateProtectedSharedMemoryParams(int i) {
            super(24, i);
            this.handleFd = InvalidHandle.INSTANCE;
        }
    }

    static final class VideoProtectedBufferAllocatorReleaseProtectedBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle handleFd;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoProtectedBufferAllocatorReleaseProtectedBufferParams() {
            super(16, 0);
            this.handleFd = InvalidHandle.INSTANCE;
        }

        public static VideoProtectedBufferAllocatorReleaseProtectedBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoProtectedBufferAllocatorReleaseProtectedBufferParams videoProtectedBufferAllocatorReleaseProtectedBufferParams = new VideoProtectedBufferAllocatorReleaseProtectedBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoProtectedBufferAllocatorReleaseProtectedBufferParams.handleFd = decoder.readUntypedHandle(8, false);
                return videoProtectedBufferAllocatorReleaseProtectedBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.handleFd, 8, false);
        }

        private VideoProtectedBufferAllocatorReleaseProtectedBufferParams(int i) {
            super(16, i);
            this.handleFd = InvalidHandle.INSTANCE;
        }
    }
}

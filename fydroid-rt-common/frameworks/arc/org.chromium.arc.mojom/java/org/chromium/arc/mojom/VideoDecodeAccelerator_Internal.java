// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.VideoDecodeAccelerator;
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

class VideoDecodeAccelerator_Internal {
    public static final Interface.Manager<VideoDecodeAccelerator, VideoDecodeAccelerator.Proxy> MANAGER = new Interface.Manager<VideoDecodeAccelerator, VideoDecodeAccelerator.Proxy>() { // from class: org.chromium.arc.mojom.VideoDecodeAccelerator_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecodeAccelerator[] buildArray(int i) {
            return new VideoDecodeAccelerator[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecodeAccelerator.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoDecodeAccelerator> buildStub(Core core, VideoDecodeAccelerator r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoDecodeAccelerator";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 4;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoDecodeAccelerator.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoDecodeAccelerator
        public void assignPictureBuffers(int i) {
            VideoDecodeAcceleratorAssignPictureBuffersParams videoDecodeAcceleratorAssignPictureBuffersParams = new VideoDecodeAcceleratorAssignPictureBuffersParams();
            videoDecodeAcceleratorAssignPictureBuffersParams.count = i;
            getProxyHandler().getMessageReceiver().accept(videoDecodeAcceleratorAssignPictureBuffersParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeAccelerator
        public void decode(BitstreamBuffer bitstreamBuffer) {
            VideoDecodeAcceleratorDecodeParams videoDecodeAcceleratorDecodeParams = new VideoDecodeAcceleratorDecodeParams();
            videoDecodeAcceleratorDecodeParams.bitstreamBuffer = bitstreamBuffer;
            getProxyHandler().getMessageReceiver().accept(videoDecodeAcceleratorDecodeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeAccelerator
        public void flush(VideoDecodeAccelerator.FlushResponse flushResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VideoDecodeAcceleratorFlushParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new VideoDecodeAcceleratorFlushResponseParamsForwardToCallback(flushResponse));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeAccelerator
        public void importBufferForPicture(int i, int i2, UntypedHandle untypedHandle, VideoFramePlane[] videoFramePlaneArr, BufferModifier bufferModifier) {
            VideoDecodeAcceleratorImportBufferForPictureParams videoDecodeAcceleratorImportBufferForPictureParams = new VideoDecodeAcceleratorImportBufferForPictureParams();
            videoDecodeAcceleratorImportBufferForPictureParams.pictureBufferId = i;
            videoDecodeAcceleratorImportBufferForPictureParams.format = i2;
            videoDecodeAcceleratorImportBufferForPictureParams.handleFd = untypedHandle;
            videoDecodeAcceleratorImportBufferForPictureParams.planes = videoFramePlaneArr;
            videoDecodeAcceleratorImportBufferForPictureParams.modifier = bufferModifier;
            getProxyHandler().getMessageReceiver().accept(videoDecodeAcceleratorImportBufferForPictureParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeAccelerator
        public void initialize(VideoDecodeAcceleratorConfig videoDecodeAcceleratorConfig, VideoDecodeClient videoDecodeClient, VideoDecodeAccelerator.InitializeResponse initializeResponse) {
            VideoDecodeAcceleratorInitializeParams videoDecodeAcceleratorInitializeParams = new VideoDecodeAcceleratorInitializeParams();
            videoDecodeAcceleratorInitializeParams.config = videoDecodeAcceleratorConfig;
            videoDecodeAcceleratorInitializeParams.client = videoDecodeClient;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoDecodeAcceleratorInitializeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new VideoDecodeAcceleratorInitializeResponseParamsForwardToCallback(initializeResponse));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeAccelerator
        public void reset(VideoDecodeAccelerator.ResetResponse resetResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VideoDecodeAcceleratorResetParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new VideoDecodeAcceleratorResetResponseParamsForwardToCallback(resetResponse));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeAccelerator
        public void reusePictureBuffer(int i) {
            VideoDecodeAcceleratorReusePictureBufferParams videoDecodeAcceleratorReusePictureBufferParams = new VideoDecodeAcceleratorReusePictureBufferParams();
            videoDecodeAcceleratorReusePictureBufferParams.pictureBufferId = i;
            getProxyHandler().getMessageReceiver().accept(videoDecodeAcceleratorReusePictureBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }
    }

    static final class Stub extends Interface.Stub<VideoDecodeAccelerator> {
        Stub(Core core, VideoDecodeAccelerator videoDecodeAccelerator) {
            super(core, videoDecodeAccelerator);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoDecodeAccelerator_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 4) {
                    getImpl().reusePictureBuffer(VideoDecodeAcceleratorReusePictureBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload()).pictureBufferId);
                    return true;
                }
                if (type == 9) {
                    VideoDecodeAcceleratorImportBufferForPictureParams videoDecodeAcceleratorImportBufferForPictureParamsDeserialize = VideoDecodeAcceleratorImportBufferForPictureParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().importBufferForPicture(videoDecodeAcceleratorImportBufferForPictureParamsDeserialize.pictureBufferId, videoDecodeAcceleratorImportBufferForPictureParamsDeserialize.format, videoDecodeAcceleratorImportBufferForPictureParamsDeserialize.handleFd, videoDecodeAcceleratorImportBufferForPictureParamsDeserialize.planes, videoDecodeAcceleratorImportBufferForPictureParamsDeserialize.modifier);
                    return true;
                }
                if (type == 1) {
                    getImpl().decode(VideoDecodeAcceleratorDecodeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).bitstreamBuffer);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().assignPictureBuffers(VideoDecodeAcceleratorAssignPictureBuffersParams.deserialize(serviceMessageAsServiceMessage.getPayload()).count);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoDecodeAccelerator_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    VideoDecodeAcceleratorInitializeParams videoDecodeAcceleratorInitializeParamsDeserialize = VideoDecodeAcceleratorInitializeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().initialize(videoDecodeAcceleratorInitializeParamsDeserialize.config, videoDecodeAcceleratorInitializeParamsDeserialize.client, new VideoDecodeAcceleratorInitializeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 5) {
                    VideoDecodeAcceleratorResetParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().reset(new VideoDecodeAcceleratorResetResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 6) {
                    return false;
                }
                VideoDecodeAcceleratorFlushParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().flush(new VideoDecodeAcceleratorFlushResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoDecodeAcceleratorAssignPictureBuffersParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int count;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorAssignPictureBuffersParams() {
            super(16, 0);
        }

        public static VideoDecodeAcceleratorAssignPictureBuffersParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeAcceleratorAssignPictureBuffersParams videoDecodeAcceleratorAssignPictureBuffersParams = new VideoDecodeAcceleratorAssignPictureBuffersParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecodeAcceleratorAssignPictureBuffersParams.count = decoder.readInt(8);
                return videoDecodeAcceleratorAssignPictureBuffersParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.count, 8);
        }

        private VideoDecodeAcceleratorAssignPictureBuffersParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecodeAcceleratorDecodeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BitstreamBuffer bitstreamBuffer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorDecodeParams() {
            super(16, 0);
        }

        public static VideoDecodeAcceleratorDecodeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeAcceleratorDecodeParams videoDecodeAcceleratorDecodeParams = new VideoDecodeAcceleratorDecodeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecodeAcceleratorDecodeParams.bitstreamBuffer = BitstreamBuffer.decode(decoder.readPointer(8, false));
                return videoDecodeAcceleratorDecodeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.bitstreamBuffer, 8, false);
        }

        private VideoDecodeAcceleratorDecodeParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecodeAcceleratorFlushParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorFlushParams() {
            super(8, 0);
        }

        public static VideoDecodeAcceleratorFlushParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VideoDecodeAcceleratorFlushParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VideoDecodeAcceleratorFlushParams(int i) {
            super(8, i);
        }
    }

    static final class VideoDecodeAcceleratorFlushResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorFlushResponseParams() {
            super(16, 0);
        }

        public static VideoDecodeAcceleratorFlushResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeAcceleratorFlushResponseParams videoDecodeAcceleratorFlushResponseParams = new VideoDecodeAcceleratorFlushResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoDecodeAcceleratorFlushResponseParams.result = i;
                VideoDecodeAccelerator.Result.validate(i);
                return videoDecodeAcceleratorFlushResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private VideoDecodeAcceleratorFlushResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoDecodeAcceleratorFlushResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoDecodeAccelerator.FlushResponse mCallback;

        VideoDecodeAcceleratorFlushResponseParamsForwardToCallback(VideoDecodeAccelerator.FlushResponse flushResponse) {
            this.mCallback = flushResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VideoDecodeAcceleratorFlushResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoDecodeAcceleratorFlushResponseParamsProxyToResponder implements VideoDecodeAccelerator.FlushResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoDecodeAcceleratorFlushResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VideoDecodeAcceleratorFlushResponseParams videoDecodeAcceleratorFlushResponseParams = new VideoDecodeAcceleratorFlushResponseParams();
            videoDecodeAcceleratorFlushResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(videoDecodeAcceleratorFlushResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class VideoDecodeAcceleratorInitializeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoDecodeClient client;
        public VideoDecodeAcceleratorConfig config;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorInitializeParams() {
            super(24, 0);
        }

        public static VideoDecodeAcceleratorInitializeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeAcceleratorInitializeParams videoDecodeAcceleratorInitializeParams = new VideoDecodeAcceleratorInitializeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecodeAcceleratorInitializeParams.config = VideoDecodeAcceleratorConfig.decode(decoder.readPointer(8, false));
                videoDecodeAcceleratorInitializeParams.client = (VideoDecodeClient) decoder.readServiceInterface(16, false, VideoDecodeClient.MANAGER);
                return videoDecodeAcceleratorInitializeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.config, 8, false);
            encoderAtDataOffset.encode(this.client, 16, false, (Interface.Manager<VideoDecodeClient, ?>) VideoDecodeClient.MANAGER);
        }

        private VideoDecodeAcceleratorInitializeParams(int i) {
            super(24, i);
        }
    }

    static final class VideoDecodeAcceleratorInitializeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorInitializeResponseParams() {
            super(16, 0);
        }

        public static VideoDecodeAcceleratorInitializeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeAcceleratorInitializeResponseParams videoDecodeAcceleratorInitializeResponseParams = new VideoDecodeAcceleratorInitializeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoDecodeAcceleratorInitializeResponseParams.result = i;
                VideoDecodeAccelerator.Result.validate(i);
                return videoDecodeAcceleratorInitializeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private VideoDecodeAcceleratorInitializeResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoDecodeAcceleratorInitializeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoDecodeAccelerator.InitializeResponse mCallback;

        VideoDecodeAcceleratorInitializeResponseParamsForwardToCallback(VideoDecodeAccelerator.InitializeResponse initializeResponse) {
            this.mCallback = initializeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VideoDecodeAcceleratorInitializeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoDecodeAcceleratorInitializeResponseParamsProxyToResponder implements VideoDecodeAccelerator.InitializeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoDecodeAcceleratorInitializeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VideoDecodeAcceleratorInitializeResponseParams videoDecodeAcceleratorInitializeResponseParams = new VideoDecodeAcceleratorInitializeResponseParams();
            videoDecodeAcceleratorInitializeResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(videoDecodeAcceleratorInitializeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class VideoDecodeAcceleratorResetParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorResetParams() {
            super(8, 0);
        }

        public static VideoDecodeAcceleratorResetParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VideoDecodeAcceleratorResetParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VideoDecodeAcceleratorResetParams(int i) {
            super(8, i);
        }
    }

    static final class VideoDecodeAcceleratorResetResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorResetResponseParams() {
            super(16, 0);
        }

        public static VideoDecodeAcceleratorResetResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeAcceleratorResetResponseParams videoDecodeAcceleratorResetResponseParams = new VideoDecodeAcceleratorResetResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoDecodeAcceleratorResetResponseParams.result = i;
                VideoDecodeAccelerator.Result.validate(i);
                return videoDecodeAcceleratorResetResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private VideoDecodeAcceleratorResetResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoDecodeAcceleratorResetResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoDecodeAccelerator.ResetResponse mCallback;

        VideoDecodeAcceleratorResetResponseParamsForwardToCallback(VideoDecodeAccelerator.ResetResponse resetResponse) {
            this.mCallback = resetResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VideoDecodeAcceleratorResetResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoDecodeAcceleratorResetResponseParamsProxyToResponder implements VideoDecodeAccelerator.ResetResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoDecodeAcceleratorResetResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VideoDecodeAcceleratorResetResponseParams videoDecodeAcceleratorResetResponseParams = new VideoDecodeAcceleratorResetResponseParams();
            videoDecodeAcceleratorResetResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(videoDecodeAcceleratorResetResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class VideoDecodeAcceleratorReusePictureBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int pictureBufferId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeAcceleratorReusePictureBufferParams() {
            super(16, 0);
        }

        public static VideoDecodeAcceleratorReusePictureBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeAcceleratorReusePictureBufferParams videoDecodeAcceleratorReusePictureBufferParams = new VideoDecodeAcceleratorReusePictureBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecodeAcceleratorReusePictureBufferParams.pictureBufferId = decoder.readInt(8);
                return videoDecodeAcceleratorReusePictureBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.pictureBufferId, 8);
        }

        private VideoDecodeAcceleratorReusePictureBufferParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecodeAcceleratorImportBufferForPictureParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int format;
        public UntypedHandle handleFd;
        public BufferModifier modifier;
        public int pictureBufferId;
        public VideoFramePlane[] planes;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 4)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public VideoDecodeAcceleratorImportBufferForPictureParams() {
            super(40, 4);
            this.handleFd = InvalidHandle.INSTANCE;
        }

        public static VideoDecodeAcceleratorImportBufferForPictureParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                int i = decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                VideoDecodeAcceleratorImportBufferForPictureParams videoDecodeAcceleratorImportBufferForPictureParams = new VideoDecodeAcceleratorImportBufferForPictureParams(i);
                videoDecodeAcceleratorImportBufferForPictureParams.pictureBufferId = decoderOutline3.readInt(8);
                int i2 = decoderOutline3.readInt(12);
                videoDecodeAcceleratorImportBufferForPictureParams.format = i2;
                HalPixelFormat.validate(i2);
                videoDecodeAcceleratorImportBufferForPictureParams.handleFd = decoderOutline3.readUntypedHandle(16, false);
                Decoder pointer = decoderOutline3.readPointer(24, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                videoDecodeAcceleratorImportBufferForPictureParams.planes = new VideoFramePlane[dataHeaderForPointerArray.elementsOrVersion];
                for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                    videoDecodeAcceleratorImportBufferForPictureParams.planes[i3] = VideoFramePlane.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer, false));
                }
                if (i >= 4) {
                    videoDecodeAcceleratorImportBufferForPictureParams.modifier = BufferModifier.decode(decoderOutline3.readPointer(32, true));
                }
                return videoDecodeAcceleratorImportBufferForPictureParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.pictureBufferId, 8);
            encoderAtDataOffset.encode(this.format, 12);
            encoderAtDataOffset.encode((Handle) this.handleFd, 16, false);
            VideoFramePlane[] videoFramePlaneArr = this.planes;
            if (videoFramePlaneArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(videoFramePlaneArr.length, 24, -1);
                int i = 0;
                while (true) {
                    VideoFramePlane[] videoFramePlaneArr2 = this.planes;
                    if (i >= videoFramePlaneArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) videoFramePlaneArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(24, false);
            }
            encoderAtDataOffset.encode((Struct) this.modifier, 32, true);
        }

        private VideoDecodeAcceleratorImportBufferForPictureParams(int i) {
            super(40, i);
            this.handleFd = InvalidHandle.INSTANCE;
        }
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.VideoEncodeAccelerator;
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

class VideoEncodeAccelerator_Internal {
    public static final Interface.Manager<VideoEncodeAccelerator, VideoEncodeAccelerator.Proxy> MANAGER = new Interface.Manager<VideoEncodeAccelerator, VideoEncodeAccelerator.Proxy>() { // from class: org.chromium.arc.mojom.VideoEncodeAccelerator_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoEncodeAccelerator[] buildArray(int i) {
            return new VideoEncodeAccelerator[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoEncodeAccelerator.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoEncodeAccelerator> buildStub(Core core, VideoEncodeAccelerator r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoEncodeAccelerator";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 4;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoEncodeAccelerator.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoEncodeAccelerator
        public void encode(int i, UntypedHandle untypedHandle, VideoFramePlane[] videoFramePlaneArr, long j, boolean z, VideoEncodeAccelerator.EncodeResponse encodeResponse) {
            VideoEncodeAcceleratorEncodeParams videoEncodeAcceleratorEncodeParams = new VideoEncodeAcceleratorEncodeParams();
            videoEncodeAcceleratorEncodeParams.format = i;
            videoEncodeAcceleratorEncodeParams.frameFd = untypedHandle;
            videoEncodeAcceleratorEncodeParams.planes = videoFramePlaneArr;
            videoEncodeAcceleratorEncodeParams.timestamp = j;
            videoEncodeAcceleratorEncodeParams.forceKeyframe = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoEncodeAcceleratorEncodeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new VideoEncodeAcceleratorEncodeResponseParamsForwardToCallback(encodeResponse));
        }

        @Override // org.chromium.arc.mojom.VideoEncodeAccelerator
        public void flush(VideoEncodeAccelerator.FlushResponse flushResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VideoEncodeAcceleratorFlushParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new VideoEncodeAcceleratorFlushResponseParamsForwardToCallback(flushResponse));
        }

        @Override // org.chromium.arc.mojom.VideoEncodeAccelerator
        public void getSupportedProfiles(VideoEncodeAccelerator.GetSupportedProfilesResponse getSupportedProfilesResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VideoEncodeAcceleratorGetSupportedProfilesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new VideoEncodeAcceleratorGetSupportedProfilesResponseParamsForwardToCallback(getSupportedProfilesResponse));
        }

        @Override // org.chromium.arc.mojom.VideoEncodeAccelerator
        public void initialize(VideoEncodeAcceleratorConfig videoEncodeAcceleratorConfig, VideoEncodeClient videoEncodeClient, VideoEncodeAccelerator.InitializeResponse initializeResponse) {
            VideoEncodeAcceleratorInitializeParams videoEncodeAcceleratorInitializeParams = new VideoEncodeAcceleratorInitializeParams();
            videoEncodeAcceleratorInitializeParams.config = videoEncodeAcceleratorConfig;
            videoEncodeAcceleratorInitializeParams.client = videoEncodeClient;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoEncodeAcceleratorInitializeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new VideoEncodeAcceleratorInitializeResponseParamsForwardToCallback(initializeResponse));
        }

        @Override // org.chromium.arc.mojom.VideoEncodeAccelerator
        public void initializeDeprecated(VideoEncodeAcceleratorConfig videoEncodeAcceleratorConfig, VideoEncodeClient videoEncodeClient, VideoEncodeAccelerator.InitializeDeprecatedResponse initializeDeprecatedResponse) {
            VideoEncodeAcceleratorInitializeDeprecatedParams videoEncodeAcceleratorInitializeDeprecatedParams = new VideoEncodeAcceleratorInitializeDeprecatedParams();
            videoEncodeAcceleratorInitializeDeprecatedParams.config = videoEncodeAcceleratorConfig;
            videoEncodeAcceleratorInitializeDeprecatedParams.client = videoEncodeClient;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoEncodeAcceleratorInitializeDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new VideoEncodeAcceleratorInitializeDeprecatedResponseParamsForwardToCallback(initializeDeprecatedResponse));
        }

        @Override // org.chromium.arc.mojom.VideoEncodeAccelerator
        public void requestEncodingParametersChange(int i, int i2) {
            VideoEncodeAcceleratorRequestEncodingParametersChangeParams videoEncodeAcceleratorRequestEncodingParametersChangeParams = new VideoEncodeAcceleratorRequestEncodingParametersChangeParams();
            videoEncodeAcceleratorRequestEncodingParametersChangeParams.bitrate = i;
            videoEncodeAcceleratorRequestEncodingParametersChangeParams.framerate = i2;
            getProxyHandler().getMessageReceiver().accept(videoEncodeAcceleratorRequestEncodingParametersChangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.VideoEncodeAccelerator
        public void useBitstreamBuffer(UntypedHandle untypedHandle, int i, int i2, VideoEncodeAccelerator.UseBitstreamBufferResponse useBitstreamBufferResponse) {
            VideoEncodeAcceleratorUseBitstreamBufferParams videoEncodeAcceleratorUseBitstreamBufferParams = new VideoEncodeAcceleratorUseBitstreamBufferParams();
            videoEncodeAcceleratorUseBitstreamBufferParams.shmemFd = untypedHandle;
            videoEncodeAcceleratorUseBitstreamBufferParams.offset = i;
            videoEncodeAcceleratorUseBitstreamBufferParams.size = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(videoEncodeAcceleratorUseBitstreamBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new VideoEncodeAcceleratorUseBitstreamBufferResponseParamsForwardToCallback(useBitstreamBufferResponse));
        }
    }

    static final class Stub extends Interface.Stub<VideoEncodeAccelerator> {
        Stub(Core core, VideoEncodeAccelerator videoEncodeAccelerator) {
            super(core, videoEncodeAccelerator);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoEncodeAccelerator_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 4) {
                    return false;
                }
                VideoEncodeAcceleratorRequestEncodingParametersChangeParams videoEncodeAcceleratorRequestEncodingParametersChangeParamsDeserialize = VideoEncodeAcceleratorRequestEncodingParametersChangeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().requestEncodingParametersChange(videoEncodeAcceleratorRequestEncodingParametersChangeParamsDeserialize.bitrate, videoEncodeAcceleratorRequestEncodingParametersChangeParamsDeserialize.framerate);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoEncodeAccelerator_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    VideoEncodeAcceleratorGetSupportedProfilesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getSupportedProfiles(new VideoEncodeAcceleratorGetSupportedProfilesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 3) {
                    VideoEncodeAcceleratorUseBitstreamBufferParams videoEncodeAcceleratorUseBitstreamBufferParamsDeserialize = VideoEncodeAcceleratorUseBitstreamBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().useBitstreamBuffer(videoEncodeAcceleratorUseBitstreamBufferParamsDeserialize.shmemFd, videoEncodeAcceleratorUseBitstreamBufferParamsDeserialize.offset, videoEncodeAcceleratorUseBitstreamBufferParamsDeserialize.size, new VideoEncodeAcceleratorUseBitstreamBufferResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 5) {
                    VideoEncodeAcceleratorFlushParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().flush(new VideoEncodeAcceleratorFlushResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 7) {
                    VideoEncodeAcceleratorInitializeDeprecatedParams videoEncodeAcceleratorInitializeDeprecatedParamsDeserialize = VideoEncodeAcceleratorInitializeDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().initializeDeprecated(videoEncodeAcceleratorInitializeDeprecatedParamsDeserialize.config, videoEncodeAcceleratorInitializeDeprecatedParamsDeserialize.client, new VideoEncodeAcceleratorInitializeDeprecatedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 8) {
                    VideoEncodeAcceleratorEncodeParams videoEncodeAcceleratorEncodeParamsDeserialize = VideoEncodeAcceleratorEncodeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().encode(videoEncodeAcceleratorEncodeParamsDeserialize.format, videoEncodeAcceleratorEncodeParamsDeserialize.frameFd, videoEncodeAcceleratorEncodeParamsDeserialize.planes, videoEncodeAcceleratorEncodeParamsDeserialize.timestamp, videoEncodeAcceleratorEncodeParamsDeserialize.forceKeyframe, new VideoEncodeAcceleratorEncodeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 9) {
                    return false;
                }
                VideoEncodeAcceleratorInitializeParams videoEncodeAcceleratorInitializeParamsDeserialize = VideoEncodeAcceleratorInitializeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().initialize(videoEncodeAcceleratorInitializeParamsDeserialize.config, videoEncodeAcceleratorInitializeParamsDeserialize.client, new VideoEncodeAcceleratorInitializeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoEncodeAcceleratorEncodeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorEncodeResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class VideoEncodeAcceleratorEncodeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoEncodeAccelerator.EncodeResponse mCallback;

        VideoEncodeAcceleratorEncodeResponseParamsForwardToCallback(VideoEncodeAccelerator.EncodeResponse encodeResponse) {
            this.mCallback = encodeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoEncodeAcceleratorEncodeResponseParamsProxyToResponder implements VideoEncodeAccelerator.EncodeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoEncodeAcceleratorEncodeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new VideoEncodeAcceleratorEncodeResponseParams().serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class VideoEncodeAcceleratorFlushParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorFlushParams() {
            super(8, 0);
        }

        public static VideoEncodeAcceleratorFlushParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VideoEncodeAcceleratorFlushParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VideoEncodeAcceleratorFlushParams(int i) {
            super(8, i);
        }
    }

    static final class VideoEncodeAcceleratorFlushResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean flushDone;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorFlushResponseParams() {
            super(16, 0);
        }

        public static VideoEncodeAcceleratorFlushResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorFlushResponseParams videoEncodeAcceleratorFlushResponseParams = new VideoEncodeAcceleratorFlushResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeAcceleratorFlushResponseParams.flushDone = decoder.readBoolean(8, 0);
                return videoEncodeAcceleratorFlushResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.flushDone, 8, 0);
        }

        private VideoEncodeAcceleratorFlushResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoEncodeAcceleratorFlushResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoEncodeAccelerator.FlushResponse mCallback;

        VideoEncodeAcceleratorFlushResponseParamsForwardToCallback(VideoEncodeAccelerator.FlushResponse flushResponse) {
            this.mCallback = flushResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(VideoEncodeAcceleratorFlushResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).flushDone));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoEncodeAcceleratorFlushResponseParamsProxyToResponder implements VideoEncodeAccelerator.FlushResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoEncodeAcceleratorFlushResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            VideoEncodeAcceleratorFlushResponseParams videoEncodeAcceleratorFlushResponseParams = new VideoEncodeAcceleratorFlushResponseParams();
            videoEncodeAcceleratorFlushResponseParams.flushDone = bool.booleanValue();
            this.mMessageReceiver.accept(videoEncodeAcceleratorFlushResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class VideoEncodeAcceleratorGetSupportedProfilesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorGetSupportedProfilesParams() {
            super(8, 0);
        }

        public static VideoEncodeAcceleratorGetSupportedProfilesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VideoEncodeAcceleratorGetSupportedProfilesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VideoEncodeAcceleratorGetSupportedProfilesParams(int i) {
            super(8, i);
        }
    }

    static final class VideoEncodeAcceleratorGetSupportedProfilesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoEncodeProfile[] profiles;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorGetSupportedProfilesResponseParams() {
            super(16, 0);
        }

        public static VideoEncodeAcceleratorGetSupportedProfilesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                VideoEncodeAcceleratorGetSupportedProfilesResponseParams videoEncodeAcceleratorGetSupportedProfilesResponseParams = new VideoEncodeAcceleratorGetSupportedProfilesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                videoEncodeAcceleratorGetSupportedProfilesResponseParams.profiles = new VideoEncodeProfile[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    videoEncodeAcceleratorGetSupportedProfilesResponseParams.profiles[i] = VideoEncodeProfile.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return videoEncodeAcceleratorGetSupportedProfilesResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            VideoEncodeProfile[] videoEncodeProfileArr = this.profiles;
            if (videoEncodeProfileArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(videoEncodeProfileArr.length, 8, -1);
            int i = 0;
            while (true) {
                VideoEncodeProfile[] videoEncodeProfileArr2 = this.profiles;
                if (i >= videoEncodeProfileArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) videoEncodeProfileArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private VideoEncodeAcceleratorGetSupportedProfilesResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoEncodeAcceleratorGetSupportedProfilesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoEncodeAccelerator.GetSupportedProfilesResponse mCallback;

        VideoEncodeAcceleratorGetSupportedProfilesResponseParamsForwardToCallback(VideoEncodeAccelerator.GetSupportedProfilesResponse getSupportedProfilesResponse) {
            this.mCallback = getSupportedProfilesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(VideoEncodeAcceleratorGetSupportedProfilesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).profiles);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoEncodeAcceleratorGetSupportedProfilesResponseParamsProxyToResponder implements VideoEncodeAccelerator.GetSupportedProfilesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoEncodeAcceleratorGetSupportedProfilesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(VideoEncodeProfile[] videoEncodeProfileArr) {
            VideoEncodeAcceleratorGetSupportedProfilesResponseParams videoEncodeAcceleratorGetSupportedProfilesResponseParams = new VideoEncodeAcceleratorGetSupportedProfilesResponseParams();
            videoEncodeAcceleratorGetSupportedProfilesResponseParams.profiles = videoEncodeProfileArr;
            this.mMessageReceiver.accept(videoEncodeAcceleratorGetSupportedProfilesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class VideoEncodeAcceleratorInitializeDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoEncodeClient client;
        public VideoEncodeAcceleratorConfig config;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorInitializeDeprecatedParams() {
            super(24, 0);
        }

        public static VideoEncodeAcceleratorInitializeDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorInitializeDeprecatedParams videoEncodeAcceleratorInitializeDeprecatedParams = new VideoEncodeAcceleratorInitializeDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeAcceleratorInitializeDeprecatedParams.config = VideoEncodeAcceleratorConfig.decode(decoder.readPointer(8, false));
                videoEncodeAcceleratorInitializeDeprecatedParams.client = (VideoEncodeClient) decoder.readServiceInterface(16, false, VideoEncodeClient.MANAGER);
                return videoEncodeAcceleratorInitializeDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.config, 8, false);
            encoderAtDataOffset.encode(this.client, 16, false, (Interface.Manager<VideoEncodeClient, ?>) VideoEncodeClient.MANAGER);
        }

        private VideoEncodeAcceleratorInitializeDeprecatedParams(int i) {
            super(24, i);
        }
    }

    static final class VideoEncodeAcceleratorInitializeDeprecatedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorInitializeDeprecatedResponseParams() {
            super(16, 0);
        }

        public static VideoEncodeAcceleratorInitializeDeprecatedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorInitializeDeprecatedResponseParams videoEncodeAcceleratorInitializeDeprecatedResponseParams = new VideoEncodeAcceleratorInitializeDeprecatedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeAcceleratorInitializeDeprecatedResponseParams.success = decoder.readBoolean(8, 0);
                return videoEncodeAcceleratorInitializeDeprecatedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private VideoEncodeAcceleratorInitializeDeprecatedResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoEncodeAcceleratorInitializeDeprecatedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoEncodeAccelerator.InitializeDeprecatedResponse mCallback;

        VideoEncodeAcceleratorInitializeDeprecatedResponseParamsForwardToCallback(VideoEncodeAccelerator.InitializeDeprecatedResponse initializeDeprecatedResponse) {
            this.mCallback = initializeDeprecatedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(VideoEncodeAcceleratorInitializeDeprecatedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoEncodeAcceleratorInitializeDeprecatedResponseParamsProxyToResponder implements VideoEncodeAccelerator.InitializeDeprecatedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoEncodeAcceleratorInitializeDeprecatedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            VideoEncodeAcceleratorInitializeDeprecatedResponseParams videoEncodeAcceleratorInitializeDeprecatedResponseParams = new VideoEncodeAcceleratorInitializeDeprecatedResponseParams();
            videoEncodeAcceleratorInitializeDeprecatedResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(videoEncodeAcceleratorInitializeDeprecatedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class VideoEncodeAcceleratorInitializeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public VideoEncodeClient client;
        public VideoEncodeAcceleratorConfig config;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorInitializeParams() {
            super(24, 0);
        }

        public static VideoEncodeAcceleratorInitializeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorInitializeParams videoEncodeAcceleratorInitializeParams = new VideoEncodeAcceleratorInitializeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeAcceleratorInitializeParams.config = VideoEncodeAcceleratorConfig.decode(decoder.readPointer(8, false));
                videoEncodeAcceleratorInitializeParams.client = (VideoEncodeClient) decoder.readServiceInterface(16, false, VideoEncodeClient.MANAGER);
                return videoEncodeAcceleratorInitializeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.config, 8, false);
            encoderAtDataOffset.encode(this.client, 16, false, (Interface.Manager<VideoEncodeClient, ?>) VideoEncodeClient.MANAGER);
        }

        private VideoEncodeAcceleratorInitializeParams(int i) {
            super(24, i);
        }
    }

    static final class VideoEncodeAcceleratorInitializeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorInitializeResponseParams() {
            super(16, 0);
        }

        public static VideoEncodeAcceleratorInitializeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorInitializeResponseParams videoEncodeAcceleratorInitializeResponseParams = new VideoEncodeAcceleratorInitializeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoEncodeAcceleratorInitializeResponseParams.result = i;
                VideoEncodeAccelerator.Result.validate(i);
                return videoEncodeAcceleratorInitializeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private VideoEncodeAcceleratorInitializeResponseParams(int i) {
            super(16, i);
        }
    }

    static class VideoEncodeAcceleratorInitializeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoEncodeAccelerator.InitializeResponse mCallback;

        VideoEncodeAcceleratorInitializeResponseParamsForwardToCallback(VideoEncodeAccelerator.InitializeResponse initializeResponse) {
            this.mCallback = initializeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VideoEncodeAcceleratorInitializeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoEncodeAcceleratorInitializeResponseParamsProxyToResponder implements VideoEncodeAccelerator.InitializeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoEncodeAcceleratorInitializeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VideoEncodeAcceleratorInitializeResponseParams videoEncodeAcceleratorInitializeResponseParams = new VideoEncodeAcceleratorInitializeResponseParams();
            videoEncodeAcceleratorInitializeResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(videoEncodeAcceleratorInitializeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class VideoEncodeAcceleratorRequestEncodingParametersChangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int bitrate;
        public int framerate;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorRequestEncodingParametersChangeParams() {
            super(16, 0);
        }

        public static VideoEncodeAcceleratorRequestEncodingParametersChangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorRequestEncodingParametersChangeParams videoEncodeAcceleratorRequestEncodingParametersChangeParams = new VideoEncodeAcceleratorRequestEncodingParametersChangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeAcceleratorRequestEncodingParametersChangeParams.bitrate = decoder.readInt(8);
                videoEncodeAcceleratorRequestEncodingParametersChangeParams.framerate = decoder.readInt(12);
                return videoEncodeAcceleratorRequestEncodingParametersChangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.bitrate, 8);
            encoderAtDataOffset.encode(this.framerate, 12);
        }

        private VideoEncodeAcceleratorRequestEncodingParametersChangeParams(int i) {
            super(16, i);
        }
    }

    static final class VideoEncodeAcceleratorUseBitstreamBufferResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean keyFrame;
        public int payloadSize;
        public long timestamp;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorUseBitstreamBufferResponseParams() {
            super(24, 0);
        }

        public static VideoEncodeAcceleratorUseBitstreamBufferResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorUseBitstreamBufferResponseParams videoEncodeAcceleratorUseBitstreamBufferResponseParams = new VideoEncodeAcceleratorUseBitstreamBufferResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeAcceleratorUseBitstreamBufferResponseParams.payloadSize = decoder.readInt(8);
                videoEncodeAcceleratorUseBitstreamBufferResponseParams.keyFrame = decoder.readBoolean(12, 0);
                videoEncodeAcceleratorUseBitstreamBufferResponseParams.timestamp = decoder.readLong(16);
                return videoEncodeAcceleratorUseBitstreamBufferResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.payloadSize, 8);
            encoderAtDataOffset.encode(this.keyFrame, 12, 0);
            encoderAtDataOffset.encode(this.timestamp, 16);
        }

        private VideoEncodeAcceleratorUseBitstreamBufferResponseParams(int i) {
            super(24, i);
        }
    }

    static class VideoEncodeAcceleratorUseBitstreamBufferResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VideoEncodeAccelerator.UseBitstreamBufferResponse mCallback;

        VideoEncodeAcceleratorUseBitstreamBufferResponseParamsForwardToCallback(VideoEncodeAccelerator.UseBitstreamBufferResponse useBitstreamBufferResponse) {
            this.mCallback = useBitstreamBufferResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                VideoEncodeAcceleratorUseBitstreamBufferResponseParams videoEncodeAcceleratorUseBitstreamBufferResponseParamsDeserialize = VideoEncodeAcceleratorUseBitstreamBufferResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(videoEncodeAcceleratorUseBitstreamBufferResponseParamsDeserialize.payloadSize), Boolean.valueOf(videoEncodeAcceleratorUseBitstreamBufferResponseParamsDeserialize.keyFrame), Long.valueOf(videoEncodeAcceleratorUseBitstreamBufferResponseParamsDeserialize.timestamp));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VideoEncodeAcceleratorUseBitstreamBufferResponseParamsProxyToResponder implements VideoEncodeAccelerator.UseBitstreamBufferResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VideoEncodeAcceleratorUseBitstreamBufferResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Integer num, Boolean bool, Long l) {
            VideoEncodeAcceleratorUseBitstreamBufferResponseParams videoEncodeAcceleratorUseBitstreamBufferResponseParams = new VideoEncodeAcceleratorUseBitstreamBufferResponseParams();
            videoEncodeAcceleratorUseBitstreamBufferResponseParams.payloadSize = num.intValue();
            videoEncodeAcceleratorUseBitstreamBufferResponseParams.keyFrame = bool.booleanValue();
            videoEncodeAcceleratorUseBitstreamBufferResponseParams.timestamp = l.longValue();
            this.mMessageReceiver.accept(videoEncodeAcceleratorUseBitstreamBufferResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class VideoEncodeAcceleratorEncodeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean forceKeyframe;
        public int format;
        public UntypedHandle frameFd;
        public VideoFramePlane[] planes;
        public long timestamp;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorEncodeParams() {
            super(40, 0);
            this.frameFd = InvalidHandle.INSTANCE;
        }

        public static VideoEncodeAcceleratorEncodeParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                VideoEncodeAcceleratorEncodeParams videoEncodeAcceleratorEncodeParams = new VideoEncodeAcceleratorEncodeParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                videoEncodeAcceleratorEncodeParams.format = i;
                VideoPixelFormat.validate(i);
                videoEncodeAcceleratorEncodeParams.frameFd = decoderOutline3.readUntypedHandle(12, false);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                videoEncodeAcceleratorEncodeParams.planes = new VideoFramePlane[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    videoEncodeAcceleratorEncodeParams.planes[i2] = VideoFramePlane.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                videoEncodeAcceleratorEncodeParams.timestamp = decoderOutline3.readLong(24);
                videoEncodeAcceleratorEncodeParams.forceKeyframe = decoderOutline3.readBoolean(32, 0);
                return videoEncodeAcceleratorEncodeParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.format, 8);
            encoderAtDataOffset.encode((Handle) this.frameFd, 12, false);
            VideoFramePlane[] videoFramePlaneArr = this.planes;
            if (videoFramePlaneArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(videoFramePlaneArr.length, 16, -1);
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
                encoderAtDataOffset.encodeNullPointer(16, false);
            }
            encoderAtDataOffset.encode(this.timestamp, 24);
            encoderAtDataOffset.encode(this.forceKeyframe, 32, 0);
        }

        private VideoEncodeAcceleratorEncodeParams(int i) {
            super(40, i);
            this.frameFd = InvalidHandle.INSTANCE;
        }
    }

    static final class VideoEncodeAcceleratorUseBitstreamBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int offset;
        public UntypedHandle shmemFd;
        public int size;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeAcceleratorUseBitstreamBufferParams() {
            super(24, 0);
            this.shmemFd = InvalidHandle.INSTANCE;
        }

        public static VideoEncodeAcceleratorUseBitstreamBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeAcceleratorUseBitstreamBufferParams videoEncodeAcceleratorUseBitstreamBufferParams = new VideoEncodeAcceleratorUseBitstreamBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeAcceleratorUseBitstreamBufferParams.shmemFd = decoder.readUntypedHandle(8, false);
                videoEncodeAcceleratorUseBitstreamBufferParams.offset = decoder.readInt(12);
                videoEncodeAcceleratorUseBitstreamBufferParams.size = decoder.readInt(16);
                return videoEncodeAcceleratorUseBitstreamBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.shmemFd, 8, false);
            encoderAtDataOffset.encode(this.offset, 12);
            encoderAtDataOffset.encode(this.size, 16);
        }

        private VideoEncodeAcceleratorUseBitstreamBufferParams(int i) {
            super(24, i);
            this.shmemFd = InvalidHandle.INSTANCE;
        }
    }
}

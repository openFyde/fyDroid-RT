// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoDecodeAccelerator;
import org.chromium.arc.mojom.VideoDecodeClient;
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

class VideoDecodeClient_Internal {
    public static final Interface.Manager<VideoDecodeClient, VideoDecodeClient.Proxy> MANAGER = new Interface.Manager<VideoDecodeClient, VideoDecodeClient.Proxy>() { // from class: org.chromium.arc.mojom.VideoDecodeClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecodeClient[] buildArray(int i) {
            return new VideoDecodeClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecodeClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoDecodeClient> buildStub(Core core, VideoDecodeClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoDecodeClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoDecodeClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoDecodeClient
        public void notifyEndOfBitstreamBuffer(int i) {
            VideoDecodeClientNotifyEndOfBitstreamBufferParams videoDecodeClientNotifyEndOfBitstreamBufferParams = new VideoDecodeClientNotifyEndOfBitstreamBufferParams();
            videoDecodeClientNotifyEndOfBitstreamBufferParams.bitstreamId = i;
            getProxyHandler().getMessageReceiver().accept(videoDecodeClientNotifyEndOfBitstreamBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeClient
        public void notifyError(int i) {
            VideoDecodeClientNotifyErrorParams videoDecodeClientNotifyErrorParams = new VideoDecodeClientNotifyErrorParams();
            videoDecodeClientNotifyErrorParams.error = i;
            getProxyHandler().getMessageReceiver().accept(videoDecodeClientNotifyErrorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeClient
        public void pictureReady(Picture picture) {
            VideoDecodeClientPictureReadyParams videoDecodeClientPictureReadyParams = new VideoDecodeClientPictureReadyParams();
            videoDecodeClientPictureReadyParams.picture = picture;
            getProxyHandler().getMessageReceiver().accept(videoDecodeClientPictureReadyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.VideoDecodeClient
        public void providePictureBuffers(PictureBufferFormat pictureBufferFormat, Rect rect) {
            VideoDecodeClientProvidePictureBuffersParams videoDecodeClientProvidePictureBuffersParams = new VideoDecodeClientProvidePictureBuffersParams();
            videoDecodeClientProvidePictureBuffersParams.format = pictureBufferFormat;
            videoDecodeClientProvidePictureBuffersParams.visibleRect = rect;
            getProxyHandler().getMessageReceiver().accept(videoDecodeClientProvidePictureBuffersParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }
    }

    static final class Stub extends Interface.Stub<VideoDecodeClient> {
        Stub(Core core, VideoDecodeClient videoDecodeClient) {
            super(core, videoDecodeClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoDecodeClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 5) {
                    VideoDecodeClientProvidePictureBuffersParams videoDecodeClientProvidePictureBuffersParamsDeserialize = VideoDecodeClientProvidePictureBuffersParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().providePictureBuffers(videoDecodeClientProvidePictureBuffersParamsDeserialize.format, videoDecodeClientProvidePictureBuffersParamsDeserialize.visibleRect);
                    return true;
                }
                if (type == 1) {
                    getImpl().pictureReady(VideoDecodeClientPictureReadyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).picture);
                    return true;
                }
                if (type == 2) {
                    getImpl().notifyEndOfBitstreamBuffer(VideoDecodeClientNotifyEndOfBitstreamBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload()).bitstreamId);
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().notifyError(VideoDecodeClientNotifyErrorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoDecodeClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoDecodeClientNotifyEndOfBitstreamBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int bitstreamId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeClientNotifyEndOfBitstreamBufferParams() {
            super(16, 0);
        }

        public static VideoDecodeClientNotifyEndOfBitstreamBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeClientNotifyEndOfBitstreamBufferParams videoDecodeClientNotifyEndOfBitstreamBufferParams = new VideoDecodeClientNotifyEndOfBitstreamBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecodeClientNotifyEndOfBitstreamBufferParams.bitstreamId = decoder.readInt(8);
                return videoDecodeClientNotifyEndOfBitstreamBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.bitstreamId, 8);
        }

        private VideoDecodeClientNotifyEndOfBitstreamBufferParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecodeClientNotifyErrorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeClientNotifyErrorParams() {
            super(16, 0);
        }

        public static VideoDecodeClientNotifyErrorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeClientNotifyErrorParams videoDecodeClientNotifyErrorParams = new VideoDecodeClientNotifyErrorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoDecodeClientNotifyErrorParams.error = i;
                VideoDecodeAccelerator.Result.validate(i);
                return videoDecodeClientNotifyErrorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private VideoDecodeClientNotifyErrorParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecodeClientPictureReadyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Picture picture;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeClientPictureReadyParams() {
            super(16, 0);
        }

        public static VideoDecodeClientPictureReadyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeClientPictureReadyParams videoDecodeClientPictureReadyParams = new VideoDecodeClientPictureReadyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecodeClientPictureReadyParams.picture = Picture.decode(decoder.readPointer(8, false));
                return videoDecodeClientPictureReadyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.picture, 8, false);
        }

        private VideoDecodeClientPictureReadyParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecodeClientProvidePictureBuffersParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PictureBufferFormat format;
        public Rect visibleRect;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecodeClientProvidePictureBuffersParams() {
            super(24, 0);
        }

        public static VideoDecodeClientProvidePictureBuffersParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecodeClientProvidePictureBuffersParams videoDecodeClientProvidePictureBuffersParams = new VideoDecodeClientProvidePictureBuffersParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecodeClientProvidePictureBuffersParams.format = PictureBufferFormat.decode(decoder.readPointer(8, false));
                videoDecodeClientProvidePictureBuffersParams.visibleRect = Rect.decode(decoder.readPointer(16, false));
                return videoDecodeClientProvidePictureBuffersParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.format, 8, false);
            encoderAtDataOffset.encode((Struct) this.visibleRect, 16, false);
        }

        private VideoDecodeClientProvidePictureBuffersParams(int i) {
            super(24, i);
        }
    }
}

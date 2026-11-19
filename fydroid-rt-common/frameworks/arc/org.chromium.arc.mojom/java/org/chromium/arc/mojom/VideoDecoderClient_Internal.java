// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoDecoderClient;
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

class VideoDecoderClient_Internal {
    public static final Interface.Manager<VideoDecoderClient, VideoDecoderClient.Proxy> MANAGER = new Interface.Manager<VideoDecoderClient, VideoDecoderClient.Proxy>() { // from class: org.chromium.arc.mojom.VideoDecoderClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecoderClient[] buildArray(int i) {
            return new VideoDecoderClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoDecoderClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoDecoderClient> buildStub(Core core, VideoDecoderClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoDecoderClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoDecoderClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoDecoderClient
        public void onError(int i) {
            VideoDecoderClientOnErrorParams videoDecoderClientOnErrorParams = new VideoDecoderClientOnErrorParams();
            videoDecoderClientOnErrorParams.status = i;
            getProxyHandler().getMessageReceiver().accept(videoDecoderClientOnErrorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.VideoDecoderClient
        public void onVideoFrameDecoded(int i, Rect rect, long j) {
            VideoDecoderClientOnVideoFrameDecodedParams videoDecoderClientOnVideoFrameDecodedParams = new VideoDecoderClientOnVideoFrameDecodedParams();
            videoDecoderClientOnVideoFrameDecodedParams.videoFrameId = i;
            videoDecoderClientOnVideoFrameDecodedParams.visibleRect = rect;
            videoDecoderClientOnVideoFrameDecodedParams.timestamp = j;
            getProxyHandler().getMessageReceiver().accept(videoDecoderClientOnVideoFrameDecodedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<VideoDecoderClient> {
        Stub(Core core, VideoDecoderClient videoDecoderClient) {
            super(core, videoDecoderClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoDecoderClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    VideoDecoderClientOnVideoFrameDecodedParams videoDecoderClientOnVideoFrameDecodedParamsDeserialize = VideoDecoderClientOnVideoFrameDecodedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onVideoFrameDecoded(videoDecoderClientOnVideoFrameDecodedParamsDeserialize.videoFrameId, videoDecoderClientOnVideoFrameDecodedParamsDeserialize.visibleRect, videoDecoderClientOnVideoFrameDecodedParamsDeserialize.timestamp);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onError(VideoDecoderClientOnErrorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoDecoderClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoDecoderClientOnErrorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderClientOnErrorParams() {
            super(16, 0);
        }

        public static VideoDecoderClientOnErrorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecoderClientOnErrorParams videoDecoderClientOnErrorParams = new VideoDecoderClientOnErrorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoDecoderClientOnErrorParams.status = i;
                DecoderStatus.validate(i);
                return videoDecoderClientOnErrorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private VideoDecoderClientOnErrorParams(int i) {
            super(16, i);
        }
    }

    static final class VideoDecoderClientOnVideoFrameDecodedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long timestamp;
        public int videoFrameId;
        public Rect visibleRect;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoDecoderClientOnVideoFrameDecodedParams() {
            super(32, 0);
        }

        public static VideoDecoderClientOnVideoFrameDecodedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoDecoderClientOnVideoFrameDecodedParams videoDecoderClientOnVideoFrameDecodedParams = new VideoDecoderClientOnVideoFrameDecodedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoDecoderClientOnVideoFrameDecodedParams.videoFrameId = decoder.readInt(8);
                videoDecoderClientOnVideoFrameDecodedParams.visibleRect = Rect.decode(decoder.readPointer(16, false));
                videoDecoderClientOnVideoFrameDecodedParams.timestamp = decoder.readLong(24);
                return videoDecoderClientOnVideoFrameDecodedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.videoFrameId, 8);
            encoderAtDataOffset.encode((Struct) this.visibleRect, 16, false);
            encoderAtDataOffset.encode(this.timestamp, 24);
        }

        private VideoDecoderClientOnVideoFrameDecodedParams(int i) {
            super(32, i);
        }
    }
}

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoEncodeAccelerator;
import org.chromium.arc.mojom.VideoEncodeClient;
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

class VideoEncodeClient_Internal {
    public static final Interface.Manager<VideoEncodeClient, VideoEncodeClient.Proxy> MANAGER = new Interface.Manager<VideoEncodeClient, VideoEncodeClient.Proxy>() { // from class: org.chromium.arc.mojom.VideoEncodeClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoEncodeClient[] buildArray(int i) {
            return new VideoEncodeClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoEncodeClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoEncodeClient> buildStub(Core core, VideoEncodeClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoEncodeClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoEncodeClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoEncodeClient
        public void notifyError(int i) {
            VideoEncodeClientNotifyErrorParams videoEncodeClientNotifyErrorParams = new VideoEncodeClientNotifyErrorParams();
            videoEncodeClientNotifyErrorParams.error = i;
            getProxyHandler().getMessageReceiver().accept(videoEncodeClientNotifyErrorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.VideoEncodeClient
        public void requireBitstreamBuffers(int i, Size size, int i2) {
            VideoEncodeClientRequireBitstreamBuffersParams videoEncodeClientRequireBitstreamBuffersParams = new VideoEncodeClientRequireBitstreamBuffersParams();
            videoEncodeClientRequireBitstreamBuffersParams.inputCount = i;
            videoEncodeClientRequireBitstreamBuffersParams.inputCodedSize = size;
            videoEncodeClientRequireBitstreamBuffersParams.outputBufferSize = i2;
            getProxyHandler().getMessageReceiver().accept(videoEncodeClientRequireBitstreamBuffersParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<VideoEncodeClient> {
        Stub(Core core, VideoEncodeClient videoEncodeClient) {
            super(core, videoEncodeClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoEncodeClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    VideoEncodeClientRequireBitstreamBuffersParams videoEncodeClientRequireBitstreamBuffersParamsDeserialize = VideoEncodeClientRequireBitstreamBuffersParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requireBitstreamBuffers(videoEncodeClientRequireBitstreamBuffersParamsDeserialize.inputCount, videoEncodeClientRequireBitstreamBuffersParamsDeserialize.inputCodedSize, videoEncodeClientRequireBitstreamBuffersParamsDeserialize.outputBufferSize);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().notifyError(VideoEncodeClientNotifyErrorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoEncodeClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoEncodeClientNotifyErrorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeClientNotifyErrorParams() {
            super(16, 0);
        }

        public static VideoEncodeClientNotifyErrorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeClientNotifyErrorParams videoEncodeClientNotifyErrorParams = new VideoEncodeClientNotifyErrorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                videoEncodeClientNotifyErrorParams.error = i;
                VideoEncodeAccelerator.Error.validate(i);
                return videoEncodeClientNotifyErrorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private VideoEncodeClientNotifyErrorParams(int i) {
            super(16, i);
        }
    }

    static final class VideoEncodeClientRequireBitstreamBuffersParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Size inputCodedSize;
        public int inputCount;
        public int outputBufferSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoEncodeClientRequireBitstreamBuffersParams() {
            super(24, 0);
        }

        public static VideoEncodeClientRequireBitstreamBuffersParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoEncodeClientRequireBitstreamBuffersParams videoEncodeClientRequireBitstreamBuffersParams = new VideoEncodeClientRequireBitstreamBuffersParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoEncodeClientRequireBitstreamBuffersParams.inputCount = decoder.readInt(8);
                videoEncodeClientRequireBitstreamBuffersParams.outputBufferSize = decoder.readInt(12);
                videoEncodeClientRequireBitstreamBuffersParams.inputCodedSize = Size.decode(decoder.readPointer(16, false));
                return videoEncodeClientRequireBitstreamBuffersParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.inputCount, 8);
            encoderAtDataOffset.encode(this.outputBufferSize, 12);
            encoderAtDataOffset.encode((Struct) this.inputCodedSize, 16, false);
        }

        private VideoEncodeClientRequireBitstreamBuffersParams(int i) {
            super(24, i);
        }
    }
}

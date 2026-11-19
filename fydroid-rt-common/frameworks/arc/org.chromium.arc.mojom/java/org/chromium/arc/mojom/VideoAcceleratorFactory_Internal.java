package org.chromium.arc.mojom;

import org.chromium.arc.mojom.VideoAcceleratorFactory;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class VideoAcceleratorFactory_Internal {
    public static final Interface.Manager<VideoAcceleratorFactory, VideoAcceleratorFactory.Proxy> MANAGER = new Interface.Manager<VideoAcceleratorFactory, VideoAcceleratorFactory.Proxy>() { // from class: org.chromium.arc.mojom.VideoAcceleratorFactory_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoAcceleratorFactory[] buildArray(int i) {
            return new VideoAcceleratorFactory[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VideoAcceleratorFactory.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VideoAcceleratorFactory> buildStub(Core core, VideoAcceleratorFactory r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.VideoAcceleratorFactory";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 9;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VideoAcceleratorFactory.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.VideoAcceleratorFactory
        public void createDecodeAccelerator(InterfaceRequest<VideoDecodeAccelerator> interfaceRequest) {
            VideoAcceleratorFactoryCreateDecodeAcceleratorParams videoAcceleratorFactoryCreateDecodeAcceleratorParams = new VideoAcceleratorFactoryCreateDecodeAcceleratorParams();
            videoAcceleratorFactoryCreateDecodeAcceleratorParams.videoDecoder = interfaceRequest;
            getProxyHandler().getMessageReceiver().accept(videoAcceleratorFactoryCreateDecodeAcceleratorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.VideoAcceleratorFactory
        public void createEncodeAccelerator(InterfaceRequest<VideoEncodeAccelerator> interfaceRequest) {
            VideoAcceleratorFactoryCreateEncodeAcceleratorParams videoAcceleratorFactoryCreateEncodeAcceleratorParams = new VideoAcceleratorFactoryCreateEncodeAcceleratorParams();
            videoAcceleratorFactoryCreateEncodeAcceleratorParams.videoEncoder = interfaceRequest;
            getProxyHandler().getMessageReceiver().accept(videoAcceleratorFactoryCreateEncodeAcceleratorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.VideoAcceleratorFactory
        public void createProtectedBufferAllocator(InterfaceRequest<VideoProtectedBufferAllocator> interfaceRequest) {
            VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams videoAcceleratorFactoryCreateProtectedBufferAllocatorParams = new VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams();
            videoAcceleratorFactoryCreateProtectedBufferAllocatorParams.videoProtectedBufferAllocator = interfaceRequest;
            getProxyHandler().getMessageReceiver().accept(videoAcceleratorFactoryCreateProtectedBufferAllocatorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.VideoAcceleratorFactory
        public void createVideoDecoder(InterfaceRequest<VideoDecoder> interfaceRequest) {
            VideoAcceleratorFactoryCreateVideoDecoderParams videoAcceleratorFactoryCreateVideoDecoderParams = new VideoAcceleratorFactoryCreateVideoDecoderParams();
            videoAcceleratorFactoryCreateVideoDecoderParams.videoDecoder = interfaceRequest;
            getProxyHandler().getMessageReceiver().accept(videoAcceleratorFactoryCreateVideoDecoderParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }
    }

    static final class Stub extends Interface.Stub<VideoAcceleratorFactory> {
        Stub(Core core, VideoAcceleratorFactory videoAcceleratorFactory) {
            super(core, videoAcceleratorFactory);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VideoAcceleratorFactory_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    getImpl().createEncodeAccelerator(VideoAcceleratorFactoryCreateEncodeAcceleratorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).videoEncoder);
                    return true;
                }
                if (type == 2) {
                    getImpl().createDecodeAccelerator(VideoAcceleratorFactoryCreateDecodeAcceleratorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).videoDecoder);
                    return true;
                }
                if (type == 3) {
                    getImpl().createProtectedBufferAllocator(VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).videoProtectedBufferAllocator);
                    return true;
                }
                if (type != 4) {
                    return false;
                }
                getImpl().createVideoDecoder(VideoAcceleratorFactoryCreateVideoDecoderParams.deserialize(serviceMessageAsServiceMessage.getPayload()).videoDecoder);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VideoAcceleratorFactory_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VideoAcceleratorFactoryCreateDecodeAcceleratorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<VideoDecodeAccelerator> videoDecoder;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoAcceleratorFactoryCreateDecodeAcceleratorParams() {
            super(16, 0);
        }

        public static VideoAcceleratorFactoryCreateDecodeAcceleratorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoAcceleratorFactoryCreateDecodeAcceleratorParams videoAcceleratorFactoryCreateDecodeAcceleratorParams = new VideoAcceleratorFactoryCreateDecodeAcceleratorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoAcceleratorFactoryCreateDecodeAcceleratorParams.videoDecoder = decoder.readInterfaceRequest(8, false);
                return videoAcceleratorFactoryCreateDecodeAcceleratorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((InterfaceRequest) this.videoDecoder, 8, false);
        }

        private VideoAcceleratorFactoryCreateDecodeAcceleratorParams(int i) {
            super(16, i);
        }
    }

    static final class VideoAcceleratorFactoryCreateEncodeAcceleratorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<VideoEncodeAccelerator> videoEncoder;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoAcceleratorFactoryCreateEncodeAcceleratorParams() {
            super(16, 0);
        }

        public static VideoAcceleratorFactoryCreateEncodeAcceleratorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoAcceleratorFactoryCreateEncodeAcceleratorParams videoAcceleratorFactoryCreateEncodeAcceleratorParams = new VideoAcceleratorFactoryCreateEncodeAcceleratorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoAcceleratorFactoryCreateEncodeAcceleratorParams.videoEncoder = decoder.readInterfaceRequest(8, false);
                return videoAcceleratorFactoryCreateEncodeAcceleratorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((InterfaceRequest) this.videoEncoder, 8, false);
        }

        private VideoAcceleratorFactoryCreateEncodeAcceleratorParams(int i) {
            super(16, i);
        }
    }

    static final class VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<VideoProtectedBufferAllocator> videoProtectedBufferAllocator;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams() {
            super(16, 0);
        }

        public static VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams videoAcceleratorFactoryCreateProtectedBufferAllocatorParams = new VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoAcceleratorFactoryCreateProtectedBufferAllocatorParams.videoProtectedBufferAllocator = decoder.readInterfaceRequest(8, false);
                return videoAcceleratorFactoryCreateProtectedBufferAllocatorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((InterfaceRequest) this.videoProtectedBufferAllocator, 8, false);
        }

        private VideoAcceleratorFactoryCreateProtectedBufferAllocatorParams(int i) {
            super(16, i);
        }
    }

    static final class VideoAcceleratorFactoryCreateVideoDecoderParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<VideoDecoder> videoDecoder;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VideoAcceleratorFactoryCreateVideoDecoderParams() {
            super(16, 0);
        }

        public static VideoAcceleratorFactoryCreateVideoDecoderParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VideoAcceleratorFactoryCreateVideoDecoderParams videoAcceleratorFactoryCreateVideoDecoderParams = new VideoAcceleratorFactoryCreateVideoDecoderParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                videoAcceleratorFactoryCreateVideoDecoderParams.videoDecoder = decoder.readInterfaceRequest(8, false);
                return videoAcceleratorFactoryCreateVideoDecoderParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((InterfaceRequest) this.videoDecoder, 8, false);
        }

        private VideoAcceleratorFactoryCreateVideoDecoderParams(int i) {
            super(16, i);
        }
    }
}

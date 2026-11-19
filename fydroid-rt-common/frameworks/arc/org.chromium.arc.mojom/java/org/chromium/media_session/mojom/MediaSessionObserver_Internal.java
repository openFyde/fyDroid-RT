package org.chromium.media_session.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.util.HashMap;
import java.util.Map;
import org.chromium.media_session.mojom.MediaSessionObserver;
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

class MediaSessionObserver_Internal {
    public static final Interface.Manager<MediaSessionObserver, MediaSessionObserver.Proxy> MANAGER = new Interface.Manager<MediaSessionObserver, MediaSessionObserver.Proxy>() { // from class: org.chromium.media_session.mojom.MediaSessionObserver_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MediaSessionObserver[] buildArray(int i) {
            return new MediaSessionObserver[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MediaSessionObserver.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MediaSessionObserver> buildStub(Core core, MediaSessionObserver r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "media_session.mojom.MediaSessionObserver";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class MediaSessionObserverMediaSessionActionsChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int[] action;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionObserverMediaSessionActionsChangedParams() {
            super(16, 0);
        }

        public static MediaSessionObserverMediaSessionActionsChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionObserverMediaSessionActionsChangedParams mediaSessionObserverMediaSessionActionsChangedParams = new MediaSessionObserverMediaSessionActionsChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionObserverMediaSessionActionsChangedParams.action = decoder.readInts(8, 0, -1);
                for (int i = 0; i < mediaSessionObserverMediaSessionActionsChangedParams.action.length; i++) {
                    MediaSessionAction.validate(mediaSessionObserverMediaSessionActionsChangedParams.action[i]);
                }
                return mediaSessionObserverMediaSessionActionsChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.action, 8, 0, -1);
        }

        private MediaSessionObserverMediaSessionActionsChangedParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionObserverMediaSessionImagesChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Map<Integer, MediaImage[]> images;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionObserverMediaSessionImagesChangedParams() {
            super(16, 0);
        }

        public static MediaSessionObserverMediaSessionImagesChangedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                MediaSessionObserverMediaSessionImagesChangedParams mediaSessionObserverMediaSessionImagesChangedParams = new MediaSessionObserverMediaSessionImagesChangedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                pointer.readDataHeaderForMap();
                int[] ints = pointer.readInts(8, 0, -1);
                for (int i : ints) {
                    MediaSessionImageType.validate(i);
                }
                Decoder pointer2 = pointer.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(ints.length);
                MediaImage[][] mediaImageArr = new MediaImage[dataHeaderForPointerArray.elementsOrVersion][];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    Decoder decoderOutline2 = GeneratedOutlineSupport.outline2(i2, 8, 8, pointer2, false);
                    DataHeader dataHeaderForPointerArray2 = decoderOutline2.readDataHeaderForPointerArray(-1);
                    mediaImageArr[i2] = new MediaImage[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                        mediaImageArr[i2][i3] = MediaImage.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, decoderOutline2, false));
                    }
                }
                mediaSessionObserverMediaSessionImagesChangedParams.images = new HashMap();
                for (int i4 = 0; i4 < ints.length; i4++) {
                    mediaSessionObserverMediaSessionImagesChangedParams.images.put(Integer.valueOf(ints[i4]), mediaImageArr[i4]);
                }
                return mediaSessionObserverMediaSessionImagesChangedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            if (this.images == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(8);
            int size = this.images.size();
            int[] iArr = new int[size];
            MediaImage[][] mediaImageArr = new MediaImage[size][];
            int i = 0;
            for (Map.Entry<Integer, MediaImage[]> entry : this.images.entrySet()) {
                iArr[i] = entry.getKey().intValue();
                mediaImageArr[i] = entry.getValue();
                i++;
            }
            encoderEncoderForMap.encode(iArr, 8, 0, -1);
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 16, -1);
            for (int i2 = 0; i2 < size; i2++) {
                if (mediaImageArr[i2] == null) {
                    encoderEncodePointerArray.encodeNullPointer((i2 * 8) + 8, false);
                } else {
                    Encoder encoderEncodePointerArray2 = encoderEncodePointerArray.encodePointerArray(mediaImageArr[i2].length, (i2 * 8) + 8, -1);
                    for (int i3 = 0; i3 < mediaImageArr[i2].length; i3++) {
                        encoderEncodePointerArray2.encode((Struct) mediaImageArr[i2][i3], (i3 * 8) + 8, false);
                    }
                }
            }
        }

        private MediaSessionObserverMediaSessionImagesChangedParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionObserverMediaSessionInfoChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionInfo info;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionObserverMediaSessionInfoChangedParams() {
            super(16, 0);
        }

        public static MediaSessionObserverMediaSessionInfoChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionObserverMediaSessionInfoChangedParams mediaSessionObserverMediaSessionInfoChangedParams = new MediaSessionObserverMediaSessionInfoChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionObserverMediaSessionInfoChangedParams.info = MediaSessionInfo.decode(decoder.readPointer(8, false));
                return mediaSessionObserverMediaSessionInfoChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.info, 8, false);
        }

        private MediaSessionObserverMediaSessionInfoChangedParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionObserverMediaSessionMetadataChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaMetadata metadata;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionObserverMediaSessionMetadataChangedParams() {
            super(16, 0);
        }

        public static MediaSessionObserverMediaSessionMetadataChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionObserverMediaSessionMetadataChangedParams mediaSessionObserverMediaSessionMetadataChangedParams = new MediaSessionObserverMediaSessionMetadataChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionObserverMediaSessionMetadataChangedParams.metadata = MediaMetadata.decode(decoder.readPointer(8, true));
                return mediaSessionObserverMediaSessionMetadataChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.metadata, 8, true);
        }

        private MediaSessionObserverMediaSessionMetadataChangedParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionObserverMediaSessionPositionChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaPosition position;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionObserverMediaSessionPositionChangedParams() {
            super(16, 0);
        }

        public static MediaSessionObserverMediaSessionPositionChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionObserverMediaSessionPositionChangedParams mediaSessionObserverMediaSessionPositionChangedParams = new MediaSessionObserverMediaSessionPositionChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionObserverMediaSessionPositionChangedParams.position = MediaPosition.decode(decoder.readPointer(8, true));
                return mediaSessionObserverMediaSessionPositionChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.position, 8, true);
        }

        private MediaSessionObserverMediaSessionPositionChangedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MediaSessionObserver.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.media_session.mojom.MediaSessionObserver
        public void mediaSessionActionsChanged(int[] iArr) {
            MediaSessionObserverMediaSessionActionsChangedParams mediaSessionObserverMediaSessionActionsChangedParams = new MediaSessionObserverMediaSessionActionsChangedParams();
            mediaSessionObserverMediaSessionActionsChangedParams.action = iArr;
            getProxyHandler().getMessageReceiver().accept(mediaSessionObserverMediaSessionActionsChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.media_session.mojom.MediaSessionObserver
        public void mediaSessionImagesChanged(Map<Integer, MediaImage[]> map) {
            MediaSessionObserverMediaSessionImagesChangedParams mediaSessionObserverMediaSessionImagesChangedParams = new MediaSessionObserverMediaSessionImagesChangedParams();
            mediaSessionObserverMediaSessionImagesChangedParams.images = map;
            getProxyHandler().getMessageReceiver().accept(mediaSessionObserverMediaSessionImagesChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.media_session.mojom.MediaSessionObserver
        public void mediaSessionInfoChanged(MediaSessionInfo mediaSessionInfo) {
            MediaSessionObserverMediaSessionInfoChangedParams mediaSessionObserverMediaSessionInfoChangedParams = new MediaSessionObserverMediaSessionInfoChangedParams();
            mediaSessionObserverMediaSessionInfoChangedParams.info = mediaSessionInfo;
            getProxyHandler().getMessageReceiver().accept(mediaSessionObserverMediaSessionInfoChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.media_session.mojom.MediaSessionObserver
        public void mediaSessionMetadataChanged(MediaMetadata mediaMetadata) {
            MediaSessionObserverMediaSessionMetadataChangedParams mediaSessionObserverMediaSessionMetadataChangedParams = new MediaSessionObserverMediaSessionMetadataChangedParams();
            mediaSessionObserverMediaSessionMetadataChangedParams.metadata = mediaMetadata;
            getProxyHandler().getMessageReceiver().accept(mediaSessionObserverMediaSessionMetadataChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.media_session.mojom.MediaSessionObserver
        public void mediaSessionPositionChanged(MediaPosition mediaPosition) {
            MediaSessionObserverMediaSessionPositionChangedParams mediaSessionObserverMediaSessionPositionChangedParams = new MediaSessionObserverMediaSessionPositionChangedParams();
            mediaSessionObserverMediaSessionPositionChangedParams.position = mediaPosition;
            getProxyHandler().getMessageReceiver().accept(mediaSessionObserverMediaSessionPositionChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }
    }

    static final class Stub extends Interface.Stub<MediaSessionObserver> {
        Stub(Core core, MediaSessionObserver mediaSessionObserver) {
            super(core, mediaSessionObserver);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(MediaSessionObserver_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().mediaSessionInfoChanged(MediaSessionObserverMediaSessionInfoChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).info);
                    return true;
                }
                if (type == 1) {
                    getImpl().mediaSessionMetadataChanged(MediaSessionObserverMediaSessionMetadataChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).metadata);
                    return true;
                }
                if (type == 2) {
                    getImpl().mediaSessionActionsChanged(MediaSessionObserverMediaSessionActionsChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).action);
                    return true;
                }
                if (type == 3) {
                    getImpl().mediaSessionImagesChanged(MediaSessionObserverMediaSessionImagesChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).images);
                    return true;
                }
                if (type != 4) {
                    return false;
                }
                getImpl().mediaSessionPositionChanged(MediaSessionObserverMediaSessionPositionChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).position);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MediaSessionObserver_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

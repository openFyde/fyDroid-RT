package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.PrivacyItemsInstance;
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

class PrivacyItemsInstance_Internal {
    public static final Interface.Manager<PrivacyItemsInstance, PrivacyItemsInstance.Proxy> MANAGER = new Interface.Manager<PrivacyItemsInstance, PrivacyItemsInstance.Proxy>() { // from class: org.chromium.arc.mojom.PrivacyItemsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrivacyItemsInstance[] buildArray(int i) {
            return new PrivacyItemsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrivacyItemsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PrivacyItemsInstance> buildStub(Core core, PrivacyItemsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PrivacyItemsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class PrivacyItemsInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PrivacyItemsHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrivacyItemsInstanceInitParams() {
            super(16, 0);
        }

        public static PrivacyItemsInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PrivacyItemsInstanceInitParams privacyItemsInstanceInitParams = new PrivacyItemsInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                privacyItemsInstanceInitParams.hostRemote = (PrivacyItemsHost) decoder.readServiceInterface(8, false, PrivacyItemsHost.MANAGER);
                return privacyItemsInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<PrivacyItemsHost, ?>) PrivacyItemsHost.MANAGER);
        }

        private PrivacyItemsInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class PrivacyItemsInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrivacyItemsInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class PrivacyItemsInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PrivacyItemsInstance.InitResponse mCallback;

        PrivacyItemsInstanceInitResponseParamsForwardToCallback(PrivacyItemsInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PrivacyItemsInstanceInitResponseParamsProxyToResponder implements PrivacyItemsInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PrivacyItemsInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new PrivacyItemsInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Rect[] bounds;
        public int displayId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams() {
            super(24, 0);
        }

        public static PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams = new PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams.displayId = decoderOutline3.readInt(8);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams.bounds = new Rect[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams.bounds[i] = Rect.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.displayId, 8);
            Rect[] rectArr = this.bounds;
            if (rectArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(rectArr.length, 16, -1);
            int i = 0;
            while (true) {
                Rect[] rectArr2 = this.bounds;
                if (i >= rectArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) rectArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PrivacyItemsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PrivacyItemsInstance
        public void init(PrivacyItemsHost privacyItemsHost, PrivacyItemsInstance.InitResponse initResponse) {
            PrivacyItemsInstanceInitParams privacyItemsInstanceInitParams = new PrivacyItemsInstanceInitParams();
            privacyItemsInstanceInitParams.hostRemote = privacyItemsHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(privacyItemsInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new PrivacyItemsInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.PrivacyItemsInstance
        public void onStaticPrivacyIndicatorBoundsChanged(int i, Rect[] rectArr) {
            PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams = new PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams();
            privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams.displayId = i;
            privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams.bounds = rectArr;
            getProxyHandler().getMessageReceiver().accept(privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<PrivacyItemsInstance> {
        Stub(Core core, PrivacyItemsInstance privacyItemsInstance) {
            super(core, privacyItemsInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PrivacyItemsInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParamsDeserialize = PrivacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onStaticPrivacyIndicatorBoundsChanged(privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParamsDeserialize.displayId, privacyItemsInstanceOnStaticPrivacyIndicatorBoundsChangedParamsDeserialize.bounds);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PrivacyItemsInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().init(PrivacyItemsInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new PrivacyItemsInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

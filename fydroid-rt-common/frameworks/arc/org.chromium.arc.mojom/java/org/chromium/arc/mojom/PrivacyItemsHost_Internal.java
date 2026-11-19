package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.PrivacyItemsHost;
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

class PrivacyItemsHost_Internal {
    public static final Interface.Manager<PrivacyItemsHost, PrivacyItemsHost.Proxy> MANAGER = new Interface.Manager<PrivacyItemsHost, PrivacyItemsHost.Proxy>() { // from class: org.chromium.arc.mojom.PrivacyItemsHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrivacyItemsHost[] buildArray(int i) {
            return new PrivacyItemsHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrivacyItemsHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PrivacyItemsHost> buildStub(Core core, PrivacyItemsHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PrivacyItemsHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class PrivacyItemsHostOnLocationIndicatorRequirementChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean flag;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrivacyItemsHostOnLocationIndicatorRequirementChangedParams() {
            super(16, 0);
        }

        public static PrivacyItemsHostOnLocationIndicatorRequirementChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PrivacyItemsHostOnLocationIndicatorRequirementChangedParams privacyItemsHostOnLocationIndicatorRequirementChangedParams = new PrivacyItemsHostOnLocationIndicatorRequirementChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                privacyItemsHostOnLocationIndicatorRequirementChangedParams.flag = decoder.readBoolean(8, 0);
                return privacyItemsHostOnLocationIndicatorRequirementChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.flag, 8, 0);
        }

        private PrivacyItemsHostOnLocationIndicatorRequirementChangedParams(int i) {
            super(16, i);
        }
    }

    static final class PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean flag;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams() {
            super(16, 0);
        }

        public static PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams privacyItemsHostOnMicCameraIndicatorRequirementChangedParams = new PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                privacyItemsHostOnMicCameraIndicatorRequirementChangedParams.flag = decoder.readBoolean(8, 0);
                return privacyItemsHostOnMicCameraIndicatorRequirementChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.flag, 8, 0);
        }

        private PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams(int i) {
            super(16, i);
        }
    }

    static final class PrivacyItemsHostOnPrivacyItemsChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PrivacyItem[] privacyItems;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrivacyItemsHostOnPrivacyItemsChangedParams() {
            super(16, 0);
        }

        public static PrivacyItemsHostOnPrivacyItemsChangedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                PrivacyItemsHostOnPrivacyItemsChangedParams privacyItemsHostOnPrivacyItemsChangedParams = new PrivacyItemsHostOnPrivacyItemsChangedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                privacyItemsHostOnPrivacyItemsChangedParams.privacyItems = new PrivacyItem[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    privacyItemsHostOnPrivacyItemsChangedParams.privacyItems[i] = PrivacyItem.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return privacyItemsHostOnPrivacyItemsChangedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            PrivacyItem[] privacyItemArr = this.privacyItems;
            if (privacyItemArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(privacyItemArr.length, 8, -1);
            int i = 0;
            while (true) {
                PrivacyItem[] privacyItemArr2 = this.privacyItems;
                if (i >= privacyItemArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) privacyItemArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private PrivacyItemsHostOnPrivacyItemsChangedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PrivacyItemsHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PrivacyItemsHost
        public void onLocationIndicatorRequirementChanged(boolean z) {
            PrivacyItemsHostOnLocationIndicatorRequirementChangedParams privacyItemsHostOnLocationIndicatorRequirementChangedParams = new PrivacyItemsHostOnLocationIndicatorRequirementChangedParams();
            privacyItemsHostOnLocationIndicatorRequirementChangedParams.flag = z;
            getProxyHandler().getMessageReceiver().accept(privacyItemsHostOnLocationIndicatorRequirementChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.PrivacyItemsHost
        public void onMicCameraIndicatorRequirementChanged(boolean z) {
            PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams privacyItemsHostOnMicCameraIndicatorRequirementChangedParams = new PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams();
            privacyItemsHostOnMicCameraIndicatorRequirementChangedParams.flag = z;
            getProxyHandler().getMessageReceiver().accept(privacyItemsHostOnMicCameraIndicatorRequirementChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.PrivacyItemsHost
        public void onPrivacyItemsChanged(PrivacyItem[] privacyItemArr) {
            PrivacyItemsHostOnPrivacyItemsChangedParams privacyItemsHostOnPrivacyItemsChangedParams = new PrivacyItemsHostOnPrivacyItemsChangedParams();
            privacyItemsHostOnPrivacyItemsChangedParams.privacyItems = privacyItemArr;
            getProxyHandler().getMessageReceiver().accept(privacyItemsHostOnPrivacyItemsChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<PrivacyItemsHost> {
        Stub(Core core, PrivacyItemsHost privacyItemsHost) {
            super(core, privacyItemsHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PrivacyItemsHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onPrivacyItemsChanged(PrivacyItemsHostOnPrivacyItemsChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).privacyItems);
                    return true;
                }
                if (type == 1) {
                    getImpl().onMicCameraIndicatorRequirementChanged(PrivacyItemsHostOnMicCameraIndicatorRequirementChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).flag);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().onLocationIndicatorRequirementChanged(PrivacyItemsHostOnLocationIndicatorRequirementChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).flag);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PrivacyItemsHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

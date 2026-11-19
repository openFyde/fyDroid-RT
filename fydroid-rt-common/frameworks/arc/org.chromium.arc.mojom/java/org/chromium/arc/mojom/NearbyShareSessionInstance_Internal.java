package org.chromium.arc.mojom;

import org.chromium.arc.mojom.NearbyShareSessionInstance;
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

class NearbyShareSessionInstance_Internal {
    public static final Interface.Manager<NearbyShareSessionInstance, NearbyShareSessionInstance.Proxy> MANAGER = new Interface.Manager<NearbyShareSessionInstance, NearbyShareSessionInstance.Proxy>() { // from class: org.chromium.arc.mojom.NearbyShareSessionInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NearbyShareSessionInstance[] buildArray(int i) {
            return new NearbyShareSessionInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NearbyShareSessionInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<NearbyShareSessionInstance> buildStub(Core core, NearbyShareSessionInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.NearbyShareSessionInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class NearbyShareSessionInstanceOnNearbyShareViewClosedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NearbyShareSessionInstanceOnNearbyShareViewClosedParams() {
            super(8, 0);
        }

        public static NearbyShareSessionInstanceOnNearbyShareViewClosedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NearbyShareSessionInstanceOnNearbyShareViewClosedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NearbyShareSessionInstanceOnNearbyShareViewClosedParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements NearbyShareSessionInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.NearbyShareSessionInstance
        public void onNearbyShareViewClosed() {
            getProxyHandler().getMessageReceiver().accept(new NearbyShareSessionInstanceOnNearbyShareViewClosedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<NearbyShareSessionInstance> {
        Stub(Core core, NearbyShareSessionInstance nearbyShareSessionInstance) {
            super(core, nearbyShareSessionInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(NearbyShareSessionInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                NearbyShareSessionInstanceOnNearbyShareViewClosedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onNearbyShareViewClosed();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), NearbyShareSessionInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

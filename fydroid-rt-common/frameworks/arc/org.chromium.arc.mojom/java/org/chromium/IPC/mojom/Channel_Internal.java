package org.chromium.IPC.mojom;

import org.chromium.IPC.mojom.Channel;
import org.chromium.mojo.bindings.AssociatedInterfaceRequestNotSupported;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class Channel_Internal {
    public static final Interface.Manager<Channel, Channel.Proxy> MANAGER = new Interface.Manager<Channel, Channel.Proxy>() { // from class: org.chromium.IPC.mojom.Channel_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Channel[] buildArray(int i) {
            return new Channel[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Channel.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<Channel> buildStub(Core core, Channel r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "IPC.mojom.Channel";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ChannelGetAssociatedInterfaceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String name;
        public AssociatedInterfaceRequestNotSupported request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ChannelGetAssociatedInterfaceParams() {
            super(24, 0);
        }

        public static ChannelGetAssociatedInterfaceParams deserialize(org.chromium.mojo.bindings.Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ChannelGetAssociatedInterfaceParams channelGetAssociatedInterfaceParams = new ChannelGetAssociatedInterfaceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                channelGetAssociatedInterfaceParams.name = decoder.readString(8, false);
                channelGetAssociatedInterfaceParams.request = decoder.readAssociatedInterfaceRequestNotSupported(16, false);
                return channelGetAssociatedInterfaceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.name, 8, false);
            encoderAtDataOffset.encode(this.request, 16, false);
        }

        private ChannelGetAssociatedInterfaceParams(int i) {
            super(24, i);
        }
    }

    static final class ChannelReceiveParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Message message;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ChannelReceiveParams() {
            super(16, 0);
        }

        public static ChannelReceiveParams deserialize(org.chromium.mojo.bindings.Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ChannelReceiveParams channelReceiveParams = new ChannelReceiveParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                channelReceiveParams.message = Message.decode(decoder.readPointer(8, false));
                return channelReceiveParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.message, 8, false);
        }

        private ChannelReceiveParams(int i) {
            super(16, i);
        }
    }

    static final class ChannelSetPeerPidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int pid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ChannelSetPeerPidParams() {
            super(16, 0);
        }

        public static ChannelSetPeerPidParams deserialize(org.chromium.mojo.bindings.Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ChannelSetPeerPidParams channelSetPeerPidParams = new ChannelSetPeerPidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                channelSetPeerPidParams.pid = decoder.readInt(8);
                return channelSetPeerPidParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.pid, 8);
        }

        private ChannelSetPeerPidParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements Channel.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.IPC.mojom.Channel
        public void getAssociatedInterface(String str, AssociatedInterfaceRequestNotSupported associatedInterfaceRequestNotSupported) {
            ChannelGetAssociatedInterfaceParams channelGetAssociatedInterfaceParams = new ChannelGetAssociatedInterfaceParams();
            channelGetAssociatedInterfaceParams.name = str;
            channelGetAssociatedInterfaceParams.request = associatedInterfaceRequestNotSupported;
            getProxyHandler().getMessageReceiver().accept(channelGetAssociatedInterfaceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.IPC.mojom.Channel
        public void receive(Message message) {
            ChannelReceiveParams channelReceiveParams = new ChannelReceiveParams();
            channelReceiveParams.message = message;
            getProxyHandler().getMessageReceiver().accept(channelReceiveParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.IPC.mojom.Channel
        public void setPeerPid(int i) {
            ChannelSetPeerPidParams channelSetPeerPidParams = new ChannelSetPeerPidParams();
            channelSetPeerPidParams.pid = i;
            getProxyHandler().getMessageReceiver().accept(channelSetPeerPidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<Channel> {
        Stub(Core core, Channel channel) {
            super(core, channel);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(org.chromium.mojo.bindings.Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (!header.validateHeader(0)) {
                    return false;
                }
                int type = header.getType();
                if (type == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(Channel_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().setPeerPid(ChannelSetPeerPidParams.deserialize(serviceMessageAsServiceMessage.getPayload()).pid);
                    return true;
                }
                if (type == 1) {
                    getImpl().receive(ChannelReceiveParams.deserialize(serviceMessageAsServiceMessage.getPayload()).message);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                ChannelGetAssociatedInterfaceParams channelGetAssociatedInterfaceParamsDeserialize = ChannelGetAssociatedInterfaceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getAssociatedInterface(channelGetAssociatedInterfaceParamsDeserialize.name, channelGetAssociatedInterfaceParamsDeserialize.request);
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }

        @Override // org.chromium.mojo.bindings.MessageReceiverWithResponder
        public boolean acceptWithResponder(org.chromium.mojo.bindings.Message message, MessageReceiver messageReceiver) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), Channel_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

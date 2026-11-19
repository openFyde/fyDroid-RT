package org.chromium.arc.mojom;

import org.chromium.arc.mojom.SystemUiInstance;
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

class SystemUiInstance_Internal {
    public static final Interface.Manager<SystemUiInstance, SystemUiInstance.Proxy> MANAGER = new Interface.Manager<SystemUiInstance, SystemUiInstance.Proxy>() { // from class: org.chromium.arc.mojom.SystemUiInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SystemUiInstance[] buildArray(int i) {
            return new SystemUiInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SystemUiInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SystemUiInstance> buildStub(Core core, SystemUiInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.SystemUiInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SystemUiInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.SystemUiInstance
        public void setDarkThemeStatus(boolean z) {
            SystemUiInstanceSetDarkThemeStatusParams systemUiInstanceSetDarkThemeStatusParams = new SystemUiInstanceSetDarkThemeStatusParams();
            systemUiInstanceSetDarkThemeStatusParams.active = z;
            getProxyHandler().getMessageReceiver().accept(systemUiInstanceSetDarkThemeStatusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.SystemUiInstance
        public void setOverlayColor(int i, int i2) {
            SystemUiInstanceSetOverlayColorParams systemUiInstanceSetOverlayColorParams = new SystemUiInstanceSetOverlayColorParams();
            systemUiInstanceSetOverlayColorParams.sourceColor = i;
            systemUiInstanceSetOverlayColorParams.themeStyle = i2;
            getProxyHandler().getMessageReceiver().accept(systemUiInstanceSetOverlayColorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<SystemUiInstance> {
        Stub(Core core, SystemUiInstance systemUiInstance) {
            super(core, systemUiInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SystemUiInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().setDarkThemeStatus(SystemUiInstanceSetDarkThemeStatusParams.deserialize(serviceMessageAsServiceMessage.getPayload()).active);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                SystemUiInstanceSetOverlayColorParams systemUiInstanceSetOverlayColorParamsDeserialize = SystemUiInstanceSetOverlayColorParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().setOverlayColor(systemUiInstanceSetOverlayColorParamsDeserialize.sourceColor, systemUiInstanceSetOverlayColorParamsDeserialize.themeStyle);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SystemUiInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class SystemUiInstanceSetDarkThemeStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean active;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SystemUiInstanceSetDarkThemeStatusParams() {
            super(16, 0);
        }

        public static SystemUiInstanceSetDarkThemeStatusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SystemUiInstanceSetDarkThemeStatusParams systemUiInstanceSetDarkThemeStatusParams = new SystemUiInstanceSetDarkThemeStatusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                systemUiInstanceSetDarkThemeStatusParams.active = decoder.readBoolean(8, 0);
                return systemUiInstanceSetDarkThemeStatusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.active, 8, 0);
        }

        private SystemUiInstanceSetDarkThemeStatusParams(int i) {
            super(16, i);
        }
    }

    static final class SystemUiInstanceSetOverlayColorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int sourceColor;
        public int themeStyle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SystemUiInstanceSetOverlayColorParams() {
            super(16, 0);
        }

        public static SystemUiInstanceSetOverlayColorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SystemUiInstanceSetOverlayColorParams systemUiInstanceSetOverlayColorParams = new SystemUiInstanceSetOverlayColorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                systemUiInstanceSetOverlayColorParams.sourceColor = decoder.readInt(8);
                int i = decoder.readInt(12);
                systemUiInstanceSetOverlayColorParams.themeStyle = i;
                ThemeStyleType.validate(i);
                return systemUiInstanceSetOverlayColorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.sourceColor, 8);
            encoderAtDataOffset.encode(this.themeStyle, 12);
        }

        private SystemUiInstanceSetOverlayColorParams(int i) {
            super(16, i);
        }
    }
}

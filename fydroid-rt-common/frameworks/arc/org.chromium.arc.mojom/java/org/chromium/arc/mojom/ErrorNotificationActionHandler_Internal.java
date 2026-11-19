package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ErrorNotificationActionHandler;
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

class ErrorNotificationActionHandler_Internal {
    public static final Interface.Manager<ErrorNotificationActionHandler, ErrorNotificationActionHandler.Proxy> MANAGER = new Interface.Manager<ErrorNotificationActionHandler, ErrorNotificationActionHandler.Proxy>() { // from class: org.chromium.arc.mojom.ErrorNotificationActionHandler_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ErrorNotificationActionHandler[] buildArray(int i) {
            return new ErrorNotificationActionHandler[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ErrorNotificationActionHandler.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ErrorNotificationActionHandler> buildStub(Core core, ErrorNotificationActionHandler r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ErrorNotificationActionHandler";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ErrorNotificationActionHandlerOnNotificationButtonClickedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int buttonIndex;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ErrorNotificationActionHandlerOnNotificationButtonClickedParams() {
            super(16, 0);
        }

        public static ErrorNotificationActionHandlerOnNotificationButtonClickedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ErrorNotificationActionHandlerOnNotificationButtonClickedParams errorNotificationActionHandlerOnNotificationButtonClickedParams = new ErrorNotificationActionHandlerOnNotificationButtonClickedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                errorNotificationActionHandlerOnNotificationButtonClickedParams.buttonIndex = decoder.readInt(8);
                return errorNotificationActionHandlerOnNotificationButtonClickedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.buttonIndex, 8);
        }

        private ErrorNotificationActionHandlerOnNotificationButtonClickedParams(int i) {
            super(16, i);
        }
    }

    static final class ErrorNotificationActionHandlerOnNotificationClosedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ErrorNotificationActionHandlerOnNotificationClosedParams() {
            super(8, 0);
        }

        public static ErrorNotificationActionHandlerOnNotificationClosedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ErrorNotificationActionHandlerOnNotificationClosedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ErrorNotificationActionHandlerOnNotificationClosedParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ErrorNotificationActionHandler.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ErrorNotificationActionHandler
        public void onNotificationButtonClicked(int i) {
            ErrorNotificationActionHandlerOnNotificationButtonClickedParams errorNotificationActionHandlerOnNotificationButtonClickedParams = new ErrorNotificationActionHandlerOnNotificationButtonClickedParams();
            errorNotificationActionHandlerOnNotificationButtonClickedParams.buttonIndex = i;
            getProxyHandler().getMessageReceiver().accept(errorNotificationActionHandlerOnNotificationButtonClickedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.ErrorNotificationActionHandler
        public void onNotificationClosed() {
            getProxyHandler().getMessageReceiver().accept(new ErrorNotificationActionHandlerOnNotificationClosedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<ErrorNotificationActionHandler> {
        Stub(Core core, ErrorNotificationActionHandler errorNotificationActionHandler) {
            super(core, errorNotificationActionHandler);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ErrorNotificationActionHandler_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onNotificationButtonClicked(ErrorNotificationActionHandlerOnNotificationButtonClickedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).buttonIndex);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                ErrorNotificationActionHandlerOnNotificationClosedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onNotificationClosed();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ErrorNotificationActionHandler_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

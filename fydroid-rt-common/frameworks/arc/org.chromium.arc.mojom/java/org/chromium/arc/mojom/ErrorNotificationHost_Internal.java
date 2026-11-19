// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ErrorNotificationHost;
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

class ErrorNotificationHost_Internal {
    public static final Interface.Manager<ErrorNotificationHost, ErrorNotificationHost.Proxy> MANAGER = new Interface.Manager<ErrorNotificationHost, ErrorNotificationHost.Proxy>() { // from class: org.chromium.arc.mojom.ErrorNotificationHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ErrorNotificationHost[] buildArray(int i) {
            return new ErrorNotificationHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ErrorNotificationHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ErrorNotificationHost> buildStub(Core core, ErrorNotificationHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ErrorNotificationHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ErrorNotificationHostSendErrorDetailsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ErrorNotificationActionHandler actionHandler;
        public ErrorDetails details;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ErrorNotificationHostSendErrorDetailsParams() {
            super(24, 0);
        }

        public static ErrorNotificationHostSendErrorDetailsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ErrorNotificationHostSendErrorDetailsParams errorNotificationHostSendErrorDetailsParams = new ErrorNotificationHostSendErrorDetailsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                errorNotificationHostSendErrorDetailsParams.details = ErrorDetails.decode(decoder.readPointer(8, false));
                errorNotificationHostSendErrorDetailsParams.actionHandler = (ErrorNotificationActionHandler) decoder.readServiceInterface(16, false, ErrorNotificationActionHandler.MANAGER);
                return errorNotificationHostSendErrorDetailsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.details, 8, false);
            encoderAtDataOffset.encode(this.actionHandler, 16, false, (Interface.Manager<ErrorNotificationActionHandler, ?>) ErrorNotificationActionHandler.MANAGER);
        }

        private ErrorNotificationHostSendErrorDetailsParams(int i) {
            super(24, i);
        }
    }

    static final class ErrorNotificationHostSendErrorDetailsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ErrorNotificationItem item;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ErrorNotificationHostSendErrorDetailsResponseParams() {
            super(16, 0);
        }

        public static ErrorNotificationHostSendErrorDetailsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ErrorNotificationHostSendErrorDetailsResponseParams errorNotificationHostSendErrorDetailsResponseParams = new ErrorNotificationHostSendErrorDetailsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                errorNotificationHostSendErrorDetailsResponseParams.item = (ErrorNotificationItem) decoder.readServiceInterface(8, true, ErrorNotificationItem.MANAGER);
                return errorNotificationHostSendErrorDetailsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.item, 8, true, (Interface.Manager<ErrorNotificationItem, ?>) ErrorNotificationItem.MANAGER);
        }

        private ErrorNotificationHostSendErrorDetailsResponseParams(int i) {
            super(16, i);
        }
    }

    static class ErrorNotificationHostSendErrorDetailsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ErrorNotificationHost.SendErrorDetailsResponse mCallback;

        ErrorNotificationHostSendErrorDetailsResponseParamsForwardToCallback(ErrorNotificationHost.SendErrorDetailsResponse sendErrorDetailsResponse) {
            this.mCallback = sendErrorDetailsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(ErrorNotificationHostSendErrorDetailsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).item);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ErrorNotificationHostSendErrorDetailsResponseParamsProxyToResponder implements ErrorNotificationHost.SendErrorDetailsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ErrorNotificationHostSendErrorDetailsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ErrorNotificationItem errorNotificationItem) {
            ErrorNotificationHostSendErrorDetailsResponseParams errorNotificationHostSendErrorDetailsResponseParams = new ErrorNotificationHostSendErrorDetailsResponseParams();
            errorNotificationHostSendErrorDetailsResponseParams.item = errorNotificationItem;
            this.mMessageReceiver.accept(errorNotificationHostSendErrorDetailsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ErrorNotificationHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ErrorNotificationHost
        public void sendErrorDetails(ErrorDetails errorDetails, ErrorNotificationActionHandler errorNotificationActionHandler, ErrorNotificationHost.SendErrorDetailsResponse sendErrorDetailsResponse) {
            ErrorNotificationHostSendErrorDetailsParams errorNotificationHostSendErrorDetailsParams = new ErrorNotificationHostSendErrorDetailsParams();
            errorNotificationHostSendErrorDetailsParams.details = errorDetails;
            errorNotificationHostSendErrorDetailsParams.actionHandler = errorNotificationActionHandler;
            getProxyHandler().getMessageReceiver().acceptWithResponder(errorNotificationHostSendErrorDetailsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new ErrorNotificationHostSendErrorDetailsResponseParamsForwardToCallback(sendErrorDetailsResponse));
        }
    }

    static final class Stub extends Interface.Stub<ErrorNotificationHost> {
        Stub(Core core, ErrorNotificationHost errorNotificationHost) {
            super(core, errorNotificationHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ErrorNotificationHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                return false;
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ErrorNotificationHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                ErrorNotificationHostSendErrorDetailsParams errorNotificationHostSendErrorDetailsParamsDeserialize = ErrorNotificationHostSendErrorDetailsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().sendErrorDetails(errorNotificationHostSendErrorDetailsParamsDeserialize.details, errorNotificationHostSendErrorDetailsParamsDeserialize.actionHandler, new ErrorNotificationHostSendErrorDetailsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

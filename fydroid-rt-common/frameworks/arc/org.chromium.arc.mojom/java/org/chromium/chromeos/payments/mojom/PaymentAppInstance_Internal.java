// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.payments.mojom;

import org.chromium.chromeos.payments.mojom.PaymentAppInstance;
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
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.system.Core;

class PaymentAppInstance_Internal {
    public static final Interface.Manager<PaymentAppInstance, PaymentAppInstance.Proxy> MANAGER = new Interface.Manager<PaymentAppInstance, PaymentAppInstance.Proxy>() { // from class: org.chromium.chromeos.payments.mojom.PaymentAppInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PaymentAppInstance[] buildArray(int i) {
            return new PaymentAppInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PaymentAppInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PaymentAppInstance> buildStub(Core core, PaymentAppInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.payments.mojom.PaymentAppInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class PaymentAppInstanceAbortPaymentAppParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String requestToken;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceAbortPaymentAppParams() {
            super(16, 0);
        }

        public static PaymentAppInstanceAbortPaymentAppParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceAbortPaymentAppParams paymentAppInstanceAbortPaymentAppParams = new PaymentAppInstanceAbortPaymentAppParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceAbortPaymentAppParams.requestToken = decoder.readString(8, false);
                return paymentAppInstanceAbortPaymentAppParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.requestToken, 8, false);
        }

        private PaymentAppInstanceAbortPaymentAppParams(int i) {
            super(16, i);
        }
    }

    static final class PaymentAppInstanceAbortPaymentAppResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean aborted;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceAbortPaymentAppResponseParams() {
            super(16, 0);
        }

        public static PaymentAppInstanceAbortPaymentAppResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceAbortPaymentAppResponseParams paymentAppInstanceAbortPaymentAppResponseParams = new PaymentAppInstanceAbortPaymentAppResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceAbortPaymentAppResponseParams.aborted = decoder.readBoolean(8, 0);
                return paymentAppInstanceAbortPaymentAppResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.aborted, 8, 0);
        }

        private PaymentAppInstanceAbortPaymentAppResponseParams(int i) {
            super(16, i);
        }
    }

    static class PaymentAppInstanceAbortPaymentAppResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PaymentAppInstance.AbortPaymentAppResponse mCallback;

        PaymentAppInstanceAbortPaymentAppResponseParamsForwardToCallback(PaymentAppInstance.AbortPaymentAppResponse abortPaymentAppResponse) {
            this.mCallback = abortPaymentAppResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(PaymentAppInstanceAbortPaymentAppResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).aborted));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PaymentAppInstanceAbortPaymentAppResponseParamsProxyToResponder implements PaymentAppInstance.AbortPaymentAppResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PaymentAppInstanceAbortPaymentAppResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            PaymentAppInstanceAbortPaymentAppResponseParams paymentAppInstanceAbortPaymentAppResponseParams = new PaymentAppInstanceAbortPaymentAppResponseParams();
            paymentAppInstanceAbortPaymentAppResponseParams.aborted = bool.booleanValue();
            this.mMessageReceiver.accept(paymentAppInstanceAbortPaymentAppResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class PaymentAppInstanceInvokePaymentAppParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PaymentParameters parameters;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceInvokePaymentAppParams() {
            super(16, 0);
        }

        public static PaymentAppInstanceInvokePaymentAppParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceInvokePaymentAppParams paymentAppInstanceInvokePaymentAppParams = new PaymentAppInstanceInvokePaymentAppParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceInvokePaymentAppParams.parameters = PaymentParameters.decode(decoder.readPointer(8, false));
                return paymentAppInstanceInvokePaymentAppParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.parameters, 8, false);
        }

        private PaymentAppInstanceInvokePaymentAppParams(int i) {
            super(16, i);
        }
    }

    static final class PaymentAppInstanceInvokePaymentAppResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InvokePaymentAppResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceInvokePaymentAppResponseParams() {
            super(24, 0);
        }

        public static PaymentAppInstanceInvokePaymentAppResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceInvokePaymentAppResponseParams paymentAppInstanceInvokePaymentAppResponseParams = new PaymentAppInstanceInvokePaymentAppResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceInvokePaymentAppResponseParams.response = InvokePaymentAppResult.decode(decoder, 8);
                return paymentAppInstanceInvokePaymentAppResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Union) this.response, 8, false);
        }

        private PaymentAppInstanceInvokePaymentAppResponseParams(int i) {
            super(24, i);
        }
    }

    static class PaymentAppInstanceInvokePaymentAppResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PaymentAppInstance.InvokePaymentAppResponse mCallback;

        PaymentAppInstanceInvokePaymentAppResponseParamsForwardToCallback(PaymentAppInstance.InvokePaymentAppResponse invokePaymentAppResponse) {
            this.mCallback = invokePaymentAppResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(PaymentAppInstanceInvokePaymentAppResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PaymentAppInstanceInvokePaymentAppResponseParamsProxyToResponder implements PaymentAppInstance.InvokePaymentAppResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PaymentAppInstanceInvokePaymentAppResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(InvokePaymentAppResult invokePaymentAppResult) {
            PaymentAppInstanceInvokePaymentAppResponseParams paymentAppInstanceInvokePaymentAppResponseParams = new PaymentAppInstanceInvokePaymentAppResponseParams();
            paymentAppInstanceInvokePaymentAppResponseParams.response = invokePaymentAppResult;
            this.mMessageReceiver.accept(paymentAppInstanceInvokePaymentAppResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class PaymentAppInstanceIsPaymentImplementedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceIsPaymentImplementedParams() {
            super(16, 0);
        }

        public static PaymentAppInstanceIsPaymentImplementedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceIsPaymentImplementedParams paymentAppInstanceIsPaymentImplementedParams = new PaymentAppInstanceIsPaymentImplementedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceIsPaymentImplementedParams.packageName = decoder.readString(8, false);
                return paymentAppInstanceIsPaymentImplementedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private PaymentAppInstanceIsPaymentImplementedParams(int i) {
            super(16, i);
        }
    }

    static final class PaymentAppInstanceIsPaymentImplementedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IsPaymentImplementedResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceIsPaymentImplementedResponseParams() {
            super(24, 0);
        }

        public static PaymentAppInstanceIsPaymentImplementedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceIsPaymentImplementedResponseParams paymentAppInstanceIsPaymentImplementedResponseParams = new PaymentAppInstanceIsPaymentImplementedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceIsPaymentImplementedResponseParams.response = IsPaymentImplementedResult.decode(decoder, 8);
                return paymentAppInstanceIsPaymentImplementedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Union) this.response, 8, false);
        }

        private PaymentAppInstanceIsPaymentImplementedResponseParams(int i) {
            super(24, i);
        }
    }

    static class PaymentAppInstanceIsPaymentImplementedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PaymentAppInstance.IsPaymentImplementedResponse mCallback;

        PaymentAppInstanceIsPaymentImplementedResponseParamsForwardToCallback(PaymentAppInstance.IsPaymentImplementedResponse isPaymentImplementedResponse) {
            this.mCallback = isPaymentImplementedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(PaymentAppInstanceIsPaymentImplementedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PaymentAppInstanceIsPaymentImplementedResponseParamsProxyToResponder implements PaymentAppInstance.IsPaymentImplementedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PaymentAppInstanceIsPaymentImplementedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(IsPaymentImplementedResult isPaymentImplementedResult) {
            PaymentAppInstanceIsPaymentImplementedResponseParams paymentAppInstanceIsPaymentImplementedResponseParams = new PaymentAppInstanceIsPaymentImplementedResponseParams();
            paymentAppInstanceIsPaymentImplementedResponseParams.response = isPaymentImplementedResult;
            this.mMessageReceiver.accept(paymentAppInstanceIsPaymentImplementedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class PaymentAppInstanceIsReadyToPayParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PaymentParameters parameters;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceIsReadyToPayParams() {
            super(16, 0);
        }

        public static PaymentAppInstanceIsReadyToPayParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceIsReadyToPayParams paymentAppInstanceIsReadyToPayParams = new PaymentAppInstanceIsReadyToPayParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceIsReadyToPayParams.parameters = PaymentParameters.decode(decoder.readPointer(8, false));
                return paymentAppInstanceIsReadyToPayParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.parameters, 8, false);
        }

        private PaymentAppInstanceIsReadyToPayParams(int i) {
            super(16, i);
        }
    }

    static final class PaymentAppInstanceIsReadyToPayResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IsReadyToPayResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PaymentAppInstanceIsReadyToPayResponseParams() {
            super(24, 0);
        }

        public static PaymentAppInstanceIsReadyToPayResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PaymentAppInstanceIsReadyToPayResponseParams paymentAppInstanceIsReadyToPayResponseParams = new PaymentAppInstanceIsReadyToPayResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                paymentAppInstanceIsReadyToPayResponseParams.response = IsReadyToPayResult.decode(decoder, 8);
                return paymentAppInstanceIsReadyToPayResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Union) this.response, 8, false);
        }

        private PaymentAppInstanceIsReadyToPayResponseParams(int i) {
            super(24, i);
        }
    }

    static class PaymentAppInstanceIsReadyToPayResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PaymentAppInstance.IsReadyToPayResponse mCallback;

        PaymentAppInstanceIsReadyToPayResponseParamsForwardToCallback(PaymentAppInstance.IsReadyToPayResponse isReadyToPayResponse) {
            this.mCallback = isReadyToPayResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(PaymentAppInstanceIsReadyToPayResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PaymentAppInstanceIsReadyToPayResponseParamsProxyToResponder implements PaymentAppInstance.IsReadyToPayResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PaymentAppInstanceIsReadyToPayResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(IsReadyToPayResult isReadyToPayResult) {
            PaymentAppInstanceIsReadyToPayResponseParams paymentAppInstanceIsReadyToPayResponseParams = new PaymentAppInstanceIsReadyToPayResponseParams();
            paymentAppInstanceIsReadyToPayResponseParams.response = isReadyToPayResult;
            this.mMessageReceiver.accept(paymentAppInstanceIsReadyToPayResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PaymentAppInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.payments.mojom.PaymentAppInstance
        public void abortPaymentApp(String str, PaymentAppInstance.AbortPaymentAppResponse abortPaymentAppResponse) {
            PaymentAppInstanceAbortPaymentAppParams paymentAppInstanceAbortPaymentAppParams = new PaymentAppInstanceAbortPaymentAppParams();
            paymentAppInstanceAbortPaymentAppParams.requestToken = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(paymentAppInstanceAbortPaymentAppParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new PaymentAppInstanceAbortPaymentAppResponseParamsForwardToCallback(abortPaymentAppResponse));
        }

        @Override // org.chromium.chromeos.payments.mojom.PaymentAppInstance
        public void invokePaymentApp(PaymentParameters paymentParameters, PaymentAppInstance.InvokePaymentAppResponse invokePaymentAppResponse) {
            PaymentAppInstanceInvokePaymentAppParams paymentAppInstanceInvokePaymentAppParams = new PaymentAppInstanceInvokePaymentAppParams();
            paymentAppInstanceInvokePaymentAppParams.parameters = paymentParameters;
            getProxyHandler().getMessageReceiver().acceptWithResponder(paymentAppInstanceInvokePaymentAppParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new PaymentAppInstanceInvokePaymentAppResponseParamsForwardToCallback(invokePaymentAppResponse));
        }

        @Override // org.chromium.chromeos.payments.mojom.PaymentAppInstance
        public void isPaymentImplemented(String str, PaymentAppInstance.IsPaymentImplementedResponse isPaymentImplementedResponse) {
            PaymentAppInstanceIsPaymentImplementedParams paymentAppInstanceIsPaymentImplementedParams = new PaymentAppInstanceIsPaymentImplementedParams();
            paymentAppInstanceIsPaymentImplementedParams.packageName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(paymentAppInstanceIsPaymentImplementedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new PaymentAppInstanceIsPaymentImplementedResponseParamsForwardToCallback(isPaymentImplementedResponse));
        }

        @Override // org.chromium.chromeos.payments.mojom.PaymentAppInstance
        public void isReadyToPay(PaymentParameters paymentParameters, PaymentAppInstance.IsReadyToPayResponse isReadyToPayResponse) {
            PaymentAppInstanceIsReadyToPayParams paymentAppInstanceIsReadyToPayParams = new PaymentAppInstanceIsReadyToPayParams();
            paymentAppInstanceIsReadyToPayParams.parameters = paymentParameters;
            getProxyHandler().getMessageReceiver().acceptWithResponder(paymentAppInstanceIsReadyToPayParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new PaymentAppInstanceIsReadyToPayResponseParamsForwardToCallback(isReadyToPayResponse));
        }
    }

    static final class Stub extends Interface.Stub<PaymentAppInstance> {
        Stub(Core core, PaymentAppInstance paymentAppInstance) {
            super(core, paymentAppInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PaymentAppInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PaymentAppInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().isPaymentImplemented(PaymentAppInstanceIsPaymentImplementedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName, new PaymentAppInstanceIsPaymentImplementedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    getImpl().isReadyToPay(PaymentAppInstanceIsReadyToPayParams.deserialize(serviceMessageAsServiceMessage.getPayload()).parameters, new PaymentAppInstanceIsReadyToPayResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 2) {
                    getImpl().invokePaymentApp(PaymentAppInstanceInvokePaymentAppParams.deserialize(serviceMessageAsServiceMessage.getPayload()).parameters, new PaymentAppInstanceInvokePaymentAppResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().abortPaymentApp(PaymentAppInstanceAbortPaymentAppParams.deserialize(serviceMessageAsServiceMessage.getPayload()).requestToken, new PaymentAppInstanceAbortPaymentAppResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

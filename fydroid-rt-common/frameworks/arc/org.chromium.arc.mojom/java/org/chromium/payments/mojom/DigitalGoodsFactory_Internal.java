// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.payments.mojom;

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
import org.chromium.payments.mojom.DigitalGoodsFactory;

class DigitalGoodsFactory_Internal {
    public static final Interface.Manager<DigitalGoodsFactory, DigitalGoodsFactory.Proxy> MANAGER = new Interface.Manager<DigitalGoodsFactory, DigitalGoodsFactory.Proxy>() { // from class: org.chromium.payments.mojom.DigitalGoodsFactory_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DigitalGoodsFactory[] buildArray(int i) {
            return new DigitalGoodsFactory[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DigitalGoodsFactory.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<DigitalGoodsFactory> buildStub(Core core, DigitalGoodsFactory r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "payments.mojom.DigitalGoodsFactory";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class DigitalGoodsFactoryCreateDigitalGoodsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String paymentMethod;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsFactoryCreateDigitalGoodsParams() {
            super(16, 0);
        }

        public static DigitalGoodsFactoryCreateDigitalGoodsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsFactoryCreateDigitalGoodsParams digitalGoodsFactoryCreateDigitalGoodsParams = new DigitalGoodsFactoryCreateDigitalGoodsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsFactoryCreateDigitalGoodsParams.paymentMethod = decoder.readString(8, false);
                return digitalGoodsFactoryCreateDigitalGoodsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.paymentMethod, 8, false);
        }

        private DigitalGoodsFactoryCreateDigitalGoodsParams(int i) {
            super(16, i);
        }
    }

    static final class DigitalGoodsFactoryCreateDigitalGoodsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public DigitalGoods digitalGoods;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsFactoryCreateDigitalGoodsResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsFactoryCreateDigitalGoodsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsFactoryCreateDigitalGoodsResponseParams digitalGoodsFactoryCreateDigitalGoodsResponseParams = new DigitalGoodsFactoryCreateDigitalGoodsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                digitalGoodsFactoryCreateDigitalGoodsResponseParams.code = i;
                CreateDigitalGoodsResponseCode.validate(i);
                digitalGoodsFactoryCreateDigitalGoodsResponseParams.digitalGoods = (DigitalGoods) decoder.readServiceInterface(12, true, DigitalGoods.MANAGER);
                return digitalGoodsFactoryCreateDigitalGoodsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.code, 8);
            encoderAtDataOffset.encode(this.digitalGoods, 12, true, (Interface.Manager<DigitalGoods, ?>) DigitalGoods.MANAGER);
        }

        private DigitalGoodsFactoryCreateDigitalGoodsResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsFactoryCreateDigitalGoodsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoodsFactory.CreateDigitalGoodsResponse mCallback;

        DigitalGoodsFactoryCreateDigitalGoodsResponseParamsForwardToCallback(DigitalGoodsFactory.CreateDigitalGoodsResponse createDigitalGoodsResponse) {
            this.mCallback = createDigitalGoodsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                DigitalGoodsFactoryCreateDigitalGoodsResponseParams digitalGoodsFactoryCreateDigitalGoodsResponseParamsDeserialize = DigitalGoodsFactoryCreateDigitalGoodsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsFactoryCreateDigitalGoodsResponseParamsDeserialize.code), digitalGoodsFactoryCreateDigitalGoodsResponseParamsDeserialize.digitalGoods);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsFactoryCreateDigitalGoodsResponseParamsProxyToResponder implements DigitalGoodsFactory.CreateDigitalGoodsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsFactoryCreateDigitalGoodsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, DigitalGoods digitalGoods) {
            DigitalGoodsFactoryCreateDigitalGoodsResponseParams digitalGoodsFactoryCreateDigitalGoodsResponseParams = new DigitalGoodsFactoryCreateDigitalGoodsResponseParams();
            digitalGoodsFactoryCreateDigitalGoodsResponseParams.code = num.intValue();
            digitalGoodsFactoryCreateDigitalGoodsResponseParams.digitalGoods = digitalGoods;
            this.mMessageReceiver.accept(digitalGoodsFactoryCreateDigitalGoodsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements DigitalGoodsFactory.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.payments.mojom.DigitalGoodsFactory
        public void createDigitalGoods(String str, DigitalGoodsFactory.CreateDigitalGoodsResponse createDigitalGoodsResponse) {
            DigitalGoodsFactoryCreateDigitalGoodsParams digitalGoodsFactoryCreateDigitalGoodsParams = new DigitalGoodsFactoryCreateDigitalGoodsParams();
            digitalGoodsFactoryCreateDigitalGoodsParams.paymentMethod = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsFactoryCreateDigitalGoodsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new DigitalGoodsFactoryCreateDigitalGoodsResponseParamsForwardToCallback(createDigitalGoodsResponse));
        }
    }

    static final class Stub extends Interface.Stub<DigitalGoodsFactory> {
        Stub(Core core, DigitalGoodsFactory digitalGoodsFactory) {
            super(core, digitalGoodsFactory);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(DigitalGoodsFactory_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), DigitalGoodsFactory_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().createDigitalGoods(DigitalGoodsFactoryCreateDigitalGoodsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).paymentMethod, new DigitalGoodsFactoryCreateDigitalGoodsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

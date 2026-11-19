// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.DigitalGoodsInstance;
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
import org.chromium.payments.mojom.BillingResponseCode;
import org.chromium.payments.mojom.ItemDetails;
import org.chromium.payments.mojom.PurchaseDetails;
import org.chromium.payments.mojom.PurchaseReference;

class DigitalGoodsInstance_Internal {
    public static final Interface.Manager<DigitalGoodsInstance, DigitalGoodsInstance.Proxy> MANAGER = new Interface.Manager<DigitalGoodsInstance, DigitalGoodsInstance.Proxy>() { // from class: org.chromium.arc.mojom.DigitalGoodsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DigitalGoodsInstance[] buildArray(int i) {
            return new DigitalGoodsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DigitalGoodsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<DigitalGoodsInstance> buildStub(Core core, DigitalGoodsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.DigitalGoodsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class DigitalGoodsInstanceAcknowledgeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean makeAvailableAgain;
        public String packageName;
        public String purchaseToken;
        public String scope;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceAcknowledgeParams() {
            super(40, 0);
        }

        public static DigitalGoodsInstanceAcknowledgeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsInstanceAcknowledgeParams digitalGoodsInstanceAcknowledgeParams = new DigitalGoodsInstanceAcknowledgeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsInstanceAcknowledgeParams.packageName = decoder.readString(8, false);
                digitalGoodsInstanceAcknowledgeParams.scope = decoder.readString(16, false);
                digitalGoodsInstanceAcknowledgeParams.purchaseToken = decoder.readString(24, false);
                digitalGoodsInstanceAcknowledgeParams.makeAvailableAgain = decoder.readBoolean(32, 0);
                return digitalGoodsInstanceAcknowledgeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.scope, 16, false);
            encoderAtDataOffset.encode(this.purchaseToken, 24, false);
            encoderAtDataOffset.encode(this.makeAvailableAgain, 32, 0);
        }

        private DigitalGoodsInstanceAcknowledgeParams(int i) {
            super(40, i);
        }
    }

    static final class DigitalGoodsInstanceAcknowledgeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceAcknowledgeResponseParams() {
            super(16, 0);
        }

        public static DigitalGoodsInstanceAcknowledgeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsInstanceAcknowledgeResponseParams digitalGoodsInstanceAcknowledgeResponseParams = new DigitalGoodsInstanceAcknowledgeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                digitalGoodsInstanceAcknowledgeResponseParams.code = i;
                BillingResponseCode.validate(i);
                return digitalGoodsInstanceAcknowledgeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.code, 8);
        }

        private DigitalGoodsInstanceAcknowledgeResponseParams(int i) {
            super(16, i);
        }
    }

    static class DigitalGoodsInstanceAcknowledgeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoodsInstance.AcknowledgeResponse mCallback;

        DigitalGoodsInstanceAcknowledgeResponseParamsForwardToCallback(DigitalGoodsInstance.AcknowledgeResponse acknowledgeResponse) {
            this.mCallback = acknowledgeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(DigitalGoodsInstanceAcknowledgeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).code));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsInstanceAcknowledgeResponseParamsProxyToResponder implements DigitalGoodsInstance.AcknowledgeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsInstanceAcknowledgeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            DigitalGoodsInstanceAcknowledgeResponseParams digitalGoodsInstanceAcknowledgeResponseParams = new DigitalGoodsInstanceAcknowledgeResponseParams();
            digitalGoodsInstanceAcknowledgeResponseParams.code = num.intValue();
            this.mMessageReceiver.accept(digitalGoodsInstanceAcknowledgeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsInstanceConsumeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public String purchaseToken;
        public String scope;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceConsumeParams() {
            super(32, 0);
        }

        public static DigitalGoodsInstanceConsumeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsInstanceConsumeParams digitalGoodsInstanceConsumeParams = new DigitalGoodsInstanceConsumeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsInstanceConsumeParams.packageName = decoder.readString(8, false);
                digitalGoodsInstanceConsumeParams.scope = decoder.readString(16, false);
                digitalGoodsInstanceConsumeParams.purchaseToken = decoder.readString(24, false);
                return digitalGoodsInstanceConsumeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.scope, 16, false);
            encoderAtDataOffset.encode(this.purchaseToken, 24, false);
        }

        private DigitalGoodsInstanceConsumeParams(int i) {
            super(32, i);
        }
    }

    static final class DigitalGoodsInstanceConsumeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceConsumeResponseParams() {
            super(16, 0);
        }

        public static DigitalGoodsInstanceConsumeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsInstanceConsumeResponseParams digitalGoodsInstanceConsumeResponseParams = new DigitalGoodsInstanceConsumeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                digitalGoodsInstanceConsumeResponseParams.code = i;
                BillingResponseCode.validate(i);
                return digitalGoodsInstanceConsumeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.code, 8);
        }

        private DigitalGoodsInstanceConsumeResponseParams(int i) {
            super(16, i);
        }
    }

    static class DigitalGoodsInstanceConsumeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoodsInstance.ConsumeResponse mCallback;

        DigitalGoodsInstanceConsumeResponseParamsForwardToCallback(DigitalGoodsInstance.ConsumeResponse consumeResponse) {
            this.mCallback = consumeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(DigitalGoodsInstanceConsumeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).code));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsInstanceConsumeResponseParamsProxyToResponder implements DigitalGoodsInstance.ConsumeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsInstanceConsumeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            DigitalGoodsInstanceConsumeResponseParams digitalGoodsInstanceConsumeResponseParams = new DigitalGoodsInstanceConsumeResponseParams();
            digitalGoodsInstanceConsumeResponseParams.code = num.intValue();
            this.mMessageReceiver.accept(digitalGoodsInstanceConsumeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsInstanceDeprecatedListPurchasesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public String scope;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceDeprecatedListPurchasesParams() {
            super(24, 0);
        }

        public static DigitalGoodsInstanceDeprecatedListPurchasesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsInstanceDeprecatedListPurchasesParams digitalGoodsInstanceDeprecatedListPurchasesParams = new DigitalGoodsInstanceDeprecatedListPurchasesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsInstanceDeprecatedListPurchasesParams.packageName = decoder.readString(8, false);
                digitalGoodsInstanceDeprecatedListPurchasesParams.scope = decoder.readString(16, false);
                return digitalGoodsInstanceDeprecatedListPurchasesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.scope, 16, false);
        }

        private DigitalGoodsInstanceDeprecatedListPurchasesParams(int i) {
            super(24, i);
        }
    }

    static final class DigitalGoodsInstanceDeprecatedListPurchasesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public PurchaseDetails[] purchaseDetailsList;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceDeprecatedListPurchasesResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsInstanceDeprecatedListPurchasesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsInstanceDeprecatedListPurchasesResponseParams digitalGoodsInstanceDeprecatedListPurchasesResponseParams = new DigitalGoodsInstanceDeprecatedListPurchasesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                digitalGoodsInstanceDeprecatedListPurchasesResponseParams.code = i;
                BillingResponseCode.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsInstanceDeprecatedListPurchasesResponseParams.purchaseDetailsList = new PurchaseDetails[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    digitalGoodsInstanceDeprecatedListPurchasesResponseParams.purchaseDetailsList[i2] = PurchaseDetails.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return digitalGoodsInstanceDeprecatedListPurchasesResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.code, 8);
            PurchaseDetails[] purchaseDetailsArr = this.purchaseDetailsList;
            if (purchaseDetailsArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(purchaseDetailsArr.length, 16, -1);
            int i = 0;
            while (true) {
                PurchaseDetails[] purchaseDetailsArr2 = this.purchaseDetailsList;
                if (i >= purchaseDetailsArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) purchaseDetailsArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private DigitalGoodsInstanceDeprecatedListPurchasesResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsInstanceDeprecatedListPurchasesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoodsInstance.DeprecatedListPurchasesResponse mCallback;

        DigitalGoodsInstanceDeprecatedListPurchasesResponseParamsForwardToCallback(DigitalGoodsInstance.DeprecatedListPurchasesResponse deprecatedListPurchasesResponse) {
            this.mCallback = deprecatedListPurchasesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                DigitalGoodsInstanceDeprecatedListPurchasesResponseParams digitalGoodsInstanceDeprecatedListPurchasesResponseParamsDeserialize = DigitalGoodsInstanceDeprecatedListPurchasesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsInstanceDeprecatedListPurchasesResponseParamsDeserialize.code), digitalGoodsInstanceDeprecatedListPurchasesResponseParamsDeserialize.purchaseDetailsList);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsInstanceDeprecatedListPurchasesResponseParamsProxyToResponder implements DigitalGoodsInstance.DeprecatedListPurchasesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsInstanceDeprecatedListPurchasesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, PurchaseDetails[] purchaseDetailsArr) {
            DigitalGoodsInstanceDeprecatedListPurchasesResponseParams digitalGoodsInstanceDeprecatedListPurchasesResponseParams = new DigitalGoodsInstanceDeprecatedListPurchasesResponseParams();
            digitalGoodsInstanceDeprecatedListPurchasesResponseParams.code = num.intValue();
            digitalGoodsInstanceDeprecatedListPurchasesResponseParams.purchaseDetailsList = purchaseDetailsArr;
            this.mMessageReceiver.accept(digitalGoodsInstanceDeprecatedListPurchasesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsInstanceGetDetailsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] itemIds;
        public String packageName;
        public String scope;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceGetDetailsParams() {
            super(32, 0);
        }

        public static DigitalGoodsInstanceGetDetailsParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsInstanceGetDetailsParams digitalGoodsInstanceGetDetailsParams = new DigitalGoodsInstanceGetDetailsParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsInstanceGetDetailsParams.packageName = decoderOutline3.readString(8, false);
                digitalGoodsInstanceGetDetailsParams.scope = decoderOutline3.readString(16, false);
                Decoder pointer = decoderOutline3.readPointer(24, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsInstanceGetDetailsParams.itemIds = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    digitalGoodsInstanceGetDetailsParams.itemIds[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return digitalGoodsInstanceGetDetailsParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.scope, 16, false);
            String[] strArr = this.itemIds;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(24, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 24, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.itemIds;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private DigitalGoodsInstanceGetDetailsParams(int i) {
            super(32, i);
        }
    }

    static final class DigitalGoodsInstanceGetDetailsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public ItemDetails[] itemDetailsList;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceGetDetailsResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsInstanceGetDetailsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsInstanceGetDetailsResponseParams digitalGoodsInstanceGetDetailsResponseParams = new DigitalGoodsInstanceGetDetailsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                digitalGoodsInstanceGetDetailsResponseParams.code = i;
                BillingResponseCode.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsInstanceGetDetailsResponseParams.itemDetailsList = new ItemDetails[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    digitalGoodsInstanceGetDetailsResponseParams.itemDetailsList[i2] = ItemDetails.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return digitalGoodsInstanceGetDetailsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.code, 8);
            ItemDetails[] itemDetailsArr = this.itemDetailsList;
            if (itemDetailsArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(itemDetailsArr.length, 16, -1);
            int i = 0;
            while (true) {
                ItemDetails[] itemDetailsArr2 = this.itemDetailsList;
                if (i >= itemDetailsArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) itemDetailsArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private DigitalGoodsInstanceGetDetailsResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsInstanceGetDetailsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoodsInstance.GetDetailsResponse mCallback;

        DigitalGoodsInstanceGetDetailsResponseParamsForwardToCallback(DigitalGoodsInstance.GetDetailsResponse getDetailsResponse) {
            this.mCallback = getDetailsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                DigitalGoodsInstanceGetDetailsResponseParams digitalGoodsInstanceGetDetailsResponseParamsDeserialize = DigitalGoodsInstanceGetDetailsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsInstanceGetDetailsResponseParamsDeserialize.code), digitalGoodsInstanceGetDetailsResponseParamsDeserialize.itemDetailsList);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsInstanceGetDetailsResponseParamsProxyToResponder implements DigitalGoodsInstance.GetDetailsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsInstanceGetDetailsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, ItemDetails[] itemDetailsArr) {
            DigitalGoodsInstanceGetDetailsResponseParams digitalGoodsInstanceGetDetailsResponseParams = new DigitalGoodsInstanceGetDetailsResponseParams();
            digitalGoodsInstanceGetDetailsResponseParams.code = num.intValue();
            digitalGoodsInstanceGetDetailsResponseParams.itemDetailsList = itemDetailsArr;
            this.mMessageReceiver.accept(digitalGoodsInstanceGetDetailsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsInstanceListPurchaseHistoryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public String scope;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceListPurchaseHistoryParams() {
            super(24, 0);
        }

        public static DigitalGoodsInstanceListPurchaseHistoryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsInstanceListPurchaseHistoryParams digitalGoodsInstanceListPurchaseHistoryParams = new DigitalGoodsInstanceListPurchaseHistoryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsInstanceListPurchaseHistoryParams.packageName = decoder.readString(8, false);
                digitalGoodsInstanceListPurchaseHistoryParams.scope = decoder.readString(16, false);
                return digitalGoodsInstanceListPurchaseHistoryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.scope, 16, false);
        }

        private DigitalGoodsInstanceListPurchaseHistoryParams(int i) {
            super(24, i);
        }
    }

    static final class DigitalGoodsInstanceListPurchaseHistoryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public PurchaseReference[] purchaseReferenceList;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceListPurchaseHistoryResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsInstanceListPurchaseHistoryResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsInstanceListPurchaseHistoryResponseParams digitalGoodsInstanceListPurchaseHistoryResponseParams = new DigitalGoodsInstanceListPurchaseHistoryResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                digitalGoodsInstanceListPurchaseHistoryResponseParams.code = i;
                BillingResponseCode.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsInstanceListPurchaseHistoryResponseParams.purchaseReferenceList = new PurchaseReference[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    digitalGoodsInstanceListPurchaseHistoryResponseParams.purchaseReferenceList[i2] = PurchaseReference.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return digitalGoodsInstanceListPurchaseHistoryResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.code, 8);
            PurchaseReference[] purchaseReferenceArr = this.purchaseReferenceList;
            if (purchaseReferenceArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(purchaseReferenceArr.length, 16, -1);
            int i = 0;
            while (true) {
                PurchaseReference[] purchaseReferenceArr2 = this.purchaseReferenceList;
                if (i >= purchaseReferenceArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) purchaseReferenceArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private DigitalGoodsInstanceListPurchaseHistoryResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsInstanceListPurchaseHistoryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoodsInstance.ListPurchaseHistoryResponse mCallback;

        DigitalGoodsInstanceListPurchaseHistoryResponseParamsForwardToCallback(DigitalGoodsInstance.ListPurchaseHistoryResponse listPurchaseHistoryResponse) {
            this.mCallback = listPurchaseHistoryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                DigitalGoodsInstanceListPurchaseHistoryResponseParams digitalGoodsInstanceListPurchaseHistoryResponseParamsDeserialize = DigitalGoodsInstanceListPurchaseHistoryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsInstanceListPurchaseHistoryResponseParamsDeserialize.code), digitalGoodsInstanceListPurchaseHistoryResponseParamsDeserialize.purchaseReferenceList);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsInstanceListPurchaseHistoryResponseParamsProxyToResponder implements DigitalGoodsInstance.ListPurchaseHistoryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsInstanceListPurchaseHistoryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, PurchaseReference[] purchaseReferenceArr) {
            DigitalGoodsInstanceListPurchaseHistoryResponseParams digitalGoodsInstanceListPurchaseHistoryResponseParams = new DigitalGoodsInstanceListPurchaseHistoryResponseParams();
            digitalGoodsInstanceListPurchaseHistoryResponseParams.code = num.intValue();
            digitalGoodsInstanceListPurchaseHistoryResponseParams.purchaseReferenceList = purchaseReferenceArr;
            this.mMessageReceiver.accept(digitalGoodsInstanceListPurchaseHistoryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsInstanceListPurchasesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public String scope;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceListPurchasesParams() {
            super(24, 0);
        }

        public static DigitalGoodsInstanceListPurchasesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsInstanceListPurchasesParams digitalGoodsInstanceListPurchasesParams = new DigitalGoodsInstanceListPurchasesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsInstanceListPurchasesParams.packageName = decoder.readString(8, false);
                digitalGoodsInstanceListPurchasesParams.scope = decoder.readString(16, false);
                return digitalGoodsInstanceListPurchasesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.scope, 16, false);
        }

        private DigitalGoodsInstanceListPurchasesParams(int i) {
            super(24, i);
        }
    }

    static final class DigitalGoodsInstanceListPurchasesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public PurchaseReference[] purchaseReferenceList;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsInstanceListPurchasesResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsInstanceListPurchasesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsInstanceListPurchasesResponseParams digitalGoodsInstanceListPurchasesResponseParams = new DigitalGoodsInstanceListPurchasesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                digitalGoodsInstanceListPurchasesResponseParams.code = i;
                BillingResponseCode.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsInstanceListPurchasesResponseParams.purchaseReferenceList = new PurchaseReference[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    digitalGoodsInstanceListPurchasesResponseParams.purchaseReferenceList[i2] = PurchaseReference.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return digitalGoodsInstanceListPurchasesResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.code, 8);
            PurchaseReference[] purchaseReferenceArr = this.purchaseReferenceList;
            if (purchaseReferenceArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(purchaseReferenceArr.length, 16, -1);
            int i = 0;
            while (true) {
                PurchaseReference[] purchaseReferenceArr2 = this.purchaseReferenceList;
                if (i >= purchaseReferenceArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) purchaseReferenceArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private DigitalGoodsInstanceListPurchasesResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsInstanceListPurchasesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoodsInstance.ListPurchasesResponse mCallback;

        DigitalGoodsInstanceListPurchasesResponseParamsForwardToCallback(DigitalGoodsInstance.ListPurchasesResponse listPurchasesResponse) {
            this.mCallback = listPurchasesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                DigitalGoodsInstanceListPurchasesResponseParams digitalGoodsInstanceListPurchasesResponseParamsDeserialize = DigitalGoodsInstanceListPurchasesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsInstanceListPurchasesResponseParamsDeserialize.code), digitalGoodsInstanceListPurchasesResponseParamsDeserialize.purchaseReferenceList);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsInstanceListPurchasesResponseParamsProxyToResponder implements DigitalGoodsInstance.ListPurchasesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsInstanceListPurchasesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, PurchaseReference[] purchaseReferenceArr) {
            DigitalGoodsInstanceListPurchasesResponseParams digitalGoodsInstanceListPurchasesResponseParams = new DigitalGoodsInstanceListPurchasesResponseParams();
            digitalGoodsInstanceListPurchasesResponseParams.code = num.intValue();
            digitalGoodsInstanceListPurchasesResponseParams.purchaseReferenceList = purchaseReferenceArr;
            this.mMessageReceiver.accept(digitalGoodsInstanceListPurchasesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements DigitalGoodsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.DigitalGoodsInstance
        public void acknowledge(String str, String str2, String str3, boolean z, DigitalGoodsInstance.AcknowledgeResponse acknowledgeResponse) {
            DigitalGoodsInstanceAcknowledgeParams digitalGoodsInstanceAcknowledgeParams = new DigitalGoodsInstanceAcknowledgeParams();
            digitalGoodsInstanceAcknowledgeParams.packageName = str;
            digitalGoodsInstanceAcknowledgeParams.scope = str2;
            digitalGoodsInstanceAcknowledgeParams.purchaseToken = str3;
            digitalGoodsInstanceAcknowledgeParams.makeAvailableAgain = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsInstanceAcknowledgeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new DigitalGoodsInstanceAcknowledgeResponseParamsForwardToCallback(acknowledgeResponse));
        }

        @Override // org.chromium.arc.mojom.DigitalGoodsInstance
        public void consume(String str, String str2, String str3, DigitalGoodsInstance.ConsumeResponse consumeResponse) {
            DigitalGoodsInstanceConsumeParams digitalGoodsInstanceConsumeParams = new DigitalGoodsInstanceConsumeParams();
            digitalGoodsInstanceConsumeParams.packageName = str;
            digitalGoodsInstanceConsumeParams.scope = str2;
            digitalGoodsInstanceConsumeParams.purchaseToken = str3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsInstanceConsumeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new DigitalGoodsInstanceConsumeResponseParamsForwardToCallback(consumeResponse));
        }

        @Override // org.chromium.arc.mojom.DigitalGoodsInstance
        public void deprecatedListPurchases(String str, String str2, DigitalGoodsInstance.DeprecatedListPurchasesResponse deprecatedListPurchasesResponse) {
            DigitalGoodsInstanceDeprecatedListPurchasesParams digitalGoodsInstanceDeprecatedListPurchasesParams = new DigitalGoodsInstanceDeprecatedListPurchasesParams();
            digitalGoodsInstanceDeprecatedListPurchasesParams.packageName = str;
            digitalGoodsInstanceDeprecatedListPurchasesParams.scope = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsInstanceDeprecatedListPurchasesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new DigitalGoodsInstanceDeprecatedListPurchasesResponseParamsForwardToCallback(deprecatedListPurchasesResponse));
        }

        @Override // org.chromium.arc.mojom.DigitalGoodsInstance
        public void getDetails(String str, String str2, String[] strArr, DigitalGoodsInstance.GetDetailsResponse getDetailsResponse) {
            DigitalGoodsInstanceGetDetailsParams digitalGoodsInstanceGetDetailsParams = new DigitalGoodsInstanceGetDetailsParams();
            digitalGoodsInstanceGetDetailsParams.packageName = str;
            digitalGoodsInstanceGetDetailsParams.scope = str2;
            digitalGoodsInstanceGetDetailsParams.itemIds = strArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsInstanceGetDetailsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new DigitalGoodsInstanceGetDetailsResponseParamsForwardToCallback(getDetailsResponse));
        }

        @Override // org.chromium.arc.mojom.DigitalGoodsInstance
        public void listPurchaseHistory(String str, String str2, DigitalGoodsInstance.ListPurchaseHistoryResponse listPurchaseHistoryResponse) {
            DigitalGoodsInstanceListPurchaseHistoryParams digitalGoodsInstanceListPurchaseHistoryParams = new DigitalGoodsInstanceListPurchaseHistoryParams();
            digitalGoodsInstanceListPurchaseHistoryParams.packageName = str;
            digitalGoodsInstanceListPurchaseHistoryParams.scope = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsInstanceListPurchaseHistoryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new DigitalGoodsInstanceListPurchaseHistoryResponseParamsForwardToCallback(listPurchaseHistoryResponse));
        }

        @Override // org.chromium.arc.mojom.DigitalGoodsInstance
        public void listPurchases(String str, String str2, DigitalGoodsInstance.ListPurchasesResponse listPurchasesResponse) {
            DigitalGoodsInstanceListPurchasesParams digitalGoodsInstanceListPurchasesParams = new DigitalGoodsInstanceListPurchasesParams();
            digitalGoodsInstanceListPurchasesParams.packageName = str;
            digitalGoodsInstanceListPurchasesParams.scope = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsInstanceListPurchasesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new DigitalGoodsInstanceListPurchasesResponseParamsForwardToCallback(listPurchasesResponse));
        }
    }

    static final class Stub extends Interface.Stub<DigitalGoodsInstance> {
        Stub(Core core, DigitalGoodsInstance digitalGoodsInstance) {
            super(core, digitalGoodsInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(DigitalGoodsInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                switch (header.getType()) {
                    case 0:
                        DigitalGoodsInstanceGetDetailsParams digitalGoodsInstanceGetDetailsParamsDeserialize = DigitalGoodsInstanceGetDetailsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getDetails(digitalGoodsInstanceGetDetailsParamsDeserialize.packageName, digitalGoodsInstanceGetDetailsParamsDeserialize.scope, digitalGoodsInstanceGetDetailsParamsDeserialize.itemIds, new DigitalGoodsInstanceGetDetailsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 1:
                        DigitalGoodsInstanceAcknowledgeParams digitalGoodsInstanceAcknowledgeParamsDeserialize = DigitalGoodsInstanceAcknowledgeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().acknowledge(digitalGoodsInstanceAcknowledgeParamsDeserialize.packageName, digitalGoodsInstanceAcknowledgeParamsDeserialize.scope, digitalGoodsInstanceAcknowledgeParamsDeserialize.purchaseToken, digitalGoodsInstanceAcknowledgeParamsDeserialize.makeAvailableAgain, new DigitalGoodsInstanceAcknowledgeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        DigitalGoodsInstanceDeprecatedListPurchasesParams digitalGoodsInstanceDeprecatedListPurchasesParamsDeserialize = DigitalGoodsInstanceDeprecatedListPurchasesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deprecatedListPurchases(digitalGoodsInstanceDeprecatedListPurchasesParamsDeserialize.packageName, digitalGoodsInstanceDeprecatedListPurchasesParamsDeserialize.scope, new DigitalGoodsInstanceDeprecatedListPurchasesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        DigitalGoodsInstanceListPurchasesParams digitalGoodsInstanceListPurchasesParamsDeserialize = DigitalGoodsInstanceListPurchasesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().listPurchases(digitalGoodsInstanceListPurchasesParamsDeserialize.packageName, digitalGoodsInstanceListPurchasesParamsDeserialize.scope, new DigitalGoodsInstanceListPurchasesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        DigitalGoodsInstanceListPurchaseHistoryParams digitalGoodsInstanceListPurchaseHistoryParamsDeserialize = DigitalGoodsInstanceListPurchaseHistoryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().listPurchaseHistory(digitalGoodsInstanceListPurchaseHistoryParamsDeserialize.packageName, digitalGoodsInstanceListPurchaseHistoryParamsDeserialize.scope, new DigitalGoodsInstanceListPurchaseHistoryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        DigitalGoodsInstanceConsumeParams digitalGoodsInstanceConsumeParamsDeserialize = DigitalGoodsInstanceConsumeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().consume(digitalGoodsInstanceConsumeParamsDeserialize.packageName, digitalGoodsInstanceConsumeParamsDeserialize.scope, digitalGoodsInstanceConsumeParamsDeserialize.purchaseToken, new DigitalGoodsInstanceConsumeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                    return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

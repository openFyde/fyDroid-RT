package org.chromium.payments.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
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
import org.chromium.payments.mojom.DigitalGoods;

class DigitalGoods_Internal {
    public static final Interface.Manager<DigitalGoods, DigitalGoods.Proxy> MANAGER = new Interface.Manager<DigitalGoods, DigitalGoods.Proxy>() { // from class: org.chromium.payments.mojom.DigitalGoods_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DigitalGoods[] buildArray(int i) {
            return new DigitalGoods[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DigitalGoods.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<DigitalGoods> buildStub(Core core, DigitalGoods r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "payments.mojom.DigitalGoods";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class DigitalGoodsConsumeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String purchaseToken;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsConsumeParams() {
            super(16, 0);
        }

        public static DigitalGoodsConsumeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsConsumeParams digitalGoodsConsumeParams = new DigitalGoodsConsumeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                digitalGoodsConsumeParams.purchaseToken = decoder.readString(8, false);
                return digitalGoodsConsumeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.purchaseToken, 8, false);
        }

        private DigitalGoodsConsumeParams(int i) {
            super(16, i);
        }
    }

    static final class DigitalGoodsConsumeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsConsumeResponseParams() {
            super(16, 0);
        }

        public static DigitalGoodsConsumeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DigitalGoodsConsumeResponseParams digitalGoodsConsumeResponseParams = new DigitalGoodsConsumeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                digitalGoodsConsumeResponseParams.code = i;
                BillingResponseCode.validate(i);
                return digitalGoodsConsumeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.code, 8);
        }

        private DigitalGoodsConsumeResponseParams(int i) {
            super(16, i);
        }
    }

    static class DigitalGoodsConsumeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoods.ConsumeResponse mCallback;

        DigitalGoodsConsumeResponseParamsForwardToCallback(DigitalGoods.ConsumeResponse consumeResponse) {
            this.mCallback = consumeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(DigitalGoodsConsumeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).code));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsConsumeResponseParamsProxyToResponder implements DigitalGoods.ConsumeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsConsumeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            DigitalGoodsConsumeResponseParams digitalGoodsConsumeResponseParams = new DigitalGoodsConsumeResponseParams();
            digitalGoodsConsumeResponseParams.code = num.intValue();
            this.mMessageReceiver.accept(digitalGoodsConsumeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsGetDetailsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] itemIds;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsGetDetailsParams() {
            super(16, 0);
        }

        public static DigitalGoodsGetDetailsParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsGetDetailsParams digitalGoodsGetDetailsParams = new DigitalGoodsGetDetailsParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsGetDetailsParams.itemIds = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    digitalGoodsGetDetailsParams.itemIds[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return digitalGoodsGetDetailsParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.itemIds;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.itemIds;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private DigitalGoodsGetDetailsParams(int i) {
            super(16, i);
        }
    }

    static final class DigitalGoodsGetDetailsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public ItemDetails[] itemDetailsList;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsGetDetailsResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsGetDetailsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsGetDetailsResponseParams digitalGoodsGetDetailsResponseParams = new DigitalGoodsGetDetailsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                digitalGoodsGetDetailsResponseParams.code = i;
                BillingResponseCode.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsGetDetailsResponseParams.itemDetailsList = new ItemDetails[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    digitalGoodsGetDetailsResponseParams.itemDetailsList[i2] = ItemDetails.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return digitalGoodsGetDetailsResponseParams;
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

        private DigitalGoodsGetDetailsResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsGetDetailsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoods.GetDetailsResponse mCallback;

        DigitalGoodsGetDetailsResponseParamsForwardToCallback(DigitalGoods.GetDetailsResponse getDetailsResponse) {
            this.mCallback = getDetailsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                DigitalGoodsGetDetailsResponseParams digitalGoodsGetDetailsResponseParamsDeserialize = DigitalGoodsGetDetailsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsGetDetailsResponseParamsDeserialize.code), digitalGoodsGetDetailsResponseParamsDeserialize.itemDetailsList);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsGetDetailsResponseParamsProxyToResponder implements DigitalGoods.GetDetailsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsGetDetailsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, ItemDetails[] itemDetailsArr) {
            DigitalGoodsGetDetailsResponseParams digitalGoodsGetDetailsResponseParams = new DigitalGoodsGetDetailsResponseParams();
            digitalGoodsGetDetailsResponseParams.code = num.intValue();
            digitalGoodsGetDetailsResponseParams.itemDetailsList = itemDetailsArr;
            this.mMessageReceiver.accept(digitalGoodsGetDetailsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsListPurchaseHistoryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsListPurchaseHistoryParams() {
            super(8, 0);
        }

        public static DigitalGoodsListPurchaseHistoryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new DigitalGoodsListPurchaseHistoryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private DigitalGoodsListPurchaseHistoryParams(int i) {
            super(8, i);
        }
    }

    static final class DigitalGoodsListPurchaseHistoryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public PurchaseReference[] purchaseReferenceList;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsListPurchaseHistoryResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsListPurchaseHistoryResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsListPurchaseHistoryResponseParams digitalGoodsListPurchaseHistoryResponseParams = new DigitalGoodsListPurchaseHistoryResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                digitalGoodsListPurchaseHistoryResponseParams.code = i;
                BillingResponseCode.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsListPurchaseHistoryResponseParams.purchaseReferenceList = new PurchaseReference[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    digitalGoodsListPurchaseHistoryResponseParams.purchaseReferenceList[i2] = PurchaseReference.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return digitalGoodsListPurchaseHistoryResponseParams;
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

        private DigitalGoodsListPurchaseHistoryResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsListPurchaseHistoryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoods.ListPurchaseHistoryResponse mCallback;

        DigitalGoodsListPurchaseHistoryResponseParamsForwardToCallback(DigitalGoods.ListPurchaseHistoryResponse listPurchaseHistoryResponse) {
            this.mCallback = listPurchaseHistoryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                DigitalGoodsListPurchaseHistoryResponseParams digitalGoodsListPurchaseHistoryResponseParamsDeserialize = DigitalGoodsListPurchaseHistoryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsListPurchaseHistoryResponseParamsDeserialize.code), digitalGoodsListPurchaseHistoryResponseParamsDeserialize.purchaseReferenceList);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsListPurchaseHistoryResponseParamsProxyToResponder implements DigitalGoods.ListPurchaseHistoryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsListPurchaseHistoryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, PurchaseReference[] purchaseReferenceArr) {
            DigitalGoodsListPurchaseHistoryResponseParams digitalGoodsListPurchaseHistoryResponseParams = new DigitalGoodsListPurchaseHistoryResponseParams();
            digitalGoodsListPurchaseHistoryResponseParams.code = num.intValue();
            digitalGoodsListPurchaseHistoryResponseParams.purchaseReferenceList = purchaseReferenceArr;
            this.mMessageReceiver.accept(digitalGoodsListPurchaseHistoryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class DigitalGoodsListPurchasesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsListPurchasesParams() {
            super(8, 0);
        }

        public static DigitalGoodsListPurchasesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new DigitalGoodsListPurchasesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private DigitalGoodsListPurchasesParams(int i) {
            super(8, i);
        }
    }

    static final class DigitalGoodsListPurchasesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int code;
        public PurchaseReference[] purchaseReferenceList;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DigitalGoodsListPurchasesResponseParams() {
            super(24, 0);
        }

        public static DigitalGoodsListPurchasesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                DigitalGoodsListPurchasesResponseParams digitalGoodsListPurchasesResponseParams = new DigitalGoodsListPurchasesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                digitalGoodsListPurchasesResponseParams.code = i;
                BillingResponseCode.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                digitalGoodsListPurchasesResponseParams.purchaseReferenceList = new PurchaseReference[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    digitalGoodsListPurchasesResponseParams.purchaseReferenceList[i2] = PurchaseReference.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return digitalGoodsListPurchasesResponseParams;
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

        private DigitalGoodsListPurchasesResponseParams(int i) {
            super(24, i);
        }
    }

    static class DigitalGoodsListPurchasesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DigitalGoods.ListPurchasesResponse mCallback;

        DigitalGoodsListPurchasesResponseParamsForwardToCallback(DigitalGoods.ListPurchasesResponse listPurchasesResponse) {
            this.mCallback = listPurchasesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                DigitalGoodsListPurchasesResponseParams digitalGoodsListPurchasesResponseParamsDeserialize = DigitalGoodsListPurchasesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(digitalGoodsListPurchasesResponseParamsDeserialize.code), digitalGoodsListPurchasesResponseParamsDeserialize.purchaseReferenceList);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DigitalGoodsListPurchasesResponseParamsProxyToResponder implements DigitalGoods.ListPurchasesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DigitalGoodsListPurchasesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, PurchaseReference[] purchaseReferenceArr) {
            DigitalGoodsListPurchasesResponseParams digitalGoodsListPurchasesResponseParams = new DigitalGoodsListPurchasesResponseParams();
            digitalGoodsListPurchasesResponseParams.code = num.intValue();
            digitalGoodsListPurchasesResponseParams.purchaseReferenceList = purchaseReferenceArr;
            this.mMessageReceiver.accept(digitalGoodsListPurchasesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements DigitalGoods.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.payments.mojom.DigitalGoods
        public void consume(String str, DigitalGoods.ConsumeResponse consumeResponse) {
            DigitalGoodsConsumeParams digitalGoodsConsumeParams = new DigitalGoodsConsumeParams();
            digitalGoodsConsumeParams.purchaseToken = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsConsumeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new DigitalGoodsConsumeResponseParamsForwardToCallback(consumeResponse));
        }

        @Override // org.chromium.payments.mojom.DigitalGoods
        public void getDetails(String[] strArr, DigitalGoods.GetDetailsResponse getDetailsResponse) {
            DigitalGoodsGetDetailsParams digitalGoodsGetDetailsParams = new DigitalGoodsGetDetailsParams();
            digitalGoodsGetDetailsParams.itemIds = strArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(digitalGoodsGetDetailsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new DigitalGoodsGetDetailsResponseParamsForwardToCallback(getDetailsResponse));
        }

        @Override // org.chromium.payments.mojom.DigitalGoods
        public void listPurchaseHistory(DigitalGoods.ListPurchaseHistoryResponse listPurchaseHistoryResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new DigitalGoodsListPurchaseHistoryParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new DigitalGoodsListPurchaseHistoryResponseParamsForwardToCallback(listPurchaseHistoryResponse));
        }

        @Override // org.chromium.payments.mojom.DigitalGoods
        public void listPurchases(DigitalGoods.ListPurchasesResponse listPurchasesResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new DigitalGoodsListPurchasesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new DigitalGoodsListPurchasesResponseParamsForwardToCallback(listPurchasesResponse));
        }
    }

    static final class Stub extends Interface.Stub<DigitalGoods> {
        Stub(Core core, DigitalGoods digitalGoods) {
            super(core, digitalGoods);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(DigitalGoods_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), DigitalGoods_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().getDetails(DigitalGoodsGetDetailsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).itemIds, new DigitalGoodsGetDetailsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    DigitalGoodsListPurchasesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().listPurchases(new DigitalGoodsListPurchasesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 2) {
                    DigitalGoodsListPurchaseHistoryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().listPurchaseHistory(new DigitalGoodsListPurchaseHistoryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().consume(DigitalGoodsConsumeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).purchaseToken, new DigitalGoodsConsumeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

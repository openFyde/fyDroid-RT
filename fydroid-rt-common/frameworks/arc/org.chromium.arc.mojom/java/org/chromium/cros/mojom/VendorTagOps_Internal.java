// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.cros.mojom.VendorTagOps;
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

class VendorTagOps_Internal {
    public static final Interface.Manager<VendorTagOps, VendorTagOps.Proxy> MANAGER = new Interface.Manager<VendorTagOps, VendorTagOps.Proxy>() { // from class: org.chromium.cros.mojom.VendorTagOps_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VendorTagOps[] buildArray(int i) {
            return new VendorTagOps[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public VendorTagOps.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<VendorTagOps> buildStub(Core core, VendorTagOps r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "cros.mojom.VendorTagOps";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements VendorTagOps.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.cros.mojom.VendorTagOps
        public void getAllTags(VendorTagOps.GetAllTagsResponse getAllTagsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VendorTagOpsGetAllTagsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new VendorTagOpsGetAllTagsResponseParamsForwardToCallback(getAllTagsResponse));
        }

        @Override // org.chromium.cros.mojom.VendorTagOps
        public void getSectionName(int i, VendorTagOps.GetSectionNameResponse getSectionNameResponse) {
            VendorTagOpsGetSectionNameParams vendorTagOpsGetSectionNameParams = new VendorTagOpsGetSectionNameParams();
            vendorTagOpsGetSectionNameParams.tag = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(vendorTagOpsGetSectionNameParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new VendorTagOpsGetSectionNameResponseParamsForwardToCallback(getSectionNameResponse));
        }

        @Override // org.chromium.cros.mojom.VendorTagOps
        public void getTagCount(VendorTagOps.GetTagCountResponse getTagCountResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VendorTagOpsGetTagCountParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new VendorTagOpsGetTagCountResponseParamsForwardToCallback(getTagCountResponse));
        }

        @Override // org.chromium.cros.mojom.VendorTagOps
        public void getTagName(int i, VendorTagOps.GetTagNameResponse getTagNameResponse) {
            VendorTagOpsGetTagNameParams vendorTagOpsGetTagNameParams = new VendorTagOpsGetTagNameParams();
            vendorTagOpsGetTagNameParams.tag = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(vendorTagOpsGetTagNameParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new VendorTagOpsGetTagNameResponseParamsForwardToCallback(getTagNameResponse));
        }

        @Override // org.chromium.cros.mojom.VendorTagOps
        public void getTagType(int i, VendorTagOps.GetTagTypeResponse getTagTypeResponse) {
            VendorTagOpsGetTagTypeParams vendorTagOpsGetTagTypeParams = new VendorTagOpsGetTagTypeParams();
            vendorTagOpsGetTagTypeParams.tag = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(vendorTagOpsGetTagTypeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new VendorTagOpsGetTagTypeResponseParamsForwardToCallback(getTagTypeResponse));
        }
    }

    static final class Stub extends Interface.Stub<VendorTagOps> {
        Stub(Core core, VendorTagOps vendorTagOps) {
            super(core, vendorTagOps);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(VendorTagOps_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), VendorTagOps_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    VendorTagOpsGetTagCountParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getTagCount(new VendorTagOpsGetTagCountResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    VendorTagOpsGetAllTagsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getAllTags(new VendorTagOpsGetAllTagsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 2) {
                    getImpl().getSectionName(VendorTagOpsGetSectionNameParams.deserialize(serviceMessageAsServiceMessage.getPayload()).tag, new VendorTagOpsGetSectionNameResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 3) {
                    getImpl().getTagName(VendorTagOpsGetTagNameParams.deserialize(serviceMessageAsServiceMessage.getPayload()).tag, new VendorTagOpsGetTagNameResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 4) {
                    return false;
                }
                getImpl().getTagType(VendorTagOpsGetTagTypeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).tag, new VendorTagOpsGetTagTypeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class VendorTagOpsGetAllTagsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetAllTagsParams() {
            super(8, 0);
        }

        public static VendorTagOpsGetAllTagsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VendorTagOpsGetAllTagsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VendorTagOpsGetAllTagsParams(int i) {
            super(8, i);
        }
    }

    static final class VendorTagOpsGetAllTagsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int[] tagArray;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetAllTagsResponseParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetAllTagsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetAllTagsResponseParams vendorTagOpsGetAllTagsResponseParams = new VendorTagOpsGetAllTagsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetAllTagsResponseParams.tagArray = decoder.readInts(8, 0, -1);
                return vendorTagOpsGetAllTagsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.tagArray, 8, 0, -1);
        }

        private VendorTagOpsGetAllTagsResponseParams(int i) {
            super(16, i);
        }
    }

    static class VendorTagOpsGetAllTagsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VendorTagOps.GetAllTagsResponse mCallback;

        VendorTagOpsGetAllTagsResponseParamsForwardToCallback(VendorTagOps.GetAllTagsResponse getAllTagsResponse) {
            this.mCallback = getAllTagsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(VendorTagOpsGetAllTagsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).tagArray);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VendorTagOpsGetAllTagsResponseParamsProxyToResponder implements VendorTagOps.GetAllTagsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VendorTagOpsGetAllTagsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(int[] iArr) {
            VendorTagOpsGetAllTagsResponseParams vendorTagOpsGetAllTagsResponseParams = new VendorTagOpsGetAllTagsResponseParams();
            vendorTagOpsGetAllTagsResponseParams.tagArray = iArr;
            this.mMessageReceiver.accept(vendorTagOpsGetAllTagsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class VendorTagOpsGetSectionNameParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int tag;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetSectionNameParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetSectionNameParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetSectionNameParams vendorTagOpsGetSectionNameParams = new VendorTagOpsGetSectionNameParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetSectionNameParams.tag = decoder.readInt(8);
                return vendorTagOpsGetSectionNameParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.tag, 8);
        }

        private VendorTagOpsGetSectionNameParams(int i) {
            super(16, i);
        }
    }

    static final class VendorTagOpsGetSectionNameResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String name;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetSectionNameResponseParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetSectionNameResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetSectionNameResponseParams vendorTagOpsGetSectionNameResponseParams = new VendorTagOpsGetSectionNameResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetSectionNameResponseParams.name = decoder.readString(8, true);
                return vendorTagOpsGetSectionNameResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.name, 8, true);
        }

        private VendorTagOpsGetSectionNameResponseParams(int i) {
            super(16, i);
        }
    }

    static class VendorTagOpsGetSectionNameResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VendorTagOps.GetSectionNameResponse mCallback;

        VendorTagOpsGetSectionNameResponseParamsForwardToCallback(VendorTagOps.GetSectionNameResponse getSectionNameResponse) {
            this.mCallback = getSectionNameResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(VendorTagOpsGetSectionNameResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).name);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VendorTagOpsGetSectionNameResponseParamsProxyToResponder implements VendorTagOps.GetSectionNameResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VendorTagOpsGetSectionNameResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            VendorTagOpsGetSectionNameResponseParams vendorTagOpsGetSectionNameResponseParams = new VendorTagOpsGetSectionNameResponseParams();
            vendorTagOpsGetSectionNameResponseParams.name = str;
            this.mMessageReceiver.accept(vendorTagOpsGetSectionNameResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class VendorTagOpsGetTagCountParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetTagCountParams() {
            super(8, 0);
        }

        public static VendorTagOpsGetTagCountParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new VendorTagOpsGetTagCountParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private VendorTagOpsGetTagCountParams(int i) {
            super(8, i);
        }
    }

    static final class VendorTagOpsGetTagCountResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetTagCountResponseParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetTagCountResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetTagCountResponseParams vendorTagOpsGetTagCountResponseParams = new VendorTagOpsGetTagCountResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetTagCountResponseParams.result = decoder.readInt(8);
                return vendorTagOpsGetTagCountResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private VendorTagOpsGetTagCountResponseParams(int i) {
            super(16, i);
        }
    }

    static class VendorTagOpsGetTagCountResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VendorTagOps.GetTagCountResponse mCallback;

        VendorTagOpsGetTagCountResponseParamsForwardToCallback(VendorTagOps.GetTagCountResponse getTagCountResponse) {
            this.mCallback = getTagCountResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VendorTagOpsGetTagCountResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VendorTagOpsGetTagCountResponseParamsProxyToResponder implements VendorTagOps.GetTagCountResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VendorTagOpsGetTagCountResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VendorTagOpsGetTagCountResponseParams vendorTagOpsGetTagCountResponseParams = new VendorTagOpsGetTagCountResponseParams();
            vendorTagOpsGetTagCountResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(vendorTagOpsGetTagCountResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class VendorTagOpsGetTagNameParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int tag;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetTagNameParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetTagNameParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetTagNameParams vendorTagOpsGetTagNameParams = new VendorTagOpsGetTagNameParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetTagNameParams.tag = decoder.readInt(8);
                return vendorTagOpsGetTagNameParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.tag, 8);
        }

        private VendorTagOpsGetTagNameParams(int i) {
            super(16, i);
        }
    }

    static final class VendorTagOpsGetTagNameResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String name;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetTagNameResponseParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetTagNameResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetTagNameResponseParams vendorTagOpsGetTagNameResponseParams = new VendorTagOpsGetTagNameResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetTagNameResponseParams.name = decoder.readString(8, true);
                return vendorTagOpsGetTagNameResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.name, 8, true);
        }

        private VendorTagOpsGetTagNameResponseParams(int i) {
            super(16, i);
        }
    }

    static class VendorTagOpsGetTagNameResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VendorTagOps.GetTagNameResponse mCallback;

        VendorTagOpsGetTagNameResponseParamsForwardToCallback(VendorTagOps.GetTagNameResponse getTagNameResponse) {
            this.mCallback = getTagNameResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(VendorTagOpsGetTagNameResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).name);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VendorTagOpsGetTagNameResponseParamsProxyToResponder implements VendorTagOps.GetTagNameResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VendorTagOpsGetTagNameResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            VendorTagOpsGetTagNameResponseParams vendorTagOpsGetTagNameResponseParams = new VendorTagOpsGetTagNameResponseParams();
            vendorTagOpsGetTagNameResponseParams.name = str;
            this.mMessageReceiver.accept(vendorTagOpsGetTagNameResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class VendorTagOpsGetTagTypeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int tag;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetTagTypeParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetTagTypeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetTagTypeParams vendorTagOpsGetTagTypeParams = new VendorTagOpsGetTagTypeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetTagTypeParams.tag = decoder.readInt(8);
                return vendorTagOpsGetTagTypeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.tag, 8);
        }

        private VendorTagOpsGetTagTypeParams(int i) {
            super(16, i);
        }
    }

    static final class VendorTagOpsGetTagTypeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public VendorTagOpsGetTagTypeResponseParams() {
            super(16, 0);
        }

        public static VendorTagOpsGetTagTypeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                VendorTagOpsGetTagTypeResponseParams vendorTagOpsGetTagTypeResponseParams = new VendorTagOpsGetTagTypeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                vendorTagOpsGetTagTypeResponseParams.type = decoder.readInt(8);
                return vendorTagOpsGetTagTypeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private VendorTagOpsGetTagTypeResponseParams(int i) {
            super(16, i);
        }
    }

    static class VendorTagOpsGetTagTypeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VendorTagOps.GetTagTypeResponse mCallback;

        VendorTagOpsGetTagTypeResponseParamsForwardToCallback(VendorTagOps.GetTagTypeResponse getTagTypeResponse) {
            this.mCallback = getTagTypeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(VendorTagOpsGetTagTypeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class VendorTagOpsGetTagTypeResponseParamsProxyToResponder implements VendorTagOps.GetTagTypeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VendorTagOpsGetTagTypeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            VendorTagOpsGetTagTypeResponseParams vendorTagOpsGetTagTypeResponseParams = new VendorTagOpsGetTagTypeResponseParams();
            vendorTagOpsGetTagTypeResponseParams.type = num.intValue();
            this.mMessageReceiver.accept(vendorTagOpsGetTagTypeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }
}

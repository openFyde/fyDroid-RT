package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.OemCryptoService;
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

class OemCryptoService_Internal {
    public static final Interface.Manager<OemCryptoService, OemCryptoService.Proxy> MANAGER = new Interface.Manager<OemCryptoService, OemCryptoService.Proxy>() { // from class: org.chromium.arc.mojom.OemCryptoService_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public OemCryptoService[] buildArray(int i) {
            return new OemCryptoService[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public OemCryptoService.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<OemCryptoService> buildStub(Core core, OemCryptoService r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.OemCryptoService";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class OemCryptoServiceBuildInformationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceBuildInformationParams() {
            super(8, 0);
        }

        public static OemCryptoServiceBuildInformationParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceBuildInformationParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceBuildInformationParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceBuildInformationResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String buildInformation;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceBuildInformationResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceBuildInformationResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceBuildInformationResponseParams oemCryptoServiceBuildInformationResponseParams = new OemCryptoServiceBuildInformationResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceBuildInformationResponseParams.buildInformation = decoder.readString(8, false);
                return oemCryptoServiceBuildInformationResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.buildInformation, 8, false);
        }

        private OemCryptoServiceBuildInformationResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceBuildInformationResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.BuildInformationResponse mCallback;

        OemCryptoServiceBuildInformationResponseParamsForwardToCallback(OemCryptoService.BuildInformationResponse buildInformationResponse) {
            this.mCallback = buildInformationResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(60, 2)) {
                    return false;
                }
                this.mCallback.call(OemCryptoServiceBuildInformationResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).buildInformation);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceBuildInformationResponseParamsProxyToResponder implements OemCryptoService.BuildInformationResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceBuildInformationResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            OemCryptoServiceBuildInformationResponseParams oemCryptoServiceBuildInformationResponseParams = new OemCryptoServiceBuildInformationResponseParams();
            oemCryptoServiceBuildInformationResponseParams.buildInformation = str;
            this.mMessageReceiver.accept(oemCryptoServiceBuildInformationResponseParams.serializeWithHeader(this.mCore, new MessageHeader(60, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceCloseSessionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCloseSessionParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCloseSessionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCloseSessionParams oemCryptoServiceCloseSessionParams = new OemCryptoServiceCloseSessionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceCloseSessionParams.session = decoder.readInt(8);
                return oemCryptoServiceCloseSessionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.session, 8);
        }

        private OemCryptoServiceCloseSessionParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceCloseSessionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCloseSessionResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCloseSessionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCloseSessionResponseParams oemCryptoServiceCloseSessionResponseParams = new OemCryptoServiceCloseSessionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceCloseSessionResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceCloseSessionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceCloseSessionResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceCloseSessionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.CloseSessionResponse mCallback;

        OemCryptoServiceCloseSessionResponseParamsForwardToCallback(OemCryptoService.CloseSessionResponse closeSessionResponse) {
            this.mCallback = closeSessionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceCloseSessionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceCloseSessionResponseParamsProxyToResponder implements OemCryptoService.CloseSessionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceCloseSessionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceCloseSessionResponseParams oemCryptoServiceCloseSessionResponseParams = new OemCryptoServiceCloseSessionResponseParams();
            oemCryptoServiceCloseSessionResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceCloseSessionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceCopyBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public OemCryptoSecureBuffer outBuffer;
        public int session;
        public byte subsampleFlags;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCopyBufferParams() {
            super(32, 0);
        }

        public static OemCryptoServiceCopyBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCopyBufferParams oemCryptoServiceCopyBufferParams = new OemCryptoServiceCopyBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceCopyBufferParams.session = decoder.readInt(8);
                oemCryptoServiceCopyBufferParams.subsampleFlags = decoder.readByte(12);
                oemCryptoServiceCopyBufferParams.data = decoder.readBytes(16, 0, -1);
                oemCryptoServiceCopyBufferParams.outBuffer = OemCryptoSecureBuffer.decode(decoder.readPointer(24, false));
                return oemCryptoServiceCopyBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.subsampleFlags, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode((Struct) this.outBuffer, 24, false);
        }

        private OemCryptoServiceCopyBufferParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceCopyBufferResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCopyBufferResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCopyBufferResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCopyBufferResponseParams oemCryptoServiceCopyBufferResponseParams = new OemCryptoServiceCopyBufferResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceCopyBufferResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceCopyBufferResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceCopyBufferResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceCopyBufferResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.CopyBufferResponse mCallback;

        OemCryptoServiceCopyBufferResponseParamsForwardToCallback(OemCryptoService.CopyBufferResponse copyBufferResponse) {
            this.mCallback = copyBufferResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(76, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceCopyBufferResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceCopyBufferResponseParamsProxyToResponder implements OemCryptoService.CopyBufferResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceCopyBufferResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceCopyBufferResponseParams oemCryptoServiceCopyBufferResponseParams = new OemCryptoServiceCopyBufferResponseParams();
            oemCryptoServiceCopyBufferResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceCopyBufferResponseParams.serializeWithHeader(this.mCore, new MessageHeader(76, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceCopyBufferV14Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public OemCryptoSecureBuffer outBuffer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCopyBufferV14Params() {
            super(24, 0);
        }

        public static OemCryptoServiceCopyBufferV14Params deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCopyBufferV14Params oemCryptoServiceCopyBufferV14Params = new OemCryptoServiceCopyBufferV14Params(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceCopyBufferV14Params.data = decoder.readBytes(8, 0, -1);
                oemCryptoServiceCopyBufferV14Params.outBuffer = OemCryptoSecureBuffer.decode(decoder.readPointer(16, false));
                return oemCryptoServiceCopyBufferV14Params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.data, 8, 0, -1);
            encoderAtDataOffset.encode((Struct) this.outBuffer, 16, false);
        }

        private OemCryptoServiceCopyBufferV14Params(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceCopyBufferV14ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCopyBufferV14ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCopyBufferV14ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCopyBufferV14ResponseParams oemCryptoServiceCopyBufferV14ResponseParams = new OemCryptoServiceCopyBufferV14ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceCopyBufferV14ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceCopyBufferV14ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceCopyBufferV14ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceCopyBufferV14ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.CopyBufferV14Response mCallback;

        OemCryptoServiceCopyBufferV14ResponseParamsForwardToCallback(OemCryptoService.CopyBufferV14Response copyBufferV14Response) {
            this.mCallback = copyBufferV14Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(16, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceCopyBufferV14ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceCopyBufferV14ResponseParamsProxyToResponder implements OemCryptoService.CopyBufferV14Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceCopyBufferV14ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceCopyBufferV14ResponseParams oemCryptoServiceCopyBufferV14ResponseParams = new OemCryptoServiceCopyBufferV14ResponseParams();
            oemCryptoServiceCopyBufferV14ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceCopyBufferV14ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(16, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceCopyOldUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] pst;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCopyOldUsageEntryParams() {
            super(24, 0);
        }

        public static OemCryptoServiceCopyOldUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCopyOldUsageEntryParams oemCryptoServiceCopyOldUsageEntryParams = new OemCryptoServiceCopyOldUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceCopyOldUsageEntryParams.session = decoder.readInt(8);
                oemCryptoServiceCopyOldUsageEntryParams.pst = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceCopyOldUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.pst, 16, 0, -1);
        }

        private OemCryptoServiceCopyOldUsageEntryParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceCopyOldUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCopyOldUsageEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCopyOldUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCopyOldUsageEntryResponseParams oemCryptoServiceCopyOldUsageEntryResponseParams = new OemCryptoServiceCopyOldUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceCopyOldUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceCopyOldUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceCopyOldUsageEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceCopyOldUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.CopyOldUsageEntryResponse mCallback;

        OemCryptoServiceCopyOldUsageEntryResponseParamsForwardToCallback(OemCryptoService.CopyOldUsageEntryResponse copyOldUsageEntryResponse) {
            this.mCallback = copyOldUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(51, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceCopyOldUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceCopyOldUsageEntryResponseParamsProxyToResponder implements OemCryptoService.CopyOldUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceCopyOldUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceCopyOldUsageEntryResponseParams oemCryptoServiceCopyOldUsageEntryResponseParams = new OemCryptoServiceCopyOldUsageEntryResponseParams();
            oemCryptoServiceCopyOldUsageEntryResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceCopyOldUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(51, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceCreateNewUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCreateNewUsageEntryParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCreateNewUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCreateNewUsageEntryParams oemCryptoServiceCreateNewUsageEntryParams = new OemCryptoServiceCreateNewUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceCreateNewUsageEntryParams.session = decoder.readInt(8);
                return oemCryptoServiceCreateNewUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.session, 8);
        }

        private OemCryptoServiceCreateNewUsageEntryParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceCreateNewUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public int usageEntryNumber;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCreateNewUsageEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCreateNewUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCreateNewUsageEntryResponseParams oemCryptoServiceCreateNewUsageEntryResponseParams = new OemCryptoServiceCreateNewUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceCreateNewUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceCreateNewUsageEntryResponseParams.usageEntryNumber = decoder.readInt(12);
                return oemCryptoServiceCreateNewUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.usageEntryNumber, 12);
        }

        private OemCryptoServiceCreateNewUsageEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceCreateNewUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.CreateNewUsageEntryResponse mCallback;

        OemCryptoServiceCreateNewUsageEntryResponseParamsForwardToCallback(OemCryptoService.CreateNewUsageEntryResponse createNewUsageEntryResponse) {
            this.mCallback = createNewUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(45, 2)) {
                    return false;
                }
                OemCryptoServiceCreateNewUsageEntryResponseParams oemCryptoServiceCreateNewUsageEntryResponseParamsDeserialize = OemCryptoServiceCreateNewUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceCreateNewUsageEntryResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServiceCreateNewUsageEntryResponseParamsDeserialize.usageEntryNumber));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceCreateNewUsageEntryResponseParamsProxyToResponder implements OemCryptoService.CreateNewUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceCreateNewUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Integer num2) {
            OemCryptoServiceCreateNewUsageEntryResponseParams oemCryptoServiceCreateNewUsageEntryResponseParams = new OemCryptoServiceCreateNewUsageEntryResponseParams();
            oemCryptoServiceCreateNewUsageEntryResponseParams.result = num.intValue();
            oemCryptoServiceCreateNewUsageEntryResponseParams.usageEntryNumber = num2.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceCreateNewUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(45, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceCreateOldUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] clientMacKey;
        public byte[] pst;
        public byte[] serverMacKey;
        public int status;
        public long timeSinceFirstDecrypt;
        public long timeSinceLastDecrypt;
        public long timeSinceLicenseReceived;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(64, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCreateOldUsageEntryParams() {
            super(64, 0);
        }

        public static OemCryptoServiceCreateOldUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCreateOldUsageEntryParams oemCryptoServiceCreateOldUsageEntryParams = new OemCryptoServiceCreateOldUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceCreateOldUsageEntryParams.timeSinceLicenseReceived = decoder.readLong(8);
                oemCryptoServiceCreateOldUsageEntryParams.timeSinceFirstDecrypt = decoder.readLong(16);
                oemCryptoServiceCreateOldUsageEntryParams.timeSinceLastDecrypt = decoder.readLong(24);
                int i = decoder.readInt(32);
                oemCryptoServiceCreateOldUsageEntryParams.status = i;
                OemCryptoUsageEntryStatus.validate(i);
                oemCryptoServiceCreateOldUsageEntryParams.serverMacKey = decoder.readBytes(40, 0, -1);
                oemCryptoServiceCreateOldUsageEntryParams.clientMacKey = decoder.readBytes(48, 0, -1);
                oemCryptoServiceCreateOldUsageEntryParams.pst = decoder.readBytes(56, 0, -1);
                return oemCryptoServiceCreateOldUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.timeSinceLicenseReceived, 8);
            encoderAtDataOffset.encode(this.timeSinceFirstDecrypt, 16);
            encoderAtDataOffset.encode(this.timeSinceLastDecrypt, 24);
            encoderAtDataOffset.encode(this.status, 32);
            encoderAtDataOffset.encode(this.serverMacKey, 40, 0, -1);
            encoderAtDataOffset.encode(this.clientMacKey, 48, 0, -1);
            encoderAtDataOffset.encode(this.pst, 56, 0, -1);
        }

        private OemCryptoServiceCreateOldUsageEntryParams(int i) {
            super(64, i);
        }
    }

    static final class OemCryptoServiceCreateOldUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCreateOldUsageEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCreateOldUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCreateOldUsageEntryResponseParams oemCryptoServiceCreateOldUsageEntryResponseParams = new OemCryptoServiceCreateOldUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceCreateOldUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceCreateOldUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceCreateOldUsageEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceCreateOldUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.CreateOldUsageEntryResponse mCallback;

        OemCryptoServiceCreateOldUsageEntryResponseParamsForwardToCallback(OemCryptoService.CreateOldUsageEntryResponse createOldUsageEntryResponse) {
            this.mCallback = createOldUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(52, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceCreateOldUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceCreateOldUsageEntryResponseParamsProxyToResponder implements OemCryptoService.CreateOldUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceCreateOldUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceCreateOldUsageEntryResponseParams oemCryptoServiceCreateOldUsageEntryResponseParams = new OemCryptoServiceCreateOldUsageEntryResponseParams();
            oemCryptoServiceCreateOldUsageEntryResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceCreateOldUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(52, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceCreateUsageTableHeaderParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int availHeaderLength;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCreateUsageTableHeaderParams() {
            super(16, 0);
        }

        public static OemCryptoServiceCreateUsageTableHeaderParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCreateUsageTableHeaderParams oemCryptoServiceCreateUsageTableHeaderParams = new OemCryptoServiceCreateUsageTableHeaderParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceCreateUsageTableHeaderParams.availHeaderLength = decoder.readInt(8);
                return oemCryptoServiceCreateUsageTableHeaderParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.availHeaderLength, 8);
        }

        private OemCryptoServiceCreateUsageTableHeaderParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceCreateUsageTableHeaderResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] header;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceCreateUsageTableHeaderResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceCreateUsageTableHeaderResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceCreateUsageTableHeaderResponseParams oemCryptoServiceCreateUsageTableHeaderResponseParams = new OemCryptoServiceCreateUsageTableHeaderResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceCreateUsageTableHeaderResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceCreateUsageTableHeaderResponseParams.header = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceCreateUsageTableHeaderResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.header, 16, 1, -1);
        }

        private OemCryptoServiceCreateUsageTableHeaderResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceCreateUsageTableHeaderResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.CreateUsageTableHeaderResponse mCallback;

        OemCryptoServiceCreateUsageTableHeaderResponseParamsForwardToCallback(OemCryptoService.CreateUsageTableHeaderResponse createUsageTableHeaderResponse) {
            this.mCallback = createUsageTableHeaderResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(43, 2)) {
                    return false;
                }
                OemCryptoServiceCreateUsageTableHeaderResponseParams oemCryptoServiceCreateUsageTableHeaderResponseParamsDeserialize = OemCryptoServiceCreateUsageTableHeaderResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceCreateUsageTableHeaderResponseParamsDeserialize.result), oemCryptoServiceCreateUsageTableHeaderResponseParamsDeserialize.header);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceCreateUsageTableHeaderResponseParamsProxyToResponder implements OemCryptoService.CreateUsageTableHeaderResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceCreateUsageTableHeaderResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceCreateUsageTableHeaderResponseParams oemCryptoServiceCreateUsageTableHeaderResponseParams = new OemCryptoServiceCreateUsageTableHeaderResponseParams();
            oemCryptoServiceCreateUsageTableHeaderResponseParams.result = num.intValue();
            oemCryptoServiceCreateUsageTableHeaderResponseParams.header = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceCreateUsageTableHeaderResponseParams.serializeWithHeader(this.mCore, new MessageHeader(43, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceDeactivateUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] pst;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeactivateUsageEntryParams() {
            super(24, 0);
        }

        public static OemCryptoServiceDeactivateUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeactivateUsageEntryParams oemCryptoServiceDeactivateUsageEntryParams = new OemCryptoServiceDeactivateUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceDeactivateUsageEntryParams.session = decoder.readInt(8);
                oemCryptoServiceDeactivateUsageEntryParams.pst = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceDeactivateUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.pst, 16, 0, -1);
        }

        private OemCryptoServiceDeactivateUsageEntryParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceDeactivateUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeactivateUsageEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceDeactivateUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeactivateUsageEntryResponseParams oemCryptoServiceDeactivateUsageEntryResponseParams = new OemCryptoServiceDeactivateUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceDeactivateUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceDeactivateUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceDeactivateUsageEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceDeactivateUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.DeactivateUsageEntryResponse mCallback;

        OemCryptoServiceDeactivateUsageEntryResponseParamsForwardToCallback(OemCryptoService.DeactivateUsageEntryResponse deactivateUsageEntryResponse) {
            this.mCallback = deactivateUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(48, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceDeactivateUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceDeactivateUsageEntryResponseParamsProxyToResponder implements OemCryptoService.DeactivateUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceDeactivateUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceDeactivateUsageEntryResponseParams oemCryptoServiceDeactivateUsageEntryResponseParams = new OemCryptoServiceDeactivateUsageEntryResponseParams();
            oemCryptoServiceDeactivateUsageEntryResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceDeactivateUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(48, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceDeactivateUsageEntryV12Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] pst;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeactivateUsageEntryV12Params() {
            super(16, 0);
        }

        public static OemCryptoServiceDeactivateUsageEntryV12Params deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeactivateUsageEntryV12Params oemCryptoServiceDeactivateUsageEntryV12Params = new OemCryptoServiceDeactivateUsageEntryV12Params(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceDeactivateUsageEntryV12Params.pst = decoder.readBytes(8, 0, -1);
                return oemCryptoServiceDeactivateUsageEntryV12Params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.pst, 8, 0, -1);
        }

        private OemCryptoServiceDeactivateUsageEntryV12Params(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceDeactivateUsageEntryV12ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeactivateUsageEntryV12ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceDeactivateUsageEntryV12ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeactivateUsageEntryV12ResponseParams oemCryptoServiceDeactivateUsageEntryV12ResponseParams = new OemCryptoServiceDeactivateUsageEntryV12ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceDeactivateUsageEntryV12ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceDeactivateUsageEntryV12ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceDeactivateUsageEntryV12ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceDeactivateUsageEntryV12ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.DeactivateUsageEntryV12Response mCallback;

        OemCryptoServiceDeactivateUsageEntryV12ResponseParamsForwardToCallback(OemCryptoService.DeactivateUsageEntryV12Response deactivateUsageEntryV12Response) {
            this.mCallback = deactivateUsageEntryV12Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(31, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceDeactivateUsageEntryV12ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceDeactivateUsageEntryV12ResponseParamsProxyToResponder implements OemCryptoService.DeactivateUsageEntryV12Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceDeactivateUsageEntryV12ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceDeactivateUsageEntryV12ResponseParams oemCryptoServiceDeactivateUsageEntryV12ResponseParams = new OemCryptoServiceDeactivateUsageEntryV12ResponseParams();
            oemCryptoServiceDeactivateUsageEntryV12ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceDeactivateUsageEntryV12ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(31, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceDecryptCencParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public byte[] iv;
        public OemCryptoCencEncryptPatternDesc pattern;
        public OemCryptoSecureBuffer secureBuffer;
        public int session;
        public SubSampleDescription[] subSamples;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(56, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDecryptCencParams() {
            super(56, 0);
        }

        public static OemCryptoServiceDecryptCencParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceDecryptCencParams oemCryptoServiceDecryptCencParams = new OemCryptoServiceDecryptCencParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceDecryptCencParams.session = decoderOutline3.readInt(8);
                oemCryptoServiceDecryptCencParams.data = decoderOutline3.readBytes(16, 0, -1);
                oemCryptoServiceDecryptCencParams.iv = decoderOutline3.readBytes(24, 0, 16);
                Decoder pointer = decoderOutline3.readPointer(32, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceDecryptCencParams.subSamples = new SubSampleDescription[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    oemCryptoServiceDecryptCencParams.subSamples[i] = SubSampleDescription.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                oemCryptoServiceDecryptCencParams.pattern = OemCryptoCencEncryptPatternDesc.decode(decoderOutline3.readPointer(40, false));
                oemCryptoServiceDecryptCencParams.secureBuffer = OemCryptoSecureBuffer.decode(decoderOutline3.readPointer(48, true));
                return oemCryptoServiceDecryptCencParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode(this.iv, 24, 0, 16);
            SubSampleDescription[] subSampleDescriptionArr = this.subSamples;
            if (subSampleDescriptionArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(subSampleDescriptionArr.length, 32, -1);
                int i = 0;
                while (true) {
                    SubSampleDescription[] subSampleDescriptionArr2 = this.subSamples;
                    if (i >= subSampleDescriptionArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) subSampleDescriptionArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(32, false);
            }
            encoderAtDataOffset.encode((Struct) this.pattern, 40, false);
            encoderAtDataOffset.encode((Struct) this.secureBuffer, 48, true);
        }

        private OemCryptoServiceDecryptCencParams(int i) {
            super(56, i);
        }
    }

    static final class OemCryptoServiceDecryptCencResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] decryptedData;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDecryptCencResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceDecryptCencResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDecryptCencResponseParams oemCryptoServiceDecryptCencResponseParams = new OemCryptoServiceDecryptCencResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceDecryptCencResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceDecryptCencResponseParams.decryptedData = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceDecryptCencResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.decryptedData, 16, 1, -1);
        }

        private OemCryptoServiceDecryptCencResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceDecryptCencResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.DecryptCencResponse mCallback;

        OemCryptoServiceDecryptCencResponseParamsForwardToCallback(OemCryptoService.DecryptCencResponse decryptCencResponse) {
            this.mCallback = decryptCencResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(75, 2)) {
                    return false;
                }
                OemCryptoServiceDecryptCencResponseParams oemCryptoServiceDecryptCencResponseParamsDeserialize = OemCryptoServiceDecryptCencResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceDecryptCencResponseParamsDeserialize.result), oemCryptoServiceDecryptCencResponseParamsDeserialize.decryptedData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceDecryptCencResponseParamsProxyToResponder implements OemCryptoService.DecryptCencResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceDecryptCencResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceDecryptCencResponseParams oemCryptoServiceDecryptCencResponseParams = new OemCryptoServiceDecryptCencResponseParams();
            oemCryptoServiceDecryptCencResponseParams.result = num.intValue();
            oemCryptoServiceDecryptCencResponseParams.decryptedData = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceDecryptCencResponseParams.serializeWithHeader(this.mCore, new MessageHeader(75, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceDecryptCencV15Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int blockOffset;
        public byte[] data;
        public boolean isEncrypted;
        public byte[] iv;
        public OemCryptoCencEncryptPatternDesc pattern;
        public OemCryptoSecureBuffer secureBuffer;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(56, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDecryptCencV15Params() {
            super(56, 0);
        }

        public static OemCryptoServiceDecryptCencV15Params deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDecryptCencV15Params oemCryptoServiceDecryptCencV15Params = new OemCryptoServiceDecryptCencV15Params(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceDecryptCencV15Params.session = decoder.readInt(8);
                oemCryptoServiceDecryptCencV15Params.isEncrypted = decoder.readBoolean(12, 0);
                oemCryptoServiceDecryptCencV15Params.data = decoder.readBytes(16, 0, -1);
                oemCryptoServiceDecryptCencV15Params.iv = decoder.readBytes(24, 0, 16);
                oemCryptoServiceDecryptCencV15Params.blockOffset = decoder.readInt(32);
                oemCryptoServiceDecryptCencV15Params.secureBuffer = OemCryptoSecureBuffer.decode(decoder.readPointer(40, true));
                oemCryptoServiceDecryptCencV15Params.pattern = OemCryptoCencEncryptPatternDesc.decode(decoder.readPointer(48, false));
                return oemCryptoServiceDecryptCencV15Params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.isEncrypted, 12, 0);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode(this.iv, 24, 0, 16);
            encoderAtDataOffset.encode(this.blockOffset, 32);
            encoderAtDataOffset.encode((Struct) this.secureBuffer, 40, true);
            encoderAtDataOffset.encode((Struct) this.pattern, 48, false);
        }

        private OemCryptoServiceDecryptCencV15Params(int i) {
            super(56, i);
        }
    }

    static final class OemCryptoServiceDecryptCencV15ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] decryptedData;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDecryptCencV15ResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceDecryptCencV15ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDecryptCencV15ResponseParams oemCryptoServiceDecryptCencV15ResponseParams = new OemCryptoServiceDecryptCencV15ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceDecryptCencV15ResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceDecryptCencV15ResponseParams.decryptedData = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceDecryptCencV15ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.decryptedData, 16, 1, -1);
        }

        private OemCryptoServiceDecryptCencV15ResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceDecryptCencV15ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.DecryptCencV15Response mCallback;

        OemCryptoServiceDecryptCencV15ResponseParamsForwardToCallback(OemCryptoService.DecryptCencV15Response decryptCencV15Response) {
            this.mCallback = decryptCencV15Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(11, 2)) {
                    return false;
                }
                OemCryptoServiceDecryptCencV15ResponseParams oemCryptoServiceDecryptCencV15ResponseParamsDeserialize = OemCryptoServiceDecryptCencV15ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceDecryptCencV15ResponseParamsDeserialize.result), oemCryptoServiceDecryptCencV15ResponseParamsDeserialize.decryptedData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceDecryptCencV15ResponseParamsProxyToResponder implements OemCryptoService.DecryptCencV15Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceDecryptCencV15ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceDecryptCencV15ResponseParams oemCryptoServiceDecryptCencV15ResponseParams = new OemCryptoServiceDecryptCencV15ResponseParams();
            oemCryptoServiceDecryptCencV15ResponseParams.result = num.intValue();
            oemCryptoServiceDecryptCencV15ResponseParams.decryptedData = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceDecryptCencV15ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(11, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceDeleteOldUsageTableParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeleteOldUsageTableParams() {
            super(8, 0);
        }

        public static OemCryptoServiceDeleteOldUsageTableParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceDeleteOldUsageTableParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceDeleteOldUsageTableParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceDeleteOldUsageTableResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeleteOldUsageTableResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceDeleteOldUsageTableResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeleteOldUsageTableResponseParams oemCryptoServiceDeleteOldUsageTableResponseParams = new OemCryptoServiceDeleteOldUsageTableResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceDeleteOldUsageTableResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceDeleteOldUsageTableResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceDeleteOldUsageTableResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceDeleteOldUsageTableResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.DeleteOldUsageTableResponse mCallback;

        OemCryptoServiceDeleteOldUsageTableResponseParamsForwardToCallback(OemCryptoService.DeleteOldUsageTableResponse deleteOldUsageTableResponse) {
            this.mCallback = deleteOldUsageTableResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(35, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceDeleteOldUsageTableResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceDeleteOldUsageTableResponseParamsProxyToResponder implements OemCryptoService.DeleteOldUsageTableResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceDeleteOldUsageTableResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceDeleteOldUsageTableResponseParams oemCryptoServiceDeleteOldUsageTableResponseParams = new OemCryptoServiceDeleteOldUsageTableResponseParams();
            oemCryptoServiceDeleteOldUsageTableResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceDeleteOldUsageTableResponseParams.serializeWithHeader(this.mCore, new MessageHeader(35, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceDeleteUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] message;
        public int pstLength;
        public int pstOffset;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeleteUsageEntryParams() {
            super(40, 0);
        }

        public static OemCryptoServiceDeleteUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeleteUsageEntryParams oemCryptoServiceDeleteUsageEntryParams = new OemCryptoServiceDeleteUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceDeleteUsageEntryParams.session = decoder.readInt(8);
                oemCryptoServiceDeleteUsageEntryParams.pstOffset = decoder.readInt(12);
                oemCryptoServiceDeleteUsageEntryParams.pstLength = decoder.readInt(16);
                oemCryptoServiceDeleteUsageEntryParams.message = decoder.readBytes(24, 0, -1);
                oemCryptoServiceDeleteUsageEntryParams.signature = decoder.readBytes(32, 0, -1);
                return oemCryptoServiceDeleteUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.pstOffset, 12);
            encoderAtDataOffset.encode(this.pstLength, 16);
            encoderAtDataOffset.encode(this.message, 24, 0, -1);
            encoderAtDataOffset.encode(this.signature, 32, 0, -1);
        }

        private OemCryptoServiceDeleteUsageEntryParams(int i) {
            super(40, i);
        }
    }

    static final class OemCryptoServiceDeleteUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeleteUsageEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceDeleteUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeleteUsageEntryResponseParams oemCryptoServiceDeleteUsageEntryResponseParams = new OemCryptoServiceDeleteUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceDeleteUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceDeleteUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceDeleteUsageEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceDeleteUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.DeleteUsageEntryResponse mCallback;

        OemCryptoServiceDeleteUsageEntryResponseParamsForwardToCallback(OemCryptoService.DeleteUsageEntryResponse deleteUsageEntryResponse) {
            this.mCallback = deleteUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(33, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceDeleteUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceDeleteUsageEntryResponseParamsProxyToResponder implements OemCryptoService.DeleteUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceDeleteUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceDeleteUsageEntryResponseParams oemCryptoServiceDeleteUsageEntryResponseParams = new OemCryptoServiceDeleteUsageEntryResponseParams();
            oemCryptoServiceDeleteUsageEntryResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceDeleteUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(33, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceDeriveKeysFromSessionKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] encKeyContext;
        public byte[] encSessionKey;
        public byte[] macKeyContext;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeriveKeysFromSessionKeyParams() {
            super(40, 0);
        }

        public static OemCryptoServiceDeriveKeysFromSessionKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeriveKeysFromSessionKeyParams oemCryptoServiceDeriveKeysFromSessionKeyParams = new OemCryptoServiceDeriveKeysFromSessionKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceDeriveKeysFromSessionKeyParams.session = decoder.readInt(8);
                oemCryptoServiceDeriveKeysFromSessionKeyParams.encSessionKey = decoder.readBytes(16, 0, -1);
                oemCryptoServiceDeriveKeysFromSessionKeyParams.macKeyContext = decoder.readBytes(24, 0, -1);
                oemCryptoServiceDeriveKeysFromSessionKeyParams.encKeyContext = decoder.readBytes(32, 0, -1);
                return oemCryptoServiceDeriveKeysFromSessionKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.encSessionKey, 16, 0, -1);
            encoderAtDataOffset.encode(this.macKeyContext, 24, 0, -1);
            encoderAtDataOffset.encode(this.encKeyContext, 32, 0, -1);
        }

        private OemCryptoServiceDeriveKeysFromSessionKeyParams(int i) {
            super(40, i);
        }
    }

    static final class OemCryptoServiceDeriveKeysFromSessionKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceDeriveKeysFromSessionKeyResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceDeriveKeysFromSessionKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceDeriveKeysFromSessionKeyResponseParams oemCryptoServiceDeriveKeysFromSessionKeyResponseParams = new OemCryptoServiceDeriveKeysFromSessionKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceDeriveKeysFromSessionKeyResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceDeriveKeysFromSessionKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceDeriveKeysFromSessionKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceDeriveKeysFromSessionKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.DeriveKeysFromSessionKeyResponse mCallback;

        OemCryptoServiceDeriveKeysFromSessionKeyResponseParamsForwardToCallback(OemCryptoService.DeriveKeysFromSessionKeyResponse deriveKeysFromSessionKeyResponse) {
            this.mCallback = deriveKeysFromSessionKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(27, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceDeriveKeysFromSessionKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceDeriveKeysFromSessionKeyResponseParamsProxyToResponder implements OemCryptoService.DeriveKeysFromSessionKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceDeriveKeysFromSessionKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceDeriveKeysFromSessionKeyResponseParams oemCryptoServiceDeriveKeysFromSessionKeyResponseParams = new OemCryptoServiceDeriveKeysFromSessionKeyResponseParams();
            oemCryptoServiceDeriveKeysFromSessionKeyResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceDeriveKeysFromSessionKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(27, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceForceDeleteUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] pst;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceForceDeleteUsageEntryParams() {
            super(16, 0);
        }

        public static OemCryptoServiceForceDeleteUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceForceDeleteUsageEntryParams oemCryptoServiceForceDeleteUsageEntryParams = new OemCryptoServiceForceDeleteUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceForceDeleteUsageEntryParams.pst = decoder.readBytes(8, 0, -1);
                return oemCryptoServiceForceDeleteUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.pst, 8, 0, -1);
        }

        private OemCryptoServiceForceDeleteUsageEntryParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceForceDeleteUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceForceDeleteUsageEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceForceDeleteUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceForceDeleteUsageEntryResponseParams oemCryptoServiceForceDeleteUsageEntryResponseParams = new OemCryptoServiceForceDeleteUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceForceDeleteUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceForceDeleteUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceForceDeleteUsageEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceForceDeleteUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.ForceDeleteUsageEntryResponse mCallback;

        OemCryptoServiceForceDeleteUsageEntryResponseParamsForwardToCallback(OemCryptoService.ForceDeleteUsageEntryResponse forceDeleteUsageEntryResponse) {
            this.mCallback = forceDeleteUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(34, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceForceDeleteUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceForceDeleteUsageEntryResponseParamsProxyToResponder implements OemCryptoService.ForceDeleteUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceForceDeleteUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceForceDeleteUsageEntryResponseParams oemCryptoServiceForceDeleteUsageEntryResponseParams = new OemCryptoServiceForceDeleteUsageEntryResponseParams();
            oemCryptoServiceForceDeleteUsageEntryResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceForceDeleteUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(34, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenerateDerivedKeysParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] encKeyContext;
        public byte[] macKeyContext;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateDerivedKeysParams() {
            super(32, 0);
        }

        public static OemCryptoServiceGenerateDerivedKeysParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateDerivedKeysParams oemCryptoServiceGenerateDerivedKeysParams = new OemCryptoServiceGenerateDerivedKeysParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenerateDerivedKeysParams.session = decoder.readInt(8);
                oemCryptoServiceGenerateDerivedKeysParams.macKeyContext = decoder.readBytes(16, 0, -1);
                oemCryptoServiceGenerateDerivedKeysParams.encKeyContext = decoder.readBytes(24, 0, -1);
                return oemCryptoServiceGenerateDerivedKeysParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.macKeyContext, 16, 0, -1);
            encoderAtDataOffset.encode(this.encKeyContext, 24, 0, -1);
        }

        private OemCryptoServiceGenerateDerivedKeysParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceGenerateDerivedKeysResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateDerivedKeysResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGenerateDerivedKeysResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateDerivedKeysResponseParams oemCryptoServiceGenerateDerivedKeysResponseParams = new OemCryptoServiceGenerateDerivedKeysResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenerateDerivedKeysResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceGenerateDerivedKeysResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceGenerateDerivedKeysResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGenerateDerivedKeysResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenerateDerivedKeysResponse mCallback;

        OemCryptoServiceGenerateDerivedKeysResponseParamsForwardToCallback(OemCryptoService.GenerateDerivedKeysResponse generateDerivedKeysResponse) {
            this.mCallback = generateDerivedKeysResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceGenerateDerivedKeysResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenerateDerivedKeysResponseParamsProxyToResponder implements OemCryptoService.GenerateDerivedKeysResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenerateDerivedKeysResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceGenerateDerivedKeysResponseParams oemCryptoServiceGenerateDerivedKeysResponseParams = new OemCryptoServiceGenerateDerivedKeysResponseParams();
            oemCryptoServiceGenerateDerivedKeysResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGenerateDerivedKeysResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenerateNonceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateNonceParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGenerateNonceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateNonceParams oemCryptoServiceGenerateNonceParams = new OemCryptoServiceGenerateNonceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenerateNonceParams.session = decoder.readInt(8);
                return oemCryptoServiceGenerateNonceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.session, 8);
        }

        private OemCryptoServiceGenerateNonceParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceGenerateNonceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int nonce;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateNonceResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGenerateNonceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateNonceResponseParams oemCryptoServiceGenerateNonceResponseParams = new OemCryptoServiceGenerateNonceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenerateNonceResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGenerateNonceResponseParams.nonce = decoder.readInt(12);
                return oemCryptoServiceGenerateNonceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.nonce, 12);
        }

        private OemCryptoServiceGenerateNonceResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGenerateNonceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenerateNonceResponse mCallback;

        OemCryptoServiceGenerateNonceResponseParamsForwardToCallback(OemCryptoService.GenerateNonceResponse generateNonceResponse) {
            this.mCallback = generateNonceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                OemCryptoServiceGenerateNonceResponseParams oemCryptoServiceGenerateNonceResponseParamsDeserialize = OemCryptoServiceGenerateNonceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGenerateNonceResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServiceGenerateNonceResponseParamsDeserialize.nonce));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenerateNonceResponseParamsProxyToResponder implements OemCryptoService.GenerateNonceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenerateNonceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Integer num2) {
            OemCryptoServiceGenerateNonceResponseParams oemCryptoServiceGenerateNonceResponseParams = new OemCryptoServiceGenerateNonceResponseParams();
            oemCryptoServiceGenerateNonceResponseParams.result = num.intValue();
            oemCryptoServiceGenerateNonceResponseParams.nonce = num2.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGenerateNonceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenerateRsaSignatureParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] message;
        public int paddingScheme;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateRsaSignatureParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenerateRsaSignatureParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateRsaSignatureParams oemCryptoServiceGenerateRsaSignatureParams = new OemCryptoServiceGenerateRsaSignatureParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenerateRsaSignatureParams.session = decoder.readInt(8);
                int i = decoder.readInt(12);
                oemCryptoServiceGenerateRsaSignatureParams.paddingScheme = i;
                OemCryptoRsaPaddingScheme.validate(i);
                oemCryptoServiceGenerateRsaSignatureParams.message = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceGenerateRsaSignatureParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.paddingScheme, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
        }

        private OemCryptoServiceGenerateRsaSignatureParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceGenerateRsaSignatureResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateRsaSignatureResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenerateRsaSignatureResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateRsaSignatureResponseParams oemCryptoServiceGenerateRsaSignatureResponseParams = new OemCryptoServiceGenerateRsaSignatureResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenerateRsaSignatureResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGenerateRsaSignatureResponseParams.signature = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGenerateRsaSignatureResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.signature, 16, 1, -1);
        }

        private OemCryptoServiceGenerateRsaSignatureResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGenerateRsaSignatureResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenerateRsaSignatureResponse mCallback;

        OemCryptoServiceGenerateRsaSignatureResponseParamsForwardToCallback(OemCryptoService.GenerateRsaSignatureResponse generateRsaSignatureResponse) {
            this.mCallback = generateRsaSignatureResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(26, 2)) {
                    return false;
                }
                OemCryptoServiceGenerateRsaSignatureResponseParams oemCryptoServiceGenerateRsaSignatureResponseParamsDeserialize = OemCryptoServiceGenerateRsaSignatureResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGenerateRsaSignatureResponseParamsDeserialize.result), oemCryptoServiceGenerateRsaSignatureResponseParamsDeserialize.signature);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenerateRsaSignatureResponseParamsProxyToResponder implements OemCryptoService.GenerateRsaSignatureResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenerateRsaSignatureResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGenerateRsaSignatureResponseParams oemCryptoServiceGenerateRsaSignatureResponseParams = new OemCryptoServiceGenerateRsaSignatureResponseParams();
            oemCryptoServiceGenerateRsaSignatureResponseParams.result = num.intValue();
            oemCryptoServiceGenerateRsaSignatureResponseParams.signature = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGenerateRsaSignatureResponseParams.serializeWithHeader(this.mCore, new MessageHeader(26, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenerateSignatureParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] message;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateSignatureParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenerateSignatureParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateSignatureParams oemCryptoServiceGenerateSignatureParams = new OemCryptoServiceGenerateSignatureParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenerateSignatureParams.session = decoder.readInt(8);
                oemCryptoServiceGenerateSignatureParams.message = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceGenerateSignatureParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
        }

        private OemCryptoServiceGenerateSignatureParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceGenerateSignatureResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenerateSignatureResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenerateSignatureResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenerateSignatureResponseParams oemCryptoServiceGenerateSignatureResponseParams = new OemCryptoServiceGenerateSignatureResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenerateSignatureResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGenerateSignatureResponseParams.signature = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGenerateSignatureResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.signature, 16, 1, -1);
        }

        private OemCryptoServiceGenerateSignatureResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGenerateSignatureResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenerateSignatureResponse mCallback;

        OemCryptoServiceGenerateSignatureResponseParamsForwardToCallback(OemCryptoService.GenerateSignatureResponse generateSignatureResponse) {
            this.mCallback = generateSignatureResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                OemCryptoServiceGenerateSignatureResponseParams oemCryptoServiceGenerateSignatureResponseParamsDeserialize = OemCryptoServiceGenerateSignatureResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGenerateSignatureResponseParamsDeserialize.result), oemCryptoServiceGenerateSignatureResponseParamsDeserialize.signature);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenerateSignatureResponseParamsProxyToResponder implements OemCryptoService.GenerateSignatureResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenerateSignatureResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGenerateSignatureResponseParams oemCryptoServiceGenerateSignatureResponseParams = new OemCryptoServiceGenerateSignatureResponseParams();
            oemCryptoServiceGenerateSignatureResponseParams.result = num.intValue();
            oemCryptoServiceGenerateSignatureResponseParams.signature = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGenerateSignatureResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenericDecryptParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int algorithm;
        public byte[] data;
        public byte[] iv;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericDecryptParams() {
            super(32, 0);
        }

        public static OemCryptoServiceGenericDecryptParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericDecryptParams oemCryptoServiceGenericDecryptParams = new OemCryptoServiceGenericDecryptParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenericDecryptParams.session = decoder.readInt(8);
                int i = decoder.readInt(12);
                oemCryptoServiceGenericDecryptParams.algorithm = i;
                OemCryptoAlgorithm.validate(i);
                oemCryptoServiceGenericDecryptParams.data = decoder.readBytes(16, 0, -1);
                oemCryptoServiceGenericDecryptParams.iv = decoder.readBytes(24, 0, 16);
                return oemCryptoServiceGenericDecryptParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.algorithm, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode(this.iv, 24, 0, 16);
        }

        private OemCryptoServiceGenericDecryptParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceGenericDecryptResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] decryptedData;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericDecryptResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenericDecryptResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericDecryptResponseParams oemCryptoServiceGenericDecryptResponseParams = new OemCryptoServiceGenericDecryptResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenericDecryptResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGenericDecryptResponseParams.decryptedData = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGenericDecryptResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.decryptedData, 16, 1, -1);
        }

        private OemCryptoServiceGenericDecryptResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGenericDecryptResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenericDecryptResponse mCallback;

        OemCryptoServiceGenericDecryptResponseParamsForwardToCallback(OemCryptoService.GenericDecryptResponse genericDecryptResponse) {
            this.mCallback = genericDecryptResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(13, 2)) {
                    return false;
                }
                OemCryptoServiceGenericDecryptResponseParams oemCryptoServiceGenericDecryptResponseParamsDeserialize = OemCryptoServiceGenericDecryptResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGenericDecryptResponseParamsDeserialize.result), oemCryptoServiceGenericDecryptResponseParamsDeserialize.decryptedData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenericDecryptResponseParamsProxyToResponder implements OemCryptoService.GenericDecryptResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenericDecryptResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGenericDecryptResponseParams oemCryptoServiceGenericDecryptResponseParams = new OemCryptoServiceGenericDecryptResponseParams();
            oemCryptoServiceGenericDecryptResponseParams.result = num.intValue();
            oemCryptoServiceGenericDecryptResponseParams.decryptedData = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGenericDecryptResponseParams.serializeWithHeader(this.mCore, new MessageHeader(13, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenericEncryptParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int algorithm;
        public byte[] data;
        public byte[] iv;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericEncryptParams() {
            super(32, 0);
        }

        public static OemCryptoServiceGenericEncryptParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericEncryptParams oemCryptoServiceGenericEncryptParams = new OemCryptoServiceGenericEncryptParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenericEncryptParams.session = decoder.readInt(8);
                int i = decoder.readInt(12);
                oemCryptoServiceGenericEncryptParams.algorithm = i;
                OemCryptoAlgorithm.validate(i);
                oemCryptoServiceGenericEncryptParams.data = decoder.readBytes(16, 0, -1);
                oemCryptoServiceGenericEncryptParams.iv = decoder.readBytes(24, 0, 16);
                return oemCryptoServiceGenericEncryptParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.algorithm, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode(this.iv, 24, 0, 16);
        }

        private OemCryptoServiceGenericEncryptParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceGenericEncryptResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] encryptedData;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericEncryptResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenericEncryptResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericEncryptResponseParams oemCryptoServiceGenericEncryptResponseParams = new OemCryptoServiceGenericEncryptResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenericEncryptResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGenericEncryptResponseParams.encryptedData = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGenericEncryptResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.encryptedData, 16, 1, -1);
        }

        private OemCryptoServiceGenericEncryptResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGenericEncryptResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenericEncryptResponse mCallback;

        OemCryptoServiceGenericEncryptResponseParamsForwardToCallback(OemCryptoService.GenericEncryptResponse genericEncryptResponse) {
            this.mCallback = genericEncryptResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                OemCryptoServiceGenericEncryptResponseParams oemCryptoServiceGenericEncryptResponseParamsDeserialize = OemCryptoServiceGenericEncryptResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGenericEncryptResponseParamsDeserialize.result), oemCryptoServiceGenericEncryptResponseParamsDeserialize.encryptedData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenericEncryptResponseParamsProxyToResponder implements OemCryptoService.GenericEncryptResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenericEncryptResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGenericEncryptResponseParams oemCryptoServiceGenericEncryptResponseParams = new OemCryptoServiceGenericEncryptResponseParams();
            oemCryptoServiceGenericEncryptResponseParams.result = num.intValue();
            oemCryptoServiceGenericEncryptResponseParams.encryptedData = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGenericEncryptResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenericSignParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int algorithm;
        public byte[] data;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericSignParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenericSignParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericSignParams oemCryptoServiceGenericSignParams = new OemCryptoServiceGenericSignParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenericSignParams.session = decoder.readInt(8);
                int i = decoder.readInt(12);
                oemCryptoServiceGenericSignParams.algorithm = i;
                OemCryptoAlgorithm.validate(i);
                oemCryptoServiceGenericSignParams.data = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceGenericSignParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.algorithm, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
        }

        private OemCryptoServiceGenericSignParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceGenericSignResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericSignResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGenericSignResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericSignResponseParams oemCryptoServiceGenericSignResponseParams = new OemCryptoServiceGenericSignResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenericSignResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGenericSignResponseParams.signature = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGenericSignResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.signature, 16, 1, -1);
        }

        private OemCryptoServiceGenericSignResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGenericSignResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenericSignResponse mCallback;

        OemCryptoServiceGenericSignResponseParamsForwardToCallback(OemCryptoService.GenericSignResponse genericSignResponse) {
            this.mCallback = genericSignResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(14, 2)) {
                    return false;
                }
                OemCryptoServiceGenericSignResponseParams oemCryptoServiceGenericSignResponseParamsDeserialize = OemCryptoServiceGenericSignResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGenericSignResponseParamsDeserialize.result), oemCryptoServiceGenericSignResponseParamsDeserialize.signature);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenericSignResponseParamsProxyToResponder implements OemCryptoService.GenericSignResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenericSignResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGenericSignResponseParams oemCryptoServiceGenericSignResponseParams = new OemCryptoServiceGenericSignResponseParams();
            oemCryptoServiceGenericSignResponseParams.result = num.intValue();
            oemCryptoServiceGenericSignResponseParams.signature = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGenericSignResponseParams.serializeWithHeader(this.mCore, new MessageHeader(14, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGenericVerifyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int algorithm;
        public byte[] data;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericVerifyParams() {
            super(32, 0);
        }

        public static OemCryptoServiceGenericVerifyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericVerifyParams oemCryptoServiceGenericVerifyParams = new OemCryptoServiceGenericVerifyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGenericVerifyParams.session = decoder.readInt(8);
                int i = decoder.readInt(12);
                oemCryptoServiceGenericVerifyParams.algorithm = i;
                OemCryptoAlgorithm.validate(i);
                oemCryptoServiceGenericVerifyParams.data = decoder.readBytes(16, 0, -1);
                oemCryptoServiceGenericVerifyParams.signature = decoder.readBytes(24, 0, -1);
                return oemCryptoServiceGenericVerifyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.algorithm, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
        }

        private OemCryptoServiceGenericVerifyParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceGenericVerifyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGenericVerifyResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGenericVerifyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGenericVerifyResponseParams oemCryptoServiceGenericVerifyResponseParams = new OemCryptoServiceGenericVerifyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGenericVerifyResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceGenericVerifyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceGenericVerifyResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGenericVerifyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GenericVerifyResponse mCallback;

        OemCryptoServiceGenericVerifyResponseParamsForwardToCallback(OemCryptoService.GenericVerifyResponse genericVerifyResponse) {
            this.mCallback = genericVerifyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(15, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceGenericVerifyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGenericVerifyResponseParamsProxyToResponder implements OemCryptoService.GenericVerifyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGenericVerifyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceGenericVerifyResponseParams oemCryptoServiceGenericVerifyResponseParams = new OemCryptoServiceGenericVerifyResponseParams();
            oemCryptoServiceGenericVerifyResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGenericVerifyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(15, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetAnalogOutputFlagsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetAnalogOutputFlagsParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetAnalogOutputFlagsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetAnalogOutputFlagsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetAnalogOutputFlagsParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetAnalogOutputFlagsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetAnalogOutputFlagsResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGetAnalogOutputFlagsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetAnalogOutputFlagsResponseParams oemCryptoServiceGetAnalogOutputFlagsResponseParams = new OemCryptoServiceGetAnalogOutputFlagsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGetAnalogOutputFlagsResponseParams.result = decoder.readInt(8);
                return oemCryptoServiceGetAnalogOutputFlagsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceGetAnalogOutputFlagsResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGetAnalogOutputFlagsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetAnalogOutputFlagsResponse mCallback;

        OemCryptoServiceGetAnalogOutputFlagsResponseParamsForwardToCallback(OemCryptoService.GetAnalogOutputFlagsResponse getAnalogOutputFlagsResponse) {
            this.mCallback = getAnalogOutputFlagsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(53, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceGetAnalogOutputFlagsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetAnalogOutputFlagsResponseParamsProxyToResponder implements OemCryptoService.GetAnalogOutputFlagsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetAnalogOutputFlagsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceGetAnalogOutputFlagsResponseParams oemCryptoServiceGetAnalogOutputFlagsResponseParams = new OemCryptoServiceGetAnalogOutputFlagsResponseParams();
            oemCryptoServiceGetAnalogOutputFlagsResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGetAnalogOutputFlagsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(53, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetCurrentSrmVersionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetCurrentSrmVersionParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetCurrentSrmVersionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetCurrentSrmVersionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetCurrentSrmVersionParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetCurrentSrmVersionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public short version;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetCurrentSrmVersionResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGetCurrentSrmVersionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetCurrentSrmVersionResponseParams oemCryptoServiceGetCurrentSrmVersionResponseParams = new OemCryptoServiceGetCurrentSrmVersionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetCurrentSrmVersionResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGetCurrentSrmVersionResponseParams.version = decoder.readShort(12);
                return oemCryptoServiceGetCurrentSrmVersionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.version, 12);
        }

        private OemCryptoServiceGetCurrentSrmVersionResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGetCurrentSrmVersionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetCurrentSrmVersionResponse mCallback;

        OemCryptoServiceGetCurrentSrmVersionResponseParamsForwardToCallback(OemCryptoService.GetCurrentSrmVersionResponse getCurrentSrmVersionResponse) {
            this.mCallback = getCurrentSrmVersionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(40, 2)) {
                    return false;
                }
                OemCryptoServiceGetCurrentSrmVersionResponseParams oemCryptoServiceGetCurrentSrmVersionResponseParamsDeserialize = OemCryptoServiceGetCurrentSrmVersionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetCurrentSrmVersionResponseParamsDeserialize.result), Short.valueOf(oemCryptoServiceGetCurrentSrmVersionResponseParamsDeserialize.version));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetCurrentSrmVersionResponseParamsProxyToResponder implements OemCryptoService.GetCurrentSrmVersionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetCurrentSrmVersionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Short sh) {
            OemCryptoServiceGetCurrentSrmVersionResponseParams oemCryptoServiceGetCurrentSrmVersionResponseParams = new OemCryptoServiceGetCurrentSrmVersionResponseParams();
            oemCryptoServiceGetCurrentSrmVersionResponseParams.result = num.intValue();
            oemCryptoServiceGetCurrentSrmVersionResponseParams.version = sh.shortValue();
            this.mMessageReceiver.accept(oemCryptoServiceGetCurrentSrmVersionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(40, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetDeviceIdParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetDeviceIdParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetDeviceIdParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetDeviceIdParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetDeviceIdParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetDeviceIdResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] deviceId;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetDeviceIdResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGetDeviceIdResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetDeviceIdResponseParams oemCryptoServiceGetDeviceIdResponseParams = new OemCryptoServiceGetDeviceIdResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetDeviceIdResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGetDeviceIdResponseParams.deviceId = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGetDeviceIdResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.deviceId, 16, 1, -1);
        }

        private OemCryptoServiceGetDeviceIdResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGetDeviceIdResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetDeviceIdResponse mCallback;

        OemCryptoServiceGetDeviceIdResponseParamsForwardToCallback(OemCryptoService.GetDeviceIdResponse getDeviceIdResponse) {
            this.mCallback = getDeviceIdResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(19, 2)) {
                    return false;
                }
                OemCryptoServiceGetDeviceIdResponseParams oemCryptoServiceGetDeviceIdResponseParamsDeserialize = OemCryptoServiceGetDeviceIdResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetDeviceIdResponseParamsDeserialize.result), oemCryptoServiceGetDeviceIdResponseParamsDeserialize.deviceId);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetDeviceIdResponseParamsProxyToResponder implements OemCryptoService.GetDeviceIdResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetDeviceIdResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGetDeviceIdResponseParams oemCryptoServiceGetDeviceIdResponseParams = new OemCryptoServiceGetDeviceIdResponseParams();
            oemCryptoServiceGetDeviceIdResponseParams.result = num.intValue();
            oemCryptoServiceGetDeviceIdResponseParams.deviceId = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGetDeviceIdResponseParams.serializeWithHeader(this.mCore, new MessageHeader(19, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetHdcpCapabilityParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetHdcpCapabilityParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetHdcpCapabilityParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetHdcpCapabilityParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetHdcpCapabilityParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetHdcpCapabilityResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int current;
        public int maximum;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetHdcpCapabilityResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGetHdcpCapabilityResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetHdcpCapabilityResponseParams oemCryptoServiceGetHdcpCapabilityResponseParams = new OemCryptoServiceGetHdcpCapabilityResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetHdcpCapabilityResponseParams.result = i;
                OemCryptoResult.validate(i);
                int i2 = decoder.readInt(12);
                oemCryptoServiceGetHdcpCapabilityResponseParams.current = i2;
                OemCryptoHdcpCapability.validate(i2);
                int i3 = decoder.readInt(16);
                oemCryptoServiceGetHdcpCapabilityResponseParams.maximum = i3;
                OemCryptoHdcpCapability.validate(i3);
                return oemCryptoServiceGetHdcpCapabilityResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.current, 12);
            encoderAtDataOffset.encode(this.maximum, 16);
        }

        private OemCryptoServiceGetHdcpCapabilityResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGetHdcpCapabilityResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetHdcpCapabilityResponse mCallback;

        OemCryptoServiceGetHdcpCapabilityResponseParamsForwardToCallback(OemCryptoService.GetHdcpCapabilityResponse getHdcpCapabilityResponse) {
            this.mCallback = getHdcpCapabilityResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(29, 2)) {
                    return false;
                }
                OemCryptoServiceGetHdcpCapabilityResponseParams oemCryptoServiceGetHdcpCapabilityResponseParamsDeserialize = OemCryptoServiceGetHdcpCapabilityResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetHdcpCapabilityResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServiceGetHdcpCapabilityResponseParamsDeserialize.current), Integer.valueOf(oemCryptoServiceGetHdcpCapabilityResponseParamsDeserialize.maximum));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetHdcpCapabilityResponseParamsProxyToResponder implements OemCryptoService.GetHdcpCapabilityResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetHdcpCapabilityResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Integer num, Integer num2, Integer num3) {
            OemCryptoServiceGetHdcpCapabilityResponseParams oemCryptoServiceGetHdcpCapabilityResponseParams = new OemCryptoServiceGetHdcpCapabilityResponseParams();
            oemCryptoServiceGetHdcpCapabilityResponseParams.result = num.intValue();
            oemCryptoServiceGetHdcpCapabilityResponseParams.current = num2.intValue();
            oemCryptoServiceGetHdcpCapabilityResponseParams.maximum = num3.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGetHdcpCapabilityResponseParams.serializeWithHeader(this.mCore, new MessageHeader(29, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetKeyDataParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetKeyDataParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetKeyDataParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetKeyDataParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetKeyDataParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetKeyDataResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] keyData;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetKeyDataResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGetKeyDataResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetKeyDataResponseParams oemCryptoServiceGetKeyDataResponseParams = new OemCryptoServiceGetKeyDataResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetKeyDataResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGetKeyDataResponseParams.keyData = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGetKeyDataResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.keyData, 16, 1, -1);
        }

        private OemCryptoServiceGetKeyDataResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGetKeyDataResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetKeyDataResponse mCallback;

        OemCryptoServiceGetKeyDataResponseParamsForwardToCallback(OemCryptoService.GetKeyDataResponse getKeyDataResponse) {
            this.mCallback = getKeyDataResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(20, 2)) {
                    return false;
                }
                OemCryptoServiceGetKeyDataResponseParams oemCryptoServiceGetKeyDataResponseParamsDeserialize = OemCryptoServiceGetKeyDataResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetKeyDataResponseParamsDeserialize.result), oemCryptoServiceGetKeyDataResponseParamsDeserialize.keyData);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetKeyDataResponseParamsProxyToResponder implements OemCryptoService.GetKeyDataResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetKeyDataResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGetKeyDataResponseParams oemCryptoServiceGetKeyDataResponseParams = new OemCryptoServiceGetKeyDataResponseParams();
            oemCryptoServiceGetKeyDataResponseParams.result = num.intValue();
            oemCryptoServiceGetKeyDataResponseParams.keyData = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGetKeyDataResponseParams.serializeWithHeader(this.mCore, new MessageHeader(20, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetMaxNumberOfSessionsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetMaxNumberOfSessionsParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetMaxNumberOfSessionsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetMaxNumberOfSessionsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetMaxNumberOfSessionsParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetMaxNumberOfSessionsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int max;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetMaxNumberOfSessionsResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGetMaxNumberOfSessionsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetMaxNumberOfSessionsResponseParams oemCryptoServiceGetMaxNumberOfSessionsResponseParams = new OemCryptoServiceGetMaxNumberOfSessionsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetMaxNumberOfSessionsResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGetMaxNumberOfSessionsResponseParams.max = decoder.readInt(12);
                return oemCryptoServiceGetMaxNumberOfSessionsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.max, 12);
        }

        private OemCryptoServiceGetMaxNumberOfSessionsResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGetMaxNumberOfSessionsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetMaxNumberOfSessionsResponse mCallback;

        OemCryptoServiceGetMaxNumberOfSessionsResponseParamsForwardToCallback(OemCryptoService.GetMaxNumberOfSessionsResponse getMaxNumberOfSessionsResponse) {
            this.mCallback = getMaxNumberOfSessionsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(23, 2)) {
                    return false;
                }
                OemCryptoServiceGetMaxNumberOfSessionsResponseParams oemCryptoServiceGetMaxNumberOfSessionsResponseParamsDeserialize = OemCryptoServiceGetMaxNumberOfSessionsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetMaxNumberOfSessionsResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServiceGetMaxNumberOfSessionsResponseParamsDeserialize.max));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetMaxNumberOfSessionsResponseParamsProxyToResponder implements OemCryptoService.GetMaxNumberOfSessionsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetMaxNumberOfSessionsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Integer num2) {
            OemCryptoServiceGetMaxNumberOfSessionsResponseParams oemCryptoServiceGetMaxNumberOfSessionsResponseParams = new OemCryptoServiceGetMaxNumberOfSessionsResponseParams();
            oemCryptoServiceGetMaxNumberOfSessionsResponseParams.result = num.intValue();
            oemCryptoServiceGetMaxNumberOfSessionsResponseParams.max = num2.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGetMaxNumberOfSessionsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(23, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetNumberOfOpenSessionsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetNumberOfOpenSessionsParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetNumberOfOpenSessionsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetNumberOfOpenSessionsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetNumberOfOpenSessionsParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetNumberOfOpenSessionsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int num;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetNumberOfOpenSessionsResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGetNumberOfOpenSessionsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetNumberOfOpenSessionsResponseParams oemCryptoServiceGetNumberOfOpenSessionsResponseParams = new OemCryptoServiceGetNumberOfOpenSessionsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetNumberOfOpenSessionsResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGetNumberOfOpenSessionsResponseParams.num = decoder.readInt(12);
                return oemCryptoServiceGetNumberOfOpenSessionsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.num, 12);
        }

        private OemCryptoServiceGetNumberOfOpenSessionsResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGetNumberOfOpenSessionsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetNumberOfOpenSessionsResponse mCallback;

        OemCryptoServiceGetNumberOfOpenSessionsResponseParamsForwardToCallback(OemCryptoService.GetNumberOfOpenSessionsResponse getNumberOfOpenSessionsResponse) {
            this.mCallback = getNumberOfOpenSessionsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(22, 2)) {
                    return false;
                }
                OemCryptoServiceGetNumberOfOpenSessionsResponseParams oemCryptoServiceGetNumberOfOpenSessionsResponseParamsDeserialize = OemCryptoServiceGetNumberOfOpenSessionsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetNumberOfOpenSessionsResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServiceGetNumberOfOpenSessionsResponseParamsDeserialize.num));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetNumberOfOpenSessionsResponseParamsProxyToResponder implements OemCryptoService.GetNumberOfOpenSessionsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetNumberOfOpenSessionsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Integer num2) {
            OemCryptoServiceGetNumberOfOpenSessionsResponseParams oemCryptoServiceGetNumberOfOpenSessionsResponseParams = new OemCryptoServiceGetNumberOfOpenSessionsResponseParams();
            oemCryptoServiceGetNumberOfOpenSessionsResponseParams.result = num.intValue();
            oemCryptoServiceGetNumberOfOpenSessionsResponseParams.num = num2.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGetNumberOfOpenSessionsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(22, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetOemPublicCertificateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetOemPublicCertificateParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetOemPublicCertificateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetOemPublicCertificateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetOemPublicCertificateParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetOemPublicCertificateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] publicCert;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetOemPublicCertificateResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGetOemPublicCertificateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetOemPublicCertificateResponseParams oemCryptoServiceGetOemPublicCertificateResponseParams = new OemCryptoServiceGetOemPublicCertificateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetOemPublicCertificateResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGetOemPublicCertificateResponseParams.publicCert = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGetOemPublicCertificateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.publicCert, 16, 1, -1);
        }

        private OemCryptoServiceGetOemPublicCertificateResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGetOemPublicCertificateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetOemPublicCertificateResponse mCallback;

        OemCryptoServiceGetOemPublicCertificateResponseParamsForwardToCallback(OemCryptoService.GetOemPublicCertificateResponse getOemPublicCertificateResponse) {
            this.mCallback = getOemPublicCertificateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(63, 2)) {
                    return false;
                }
                OemCryptoServiceGetOemPublicCertificateResponseParams oemCryptoServiceGetOemPublicCertificateResponseParamsDeserialize = OemCryptoServiceGetOemPublicCertificateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetOemPublicCertificateResponseParamsDeserialize.result), oemCryptoServiceGetOemPublicCertificateResponseParamsDeserialize.publicCert);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetOemPublicCertificateResponseParamsProxyToResponder implements OemCryptoService.GetOemPublicCertificateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetOemPublicCertificateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGetOemPublicCertificateResponseParams oemCryptoServiceGetOemPublicCertificateResponseParams = new OemCryptoServiceGetOemPublicCertificateResponseParams();
            oemCryptoServiceGetOemPublicCertificateResponseParams.result = num.intValue();
            oemCryptoServiceGetOemPublicCertificateResponseParams.publicCert = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGetOemPublicCertificateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(63, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetProvisioningMethodParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetProvisioningMethodParams() {
            super(8, 0);
        }

        public static OemCryptoServiceGetProvisioningMethodParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceGetProvisioningMethodParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceGetProvisioningMethodParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceGetProvisioningMethodResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetProvisioningMethodResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGetProvisioningMethodResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetProvisioningMethodResponseParams oemCryptoServiceGetProvisioningMethodResponseParams = new OemCryptoServiceGetProvisioningMethodResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetProvisioningMethodResponseParams.result = i;
                OemCryptoProvisioningMethod.validate(i);
                return oemCryptoServiceGetProvisioningMethodResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceGetProvisioningMethodResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceGetProvisioningMethodResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetProvisioningMethodResponse mCallback;

        OemCryptoServiceGetProvisioningMethodResponseParamsForwardToCallback(OemCryptoService.GetProvisioningMethodResponse getProvisioningMethodResponse) {
            this.mCallback = getProvisioningMethodResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(37, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceGetProvisioningMethodResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetProvisioningMethodResponseParamsProxyToResponder implements OemCryptoService.GetProvisioningMethodResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetProvisioningMethodResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceGetProvisioningMethodResponseParams oemCryptoServiceGetProvisioningMethodResponseParams = new OemCryptoServiceGetProvisioningMethodResponseParams();
            oemCryptoServiceGetProvisioningMethodResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceGetProvisioningMethodResponseParams.serializeWithHeader(this.mCore, new MessageHeader(37, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceGetRandomParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int length;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetRandomParams() {
            super(16, 0);
        }

        public static OemCryptoServiceGetRandomParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetRandomParams oemCryptoServiceGetRandomParams = new OemCryptoServiceGetRandomParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceGetRandomParams.length = decoder.readInt(8);
                return oemCryptoServiceGetRandomParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.length, 8);
        }

        private OemCryptoServiceGetRandomParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceGetRandomResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceGetRandomResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceGetRandomResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceGetRandomResponseParams oemCryptoServiceGetRandomResponseParams = new OemCryptoServiceGetRandomResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceGetRandomResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceGetRandomResponseParams.data = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceGetRandomResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.data, 16, 1, -1);
        }

        private OemCryptoServiceGetRandomResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceGetRandomResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.GetRandomResponse mCallback;

        OemCryptoServiceGetRandomResponseParamsForwardToCallback(OemCryptoService.GetRandomResponse getRandomResponse) {
            this.mCallback = getRandomResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(21, 2)) {
                    return false;
                }
                OemCryptoServiceGetRandomResponseParams oemCryptoServiceGetRandomResponseParamsDeserialize = OemCryptoServiceGetRandomResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceGetRandomResponseParamsDeserialize.result), oemCryptoServiceGetRandomResponseParamsDeserialize.data);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceGetRandomResponseParamsProxyToResponder implements OemCryptoService.GetRandomResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceGetRandomResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceGetRandomResponseParams oemCryptoServiceGetRandomResponseParams = new OemCryptoServiceGetRandomResponseParams();
            oemCryptoServiceGetRandomResponseParams.result = num.intValue();
            oemCryptoServiceGetRandomResponseParams.data = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceGetRandomResponseParams.serializeWithHeader(this.mCore, new MessageHeader(21, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceInitializeDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceInitializeDeprecatedParams() {
            super(8, 0);
        }

        public static OemCryptoServiceInitializeDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceInitializeDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceInitializeDeprecatedParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceInitializeDeprecatedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceInitializeDeprecatedResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceInitializeDeprecatedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceInitializeDeprecatedResponseParams oemCryptoServiceInitializeDeprecatedResponseParams = new OemCryptoServiceInitializeDeprecatedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceInitializeDeprecatedResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceInitializeDeprecatedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceInitializeDeprecatedResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceInitializeDeprecatedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.InitializeDeprecatedResponse mCallback;

        OemCryptoServiceInitializeDeprecatedResponseParamsForwardToCallback(OemCryptoService.InitializeDeprecatedResponse initializeDeprecatedResponse) {
            this.mCallback = initializeDeprecatedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceInitializeDeprecatedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceInitializeDeprecatedResponseParamsProxyToResponder implements OemCryptoService.InitializeDeprecatedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceInitializeDeprecatedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceInitializeDeprecatedResponseParams oemCryptoServiceInitializeDeprecatedResponseParams = new OemCryptoServiceInitializeDeprecatedResponseParams();
            oemCryptoServiceInitializeDeprecatedResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceInitializeDeprecatedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceInitializeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int oemcryptoVersion;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceInitializeParams() {
            super(16, 0);
        }

        public static OemCryptoServiceInitializeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceInitializeParams oemCryptoServiceInitializeParams = new OemCryptoServiceInitializeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceInitializeParams.oemcryptoVersion = decoder.readInt(8);
                return oemCryptoServiceInitializeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.oemcryptoVersion, 8);
        }

        private OemCryptoServiceInitializeParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceInitializeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceInitializeResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceInitializeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceInitializeResponseParams oemCryptoServiceInitializeResponseParams = new OemCryptoServiceInitializeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceInitializeResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceInitializeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceInitializeResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceInitializeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.InitializeResponse mCallback;

        OemCryptoServiceInitializeResponseParamsForwardToCallback(OemCryptoService.InitializeResponse initializeResponse) {
            this.mCallback = initializeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(36, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceInitializeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceInitializeResponseParamsProxyToResponder implements OemCryptoService.InitializeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceInitializeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceInitializeResponseParams oemCryptoServiceInitializeResponseParams = new OemCryptoServiceInitializeResponseParams();
            oemCryptoServiceInitializeResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceInitializeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(36, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceIsAntiRollbackHwPresentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceIsAntiRollbackHwPresentParams() {
            super(8, 0);
        }

        public static OemCryptoServiceIsAntiRollbackHwPresentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceIsAntiRollbackHwPresentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceIsAntiRollbackHwPresentParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceIsAntiRollbackHwPresentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceIsAntiRollbackHwPresentResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceIsAntiRollbackHwPresentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceIsAntiRollbackHwPresentResponseParams oemCryptoServiceIsAntiRollbackHwPresentResponseParams = new OemCryptoServiceIsAntiRollbackHwPresentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceIsAntiRollbackHwPresentResponseParams.result = decoder.readBoolean(8, 0);
                return oemCryptoServiceIsAntiRollbackHwPresentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private OemCryptoServiceIsAntiRollbackHwPresentResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceIsAntiRollbackHwPresentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.IsAntiRollbackHwPresentResponse mCallback;

        OemCryptoServiceIsAntiRollbackHwPresentResponseParamsForwardToCallback(OemCryptoService.IsAntiRollbackHwPresentResponse isAntiRollbackHwPresentResponse) {
            this.mCallback = isAntiRollbackHwPresentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(65, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(OemCryptoServiceIsAntiRollbackHwPresentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceIsAntiRollbackHwPresentResponseParamsProxyToResponder implements OemCryptoService.IsAntiRollbackHwPresentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceIsAntiRollbackHwPresentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            OemCryptoServiceIsAntiRollbackHwPresentResponseParams oemCryptoServiceIsAntiRollbackHwPresentResponseParams = new OemCryptoServiceIsAntiRollbackHwPresentResponseParams();
            oemCryptoServiceIsAntiRollbackHwPresentResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(oemCryptoServiceIsAntiRollbackHwPresentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(65, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceIsRootKeyCertificateValidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceIsRootKeyCertificateValidParams() {
            super(8, 0);
        }

        public static OemCryptoServiceIsRootKeyCertificateValidParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceIsRootKeyCertificateValidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceIsRootKeyCertificateValidParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceIsRootKeyCertificateValidResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceIsRootKeyCertificateValidResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceIsRootKeyCertificateValidResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceIsRootKeyCertificateValidResponseParams oemCryptoServiceIsRootKeyCertificateValidResponseParams = new OemCryptoServiceIsRootKeyCertificateValidResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceIsRootKeyCertificateValidResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceIsRootKeyCertificateValidResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceIsRootKeyCertificateValidResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceIsRootKeyCertificateValidResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.IsRootKeyCertificateValidResponse mCallback;

        OemCryptoServiceIsRootKeyCertificateValidResponseParamsForwardToCallback(OemCryptoService.IsRootKeyCertificateValidResponse isRootKeyCertificateValidResponse) {
            this.mCallback = isRootKeyCertificateValidResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(18, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceIsRootKeyCertificateValidResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceIsRootKeyCertificateValidResponseParamsProxyToResponder implements OemCryptoService.IsRootKeyCertificateValidResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceIsRootKeyCertificateValidResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceIsRootKeyCertificateValidResponseParams oemCryptoServiceIsRootKeyCertificateValidResponseParams = new OemCryptoServiceIsRootKeyCertificateValidResponseParams();
            oemCryptoServiceIsRootKeyCertificateValidResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceIsRootKeyCertificateValidResponseParams.serializeWithHeader(this.mCore, new MessageHeader(18, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceIsSrmUpdateSupportedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceIsSrmUpdateSupportedParams() {
            super(8, 0);
        }

        public static OemCryptoServiceIsSrmUpdateSupportedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceIsSrmUpdateSupportedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceIsSrmUpdateSupportedParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceIsSrmUpdateSupportedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceIsSrmUpdateSupportedResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceIsSrmUpdateSupportedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceIsSrmUpdateSupportedResponseParams oemCryptoServiceIsSrmUpdateSupportedResponseParams = new OemCryptoServiceIsSrmUpdateSupportedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceIsSrmUpdateSupportedResponseParams.result = decoder.readBoolean(8, 0);
                return oemCryptoServiceIsSrmUpdateSupportedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private OemCryptoServiceIsSrmUpdateSupportedResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceIsSrmUpdateSupportedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.IsSrmUpdateSupportedResponse mCallback;

        OemCryptoServiceIsSrmUpdateSupportedResponseParamsForwardToCallback(OemCryptoService.IsSrmUpdateSupportedResponse isSrmUpdateSupportedResponse) {
            this.mCallback = isSrmUpdateSupportedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(39, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(OemCryptoServiceIsSrmUpdateSupportedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceIsSrmUpdateSupportedResponseParamsProxyToResponder implements OemCryptoService.IsSrmUpdateSupportedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceIsSrmUpdateSupportedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            OemCryptoServiceIsSrmUpdateSupportedResponseParams oemCryptoServiceIsSrmUpdateSupportedResponseParams = new OemCryptoServiceIsSrmUpdateSupportedResponseParams();
            oemCryptoServiceIsSrmUpdateSupportedResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(oemCryptoServiceIsSrmUpdateSupportedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(39, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadDeviceRsaKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int session;
        public byte[] wrappedRsaKey;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadDeviceRsaKeyParams() {
            super(24, 0);
        }

        public static OemCryptoServiceLoadDeviceRsaKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadDeviceRsaKeyParams oemCryptoServiceLoadDeviceRsaKeyParams = new OemCryptoServiceLoadDeviceRsaKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadDeviceRsaKeyParams.session = decoder.readInt(8);
                oemCryptoServiceLoadDeviceRsaKeyParams.wrappedRsaKey = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceLoadDeviceRsaKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.wrappedRsaKey, 16, 0, -1);
        }

        private OemCryptoServiceLoadDeviceRsaKeyParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceLoadDeviceRsaKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadDeviceRsaKeyResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadDeviceRsaKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadDeviceRsaKeyResponseParams oemCryptoServiceLoadDeviceRsaKeyResponseParams = new OemCryptoServiceLoadDeviceRsaKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadDeviceRsaKeyResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadDeviceRsaKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadDeviceRsaKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadDeviceRsaKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadDeviceRsaKeyResponse mCallback;

        OemCryptoServiceLoadDeviceRsaKeyResponseParamsForwardToCallback(OemCryptoService.LoadDeviceRsaKeyResponse loadDeviceRsaKeyResponse) {
            this.mCallback = loadDeviceRsaKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(25, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadDeviceRsaKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadDeviceRsaKeyResponseParamsProxyToResponder implements OemCryptoService.LoadDeviceRsaKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadDeviceRsaKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadDeviceRsaKeyResponseParams oemCryptoServiceLoadDeviceRsaKeyResponseParams = new OemCryptoServiceLoadDeviceRsaKeyResponseParams();
            oemCryptoServiceLoadDeviceRsaKeyResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadDeviceRsaKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(25, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadDrmPrivateKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int keyType;
        public int session;
        public byte[] wrappedPrivateKey;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadDrmPrivateKeyParams() {
            super(24, 0);
        }

        public static OemCryptoServiceLoadDrmPrivateKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadDrmPrivateKeyParams oemCryptoServiceLoadDrmPrivateKeyParams = new OemCryptoServiceLoadDrmPrivateKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadDrmPrivateKeyParams.session = decoder.readInt(8);
                int i = decoder.readInt(12);
                oemCryptoServiceLoadDrmPrivateKeyParams.keyType = i;
                OemCryptoPrivateKey.validate(i);
                oemCryptoServiceLoadDrmPrivateKeyParams.wrappedPrivateKey = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceLoadDrmPrivateKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.keyType, 12);
            encoderAtDataOffset.encode(this.wrappedPrivateKey, 16, 0, -1);
        }

        private OemCryptoServiceLoadDrmPrivateKeyParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceLoadDrmPrivateKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadDrmPrivateKeyResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadDrmPrivateKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadDrmPrivateKeyResponseParams oemCryptoServiceLoadDrmPrivateKeyResponseParams = new OemCryptoServiceLoadDrmPrivateKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadDrmPrivateKeyResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadDrmPrivateKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadDrmPrivateKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadDrmPrivateKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadDrmPrivateKeyResponse mCallback;

        OemCryptoServiceLoadDrmPrivateKeyResponseParamsForwardToCallback(OemCryptoService.LoadDrmPrivateKeyResponse loadDrmPrivateKeyResponse) {
            this.mCallback = loadDrmPrivateKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(74, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadDrmPrivateKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadDrmPrivateKeyResponseParamsProxyToResponder implements OemCryptoService.LoadDrmPrivateKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadDrmPrivateKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadDrmPrivateKeyResponseParams oemCryptoServiceLoadDrmPrivateKeyResponseParams = new OemCryptoServiceLoadDrmPrivateKeyResponseParams();
            oemCryptoServiceLoadDrmPrivateKeyResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadDrmPrivateKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(74, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadEntitledContentKeysParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OemCryptoEntitledContentKeyObject[] keyArray;
        public byte[] message;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadEntitledContentKeysParams() {
            super(32, 0);
        }

        public static OemCryptoServiceLoadEntitledContentKeysParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceLoadEntitledContentKeysParams oemCryptoServiceLoadEntitledContentKeysParams = new OemCryptoServiceLoadEntitledContentKeysParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadEntitledContentKeysParams.session = decoderOutline3.readInt(8);
                oemCryptoServiceLoadEntitledContentKeysParams.message = decoderOutline3.readBytes(16, 0, -1);
                Decoder pointer = decoderOutline3.readPointer(24, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceLoadEntitledContentKeysParams.keyArray = new OemCryptoEntitledContentKeyObject[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    oemCryptoServiceLoadEntitledContentKeysParams.keyArray[i] = OemCryptoEntitledContentKeyObject.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return oemCryptoServiceLoadEntitledContentKeysParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            OemCryptoEntitledContentKeyObject[] oemCryptoEntitledContentKeyObjectArr = this.keyArray;
            if (oemCryptoEntitledContentKeyObjectArr == null) {
                encoderAtDataOffset.encodeNullPointer(24, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(oemCryptoEntitledContentKeyObjectArr.length, 24, -1);
            int i = 0;
            while (true) {
                OemCryptoEntitledContentKeyObject[] oemCryptoEntitledContentKeyObjectArr2 = this.keyArray;
                if (i >= oemCryptoEntitledContentKeyObjectArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) oemCryptoEntitledContentKeyObjectArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private OemCryptoServiceLoadEntitledContentKeysParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceLoadEntitledContentKeysResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadEntitledContentKeysResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadEntitledContentKeysResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadEntitledContentKeysResponseParams oemCryptoServiceLoadEntitledContentKeysResponseParams = new OemCryptoServiceLoadEntitledContentKeysResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadEntitledContentKeysResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadEntitledContentKeysResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadEntitledContentKeysResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadEntitledContentKeysResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadEntitledContentKeysResponse mCallback;

        OemCryptoServiceLoadEntitledContentKeysResponseParamsForwardToCallback(OemCryptoService.LoadEntitledContentKeysResponse loadEntitledContentKeysResponse) {
            this.mCallback = loadEntitledContentKeysResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(62, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadEntitledContentKeysResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadEntitledContentKeysResponseParamsProxyToResponder implements OemCryptoService.LoadEntitledContentKeysResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadEntitledContentKeysResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadEntitledContentKeysResponseParams oemCryptoServiceLoadEntitledContentKeysResponseParams = new OemCryptoServiceLoadEntitledContentKeysResponseParams();
            oemCryptoServiceLoadEntitledContentKeysResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadEntitledContentKeysResponseParams.serializeWithHeader(this.mCore, new MessageHeader(62, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadEntitledContentKeysV14Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OemCryptoEntitledContentKeyObjectV14[] keyArray;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadEntitledContentKeysV14Params() {
            super(24, 0);
        }

        public static OemCryptoServiceLoadEntitledContentKeysV14Params deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceLoadEntitledContentKeysV14Params oemCryptoServiceLoadEntitledContentKeysV14Params = new OemCryptoServiceLoadEntitledContentKeysV14Params(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadEntitledContentKeysV14Params.session = decoderOutline3.readInt(8);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceLoadEntitledContentKeysV14Params.keyArray = new OemCryptoEntitledContentKeyObjectV14[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    oemCryptoServiceLoadEntitledContentKeysV14Params.keyArray[i] = OemCryptoEntitledContentKeyObjectV14.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return oemCryptoServiceLoadEntitledContentKeysV14Params;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            OemCryptoEntitledContentKeyObjectV14[] oemCryptoEntitledContentKeyObjectV14Arr = this.keyArray;
            if (oemCryptoEntitledContentKeyObjectV14Arr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(oemCryptoEntitledContentKeyObjectV14Arr.length, 16, -1);
            int i = 0;
            while (true) {
                OemCryptoEntitledContentKeyObjectV14[] oemCryptoEntitledContentKeyObjectV14Arr2 = this.keyArray;
                if (i >= oemCryptoEntitledContentKeyObjectV14Arr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) oemCryptoEntitledContentKeyObjectV14Arr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private OemCryptoServiceLoadEntitledContentKeysV14Params(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceLoadEntitledContentKeysV14ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadEntitledContentKeysV14ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadEntitledContentKeysV14ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadEntitledContentKeysV14ResponseParams oemCryptoServiceLoadEntitledContentKeysV14ResponseParams = new OemCryptoServiceLoadEntitledContentKeysV14ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadEntitledContentKeysV14ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadEntitledContentKeysV14ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadEntitledContentKeysV14ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadEntitledContentKeysV14ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadEntitledContentKeysV14Response mCallback;

        OemCryptoServiceLoadEntitledContentKeysV14ResponseParamsForwardToCallback(OemCryptoService.LoadEntitledContentKeysV14Response loadEntitledContentKeysV14Response) {
            this.mCallback = loadEntitledContentKeysV14Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(55, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadEntitledContentKeysV14ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadEntitledContentKeysV14ResponseParamsProxyToResponder implements OemCryptoService.LoadEntitledContentKeysV14Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadEntitledContentKeysV14ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadEntitledContentKeysV14ResponseParams oemCryptoServiceLoadEntitledContentKeysV14ResponseParams = new OemCryptoServiceLoadEntitledContentKeysV14ResponseParams();
            oemCryptoServiceLoadEntitledContentKeysV14ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadEntitledContentKeysV14ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(55, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadKeysParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OemCryptoSubstring encMacKeys;
        public OemCryptoSubstring encMacKeysIv;
        public OemCryptoKeyObject[] keyArray;
        public int licenseType;
        public byte[] message;
        public OemCryptoSubstring pst;
        public int session;
        public byte[] signature;
        public OemCryptoSubstring srmRestrictionData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(72, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadKeysParams() {
            super(72, 0);
        }

        public static OemCryptoServiceLoadKeysParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceLoadKeysParams oemCryptoServiceLoadKeysParams = new OemCryptoServiceLoadKeysParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadKeysParams.session = decoderOutline3.readInt(8);
                int i = decoderOutline3.readInt(12);
                oemCryptoServiceLoadKeysParams.licenseType = i;
                OemCryptoLicenseType.validate(i);
                oemCryptoServiceLoadKeysParams.message = decoderOutline3.readBytes(16, 0, -1);
                oemCryptoServiceLoadKeysParams.signature = decoderOutline3.readBytes(24, 0, -1);
                oemCryptoServiceLoadKeysParams.encMacKeysIv = OemCryptoSubstring.decode(decoderOutline3.readPointer(32, false));
                oemCryptoServiceLoadKeysParams.encMacKeys = OemCryptoSubstring.decode(decoderOutline3.readPointer(40, false));
                Decoder pointer = decoderOutline3.readPointer(48, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceLoadKeysParams.keyArray = new OemCryptoKeyObject[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    oemCryptoServiceLoadKeysParams.keyArray[i2] = OemCryptoKeyObject.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                oemCryptoServiceLoadKeysParams.pst = OemCryptoSubstring.decode(decoderOutline3.readPointer(56, false));
                oemCryptoServiceLoadKeysParams.srmRestrictionData = OemCryptoSubstring.decode(decoderOutline3.readPointer(64, false));
                return oemCryptoServiceLoadKeysParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.licenseType, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
            encoderAtDataOffset.encode((Struct) this.encMacKeysIv, 32, false);
            encoderAtDataOffset.encode((Struct) this.encMacKeys, 40, false);
            OemCryptoKeyObject[] oemCryptoKeyObjectArr = this.keyArray;
            if (oemCryptoKeyObjectArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(oemCryptoKeyObjectArr.length, 48, -1);
                int i = 0;
                while (true) {
                    OemCryptoKeyObject[] oemCryptoKeyObjectArr2 = this.keyArray;
                    if (i >= oemCryptoKeyObjectArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) oemCryptoKeyObjectArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(48, false);
            }
            encoderAtDataOffset.encode((Struct) this.pst, 56, false);
            encoderAtDataOffset.encode((Struct) this.srmRestrictionData, 64, false);
        }

        private OemCryptoServiceLoadKeysParams(int i) {
            super(72, i);
        }
    }

    static final class OemCryptoServiceLoadKeysResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadKeysResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadKeysResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadKeysResponseParams oemCryptoServiceLoadKeysResponseParams = new OemCryptoServiceLoadKeysResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadKeysResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadKeysResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadKeysResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadKeysResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadKeysResponse mCallback;

        OemCryptoServiceLoadKeysResponseParamsForwardToCallback(OemCryptoService.LoadKeysResponse loadKeysResponse) {
            this.mCallback = loadKeysResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(58, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadKeysResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadKeysResponseParamsProxyToResponder implements OemCryptoService.LoadKeysResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadKeysResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadKeysResponseParams oemCryptoServiceLoadKeysResponseParams = new OemCryptoServiceLoadKeysResponseParams();
            oemCryptoServiceLoadKeysResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadKeysResponseParams.serializeWithHeader(this.mCore, new MessageHeader(58, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadKeysV11OrV12Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int encMacKeysIvOffset;
        public int encMacKeysOffset;
        public boolean hasEncMacKeys;
        public OemCryptoKeyObjectV14[] keyArray;
        public byte[] message;
        public int pstLength;
        public int pstOffset;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(56, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadKeysV11OrV12Params() {
            super(56, 0);
        }

        public static OemCryptoServiceLoadKeysV11OrV12Params deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceLoadKeysV11OrV12Params oemCryptoServiceLoadKeysV11OrV12Params = new OemCryptoServiceLoadKeysV11OrV12Params(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadKeysV11OrV12Params.session = decoderOutline3.readInt(8);
                oemCryptoServiceLoadKeysV11OrV12Params.hasEncMacKeys = decoderOutline3.readBoolean(12, 0);
                oemCryptoServiceLoadKeysV11OrV12Params.message = decoderOutline3.readBytes(16, 0, -1);
                oemCryptoServiceLoadKeysV11OrV12Params.signature = decoderOutline3.readBytes(24, 0, -1);
                oemCryptoServiceLoadKeysV11OrV12Params.encMacKeysIvOffset = decoderOutline3.readInt(32);
                oemCryptoServiceLoadKeysV11OrV12Params.encMacKeysOffset = decoderOutline3.readInt(36);
                Decoder pointer = decoderOutline3.readPointer(40, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceLoadKeysV11OrV12Params.keyArray = new OemCryptoKeyObjectV14[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    oemCryptoServiceLoadKeysV11OrV12Params.keyArray[i] = OemCryptoKeyObjectV14.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                oemCryptoServiceLoadKeysV11OrV12Params.pstOffset = decoderOutline3.readInt(48);
                oemCryptoServiceLoadKeysV11OrV12Params.pstLength = decoderOutline3.readInt(52);
                return oemCryptoServiceLoadKeysV11OrV12Params;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.hasEncMacKeys, 12, 0);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
            encoderAtDataOffset.encode(this.encMacKeysIvOffset, 32);
            encoderAtDataOffset.encode(this.encMacKeysOffset, 36);
            OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr = this.keyArray;
            if (oemCryptoKeyObjectV14Arr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(oemCryptoKeyObjectV14Arr.length, 40, -1);
                int i = 0;
                while (true) {
                    OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr2 = this.keyArray;
                    if (i >= oemCryptoKeyObjectV14Arr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) oemCryptoKeyObjectV14Arr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(40, false);
            }
            encoderAtDataOffset.encode(this.pstOffset, 48);
            encoderAtDataOffset.encode(this.pstLength, 52);
        }

        private OemCryptoServiceLoadKeysV11OrV12Params(int i) {
            super(56, i);
        }
    }

    static final class OemCryptoServiceLoadKeysV11OrV12ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadKeysV11OrV12ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadKeysV11OrV12ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadKeysV11OrV12ResponseParams oemCryptoServiceLoadKeysV11OrV12ResponseParams = new OemCryptoServiceLoadKeysV11OrV12ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadKeysV11OrV12ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadKeysV11OrV12ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadKeysV11OrV12ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadKeysV11OrV12ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadKeysV11OrV12Response mCallback;

        OemCryptoServiceLoadKeysV11OrV12ResponseParamsForwardToCallback(OemCryptoService.LoadKeysV11OrV12Response loadKeysV11OrV12Response) {
            this.mCallback = loadKeysV11OrV12Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadKeysV11OrV12ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadKeysV11OrV12ResponseParamsProxyToResponder implements OemCryptoService.LoadKeysV11OrV12Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadKeysV11OrV12ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadKeysV11OrV12ResponseParams oemCryptoServiceLoadKeysV11OrV12ResponseParams = new OemCryptoServiceLoadKeysV11OrV12ResponseParams();
            oemCryptoServiceLoadKeysV11OrV12ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadKeysV11OrV12ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadKeysV14Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int encMacKeysIvOffset;
        public int encMacKeysOffset;
        public boolean hasEncMacKeys;
        public OemCryptoKeyObjectV14[] keyArray;
        public int licenseType;
        public byte[] message;
        public int pstLength;
        public int pstOffset;
        public int session;
        public byte[] signature;
        public byte[] srmRequirement;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(72, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadKeysV14Params() {
            super(72, 0);
        }

        public static OemCryptoServiceLoadKeysV14Params deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceLoadKeysV14Params oemCryptoServiceLoadKeysV14Params = new OemCryptoServiceLoadKeysV14Params(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadKeysV14Params.session = decoderOutline3.readInt(8);
                oemCryptoServiceLoadKeysV14Params.hasEncMacKeys = decoderOutline3.readBoolean(12, 0);
                oemCryptoServiceLoadKeysV14Params.message = decoderOutline3.readBytes(16, 0, -1);
                oemCryptoServiceLoadKeysV14Params.signature = decoderOutline3.readBytes(24, 0, -1);
                oemCryptoServiceLoadKeysV14Params.encMacKeysIvOffset = decoderOutline3.readInt(32);
                oemCryptoServiceLoadKeysV14Params.encMacKeysOffset = decoderOutline3.readInt(36);
                Decoder pointer = decoderOutline3.readPointer(40, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceLoadKeysV14Params.keyArray = new OemCryptoKeyObjectV14[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    oemCryptoServiceLoadKeysV14Params.keyArray[i] = OemCryptoKeyObjectV14.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                oemCryptoServiceLoadKeysV14Params.pstOffset = decoderOutline3.readInt(48);
                oemCryptoServiceLoadKeysV14Params.pstLength = decoderOutline3.readInt(52);
                oemCryptoServiceLoadKeysV14Params.srmRequirement = decoderOutline3.readBytes(56, 0, -1);
                int i2 = decoderOutline3.readInt(64);
                oemCryptoServiceLoadKeysV14Params.licenseType = i2;
                OemCryptoLicenseType.validate(i2);
                return oemCryptoServiceLoadKeysV14Params;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.hasEncMacKeys, 12, 0);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
            encoderAtDataOffset.encode(this.encMacKeysIvOffset, 32);
            encoderAtDataOffset.encode(this.encMacKeysOffset, 36);
            OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr = this.keyArray;
            if (oemCryptoKeyObjectV14Arr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(oemCryptoKeyObjectV14Arr.length, 40, -1);
                int i = 0;
                while (true) {
                    OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr2 = this.keyArray;
                    if (i >= oemCryptoKeyObjectV14Arr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) oemCryptoKeyObjectV14Arr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(40, false);
            }
            encoderAtDataOffset.encode(this.pstOffset, 48);
            encoderAtDataOffset.encode(this.pstLength, 52);
            encoderAtDataOffset.encode(this.srmRequirement, 56, 0, -1);
            encoderAtDataOffset.encode(this.licenseType, 64);
        }

        private OemCryptoServiceLoadKeysV14Params(int i) {
            super(72, i);
        }
    }

    static final class OemCryptoServiceLoadKeysV14ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadKeysV14ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadKeysV14ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadKeysV14ResponseParams oemCryptoServiceLoadKeysV14ResponseParams = new OemCryptoServiceLoadKeysV14ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadKeysV14ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadKeysV14ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadKeysV14ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadKeysV14ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadKeysV14Response mCallback;

        OemCryptoServiceLoadKeysV14ResponseParamsForwardToCallback(OemCryptoService.LoadKeysV14Response loadKeysV14Response) {
            this.mCallback = loadKeysV14Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(57, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadKeysV14ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadKeysV14ResponseParamsProxyToResponder implements OemCryptoService.LoadKeysV14Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadKeysV14ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadKeysV14ResponseParams oemCryptoServiceLoadKeysV14ResponseParams = new OemCryptoServiceLoadKeysV14ResponseParams();
            oemCryptoServiceLoadKeysV14ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadKeysV14ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(57, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadLicenseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int coreMessageLength;
        public byte[] message;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadLicenseParams() {
            super(32, 0);
        }

        public static OemCryptoServiceLoadLicenseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadLicenseParams oemCryptoServiceLoadLicenseParams = new OemCryptoServiceLoadLicenseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadLicenseParams.session = decoder.readInt(8);
                oemCryptoServiceLoadLicenseParams.coreMessageLength = decoder.readInt(12);
                oemCryptoServiceLoadLicenseParams.message = decoder.readBytes(16, 0, -1);
                oemCryptoServiceLoadLicenseParams.signature = decoder.readBytes(24, 0, -1);
                return oemCryptoServiceLoadLicenseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.coreMessageLength, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
        }

        private OemCryptoServiceLoadLicenseParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceLoadLicenseResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadLicenseResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadLicenseResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadLicenseResponseParams oemCryptoServiceLoadLicenseResponseParams = new OemCryptoServiceLoadLicenseResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadLicenseResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadLicenseResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadLicenseResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadLicenseResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadLicenseResponse mCallback;

        OemCryptoServiceLoadLicenseResponseParamsForwardToCallback(OemCryptoService.LoadLicenseResponse loadLicenseResponse) {
            this.mCallback = loadLicenseResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(70, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadLicenseResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadLicenseResponseParamsProxyToResponder implements OemCryptoService.LoadLicenseResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadLicenseResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadLicenseResponseParams oemCryptoServiceLoadLicenseResponseParams = new OemCryptoServiceLoadLicenseResponseParams();
            oemCryptoServiceLoadLicenseResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadLicenseResponseParams.serializeWithHeader(this.mCore, new MessageHeader(70, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadOemPrivateKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadOemPrivateKeyParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadOemPrivateKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadOemPrivateKeyParams oemCryptoServiceLoadOemPrivateKeyParams = new OemCryptoServiceLoadOemPrivateKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadOemPrivateKeyParams.session = decoder.readInt(8);
                return oemCryptoServiceLoadOemPrivateKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.session, 8);
        }

        private OemCryptoServiceLoadOemPrivateKeyParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceLoadOemPrivateKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadOemPrivateKeyResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadOemPrivateKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadOemPrivateKeyResponseParams oemCryptoServiceLoadOemPrivateKeyResponseParams = new OemCryptoServiceLoadOemPrivateKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadOemPrivateKeyResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadOemPrivateKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadOemPrivateKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadOemPrivateKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadOemPrivateKeyResponse mCallback;

        OemCryptoServiceLoadOemPrivateKeyResponseParamsForwardToCallback(OemCryptoService.LoadOemPrivateKeyResponse loadOemPrivateKeyResponse) {
            this.mCallback = loadOemPrivateKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(73, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadOemPrivateKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadOemPrivateKeyResponseParamsProxyToResponder implements OemCryptoService.LoadOemPrivateKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadOemPrivateKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadOemPrivateKeyResponseParams oemCryptoServiceLoadOemPrivateKeyResponseParams = new OemCryptoServiceLoadOemPrivateKeyResponseParams();
            oemCryptoServiceLoadOemPrivateKeyResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadOemPrivateKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(73, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadProvisioningParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int availWrappedPrivateKeySize;
        public int coreMessageLength;
        public byte[] message;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadProvisioningParams() {
            super(40, 0);
        }

        public static OemCryptoServiceLoadProvisioningParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadProvisioningParams oemCryptoServiceLoadProvisioningParams = new OemCryptoServiceLoadProvisioningParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadProvisioningParams.session = decoder.readInt(8);
                oemCryptoServiceLoadProvisioningParams.coreMessageLength = decoder.readInt(12);
                oemCryptoServiceLoadProvisioningParams.message = decoder.readBytes(16, 0, -1);
                oemCryptoServiceLoadProvisioningParams.signature = decoder.readBytes(24, 0, -1);
                oemCryptoServiceLoadProvisioningParams.availWrappedPrivateKeySize = decoder.readInt(32);
                return oemCryptoServiceLoadProvisioningParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.coreMessageLength, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
            encoderAtDataOffset.encode(this.availWrappedPrivateKeySize, 32);
        }

        private OemCryptoServiceLoadProvisioningParams(int i) {
            super(40, i);
        }
    }

    static final class OemCryptoServiceLoadProvisioningResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public byte[] wrappedPrivateKey;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadProvisioningResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceLoadProvisioningResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadProvisioningResponseParams oemCryptoServiceLoadProvisioningResponseParams = new OemCryptoServiceLoadProvisioningResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadProvisioningResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceLoadProvisioningResponseParams.wrappedPrivateKey = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceLoadProvisioningResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.wrappedPrivateKey, 16, 1, -1);
        }

        private OemCryptoServiceLoadProvisioningResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceLoadProvisioningResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadProvisioningResponse mCallback;

        OemCryptoServiceLoadProvisioningResponseParamsForwardToCallback(OemCryptoService.LoadProvisioningResponse loadProvisioningResponse) {
            this.mCallback = loadProvisioningResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(72, 2)) {
                    return false;
                }
                OemCryptoServiceLoadProvisioningResponseParams oemCryptoServiceLoadProvisioningResponseParamsDeserialize = OemCryptoServiceLoadProvisioningResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceLoadProvisioningResponseParamsDeserialize.result), oemCryptoServiceLoadProvisioningResponseParamsDeserialize.wrappedPrivateKey);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadProvisioningResponseParamsProxyToResponder implements OemCryptoService.LoadProvisioningResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadProvisioningResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceLoadProvisioningResponseParams oemCryptoServiceLoadProvisioningResponseParams = new OemCryptoServiceLoadProvisioningResponseParams();
            oemCryptoServiceLoadProvisioningResponseParams.result = num.intValue();
            oemCryptoServiceLoadProvisioningResponseParams.wrappedPrivateKey = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceLoadProvisioningResponseParams.serializeWithHeader(this.mCore, new MessageHeader(72, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadRenewalParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int coreMessageLength;
        public byte[] message;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadRenewalParams() {
            super(32, 0);
        }

        public static OemCryptoServiceLoadRenewalParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadRenewalParams oemCryptoServiceLoadRenewalParams = new OemCryptoServiceLoadRenewalParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadRenewalParams.session = decoder.readInt(8);
                oemCryptoServiceLoadRenewalParams.coreMessageLength = decoder.readInt(12);
                oemCryptoServiceLoadRenewalParams.message = decoder.readBytes(16, 0, -1);
                oemCryptoServiceLoadRenewalParams.signature = decoder.readBytes(24, 0, -1);
                return oemCryptoServiceLoadRenewalParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.coreMessageLength, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
        }

        private OemCryptoServiceLoadRenewalParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServiceLoadRenewalResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadRenewalResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadRenewalResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadRenewalResponseParams oemCryptoServiceLoadRenewalResponseParams = new OemCryptoServiceLoadRenewalResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadRenewalResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadRenewalResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadRenewalResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadRenewalResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadRenewalResponse mCallback;

        OemCryptoServiceLoadRenewalResponseParamsForwardToCallback(OemCryptoService.LoadRenewalResponse loadRenewalResponse) {
            this.mCallback = loadRenewalResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(71, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadRenewalResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadRenewalResponseParamsProxyToResponder implements OemCryptoService.LoadRenewalResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadRenewalResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadRenewalResponseParams oemCryptoServiceLoadRenewalResponseParams = new OemCryptoServiceLoadRenewalResponseParams();
            oemCryptoServiceLoadRenewalResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadRenewalResponseParams.serializeWithHeader(this.mCore, new MessageHeader(71, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadSrmParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] buffer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadSrmParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadSrmParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadSrmParams oemCryptoServiceLoadSrmParams = new OemCryptoServiceLoadSrmParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadSrmParams.buffer = decoder.readBytes(8, 0, -1);
                return oemCryptoServiceLoadSrmParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.buffer, 8, 0, -1);
        }

        private OemCryptoServiceLoadSrmParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceLoadSrmResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadSrmResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadSrmResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadSrmResponseParams oemCryptoServiceLoadSrmResponseParams = new OemCryptoServiceLoadSrmResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadSrmResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadSrmResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadSrmResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadSrmResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadSrmResponse mCallback;

        OemCryptoServiceLoadSrmResponseParamsForwardToCallback(OemCryptoService.LoadSrmResponse loadSrmResponse) {
            this.mCallback = loadSrmResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(41, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadSrmResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadSrmResponseParamsProxyToResponder implements OemCryptoService.LoadSrmResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadSrmResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadSrmResponseParams oemCryptoServiceLoadSrmResponseParams = new OemCryptoServiceLoadSrmResponseParams();
            oemCryptoServiceLoadSrmResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadSrmResponseParams.serializeWithHeader(this.mCore, new MessageHeader(41, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadTestKeyboxParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] buffer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadTestKeyboxParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadTestKeyboxParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadTestKeyboxParams oemCryptoServiceLoadTestKeyboxParams = new OemCryptoServiceLoadTestKeyboxParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadTestKeyboxParams.buffer = decoder.readBytes(8, 0, -1);
                return oemCryptoServiceLoadTestKeyboxParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.buffer, 8, 0, -1);
        }

        private OemCryptoServiceLoadTestKeyboxParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceLoadTestKeyboxResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadTestKeyboxResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadTestKeyboxResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadTestKeyboxResponseParams oemCryptoServiceLoadTestKeyboxResponseParams = new OemCryptoServiceLoadTestKeyboxResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadTestKeyboxResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadTestKeyboxResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadTestKeyboxResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadTestKeyboxResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadTestKeyboxResponse mCallback;

        OemCryptoServiceLoadTestKeyboxResponseParamsForwardToCallback(OemCryptoService.LoadTestKeyboxResponse loadTestKeyboxResponse) {
            this.mCallback = loadTestKeyboxResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(54, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadTestKeyboxResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadTestKeyboxResponseParamsProxyToResponder implements OemCryptoService.LoadTestKeyboxResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadTestKeyboxResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadTestKeyboxResponseParams oemCryptoServiceLoadTestKeyboxResponseParams = new OemCryptoServiceLoadTestKeyboxResponseParams();
            oemCryptoServiceLoadTestKeyboxResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadTestKeyboxResponseParams.serializeWithHeader(this.mCore, new MessageHeader(54, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadTestKeyboxV13Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadTestKeyboxV13Params() {
            super(8, 0);
        }

        public static OemCryptoServiceLoadTestKeyboxV13Params deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceLoadTestKeyboxV13Params(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceLoadTestKeyboxV13Params(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceLoadTestKeyboxV13ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadTestKeyboxV13ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadTestKeyboxV13ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadTestKeyboxV13ResponseParams oemCryptoServiceLoadTestKeyboxV13ResponseParams = new OemCryptoServiceLoadTestKeyboxV13ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadTestKeyboxV13ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadTestKeyboxV13ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadTestKeyboxV13ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadTestKeyboxV13ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadTestKeyboxV13Response mCallback;

        OemCryptoServiceLoadTestKeyboxV13ResponseParamsForwardToCallback(OemCryptoService.LoadTestKeyboxV13Response loadTestKeyboxV13Response) {
            this.mCallback = loadTestKeyboxV13Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(17, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadTestKeyboxV13ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadTestKeyboxV13ResponseParamsProxyToResponder implements OemCryptoService.LoadTestKeyboxV13Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadTestKeyboxV13ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadTestKeyboxV13ResponseParams oemCryptoServiceLoadTestKeyboxV13ResponseParams = new OemCryptoServiceLoadTestKeyboxV13ResponseParams();
            oemCryptoServiceLoadTestKeyboxV13ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadTestKeyboxV13ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(17, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] buffer;
        public int index;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadUsageEntryParams() {
            super(24, 0);
        }

        public static OemCryptoServiceLoadUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadUsageEntryParams oemCryptoServiceLoadUsageEntryParams = new OemCryptoServiceLoadUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadUsageEntryParams.session = decoder.readInt(8);
                oemCryptoServiceLoadUsageEntryParams.index = decoder.readInt(12);
                oemCryptoServiceLoadUsageEntryParams.buffer = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceLoadUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.index, 12);
            encoderAtDataOffset.encode(this.buffer, 16, 0, -1);
        }

        private OemCryptoServiceLoadUsageEntryParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceLoadUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadUsageEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadUsageEntryResponseParams oemCryptoServiceLoadUsageEntryResponseParams = new OemCryptoServiceLoadUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadUsageEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadUsageEntryResponse mCallback;

        OemCryptoServiceLoadUsageEntryResponseParamsForwardToCallback(OemCryptoService.LoadUsageEntryResponse loadUsageEntryResponse) {
            this.mCallback = loadUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(46, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadUsageEntryResponseParamsProxyToResponder implements OemCryptoService.LoadUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadUsageEntryResponseParams oemCryptoServiceLoadUsageEntryResponseParams = new OemCryptoServiceLoadUsageEntryResponseParams();
            oemCryptoServiceLoadUsageEntryResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(46, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceLoadUsageTableHeaderParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] buffer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadUsageTableHeaderParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadUsageTableHeaderParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadUsageTableHeaderParams oemCryptoServiceLoadUsageTableHeaderParams = new OemCryptoServiceLoadUsageTableHeaderParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceLoadUsageTableHeaderParams.buffer = decoder.readBytes(8, 0, -1);
                return oemCryptoServiceLoadUsageTableHeaderParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.buffer, 8, 0, -1);
        }

        private OemCryptoServiceLoadUsageTableHeaderParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceLoadUsageTableHeaderResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceLoadUsageTableHeaderResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceLoadUsageTableHeaderResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceLoadUsageTableHeaderResponseParams oemCryptoServiceLoadUsageTableHeaderResponseParams = new OemCryptoServiceLoadUsageTableHeaderResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceLoadUsageTableHeaderResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceLoadUsageTableHeaderResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceLoadUsageTableHeaderResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceLoadUsageTableHeaderResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.LoadUsageTableHeaderResponse mCallback;

        OemCryptoServiceLoadUsageTableHeaderResponseParamsForwardToCallback(OemCryptoService.LoadUsageTableHeaderResponse loadUsageTableHeaderResponse) {
            this.mCallback = loadUsageTableHeaderResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(44, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceLoadUsageTableHeaderResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceLoadUsageTableHeaderResponseParamsProxyToResponder implements OemCryptoService.LoadUsageTableHeaderResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceLoadUsageTableHeaderResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceLoadUsageTableHeaderResponseParams oemCryptoServiceLoadUsageTableHeaderResponseParams = new OemCryptoServiceLoadUsageTableHeaderResponseParams();
            oemCryptoServiceLoadUsageTableHeaderResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceLoadUsageTableHeaderResponseParams.serializeWithHeader(this.mCore, new MessageHeader(44, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceMaximumUsageTableHeaderSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceMaximumUsageTableHeaderSizeParams() {
            super(8, 0);
        }

        public static OemCryptoServiceMaximumUsageTableHeaderSizeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceMaximumUsageTableHeaderSizeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceMaximumUsageTableHeaderSizeParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int size;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams oemCryptoServiceMaximumUsageTableHeaderSizeResponseParams = new OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceMaximumUsageTableHeaderSizeResponseParams.size = decoder.readInt(8);
                return oemCryptoServiceMaximumUsageTableHeaderSizeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.size, 8);
        }

        private OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceMaximumUsageTableHeaderSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.MaximumUsageTableHeaderSizeResponse mCallback;

        OemCryptoServiceMaximumUsageTableHeaderSizeResponseParamsForwardToCallback(OemCryptoService.MaximumUsageTableHeaderSizeResponse maximumUsageTableHeaderSizeResponse) {
            this.mCallback = maximumUsageTableHeaderSizeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(64, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).size));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceMaximumUsageTableHeaderSizeResponseParamsProxyToResponder implements OemCryptoService.MaximumUsageTableHeaderSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceMaximumUsageTableHeaderSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams oemCryptoServiceMaximumUsageTableHeaderSizeResponseParams = new OemCryptoServiceMaximumUsageTableHeaderSizeResponseParams();
            oemCryptoServiceMaximumUsageTableHeaderSizeResponseParams.size = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceMaximumUsageTableHeaderSizeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(64, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceMinorApiVersionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceMinorApiVersionParams() {
            super(8, 0);
        }

        public static OemCryptoServiceMinorApiVersionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceMinorApiVersionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceMinorApiVersionParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceMinorApiVersionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int version;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceMinorApiVersionResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceMinorApiVersionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceMinorApiVersionResponseParams oemCryptoServiceMinorApiVersionResponseParams = new OemCryptoServiceMinorApiVersionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceMinorApiVersionResponseParams.version = decoder.readInt(8);
                return oemCryptoServiceMinorApiVersionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.version, 8);
        }

        private OemCryptoServiceMinorApiVersionResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceMinorApiVersionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.MinorApiVersionResponse mCallback;

        OemCryptoServiceMinorApiVersionResponseParamsForwardToCallback(OemCryptoService.MinorApiVersionResponse minorApiVersionResponse) {
            this.mCallback = minorApiVersionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(66, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceMinorApiVersionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).version));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceMinorApiVersionResponseParamsProxyToResponder implements OemCryptoService.MinorApiVersionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceMinorApiVersionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceMinorApiVersionResponseParams oemCryptoServiceMinorApiVersionResponseParams = new OemCryptoServiceMinorApiVersionResponseParams();
            oemCryptoServiceMinorApiVersionResponseParams.version = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceMinorApiVersionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(66, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceMoveEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int newIndex;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceMoveEntryParams() {
            super(16, 0);
        }

        public static OemCryptoServiceMoveEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceMoveEntryParams oemCryptoServiceMoveEntryParams = new OemCryptoServiceMoveEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceMoveEntryParams.session = decoder.readInt(8);
                oemCryptoServiceMoveEntryParams.newIndex = decoder.readInt(12);
                return oemCryptoServiceMoveEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.newIndex, 12);
        }

        private OemCryptoServiceMoveEntryParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceMoveEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceMoveEntryResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceMoveEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceMoveEntryResponseParams oemCryptoServiceMoveEntryResponseParams = new OemCryptoServiceMoveEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceMoveEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceMoveEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceMoveEntryResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceMoveEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.MoveEntryResponse mCallback;

        OemCryptoServiceMoveEntryResponseParamsForwardToCallback(OemCryptoService.MoveEntryResponse moveEntryResponse) {
            this.mCallback = moveEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(50, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceMoveEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceMoveEntryResponseParamsProxyToResponder implements OemCryptoService.MoveEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceMoveEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceMoveEntryResponseParams oemCryptoServiceMoveEntryResponseParams = new OemCryptoServiceMoveEntryResponseParams();
            oemCryptoServiceMoveEntryResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceMoveEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(50, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceOpenSessionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceOpenSessionParams() {
            super(8, 0);
        }

        public static OemCryptoServiceOpenSessionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceOpenSessionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceOpenSessionParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceOpenSessionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceOpenSessionResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceOpenSessionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceOpenSessionResponseParams oemCryptoServiceOpenSessionResponseParams = new OemCryptoServiceOpenSessionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceOpenSessionResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceOpenSessionResponseParams.session = decoder.readInt(12);
                return oemCryptoServiceOpenSessionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.session, 12);
        }

        private OemCryptoServiceOpenSessionResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceOpenSessionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.OpenSessionResponse mCallback;

        OemCryptoServiceOpenSessionResponseParamsForwardToCallback(OemCryptoService.OpenSessionResponse openSessionResponse) {
            this.mCallback = openSessionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                OemCryptoServiceOpenSessionResponseParams oemCryptoServiceOpenSessionResponseParamsDeserialize = OemCryptoServiceOpenSessionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceOpenSessionResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServiceOpenSessionResponseParamsDeserialize.session));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceOpenSessionResponseParamsProxyToResponder implements OemCryptoService.OpenSessionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceOpenSessionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Integer num2) {
            OemCryptoServiceOpenSessionResponseParams oemCryptoServiceOpenSessionResponseParams = new OemCryptoServiceOpenSessionResponseParams();
            oemCryptoServiceOpenSessionResponseParams.result = num.intValue();
            oemCryptoServiceOpenSessionResponseParams.session = num2.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceOpenSessionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServicePrepAndSignLicenseRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int availSignatureSize;
        public int coreMessageSize;
        public byte[] message;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServicePrepAndSignLicenseRequestParams() {
            super(32, 0);
        }

        public static OemCryptoServicePrepAndSignLicenseRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServicePrepAndSignLicenseRequestParams oemCryptoServicePrepAndSignLicenseRequestParams = new OemCryptoServicePrepAndSignLicenseRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServicePrepAndSignLicenseRequestParams.session = decoder.readInt(8);
                oemCryptoServicePrepAndSignLicenseRequestParams.coreMessageSize = decoder.readInt(12);
                oemCryptoServicePrepAndSignLicenseRequestParams.message = decoder.readBytes(16, 0, -1);
                oemCryptoServicePrepAndSignLicenseRequestParams.availSignatureSize = decoder.readInt(24);
                return oemCryptoServicePrepAndSignLicenseRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.coreMessageSize, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.availSignatureSize, 24);
        }

        private OemCryptoServicePrepAndSignLicenseRequestParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServicePrepAndSignLicenseRequestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int coreMessageSize;
        public byte[] message;
        public int result;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServicePrepAndSignLicenseRequestResponseParams() {
            super(32, 0);
        }

        public static OemCryptoServicePrepAndSignLicenseRequestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServicePrepAndSignLicenseRequestResponseParams oemCryptoServicePrepAndSignLicenseRequestResponseParams = new OemCryptoServicePrepAndSignLicenseRequestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServicePrepAndSignLicenseRequestResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServicePrepAndSignLicenseRequestResponseParams.coreMessageSize = decoder.readInt(12);
                oemCryptoServicePrepAndSignLicenseRequestResponseParams.message = decoder.readBytes(16, 1, -1);
                oemCryptoServicePrepAndSignLicenseRequestResponseParams.signature = decoder.readBytes(24, 1, -1);
                return oemCryptoServicePrepAndSignLicenseRequestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.coreMessageSize, 12);
            encoderAtDataOffset.encode(this.message, 16, 1, -1);
            encoderAtDataOffset.encode(this.signature, 24, 1, -1);
        }

        private OemCryptoServicePrepAndSignLicenseRequestResponseParams(int i) {
            super(32, i);
        }
    }

    static class OemCryptoServicePrepAndSignLicenseRequestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.PrepAndSignLicenseRequestResponse mCallback;

        OemCryptoServicePrepAndSignLicenseRequestResponseParamsForwardToCallback(OemCryptoService.PrepAndSignLicenseRequestResponse prepAndSignLicenseRequestResponse) {
            this.mCallback = prepAndSignLicenseRequestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(67, 2)) {
                    return false;
                }
                OemCryptoServicePrepAndSignLicenseRequestResponseParams oemCryptoServicePrepAndSignLicenseRequestResponseParamsDeserialize = OemCryptoServicePrepAndSignLicenseRequestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServicePrepAndSignLicenseRequestResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServicePrepAndSignLicenseRequestResponseParamsDeserialize.coreMessageSize), oemCryptoServicePrepAndSignLicenseRequestResponseParamsDeserialize.message, oemCryptoServicePrepAndSignLicenseRequestResponseParamsDeserialize.signature);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServicePrepAndSignLicenseRequestResponseParamsProxyToResponder implements OemCryptoService.PrepAndSignLicenseRequestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServicePrepAndSignLicenseRequestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback4
        public void call(Integer num, Integer num2, byte[] bArr, byte[] bArr2) {
            OemCryptoServicePrepAndSignLicenseRequestResponseParams oemCryptoServicePrepAndSignLicenseRequestResponseParams = new OemCryptoServicePrepAndSignLicenseRequestResponseParams();
            oemCryptoServicePrepAndSignLicenseRequestResponseParams.result = num.intValue();
            oemCryptoServicePrepAndSignLicenseRequestResponseParams.coreMessageSize = num2.intValue();
            oemCryptoServicePrepAndSignLicenseRequestResponseParams.message = bArr;
            oemCryptoServicePrepAndSignLicenseRequestResponseParams.signature = bArr2;
            this.mMessageReceiver.accept(oemCryptoServicePrepAndSignLicenseRequestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(67, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServicePrepAndSignProvisioningRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int availSignatureSize;
        public int coreMessageSize;
        public byte[] message;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServicePrepAndSignProvisioningRequestParams() {
            super(32, 0);
        }

        public static OemCryptoServicePrepAndSignProvisioningRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServicePrepAndSignProvisioningRequestParams oemCryptoServicePrepAndSignProvisioningRequestParams = new OemCryptoServicePrepAndSignProvisioningRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServicePrepAndSignProvisioningRequestParams.session = decoder.readInt(8);
                oemCryptoServicePrepAndSignProvisioningRequestParams.coreMessageSize = decoder.readInt(12);
                oemCryptoServicePrepAndSignProvisioningRequestParams.message = decoder.readBytes(16, 0, -1);
                oemCryptoServicePrepAndSignProvisioningRequestParams.availSignatureSize = decoder.readInt(24);
                return oemCryptoServicePrepAndSignProvisioningRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.coreMessageSize, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.availSignatureSize, 24);
        }

        private OemCryptoServicePrepAndSignProvisioningRequestParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServicePrepAndSignProvisioningRequestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int coreMessageSize;
        public byte[] message;
        public int result;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServicePrepAndSignProvisioningRequestResponseParams() {
            super(32, 0);
        }

        public static OemCryptoServicePrepAndSignProvisioningRequestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServicePrepAndSignProvisioningRequestResponseParams oemCryptoServicePrepAndSignProvisioningRequestResponseParams = new OemCryptoServicePrepAndSignProvisioningRequestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServicePrepAndSignProvisioningRequestResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServicePrepAndSignProvisioningRequestResponseParams.coreMessageSize = decoder.readInt(12);
                oemCryptoServicePrepAndSignProvisioningRequestResponseParams.message = decoder.readBytes(16, 1, -1);
                oemCryptoServicePrepAndSignProvisioningRequestResponseParams.signature = decoder.readBytes(24, 1, -1);
                return oemCryptoServicePrepAndSignProvisioningRequestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.coreMessageSize, 12);
            encoderAtDataOffset.encode(this.message, 16, 1, -1);
            encoderAtDataOffset.encode(this.signature, 24, 1, -1);
        }

        private OemCryptoServicePrepAndSignProvisioningRequestResponseParams(int i) {
            super(32, i);
        }
    }

    static class OemCryptoServicePrepAndSignProvisioningRequestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.PrepAndSignProvisioningRequestResponse mCallback;

        OemCryptoServicePrepAndSignProvisioningRequestResponseParamsForwardToCallback(OemCryptoService.PrepAndSignProvisioningRequestResponse prepAndSignProvisioningRequestResponse) {
            this.mCallback = prepAndSignProvisioningRequestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(69, 2)) {
                    return false;
                }
                OemCryptoServicePrepAndSignProvisioningRequestResponseParams oemCryptoServicePrepAndSignProvisioningRequestResponseParamsDeserialize = OemCryptoServicePrepAndSignProvisioningRequestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServicePrepAndSignProvisioningRequestResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServicePrepAndSignProvisioningRequestResponseParamsDeserialize.coreMessageSize), oemCryptoServicePrepAndSignProvisioningRequestResponseParamsDeserialize.message, oemCryptoServicePrepAndSignProvisioningRequestResponseParamsDeserialize.signature);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServicePrepAndSignProvisioningRequestResponseParamsProxyToResponder implements OemCryptoService.PrepAndSignProvisioningRequestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServicePrepAndSignProvisioningRequestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback4
        public void call(Integer num, Integer num2, byte[] bArr, byte[] bArr2) {
            OemCryptoServicePrepAndSignProvisioningRequestResponseParams oemCryptoServicePrepAndSignProvisioningRequestResponseParams = new OemCryptoServicePrepAndSignProvisioningRequestResponseParams();
            oemCryptoServicePrepAndSignProvisioningRequestResponseParams.result = num.intValue();
            oemCryptoServicePrepAndSignProvisioningRequestResponseParams.coreMessageSize = num2.intValue();
            oemCryptoServicePrepAndSignProvisioningRequestResponseParams.message = bArr;
            oemCryptoServicePrepAndSignProvisioningRequestResponseParams.signature = bArr2;
            this.mMessageReceiver.accept(oemCryptoServicePrepAndSignProvisioningRequestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(69, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServicePrepAndSignRenewalRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int availSignatureSize;
        public int coreMessageSize;
        public byte[] message;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServicePrepAndSignRenewalRequestParams() {
            super(32, 0);
        }

        public static OemCryptoServicePrepAndSignRenewalRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServicePrepAndSignRenewalRequestParams oemCryptoServicePrepAndSignRenewalRequestParams = new OemCryptoServicePrepAndSignRenewalRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServicePrepAndSignRenewalRequestParams.session = decoder.readInt(8);
                oemCryptoServicePrepAndSignRenewalRequestParams.coreMessageSize = decoder.readInt(12);
                oemCryptoServicePrepAndSignRenewalRequestParams.message = decoder.readBytes(16, 0, -1);
                oemCryptoServicePrepAndSignRenewalRequestParams.availSignatureSize = decoder.readInt(24);
                return oemCryptoServicePrepAndSignRenewalRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.coreMessageSize, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.availSignatureSize, 24);
        }

        private OemCryptoServicePrepAndSignRenewalRequestParams(int i) {
            super(32, i);
        }
    }

    static final class OemCryptoServicePrepAndSignRenewalRequestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int coreMessageSize;
        public byte[] message;
        public int result;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServicePrepAndSignRenewalRequestResponseParams() {
            super(32, 0);
        }

        public static OemCryptoServicePrepAndSignRenewalRequestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServicePrepAndSignRenewalRequestResponseParams oemCryptoServicePrepAndSignRenewalRequestResponseParams = new OemCryptoServicePrepAndSignRenewalRequestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServicePrepAndSignRenewalRequestResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServicePrepAndSignRenewalRequestResponseParams.coreMessageSize = decoder.readInt(12);
                oemCryptoServicePrepAndSignRenewalRequestResponseParams.message = decoder.readBytes(16, 1, -1);
                oemCryptoServicePrepAndSignRenewalRequestResponseParams.signature = decoder.readBytes(24, 1, -1);
                return oemCryptoServicePrepAndSignRenewalRequestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.coreMessageSize, 12);
            encoderAtDataOffset.encode(this.message, 16, 1, -1);
            encoderAtDataOffset.encode(this.signature, 24, 1, -1);
        }

        private OemCryptoServicePrepAndSignRenewalRequestResponseParams(int i) {
            super(32, i);
        }
    }

    static class OemCryptoServicePrepAndSignRenewalRequestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.PrepAndSignRenewalRequestResponse mCallback;

        OemCryptoServicePrepAndSignRenewalRequestResponseParamsForwardToCallback(OemCryptoService.PrepAndSignRenewalRequestResponse prepAndSignRenewalRequestResponse) {
            this.mCallback = prepAndSignRenewalRequestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(68, 2)) {
                    return false;
                }
                OemCryptoServicePrepAndSignRenewalRequestResponseParams oemCryptoServicePrepAndSignRenewalRequestResponseParamsDeserialize = OemCryptoServicePrepAndSignRenewalRequestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServicePrepAndSignRenewalRequestResponseParamsDeserialize.result), Integer.valueOf(oemCryptoServicePrepAndSignRenewalRequestResponseParamsDeserialize.coreMessageSize), oemCryptoServicePrepAndSignRenewalRequestResponseParamsDeserialize.message, oemCryptoServicePrepAndSignRenewalRequestResponseParamsDeserialize.signature);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServicePrepAndSignRenewalRequestResponseParamsProxyToResponder implements OemCryptoService.PrepAndSignRenewalRequestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServicePrepAndSignRenewalRequestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback4
        public void call(Integer num, Integer num2, byte[] bArr, byte[] bArr2) {
            OemCryptoServicePrepAndSignRenewalRequestResponseParams oemCryptoServicePrepAndSignRenewalRequestResponseParams = new OemCryptoServicePrepAndSignRenewalRequestResponseParams();
            oemCryptoServicePrepAndSignRenewalRequestResponseParams.result = num.intValue();
            oemCryptoServicePrepAndSignRenewalRequestResponseParams.coreMessageSize = num2.intValue();
            oemCryptoServicePrepAndSignRenewalRequestResponseParams.message = bArr;
            oemCryptoServicePrepAndSignRenewalRequestResponseParams.signature = bArr2;
            this.mMessageReceiver.accept(oemCryptoServicePrepAndSignRenewalRequestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(68, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceQueryKeyControlParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] keyId;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceQueryKeyControlParams() {
            super(24, 0);
        }

        public static OemCryptoServiceQueryKeyControlParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceQueryKeyControlParams oemCryptoServiceQueryKeyControlParams = new OemCryptoServiceQueryKeyControlParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceQueryKeyControlParams.session = decoder.readInt(8);
                oemCryptoServiceQueryKeyControlParams.keyId = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceQueryKeyControlParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.keyId, 16, 0, -1);
        }

        private OemCryptoServiceQueryKeyControlParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceQueryKeyControlResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] keyControlBlock;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceQueryKeyControlResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceQueryKeyControlResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceQueryKeyControlResponseParams oemCryptoServiceQueryKeyControlResponseParams = new OemCryptoServiceQueryKeyControlResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceQueryKeyControlResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceQueryKeyControlResponseParams.keyControlBlock = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceQueryKeyControlResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.keyControlBlock, 16, 1, -1);
        }

        private OemCryptoServiceQueryKeyControlResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceQueryKeyControlResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.QueryKeyControlResponse mCallback;

        OemCryptoServiceQueryKeyControlResponseParamsForwardToCallback(OemCryptoService.QueryKeyControlResponse queryKeyControlResponse) {
            this.mCallback = queryKeyControlResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                OemCryptoServiceQueryKeyControlResponseParams oemCryptoServiceQueryKeyControlResponseParamsDeserialize = OemCryptoServiceQueryKeyControlResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceQueryKeyControlResponseParamsDeserialize.result), oemCryptoServiceQueryKeyControlResponseParamsDeserialize.keyControlBlock);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceQueryKeyControlResponseParamsProxyToResponder implements OemCryptoService.QueryKeyControlResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceQueryKeyControlResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceQueryKeyControlResponseParams oemCryptoServiceQueryKeyControlResponseParams = new OemCryptoServiceQueryKeyControlResponseParams();
            oemCryptoServiceQueryKeyControlResponseParams.result = num.intValue();
            oemCryptoServiceQueryKeyControlResponseParams.keyControlBlock = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceQueryKeyControlResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceRefreshKeysParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OemCryptoKeyRefreshObject[] keyArray;
        public byte[] message;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRefreshKeysParams() {
            super(40, 0);
        }

        public static OemCryptoServiceRefreshKeysParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceRefreshKeysParams oemCryptoServiceRefreshKeysParams = new OemCryptoServiceRefreshKeysParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceRefreshKeysParams.session = decoderOutline3.readInt(8);
                oemCryptoServiceRefreshKeysParams.message = decoderOutline3.readBytes(16, 0, -1);
                oemCryptoServiceRefreshKeysParams.signature = decoderOutline3.readBytes(24, 0, -1);
                Decoder pointer = decoderOutline3.readPointer(32, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceRefreshKeysParams.keyArray = new OemCryptoKeyRefreshObject[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    oemCryptoServiceRefreshKeysParams.keyArray[i] = OemCryptoKeyRefreshObject.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return oemCryptoServiceRefreshKeysParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
            OemCryptoKeyRefreshObject[] oemCryptoKeyRefreshObjectArr = this.keyArray;
            if (oemCryptoKeyRefreshObjectArr == null) {
                encoderAtDataOffset.encodeNullPointer(32, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(oemCryptoKeyRefreshObjectArr.length, 32, -1);
            int i = 0;
            while (true) {
                OemCryptoKeyRefreshObject[] oemCryptoKeyRefreshObjectArr2 = this.keyArray;
                if (i >= oemCryptoKeyRefreshObjectArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) oemCryptoKeyRefreshObjectArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private OemCryptoServiceRefreshKeysParams(int i) {
            super(40, i);
        }
    }

    static final class OemCryptoServiceRefreshKeysResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRefreshKeysResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceRefreshKeysResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceRefreshKeysResponseParams oemCryptoServiceRefreshKeysResponseParams = new OemCryptoServiceRefreshKeysResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceRefreshKeysResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceRefreshKeysResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceRefreshKeysResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceRefreshKeysResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.RefreshKeysResponse mCallback;

        OemCryptoServiceRefreshKeysResponseParamsForwardToCallback(OemCryptoService.RefreshKeysResponse refreshKeysResponse) {
            this.mCallback = refreshKeysResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(61, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceRefreshKeysResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceRefreshKeysResponseParamsProxyToResponder implements OemCryptoService.RefreshKeysResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceRefreshKeysResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceRefreshKeysResponseParams oemCryptoServiceRefreshKeysResponseParams = new OemCryptoServiceRefreshKeysResponseParams();
            oemCryptoServiceRefreshKeysResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceRefreshKeysResponseParams.serializeWithHeader(this.mCore, new MessageHeader(61, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceRefreshKeysV14Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OemCryptoKeyRefreshObjectV14[] keyArray;
        public byte[] message;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRefreshKeysV14Params() {
            super(40, 0);
        }

        public static OemCryptoServiceRefreshKeysV14Params deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                OemCryptoServiceRefreshKeysV14Params oemCryptoServiceRefreshKeysV14Params = new OemCryptoServiceRefreshKeysV14Params(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceRefreshKeysV14Params.session = decoderOutline3.readInt(8);
                oemCryptoServiceRefreshKeysV14Params.message = decoderOutline3.readBytes(16, 0, -1);
                oemCryptoServiceRefreshKeysV14Params.signature = decoderOutline3.readBytes(24, 0, -1);
                Decoder pointer = decoderOutline3.readPointer(32, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                oemCryptoServiceRefreshKeysV14Params.keyArray = new OemCryptoKeyRefreshObjectV14[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    oemCryptoServiceRefreshKeysV14Params.keyArray[i] = OemCryptoKeyRefreshObjectV14.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return oemCryptoServiceRefreshKeysV14Params;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
            OemCryptoKeyRefreshObjectV14[] oemCryptoKeyRefreshObjectV14Arr = this.keyArray;
            if (oemCryptoKeyRefreshObjectV14Arr == null) {
                encoderAtDataOffset.encodeNullPointer(32, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(oemCryptoKeyRefreshObjectV14Arr.length, 32, -1);
            int i = 0;
            while (true) {
                OemCryptoKeyRefreshObjectV14[] oemCryptoKeyRefreshObjectV14Arr2 = this.keyArray;
                if (i >= oemCryptoKeyRefreshObjectV14Arr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) oemCryptoKeyRefreshObjectV14Arr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private OemCryptoServiceRefreshKeysV14Params(int i) {
            super(40, i);
        }
    }

    static final class OemCryptoServiceRefreshKeysV14ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRefreshKeysV14ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceRefreshKeysV14ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceRefreshKeysV14ResponseParams oemCryptoServiceRefreshKeysV14ResponseParams = new OemCryptoServiceRefreshKeysV14ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceRefreshKeysV14ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceRefreshKeysV14ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceRefreshKeysV14ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceRefreshKeysV14ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.RefreshKeysV14Response mCallback;

        OemCryptoServiceRefreshKeysV14ResponseParamsForwardToCallback(OemCryptoService.RefreshKeysV14Response refreshKeysV14Response) {
            this.mCallback = refreshKeysV14Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceRefreshKeysV14ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceRefreshKeysV14ResponseParamsProxyToResponder implements OemCryptoService.RefreshKeysV14Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceRefreshKeysV14ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceRefreshKeysV14ResponseParams oemCryptoServiceRefreshKeysV14ResponseParams = new OemCryptoServiceRefreshKeysV14ResponseParams();
            oemCryptoServiceRefreshKeysV14ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceRefreshKeysV14ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceRemoveSrmParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRemoveSrmParams() {
            super(8, 0);
        }

        public static OemCryptoServiceRemoveSrmParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceRemoveSrmParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceRemoveSrmParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceRemoveSrmResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRemoveSrmResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceRemoveSrmResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceRemoveSrmResponseParams oemCryptoServiceRemoveSrmResponseParams = new OemCryptoServiceRemoveSrmResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceRemoveSrmResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceRemoveSrmResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceRemoveSrmResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceRemoveSrmResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.RemoveSrmResponse mCallback;

        OemCryptoServiceRemoveSrmResponseParamsForwardToCallback(OemCryptoService.RemoveSrmResponse removeSrmResponse) {
            this.mCallback = removeSrmResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(42, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceRemoveSrmResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceRemoveSrmResponseParamsProxyToResponder implements OemCryptoService.RemoveSrmResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceRemoveSrmResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceRemoveSrmResponseParams oemCryptoServiceRemoveSrmResponseParams = new OemCryptoServiceRemoveSrmResponseParams();
            oemCryptoServiceRemoveSrmResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceRemoveSrmResponseParams.serializeWithHeader(this.mCore, new MessageHeader(42, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceReportUsageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] pst;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceReportUsageParams() {
            super(24, 0);
        }

        public static OemCryptoServiceReportUsageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceReportUsageParams oemCryptoServiceReportUsageParams = new OemCryptoServiceReportUsageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceReportUsageParams.session = decoder.readInt(8);
                oemCryptoServiceReportUsageParams.pst = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceReportUsageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.pst, 16, 0, -1);
        }

        private OemCryptoServiceReportUsageParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceReportUsageResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OemCryptoPstReport report;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceReportUsageResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceReportUsageResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceReportUsageResponseParams oemCryptoServiceReportUsageResponseParams = new OemCryptoServiceReportUsageResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceReportUsageResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceReportUsageResponseParams.report = OemCryptoPstReport.decode(decoder.readPointer(16, true));
                return oemCryptoServiceReportUsageResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode((Struct) this.report, 16, true);
        }

        private OemCryptoServiceReportUsageResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceReportUsageResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.ReportUsageResponse mCallback;

        OemCryptoServiceReportUsageResponseParamsForwardToCallback(OemCryptoService.ReportUsageResponse reportUsageResponse) {
            this.mCallback = reportUsageResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(32, 2)) {
                    return false;
                }
                OemCryptoServiceReportUsageResponseParams oemCryptoServiceReportUsageResponseParamsDeserialize = OemCryptoServiceReportUsageResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceReportUsageResponseParamsDeserialize.result), oemCryptoServiceReportUsageResponseParamsDeserialize.report);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceReportUsageResponseParamsProxyToResponder implements OemCryptoService.ReportUsageResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceReportUsageResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, OemCryptoPstReport oemCryptoPstReport) {
            OemCryptoServiceReportUsageResponseParams oemCryptoServiceReportUsageResponseParams = new OemCryptoServiceReportUsageResponseParams();
            oemCryptoServiceReportUsageResponseParams.result = num.intValue();
            oemCryptoServiceReportUsageResponseParams.report = oemCryptoPstReport;
            this.mMessageReceiver.accept(oemCryptoServiceReportUsageResponseParams.serializeWithHeader(this.mCore, new MessageHeader(32, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceResourceRatingTierParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceResourceRatingTierParams() {
            super(8, 0);
        }

        public static OemCryptoServiceResourceRatingTierParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceResourceRatingTierParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceResourceRatingTierParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceResourceRatingTierResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int ratingTier;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceResourceRatingTierResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceResourceRatingTierResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceResourceRatingTierResponseParams oemCryptoServiceResourceRatingTierResponseParams = new OemCryptoServiceResourceRatingTierResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceResourceRatingTierResponseParams.ratingTier = decoder.readInt(8);
                return oemCryptoServiceResourceRatingTierResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.ratingTier, 8);
        }

        private OemCryptoServiceResourceRatingTierResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceResourceRatingTierResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.ResourceRatingTierResponse mCallback;

        OemCryptoServiceResourceRatingTierResponseParamsForwardToCallback(OemCryptoService.ResourceRatingTierResponse resourceRatingTierResponse) {
            this.mCallback = resourceRatingTierResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(59, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceResourceRatingTierResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).ratingTier));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceResourceRatingTierResponseParamsProxyToResponder implements OemCryptoService.ResourceRatingTierResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceResourceRatingTierResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceResourceRatingTierResponseParams oemCryptoServiceResourceRatingTierResponseParams = new OemCryptoServiceResourceRatingTierResponseParams();
            oemCryptoServiceResourceRatingTierResponseParams.ratingTier = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceResourceRatingTierResponseParams.serializeWithHeader(this.mCore, new MessageHeader(59, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceRewrapDeviceRsaKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int encRsaKeyIvOffset;
        public int encRsaKeyLength;
        public int encRsaKeyOffset;
        public byte[] message;
        public int nonceOffset;
        public int session;
        public byte[] signature;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRewrapDeviceRsaKeyParams() {
            super(48, 0);
        }

        public static OemCryptoServiceRewrapDeviceRsaKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceRewrapDeviceRsaKeyParams oemCryptoServiceRewrapDeviceRsaKeyParams = new OemCryptoServiceRewrapDeviceRsaKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceRewrapDeviceRsaKeyParams.session = decoder.readInt(8);
                oemCryptoServiceRewrapDeviceRsaKeyParams.nonceOffset = decoder.readInt(12);
                oemCryptoServiceRewrapDeviceRsaKeyParams.message = decoder.readBytes(16, 0, -1);
                oemCryptoServiceRewrapDeviceRsaKeyParams.signature = decoder.readBytes(24, 0, -1);
                oemCryptoServiceRewrapDeviceRsaKeyParams.encRsaKeyOffset = decoder.readInt(32);
                oemCryptoServiceRewrapDeviceRsaKeyParams.encRsaKeyLength = decoder.readInt(36);
                oemCryptoServiceRewrapDeviceRsaKeyParams.encRsaKeyIvOffset = decoder.readInt(40);
                return oemCryptoServiceRewrapDeviceRsaKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.nonceOffset, 12);
            encoderAtDataOffset.encode(this.message, 16, 0, -1);
            encoderAtDataOffset.encode(this.signature, 24, 0, -1);
            encoderAtDataOffset.encode(this.encRsaKeyOffset, 32);
            encoderAtDataOffset.encode(this.encRsaKeyLength, 36);
            encoderAtDataOffset.encode(this.encRsaKeyIvOffset, 40);
        }

        private OemCryptoServiceRewrapDeviceRsaKeyParams(int i) {
            super(48, i);
        }
    }

    static final class OemCryptoServiceRewrapDeviceRsaKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public byte[] wrappedKey;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceRewrapDeviceRsaKeyResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceRewrapDeviceRsaKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceRewrapDeviceRsaKeyResponseParams oemCryptoServiceRewrapDeviceRsaKeyResponseParams = new OemCryptoServiceRewrapDeviceRsaKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceRewrapDeviceRsaKeyResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceRewrapDeviceRsaKeyResponseParams.wrappedKey = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceRewrapDeviceRsaKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.wrappedKey, 16, 1, -1);
        }

        private OemCryptoServiceRewrapDeviceRsaKeyResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceRewrapDeviceRsaKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.RewrapDeviceRsaKeyResponse mCallback;

        OemCryptoServiceRewrapDeviceRsaKeyResponseParamsForwardToCallback(OemCryptoService.RewrapDeviceRsaKeyResponse rewrapDeviceRsaKeyResponse) {
            this.mCallback = rewrapDeviceRsaKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(24, 2)) {
                    return false;
                }
                OemCryptoServiceRewrapDeviceRsaKeyResponseParams oemCryptoServiceRewrapDeviceRsaKeyResponseParamsDeserialize = OemCryptoServiceRewrapDeviceRsaKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceRewrapDeviceRsaKeyResponseParamsDeserialize.result), oemCryptoServiceRewrapDeviceRsaKeyResponseParamsDeserialize.wrappedKey);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceRewrapDeviceRsaKeyResponseParamsProxyToResponder implements OemCryptoService.RewrapDeviceRsaKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceRewrapDeviceRsaKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceRewrapDeviceRsaKeyResponseParams oemCryptoServiceRewrapDeviceRsaKeyResponseParams = new OemCryptoServiceRewrapDeviceRsaKeyResponseParams();
            oemCryptoServiceRewrapDeviceRsaKeyResponseParams.result = num.intValue();
            oemCryptoServiceRewrapDeviceRsaKeyResponseParams.wrappedKey = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceRewrapDeviceRsaKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(24, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceSecurityPatchLevelParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSecurityPatchLevelParams() {
            super(8, 0);
        }

        public static OemCryptoServiceSecurityPatchLevelParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceSecurityPatchLevelParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceSecurityPatchLevelParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceSecurityPatchLevelResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte securityPatchLevel;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSecurityPatchLevelResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceSecurityPatchLevelResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceSecurityPatchLevelResponseParams oemCryptoServiceSecurityPatchLevelResponseParams = new OemCryptoServiceSecurityPatchLevelResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceSecurityPatchLevelResponseParams.securityPatchLevel = decoder.readByte(8);
                return oemCryptoServiceSecurityPatchLevelResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.securityPatchLevel, 8);
        }

        private OemCryptoServiceSecurityPatchLevelResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceSecurityPatchLevelResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.SecurityPatchLevelResponse mCallback;

        OemCryptoServiceSecurityPatchLevelResponseParamsForwardToCallback(OemCryptoService.SecurityPatchLevelResponse securityPatchLevelResponse) {
            this.mCallback = securityPatchLevelResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(28, 2)) {
                    return false;
                }
                this.mCallback.call(Byte.valueOf(OemCryptoServiceSecurityPatchLevelResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).securityPatchLevel));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceSecurityPatchLevelResponseParamsProxyToResponder implements OemCryptoService.SecurityPatchLevelResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceSecurityPatchLevelResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Byte b) {
            OemCryptoServiceSecurityPatchLevelResponseParams oemCryptoServiceSecurityPatchLevelResponseParams = new OemCryptoServiceSecurityPatchLevelResponseParams();
            oemCryptoServiceSecurityPatchLevelResponseParams.securityPatchLevel = b.byteValue();
            this.mMessageReceiver.accept(oemCryptoServiceSecurityPatchLevelResponseParams.serializeWithHeader(this.mCore, new MessageHeader(28, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceSelectKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int cipherMode;
        public byte[] contentKeyId;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSelectKeyParams() {
            super(24, 0);
        }

        public static OemCryptoServiceSelectKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceSelectKeyParams oemCryptoServiceSelectKeyParams = new OemCryptoServiceSelectKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceSelectKeyParams.session = decoder.readInt(8);
                int i = decoder.readInt(12);
                oemCryptoServiceSelectKeyParams.cipherMode = i;
                OemCryptoCipherMode.validate(i);
                oemCryptoServiceSelectKeyParams.contentKeyId = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceSelectKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.cipherMode, 12);
            encoderAtDataOffset.encode(this.contentKeyId, 16, 0, -1);
        }

        private OemCryptoServiceSelectKeyParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceSelectKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSelectKeyResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceSelectKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceSelectKeyResponseParams oemCryptoServiceSelectKeyResponseParams = new OemCryptoServiceSelectKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceSelectKeyResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceSelectKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceSelectKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceSelectKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.SelectKeyResponse mCallback;

        OemCryptoServiceSelectKeyResponseParamsForwardToCallback(OemCryptoService.SelectKeyResponse selectKeyResponse) {
            this.mCallback = selectKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(56, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceSelectKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceSelectKeyResponseParamsProxyToResponder implements OemCryptoService.SelectKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceSelectKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceSelectKeyResponseParams oemCryptoServiceSelectKeyResponseParams = new OemCryptoServiceSelectKeyResponseParams();
            oemCryptoServiceSelectKeyResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceSelectKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(56, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceSelectKeyV13Params extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] keyId;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSelectKeyV13Params() {
            super(24, 0);
        }

        public static OemCryptoServiceSelectKeyV13Params deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceSelectKeyV13Params oemCryptoServiceSelectKeyV13Params = new OemCryptoServiceSelectKeyV13Params(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceSelectKeyV13Params.session = decoder.readInt(8);
                oemCryptoServiceSelectKeyV13Params.keyId = decoder.readBytes(16, 0, -1);
                return oemCryptoServiceSelectKeyV13Params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.keyId, 16, 0, -1);
        }

        private OemCryptoServiceSelectKeyV13Params(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceSelectKeyV13ResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSelectKeyV13ResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceSelectKeyV13ResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceSelectKeyV13ResponseParams oemCryptoServiceSelectKeyV13ResponseParams = new OemCryptoServiceSelectKeyV13ResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceSelectKeyV13ResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceSelectKeyV13ResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceSelectKeyV13ResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceSelectKeyV13ResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.SelectKeyV13Response mCallback;

        OemCryptoServiceSelectKeyV13ResponseParamsForwardToCallback(OemCryptoService.SelectKeyV13Response selectKeyV13Response) {
            this.mCallback = selectKeyV13Response;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(10, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceSelectKeyV13ResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceSelectKeyV13ResponseParamsProxyToResponder implements OemCryptoService.SelectKeyV13Response {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceSelectKeyV13ResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceSelectKeyV13ResponseParams oemCryptoServiceSelectKeyV13ResponseParams = new OemCryptoServiceSelectKeyV13ResponseParams();
            oemCryptoServiceSelectKeyV13ResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceSelectKeyV13ResponseParams.serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceShrinkUsageTableHeaderParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int availHeaderLength;
        public int newEntryCount;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceShrinkUsageTableHeaderParams() {
            super(16, 0);
        }

        public static OemCryptoServiceShrinkUsageTableHeaderParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceShrinkUsageTableHeaderParams oemCryptoServiceShrinkUsageTableHeaderParams = new OemCryptoServiceShrinkUsageTableHeaderParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceShrinkUsageTableHeaderParams.newEntryCount = decoder.readInt(8);
                oemCryptoServiceShrinkUsageTableHeaderParams.availHeaderLength = decoder.readInt(12);
                return oemCryptoServiceShrinkUsageTableHeaderParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.newEntryCount, 8);
            encoderAtDataOffset.encode(this.availHeaderLength, 12);
        }

        private OemCryptoServiceShrinkUsageTableHeaderParams(int i) {
            super(16, i);
        }
    }

    static final class OemCryptoServiceShrinkUsageTableHeaderResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] header;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceShrinkUsageTableHeaderResponseParams() {
            super(24, 0);
        }

        public static OemCryptoServiceShrinkUsageTableHeaderResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceShrinkUsageTableHeaderResponseParams oemCryptoServiceShrinkUsageTableHeaderResponseParams = new OemCryptoServiceShrinkUsageTableHeaderResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceShrinkUsageTableHeaderResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceShrinkUsageTableHeaderResponseParams.header = decoder.readBytes(16, 1, -1);
                return oemCryptoServiceShrinkUsageTableHeaderResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.header, 16, 1, -1);
        }

        private OemCryptoServiceShrinkUsageTableHeaderResponseParams(int i) {
            super(24, i);
        }
    }

    static class OemCryptoServiceShrinkUsageTableHeaderResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.ShrinkUsageTableHeaderResponse mCallback;

        OemCryptoServiceShrinkUsageTableHeaderResponseParamsForwardToCallback(OemCryptoService.ShrinkUsageTableHeaderResponse shrinkUsageTableHeaderResponse) {
            this.mCallback = shrinkUsageTableHeaderResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(49, 2)) {
                    return false;
                }
                OemCryptoServiceShrinkUsageTableHeaderResponseParams oemCryptoServiceShrinkUsageTableHeaderResponseParamsDeserialize = OemCryptoServiceShrinkUsageTableHeaderResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceShrinkUsageTableHeaderResponseParamsDeserialize.result), oemCryptoServiceShrinkUsageTableHeaderResponseParamsDeserialize.header);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceShrinkUsageTableHeaderResponseParamsProxyToResponder implements OemCryptoService.ShrinkUsageTableHeaderResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceShrinkUsageTableHeaderResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            OemCryptoServiceShrinkUsageTableHeaderResponseParams oemCryptoServiceShrinkUsageTableHeaderResponseParams = new OemCryptoServiceShrinkUsageTableHeaderResponseParams();
            oemCryptoServiceShrinkUsageTableHeaderResponseParams.result = num.intValue();
            oemCryptoServiceShrinkUsageTableHeaderResponseParams.header = bArr;
            this.mMessageReceiver.accept(oemCryptoServiceShrinkUsageTableHeaderResponseParams.serializeWithHeader(this.mCore, new MessageHeader(49, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceSupportedCertificatesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSupportedCertificatesParams() {
            super(8, 0);
        }

        public static OemCryptoServiceSupportedCertificatesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceSupportedCertificatesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceSupportedCertificatesParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceSupportedCertificatesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceSupportedCertificatesResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceSupportedCertificatesResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceSupportedCertificatesResponseParams oemCryptoServiceSupportedCertificatesResponseParams = new OemCryptoServiceSupportedCertificatesResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceSupportedCertificatesResponseParams.result = decoder.readInt(8);
                return oemCryptoServiceSupportedCertificatesResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceSupportedCertificatesResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceSupportedCertificatesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.SupportedCertificatesResponse mCallback;

        OemCryptoServiceSupportedCertificatesResponseParamsForwardToCallback(OemCryptoService.SupportedCertificatesResponse supportedCertificatesResponse) {
            this.mCallback = supportedCertificatesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(38, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceSupportedCertificatesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceSupportedCertificatesResponseParamsProxyToResponder implements OemCryptoService.SupportedCertificatesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceSupportedCertificatesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceSupportedCertificatesResponseParams oemCryptoServiceSupportedCertificatesResponseParams = new OemCryptoServiceSupportedCertificatesResponseParams();
            oemCryptoServiceSupportedCertificatesResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceSupportedCertificatesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(38, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceTerminateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceTerminateParams() {
            super(8, 0);
        }

        public static OemCryptoServiceTerminateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceTerminateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceTerminateParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceTerminateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceTerminateResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceTerminateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceTerminateResponseParams oemCryptoServiceTerminateResponseParams = new OemCryptoServiceTerminateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceTerminateResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceTerminateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceTerminateResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceTerminateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.TerminateResponse mCallback;

        OemCryptoServiceTerminateResponseParamsForwardToCallback(OemCryptoService.TerminateResponse terminateResponse) {
            this.mCallback = terminateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceTerminateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceTerminateResponseParamsProxyToResponder implements OemCryptoService.TerminateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceTerminateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceTerminateResponseParams oemCryptoServiceTerminateResponseParams = new OemCryptoServiceTerminateResponseParams();
            oemCryptoServiceTerminateResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceTerminateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceUpdateUsageEntryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int availEntryLength;
        public int availHeaderLength;
        public int session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceUpdateUsageEntryParams() {
            super(24, 0);
        }

        public static OemCryptoServiceUpdateUsageEntryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceUpdateUsageEntryParams oemCryptoServiceUpdateUsageEntryParams = new OemCryptoServiceUpdateUsageEntryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoServiceUpdateUsageEntryParams.session = decoder.readInt(8);
                oemCryptoServiceUpdateUsageEntryParams.availHeaderLength = decoder.readInt(12);
                oemCryptoServiceUpdateUsageEntryParams.availEntryLength = decoder.readInt(16);
                return oemCryptoServiceUpdateUsageEntryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.session, 8);
            encoderAtDataOffset.encode(this.availHeaderLength, 12);
            encoderAtDataOffset.encode(this.availEntryLength, 16);
        }

        private OemCryptoServiceUpdateUsageEntryParams(int i) {
            super(24, i);
        }
    }

    static final class OemCryptoServiceUpdateUsageEntryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] entry;
        public byte[] header;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceUpdateUsageEntryResponseParams() {
            super(32, 0);
        }

        public static OemCryptoServiceUpdateUsageEntryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceUpdateUsageEntryResponseParams oemCryptoServiceUpdateUsageEntryResponseParams = new OemCryptoServiceUpdateUsageEntryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceUpdateUsageEntryResponseParams.result = i;
                OemCryptoResult.validate(i);
                oemCryptoServiceUpdateUsageEntryResponseParams.header = decoder.readBytes(16, 1, -1);
                oemCryptoServiceUpdateUsageEntryResponseParams.entry = decoder.readBytes(24, 1, -1);
                return oemCryptoServiceUpdateUsageEntryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode(this.header, 16, 1, -1);
            encoderAtDataOffset.encode(this.entry, 24, 1, -1);
        }

        private OemCryptoServiceUpdateUsageEntryResponseParams(int i) {
            super(32, i);
        }
    }

    static class OemCryptoServiceUpdateUsageEntryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.UpdateUsageEntryResponse mCallback;

        OemCryptoServiceUpdateUsageEntryResponseParamsForwardToCallback(OemCryptoService.UpdateUsageEntryResponse updateUsageEntryResponse) {
            this.mCallback = updateUsageEntryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(47, 2)) {
                    return false;
                }
                OemCryptoServiceUpdateUsageEntryResponseParams oemCryptoServiceUpdateUsageEntryResponseParamsDeserialize = OemCryptoServiceUpdateUsageEntryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(oemCryptoServiceUpdateUsageEntryResponseParamsDeserialize.result), oemCryptoServiceUpdateUsageEntryResponseParamsDeserialize.header, oemCryptoServiceUpdateUsageEntryResponseParamsDeserialize.entry);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceUpdateUsageEntryResponseParamsProxyToResponder implements OemCryptoService.UpdateUsageEntryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceUpdateUsageEntryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Integer num, byte[] bArr, byte[] bArr2) {
            OemCryptoServiceUpdateUsageEntryResponseParams oemCryptoServiceUpdateUsageEntryResponseParams = new OemCryptoServiceUpdateUsageEntryResponseParams();
            oemCryptoServiceUpdateUsageEntryResponseParams.result = num.intValue();
            oemCryptoServiceUpdateUsageEntryResponseParams.header = bArr;
            oemCryptoServiceUpdateUsageEntryResponseParams.entry = bArr2;
            this.mMessageReceiver.accept(oemCryptoServiceUpdateUsageEntryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(47, 2, this.mRequestId)));
        }
    }

    static final class OemCryptoServiceUpdateUsageTableParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceUpdateUsageTableParams() {
            super(8, 0);
        }

        public static OemCryptoServiceUpdateUsageTableParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new OemCryptoServiceUpdateUsageTableParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private OemCryptoServiceUpdateUsageTableParams(int i) {
            super(8, i);
        }
    }

    static final class OemCryptoServiceUpdateUsageTableResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoServiceUpdateUsageTableResponseParams() {
            super(16, 0);
        }

        public static OemCryptoServiceUpdateUsageTableResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoServiceUpdateUsageTableResponseParams oemCryptoServiceUpdateUsageTableResponseParams = new OemCryptoServiceUpdateUsageTableResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                oemCryptoServiceUpdateUsageTableResponseParams.result = i;
                OemCryptoResult.validate(i);
                return oemCryptoServiceUpdateUsageTableResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private OemCryptoServiceUpdateUsageTableResponseParams(int i) {
            super(16, i);
        }
    }

    static class OemCryptoServiceUpdateUsageTableResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OemCryptoService.UpdateUsageTableResponse mCallback;

        OemCryptoServiceUpdateUsageTableResponseParamsForwardToCallback(OemCryptoService.UpdateUsageTableResponse updateUsageTableResponse) {
            this.mCallback = updateUsageTableResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(30, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(OemCryptoServiceUpdateUsageTableResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class OemCryptoServiceUpdateUsageTableResponseParamsProxyToResponder implements OemCryptoService.UpdateUsageTableResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        OemCryptoServiceUpdateUsageTableResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            OemCryptoServiceUpdateUsageTableResponseParams oemCryptoServiceUpdateUsageTableResponseParams = new OemCryptoServiceUpdateUsageTableResponseParams();
            oemCryptoServiceUpdateUsageTableResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(oemCryptoServiceUpdateUsageTableResponseParams.serializeWithHeader(this.mCore, new MessageHeader(30, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements OemCryptoService.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void buildInformation(OemCryptoService.BuildInformationResponse buildInformationResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceBuildInformationParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(60, 1, 0L)), new OemCryptoServiceBuildInformationResponseParamsForwardToCallback(buildInformationResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void closeSession(int i, OemCryptoService.CloseSessionResponse closeSessionResponse) {
            OemCryptoServiceCloseSessionParams oemCryptoServiceCloseSessionParams = new OemCryptoServiceCloseSessionParams();
            oemCryptoServiceCloseSessionParams.session = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceCloseSessionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new OemCryptoServiceCloseSessionResponseParamsForwardToCallback(closeSessionResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void copyBuffer(int i, byte[] bArr, OemCryptoSecureBuffer oemCryptoSecureBuffer, byte b, OemCryptoService.CopyBufferResponse copyBufferResponse) {
            OemCryptoServiceCopyBufferParams oemCryptoServiceCopyBufferParams = new OemCryptoServiceCopyBufferParams();
            oemCryptoServiceCopyBufferParams.session = i;
            oemCryptoServiceCopyBufferParams.data = bArr;
            oemCryptoServiceCopyBufferParams.outBuffer = oemCryptoSecureBuffer;
            oemCryptoServiceCopyBufferParams.subsampleFlags = b;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceCopyBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(76, 1, 0L)), new OemCryptoServiceCopyBufferResponseParamsForwardToCallback(copyBufferResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void copyBufferV14(byte[] bArr, OemCryptoSecureBuffer oemCryptoSecureBuffer, OemCryptoService.CopyBufferV14Response copyBufferV14Response) {
            OemCryptoServiceCopyBufferV14Params oemCryptoServiceCopyBufferV14Params = new OemCryptoServiceCopyBufferV14Params();
            oemCryptoServiceCopyBufferV14Params.data = bArr;
            oemCryptoServiceCopyBufferV14Params.outBuffer = oemCryptoSecureBuffer;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceCopyBufferV14Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16, 1, 0L)), new OemCryptoServiceCopyBufferV14ResponseParamsForwardToCallback(copyBufferV14Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void copyOldUsageEntry(int i, byte[] bArr, OemCryptoService.CopyOldUsageEntryResponse copyOldUsageEntryResponse) {
            OemCryptoServiceCopyOldUsageEntryParams oemCryptoServiceCopyOldUsageEntryParams = new OemCryptoServiceCopyOldUsageEntryParams();
            oemCryptoServiceCopyOldUsageEntryParams.session = i;
            oemCryptoServiceCopyOldUsageEntryParams.pst = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceCopyOldUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(51, 1, 0L)), new OemCryptoServiceCopyOldUsageEntryResponseParamsForwardToCallback(copyOldUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void createNewUsageEntry(int i, OemCryptoService.CreateNewUsageEntryResponse createNewUsageEntryResponse) {
            OemCryptoServiceCreateNewUsageEntryParams oemCryptoServiceCreateNewUsageEntryParams = new OemCryptoServiceCreateNewUsageEntryParams();
            oemCryptoServiceCreateNewUsageEntryParams.session = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceCreateNewUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(45, 1, 0L)), new OemCryptoServiceCreateNewUsageEntryResponseParamsForwardToCallback(createNewUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void createOldUsageEntry(long j, long j2, long j3, int i, byte[] bArr, byte[] bArr2, byte[] bArr3, OemCryptoService.CreateOldUsageEntryResponse createOldUsageEntryResponse) {
            OemCryptoServiceCreateOldUsageEntryParams oemCryptoServiceCreateOldUsageEntryParams = new OemCryptoServiceCreateOldUsageEntryParams();
            oemCryptoServiceCreateOldUsageEntryParams.timeSinceLicenseReceived = j;
            oemCryptoServiceCreateOldUsageEntryParams.timeSinceFirstDecrypt = j2;
            oemCryptoServiceCreateOldUsageEntryParams.timeSinceLastDecrypt = j3;
            oemCryptoServiceCreateOldUsageEntryParams.status = i;
            oemCryptoServiceCreateOldUsageEntryParams.serverMacKey = bArr;
            oemCryptoServiceCreateOldUsageEntryParams.clientMacKey = bArr2;
            oemCryptoServiceCreateOldUsageEntryParams.pst = bArr3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceCreateOldUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(52, 1, 0L)), new OemCryptoServiceCreateOldUsageEntryResponseParamsForwardToCallback(createOldUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void createUsageTableHeader(int i, OemCryptoService.CreateUsageTableHeaderResponse createUsageTableHeaderResponse) {
            OemCryptoServiceCreateUsageTableHeaderParams oemCryptoServiceCreateUsageTableHeaderParams = new OemCryptoServiceCreateUsageTableHeaderParams();
            oemCryptoServiceCreateUsageTableHeaderParams.availHeaderLength = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceCreateUsageTableHeaderParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(43, 1, 0L)), new OemCryptoServiceCreateUsageTableHeaderResponseParamsForwardToCallback(createUsageTableHeaderResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void deactivateUsageEntry(int i, byte[] bArr, OemCryptoService.DeactivateUsageEntryResponse deactivateUsageEntryResponse) {
            OemCryptoServiceDeactivateUsageEntryParams oemCryptoServiceDeactivateUsageEntryParams = new OemCryptoServiceDeactivateUsageEntryParams();
            oemCryptoServiceDeactivateUsageEntryParams.session = i;
            oemCryptoServiceDeactivateUsageEntryParams.pst = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceDeactivateUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(48, 1, 0L)), new OemCryptoServiceDeactivateUsageEntryResponseParamsForwardToCallback(deactivateUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void deactivateUsageEntryV12(byte[] bArr, OemCryptoService.DeactivateUsageEntryV12Response deactivateUsageEntryV12Response) {
            OemCryptoServiceDeactivateUsageEntryV12Params oemCryptoServiceDeactivateUsageEntryV12Params = new OemCryptoServiceDeactivateUsageEntryV12Params();
            oemCryptoServiceDeactivateUsageEntryV12Params.pst = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceDeactivateUsageEntryV12Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(31, 1, 0L)), new OemCryptoServiceDeactivateUsageEntryV12ResponseParamsForwardToCallback(deactivateUsageEntryV12Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void decryptCenc(int i, byte[] bArr, byte[] bArr2, SubSampleDescription[] subSampleDescriptionArr, OemCryptoCencEncryptPatternDesc oemCryptoCencEncryptPatternDesc, OemCryptoSecureBuffer oemCryptoSecureBuffer, OemCryptoService.DecryptCencResponse decryptCencResponse) {
            OemCryptoServiceDecryptCencParams oemCryptoServiceDecryptCencParams = new OemCryptoServiceDecryptCencParams();
            oemCryptoServiceDecryptCencParams.session = i;
            oemCryptoServiceDecryptCencParams.data = bArr;
            oemCryptoServiceDecryptCencParams.iv = bArr2;
            oemCryptoServiceDecryptCencParams.subSamples = subSampleDescriptionArr;
            oemCryptoServiceDecryptCencParams.pattern = oemCryptoCencEncryptPatternDesc;
            oemCryptoServiceDecryptCencParams.secureBuffer = oemCryptoSecureBuffer;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceDecryptCencParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(75, 1, 0L)), new OemCryptoServiceDecryptCencResponseParamsForwardToCallback(decryptCencResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void decryptCencV15(int i, byte[] bArr, boolean z, byte[] bArr2, int i2, OemCryptoSecureBuffer oemCryptoSecureBuffer, OemCryptoCencEncryptPatternDesc oemCryptoCencEncryptPatternDesc, OemCryptoService.DecryptCencV15Response decryptCencV15Response) {
            OemCryptoServiceDecryptCencV15Params oemCryptoServiceDecryptCencV15Params = new OemCryptoServiceDecryptCencV15Params();
            oemCryptoServiceDecryptCencV15Params.session = i;
            oemCryptoServiceDecryptCencV15Params.data = bArr;
            oemCryptoServiceDecryptCencV15Params.isEncrypted = z;
            oemCryptoServiceDecryptCencV15Params.iv = bArr2;
            oemCryptoServiceDecryptCencV15Params.blockOffset = i2;
            oemCryptoServiceDecryptCencV15Params.secureBuffer = oemCryptoSecureBuffer;
            oemCryptoServiceDecryptCencV15Params.pattern = oemCryptoCencEncryptPatternDesc;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceDecryptCencV15Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11, 1, 0L)), new OemCryptoServiceDecryptCencV15ResponseParamsForwardToCallback(decryptCencV15Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void deleteOldUsageTable(OemCryptoService.DeleteOldUsageTableResponse deleteOldUsageTableResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceDeleteOldUsageTableParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(35, 1, 0L)), new OemCryptoServiceDeleteOldUsageTableResponseParamsForwardToCallback(deleteOldUsageTableResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void deleteUsageEntry(int i, int i2, int i3, byte[] bArr, byte[] bArr2, OemCryptoService.DeleteUsageEntryResponse deleteUsageEntryResponse) {
            OemCryptoServiceDeleteUsageEntryParams oemCryptoServiceDeleteUsageEntryParams = new OemCryptoServiceDeleteUsageEntryParams();
            oemCryptoServiceDeleteUsageEntryParams.session = i;
            oemCryptoServiceDeleteUsageEntryParams.pstOffset = i2;
            oemCryptoServiceDeleteUsageEntryParams.pstLength = i3;
            oemCryptoServiceDeleteUsageEntryParams.message = bArr;
            oemCryptoServiceDeleteUsageEntryParams.signature = bArr2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceDeleteUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(33, 1, 0L)), new OemCryptoServiceDeleteUsageEntryResponseParamsForwardToCallback(deleteUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void deriveKeysFromSessionKey(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, OemCryptoService.DeriveKeysFromSessionKeyResponse deriveKeysFromSessionKeyResponse) {
            OemCryptoServiceDeriveKeysFromSessionKeyParams oemCryptoServiceDeriveKeysFromSessionKeyParams = new OemCryptoServiceDeriveKeysFromSessionKeyParams();
            oemCryptoServiceDeriveKeysFromSessionKeyParams.session = i;
            oemCryptoServiceDeriveKeysFromSessionKeyParams.encSessionKey = bArr;
            oemCryptoServiceDeriveKeysFromSessionKeyParams.macKeyContext = bArr2;
            oemCryptoServiceDeriveKeysFromSessionKeyParams.encKeyContext = bArr3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceDeriveKeysFromSessionKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(27, 1, 0L)), new OemCryptoServiceDeriveKeysFromSessionKeyResponseParamsForwardToCallback(deriveKeysFromSessionKeyResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void forceDeleteUsageEntry(byte[] bArr, OemCryptoService.ForceDeleteUsageEntryResponse forceDeleteUsageEntryResponse) {
            OemCryptoServiceForceDeleteUsageEntryParams oemCryptoServiceForceDeleteUsageEntryParams = new OemCryptoServiceForceDeleteUsageEntryParams();
            oemCryptoServiceForceDeleteUsageEntryParams.pst = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceForceDeleteUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(34, 1, 0L)), new OemCryptoServiceForceDeleteUsageEntryResponseParamsForwardToCallback(forceDeleteUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void generateDerivedKeys(int i, byte[] bArr, byte[] bArr2, OemCryptoService.GenerateDerivedKeysResponse generateDerivedKeysResponse) {
            OemCryptoServiceGenerateDerivedKeysParams oemCryptoServiceGenerateDerivedKeysParams = new OemCryptoServiceGenerateDerivedKeysParams();
            oemCryptoServiceGenerateDerivedKeysParams.session = i;
            oemCryptoServiceGenerateDerivedKeysParams.macKeyContext = bArr;
            oemCryptoServiceGenerateDerivedKeysParams.encKeyContext = bArr2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenerateDerivedKeysParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new OemCryptoServiceGenerateDerivedKeysResponseParamsForwardToCallback(generateDerivedKeysResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void generateNonce(int i, OemCryptoService.GenerateNonceResponse generateNonceResponse) {
            OemCryptoServiceGenerateNonceParams oemCryptoServiceGenerateNonceParams = new OemCryptoServiceGenerateNonceParams();
            oemCryptoServiceGenerateNonceParams.session = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenerateNonceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new OemCryptoServiceGenerateNonceResponseParamsForwardToCallback(generateNonceResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void generateRsaSignature(int i, byte[] bArr, int i2, OemCryptoService.GenerateRsaSignatureResponse generateRsaSignatureResponse) {
            OemCryptoServiceGenerateRsaSignatureParams oemCryptoServiceGenerateRsaSignatureParams = new OemCryptoServiceGenerateRsaSignatureParams();
            oemCryptoServiceGenerateRsaSignatureParams.session = i;
            oemCryptoServiceGenerateRsaSignatureParams.message = bArr;
            oemCryptoServiceGenerateRsaSignatureParams.paddingScheme = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenerateRsaSignatureParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(26, 1, 0L)), new OemCryptoServiceGenerateRsaSignatureResponseParamsForwardToCallback(generateRsaSignatureResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void generateSignature(int i, byte[] bArr, OemCryptoService.GenerateSignatureResponse generateSignatureResponse) {
            OemCryptoServiceGenerateSignatureParams oemCryptoServiceGenerateSignatureParams = new OemCryptoServiceGenerateSignatureParams();
            oemCryptoServiceGenerateSignatureParams.session = i;
            oemCryptoServiceGenerateSignatureParams.message = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenerateSignatureParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new OemCryptoServiceGenerateSignatureResponseParamsForwardToCallback(generateSignatureResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void genericDecrypt(int i, byte[] bArr, byte[] bArr2, int i2, OemCryptoService.GenericDecryptResponse genericDecryptResponse) {
            OemCryptoServiceGenericDecryptParams oemCryptoServiceGenericDecryptParams = new OemCryptoServiceGenericDecryptParams();
            oemCryptoServiceGenericDecryptParams.session = i;
            oemCryptoServiceGenericDecryptParams.data = bArr;
            oemCryptoServiceGenericDecryptParams.iv = bArr2;
            oemCryptoServiceGenericDecryptParams.algorithm = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenericDecryptParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13, 1, 0L)), new OemCryptoServiceGenericDecryptResponseParamsForwardToCallback(genericDecryptResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void genericEncrypt(int i, byte[] bArr, byte[] bArr2, int i2, OemCryptoService.GenericEncryptResponse genericEncryptResponse) {
            OemCryptoServiceGenericEncryptParams oemCryptoServiceGenericEncryptParams = new OemCryptoServiceGenericEncryptParams();
            oemCryptoServiceGenericEncryptParams.session = i;
            oemCryptoServiceGenericEncryptParams.data = bArr;
            oemCryptoServiceGenericEncryptParams.iv = bArr2;
            oemCryptoServiceGenericEncryptParams.algorithm = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenericEncryptParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new OemCryptoServiceGenericEncryptResponseParamsForwardToCallback(genericEncryptResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void genericSign(int i, byte[] bArr, int i2, OemCryptoService.GenericSignResponse genericSignResponse) {
            OemCryptoServiceGenericSignParams oemCryptoServiceGenericSignParams = new OemCryptoServiceGenericSignParams();
            oemCryptoServiceGenericSignParams.session = i;
            oemCryptoServiceGenericSignParams.data = bArr;
            oemCryptoServiceGenericSignParams.algorithm = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenericSignParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14, 1, 0L)), new OemCryptoServiceGenericSignResponseParamsForwardToCallback(genericSignResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void genericVerify(int i, byte[] bArr, int i2, byte[] bArr2, OemCryptoService.GenericVerifyResponse genericVerifyResponse) {
            OemCryptoServiceGenericVerifyParams oemCryptoServiceGenericVerifyParams = new OemCryptoServiceGenericVerifyParams();
            oemCryptoServiceGenericVerifyParams.session = i;
            oemCryptoServiceGenericVerifyParams.data = bArr;
            oemCryptoServiceGenericVerifyParams.algorithm = i2;
            oemCryptoServiceGenericVerifyParams.signature = bArr2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGenericVerifyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15, 1, 0L)), new OemCryptoServiceGenericVerifyResponseParamsForwardToCallback(genericVerifyResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getAnalogOutputFlags(OemCryptoService.GetAnalogOutputFlagsResponse getAnalogOutputFlagsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetAnalogOutputFlagsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(53, 1, 0L)), new OemCryptoServiceGetAnalogOutputFlagsResponseParamsForwardToCallback(getAnalogOutputFlagsResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getCurrentSrmVersion(OemCryptoService.GetCurrentSrmVersionResponse getCurrentSrmVersionResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetCurrentSrmVersionParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(40, 1, 0L)), new OemCryptoServiceGetCurrentSrmVersionResponseParamsForwardToCallback(getCurrentSrmVersionResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getDeviceId(OemCryptoService.GetDeviceIdResponse getDeviceIdResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetDeviceIdParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19, 1, 0L)), new OemCryptoServiceGetDeviceIdResponseParamsForwardToCallback(getDeviceIdResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getHdcpCapability(OemCryptoService.GetHdcpCapabilityResponse getHdcpCapabilityResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetHdcpCapabilityParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(29, 1, 0L)), new OemCryptoServiceGetHdcpCapabilityResponseParamsForwardToCallback(getHdcpCapabilityResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getKeyData(OemCryptoService.GetKeyDataResponse getKeyDataResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetKeyDataParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20, 1, 0L)), new OemCryptoServiceGetKeyDataResponseParamsForwardToCallback(getKeyDataResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getMaxNumberOfSessions(OemCryptoService.GetMaxNumberOfSessionsResponse getMaxNumberOfSessionsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetMaxNumberOfSessionsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23, 1, 0L)), new OemCryptoServiceGetMaxNumberOfSessionsResponseParamsForwardToCallback(getMaxNumberOfSessionsResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getNumberOfOpenSessions(OemCryptoService.GetNumberOfOpenSessionsResponse getNumberOfOpenSessionsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetNumberOfOpenSessionsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22, 1, 0L)), new OemCryptoServiceGetNumberOfOpenSessionsResponseParamsForwardToCallback(getNumberOfOpenSessionsResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getOemPublicCertificate(OemCryptoService.GetOemPublicCertificateResponse getOemPublicCertificateResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetOemPublicCertificateParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(63, 1, 0L)), new OemCryptoServiceGetOemPublicCertificateResponseParamsForwardToCallback(getOemPublicCertificateResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getProvisioningMethod(OemCryptoService.GetProvisioningMethodResponse getProvisioningMethodResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceGetProvisioningMethodParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(37, 1, 0L)), new OemCryptoServiceGetProvisioningMethodResponseParamsForwardToCallback(getProvisioningMethodResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void getRandom(int i, OemCryptoService.GetRandomResponse getRandomResponse) {
            OemCryptoServiceGetRandomParams oemCryptoServiceGetRandomParams = new OemCryptoServiceGetRandomParams();
            oemCryptoServiceGetRandomParams.length = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceGetRandomParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21, 1, 0L)), new OemCryptoServiceGetRandomResponseParamsForwardToCallback(getRandomResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void initialize(int i, OemCryptoService.InitializeResponse initializeResponse) {
            OemCryptoServiceInitializeParams oemCryptoServiceInitializeParams = new OemCryptoServiceInitializeParams();
            oemCryptoServiceInitializeParams.oemcryptoVersion = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceInitializeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(36, 1, 0L)), new OemCryptoServiceInitializeResponseParamsForwardToCallback(initializeResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void initializeDeprecated(OemCryptoService.InitializeDeprecatedResponse initializeDeprecatedResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceInitializeDeprecatedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new OemCryptoServiceInitializeDeprecatedResponseParamsForwardToCallback(initializeDeprecatedResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void isAntiRollbackHwPresent(OemCryptoService.IsAntiRollbackHwPresentResponse isAntiRollbackHwPresentResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceIsAntiRollbackHwPresentParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(65, 1, 0L)), new OemCryptoServiceIsAntiRollbackHwPresentResponseParamsForwardToCallback(isAntiRollbackHwPresentResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void isRootKeyCertificateValid(OemCryptoService.IsRootKeyCertificateValidResponse isRootKeyCertificateValidResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceIsRootKeyCertificateValidParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18, 1, 0L)), new OemCryptoServiceIsRootKeyCertificateValidResponseParamsForwardToCallback(isRootKeyCertificateValidResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void isSrmUpdateSupported(OemCryptoService.IsSrmUpdateSupportedResponse isSrmUpdateSupportedResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceIsSrmUpdateSupportedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(39, 1, 0L)), new OemCryptoServiceIsSrmUpdateSupportedResponseParamsForwardToCallback(isSrmUpdateSupportedResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadDeviceRsaKey(int i, byte[] bArr, OemCryptoService.LoadDeviceRsaKeyResponse loadDeviceRsaKeyResponse) {
            OemCryptoServiceLoadDeviceRsaKeyParams oemCryptoServiceLoadDeviceRsaKeyParams = new OemCryptoServiceLoadDeviceRsaKeyParams();
            oemCryptoServiceLoadDeviceRsaKeyParams.session = i;
            oemCryptoServiceLoadDeviceRsaKeyParams.wrappedRsaKey = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadDeviceRsaKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(25, 1, 0L)), new OemCryptoServiceLoadDeviceRsaKeyResponseParamsForwardToCallback(loadDeviceRsaKeyResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadDrmPrivateKey(int i, int i2, byte[] bArr, OemCryptoService.LoadDrmPrivateKeyResponse loadDrmPrivateKeyResponse) {
            OemCryptoServiceLoadDrmPrivateKeyParams oemCryptoServiceLoadDrmPrivateKeyParams = new OemCryptoServiceLoadDrmPrivateKeyParams();
            oemCryptoServiceLoadDrmPrivateKeyParams.session = i;
            oemCryptoServiceLoadDrmPrivateKeyParams.keyType = i2;
            oemCryptoServiceLoadDrmPrivateKeyParams.wrappedPrivateKey = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadDrmPrivateKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(74, 1, 0L)), new OemCryptoServiceLoadDrmPrivateKeyResponseParamsForwardToCallback(loadDrmPrivateKeyResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadEntitledContentKeys(int i, byte[] bArr, OemCryptoEntitledContentKeyObject[] oemCryptoEntitledContentKeyObjectArr, OemCryptoService.LoadEntitledContentKeysResponse loadEntitledContentKeysResponse) {
            OemCryptoServiceLoadEntitledContentKeysParams oemCryptoServiceLoadEntitledContentKeysParams = new OemCryptoServiceLoadEntitledContentKeysParams();
            oemCryptoServiceLoadEntitledContentKeysParams.session = i;
            oemCryptoServiceLoadEntitledContentKeysParams.message = bArr;
            oemCryptoServiceLoadEntitledContentKeysParams.keyArray = oemCryptoEntitledContentKeyObjectArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadEntitledContentKeysParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(62, 1, 0L)), new OemCryptoServiceLoadEntitledContentKeysResponseParamsForwardToCallback(loadEntitledContentKeysResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadEntitledContentKeysV14(int i, OemCryptoEntitledContentKeyObjectV14[] oemCryptoEntitledContentKeyObjectV14Arr, OemCryptoService.LoadEntitledContentKeysV14Response loadEntitledContentKeysV14Response) {
            OemCryptoServiceLoadEntitledContentKeysV14Params oemCryptoServiceLoadEntitledContentKeysV14Params = new OemCryptoServiceLoadEntitledContentKeysV14Params();
            oemCryptoServiceLoadEntitledContentKeysV14Params.session = i;
            oemCryptoServiceLoadEntitledContentKeysV14Params.keyArray = oemCryptoEntitledContentKeyObjectV14Arr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadEntitledContentKeysV14Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(55, 1, 0L)), new OemCryptoServiceLoadEntitledContentKeysV14ResponseParamsForwardToCallback(loadEntitledContentKeysV14Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadKeys(int i, byte[] bArr, byte[] bArr2, OemCryptoSubstring oemCryptoSubstring, OemCryptoSubstring oemCryptoSubstring2, OemCryptoKeyObject[] oemCryptoKeyObjectArr, OemCryptoSubstring oemCryptoSubstring3, OemCryptoSubstring oemCryptoSubstring4, int i2, OemCryptoService.LoadKeysResponse loadKeysResponse) {
            OemCryptoServiceLoadKeysParams oemCryptoServiceLoadKeysParams = new OemCryptoServiceLoadKeysParams();
            oemCryptoServiceLoadKeysParams.session = i;
            oemCryptoServiceLoadKeysParams.message = bArr;
            oemCryptoServiceLoadKeysParams.signature = bArr2;
            oemCryptoServiceLoadKeysParams.encMacKeysIv = oemCryptoSubstring;
            oemCryptoServiceLoadKeysParams.encMacKeys = oemCryptoSubstring2;
            oemCryptoServiceLoadKeysParams.keyArray = oemCryptoKeyObjectArr;
            oemCryptoServiceLoadKeysParams.pst = oemCryptoSubstring3;
            oemCryptoServiceLoadKeysParams.srmRestrictionData = oemCryptoSubstring4;
            oemCryptoServiceLoadKeysParams.licenseType = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadKeysParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(58, 1, 0L)), new OemCryptoServiceLoadKeysResponseParamsForwardToCallback(loadKeysResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadKeysV11OrV12(int i, byte[] bArr, byte[] bArr2, boolean z, int i2, int i3, OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr, int i4, int i5, OemCryptoService.LoadKeysV11OrV12Response loadKeysV11OrV12Response) {
            OemCryptoServiceLoadKeysV11OrV12Params oemCryptoServiceLoadKeysV11OrV12Params = new OemCryptoServiceLoadKeysV11OrV12Params();
            oemCryptoServiceLoadKeysV11OrV12Params.session = i;
            oemCryptoServiceLoadKeysV11OrV12Params.message = bArr;
            oemCryptoServiceLoadKeysV11OrV12Params.signature = bArr2;
            oemCryptoServiceLoadKeysV11OrV12Params.hasEncMacKeys = z;
            oemCryptoServiceLoadKeysV11OrV12Params.encMacKeysIvOffset = i2;
            oemCryptoServiceLoadKeysV11OrV12Params.encMacKeysOffset = i3;
            oemCryptoServiceLoadKeysV11OrV12Params.keyArray = oemCryptoKeyObjectV14Arr;
            oemCryptoServiceLoadKeysV11OrV12Params.pstOffset = i4;
            oemCryptoServiceLoadKeysV11OrV12Params.pstLength = i5;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadKeysV11OrV12Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new OemCryptoServiceLoadKeysV11OrV12ResponseParamsForwardToCallback(loadKeysV11OrV12Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadKeysV14(int i, byte[] bArr, byte[] bArr2, boolean z, int i2, int i3, OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr, int i4, int i5, byte[] bArr3, int i6, OemCryptoService.LoadKeysV14Response loadKeysV14Response) {
            OemCryptoServiceLoadKeysV14Params oemCryptoServiceLoadKeysV14Params = new OemCryptoServiceLoadKeysV14Params();
            oemCryptoServiceLoadKeysV14Params.session = i;
            oemCryptoServiceLoadKeysV14Params.message = bArr;
            oemCryptoServiceLoadKeysV14Params.signature = bArr2;
            oemCryptoServiceLoadKeysV14Params.hasEncMacKeys = z;
            oemCryptoServiceLoadKeysV14Params.encMacKeysIvOffset = i2;
            oemCryptoServiceLoadKeysV14Params.encMacKeysOffset = i3;
            oemCryptoServiceLoadKeysV14Params.keyArray = oemCryptoKeyObjectV14Arr;
            oemCryptoServiceLoadKeysV14Params.pstOffset = i4;
            oemCryptoServiceLoadKeysV14Params.pstLength = i5;
            oemCryptoServiceLoadKeysV14Params.srmRequirement = bArr3;
            oemCryptoServiceLoadKeysV14Params.licenseType = i6;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadKeysV14Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(57, 1, 0L)), new OemCryptoServiceLoadKeysV14ResponseParamsForwardToCallback(loadKeysV14Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadLicense(int i, byte[] bArr, int i2, byte[] bArr2, OemCryptoService.LoadLicenseResponse loadLicenseResponse) {
            OemCryptoServiceLoadLicenseParams oemCryptoServiceLoadLicenseParams = new OemCryptoServiceLoadLicenseParams();
            oemCryptoServiceLoadLicenseParams.session = i;
            oemCryptoServiceLoadLicenseParams.message = bArr;
            oemCryptoServiceLoadLicenseParams.coreMessageLength = i2;
            oemCryptoServiceLoadLicenseParams.signature = bArr2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadLicenseParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(70, 1, 0L)), new OemCryptoServiceLoadLicenseResponseParamsForwardToCallback(loadLicenseResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadOemPrivateKey(int i, OemCryptoService.LoadOemPrivateKeyResponse loadOemPrivateKeyResponse) {
            OemCryptoServiceLoadOemPrivateKeyParams oemCryptoServiceLoadOemPrivateKeyParams = new OemCryptoServiceLoadOemPrivateKeyParams();
            oemCryptoServiceLoadOemPrivateKeyParams.session = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadOemPrivateKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(73, 1, 0L)), new OemCryptoServiceLoadOemPrivateKeyResponseParamsForwardToCallback(loadOemPrivateKeyResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadProvisioning(int i, byte[] bArr, int i2, byte[] bArr2, int i3, OemCryptoService.LoadProvisioningResponse loadProvisioningResponse) {
            OemCryptoServiceLoadProvisioningParams oemCryptoServiceLoadProvisioningParams = new OemCryptoServiceLoadProvisioningParams();
            oemCryptoServiceLoadProvisioningParams.session = i;
            oemCryptoServiceLoadProvisioningParams.message = bArr;
            oemCryptoServiceLoadProvisioningParams.coreMessageLength = i2;
            oemCryptoServiceLoadProvisioningParams.signature = bArr2;
            oemCryptoServiceLoadProvisioningParams.availWrappedPrivateKeySize = i3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadProvisioningParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(72, 1, 0L)), new OemCryptoServiceLoadProvisioningResponseParamsForwardToCallback(loadProvisioningResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadRenewal(int i, byte[] bArr, int i2, byte[] bArr2, OemCryptoService.LoadRenewalResponse loadRenewalResponse) {
            OemCryptoServiceLoadRenewalParams oemCryptoServiceLoadRenewalParams = new OemCryptoServiceLoadRenewalParams();
            oemCryptoServiceLoadRenewalParams.session = i;
            oemCryptoServiceLoadRenewalParams.message = bArr;
            oemCryptoServiceLoadRenewalParams.coreMessageLength = i2;
            oemCryptoServiceLoadRenewalParams.signature = bArr2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadRenewalParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(71, 1, 0L)), new OemCryptoServiceLoadRenewalResponseParamsForwardToCallback(loadRenewalResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadSrm(byte[] bArr, OemCryptoService.LoadSrmResponse loadSrmResponse) {
            OemCryptoServiceLoadSrmParams oemCryptoServiceLoadSrmParams = new OemCryptoServiceLoadSrmParams();
            oemCryptoServiceLoadSrmParams.buffer = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadSrmParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(41, 1, 0L)), new OemCryptoServiceLoadSrmResponseParamsForwardToCallback(loadSrmResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadTestKeybox(byte[] bArr, OemCryptoService.LoadTestKeyboxResponse loadTestKeyboxResponse) {
            OemCryptoServiceLoadTestKeyboxParams oemCryptoServiceLoadTestKeyboxParams = new OemCryptoServiceLoadTestKeyboxParams();
            oemCryptoServiceLoadTestKeyboxParams.buffer = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadTestKeyboxParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(54, 1, 0L)), new OemCryptoServiceLoadTestKeyboxResponseParamsForwardToCallback(loadTestKeyboxResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadTestKeyboxV13(OemCryptoService.LoadTestKeyboxV13Response loadTestKeyboxV13Response) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceLoadTestKeyboxV13Params().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17, 1, 0L)), new OemCryptoServiceLoadTestKeyboxV13ResponseParamsForwardToCallback(loadTestKeyboxV13Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadUsageEntry(int i, int i2, byte[] bArr, OemCryptoService.LoadUsageEntryResponse loadUsageEntryResponse) {
            OemCryptoServiceLoadUsageEntryParams oemCryptoServiceLoadUsageEntryParams = new OemCryptoServiceLoadUsageEntryParams();
            oemCryptoServiceLoadUsageEntryParams.session = i;
            oemCryptoServiceLoadUsageEntryParams.index = i2;
            oemCryptoServiceLoadUsageEntryParams.buffer = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(46, 1, 0L)), new OemCryptoServiceLoadUsageEntryResponseParamsForwardToCallback(loadUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void loadUsageTableHeader(byte[] bArr, OemCryptoService.LoadUsageTableHeaderResponse loadUsageTableHeaderResponse) {
            OemCryptoServiceLoadUsageTableHeaderParams oemCryptoServiceLoadUsageTableHeaderParams = new OemCryptoServiceLoadUsageTableHeaderParams();
            oemCryptoServiceLoadUsageTableHeaderParams.buffer = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceLoadUsageTableHeaderParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(44, 1, 0L)), new OemCryptoServiceLoadUsageTableHeaderResponseParamsForwardToCallback(loadUsageTableHeaderResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void maximumUsageTableHeaderSize(OemCryptoService.MaximumUsageTableHeaderSizeResponse maximumUsageTableHeaderSizeResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceMaximumUsageTableHeaderSizeParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(64, 1, 0L)), new OemCryptoServiceMaximumUsageTableHeaderSizeResponseParamsForwardToCallback(maximumUsageTableHeaderSizeResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void minorApiVersion(OemCryptoService.MinorApiVersionResponse minorApiVersionResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceMinorApiVersionParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(66, 1, 0L)), new OemCryptoServiceMinorApiVersionResponseParamsForwardToCallback(minorApiVersionResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void moveEntry(int i, int i2, OemCryptoService.MoveEntryResponse moveEntryResponse) {
            OemCryptoServiceMoveEntryParams oemCryptoServiceMoveEntryParams = new OemCryptoServiceMoveEntryParams();
            oemCryptoServiceMoveEntryParams.session = i;
            oemCryptoServiceMoveEntryParams.newIndex = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceMoveEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(50, 1, 0L)), new OemCryptoServiceMoveEntryResponseParamsForwardToCallback(moveEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void openSession(OemCryptoService.OpenSessionResponse openSessionResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceOpenSessionParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new OemCryptoServiceOpenSessionResponseParamsForwardToCallback(openSessionResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void prepAndSignLicenseRequest(int i, byte[] bArr, int i2, int i3, OemCryptoService.PrepAndSignLicenseRequestResponse prepAndSignLicenseRequestResponse) {
            OemCryptoServicePrepAndSignLicenseRequestParams oemCryptoServicePrepAndSignLicenseRequestParams = new OemCryptoServicePrepAndSignLicenseRequestParams();
            oemCryptoServicePrepAndSignLicenseRequestParams.session = i;
            oemCryptoServicePrepAndSignLicenseRequestParams.message = bArr;
            oemCryptoServicePrepAndSignLicenseRequestParams.coreMessageSize = i2;
            oemCryptoServicePrepAndSignLicenseRequestParams.availSignatureSize = i3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServicePrepAndSignLicenseRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(67, 1, 0L)), new OemCryptoServicePrepAndSignLicenseRequestResponseParamsForwardToCallback(prepAndSignLicenseRequestResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void prepAndSignProvisioningRequest(int i, byte[] bArr, int i2, int i3, OemCryptoService.PrepAndSignProvisioningRequestResponse prepAndSignProvisioningRequestResponse) {
            OemCryptoServicePrepAndSignProvisioningRequestParams oemCryptoServicePrepAndSignProvisioningRequestParams = new OemCryptoServicePrepAndSignProvisioningRequestParams();
            oemCryptoServicePrepAndSignProvisioningRequestParams.session = i;
            oemCryptoServicePrepAndSignProvisioningRequestParams.message = bArr;
            oemCryptoServicePrepAndSignProvisioningRequestParams.coreMessageSize = i2;
            oemCryptoServicePrepAndSignProvisioningRequestParams.availSignatureSize = i3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServicePrepAndSignProvisioningRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(69, 1, 0L)), new OemCryptoServicePrepAndSignProvisioningRequestResponseParamsForwardToCallback(prepAndSignProvisioningRequestResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void prepAndSignRenewalRequest(int i, byte[] bArr, int i2, int i3, OemCryptoService.PrepAndSignRenewalRequestResponse prepAndSignRenewalRequestResponse) {
            OemCryptoServicePrepAndSignRenewalRequestParams oemCryptoServicePrepAndSignRenewalRequestParams = new OemCryptoServicePrepAndSignRenewalRequestParams();
            oemCryptoServicePrepAndSignRenewalRequestParams.session = i;
            oemCryptoServicePrepAndSignRenewalRequestParams.message = bArr;
            oemCryptoServicePrepAndSignRenewalRequestParams.coreMessageSize = i2;
            oemCryptoServicePrepAndSignRenewalRequestParams.availSignatureSize = i3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServicePrepAndSignRenewalRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(68, 1, 0L)), new OemCryptoServicePrepAndSignRenewalRequestResponseParamsForwardToCallback(prepAndSignRenewalRequestResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void queryKeyControl(int i, byte[] bArr, OemCryptoService.QueryKeyControlResponse queryKeyControlResponse) {
            OemCryptoServiceQueryKeyControlParams oemCryptoServiceQueryKeyControlParams = new OemCryptoServiceQueryKeyControlParams();
            oemCryptoServiceQueryKeyControlParams.session = i;
            oemCryptoServiceQueryKeyControlParams.keyId = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceQueryKeyControlParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new OemCryptoServiceQueryKeyControlResponseParamsForwardToCallback(queryKeyControlResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void refreshKeys(int i, byte[] bArr, byte[] bArr2, OemCryptoKeyRefreshObject[] oemCryptoKeyRefreshObjectArr, OemCryptoService.RefreshKeysResponse refreshKeysResponse) {
            OemCryptoServiceRefreshKeysParams oemCryptoServiceRefreshKeysParams = new OemCryptoServiceRefreshKeysParams();
            oemCryptoServiceRefreshKeysParams.session = i;
            oemCryptoServiceRefreshKeysParams.message = bArr;
            oemCryptoServiceRefreshKeysParams.signature = bArr2;
            oemCryptoServiceRefreshKeysParams.keyArray = oemCryptoKeyRefreshObjectArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceRefreshKeysParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(61, 1, 0L)), new OemCryptoServiceRefreshKeysResponseParamsForwardToCallback(refreshKeysResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void refreshKeysV14(int i, byte[] bArr, byte[] bArr2, OemCryptoKeyRefreshObjectV14[] oemCryptoKeyRefreshObjectV14Arr, OemCryptoService.RefreshKeysV14Response refreshKeysV14Response) {
            OemCryptoServiceRefreshKeysV14Params oemCryptoServiceRefreshKeysV14Params = new OemCryptoServiceRefreshKeysV14Params();
            oemCryptoServiceRefreshKeysV14Params.session = i;
            oemCryptoServiceRefreshKeysV14Params.message = bArr;
            oemCryptoServiceRefreshKeysV14Params.signature = bArr2;
            oemCryptoServiceRefreshKeysV14Params.keyArray = oemCryptoKeyRefreshObjectV14Arr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceRefreshKeysV14Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new OemCryptoServiceRefreshKeysV14ResponseParamsForwardToCallback(refreshKeysV14Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void removeSrm(OemCryptoService.RemoveSrmResponse removeSrmResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceRemoveSrmParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(42, 1, 0L)), new OemCryptoServiceRemoveSrmResponseParamsForwardToCallback(removeSrmResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void reportUsage(int i, byte[] bArr, OemCryptoService.ReportUsageResponse reportUsageResponse) {
            OemCryptoServiceReportUsageParams oemCryptoServiceReportUsageParams = new OemCryptoServiceReportUsageParams();
            oemCryptoServiceReportUsageParams.session = i;
            oemCryptoServiceReportUsageParams.pst = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceReportUsageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(32, 1, 0L)), new OemCryptoServiceReportUsageResponseParamsForwardToCallback(reportUsageResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void resourceRatingTier(OemCryptoService.ResourceRatingTierResponse resourceRatingTierResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceResourceRatingTierParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(59, 1, 0L)), new OemCryptoServiceResourceRatingTierResponseParamsForwardToCallback(resourceRatingTierResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void rewrapDeviceRsaKey(int i, byte[] bArr, byte[] bArr2, int i2, int i3, int i4, int i5, OemCryptoService.RewrapDeviceRsaKeyResponse rewrapDeviceRsaKeyResponse) {
            OemCryptoServiceRewrapDeviceRsaKeyParams oemCryptoServiceRewrapDeviceRsaKeyParams = new OemCryptoServiceRewrapDeviceRsaKeyParams();
            oemCryptoServiceRewrapDeviceRsaKeyParams.session = i;
            oemCryptoServiceRewrapDeviceRsaKeyParams.message = bArr;
            oemCryptoServiceRewrapDeviceRsaKeyParams.signature = bArr2;
            oemCryptoServiceRewrapDeviceRsaKeyParams.nonceOffset = i2;
            oemCryptoServiceRewrapDeviceRsaKeyParams.encRsaKeyOffset = i3;
            oemCryptoServiceRewrapDeviceRsaKeyParams.encRsaKeyLength = i4;
            oemCryptoServiceRewrapDeviceRsaKeyParams.encRsaKeyIvOffset = i5;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceRewrapDeviceRsaKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(24, 1, 0L)), new OemCryptoServiceRewrapDeviceRsaKeyResponseParamsForwardToCallback(rewrapDeviceRsaKeyResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void securityPatchLevel(OemCryptoService.SecurityPatchLevelResponse securityPatchLevelResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceSecurityPatchLevelParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(28, 1, 0L)), new OemCryptoServiceSecurityPatchLevelResponseParamsForwardToCallback(securityPatchLevelResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void selectKey(int i, byte[] bArr, int i2, OemCryptoService.SelectKeyResponse selectKeyResponse) {
            OemCryptoServiceSelectKeyParams oemCryptoServiceSelectKeyParams = new OemCryptoServiceSelectKeyParams();
            oemCryptoServiceSelectKeyParams.session = i;
            oemCryptoServiceSelectKeyParams.contentKeyId = bArr;
            oemCryptoServiceSelectKeyParams.cipherMode = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceSelectKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(56, 1, 0L)), new OemCryptoServiceSelectKeyResponseParamsForwardToCallback(selectKeyResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void selectKeyV13(int i, byte[] bArr, OemCryptoService.SelectKeyV13Response selectKeyV13Response) {
            OemCryptoServiceSelectKeyV13Params oemCryptoServiceSelectKeyV13Params = new OemCryptoServiceSelectKeyV13Params();
            oemCryptoServiceSelectKeyV13Params.session = i;
            oemCryptoServiceSelectKeyV13Params.keyId = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceSelectKeyV13Params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new OemCryptoServiceSelectKeyV13ResponseParamsForwardToCallback(selectKeyV13Response));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void shrinkUsageTableHeader(int i, int i2, OemCryptoService.ShrinkUsageTableHeaderResponse shrinkUsageTableHeaderResponse) {
            OemCryptoServiceShrinkUsageTableHeaderParams oemCryptoServiceShrinkUsageTableHeaderParams = new OemCryptoServiceShrinkUsageTableHeaderParams();
            oemCryptoServiceShrinkUsageTableHeaderParams.newEntryCount = i;
            oemCryptoServiceShrinkUsageTableHeaderParams.availHeaderLength = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceShrinkUsageTableHeaderParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(49, 1, 0L)), new OemCryptoServiceShrinkUsageTableHeaderResponseParamsForwardToCallback(shrinkUsageTableHeaderResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void supportedCertificates(OemCryptoService.SupportedCertificatesResponse supportedCertificatesResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceSupportedCertificatesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(38, 1, 0L)), new OemCryptoServiceSupportedCertificatesResponseParamsForwardToCallback(supportedCertificatesResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void terminate(OemCryptoService.TerminateResponse terminateResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceTerminateParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new OemCryptoServiceTerminateResponseParamsForwardToCallback(terminateResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void updateUsageEntry(int i, int i2, int i3, OemCryptoService.UpdateUsageEntryResponse updateUsageEntryResponse) {
            OemCryptoServiceUpdateUsageEntryParams oemCryptoServiceUpdateUsageEntryParams = new OemCryptoServiceUpdateUsageEntryParams();
            oemCryptoServiceUpdateUsageEntryParams.session = i;
            oemCryptoServiceUpdateUsageEntryParams.availHeaderLength = i2;
            oemCryptoServiceUpdateUsageEntryParams.availEntryLength = i3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(oemCryptoServiceUpdateUsageEntryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(47, 1, 0L)), new OemCryptoServiceUpdateUsageEntryResponseParamsForwardToCallback(updateUsageEntryResponse));
        }

        @Override // org.chromium.arc.mojom.OemCryptoService
        public void updateUsageTable(OemCryptoService.UpdateUsageTableResponse updateUsageTableResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new OemCryptoServiceUpdateUsageTableParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(30, 1, 0L)), new OemCryptoServiceUpdateUsageTableResponseParamsForwardToCallback(updateUsageTableResponse));
        }
    }

    static final class Stub extends Interface.Stub<OemCryptoService> {
        Stub(Core core, OemCryptoService oemCryptoService) {
            super(core, oemCryptoService);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(OemCryptoService_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                        OemCryptoServiceInitializeDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().initializeDeprecated(new OemCryptoServiceInitializeDeprecatedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 1:
                        OemCryptoServiceTerminateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().terminate(new OemCryptoServiceTerminateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        OemCryptoServiceOpenSessionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().openSession(new OemCryptoServiceOpenSessionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        getImpl().closeSession(OemCryptoServiceCloseSessionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).session, new OemCryptoServiceCloseSessionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        OemCryptoServiceGenerateDerivedKeysParams oemCryptoServiceGenerateDerivedKeysParamsDeserialize = OemCryptoServiceGenerateDerivedKeysParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().generateDerivedKeys(oemCryptoServiceGenerateDerivedKeysParamsDeserialize.session, oemCryptoServiceGenerateDerivedKeysParamsDeserialize.macKeyContext, oemCryptoServiceGenerateDerivedKeysParamsDeserialize.encKeyContext, new OemCryptoServiceGenerateDerivedKeysResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        getImpl().generateNonce(OemCryptoServiceGenerateNonceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).session, new OemCryptoServiceGenerateNonceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        OemCryptoServiceGenerateSignatureParams oemCryptoServiceGenerateSignatureParamsDeserialize = OemCryptoServiceGenerateSignatureParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().generateSignature(oemCryptoServiceGenerateSignatureParamsDeserialize.session, oemCryptoServiceGenerateSignatureParamsDeserialize.message, new OemCryptoServiceGenerateSignatureResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        OemCryptoServiceLoadKeysV11OrV12Params oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize = OemCryptoServiceLoadKeysV11OrV12Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadKeysV11OrV12(oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.session, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.message, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.signature, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.hasEncMacKeys, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.encMacKeysIvOffset, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.encMacKeysOffset, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.keyArray, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.pstOffset, oemCryptoServiceLoadKeysV11OrV12ParamsDeserialize.pstLength, new OemCryptoServiceLoadKeysV11OrV12ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 8:
                        OemCryptoServiceRefreshKeysV14Params oemCryptoServiceRefreshKeysV14ParamsDeserialize = OemCryptoServiceRefreshKeysV14Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().refreshKeysV14(oemCryptoServiceRefreshKeysV14ParamsDeserialize.session, oemCryptoServiceRefreshKeysV14ParamsDeserialize.message, oemCryptoServiceRefreshKeysV14ParamsDeserialize.signature, oemCryptoServiceRefreshKeysV14ParamsDeserialize.keyArray, new OemCryptoServiceRefreshKeysV14ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 9:
                        OemCryptoServiceQueryKeyControlParams oemCryptoServiceQueryKeyControlParamsDeserialize = OemCryptoServiceQueryKeyControlParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().queryKeyControl(oemCryptoServiceQueryKeyControlParamsDeserialize.session, oemCryptoServiceQueryKeyControlParamsDeserialize.keyId, new OemCryptoServiceQueryKeyControlResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 10:
                        OemCryptoServiceSelectKeyV13Params oemCryptoServiceSelectKeyV13ParamsDeserialize = OemCryptoServiceSelectKeyV13Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().selectKeyV13(oemCryptoServiceSelectKeyV13ParamsDeserialize.session, oemCryptoServiceSelectKeyV13ParamsDeserialize.keyId, new OemCryptoServiceSelectKeyV13ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 11:
                        OemCryptoServiceDecryptCencV15Params oemCryptoServiceDecryptCencV15ParamsDeserialize = OemCryptoServiceDecryptCencV15Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().decryptCencV15(oemCryptoServiceDecryptCencV15ParamsDeserialize.session, oemCryptoServiceDecryptCencV15ParamsDeserialize.data, oemCryptoServiceDecryptCencV15ParamsDeserialize.isEncrypted, oemCryptoServiceDecryptCencV15ParamsDeserialize.iv, oemCryptoServiceDecryptCencV15ParamsDeserialize.blockOffset, oemCryptoServiceDecryptCencV15ParamsDeserialize.secureBuffer, oemCryptoServiceDecryptCencV15ParamsDeserialize.pattern, new OemCryptoServiceDecryptCencV15ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 12:
                        OemCryptoServiceGenericEncryptParams oemCryptoServiceGenericEncryptParamsDeserialize = OemCryptoServiceGenericEncryptParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().genericEncrypt(oemCryptoServiceGenericEncryptParamsDeserialize.session, oemCryptoServiceGenericEncryptParamsDeserialize.data, oemCryptoServiceGenericEncryptParamsDeserialize.iv, oemCryptoServiceGenericEncryptParamsDeserialize.algorithm, new OemCryptoServiceGenericEncryptResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 13:
                        OemCryptoServiceGenericDecryptParams oemCryptoServiceGenericDecryptParamsDeserialize = OemCryptoServiceGenericDecryptParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().genericDecrypt(oemCryptoServiceGenericDecryptParamsDeserialize.session, oemCryptoServiceGenericDecryptParamsDeserialize.data, oemCryptoServiceGenericDecryptParamsDeserialize.iv, oemCryptoServiceGenericDecryptParamsDeserialize.algorithm, new OemCryptoServiceGenericDecryptResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 14:
                        OemCryptoServiceGenericSignParams oemCryptoServiceGenericSignParamsDeserialize = OemCryptoServiceGenericSignParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().genericSign(oemCryptoServiceGenericSignParamsDeserialize.session, oemCryptoServiceGenericSignParamsDeserialize.data, oemCryptoServiceGenericSignParamsDeserialize.algorithm, new OemCryptoServiceGenericSignResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 15:
                        OemCryptoServiceGenericVerifyParams oemCryptoServiceGenericVerifyParamsDeserialize = OemCryptoServiceGenericVerifyParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().genericVerify(oemCryptoServiceGenericVerifyParamsDeserialize.session, oemCryptoServiceGenericVerifyParamsDeserialize.data, oemCryptoServiceGenericVerifyParamsDeserialize.algorithm, oemCryptoServiceGenericVerifyParamsDeserialize.signature, new OemCryptoServiceGenericVerifyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 16:
                        OemCryptoServiceCopyBufferV14Params oemCryptoServiceCopyBufferV14ParamsDeserialize = OemCryptoServiceCopyBufferV14Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().copyBufferV14(oemCryptoServiceCopyBufferV14ParamsDeserialize.data, oemCryptoServiceCopyBufferV14ParamsDeserialize.outBuffer, new OemCryptoServiceCopyBufferV14ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 17:
                        OemCryptoServiceLoadTestKeyboxV13Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadTestKeyboxV13(new OemCryptoServiceLoadTestKeyboxV13ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 18:
                        OemCryptoServiceIsRootKeyCertificateValidParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().isRootKeyCertificateValid(new OemCryptoServiceIsRootKeyCertificateValidResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 19:
                        OemCryptoServiceGetDeviceIdParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getDeviceId(new OemCryptoServiceGetDeviceIdResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 20:
                        OemCryptoServiceGetKeyDataParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getKeyData(new OemCryptoServiceGetKeyDataResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 21:
                        getImpl().getRandom(OemCryptoServiceGetRandomParams.deserialize(serviceMessageAsServiceMessage.getPayload()).length, new OemCryptoServiceGetRandomResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 22:
                        OemCryptoServiceGetNumberOfOpenSessionsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getNumberOfOpenSessions(new OemCryptoServiceGetNumberOfOpenSessionsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 23:
                        OemCryptoServiceGetMaxNumberOfSessionsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getMaxNumberOfSessions(new OemCryptoServiceGetMaxNumberOfSessionsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 24:
                        OemCryptoServiceRewrapDeviceRsaKeyParams oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize = OemCryptoServiceRewrapDeviceRsaKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().rewrapDeviceRsaKey(oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize.session, oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize.message, oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize.signature, oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize.nonceOffset, oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize.encRsaKeyOffset, oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize.encRsaKeyLength, oemCryptoServiceRewrapDeviceRsaKeyParamsDeserialize.encRsaKeyIvOffset, new OemCryptoServiceRewrapDeviceRsaKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 25:
                        OemCryptoServiceLoadDeviceRsaKeyParams oemCryptoServiceLoadDeviceRsaKeyParamsDeserialize = OemCryptoServiceLoadDeviceRsaKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadDeviceRsaKey(oemCryptoServiceLoadDeviceRsaKeyParamsDeserialize.session, oemCryptoServiceLoadDeviceRsaKeyParamsDeserialize.wrappedRsaKey, new OemCryptoServiceLoadDeviceRsaKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 26:
                        OemCryptoServiceGenerateRsaSignatureParams oemCryptoServiceGenerateRsaSignatureParamsDeserialize = OemCryptoServiceGenerateRsaSignatureParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().generateRsaSignature(oemCryptoServiceGenerateRsaSignatureParamsDeserialize.session, oemCryptoServiceGenerateRsaSignatureParamsDeserialize.message, oemCryptoServiceGenerateRsaSignatureParamsDeserialize.paddingScheme, new OemCryptoServiceGenerateRsaSignatureResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 27:
                        OemCryptoServiceDeriveKeysFromSessionKeyParams oemCryptoServiceDeriveKeysFromSessionKeyParamsDeserialize = OemCryptoServiceDeriveKeysFromSessionKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deriveKeysFromSessionKey(oemCryptoServiceDeriveKeysFromSessionKeyParamsDeserialize.session, oemCryptoServiceDeriveKeysFromSessionKeyParamsDeserialize.encSessionKey, oemCryptoServiceDeriveKeysFromSessionKeyParamsDeserialize.macKeyContext, oemCryptoServiceDeriveKeysFromSessionKeyParamsDeserialize.encKeyContext, new OemCryptoServiceDeriveKeysFromSessionKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 28:
                        OemCryptoServiceSecurityPatchLevelParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().securityPatchLevel(new OemCryptoServiceSecurityPatchLevelResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 29:
                        OemCryptoServiceGetHdcpCapabilityParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getHdcpCapability(new OemCryptoServiceGetHdcpCapabilityResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 30:
                        OemCryptoServiceUpdateUsageTableParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().updateUsageTable(new OemCryptoServiceUpdateUsageTableResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 31:
                        getImpl().deactivateUsageEntryV12(OemCryptoServiceDeactivateUsageEntryV12Params.deserialize(serviceMessageAsServiceMessage.getPayload()).pst, new OemCryptoServiceDeactivateUsageEntryV12ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 32:
                        OemCryptoServiceReportUsageParams oemCryptoServiceReportUsageParamsDeserialize = OemCryptoServiceReportUsageParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportUsage(oemCryptoServiceReportUsageParamsDeserialize.session, oemCryptoServiceReportUsageParamsDeserialize.pst, new OemCryptoServiceReportUsageResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 33:
                        OemCryptoServiceDeleteUsageEntryParams oemCryptoServiceDeleteUsageEntryParamsDeserialize = OemCryptoServiceDeleteUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deleteUsageEntry(oemCryptoServiceDeleteUsageEntryParamsDeserialize.session, oemCryptoServiceDeleteUsageEntryParamsDeserialize.pstOffset, oemCryptoServiceDeleteUsageEntryParamsDeserialize.pstLength, oemCryptoServiceDeleteUsageEntryParamsDeserialize.message, oemCryptoServiceDeleteUsageEntryParamsDeserialize.signature, new OemCryptoServiceDeleteUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 34:
                        getImpl().forceDeleteUsageEntry(OemCryptoServiceForceDeleteUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload()).pst, new OemCryptoServiceForceDeleteUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 35:
                        OemCryptoServiceDeleteOldUsageTableParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deleteOldUsageTable(new OemCryptoServiceDeleteOldUsageTableResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 36:
                        getImpl().initialize(OemCryptoServiceInitializeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).oemcryptoVersion, new OemCryptoServiceInitializeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 37:
                        OemCryptoServiceGetProvisioningMethodParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getProvisioningMethod(new OemCryptoServiceGetProvisioningMethodResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 38:
                        OemCryptoServiceSupportedCertificatesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().supportedCertificates(new OemCryptoServiceSupportedCertificatesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 39:
                        OemCryptoServiceIsSrmUpdateSupportedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().isSrmUpdateSupported(new OemCryptoServiceIsSrmUpdateSupportedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 40:
                        OemCryptoServiceGetCurrentSrmVersionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getCurrentSrmVersion(new OemCryptoServiceGetCurrentSrmVersionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 41:
                        getImpl().loadSrm(OemCryptoServiceLoadSrmParams.deserialize(serviceMessageAsServiceMessage.getPayload()).buffer, new OemCryptoServiceLoadSrmResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 42:
                        OemCryptoServiceRemoveSrmParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().removeSrm(new OemCryptoServiceRemoveSrmResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_ANALOG_OUTPUT /* 43 */:
                        getImpl().createUsageTableHeader(OemCryptoServiceCreateUsageTableHeaderParams.deserialize(serviceMessageAsServiceMessage.getPayload()).availHeaderLength, new OemCryptoServiceCreateUsageTableHeaderResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_WRONG_PST /* 44 */:
                        getImpl().loadUsageTableHeader(OemCryptoServiceLoadUsageTableHeaderParams.deserialize(serviceMessageAsServiceMessage.getPayload()).buffer, new OemCryptoServiceLoadUsageTableHeaderResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_WRONG_KEYS /* 45 */:
                        getImpl().createNewUsageEntry(OemCryptoServiceCreateNewUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload()).session, new OemCryptoServiceCreateNewUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_MISSING_MASTER /* 46 */:
                        OemCryptoServiceLoadUsageEntryParams oemCryptoServiceLoadUsageEntryParamsDeserialize = OemCryptoServiceLoadUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadUsageEntry(oemCryptoServiceLoadUsageEntryParamsDeserialize.session, oemCryptoServiceLoadUsageEntryParamsDeserialize.index, oemCryptoServiceLoadUsageEntryParamsDeserialize.buffer, new OemCryptoServiceLoadUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_LICENSE_INACTIVE /* 47 */:
                        OemCryptoServiceUpdateUsageEntryParams oemCryptoServiceUpdateUsageEntryParamsDeserialize = OemCryptoServiceUpdateUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().updateUsageEntry(oemCryptoServiceUpdateUsageEntryParamsDeserialize.session, oemCryptoServiceUpdateUsageEntryParamsDeserialize.availHeaderLength, oemCryptoServiceUpdateUsageEntryParamsDeserialize.availEntryLength, new OemCryptoServiceUpdateUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 48:
                        OemCryptoServiceDeactivateUsageEntryParams oemCryptoServiceDeactivateUsageEntryParamsDeserialize = OemCryptoServiceDeactivateUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deactivateUsageEntry(oemCryptoServiceDeactivateUsageEntryParamsDeserialize.session, oemCryptoServiceDeactivateUsageEntryParamsDeserialize.pst, new OemCryptoServiceDeactivateUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 49:
                        OemCryptoServiceShrinkUsageTableHeaderParams oemCryptoServiceShrinkUsageTableHeaderParamsDeserialize = OemCryptoServiceShrinkUsageTableHeaderParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().shrinkUsageTableHeader(oemCryptoServiceShrinkUsageTableHeaderParamsDeserialize.newEntryCount, oemCryptoServiceShrinkUsageTableHeaderParamsDeserialize.availHeaderLength, new OemCryptoServiceShrinkUsageTableHeaderResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_USAGE_TABLE_UNRECOVERABLE /* 50 */:
                        OemCryptoServiceMoveEntryParams oemCryptoServiceMoveEntryParamsDeserialize = OemCryptoServiceMoveEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().moveEntry(oemCryptoServiceMoveEntryParamsDeserialize.session, oemCryptoServiceMoveEntryParamsDeserialize.newIndex, new OemCryptoServiceMoveEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.KEY_NOT_LOADED /* 51 */:
                        OemCryptoServiceCopyOldUsageEntryParams oemCryptoServiceCopyOldUsageEntryParamsDeserialize = OemCryptoServiceCopyOldUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().copyOldUsageEntry(oemCryptoServiceCopyOldUsageEntryParamsDeserialize.session, oemCryptoServiceCopyOldUsageEntryParamsDeserialize.pst, new OemCryptoServiceCopyOldUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.KEY_NOT_ENTITLED /* 52 */:
                        OemCryptoServiceCreateOldUsageEntryParams oemCryptoServiceCreateOldUsageEntryParamsDeserialize = OemCryptoServiceCreateOldUsageEntryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().createOldUsageEntry(oemCryptoServiceCreateOldUsageEntryParamsDeserialize.timeSinceLicenseReceived, oemCryptoServiceCreateOldUsageEntryParamsDeserialize.timeSinceFirstDecrypt, oemCryptoServiceCreateOldUsageEntryParamsDeserialize.timeSinceLastDecrypt, oemCryptoServiceCreateOldUsageEntryParamsDeserialize.status, oemCryptoServiceCreateOldUsageEntryParamsDeserialize.serverMacKey, oemCryptoServiceCreateOldUsageEntryParamsDeserialize.clientMacKey, oemCryptoServiceCreateOldUsageEntryParamsDeserialize.pst, new OemCryptoServiceCreateOldUsageEntryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_BAD_HASH /* 53 */:
                        OemCryptoServiceGetAnalogOutputFlagsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getAnalogOutputFlags(new OemCryptoServiceGetAnalogOutputFlagsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_OUTPUT_TOO_LARGE /* 54 */:
                        getImpl().loadTestKeybox(OemCryptoServiceLoadTestKeyboxParams.deserialize(serviceMessageAsServiceMessage.getPayload()).buffer, new OemCryptoServiceLoadTestKeyboxResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_SESSION_LOST_STATE /* 55 */:
                        OemCryptoServiceLoadEntitledContentKeysV14Params oemCryptoServiceLoadEntitledContentKeysV14ParamsDeserialize = OemCryptoServiceLoadEntitledContentKeysV14Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadEntitledContentKeysV14(oemCryptoServiceLoadEntitledContentKeysV14ParamsDeserialize.session, oemCryptoServiceLoadEntitledContentKeysV14ParamsDeserialize.keyArray, new OemCryptoServiceLoadEntitledContentKeysV14ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_SYSTEM_INVALIDATED /* 56 */:
                        OemCryptoServiceSelectKeyParams oemCryptoServiceSelectKeyParamsDeserialize = OemCryptoServiceSelectKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().selectKey(oemCryptoServiceSelectKeyParamsDeserialize.session, oemCryptoServiceSelectKeyParamsDeserialize.contentKeyId, oemCryptoServiceSelectKeyParamsDeserialize.cipherMode, new OemCryptoServiceSelectKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_LICENSE_RELOAD /* 57 */:
                        OemCryptoServiceLoadKeysV14Params oemCryptoServiceLoadKeysV14ParamsDeserialize = OemCryptoServiceLoadKeysV14Params.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadKeysV14(oemCryptoServiceLoadKeysV14ParamsDeserialize.session, oemCryptoServiceLoadKeysV14ParamsDeserialize.message, oemCryptoServiceLoadKeysV14ParamsDeserialize.signature, oemCryptoServiceLoadKeysV14ParamsDeserialize.hasEncMacKeys, oemCryptoServiceLoadKeysV14ParamsDeserialize.encMacKeysIvOffset, oemCryptoServiceLoadKeysV14ParamsDeserialize.encMacKeysOffset, oemCryptoServiceLoadKeysV14ParamsDeserialize.keyArray, oemCryptoServiceLoadKeysV14ParamsDeserialize.pstOffset, oemCryptoServiceLoadKeysV14ParamsDeserialize.pstLength, oemCryptoServiceLoadKeysV14ParamsDeserialize.srmRequirement, oemCryptoServiceLoadKeysV14ParamsDeserialize.licenseType, new OemCryptoServiceLoadKeysV14ResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.ERROR_MULTIPLE_USAGE_ENTRIES /* 58 */:
                        OemCryptoServiceLoadKeysParams oemCryptoServiceLoadKeysParamsDeserialize = OemCryptoServiceLoadKeysParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadKeys(oemCryptoServiceLoadKeysParamsDeserialize.session, oemCryptoServiceLoadKeysParamsDeserialize.message, oemCryptoServiceLoadKeysParamsDeserialize.signature, oemCryptoServiceLoadKeysParamsDeserialize.encMacKeysIv, oemCryptoServiceLoadKeysParamsDeserialize.encMacKeys, oemCryptoServiceLoadKeysParamsDeserialize.keyArray, oemCryptoServiceLoadKeysParamsDeserialize.pst, oemCryptoServiceLoadKeysParamsDeserialize.srmRestrictionData, oemCryptoServiceLoadKeysParamsDeserialize.licenseType, new OemCryptoServiceLoadKeysResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case OemCryptoResult.WARNING_MIXED_OUTPUT_PROTECTION /* 59 */:
                        OemCryptoServiceResourceRatingTierParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().resourceRatingTier(new OemCryptoServiceResourceRatingTierResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 60:
                        OemCryptoServiceBuildInformationParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().buildInformation(new OemCryptoServiceBuildInformationResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 61:
                        OemCryptoServiceRefreshKeysParams oemCryptoServiceRefreshKeysParamsDeserialize = OemCryptoServiceRefreshKeysParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().refreshKeys(oemCryptoServiceRefreshKeysParamsDeserialize.session, oemCryptoServiceRefreshKeysParamsDeserialize.message, oemCryptoServiceRefreshKeysParamsDeserialize.signature, oemCryptoServiceRefreshKeysParamsDeserialize.keyArray, new OemCryptoServiceRefreshKeysResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 62:
                        OemCryptoServiceLoadEntitledContentKeysParams oemCryptoServiceLoadEntitledContentKeysParamsDeserialize = OemCryptoServiceLoadEntitledContentKeysParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadEntitledContentKeys(oemCryptoServiceLoadEntitledContentKeysParamsDeserialize.session, oemCryptoServiceLoadEntitledContentKeysParamsDeserialize.message, oemCryptoServiceLoadEntitledContentKeysParamsDeserialize.keyArray, new OemCryptoServiceLoadEntitledContentKeysResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 63:
                        OemCryptoServiceGetOemPublicCertificateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getOemPublicCertificate(new OemCryptoServiceGetOemPublicCertificateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case ImeConstants.TEXT_INPUT_FLAG_AUTOCAPITALIZE_NONE /* 64 */:
                        OemCryptoServiceMaximumUsageTableHeaderSizeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().maximumUsageTableHeaderSize(new OemCryptoServiceMaximumUsageTableHeaderSizeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 65:
                        OemCryptoServiceIsAntiRollbackHwPresentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().isAntiRollbackHwPresent(new OemCryptoServiceIsAntiRollbackHwPresentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 66:
                        OemCryptoServiceMinorApiVersionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().minorApiVersion(new OemCryptoServiceMinorApiVersionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 67:
                        OemCryptoServicePrepAndSignLicenseRequestParams oemCryptoServicePrepAndSignLicenseRequestParamsDeserialize = OemCryptoServicePrepAndSignLicenseRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().prepAndSignLicenseRequest(oemCryptoServicePrepAndSignLicenseRequestParamsDeserialize.session, oemCryptoServicePrepAndSignLicenseRequestParamsDeserialize.message, oemCryptoServicePrepAndSignLicenseRequestParamsDeserialize.coreMessageSize, oemCryptoServicePrepAndSignLicenseRequestParamsDeserialize.availSignatureSize, new OemCryptoServicePrepAndSignLicenseRequestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 68:
                        OemCryptoServicePrepAndSignRenewalRequestParams oemCryptoServicePrepAndSignRenewalRequestParamsDeserialize = OemCryptoServicePrepAndSignRenewalRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().prepAndSignRenewalRequest(oemCryptoServicePrepAndSignRenewalRequestParamsDeserialize.session, oemCryptoServicePrepAndSignRenewalRequestParamsDeserialize.message, oemCryptoServicePrepAndSignRenewalRequestParamsDeserialize.coreMessageSize, oemCryptoServicePrepAndSignRenewalRequestParamsDeserialize.availSignatureSize, new OemCryptoServicePrepAndSignRenewalRequestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 69:
                        OemCryptoServicePrepAndSignProvisioningRequestParams oemCryptoServicePrepAndSignProvisioningRequestParamsDeserialize = OemCryptoServicePrepAndSignProvisioningRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().prepAndSignProvisioningRequest(oemCryptoServicePrepAndSignProvisioningRequestParamsDeserialize.session, oemCryptoServicePrepAndSignProvisioningRequestParamsDeserialize.message, oemCryptoServicePrepAndSignProvisioningRequestParamsDeserialize.coreMessageSize, oemCryptoServicePrepAndSignProvisioningRequestParamsDeserialize.availSignatureSize, new OemCryptoServicePrepAndSignProvisioningRequestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 70:
                        OemCryptoServiceLoadLicenseParams oemCryptoServiceLoadLicenseParamsDeserialize = OemCryptoServiceLoadLicenseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadLicense(oemCryptoServiceLoadLicenseParamsDeserialize.session, oemCryptoServiceLoadLicenseParamsDeserialize.message, oemCryptoServiceLoadLicenseParamsDeserialize.coreMessageLength, oemCryptoServiceLoadLicenseParamsDeserialize.signature, new OemCryptoServiceLoadLicenseResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case ChromePage.OSLANGUAGESINPUT /* 71 */:
                        OemCryptoServiceLoadRenewalParams oemCryptoServiceLoadRenewalParamsDeserialize = OemCryptoServiceLoadRenewalParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadRenewal(oemCryptoServiceLoadRenewalParamsDeserialize.session, oemCryptoServiceLoadRenewalParamsDeserialize.message, oemCryptoServiceLoadRenewalParamsDeserialize.coreMessageLength, oemCryptoServiceLoadRenewalParamsDeserialize.signature, new OemCryptoServiceLoadRenewalResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case ChromePage.OSLANGUAGESLANGUAGES /* 72 */:
                        OemCryptoServiceLoadProvisioningParams oemCryptoServiceLoadProvisioningParamsDeserialize = OemCryptoServiceLoadProvisioningParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadProvisioning(oemCryptoServiceLoadProvisioningParamsDeserialize.session, oemCryptoServiceLoadProvisioningParamsDeserialize.message, oemCryptoServiceLoadProvisioningParamsDeserialize.coreMessageLength, oemCryptoServiceLoadProvisioningParamsDeserialize.signature, oemCryptoServiceLoadProvisioningParamsDeserialize.availWrappedPrivateKeySize, new OemCryptoServiceLoadProvisioningResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 73:
                        getImpl().loadOemPrivateKey(OemCryptoServiceLoadOemPrivateKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).session, new OemCryptoServiceLoadOemPrivateKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 74:
                        OemCryptoServiceLoadDrmPrivateKeyParams oemCryptoServiceLoadDrmPrivateKeyParamsDeserialize = OemCryptoServiceLoadDrmPrivateKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().loadDrmPrivateKey(oemCryptoServiceLoadDrmPrivateKeyParamsDeserialize.session, oemCryptoServiceLoadDrmPrivateKeyParamsDeserialize.keyType, oemCryptoServiceLoadDrmPrivateKeyParamsDeserialize.wrappedPrivateKey, new OemCryptoServiceLoadDrmPrivateKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 75:
                        OemCryptoServiceDecryptCencParams oemCryptoServiceDecryptCencParamsDeserialize = OemCryptoServiceDecryptCencParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().decryptCenc(oemCryptoServiceDecryptCencParamsDeserialize.session, oemCryptoServiceDecryptCencParamsDeserialize.data, oemCryptoServiceDecryptCencParamsDeserialize.iv, oemCryptoServiceDecryptCencParamsDeserialize.subSamples, oemCryptoServiceDecryptCencParamsDeserialize.pattern, oemCryptoServiceDecryptCencParamsDeserialize.secureBuffer, new OemCryptoServiceDecryptCencResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 76:
                        OemCryptoServiceCopyBufferParams oemCryptoServiceCopyBufferParamsDeserialize = OemCryptoServiceCopyBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().copyBuffer(oemCryptoServiceCopyBufferParamsDeserialize.session, oemCryptoServiceCopyBufferParamsDeserialize.data, oemCryptoServiceCopyBufferParamsDeserialize.outBuffer, oemCryptoServiceCopyBufferParamsDeserialize.subsampleFlags, new OemCryptoServiceCopyBufferResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
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

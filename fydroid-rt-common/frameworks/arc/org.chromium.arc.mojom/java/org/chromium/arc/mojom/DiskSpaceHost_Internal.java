package org.chromium.arc.mojom;

import org.chromium.arc.mojom.DiskSpaceHost;
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

class DiskSpaceHost_Internal {
    public static final Interface.Manager<DiskSpaceHost, DiskSpaceHost.Proxy> MANAGER = new Interface.Manager<DiskSpaceHost, DiskSpaceHost.Proxy>() { // from class: org.chromium.arc.mojom.DiskSpaceHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DiskSpaceHost[] buildArray(int i) {
            return new DiskSpaceHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public DiskSpaceHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<DiskSpaceHost> buildStub(Core core, DiskSpaceHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.DiskSpaceHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class DiskSpaceHostGetFreeDiskSpaceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetFreeDiskSpaceParams() {
            super(8, 0);
        }

        public static DiskSpaceHostGetFreeDiskSpaceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new DiskSpaceHostGetFreeDiskSpaceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private DiskSpaceHostGetFreeDiskSpaceParams(int i) {
            super(8, i);
        }
    }

    static final class DiskSpaceHostGetFreeDiskSpaceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public DiskSpace freeSpace;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetFreeDiskSpaceResponseParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetFreeDiskSpaceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetFreeDiskSpaceResponseParams diskSpaceHostGetFreeDiskSpaceResponseParams = new DiskSpaceHostGetFreeDiskSpaceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetFreeDiskSpaceResponseParams.freeSpace = DiskSpace.decode(decoder.readPointer(8, true));
                return diskSpaceHostGetFreeDiskSpaceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.freeSpace, 8, true);
        }

        private DiskSpaceHostGetFreeDiskSpaceResponseParams(int i) {
            super(16, i);
        }
    }

    static class DiskSpaceHostGetFreeDiskSpaceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceHost.GetFreeDiskSpaceResponse mCallback;

        DiskSpaceHostGetFreeDiskSpaceResponseParamsForwardToCallback(DiskSpaceHost.GetFreeDiskSpaceResponse getFreeDiskSpaceResponse) {
            this.mCallback = getFreeDiskSpaceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(DiskSpaceHostGetFreeDiskSpaceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).freeSpace);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DiskSpaceHostGetFreeDiskSpaceResponseParamsProxyToResponder implements DiskSpaceHost.GetFreeDiskSpaceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceHostGetFreeDiskSpaceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(DiskSpace diskSpace) {
            DiskSpaceHostGetFreeDiskSpaceResponseParams diskSpaceHostGetFreeDiskSpaceResponseParams = new DiskSpaceHostGetFreeDiskSpaceResponseParams();
            diskSpaceHostGetFreeDiskSpaceResponseParams.freeSpace = diskSpace;
            this.mMessageReceiver.accept(diskSpaceHostGetFreeDiskSpaceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpaceForGidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int gid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpaceForGidParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpaceForGidParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpaceForGidParams diskSpaceHostGetQuotaCurrentSpaceForGidParams = new DiskSpaceHostGetQuotaCurrentSpaceForGidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpaceForGidParams.gid = decoder.readInt(8);
                return diskSpaceHostGetQuotaCurrentSpaceForGidParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.gid, 8);
        }

        private DiskSpaceHostGetQuotaCurrentSpaceForGidParams(int i) {
            super(16, i);
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long curSpace;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams diskSpaceHostGetQuotaCurrentSpaceForGidResponseParams = new DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpaceForGidResponseParams.curSpace = decoder.readLong(8);
                return diskSpaceHostGetQuotaCurrentSpaceForGidResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.curSpace, 8);
        }

        private DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams(int i) {
            super(16, i);
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceHost.GetQuotaCurrentSpaceForGidResponse mCallback;

        DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParamsForwardToCallback(DiskSpaceHost.GetQuotaCurrentSpaceForGidResponse getQuotaCurrentSpaceForGidResponse) {
            this.mCallback = getQuotaCurrentSpaceForGidResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Long.valueOf(DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).curSpace));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParamsProxyToResponder implements DiskSpaceHost.GetQuotaCurrentSpaceForGidResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Long l) {
            DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams diskSpaceHostGetQuotaCurrentSpaceForGidResponseParams = new DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParams();
            diskSpaceHostGetQuotaCurrentSpaceForGidResponseParams.curSpace = l.longValue();
            this.mMessageReceiver.accept(diskSpaceHostGetQuotaCurrentSpaceForGidResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int projectId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams diskSpaceHostGetQuotaCurrentSpaceForProjectIdParams = new DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpaceForProjectIdParams.projectId = decoder.readInt(8);
                return diskSpaceHostGetQuotaCurrentSpaceForProjectIdParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.projectId, 8);
        }

        private DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams(int i) {
            super(16, i);
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long curSpace;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams diskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams = new DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams.curSpace = decoder.readLong(8);
                return diskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.curSpace, 8);
        }

        private DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams(int i) {
            super(16, i);
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceHost.GetQuotaCurrentSpaceForProjectIdResponse mCallback;

        DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParamsForwardToCallback(DiskSpaceHost.GetQuotaCurrentSpaceForProjectIdResponse getQuotaCurrentSpaceForProjectIdResponse) {
            this.mCallback = getQuotaCurrentSpaceForProjectIdResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Long.valueOf(DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).curSpace));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParamsProxyToResponder implements DiskSpaceHost.GetQuotaCurrentSpaceForProjectIdResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Long l) {
            DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams diskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams = new DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams();
            diskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams.curSpace = l.longValue();
            this.mMessageReceiver.accept(diskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpaceForUidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int uid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpaceForUidParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpaceForUidParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpaceForUidParams diskSpaceHostGetQuotaCurrentSpaceForUidParams = new DiskSpaceHostGetQuotaCurrentSpaceForUidParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpaceForUidParams.uid = decoder.readInt(8);
                return diskSpaceHostGetQuotaCurrentSpaceForUidParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.uid, 8);
        }

        private DiskSpaceHostGetQuotaCurrentSpaceForUidParams(int i) {
            super(16, i);
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long curSpace;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams diskSpaceHostGetQuotaCurrentSpaceForUidResponseParams = new DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpaceForUidResponseParams.curSpace = decoder.readLong(8);
                return diskSpaceHostGetQuotaCurrentSpaceForUidResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.curSpace, 8);
        }

        private DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams(int i) {
            super(16, i);
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceHost.GetQuotaCurrentSpaceForUidResponse mCallback;

        DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParamsForwardToCallback(DiskSpaceHost.GetQuotaCurrentSpaceForUidResponse getQuotaCurrentSpaceForUidResponse) {
            this.mCallback = getQuotaCurrentSpaceForUidResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Long.valueOf(DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).curSpace));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParamsProxyToResponder implements DiskSpaceHost.GetQuotaCurrentSpaceForUidResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Long l) {
            DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams diskSpaceHostGetQuotaCurrentSpaceForUidResponseParams = new DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParams();
            diskSpaceHostGetQuotaCurrentSpaceForUidResponseParams.curSpace = l.longValue();
            this.mMessageReceiver.accept(diskSpaceHostGetQuotaCurrentSpaceForUidResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpacesForIdsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int[] gids;
        public int[] projectIds;
        public int[] uids;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpacesForIdsParams() {
            super(32, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpacesForIdsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpacesForIdsParams diskSpaceHostGetQuotaCurrentSpacesForIdsParams = new DiskSpaceHostGetQuotaCurrentSpacesForIdsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpacesForIdsParams.uids = decoder.readInts(8, 0, -1);
                diskSpaceHostGetQuotaCurrentSpacesForIdsParams.gids = decoder.readInts(16, 0, -1);
                diskSpaceHostGetQuotaCurrentSpacesForIdsParams.projectIds = decoder.readInts(24, 0, -1);
                return diskSpaceHostGetQuotaCurrentSpacesForIdsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.uids, 8, 0, -1);
            encoderAtDataOffset.encode(this.gids, 16, 0, -1);
            encoderAtDataOffset.encode(this.projectIds, 24, 0, -1);
        }

        private DiskSpaceHostGetQuotaCurrentSpacesForIdsParams(int i) {
            super(32, i);
        }
    }

    static final class DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public QuotaSpaces quotaSpaces;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams() {
            super(16, 0);
        }

        public static DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams diskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams = new DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams.quotaSpaces = QuotaSpaces.decode(decoder.readPointer(8, true));
                return diskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.quotaSpaces, 8, true);
        }

        private DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams(int i) {
            super(16, i);
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceHost.GetQuotaCurrentSpacesForIdsResponse mCallback;

        DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParamsForwardToCallback(DiskSpaceHost.GetQuotaCurrentSpacesForIdsResponse getQuotaCurrentSpacesForIdsResponse) {
            this.mCallback = getQuotaCurrentSpacesForIdsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).quotaSpaces);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParamsProxyToResponder implements DiskSpaceHost.GetQuotaCurrentSpacesForIdsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(QuotaSpaces quotaSpaces) {
            DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams diskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams = new DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams();
            diskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams.quotaSpaces = quotaSpaces;
            this.mMessageReceiver.accept(diskSpaceHostGetQuotaCurrentSpacesForIdsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class DiskSpaceHostIsQuotaSupportedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostIsQuotaSupportedParams() {
            super(8, 0);
        }

        public static DiskSpaceHostIsQuotaSupportedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new DiskSpaceHostIsQuotaSupportedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private DiskSpaceHostIsQuotaSupportedParams(int i) {
            super(8, i);
        }
    }

    static final class DiskSpaceHostIsQuotaSupportedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean supported;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public DiskSpaceHostIsQuotaSupportedResponseParams() {
            super(16, 0);
        }

        public static DiskSpaceHostIsQuotaSupportedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                DiskSpaceHostIsQuotaSupportedResponseParams diskSpaceHostIsQuotaSupportedResponseParams = new DiskSpaceHostIsQuotaSupportedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                diskSpaceHostIsQuotaSupportedResponseParams.supported = decoder.readBoolean(8, 0);
                return diskSpaceHostIsQuotaSupportedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.supported, 8, 0);
        }

        private DiskSpaceHostIsQuotaSupportedResponseParams(int i) {
            super(16, i);
        }
    }

    static class DiskSpaceHostIsQuotaSupportedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final DiskSpaceHost.IsQuotaSupportedResponse mCallback;

        DiskSpaceHostIsQuotaSupportedResponseParamsForwardToCallback(DiskSpaceHost.IsQuotaSupportedResponse isQuotaSupportedResponse) {
            this.mCallback = isQuotaSupportedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(DiskSpaceHostIsQuotaSupportedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).supported));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class DiskSpaceHostIsQuotaSupportedResponseParamsProxyToResponder implements DiskSpaceHost.IsQuotaSupportedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        DiskSpaceHostIsQuotaSupportedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            DiskSpaceHostIsQuotaSupportedResponseParams diskSpaceHostIsQuotaSupportedResponseParams = new DiskSpaceHostIsQuotaSupportedResponseParams();
            diskSpaceHostIsQuotaSupportedResponseParams.supported = bool.booleanValue();
            this.mMessageReceiver.accept(diskSpaceHostIsQuotaSupportedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements DiskSpaceHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.DiskSpaceHost
        public void getFreeDiskSpace(DiskSpaceHost.GetFreeDiskSpaceResponse getFreeDiskSpaceResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new DiskSpaceHostGetFreeDiskSpaceParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new DiskSpaceHostGetFreeDiskSpaceResponseParamsForwardToCallback(getFreeDiskSpaceResponse));
        }

        @Override // org.chromium.arc.mojom.DiskSpaceHost
        public void getQuotaCurrentSpaceForGid(int i, DiskSpaceHost.GetQuotaCurrentSpaceForGidResponse getQuotaCurrentSpaceForGidResponse) {
            DiskSpaceHostGetQuotaCurrentSpaceForGidParams diskSpaceHostGetQuotaCurrentSpaceForGidParams = new DiskSpaceHostGetQuotaCurrentSpaceForGidParams();
            diskSpaceHostGetQuotaCurrentSpaceForGidParams.gid = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(diskSpaceHostGetQuotaCurrentSpaceForGidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParamsForwardToCallback(getQuotaCurrentSpaceForGidResponse));
        }

        @Override // org.chromium.arc.mojom.DiskSpaceHost
        public void getQuotaCurrentSpaceForProjectId(int i, DiskSpaceHost.GetQuotaCurrentSpaceForProjectIdResponse getQuotaCurrentSpaceForProjectIdResponse) {
            DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams diskSpaceHostGetQuotaCurrentSpaceForProjectIdParams = new DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams();
            diskSpaceHostGetQuotaCurrentSpaceForProjectIdParams.projectId = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(diskSpaceHostGetQuotaCurrentSpaceForProjectIdParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParamsForwardToCallback(getQuotaCurrentSpaceForProjectIdResponse));
        }

        @Override // org.chromium.arc.mojom.DiskSpaceHost
        public void getQuotaCurrentSpaceForUid(int i, DiskSpaceHost.GetQuotaCurrentSpaceForUidResponse getQuotaCurrentSpaceForUidResponse) {
            DiskSpaceHostGetQuotaCurrentSpaceForUidParams diskSpaceHostGetQuotaCurrentSpaceForUidParams = new DiskSpaceHostGetQuotaCurrentSpaceForUidParams();
            diskSpaceHostGetQuotaCurrentSpaceForUidParams.uid = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(diskSpaceHostGetQuotaCurrentSpaceForUidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParamsForwardToCallback(getQuotaCurrentSpaceForUidResponse));
        }

        @Override // org.chromium.arc.mojom.DiskSpaceHost
        public void getQuotaCurrentSpacesForIds(int[] iArr, int[] iArr2, int[] iArr3, DiskSpaceHost.GetQuotaCurrentSpacesForIdsResponse getQuotaCurrentSpacesForIdsResponse) {
            DiskSpaceHostGetQuotaCurrentSpacesForIdsParams diskSpaceHostGetQuotaCurrentSpacesForIdsParams = new DiskSpaceHostGetQuotaCurrentSpacesForIdsParams();
            diskSpaceHostGetQuotaCurrentSpacesForIdsParams.uids = iArr;
            diskSpaceHostGetQuotaCurrentSpacesForIdsParams.gids = iArr2;
            diskSpaceHostGetQuotaCurrentSpacesForIdsParams.projectIds = iArr3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(diskSpaceHostGetQuotaCurrentSpacesForIdsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParamsForwardToCallback(getQuotaCurrentSpacesForIdsResponse));
        }

        @Override // org.chromium.arc.mojom.DiskSpaceHost
        public void isQuotaSupported(DiskSpaceHost.IsQuotaSupportedResponse isQuotaSupportedResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new DiskSpaceHostIsQuotaSupportedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new DiskSpaceHostIsQuotaSupportedResponseParamsForwardToCallback(isQuotaSupportedResponse));
        }
    }

    static final class Stub extends Interface.Stub<DiskSpaceHost> {
        Stub(Core core, DiskSpaceHost diskSpaceHost) {
            super(core, diskSpaceHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(DiskSpaceHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), DiskSpaceHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    DiskSpaceHostIsQuotaSupportedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().isQuotaSupported(new DiskSpaceHostIsQuotaSupportedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    getImpl().getQuotaCurrentSpaceForUid(DiskSpaceHostGetQuotaCurrentSpaceForUidParams.deserialize(serviceMessageAsServiceMessage.getPayload()).uid, new DiskSpaceHostGetQuotaCurrentSpaceForUidResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 2) {
                    getImpl().getQuotaCurrentSpaceForGid(DiskSpaceHostGetQuotaCurrentSpaceForGidParams.deserialize(serviceMessageAsServiceMessage.getPayload()).gid, new DiskSpaceHostGetQuotaCurrentSpaceForGidResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 3) {
                    getImpl().getQuotaCurrentSpaceForProjectId(DiskSpaceHostGetQuotaCurrentSpaceForProjectIdParams.deserialize(serviceMessageAsServiceMessage.getPayload()).projectId, new DiskSpaceHostGetQuotaCurrentSpaceForProjectIdResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 5) {
                    DiskSpaceHostGetFreeDiskSpaceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getFreeDiskSpace(new DiskSpaceHostGetFreeDiskSpaceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 6) {
                    return false;
                }
                DiskSpaceHostGetQuotaCurrentSpacesForIdsParams diskSpaceHostGetQuotaCurrentSpacesForIdsParamsDeserialize = DiskSpaceHostGetQuotaCurrentSpacesForIdsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getQuotaCurrentSpacesForIds(diskSpaceHostGetQuotaCurrentSpacesForIdsParamsDeserialize.uids, diskSpaceHostGetQuotaCurrentSpacesForIdsParamsDeserialize.gids, diskSpaceHostGetQuotaCurrentSpacesForIdsParamsDeserialize.projectIds, new DiskSpaceHostGetQuotaCurrentSpacesForIdsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

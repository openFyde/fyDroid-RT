package org.chromium.arc.mojom;

import org.chromium.arc.mojom.FileSystemHost;
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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.mojo_base.mojom.Time;
import org.chromium.url.mojom.Url;

class FileSystemHost_Internal {
    public static final Interface.Manager<FileSystemHost, FileSystemHost.Proxy> MANAGER = new Interface.Manager<FileSystemHost, FileSystemHost.Proxy>() { // from class: org.chromium.arc.mojom.FileSystemHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public FileSystemHost[] buildArray(int i) {
            return new FileSystemHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public FileSystemHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<FileSystemHost> buildStub(Core core, FileSystemHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.FileSystemHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 23;
        }
    };

    static final class FileSystemHostGetFileNameParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileNameParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileNameParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileNameParams fileSystemHostGetFileNameParams = new FileSystemHostGetFileNameParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileNameParams.url = decoder.readString(8, false);
                return fileSystemHostGetFileNameParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private FileSystemHostGetFileNameParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostGetFileNameResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String name;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileNameResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileNameResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileNameResponseParams fileSystemHostGetFileNameResponseParams = new FileSystemHostGetFileNameResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileNameResponseParams.name = decoder.readString(8, true);
                return fileSystemHostGetFileNameResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.name, 8, true);
        }

        private FileSystemHostGetFileNameResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostGetFileNameResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.GetFileNameResponse mCallback;

        FileSystemHostGetFileNameResponseParamsForwardToCallback(FileSystemHost.GetFileNameResponse getFileNameResponse) {
            this.mCallback = getFileNameResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemHostGetFileNameResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).name);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostGetFileNameResponseParamsProxyToResponder implements FileSystemHost.GetFileNameResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostGetFileNameResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            FileSystemHostGetFileNameResponseParams fileSystemHostGetFileNameResponseParams = new FileSystemHostGetFileNameResponseParams();
            fileSystemHostGetFileNameResponseParams.name = str;
            this.mMessageReceiver.accept(fileSystemHostGetFileNameResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostGetFileSelectorElementsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GetFileSelectorElementsRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileSelectorElementsParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileSelectorElementsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileSelectorElementsParams fileSystemHostGetFileSelectorElementsParams = new FileSystemHostGetFileSelectorElementsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileSelectorElementsParams.request = GetFileSelectorElementsRequest.decode(decoder.readPointer(8, false));
                return fileSystemHostGetFileSelectorElementsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private FileSystemHostGetFileSelectorElementsParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostGetFileSelectorElementsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FileSelectorElements elements;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileSelectorElementsResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileSelectorElementsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileSelectorElementsResponseParams fileSystemHostGetFileSelectorElementsResponseParams = new FileSystemHostGetFileSelectorElementsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileSelectorElementsResponseParams.elements = FileSelectorElements.decode(decoder.readPointer(8, false));
                return fileSystemHostGetFileSelectorElementsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.elements, 8, false);
        }

        private FileSystemHostGetFileSelectorElementsResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostGetFileSelectorElementsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.GetFileSelectorElementsResponse mCallback;

        FileSystemHostGetFileSelectorElementsResponseParamsForwardToCallback(FileSystemHost.GetFileSelectorElementsResponse getFileSelectorElementsResponse) {
            this.mCallback = getFileSelectorElementsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemHostGetFileSelectorElementsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).elements);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostGetFileSelectorElementsResponseParamsProxyToResponder implements FileSystemHost.GetFileSelectorElementsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostGetFileSelectorElementsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(FileSelectorElements fileSelectorElements) {
            FileSystemHostGetFileSelectorElementsResponseParams fileSystemHostGetFileSelectorElementsResponseParams = new FileSystemHostGetFileSelectorElementsResponseParams();
            fileSystemHostGetFileSelectorElementsResponseParams.elements = fileSelectorElements;
            this.mMessageReceiver.accept(fileSystemHostGetFileSelectorElementsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostGetFileSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileSizeParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileSizeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileSizeParams fileSystemHostGetFileSizeParams = new FileSystemHostGetFileSizeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileSizeParams.url = decoder.readString(8, false);
                return fileSystemHostGetFileSizeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private FileSystemHostGetFileSizeParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostGetFileSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long size;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileSizeResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileSizeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileSizeResponseParams fileSystemHostGetFileSizeResponseParams = new FileSystemHostGetFileSizeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileSizeResponseParams.size = decoder.readLong(8);
                return fileSystemHostGetFileSizeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.size, 8);
        }

        private FileSystemHostGetFileSizeResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostGetFileSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.GetFileSizeResponse mCallback;

        FileSystemHostGetFileSizeResponseParamsForwardToCallback(FileSystemHost.GetFileSizeResponse getFileSizeResponse) {
            this.mCallback = getFileSizeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Long.valueOf(FileSystemHostGetFileSizeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).size));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostGetFileSizeResponseParamsProxyToResponder implements FileSystemHost.GetFileSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostGetFileSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Long l) {
            FileSystemHostGetFileSizeResponseParams fileSystemHostGetFileSizeResponseParams = new FileSystemHostGetFileSizeResponseParams();
            fileSystemHostGetFileSizeResponseParams.size = l.longValue();
            this.mMessageReceiver.accept(fileSystemHostGetFileSizeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostGetFileTypeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileTypeParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileTypeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileTypeParams fileSystemHostGetFileTypeParams = new FileSystemHostGetFileTypeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileTypeParams.url = decoder.readString(8, false);
                return fileSystemHostGetFileTypeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private FileSystemHostGetFileTypeParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostGetFileTypeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String mimeType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetFileTypeResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostGetFileTypeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetFileTypeResponseParams fileSystemHostGetFileTypeResponseParams = new FileSystemHostGetFileTypeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetFileTypeResponseParams.mimeType = decoder.readString(8, true);
                return fileSystemHostGetFileTypeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.mimeType, 8, true);
        }

        private FileSystemHostGetFileTypeResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostGetFileTypeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.GetFileTypeResponse mCallback;

        FileSystemHostGetFileTypeResponseParamsForwardToCallback(FileSystemHost.GetFileTypeResponse getFileTypeResponse) {
            this.mCallback = getFileTypeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemHostGetFileTypeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).mimeType);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostGetFileTypeResponseParamsProxyToResponder implements FileSystemHost.GetFileTypeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostGetFileTypeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            FileSystemHostGetFileTypeResponseParams fileSystemHostGetFileTypeResponseParams = new FileSystemHostGetFileTypeResponseParams();
            fileSystemHostGetFileTypeResponseParams.mimeType = str;
            this.mMessageReceiver.accept(fileSystemHostGetFileTypeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostGetLastModifiedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Url url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetLastModifiedParams() {
            super(16, 0);
        }

        public static FileSystemHostGetLastModifiedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetLastModifiedParams fileSystemHostGetLastModifiedParams = new FileSystemHostGetLastModifiedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetLastModifiedParams.url = Url.decode(decoder.readPointer(8, false));
                return fileSystemHostGetLastModifiedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.url, 8, false);
        }

        private FileSystemHostGetLastModifiedParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostGetLastModifiedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Time lastModified;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetLastModifiedResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostGetLastModifiedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetLastModifiedResponseParams fileSystemHostGetLastModifiedResponseParams = new FileSystemHostGetLastModifiedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetLastModifiedResponseParams.lastModified = Time.decode(decoder.readPointer(8, true));
                return fileSystemHostGetLastModifiedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.lastModified, 8, true);
        }

        private FileSystemHostGetLastModifiedResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostGetLastModifiedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.GetLastModifiedResponse mCallback;

        FileSystemHostGetLastModifiedResponseParamsForwardToCallback(FileSystemHost.GetLastModifiedResponse getLastModifiedResponse) {
            this.mCallback = getLastModifiedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(11, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemHostGetLastModifiedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).lastModified);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostGetLastModifiedResponseParamsProxyToResponder implements FileSystemHost.GetLastModifiedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostGetLastModifiedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Time time) {
            FileSystemHostGetLastModifiedResponseParams fileSystemHostGetLastModifiedResponseParams = new FileSystemHostGetLastModifiedResponseParams();
            fileSystemHostGetLastModifiedResponseParams.lastModified = time;
            this.mMessageReceiver.accept(fileSystemHostGetLastModifiedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(11, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostGetVirtualFileIdParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetVirtualFileIdParams() {
            super(16, 0);
        }

        public static FileSystemHostGetVirtualFileIdParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetVirtualFileIdParams fileSystemHostGetVirtualFileIdParams = new FileSystemHostGetVirtualFileIdParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetVirtualFileIdParams.url = decoder.readString(8, false);
                return fileSystemHostGetVirtualFileIdParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private FileSystemHostGetVirtualFileIdParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostGetVirtualFileIdResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String id;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostGetVirtualFileIdResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostGetVirtualFileIdResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostGetVirtualFileIdResponseParams fileSystemHostGetVirtualFileIdResponseParams = new FileSystemHostGetVirtualFileIdResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostGetVirtualFileIdResponseParams.id = decoder.readString(8, true);
                return fileSystemHostGetVirtualFileIdResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.id, 8, true);
        }

        private FileSystemHostGetVirtualFileIdResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostGetVirtualFileIdResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.GetVirtualFileIdResponse mCallback;

        FileSystemHostGetVirtualFileIdResponseParamsForwardToCallback(FileSystemHost.GetVirtualFileIdResponse getVirtualFileIdResponse) {
            this.mCallback = getVirtualFileIdResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemHostGetVirtualFileIdResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).id);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostGetVirtualFileIdResponseParamsProxyToResponder implements FileSystemHost.GetVirtualFileIdResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostGetVirtualFileIdResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            FileSystemHostGetVirtualFileIdResponseParams fileSystemHostGetVirtualFileIdResponseParams = new FileSystemHostGetVirtualFileIdResponseParams();
            fileSystemHostGetVirtualFileIdResponseParams.id = str;
            this.mMessageReceiver.accept(fileSystemHostGetVirtualFileIdResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostHandleIdReleasedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String id;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostHandleIdReleasedParams() {
            super(16, 0);
        }

        public static FileSystemHostHandleIdReleasedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostHandleIdReleasedParams fileSystemHostHandleIdReleasedParams = new FileSystemHostHandleIdReleasedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostHandleIdReleasedParams.id = decoder.readString(8, false);
                return fileSystemHostHandleIdReleasedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.id, 8, false);
        }

        private FileSystemHostHandleIdReleasedParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostHandleIdReleasedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostHandleIdReleasedResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostHandleIdReleasedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostHandleIdReleasedResponseParams fileSystemHostHandleIdReleasedResponseParams = new FileSystemHostHandleIdReleasedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostHandleIdReleasedResponseParams.success = decoder.readBoolean(8, 0);
                return fileSystemHostHandleIdReleasedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private FileSystemHostHandleIdReleasedResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostHandleIdReleasedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.HandleIdReleasedResponse mCallback;

        FileSystemHostHandleIdReleasedResponseParamsForwardToCallback(FileSystemHost.HandleIdReleasedResponse handleIdReleasedResponse) {
            this.mCallback = handleIdReleasedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(10, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(FileSystemHostHandleIdReleasedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostHandleIdReleasedResponseParamsProxyToResponder implements FileSystemHost.HandleIdReleasedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostHandleIdReleasedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            FileSystemHostHandleIdReleasedResponseParams fileSystemHostHandleIdReleasedResponseParams = new FileSystemHostHandleIdReleasedResponseParams();
            fileSystemHostHandleIdReleasedResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(fileSystemHostHandleIdReleasedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostOnDocumentChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;
        public long watcherId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostOnDocumentChangedParams() {
            super(24, 0);
        }

        public static FileSystemHostOnDocumentChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostOnDocumentChangedParams fileSystemHostOnDocumentChangedParams = new FileSystemHostOnDocumentChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostOnDocumentChangedParams.watcherId = decoder.readLong(8);
                int i = decoder.readInt(16);
                fileSystemHostOnDocumentChangedParams.type = i;
                ChangeType.validate(i);
                return fileSystemHostOnDocumentChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.watcherId, 8);
            encoderAtDataOffset.encode(this.type, 16);
        }

        private FileSystemHostOnDocumentChangedParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemHostOnFileSelectorEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FileSelectorEvent event;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostOnFileSelectorEventParams() {
            super(16, 0);
        }

        public static FileSystemHostOnFileSelectorEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostOnFileSelectorEventParams fileSystemHostOnFileSelectorEventParams = new FileSystemHostOnFileSelectorEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostOnFileSelectorEventParams.event = FileSelectorEvent.decode(decoder.readPointer(8, false));
                return fileSystemHostOnFileSelectorEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.event, 8, false);
        }

        private FileSystemHostOnFileSelectorEventParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostOnFileSelectorEventResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostOnFileSelectorEventResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class FileSystemHostOnFileSelectorEventResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.OnFileSelectorEventResponse mCallback;

        FileSystemHostOnFileSelectorEventResponseParamsForwardToCallback(FileSystemHost.OnFileSelectorEventResponse onFileSelectorEventResponse) {
            this.mCallback = onFileSelectorEventResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostOnFileSelectorEventResponseParamsProxyToResponder implements FileSystemHost.OnFileSelectorEventResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostOnFileSelectorEventResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new FileSystemHostOnFileSelectorEventResponseParams().serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostOnMediaStoreUriAddedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaStoreMetadata metadata;
        public Url uri;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostOnMediaStoreUriAddedParams() {
            super(32, 0);
        }

        public static FileSystemHostOnMediaStoreUriAddedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostOnMediaStoreUriAddedParams fileSystemHostOnMediaStoreUriAddedParams = new FileSystemHostOnMediaStoreUriAddedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostOnMediaStoreUriAddedParams.uri = Url.decode(decoder.readPointer(8, false));
                fileSystemHostOnMediaStoreUriAddedParams.metadata = MediaStoreMetadata.decode(decoder, 16);
                return fileSystemHostOnMediaStoreUriAddedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.uri, 8, false);
            encoderAtDataOffset.encode((Union) this.metadata, 16, false);
        }

        private FileSystemHostOnMediaStoreUriAddedParams(int i) {
            super(32, i);
        }
    }

    static final class FileSystemHostOnRootsChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostOnRootsChangedParams() {
            super(8, 0);
        }

        public static FileSystemHostOnRootsChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new FileSystemHostOnRootsChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private FileSystemHostOnRootsChangedParams(int i) {
            super(8, i);
        }
    }

    static final class FileSystemHostOpenFileToReadParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostOpenFileToReadParams() {
            super(16, 0);
        }

        public static FileSystemHostOpenFileToReadParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostOpenFileToReadParams fileSystemHostOpenFileToReadParams = new FileSystemHostOpenFileToReadParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostOpenFileToReadParams.url = decoder.readString(8, false);
                return fileSystemHostOpenFileToReadParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private FileSystemHostOpenFileToReadParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostOpenFileToReadResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.OpenFileToReadResponse mCallback;

        FileSystemHostOpenFileToReadResponseParamsForwardToCallback(FileSystemHost.OpenFileToReadResponse openFileToReadResponse) {
            this.mCallback = openFileToReadResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemHostOpenFileToReadResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).fd);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostOpenFileToReadResponseParamsProxyToResponder implements FileSystemHost.OpenFileToReadResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostOpenFileToReadResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UntypedHandle untypedHandle) {
            FileSystemHostOpenFileToReadResponseParams fileSystemHostOpenFileToReadResponseParams = new FileSystemHostOpenFileToReadResponseParams();
            fileSystemHostOpenFileToReadResponseParams.fd = untypedHandle;
            this.mMessageReceiver.accept(fileSystemHostOpenFileToReadResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class FileSystemHostSelectFilesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SelectFilesRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostSelectFilesParams() {
            super(16, 0);
        }

        public static FileSystemHostSelectFilesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostSelectFilesParams fileSystemHostSelectFilesParams = new FileSystemHostSelectFilesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostSelectFilesParams.request = SelectFilesRequest.decode(decoder.readPointer(8, false));
                return fileSystemHostSelectFilesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private FileSystemHostSelectFilesParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemHostSelectFilesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SelectFilesResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostSelectFilesResponseParams() {
            super(16, 0);
        }

        public static FileSystemHostSelectFilesResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostSelectFilesResponseParams fileSystemHostSelectFilesResponseParams = new FileSystemHostSelectFilesResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostSelectFilesResponseParams.result = SelectFilesResult.decode(decoder.readPointer(8, false));
                return fileSystemHostSelectFilesResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private FileSystemHostSelectFilesResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemHostSelectFilesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemHost.SelectFilesResponse mCallback;

        FileSystemHostSelectFilesResponseParamsForwardToCallback(FileSystemHost.SelectFilesResponse selectFilesResponse) {
            this.mCallback = selectFilesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemHostSelectFilesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemHostSelectFilesResponseParamsProxyToResponder implements FileSystemHost.SelectFilesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemHostSelectFilesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(SelectFilesResult selectFilesResult) {
            FileSystemHostSelectFilesResponseParams fileSystemHostSelectFilesResponseParams = new FileSystemHostSelectFilesResponseParams();
            fileSystemHostSelectFilesResponseParams.result = selectFilesResult;
            this.mMessageReceiver.accept(fileSystemHostSelectFilesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements FileSystemHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void getFileName(String str, FileSystemHost.GetFileNameResponse getFileNameResponse) {
            FileSystemHostGetFileNameParams fileSystemHostGetFileNameParams = new FileSystemHostGetFileNameParams();
            fileSystemHostGetFileNameParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostGetFileNameParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new FileSystemHostGetFileNameResponseParamsForwardToCallback(getFileNameResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void getFileSelectorElements(GetFileSelectorElementsRequest getFileSelectorElementsRequest, FileSystemHost.GetFileSelectorElementsResponse getFileSelectorElementsResponse) {
            FileSystemHostGetFileSelectorElementsParams fileSystemHostGetFileSelectorElementsParams = new FileSystemHostGetFileSelectorElementsParams();
            fileSystemHostGetFileSelectorElementsParams.request = getFileSelectorElementsRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostGetFileSelectorElementsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new FileSystemHostGetFileSelectorElementsResponseParamsForwardToCallback(getFileSelectorElementsResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void getFileSize(String str, FileSystemHost.GetFileSizeResponse getFileSizeResponse) {
            FileSystemHostGetFileSizeParams fileSystemHostGetFileSizeParams = new FileSystemHostGetFileSizeParams();
            fileSystemHostGetFileSizeParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostGetFileSizeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new FileSystemHostGetFileSizeResponseParamsForwardToCallback(getFileSizeResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void getFileType(String str, FileSystemHost.GetFileTypeResponse getFileTypeResponse) {
            FileSystemHostGetFileTypeParams fileSystemHostGetFileTypeParams = new FileSystemHostGetFileTypeParams();
            fileSystemHostGetFileTypeParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostGetFileTypeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new FileSystemHostGetFileTypeResponseParamsForwardToCallback(getFileTypeResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void getLastModified(Url url, FileSystemHost.GetLastModifiedResponse getLastModifiedResponse) {
            FileSystemHostGetLastModifiedParams fileSystemHostGetLastModifiedParams = new FileSystemHostGetLastModifiedParams();
            fileSystemHostGetLastModifiedParams.url = url;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostGetLastModifiedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11, 1, 0L)), new FileSystemHostGetLastModifiedResponseParamsForwardToCallback(getLastModifiedResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void getVirtualFileId(String str, FileSystemHost.GetVirtualFileIdResponse getVirtualFileIdResponse) {
            FileSystemHostGetVirtualFileIdParams fileSystemHostGetVirtualFileIdParams = new FileSystemHostGetVirtualFileIdParams();
            fileSystemHostGetVirtualFileIdParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostGetVirtualFileIdParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new FileSystemHostGetVirtualFileIdResponseParamsForwardToCallback(getVirtualFileIdResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void handleIdReleased(String str, FileSystemHost.HandleIdReleasedResponse handleIdReleasedResponse) {
            FileSystemHostHandleIdReleasedParams fileSystemHostHandleIdReleasedParams = new FileSystemHostHandleIdReleasedParams();
            fileSystemHostHandleIdReleasedParams.id = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostHandleIdReleasedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new FileSystemHostHandleIdReleasedResponseParamsForwardToCallback(handleIdReleasedResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void onDocumentChanged(long j, int i) {
            FileSystemHostOnDocumentChangedParams fileSystemHostOnDocumentChangedParams = new FileSystemHostOnDocumentChangedParams();
            fileSystemHostOnDocumentChangedParams.watcherId = j;
            fileSystemHostOnDocumentChangedParams.type = i;
            getProxyHandler().getMessageReceiver().accept(fileSystemHostOnDocumentChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void onFileSelectorEvent(FileSelectorEvent fileSelectorEvent, FileSystemHost.OnFileSelectorEventResponse onFileSelectorEventResponse) {
            FileSystemHostOnFileSelectorEventParams fileSystemHostOnFileSelectorEventParams = new FileSystemHostOnFileSelectorEventParams();
            fileSystemHostOnFileSelectorEventParams.event = fileSelectorEvent;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostOnFileSelectorEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new FileSystemHostOnFileSelectorEventResponseParamsForwardToCallback(onFileSelectorEventResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void onMediaStoreUriAdded(Url url, MediaStoreMetadata mediaStoreMetadata) {
            FileSystemHostOnMediaStoreUriAddedParams fileSystemHostOnMediaStoreUriAddedParams = new FileSystemHostOnMediaStoreUriAddedParams();
            fileSystemHostOnMediaStoreUriAddedParams.uri = url;
            fileSystemHostOnMediaStoreUriAddedParams.metadata = mediaStoreMetadata;
            getProxyHandler().getMessageReceiver().accept(fileSystemHostOnMediaStoreUriAddedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12)));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void onRootsChanged() {
            getProxyHandler().getMessageReceiver().accept(new FileSystemHostOnRootsChangedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void openFileToRead(String str, FileSystemHost.OpenFileToReadResponse openFileToReadResponse) {
            FileSystemHostOpenFileToReadParams fileSystemHostOpenFileToReadParams = new FileSystemHostOpenFileToReadParams();
            fileSystemHostOpenFileToReadParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostOpenFileToReadParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new FileSystemHostOpenFileToReadResponseParamsForwardToCallback(openFileToReadResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemHost
        public void selectFiles(SelectFilesRequest selectFilesRequest, FileSystemHost.SelectFilesResponse selectFilesResponse) {
            FileSystemHostSelectFilesParams fileSystemHostSelectFilesParams = new FileSystemHostSelectFilesParams();
            fileSystemHostSelectFilesParams.request = selectFilesRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemHostSelectFilesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new FileSystemHostSelectFilesResponseParamsForwardToCallback(selectFilesResponse));
        }
    }

    static final class Stub extends Interface.Stub<FileSystemHost> {
        Stub(Core core, FileSystemHost fileSystemHost) {
            super(core, fileSystemHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(FileSystemHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    FileSystemHostOnDocumentChangedParams fileSystemHostOnDocumentChangedParamsDeserialize = FileSystemHostOnDocumentChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onDocumentChanged(fileSystemHostOnDocumentChangedParamsDeserialize.watcherId, fileSystemHostOnDocumentChangedParamsDeserialize.type);
                    return true;
                }
                if (type == 6) {
                    FileSystemHostOnRootsChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onRootsChanged();
                    return true;
                }
                if (type != 12) {
                    return false;
                }
                FileSystemHostOnMediaStoreUriAddedParams fileSystemHostOnMediaStoreUriAddedParamsDeserialize = FileSystemHostOnMediaStoreUriAddedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onMediaStoreUriAdded(fileSystemHostOnMediaStoreUriAddedParamsDeserialize.uri, fileSystemHostOnMediaStoreUriAddedParamsDeserialize.metadata);
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
                if (!header.validateHeader(1)) {
                    return false;
                }
                switch (header.getType()) {
                    case 1:
                        getImpl().getFileName(FileSystemHostGetFileNameParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemHostGetFileNameResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        getImpl().getFileSize(FileSystemHostGetFileSizeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemHostGetFileSizeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        getImpl().getFileType(FileSystemHostGetFileTypeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemHostGetFileTypeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        getImpl().openFileToRead(FileSystemHostOpenFileToReadParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemHostOpenFileToReadResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        getImpl().selectFiles(FileSystemHostSelectFilesParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new FileSystemHostSelectFilesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        getImpl().onFileSelectorEvent(FileSystemHostOnFileSelectorEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).event, new FileSystemHostOnFileSelectorEventResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 8:
                        getImpl().getFileSelectorElements(FileSystemHostGetFileSelectorElementsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new FileSystemHostGetFileSelectorElementsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 9:
                        getImpl().getVirtualFileId(FileSystemHostGetVirtualFileIdParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemHostGetVirtualFileIdResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 10:
                        getImpl().handleIdReleased(FileSystemHostHandleIdReleasedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).id, new FileSystemHostHandleIdReleasedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 11:
                        getImpl().getLastModified(FileSystemHostGetLastModifiedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemHostGetLastModifiedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                    return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class FileSystemHostOpenFileToReadResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle fd;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemHostOpenFileToReadResponseParams() {
            super(16, 0);
            this.fd = InvalidHandle.INSTANCE;
        }

        public static FileSystemHostOpenFileToReadResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemHostOpenFileToReadResponseParams fileSystemHostOpenFileToReadResponseParams = new FileSystemHostOpenFileToReadResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemHostOpenFileToReadResponseParams.fd = decoder.readUntypedHandle(8, true);
                return fileSystemHostOpenFileToReadResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.fd, 8, true);
        }

        private FileSystemHostOpenFileToReadResponseParams(int i) {
            super(16, i);
            this.fd = InvalidHandle.INSTANCE;
        }
    }
}

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.FileSystemInstance;
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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.url.mojom.Url;

class FileSystemInstance_Internal {
    public static final Interface.Manager<FileSystemInstance, FileSystemInstance.Proxy> MANAGER = new Interface.Manager<FileSystemInstance, FileSystemInstance.Proxy>() { // from class: org.chromium.arc.mojom.FileSystemInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public FileSystemInstance[] buildArray(int i) {
            return new FileSystemInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public FileSystemInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<FileSystemInstance> buildStub(Core core, FileSystemInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.FileSystemInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 21;
        }
    };

    static final class FileSystemInstanceAddWatcherParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String documentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceAddWatcherParams() {
            super(24, 0);
        }

        public static FileSystemInstanceAddWatcherParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceAddWatcherParams fileSystemInstanceAddWatcherParams = new FileSystemInstanceAddWatcherParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceAddWatcherParams.authority = decoder.readString(8, false);
                fileSystemInstanceAddWatcherParams.documentId = decoder.readString(16, false);
                return fileSystemInstanceAddWatcherParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.documentId, 16, false);
        }

        private FileSystemInstanceAddWatcherParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceAddWatcherResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long watcherId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceAddWatcherResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceAddWatcherResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceAddWatcherResponseParams fileSystemInstanceAddWatcherResponseParams = new FileSystemInstanceAddWatcherResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceAddWatcherResponseParams.watcherId = decoder.readLong(8);
                return fileSystemInstanceAddWatcherResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.watcherId, 8);
        }

        private FileSystemInstanceAddWatcherResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceAddWatcherResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.AddWatcherResponse mCallback;

        FileSystemInstanceAddWatcherResponseParamsForwardToCallback(FileSystemInstance.AddWatcherResponse addWatcherResponse) {
            this.mCallback = addWatcherResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(Long.valueOf(FileSystemInstanceAddWatcherResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).watcherId));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceAddWatcherResponseParamsProxyToResponder implements FileSystemInstance.AddWatcherResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceAddWatcherResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Long l) {
            FileSystemInstanceAddWatcherResponseParams fileSystemInstanceAddWatcherResponseParams = new FileSystemInstanceAddWatcherResponseParams();
            fileSystemInstanceAddWatcherResponseParams.watcherId = l.longValue();
            this.mMessageReceiver.accept(fileSystemInstanceAddWatcherResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceCloseFileSessionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String errorMessage;
        public String urlId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceCloseFileSessionParams() {
            super(24, 0);
        }

        public static FileSystemInstanceCloseFileSessionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceCloseFileSessionParams fileSystemInstanceCloseFileSessionParams = new FileSystemInstanceCloseFileSessionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceCloseFileSessionParams.urlId = decoder.readString(8, false);
                fileSystemInstanceCloseFileSessionParams.errorMessage = decoder.readString(16, false);
                return fileSystemInstanceCloseFileSessionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.urlId, 8, false);
            encoderAtDataOffset.encode(this.errorMessage, 16, false);
        }

        private FileSystemInstanceCloseFileSessionParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceCopyDocumentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String sourceDocumentId;
        public String targetParentDocumentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceCopyDocumentParams() {
            super(32, 0);
        }

        public static FileSystemInstanceCopyDocumentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceCopyDocumentParams fileSystemInstanceCopyDocumentParams = new FileSystemInstanceCopyDocumentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceCopyDocumentParams.authority = decoder.readString(8, false);
                fileSystemInstanceCopyDocumentParams.sourceDocumentId = decoder.readString(16, false);
                fileSystemInstanceCopyDocumentParams.targetParentDocumentId = decoder.readString(24, false);
                return fileSystemInstanceCopyDocumentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.sourceDocumentId, 16, false);
            encoderAtDataOffset.encode(this.targetParentDocumentId, 24, false);
        }

        private FileSystemInstanceCopyDocumentParams(int i) {
            super(32, i);
        }
    }

    static final class FileSystemInstanceCopyDocumentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Document document;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceCopyDocumentResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceCopyDocumentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceCopyDocumentResponseParams fileSystemInstanceCopyDocumentResponseParams = new FileSystemInstanceCopyDocumentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceCopyDocumentResponseParams.document = Document.decode(decoder.readPointer(8, true));
                return fileSystemInstanceCopyDocumentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.document, 8, true);
        }

        private FileSystemInstanceCopyDocumentResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceCopyDocumentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.CopyDocumentResponse mCallback;

        FileSystemInstanceCopyDocumentResponseParamsForwardToCallback(FileSystemInstance.CopyDocumentResponse copyDocumentResponse) {
            this.mCallback = copyDocumentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(16, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceCopyDocumentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).document);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceCopyDocumentResponseParamsProxyToResponder implements FileSystemInstance.CopyDocumentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceCopyDocumentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Document document) {
            FileSystemInstanceCopyDocumentResponseParams fileSystemInstanceCopyDocumentResponseParams = new FileSystemInstanceCopyDocumentResponseParams();
            fileSystemInstanceCopyDocumentResponseParams.document = document;
            this.mMessageReceiver.accept(fileSystemInstanceCopyDocumentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(16, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceCreateDocumentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String displayName;
        public String mimeType;
        public String parentDocumentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceCreateDocumentParams() {
            super(40, 0);
        }

        public static FileSystemInstanceCreateDocumentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceCreateDocumentParams fileSystemInstanceCreateDocumentParams = new FileSystemInstanceCreateDocumentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceCreateDocumentParams.authority = decoder.readString(8, false);
                fileSystemInstanceCreateDocumentParams.parentDocumentId = decoder.readString(16, false);
                fileSystemInstanceCreateDocumentParams.mimeType = decoder.readString(24, false);
                fileSystemInstanceCreateDocumentParams.displayName = decoder.readString(32, false);
                return fileSystemInstanceCreateDocumentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.parentDocumentId, 16, false);
            encoderAtDataOffset.encode(this.mimeType, 24, false);
            encoderAtDataOffset.encode(this.displayName, 32, false);
        }

        private FileSystemInstanceCreateDocumentParams(int i) {
            super(40, i);
        }
    }

    static final class FileSystemInstanceCreateDocumentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Document document;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceCreateDocumentResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceCreateDocumentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceCreateDocumentResponseParams fileSystemInstanceCreateDocumentResponseParams = new FileSystemInstanceCreateDocumentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceCreateDocumentResponseParams.document = Document.decode(decoder.readPointer(8, true));
                return fileSystemInstanceCreateDocumentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.document, 8, true);
        }

        private FileSystemInstanceCreateDocumentResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceCreateDocumentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.CreateDocumentResponse mCallback;

        FileSystemInstanceCreateDocumentResponseParamsForwardToCallback(FileSystemInstance.CreateDocumentResponse createDocumentResponse) {
            this.mCallback = createDocumentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(15, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceCreateDocumentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).document);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceCreateDocumentResponseParamsProxyToResponder implements FileSystemInstance.CreateDocumentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceCreateDocumentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Document document) {
            FileSystemInstanceCreateDocumentResponseParams fileSystemInstanceCreateDocumentResponseParams = new FileSystemInstanceCreateDocumentResponseParams();
            fileSystemInstanceCreateDocumentResponseParams.document = document;
            this.mMessageReceiver.accept(fileSystemInstanceCreateDocumentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(15, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceDeleteDocumentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String documentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceDeleteDocumentParams() {
            super(24, 0);
        }

        public static FileSystemInstanceDeleteDocumentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceDeleteDocumentParams fileSystemInstanceDeleteDocumentParams = new FileSystemInstanceDeleteDocumentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceDeleteDocumentParams.authority = decoder.readString(8, false);
                fileSystemInstanceDeleteDocumentParams.documentId = decoder.readString(16, false);
                return fileSystemInstanceDeleteDocumentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.documentId, 16, false);
        }

        private FileSystemInstanceDeleteDocumentParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceDeleteDocumentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceDeleteDocumentResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceDeleteDocumentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceDeleteDocumentResponseParams fileSystemInstanceDeleteDocumentResponseParams = new FileSystemInstanceDeleteDocumentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceDeleteDocumentResponseParams.success = decoder.readBoolean(8, 0);
                return fileSystemInstanceDeleteDocumentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private FileSystemInstanceDeleteDocumentResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceDeleteDocumentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.DeleteDocumentResponse mCallback;

        FileSystemInstanceDeleteDocumentResponseParamsForwardToCallback(FileSystemInstance.DeleteDocumentResponse deleteDocumentResponse) {
            this.mCallback = deleteDocumentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(13, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(FileSystemInstanceDeleteDocumentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceDeleteDocumentResponseParamsProxyToResponder implements FileSystemInstance.DeleteDocumentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceDeleteDocumentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            FileSystemInstanceDeleteDocumentResponseParams fileSystemInstanceDeleteDocumentResponseParams = new FileSystemInstanceDeleteDocumentResponseParams();
            fileSystemInstanceDeleteDocumentResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(fileSystemInstanceDeleteDocumentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(13, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceGetChildDocumentsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String parentDocumentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetChildDocumentsParams() {
            super(24, 0);
        }

        public static FileSystemInstanceGetChildDocumentsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetChildDocumentsParams fileSystemInstanceGetChildDocumentsParams = new FileSystemInstanceGetChildDocumentsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetChildDocumentsParams.authority = decoder.readString(8, false);
                fileSystemInstanceGetChildDocumentsParams.parentDocumentId = decoder.readString(16, false);
                return fileSystemInstanceGetChildDocumentsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.parentDocumentId, 16, false);
        }

        private FileSystemInstanceGetChildDocumentsParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceGetChildDocumentsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Document[] documents;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetChildDocumentsResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetChildDocumentsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                FileSystemInstanceGetChildDocumentsResponseParams fileSystemInstanceGetChildDocumentsResponseParams = new FileSystemInstanceGetChildDocumentsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, true);
                if (pointer == null) {
                    fileSystemInstanceGetChildDocumentsResponseParams.documents = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    fileSystemInstanceGetChildDocumentsResponseParams.documents = new Document[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                        fileSystemInstanceGetChildDocumentsResponseParams.documents[i] = Document.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                    }
                }
                return fileSystemInstanceGetChildDocumentsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            Document[] documentArr = this.documents;
            if (documentArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, true);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(documentArr.length, 8, -1);
            int i = 0;
            while (true) {
                Document[] documentArr2 = this.documents;
                if (i >= documentArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) documentArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private FileSystemInstanceGetChildDocumentsResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceGetChildDocumentsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.GetChildDocumentsResponse mCallback;

        FileSystemInstanceGetChildDocumentsResponseParamsForwardToCallback(FileSystemInstance.GetChildDocumentsResponse getChildDocumentsResponse) {
            this.mCallback = getChildDocumentsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceGetChildDocumentsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).documents);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceGetChildDocumentsResponseParamsProxyToResponder implements FileSystemInstance.GetChildDocumentsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceGetChildDocumentsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Document[] documentArr) {
            FileSystemInstanceGetChildDocumentsResponseParams fileSystemInstanceGetChildDocumentsResponseParams = new FileSystemInstanceGetChildDocumentsResponseParams();
            fileSystemInstanceGetChildDocumentsResponseParams.documents = documentArr;
            this.mMessageReceiver.accept(fileSystemInstanceGetChildDocumentsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceGetDocumentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String documentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetDocumentParams() {
            super(24, 0);
        }

        public static FileSystemInstanceGetDocumentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetDocumentParams fileSystemInstanceGetDocumentParams = new FileSystemInstanceGetDocumentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetDocumentParams.authority = decoder.readString(8, false);
                fileSystemInstanceGetDocumentParams.documentId = decoder.readString(16, false);
                return fileSystemInstanceGetDocumentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.documentId, 16, false);
        }

        private FileSystemInstanceGetDocumentParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceGetDocumentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Document document;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetDocumentResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetDocumentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetDocumentResponseParams fileSystemInstanceGetDocumentResponseParams = new FileSystemInstanceGetDocumentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetDocumentResponseParams.document = Document.decode(decoder.readPointer(8, true));
                return fileSystemInstanceGetDocumentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.document, 8, true);
        }

        private FileSystemInstanceGetDocumentResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceGetDocumentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.GetDocumentResponse mCallback;

        FileSystemInstanceGetDocumentResponseParamsForwardToCallback(FileSystemInstance.GetDocumentResponse getDocumentResponse) {
            this.mCallback = getDocumentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceGetDocumentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).document);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceGetDocumentResponseParamsProxyToResponder implements FileSystemInstance.GetDocumentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceGetDocumentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Document document) {
            FileSystemInstanceGetDocumentResponseParams fileSystemInstanceGetDocumentResponseParams = new FileSystemInstanceGetDocumentResponseParams();
            fileSystemInstanceGetDocumentResponseParams.document = document;
            this.mMessageReceiver.accept(fileSystemInstanceGetDocumentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceGetFileSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetFileSizeParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetFileSizeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetFileSizeParams fileSystemInstanceGetFileSizeParams = new FileSystemInstanceGetFileSizeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetFileSizeParams.url = decoder.readString(8, false);
                return fileSystemInstanceGetFileSizeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private FileSystemInstanceGetFileSizeParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceGetFileSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long size;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetFileSizeResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetFileSizeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetFileSizeResponseParams fileSystemInstanceGetFileSizeResponseParams = new FileSystemInstanceGetFileSizeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetFileSizeResponseParams.size = decoder.readLong(8);
                return fileSystemInstanceGetFileSizeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.size, 8);
        }

        private FileSystemInstanceGetFileSizeResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceGetFileSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.GetFileSizeResponse mCallback;

        FileSystemInstanceGetFileSizeResponseParamsForwardToCallback(FileSystemInstance.GetFileSizeResponse getFileSizeResponse) {
            this.mCallback = getFileSizeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Long.valueOf(FileSystemInstanceGetFileSizeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).size));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceGetFileSizeResponseParamsProxyToResponder implements FileSystemInstance.GetFileSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceGetFileSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Long l) {
            FileSystemInstanceGetFileSizeResponseParams fileSystemInstanceGetFileSizeResponseParams = new FileSystemInstanceGetFileSizeResponseParams();
            fileSystemInstanceGetFileSizeResponseParams.size = l.longValue();
            this.mMessageReceiver.accept(fileSystemInstanceGetFileSizeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceGetMimeTypeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetMimeTypeParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetMimeTypeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetMimeTypeParams fileSystemInstanceGetMimeTypeParams = new FileSystemInstanceGetMimeTypeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetMimeTypeParams.url = decoder.readString(8, false);
                return fileSystemInstanceGetMimeTypeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private FileSystemInstanceGetMimeTypeParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceGetMimeTypeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String mimeType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetMimeTypeResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetMimeTypeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetMimeTypeResponseParams fileSystemInstanceGetMimeTypeResponseParams = new FileSystemInstanceGetMimeTypeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetMimeTypeResponseParams.mimeType = decoder.readString(8, true);
                return fileSystemInstanceGetMimeTypeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.mimeType, 8, true);
        }

        private FileSystemInstanceGetMimeTypeResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceGetMimeTypeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.GetMimeTypeResponse mCallback;

        FileSystemInstanceGetMimeTypeResponseParamsForwardToCallback(FileSystemInstance.GetMimeTypeResponse getMimeTypeResponse) {
            this.mCallback = getMimeTypeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceGetMimeTypeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).mimeType);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceGetMimeTypeResponseParamsProxyToResponder implements FileSystemInstance.GetMimeTypeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceGetMimeTypeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            FileSystemInstanceGetMimeTypeResponseParams fileSystemInstanceGetMimeTypeResponseParams = new FileSystemInstanceGetMimeTypeResponseParams();
            fileSystemInstanceGetMimeTypeResponseParams.mimeType = str;
            this.mMessageReceiver.accept(fileSystemInstanceGetMimeTypeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceGetRecentDocumentsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String rootId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetRecentDocumentsParams() {
            super(24, 0);
        }

        public static FileSystemInstanceGetRecentDocumentsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetRecentDocumentsParams fileSystemInstanceGetRecentDocumentsParams = new FileSystemInstanceGetRecentDocumentsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetRecentDocumentsParams.authority = decoder.readString(8, false);
                fileSystemInstanceGetRecentDocumentsParams.rootId = decoder.readString(16, false);
                return fileSystemInstanceGetRecentDocumentsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.rootId, 16, false);
        }

        private FileSystemInstanceGetRecentDocumentsParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceGetRecentDocumentsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Document[] documents;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetRecentDocumentsResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetRecentDocumentsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                FileSystemInstanceGetRecentDocumentsResponseParams fileSystemInstanceGetRecentDocumentsResponseParams = new FileSystemInstanceGetRecentDocumentsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, true);
                if (pointer == null) {
                    fileSystemInstanceGetRecentDocumentsResponseParams.documents = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    fileSystemInstanceGetRecentDocumentsResponseParams.documents = new Document[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                        fileSystemInstanceGetRecentDocumentsResponseParams.documents[i] = Document.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                    }
                }
                return fileSystemInstanceGetRecentDocumentsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            Document[] documentArr = this.documents;
            if (documentArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, true);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(documentArr.length, 8, -1);
            int i = 0;
            while (true) {
                Document[] documentArr2 = this.documents;
                if (i >= documentArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) documentArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private FileSystemInstanceGetRecentDocumentsResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceGetRecentDocumentsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.GetRecentDocumentsResponse mCallback;

        FileSystemInstanceGetRecentDocumentsResponseParamsForwardToCallback(FileSystemInstance.GetRecentDocumentsResponse getRecentDocumentsResponse) {
            this.mCallback = getRecentDocumentsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceGetRecentDocumentsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).documents);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceGetRecentDocumentsResponseParamsProxyToResponder implements FileSystemInstance.GetRecentDocumentsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceGetRecentDocumentsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Document[] documentArr) {
            FileSystemInstanceGetRecentDocumentsResponseParams fileSystemInstanceGetRecentDocumentsResponseParams = new FileSystemInstanceGetRecentDocumentsResponseParams();
            fileSystemInstanceGetRecentDocumentsResponseParams.documents = documentArr;
            this.mMessageReceiver.accept(fileSystemInstanceGetRecentDocumentsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceGetRootSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String rootId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetRootSizeParams() {
            super(24, 0);
        }

        public static FileSystemInstanceGetRootSizeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetRootSizeParams fileSystemInstanceGetRootSizeParams = new FileSystemInstanceGetRootSizeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetRootSizeParams.authority = decoder.readString(8, false);
                fileSystemInstanceGetRootSizeParams.rootId = decoder.readString(16, false);
                return fileSystemInstanceGetRootSizeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.rootId, 16, false);
        }

        private FileSystemInstanceGetRootSizeParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceGetRootSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public RootSize rootSize;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetRootSizeResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetRootSizeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceGetRootSizeResponseParams fileSystemInstanceGetRootSizeResponseParams = new FileSystemInstanceGetRootSizeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceGetRootSizeResponseParams.rootSize = RootSize.decode(decoder.readPointer(8, true));
                return fileSystemInstanceGetRootSizeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.rootSize, 8, true);
        }

        private FileSystemInstanceGetRootSizeResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceGetRootSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.GetRootSizeResponse mCallback;

        FileSystemInstanceGetRootSizeResponseParamsForwardToCallback(FileSystemInstance.GetRootSizeResponse getRootSizeResponse) {
            this.mCallback = getRootSizeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(22, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceGetRootSizeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).rootSize);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceGetRootSizeResponseParamsProxyToResponder implements FileSystemInstance.GetRootSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceGetRootSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(RootSize rootSize) {
            FileSystemInstanceGetRootSizeResponseParams fileSystemInstanceGetRootSizeResponseParams = new FileSystemInstanceGetRootSizeResponseParams();
            fileSystemInstanceGetRootSizeResponseParams.rootSize = rootSize;
            this.mMessageReceiver.accept(fileSystemInstanceGetRootSizeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(22, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceGetRootsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetRootsParams() {
            super(8, 0);
        }

        public static FileSystemInstanceGetRootsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new FileSystemInstanceGetRootsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private FileSystemInstanceGetRootsParams(int i) {
            super(8, i);
        }
    }

    static final class FileSystemInstanceGetRootsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Root[] roots;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceGetRootsResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceGetRootsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                FileSystemInstanceGetRootsResponseParams fileSystemInstanceGetRootsResponseParams = new FileSystemInstanceGetRootsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, true);
                if (pointer == null) {
                    fileSystemInstanceGetRootsResponseParams.roots = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    fileSystemInstanceGetRootsResponseParams.roots = new Root[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                        fileSystemInstanceGetRootsResponseParams.roots[i] = Root.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                    }
                }
                return fileSystemInstanceGetRootsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            Root[] rootArr = this.roots;
            if (rootArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, true);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(rootArr.length, 8, -1);
            int i = 0;
            while (true) {
                Root[] rootArr2 = this.roots;
                if (i >= rootArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) rootArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private FileSystemInstanceGetRootsResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceGetRootsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.GetRootsResponse mCallback;

        FileSystemInstanceGetRootsResponseParamsForwardToCallback(FileSystemInstance.GetRootsResponse getRootsResponse) {
            this.mCallback = getRootsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceGetRootsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).roots);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceGetRootsResponseParamsProxyToResponder implements FileSystemInstance.GetRootsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceGetRootsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Root[] rootArr) {
            FileSystemInstanceGetRootsResponseParams fileSystemInstanceGetRootsResponseParams = new FileSystemInstanceGetRootsResponseParams();
            fileSystemInstanceGetRootsResponseParams.roots = rootArr;
            this.mMessageReceiver.accept(fileSystemInstanceGetRootsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FileSystemHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceInitParams() {
            super(16, 0);
        }

        public static FileSystemInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceInitParams fileSystemInstanceInitParams = new FileSystemInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceInitParams.hostRemote = (FileSystemHost) decoder.readServiceInterface(8, false, FileSystemHost.MANAGER);
                return fileSystemInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<FileSystemHost, ?>) FileSystemHost.MANAGER);
        }

        private FileSystemInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class FileSystemInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.InitResponse mCallback;

        FileSystemInstanceInitResponseParamsForwardToCallback(FileSystemInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(10, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceInitResponseParamsProxyToResponder implements FileSystemInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new FileSystemInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceMoveDocumentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String sourceDocumentId;
        public String sourceParentDocumentId;
        public String targetParentDocumentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceMoveDocumentParams() {
            super(40, 0);
        }

        public static FileSystemInstanceMoveDocumentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceMoveDocumentParams fileSystemInstanceMoveDocumentParams = new FileSystemInstanceMoveDocumentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceMoveDocumentParams.authority = decoder.readString(8, false);
                fileSystemInstanceMoveDocumentParams.sourceDocumentId = decoder.readString(16, false);
                fileSystemInstanceMoveDocumentParams.sourceParentDocumentId = decoder.readString(24, false);
                fileSystemInstanceMoveDocumentParams.targetParentDocumentId = decoder.readString(32, false);
                return fileSystemInstanceMoveDocumentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.sourceDocumentId, 16, false);
            encoderAtDataOffset.encode(this.sourceParentDocumentId, 24, false);
            encoderAtDataOffset.encode(this.targetParentDocumentId, 32, false);
        }

        private FileSystemInstanceMoveDocumentParams(int i) {
            super(40, i);
        }
    }

    static final class FileSystemInstanceMoveDocumentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Document document;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceMoveDocumentResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceMoveDocumentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceMoveDocumentResponseParams fileSystemInstanceMoveDocumentResponseParams = new FileSystemInstanceMoveDocumentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceMoveDocumentResponseParams.document = Document.decode(decoder.readPointer(8, true));
                return fileSystemInstanceMoveDocumentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.document, 8, true);
        }

        private FileSystemInstanceMoveDocumentResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceMoveDocumentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.MoveDocumentResponse mCallback;

        FileSystemInstanceMoveDocumentResponseParamsForwardToCallback(FileSystemInstance.MoveDocumentResponse moveDocumentResponse) {
            this.mCallback = moveDocumentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(17, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceMoveDocumentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).document);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceMoveDocumentResponseParamsProxyToResponder implements FileSystemInstance.MoveDocumentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceMoveDocumentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Document document) {
            FileSystemInstanceMoveDocumentResponseParams fileSystemInstanceMoveDocumentResponseParams = new FileSystemInstanceMoveDocumentResponseParams();
            fileSystemInstanceMoveDocumentResponseParams.document = document;
            this.mMessageReceiver.accept(fileSystemInstanceMoveDocumentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(17, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceOpenFileSessionToReadParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Url url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenFileSessionToReadParams() {
            super(16, 0);
        }

        public static FileSystemInstanceOpenFileSessionToReadParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceOpenFileSessionToReadParams fileSystemInstanceOpenFileSessionToReadParams = new FileSystemInstanceOpenFileSessionToReadParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceOpenFileSessionToReadParams.url = Url.decode(decoder.readPointer(8, false));
                return fileSystemInstanceOpenFileSessionToReadParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.url, 8, false);
        }

        private FileSystemInstanceOpenFileSessionToReadParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceOpenFileSessionToReadResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FileSession fileSession;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenFileSessionToReadResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceOpenFileSessionToReadResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceOpenFileSessionToReadResponseParams fileSystemInstanceOpenFileSessionToReadResponseParams = new FileSystemInstanceOpenFileSessionToReadResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceOpenFileSessionToReadResponseParams.fileSession = FileSession.decode(decoder.readPointer(8, true));
                return fileSystemInstanceOpenFileSessionToReadResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.fileSession, 8, true);
        }

        private FileSystemInstanceOpenFileSessionToReadResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceOpenFileSessionToReadResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.OpenFileSessionToReadResponse mCallback;

        FileSystemInstanceOpenFileSessionToReadResponseParamsForwardToCallback(FileSystemInstance.OpenFileSessionToReadResponse openFileSessionToReadResponse) {
            this.mCallback = openFileSessionToReadResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(26, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceOpenFileSessionToReadResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).fileSession);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceOpenFileSessionToReadResponseParamsProxyToResponder implements FileSystemInstance.OpenFileSessionToReadResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceOpenFileSessionToReadResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(FileSession fileSession) {
            FileSystemInstanceOpenFileSessionToReadResponseParams fileSystemInstanceOpenFileSessionToReadResponseParams = new FileSystemInstanceOpenFileSessionToReadResponseParams();
            fileSystemInstanceOpenFileSessionToReadResponseParams.fileSession = fileSession;
            this.mMessageReceiver.accept(fileSystemInstanceOpenFileSessionToReadResponseParams.serializeWithHeader(this.mCore, new MessageHeader(26, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceOpenFileSessionToWriteParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Url url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenFileSessionToWriteParams() {
            super(16, 0);
        }

        public static FileSystemInstanceOpenFileSessionToWriteParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceOpenFileSessionToWriteParams fileSystemInstanceOpenFileSessionToWriteParams = new FileSystemInstanceOpenFileSessionToWriteParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceOpenFileSessionToWriteParams.url = Url.decode(decoder.readPointer(8, false));
                return fileSystemInstanceOpenFileSessionToWriteParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.url, 8, false);
        }

        private FileSystemInstanceOpenFileSessionToWriteParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceOpenFileSessionToWriteResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FileSession fileSession;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenFileSessionToWriteResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceOpenFileSessionToWriteResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceOpenFileSessionToWriteResponseParams fileSystemInstanceOpenFileSessionToWriteResponseParams = new FileSystemInstanceOpenFileSessionToWriteResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceOpenFileSessionToWriteResponseParams.fileSession = FileSession.decode(decoder.readPointer(8, true));
                return fileSystemInstanceOpenFileSessionToWriteResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.fileSession, 8, true);
        }

        private FileSystemInstanceOpenFileSessionToWriteResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceOpenFileSessionToWriteResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.OpenFileSessionToWriteResponse mCallback;

        FileSystemInstanceOpenFileSessionToWriteResponseParamsForwardToCallback(FileSystemInstance.OpenFileSessionToWriteResponse openFileSessionToWriteResponse) {
            this.mCallback = openFileSessionToWriteResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(25, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceOpenFileSessionToWriteResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).fileSession);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceOpenFileSessionToWriteResponseParamsProxyToResponder implements FileSystemInstance.OpenFileSessionToWriteResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceOpenFileSessionToWriteResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(FileSession fileSession) {
            FileSystemInstanceOpenFileSessionToWriteResponseParams fileSystemInstanceOpenFileSessionToWriteResponseParams = new FileSystemInstanceOpenFileSessionToWriteResponseParams();
            fileSystemInstanceOpenFileSessionToWriteResponseParams.fileSession = fileSession;
            this.mMessageReceiver.accept(fileSystemInstanceOpenFileSessionToWriteResponseParams.serializeWithHeader(this.mCore, new MessageHeader(25, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceOpenThumbnailParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Size sizeHint;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenThumbnailParams() {
            super(24, 0);
        }

        public static FileSystemInstanceOpenThumbnailParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceOpenThumbnailParams fileSystemInstanceOpenThumbnailParams = new FileSystemInstanceOpenThumbnailParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceOpenThumbnailParams.url = decoder.readString(8, false);
                fileSystemInstanceOpenThumbnailParams.sizeHint = Size.decode(decoder.readPointer(16, false));
                return fileSystemInstanceOpenThumbnailParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.url, 8, false);
            encoderAtDataOffset.encode((Struct) this.sizeHint, 16, false);
        }

        private FileSystemInstanceOpenThumbnailParams(int i) {
            super(24, i);
        }
    }

    static class FileSystemInstanceOpenThumbnailResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.OpenThumbnailResponse mCallback;

        FileSystemInstanceOpenThumbnailResponseParamsForwardToCallback(FileSystemInstance.OpenThumbnailResponse openThumbnailResponse) {
            this.mCallback = openThumbnailResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(21, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceOpenThumbnailResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).fd);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceOpenThumbnailResponseParamsProxyToResponder implements FileSystemInstance.OpenThumbnailResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceOpenThumbnailResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UntypedHandle untypedHandle) {
            FileSystemInstanceOpenThumbnailResponseParams fileSystemInstanceOpenThumbnailResponseParams = new FileSystemInstanceOpenThumbnailResponseParams();
            fileSystemInstanceOpenThumbnailResponseParams.fd = untypedHandle;
            this.mMessageReceiver.accept(fileSystemInstanceOpenThumbnailResponseParams.serializeWithHeader(this.mCore, new MessageHeader(21, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public OpenUrlsRequest request;
        public WindowInfo windowInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams() {
            super(24, 0);
        }

        public static FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams = new FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams.request = OpenUrlsRequest.decode(decoder.readPointer(8, false));
                fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams.windowInfo = WindowInfo.decode(decoder.readPointer(16, false));
                return fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.request, 8, false);
            encoderAtDataOffset.encode((Struct) this.windowInfo, 16, false);
        }

        private FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams(int i) {
            super(24, i);
        }
    }

    static final class FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.OpenUrlsWithPermissionAndWindowInfoResponse mCallback;

        FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParamsForwardToCallback(FileSystemInstance.OpenUrlsWithPermissionAndWindowInfoResponse openUrlsWithPermissionAndWindowInfoResponse) {
            this.mCallback = openUrlsWithPermissionAndWindowInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(23, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParamsProxyToResponder implements FileSystemInstance.OpenUrlsWithPermissionAndWindowInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParams().serializeWithHeader(this.mCore, new MessageHeader(23, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceReindexDirectoryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String directoryPath;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceReindexDirectoryParams() {
            super(16, 0);
        }

        public static FileSystemInstanceReindexDirectoryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceReindexDirectoryParams fileSystemInstanceReindexDirectoryParams = new FileSystemInstanceReindexDirectoryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceReindexDirectoryParams.directoryPath = decoder.readString(8, false);
                return fileSystemInstanceReindexDirectoryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.directoryPath, 8, false);
        }

        private FileSystemInstanceReindexDirectoryParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceRemoveWatcherParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long watcherId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceRemoveWatcherParams() {
            super(16, 0);
        }

        public static FileSystemInstanceRemoveWatcherParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceRemoveWatcherParams fileSystemInstanceRemoveWatcherParams = new FileSystemInstanceRemoveWatcherParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceRemoveWatcherParams.watcherId = decoder.readLong(8);
                return fileSystemInstanceRemoveWatcherParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.watcherId, 8);
        }

        private FileSystemInstanceRemoveWatcherParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceRemoveWatcherResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceRemoveWatcherResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceRemoveWatcherResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceRemoveWatcherResponseParams fileSystemInstanceRemoveWatcherResponseParams = new FileSystemInstanceRemoveWatcherResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceRemoveWatcherResponseParams.success = decoder.readBoolean(8, 0);
                return fileSystemInstanceRemoveWatcherResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private FileSystemInstanceRemoveWatcherResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceRemoveWatcherResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.RemoveWatcherResponse mCallback;

        FileSystemInstanceRemoveWatcherResponseParamsForwardToCallback(FileSystemInstance.RemoveWatcherResponse removeWatcherResponse) {
            this.mCallback = removeWatcherResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(FileSystemInstanceRemoveWatcherResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceRemoveWatcherResponseParamsProxyToResponder implements FileSystemInstance.RemoveWatcherResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceRemoveWatcherResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            FileSystemInstanceRemoveWatcherResponseParams fileSystemInstanceRemoveWatcherResponseParams = new FileSystemInstanceRemoveWatcherResponseParams();
            fileSystemInstanceRemoveWatcherResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(fileSystemInstanceRemoveWatcherResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceRenameDocumentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String authority;
        public String displayName;
        public String documentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceRenameDocumentParams() {
            super(32, 0);
        }

        public static FileSystemInstanceRenameDocumentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceRenameDocumentParams fileSystemInstanceRenameDocumentParams = new FileSystemInstanceRenameDocumentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceRenameDocumentParams.authority = decoder.readString(8, false);
                fileSystemInstanceRenameDocumentParams.documentId = decoder.readString(16, false);
                fileSystemInstanceRenameDocumentParams.displayName = decoder.readString(24, false);
                return fileSystemInstanceRenameDocumentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.authority, 8, false);
            encoderAtDataOffset.encode(this.documentId, 16, false);
            encoderAtDataOffset.encode(this.displayName, 24, false);
        }

        private FileSystemInstanceRenameDocumentParams(int i) {
            super(32, i);
        }
    }

    static final class FileSystemInstanceRenameDocumentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Document document;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceRenameDocumentResponseParams() {
            super(16, 0);
        }

        public static FileSystemInstanceRenameDocumentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceRenameDocumentResponseParams fileSystemInstanceRenameDocumentResponseParams = new FileSystemInstanceRenameDocumentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceRenameDocumentResponseParams.document = Document.decode(decoder.readPointer(8, true));
                return fileSystemInstanceRenameDocumentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.document, 8, true);
        }

        private FileSystemInstanceRenameDocumentResponseParams(int i) {
            super(16, i);
        }
    }

    static class FileSystemInstanceRenameDocumentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FileSystemInstance.RenameDocumentResponse mCallback;

        FileSystemInstanceRenameDocumentResponseParamsForwardToCallback(FileSystemInstance.RenameDocumentResponse renameDocumentResponse) {
            this.mCallback = renameDocumentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(14, 2)) {
                    return false;
                }
                this.mCallback.call(FileSystemInstanceRenameDocumentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).document);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class FileSystemInstanceRenameDocumentResponseParamsProxyToResponder implements FileSystemInstance.RenameDocumentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        FileSystemInstanceRenameDocumentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Document document) {
            FileSystemInstanceRenameDocumentResponseParams fileSystemInstanceRenameDocumentResponseParams = new FileSystemInstanceRenameDocumentResponseParams();
            fileSystemInstanceRenameDocumentResponseParams.document = document;
            this.mMessageReceiver.accept(fileSystemInstanceRenameDocumentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(14, 2, this.mRequestId)));
        }
    }

    static final class FileSystemInstanceRequestFileRemovalScanParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] directoryPaths;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceRequestFileRemovalScanParams() {
            super(16, 0);
        }

        public static FileSystemInstanceRequestFileRemovalScanParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                FileSystemInstanceRequestFileRemovalScanParams fileSystemInstanceRequestFileRemovalScanParams = new FileSystemInstanceRequestFileRemovalScanParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                fileSystemInstanceRequestFileRemovalScanParams.directoryPaths = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    fileSystemInstanceRequestFileRemovalScanParams.directoryPaths[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return fileSystemInstanceRequestFileRemovalScanParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.directoryPaths;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.directoryPaths;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private FileSystemInstanceRequestFileRemovalScanParams(int i) {
            super(16, i);
        }
    }

    static final class FileSystemInstanceRequestMediaScanParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] paths;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceRequestMediaScanParams() {
            super(16, 0);
        }

        public static FileSystemInstanceRequestMediaScanParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                FileSystemInstanceRequestMediaScanParams fileSystemInstanceRequestMediaScanParams = new FileSystemInstanceRequestMediaScanParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                fileSystemInstanceRequestMediaScanParams.paths = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    fileSystemInstanceRequestMediaScanParams.paths[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return fileSystemInstanceRequestMediaScanParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.paths;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.paths;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private FileSystemInstanceRequestMediaScanParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements FileSystemInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void addWatcher(String str, String str2, FileSystemInstance.AddWatcherResponse addWatcherResponse) {
            FileSystemInstanceAddWatcherParams fileSystemInstanceAddWatcherParams = new FileSystemInstanceAddWatcherParams();
            fileSystemInstanceAddWatcherParams.authority = str;
            fileSystemInstanceAddWatcherParams.documentId = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceAddWatcherParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new FileSystemInstanceAddWatcherResponseParamsForwardToCallback(addWatcherResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void closeFileSession(String str, String str2) {
            FileSystemInstanceCloseFileSessionParams fileSystemInstanceCloseFileSessionParams = new FileSystemInstanceCloseFileSessionParams();
            fileSystemInstanceCloseFileSessionParams.urlId = str;
            fileSystemInstanceCloseFileSessionParams.errorMessage = str2;
            getProxyHandler().getMessageReceiver().accept(fileSystemInstanceCloseFileSessionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(24)));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void copyDocument(String str, String str2, String str3, FileSystemInstance.CopyDocumentResponse copyDocumentResponse) {
            FileSystemInstanceCopyDocumentParams fileSystemInstanceCopyDocumentParams = new FileSystemInstanceCopyDocumentParams();
            fileSystemInstanceCopyDocumentParams.authority = str;
            fileSystemInstanceCopyDocumentParams.sourceDocumentId = str2;
            fileSystemInstanceCopyDocumentParams.targetParentDocumentId = str3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceCopyDocumentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16, 1, 0L)), new FileSystemInstanceCopyDocumentResponseParamsForwardToCallback(copyDocumentResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void createDocument(String str, String str2, String str3, String str4, FileSystemInstance.CreateDocumentResponse createDocumentResponse) {
            FileSystemInstanceCreateDocumentParams fileSystemInstanceCreateDocumentParams = new FileSystemInstanceCreateDocumentParams();
            fileSystemInstanceCreateDocumentParams.authority = str;
            fileSystemInstanceCreateDocumentParams.parentDocumentId = str2;
            fileSystemInstanceCreateDocumentParams.mimeType = str3;
            fileSystemInstanceCreateDocumentParams.displayName = str4;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceCreateDocumentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15, 1, 0L)), new FileSystemInstanceCreateDocumentResponseParamsForwardToCallback(createDocumentResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void deleteDocument(String str, String str2, FileSystemInstance.DeleteDocumentResponse deleteDocumentResponse) {
            FileSystemInstanceDeleteDocumentParams fileSystemInstanceDeleteDocumentParams = new FileSystemInstanceDeleteDocumentParams();
            fileSystemInstanceDeleteDocumentParams.authority = str;
            fileSystemInstanceDeleteDocumentParams.documentId = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceDeleteDocumentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13, 1, 0L)), new FileSystemInstanceDeleteDocumentResponseParamsForwardToCallback(deleteDocumentResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void getChildDocuments(String str, String str2, FileSystemInstance.GetChildDocumentsResponse getChildDocumentsResponse) {
            FileSystemInstanceGetChildDocumentsParams fileSystemInstanceGetChildDocumentsParams = new FileSystemInstanceGetChildDocumentsParams();
            fileSystemInstanceGetChildDocumentsParams.authority = str;
            fileSystemInstanceGetChildDocumentsParams.parentDocumentId = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceGetChildDocumentsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new FileSystemInstanceGetChildDocumentsResponseParamsForwardToCallback(getChildDocumentsResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void getDocument(String str, String str2, FileSystemInstance.GetDocumentResponse getDocumentResponse) {
            FileSystemInstanceGetDocumentParams fileSystemInstanceGetDocumentParams = new FileSystemInstanceGetDocumentParams();
            fileSystemInstanceGetDocumentParams.authority = str;
            fileSystemInstanceGetDocumentParams.documentId = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceGetDocumentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new FileSystemInstanceGetDocumentResponseParamsForwardToCallback(getDocumentResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void getFileSize(String str, FileSystemInstance.GetFileSizeResponse getFileSizeResponse) {
            FileSystemInstanceGetFileSizeParams fileSystemInstanceGetFileSizeParams = new FileSystemInstanceGetFileSizeParams();
            fileSystemInstanceGetFileSizeParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceGetFileSizeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new FileSystemInstanceGetFileSizeResponseParamsForwardToCallback(getFileSizeResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void getMimeType(String str, FileSystemInstance.GetMimeTypeResponse getMimeTypeResponse) {
            FileSystemInstanceGetMimeTypeParams fileSystemInstanceGetMimeTypeParams = new FileSystemInstanceGetMimeTypeParams();
            fileSystemInstanceGetMimeTypeParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceGetMimeTypeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new FileSystemInstanceGetMimeTypeResponseParamsForwardToCallback(getMimeTypeResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void getRecentDocuments(String str, String str2, FileSystemInstance.GetRecentDocumentsResponse getRecentDocumentsResponse) {
            FileSystemInstanceGetRecentDocumentsParams fileSystemInstanceGetRecentDocumentsParams = new FileSystemInstanceGetRecentDocumentsParams();
            fileSystemInstanceGetRecentDocumentsParams.authority = str;
            fileSystemInstanceGetRecentDocumentsParams.rootId = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceGetRecentDocumentsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new FileSystemInstanceGetRecentDocumentsResponseParamsForwardToCallback(getRecentDocumentsResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void getRootSize(String str, String str2, FileSystemInstance.GetRootSizeResponse getRootSizeResponse) {
            FileSystemInstanceGetRootSizeParams fileSystemInstanceGetRootSizeParams = new FileSystemInstanceGetRootSizeParams();
            fileSystemInstanceGetRootSizeParams.authority = str;
            fileSystemInstanceGetRootSizeParams.rootId = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceGetRootSizeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22, 1, 0L)), new FileSystemInstanceGetRootSizeResponseParamsForwardToCallback(getRootSizeResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void getRoots(FileSystemInstance.GetRootsResponse getRootsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new FileSystemInstanceGetRootsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new FileSystemInstanceGetRootsResponseParamsForwardToCallback(getRootsResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void init(FileSystemHost fileSystemHost, FileSystemInstance.InitResponse initResponse) {
            FileSystemInstanceInitParams fileSystemInstanceInitParams = new FileSystemInstanceInitParams();
            fileSystemInstanceInitParams.hostRemote = fileSystemHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new FileSystemInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void moveDocument(String str, String str2, String str3, String str4, FileSystemInstance.MoveDocumentResponse moveDocumentResponse) {
            FileSystemInstanceMoveDocumentParams fileSystemInstanceMoveDocumentParams = new FileSystemInstanceMoveDocumentParams();
            fileSystemInstanceMoveDocumentParams.authority = str;
            fileSystemInstanceMoveDocumentParams.sourceDocumentId = str2;
            fileSystemInstanceMoveDocumentParams.sourceParentDocumentId = str3;
            fileSystemInstanceMoveDocumentParams.targetParentDocumentId = str4;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceMoveDocumentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17, 1, 0L)), new FileSystemInstanceMoveDocumentResponseParamsForwardToCallback(moveDocumentResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void openFileSessionToRead(Url url, FileSystemInstance.OpenFileSessionToReadResponse openFileSessionToReadResponse) {
            FileSystemInstanceOpenFileSessionToReadParams fileSystemInstanceOpenFileSessionToReadParams = new FileSystemInstanceOpenFileSessionToReadParams();
            fileSystemInstanceOpenFileSessionToReadParams.url = url;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceOpenFileSessionToReadParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(26, 1, 0L)), new FileSystemInstanceOpenFileSessionToReadResponseParamsForwardToCallback(openFileSessionToReadResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void openFileSessionToWrite(Url url, FileSystemInstance.OpenFileSessionToWriteResponse openFileSessionToWriteResponse) {
            FileSystemInstanceOpenFileSessionToWriteParams fileSystemInstanceOpenFileSessionToWriteParams = new FileSystemInstanceOpenFileSessionToWriteParams();
            fileSystemInstanceOpenFileSessionToWriteParams.url = url;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceOpenFileSessionToWriteParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(25, 1, 0L)), new FileSystemInstanceOpenFileSessionToWriteResponseParamsForwardToCallback(openFileSessionToWriteResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void openThumbnail(String str, Size size, FileSystemInstance.OpenThumbnailResponse openThumbnailResponse) {
            FileSystemInstanceOpenThumbnailParams fileSystemInstanceOpenThumbnailParams = new FileSystemInstanceOpenThumbnailParams();
            fileSystemInstanceOpenThumbnailParams.url = str;
            fileSystemInstanceOpenThumbnailParams.sizeHint = size;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceOpenThumbnailParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21, 1, 0L)), new FileSystemInstanceOpenThumbnailResponseParamsForwardToCallback(openThumbnailResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void openUrlsWithPermissionAndWindowInfo(OpenUrlsRequest openUrlsRequest, WindowInfo windowInfo, FileSystemInstance.OpenUrlsWithPermissionAndWindowInfoResponse openUrlsWithPermissionAndWindowInfoResponse) {
            FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams = new FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams();
            fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams.request = openUrlsRequest;
            fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams.windowInfo = windowInfo;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23, 1, 0L)), new FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParamsForwardToCallback(openUrlsWithPermissionAndWindowInfoResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void reindexDirectory(String str) {
            FileSystemInstanceReindexDirectoryParams fileSystemInstanceReindexDirectoryParams = new FileSystemInstanceReindexDirectoryParams();
            fileSystemInstanceReindexDirectoryParams.directoryPath = str;
            getProxyHandler().getMessageReceiver().accept(fileSystemInstanceReindexDirectoryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void removeWatcher(long j, FileSystemInstance.RemoveWatcherResponse removeWatcherResponse) {
            FileSystemInstanceRemoveWatcherParams fileSystemInstanceRemoveWatcherParams = new FileSystemInstanceRemoveWatcherParams();
            fileSystemInstanceRemoveWatcherParams.watcherId = j;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceRemoveWatcherParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new FileSystemInstanceRemoveWatcherResponseParamsForwardToCallback(removeWatcherResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void renameDocument(String str, String str2, String str3, FileSystemInstance.RenameDocumentResponse renameDocumentResponse) {
            FileSystemInstanceRenameDocumentParams fileSystemInstanceRenameDocumentParams = new FileSystemInstanceRenameDocumentParams();
            fileSystemInstanceRenameDocumentParams.authority = str;
            fileSystemInstanceRenameDocumentParams.documentId = str2;
            fileSystemInstanceRenameDocumentParams.displayName = str3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(fileSystemInstanceRenameDocumentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14, 1, 0L)), new FileSystemInstanceRenameDocumentResponseParamsForwardToCallback(renameDocumentResponse));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void requestFileRemovalScan(String[] strArr) {
            FileSystemInstanceRequestFileRemovalScanParams fileSystemInstanceRequestFileRemovalScanParams = new FileSystemInstanceRequestFileRemovalScanParams();
            fileSystemInstanceRequestFileRemovalScanParams.directoryPaths = strArr;
            getProxyHandler().getMessageReceiver().accept(fileSystemInstanceRequestFileRemovalScanParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.arc.mojom.FileSystemInstance
        public void requestMediaScan(String[] strArr) {
            FileSystemInstanceRequestMediaScanParams fileSystemInstanceRequestMediaScanParams = new FileSystemInstanceRequestMediaScanParams();
            fileSystemInstanceRequestMediaScanParams.paths = strArr;
            getProxyHandler().getMessageReceiver().accept(fileSystemInstanceRequestMediaScanParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<FileSystemInstance> {
        Stub(Core core, FileSystemInstance fileSystemInstance) {
            super(core, fileSystemInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(FileSystemInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().requestMediaScan(FileSystemInstanceRequestMediaScanParams.deserialize(serviceMessageAsServiceMessage.getPayload()).paths);
                    return true;
                }
                if (type == 24) {
                    FileSystemInstanceCloseFileSessionParams fileSystemInstanceCloseFileSessionParamsDeserialize = FileSystemInstanceCloseFileSessionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().closeFileSession(fileSystemInstanceCloseFileSessionParamsDeserialize.urlId, fileSystemInstanceCloseFileSessionParamsDeserialize.errorMessage);
                    return true;
                }
                if (type == 19) {
                    getImpl().reindexDirectory(FileSystemInstanceReindexDirectoryParams.deserialize(serviceMessageAsServiceMessage.getPayload()).directoryPath);
                    return true;
                }
                if (type != 20) {
                    return false;
                }
                getImpl().requestFileRemovalScan(FileSystemInstanceRequestFileRemovalScanParams.deserialize(serviceMessageAsServiceMessage.getPayload()).directoryPaths);
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
                        getImpl().getFileSize(FileSystemInstanceGetFileSizeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemInstanceGetFileSizeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        FileSystemInstanceGetDocumentParams fileSystemInstanceGetDocumentParamsDeserialize = FileSystemInstanceGetDocumentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getDocument(fileSystemInstanceGetDocumentParamsDeserialize.authority, fileSystemInstanceGetDocumentParamsDeserialize.documentId, new FileSystemInstanceGetDocumentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        FileSystemInstanceGetChildDocumentsParams fileSystemInstanceGetChildDocumentsParamsDeserialize = FileSystemInstanceGetChildDocumentsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getChildDocuments(fileSystemInstanceGetChildDocumentsParamsDeserialize.authority, fileSystemInstanceGetChildDocumentsParamsDeserialize.parentDocumentId, new FileSystemInstanceGetChildDocumentsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        FileSystemInstanceAddWatcherParams fileSystemInstanceAddWatcherParamsDeserialize = FileSystemInstanceAddWatcherParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().addWatcher(fileSystemInstanceAddWatcherParamsDeserialize.authority, fileSystemInstanceAddWatcherParamsDeserialize.documentId, new FileSystemInstanceAddWatcherResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        getImpl().removeWatcher(FileSystemInstanceRemoveWatcherParams.deserialize(serviceMessageAsServiceMessage.getPayload()).watcherId, new FileSystemInstanceRemoveWatcherResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 8:
                        getImpl().getMimeType(FileSystemInstanceGetMimeTypeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemInstanceGetMimeTypeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 9:
                        FileSystemInstanceGetRecentDocumentsParams fileSystemInstanceGetRecentDocumentsParamsDeserialize = FileSystemInstanceGetRecentDocumentsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getRecentDocuments(fileSystemInstanceGetRecentDocumentsParamsDeserialize.authority, fileSystemInstanceGetRecentDocumentsParamsDeserialize.rootId, new FileSystemInstanceGetRecentDocumentsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 10:
                        getImpl().init(FileSystemInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new FileSystemInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 12:
                        FileSystemInstanceGetRootsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getRoots(new FileSystemInstanceGetRootsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 13:
                        FileSystemInstanceDeleteDocumentParams fileSystemInstanceDeleteDocumentParamsDeserialize = FileSystemInstanceDeleteDocumentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deleteDocument(fileSystemInstanceDeleteDocumentParamsDeserialize.authority, fileSystemInstanceDeleteDocumentParamsDeserialize.documentId, new FileSystemInstanceDeleteDocumentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 14:
                        FileSystemInstanceRenameDocumentParams fileSystemInstanceRenameDocumentParamsDeserialize = FileSystemInstanceRenameDocumentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().renameDocument(fileSystemInstanceRenameDocumentParamsDeserialize.authority, fileSystemInstanceRenameDocumentParamsDeserialize.documentId, fileSystemInstanceRenameDocumentParamsDeserialize.displayName, new FileSystemInstanceRenameDocumentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 15:
                        FileSystemInstanceCreateDocumentParams fileSystemInstanceCreateDocumentParamsDeserialize = FileSystemInstanceCreateDocumentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().createDocument(fileSystemInstanceCreateDocumentParamsDeserialize.authority, fileSystemInstanceCreateDocumentParamsDeserialize.parentDocumentId, fileSystemInstanceCreateDocumentParamsDeserialize.mimeType, fileSystemInstanceCreateDocumentParamsDeserialize.displayName, new FileSystemInstanceCreateDocumentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 16:
                        FileSystemInstanceCopyDocumentParams fileSystemInstanceCopyDocumentParamsDeserialize = FileSystemInstanceCopyDocumentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().copyDocument(fileSystemInstanceCopyDocumentParamsDeserialize.authority, fileSystemInstanceCopyDocumentParamsDeserialize.sourceDocumentId, fileSystemInstanceCopyDocumentParamsDeserialize.targetParentDocumentId, new FileSystemInstanceCopyDocumentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 17:
                        FileSystemInstanceMoveDocumentParams fileSystemInstanceMoveDocumentParamsDeserialize = FileSystemInstanceMoveDocumentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().moveDocument(fileSystemInstanceMoveDocumentParamsDeserialize.authority, fileSystemInstanceMoveDocumentParamsDeserialize.sourceDocumentId, fileSystemInstanceMoveDocumentParamsDeserialize.sourceParentDocumentId, fileSystemInstanceMoveDocumentParamsDeserialize.targetParentDocumentId, new FileSystemInstanceMoveDocumentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 21:
                        FileSystemInstanceOpenThumbnailParams fileSystemInstanceOpenThumbnailParamsDeserialize = FileSystemInstanceOpenThumbnailParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().openThumbnail(fileSystemInstanceOpenThumbnailParamsDeserialize.url, fileSystemInstanceOpenThumbnailParamsDeserialize.sizeHint, new FileSystemInstanceOpenThumbnailResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 22:
                        FileSystemInstanceGetRootSizeParams fileSystemInstanceGetRootSizeParamsDeserialize = FileSystemInstanceGetRootSizeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getRootSize(fileSystemInstanceGetRootSizeParamsDeserialize.authority, fileSystemInstanceGetRootSizeParamsDeserialize.rootId, new FileSystemInstanceGetRootSizeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 23:
                        FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParamsDeserialize = FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().openUrlsWithPermissionAndWindowInfo(fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParamsDeserialize.request, fileSystemInstanceOpenUrlsWithPermissionAndWindowInfoParamsDeserialize.windowInfo, new FileSystemInstanceOpenUrlsWithPermissionAndWindowInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 25:
                        getImpl().openFileSessionToWrite(FileSystemInstanceOpenFileSessionToWriteParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemInstanceOpenFileSessionToWriteResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 26:
                        getImpl().openFileSessionToRead(FileSystemInstanceOpenFileSessionToReadParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new FileSystemInstanceOpenFileSessionToReadResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                    return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class FileSystemInstanceOpenThumbnailResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle fd;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public FileSystemInstanceOpenThumbnailResponseParams() {
            super(16, 0);
            this.fd = InvalidHandle.INSTANCE;
        }

        public static FileSystemInstanceOpenThumbnailResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                FileSystemInstanceOpenThumbnailResponseParams fileSystemInstanceOpenThumbnailResponseParams = new FileSystemInstanceOpenThumbnailResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                fileSystemInstanceOpenThumbnailResponseParams.fd = decoder.readUntypedHandle(8, true);
                return fileSystemInstanceOpenThumbnailResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.fd, 8, true);
        }

        private FileSystemInstanceOpenThumbnailResponseParams(int i) {
            super(16, i);
            this.fd = InvalidHandle.INSTANCE;
        }
    }
}

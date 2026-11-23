// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.PrintSessionInstance;
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

class PrintSessionInstance_Internal {
    public static final Interface.Manager<PrintSessionInstance, PrintSessionInstance.Proxy> MANAGER = new Interface.Manager<PrintSessionInstance, PrintSessionInstance.Proxy>() { // from class: org.chromium.arc.mojom.PrintSessionInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrintSessionInstance[] buildArray(int i) {
            return new PrintSessionInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrintSessionInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PrintSessionInstance> buildStub(Core core, PrintSessionInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PrintSessionInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class PrintSessionInstanceCreatePreviewDocumentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PrintDocumentRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrintSessionInstanceCreatePreviewDocumentParams() {
            super(16, 0);
        }

        public static PrintSessionInstanceCreatePreviewDocumentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PrintSessionInstanceCreatePreviewDocumentParams printSessionInstanceCreatePreviewDocumentParams = new PrintSessionInstanceCreatePreviewDocumentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                printSessionInstanceCreatePreviewDocumentParams.request = PrintDocumentRequest.decode(decoder.readPointer(8, false));
                return printSessionInstanceCreatePreviewDocumentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private PrintSessionInstanceCreatePreviewDocumentParams(int i) {
            super(16, i);
        }
    }

    static class PrintSessionInstanceCreatePreviewDocumentResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PrintSessionInstance.CreatePreviewDocumentResponse mCallback;

        PrintSessionInstanceCreatePreviewDocumentResponseParamsForwardToCallback(PrintSessionInstance.CreatePreviewDocumentResponse createPreviewDocumentResponse) {
            this.mCallback = createPreviewDocumentResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                PrintSessionInstanceCreatePreviewDocumentResponseParams printSessionInstanceCreatePreviewDocumentResponseParamsDeserialize = PrintSessionInstanceCreatePreviewDocumentResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(printSessionInstanceCreatePreviewDocumentResponseParamsDeserialize.previewDocument, Long.valueOf(printSessionInstanceCreatePreviewDocumentResponseParamsDeserialize.dataSize));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PrintSessionInstanceCreatePreviewDocumentResponseParamsProxyToResponder implements PrintSessionInstance.CreatePreviewDocumentResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PrintSessionInstanceCreatePreviewDocumentResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(UntypedHandle untypedHandle, Long l) {
            PrintSessionInstanceCreatePreviewDocumentResponseParams printSessionInstanceCreatePreviewDocumentResponseParams = new PrintSessionInstanceCreatePreviewDocumentResponseParams();
            printSessionInstanceCreatePreviewDocumentResponseParams.previewDocument = untypedHandle;
            printSessionInstanceCreatePreviewDocumentResponseParams.dataSize = l.longValue();
            this.mMessageReceiver.accept(printSessionInstanceCreatePreviewDocumentResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class PrintSessionInstanceOnPrintPreviewClosedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrintSessionInstanceOnPrintPreviewClosedParams() {
            super(8, 0);
        }

        public static PrintSessionInstanceOnPrintPreviewClosedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PrintSessionInstanceOnPrintPreviewClosedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PrintSessionInstanceOnPrintPreviewClosedParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PrintSessionInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PrintSessionInstance
        public void createPreviewDocument(PrintDocumentRequest printDocumentRequest, PrintSessionInstance.CreatePreviewDocumentResponse createPreviewDocumentResponse) {
            PrintSessionInstanceCreatePreviewDocumentParams printSessionInstanceCreatePreviewDocumentParams = new PrintSessionInstanceCreatePreviewDocumentParams();
            printSessionInstanceCreatePreviewDocumentParams.request = printDocumentRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(printSessionInstanceCreatePreviewDocumentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new PrintSessionInstanceCreatePreviewDocumentResponseParamsForwardToCallback(createPreviewDocumentResponse));
        }

        @Override // org.chromium.arc.mojom.PrintSessionInstance
        public void onPrintPreviewClosed() {
            getProxyHandler().getMessageReceiver().accept(new PrintSessionInstanceOnPrintPreviewClosedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<PrintSessionInstance> {
        Stub(Core core, PrintSessionInstance printSessionInstance) {
            super(core, printSessionInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PrintSessionInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                PrintSessionInstanceOnPrintPreviewClosedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onPrintPreviewClosed();
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
                int type = header.getType();
                if (type == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PrintSessionInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().createPreviewDocument(PrintSessionInstanceCreatePreviewDocumentParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new PrintSessionInstanceCreatePreviewDocumentResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class PrintSessionInstanceCreatePreviewDocumentResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long dataSize;
        public UntypedHandle previewDocument;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrintSessionInstanceCreatePreviewDocumentResponseParams() {
            super(24, 0);
            this.previewDocument = InvalidHandle.INSTANCE;
        }

        public static PrintSessionInstanceCreatePreviewDocumentResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PrintSessionInstanceCreatePreviewDocumentResponseParams printSessionInstanceCreatePreviewDocumentResponseParams = new PrintSessionInstanceCreatePreviewDocumentResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                printSessionInstanceCreatePreviewDocumentResponseParams.previewDocument = decoder.readUntypedHandle(8, true);
                printSessionInstanceCreatePreviewDocumentResponseParams.dataSize = decoder.readLong(16);
                return printSessionInstanceCreatePreviewDocumentResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.previewDocument, 8, true);
            encoderAtDataOffset.encode(this.dataSize, 16);
        }

        private PrintSessionInstanceCreatePreviewDocumentResponseParams(int i) {
            super(24, i);
            this.previewDocument = InvalidHandle.INSTANCE;
        }
    }
}

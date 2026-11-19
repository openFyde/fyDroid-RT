// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.PrintSpoolerHost;
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

class PrintSpoolerHost_Internal {
    public static final Interface.Manager<PrintSpoolerHost, PrintSpoolerHost.Proxy> MANAGER = new Interface.Manager<PrintSpoolerHost, PrintSpoolerHost.Proxy>() { // from class: org.chromium.arc.mojom.PrintSpoolerHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrintSpoolerHost[] buildArray(int i) {
            return new PrintSpoolerHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PrintSpoolerHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PrintSpoolerHost> buildStub(Core core, PrintSpoolerHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PrintSpoolerHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 4;
        }
    };

    static final class PrintSpoolerHostStartPrintInCustomTabResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PrintSessionHost host;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrintSpoolerHostStartPrintInCustomTabResponseParams() {
            super(16, 0);
        }

        public static PrintSpoolerHostStartPrintInCustomTabResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PrintSpoolerHostStartPrintInCustomTabResponseParams printSpoolerHostStartPrintInCustomTabResponseParams = new PrintSpoolerHostStartPrintInCustomTabResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                printSpoolerHostStartPrintInCustomTabResponseParams.host = (PrintSessionHost) decoder.readServiceInterface(8, true, PrintSessionHost.MANAGER);
                return printSpoolerHostStartPrintInCustomTabResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.host, 8, true, (Interface.Manager<PrintSessionHost, ?>) PrintSessionHost.MANAGER);
        }

        private PrintSpoolerHostStartPrintInCustomTabResponseParams(int i) {
            super(16, i);
        }
    }

    static class PrintSpoolerHostStartPrintInCustomTabResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PrintSpoolerHost.StartPrintInCustomTabResponse mCallback;

        PrintSpoolerHostStartPrintInCustomTabResponseParamsForwardToCallback(PrintSpoolerHost.StartPrintInCustomTabResponse startPrintInCustomTabResponse) {
            this.mCallback = startPrintInCustomTabResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(PrintSpoolerHostStartPrintInCustomTabResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).host);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PrintSpoolerHostStartPrintInCustomTabResponseParamsProxyToResponder implements PrintSpoolerHost.StartPrintInCustomTabResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PrintSpoolerHostStartPrintInCustomTabResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(PrintSessionHost printSessionHost) {
            PrintSpoolerHostStartPrintInCustomTabResponseParams printSpoolerHostStartPrintInCustomTabResponseParams = new PrintSpoolerHostStartPrintInCustomTabResponseParams();
            printSpoolerHostStartPrintInCustomTabResponseParams.host = printSessionHost;
            this.mMessageReceiver.accept(printSpoolerHostStartPrintInCustomTabResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PrintSpoolerHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PrintSpoolerHost
        public void startPrintInCustomTab(UntypedHandle untypedHandle, int i, PrintSessionInstance printSessionInstance, PrintSpoolerHost.StartPrintInCustomTabResponse startPrintInCustomTabResponse) {
            PrintSpoolerHostStartPrintInCustomTabParams printSpoolerHostStartPrintInCustomTabParams = new PrintSpoolerHostStartPrintInCustomTabParams();
            printSpoolerHostStartPrintInCustomTabParams.scopedHandle = untypedHandle;
            printSpoolerHostStartPrintInCustomTabParams.taskId = i;
            printSpoolerHostStartPrintInCustomTabParams.instance = printSessionInstance;
            getProxyHandler().getMessageReceiver().acceptWithResponder(printSpoolerHostStartPrintInCustomTabParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new PrintSpoolerHostStartPrintInCustomTabResponseParamsForwardToCallback(startPrintInCustomTabResponse));
        }
    }

    static final class Stub extends Interface.Stub<PrintSpoolerHost> {
        Stub(Core core, PrintSpoolerHost printSpoolerHost) {
            super(core, printSpoolerHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PrintSpoolerHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PrintSpoolerHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 1) {
                    return false;
                }
                PrintSpoolerHostStartPrintInCustomTabParams printSpoolerHostStartPrintInCustomTabParamsDeserialize = PrintSpoolerHostStartPrintInCustomTabParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().startPrintInCustomTab(printSpoolerHostStartPrintInCustomTabParamsDeserialize.scopedHandle, printSpoolerHostStartPrintInCustomTabParamsDeserialize.taskId, printSpoolerHostStartPrintInCustomTabParamsDeserialize.instance, new PrintSpoolerHostStartPrintInCustomTabResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class PrintSpoolerHostStartPrintInCustomTabParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PrintSessionInstance instance;
        public UntypedHandle scopedHandle;
        public int taskId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PrintSpoolerHostStartPrintInCustomTabParams() {
            super(24, 0);
            this.scopedHandle = InvalidHandle.INSTANCE;
        }

        public static PrintSpoolerHostStartPrintInCustomTabParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PrintSpoolerHostStartPrintInCustomTabParams printSpoolerHostStartPrintInCustomTabParams = new PrintSpoolerHostStartPrintInCustomTabParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                printSpoolerHostStartPrintInCustomTabParams.scopedHandle = decoder.readUntypedHandle(8, false);
                printSpoolerHostStartPrintInCustomTabParams.taskId = decoder.readInt(12);
                printSpoolerHostStartPrintInCustomTabParams.instance = (PrintSessionInstance) decoder.readServiceInterface(16, false, PrintSessionInstance.MANAGER);
                return printSpoolerHostStartPrintInCustomTabParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Handle) this.scopedHandle, 8, false);
            encoderAtDataOffset.encode(this.taskId, 12);
            encoderAtDataOffset.encode(this.instance, 16, false, (Interface.Manager<PrintSessionInstance, ?>) PrintSessionInstance.MANAGER);
        }

        private PrintSpoolerHostStartPrintInCustomTabParams(int i) {
            super(24, i);
            this.scopedHandle = InvalidHandle.INSTANCE;
        }
    }
}

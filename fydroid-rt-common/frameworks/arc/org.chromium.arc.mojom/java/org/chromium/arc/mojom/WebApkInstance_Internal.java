// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.WebApkInstance;
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

class WebApkInstance_Internal {
    public static final Interface.Manager<WebApkInstance, WebApkInstance.Proxy> MANAGER = new Interface.Manager<WebApkInstance, WebApkInstance.Proxy>() { // from class: org.chromium.arc.mojom.WebApkInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WebApkInstance[] buildArray(int i) {
            return new WebApkInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WebApkInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<WebApkInstance> buildStub(Core core, WebApkInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.WebApkInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements WebApkInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.WebApkInstance
        public void getWebApkInfo(String str, WebApkInstance.GetWebApkInfoResponse getWebApkInfoResponse) {
            WebApkInstanceGetWebApkInfoParams webApkInstanceGetWebApkInfoParams = new WebApkInstanceGetWebApkInfoParams();
            webApkInstanceGetWebApkInfoParams.packageName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(webApkInstanceGetWebApkInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new WebApkInstanceGetWebApkInfoResponseParamsForwardToCallback(getWebApkInfoResponse));
        }

        @Override // org.chromium.arc.mojom.WebApkInstance
        public void installWebApk(String str, int i, String str2, String str3, WebApkInstance.InstallWebApkResponse installWebApkResponse) {
            WebApkInstanceInstallWebApkParams webApkInstanceInstallWebApkParams = new WebApkInstanceInstallWebApkParams();
            webApkInstanceInstallWebApkParams.packageName = str;
            webApkInstanceInstallWebApkParams.version = i;
            webApkInstanceInstallWebApkParams.appName = str2;
            webApkInstanceInstallWebApkParams.token = str3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(webApkInstanceInstallWebApkParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new WebApkInstanceInstallWebApkResponseParamsForwardToCallback(installWebApkResponse));
        }
    }

    static final class Stub extends Interface.Stub<WebApkInstance> {
        Stub(Core core, WebApkInstance webApkInstance) {
            super(core, webApkInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(WebApkInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), WebApkInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    WebApkInstanceInstallWebApkParams webApkInstanceInstallWebApkParamsDeserialize = WebApkInstanceInstallWebApkParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().installWebApk(webApkInstanceInstallWebApkParamsDeserialize.packageName, webApkInstanceInstallWebApkParamsDeserialize.version, webApkInstanceInstallWebApkParamsDeserialize.appName, webApkInstanceInstallWebApkParamsDeserialize.token, new WebApkInstanceInstallWebApkResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().getWebApkInfo(WebApkInstanceGetWebApkInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName, new WebApkInstanceGetWebApkInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class WebApkInstanceGetWebApkInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WebApkInstanceGetWebApkInfoParams() {
            super(16, 0);
        }

        public static WebApkInstanceGetWebApkInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WebApkInstanceGetWebApkInfoParams webApkInstanceGetWebApkInfoParams = new WebApkInstanceGetWebApkInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                webApkInstanceGetWebApkInfoParams.packageName = decoder.readString(8, false);
                return webApkInstanceGetWebApkInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private WebApkInstanceGetWebApkInfoParams(int i) {
            super(16, i);
        }
    }

    static final class WebApkInstanceGetWebApkInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WebApkInfo result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WebApkInstanceGetWebApkInfoResponseParams() {
            super(16, 0);
        }

        public static WebApkInstanceGetWebApkInfoResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WebApkInstanceGetWebApkInfoResponseParams webApkInstanceGetWebApkInfoResponseParams = new WebApkInstanceGetWebApkInfoResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                webApkInstanceGetWebApkInfoResponseParams.result = WebApkInfo.decode(decoder.readPointer(8, true));
                return webApkInstanceGetWebApkInfoResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, true);
        }

        private WebApkInstanceGetWebApkInfoResponseParams(int i) {
            super(16, i);
        }
    }

    static class WebApkInstanceGetWebApkInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final WebApkInstance.GetWebApkInfoResponse mCallback;

        WebApkInstanceGetWebApkInfoResponseParamsForwardToCallback(WebApkInstance.GetWebApkInfoResponse getWebApkInfoResponse) {
            this.mCallback = getWebApkInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(WebApkInstanceGetWebApkInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class WebApkInstanceGetWebApkInfoResponseParamsProxyToResponder implements WebApkInstance.GetWebApkInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        WebApkInstanceGetWebApkInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(WebApkInfo webApkInfo) {
            WebApkInstanceGetWebApkInfoResponseParams webApkInstanceGetWebApkInfoResponseParams = new WebApkInstanceGetWebApkInfoResponseParams();
            webApkInstanceGetWebApkInfoResponseParams.result = webApkInfo;
            this.mMessageReceiver.accept(webApkInstanceGetWebApkInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class WebApkInstanceInstallWebApkParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String appName;
        public String packageName;
        public String token;
        public int version;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WebApkInstanceInstallWebApkParams() {
            super(40, 0);
        }

        public static WebApkInstanceInstallWebApkParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WebApkInstanceInstallWebApkParams webApkInstanceInstallWebApkParams = new WebApkInstanceInstallWebApkParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                webApkInstanceInstallWebApkParams.packageName = decoder.readString(8, false);
                webApkInstanceInstallWebApkParams.version = decoder.readInt(16);
                webApkInstanceInstallWebApkParams.appName = decoder.readString(24, false);
                webApkInstanceInstallWebApkParams.token = decoder.readString(32, false);
                return webApkInstanceInstallWebApkParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.version, 16);
            encoderAtDataOffset.encode(this.appName, 24, false);
            encoderAtDataOffset.encode(this.token, 32, false);
        }

        private WebApkInstanceInstallWebApkParams(int i) {
            super(40, i);
        }
    }

    static final class WebApkInstanceInstallWebApkResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WebApkInstanceInstallWebApkResponseParams() {
            super(16, 0);
        }

        public static WebApkInstanceInstallWebApkResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WebApkInstanceInstallWebApkResponseParams webApkInstanceInstallWebApkResponseParams = new WebApkInstanceInstallWebApkResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                webApkInstanceInstallWebApkResponseParams.result = i;
                WebApkInstallResult.validate(i);
                return webApkInstanceInstallWebApkResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private WebApkInstanceInstallWebApkResponseParams(int i) {
            super(16, i);
        }
    }

    static class WebApkInstanceInstallWebApkResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final WebApkInstance.InstallWebApkResponse mCallback;

        WebApkInstanceInstallWebApkResponseParamsForwardToCallback(WebApkInstance.InstallWebApkResponse installWebApkResponse) {
            this.mCallback = installWebApkResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(WebApkInstanceInstallWebApkResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class WebApkInstanceInstallWebApkResponseParamsProxyToResponder implements WebApkInstance.InstallWebApkResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        WebApkInstanceInstallWebApkResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            WebApkInstanceInstallWebApkResponseParams webApkInstanceInstallWebApkResponseParams = new WebApkInstanceInstallWebApkResponseParams();
            webApkInstanceInstallWebApkResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(webApkInstanceInstallWebApkResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }
}

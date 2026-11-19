// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.TimerHost;
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
import org.chromium.mojo_base.mojom.Time;
import org.chromium.mojo_base.mojom.TimeTicks;

class TimerHost_Internal {
    public static final Interface.Manager<TimerHost, TimerHost.Proxy> MANAGER = new Interface.Manager<TimerHost, TimerHost.Proxy>() { // from class: org.chromium.arc.mojom.TimerHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TimerHost[] buildArray(int i) {
            return new TimerHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TimerHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<TimerHost> buildStub(Core core, TimerHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.TimerHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements TimerHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.TimerHost
        public void createTimers(CreateTimerRequest[] createTimerRequestArr, TimerHost.CreateTimersResponse createTimersResponse) {
            TimerHostCreateTimersParams timerHostCreateTimersParams = new TimerHostCreateTimersParams();
            timerHostCreateTimersParams.timerRequests = createTimerRequestArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(timerHostCreateTimersParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new TimerHostCreateTimersResponseParamsForwardToCallback(createTimersResponse));
        }

        @Override // org.chromium.arc.mojom.TimerHost
        public void setTime(Time time, TimerHost.SetTimeResponse setTimeResponse) {
            TimerHostSetTimeParams timerHostSetTimeParams = new TimerHostSetTimeParams();
            timerHostSetTimeParams.time = time;
            getProxyHandler().getMessageReceiver().acceptWithResponder(timerHostSetTimeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new TimerHostSetTimeResponseParamsForwardToCallback(setTimeResponse));
        }

        @Override // org.chromium.arc.mojom.TimerHost
        public void startTimer(int i, TimeTicks timeTicks, TimerHost.StartTimerResponse startTimerResponse) {
            TimerHostStartTimerParams timerHostStartTimerParams = new TimerHostStartTimerParams();
            timerHostStartTimerParams.clockId = i;
            timerHostStartTimerParams.absoluteExpirationTime = timeTicks;
            getProxyHandler().getMessageReceiver().acceptWithResponder(timerHostStartTimerParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new TimerHostStartTimerResponseParamsForwardToCallback(startTimerResponse));
        }
    }

    static final class Stub extends Interface.Stub<TimerHost> {
        Stub(Core core, TimerHost timerHost) {
            super(core, timerHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(TimerHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), TimerHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().createTimers(TimerHostCreateTimersParams.deserialize(serviceMessageAsServiceMessage.getPayload()).timerRequests, new TimerHostCreateTimersResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    TimerHostStartTimerParams timerHostStartTimerParamsDeserialize = TimerHostStartTimerParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().startTimer(timerHostStartTimerParamsDeserialize.clockId, timerHostStartTimerParamsDeserialize.absoluteExpirationTime, new TimerHostStartTimerResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().setTime(TimerHostSetTimeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).time, new TimerHostSetTimeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class TimerHostCreateTimersParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CreateTimerRequest[] timerRequests;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TimerHostCreateTimersParams() {
            super(16, 0);
        }

        public static TimerHostCreateTimersParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                TimerHostCreateTimersParams timerHostCreateTimersParams = new TimerHostCreateTimersParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                timerHostCreateTimersParams.timerRequests = new CreateTimerRequest[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    timerHostCreateTimersParams.timerRequests[i] = CreateTimerRequest.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return timerHostCreateTimersParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            CreateTimerRequest[] createTimerRequestArr = this.timerRequests;
            if (createTimerRequestArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(createTimerRequestArr.length, 8, -1);
            int i = 0;
            while (true) {
                CreateTimerRequest[] createTimerRequestArr2 = this.timerRequests;
                if (i >= createTimerRequestArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) createTimerRequestArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private TimerHostCreateTimersParams(int i) {
            super(16, i);
        }
    }

    static final class TimerHostCreateTimersResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TimerHostCreateTimersResponseParams() {
            super(16, 0);
        }

        public static TimerHostCreateTimersResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TimerHostCreateTimersResponseParams timerHostCreateTimersResponseParams = new TimerHostCreateTimersResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                timerHostCreateTimersResponseParams.result = i;
                ArcTimerResult.validate(i);
                return timerHostCreateTimersResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private TimerHostCreateTimersResponseParams(int i) {
            super(16, i);
        }
    }

    static class TimerHostCreateTimersResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final TimerHost.CreateTimersResponse mCallback;

        TimerHostCreateTimersResponseParamsForwardToCallback(TimerHost.CreateTimersResponse createTimersResponse) {
            this.mCallback = createTimersResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(TimerHostCreateTimersResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class TimerHostCreateTimersResponseParamsProxyToResponder implements TimerHost.CreateTimersResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TimerHostCreateTimersResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            TimerHostCreateTimersResponseParams timerHostCreateTimersResponseParams = new TimerHostCreateTimersResponseParams();
            timerHostCreateTimersResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(timerHostCreateTimersResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class TimerHostSetTimeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Time time;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TimerHostSetTimeParams() {
            super(16, 0);
        }

        public static TimerHostSetTimeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TimerHostSetTimeParams timerHostSetTimeParams = new TimerHostSetTimeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                timerHostSetTimeParams.time = Time.decode(decoder.readPointer(8, false));
                return timerHostSetTimeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.time, 8, false);
        }

        private TimerHostSetTimeParams(int i) {
            super(16, i);
        }
    }

    static final class TimerHostSetTimeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TimerHostSetTimeResponseParams() {
            super(16, 0);
        }

        public static TimerHostSetTimeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TimerHostSetTimeResponseParams timerHostSetTimeResponseParams = new TimerHostSetTimeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                timerHostSetTimeResponseParams.result = i;
                ArcTimerResult.validate(i);
                return timerHostSetTimeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private TimerHostSetTimeResponseParams(int i) {
            super(16, i);
        }
    }

    static class TimerHostSetTimeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final TimerHost.SetTimeResponse mCallback;

        TimerHostSetTimeResponseParamsForwardToCallback(TimerHost.SetTimeResponse setTimeResponse) {
            this.mCallback = setTimeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(TimerHostSetTimeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class TimerHostSetTimeResponseParamsProxyToResponder implements TimerHost.SetTimeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TimerHostSetTimeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            TimerHostSetTimeResponseParams timerHostSetTimeResponseParams = new TimerHostSetTimeResponseParams();
            timerHostSetTimeResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(timerHostSetTimeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class TimerHostStartTimerParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeTicks absoluteExpirationTime;
        public int clockId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TimerHostStartTimerParams() {
            super(24, 0);
        }

        public static TimerHostStartTimerParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TimerHostStartTimerParams timerHostStartTimerParams = new TimerHostStartTimerParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                timerHostStartTimerParams.clockId = i;
                ClockId.validate(i);
                timerHostStartTimerParams.absoluteExpirationTime = TimeTicks.decode(decoder.readPointer(16, false));
                return timerHostStartTimerParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.clockId, 8);
            encoderAtDataOffset.encode((Struct) this.absoluteExpirationTime, 16, false);
        }

        private TimerHostStartTimerParams(int i) {
            super(24, i);
        }
    }

    static final class TimerHostStartTimerResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TimerHostStartTimerResponseParams() {
            super(16, 0);
        }

        public static TimerHostStartTimerResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TimerHostStartTimerResponseParams timerHostStartTimerResponseParams = new TimerHostStartTimerResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                timerHostStartTimerResponseParams.result = i;
                ArcTimerResult.validate(i);
                return timerHostStartTimerResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private TimerHostStartTimerResponseParams(int i) {
            super(16, i);
        }
    }

    static class TimerHostStartTimerResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final TimerHost.StartTimerResponse mCallback;

        TimerHostStartTimerResponseParamsForwardToCallback(TimerHost.StartTimerResponse startTimerResponse) {
            this.mCallback = startTimerResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(TimerHostStartTimerResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class TimerHostStartTimerResponseParamsProxyToResponder implements TimerHost.StartTimerResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TimerHostStartTimerResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            TimerHostStartTimerResponseParams timerHostStartTimerResponseParams = new TimerHostStartTimerResponseParams();
            timerHostStartTimerResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(timerHostStartTimerResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }
}

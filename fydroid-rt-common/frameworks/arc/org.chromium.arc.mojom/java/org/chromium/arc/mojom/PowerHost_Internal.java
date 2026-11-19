// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.PowerHost;
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

class PowerHost_Internal {
    public static final Interface.Manager<PowerHost, PowerHost.Proxy> MANAGER = new Interface.Manager<PowerHost, PowerHost.Proxy>() { // from class: org.chromium.arc.mojom.PowerHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PowerHost[] buildArray(int i) {
            return new PowerHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PowerHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PowerHost> buildStub(Core core, PowerHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PowerHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 9;
        }
    };

    static final class PowerHostGetBatterySaverModeStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostGetBatterySaverModeStateParams() {
            super(8, 0);
        }

        public static PowerHostGetBatterySaverModeStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PowerHostGetBatterySaverModeStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PowerHostGetBatterySaverModeStateParams(int i) {
            super(8, i);
        }
    }

    static final class PowerHostGetBatterySaverModeStateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BatterySaverModeState state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostGetBatterySaverModeStateResponseParams() {
            super(16, 0);
        }

        public static PowerHostGetBatterySaverModeStateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostGetBatterySaverModeStateResponseParams powerHostGetBatterySaverModeStateResponseParams = new PowerHostGetBatterySaverModeStateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                powerHostGetBatterySaverModeStateResponseParams.state = BatterySaverModeState.decode(decoder.readPointer(8, false));
                return powerHostGetBatterySaverModeStateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.state, 8, false);
        }

        private PowerHostGetBatterySaverModeStateResponseParams(int i) {
            super(16, i);
        }
    }

    static class PowerHostGetBatterySaverModeStateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PowerHost.GetBatterySaverModeStateResponse mCallback;

        PowerHostGetBatterySaverModeStateResponseParamsForwardToCallback(PowerHost.GetBatterySaverModeStateResponse getBatterySaverModeStateResponse) {
            this.mCallback = getBatterySaverModeStateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(PowerHostGetBatterySaverModeStateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PowerHostGetBatterySaverModeStateResponseParamsProxyToResponder implements PowerHost.GetBatterySaverModeStateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PowerHostGetBatterySaverModeStateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(BatterySaverModeState batterySaverModeState) {
            PowerHostGetBatterySaverModeStateResponseParams powerHostGetBatterySaverModeStateResponseParams = new PowerHostGetBatterySaverModeStateResponseParams();
            powerHostGetBatterySaverModeStateResponseParams.state = batterySaverModeState;
            this.mMessageReceiver.accept(powerHostGetBatterySaverModeStateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class PowerHostIsDisplayOnParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostIsDisplayOnParams() {
            super(8, 0);
        }

        public static PowerHostIsDisplayOnParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PowerHostIsDisplayOnParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PowerHostIsDisplayOnParams(int i) {
            super(8, i);
        }
    }

    static final class PowerHostIsDisplayOnResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isOn;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostIsDisplayOnResponseParams() {
            super(16, 0);
        }

        public static PowerHostIsDisplayOnResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostIsDisplayOnResponseParams powerHostIsDisplayOnResponseParams = new PowerHostIsDisplayOnResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                powerHostIsDisplayOnResponseParams.isOn = decoder.readBoolean(8, 0);
                return powerHostIsDisplayOnResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isOn, 8, 0);
        }

        private PowerHostIsDisplayOnResponseParams(int i) {
            super(16, i);
        }
    }

    static class PowerHostIsDisplayOnResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PowerHost.IsDisplayOnResponse mCallback;

        PowerHostIsDisplayOnResponseParamsForwardToCallback(PowerHost.IsDisplayOnResponse isDisplayOnResponse) {
            this.mCallback = isDisplayOnResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(PowerHostIsDisplayOnResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isOn));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PowerHostIsDisplayOnResponseParamsProxyToResponder implements PowerHost.IsDisplayOnResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PowerHostIsDisplayOnResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            PowerHostIsDisplayOnResponseParams powerHostIsDisplayOnResponseParams = new PowerHostIsDisplayOnResponseParams();
            powerHostIsDisplayOnResponseParams.isOn = bool.booleanValue();
            this.mMessageReceiver.accept(powerHostIsDisplayOnResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class PowerHostOnAcquireDisplayWakeLockParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostOnAcquireDisplayWakeLockParams() {
            super(16, 0);
        }

        public static PowerHostOnAcquireDisplayWakeLockParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostOnAcquireDisplayWakeLockParams powerHostOnAcquireDisplayWakeLockParams = new PowerHostOnAcquireDisplayWakeLockParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerHostOnAcquireDisplayWakeLockParams.type = i;
                DisplayWakeLockType.validate(i);
                return powerHostOnAcquireDisplayWakeLockParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private PowerHostOnAcquireDisplayWakeLockParams(int i) {
            super(16, i);
        }
    }

    static final class PowerHostOnAnrRecoveryFailedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostOnAnrRecoveryFailedParams() {
            super(16, 0);
        }

        public static PowerHostOnAnrRecoveryFailedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostOnAnrRecoveryFailedParams powerHostOnAnrRecoveryFailedParams = new PowerHostOnAnrRecoveryFailedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerHostOnAnrRecoveryFailedParams.type = i;
                AnrType.validate(i);
                return powerHostOnAnrRecoveryFailedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private PowerHostOnAnrRecoveryFailedParams(int i) {
            super(16, i);
        }
    }

    static final class PowerHostOnPreAnrParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostOnPreAnrParams() {
            super(16, 0);
        }

        public static PowerHostOnPreAnrParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostOnPreAnrParams powerHostOnPreAnrParams = new PowerHostOnPreAnrParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerHostOnPreAnrParams.type = i;
                AnrType.validate(i);
                return powerHostOnPreAnrParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private PowerHostOnPreAnrParams(int i) {
            super(16, i);
        }
    }

    static final class PowerHostOnReleaseDisplayWakeLockParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostOnReleaseDisplayWakeLockParams() {
            super(16, 0);
        }

        public static PowerHostOnReleaseDisplayWakeLockParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostOnReleaseDisplayWakeLockParams powerHostOnReleaseDisplayWakeLockParams = new PowerHostOnReleaseDisplayWakeLockParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerHostOnReleaseDisplayWakeLockParams.type = i;
                DisplayWakeLockType.validate(i);
                return powerHostOnReleaseDisplayWakeLockParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private PowerHostOnReleaseDisplayWakeLockParams(int i) {
            super(16, i);
        }
    }

    static final class PowerHostOnScreenBrightnessUpdateRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public double percent;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostOnScreenBrightnessUpdateRequestParams() {
            super(16, 0);
        }

        public static PowerHostOnScreenBrightnessUpdateRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostOnScreenBrightnessUpdateRequestParams powerHostOnScreenBrightnessUpdateRequestParams = new PowerHostOnScreenBrightnessUpdateRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                powerHostOnScreenBrightnessUpdateRequestParams.percent = decoder.readDouble(8);
                return powerHostOnScreenBrightnessUpdateRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.percent, 8);
        }

        private PowerHostOnScreenBrightnessUpdateRequestParams(int i) {
            super(16, i);
        }
    }

    static final class PowerHostOnWakefulnessChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int mode;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerHostOnWakefulnessChangedParams() {
            super(16, 0);
        }

        public static PowerHostOnWakefulnessChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerHostOnWakefulnessChangedParams powerHostOnWakefulnessChangedParams = new PowerHostOnWakefulnessChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerHostOnWakefulnessChangedParams.mode = i;
                WakefulnessMode.validate(i);
                return powerHostOnWakefulnessChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.mode, 8);
        }

        private PowerHostOnWakefulnessChangedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PowerHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void getBatterySaverModeState(PowerHost.GetBatterySaverModeStateResponse getBatterySaverModeStateResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new PowerHostGetBatterySaverModeStateParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new PowerHostGetBatterySaverModeStateResponseParamsForwardToCallback(getBatterySaverModeStateResponse));
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void isDisplayOn(PowerHost.IsDisplayOnResponse isDisplayOnResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new PowerHostIsDisplayOnParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new PowerHostIsDisplayOnResponseParamsForwardToCallback(isDisplayOnResponse));
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void onAcquireDisplayWakeLock(int i) {
            PowerHostOnAcquireDisplayWakeLockParams powerHostOnAcquireDisplayWakeLockParams = new PowerHostOnAcquireDisplayWakeLockParams();
            powerHostOnAcquireDisplayWakeLockParams.type = i;
            getProxyHandler().getMessageReceiver().accept(powerHostOnAcquireDisplayWakeLockParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void onAnrRecoveryFailed(int i) {
            PowerHostOnAnrRecoveryFailedParams powerHostOnAnrRecoveryFailedParams = new PowerHostOnAnrRecoveryFailedParams();
            powerHostOnAnrRecoveryFailedParams.type = i;
            getProxyHandler().getMessageReceiver().accept(powerHostOnAnrRecoveryFailedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void onPreAnr(int i) {
            PowerHostOnPreAnrParams powerHostOnPreAnrParams = new PowerHostOnPreAnrParams();
            powerHostOnPreAnrParams.type = i;
            getProxyHandler().getMessageReceiver().accept(powerHostOnPreAnrParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void onReleaseDisplayWakeLock(int i) {
            PowerHostOnReleaseDisplayWakeLockParams powerHostOnReleaseDisplayWakeLockParams = new PowerHostOnReleaseDisplayWakeLockParams();
            powerHostOnReleaseDisplayWakeLockParams.type = i;
            getProxyHandler().getMessageReceiver().accept(powerHostOnReleaseDisplayWakeLockParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void onScreenBrightnessUpdateRequest(double d) {
            PowerHostOnScreenBrightnessUpdateRequestParams powerHostOnScreenBrightnessUpdateRequestParams = new PowerHostOnScreenBrightnessUpdateRequestParams();
            powerHostOnScreenBrightnessUpdateRequestParams.percent = d;
            getProxyHandler().getMessageReceiver().accept(powerHostOnScreenBrightnessUpdateRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.PowerHost
        public void onWakefulnessChanged(int i) {
            PowerHostOnWakefulnessChangedParams powerHostOnWakefulnessChangedParams = new PowerHostOnWakefulnessChangedParams();
            powerHostOnWakefulnessChangedParams.mode = i;
            getProxyHandler().getMessageReceiver().accept(powerHostOnWakefulnessChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }
    }

    static final class Stub extends Interface.Stub<PowerHost> {
        Stub(Core core, PowerHost powerHost) {
            super(core, powerHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PowerHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 3) {
                    getImpl().onScreenBrightnessUpdateRequest(PowerHostOnScreenBrightnessUpdateRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload()).percent);
                    return true;
                }
                if (type == 0) {
                    getImpl().onAcquireDisplayWakeLock(PowerHostOnAcquireDisplayWakeLockParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type);
                    return true;
                }
                if (type == 1) {
                    getImpl().onReleaseDisplayWakeLock(PowerHostOnReleaseDisplayWakeLockParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type);
                    return true;
                }
                if (type == 5) {
                    getImpl().onWakefulnessChanged(PowerHostOnWakefulnessChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).mode);
                    return true;
                }
                if (type == 6) {
                    getImpl().onPreAnr(PowerHostOnPreAnrParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type);
                    return true;
                }
                if (type != 7) {
                    return false;
                }
                getImpl().onAnrRecoveryFailed(PowerHostOnAnrRecoveryFailedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PowerHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 2) {
                    PowerHostIsDisplayOnParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().isDisplayOn(new PowerHostIsDisplayOnResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 8) {
                    return false;
                }
                PowerHostGetBatterySaverModeStateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getBatterySaverModeState(new PowerHostGetBatterySaverModeStateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

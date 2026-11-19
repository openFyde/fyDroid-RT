// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.PowerInstance;
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

class PowerInstance_Internal {
    public static final Interface.Manager<PowerInstance, PowerInstance.Proxy> MANAGER = new Interface.Manager<PowerInstance, PowerInstance.Proxy>() { // from class: org.chromium.arc.mojom.PowerInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PowerInstance[] buildArray(int i) {
            return new PowerInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PowerInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PowerInstance> buildStub(Core core, PowerInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PowerInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 10;
        }
    };

    static final class PowerInstanceGetWakefulnessModeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceGetWakefulnessModeParams() {
            super(8, 0);
        }

        public static PowerInstanceGetWakefulnessModeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PowerInstanceGetWakefulnessModeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PowerInstanceGetWakefulnessModeParams(int i) {
            super(8, i);
        }
    }

    static final class PowerInstanceGetWakefulnessModeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int mode;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceGetWakefulnessModeResponseParams() {
            super(16, 0);
        }

        public static PowerInstanceGetWakefulnessModeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerInstanceGetWakefulnessModeResponseParams powerInstanceGetWakefulnessModeResponseParams = new PowerInstanceGetWakefulnessModeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerInstanceGetWakefulnessModeResponseParams.mode = i;
                WakefulnessMode.validate(i);
                return powerInstanceGetWakefulnessModeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.mode, 8);
        }

        private PowerInstanceGetWakefulnessModeResponseParams(int i) {
            super(16, i);
        }
    }

    static class PowerInstanceGetWakefulnessModeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PowerInstance.GetWakefulnessModeResponse mCallback;

        PowerInstanceGetWakefulnessModeResponseParamsForwardToCallback(PowerInstance.GetWakefulnessModeResponse getWakefulnessModeResponse) {
            this.mCallback = getWakefulnessModeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(PowerInstanceGetWakefulnessModeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).mode));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PowerInstanceGetWakefulnessModeResponseParamsProxyToResponder implements PowerInstance.GetWakefulnessModeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PowerInstanceGetWakefulnessModeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            PowerInstanceGetWakefulnessModeResponseParams powerInstanceGetWakefulnessModeResponseParams = new PowerInstanceGetWakefulnessModeResponseParams();
            powerInstanceGetWakefulnessModeResponseParams.mode = num.intValue();
            this.mMessageReceiver.accept(powerInstanceGetWakefulnessModeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class PowerInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PowerHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceInitParams() {
            super(16, 0);
        }

        public static PowerInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerInstanceInitParams powerInstanceInitParams = new PowerInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                powerInstanceInitParams.hostRemote = (PowerHost) decoder.readServiceInterface(8, false, PowerHost.MANAGER);
                return powerInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<PowerHost, ?>) PowerHost.MANAGER);
        }

        private PowerInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class PowerInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class PowerInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PowerInstance.InitResponse mCallback;

        PowerInstanceInitResponseParamsForwardToCallback(PowerInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PowerInstanceInitResponseParamsProxyToResponder implements PowerInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PowerInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new PowerInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class PowerInstanceOnBatterySaverModeStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BatterySaverModeState state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceOnBatterySaverModeStateChangedParams() {
            super(16, 0);
        }

        public static PowerInstanceOnBatterySaverModeStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerInstanceOnBatterySaverModeStateChangedParams powerInstanceOnBatterySaverModeStateChangedParams = new PowerInstanceOnBatterySaverModeStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                powerInstanceOnBatterySaverModeStateChangedParams.state = BatterySaverModeState.decode(decoder.readPointer(8, false));
                return powerInstanceOnBatterySaverModeStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.state, 8, false);
        }

        private PowerInstanceOnBatterySaverModeStateChangedParams(int i) {
            super(16, i);
        }
    }

    static final class PowerInstanceOnCpuRestrictionChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceOnCpuRestrictionChangedParams() {
            super(16, 0);
        }

        public static PowerInstanceOnCpuRestrictionChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerInstanceOnCpuRestrictionChangedParams powerInstanceOnCpuRestrictionChangedParams = new PowerInstanceOnCpuRestrictionChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerInstanceOnCpuRestrictionChangedParams.state = i;
                CpuRestrictionState.validate(i);
                return powerInstanceOnCpuRestrictionChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private PowerInstanceOnCpuRestrictionChangedParams(int i) {
            super(16, i);
        }
    }

    static final class PowerInstancePowerSupplyInfoChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstancePowerSupplyInfoChangedParams() {
            super(8, 0);
        }

        public static PowerInstancePowerSupplyInfoChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PowerInstancePowerSupplyInfoChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PowerInstancePowerSupplyInfoChangedParams(int i) {
            super(8, i);
        }
    }

    static final class PowerInstanceResumeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceResumeParams() {
            super(8, 0);
        }

        public static PowerInstanceResumeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PowerInstanceResumeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PowerInstanceResumeParams(int i) {
            super(8, i);
        }
    }

    static final class PowerInstanceSetIdleStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceSetIdleStateParams() {
            super(16, 0);
        }

        public static PowerInstanceSetIdleStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerInstanceSetIdleStateParams powerInstanceSetIdleStateParams = new PowerInstanceSetIdleStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                powerInstanceSetIdleStateParams.state = i;
                IdleState.validate(i);
                return powerInstanceSetIdleStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private PowerInstanceSetIdleStateParams(int i) {
            super(16, i);
        }
    }

    static final class PowerInstanceSetInteractiveDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceSetInteractiveDeprecatedParams() {
            super(16, 0);
        }

        public static PowerInstanceSetInteractiveDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerInstanceSetInteractiveDeprecatedParams powerInstanceSetInteractiveDeprecatedParams = new PowerInstanceSetInteractiveDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                powerInstanceSetInteractiveDeprecatedParams.enabled = decoder.readBoolean(8, 0);
                return powerInstanceSetInteractiveDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private PowerInstanceSetInteractiveDeprecatedParams(int i) {
            super(16, i);
        }
    }

    static final class PowerInstanceSuspendParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceSuspendParams() {
            super(8, 0);
        }

        public static PowerInstanceSuspendParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PowerInstanceSuspendParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PowerInstanceSuspendParams(int i) {
            super(8, i);
        }
    }

    static final class PowerInstanceSuspendResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceSuspendResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class PowerInstanceSuspendResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PowerInstance.SuspendResponse mCallback;

        PowerInstanceSuspendResponseParamsForwardToCallback(PowerInstance.SuspendResponse suspendResponse) {
            this.mCallback = suspendResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PowerInstanceSuspendResponseParamsProxyToResponder implements PowerInstance.SuspendResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PowerInstanceSuspendResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new PowerInstanceSuspendResponseParams().serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class PowerInstanceUpdateScreenBrightnessSettingsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public double percent;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PowerInstanceUpdateScreenBrightnessSettingsParams() {
            super(16, 0);
        }

        public static PowerInstanceUpdateScreenBrightnessSettingsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PowerInstanceUpdateScreenBrightnessSettingsParams powerInstanceUpdateScreenBrightnessSettingsParams = new PowerInstanceUpdateScreenBrightnessSettingsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                powerInstanceUpdateScreenBrightnessSettingsParams.percent = decoder.readDouble(8);
                return powerInstanceUpdateScreenBrightnessSettingsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.percent, 8);
        }

        private PowerInstanceUpdateScreenBrightnessSettingsParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PowerInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void getWakefulnessMode(PowerInstance.GetWakefulnessModeResponse getWakefulnessModeResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new PowerInstanceGetWakefulnessModeParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new PowerInstanceGetWakefulnessModeResponseParamsForwardToCallback(getWakefulnessModeResponse));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void init(PowerHost powerHost, PowerInstance.InitResponse initResponse) {
            PowerInstanceInitParams powerInstanceInitParams = new PowerInstanceInitParams();
            powerInstanceInitParams.hostRemote = powerHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(powerInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new PowerInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void onBatterySaverModeStateChanged(BatterySaverModeState batterySaverModeState) {
            PowerInstanceOnBatterySaverModeStateChangedParams powerInstanceOnBatterySaverModeStateChangedParams = new PowerInstanceOnBatterySaverModeStateChangedParams();
            powerInstanceOnBatterySaverModeStateChangedParams.state = batterySaverModeState;
            getProxyHandler().getMessageReceiver().accept(powerInstanceOnBatterySaverModeStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void onCpuRestrictionChanged(int i) {
            PowerInstanceOnCpuRestrictionChangedParams powerInstanceOnCpuRestrictionChangedParams = new PowerInstanceOnCpuRestrictionChangedParams();
            powerInstanceOnCpuRestrictionChangedParams.state = i;
            getProxyHandler().getMessageReceiver().accept(powerInstanceOnCpuRestrictionChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void powerSupplyInfoChanged() {
            getProxyHandler().getMessageReceiver().accept(new PowerInstancePowerSupplyInfoChangedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void resume() {
            getProxyHandler().getMessageReceiver().accept(new PowerInstanceResumeParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void setIdleState(int i) {
            PowerInstanceSetIdleStateParams powerInstanceSetIdleStateParams = new PowerInstanceSetIdleStateParams();
            powerInstanceSetIdleStateParams.state = i;
            getProxyHandler().getMessageReceiver().accept(powerInstanceSetIdleStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void setInteractiveDeprecated(boolean z) {
            PowerInstanceSetInteractiveDeprecatedParams powerInstanceSetInteractiveDeprecatedParams = new PowerInstanceSetInteractiveDeprecatedParams();
            powerInstanceSetInteractiveDeprecatedParams.enabled = z;
            getProxyHandler().getMessageReceiver().accept(powerInstanceSetInteractiveDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void suspend(PowerInstance.SuspendResponse suspendResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new PowerInstanceSuspendParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new PowerInstanceSuspendResponseParamsForwardToCallback(suspendResponse));
        }

        @Override // org.chromium.arc.mojom.PowerInstance
        public void updateScreenBrightnessSettings(double d) {
            PowerInstanceUpdateScreenBrightnessSettingsParams powerInstanceUpdateScreenBrightnessSettingsParams = new PowerInstanceUpdateScreenBrightnessSettingsParams();
            powerInstanceUpdateScreenBrightnessSettingsParams.percent = d;
            getProxyHandler().getMessageReceiver().accept(powerInstanceUpdateScreenBrightnessSettingsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }
    }

    static final class Stub extends Interface.Stub<PowerInstance> {
        Stub(Core core, PowerInstance powerInstance) {
            super(core, powerInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PowerInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    getImpl().setInteractiveDeprecated(PowerInstanceSetInteractiveDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled);
                    return true;
                }
                if (type == 6) {
                    PowerInstancePowerSupplyInfoChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().powerSupplyInfoChanged();
                    return true;
                }
                if (type == 3) {
                    PowerInstanceResumeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().resume();
                    return true;
                }
                if (type == 4) {
                    getImpl().updateScreenBrightnessSettings(PowerInstanceUpdateScreenBrightnessSettingsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).percent);
                    return true;
                }
                if (type == 8) {
                    getImpl().onCpuRestrictionChanged(PowerInstanceOnCpuRestrictionChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                    return true;
                }
                if (type == 9) {
                    getImpl().onBatterySaverModeStateChanged(PowerInstanceOnBatterySaverModeStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                    return true;
                }
                if (type == 10) {
                    getImpl().setIdleState(PowerInstanceSetIdleStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                    return true;
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PowerInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 2) {
                    PowerInstanceSuspendParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().suspend(new PowerInstanceSuspendResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 5) {
                    getImpl().init(PowerInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new PowerInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 7) {
                    return false;
                }
                PowerInstanceGetWakefulnessModeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getWakefulnessMode(new PowerInstanceGetWakefulnessModeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

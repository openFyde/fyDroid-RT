package org.chromium.chromeos.sensors.mojom;

import java.util.HashMap;
import java.util.Map;
import org.chromium.chromeos.sensors.mojom.SensorDeviceSamplesObserver;
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
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class SensorDeviceSamplesObserver_Internal {
    public static final Interface.Manager<SensorDeviceSamplesObserver, SensorDeviceSamplesObserver.Proxy> MANAGER = new Interface.Manager<SensorDeviceSamplesObserver, SensorDeviceSamplesObserver.Proxy>() { // from class: org.chromium.chromeos.sensors.mojom.SensorDeviceSamplesObserver_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorDeviceSamplesObserver[] buildArray(int i) {
            return new SensorDeviceSamplesObserver[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorDeviceSamplesObserver.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SensorDeviceSamplesObserver> buildStub(Core core, SensorDeviceSamplesObserver r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.sensors.mojom.SensorDeviceSamplesObserver";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SensorDeviceSamplesObserver.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDeviceSamplesObserver
        public void onErrorOccurred(int i) {
            SensorDeviceSamplesObserverOnErrorOccurredParams sensorDeviceSamplesObserverOnErrorOccurredParams = new SensorDeviceSamplesObserverOnErrorOccurredParams();
            sensorDeviceSamplesObserverOnErrorOccurredParams.type = i;
            getProxyHandler().getMessageReceiver().accept(sensorDeviceSamplesObserverOnErrorOccurredParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDeviceSamplesObserver
        public void onSampleUpdated(Map<Integer, Long> map) {
            SensorDeviceSamplesObserverOnSampleUpdatedParams sensorDeviceSamplesObserverOnSampleUpdatedParams = new SensorDeviceSamplesObserverOnSampleUpdatedParams();
            sensorDeviceSamplesObserverOnSampleUpdatedParams.sample = map;
            getProxyHandler().getMessageReceiver().accept(sensorDeviceSamplesObserverOnSampleUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class SensorDeviceSamplesObserverOnErrorOccurredParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceSamplesObserverOnErrorOccurredParams() {
            super(16, 0);
        }

        public static SensorDeviceSamplesObserverOnErrorOccurredParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceSamplesObserverOnErrorOccurredParams sensorDeviceSamplesObserverOnErrorOccurredParams = new SensorDeviceSamplesObserverOnErrorOccurredParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                sensorDeviceSamplesObserverOnErrorOccurredParams.type = i;
                ObserverErrorType.validate(i);
                return sensorDeviceSamplesObserverOnErrorOccurredParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private SensorDeviceSamplesObserverOnErrorOccurredParams(int i) {
            super(16, i);
        }
    }

    static final class SensorDeviceSamplesObserverOnSampleUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Map<Integer, Long> sample;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceSamplesObserverOnSampleUpdatedParams() {
            super(16, 0);
        }

        public static SensorDeviceSamplesObserverOnSampleUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceSamplesObserverOnSampleUpdatedParams sensorDeviceSamplesObserverOnSampleUpdatedParams = new SensorDeviceSamplesObserverOnSampleUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoder.readPointer(8, false);
                pointer.readDataHeaderForMap();
                int[] ints = pointer.readInts(8, 0, -1);
                long[] longs = pointer.readLongs(16, 0, ints.length);
                sensorDeviceSamplesObserverOnSampleUpdatedParams.sample = new HashMap();
                for (int i = 0; i < ints.length; i++) {
                    sensorDeviceSamplesObserverOnSampleUpdatedParams.sample.put(Integer.valueOf(ints[i]), Long.valueOf(longs[i]));
                }
                return sensorDeviceSamplesObserverOnSampleUpdatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            if (this.sample == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(8);
            int size = this.sample.size();
            int[] iArr = new int[size];
            long[] jArr = new long[size];
            int i = 0;
            for (Map.Entry<Integer, Long> entry : this.sample.entrySet()) {
                iArr[i] = entry.getKey().intValue();
                jArr[i] = entry.getValue().longValue();
                i++;
            }
            encoderEncoderForMap.encode(iArr, 8, 0, -1);
            encoderEncoderForMap.encode(jArr, 16, 0, -1);
        }

        private SensorDeviceSamplesObserverOnSampleUpdatedParams(int i) {
            super(16, i);
        }
    }

    static final class Stub extends Interface.Stub<SensorDeviceSamplesObserver> {
        Stub(Core core, SensorDeviceSamplesObserver sensorDeviceSamplesObserver) {
            super(core, sensorDeviceSamplesObserver);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SensorDeviceSamplesObserver_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onSampleUpdated(SensorDeviceSamplesObserverOnSampleUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).sample);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onErrorOccurred(SensorDeviceSamplesObserverOnErrorOccurredParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type);
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
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SensorDeviceSamplesObserver_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}

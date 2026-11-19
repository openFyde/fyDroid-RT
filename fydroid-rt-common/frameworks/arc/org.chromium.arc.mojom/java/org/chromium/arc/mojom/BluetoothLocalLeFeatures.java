package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BluetoothLocalLeFeatures extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte activityEnergyInfoSupported;
    public boolean debugLoggingSupported;
    public boolean extendedScanSupport;
    public byte localPrivacyEnabled;
    public byte maxAdvFilterSupported;
    public byte maxAdvInstance;
    public byte maxIrkListSize;
    public byte rpaOffloadSupported;
    public short scanResultStorageSize;
    public short totalTrackableAdvertisers;
    public short versionSupported;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BluetoothLocalLeFeatures() {
        super(24, 0);
    }

    public static BluetoothLocalLeFeatures decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BluetoothLocalLeFeatures bluetoothLocalLeFeatures = new BluetoothLocalLeFeatures(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bluetoothLocalLeFeatures.versionSupported = decoder.readShort(8);
            bluetoothLocalLeFeatures.localPrivacyEnabled = decoder.readByte(10);
            bluetoothLocalLeFeatures.maxAdvInstance = decoder.readByte(11);
            bluetoothLocalLeFeatures.rpaOffloadSupported = decoder.readByte(12);
            bluetoothLocalLeFeatures.maxIrkListSize = decoder.readByte(13);
            bluetoothLocalLeFeatures.maxAdvFilterSupported = decoder.readByte(14);
            bluetoothLocalLeFeatures.activityEnergyInfoSupported = decoder.readByte(15);
            bluetoothLocalLeFeatures.scanResultStorageSize = decoder.readShort(16);
            bluetoothLocalLeFeatures.totalTrackableAdvertisers = decoder.readShort(18);
            bluetoothLocalLeFeatures.extendedScanSupport = decoder.readBoolean(20, 0);
            bluetoothLocalLeFeatures.debugLoggingSupported = decoder.readBoolean(20, 1);
            return bluetoothLocalLeFeatures;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BluetoothLocalLeFeatures deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.versionSupported, 8);
        encoderAtDataOffset.encode(this.localPrivacyEnabled, 10);
        encoderAtDataOffset.encode(this.maxAdvInstance, 11);
        encoderAtDataOffset.encode(this.rpaOffloadSupported, 12);
        encoderAtDataOffset.encode(this.maxIrkListSize, 13);
        encoderAtDataOffset.encode(this.maxAdvFilterSupported, 14);
        encoderAtDataOffset.encode(this.activityEnergyInfoSupported, 15);
        encoderAtDataOffset.encode(this.scanResultStorageSize, 16);
        encoderAtDataOffset.encode(this.totalTrackableAdvertisers, 18);
        encoderAtDataOffset.encode(this.extendedScanSupport, 20, 0);
        encoderAtDataOffset.encode(this.debugLoggingSupported, 20, 1);
    }

    private BluetoothLocalLeFeatures(int i) {
        super(24, i);
    }

    public static BluetoothLocalLeFeatures deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

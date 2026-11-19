package org.chromium.device.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo_base.mojom.String16;

public final class UsbConfigurationInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String16 configurationName;
    public byte configurationValue;
    public byte[] extraData;
    public UsbInterfaceInfo[] interfaces;
    public byte maximumPower;
    public boolean remoteWakeup;
    public boolean selfPowered;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UsbConfigurationInfo() {
        super(40, 0);
    }

    public static UsbConfigurationInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UsbConfigurationInfo usbConfigurationInfo = new UsbConfigurationInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            usbConfigurationInfo.configurationValue = decoder.readByte(8);
            usbConfigurationInfo.selfPowered = decoder.readBoolean(9, 0);
            usbConfigurationInfo.remoteWakeup = decoder.readBoolean(9, 1);
            usbConfigurationInfo.maximumPower = decoder.readByte(10);
            usbConfigurationInfo.configurationName = String16.decode(decoder.readPointer(16, true));
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            usbConfigurationInfo.interfaces = new UsbInterfaceInfo[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                usbConfigurationInfo.interfaces[i] = UsbInterfaceInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            usbConfigurationInfo.extraData = decoder.readBytes(32, 0, -1);
            return usbConfigurationInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UsbConfigurationInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.configurationValue, 8);
        encoderAtDataOffset.encode(this.selfPowered, 9, 0);
        encoderAtDataOffset.encode(this.remoteWakeup, 9, 1);
        encoderAtDataOffset.encode(this.maximumPower, 10);
        encoderAtDataOffset.encode((Struct) this.configurationName, 16, true);
        UsbInterfaceInfo[] usbInterfaceInfoArr = this.interfaces;
        if (usbInterfaceInfoArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(usbInterfaceInfoArr.length, 24, -1);
            int i = 0;
            while (true) {
                UsbInterfaceInfo[] usbInterfaceInfoArr2 = this.interfaces;
                if (i >= usbInterfaceInfoArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) usbInterfaceInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, false);
        }
        encoderAtDataOffset.encode(this.extraData, 32, 0, -1);
    }

    private UsbConfigurationInfo(int i) {
        super(40, i);
    }

    public static UsbConfigurationInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

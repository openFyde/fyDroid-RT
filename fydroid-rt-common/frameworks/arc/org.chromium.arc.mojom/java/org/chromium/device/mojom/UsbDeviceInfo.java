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
import org.chromium.url.mojom.Url;

public final class UsbDeviceInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte activeConfiguration;
    public int busNumber;
    public byte classCode;
    public UsbConfigurationInfo[] configurations;
    public byte deviceVersionMajor;
    public byte deviceVersionMinor;
    public byte deviceVersionSubminor;
    public String guid;
    public String16 manufacturerName;
    public int portNumber;
    public short productId;
    public String16 productName;
    public byte protocolCode;
    public String16 serialNumber;
    public byte subclassCode;
    public byte usbVersionMajor;
    public byte usbVersionMinor;
    public byte usbVersionSubminor;
    public short vendorId;
    public Url webusbLandingPage;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(80, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UsbDeviceInfo() {
        super(80, 0);
    }

    public static UsbDeviceInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UsbDeviceInfo usbDeviceInfo = new UsbDeviceInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            usbDeviceInfo.guid = decoder.readString(8, false);
            usbDeviceInfo.usbVersionMajor = decoder.readByte(16);
            usbDeviceInfo.usbVersionMinor = decoder.readByte(17);
            usbDeviceInfo.usbVersionSubminor = decoder.readByte(18);
            usbDeviceInfo.classCode = decoder.readByte(19);
            usbDeviceInfo.subclassCode = decoder.readByte(20);
            usbDeviceInfo.protocolCode = decoder.readByte(21);
            usbDeviceInfo.vendorId = decoder.readShort(22);
            usbDeviceInfo.busNumber = decoder.readInt(24);
            usbDeviceInfo.portNumber = decoder.readInt(28);
            usbDeviceInfo.productId = decoder.readShort(32);
            usbDeviceInfo.deviceVersionMajor = decoder.readByte(34);
            usbDeviceInfo.deviceVersionMinor = decoder.readByte(35);
            usbDeviceInfo.deviceVersionSubminor = decoder.readByte(36);
            usbDeviceInfo.activeConfiguration = decoder.readByte(37);
            usbDeviceInfo.manufacturerName = String16.decode(decoder.readPointer(40, true));
            usbDeviceInfo.productName = String16.decode(decoder.readPointer(48, true));
            usbDeviceInfo.serialNumber = String16.decode(decoder.readPointer(56, true));
            usbDeviceInfo.webusbLandingPage = Url.decode(decoder.readPointer(64, true));
            Decoder pointer = decoder.readPointer(72, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            usbDeviceInfo.configurations = new UsbConfigurationInfo[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                usbDeviceInfo.configurations[i] = UsbConfigurationInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            return usbDeviceInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UsbDeviceInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.guid, 8, false);
        encoderAtDataOffset.encode(this.usbVersionMajor, 16);
        encoderAtDataOffset.encode(this.usbVersionMinor, 17);
        encoderAtDataOffset.encode(this.usbVersionSubminor, 18);
        encoderAtDataOffset.encode(this.classCode, 19);
        encoderAtDataOffset.encode(this.subclassCode, 20);
        encoderAtDataOffset.encode(this.protocolCode, 21);
        encoderAtDataOffset.encode(this.vendorId, 22);
        encoderAtDataOffset.encode(this.busNumber, 24);
        encoderAtDataOffset.encode(this.portNumber, 28);
        encoderAtDataOffset.encode(this.productId, 32);
        encoderAtDataOffset.encode(this.deviceVersionMajor, 34);
        encoderAtDataOffset.encode(this.deviceVersionMinor, 35);
        encoderAtDataOffset.encode(this.deviceVersionSubminor, 36);
        encoderAtDataOffset.encode(this.activeConfiguration, 37);
        encoderAtDataOffset.encode((Struct) this.manufacturerName, 40, true);
        encoderAtDataOffset.encode((Struct) this.productName, 48, true);
        encoderAtDataOffset.encode((Struct) this.serialNumber, 56, true);
        encoderAtDataOffset.encode((Struct) this.webusbLandingPage, 64, true);
        UsbConfigurationInfo[] usbConfigurationInfoArr = this.configurations;
        if (usbConfigurationInfoArr == null) {
            encoderAtDataOffset.encodeNullPointer(72, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(usbConfigurationInfoArr.length, 72, -1);
        int i = 0;
        while (true) {
            UsbConfigurationInfo[] usbConfigurationInfoArr2 = this.configurations;
            if (i >= usbConfigurationInfoArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) usbConfigurationInfoArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private UsbDeviceInfo(int i) {
        super(80, i);
    }

    public static UsbDeviceInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

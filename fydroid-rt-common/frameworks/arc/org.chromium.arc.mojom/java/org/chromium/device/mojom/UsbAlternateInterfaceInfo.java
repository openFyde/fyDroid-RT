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

public final class UsbAlternateInterfaceInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte alternateSetting;
    public byte classCode;
    public UsbEndpointInfo[] endpoints;
    public byte[] extraData;
    public String16 interfaceName;
    public byte protocolCode;
    public byte subclassCode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UsbAlternateInterfaceInfo() {
        super(40, 0);
    }

    public static UsbAlternateInterfaceInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UsbAlternateInterfaceInfo usbAlternateInterfaceInfo = new UsbAlternateInterfaceInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            usbAlternateInterfaceInfo.alternateSetting = decoder.readByte(8);
            usbAlternateInterfaceInfo.classCode = decoder.readByte(9);
            usbAlternateInterfaceInfo.subclassCode = decoder.readByte(10);
            usbAlternateInterfaceInfo.protocolCode = decoder.readByte(11);
            usbAlternateInterfaceInfo.interfaceName = String16.decode(decoder.readPointer(16, true));
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            usbAlternateInterfaceInfo.endpoints = new UsbEndpointInfo[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                usbAlternateInterfaceInfo.endpoints[i] = UsbEndpointInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            usbAlternateInterfaceInfo.extraData = decoder.readBytes(32, 0, -1);
            return usbAlternateInterfaceInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UsbAlternateInterfaceInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.alternateSetting, 8);
        encoderAtDataOffset.encode(this.classCode, 9);
        encoderAtDataOffset.encode(this.subclassCode, 10);
        encoderAtDataOffset.encode(this.protocolCode, 11);
        encoderAtDataOffset.encode((Struct) this.interfaceName, 16, true);
        UsbEndpointInfo[] usbEndpointInfoArr = this.endpoints;
        if (usbEndpointInfoArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(usbEndpointInfoArr.length, 24, -1);
            int i = 0;
            while (true) {
                UsbEndpointInfo[] usbEndpointInfoArr2 = this.endpoints;
                if (i >= usbEndpointInfoArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) usbEndpointInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, false);
        }
        encoderAtDataOffset.encode(this.extraData, 32, 0, -1);
    }

    private UsbAlternateInterfaceInfo(int i) {
        super(40, i);
    }

    public static UsbAlternateInterfaceInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

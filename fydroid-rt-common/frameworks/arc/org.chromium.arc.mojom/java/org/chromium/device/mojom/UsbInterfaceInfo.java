package org.chromium.device.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UsbInterfaceInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public UsbAlternateInterfaceInfo[] alternates;
    public byte firstInterface;
    public byte interfaceNumber;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UsbInterfaceInfo() {
        super(24, 0);
    }

    public static UsbInterfaceInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UsbInterfaceInfo usbInterfaceInfo = new UsbInterfaceInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            usbInterfaceInfo.interfaceNumber = decoder.readByte(8);
            usbInterfaceInfo.firstInterface = decoder.readByte(9);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            usbInterfaceInfo.alternates = new UsbAlternateInterfaceInfo[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                usbInterfaceInfo.alternates[i] = UsbAlternateInterfaceInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            return usbInterfaceInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UsbInterfaceInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.interfaceNumber, 8);
        encoderAtDataOffset.encode(this.firstInterface, 9);
        UsbAlternateInterfaceInfo[] usbAlternateInterfaceInfoArr = this.alternates;
        if (usbAlternateInterfaceInfoArr == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(usbAlternateInterfaceInfoArr.length, 16, -1);
        int i = 0;
        while (true) {
            UsbAlternateInterfaceInfo[] usbAlternateInterfaceInfoArr2 = this.alternates;
            if (i >= usbAlternateInterfaceInfoArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) usbAlternateInterfaceInfoArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private UsbInterfaceInfo(int i) {
        super(24, i);
    }

    public static UsbInterfaceInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

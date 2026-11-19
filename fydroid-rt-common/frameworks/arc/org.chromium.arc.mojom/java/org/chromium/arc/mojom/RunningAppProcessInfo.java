package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RunningAppProcessInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean isFocused;
    public long lastActivityTime;
    public String[] packages;
    public int pid;
    public String processName;
    public int processState;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RunningAppProcessInfo() {
        super(48, 0);
    }

    public static RunningAppProcessInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RunningAppProcessInfo runningAppProcessInfo = new RunningAppProcessInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            runningAppProcessInfo.processName = decoder.readString(8, false);
            runningAppProcessInfo.pid = decoder.readInt(16);
            int i = decoder.readInt(20);
            runningAppProcessInfo.processState = i;
            ProcessState.validate(i);
            Decoder pointer = decoder.readPointer(24, true);
            if (pointer == null) {
                runningAppProcessInfo.packages = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                runningAppProcessInfo.packages = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    runningAppProcessInfo.packages[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
                }
            }
            runningAppProcessInfo.isFocused = decoder.readBoolean(32, 0);
            runningAppProcessInfo.lastActivityTime = decoder.readLong(40);
            return runningAppProcessInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RunningAppProcessInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.processName, 8, false);
        encoderAtDataOffset.encode(this.pid, 16);
        encoderAtDataOffset.encode(this.processState, 20);
        String[] strArr = this.packages;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 24, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.packages;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, true);
        }
        encoderAtDataOffset.encode(this.isFocused, 32, 0);
        encoderAtDataOffset.encode(this.lastActivityTime, 40);
    }

    private RunningAppProcessInfo(int i) {
        super(48, i);
    }

    public static RunningAppProcessInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

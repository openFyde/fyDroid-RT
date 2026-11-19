package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class SelectFilesRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int actionType;
    public boolean allowMultiple;
    public String defaultFileName;
    public String initialContentUri;
    public DocumentPath initialDocumentPath;
    public String[] mimeTypes;
    public boolean openableOnly;
    public String searchQuery;
    public int taskId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(64, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SelectFilesRequest() {
        super(64, 0);
    }

    public static SelectFilesRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SelectFilesRequest selectFilesRequest = new SelectFilesRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            selectFilesRequest.actionType = i;
            SelectFilesActionType.validate(i);
            selectFilesRequest.openableOnly = decoder.readBoolean(12, 0);
            selectFilesRequest.allowMultiple = decoder.readBoolean(12, 1);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            selectFilesRequest.mimeTypes = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                selectFilesRequest.mimeTypes[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
            }
            selectFilesRequest.defaultFileName = decoder.readString(24, false);
            selectFilesRequest.initialContentUri = decoder.readString(32, false);
            selectFilesRequest.initialDocumentPath = DocumentPath.decode(decoder.readPointer(40, true));
            selectFilesRequest.taskId = decoder.readInt(48);
            selectFilesRequest.searchQuery = decoder.readString(56, true);
            return selectFilesRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SelectFilesRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.actionType, 8);
        encoderAtDataOffset.encode(this.openableOnly, 12, 0);
        encoderAtDataOffset.encode(this.allowMultiple, 12, 1);
        String[] strArr = this.mimeTypes;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.mimeTypes;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode(this.defaultFileName, 24, false);
        encoderAtDataOffset.encode(this.initialContentUri, 32, false);
        encoderAtDataOffset.encode((Struct) this.initialDocumentPath, 40, true);
        encoderAtDataOffset.encode(this.taskId, 48);
        encoderAtDataOffset.encode(this.searchQuery, 56, true);
    }

    private SelectFilesRequest(int i) {
        super(64, i);
    }

    public static SelectFilesRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

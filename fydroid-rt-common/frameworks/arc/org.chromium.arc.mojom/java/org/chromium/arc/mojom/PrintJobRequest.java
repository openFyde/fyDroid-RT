// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

public final class PrintJobRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public PrintAttributes attributes;
    public int contentType;
    public int copies;
    public long creationTime;
    public UntypedHandle data;
    public long dataSize;
    public String documentName;
    public int documentPageCount;
    public byte[] id;
    public String label;
    public PrintPageRange[] pages;
    public String printerId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(88, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrintJobRequest() {
        super(88, 0);
        this.data = InvalidHandle.INSTANCE;
    }

    public static PrintJobRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrintJobRequest printJobRequest = new PrintJobRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            printJobRequest.id = decoder.readBytes(8, 0, -1);
            printJobRequest.label = decoder.readString(16, false);
            printJobRequest.printerId = decoder.readString(24, true);
            printJobRequest.creationTime = decoder.readLong(32);
            printJobRequest.copies = decoder.readInt(40);
            printJobRequest.documentPageCount = decoder.readInt(44);
            Decoder pointer = decoder.readPointer(48, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            printJobRequest.pages = new PrintPageRange[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                printJobRequest.pages[i] = PrintPageRange.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            printJobRequest.attributes = PrintAttributes.decode(decoder.readPointer(56, false));
            printJobRequest.documentName = decoder.readString(64, false);
            int i2 = decoder.readInt(72);
            printJobRequest.contentType = i2;
            PrintContentType.validate(i2);
            printJobRequest.data = decoder.readUntypedHandle(76, true);
            printJobRequest.dataSize = decoder.readLong(80);
            return printJobRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrintJobRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8, 0, -1);
        encoderAtDataOffset.encode(this.label, 16, false);
        encoderAtDataOffset.encode(this.printerId, 24, true);
        encoderAtDataOffset.encode(this.creationTime, 32);
        encoderAtDataOffset.encode(this.copies, 40);
        encoderAtDataOffset.encode(this.documentPageCount, 44);
        PrintPageRange[] printPageRangeArr = this.pages;
        if (printPageRangeArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(printPageRangeArr.length, 48, -1);
            int i = 0;
            while (true) {
                PrintPageRange[] printPageRangeArr2 = this.pages;
                if (i >= printPageRangeArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) printPageRangeArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(48, false);
        }
        encoderAtDataOffset.encode((Struct) this.attributes, 56, false);
        encoderAtDataOffset.encode(this.documentName, 64, false);
        encoderAtDataOffset.encode(this.contentType, 72);
        encoderAtDataOffset.encode((Handle) this.data, 76, true);
        encoderAtDataOffset.encode(this.dataSize, 80);
    }

    public static PrintJobRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private PrintJobRequest(int i) {
        super(88, i);
        this.data = InvalidHandle.INSTANCE;
    }
}

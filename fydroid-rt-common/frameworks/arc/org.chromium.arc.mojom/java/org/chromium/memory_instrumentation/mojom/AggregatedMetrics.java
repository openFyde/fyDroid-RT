// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.memory_instrumentation.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AggregatedMetrics extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int nativeLibraryNotResidentOrderedKb;
    public int nativeLibraryResidentKb;
    public int nativeLibraryResidentNotOrderedKb;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AggregatedMetrics() {
        super(24, 0);
        this.nativeLibraryResidentKb = 0;
        this.nativeLibraryResidentNotOrderedKb = 0;
        this.nativeLibraryNotResidentOrderedKb = 0;
    }

    public static AggregatedMetrics decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AggregatedMetrics aggregatedMetrics = new AggregatedMetrics(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            aggregatedMetrics.nativeLibraryResidentKb = decoder.readInt(8);
            aggregatedMetrics.nativeLibraryResidentNotOrderedKb = decoder.readInt(12);
            aggregatedMetrics.nativeLibraryNotResidentOrderedKb = decoder.readInt(16);
            return aggregatedMetrics;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AggregatedMetrics deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.nativeLibraryResidentKb, 8);
        encoderAtDataOffset.encode(this.nativeLibraryResidentNotOrderedKb, 12);
        encoderAtDataOffset.encode(this.nativeLibraryNotResidentOrderedKb, 16);
    }

    public static AggregatedMetrics deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private AggregatedMetrics(int i) {
        super(24, i);
        this.nativeLibraryResidentKb = 0;
        this.nativeLibraryResidentNotOrderedKb = 0;
        this.nativeLibraryNotResidentOrderedKb = 0;
    }
}

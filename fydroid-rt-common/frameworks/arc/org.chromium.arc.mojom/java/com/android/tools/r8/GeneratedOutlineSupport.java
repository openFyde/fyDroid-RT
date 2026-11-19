package com.android.tools.r8;

import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;

/**
 * R8 generates this class during code optimization to outline common code patterns.
 */
public class GeneratedOutlineSupport {

    /**
     * Encodes a String into a pointer array element and advances the loop index.
     * R8 outlined pattern: encoder.encode(value, baseOffset + index * elemSize, nullable)
     */
    public static int outline0(int index, int baseOffset, int elemSize,
            Encoder encoder, String value, boolean nullable, int loopVar, int increment) {
        encoder.encode(value, baseOffset + loopVar * elemSize, nullable);
        return index + increment;
    }

    /**
     * Reads a String from a pointer array element.
     * R8 outlined pattern: decoder.readString(baseOffset + index * elemSize, nullable)
     */
    public static String outline1(int index, int baseOffset, int elemSize,
            Decoder decoder, boolean nullable) {
        return decoder.readString(baseOffset + index * elemSize, nullable);
    }

    /**
     * Reads a nested Decoder (pointer) from a pointer array element.
     * R8 outlined pattern: decoder.readPointer(baseOffset + index * elemSize, nullable)
     */
    public static Decoder outline2(int index, int baseOffset, int elemSize,
            Decoder decoder, boolean nullable) {
        return decoder.readPointer(baseOffset + index * elemSize, nullable);
    }

    /**
     * Creates a Decoder from a Message with increased stack depth.
     * R8 outlined pattern: new Decoder(message) + increaseStackDepth()
     */
    public static Decoder outline3(Message message) {
        Decoder decoder = new Decoder(message);
        decoder.increaseStackDepth();
        return decoder;
    }
}

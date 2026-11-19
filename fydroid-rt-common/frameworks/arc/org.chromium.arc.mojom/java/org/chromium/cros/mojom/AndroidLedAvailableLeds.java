package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidLedAvailableLeds {
    public static final int ANDROID_LED_AVAILABLE_LEDS_TRANSMIT = 0;

    private AndroidLedAvailableLeds() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Open source replacement for org.chromium.arc.display.ArcDisplayLayoutUtils
 * from the proprietary com.google.android.chromeos jar.
 */
package org.chromium.arc.display;

import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.DisplayAddress;

import java.util.Optional;

/**
 * Helpers for looking up {@link ArcDisplay} entries inside an
 * {@link ArcDisplayLayout} by physical display ID.
 */
public class ArcDisplayLayoutUtils {
    private ArcDisplayLayoutUtils() {}

    public static Optional<ArcDisplay> getDisplay(ArcDisplayLayout layout,
            DisplayAddress.Physical address) {
        for (ArcDisplay display : layout.displays) {
            if (display.physicalDisplayId == address.getPhysicalDisplayId()) {
                return Optional.of(display);
            }
        }
        return Optional.empty();
    }

    public static Optional<ArcDisplay> getDisplay(DisplayManager displayManager,
            ArcDisplayLayout layout, int displayId) {
        Display display = displayManager.getDisplay(displayId);
        if (display == null || !(display.getAddress() instanceof DisplayAddress.Physical)) {
            return Optional.empty();
        }
        return getDisplay(layout, (DisplayAddress.Physical) display.getAddress());
    }
}

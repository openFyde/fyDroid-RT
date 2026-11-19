/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Open source replacement for org.chromium.arc.display.ArcDisplayUtils from
 * the proprietary com.google.android.chromeos jar.
 */
package org.chromium.arc.display;

import android.graphics.Insets;
import android.util.Size;

/**
 * Helpers for working with {@link ArcDisplay}. API-compatible with ARC++ so
 * existing consumers can keep working unchanged.
 */
public class ArcDisplayUtils {
    private ArcDisplayUtils() {}

    /**
     * Logical size of the display: width/height are swapped when the
     * transform is 90 or 270 degrees.
     */
    public static Size getSize(ArcDisplay display) {
        if (display.transform % 2 == 0) {
            return new Size(display.widthPixels, display.heightPixels);
        }
        return new Size(display.heightPixels, display.widthPixels);
    }

    /** Insets covering all system UI on this display. */
    public static Insets getInsets(ArcDisplay display) {
        return Insets.of(display.insetsLeft, display.insetsTop,
                display.insetsRight, display.insetsBottom);
    }

    /** "Stable" insets that ignore auto-hiding chrome. */
    public static Insets getStableInsets(ArcDisplay display) {
        return Insets.of(display.stableInsetsLeft, display.stableInsetsTop,
                display.stableInsetsRight, display.stableInsetsBottom);
    }

    /**
     * Insets to publish to the Android display: when the shelf auto-hides
     * Android sees the full insets; otherwise it sees the stable rect.
     */
    public static Insets getStableInsetsForAndroid(ArcDisplay display) {
        return display.autoHide ? getInsets(display) : getStableInsets(display);
    }

    /**
     * Density bucket for the display, in the same scale as
     * {@link android.util.DisplayMetrics#density} (1.0 == 160 dpi).
     */
    public static double getDensity(ArcDisplay display) {
        return display.overrideDpi * 0.00625f;
    }

    /**
     * Field-by-field equality, ignoring {@link ArcDisplay#displayRemapped}.
     */
    public static boolean isEqual(ArcDisplay lhs, ArcDisplay rhs) {
        if (lhs == null && rhs == null) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs.physicalDisplayId == rhs.physicalDisplayId
                && lhs.globalPositionX == rhs.globalPositionX
                && lhs.globalPositionY == rhs.globalPositionY
                && lhs.widthPixels == rhs.widthPixels
                && lhs.heightPixels == rhs.heightPixels
                && lhs.transform == rhs.transform
                && lhs.insetsLeft == rhs.insetsLeft
                && lhs.insetsTop == rhs.insetsTop
                && lhs.insetsRight == rhs.insetsRight
                && lhs.insetsBottom == rhs.insetsBottom
                && lhs.stableInsetsLeft == rhs.stableInsetsLeft
                && lhs.stableInsetsTop == rhs.stableInsetsTop
                && lhs.stableInsetsRight == rhs.stableInsetsRight
                && lhs.stableInsetsBottom == rhs.stableInsetsBottom
                && lhs.autoHide == rhs.autoHide
                && lhs.deviceScaleFactor == rhs.deviceScaleFactor
                && lhs.dpi == rhs.dpi
                && lhs.overrideDpi == rhs.overrideDpi) {
            return true;
        }
        return false;
    }
}

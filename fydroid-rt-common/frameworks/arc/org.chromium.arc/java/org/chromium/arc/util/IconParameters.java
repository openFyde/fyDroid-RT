package org.chromium.arc.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

public class IconParameters {
    public final int androidDensity;
    public final int iconDimension;
    public static final IconParameters DEFAULT_LDPI = new IconParameters(36, 120);
    public static final IconParameters DEFAULT_MDPI = new IconParameters(48, 160);
    public static final IconParameters DEFAULT_HDPI = new IconParameters(72, 240);
    public static final IconParameters DEFAULT_XHDPI = new IconParameters(96, 320);
    public static final IconParameters DEFAULT_XXHDPI = new IconParameters(144, 480);
    public static final IconParameters DEFAULT_XXXHDPI = new IconParameters(192, 640);

    public IconParameters(int iconDimension, int androidDensity) {
        this.iconDimension = iconDimension;
        this.androidDensity = androidDensity;
    }

    public static IconParameters createForBitmap(Resources resources, Bitmap bitmap) {
        return new IconParameters(Math.max(bitmap.getWidth(), bitmap.getHeight()),
                resources.getDisplayMetrics().densityDpi);
    }

    public static IconParameters createForDimension(int dimension) {
        if (dimension <= DEFAULT_LDPI.iconDimension) {
            return new IconParameters(dimension, DEFAULT_LDPI.androidDensity);
        }
        if (dimension <= DEFAULT_MDPI.iconDimension) {
            return new IconParameters(dimension, DEFAULT_MDPI.androidDensity);
        }
        if (dimension <= DEFAULT_HDPI.iconDimension) {
            return new IconParameters(dimension, DEFAULT_HDPI.androidDensity);
        }
        if (dimension <= DEFAULT_XHDPI.iconDimension) {
            return new IconParameters(dimension, DEFAULT_XHDPI.androidDensity);
        }
        if (dimension <= DEFAULT_XXHDPI.iconDimension) {
            return new IconParameters(dimension, DEFAULT_XXHDPI.androidDensity);
        }
        return new IconParameters(dimension, DEFAULT_XXXHDPI.androidDensity);
    }

    public static IconParameters getDefaultForDensity(int density) {
        if (density == 120) return DEFAULT_LDPI;
        if (density == 160) return DEFAULT_MDPI;
        if (density == 240) return DEFAULT_HDPI;
        if (density == 320) return DEFAULT_XHDPI;
        if (density == 480) return DEFAULT_XXHDPI;
        if (density == 640) return DEFAULT_XXXHDPI;
        Log.wtf("ArcIconParameters", "Unsupported density requested: " + density + ".");
        return DEFAULT_MDPI;
    }

    public String toString() {
        return "iconDimension: " + this.iconDimension + " density: " + this.androidDensity;
    }
}

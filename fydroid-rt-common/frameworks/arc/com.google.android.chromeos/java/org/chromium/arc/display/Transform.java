/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Open source replacement for org.chromium.arc.display.Transform from
 * the proprietary com.google.android.chromeos jar.
 */
package org.chromium.arc.display;

/**
 * Display rotation values used as the {@code transform} field of
 * {@link ArcDisplay}. Defined as an annotation interface so that the
 * constants live in the same place as in the proprietary jar.
 */
public @interface Transform {
    int ROTATION_0 = 0;
    int ROTATION_90 = 1;
    int ROTATION_180 = 2;
    int ROTATION_270 = 3;
}

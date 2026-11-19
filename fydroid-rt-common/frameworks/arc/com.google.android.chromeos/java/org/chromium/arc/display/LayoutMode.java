/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Open source replacement for org.chromium.arc.display.LayoutMode from
 * the proprietary com.google.android.chromeos jar.
 */
package org.chromium.arc.display;

/**
 * Layout mode of the Chrome OS shell, used as the {@code layoutMode} field
 * in {@link ArcDisplayLayout}. Defined as an annotation interface so that
 * the constants live in the same place as in the proprietary jar.
 */
public @interface LayoutMode {
    int LAYOUT_WINDOWED = 1;
    int LAYOUT_TABLET = 2;
}

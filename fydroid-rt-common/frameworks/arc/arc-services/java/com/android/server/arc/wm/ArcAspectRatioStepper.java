/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.server.arc.wm;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes the "next" window bounds for an unresizable activity whose user
 * just drag-resized the window. The pick cycles through a small Chrome-OS
 * style aspect-ratio list filtered by the activity's own constraints, and
 * snaps to the AR closest to whatever the user just dragged to.
 *
 * Inputs:
 *   - {@link ActivityInfo}: source of activity.minAspectRatio,
 *     maxAspectRatio, screenOrientation, and the manifest &lt;layout&gt;
 *     (defaultWidth/Height, minWidth/Height).
 *   - draggedBounds: where the user released the drag. We use its AR
 *     (long/short) as the target.
 *   - displayBounds: the current display size, in the same coordinate
 *     space as draggedBounds.
 *
 * Output: bounds centered on the display, sized to the chosen AR, with
 * dimensions respecting the manifest minWidth/Height. Returns null when
 * the activity's AR constraints don't fit any predefined AR — caller is
 * expected to fall back to portrait/landscape swap of the manifest size.
 */
final class ArcAspectRatioStepper {

    /** Predefined Chrome-OS-style aspect ratios as long-side / short-side
     * (so every value here is >= 1.0). Listed roughly in order of
     * Chrome-OS UI ergonomics. */
    private static final float[] CANDIDATES = {
        1.0f,        // 1:1 square
        4f / 3f,     // 4:3
        3f / 2f,     // 3:2
        16f / 10f,   // 16:10
        16f / 9f,    // 16:9
        2f,          // 2:1
    };

    /** Tolerance when matching an AR to the candidate list. */
    private static final float AR_EPSILON = 0.01f;

    private ArcAspectRatioStepper() {}

    /**
     * Returns the new bounds. Never null — if the activity's AR
     * constraints exclude the entire candidate list, falls back to a
     * portrait/landscape swap of the manifest default size centered on
     * the display.
     */
    static Rect computeNextBounds(ActivityInfo info, Rect draggedBounds,
            Rect displayBounds) {
        if (info == null || draggedBounds == null || displayBounds == null) {
            return null;
        }
        if (displayBounds.width() <= 0 || displayBounds.height() <= 0) {
            return null;
        }

        final float minAR = info.getMinAspectRatio() > 0f ? info.getMinAspectRatio() : 1.0f;
        final float maxAR = info.getMaxAspectRatio() > 0f
                ? info.getMaxAspectRatio() : Float.MAX_VALUE;

        // Manifest <layout> minWidth/Height — keep window above these.
        int minW = 0;
        int minH = 0;
        int manDefW = 0;
        int manDefH = 0;
        if (info.windowLayout != null) {
            minW = Math.max(0, info.windowLayout.minWidth);
            minH = Math.max(0, info.windowLayout.minHeight);
            manDefW = Math.max(0, info.windowLayout.width);
            manDefH = Math.max(0, info.windowLayout.height);
        }

        // Which orientation is the user asking for? Use the dragged
        // bounds' orientation as the steering signal; ties go to
        // landscape since most ARC apps target portrait by default.
        final boolean wantPortrait =
                draggedBounds.height() > draggedBounds.width();

        // Build the filtered AR list: candidates within [minAR, maxAR].
        // Plus the activity's own min/max ARs if they fall outside the
        // canonical set (so an app that asks for exactly 1.86 lands on
        // 1.86 rather than the nearest predefined ratio).
        List<Float> arList = new ArrayList<>(CANDIDATES.length + 2);
        for (float c : CANDIDATES) {
            if (c >= minAR - AR_EPSILON && c <= maxAR + AR_EPSILON) {
                arList.add(c);
            }
        }
        if (info.getMinAspectRatio() > 0f && !contains(arList, info.getMinAspectRatio())) {
            arList.add(info.getMinAspectRatio());
        }
        if (info.getMaxAspectRatio() > 0f && info.getMaxAspectRatio() < Float.MAX_VALUE
                && !contains(arList, info.getMaxAspectRatio())) {
            arList.add(info.getMaxAspectRatio());
        }
        if (arList.isEmpty()) {
            // No candidate fits — fall back to portrait/landscape swap
            // of the manifest size.
            return fallbackSwap(manDefW, manDefH, wantPortrait, displayBounds);
        }

        // Snap target AR to the AR of the dragged bounds (clamped into
        // [minAR, maxAR]).
        final float draggedLong = Math.max(
                draggedBounds.width(), draggedBounds.height());
        final float draggedShort = Math.max(1f, Math.min(
                draggedBounds.width(), draggedBounds.height()));
        float draggedAR = Math.max(1f, draggedLong / draggedShort);
        draggedAR = Math.max(minAR, Math.min(maxAR, draggedAR));

        // Pick the candidate AR closest to the drag-target.
        float bestAR = arList.get(0);
        float bestDist = Math.abs(bestAR - draggedAR);
        for (int i = 1; i < arList.size(); i++) {
            float c = arList.get(i);
            float d = Math.abs(c - draggedAR);
            if (d < bestDist) {
                bestDist = d;
                bestAR = c;
            }
        }

        return sizeForAspectRatio(bestAR, wantPortrait, minW, minH,
                manDefW, manDefH, displayBounds);
    }

    private static boolean contains(List<Float> list, float v) {
        for (Float f : list) {
            if (Math.abs(f - v) < AR_EPSILON) return true;
        }
        return false;
    }

    /**
     * Compute concrete bounds for the chosen AR: respect manifest
     * minWidth/Height + try to honor the manifest default size as the
     * preferred footprint; fall back to ~60 % of display short side if
     * the manifest didn't specify a default.
     */
    private static Rect sizeForAspectRatio(float ar, boolean portrait,
            int minW, int minH, int manDefW, int manDefH, Rect display) {
        final int dispW = display.width();
        final int dispH = display.height();
        final int dispShort = Math.min(dispW, dispH);

        // Preferred short-side: manifest default short side, else 60 % of
        // display short side; never under the manifest minWidth/Height
        // for the active orientation.
        int defShort;
        if (manDefW > 0 && manDefH > 0) {
            defShort = Math.min(manDefW, manDefH);
        } else {
            defShort = Math.max(360, (int) Math.round(dispShort * 0.6f));
        }
        int minShort = portrait ? minW : minH;
        if (minShort > 0) defShort = Math.max(defShort, minShort);

        int shortSide = defShort;
        int longSide = (int) Math.round(shortSide * ar);

        // Cap to display.
        if (portrait) {
            if (longSide > dispH) {
                longSide = dispH;
                shortSide = (int) Math.round(longSide / ar);
            }
            if (shortSide > dispW) {
                shortSide = dispW;
                longSide = (int) Math.round(shortSide * ar);
            }
        } else {
            if (longSide > dispW) {
                longSide = dispW;
                shortSide = (int) Math.round(longSide / ar);
            }
            if (shortSide > dispH) {
                shortSide = dispH;
                longSide = (int) Math.round(shortSide * ar);
            }
        }

        final int w = portrait ? shortSide : longSide;
        final int h = portrait ? longSide : shortSide;

        // Don't go below manifest minimums even after capping.
        if (minW > 0 && w < minW) return null;
        if (minH > 0 && h < minH) return null;

        final int x = display.left + (dispW - w) / 2;
        final int y = display.top + (dispH - h) / 2;
        return new Rect(x, y, x + w, y + h);
    }

    /**
     * Fallback when no candidate AR is in range — swap orientation of
     * the manifest default size. Centers on display; returns null if
     * the manifest had no default size (caller should leave bounds
     * unchanged).
     */
    private static Rect fallbackSwap(int manDefW, int manDefH,
            boolean wantPortrait, Rect display) {
        if (manDefW <= 0 || manDefH <= 0) return null;

        final int w = wantPortrait ? Math.min(manDefW, manDefH)
                                   : Math.max(manDefW, manDefH);
        final int h = wantPortrait ? Math.max(manDefW, manDefH)
                                   : Math.min(manDefW, manDefH);
        if (w > display.width() || h > display.height()) {
            return null;
        }
        final int x = display.left + (display.width() - w) / 2;
        final int y = display.top + (display.height() - h) / 2;
        return new Rect(x, y, x + w, y + h);
    }
}

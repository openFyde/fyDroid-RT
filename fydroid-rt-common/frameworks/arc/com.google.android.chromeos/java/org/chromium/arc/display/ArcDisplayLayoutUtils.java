/*
 * Copyright (C) 2024 Fyde Innovations
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

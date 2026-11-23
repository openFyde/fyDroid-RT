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
 * Open source replacement for org.chromium.arc.wayland.DesktopFocusState
 * from the proprietary com.google.android.chromeos jar. Integer constants
 * describing the desktop-level focus state reported by wayland_service.
 *
 * Values are bit-for-bit identical to the proprietary class so any
 * in-process consumer keeps working unchanged.
 */
package org.chromium.arc.wayland;

public @interface DesktopFocusState {
    public static final int CLIENT_FOCUSED = 2;
    public static final int NO_FOCUS = 1;
    public static final int OTHER_CLIENT_FOCUSED = 3;
}

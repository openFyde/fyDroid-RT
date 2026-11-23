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
 * Open source replacement for org.chromium.arc.wayland.CtrlType from the
 * proprietary com.google.android.chromeos jar. Integer constants describing
 * the drag / resize handle used by IWaylandShellHandler#taskDragStarted.
 *
 * Values are bit-for-bit identical to the proprietary class. Note that the
 * numbering intentionally skips 3 and 7 (there is no CTRL_TYPE value for
 * those ints) — this matches the proprietary layout and is preserved here
 * verbatim so any in-process consumer keeps working unchanged.
 */
package org.chromium.arc.wayland;

public @interface CtrlType {
    public static final int CTRL_TYPE_BOTTOM = 8;
    public static final int CTRL_TYPE_BOTTOM_LEFT = 9;
    public static final int CTRL_TYPE_BOTTOM_RIGHT = 10;
    public static final int CTRL_TYPE_LEFT = 1;
    public static final int CTRL_TYPE_NONE = 0;
    public static final int CTRL_TYPE_RIGHT = 2;
    public static final int CTRL_TYPE_TOP = 4;
    public static final int CTRL_TYPE_TOP_LEFT = 5;
    public static final int CTRL_TYPE_TOP_RIGHT = 6;
}

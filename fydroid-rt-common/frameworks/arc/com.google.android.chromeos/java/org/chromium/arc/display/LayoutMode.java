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

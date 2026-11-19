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

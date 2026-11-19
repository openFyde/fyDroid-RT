/*
 * Copyright (C) 2026 The FydeOS Authors
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

package com.android.server.arc.input_method_manager;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parsing for Settings.Secure.ENABLED_INPUT_METHODS.
 *
 * Format is {@code ime1;subtype1;subtype2:ime2:ime3;subtype3} — IME entries
 * separated by ':', subtypes within an entry by ';'.
 *
 * Insertion order is preserved (LinkedHashMap): the order of this list is what
 * the IME switcher offers, so round-tripping it must not shuffle the user's
 * keyboards.
 */
public final class InputMethodSettingsUtils {

    private static final char INPUT_METHOD_SEPARATOR = ':';
    private static final char INPUT_METHOD_SUBTYPE_SEPARATOR = ';';

    private InputMethodSettingsUtils() {}

    public static Map<String, List<String>> parseEnabledInputMethodsString(String str) {
        final Map<String, List<String>> map = new LinkedHashMap<>();
        if (TextUtils.isEmpty(str)) {
            return map;
        }
        for (String entry : str.split(String.valueOf(INPUT_METHOD_SEPARATOR))) {
            if (entry.isEmpty()) continue;
            final String[] parts = entry.split(String.valueOf(INPUT_METHOD_SUBTYPE_SEPARATOR));
            final List<String> subtypes = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                if (!parts[i].isEmpty()) subtypes.add(parts[i]);
            }
            map.put(parts[0], subtypes);
        }
        return map;
    }

    public static String buildEnabledInputMethodsString(Map<String, List<String>> map) {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (sb.length() > 0) sb.append(INPUT_METHOD_SEPARATOR);
            sb.append(entry.getKey());
            for (String subtype : entry.getValue()) {
                sb.append(INPUT_METHOD_SUBTYPE_SEPARATOR).append(subtype);
            }
        }
        return sb.toString();
    }
}

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

package com.android.server.arc.input_method_manager;

import android.content.Context;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import android.view.InputDevice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Keeps Settings > Physical keyboard in step with Gboard's sidebar language.
 *
 * Gboard on Android 13 reports no IME subtypes (its subtype module needs SDK
 * 34), so Android cannot see which language its sidebar shows, and hardware
 * keys type with whatever physical-keyboard layout Settings happens to hold.
 * fydroid_imelangd publishes Gboard's "current_input_method_entry"
 * ({@code <languageTag>:<variant>}) as {@link #PROPERTY}; this maps it to an
 * AOSP InputDevices layout and makes that the keyboard's current layout.
 *
 * The mapping is an explicit table (layoutFor) of Gboard language tags to
 * AOSP or Gboard layouts, with English (US) as the fallback for anything not in
 * it or not installed. Gboard's sidebar is the authority: a Dvorak or Colemak
 * layout comes from Gboard's own variant, not from a pick in Settings.
 *
 * One way only: Gboard cannot be told a language on Android 13. Does nothing
 * when Gboard is not installed or not the active IME.
 */
final class GboardLayoutSync {

    private static final String TAG = "ArcGboardLayoutSync";

    static final String PROPERTY = "arc.fydroid.gboard_entry";
    static final String GBOARD_PACKAGE = "com.google.android.inputmethod.latin";
    /** Layout name used when nothing fits the Gboard language. */
    private static final String FALLBACK_LAYOUT = "english_us";

    /** Gboard variant -> layout, for variants that pick a layout of their own. */
    private static final Map<String, String> VARIANT_LAYOUTS = new HashMap<>();
    /**
     * Gboard language tag -> Android layout name (keyboard_layout_<name>, from
     * AOSP InputDevices or Gboard's own InputDeviceReceiver). Keys are tried
     * lang-Script-Region, lang-Script, lang-Region, lang.
     */
    private static final Map<String, String> LANGUAGE_LAYOUTS = new HashMap<>();
    /** Script -> layout, for languages not listed (e.g. kmz-Arab -> arabic). */
    private static final Map<String, String> SCRIPT_LAYOUTS = new HashMap<>();
    /** Layouts that type Latin letters; see the Latn rule in layoutFor(). */
    private static final java.util.Set<String> LATIN_LAYOUTS = new java.util.HashSet<>();

    static {
        VARIANT_LAYOUTS.put("dvorak", "english_us_dvorak");
        VARIANT_LAYOUTS.put("colemak", "english_us_colemak");
        VARIANT_LAYOUTS.put("workman", "english_us_workman");
        VARIANT_LAYOUTS.put("bepo", "french_bepo");

        // English and Latin-script European languages (AOSP layouts).
        LANGUAGE_LAYOUTS.put("en", FALLBACK_LAYOUT);
        LANGUAGE_LAYOUTS.put("en-GB", "english_uk");
        LANGUAGE_LAYOUTS.put("en-IN", "english_india");             // Gboard
        LANGUAGE_LAYOUTS.put("de", "german");
        LANGUAGE_LAYOUTS.put("de-CH", "swiss_german");
        LANGUAGE_LAYOUTS.put("fr", "french");
        LANGUAGE_LAYOUTS.put("fr-CA", "french_ca");
        LANGUAGE_LAYOUTS.put("fr-CH", "swiss_french");
        LANGUAGE_LAYOUTS.put("fr-BE", "belgian");
        LANGUAGE_LAYOUTS.put("nl-BE", "belgian");
        LANGUAGE_LAYOUTS.put("es", "spanish");
        LANGUAGE_LAYOUTS.put("es-419", "spanish_latin");
        LANGUAGE_LAYOUTS.put("es-MX", "spanish_latin");
        LANGUAGE_LAYOUTS.put("it", "italian");
        LANGUAGE_LAYOUTS.put("pt", "portuguese");
        LANGUAGE_LAYOUTS.put("pt-BR", "brazilian");
        LANGUAGE_LAYOUTS.put("da", "danish");
        LANGUAGE_LAYOUTS.put("nb", "norwegian");
        LANGUAGE_LAYOUTS.put("no", "norwegian");
        LANGUAGE_LAYOUTS.put("sv", "swedish");
        LANGUAGE_LAYOUTS.put("fi", "finnish");
        LANGUAGE_LAYOUTS.put("is", "icelandic");
        LANGUAGE_LAYOUTS.put("et", "estonian");
        LANGUAGE_LAYOUTS.put("lv", "latvian");
        LANGUAGE_LAYOUTS.put("lt", "lithuanian");
        LANGUAGE_LAYOUTS.put("pl", "polish");
        LANGUAGE_LAYOUTS.put("cs", "czech");
        LANGUAGE_LAYOUTS.put("sk", "slovak");
        LANGUAGE_LAYOUTS.put("sl", "slovenian");
        LANGUAGE_LAYOUTS.put("hr", "croatian");
        LANGUAGE_LAYOUTS.put("hu", "hungarian");
        LANGUAGE_LAYOUTS.put("tr", "turkish");
        LANGUAGE_LAYOUTS.put("az", "azerbaijani");
        LANGUAGE_LAYOUTS.put("ro", "romanian");                     // Gboard
        LANGUAGE_LAYOUTS.put("ku", "kurdish");                      // Gboard
        LANGUAGE_LAYOUTS.put("sr-Latn", "serbian_latin");           // Gboard
        LANGUAGE_LAYOUTS.put("cnr", "montenegrin_latin");           // Gboard
        LANGUAGE_LAYOUTS.put("cnr-Cyrl", "montenegrin_cyrillic");   // Gboard
        // Cyrillic, Greek, Hebrew, Arabic script, Caucasus.
        LANGUAGE_LAYOUTS.put("ru", "russian");
        LANGUAGE_LAYOUTS.put("uk", "ukrainian");
        LANGUAGE_LAYOUTS.put("be", "belarusian");
        LANGUAGE_LAYOUTS.put("bg", "bulgarian");
        LANGUAGE_LAYOUTS.put("sr", "serbian_cyrillic");             // Gboard
        LANGUAGE_LAYOUTS.put("kk", "kazakh");                       // Gboard
        LANGUAGE_LAYOUTS.put("mn", "mongolian");
        LANGUAGE_LAYOUTS.put("el", "greek");
        LANGUAGE_LAYOUTS.put("he", "hebrew");
        LANGUAGE_LAYOUTS.put("iw", "hebrew");
        LANGUAGE_LAYOUTS.put("ar", "arabic");
        LANGUAGE_LAYOUTS.put("fa", "persian");
        LANGUAGE_LAYOUTS.put("ur", "urdu");                         // Gboard
        LANGUAGE_LAYOUTS.put("ka", "georgian");
        // Indic and Thai (Gboard's layouts).
        LANGUAGE_LAYOUTS.put("hi", "hindi_inscript");
        LANGUAGE_LAYOUTS.put("mr", "marathi");
        LANGUAGE_LAYOUTS.put("ne", "nepali_traditional");
        LANGUAGE_LAYOUTS.put("bn", "bengali_inscript");
        LANGUAGE_LAYOUTS.put("as", "assamese_inscript");
        LANGUAGE_LAYOUTS.put("gu", "gujarati");
        LANGUAGE_LAYOUTS.put("pa", "punjabi");
        LANGUAGE_LAYOUTS.put("or", "odia");
        LANGUAGE_LAYOUTS.put("kn", "kannada");
        LANGUAGE_LAYOUTS.put("ml", "malayalam");
        LANGUAGE_LAYOUTS.put("ta", "tamil");
        LANGUAGE_LAYOUTS.put("te", "telugu");
        LANGUAGE_LAYOUTS.put("si", "sinhala");
        LANGUAGE_LAYOUTS.put("th", "thai_kedmanee");
        // Typed on Latin keys and composed by Gboard (romaji, 2-set, pinyin...).
        LANGUAGE_LAYOUTS.put("ja", FALLBACK_LAYOUT);
        LANGUAGE_LAYOUTS.put("ko", FALLBACK_LAYOUT);
        LANGUAGE_LAYOUTS.put("zh", FALLBACK_LAYOUT);

        SCRIPT_LAYOUTS.put("Arab", "arabic");
        SCRIPT_LAYOUTS.put("Cyrl", "russian");
        SCRIPT_LAYOUTS.put("Hebr", "hebrew");
        SCRIPT_LAYOUTS.put("Grek", "greek");
        SCRIPT_LAYOUTS.put("Deva", "hindi_inscript");
        SCRIPT_LAYOUTS.put("Beng", "bengali_inscript");
        SCRIPT_LAYOUTS.put("Thai", "thai_kedmanee");
        SCRIPT_LAYOUTS.put("Latn", FALLBACK_LAYOUT);

        for (String l : new String[] {"english_us", "english_uk", "english_us_intl",
                "english_us_colemak", "english_us_dvorak", "english_us_workman",
                "english_india", "german", "swiss_german", "french", "french_ca",
                "swiss_french", "french_bepo", "belgian", "spanish", "spanish_latin",
                "italian", "portuguese", "brazilian", "danish", "norwegian", "swedish",
                "finnish", "icelandic", "estonian", "latvian", "lithuanian", "polish",
                "czech", "czech_qwerty", "slovak", "slovenian", "croatian", "hungarian",
                "turkish", "turkish_f", "azerbaijani", "romanian", "kurdish",
                "serbian_latin", "montenegrin_latin"}) {
            LATIN_LAYOUTS.add(l);
        }
    }

    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mSyncRunnable = this::sync;
    private String mLastApplied;

    GboardLayoutSync(Context context) {
        mContext = context;
    }

    void start() {
        // Called on any property change; coalesce onto the main thread.
        SystemProperties.addChangeCallback(() -> {
            mHandler.removeCallbacks(mSyncRunnable);
            mHandler.post(mSyncRunnable);
        });
        mHandler.post(mSyncRunnable);
    }

    /** The active IME changed: Gboard may have just become (or stopped being) it. */
    void onDefaultImeChanged() {
        mLastApplied = null;
        mHandler.post(mSyncRunnable);
    }

    private void sync() {
        final String entry = SystemProperties.get(PROPERTY, "");
        if (entry.isEmpty() || entry.equals(mLastApplied)) return;
        final String ime = Settings.Secure.getString(
                mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        if (ime == null || !ime.startsWith(GBOARD_PACKAGE + "/")) return;

        final InputDeviceIdentifier keyboard = physicalKeyboard();
        if (keyboard == null) return;  // No keyboard yet; the next change retries.

        final InputManager im = mContext.getSystemService(InputManager.class);
        final String current = im.getCurrentKeyboardLayoutForInputDevice(keyboard);
        final KeyboardLayout[] available = im.getKeyboardLayoutsForInputDevice(keyboard);
        final String descriptor = resolve(entry, available);
        if (descriptor == null) {
            // No layouts at all (InputDevices missing): nothing sensible to pick.
            mLastApplied = entry;
            return;
        }
        if (descriptor.equals(current)) {
            mLastApplied = entry;
            return;
        }
        // add first: setCurrent only accepts a layout enabled for the device.
        im.addKeyboardLayoutForInputDevice(keyboard, descriptor);
        im.setCurrentKeyboardLayoutForInputDevice(keyboard, descriptor);
        Slog.i(TAG, "Gboard entry " + entry + " -> physical layout " + layoutName(descriptor));
        mLastApplied = entry;
    }

    /**
     * The layout for a Gboard entry, as a full descriptor of an installed
     * layout: the table (layoutFor), else English (US) -- also when the mapped
     * layout is not installed. Gboard's own layouts carry their locale only in
     * android:keyboardLocale, which A13 does not read, so the table names them
     * explicitly. Never "leave it as it was": that kept, say, the Arabic
     * layout under a language that has no layout of its own, and the keys typed
     * the wrong script. Latin QWERTY is right for those languages anyway:
     * pinyin, romaji or transliteration are typed on Latin keys and composed by
     * Gboard.
     */
    static String resolve(String entry, KeyboardLayout[] available) {
        if (available == null || available.length == 0) return null;
        final String mapped = layoutFor(entry);
        if (mapped != null) {
            final String d = find(available, name -> name.equals(mapped));
            if (d != null) return d;
        }
        final String fallback = find(available, name -> name.equals(FALLBACK_LAYOUT));
        Slog.i(TAG, "Gboard entry " + entry
                + (mapped == null ? " has no mapped layout" : " maps to " + mapped
                        + ", which is not installed") + "; using " + FALLBACK_LAYOUT);
        return fallback;
    }

    private interface NameMatcher {
        boolean matches(String layoutName);
    }

    /** First descriptor, in descriptor order, whose layout name matches. */
    private static String find(KeyboardLayout[] available, NameMatcher matcher) {
        String best = null;
        for (KeyboardLayout layout : available) {
            final String d = layout.getDescriptor();
            if (matcher.matches(layoutName(d)) && (best == null || d.compareTo(best) < 0)) {
                best = d;
            }
        }
        return best;
    }

    /** ".../keyboard_layout_english_us_dvorak" -> "english_us_dvorak". */
    static String layoutName(String descriptor) {
        if (descriptor == null) return "";
        final String last = descriptor.substring(descriptor.lastIndexOf('/') + 1);
        return last.startsWith("keyboard_layout_")
                ? last.substring("keyboard_layout_".length()) : last;
    }

    /**
     * "kmz-Arab-IR:khorasani_turkic_arabic" -> "arabic"; null if neither the
     * language nor its script is in the table.
     */
    static String layoutFor(String entry) {
        final int colon = entry.indexOf(':');
        final String tag = colon < 0 ? entry : entry.substring(0, colon);
        final String variant = colon < 0 ? "" : entry.substring(colon + 1);
        if (VARIANT_LAYOUTS.containsKey(variant)) return VARIANT_LAYOUTS.get(variant);

        final Locale locale = Locale.forLanguageTag(tag);
        final String language = locale.getLanguage();
        if (TextUtils.isEmpty(language)) return null;
        final String script = locale.getScript();
        String region = locale.getCountry();
        if (region.isEmpty() && tag.endsWith("-419")) region = "419";
        String layout = null;
        for (String key : new String[] {
                join(language, script, region), join(language, script, ""),
                join(language, "", region), language}) {
            if (key != null && LANGUAGE_LAYOUTS.containsKey(key)) {
                layout = LANGUAGE_LAYOUTS.get(key);
                break;
            }
        }
        if (layout == null) layout = SCRIPT_LAYOUTS.get(script);
        // A Latin-script entry of a non-Latin language (hi-Latn transliteration,
        // sr-Latn without its own key...) is typed on Latin keys.
        if ("Latn".equals(script) && layout != null && !LATIN_LAYOUTS.contains(layout)) {
            layout = FALLBACK_LAYOUT;
        }
        return layout;
    }

    private static String join(String language, String script, String region) {
        if (script.isEmpty() && region.isEmpty()) return null;
        final StringBuilder b = new StringBuilder(language);
        if (!script.isEmpty()) b.append('-').append(script);
        if (!region.isEmpty()) b.append('-').append(region);
        return b.toString();
    }

    /** The full keyboard wl_keyboard input arrives from, or null. */
    private static InputDeviceIdentifier physicalKeyboard() {
        for (int id : InputDevice.getDeviceIds()) {
            final InputDevice device = InputDevice.getDevice(id);
            if (device != null && !device.isVirtual() && device.isFullKeyboard()) {
                return device.getIdentifier();
            }
        }
        return null;
    }
}

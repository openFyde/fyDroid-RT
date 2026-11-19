package org.chromium.arc.util;

import android.icu.util.ULocale;
import android.os.LocaleList;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class SupportedLanguageTags {
    private static final Map<String, String> sAdjustLanguageTagMap;
    private final Set<String> mSupportedLanguageTags = new TreeSet<>();

    static {
        HashMap<String, String> map = new HashMap<>();
        sAdjustLanguageTagMap = map;
        map.put("iw", "he");
        map.put("iw-IL", "he-IL");
        map.put("in", "id");
        map.put("in-ID", "id-ID");
        map.put("ji", "yi");
        map.put("mo", "ro-MD");
    }

    public SupportedLanguageTags(String[] locales) {
        for (String locale : locales) {
            mSupportedLanguageTags.add(
                    sAdjustLanguageTagMap.getOrDefault(locale, locale));
        }
    }

    private String convertLanguageTag(String tag) {
        if (mSupportedLanguageTags.contains(tag)) {
            return tag;
        }
        ULocale likely = ULocale.addLikelySubtags(ULocale.forLanguageTag(tag));
        String fullTag = likely.toLanguageTag();
        if (mSupportedLanguageTags.contains(fullTag)) {
            return fullTag;
        }
        ULocale langRegion = new ULocale.Builder()
                .setLanguage(likely.getLanguage())
                .setRegion(likely.getCountry())
                .build();
        String langRegionTag = langRegion.toLanguageTag();
        if (mSupportedLanguageTags.contains(langRegionTag)
                || ULocale.addLikelySubtags(langRegion).equals(likely)) {
            return langRegionTag;
        }
        return fullTag;
    }

    public LocaleList convertLanguageTags(String primaryTag, String preferredTags) {
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        tags.add(convertLanguageTag(
                sAdjustLanguageTagMap.getOrDefault(primaryTag, primaryTag)));
        if (!TextUtils.isEmpty(preferredTags)) {
            for (String tag : preferredTags.trim().split("\\s*,\\s*")) {
                if (!tag.isEmpty()) {
                    tags.add(convertLanguageTag(
                            sAdjustLanguageTagMap.getOrDefault(tag, tag)));
                }
            }
        }
        ArrayList<Locale> locales = new ArrayList<>();
        for (String tag : tags) {
            locales.add(Locale.forLanguageTag(tag));
        }
        return new LocaleList(locales.toArray(new Locale[locales.size()]));
    }
}

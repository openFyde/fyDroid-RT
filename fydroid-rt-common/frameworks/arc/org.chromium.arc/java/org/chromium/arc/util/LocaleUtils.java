package org.chromium.arc.util;

import android.content.Context;
import android.os.LocaleList;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.app.LocalePicker;

public final class LocaleUtils {
    private static final String TAG = "ArcLocaleHandler";

    public static void setLocale(Context context, String locale, String preferredLanguages) {
        Log.d(TAG, "setLocale: locale=" + locale
                + " preferredLanguages=" + preferredLanguages);
        LocaleList localeList = new SupportedLanguageTags(
                LocalePicker.getSupportedLocales(context))
                .convertLanguageTags(locale, preferredLanguages);
        Log.d(TAG, "Update locales: " + localeList);
        LocaleList currentLocales = context.getResources().getConfiguration().getLocales();
        if (SystemProperties.get("persist.sys.locale", "").isEmpty()
                || !localeList.equals(currentLocales)) {
            LocalePicker.updateLocales(localeList);
        }
    }
}

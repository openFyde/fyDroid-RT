package com.android.server.arc.locale;

import android.content.Context;
import android.os.SystemProperties;
import com.android.server.SystemService;
import org.chromium.arc.util.LocaleUtils;

/**
 * Sets Android locale from Chrome OS locale passed via ro.boot.locale
 * and preferred languages via ro.boot.preferred_languages.
 */
class ArcLocaleService {

    public static class LifeCycle extends SystemService {
        public LifeCycle(Context context) {
            super(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase != SystemService.PHASE_ACTIVITY_MANAGER_READY) {
                return;
            }
            String locale = SystemProperties.get("ro.boot.locale");
            if (locale.isEmpty()) {
                return;
            }
            LocaleUtils.setLocale(getContext(), locale,
                    SystemProperties.get("ro.boot.preferred_languages"));
        }

        @Override
        public void onStart() {
        }
    }

    ArcLocaleService() {
    }
}

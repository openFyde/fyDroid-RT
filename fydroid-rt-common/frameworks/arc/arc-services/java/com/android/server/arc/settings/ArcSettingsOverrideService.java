package com.android.server.arc.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.android.server.SystemService;

/**
 * Overrides audio-related settings for ARC++ container.
 * Disables system sounds that don't make sense in a container
 * (dock sounds, power sounds, lock sounds, charging sounds).
 */
public final class ArcSettingsOverrideService extends SystemService {
    private static final String TAG = "ArcSettingsOverride";

    public ArcSettingsOverrideService(Context context) {
        super(context);
    }

    private void resetAudioSettings() {
        ContentResolver resolver = getContext().getContentResolver();
        Log.d(TAG, "Turning off sound related system settings");
        Settings.System.putInt(resolver, "sound_effects_enabled", 0);
        Settings.Global.putInt(resolver, "dock_sounds_enabled", 0);
        Settings.Global.putInt(resolver, "power_sounds_enabled", 0);
        Settings.System.putInt(resolver, "lockscreen_sounds_enabled", 0);
        Settings.Secure.putInt(resolver, "charging_sounds_enabled", 0);
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == PHASE_BOOT_COMPLETED) {
            resetAudioSettings();
        }
    }

    @Override
    public void onStart() {
    }
}

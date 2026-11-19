package com.fydeos.wvprobe;

import android.app.Activity;
import android.os.Bundle;

/**
 * Launch with:
 *   am start -n com.fydeos.wvprobe/.ProbeActivity
 *   logcat -s wvprobe:V
 *
 * getKeyRequest blocks (the HAL waits on a mojo round trip), so the probe runs
 * off the main thread; the activity finishes immediately and leaves no window.
 */
public class ProbeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(WidevineProbe::run, "wvprobe").start();
        finish();
    }
}

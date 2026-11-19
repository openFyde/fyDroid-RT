/* Open source replacement for org.chromium.arc.applauncher.util.DisplayIdTranslator
 * from the proprietary com.google.android.chromeos jar.
 *
 * Converts Chrome-side display identifiers (the 64-bit "physical" display
 * IDs that wayland_service hands out) into the Android logical display id
 * via the IWaylandService.translateDisplayId binder call.
 *
 * A DisplayManager.DisplayListener keeps the cached Display[] in sync so
 * translate() can do a simple linear search with the current layout.
 *
 * Logic mirrors the proprietary class 1:1 so in-process consumers that
 * loaded the original keep compiling and behaving identically.
 */
package org.chromium.arc.applauncher.util;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Display;
import android.view.DisplayAddress;

import org.chromium.arc.wayland.IWaylandService;

import java.util.NoSuchElementException;

public class DisplayIdTranslator implements DisplayManager.DisplayListener {
    public static final long DEFAULT_DISPLAY_ID = 255;
    public static final long INVALID_DISPLAY_ID = -1;

    private static final String TAG = "DisplayIdTranslator";

    private final DisplayManager mDisplayManager;
    private Display[] mDisplays;
    private final Object mLock = new Object();
    private IWaylandService mWaylandService;

    public DisplayIdTranslator(Context context) {
        this.mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        this.mDisplays = this.mDisplayManager.getDisplays();
        this.mDisplayManager.registerDisplayListener(
                this, new Handler(Looper.getMainLooper()));
    }

    public int translate(long displayId) {
        if (displayId == INVALID_DISPLAY_ID) {
            return -1;
        }
        if (displayId == DEFAULT_DISPLAY_ID) {
            return 0;
        }
        synchronized (this.mLock) {
            IWaylandService wayland = getWaylandService();
            if (wayland == null) {
                Log.e(TAG, "Failed to get Wayland service");
                return -1;
            }
            try {
                return getLogicalDisplayId(wayland.translateDisplayId(displayId));
            } catch (RemoteException | NoSuchElementException e) {
                Log.e(TAG, "Failed translating display ID " + displayId);
                return -1;
            }
        }
    }

    private IWaylandService getWaylandService() {
        if (this.mWaylandService == null) {
            IBinder binder = ServiceManager.getService("Wayland");
            if (binder != null) {
                this.mWaylandService = IWaylandService.Stub.asInterface(binder);
            }
        }
        return this.mWaylandService;
    }

    private int getLogicalDisplayId(long physicalDisplayId) {
        DisplayAddress.Physical physical =
                DisplayAddress.fromPhysicalDisplayId(physicalDisplayId);
        for (Display display : this.mDisplays) {
            if (physical.equals(display.getAddress())) {
                return display.getDisplayId();
            }
        }
        Log.e(TAG, "Unknown physical display ID " + physicalDisplayId);
        return -1;
    }

    @Override
    public void onDisplayAdded(int displayId) {
        synchronized (this.mLock) {
            this.mDisplays = this.mDisplayManager.getDisplays();
        }
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        synchronized (this.mLock) {
            this.mDisplays = this.mDisplayManager.getDisplays();
        }
    }

    @Override
    public void onDisplayChanged(int displayId) {
    }
}

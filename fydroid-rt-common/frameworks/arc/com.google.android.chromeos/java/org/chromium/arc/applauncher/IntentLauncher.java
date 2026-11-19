/* Open source replacement for org.chromium.arc.applauncher.IntentLauncher
 * from the proprietary com.google.android.chromeos jar.
 *
 * Translates a Chrome-side (physical) display id into the matching
 * Android logical display id via DisplayIdTranslator, then starts an
 * Activity with the bounds / window state encoded in a WindowInfo mojom
 * struct. Public API is preserved so callers compile unchanged.
 */
package org.chromium.arc.applauncher;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;

import org.chromium.arc.applauncher.util.DisplayIdTranslator;
import org.chromium.arc.mojom.WindowInfo;

public class IntentLauncher {
    private static final String TAG = "ArcIntentLauncher";

    private Context mContext;
    private final DisplayIdTranslator mDisplayIdTranslator;

    public IntentLauncher(Context context) {
        this(context, new DisplayIdTranslator(context));
    }

    public IntentLauncher(Context context, DisplayIdTranslator displayIdTranslator) {
        this.mContext = context;
        this.mDisplayIdTranslator = displayIdTranslator;
    }

    public int translateChromeDisplayId(long displayId) {
        return this.mDisplayIdTranslator.translate(displayId);
    }

    public void startActivityWithWindowInfo(Intent intent, WindowInfo windowInfo) {
        ActivityOptions activityOptions = ActivityOptions.makeBasic();
        int arcDisplayId = translateChromeDisplayId(windowInfo.displayId);
        if (arcDisplayId != -1) {
            activityOptions.setLaunchDisplayId(arcDisplayId);
        }
        if (windowInfo.bounds != null) {
            activityOptions.setLaunchBounds(new Rect(
                    windowInfo.bounds.left,
                    windowInfo.bounds.top,
                    windowInfo.bounds.right,
                    windowInfo.bounds.bottom));
        }
        if (windowInfo.windowId > 0) {
            intent.setIdentifier("window_session_id=" + windowInfo.windowId);
        }
        int state = windowInfo.state;
        if (state != ChromeWindowState.CHROME_WINDOW_STATE_NULL) {
            if (state == ChromeWindowState.CHROME_WINDOW_STATE_DEFAULT) {
                activityOptions.setLaunchWindowingMode(0);
            } else if (state == ChromeWindowState.CHROME_WINDOW_STATE_NORMAL) {
                activityOptions.setLaunchWindowingMode(5);
            } else if (state == ChromeWindowState.CHROME_WINDOW_STATE_MINIMIZED) {
                activityOptions.setAvoidMoveToFront();
            } else if (state == ChromeWindowState.CHROME_WINDOW_STATE_MAXIMIZED
                    || state == ChromeWindowState.CHROME_WINDOW_STATE_FULLSCREEN) {
                activityOptions.setLaunchWindowingMode(1);
            } else {
                Log.w(TAG, "launch intent with unsupported window state: "
                        + ChromeWindowState.toString(windowInfo.state));
            }
        } else if (windowInfo.windowId
                > IntentHelperUtils.ARC_SESSION_ID_OFFSET_FOR_RESTORED_LAUNCHING) {
            return;
        } else {
            Log.w(TAG, "Launch intent with closed state but no ARC ghost window");
        }
        this.mContext.startActivity(intent, activityOptions.toBundle());
        Log.i(TAG, "Activity launched for " + intent.toUri(0));
    }
}

/* Open source replacement for com.google.android.chromeos.widget.PopupWindow from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.widget;

import android.util.Log;

import com.google.android.chromeos.ChromeOsSharedLib;
import com.google.android.chromeos.CompanionLibUtil;

/**
 * Wrapper around {@link android.widget.PopupWindow} that lets an app ask
 * Chrome OS to stop clipping the popup to the task bounds. In the
 * proprietary jar the implementation is a no-op that reports
 * ERROR_FUNCTION_NOT_IMPLEMENTED ({@code -4}); we keep the same behaviour
 * so the public API surface matches.
 */
public class PopupWindow {
    private static final String TAG = "PopupWindow";

    private final int mClientLibVersion;
    private final android.widget.PopupWindow mPopupWindow;

    public PopupWindow(int clientLibVersion, android.widget.PopupWindow popupWindow) {
        this.mClientLibVersion = clientLibVersion;
        this.mPopupWindow = popupWindow;
    }

    public int setClippingToTaskDisabled(boolean disable) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_SET_CLIPPING_TO_TASK_DISABLE);
        if (disable && mPopupWindow.isClippingEnabled()) {
            Log.w(TAG, "setClippingToTaskDisabled setting will applied only if"
                    + "PopupWindow.setClippingEnabled(false) is called.");
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_IMPLEMENTED;
        }
        return ChromeOsSharedLib.ERROR_FUNCTION_NOT_IMPLEMENTED;
    }

    public boolean isClippingToTaskDisabled() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_IS_CLIPPING_TO_TASK_DISABLED);
        return false;
    }
}

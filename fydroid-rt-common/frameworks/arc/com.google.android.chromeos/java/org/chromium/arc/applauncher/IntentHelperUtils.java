/* Open source replacement for org.chromium.arc.applauncher.IntentHelperUtils
 * from the proprietary com.google.android.chromeos jar.
 *
 * Free-standing static helpers: camera-intent filter, intent-helper
 * service address constants, and a window_session_id= parser used to
 * plumb the ghost-window id through Intent.setIdentifier. Logic mirrors
 * the proprietary class 1:1 so any caller that loaded the original keeps
 * compiling and behaving identically.
 */
package org.chromium.arc.applauncher;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IntentHelperUtils {
    public static final int ARC_SESSION_ID_OFFSET_FOR_RESTORED_LAUNCHING = 1000000000;
    public static final String INTENT_HELPER_PACKAGE = "org.chromium.arc.intent_helper";
    public static final String INTENT_HELPER_SERVICE =
            "org.chromium.arc.intent_helper.ArcIntentHelperService";
    public static final String KEY_CAMERA_CLOSE_ACTION =
            "org.chromium.arc.intent_helper.close";
    public static final String KEY_CAMERA_IMAGE_DATA = "imageData";
    public static final String KEY_CAMERA_INTENT_HANDLER = "cameraIntentHandler";
    public static final String KEY_CAMERA_PROXY_TASK_ID = "taskId";
    public static final String KEY_CAMERA_VIDEO_URI = "videoUri";
    public static final String KEY_SHOW_GLOBAL_ACTIONS_ACTION =
            "org.chromium.arc.intent_helper.showGlobalActions";

    private static final String TAG = "IntentHelperUtils";
    private static final Pattern sWindowSessionIdPattern =
            Pattern.compile(".*(?:window_session_id=)(\\d+)");

    private IntentHelperUtils() {
    }

    public static boolean isValidCameraAction(String action) {
        return "android.media.action.IMAGE_CAPTURE".equals(action)
                || "android.media.action.IMAGE_CAPTURE_SECURE".equals(action)
                || "android.media.action.VIDEO_CAPTURE".equals(action)
                || "android.media.action.STILL_IMAGE_CAMERA".equals(action)
                || "android.media.action.STILL_IMAGE_CAMERA_SECURE".equals(action)
                || "android.media.action.VIDEO_CAMERA".equals(action);
    }

    public static boolean isLaunchCameraAppAction(String action) {
        return "android.media.action.STILL_IMAGE_CAMERA".equals(action)
                || "android.media.action.STILL_IMAGE_CAMERA_SECURE".equals(action)
                || "android.media.action.VIDEO_CAMERA".equals(action);
    }

    public static int parseWindowSessionIdFromIdentifier(String identifier) {
        if (identifier == null) {
            Log.d(TAG, "intent identifier is null");
            return -1;
        }
        Matcher matcher = sWindowSessionIdPattern.matcher(identifier);
        if (!matcher.find()) {
            Log.w(TAG, "cannot parse window session id field from intent identifier: "
                    + identifier);
            return -1;
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            Log.e(TAG, "Cannot parse window session id number from intent identifier: "
                    + identifier);
            return -1;
        }
    }
}

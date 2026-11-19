/* Open source replacement for org.chromium.arc.applauncher.ChromeWindowState
 * from the proprietary com.google.android.chromeos jar.
 *
 * Not an annotation-style @interface in the proprietary jar: it is a
 * regular class with a static toString() helper and the integer state
 * constants, so we keep the same shape here.
 */
package org.chromium.arc.applauncher;

public class ChromeWindowState {
    public static final int CHROME_WINDOW_STATE_NULL = -1;
    public static final int CHROME_WINDOW_STATE_DEFAULT = 0;
    public static final int CHROME_WINDOW_STATE_NORMAL = 1;
    public static final int CHROME_WINDOW_STATE_MINIMIZED = 2;
    public static final int CHROME_WINDOW_STATE_MAXIMIZED = 3;
    public static final int CHROME_WINDOW_STATE_INACTIVE = 4;
    public static final int CHROME_WINDOW_STATE_FULLSCREEN = 5;
    public static final int CHROME_WINDOW_STATE_LEFT_SNAPPED = 6;
    public static final int CHROME_WINDOW_STATE_RIGHT_SNAPPED = 7;
    public static final int CHROME_WINDOW_STATE_AUTO_POSITIONED = 8;
    public static final int CHROME_WINDOW_STATE_PINNED = 9;
    public static final int CHROME_WINDOW_STATE_TRUSTED_PINNED = 10;
    public static final int CHROME_WINDOW_STATE_PIP = 11;

    public static String toString(int state) {
        switch (state) {
            case CHROME_WINDOW_STATE_NULL:
                return "kNull";
            case CHROME_WINDOW_STATE_DEFAULT:
                return "kDefault";
            case CHROME_WINDOW_STATE_NORMAL:
                return "kNormal";
            case CHROME_WINDOW_STATE_MINIMIZED:
                return "kMinimized";
            case CHROME_WINDOW_STATE_MAXIMIZED:
                return "kMaximized";
            case CHROME_WINDOW_STATE_INACTIVE:
                return "kInactive";
            case CHROME_WINDOW_STATE_LEFT_SNAPPED:
                return "kLeftSnapped";
            case CHROME_WINDOW_STATE_RIGHT_SNAPPED:
                return "kRightSnapped";
            case CHROME_WINDOW_STATE_AUTO_POSITIONED:
                return "kAutoPositioned";
            case CHROME_WINDOW_STATE_PINNED:
                return "kPinned";
            case CHROME_WINDOW_STATE_TRUSTED_PINNED:
                return "kTrustedPinned";
            case CHROME_WINDOW_STATE_PIP:
                return "kPip";
            case CHROME_WINDOW_STATE_FULLSCREEN:
            default:
                return "invalid";
        }
    }
}

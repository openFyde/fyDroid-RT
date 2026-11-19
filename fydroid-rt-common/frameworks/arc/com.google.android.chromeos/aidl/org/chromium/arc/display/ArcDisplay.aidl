package org.chromium.arc.display;

/**
 * Description of a single Chrome OS display as seen by the Wayland shell.
 *
 * Field order matches the proprietary org.chromium.arc.display.ArcDisplay
 * stable AIDL parcelable bit-for-bit, so any consumer (ArcSystemUI,
 * system_server) that loaded the original class keeps reading the same
 * wire format.
 */
parcelable ArcDisplay {
    long physicalDisplayId;
    int globalPositionX;
    int globalPositionY;
    int widthPixels;
    int heightPixels;
    int transform;
    int insetsLeft;
    int insetsTop;
    int insetsRight;
    int insetsBottom;
    int stableInsetsLeft;
    int stableInsetsTop;
    int stableInsetsRight;
    int stableInsetsBottom;
    boolean autoHide;
    float deviceScaleFactor;
    int dpi;
    int overrideDpi;
    boolean displayRemapped;
}

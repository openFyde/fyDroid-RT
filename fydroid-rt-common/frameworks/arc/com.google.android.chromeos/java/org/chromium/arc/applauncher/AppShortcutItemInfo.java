/* Open source replacement for org.chromium.arc.applauncher.AppShortcutItemInfo
 * from the proprietary com.google.android.chromeos jar.
 *
 * Wire-format-compatible with the ARC++ Parcelable so that binder
 * traffic between arc-services / system_server / ArcSystemUI and any
 * still-proprietary consumer keeps working unchanged.
 *
 * Field order in writeToParcel / readFromParcel must not be changed — it
 * matches the proprietary class byte-for-byte.
 */
package org.chromium.arc.applauncher;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AppShortcutItemInfo implements Parcelable {
    public static final Parcelable.Creator<AppShortcutItemInfo> CREATOR =
            new Parcelable.Creator<AppShortcutItemInfo>() {
                @Override
                public AppShortcutItemInfo createFromParcel(Parcel in) {
                    return new AppShortcutItemInfo(in);
                }

                @Override
                public AppShortcutItemInfo[] newArray(int size) {
                    return new AppShortcutItemInfo[size];
                }
            };

    public final byte[] backgroundIconPng;
    public final byte[] foregroundIconPng;
    public final byte[] iconPng;
    public boolean isAdaptiveIcon;
    public final String packageName;
    public int rank;
    public final String shortLabel;
    public final String shortcutId;
    public int type;

    public AppShortcutItemInfo(Bundle bundle) {
        this.shortcutId = bundle.getString(AppShortcutConstants.BUNDLE_KEY_SHORTCUT_ID);
        this.shortLabel = bundle.getString(AppShortcutConstants.BUNDLE_KEY_SHORT_LABEL);
        this.isAdaptiveIcon = bundle.getBoolean(
                AppShortcutConstants.BUNDLE_KEY_IS_ADAPTIVE_ICON, false);
        byte[] icon = bundle.getByteArray(AppShortcutConstants.BUNDLE_KEY_ICON);
        this.iconPng = icon == null ? new byte[0] : icon;
        byte[] foreground = bundle.getByteArray(AppShortcutConstants.BUNDLE_KEY_FOREGROUND_ICON);
        this.foregroundIconPng = foreground == null ? new byte[0] : foreground;
        byte[] background = bundle.getByteArray(AppShortcutConstants.BUNDLE_KEY_BACKGROUND_ICON);
        this.backgroundIconPng = background == null ? new byte[0] : background;
        this.packageName = bundle.getString(AppShortcutConstants.BUNDLE_KEY_PACKAGE_NAME);
        this.type = bundle.getInt(AppShortcutConstants.BUNDLE_KEY_TYPE);
        this.rank = bundle.getInt(AppShortcutConstants.BUNDLE_KEY_RANK);
    }

    private AppShortcutItemInfo(Parcel in) {
        this.shortcutId = in.readString();
        this.shortLabel = in.readString();
        this.isAdaptiveIcon = ((Boolean) in.readValue(null)).booleanValue();
        int iconPngLength = in.readInt();
        this.iconPng = new byte[iconPngLength];
        if (iconPngLength != 0) {
            in.readByteArray(this.iconPng);
        }
        int foregroundIconPngLength = in.readInt();
        this.foregroundIconPng = new byte[foregroundIconPngLength];
        if (foregroundIconPngLength != 0) {
            in.readByteArray(this.foregroundIconPng);
        }
        int backgroundIconPngLength = in.readInt();
        this.backgroundIconPng = new byte[backgroundIconPngLength];
        if (backgroundIconPngLength != 0) {
            in.readByteArray(this.backgroundIconPng);
        }
        this.packageName = in.readString();
        this.type = in.readInt();
        this.rank = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.shortcutId);
        out.writeString(this.shortLabel);
        out.writeValue(Boolean.valueOf(this.isAdaptiveIcon));
        out.writeInt(this.iconPng.length);
        if (this.iconPng.length != 0) {
            out.writeByteArray(this.iconPng);
        }
        out.writeInt(this.foregroundIconPng.length);
        if (this.foregroundIconPng.length != 0) {
            out.writeByteArray(this.foregroundIconPng);
        }
        out.writeInt(this.backgroundIconPng.length);
        if (this.backgroundIconPng.length != 0) {
            out.writeByteArray(this.backgroundIconPng);
        }
        out.writeString(this.packageName);
        out.writeInt(this.type);
        out.writeInt(this.rank);
    }
}

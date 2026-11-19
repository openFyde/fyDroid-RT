package org.chromium.arc.applauncher;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AppShortcutItemInfo implements Parcelable {
    private static final String KEY_PREFIX = "ArcAppShortcutsArcHomeService.";

    public final String shortcutId;
    public final String shortLabel;
    public final String packageName;
    public final byte[] iconPng;
    public final byte[] foregroundIconPng;
    public final byte[] backgroundIconPng;
    public int type;
    public int rank;
    public boolean isAdaptiveIcon;

    public AppShortcutItemInfo(Bundle bundle) {
        shortcutId = bundle.getString(KEY_PREFIX + "shortcutId");
        shortLabel = bundle.getString(KEY_PREFIX + "shortLabel");
        packageName = bundle.getString(KEY_PREFIX + "packageName");
        type = bundle.getInt(KEY_PREFIX + "type");
        rank = bundle.getInt(KEY_PREFIX + "rank");
        isAdaptiveIcon = bundle.getBoolean(KEY_PREFIX + "isAdaptiveIcon");
        iconPng = bundle.getByteArray(KEY_PREFIX + "icon");
        foregroundIconPng = bundle.getByteArray(KEY_PREFIX + "foregroundIcon");
        backgroundIconPng = bundle.getByteArray(KEY_PREFIX + "backgroundIcon");
    }

    private AppShortcutItemInfo(Parcel in) {
        shortcutId = in.readString();
        shortLabel = in.readString();
        packageName = in.readString();
        type = in.readInt();
        rank = in.readInt();
        isAdaptiveIcon = in.readInt() != 0;
        iconPng = in.createByteArray();
        foregroundIconPng = in.createByteArray();
        backgroundIconPng = in.createByteArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(shortcutId);
        out.writeString(shortLabel);
        out.writeString(packageName);
        out.writeInt(type);
        out.writeInt(rank);
        out.writeInt(isAdaptiveIcon ? 1 : 0);
        out.writeByteArray(iconPng);
        out.writeByteArray(foregroundIconPng);
        out.writeByteArray(backgroundIconPng);
    }

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
}

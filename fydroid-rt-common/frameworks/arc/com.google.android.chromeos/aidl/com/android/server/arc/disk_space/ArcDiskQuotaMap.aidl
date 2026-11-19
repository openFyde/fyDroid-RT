/* Open source replacement for com.android.server.arc.disk_space.ArcDiskQuotaMap from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.disk_space;

/**
 * Per-id -> current-space map used by IArcDiskSpaceService. The proprietary
 * class is a stable AIDL parcelable (size-prefixed wire format); field
 * order matches writeToParcel exactly.
 */
parcelable ArcDiskQuotaMap {
    int[] ids;
    long[] curspaces;
}

/* Open source replacement for com.android.server.arc.disk_space.ArcDiskQuotaMaps from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.disk_space;

import com.android.server.arc.disk_space.ArcDiskQuotaMap;

/**
 * Bundle of per-uid / per-gid / per-project-id ArcDiskQuotaMap snapshots
 * returned by IArcDiskSpaceService. The proprietary class is a stable
 * AIDL parcelable (size-prefixed wire format); field order matches
 * writeToParcel exactly.
 */
parcelable ArcDiskQuotaMaps {
    @nullable ArcDiskQuotaMap currentSpacesForUids;
    @nullable ArcDiskQuotaMap currentSpacesForGids;
    @nullable ArcDiskQuotaMap currentSpacesForProjectIds;
}

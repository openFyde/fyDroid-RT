/* Open source replacement for com.android.server.arc.disk_space.IArcDiskSpaceService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.disk_space;

import com.android.server.arc.disk_space.ArcDiskQuotaMaps;

/**
 * Synchronous binder used by Android's StorageStatsManager / installer
 * pipeline to query quota / free-space information backed by the
 * Chrome OS daemon.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..8:
 *   1 isReady
 *   2 isQuotaSupported
 *   3 getQuotaCurrentSpaceForUid
 *   4 getQuotaCurrentSpaceForGid
 *   5 getQuotaCurrentSpaceForProjectId
 *   6 getFreeDiskSpace
 *   7 getArcDiskQuotaMapsForUser
 *   8 getArcDiskQuotaMapsForApp
 */
interface IArcDiskSpaceService {
    // 1
    boolean isReady();
    // 2
    boolean isQuotaSupported();
    // 3
    long getQuotaCurrentSpaceForUid(int uid);
    // 4
    long getQuotaCurrentSpaceForGid(int gid);
    // 5
    long getQuotaCurrentSpaceForProjectId(int projectId);
    // 6
    long getFreeDiskSpace();
    // 7
    @nullable ArcDiskQuotaMaps getArcDiskQuotaMapsForUser();
    // 8
    @nullable ArcDiskQuotaMaps getArcDiskQuotaMapsForApp(int appId);
}

/* Open source replacement for com.android.server.arc.mounter.IArcAppFuseMounterService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.mounter;

/**
 * Synchronous binder that lets Android request AppFuse mounts from the
 * Chrome OS mounter daemon.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..3:
 *   1 mountAppFuse
 *   2 unmountAppFuse
 *   3 openAppFuseFile
 */
interface IArcAppFuseMounterService {
    // 1
    @nullable ParcelFileDescriptor mountAppFuse(int uid, int mountId);
    // 2
    boolean unmountAppFuse(int uid, int mountId);
    // 3
    @nullable ParcelFileDescriptor openAppFuseFile(int uid, int mountId,
            int fileId, int flags);
}

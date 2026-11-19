/* Open source replacement for com.android.server.arc.mounter.IArcObbMounterService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.mounter;

/**
 * Synchronous binder used by Android's OBB system to request
 * Chrome OS-side mount / unmount of OBB container files.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..2:
 *   1 mountObb
 *   2 unmountObb
 */
interface IArcObbMounterService {
    // 1
    boolean mountObb(String obbFile, String targetPath, int ownerGid);
    // 2
    boolean unmountObb(String targetPath);
}

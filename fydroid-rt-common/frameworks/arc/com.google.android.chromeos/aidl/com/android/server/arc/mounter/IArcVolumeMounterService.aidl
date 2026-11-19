/* Open source replacement for com.android.server.arc.mounter.IArcVolumeMounterService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.mounter;

import com.android.server.arc.mounter.IArcFixupListener;

/**
 * Synchronous binder exposed by the Chrome OS volume mounter.
 * reset() clears all per-package fixup state; getPackagesToFixup()
 * returns the list of packages requiring an external-storage fixup
 * and registers a listener for completion callbacks.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..2:
 *   1 reset
 *   2 getPackagesToFixup
 */
interface IArcVolumeMounterService {
    // 1
    void reset();
    // 2
    List<String> getPackagesToFixup(IArcFixupListener listener);
}

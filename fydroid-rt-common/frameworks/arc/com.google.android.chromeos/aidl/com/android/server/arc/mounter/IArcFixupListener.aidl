/* Open source replacement for com.android.server.arc.mounter.IArcFixupListener from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.mounter;

/**
 * Callback invoked once a per-package external-storage fixup completes.
 *
 * One-way: the proprietary Proxy calls transact(..., null, FLAG_ONEWAY).
 * Only one transaction code (1).
 */
oneway interface IArcFixupListener {
    // 1
    void onFixupCompleted(String packageName);
}

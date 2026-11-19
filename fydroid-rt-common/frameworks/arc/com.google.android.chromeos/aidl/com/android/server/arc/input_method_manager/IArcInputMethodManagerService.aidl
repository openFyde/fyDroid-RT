/* Open source replacement for com.android.server.arc.input_method_manager.IArcInputMethodManagerService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.input_method_manager;

import com.android.server.arc.input_method_manager.IInputMethod;

/**
 * Binder exposed by system_server's ArcInputMethodManagerService so that
 * Android's IME bridge can bind / unbind the IArcInputMethod callback and
 * trigger Chrome OS-side IME settings UI.
 *
 * One-way: every proprietary Proxy method calls
 * transact(..., null, FLAG_ONEWAY), and the Stub never writes a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..3:
 *   1 bindInputMethod
 *   2 unbindInputMethod
 *   3 showSettingsActivity
 */
oneway interface IArcInputMethodManagerService {
    // 1
    void bindInputMethod(IInputMethod inputMethod);
    // 2
    void unbindInputMethod(IInputMethod inputMethod);
    // 3
    void showSettingsActivity(String imeId);
}

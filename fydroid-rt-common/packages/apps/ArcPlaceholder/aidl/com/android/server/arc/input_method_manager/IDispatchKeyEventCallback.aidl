/* fydroid addition: no proprietary counterpart. */
package com.android.server.arc.input_method_manager;

/**
 * Answer to {@link IInputMethod#DispatchKeyEvent}: whether the active IME
 * consumed the key. If it did not, Chrome OS handles the key itself.
 */
oneway interface IDispatchKeyEventCallback {
    void onResult(boolean handled);
}

/* Open source replacement for com.android.server.arc.input_method_manager.IInputMethod from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.input_method_manager;

import android.graphics.Rect;
import android.view.KeyEvent;
import com.android.server.arc.input_method_manager.IDispatchKeyEventCallback;
import com.android.server.arc.input_method_manager.IInputConnection;
import com.android.server.arc.input_method_manager.TextInputState;

/**
 * Binder implemented by the ARC IME so that ArcInputMethodManagerService /
 * wayland_service can push focus, blur, text-state updates and show/hide
 * requests to the IME.
 *
 * One-way: every proprietary Proxy method calls
 * transact(..., null, FLAG_ONEWAY), and the Stub never writes a reply.
 *
 * Method names are intentionally capitalised to match the proprietary
 * interface.
 *
 * Method declaration order matches the proprietary transaction codes 1..5:
 *   1 OnFocus
 *   2 OnBlur
 *   3 UpdateTextInputState
 *   4 ShowVirtualKeyboard
 *   5 HideVirtualKeyboard
 */
oneway interface IInputMethod {
    // 1
    void OnFocus(IInputConnection connection, in @nullable TextInputState initial_state);
    // 2
    void OnBlur();
    // 3
    void UpdateTextInputState(in @nullable TextInputState state);
    // 4
    void ShowVirtualKeyboard();
    // 5
    void HideVirtualKeyboard();
    // 6 (fydroid addition, not in the proprietary interface): a hardware key
    // typed into a Chrome OS text field, for the active IME to handle.
    void DispatchKeyEvent(in KeyEvent event, IDispatchKeyEventCallback callback);
    // 7 (fydroid addition): caret of the focused Chrome OS field, in Android
    // screen pixels, for the IME's CursorAnchorInfo.
    void UpdateCaretBounds(in Rect caretBoundsInScreen);
}

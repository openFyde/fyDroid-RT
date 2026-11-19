/* Open source replacement for com.android.server.arc.input_method_manager.IInputConnection from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.input_method_manager;

import android.view.KeyEvent;

/**
 * ARC input-method-manager side of an InputConnection. wayland_service
 * (via IArcInputMethodManagerService) hands this binder to the IME so
 * the IME can drive the peer Chrome text field.
 *
 * One-way: every proprietary Proxy method calls
 * transact(..., null, FLAG_ONEWAY), and the Stub never writes a reply.
 *
 * Method names are intentionally capitalised (CommitText, SendKeyEvent,
 * etc.) to match the proprietary interface and keep the transaction
 * descriptor / method layout binary-compatible.
 *
 * Method declaration order matches the proprietary transaction codes 1..8:
 *   1 CommitText
 *   2 DeleteSurroundingText
 *   3 FinishComposingText
 *   4 SetComposingText
 *   5 RequestTextInputState
 *   6 SetSelection
 *   7 SendKeyEvent
 *   8 SetComposingRegion
 */
oneway interface IInputConnection {
    // 1
    void CommitText(in CharSequence text, int newCursorPos);
    // 2
    void DeleteSurroundingText(int before, int after);
    // 3
    void FinishComposingText();
    // 4
    void SetComposingText(in CharSequence text, int newCursorPos,
            int selectionStart, int selectionEnd);
    // 5
    void RequestTextInputState();
    // 6
    void SetSelection(int selectionStart, int selectionEnd);
    // 7
    void SendKeyEvent(in KeyEvent event);
    // 8
    void SetComposingRegion(int compositionStart, int compositionEnd);
}

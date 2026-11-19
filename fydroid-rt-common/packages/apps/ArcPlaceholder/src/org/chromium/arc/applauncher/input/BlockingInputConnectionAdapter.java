/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chromium.arc.applauncher.input;

import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import com.android.server.arc.input_method_manager.IInputConnection;
import com.android.server.arc.input_method_manager.TextInputState;

/**
 * Makes the asynchronous Chrome text field look synchronous to the IME.
 *
 * {@link android.view.inputmethod.InputConnection} is a blocking API — an IME
 * calls getTextBeforeCursor() and expects an answer — but everything here is
 * oneway binder out to system_server, mojo out to Chrome, and an unsolicited
 * UpdateTextInputState back. So each operation is sent and then waited on until
 * the echo carrying the resulting state arrives.
 *
 * The wait is BOUNDED. The IME calls these on its own main thread, so an
 * unbounded wait would wedge the keyboard for good the first time Chrome fails
 * to answer — the exact failure mode fixed once already in FydroidIME's key
 * path. On timeout we carry on with the last state we had: stale text is
 * recoverable, a dead keyboard is not.
 */
class BlockingInputConnectionAdapter {

    private static final String TAG = "ArcPlaceholder";

    /** Matches ARC's own budget for the Chrome round trip. */
    private static final long UPDATE_TIMEOUT_MS = 2000L;

    private final Object mLock = new Object();

    private IInputConnection mConnection;
    private TextInputState mLatestState;
    private boolean mUpdateDone;

    void setInputConnection(IInputConnection connection) {
        synchronized (mLock) {
            mConnection = connection;
            mLatestState = null;
            mUpdateDone = true;
            mLock.notifyAll();
        }
    }

    TextInputState getLatestState() {
        synchronized (mLock) {
            return mLatestState == null ? null : new TextInputState(mLatestState);
        }
    }

    void updateTextInputState(TextInputState state) {
        synchronized (mLock) {
            mLatestState = state == null ? null : new TextInputState(state);
            // Only an update caused by our own edit releases the wait. Chrome
            // also pushes unsolicited updates (the user moving the caret with
            // the mouse); treating those as our echo would let an operation
            // return against a state that predates it.
            if (state != null && state.mFirstUpdateAfterOperation) {
                mUpdateDone = true;
                mLock.notifyAll();
            }
        }
    }

    private void armWait() {
        synchronized (mLock) {
            mUpdateDone = false;
        }
    }

    private void awaitUpdate() {
        final long deadline = System.currentTimeMillis() + UPDATE_TIMEOUT_MS;
        synchronized (mLock) {
            while (!mUpdateDone) {
                final long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    Log.e(TAG, "Timed out waiting for the new text input state");
                    return;
                }
                try {
                    mLock.wait(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private IInputConnection connection() {
        synchronized (mLock) {
            return mConnection;
        }
    }

    void commitText(CharSequence text, int newCursorPos) throws RemoteException {
        final IInputConnection c = connection();
        if (c == null) return;
        armWait();
        c.CommitText(text, newCursorPos);
        awaitUpdate();
    }

    void deleteSurroundingText(int before, int after) throws RemoteException {
        final IInputConnection c = connection();
        if (c == null) return;
        armWait();
        c.DeleteSurroundingText(before, after);
        awaitUpdate();
    }

    void finishComposingText() throws RemoteException {
        final IInputConnection c = connection();
        if (c == null) return;
        armWait();
        c.FinishComposingText();
        awaitUpdate();
    }

    void setComposingText(CharSequence text, int newCursorPos,
                          int selectionStart, int selectionEnd) throws RemoteException {
        final IInputConnection c = connection();
        if (c == null) return;
        armWait();
        c.SetComposingText(text, newCursorPos, selectionStart, selectionEnd);
        awaitUpdate();
    }

    void requestTextInputState() throws RemoteException {
        final IInputConnection c = connection();
        if (c == null) return;
        armWait();
        c.RequestTextInputState();
        awaitUpdate();
    }

    void setSelection(int selectionStart, int selectionEnd) throws RemoteException {
        final IInputConnection c = connection();
        if (c == null) return;
        armWait();
        c.SetSelection(selectionStart, selectionEnd);
        awaitUpdate();
    }

    /** Fire-and-forget: a key event produces no state echo of its own. */
    void sendKeyEvent(KeyEvent event) throws RemoteException {
        final IInputConnection c = connection();
        if (c != null) c.SendKeyEvent(event);
    }

    void setComposingRegion(int start, int end) throws RemoteException {
        final IInputConnection c = connection();
        if (c != null) c.SetComposingRegion(start, end);
    }
}

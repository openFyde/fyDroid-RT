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

package com.android.server.arc.input_method_manager;

import android.util.Slog;
import android.view.KeyEvent;

import org.chromium.arc.mojom.InputConnection;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;

/**
 * The Android-facing half of a Chrome OS text field.
 *
 * The Android IME edits through an ordinary InputConnection on
 * PlaceholderActivity; that activity forwards each edit here over the
 * {@link IInputConnection} binder, and this class replays it onto the mojo
 * {@link InputConnection} Chrome handed us in
 * {@link ArcInputMethodManagerService#focus}.
 *
 * Long-lived: one instance for the service's lifetime, re-pointed at a new mojo
 * connection on every focus. The binder identity therefore stays stable, which
 * is what lets PlaceholderActivity hold it across focus changes.
 */
public class InputConnectionProxy extends IInputConnection.Stub {

    private static final String TAG = "ArcInputMethodMgr";

    /** SendKeyEvent arrived in InputConnection version 7. */
    private static final int MIN_VERSION_SEND_KEY_EVENT = 7;
    /** SetCompositionRange arrived in version 8. */
    private static final int MIN_VERSION_SET_COMPOSITION_RANGE = 8;

    private final ArcInputMethodManagerService mService;

    private final Object mLock = new Object();
    private InputConnection mConnection;
    private int mConnectionVersion;
    /** Bumped on every connection swap; see GenerationalErrorHandler. */
    private int mGeneration;

    InputConnectionProxy(ArcInputMethodManagerService service) {
        mService = service;
    }

    void setInputConnection(InputConnection connection) {
        final InputConnection old;
        final int generation;
        synchronized (mLock) {
            old = mConnection;
            // Publish immediately so an edit arriving before the version query
            // completes is not dropped; the version only gates two methods.
            mConnection = connection;
            mConnectionVersion = 0;
            generation = ++mGeneration;
        }
        if (old != null) old.close();
        if (connection == null) return;

        final Interface.Proxy.Handler handler =
                ((InputConnection.Proxy) connection).getProxyHandler();
        // Install the error handler NOW, not from the queryVersion reply. Chrome
        // signals blur by dropping the connection (ArcInputMethodManagerService::
        // Blur() is just `active_connection_.reset()` — there is no Blur method
        // in the mojom), so this handler is the ONLY notice we get that the text
        // field went away. Arming it from inside an async round-trip meant a
        // field blurred before the reply landed closed the pipe with no handler
        // attached, and the keyboard stayed up with nothing left to hide it.
        handler.setErrorHandler(new GenerationalErrorHandler(generation));
        handler.queryVersion(version -> {
            synchronized (mLock) {
                // Only adopt the version if this is still the live connection —
                // a focus change may have replaced it while the query was out.
                if (mConnection != connection) return;
                mConnectionVersion = version;
            }
        });
    }

    /**
     * Error handler bound to one particular connection.
     *
     * Chrome hands us a fresh InputConnection on every focus, and replacing the
     * old one closes it — which fires ITS error handler. With a single shared
     * handler that arrived as "the connection died", blurred the *current*
     * focus, and wiped the state the new connection had just installed: the IME
     * then saw a null state, reported EditorInfo.inputType = 0, and could not
     * send a single character. Ignore errors from any connection we have
     * already replaced.
     */
    private final class GenerationalErrorHandler implements ConnectionErrorHandler {
        private final int mOwnGeneration;

        GenerationalErrorHandler(int generation) {
            mOwnGeneration = generation;
        }

        @Override
        public void onConnectionError(MojoException e) {
            synchronized (mLock) {
                if (mOwnGeneration != mGeneration) return;  // stale
            }
            Slog.w(TAG, "InputConnection closed by Chrome: " + e);
            mService.blur();
        }
    }

    private InputConnection connection() {
        synchronized (mLock) {
            return mConnection;
        }
    }

    private int version() {
        synchronized (mLock) {
            return mConnectionVersion;
        }
    }

    // --- IInputConnection ---

    @Override
    public void CommitText(CharSequence text, int newCursorPos) {
        final InputConnection c = connection();
        if (c != null) c.commitText(ImeMojoUtils.toString16(text), newCursorPos);
    }

    @Override
    public void DeleteSurroundingText(int before, int after) {
        final InputConnection c = connection();
        if (c != null) c.deleteSurroundingText(before, after);
    }

    @Override
    public void FinishComposingText() {
        final InputConnection c = connection();
        if (c != null) c.finishComposingText();
    }

    @Override
    public void SetComposingText(CharSequence text, int newCursorPos,
                                 int selectionStart, int selectionEnd) {
        final InputConnection c = connection();
        if (c != null) {
            c.setComposingText(ImeMojoUtils.toString16(text), newCursorPos,
                    ImeMojoUtils.range(selectionStart, selectionEnd));
        }
    }

    @Override
    public void RequestTextInputState() {
        final InputConnection c = connection();
        if (c == null) return;
        // The reply is pushed back out to the IME as a state update flagged
        // "first update after operation", which is how the IME knows the answer
        // reflects its own edit rather than a stale snapshot.
        c.requestTextInputState(state -> mService.sendTextInputStateUpdate(state, true));
    }

    @Override
    public void SetSelection(int selectionStart, int selectionEnd) {
        final InputConnection c = connection();
        if (c != null) c.setSelection(ImeMojoUtils.range(selectionStart, selectionEnd));
    }

    @Override
    public void SendKeyEvent(KeyEvent event) {
        final InputConnection c = connection();
        if (c != null && version() >= MIN_VERSION_SEND_KEY_EVENT) {
            c.sendKeyEvent(ImeMojoUtils.toKeyEventData(event));
        }
    }

    @Override
    public void SetComposingRegion(int compositionStart, int compositionEnd) {
        final InputConnection c = connection();
        if (c != null && version() >= MIN_VERSION_SET_COMPOSITION_RANGE) {
            c.setCompositionRange(ImeMojoUtils.range(compositionStart, compositionEnd));
        }
    }
}

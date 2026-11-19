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

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;

import com.android.server.arc.input_method_manager.IInputConnection;
import com.android.server.arc.input_method_manager.TextInputState;

/**
 * An Android {@link InputConnection} backed by a Chrome OS text field.
 *
 * The IME believes it is editing a normal Android editor; every call is
 * translated into an {@link IInputConnection} operation that ends up in Chrome,
 * and answered from the last {@link TextInputState} Chrome pushed back.
 *
 * Offsets: TextInputState carries only a WINDOW of the field's text
 * ({@code mText}, starting at absolute index {@code mTextStart}), while cursor
 * and selection are absolute. Everything below therefore subtracts
 * {@code mTextStart} before indexing into {@code mText} — mixing the two spaces
 * is the classic way to corrupt a field or hand Chrome a range that underflows.
 */
public class InputConnectionImpl implements InputConnection {

    private static final String TAG = "ArcPlaceholder";

    private final BlockingInputConnectionAdapter mAdapter =
            new BlockingInputConnectionAdapter();

    /** Answers {@link #requestCursorUpdates}; the editor view owns the caret. */
    public interface CursorUpdatesHandler {
        boolean onRequestCursorUpdates(int cursorUpdateMode);
    }

    private volatile CursorUpdatesHandler mCursorUpdatesHandler;

    /** Told whenever the IME edits the field; see PlaceholderActivity. */
    public interface EditListener {
        void onImeEdit();
    }

    private volatile EditListener mEditListener;

    public void setEditListener(EditListener listener) {
        mEditListener = listener;
    }

    private void notifyEdit() {
        final EditListener listener = mEditListener;
        if (listener != null) listener.onImeEdit();
    }

    public void setCursorUpdatesHandler(CursorUpdatesHandler handler) {
        mCursorUpdatesHandler = handler;
    }

    public void updateTextInputState(TextInputState state) {
        mAdapter.updateTextInputState(state);
    }

    public void setInputConnection(IInputConnection connection) {
        mAdapter.setInputConnection(connection);
    }

    /** Describe the Chrome field to the IME, so it picks a matching layout. */
    public void setEditorInfo(EditorInfo info) {
        final TextInputState state = mAdapter.getLatestState();
        if (state == null) {
            info.inputType = 0;
            return;
        }
        state.setEditorInfo(info);
    }

    // --- text retrieval ---

    @Override
    public CharSequence getTextBeforeCursor(int n, int flags) {
        try {
            mAdapter.requestTextInputState();
        } catch (RemoteException e) {
            Log.e(TAG, "getTextBeforeCursor: RequestTextInputState failed", e);
            return null;
        }
        final TextInputState s = mAdapter.getLatestState();
        if (s == null || s.mText == null) return null;
        final int cursor = clamp(s.mCursorPos - s.mTextStart, 0, s.mText.length());
        return s.mText.subSequence(Math.max(0, cursor - n), cursor);
    }

    @Override
    public CharSequence getTextAfterCursor(int n, int flags) {
        try {
            mAdapter.requestTextInputState();
        } catch (RemoteException e) {
            Log.e(TAG, "getTextAfterCursor: RequestTextInputState failed", e);
            return null;
        }
        final TextInputState s = mAdapter.getLatestState();
        if (s == null || s.mText == null) return null;
        final int cursor = clamp(s.mCursorPos - s.mTextStart, 0, s.mText.length());
        return s.mText.subSequence(cursor, Math.min(cursor + n, s.mText.length()));
    }

    @Override
    public CharSequence getSelectedText(int flags) {
        try {
            mAdapter.requestTextInputState();
        } catch (RemoteException e) {
            Log.e(TAG, "getSelectedText: RequestTextInputState failed", e);
            return null;
        }
        final TextInputState s = mAdapter.getLatestState();
        if (s == null || s.mText == null) return null;
        int start = s.mSelectionStart - s.mTextStart;
        int end = s.mSelectionEnd - s.mTextStart;
        if (start == end) return null;                 // no selection
        if (start > end) {                             // reverse selection
            final int t = start; start = end; end = t;
        }
        start = clamp(start, 0, s.mText.length());
        end = clamp(end, 0, s.mText.length());
        if (start >= end) return null;
        return s.mText.subSequence(start, end);
    }

    // --- editing ---

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        notifyEdit();
        try {
            mAdapter.commitText(text, newCursorPosition);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "commitText failed", e);
            return false;
        }
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        notifyEdit();
        final TextInputState s = mAdapter.getLatestState();
        if (s == null) return false;
        final int selStart = Math.min(s.mSelectionStart, s.mSelectionEnd);
        final int selEnd = Math.max(s.mSelectionStart, s.mSelectionEnd);
        try {
            // Collapse the selection to the edge we are deleting from first:
            // Chrome's DeleteSurroundingText is relative to the caret, so with a
            // live selection it would delete the wrong run.
            if (beforeLength > 0) {
                mAdapter.setSelection(selStart, selStart);
                mAdapter.deleteSurroundingText(beforeLength, 0);
            }
            if (afterLength > 0) {
                mAdapter.setSelection(selEnd - beforeLength, selEnd - beforeLength);
                mAdapter.deleteSurroundingText(0, afterLength);
            }
            mAdapter.setSelection(selStart - beforeLength, selEnd - beforeLength);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "deleteSurroundingText failed", e);
            return false;
        }
    }

    @Override
    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
        notifyEdit();
        final TextInputState s = mAdapter.getLatestState();
        if (s == null || s.mText == null) return false;
        final int selStart = Math.min(s.mSelectionStart, s.mSelectionEnd) - s.mTextStart;
        final int selEnd = Math.max(s.mSelectionStart, s.mSelectionEnd) - s.mTextStart;
        final int before = findIndexBackward(s.mText, selStart, Math.max(beforeLength, 0));
        final int after = findIndexForward(s.mText, selEnd, Math.max(afterLength, 0));
        if (before == -1 || after == -1) {
            Log.w(TAG, "deleteSurroundingTextInCodePoints: invalid index, ignoring");
            return true;
        }
        return deleteSurroundingText(selStart - before, after - selEnd);
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        notifyEdit();
        final TextInputState s = mAdapter.getLatestState();
        final int start = s != null ? s.mSelectionStart : 0;
        final int end = s != null ? s.mSelectionEnd : 0;
        try {
            mAdapter.setComposingText(text, newCursorPosition, start, end);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "setComposingText failed", e);
            return false;
        }
    }

    @Override
    public boolean setComposingRegion(int start, int end) {
        try {
            mAdapter.setComposingRegion(start, end);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "setComposingRegion failed", e);
            return false;
        }
    }

    @Override
    public boolean finishComposingText() {
        notifyEdit();
        final TextInputState s = mAdapter.getLatestState();
        if (s == null || (s.mCompositionStart == -1 && s.mCompositionEnd == -1)) {
            return true;
        }
        final int cursor = s.mCursorPos;
        try {
            mAdapter.finishComposingText();
            final TextInputState after = mAdapter.getLatestState();
            // Committing a composition can move the caret in Chrome; put it back
            // where the IME believes it is.
            if (after != null && after.mCursorPos != cursor) {
                mAdapter.setSelection(cursor, cursor);
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "finishComposingText failed", e);
            return false;
        }
    }

    @Override
    public boolean setSelection(int start, int end) {
        try {
            mAdapter.setSelection(start, end);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "setSelection failed", e);
            return false;
        }
    }

    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        try {
            mAdapter.sendKeyEvent(event);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "sendKeyEvent failed", e);
            return false;
        }
    }

    // --- not applicable to a remote Chrome field ---

    @Override public boolean beginBatchEdit() { return true; }
    @Override public boolean endBatchEdit() { return true; }
    @Override public boolean clearMetaKeyStates(int states) { return true; }
    @Override public void closeConnection() {}
    @Override public boolean commitCompletion(CompletionInfo text) { return true; }
    @Override public boolean commitContent(InputContentInfo i, int f, Bundle o) { return false; }
    @Override public boolean commitCorrection(CorrectionInfo info) { return false; }
    @Override public int getCursorCapsMode(int reqModes) {
        return android.text.TextUtils.CAP_MODE_SENTENCES;
    }
    @Override public ExtractedText getExtractedText(ExtractedTextRequest r, int f) { return null; }
    @Override public Handler getHandler() { return null; }
    @Override public boolean performContextMenuAction(int id) { return false; }
    @Override public boolean performEditorAction(int actionCode) { return false; }
    @Override public boolean performPrivateCommand(String action, Bundle data) { return false; }
    @Override public boolean reportFullscreenMode(boolean enabled) { return true; }
    @Override public boolean requestCursorUpdates(int cursorUpdateMode) {
        final CursorUpdatesHandler handler = mCursorUpdatesHandler;
        return handler != null && handler.onRequestCursorUpdates(cursorUpdateMode);
    }

    // --- helpers ---

    private static int clamp(int v, int lo, int hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    /** Index {@code numCodePoints} before {@code from}, or -1 if malformed. */
    private static int findIndexBackward(CharSequence cs, int from, int numCodePoints) {
        int i = from;
        if (i < 0 || i > cs.length()) return -1;
        int remaining = numCodePoints;
        while (remaining > 0) {
            if (--i < 0) return 0;
            if (Character.isLowSurrogate(cs.charAt(i))) {
                if (--i < 0) return -1;
                if (!Character.isHighSurrogate(cs.charAt(i))) return -1;
            }
            remaining--;
        }
        return i;
    }

    /** Index {@code numCodePoints} after {@code from}, or -1 if malformed. */
    private static int findIndexForward(CharSequence cs, int from, int numCodePoints) {
        int i = from;
        if (i < 0 || i > cs.length()) return -1;
        int remaining = numCodePoints;
        while (remaining > 0) {
            if (i >= cs.length()) return cs.length();
            if (Character.isHighSurrogate(cs.charAt(i))) {
                if (++i >= cs.length()) return -1;
                if (!Character.isLowSurrogate(cs.charAt(i))) return -1;
            }
            i++;
            remaining--;
        }
        return i;
    }
}

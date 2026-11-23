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

package org.chromium.arc.applauncher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.android.server.arc.input_method_manager.IArcInputMethodManagerService;
import com.android.server.arc.input_method_manager.IDispatchKeyEventCallback;
import com.android.server.arc.input_method_manager.IInputConnection;
import com.android.server.arc.input_method_manager.IInputMethod;
import com.android.server.arc.input_method_manager.TextInputState;

import org.chromium.arc.applauncher.input.InputConnectionImpl;

/**
 * Gives an Android IME something to edit when the focused text field is
 * Chrome's, not Android's.
 *
 * Android will not run an IME without a focused editor in a focused window, and
 * a Chrome OS text field is neither. So this activity puts up a 1x1 invisible,
 * non-touchable window whose only view claims to be a text editor. The IME binds
 * to it exactly as it would to a real EditText; every edit is forwarded to
 * Chrome through {@link IInputConnection}, and Chrome's state updates come back
 * the other way.
 *
 * Launched by ArcInputMethodManagerService on focus. See
 * docs/gboard-system-ime.plan.
 */
public final class PlaceholderActivity extends Activity {

    private static final String TAG = "ArcPlaceholder";
    private static final String ARC_IMM_SERVICE = "arc_input_method_manager_service";

    private InputMethodManager mImm;
    private PlaceholderInputView mInputView;
    private IArcInputMethodManagerService mArcImmService;
    private InputMethodImpl mInputMethodImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mImm = getSystemService(InputMethodManager.class);

        // 1x1 and not touchable: the window must exist so the IME has a client,
        // but it must never take a pixel or a tap away from whatever the user is
        // actually looking at (a Chrome window).
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 1;
        lp.height = 1;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        getWindow().setAttributes(lp);

        final FrameLayout root = new FrameLayout(this);
        mInputView = new PlaceholderInputView(this);
        // Explicit 1x1 rather than the FrameLayout default: View.canTakeFocus()
        // requires hasSize(), so a zero-sized view can never be focused and the
        // IME can never bind to it.
        root.addView(mInputView, new FrameLayout.LayoutParams(1, 1));
        setContentView(root);

        connectToArcInputMethodManagerService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Assert that this activity HAS a UI, even though its window is 1x1 and
        // transparent. Activity.performResume() throws
        //   "did not call finish() prior to onResume() completing"
        // whenever mVisibleFromClient is false, and the platform force-finishes
        // and kills the process — before it can even call bindInputMethod(),
        // which is exactly what was observed here.
        //
        // It must be done HERE, not in onCreate: performCreate() assigns
        // mVisibleFromClient from the theme's windowNoDisplay *after* onCreate
        // returns (Activity.java:8348), so anything set earlier is overwritten.
        setVisible(true);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        disconnectFromArcInputMethodManagerService();
    }

    @Override
    public void onBackPressed() {
        // Never finish on back: the activity is the IME's only client while a
        // Chrome field is focused. Let the IME consume it (hide the keyboard).
    }

    private void connectToArcInputMethodManagerService() {
        final IBinder binder = ServiceManager.getService(ARC_IMM_SERVICE);
        if (binder == null) {
            Log.e(TAG, "ARC IMM service not published");
            return;
        }
        mArcImmService = IArcInputMethodManagerService.Stub.asInterface(binder);
        mInputMethodImpl = new InputMethodImpl();
        try {
            mArcImmService.bindInputMethod(mInputMethodImpl);
        } catch (RemoteException e) {
            Log.e(TAG, "bindInputMethod failed", e);
        }
    }

    private void disconnectFromArcInputMethodManagerService() {
        if (mArcImmService == null || mInputMethodImpl == null) return;
        try {
            mArcImmService.unbindInputMethod(mInputMethodImpl);
        } catch (RemoteException e) {
            Log.e(TAG, "unbindInputMethod failed", e);
        }
    }

    /** A View that claims to be a text editor so the IME will bind to it. */
    private final class PlaceholderInputView extends View {

        private final InputConnectionImpl mConnection = new InputConnectionImpl();
        // Not View.getHandler(): that is null while detached, and the IMM
        // dereferences the handler it is given to deliver the result.
        private final Handler mMainHandler = new Handler(Looper.getMainLooper());
        private boolean mPendingShowSoftInput;
        // Chrome's caret in Android screen pixels, and what the IME asked for
        // (InputConnection.requestCursorUpdates). UI thread.
        private Rect mCaretInScreen;
        private boolean mCursorMonitor;
        private boolean mCursorImmediatePending;
        // Key-downs handed to the IME and not answered yet. Synchronized:
        // answered from IME edit calls as well as from the IMM callback.
        private final java.util.List<PendingKey> mPendingKeyDowns =
                java.util.Collections.synchronizedList(new java.util.ArrayList<>());

        PlaceholderInputView(Context context) {
            super(context);
            // Not focusable until Chrome actually wants the keyboard, so this
            // window cannot steal focus from a real Android app in between.
            setFocusable(false);
            setFocusableInTouchMode(false);
            // Synchronous, not posted: the IME's edit call runs on the UI thread,
            // and for a word-ending key Gboard then queries the field, which
            // blocks that thread on Chrome, which holds the key until it is
            // answered. A posted answer could only run after the 1 s timeout.
            mConnection.setEditListener(this::onImeEdit);
            mConnection.setCursorUpdatesHandler(mode -> {
                post(() -> {
                    mCursorMonitor = (mode & InputConnection.CURSOR_UPDATE_MONITOR) != 0;
                    if ((mode & InputConnection.CURSOR_UPDATE_IMMEDIATE) != 0) {
                        mCursorImmediatePending = true;
                        sendCursorAnchorInfo();
                    }
                });
                return true;
            });
        }

        /** Chrome moved its caret. UI thread. */
        void updateCaret(Rect caretInScreen) {
            mCaretInScreen = caretInScreen;
            if (mCursorMonitor || mCursorImmediatePending) sendCursorAnchorInfo();
        }

        /**
         * Tells the IME where the caret is. CursorAnchorInfo is in this view's
         * coordinates plus a view-to-screen matrix, so the caret (screen) is
         * shifted by this view's own screen position and the matrix shifts it
         * back: the IME lands on Chrome's caret wherever the 1x1 window sits.
         */
        private void sendCursorAnchorInfo() {
            if (mCaretInScreen == null || !mImm.isActive(this)) return;
            final int[] origin = new int[2];
            getLocationOnScreen(origin);
            final Matrix viewToScreen = new Matrix();
            viewToScreen.setTranslate(origin[0], origin[1]);
            final Rect c = mCaretInScreen;
            final CursorAnchorInfo info = new CursorAnchorInfo.Builder()
                    .setMatrix(viewToScreen)
                    .setInsertionMarkerLocation(c.left - origin[0], c.top - origin[1],
                            c.bottom - origin[1], c.bottom - origin[1],
                            CursorAnchorInfo.FLAG_HAS_VISIBLE_REGION)
                    .build();
            mImm.updateCursorAnchorInfo(this, info);
            mCursorImmediatePending = false;
        }

        @Override
        public boolean onCheckIsTextEditor() {
            return true;
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            mConnection.setEditorInfo(outAttrs);
            Log.i(TAG, "onCreateInputConnection inputType=" + outAttrs.inputType
                    + " pendingShow=" + mPendingShowSoftInput);
            if (mPendingShowSoftInput) {
                // The show request arrived before the IME asked for a
                // connection; now that it has one, replay it.
                post(this::showSoftInputIfPending);
            }
            return mConnection;
        }

        void setInputConnection(IInputConnection connection) {
            mConnection.setInputConnection(connection);
        }

        void updateTextInputState(TextInputState state) {
            mConnection.updateTextInputState(state);
        }

        void showSoftInput() {
            setFocusable(true);
            setFocusableInTouchMode(true);
            final boolean focused = requestFocus();
            if (!focused) {
                // View.canTakeFocus() requires hasSize() for targetSdk >= P, and
                // this view sits in a 1x1 window, so a zero-sized or not-yet
                // laid-out view silently refuses focus — leaving isActive()
                // false and the keyboard never shown. Retry once the next layout
                // pass has given it a size.
                Log.i(TAG, "showSoftInput: view refused focus (" + getWidth() + "x"
                        + getHeight()
                        + " attached=" + isAttachedToWindow() + "), retrying after layout");
                mPendingShowSoftInput = true;
                requestLayout();
                post(this::showSoftInputIfPending);
                return;
            }
            // Force the IME to re-read EditorInfo: the field type may have
            // changed since it last bound.
            mImm.restartInput(this);
            final boolean active = mImm.isActive(this);
            final boolean shown = active
                    && mImm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
            mPendingShowSoftInput = !shown;
            Log.i(TAG, "showSoftInput: size=" + getWidth() + "x" + getHeight()
                    + " hasWindowFocus=" + hasWindowFocus()
                    + " isActive=" + active + " shown=" + shown);
        }

        void showSoftInputIfPending() {
            if (!mPendingShowSoftInput) return;
            setFocusable(true);
            setFocusableInTouchMode(true);
            final boolean focused = requestFocus();
            mImm.restartInput(this);
            final boolean active = mImm.isActive(this);
            final boolean shown = active
                    && mImm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
            mPendingShowSoftInput = !shown;
            Log.i(TAG, "showSoftInputIfPending: size=" + getWidth() + "x" + getHeight()
                    + " focus=" + focused + " isActive=" + active + " shown=" + shown);
        }

        /**
         * Hands a Chrome OS hardware key to the IME bound to this editor, as
         * ViewRootImpl does for a key in a real Android window, and reports
         * whether the IME consumed it. UI thread.
         */
        void dispatchKeyToIme(KeyEvent event, IDispatchKeyEventCallback callback) {
            if (!mImm.isActive(this)) {
                reply(callback, false);
                return;
            }
            final PendingKey pending = new PendingKey(callback);
            if (event.getAction() == KeyEvent.ACTION_DOWN) mPendingKeyDowns.add(pending);
            final int result = mImm.dispatchInputEvent(event, null, (token, handled) -> {
                mPendingKeyDowns.remove(pending);
                pending.answer(handled);
            }, mMainHandler);
            if (result != InputMethodManager.DISPATCH_IN_PROGRESS) {
                mPendingKeyDowns.remove(pending);
                pending.answer(result == InputMethodManager.DISPATCH_HANDLED);
            }
        }

        /**
         * The IME edited the field while key-downs were in flight: it took
         * them. Answer now rather than when the IME finishes the event: Gboard
         * commits a space at once but finishes it only after its word-end work,
         * ~1 s later, by which time Chrome had typed the space itself as well.
         * Called from the IME's edit, on whichever thread it arrives.
         */
        void onImeEdit() {
            final PendingKey[] pending;
            synchronized (mPendingKeyDowns) {
                pending = mPendingKeyDowns.toArray(new PendingKey[0]);
                mPendingKeyDowns.clear();
            }
            for (PendingKey p : pending) p.answer(true);
        }

        /** One key offered to the IME; answered once, by whichever comes first. */
        private final class PendingKey {
            private final IDispatchKeyEventCallback mCallback;
            private boolean mAnswered;

            PendingKey(IDispatchKeyEventCallback callback) {
                mCallback = callback;
            }

            void answer(boolean handled) {
                synchronized (this) {
                    if (mAnswered) return;
                    mAnswered = true;
                }
                reply(mCallback, handled);
            }
        }

        private void reply(IDispatchKeyEventCallback callback, boolean handled) {
            try {
                callback.onResult(handled);
            } catch (RemoteException e) {
                Log.w(TAG, "DispatchKeyEvent reply failed", e);
            }
        }

        void hideSoftInput() {
            mImm.hideSoftInputFromWindow(getWindowToken(), 0);
            // Drop focus again so the placeholder does not hold it while idle.
            setFocusable(false);
            setFocusableInTouchMode(false);
        }
    }

    /** What ArcInputMethodManagerService pushes Chrome's focus state into. */
    private final class InputMethodImpl extends IInputMethod.Stub {

        @Override
        public void OnFocus(IInputConnection connection, TextInputState initialState) {
            Log.i(TAG, "OnFocus from Chrome OS type="
                    + (initialState != null ? initialState.mTextInputType : -1));
            // State goes in on THIS (binder) thread — see UpdateTextInputState.
            mInputView.setInputConnection(connection);
            mInputView.updateTextInputState(initialState);
            // Only the IME interaction needs the UI thread.
            mInputView.post(() -> {
                // Raise the keyboard on focus. Chrome only sends
                // ShowVirtualKeyboard when its own notion of VK visibility
                // *changes*; on a fresh focus it expects the IME to come up by
                // itself, so waiting for that message leaves the user with a
                // focused Chrome field and no keyboard.
                //
                // TYPE_NULL means Chrome is telling us the field takes no IME
                // (see toAndroidInputType in ArcInputMethodManagerService).
                if (initialState != null
                        && initialState.mTextInputType != android.text.InputType.TYPE_NULL) {
                    mInputView.showSoftInput();
                }
            });
        }

        @Override
        public void OnBlur() {
            Log.i(TAG, "OnBlur from Chrome OS");
            // Clear the connection on THIS thread, for the same reason
            // UpdateTextInputState is not posted — and so that ordering with
            // respect to OnFocus is preserved. Posting it meant a blur could be
            // delivered *after* the OnFocus that superseded it, nulling the
            // connection the new focus had just installed and leaving the IME
            // with nothing to type into.
            mInputView.setInputConnection(null);
            mInputView.post(mInputView::hideSoftInput);
        }

        @Override
        public void UpdateTextInputState(TextInputState state) {
            // Deliberately NOT posted to the UI thread.
            //
            // The IME calls InputConnection methods on the view's UI thread, and
            // BlockingInputConnectionAdapter blocks that thread waiting for the
            // state echo this call carries. Posting the echo to the same thread
            // is a guaranteed deadlock: the runnable cannot run until the wait
            // ends, and the wait cannot end until the runnable runs — so every
            // getTextBeforeCursor() burned the full 2s timeout and typing
            // visibly lagged. The adapter is synchronized, so handing it the
            // state straight off the binder thread is both safe and the only
            // way the waiter can ever be woken.
            mInputView.updateTextInputState(state);
        }

        @Override
        public void ShowVirtualKeyboard() {
            Log.i(TAG, "ShowVirtualKeyboard from Chrome OS");
            mInputView.post(mInputView::showSoftInput);
        }

        @Override
        public void HideVirtualKeyboard() {
            mInputView.post(mInputView::hideSoftInput);
        }

        @Override
        public void UpdateCaretBounds(Rect caretBoundsInScreen) {
            mInputView.post(() -> mInputView.updateCaret(caretBoundsInScreen));
        }

        @Override
        public void DispatchKeyEvent(KeyEvent event, IDispatchKeyEventCallback callback) {
            mInputView.post(() -> mInputView.dispatchKeyToIme(event, callback));
        }
    }
}

/*
 * Copyright (C) 2024 Fyde Innovations
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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseLongArray;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.inputmethod.InputMethodManagerInternal;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.ImeInfo;
import org.chromium.arc.mojom.InputConnection;
import org.chromium.arc.mojom.InputMethodManagerHost;
import org.chromium.arc.mojom.InputMethodManagerInstance;
import org.chromium.arc.mojom.KeyEventData;
import org.chromium.mojo.system.MojoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bridges Android's InputMethodManager to Chrome OS.
 *
 * Two directions, and they are the same switch seen from both ends:
 *
 * <ul>
 *   <li><b>Chrome OS IME selected</b> — Chrome calls SwitchImeTo with its bridge
 *       id, we make FydroidIME the Android IME, and Chrome's IME result reaches
 *       Android apps through it (arc_ime_bridge).</li>
 *   <li><b>Android IME selected</b> — Chrome calls SwitchImeTo with e.g. Gboard's
 *       id, we make Gboard the Android IME, and FydroidIME is simply no longer
 *       bound. A focused Chrome text field then reaches Gboard via
 *       {@link #focus}, which points {@link InputConnectionProxy} at Chrome's
 *       InputConnection and lets PlaceholderActivity give the IME something to
 *       edit.</li>
 * </ul>
 *
 * Chrome is the single owner of that choice; nothing here ever forces an IME of
 * its own accord (beyond seeding one when the device has none at all).
 *
 * See docs/gboard-system-ime.plan.
 */
public final class ArcInputMethodManagerService extends IArcInputMethodManagerService.Stub
        implements InputMethodManagerInstance, ArcService,
                   InputMethodManagerInternal.InputMethodListListener {

    private static final String TAG = "ArcInputMethodMgr";

    private static final String BINDER_SERVICE_NAME = "arc_input_method_manager_service";

    /** InputMethodManagerInstance arrived in ArcBridgeHost version 38. */
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 38;

    /**
     * Chrome's hard-coded name for "the Android-side bridge IME"
     * (kChromeOSIMEIdInArcContainer). It is baked into
     * ArcInputMethodManagerService on the Chrome side, so we cannot rename it —
     * we translate to and from FydroidIME's real id at the boundary instead.
     * Renaming FydroidIME to match would churn SELinux labels, deploy paths and
     * ArcDataDeviceService's package-uid check for no benefit.
     */
    private static final String CHROME_BRIDGE_IME_ID =
            "org.chromium.arc.ime/.ArcInputMethodService";

    /** fydroid's actual bridge IME. */
    private static final String FYDROID_IME_ID = "com.fydeos.inputmethod/.FydroidIME";
    private static final String FYDROID_IME_PACKAGE = "com.fydeos.inputmethod";
    /** GApps' Google voice typing: a one-shot voice helper, not a keyboard. */
    private static final String GOOGLE_VOICE_IME_PACKAGE = "com.google.android.tts";

    /** The invisible editor that gives an Android IME a Chrome field to edit. */
    private static final String PLACEHOLDER_ACTIVITY =
            "org.chromium.arc.applauncher.PlaceholderActivity";

    private final Context mContext;
    private final ContentResolver mResolver;
    private final InputMethodManager mImm;

    private final InputMethodManagerHostWrapper mHost = new InputMethodManagerHostWrapper();
    private final RemoteCallbackList<IInputMethod> mInputMethods = new RemoteCallbackList<>();
    private final Map<String, InputMethodInfo> mImeIdToImi = new HashMap<>();

    private InputConnectionProxy mInputConnectionProxy;

    /** Settings > Physical keyboard follows Gboard's sidebar language. */
    private GboardLayoutSync mGboardLayoutSync;

    /** Down time of each key Chrome has offered and not yet released. */
    private final SparseLongArray mKeyDownTimes = new SparseLongArray();

    private final Object mStateLock = new Object();
    private TextInputState mActiveTextInputState;

    /**
     * Grace period before a dropped InputConnection counts as a blur.
     *
     * Chrome has no Blur method — it signals blur by closing the connection —
     * but it closes it for a second reason too: switching IME re-activates the
     * engine, which runs Blur() then Focus() with a fresh connection. Measured
     * on device, reselecting an IME from the menu:
     *
     *   .679 switched the Android IME to ...VoiceInputMethodService
     *   .685 InputConnection closed by Chrome: FAILED_PRECONDITION
     *   .686 focused a text field            <- 1ms later
     *
     * The close arrives BEFORE the replacement exists, so
     * InputConnectionProxy.GenerationalErrorHandler cannot tell it from a real
     * blur by generation. Acting on it immediately hides the keyboard the user
     * just picked an IME for (OnBlur -> hideSoftInput, posted, so it can even
     * land after the new focus). Waiting a beat costs nothing on a real blur.
     */
    private static final long BLUR_GRACE_MS = 150L;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mBlurRunnable = this::blurNow;

    public static class LifeCycle extends SystemService {
        private final ArcInputMethodManagerService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcInputMethodManagerService(context);
        }

        @Override
        public void onStart() {
            mService.initialize();
            publishBinderService(BINDER_SERVICE_NAME, mService);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onBootCompleted();
            }
        }
    }

    ArcInputMethodManagerService(Context context) {
        mContext = context;
        mResolver = context.getContentResolver();
        mImm = context.getSystemService(InputMethodManager.class);
    }

    private void initialize() {
        mInputConnectionProxy = new InputConnectionProxy(this);
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    void onBootCompleted() {
        ensureFydroidImeEnabled();
        seedDefaultImeIfUnset();
        registerSettingsObserver();
        startImeBridge();
        mGboardLayoutSync = new GboardLayoutSync(mContext);
        mGboardLayoutSync.start();
        Slog.i(TAG, "Boot completed, default IME is " + getCurrentImeId());
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "ARC bridge ready, registering InputMethodManagerInstance");
        host.onInputMethodManagerInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "ArcBridgeHost version " + version + " < "
                + MINIMUM_ARC_BRIDGE_HOST_VERSION + "; Chrome OS will not see Android IMEs");
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mHost.reset();
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mHost.reset();
    }

    // --- InputMethodManagerInstance ---

    @Override
    public void init(InputMethodManagerHost host, InitResponse response) {
        final InputMethodManagerInternal immInternal =
                LocalServices.getService(InputMethodManagerInternal.class);
        if (immInternal != null) {
            immInternal.registerInputMethodListListener(this);
        } else {
            Slog.w(TAG, "InputMethodManagerInternal unavailable; "
                    + "installed-IME changes will not reach Chrome OS");
        }

        // The first report has to wait for the host proxy — see the note on
        // InputMethodManagerHostWrapper.init().
        mHost.init(host, this, response, () -> {
            notifyInstalledImeInfo();
            notifyDefaultIme();
        });
    }

    @Override
    public void enableIme(String imeId, boolean enable, EnableImeResponse response) {
        response.call(enableImeInternal(toAndroidImeId(imeId), enable));
    }

    @Override
    public void switchImeTo(String imeId, SwitchImeToResponse response) {
        final String androidId = toAndroidImeId(imeId);
        if (!FYDROID_IME_ID.equals(androidId) && !mImeIdToImi.containsKey(androidId)) {
            Slog.w(TAG, "Switch to unknown IME requested: " + imeId);
            response.call(false);
            return;
        }
        setCurrentImeId(androidId);
        response.call(true);
    }

    @Override
    public void focus(InputConnection connection, org.chromium.arc.mojom.TextInputState state) {
        // Beat any blur still inside its grace period: an IME switch closes the
        // old connection microseconds before re-focusing with a new one.
        mHandler.removeCallbacks(mBlurRunnable);
        Slog.i(TAG, "Chrome OS focused a text field (type=" + (state != null ? state.type : -1)
                + "), bound IMEs=" + mInputMethods.getRegisteredCallbackCount());
        mInputConnectionProxy.setInputConnection(
                InputConnection.MANAGER.buildThreadSafeProxy((InputConnection.Proxy) connection));

        final TextInputState converted = convertTextInputState(state, true);
        synchronized (mStateLock) {
            mActiveTextInputState = converted;
        }

        // Android will not run an IME without a focused editor, and a Chrome text
        // field is not one. PlaceholderActivity supplies a 1x1 invisible editor
        // for the IME to bind to; every edit it receives is forwarded straight
        // back to Chrome through mInputConnectionProxy.
        focusPlaceholder(mContext);

        final int n = mInputMethods.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mInputMethods.getBroadcastItem(i).OnFocus(mInputConnectionProxy, converted);
            } catch (RemoteException e) {
                Slog.e(TAG, "OnFocus failed", e);
            }
        }
        mInputMethods.finishBroadcast();
    }

    @Override
    public void updateTextInputState(org.chromium.arc.mojom.TextInputState state) {
        // Deliberately does NOT treat TextInputType.NONE as a blur. Chrome owns
        // that decision: the keyboard hides when Chrome drops the
        // InputConnection (see InputConnectionProxy.GenerationalErrorHandler),
        // and nowhere else. Clicking a non-text part of the window that owns the
        // field is not a blur — Chrome keeps the field focused and expects the
        // keyboard to stay, exactly as its own virtual keyboard behaves.
        sendTextInputStateUpdate(state, false);
    }

    @Override
    public void showVirtualKeyboard() {
        Slog.i(TAG, "Chrome OS asked to show the virtual keyboard, bound IMEs="
                + mInputMethods.getRegisteredCallbackCount());
        final int n = mInputMethods.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mInputMethods.getBroadcastItem(i).ShowVirtualKeyboard();
            } catch (RemoteException e) {
                Slog.e(TAG, "ShowVirtualKeyboard failed", e);
            }
        }
        mInputMethods.finishBroadcast();
    }

    @Override
    public void hideVirtualKeyboard() {
        Slog.i(TAG, "Chrome OS asked to hide the virtual keyboard");
        final int n = mInputMethods.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mInputMethods.getBroadcastItem(i).HideVirtualKeyboard();
            } catch (RemoteException e) {
                Slog.e(TAG, "HideVirtualKeyboard failed", e);
            }
        }
        mInputMethods.finishBroadcast();
    }

    /**
     * A hardware key typed into a Chrome OS text field (chromium-patches 0208).
     * The bound placeholder hands it to the active IME, which commits through
     * the same InputConnection as its on-screen keys; Chrome handles the key
     * itself when we answer false. Runs on the mojo thread, like every other
     * InputMethodManagerInstance call.
     */
    @Override
    public void dispatchKeyEvent(KeyEventData data, DispatchKeyEventResponse response) {
        final long now = android.os.SystemClock.uptimeMillis();
        final long downTime;
        if (data.pressed) {
            if (!data.isRepeat || mKeyDownTimes.indexOfKey(data.scanCode) < 0) {
                mKeyDownTimes.put(data.scanCode, now);
            }
            downTime = mKeyDownTimes.get(data.scanCode);
        } else {
            downTime = mKeyDownTimes.get(data.scanCode, now);
            mKeyDownTimes.delete(data.scanCode);
        }
        final KeyEvent event =
                ImeMojoUtils.toAndroidKeyEvent(data, physicalKeyboardDeviceId(), downTime);
        if (event == null || mInputMethods.getRegisteredCallbackCount() == 0) {
            response.call(false);
            return;
        }

        // The answer goes back on this thread, and exactly once: Chrome holds
        // the key until it arrives (and gives up after 200ms on its own).
        final Looper looper = Looper.myLooper();
        final Handler replyHandler = looper != null ? new Handler(looper) : null;
        final AtomicBoolean answered = new AtomicBoolean();
        final IDispatchKeyEventCallback callback = new IDispatchKeyEventCallback.Stub() {
            @Override
            public void onResult(boolean handled) {
                if (answered.getAndSet(true)) return;
                if (replyHandler != null) {
                    replyHandler.post(() -> response.call(handled));
                } else {
                    response.call(handled);
                }
            }
        };

        final int n = mInputMethods.beginBroadcast();
        try {
            // One placeholder at a time; it is the only client of the IME.
            if (n > 0) {
                mInputMethods.getBroadcastItem(0).DispatchKeyEvent(event, callback);
            } else {
                callback.onResult(false);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "DispatchKeyEvent failed", e);
            try {
                callback.onResult(false);
            } catch (RemoteException ignored) {
                // Local call; cannot happen.
            }
        } finally {
            mInputMethods.finishBroadcast();
        }
    }

    /**
     * The caret of the focused Chrome OS field (chromium-patches 0208), already
     * in Android screen pixels: Chrome adds the window's offset and the display
     * scale. The placeholder reports it to the IME as CursorAnchorInfo, so
     * Gboard's candidates / toolbar sit at the Chrome field, not at the 1x1
     * placeholder window.
     */
    @Override
    public void updateCaretBounds(org.chromium.arc.mojom.Rect caret) {
        if (caret == null) return;
        final android.graphics.Rect rect =
                new android.graphics.Rect(caret.left, caret.top, caret.right, caret.bottom);
        final int n = mInputMethods.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mInputMethods.getBroadcastItem(i).UpdateCaretBounds(rect);
            } catch (RemoteException e) {
                Slog.e(TAG, "UpdateCaretBounds failed", e);
            }
        }
        mInputMethods.finishBroadcast();
    }

    /**
     * The device wl_keyboard input arrives from, whose KeyCharacterMap carries
     * the Android physical keyboard layout. VIRTUAL_KEYBOARD (plain US) if there
     * is none.
     */
    private static int physicalKeyboardDeviceId() {
        for (int id : InputDevice.getDeviceIds()) {
            final InputDevice device = InputDevice.getDevice(id);
            if (device != null && !device.isVirtual() && device.isFullKeyboard()) {
                return id;
            }
        }
        return KeyCharacterMap.VIRTUAL_KEYBOARD;
    }

    // --- IArcInputMethodManagerService (called by PlaceholderActivity) ---

    @Override
    public void bindInputMethod(IInputMethod inputMethod) {
        if (mInputMethods.getRegisteredCallbackCount() > 0) {
            Slog.w(TAG, "An InputMethod is already bound");
        }
        mInputMethods.register(inputMethod);

        // Replay the live focus: the placeholder is normally launched *because*
        // a field was focused, so it binds after OnFocus has already gone out.
        final TextInputState state;
        synchronized (mStateLock) {
            state = mActiveTextInputState;
        }
        if (state != null) {
            try {
                inputMethod.OnFocus(mInputConnectionProxy, state);
            } catch (RemoteException e) {
                Slog.e(TAG, "OnFocus replay failed", e);
            }
        }
    }

    @Override
    public void unbindInputMethod(IInputMethod inputMethod) {
        if (!mInputMethods.unregister(inputMethod)) {
            Slog.e(TAG, "unbindInputMethod: not registered");
        }
    }

    @Override
    public void showSettingsActivity(String imeId) {
        final InputMethodInfo imi = mImeIdToImi.get(toAndroidImeId(imeId));
        if (imi == null || imi.getSettingsActivity() == null) {
            Slog.w(TAG, "No settings activity for " + imeId);
            return;
        }
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(imi.getPackageName(), imi.getSettingsActivity());
        intent.setPackage(imi.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    // --- InputMethodManagerInternal.InputMethodListListener ---

    @Override
    public void onInputMethodListUpdated(List<InputMethodInfo> imis, int userId) {
        if (updateImeIdToImiMap(imis)) {
            restoreRememberedImes();
            notifyInstalledImeInfo();
        }
    }

    // --- internals ---

    /**
     * Chrome dropped the InputConnection. That is either a real blur or the
     * teardown half of an IME switch — see {@link #BLUR_GRACE_MS}. Defer, and
     * let a focus() arriving in the meantime cancel it.
     */
    void blur() {
        mHandler.removeCallbacks(mBlurRunnable);
        mHandler.postDelayed(mBlurRunnable, BLUR_GRACE_MS);
    }

    private void blurNow() {
        Slog.i(TAG, "Chrome OS text field lost focus");
        synchronized (mStateLock) {
            mActiveTextInputState = null;
        }
        final int n = mInputMethods.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mInputMethods.getBroadcastItem(i).OnBlur();
            } catch (RemoteException e) {
                Slog.e(TAG, "OnBlur failed", e);
            }
        }
        mInputMethods.finishBroadcast();
    }

    void sendTextInputStateUpdate(org.chromium.arc.mojom.TextInputState state,
                                  boolean firstUpdateAfterOperation) {
        final TextInputState converted =
                convertTextInputState(state, firstUpdateAfterOperation);
        synchronized (mStateLock) {
            mActiveTextInputState = converted;
        }
        final int n = mInputMethods.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mInputMethods.getBroadcastItem(i).UpdateTextInputState(converted);
            } catch (RemoteException e) {
                Slog.e(TAG, "UpdateTextInputState failed", e);
            }
        }
        mInputMethods.finishBroadcast();
    }

    private static TextInputState convertTextInputState(
            org.chromium.arc.mojom.TextInputState in, boolean firstUpdateAfterOperation) {
        final TextInputState out = new TextInputState();
        out.mCursorPos = in.cursorPos;
        out.mText = ImeMojoUtils.fromString16(in.text);
        out.mTextStart = in.textRange != null ? in.textRange.start : 0;
        out.mTextEnd = in.textRange != null ? in.textRange.end : 0;
        out.mSelectionStart = in.selectionRange != null ? in.selectionRange.start : 0;
        out.mSelectionEnd = in.selectionRange != null ? in.selectionRange.end : 0;
        out.mTextInputType = toAndroidInputType(in.type);
        out.mShouldDoLearning = in.shouldDoLearning;
        out.mFlags = in.flags;
        out.mFirstUpdateAfterOperation = in.firstUpdateAfterOperation || firstUpdateAfterOperation;
        if (in.compositionTextRange == null) {
            out.mCompositionStart = -1;
            out.mCompositionEnd = -1;
        } else {
            out.mCompositionStart = in.compositionTextRange.start;
            out.mCompositionEnd = in.compositionTextRange.end;
        }
        return out;
    }

    /**
     * arc.mojom.TextInputType -> android.text.InputType.
     *
     * This is what TextInputState.setEditorInfo() hands the IME as
     * EditorInfo.inputType, so it decides which layout Gboard puts up: passing
     * the mojo ordinal through unmapped would show a text keyboard for every
     * field, including number and email ones.
     */
    private static int toAndroidInputType(int mojoType) {
        switch (mojoType) {
            case 0:  // NONE
                return InputType.TYPE_NULL;
            case 2:  // PASSWORD
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            case 3:  // SEARCH
                return InputType.TYPE_CLASS_TEXT;
            case 4:  // EMAIL
                return InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            case 5:  // NUMBER
                return InputType.TYPE_CLASS_NUMBER;
            case 6:  // TELEPHONE
                return InputType.TYPE_CLASS_PHONE;
            case 7:  // URL
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI;
            case 8:  // DATE
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE;
            case 9:  // TIME
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME;
            case 10: // DATETIME
                return InputType.TYPE_CLASS_DATETIME
                        | InputType.TYPE_DATETIME_VARIATION_NORMAL;
            case 11: // ANDROID_NULL — Chrome says "this field takes no IME".
                return InputType.TYPE_NULL;
            case 1:  // TEXT
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

    /**
     * Bring the placeholder to the front so it takes Android window focus.
     *
     * Two callers, one mechanism. This one wants an editor for Chrome's text
     * field; ArcWindowManagerService wants a focus sink for when Chrome OS
     * activates a window that is not ARC's at all — Android WM cannot represent
     * "nothing focused", so something of ours has to hold focus, and this
     * activity is already the thing that does. It is 1x1, translucent,
     * excluded from recents and singleInstance, and it never asks for the
     * keyboard on its own (PlaceholderInputView only calls showSoftInput when
     * the bridge tells it to), so as a plain focus sink it is invisible and
     * silent — the outgoing app's IME hides through the stock
     * "focused view is not a text editor" path.
     */
    public static void focusPlaceholder(Context context) {
        try {
            final Intent intent = new Intent(PLACEHOLDER_ACTIVITY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION
                    | Intent.FLAG_ACTIVITY_NO_USER_ACTION
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        } catch (Exception e) {
            // Not fatal: without the placeholder, Chrome text fields simply do
            // not reach the Android IME, and a deactivated ARC keeps the focus
            // it had. Android app fields are unaffected.
            Slog.w(TAG, "Failed to launch " + PLACEHOLDER_ACTIVITY, e);
        }
    }

    /** Chrome's bridge-IME id -> FydroidIME; everything else passes through. */
    private static String toAndroidImeId(String imeId) {
        return CHROME_BRIDGE_IME_ID.equals(imeId) ? FYDROID_IME_ID : imeId;
    }

    /** FydroidIME -> Chrome's bridge-IME id; everything else passes through. */
    private static String toChromeImeId(String imeId) {
        return FYDROID_IME_ID.equals(imeId) ? CHROME_BRIDGE_IME_ID : imeId;
    }

    private String getCurrentImeId() {
        final String id = Settings.Secure.getString(
                mResolver, Settings.Secure.DEFAULT_INPUT_METHOD);
        return id == null ? "" : id;
    }

    private void setCurrentImeId(String imeId) {
        if (getCurrentImeId().equals(imeId)) return;
        Settings.Secure.putInt(mResolver, Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE, -1);
        Settings.Secure.putString(mResolver, Settings.Secure.DEFAULT_INPUT_METHOD, imeId);
        Slog.i(TAG, "Chrome OS switched the Android IME to " + imeId);
    }

    private boolean enableImeInternal(String imeId, boolean enable) {
        final Map<String, List<String>> imes = InputMethodSettingsUtils
                .parseEnabledInputMethodsString(Settings.Secure.getString(
                        mResolver, Settings.Secure.ENABLED_INPUT_METHODS));
        final boolean present = imes.containsKey(imeId);
        if (enable == present) return true;
        if (enable) {
            imes.put(imeId, new ArrayList<>());
        } else {
            imes.remove(imeId);
        }
        final boolean ok = Settings.Secure.putString(mResolver,
                Settings.Secure.ENABLED_INPUT_METHODS,
                InputMethodSettingsUtils.buildEnabledInputMethodsString(imes));
        Slog.i(TAG, (enable ? "Enabled " : "Disabled ") + imeId + " -> " + ok);
        if (ok) rememberImeChoice(imeId, enable);
        return ok;
    }

    /**
     * Record what Chrome asked for, so a boot cannot quietly undo it.
     *
     * ENABLED_INPUT_METHODS does not survive a container restart for an IME
     * installed in /data. Measured on device: Gboard and Google voice typing were
     * enabled, ARC restarted, and both came back disabled with no EnableIme
     * request from Chrome at all. IMMS validates the enabled set during
     * systemRunning() (10:52:35.847), but our own report shows the IME list is
     * still being built well past that — Gboard first appears at .853 and voice
     * typing only at 38.547 — so at validation time neither is installed as far
     * as IMMS is concerned, and both are dropped from the setting for good.
     *
     * The user then has no way back: we report the IME (off), Chrome removes it
     * from the Chrome OS list, and the picker no longer offers it.
     *
     * This is not a second opinion about which IME should be enabled — it is
     * Chrome's own decision, replayed after a boot race destroyed it. A real
     * disable from Chrome clears the memo, so the two can never disagree.
     */
    private static final String ENABLED_IMES_MEMO = "fydroid_enabled_android_imes";

    private void rememberImeChoice(String imeId, boolean enable) {
        final Set<String> memo = readImeMemo();
        if (enable ? !memo.add(imeId) : !memo.remove(imeId)) return;
        Settings.Secure.putString(mResolver, ENABLED_IMES_MEMO,
                TextUtils.join(":", memo));
    }

    private Set<String> readImeMemo() {
        final Set<String> out = new LinkedHashSet<>();
        final String raw = Settings.Secure.getString(mResolver, ENABLED_IMES_MEMO);
        if (raw == null || raw.isEmpty()) return out;
        for (String id : raw.split(":")) {
            if (!id.isEmpty()) out.add(id);
        }
        return out;
    }

    /**
     * Re-enable any remembered IME that is installed but no longer enabled.
     * Called when the IME list changes, which is when the late /data IMEs
     * finally show up.
     */
    private void restoreRememberedImes() {
        final Set<String> memo = readImeMemo();
        if (memo.isEmpty()) return;
        final Map<String, List<String>> enabled = InputMethodSettingsUtils
                .parseEnabledInputMethodsString(Settings.Secure.getString(
                        mResolver, Settings.Secure.ENABLED_INPUT_METHODS));
        for (String imeId : memo) {
            if (enabled.containsKey(imeId)) continue;
            if (!mImeIdToImi.containsKey(imeId)) continue;  // not installed (yet)
            enabled.put(imeId, new ArrayList<>());
            Slog.i(TAG, "Restoring " + imeId + ", which a boot dropped from "
                    + "enabled_input_methods");
        }
        Settings.Secure.putString(mResolver, Settings.Secure.ENABLED_INPUT_METHODS,
                InputMethodSettingsUtils.buildEnabledInputMethodsString(enabled));
    }

    private boolean updateImeIdToImiMap(List<InputMethodInfo> imis) {
        boolean changed = imis.size() != mImeIdToImi.size();
        if (!changed) {
            for (InputMethodInfo imi : imis) {
                if (!mImeIdToImi.containsKey(imi.getId())) {
                    changed = true;
                    break;
                }
            }
        }
        mImeIdToImi.clear();
        for (InputMethodInfo imi : imis) {
            mImeIdToImi.put(imi.getId(), imi);
        }
        return changed;
    }

    private List<ImeInfo> createImeInfoList() {
        final List<InputMethodInfo> enabled = mImm.getEnabledInputMethodList();
        final List<ImeInfo> out = new ArrayList<>();
        for (InputMethodInfo imi : mImeIdToImi.values()) {
            // Never offer the bridge IME as a choice: to Chrome OS it is not an
            // "Android IME", it is Chrome's own keyboard reaching into Android.
            // Nor Google voice typing, which only works when another keyboard
            // hands off to it.
            final String pkg = imi.getPackageName();
            if (FYDROID_IME_PACKAGE.equals(pkg)
                    || GOOGLE_VOICE_IME_PACKAGE.equals(pkg)) continue;

            final ImeInfo info = new ImeInfo();
            info.imeId = imi.getId();
            info.displayName = imi.loadLabel(mContext.getPackageManager()).toString();
            info.enabled = false;
            for (InputMethodInfo e : enabled) {
                if (e.getId().equals(imi.getId())) {
                    info.enabled = true;
                    break;
                }
            }
            // No settings deep-link yet: Chrome only accepts an intent: URL and
            // tolerates an empty one (ArcInputMethodState drops anything else).
            info.settingsUrl = "";
            // Keep Android IMEs available with a keyboard attached.
            //
            // This maps to ArcInputMethodState's entry.always_allowed_, and
            // Chrome's availability test is `all_allowed || always_allowed_`
            // where all_allowed is tablet mode. Reporting false does more than
            // hide the IME in clamshell: GetAvailableInputMethods() comes back
            // EMPTY, which sends UpdateInputMethodEntryWithImeInfo() down
            //
            //   if (installed_imes.empty()) {
            //     prefs_.UpdateEnabledImes({});   // clears the prefs FIRST
            //     SyncEnabledImesInArc();          // -> EnableIme(id, false)
            //     return;
            //   }
            //
            // and SyncEnabledImesInArc()'s `!enabled_ime_ids_on_prefs.count(id)`
            // guard — written precisely to stop a laptop-mode deactivation
            // reaching the container — cannot fire, because the prefs it reads
            // were just cleared. So the IME is DISABLED in Android, we then
            // report it (off), and Chrome drops it from the Chrome OS list.
            // That is a one-way trip: the only way back in is being reported
            // (on), and the user has no UI for that. Observed on device with both
            // Gboard and Google voice typing vanishing from the picker, each
            // needing a manual `ime enable` in the container to return.
            //
            // fydroid wants Gboard as THE system IME, not a tablet-only one, so
            // the tablet gate does not apply here.
            info.isAllowedInClamshellMode = true;
            out.add(info);
        }
        return out;
    }

    private void notifyInstalledImeInfo() {
        final List<ImeInfo> imes;
        synchronized (mImeIdToImi) {
            updateImeIdToImiMap(mImm.getInputMethodList());
            imes = createImeInfoList();
        }
        final StringBuilder sb = new StringBuilder();
        for (ImeInfo i : imes) {
            sb.append(i.imeId).append(i.enabled ? "(on) " : "(off) ");
        }
        Slog.i(TAG, "Reporting " + imes.size() + " Android IME(s) to Chrome OS: " + sb);
        mHost.onImeInfoChanged(imes);
    }

    private void notifyDefaultIme() {
        if (mGboardLayoutSync != null) mGboardLayoutSync.onDefaultImeChanged();
        final String current = getCurrentImeId();
        if (!current.isEmpty()) {
            mHost.onActiveImeChanged(toChromeImeId(current));
        }
    }

    private void onEnabledInputMethodsChanged() {
        notifyInstalledImeInfo();
    }

    /**
     * Start the arc_ime_bridge native daemon. Still needed whenever FydroidIME
     * is the active IME — that is the path Chrome's own IME uses to type into
     * Android apps.
     */
    private void startImeBridge() {
        try {
            final ProcessBuilder pb = new ProcessBuilder("/system/bin/arc_ime_bridge");
            pb.redirectErrorStream(true);
            pb.start();
            Slog.i(TAG, "Started arc_ime_bridge");
        } catch (Exception e) {
            Slog.e(TAG, "Failed to start arc_ime_bridge", e);
        }
    }

    /**
     * Keep FydroidIME in the enabled list. It must stay available so Chrome can
     * switch back to it whenever a Chrome OS IME is selected — but it is never
     * forced to be the *active* one.
     */
    private void ensureFydroidImeEnabled() {
        final Map<String, List<String>> imes = InputMethodSettingsUtils
                .parseEnabledInputMethodsString(Settings.Secure.getString(
                        mResolver, Settings.Secure.ENABLED_INPUT_METHODS));
        if (!imes.containsKey(FYDROID_IME_ID)) {
            imes.put(FYDROID_IME_ID, new ArrayList<>());
            Settings.Secure.putString(mResolver, Settings.Secure.ENABLED_INPUT_METHODS,
                    InputMethodSettingsUtils.buildEnabledInputMethodsString(imes));
            Slog.i(TAG, "Added FydroidIME to enabled_input_methods");
        }
    }

    /**
     * Seed FydroidIME as the default only when the device has no usable one — a
     * fresh image, or a default naming an IME that is no longer installed.
     * Deliberately not a re-assert: Chrome owns the choice.
     */
    private void seedDefaultImeIfUnset() {
        final String current = getCurrentImeId();
        if (!current.isEmpty() && isImeInstalled(current)) {
            Slog.i(TAG, "Leaving default IME as " + current);
            return;
        }
        Settings.Secure.putString(mResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD, FYDROID_IME_ID);
        Settings.Secure.putInt(mResolver,
                Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE, -1);
        Slog.i(TAG, "Seeded FydroidIME as default (was: " + current + ")");
    }

    private boolean isImeInstalled(String imeId) {
        if (mImm == null) return true;
        for (InputMethodInfo imi : mImm.getInputMethodList()) {
            if (imi.getId().equals(imeId)) return true;
        }
        return false;
    }

    private void registerSettingsObserver() {
        final ContentObserver observer = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                final String setting = uri != null ? uri.getLastPathSegment() : "";
                if (Settings.Secure.DEFAULT_INPUT_METHOD.equals(setting)) {
                    notifyDefaultIme();
                } else if (Settings.Secure.ENABLED_INPUT_METHODS.equals(setting)) {
                    ensureFydroidImeEnabled();
                    onEnabledInputMethodsChanged();
                }
            }
        };
        mResolver.registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.DEFAULT_INPUT_METHOD),
                false, observer);
        mResolver.registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.ENABLED_INPUT_METHODS),
                false, observer);
    }
}

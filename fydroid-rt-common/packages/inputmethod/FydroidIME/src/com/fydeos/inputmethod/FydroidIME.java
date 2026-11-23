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

package com.fydeos.inputmethod;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.InputType;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Headless InputMethodService for fydroid-rt.
 *
 * Input comes from hardware keys (wl_keyboard → InputReaderArc).
 * When the arc_ime_bridge daemon is available, key events are forwarded to
 * Chrome OS's IME via Mojo for proper composition/CJK/layout support.
 * Falls back to local keycodeToChar() when the bridge is unavailable.
 */
public class FydroidIME extends InputMethodService {

    private static final String TAG = "FydroidIME";
    private static final String WAYLAND_SERVICE = "Wayland";
    private static final String WAYLAND_DESCRIPTOR =
            "org.chromium.arc.wayland.IWaylandService";
    private static final int TRANSACTION_setClipboardText = 30;
    private static final int TRANSACTION_registerClipboardCallback = 31;
    private static final int TRANSACTION_getFocusedWindowOffset = 42;

    // arc_ime_bridge Binder transaction codes (FydroidIME → bridge).
    private static final int IME_REGISTER_CALLBACK = 1;
    private static final int IME_ON_TEXT_INPUT_TYPE_CHANGED = 2;
    private static final int IME_ON_CURSOR_RECT_CHANGED = 3;
    private static final int IME_SEND_KEY_EVENT = 4;
    private static final int IME_ON_CANCEL_COMPOSITION = 5;
    private static final int IME_SHOW_VIRTUAL_KEYBOARD = 6;
    private static final int IME_ON_CURSOR_RECT_WITH_SURROUNDING_TEXT = 7;

    // How much text on each side of the cursor Chrome gets. Enough context for
    // its engine to delete and autocorrect, small enough to stay cheap: this is
    // fetched from the app over binder on every cursor move.
    private static final int SURROUNDING_TEXT_LENGTH = 100;

    // arc_ime_bridge callback transaction codes (bridge → FydroidIME).
    private static final int CB_INSERT_TEXT = 1;
    private static final int CB_SET_COMPOSITION_TEXT = 2;
    private static final int CB_CONFIRM_COMPOSITION_TEXT = 3;
    private static final int CB_EXTEND_SELECTION_AND_DELETE = 4;
    private static final int CB_SET_SELECTION_TEXT = 5;
    private static final int CB_SET_COMPOSING_REGION = 6;
    private static final int CB_ON_KEYBOARD_APPEARANCE = 7;

    private KeyCharacterMap mKeyCharMap;

    // Clipboard bridge
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private ClipboardManager mClipboardManager;
    private IBinder mWaylandService;
    private String mLastSentText = "";
    private boolean mSettingClipboard = false;

    // IME bridge
    private IBinder mImeBridge;
    private volatile boolean mImeBridgeConnected = false;

    // English fast-path (adaptive, CJK-safe). Normally every key round-trips to
    // Chrome's IME (onKeyDown → bridge → InsertText), which is correct but means
    // English input depends on Chrome for characters Chrome just echoes back. We
    // can't tell "English a" from "pinyin a" before sending the first key, but we
    // CAN observe Chrome's behavior: a non-composing layout (US xkb) only ever
    // InsertTexts; a composing IME (CJK/autocorrect) SetComposingTexts. So:
    //   - until verified, round-trip every key (current behavior);
    //   - verification is PER KEY: when Chrome plain-InsertTexts exactly the
    //     character Android's own (US) key map gives for the one key in
    //     flight, with no composition seen this session, that character is
    //     verified and later presses of its key are committed LOCALLY (no
    //     echo) → no Chrome dependency for them;
    //   - a non-composing layout that remaps keys (Arabic, Cyrillic, Hebrew,
    //     AZERTY...) only verifies the keys it shares with US (space, digits,
    //     AZERTY's s/d/f...), so every remapped key keeps round-tripping. One
    //     matching key is no proof for the rest: a single "confirmed" flag
    //     typed Arabic as Latin after the first space;
    //   - if Chrome EVER composes this session, verify nothing → keep the
    //     bridge, so CJK is structurally safe (it composes from key 1).
    // Reset per input session (onStartInput). All touched on the IME main thread.
    private boolean mComposing = false;            // Chrome is mid-composition
    private boolean mImeComposingSeen = false;     // composition seen this session
    // Local characters Chrome has inserted verbatim for their own key.
    private final java.util.Set<String> mVerifiedLocalChars = new java.util.HashSet<>();
    // Keys echoed to Chrome and consumed since its last edit, and what the
    // fast-path would commit for the latest. An InsertText is attributed to a
    // key only when exactly one is in flight; under fast typing it may answer
    // an earlier key, and a dead key consumes with no insert of its own.
    private int mKeysInFlight = 0;
    private String mLastEchoedLocalChar = null;
    // Keys whose DOWN we echoed to Chrome — so onKeyUp echoes the UP only for
    // those (a fast-local key's down was never sent; an asymmetric up would
    // desync Chrome's IME state). Main-thread only.
    private final java.util.Set<Integer> mEchoedDownKeys = new java.util.HashSet<>();

    // After this much typing idle, drop the passthrough confirmation and
    // re-learn the IME mode on the next key — closes the "switch Chrome OS input
    // method mid-field" hole (the switch involves a pause; no onStartInput).
    private static final long IME_REVALIDATE_IDLE_MS = 500;
    private long mLastKeyTimeMs = 0;

    // Chrome OS virtual keyboard. We have no keyboard of our own: an app's
    // showSoftInput() is forwarded to Chrome (ShowVirtualKeyboardIfEnabled),
    // and Chrome reports back where it put the keyboard. The IME window we
    // show is an empty, untouchable placeholder sized to Chrome's keyboard —
    // its only job is to give the app the right IME insets, so ADJUST_RESIZE /
    // ADJUST_PAN move the focused field out from under Chrome's keyboard.
    // Main-thread only.
    private View mPlaceholderView;
    private int mVkHeightPx = 0;
    // Last editor state sent to Chrome, so a bare show request (no new
    // onStartInput) can re-assert it after a hide reported type NONE.
    private int mLastMojoType = 0;
    private int mLastMojoFlags = 0;
    // Last caret rect sent to Chrome, in screen space — resent alongside the
    // surrounding text, which Chrome delivers through the same call.
    private final Rect mLastCursorRect = new Rect();
    // Absolute selection in the editor, from EditorInfo at focus and from
    // onUpdateSelection thereafter. -1 while unknown.
    private int mSelStart = -1;
    private int mSelEnd = -1;

    private final ClipboardManager.OnPrimaryClipChangedListener mClipListener = () -> {
        if (mSettingClipboard) return;
        try {
            ClipData clip = mClipboardManager.getPrimaryClip();
            if (clip == null || clip.getItemCount() == 0) return;
            CharSequence text = clip.getItemAt(0).getText();
            if (text == null) return;
            String str = text.toString();
            if (str.isEmpty() || str.equals(mLastSentText)) return;
            mLastSentText = str;
            sendTextToNative(str);
        } catch (SecurityException e) {
            Log.w(TAG, "Clipboard read denied");
        }
    };

    private final Binder mClipboardCallbackBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            if (code == 1) {
                String text = data.readString();
                if (text != null && !text.isEmpty()) {
                    mMainHandler.post(() -> onNativeClipboardChanged(text));
                }
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    /**
     * Callback binder that receives ImeInstance operations from arc_ime_bridge.
     * These calls originate from Chrome OS's IME system via Mojo.
     */
    private final Binder mImeCallbackBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            switch (code) {
                case CB_INSERT_TEXT: {
                    String text = data.readString();
                    int newCursorPos = data.readInt();
                            mMainHandler.post(() -> {
                        // A plain insert, no composition seen this session,
                        // of exactly what our own key map gives for the one
                        // key in flight → that key can be committed locally.
                        mComposing = false;
                        if (!mImeComposingSeen && mKeysInFlight == 1
                                && text != null
                                && text.equals(mLastEchoedLocalChar)) {
                            mVerifiedLocalChars.add(text);
                        }
                        mKeysInFlight = 0;
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null && text != null) {
                            ic.commitText(text, newCursorPos);
                        }
                    });
                    return true;
                }

                case CB_SET_COMPOSITION_TEXT: {
                    String text = data.readString();
                    int numSegments = data.readInt();
                    // Skip segment details — Android's setComposingText doesn't
                    // use them directly (they're for visual styling in the IME view).
                    for (int i = 0; i < numSegments; i++) {
                        data.readInt(); // start
                        data.readInt(); // end
                        data.readInt(); // style
                    }
                    boolean hasSelection = data.readInt() != 0;
                    if (hasSelection) {
                        data.readInt(); // selStart
                        data.readInt(); // selEnd
                    }
                    mMainHandler.post(() -> {
                        // Chrome is composing → this IME is NOT a passthrough
                        // layout; never take the local fast-path this session.
                        mImeComposingSeen = true;
                        mVerifiedLocalChars.clear();
                        mKeysInFlight = 0;
                        mComposing = text != null && !text.isEmpty();
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null && text != null) {
                            ic.setComposingText(text, 1);
                        }
                    });
                    return true;
                }

                case CB_CONFIRM_COMPOSITION_TEXT: {
                    mMainHandler.post(() -> {
                        mComposing = false;
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.finishComposingText();
                        }
                    });
                    return true;
                }

                case CB_EXTEND_SELECTION_AND_DELETE: {
                    long before = data.readLong();
                    long after = data.readLong();
                    mMainHandler.post(() -> {
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.deleteSurroundingText((int) before, (int) after);
                        }
                    });
                    return true;
                }

                case CB_SET_SELECTION_TEXT: {
                    int start = data.readInt();
                    int end = data.readInt();
                    mMainHandler.post(() -> {
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.setSelection(start, end);
                        }
                    });
                    return true;
                }

                case CB_SET_COMPOSING_REGION: {
                    int start = data.readInt();
                    int end = data.readInt();
                    mMainHandler.post(() -> {
                        // Composing region set → composing IME; disable fast-path.
                        mImeComposingSeen = true;
                        mVerifiedLocalChars.clear();
                        mKeysInFlight = 0;
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.setComposingRegion(start, end);
                        }
                    });
                    return true;
                }

                case CB_ON_KEYBOARD_APPEARANCE: {
                    int left = data.readInt();
                    int top = data.readInt();
                    int right = data.readInt();
                    int bottom = data.readInt();
                    boolean available = data.readInt() != 0;
                    Log.d(TAG, "OnKeyboardAppearance: " + left + "," + top + ","
                            + right + "," + bottom + " available=" + available);
                    final int height = available ? Math.max(0, bottom - top) : 0;
                    mMainHandler.post(() -> onVirtualKeyboardBounds(available, height));
                    return true;
                }

                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mKeyCharMap = loadKeyCharMap();
        Log.i(TAG, "started, kcm=" + (mKeyCharMap != null ? "type=" + mKeyCharMap.getKeyboardType() : "manual"));

        // Clipboard bridge — IME is exempt from clipboard READ focus checks
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        connectToNative();

        // IME bridge — connect to arc_ime_bridge daemon
        connectToImeBridge();
    }

    @Override
    public void onDestroy() {
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(mClipListener);
        }
        super.onDestroy();
    }

    // -------------------------------------------------------------------------
    // Clipboard bridge
    // -------------------------------------------------------------------------

    private void connectToNative() {
        mWaylandService = ServiceManager.getService(WAYLAND_SERVICE);
        if (mWaylandService == null) {
            Log.w(TAG, "Wayland service not found — retrying in 2s");
            mMainHandler.postDelayed(this::connectToNative, 2000);
            return;
        }
        registerClipboardCallback();
        mClipboardManager.addPrimaryClipChangedListener(mClipListener);
    }

    private void registerClipboardCallback() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(WAYLAND_DESCRIPTOR);
            data.writeStrongBinder(mClipboardCallbackBinder);
            mWaylandService.transact(TRANSACTION_registerClipboardCallback, data, reply, 0);
            reply.readException();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register clipboard callback", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private void sendTextToNative(String text) {
        if (mWaylandService == null) return;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(WAYLAND_DESCRIPTOR);
            data.writeString(text);
            mWaylandService.transact(TRANSACTION_setClipboardText, data, reply, 0);
            reply.readException();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to send clipboard text", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private void onNativeClipboardChanged(String text) {
        if (text.equals(mLastSentText)) return;
        mSettingClipboard = true;
        mClipboardManager.setPrimaryClip(ClipData.newPlainText("clipboard", text));
        mSettingClipboard = false;
    }

    // -------------------------------------------------------------------------
    // IME bridge (arc_ime_bridge daemon)
    // -------------------------------------------------------------------------

    private void connectToImeBridge() {
        mImeBridge = ServiceManager.getService("arc_ime");
        if (mImeBridge == null) {
            Log.w(TAG, "arc_ime service not found — retrying in 2s");
            mMainHandler.postDelayed(this::connectToImeBridge, 2000);
            return;
        }
        Log.i(TAG, "Connected to arc_ime_bridge");

        // Register our callback binder so the bridge can send us ImeInstance calls.
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeStrongBinder(mImeCallbackBinder);
            mImeBridge.transact(IME_REGISTER_CALLBACK, data, reply, 0);
            mImeBridgeConnected = true;
            Log.i(TAG, "Registered IME callback with arc_ime_bridge");
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register IME callback", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Android {@link KeyEvent} keycode → what Chrome needs to identify the key,
     * packed as {@code (ui::KeyboardCode << 16) | evdev scancode}.
     *
     * <p>Chrome resolves a forwarded key to a {@code ui::DomCode} — from
     * {@code scan_code} when that is non-zero, otherwise by reading
     * {@code key_code} as a {@code ui::KeyboardCode}, which is what the mojom
     * contracts it to be. If neither resolves, Chrome's {@code StructTraits::Read}
     * returns false; mojo treats a deserialisation failure as a protocol
     * violation and closes the endpoint, and nothing re-establishes the ImeHost
     * pipe short of restarting ARC. So an unmappable key is not a dropped
     * keystroke, it is the end of this IME's Chrome connection — hence the
     * table, and hence refusing to send anything it cannot describe.
     *
     * <p>Derived mechanically from AOSP {@code data/keyboards/Generic.kl}
     * (evdev → Android keycode), Chromium {@code dom_code_data.inc}
     * (evdev → DomCode) and {@code dom_us_layout_data.h} (DomCode →
     * ui::KeyboardCode). Keys absent here are gamepad buttons and TV/remote
     * keys, which have no DomCode and were never text input.
     */
    private static final SparseIntArray KEY_TO_CHROME = new SparseIntArray(135);
    static {
        final SparseIntArray m = KEY_TO_CHROME;
        m.put(KeyEvent.KEYCODE_HOME, 0xAC00AC);
        m.put(KeyEvent.KEYCODE_BACK, 0xA6009E);
        m.put(KeyEvent.KEYCODE_0, 0x30000B);
        m.put(KeyEvent.KEYCODE_1, 0x310002);
        m.put(KeyEvent.KEYCODE_2, 0x320003);
        m.put(KeyEvent.KEYCODE_3, 0x330004);
        m.put(KeyEvent.KEYCODE_4, 0x340005);
        m.put(KeyEvent.KEYCODE_5, 0x350006);
        m.put(KeyEvent.KEYCODE_6, 0x360007);
        m.put(KeyEvent.KEYCODE_7, 0x370008);
        m.put(KeyEvent.KEYCODE_8, 0x380009);
        m.put(KeyEvent.KEYCODE_9, 0x39000A);
        m.put(KeyEvent.KEYCODE_DPAD_UP, 0x260067);
        m.put(KeyEvent.KEYCODE_DPAD_DOWN, 0x28006C);
        m.put(KeyEvent.KEYCODE_DPAD_LEFT, 0x250069);
        m.put(KeyEvent.KEYCODE_DPAD_RIGHT, 0x27006A);
        m.put(KeyEvent.KEYCODE_VOLUME_UP, 0xAF0073);
        m.put(KeyEvent.KEYCODE_VOLUME_DOWN, 0xAE0072);
        m.put(KeyEvent.KEYCODE_POWER, 0x980074);
        m.put(KeyEvent.KEYCODE_A, 0x41001E);
        m.put(KeyEvent.KEYCODE_B, 0x420030);
        m.put(KeyEvent.KEYCODE_C, 0x43002E);
        m.put(KeyEvent.KEYCODE_D, 0x440020);
        m.put(KeyEvent.KEYCODE_E, 0x450012);
        m.put(KeyEvent.KEYCODE_F, 0x460021);
        m.put(KeyEvent.KEYCODE_G, 0x470022);
        m.put(KeyEvent.KEYCODE_H, 0x480023);
        m.put(KeyEvent.KEYCODE_I, 0x490017);
        m.put(KeyEvent.KEYCODE_J, 0x4A0024);
        m.put(KeyEvent.KEYCODE_K, 0x4B0025);
        m.put(KeyEvent.KEYCODE_L, 0x4C0026);
        m.put(KeyEvent.KEYCODE_M, 0x4D0032);
        m.put(KeyEvent.KEYCODE_N, 0x4E0031);
        m.put(KeyEvent.KEYCODE_O, 0x4F0018);
        m.put(KeyEvent.KEYCODE_P, 0x500019);
        m.put(KeyEvent.KEYCODE_Q, 0x510010);
        m.put(KeyEvent.KEYCODE_R, 0x520013);
        m.put(KeyEvent.KEYCODE_S, 0x53001F);
        m.put(KeyEvent.KEYCODE_T, 0x540014);
        m.put(KeyEvent.KEYCODE_U, 0x550016);
        m.put(KeyEvent.KEYCODE_V, 0x56002F);
        m.put(KeyEvent.KEYCODE_W, 0x570011);
        m.put(KeyEvent.KEYCODE_X, 0x58002D);
        m.put(KeyEvent.KEYCODE_Y, 0x590015);
        m.put(KeyEvent.KEYCODE_Z, 0x5A002C);
        m.put(KeyEvent.KEYCODE_COMMA, 0xBC0033);
        m.put(KeyEvent.KEYCODE_PERIOD, 0xBE0034);
        m.put(KeyEvent.KEYCODE_ALT_LEFT, 0xA40038);
        m.put(KeyEvent.KEYCODE_ALT_RIGHT, 0xA50064);
        m.put(KeyEvent.KEYCODE_SHIFT_LEFT, 0xA0002A);
        m.put(KeyEvent.KEYCODE_SHIFT_RIGHT, 0xA10036);
        m.put(KeyEvent.KEYCODE_TAB, 0x09000F);
        m.put(KeyEvent.KEYCODE_SPACE, 0x200039);
        m.put(KeyEvent.KEYCODE_ENVELOPE, 0xB4009B);
        m.put(KeyEvent.KEYCODE_ENTER, 0x0D001C);
        m.put(KeyEvent.KEYCODE_DEL, 0x08000E);
        m.put(KeyEvent.KEYCODE_GRAVE, 0xC00029);
        m.put(KeyEvent.KEYCODE_MINUS, 0xBD000C);
        m.put(KeyEvent.KEYCODE_EQUALS, 0xBB000D);
        m.put(KeyEvent.KEYCODE_LEFT_BRACKET, 0xDB001A);
        m.put(KeyEvent.KEYCODE_RIGHT_BRACKET, 0xDD001B);
        m.put(KeyEvent.KEYCODE_BACKSLASH, 0xDC002B);
        m.put(KeyEvent.KEYCODE_SEMICOLON, 0xBA0027);
        m.put(KeyEvent.KEYCODE_APOSTROPHE, 0xDE0028);
        m.put(KeyEvent.KEYCODE_SLASH, 0xBF0035);
        m.put(KeyEvent.KEYCODE_MENU, 0x5D007F);
        m.put(KeyEvent.KEYCODE_NOTIFICATION, 0xEF00CC);
        m.put(KeyEvent.KEYCODE_SEARCH, 0xAA00D9);
        m.put(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0xB300A4);
        m.put(KeyEvent.KEYCODE_MEDIA_STOP, 0xB200A6);
        m.put(KeyEvent.KEYCODE_MEDIA_NEXT, 0xB000A3);
        m.put(KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0xB100A5);
        m.put(KeyEvent.KEYCODE_PAGE_UP, 0x210068);
        m.put(KeyEvent.KEYCODE_PAGE_DOWN, 0x22006D);
        m.put(KeyEvent.KEYCODE_ESCAPE, 0x1B0001);
        m.put(KeyEvent.KEYCODE_FORWARD_DEL, 0x2E006F);
        m.put(KeyEvent.KEYCODE_CTRL_LEFT, 0xA2001D);
        m.put(KeyEvent.KEYCODE_CTRL_RIGHT, 0xA30061);
        m.put(KeyEvent.KEYCODE_CAPS_LOCK, 0x14003A);
        m.put(KeyEvent.KEYCODE_SCROLL_LOCK, 0x910046);
        m.put(KeyEvent.KEYCODE_META_LEFT, 0x5B007D);
        m.put(KeyEvent.KEYCODE_META_RIGHT, 0x5C007E);
        m.put(KeyEvent.KEYCODE_FUNCTION, 0xFF01D0);
        m.put(KeyEvent.KEYCODE_SYSRQ, 0x2C0063);
        m.put(KeyEvent.KEYCODE_BREAK, 0x130077);
        m.put(KeyEvent.KEYCODE_MOVE_HOME, 0x240066);
        m.put(KeyEvent.KEYCODE_MOVE_END, 0x23006B);
        m.put(KeyEvent.KEYCODE_INSERT, 0x2D006E);
        m.put(KeyEvent.KEYCODE_FORWARD, 0xA7009F);
        m.put(KeyEvent.KEYCODE_F1, 0x70003B);
        m.put(KeyEvent.KEYCODE_F2, 0x71003C);
        m.put(KeyEvent.KEYCODE_F3, 0x72003D);
        m.put(KeyEvent.KEYCODE_F4, 0x73003E);
        m.put(KeyEvent.KEYCODE_F5, 0x74003F);
        m.put(KeyEvent.KEYCODE_F6, 0x750040);
        m.put(KeyEvent.KEYCODE_F7, 0x760041);
        m.put(KeyEvent.KEYCODE_F8, 0x770042);
        m.put(KeyEvent.KEYCODE_F9, 0x780043);
        m.put(KeyEvent.KEYCODE_F10, 0x790044);
        m.put(KeyEvent.KEYCODE_F11, 0x7A0057);
        m.put(KeyEvent.KEYCODE_F12, 0x7B0058);
        m.put(KeyEvent.KEYCODE_NUM_LOCK, 0x900045);
        m.put(KeyEvent.KEYCODE_NUMPAD_0, 0x600052);
        m.put(KeyEvent.KEYCODE_NUMPAD_1, 0x61004F);
        m.put(KeyEvent.KEYCODE_NUMPAD_2, 0x620050);
        m.put(KeyEvent.KEYCODE_NUMPAD_3, 0x630051);
        m.put(KeyEvent.KEYCODE_NUMPAD_4, 0x64004B);
        m.put(KeyEvent.KEYCODE_NUMPAD_5, 0x65004C);
        m.put(KeyEvent.KEYCODE_NUMPAD_6, 0x66004D);
        m.put(KeyEvent.KEYCODE_NUMPAD_7, 0x670047);
        m.put(KeyEvent.KEYCODE_NUMPAD_8, 0x680048);
        m.put(KeyEvent.KEYCODE_NUMPAD_9, 0x690049);
        m.put(KeyEvent.KEYCODE_NUMPAD_DIVIDE, 0x6F0062);
        m.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, 0x6A0037);
        m.put(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, 0x6D004A);
        m.put(KeyEvent.KEYCODE_NUMPAD_ADD, 0x6B004E);
        m.put(KeyEvent.KEYCODE_NUMPAD_DOT, 0x6E0053);
        m.put(KeyEvent.KEYCODE_NUMPAD_COMMA, 0xBC0079);
        m.put(KeyEvent.KEYCODE_NUMPAD_ENTER, 0x0D0060);
        m.put(KeyEvent.KEYCODE_VOLUME_MUTE, 0xAD0071);
        m.put(KeyEvent.KEYCODE_BOOKMARK, 0xAB009C);
        m.put(KeyEvent.KEYCODE_MUSIC, 0xB500AB);
        m.put(KeyEvent.KEYCODE_CALCULATOR, 0xB7008C);
        m.put(KeyEvent.KEYCODE_EISU, 0x19007B);
        m.put(KeyEvent.KEYCODE_MUHENKAN, 0x1D005E);
        m.put(KeyEvent.KEYCODE_HENKAN, 0x1C005C);
        m.put(KeyEvent.KEYCODE_KATAKANA_HIRAGANA, 0x15005D);
        m.put(KeyEvent.KEYCODE_YEN, 0xDC007C);
        m.put(KeyEvent.KEYCODE_RO, 0xE20059);
        m.put(KeyEvent.KEYCODE_KANA, 0x15007A);
        m.put(KeyEvent.KEYCODE_ASSIST, 0x990247);
        m.put(KeyEvent.KEYCODE_BRIGHTNESS_DOWN, 0xD800E0);
        m.put(KeyEvent.KEYCODE_BRIGHTNESS_UP, 0xD900E1);
        m.put(KeyEvent.KEYCODE_SLEEP, 0x5F008E);
        m.put(KeyEvent.KEYCODE_REFRESH, 0xA800AD);
    }

    /** Chrome's {@code ui::KeyboardCode} for |keyCode|, or 0 (VKEY_UNKNOWN). */
    private static int chromeVkey(int keyCode) {
        return KEY_TO_CHROME.get(keyCode, 0) >>> 16;
    }

    /** The standard evdev scancode for |keyCode|, or 0 if unknown. */
    private static int chromeScanCode(int keyCode) {
        return KEY_TO_CHROME.get(keyCode, 0) & 0xFFFF;
    }

    /**
     * Write a {@code KeyEventData} body for |event| into |data|.
     *
     * @return false if this key cannot be described to Chrome and must not be
     *         sent — see {@link #KEY_TO_CHROME} for why sending it anyway would
     *         take the ImeHost pipe down with it.
     */
    private boolean writeKeyEventData(Parcel data, boolean pressed, int keyCode,
            KeyEvent event) {
        // A synthetic event (input keyevent, automation, an accessibility
        // service) carries no scancode. Substitute the standard one for this
        // key so Chrome resolves it down exactly the path a real press takes,
        // rather than falling back to key_code.
        int scanCode = event.getScanCode();
        if (scanCode == 0) {
            scanCode = chromeScanCode(keyCode);
        }
        final int vkey = chromeVkey(keyCode);
        if (scanCode == 0 && vkey == 0) {
            return false;
        }

        final int metaState = event.getMetaState();
        data.writeInt(pressed ? 1 : 0);
        data.writeInt(vkey);
        data.writeInt((metaState & KeyEvent.META_SHIFT_ON) != 0 ? 1 : 0);
        data.writeInt((metaState & KeyEvent.META_CTRL_ON) != 0 ? 1 : 0);
        data.writeInt((metaState & KeyEvent.META_ALT_ON) != 0 ? 1 : 0);
        data.writeInt((metaState & KeyEvent.META_CAPS_LOCK_ON) != 0 ? 1 : 0);
        data.writeInt(scanCode);
        data.writeInt(0); // alt_gr — not available from Android KeyEvent
        data.writeInt(pressed && event.getRepeatCount() > 0 ? 1 : 0);
        return true;
    }

    /**
     * Send a key event to Chrome OS IME via arc_ime_bridge.
     * Returns true if Chrome consumed the event (will send InsertText etc. later).
     */
    private boolean sendKeyEventToBridge(int keyCode, KeyEvent event) {
        if (!mImeBridgeConnected || mImeBridge == null) return false;

        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            if (!writeKeyEventData(data, true /* pressed */, keyCode, event)) {
                // Not describable to Chrome; handle it locally instead of
                // killing the pipe. Chrome's IME could not have used it anyway.
                return false;
            }

            mImeBridge.transact(IME_SEND_KEY_EVENT, data, reply, 0);
            return reply.readInt() != 0;
        } catch (RemoteException e) {
            Log.w(TAG, "sendKeyEventToBridge failed", e);
            mImeBridgeConnected = false;
            // Retry connection
            mMainHandler.postDelayed(this::connectToImeBridge, 2000);
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Send a key-up event to Chrome OS IME.
     */
    private void sendKeyUpToBridge(int keyCode, KeyEvent event) {
        if (!mImeBridgeConnected || mImeBridge == null) return;

        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            if (!writeKeyEventData(data, false /* pressed */, keyCode, event)) {
                return;
            }

            mImeBridge.transact(IME_SEND_KEY_EVENT, data, reply, 0);
            // Ignore result for key-up
        } catch (RemoteException e) {
            Log.w(TAG, "sendKeyUpToBridge failed", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Notify Chrome of text input type change.
     */
    private void sendTextInputTypeChanged(int mojoType, int flags) {
        if (!mImeBridgeConnected || mImeBridge == null) return;

        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInt(mojoType);
            data.writeInt(1); // is_personalized_learning_allowed
            data.writeInt(flags);
            mImeBridge.transact(IME_ON_TEXT_INPUT_TYPE_CHANGED, data, reply,
                    IBinder.FLAG_ONEWAY);
        } catch (RemoteException e) {
            Log.w(TAG, "sendTextInputTypeChanged failed", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Map Android EditorInfo.inputType to Mojo TextInputType.
     */
    private static int androidInputTypeToMojo(int inputType) {
        if (inputType == InputType.TYPE_NULL) {
            return 0; // NONE
        }
        int cls = inputType & InputType.TYPE_MASK_CLASS;
        int variation = inputType & InputType.TYPE_MASK_VARIATION;

        switch (cls) {
            case InputType.TYPE_CLASS_TEXT:
                switch (variation) {
                    case InputType.TYPE_TEXT_VARIATION_PASSWORD:
                    case InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD:
                    case InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
                        return 2; // PASSWORD
                    case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                    case InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS:
                        return 4; // EMAIL
                    case InputType.TYPE_TEXT_VARIATION_URI:
                        return 7; // URL
                    default:
                        return 1; // TEXT
                }
            case InputType.TYPE_CLASS_NUMBER:
                return 5; // NUMBER
            case InputType.TYPE_CLASS_PHONE:
                return 6; // TELEPHONE
            case InputType.TYPE_CLASS_DATETIME:
                return 10; // DATETIME
            default:
                return 1; // TEXT
        }
    }

    /**
     * Map Android EditorInfo to Mojo text input flags.
     */
    private static int androidInputTypeToMojoFlags(int inputType) {
        int flags = 0;
        int capsType = inputType & InputType.TYPE_MASK_FLAGS;
        if ((capsType & InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS) != 0) {
            flags |= 128; // TEXT_INPUT_FLAG_AUTOCAPITALIZE_CHARACTERS
        } else if ((capsType & InputType.TYPE_TEXT_FLAG_CAP_WORDS) != 0) {
            flags |= 256; // TEXT_INPUT_FLAG_AUTOCAPITALIZE_WORDS
        } else if ((capsType & InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0) {
            flags |= 512; // TEXT_INPUT_FLAG_AUTOCAPITALIZE_SENTENCES
        } else {
            flags |= 64; // TEXT_INPUT_FLAG_AUTOCAPITALIZE_NONE
        }
        return flags;
    }

    // -------------------------------------------------------------------------
    // InputMethodService lifecycle
    // -------------------------------------------------------------------------

    private static KeyCharacterMap loadKeyCharMap() {
        int[] deviceIds = {1, KeyCharacterMap.BUILT_IN_KEYBOARD, KeyCharacterMap.VIRTUAL_KEYBOARD};
        for (int id : deviceIds) {
            try {
                return KeyCharacterMap.load(id);
            } catch (Exception e) {
                // Try next
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Chrome OS virtual keyboard
    // -------------------------------------------------------------------------

    /**
     * The IME "view" is an empty placeholder the height of Chrome's virtual
     * keyboard. Chrome draws the actual keyboard above the ARC surface; all we
     * contribute is the window whose frame becomes the app's IME inset.
     */
    @Override
    public View onCreateInputView() {
        View v = new View(this);
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mVkHeightPx));
        mPlaceholderView = v;
        return v;
    }

    @Override
    public boolean onEvaluateInputViewShown() {
        // Never let the hardware-keyboard configuration veto the window: whether
        // a keyboard is appropriate is Chrome's call (ShowVirtualKeyboardIfEnabled),
        // and an on-screen keyboard is exactly what a board with a touchscreen
        // and no physical keys needs.
        return true;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        // No extract view — Chrome's keyboard is not ours to lay out around.
        return false;
    }

    @Override
    public boolean onShowInputRequested(int flags, boolean configChange) {
        // The app asked for a keyboard. We have none — ask Chrome for its
        // virtual keyboard instead. Chrome applies its own policy (tablet mode,
        // accessibility setting), so a board with a physical keyboard attached
        // simply gets nothing back and the placeholder stays zero-height.
        if (mImeBridgeConnected) {
            if (mLastMojoType != 0) {
                // A previous hide reported NONE to Chrome; re-assert the field.
                sendTextInputTypeChanged(mLastMojoType, mLastMojoFlags);
            }
            sendShowVirtualKeyboard();
        }
        return true;
    }

    @Override
    public void hideWindow() {
        boolean wasShown = isInputViewShown();
        super.hideWindow();
        // Chrome hides its virtual keyboard when the focused field reports
        // TEXT_INPUT_TYPE_NONE; the real type is re-asserted on the next show
        // request (or the next onStartInput).
        if (wasShown && mImeBridgeConnected && mLastMojoType != 0) {
            sendTextInputTypeChanged(0 /* NONE */, 0);
        }
    }

    @Override
    public void onWindowShown() {
        super.onWindowShown();
        // Keep the placeholder invisible — the theme's window background would
        // otherwise paint a slab over the app.
        Window w = getWindow() != null ? getWindow().getWindow() : null;
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onComputeInsets(Insets outInsets) {
        super.onComputeInsets(outInsets);
        // The IME inset must be exactly as tall as Chrome's keyboard, so state
        // it outright instead of inheriting it. super derives contentTopInsets
        // from where the placeholder happens to sit in the decor, and the
        // placeholder is an empty zero-height View: it lands at the TOP of a
        // full-screen decor, so the app is handed a full-screen bottom inset.
        // An ADJUST_RESIZE window then lays out at zero height and paints
        // blank — the Play Store / GMS sign-in WebView went white the moment
        // its email field took focus. Chrome reports no keyboard at all on a
        // board with physical keys (no OnKeyboardAppearance ever arrives), so
        // this is the DEFAULT path, not an edge case.
        final Window w = getWindow() != null ? getWindow().getWindow() : null;
        final View decor = w != null ? w.getDecorView() : null;
        if (decor != null) {
            final int top = Math.max(0, decor.getHeight() - mVkHeightPx);
            outInsets.contentTopInsets = top;
            outInsets.visibleTopInsets = top;
        }
        // The placeholder must never eat a touch: Chrome's keyboard sits above
        // the ARC surface in the host compositor and takes its own input, so
        // anything reaching Android in that region belongs to the app.
        outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_REGION;
        outInsets.touchableRegion.setEmpty();
    }

    /** Chrome told us where (and whether) it put its virtual keyboard. */
    private void onVirtualKeyboardBounds(boolean available, int heightPx) {
        if (mVkHeightPx != heightPx) {
            mVkHeightPx = heightPx;
            if (mPlaceholderView != null) {
                ViewGroup.LayoutParams lp = mPlaceholderView.getLayoutParams();
                if (lp != null) {
                    lp.height = heightPx;
                    mPlaceholderView.setLayoutParams(lp);
                } else {
                    mPlaceholderView.requestLayout();
                }
            }
            // onComputeInsets now reports the height we just learned, but it
            // only runs on a traversal — and the placeholder may not exist yet
            // (no input view created), in which case nothing above scheduled
            // one. Ask the decor directly so the app's inset never lags Chrome.
            final Window w = getWindow() != null ? getWindow().getWindow() : null;
            final View decor = w != null ? w.getDecorView() : null;
            if (decor != null) {
                decor.requestLayout();
            }
        }
        if (!available && isInputViewShown()) {
            // Dismissed from Chrome's side (keyboard's own close button, or
            // Chrome deciding a physical keyboard took over) — keep Android's
            // IME visibility, and the app's insets, in step with reality.
            requestHideSelf(0);
        }
    }

    private void sendShowVirtualKeyboard() {
        if (!mImeBridgeConnected || mImeBridge == null) return;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            mImeBridge.transact(IME_SHOW_VIRTUAL_KEYBOARD, data, reply,
                    IBinder.FLAG_ONEWAY);
        } catch (RemoteException e) {
            Log.w(TAG, "sendShowVirtualKeyboard failed", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        // New field: re-learn the IME's composing behavior from scratch (the
        // active Chrome OS layout may differ per field).
        mComposing = false;
        mImeComposingSeen = false;
        mVerifiedLocalChars.clear();
        mKeysInFlight = 0;
        mLastEchoedLocalChar = null;
        mEchoedDownKeys.clear();
        if (attribute != null) {
            mLastMojoType = androidInputTypeToMojo(attribute.inputType);
            mLastMojoFlags = androidInputTypeToMojoFlags(attribute.inputType);
            mSelStart = attribute.initialSelStart;
            mSelEnd = attribute.initialSelEnd;
        }
        if (mImeBridgeConnected && attribute != null) {
            int mojoType = mLastMojoType;
            int mojoFlags = mLastMojoFlags;
            sendTextInputTypeChanged(mojoType, mojoFlags);

            // Send initial cursor rect from focused task bounds.
            Rect taskBounds = getFocusedTaskBounds();
            if (taskBounds != null) {
                sendCursorRectToBridge(taskBounds.left, taskBounds.top,
                        taskBounds.left + 1, taskBounds.top + 1);
            }
        }
        // Request cursor position updates so we can forward to Chrome.
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.requestCursorUpdates(InputConnection.CURSOR_UPDATE_MONITOR);
        }
    }

    @Override
    public void onFinishInput() {
        if (mImeBridgeConnected) {
            sendTextInputTypeChanged(0 /* NONE */, 0);
        }
        // No field: nothing left to re-assert on a later show request.
        mLastMojoType = 0;
        mLastMojoFlags = 0;
        mSelStart = -1;
        mSelEnd = -1;
        super.onFinishInput();
    }

    @Override
    public void onUpdateCursorAnchorInfo(CursorAnchorInfo info) {
        if (!mImeBridgeConnected || mImeBridge == null) return;

        float localX = info.getInsertionMarkerHorizontal();
        float localTop = info.getInsertionMarkerTop();
        float localBottom = info.getInsertionMarkerBottom();

        int left, top, right, bottom;

        if (!Float.isNaN(localX) && !Float.isNaN(localTop) && (localX != 0 || localTop != 0)) {
            // Apply the transformation matrix to get screen coordinates.
            // Android renders at (0,0) so these are window-relative.
            Matrix matrix = info.getMatrix();
            float[] topPt = {localX, localTop};
            float[] botPt = {localX, localBottom};
            matrix.mapPoints(topPt);
            matrix.mapPoints(botPt);
            left = (int) topPt[0];
            top = (int) topPt[1];
            right = left + 1;
            bottom = (int) botPt[1];
        } else {
            // CursorAnchorInfo has no useful position — fall back to
            // focused task bounds from the WindowManager.
            Rect taskBounds = getFocusedTaskBounds();
            if (taskBounds == null) return;
            left = taskBounds.left;
            top = taskBounds.top;
            right = taskBounds.left + 1;
            bottom = taskBounds.top + 1;
        }

        // Android renders each window at (0,0). Add the Chrome OS window
        // offset so Chrome gets correct screen-space coordinates.
        int[] offset = getFocusedWindowOffset();
        left += offset[0];
        top += offset[1];
        right += offset[0];
        bottom += offset[1];

        mLastCursorRect.set(left, top, right, bottom);
        if (!sendSurroundingTextToBridge()) {
            // Editor won't tell us its text — Chrome at least gets the caret.
            sendCursorRectToBridge(left, top, right, bottom);
        }
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
            int newSelStart, int newSelEnd,
            int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        mSelStart = newSelStart;
        mSelEnd = newSelEnd;
        // The caret moved or the text changed — re-send what surrounds it, or
        // Chrome's view of the field goes stale and its keyboard starts editing
        // text that is no longer there.
        sendSurroundingTextToBridge();
    }

    /**
     * Tell Chrome what text sits around the cursor.
     *
     * Chrome's IME engine keeps its own model of the field. Given only a cursor
     * rect it assumes the field is empty, so its virtual keyboard's backspace
     * finds nothing to delete and trims its own candidate buffer instead — the
     * app's text never changes. With the surrounding text it can edit the field
     * properly (ExtendSelectionAndDelete, autocorrect, suggestions).
     */
    private boolean sendSurroundingTextToBridge() {
        if (!mImeBridgeConnected || mImeBridge == null) return false;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return false;

        // Chrome wants absolute ranges. getSurroundingText() would be the tidy
        // way to get them, but most editors return INVALID_OFFSET for it (the
        // default InputConnection cannot say where its snippet starts), so
        // build the range from the selection the editor reports to us, which is
        // absolute by definition, plus the text on either side of it.
        int selStart = Math.min(mSelStart, mSelEnd);
        int selEnd = Math.max(mSelStart, mSelEnd);
        if (selStart < 0) return false;

        CharSequence before = ic.getTextBeforeCursor(SURROUNDING_TEXT_LENGTH, 0);
        CharSequence after = ic.getTextAfterCursor(SURROUNDING_TEXT_LENGTH, 0);
        if (before == null && after == null) return false;
        if (before == null) before = "";
        if (after == null) after = "";

        // An editor does not promise that getTextBeforeCursor() stops where the
        // selection it last reported begins: a WebView answers out of the
        // renderer's newer state, and initialSelStart arrives as 0 for a field
        // that already holds text. Taking it at its word makes the text range
        // start negative, and Chrome reads that start as unsigned and subtracts
        // it from the selection (InputMethodAsh::OnCaretBoundsChanged) — the
        // underflow trips a CHECK inside gfx::Range and kills the browser
        // process, which takes the session and the whole container down with it.
        // Keep only as much prefix as the selection can account for.
        if (before.length() > selStart) {
            before = before.subSequence(before.length() - selStart, before.length());
        }

        // getTextBeforeCursor() stops at the selection start, getTextAfterCursor()
        // resumes at the selection end; the selected run itself sits between them
        // and Chrome is told about it via the selection range.
        CharSequence selected = selEnd > selStart ? ic.getSelectedText(0) : null;
        if (selected == null) selected = "";
        String text = before.toString() + selected.toString() + after.toString();
        int offset = selStart - before.length();

        // The far end of the same invariant: the selection has to stay inside the
        // range we are describing. getSelectedText() can come back empty for a
        // real selection, which would leave selEnd past the end of |text|.
        int textEnd = offset + text.length();
        if (selEnd > textEnd) selEnd = textEnd;

        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInt(mLastCursorRect.left);
            data.writeInt(mLastCursorRect.top);
            data.writeInt(mLastCursorRect.right);
            data.writeInt(mLastCursorRect.bottom);
            data.writeInt(offset);                    // text range start
            data.writeInt(offset + text.length());    // text range end
            data.writeString(text);
            data.writeInt(selStart);
            data.writeInt(selEnd);
            data.writeInt(0); // CursorCoordinateSpace::SCREEN
            mImeBridge.transact(IME_ON_CURSOR_RECT_WITH_SURROUNDING_TEXT, data,
                    reply, IBinder.FLAG_ONEWAY);
            return true;
        } catch (RemoteException e) {
            Log.w(TAG, "sendSurroundingTextToBridge failed", e);
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    /**
     * Get the focused task's window bounds via ActivityTaskManager.
     */
    private Rect getFocusedTaskBounds() {
        try {
            // Android 12 merged ActivityStack into Task: ActivityManager.StackInfo
            // → ActivityTaskManager.RootTaskInfo, getFocusedStackInfo() →
            // getFocusedRootTaskInfo() (RootTaskInfo still carries Rect bounds).
            ActivityTaskManager.RootTaskInfo stackInfo =
                    ActivityTaskManager.getService().getFocusedRootTaskInfo();
            if (stackInfo != null && stackInfo.bounds != null
                    && !stackInfo.bounds.isEmpty()) {
                return stackInfo.bounds;
            }
        } catch (Exception e) {
            Log.w(TAG, "getFocusedTaskBounds failed", e);
        }
        return null;
    }

    /**
     * Query the Wayland service for the focused window's Chrome OS position
     * in physical pixels. Returns {x, y} offset or {0, 0} on failure.
     */
    private int[] getFocusedWindowOffset() {
        if (mWaylandService == null) return new int[]{0, 0};
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(WAYLAND_DESCRIPTOR);
            mWaylandService.transact(TRANSACTION_getFocusedWindowOffset, data, reply, 0);
            reply.readException();
            int x = reply.readInt();
            int y = reply.readInt();
            return new int[]{x, y};
        } catch (RemoteException e) {
            Log.w(TAG, "getFocusedWindowOffset failed", e);
            return new int[]{0, 0};
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private void sendCursorRectToBridge(int left, int top, int right, int bottom) {
        mLastCursorRect.set(left, top, right, bottom);
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInt(left);
            data.writeInt(top);
            data.writeInt(right);
            data.writeInt(bottom);
            data.writeInt(0); // CursorCoordinateSpace::SCREEN
            mImeBridge.transact(IME_ON_CURSOR_RECT_CHANGED, data, reply,
                    IBinder.FLAG_ONEWAY);
        } catch (RemoteException e) {
            Log.w(TAG, "sendCursorRectChanged failed", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return false;
        }

        // No editable field: hand the key back to the app untouched.
        if (!hasActiveEditor()) {
            return false;
        }

        // Let the system handle modifier-only keys
        if (isModifierKey(keyCode)) {
            return false;
        }

        // Let apps handle Ctrl+combos (Ctrl+A, Ctrl+C, Ctrl+V, etc.)
        if ((event.getMetaState() & KeyEvent.META_CTRL_ON) != 0) {
            return false;
        }

        // Non-IME keys: Back, Volume, etc.
        if (isSystemKey(keyCode)) {
            return false;
        }

        // Re-validate after a typing pause: switching the Chrome OS input method
        // (CJK ↔ English) happens during a pause with no onStartInput, so drop
        // the LEARNED state and re-learn from the next key.
        //
        // mComposing is deliberately NOT dropped here. The other two are
        // heuristics this IME infers; mComposing is fact reported by Chrome
        // (SetCompositionText / InsertText / ConfirmCompositionText), and a
        // composition genuinely outlives a pause — reading the candidate list
        // before correcting a syllable takes far longer than 500ms. Clearing it
        // sent the next backspace down the local-delete path below, which edits
        // the app's mirrored text while Chrome's composition and candidate
        // window keep the old string: they silently diverge and the backspace
        // looks ignored.
        //
        // A stale mComposing is self-correcting and costs one round-trip: the
        // key goes to Chrome, which is not composing, so it answers "not
        // consumed" (KeyEventResultReceiver returns false for a key with no
        // character) and the switch below handles it locally after all. The
        // first InsertText likewise clears the flag and re-arms the fast path.
        long nowMs = android.os.SystemClock.uptimeMillis();
        if (mLastKeyTimeMs != 0 && (nowMs - mLastKeyTimeMs) > IME_REVALIDATE_IDLE_MS) {
            mVerifiedLocalChars.clear();
            mImeComposingSeen = false;
        }
        mLastKeyTimeMs = nowMs;

        // English fast-path: once Chrome has inserted this key's own character
        // this session (see mVerifiedLocalChars), commit it LOCALLY and skip
        // the Chrome round-trip. Not while composing, and only for plain
        // chars with no Ctrl/Alt/Meta (those still go to Chrome for shortcuts /
        // AltGr / composition). CJK never verifies a key, so it's unaffected.
        String localChar = localCharForPlainKey(keyCode, event);
        if (!mComposing && localChar != null
                && mVerifiedLocalChars.contains(localChar)) {
            ic.commitText(localChar, 1);
            return true;
        }

        // Backspace with nothing composing edits the app's text, and only the
        // app's text — do it here rather than round-tripping. Chrome answers
        // "consumed" for keys it originated itself (every press on its virtual
        // keyboard) and then sends no edit back, which silently ate the delete:
        // the candidate strip shrank, the field did not. While Chrome IS
        // composing, backspace belongs to the composition, so leave it alone.
        if (!mComposing && (keyCode == KeyEvent.KEYCODE_DEL
                || keyCode == KeyEvent.KEYCODE_FORWARD_DEL)) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                ic.deleteSurroundingText(1, 0);
            } else {
                ic.deleteSurroundingText(0, 1);
            }
            return true;
        }

        // Try Chrome OS IME bridge first
        if (mImeBridgeConnected) {
            mEchoedDownKeys.add(keyCode);  // echoed down → echo the up too
            boolean consumed = sendKeyEventToBridge(keyCode, event);
            if (consumed) {
                mKeysInFlight++;
                mLastEchoedLocalChar = localChar;
                return true;
            }
            // Fall through to local handling if Chrome didn't consume it
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                ic.deleteSurroundingText(1, 0);
                return true;

            case KeyEvent.KEYCODE_FORWARD_DEL:
                ic.deleteSurroundingText(0, 1);
                return true;

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                return handleEnter(ic);

            case KeyEvent.KEYCODE_TAB:
                ic.commitText("\t", 1);
                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MOVE_HOME:
            case KeyEvent.KEYCODE_MOVE_END:
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                // Pass navigation keys through to the app
                ic.sendKeyEvent(event);
                return true;

            default:
                int unicodeChar = 0;
                if (mKeyCharMap != null) {
                    unicodeChar = mKeyCharMap.get(keyCode, event.getMetaState());
                }
                if (unicodeChar == 0) {
                    unicodeChar = keycodeToChar(keyCode, event.getMetaState());
                }
                if (unicodeChar > 0) {
                    ic.commitText(String.valueOf((char) unicodeChar), 1);
                    return true;
                }
                return false;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Echo the UP to Chrome only for keys whose DOWN we echoed (tracked in
        // mEchoedDownKeys). Fast-local keys never sent a down, so they must not
        // send an up. This is symmetric regardless of when the passthrough
        // confirmation flipped between this key's down and up.
        if (mImeBridgeConnected && mEchoedDownKeys.remove(keyCode)) {
            sendKeyUpToBridge(keyCode, event);
        }

        // Mirror onKeyDown: with no editable field the DOWN was passed through,
        // so the UP has to be as well, or the app sees an unmatched pair.
        if (!hasActiveEditor()) {
            return false;
        }

        // For navigation keys we consumed on down, send the matching up event
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && isNavigationKey(keyCode)) {
            ic.sendKeyEvent(event);
            return true;
        }
        return false;
    }

    /**
     * True when a genuinely editable field has focus.
     *
     * A window with no text input — a game, a video player, any plain view —
     * still binds the IME and still gets a non-null InputConnection, but that
     * connection is <em>inactive</em>: every commitText/sendKeyEvent through it
     * is silently dropped ("sendKeyEvent on inactive InputConnection"). Since
     * this IME delivers keys exclusively via the connection and returns true to
     * consume them, handling a key in that state destroys it — it never reaches
     * the app's own onKeyDown. Asphalt 8 lost all its steering and accelerator
     * keys this way. Pass the key back instead and let the app have it.
     */
    private boolean hasActiveEditor() {
        if (!getCurrentInputStarted()) {
            return false;
        }
        EditorInfo ei = getCurrentInputEditorInfo();
        return ei != null && ei.inputType != InputType.TYPE_NULL;
    }

    private boolean handleEnter(InputConnection ic) {
        EditorInfo ei = getCurrentInputEditorInfo();
        if (ei != null) {
            int action = ei.imeOptions & EditorInfo.IME_MASK_ACTION;
            if (action != EditorInfo.IME_ACTION_NONE
                    && action != EditorInfo.IME_ACTION_UNSPECIFIED
                    && (ei.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0) {
                ic.performEditorAction(action);
                return true;
            }
        }
        ic.commitText("\n", 1);
        return true;
    }

    /**
     * The character the fast-path commits for a plain printable key (no
     * Ctrl/Alt/Meta) under Android's own key map, or null if the key is not
     * one the fast-path handles.
     */
    private String localCharForPlainKey(int keyCode, KeyEvent event) {
        if (!isPlainPrintableKey(keyCode)
                || (event.getMetaState() & (KeyEvent.META_CTRL_ON
                        | KeyEvent.META_ALT_ON | KeyEvent.META_META_ON)) != 0) {
            return null;
        }
        int unicodeChar = (mKeyCharMap != null)
                ? mKeyCharMap.get(keyCode, event.getMetaState()) : 0;
        if (unicodeChar == 0) {
            unicodeChar = keycodeToChar(keyCode, event.getMetaState());
        }
        return unicodeChar > 0 ? String.valueOf((char) unicodeChar) : null;
    }

    /**
     * True for keys that produce a plain text character (letters, digits, and
     * the punctuation handled by keycodeToChar) — the keys eligible for the
     * local English fast-path. Excludes DEL/ENTER/TAB/navigation/system keys,
     * which keep their dedicated handling and IME round-trip.
     */
    private static boolean isPlainPrintableKey(int keyCode) {
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) return true;
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) return true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_COMMA:
            case KeyEvent.KEYCODE_PERIOD:
            case KeyEvent.KEYCODE_SEMICOLON:
            case KeyEvent.KEYCODE_APOSTROPHE:
            case KeyEvent.KEYCODE_SLASH:
            case KeyEvent.KEYCODE_BACKSLASH:
            case KeyEvent.KEYCODE_LEFT_BRACKET:
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
            case KeyEvent.KEYCODE_MINUS:
            case KeyEvent.KEYCODE_EQUALS:
            case KeyEvent.KEYCODE_GRAVE:
                return true;
            default:
                return false;
        }
    }

    private static boolean isModifierKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_SHIFT_LEFT:
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
            case KeyEvent.KEYCODE_CTRL_LEFT:
            case KeyEvent.KEYCODE_CTRL_RIGHT:
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
            case KeyEvent.KEYCODE_META_LEFT:
            case KeyEvent.KEYCODE_META_RIGHT:
            case KeyEvent.KEYCODE_CAPS_LOCK:
            case KeyEvent.KEYCODE_NUM_LOCK:
            case KeyEvent.KEYCODE_SCROLL_LOCK:
                return true;
            default:
                return false;
        }
    }

    private static boolean isSystemKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_APP_SWITCH:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
            case KeyEvent.KEYCODE_POWER:
            case KeyEvent.KEYCODE_MENU:
                return true;
            default:
                return false;
        }
    }

    private static int keycodeToChar(int keyCode, int metaState) {
        boolean shift = (metaState & KeyEvent.META_SHIFT_ON) != 0;
        // Letters: KEYCODE_A(29) through KEYCODE_Z(54)
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            char base = (char) ('a' + (keyCode - KeyEvent.KEYCODE_A));
            return shift ? Character.toUpperCase(base) : base;
        }
        // Digits: KEYCODE_0(7) through KEYCODE_9(16)
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            if (shift) {
                return ")!@#$%^&*(".charAt(keyCode - KeyEvent.KEYCODE_0);
            }
            return '0' + (keyCode - KeyEvent.KEYCODE_0);
        }
        // Common punctuation
        switch (keyCode) {
            case KeyEvent.KEYCODE_SPACE:         return ' ';
            case KeyEvent.KEYCODE_COMMA:         return shift ? '<' : ',';
            case KeyEvent.KEYCODE_PERIOD:        return shift ? '>' : '.';
            case KeyEvent.KEYCODE_SEMICOLON:     return shift ? ':' : ';';
            case KeyEvent.KEYCODE_APOSTROPHE:    return shift ? '"' : '\'';
            case KeyEvent.KEYCODE_SLASH:         return shift ? '?' : '/';
            case KeyEvent.KEYCODE_BACKSLASH:     return shift ? '|' : '\\';
            case KeyEvent.KEYCODE_LEFT_BRACKET:  return shift ? '{' : '[';
            case KeyEvent.KEYCODE_RIGHT_BRACKET: return shift ? '}' : ']';
            case KeyEvent.KEYCODE_MINUS:         return shift ? '_' : '-';
            case KeyEvent.KEYCODE_EQUALS:        return shift ? '+' : '=';
            case KeyEvent.KEYCODE_GRAVE:         return shift ? '~' : '`';
            case KeyEvent.KEYCODE_AT:            return '@';
            case KeyEvent.KEYCODE_POUND:         return '#';
            case KeyEvent.KEYCODE_STAR:          return '*';
            case KeyEvent.KEYCODE_PLUS:          return '+';
            default: return 0;
        }
    }

    private static boolean isNavigationKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MOVE_HOME:
            case KeyEvent.KEYCODE_MOVE_END:
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                return true;
            default:
                return false;
        }
    }
}

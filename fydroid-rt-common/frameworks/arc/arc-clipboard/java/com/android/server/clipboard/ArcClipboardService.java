package com.android.server.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.SparseArray;
import android.util.Slog;

import com.android.server.LocalServices;
import com.android.server.SystemService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bridges Android clipboard with Chrome OS clipboard via ArcDataDeviceService.
 *
 * Publishes {@link ArcClipboardInternal} via {@link LocalServices} so that
 * ArcDataDeviceService can connect the DataDevice for clipboard bridging.
 *
 * <p>Android→Chrome: polls clipboard changes via reflection into ClipboardService's
 * internal state (same process, bypasses permission checks), then forwards to
 * Chrome via {@code DataDevice.setClipboard()}.
 *
 * <p>Chrome→Android: receives clips via {@link ArcClipboardInternal#onClipboardUpdate},
 * sets them on the Android clipboard. Echo prevention compares a normalised content
 * signature (see {@link #clipSignature}) rather than raw Parcel bytes, because
 * ClipboardService decorates the stored ClipData in place after we hand it over.
 *
 * <p>Android→Chrome uses a dual approach: a {@link ClipboardManager} listener for
 * immediate notification (works when the system grants clipboard access), with
 * reflection-based polling as fallback (always works since it bypasses permission
 * checks by reading ClipboardService internals directly in the same process).
 */
public class ArcClipboardService extends SystemService {
    private static final String TAG = "ArcClipboardService";
    private static final long POLL_INTERVAL_MS = 300;

    private final AtomicReference<ArcClipboardInternal.DataDevice> mDataDevice =
            new AtomicReference<>();
    private ClipboardManager mClipboardManager;
    private Handler mHandler;

    // True if the listener fired at least once — means we can rely on it
    // and stop polling.
    private volatile boolean mListenerWorking;

    // Signature of the last clip Chrome handed us. Kept until Chrome sends a
    // different clip — ClipboardService broadcasts a single set twice (once
    // synchronously, once after text classification finishes), so a one-shot
    // token would only suppress the first and let the second loop back.
    private volatile String mLastSignatureFromChrome;

    // Signature of the last clip we forwarded to Chrome (for change detection)
    private volatile String mLastForwardedSignature;

    // Cached reflection handles for reading clipboard without permission check.
    private Field mClipboardsField;
    private Field mPrimaryClipField;
    private Object mClipboardServiceInstance;
    // ClipboardService.setPrimaryClipInternal(ClipData, int uid): sets a clip on
    // behalf of a uid, without the calling-uid checks of the binder API.
    private Method mSetPrimaryClipAsUidMethod;

    public ArcClipboardService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        LocalServices.addService(ArcClipboardInternal.class, new ArcClipboardInternalImpl());
        Slog.i(TAG, "Published ArcClipboardInternal");
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == PHASE_BOOT_COMPLETED) {
            mClipboardManager = getContext().getSystemService(ClipboardManager.class);
            mHandler = new Handler(Looper.getMainLooper());
            initDirectClipboardAccess();
            registerClipboardListener();
            startPolling();
        }
    }

    private void registerClipboardListener() {
        if (mClipboardManager == null) return;
        mClipboardManager.addPrimaryClipChangedListener(() -> {
            ClipData clip = readClipboardDirect();
            if (clip == null) return;
            if (!mListenerWorking) {
                mListenerWorking = true;
                Slog.i(TAG, "Listener is working, polling will stop");
            }
            onClipboardChanged(clip);
        });
        Slog.i(TAG, "Registered clipboard change listener");
    }

    private void initDirectClipboardAccess() {
        try {
            // Get the ClipboardImpl binder (inner class of ClipboardService).
            // In-process, ServiceManager returns the actual object, not a proxy.
            Object clipboardImpl = ServiceManager.getService(Context.CLIPBOARD_SERVICE);

            // Navigate from inner class to outer ClipboardService via this$0.
            Field outerRef = clipboardImpl.getClass().getDeclaredField("this$0");
            outerRef.setAccessible(true);
            mClipboardServiceInstance = outerRef.get(clipboardImpl);

            // Cache field: ClipboardService.mClipboards (SparseArray<PerUserClipboard>)
            mClipboardsField = mClipboardServiceInstance.getClass().getDeclaredField("mClipboards");
            mClipboardsField.setAccessible(true);

            // Cache field: PerUserClipboard.primaryClip (ClipData)
            SparseArray<?> clipboards = (SparseArray<?>) mClipboardsField.get(mClipboardServiceInstance);
            Object perUser = clipboards.get(UserHandle.USER_SYSTEM);
            if (perUser != null) {
                mPrimaryClipField = perUser.getClass().getDeclaredField("primaryClip");
                mPrimaryClipField.setAccessible(true);
            } else {
                for (Class<?> inner : mClipboardServiceInstance.getClass().getDeclaredClasses()) {
                    if (inner.getSimpleName().equals("PerUserClipboard")) {
                        mPrimaryClipField = inner.getDeclaredField("primaryClip");
                        mPrimaryClipField.setAccessible(true);
                        break;
                    }
                }
            }

            mSetPrimaryClipAsUidMethod = mClipboardServiceInstance.getClass()
                    .getDeclaredMethod("setPrimaryClipInternal", ClipData.class, int.class);
            mSetPrimaryClipAsUidMethod.setAccessible(true);

            Slog.i(TAG, "Direct clipboard access initialized");
        } catch (Exception e) {
            Slog.e(TAG, "Failed to init direct clipboard access", e);
        }
    }

    private ClipData readClipboardDirect() {
        if (mClipboardsField == null || mPrimaryClipField == null) return null;
        try {
            SparseArray<?> clipboards =
                    (SparseArray<?>) mClipboardsField.get(mClipboardServiceInstance);
            Object perUser = clipboards.get(UserHandle.USER_SYSTEM);
            if (perUser == null) return null;
            return (ClipData) mPrimaryClipField.get(perUser);
        } catch (Exception e) {
            Slog.e(TAG, "Failed to read clipboard", e);
            return null;
        }
    }

    private void startPolling() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // Stop polling once listener proves working
                if (mListenerWorking) return;

                ClipData clip = readClipboardDirect();
                if (clip != null) {
                    onClipboardChanged(clip);
                }
                mHandler.postDelayed(this, POLL_INTERVAL_MS);
            }
        });
        Slog.i(TAG, "Started clipboard polling as fallback (interval=" + POLL_INTERVAL_MS + "ms)");
    }

    private void onClipboardChanged(ClipData clip) {
        String signature = clipSignature(clip);
        if (signature == null) return;

        // No change since last check
        if (signature.equals(mLastForwardedSignature)) {
            return;
        }

        // Echo prevention: skip if this is the clip Chrome just gave us.
        if (signature.equals(mLastSignatureFromChrome)) {
            mLastForwardedSignature = signature;
            return;
        }

        mLastForwardedSignature = signature;

        Slog.d(TAG, "Clipboard changed, forwarding to Chrome ("
                + clip.getDescription().getMimeTypeCount() + " MIME types)");

        ArcClipboardInternal.DataDevice dd = mDataDevice.get();
        if (dd != null) {
            dd.setClipboard(clip);
        }
    }

    /**
     * Build a content signature covering everything a paste would actually see:
     * label, MIME types, and each item's text / HTML / URI / intent.
     *
     * <p>Deliberately excludes the fields ClipboardService stamps onto the stored
     * ClipData after {@code setPrimaryClip} returns — the timestamp, the
     * classification status, the confidence scores and the item TextLinks. Those
     * mutate the very object we handed over (in-process there is no Parcel copy),
     * so a Parcel-based comparison sees a "new" clip on the post-classification
     * re-broadcast and forwards our own clip straight back to Chrome.
     */
    private static String clipSignature(ClipData clip) {
        if (clip == null) return null;
        StringBuilder sb = new StringBuilder();
        ClipDescription desc = clip.getDescription();
        if (desc != null) {
            sb.append(desc.getLabel()).append('\0');
            for (int i = 0; i < desc.getMimeTypeCount(); i++) {
                sb.append(desc.getMimeType(i)).append('\0');
            }
        }
        sb.append('\1');
        for (int i = 0; i < clip.getItemCount(); i++) {
            ClipData.Item item = clip.getItemAt(i);
            CharSequence text = item.getText();
            // toString() so a Spanned and its plain equivalent hash the same.
            sb.append(text == null ? "" : text.toString()).append('\0');
            String html = item.getHtmlText();
            sb.append(html == null ? "" : html).append('\0');
            Uri uri = item.getUri();
            sb.append(uri == null ? "" : uri.toString()).append('\0');
            Intent intent = item.getIntent();
            sb.append(intent == null ? "" : intent.toUri(0)).append('\1');
        }
        return sb.toString();
    }

    private class ArcClipboardInternalImpl implements ArcClipboardInternal {
        @Override
        public void onClipboardUpdate(ClipData clipData, int uid) {
            if (clipData == null) return;
            // Must be armed before setPrimaryClip — the synchronous broadcast
            // reaches onClipboardChanged before setPrimaryClip returns.
            mLastSignatureFromChrome = clipSignature(clipData);
            // Set the clip as `uid` (the ContentProvider owning the clip's URIs),
            // not as system_server: ClipboardService grants the pasting app
            // read access to those URIs on behalf of the clip's source uid, and
            // UriGrantsManager refuses every grant made on behalf of SYSTEM_UID
            // ("the system cannot issue a Uri permission grant"). An app would
            // see a Chrome image on the clipboard but could not open it.
            if (mSetPrimaryClipAsUidMethod != null && uid > 0) {
                try {
                    mSetPrimaryClipAsUidMethod.invoke(mClipboardServiceInstance, clipData, uid);
                    Slog.d(TAG, "Set Android clipboard from Chrome as uid " + uid + " ("
                            + clipData.getDescription().getMimeTypeCount() + " MIME types)");
                    return;
                } catch (ReflectiveOperationException e) {
                    Slog.e(TAG, "Failed to set clipboard as uid " + uid, e);
                }
            }
            if (mClipboardManager != null) {
                mClipboardManager.setPrimaryClip(clipData);
                Slog.d(TAG, "Set Android clipboard from Chrome ("
                        + clipData.getDescription().getMimeTypeCount() + " MIME types)");
            }
        }

        @Override
        public void setDataDevice(ArcClipboardInternal.DataDevice dataDevice) {
            if (!mDataDevice.compareAndSet(null, dataDevice)) {
                Slog.w(TAG, "DataDevice already set, replacing");
                mDataDevice.set(dataDevice);
            }
            Slog.i(TAG, "DataDevice connected");
        }
    }
}

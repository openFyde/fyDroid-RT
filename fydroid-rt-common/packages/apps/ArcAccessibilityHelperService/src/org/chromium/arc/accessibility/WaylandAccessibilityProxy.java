package org.chromium.arc.accessibility;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/**
 * Publishes the Android accessibility window id each Wayland surface is showing
 * to the Wayland service, which turns it into
 * {@code zaura_surface.set_accessibility_id} on that surface.
 *
 * <p>Why this exists: Chrome keys an ARC accessibility tree by task id, and an
 * accessibility event that carries no task id is dropped unless Chrome can map
 * the event's <em>window</em> id onto one. That map
 * ({@code ArcAccessibilityTreeTracker::window_id_to_task_id_}) is filled only
 * from {@code ash::kClientAccessibilityIdKey}, which a Wayland client sets with
 * the request above. The id is minted by {@code AccessibilityWindowManager}
 * per interaction connection, which also stamps it on the window's own layer
 * ({@code METADATA_ACCESSIBILITY_ID}) — but a task, notification or IME surface
 * composites many windows, so no single layer names it. This service, which
 * holds the window list, names those. Task-less standalone surfaces (overlays,
 * toasts) render exactly one window, and the compositor publishes their id
 * from that layer metadata without involving this class.
 *
 * <p>The route is a direct binder call to SurfaceFlinger's {@code "Wayland"}
 * service rather than a hop through arc-services: sepolicy already grants
 * {@code platform_app} {@code wayland_service:service_manager find} (the shelf
 * and launcher use the same channel), so no new surface area is needed.
 *
 * <p>Transaction codes and target values match {@code WaylandBinderService.h}
 * and {@code WaylandGlobals::AccessibilityTarget} on the native side.
 */
class WaylandAccessibilityProxy {
    private static final String TAG = ArcAccessibilityHelperBridge.TAG;
    private static final String DESCRIPTOR = "org.chromium.arc.wayland.IWaylandService";
    private static final int TRANSACTION_setAccessibilityWindowId = 54;

    /** A task's surface — an app window or a dialog, both of which are tasks. */
    static final int TARGET_TASK = 0;
    /** A notification card's surface, named by its notification key. */
    static final int TARGET_NOTIFICATION = 1;
    /** The one IME surface. */
    static final int TARGET_INPUT_METHOD = 2;

    private IBinder mService;

    /**
     * Tells the compositor which accessibility window a surface is showing.
     * {@code taskId} is meaningful for {@link #TARGET_TASK} and {@code key} for
     * {@link #TARGET_NOTIFICATION}; a negative {@code windowId} drops the
     * mapping.
     *
     * <p>One-way: this is called from the accessibility event path and the
     * compositor has nothing to answer. The native side defers the Wayland
     * request to its own event thread anyway.
     *
     * @return false if the Wayland service could not be reached, so the caller
     *     can leave the mapping unpublished and retry on the next event.
     */
    boolean setAccessibilityWindowId(int target, int taskId, String key, int windowId) {
        IBinder service = service();
        if (service == null) {
            return false;
        }
        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(target);
            data.writeInt(taskId);
            data.writeString(key);
            data.writeInt(windowId);
            service.transact(TRANSACTION_setAccessibilityWindowId, data, null,
                    IBinder.FLAG_ONEWAY);
            return true;
        } catch (RemoteException e) {
            // SurfaceFlinger restarted; drop the handle so the next call
            // re-looks it up rather than failing forever.
            Log.w(TAG, "Wayland service died publishing accessibility window id", e);
            mService = null;
            return false;
        } finally {
            data.recycle();
        }
    }

    /**
     * The Wayland binder, looked up lazily. SurfaceFlinger publishes it during
     * its own start-up, which can be after this service is bound, so a miss is
     * retried on the next call rather than being fatal.
     */
    private IBinder service() {
        if (mService != null && mService.isBinderAlive()) {
            return mService;
        }
        mService = ServiceManager.getService("Wayland");
        if (mService == null) {
            Log.w(TAG, "Wayland service not available yet");
        }
        return mService;
    }
}

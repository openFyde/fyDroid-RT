package org.chromium.arc.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.ax.android.mojom.AccessibilityActionData;
import org.chromium.ax.android.mojom.AccessibilityActionType;
import org.chromium.ax.android.mojom.AccessibilityEventData;
import org.chromium.ax.android.mojom.AccessibilityFilterType;
import org.chromium.ax.android.mojom.AccessibilityHelperHost;
import org.chromium.ax.android.mojom.AccessibilityHelperInstance;
import org.chromium.ax.android.mojom.AccessibilityNotificationStateType;
import org.chromium.ax.android.mojom.AccessibilityWindowKey;
import org.chromium.ax.android.mojom.ActionFloatArgumentType;
import org.chromium.ax.android.mojom.ActionIntArgumentType;
import org.chromium.ax.android.mojom.ActionStringArgumentType;
import org.chromium.ax.android.mojom.SetNativeChromeVoxResponse;
import org.chromium.mojo.system.MojoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bridges Android accessibility to Chrome OS so that ChromeVox can read and
 * drive Android app content.
 *
 * <p>Two directions of traffic:
 *
 * <ul>
 *   <li>Android to Chrome — every accessibility event is converted, together
 *       with the tree it happened in, into {@code AccessibilityEventData} and
 *       pushed to {@link AccessibilityHelperHost}. Chrome turns that into an AX
 *       tree, attaches it to the ARC window's exo surface, and ChromeVox speaks
 *       it like any other Chrome tree.
 *   <li>Chrome to Android — ChromeVox actions arrive as
 *       {@code AccessibilityActionData} and are replayed on the live
 *       {@link AccessibilityNodeInfo}.
 * </ul>
 *
 * <p>Threading: accessibility callbacks arrive on the service's main thread,
 * Mojo calls arrive on the Mojo IO thread. Android accessibility state is read
 * on the main thread and every Mojo call (including deferred responses) is made
 * through {@link ArcBridgeSupport#runOnMojoThread}.
 */
public class ArcAccessibilityHelperBridge extends AccessibilityService
        implements AccessibilityHelperInstance, ArcService {
    static final String TAG = "ArcA11yBridge";

    /**
     * {@code OnAccessibilityHelperInstanceReady} was added to ArcBridgeHost at
     * MinVersion 21. Registering against an older host would kill the shared
     * bridge pipe, so gate on it.
     */
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 21;

    /** Attempts made to answer one of Chrome's tree requests. */
    private static final int TREE_REQUEST_ATTEMPTS = 4;

    /** Gap between those attempts, covering window registration lag. */
    private static final long TREE_REQUEST_RETRY_MS = 300;

    private final NodeIdMap mNodeIds = new NodeIdMap();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final WaylandAccessibilityProxy mWayland = new WaylandAccessibilityProxy();

    /** Notification keys Chrome has been told about, so edges are sent once. */
    private final Set<String> mAnnouncedNotifKeys = new HashSet<>();

    /**
     * Accessibility window id last published per surface, keyed the way
     * {@link AccessibilityTreeBuilder#liveSurfaces} names them. Diffed against
     * the live list so the compositor is only told about changes.
     */
    private final Map<String, Integer> mPublishedWindowIds = new HashMap<>();

    private AccessibilityTreeBuilder mTreeBuilder;

    /** Written on the Mojo thread, read on the main thread. */
    private volatile AccessibilityHelperHost mHost;

    /** Written on the main thread, read on the main thread. */
    private int mFilterType = AccessibilityFilterType.OFF;

    @Override
    public void onServiceConnected() {
        Log.i(TAG, "accessibility service connected");
        mTreeBuilder = new AccessibilityTreeBuilder(this, mNodeIds);
        // Stay quiet until Chrome asks for events: Chrome always sends SetFilter
        // once the Mojo connection is ready.
        applyFilterToServiceInfo(AccessibilityFilterType.OFF);
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                this, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final AccessibilityHelperHost host = mHost;
        if (host == null || mFilterType == AccessibilityFilterType.OFF) {
            return;
        }

        AccessibilityEventData data;
        try {
            if (mFilterType == AccessibilityFilterType.ALL) {
                // Must run before the event is queued: Chrome only creates a
                // notification tree on SURFACE_CREATED and drops events whose
                // notification_key it has not seen yet. Both go through the same
                // Mojo thread queue, so posting this first preserves the order.
                syncSurfaces(host);
            }
            data = mFilterType == AccessibilityFilterType.FOCUS
                    ? buildFocusPayload(event)
                    : buildTreePayload(event);
        } catch (RuntimeException e) {
            Log.e(TAG, "failed to convert accessibility event", e);
            return;
        }
        if (data == null) {
            return;
        }

        final AccessibilityEventData payload = data;
        ArcBridgeSupport.getInstance().runOnMojoThread(new Runnable() {
            @Override
            public void run() {
                try {
                    host.onAccessibilityEvent(payload);
                } catch (RuntimeException e) {
                    Log.e(TAG, "failed to send accessibility event", e);
                }
            }
        });
    }

    @Override
    public void onInterrupt() {
        Log.w(TAG, "accessibility service interrupted");
    }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        mNodeIds.clear();
        mAnnouncedNotifKeys.clear();
        clearPublishedWindowIds();
        return super.onUnbind(intent);
    }

    // --- Event payloads ---

    /** Full-tree payload for {@code AccessibilityFilterType.ALL}. */
    private AccessibilityEventData buildTreePayload(AccessibilityEvent event) {
        AccessibilityEventData data = mTreeBuilder.buildTreeForEvent(event);
        if (data != null) {
            return data;
        }
        // Events without a live window (toasts, announcements) carry no tree;
        // Chrome only announces those, so send the event on its own.
        return buildAnnouncementPayload(event);
    }

    /**
     * Payload for events that have no retrievable window, such as toasts and
     * announcements. Chrome routes these straight to ChromeVox's announcement
     * API and never touches the tree, so node and window data stay empty.
     */
    private AccessibilityEventData buildAnnouncementPayload(AccessibilityEvent event) {
        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_ANNOUNCEMENT
                && type != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            return null;
        }
        AccessibilityEventData data = new AccessibilityEventData();
        data.nodeData = new org.chromium.ax.android.mojom.AccessibilityNodeInfoData[0];
        data.windowId = event.getWindowId();
        data.taskId = android.app.ActivityTaskManager.INVALID_TASK_ID;
        data.sourceId = -1;
        mTreeBuilder.populateEventProperties(data, event);
        return data;
    }

    /**
     * Reconciles what Chrome believes about the surfaces it holds with what
     * Android's window list actually says, on both channels that carry it: the
     * Mojo notification-surface edges, and the per-surface accessibility window
     * ids that go to the compositor.
     *
     * <p>Both read the same window-list snapshot, which is an IPC into
     * system_server and would otherwise be paid twice per event.
     */
    private void syncSurfaces(final AccessibilityHelperHost host) {
        AccessibilityTreeBuilder.LiveSurfaces live = mTreeBuilder.liveSurfaces();
        syncNotificationSurfaces(host, live.notificationKeys);
        syncAccessibilityWindowIds(live.windowIds);
    }

    /**
     * Publishes the accessibility window id of each addressable Wayland surface
     * to the compositor, which turns it into
     * {@code zaura_surface.set_accessibility_id}. This is the only route by
     * which an accessibility window id reaches Chrome; see
     * {@link WaylandAccessibilityProxy}.
     *
     * <p>Only differences are sent — this runs on every accessibility event.
     * A surface that has gone away is published as -1 so Chrome clears the
     * mapping rather than keeping a dead window id that a later window could
     * be confused with.
     */
    private void syncAccessibilityWindowIds(
            Map<String, AccessibilityTreeBuilder.SurfaceWindowId> live) {
        for (Map.Entry<String, AccessibilityTreeBuilder.SurfaceWindowId> entry
                : live.entrySet()) {
            AccessibilityTreeBuilder.SurfaceWindowId id = entry.getValue();
            Integer published = mPublishedWindowIds.get(entry.getKey());
            if (published != null && published.intValue() == id.windowId) {
                continue;
            }
            if (mWayland.setAccessibilityWindowId(
                    id.target, id.taskId, id.key, id.windowId)) {
                mPublishedWindowIds.put(entry.getKey(), Integer.valueOf(id.windowId));
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "accessibility window id " + id.windowId
                            + " published for " + entry.getKey());
                }
            }
        }

        Iterator<Map.Entry<String, Integer>> gone =
                mPublishedWindowIds.entrySet().iterator();
        while (gone.hasNext()) {
            Map.Entry<String, Integer> entry = gone.next();
            if (live.containsKey(entry.getKey())) {
                continue;
            }
            AccessibilityTreeBuilder.SurfaceWindowId id = parseSurfaceKey(entry.getKey());
            if (id == null || mWayland.setAccessibilityWindowId(
                    id.target, id.taskId, id.key, -1)) {
                gone.remove();
            }
        }
    }

    /**
     * Drops every accessibility window id published so far, both here and in
     * Chrome. Used when accessibility is switched off or the service is
     * unbound: the surfaces outlive us, so a mapping left behind would keep a
     * dead window id attached to a live window.
     */
    private void clearPublishedWindowIds() {
        for (String key : mPublishedWindowIds.keySet()) {
            AccessibilityTreeBuilder.SurfaceWindowId id = parseSurfaceKey(key);
            if (id != null) {
                mWayland.setAccessibilityWindowId(id.target, id.taskId, id.key, -1);
            }
        }
        mPublishedWindowIds.clear();
    }

    /**
     * Recovers the compositor's addressing of a surface from the map key, so a
     * surface that has disappeared can still be named in the message that
     * clears it.
     */
    private static AccessibilityTreeBuilder.SurfaceWindowId parseSurfaceKey(String key) {
        if (key.equals("ime")) {
            return new AccessibilityTreeBuilder.SurfaceWindowId(
                    WaylandAccessibilityProxy.TARGET_INPUT_METHOD, 0, null, -1);
        }
        if (key.startsWith("notif:")) {
            return new AccessibilityTreeBuilder.SurfaceWindowId(
                    WaylandAccessibilityProxy.TARGET_NOTIFICATION, 0,
                    key.substring("notif:".length()), -1);
        }
        if (key.startsWith("task:")) {
            try {
                return new AccessibilityTreeBuilder.SurfaceWindowId(
                        WaylandAccessibilityProxy.TARGET_TASK,
                        Integer.parseInt(key.substring("task:".length())), null, -1);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        Log.w(TAG, "unknown surface key " + key);
        return null;
    }

    /**
     * Tells Chrome which notification surfaces exist, so it can create and tear
     * down the trees their events are keyed by.
     *
     * <p>Notification content renders into its own window on the shared
     * notification VirtualDisplay, so the live set is simply the notification
     * windows Android currently reports; diffing it against what has already
     * been announced yields the create/remove edges. Announcing is idempotent
     * per key, which matters because this runs on every event.
     */
    private void syncNotificationSurfaces(final AccessibilityHelperHost host,
            Set<String> live) {
        if (live.equals(mAnnouncedNotifKeys)) {
            return;
        }

        final List<String> added = new ArrayList<>();
        for (String key : live) {
            if (!mAnnouncedNotifKeys.contains(key)) {
                added.add(key);
            }
        }
        final List<String> removed = new ArrayList<>();
        for (String key : mAnnouncedNotifKeys) {
            if (!live.contains(key)) {
                removed.add(key);
            }
        }
        mAnnouncedNotifKeys.clear();
        mAnnouncedNotifKeys.addAll(live);

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "notification surfaces +" + added + " -" + removed);
        }

        ArcBridgeSupport.getInstance().runOnMojoThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String key : added) {
                        host.onNotificationStateChanged(
                                key, AccessibilityNotificationStateType.SURFACE_CREATED);
                    }
                    for (String key : removed) {
                        host.onNotificationStateChanged(
                                key, AccessibilityNotificationStateType.SURFACE_REMOVED);
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, "failed to send notification surface state", e);
                }
            }
        });
    }

    /** Single-node payload for {@code AccessibilityFilterType.FOCUS}. */
    private AccessibilityEventData buildFocusPayload(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            return null;
        }
        return mTreeBuilder.buildFocusEvent(event);
    }

    // --- AccessibilityHelperInstance (Mojo thread) ---

    @Override
    public void init(AccessibilityHelperHost host, InitResponse response) {
        Log.i(TAG, "Chrome connected");
        mHost = host;
        response.call();
    }

    @Override
    public void setFilter(final int filterType) {
        Log.i(TAG, "setFilter: " + filterType);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mFilterType = filterType;
                if (filterType == AccessibilityFilterType.OFF) {
                    mNodeIds.clear();
                    mAnnouncedNotifKeys.clear();
                    clearPublishedWindowIds();
                }
                applyFilterToServiceInfo(filterType);
            }
        });
    }

    @Override
    public void setExploreByTouchEnabled(final boolean enabled) {
        Log.i(TAG, "setExploreByTouchEnabled: " + enabled);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                AccessibilityServiceInfo info = getServiceInfo();
                if (info == null) {
                    return;
                }
                if (enabled) {
                    info.flags |= AccessibilityServiceInfo
                            .FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
                } else {
                    info.flags &= ~AccessibilityServiceInfo
                            .FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
                }
                setServiceInfo(info);
            }
        });
    }

    @Override
    public void performAction(final AccessibilityActionData actionData,
            final PerformActionResponse response) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                try {
                    success = doPerformAction(actionData);
                } catch (RuntimeException e) {
                    Log.e(TAG, "performAction failed", e);
                }
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "performAction type=" + actionData.actionType
                            + " node=" + actionData.nodeId + " -> " + success);
                }
                respond(response, Boolean.valueOf(success));
            }
        });
    }

    @Override
    public void refreshWithExtraData(final AccessibilityActionData actionData,
            final RefreshWithExtraDataResponse response) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                org.chromium.ax.android.mojom.Rect result = null;
                try {
                    result = doGetTextLocation(actionData);
                } catch (RuntimeException e) {
                    Log.e(TAG, "refreshWithExtraData failed", e);
                }
                respond(response, result);
            }
        });
    }

    @Override
    public void requestSendAccessibilityTree(final AccessibilityWindowKey windowKey) {
        postTreeRequest(windowKey, 0);
    }

    /**
     * Chrome asks for a tree as soon as the aura window appears, which can beat
     * Android registering the matching accessibility window. Retry briefly so a
     * freshly opened app is readable immediately rather than staying silent
     * until its next accessibility event.
     */
    private void postTreeRequest(final AccessibilityWindowKey windowKey, final int attempt) {
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!sendTreeForWindowKey(windowKey) && attempt < TREE_REQUEST_ATTEMPTS - 1) {
                        postTreeRequest(windowKey, attempt + 1);
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, "requestSendAccessibilityTree failed", e);
                }
            }
        }, attempt == 0 ? 0 : TREE_REQUEST_RETRY_MS);
    }

    @Override
    public void setNativeChromeVoxArcSupportForFocusedWindow(boolean enabled,
            SetNativeChromeVoxArcSupportForFocusedWindowResponse response) {
        // fydroid ships no TalkBack, so ChromeVox is the only screen reader that
        // can serve an ARC window. Asking to hand over to TalkBack must fail
        // loudly rather than silently leaving the window unread.
        int result = enabled
                ? SetNativeChromeVoxResponse.SUCCESS
                : SetNativeChromeVoxResponse.TALKBACK_NOT_INSTALLED;
        response.call(Integer.valueOf(result));
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        mHost = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        mHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "bridge ready, registering AccessibilityHelperInstance");
        host.onAccessibilityHelperInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "bridge host version " + version + " < "
                + MINIMUM_ARC_BRIDGE_HOST_VERSION + "; accessibility disabled");
    }

    // --- Action handling (main thread) ---

    private boolean doPerformAction(AccessibilityActionData actionData) {
        if (actionData.actionType == AccessibilityActionType.GET_TEXT_LOCATION) {
            // Handled through refreshWithExtraData.
            return false;
        }

        AccessibilityNodeInfo node =
                mTreeBuilder.resolveNode(actionData.nodeId, actionData.windowId);
        if (node == null) {
            Log.w(TAG, "performAction: no node for id " + actionData.nodeId);
            return false;
        }

        int androidAction;
        if (actionData.actionType == AccessibilityActionType.CUSTOM_ACTION) {
            androidAction = actionData.customActionId;
        } else {
            androidAction = AccessibilityTreeBuilder.toAndroidAction(actionData.actionType);
            if (androidAction == 0) {
                Log.w(TAG, "performAction: unmapped action " + actionData.actionType);
                return false;
            }
        }
        return node.performAction(androidAction, buildActionArguments(actionData));
    }

    private static Bundle buildActionArguments(AccessibilityActionData actionData) {
        Bundle args = new Bundle();
        Map<Integer, Integer> intParams = actionData.intParameters;
        if (intParams != null) {
            for (Map.Entry<Integer, Integer> entry : intParams.entrySet()) {
                String key = intArgumentKey(entry.getKey().intValue());
                if (key != null) {
                    args.putInt(key, entry.getValue().intValue());
                }
            }
        }
        Map<Integer, String> stringParams = actionData.stringParameters;
        if (stringParams != null) {
            for (Map.Entry<Integer, String> entry : stringParams.entrySet()) {
                if (entry.getKey().intValue()
                        == ActionStringArgumentType.SET_TEXT_CHARSEQUENCE) {
                    args.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            entry.getValue());
                } else if (entry.getKey().intValue()
                        == ActionStringArgumentType.HTML_ELEMENT_STRING) {
                    args.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_HTML_ELEMENT_STRING,
                            entry.getValue());
                }
            }
        }
        Map<Integer, Float> floatParams = actionData.floatParameters;
        if (floatParams != null) {
            for (Map.Entry<Integer, Float> entry : floatParams.entrySet()) {
                if (entry.getKey().intValue() == ActionFloatArgumentType.PROGRESS_VALUE) {
                    args.putFloat(AccessibilityNodeInfo.ACTION_ARGUMENT_PROGRESS_VALUE,
                            entry.getValue().floatValue());
                }
            }
        }
        return args;
    }

    private static String intArgumentKey(int argumentType) {
        switch (argumentType) {
            case ActionIntArgumentType.MOVEMENT_GRANULARITY_INT:
                return AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT;
            case ActionIntArgumentType.SELECTION_START_INT:
                return AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT;
            case ActionIntArgumentType.SELECTION_END_INT:
                return AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT;
            case ActionIntArgumentType.ROW_INT:
                return AccessibilityNodeInfo.ACTION_ARGUMENT_ROW_INT;
            case ActionIntArgumentType.COLUMN_INT:
                return AccessibilityNodeInfo.ACTION_ARGUMENT_COLUMN_INT;
            case ActionIntArgumentType.MOVE_WINDOW_X:
                return AccessibilityNodeInfo.ACTION_ARGUMENT_MOVE_WINDOW_X;
            case ActionIntArgumentType.MOVE_WINDOW_Y:
                return AccessibilityNodeInfo.ACTION_ARGUMENT_MOVE_WINDOW_Y;
            case ActionIntArgumentType.PRESS_AND_HOLD_DURATION_MILLIS_INT:
                return AccessibilityNodeInfo
                        .ACTION_ARGUMENT_PRESS_AND_HOLD_DURATION_MILLIS_INT;
            default:
                return null;
        }
    }

    /**
     * Resolves the character bounds ChromeVox needs to highlight text as it is
     * spoken.
     */
    private org.chromium.ax.android.mojom.Rect doGetTextLocation(
            AccessibilityActionData actionData) {
        AccessibilityNodeInfo node =
                mTreeBuilder.resolveNode(actionData.nodeId, actionData.windowId);
        if (node == null) {
            return null;
        }
        int start = actionData.startIndex;
        int length = actionData.endIndex - actionData.startIndex;
        if (length <= 0) {
            return null;
        }

        Bundle args = new Bundle();
        args.putInt(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX,
                start);
        args.putInt(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH,
                length);
        if (!node.refreshWithExtraData(
                AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY, args)) {
            return null;
        }

        Bundle extras = node.getExtras();
        if (extras == null) {
            return null;
        }
        Parcelable[] boxes = extras.getParcelableArray(
                AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY);
        if (boxes == null) {
            return null;
        }

        Rect union = null;
        for (Parcelable box : boxes) {
            if (!(box instanceof android.graphics.RectF)) {
                continue;
            }
            android.graphics.RectF rectF = (android.graphics.RectF) box;
            Rect rect = new Rect();
            rectF.roundOut(rect);
            if (union == null) {
                union = rect;
            } else {
                union.union(rect);
            }
        }
        if (union == null) {
            return null;
        }
        // Same space as the tree's node bounds: window-relative for a
        // standalone (overlay) tree, screen space otherwise.
        Rect origin = mTreeBuilder.standaloneOriginOf(node.getWindowId());
        union.offset(-origin.left, -origin.top);
        return AccessibilityTreeBuilder.toMojoRect(union);
    }

    /**
     * Pushes the whole tree for the window Chrome asked about.
     *
     * @return false if the window is not there (yet), so the caller can retry.
     */
    private boolean sendTreeForWindowKey(AccessibilityWindowKey windowKey) {
        final AccessibilityHelperHost host = mHost;
        if (host == null || mFilterType != AccessibilityFilterType.ALL) {
            return true;
        }

        boolean byWindowId = windowKey.which() == AccessibilityWindowKey.Tag.WindowId;
        final AccessibilityEventData data = byWindowId
                ? mTreeBuilder.buildTreeForWindowId(windowKey.getWindowId())
                : mTreeBuilder.buildTreeForTaskId(windowKey.getTaskId());
        if (data == null) {
            Log.w(TAG, "requestSendAccessibilityTree: no tree yet for "
                    + (byWindowId ? "window " + windowKey.getWindowId()
                                  : "task " + windowKey.getTaskId()));
            return false;
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            // Which key Chrome asked by is the whole diagnosis for a surface
            // that has no task: EnableTree can only name a window id when it
            // found an accessibility id on the surface, so "by window N" is the
            // proof that set_accessibility_id reached Chrome. Logging only the
            // failure (below) hid exactly the case worth seeing.
            Log.d(TAG, "requestSendAccessibilityTree: sent tree for "
                    + (byWindowId ? "window " + windowKey.getWindowId()
                                  : "task " + windowKey.getTaskId())
                    + " (task=" + data.taskId + " rootWindow=" + data.windowId + ")");
        }

        ArcBridgeSupport.getInstance().runOnMojoThread(new Runnable() {
            @Override
            public void run() {
                host.onAccessibilityEvent(data);
            }
        });
        return true;
    }

    // --- Service info ---

    private void applyFilterToServiceInfo(int filterType) {
        AccessibilityServiceInfo info = getServiceInfo();
        if (info == null) {
            return;
        }
        switch (filterType) {
            case AccessibilityFilterType.ALL:
                info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
                break;
            case AccessibilityFilterType.FOCUS:
                info.eventTypes = AccessibilityEvent.TYPE_VIEW_FOCUSED;
                break;
            default:
                // Stop the framework building and delivering events entirely
                // while no Chrome OS accessibility feature is using them.
                info.eventTypes = 0;
                break;
        }
        setServiceInfo(info);
    }

    // --- Mojo response plumbing ---

    // Fully qualified: AccessibilityService has its own nested Callbacks type,
    // which shadows an import of the Mojo one.
    private static <T> void respond(
            final org.chromium.mojo.bindings.Callbacks.Callback1<T> response, final T value) {
        ArcBridgeSupport.getInstance().runOnMojoThread(new Runnable() {
            @Override
            public void run() {
                response.call(value);
            }
        });
    }
}

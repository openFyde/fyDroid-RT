package org.chromium.arc.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityTaskManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import org.chromium.ax.android.mojom.AccessibilityActionInAndroid;
import org.chromium.ax.android.mojom.AccessibilityBooleanProperty;
import org.chromium.ax.android.mojom.AccessibilityCollectionInfoData;
import org.chromium.ax.android.mojom.AccessibilityCollectionItemInfoData;
import org.chromium.ax.android.mojom.AccessibilityEventData;
import org.chromium.ax.android.mojom.AccessibilityEventIntListProperty;
import org.chromium.ax.android.mojom.AccessibilityEventIntProperty;
import org.chromium.ax.android.mojom.AccessibilityEventStringProperty;
import org.chromium.ax.android.mojom.AccessibilityEventType;
import org.chromium.ax.android.mojom.AccessibilityIntListProperty;
import org.chromium.ax.android.mojom.AccessibilityIntProperty;
import org.chromium.ax.android.mojom.AccessibilityLiveRegionType;
import org.chromium.ax.android.mojom.AccessibilityNodeInfoData;
import org.chromium.ax.android.mojom.AccessibilityRangeInfoData;
import org.chromium.ax.android.mojom.AccessibilityRangeType;
import org.chromium.ax.android.mojom.AccessibilitySelectionMode;
import org.chromium.ax.android.mojom.AccessibilityStringProperty;
import org.chromium.ax.android.mojom.AccessibilityWindowBooleanProperty;
import org.chromium.ax.android.mojom.AccessibilityWindowInfoData;
import org.chromium.ax.android.mojom.AccessibilityWindowIntListProperty;
import org.chromium.ax.android.mojom.AccessibilityWindowIntProperty;
import org.chromium.ax.android.mojom.AccessibilityWindowStringProperty;
import org.chromium.ax.android.mojom.AccessibilityWindowType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converts live Android accessibility state into the Mojo structs Chrome's
 * {@code AXTreeSourceAndroid} expects.
 *
 * <p>Chrome rebuilds its whole node map from every event, so an event carrying
 * node data must describe a self-consistent tree:
 *
 * <ul>
 *   <li>{@code window_data} must be present and non-empty — Chrome CHECKs on it
 *       — and its first entry is taken as the tree root;
 *   <li>{@code task_id} must be a real Android task id, otherwise Chrome cannot
 *       find the aura window that hosts the tree and drops the event;
 *   <li>every id referenced by {@code CHILD_NODE_IDS}, {@code root_node_id} or
 *       {@code source_id} must also appear in the payload.
 * </ul>
 */
class AccessibilityTreeBuilder {
    private static final String TAG = ArcAccessibilityHelperBridge.TAG;

    /** Upper bound on nodes per tree, guarding against pathological hierarchies. */
    private static final int MAX_NODES = 5000;

    /**
     * Title prefix ArcNotificationRenderer stamps on each notification window
     * ({@code lp.setTitle("ArcNotif:" + key)}). Every notification gets its own
     * window on the shared notification VirtualDisplay, so the title is a
     * ready-made window-to-key mapping and no extra IPC is needed to recover it.
     */
    private static final String NOTIFICATION_TITLE_PREFIX = "ArcNotif:";

    /** Extras key WebView uses to expose the underlying Blink role. */
    private static final String EXTRAS_CHROME_ROLE = "AccessibilityNodeInfo.chromeRole";

    /** Extras key for a developer supplied role description. */
    private static final String EXTRAS_ROLE_DESCRIPTION = "AccessibilityNodeInfo.roleDescription";

    /** Android action id -> Mojo {@code AccessibilityActionType}. */
    private static final Map<Integer, Integer> ACTION_TO_MOJO = buildActionTable();

    private final AccessibilityService mService;
    private final NodeIdMap mIds;

    /**
     * Window list reused for the duration of one tree build. Fetching it is an
     * IPC into system_server and a single event needs it several times.
     */
    private List<AccessibilityWindowInfo> mCachedWindows;

    /** Node ids emitted by the last buildTree, for validating an event source. */
    private final Set<Integer> mEmittedNodeIds = new HashSet<>();

    /** Root node id of the last built tree — the safe fallback source id. */
    private int mRootNodeId = -1;

    /**
     * Subtracted from every bound while one tree is built. Zero except for a
     * standalone tree (see {@link #isStandaloneRoot}), whose bounds must be
     * relative to its own window — Chrome hosts that tree on the window's own
     * surface, which already sits at the window's on-screen position.
     */
    private int mOriginX;
    private int mOriginY;

    AccessibilityTreeBuilder(AccessibilityService service, NodeIdMap ids) {
        mService = service;
        mIds = ids;
    }

    // --- Window lookup ---

    /** Returns every window Android knows about, across all displays. */
    List<AccessibilityWindowInfo> allWindows() {
        if (mCachedWindows != null) {
            return mCachedWindows;
        }
        List<AccessibilityWindowInfo> result = new ArrayList<>();
        SparseArray<List<AccessibilityWindowInfo>> perDisplay =
                mService.getWindowsOnAllDisplays();
        if (perDisplay != null) {
            for (int i = 0; i < perDisplay.size(); i++) {
                List<AccessibilityWindowInfo> windows = perDisplay.valueAt(i);
                if (windows != null) {
                    result.addAll(windows);
                }
            }
        }
        return result;
    }

    /**
     * Pins the window list for one tree build. Android hands out snapshots, so
     * reusing them within a single event also keeps the tree self-consistent.
     */
    private void beginWindowSnapshot() {
        mCachedWindows = null;
        mCachedWindows = allWindows();
    }

    private void endWindowSnapshot() {
        mCachedWindows = null;
    }

    /**
     * Returns the notification key this window renders, or null if it is not a
     * notification window.
     */
    static String notificationKeyOf(AccessibilityWindowInfo window) {
        CharSequence title = window.getTitle();
        if (title == null) {
            return null;
        }
        String text = title.toString();
        return text.startsWith(NOTIFICATION_TITLE_PREFIX)
                ? text.substring(NOTIFICATION_TITLE_PREFIX.length())
                : null;
    }

    /**
     * One accessibility window, addressed the way the compositor names the
     * Wayland surface that renders it. Mirrors
     * {@code WaylandGlobals::AccessibilityTarget}.
     */
    static final class SurfaceWindowId {
        final int target;
        final int taskId;
        final String key;
        final int windowId;

        SurfaceWindowId(int target, int taskId, String key, int windowId) {
            this.target = target;
            this.taskId = taskId;
            this.key = key;
            this.windowId = windowId;
        }
    }

    /**
     * What the live window list says about the surfaces Chrome holds: which
     * notification cards exist, and which accessibility window each addressable
     * surface is currently showing.
     *
     * <p>Both come out of one window-list snapshot because fetching that list
     * is an IPC into system_server and this runs on every accessibility event.
     */
    static final class LiveSurfaces {
        final Set<String> notificationKeys;
        /** Keyed by the compositor's name for the surface, e.g. {@code task:166}. */
        final Map<String, SurfaceWindowId> windowIds;

        LiveSurfaces(Set<String> notificationKeys, Map<String, SurfaceWindowId> windowIds) {
            this.notificationKeys = notificationKeys;
            this.windowIds = windowIds;
        }
    }

    /**
     * Reads the live window list once and derives everything the per-event
     * state syncs need from it.
     *
     * <p>A task contributes the window a tree would be rooted at — the same one
     * {@link #findWindowByTaskId} picks — so that a tree Chrome asks for by
     * window id is the tree it would have got by task id. Toasts and app
     * overlays are deliberately absent: they have no task and no key, so
     * nothing here can name the surface they render into. The compositor
     * publishes theirs itself, from the layer's METADATA_ACCESSIBILITY_ID.
     * The PIP menu is absent too — it joins the pinned task's tree.
     */
    LiveSurfaces liveSurfaces() {
        Set<String> keys = new HashSet<>();
        Map<String, SurfaceWindowId> ids = new HashMap<>();
        Set<Integer> rootedTasks = new HashSet<>();
        for (AccessibilityWindowInfo window : allWindows()) {
            String notifKey = notificationKeyOf(window);
            if (notifKey != null) {
                keys.add(notifKey);
                ids.put("notif:" + notifKey,
                        new SurfaceWindowId(WaylandAccessibilityProxy.TARGET_NOTIFICATION,
                                0, notifKey, window.getId()));
                continue;
            }
            if (window.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                ids.put("ime",
                        new SurfaceWindowId(WaylandAccessibilityProxy.TARGET_INPUT_METHOD,
                                0, null, window.getId()));
                continue;
            }
            int taskId = window.getTaskId();
            if (taskId == ActivityTaskManager.INVALID_TASK_ID) {
                continue;
            }
            // Prefer a window that is not anchored to another one; an anchored
            // popup is part of its root window's tree, not a tree of its own.
            boolean isRoot = window.getParent() == null;
            String mapKey = "task:" + taskId;
            if (!isRoot && ids.containsKey(mapKey)) {
                continue;
            }
            if (isRoot && !rootedTasks.add(Integer.valueOf(taskId))) {
                continue;
            }
            ids.put(mapKey,
                    new SurfaceWindowId(WaylandAccessibilityProxy.TARGET_TASK,
                            taskId, null, window.getId()));
        }
        return new LiveSurfaces(keys, ids);
    }

    AccessibilityWindowInfo findWindowById(int windowId) {
        for (AccessibilityWindowInfo window : allWindows()) {
            if (window.getId() == windowId) {
                return window;
            }
        }
        return null;
    }

    /**
     * Returns the first window belonging to {@code taskId}, preferring one that
     * is not anchored to another window so the caller gets a tree root.
     */
    AccessibilityWindowInfo findWindowByTaskId(int taskId) {
        AccessibilityWindowInfo fallback = null;
        for (AccessibilityWindowInfo window : allWindows()) {
            if (window.getTaskId() != taskId) {
                continue;
            }
            if (window.getParent() == null) {
                return window;
            }
            if (fallback == null) {
                fallback = window;
            }
        }
        return fallback;
    }

    /** Walks up the anchored-window chain to the outermost window. */
    static AccessibilityWindowInfo rootWindowOf(AccessibilityWindowInfo window) {
        AccessibilityWindowInfo root = window;
        // Anchor chains are shallow, but bound the walk in case of a cycle.
        for (int i = 0; i < 16; i++) {
            AccessibilityWindowInfo parent = root.getParent();
            if (parent == null) {
                return root;
            }
            root = parent;
        }
        return root;
    }

    /**
     * True for the WM Shell's PIP menu: a task-less window in PIP mode.
     *
     * <p>It is not a tree of its own. ArcSystemUI stamps the pinned task's
     * TaskId on the menu's surface so the compositor draws it inside the PIP
     * window (see {@code pip-shell-menu-adoption}); Chrome therefore has exactly
     * one surface for both, the pinned task's. The menu has to join that task's
     * tree — sent on its own, as task -1, Chrome has nowhere to put it.
     */
    static boolean isPipMenuWindow(AccessibilityWindowInfo window) {
        return window.getTaskId() == ActivityTaskManager.INVALID_TASK_ID
                && window.isInPictureInPictureMode()
                && window.getParent() == null
                && window.getType() != AccessibilityWindowInfo.TYPE_INPUT_METHOD
                && notificationKeyOf(window) == null;
    }

    /** The pinned task's root window, or null when nothing is in PIP. */
    private AccessibilityWindowInfo findPinnedTaskWindow() {
        for (AccessibilityWindowInfo window : allWindows()) {
            if (window.getTaskId() != ActivityTaskManager.INVALID_TASK_ID
                    && window.isInPictureInPictureMode()
                    && window.getParent() == null) {
                return window;
            }
        }
        return null;
    }

    /**
     * The window a tree containing {@code window} is rooted at: its outermost
     * anchor, except that a PIP menu belongs to the pinned task's tree.
     */
    private AccessibilityWindowInfo treeRootOf(AccessibilityWindowInfo window) {
        AccessibilityWindowInfo root = rootWindowOf(window);
        if (isPipMenuWindow(root)) {
            AccessibilityWindowInfo pinned = findPinnedTaskWindow();
            if (pinned != null) {
                return pinned;
            }
        }
        return root;
    }

    /**
     * True when {@code rootWindow} roots a tree Chrome keys by window id and
     * hosts on the window's own surface: a task-less overlay or system window.
     * Not a notification (keyed by key), not the IME (its own path) and not a
     * PIP menu (joins the pinned task, see {@link #isPipMenuWindow}).
     */
    static boolean isStandaloneRoot(AccessibilityWindowInfo rootWindow) {
        return rootWindow.getTaskId() == ActivityTaskManager.INVALID_TASK_ID
                && rootWindow.getType() != AccessibilityWindowInfo.TYPE_INPUT_METHOD
                && notificationKeyOf(rootWindow) == null
                && !isPipMenuWindow(rootWindow);
    }

    /**
     * Where the standalone window holding {@code windowId} sits on screen, for
     * translating a screen-space rect (text location) into that window's own
     * space. (0, 0) for every other window.
     */
    Rect standaloneOriginOf(int windowId) {
        beginWindowSnapshot();
        try {
            Rect origin = new Rect();
            AccessibilityWindowInfo window = findWindowById(windowId);
            if (window != null) {
                AccessibilityWindowInfo root = treeRootOf(window);
                if (isStandaloneRoot(root)) {
                    root.getBoundsInScreen(origin);
                }
            }
            return origin;
        } finally {
            endWindowSnapshot();
        }
    }

    // --- Tree construction ---

    /**
     * Builds the tree containing {@code event}'s source window and copies the
     * event's own properties onto it.
     *
     * @return null if the event happened in a window with no retrievable
     *     content, which is normal for toasts and announcements.
     */
    AccessibilityEventData buildTreeForEvent(AccessibilityEvent event) {
        beginWindowSnapshot();
        try {
            AccessibilityWindowInfo source = findWindowById(event.getWindowId());
            if (source == null) {
                return null;
            }
            AccessibilityEventData data = buildTree(treeRootOf(source));
            if (data == null) {
                return null;
            }
            populateEventProperties(data, event);
            return data;
        } finally {
            endWindowSnapshot();
        }
    }

    /** Builds the tree for the window Chrome named by Android window id. */
    AccessibilityEventData buildTreeForWindowId(int windowId) {
        beginWindowSnapshot();
        try {
            AccessibilityWindowInfo window = findWindowById(windowId);
            return window == null ? null : buildTree(treeRootOf(window));
        } finally {
            endWindowSnapshot();
        }
    }

    /** Builds the tree for the window Chrome named by Android task id. */
    AccessibilityEventData buildTreeForTaskId(int taskId) {
        beginWindowSnapshot();
        try {
            AccessibilityWindowInfo window = findWindowByTaskId(taskId);
            return window == null ? null : buildTree(treeRootOf(window));
        } finally {
            endWindowSnapshot();
        }
    }

    /**
     * Resolves an id previously handed to Chrome back onto the live Android
     * node.
     *
     * <p>Nodes cannot be cached — the framework hands out short-lived snapshots
     * — so the window is re-walked looking for the matching source node id.
     * Chrome keeps windows and nodes in one id space, so an action can also
     * target a window; acting on a window means acting on its root node.
     */
    AccessibilityNodeInfo resolveNode(int id, int fallbackWindowId) {
        beginWindowSnapshot();
        try {
            NodeIdMap.NodeRef ref = mIds.refFor(id);
            if (ref == null) {
                AccessibilityWindowInfo window = findWindowById(id);
                return window != null ? window.getRoot() : null;
            }
            AccessibilityWindowInfo window = findWindowById(ref.windowId);
            if (window == null) {
                window = findWindowById(fallbackWindowId);
            }
            if (window == null) {
                return null;
            }
            AccessibilityNodeInfo root = window.getRoot();
            return root == null ? null : findBySourceNodeId(root, ref.sourceNodeId, 0);
        } finally {
            endWindowSnapshot();
        }
    }

    private static AccessibilityNodeInfo findBySourceNodeId(
            AccessibilityNodeInfo node, long sourceNodeId, int depth) {
        if (depth > 100) {
            return null;
        }
        if (node.getSourceNodeId() == sourceNodeId) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child == null) {
                continue;
            }
            AccessibilityNodeInfo found = findBySourceNodeId(child, sourceNodeId, depth + 1);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Builds the complete tree rooted at {@code rootWindow}, covering the root
     * window and every window anchored beneath it.
     *
     * @return an event pre-populated with window, node and task data, or null if
     *     the window exposes no retrievable content.
     */
    private AccessibilityEventData buildTree(AccessibilityWindowInfo rootWindow) {
        try {
            return buildTreeInternal(rootWindow);
        } finally {
            mOriginX = 0;
            mOriginY = 0;
        }
    }

    private AccessibilityEventData buildTreeInternal(AccessibilityWindowInfo rootWindow) {
        List<AccessibilityWindowInfo> windows = new ArrayList<>();
        collectWindowSubtree(rootWindow, windows);

        // A pinned task adopts the PIP menu as a child window — see
        // isPipMenuWindow. Chrome parents windows by CHILD_WINDOW_IDS, so the
        // root has to list it, and the menu points back at the root.
        List<Integer> adopted = new ArrayList<>();
        if (rootWindow.getTaskId() != ActivityTaskManager.INVALID_TASK_ID
                && rootWindow.isInPictureInPictureMode()) {
            for (AccessibilityWindowInfo window : allWindows()) {
                if (isPipMenuWindow(window)) {
                    adopted.add(Integer.valueOf(window.getId()));
                    collectWindowSubtree(window, windows);
                }
            }
        }

        if (isStandaloneRoot(rootWindow)) {
            Rect origin = new Rect();
            rootWindow.getBoundsInScreen(origin);
            mOriginX = origin.left;
            mOriginY = origin.top;
        } else {
            mOriginX = 0;
            mOriginY = 0;
        }

        List<AccessibilityWindowInfoData> windowData = new ArrayList<>();
        List<AccessibilityNodeInfoData> nodeData = new ArrayList<>();
        for (AccessibilityWindowInfo window : windows) {
            AccessibilityNodeInfo root = window.getRoot();
            int rootNodeId = 0;
            if (root != null) {
                rootNodeId = mIds.idFor(window.getId(), root.getSourceNodeId());
                collectNodes(root, window.getId(), nodeData);
            }
            AccessibilityWindowInfoData converted = convertWindow(window, rootNodeId);
            if (window == rootWindow && !adopted.isEmpty()) {
                addChildWindowIds(converted, adopted);
            } else if (adopted.contains(Integer.valueOf(window.getId()))) {
                converted.intProperties.put(AccessibilityWindowIntProperty.PARENT_WINDOW_ID,
                        Integer.valueOf(rootWindow.getId()));
            }
            windowData.add(converted);
        }

        if (windowData.isEmpty() || nodeData.isEmpty()) {
            return null;
        }

        // Remember what this tree actually contains, so the event's source id
        // can be validated against it — see populateEventProperties.
        mEmittedNodeIds.clear();
        for (AccessibilityNodeInfoData node : nodeData) {
            mEmittedNodeIds.add(Integer.valueOf(node.id));
        }
        mRootNodeId = nodeData.get(0).id;

        AccessibilityEventData data = new AccessibilityEventData();
        data.windowData = windowData.toArray(new AccessibilityWindowInfoData[0]);
        data.nodeData = nodeData.toArray(new AccessibilityNodeInfoData[0]);
        data.windowId = rootWindow.getId();
        data.taskId = rootWindow.getTaskId();
        data.isInputMethodWindow =
                rootWindow.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD;
        // Chrome keys notification trees by notification_key, not by task, and
        // checks it before anything else — a notification window has no task, so
        // without this its events are looked up as a task and dropped.
        data.notificationKey = notificationKeyOf(rootWindow);
        data.sourceId = -1;
        data.eventType = AccessibilityEventType.WINDOW_STATE_CHANGED;

        if (data.taskId == ActivityTaskManager.INVALID_TASK_ID
                && !data.isInputMethodWindow && data.notificationKey == null
                && Log.isLoggable(TAG, Log.DEBUG)) {
            // Chrome keys this tree by window id, and finds the surface by the
            // accessibility id the compositor read off the layer's
            // METADATA_ACCESSIBILITY_ID. Bounds are window-relative.
            Log.d(TAG, "standalone window " + rootWindow.getId() + " origin="
                    + mOriginX + "," + mOriginY);
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            StringBuilder sb = new StringBuilder("tree task=").append(data.taskId)
                    .append(" rootWindow=").append(data.windowId)
                    .append(" nodes=").append(nodeData.size())
                    .append(" windows=[");
            for (AccessibilityWindowInfo window : windows) {
                Rect wb = new Rect();
                window.getBoundsInScreen(wb);
                sb.append(window.getId()).append(":task=").append(window.getTaskId())
                        .append(":type=").append(window.getType())
                        .append(':').append(wb.toShortString()).append(' ');
            }
            Log.d(TAG, sb.append(']').toString());
        }

        pruneStaleIds();
        return data;
    }

    /** Appends {@code ids} to a converted window's CHILD_WINDOW_IDS. */
    private static void addChildWindowIds(AccessibilityWindowInfoData data, List<Integer> ids) {
        List<Integer> all = new ArrayList<>();
        if (data.intListProperties != null) {
            int[] existing = data.intListProperties.get(
                    AccessibilityWindowIntListProperty.CHILD_WINDOW_IDS);
            if (existing != null) {
                for (int id : existing) {
                    all.add(Integer.valueOf(id));
                }
            }
        } else {
            data.intListProperties = new HashMap<>();
        }
        for (Integer id : ids) {
            if (!all.contains(id)) {
                all.add(id);
            }
        }
        data.intListProperties.put(AccessibilityWindowIntListProperty.CHILD_WINDOW_IDS,
                toIntArray(all));
    }

    /** Collects {@code window} followed by every window anchored beneath it. */
    private void collectWindowSubtree(
            AccessibilityWindowInfo window, List<AccessibilityWindowInfo> out) {
        if (out.size() > 64) {
            return;
        }
        out.add(window);
        for (int i = 0; i < window.getChildCount(); i++) {
            AccessibilityWindowInfo child = window.getChild(i);
            if (child != null) {
                collectWindowSubtree(child, out);
            }
        }
    }

    /** Forgets ids belonging to windows Android has since torn down. */
    private void pruneStaleIds() {
        Set<Integer> live = new HashSet<>();
        for (AccessibilityWindowInfo window : allWindows()) {
            live.add(Integer.valueOf(window.getId()));
        }
        mIds.retainWindows(live);
    }

    // --- Event conversion ---

    /**
     * Copies the event's own properties (type, source, text, scroll state) onto
     * {@code data}, which already holds the tree from {@link #buildTree}.
     */
    void populateEventProperties(AccessibilityEventData data, AccessibilityEvent event) {
        data.eventType = toMojoEventType(event.getEventType());

        // Chrome takes source_id as the root of the subtree to re-serialize and
        // drops the whole update when that id is not in the payload ("Update
        // root node doesn't exist"). Two ways that happens, both routine:
        // the event carries no source at all (window and announcement events),
        // or its source died before this tree was walked (any screen change,
        // e.g. a fragment swap). Either way, fall back to the tree root and
        // re-serialize everything rather than let the update be discarded.
        int sourceId = -1;
        long sourceNodeId = event.getSourceNodeId();
        if (sourceNodeId != AccessibilityNodeInfo.UNDEFINED_NODE_ID
                && event.getWindowId() != AccessibilityWindowInfo.UNDEFINED_WINDOW_ID) {
            sourceId = mIds.idFor(event.getWindowId(), sourceNodeId);
        }
        if (!mEmittedNodeIds.isEmpty()
                && !mEmittedNodeIds.contains(Integer.valueOf(sourceId))) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "event source " + sourceId
                        + " is not in the tree; re-serializing from the root");
            }
            sourceId = mRootNodeId;
        }
        data.sourceId = sourceId;

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                Rect nb = new Rect();
                source.getBoundsInScreen(nb);
                Log.d(TAG, "event " + AccessibilityEvent.eventTypeToString(
                        event.getEventType()) + " source=" + data.sourceId
                        + " bounds=" + nb.toShortString()
                        + " text=" + source.getText()
                        + " desc=" + source.getContentDescription());
            }
        }

        List<CharSequence> texts = event.getText();
        if (texts != null && !texts.isEmpty()) {
            List<String> textList = new ArrayList<>(texts.size());
            for (CharSequence cs : texts) {
                if (cs != null) {
                    textList.add(cs.toString());
                }
            }
            if (!textList.isEmpty()) {
                data.eventText = textList.toArray(new String[0]);
            }
        }

        Map<Integer, Integer> intProps = new HashMap<>();
        // ACTION must be a Mojo AccessibilityActionType, not Android's action
        // id. Chrome casts this field straight into that enum and runs it
        // through ConvertToChromeAction, whose switch ends in NOTREACHED() —
        // so an unmapped value is not ignored, it takes the browser down with
        // SIGTRAP. Android's ids are far outside the enum's range (ACTION_CLICK
        // is 16, ACTION_ACCESSIBILITY_FOCUS 64, the R.id.* ones ~0x0102003d),
        // and an event only carries one at all when an action caused it, so
        // omit the property unless it maps.
        Integer mojoAction = ACTION_TO_MOJO.get(Integer.valueOf(event.getAction()));
        if (mojoAction != null) {
            intProps.put(AccessibilityEventIntProperty.ACTION, mojoAction);
        }
        intProps.put(AccessibilityEventIntProperty.FROM_INDEX, event.getFromIndex());
        intProps.put(AccessibilityEventIntProperty.TO_INDEX, event.getToIndex());
        intProps.put(AccessibilityEventIntProperty.ITEM_COUNT, event.getItemCount());
        intProps.put(AccessibilityEventIntProperty.CURRENT_ITEM_INDEX,
                event.getCurrentItemIndex());
        intProps.put(AccessibilityEventIntProperty.SCROLL_X, event.getScrollX());
        intProps.put(AccessibilityEventIntProperty.SCROLL_Y, event.getScrollY());
        intProps.put(AccessibilityEventIntProperty.MAX_SCROLL_X, event.getMaxScrollX());
        intProps.put(AccessibilityEventIntProperty.MAX_SCROLL_Y, event.getMaxScrollY());
        intProps.put(AccessibilityEventIntProperty.SCROLL_DELTA_X, event.getScrollDeltaX());
        intProps.put(AccessibilityEventIntProperty.SCROLL_DELTA_Y, event.getScrollDeltaY());
        data.intProperties = intProps;

        Map<Integer, String> stringProps = new HashMap<>();
        // Chrome only announces a NOTIFICATION_STATE_CHANGED event when the
        // class name identifies it as a toast, so this is not optional.
        putIfPresent(stringProps, AccessibilityEventStringProperty.CLASS_NAME,
                event.getClassName());
        putIfPresent(stringProps, AccessibilityEventStringProperty.PACKAGE_NAME,
                event.getPackageName());
        putIfPresent(stringProps, AccessibilityEventStringProperty.CONTENT_DESCRIPTION,
                event.getContentDescription());
        if (!stringProps.isEmpty()) {
            data.stringProperties = stringProps;
        }

        Map<Integer, int[]> intListProps = new HashMap<>();
        intListProps.put(AccessibilityEventIntListProperty.CONTENT_CHANGE_TYPES,
                new int[] {event.getContentChangeTypes()});
        data.intListProperties = intListProps;
    }

    /**
     * Builds the single-node payload Chrome expects while the filter is FOCUS:
     * a VIEW_FOCUSED event carrying only the focused node.
     */
    AccessibilityEventData buildFocusEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return null;
        }
        AccessibilityEventData data = new AccessibilityEventData();
        data.eventType = AccessibilityEventType.VIEW_FOCUSED;
        data.windowId = event.getWindowId();
        data.taskId = ActivityTaskManager.INVALID_TASK_ID;
        data.sourceId = mIds.idFor(event.getWindowId(), source.getSourceNodeId());
        data.nodeData = new AccessibilityNodeInfoData[] {
                convertNode(source, event.getWindowId())};
        return data;
    }

    static int toMojoEventType(int androidType) {
        switch (androidType) {
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return AccessibilityEventType.VIEW_FOCUSED;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return AccessibilityEventType.VIEW_CLICKED;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return AccessibilityEventType.VIEW_LONG_CLICKED;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return AccessibilityEventType.VIEW_SELECTED;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return AccessibilityEventType.VIEW_TEXT_CHANGED;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return AccessibilityEventType.WINDOW_STATE_CHANGED;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return AccessibilityEventType.NOTIFICATION_STATE_CHANGED;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                return AccessibilityEventType.VIEW_HOVER_ENTER;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                return AccessibilityEventType.VIEW_HOVER_EXIT;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                return AccessibilityEventType.TOUCH_EXPLORATION_GESTURE_START;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                return AccessibilityEventType.TOUCH_EXPLORATION_GESTURE_END;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return AccessibilityEventType.WINDOW_CONTENT_CHANGED;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                return AccessibilityEventType.VIEW_SCROLLED;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return AccessibilityEventType.VIEW_TEXT_SELECTION_CHANGED;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                return AccessibilityEventType.ANNOUNCEMENT;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                return AccessibilityEventType.VIEW_ACCESSIBILITY_FOCUSED;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                return AccessibilityEventType.VIEW_ACCESSIBILITY_FOCUS_CLEARED;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                return AccessibilityEventType
                        .VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                return AccessibilityEventType.GESTURE_DETECTION_START;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                return AccessibilityEventType.GESTURE_DETECTION_END;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                return AccessibilityEventType.TOUCH_INTERACTION_START;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                return AccessibilityEventType.TOUCH_INTERACTION_END;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                return AccessibilityEventType.WINDOWS_CHANGED;
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                return AccessibilityEventType.VIEW_CONTEXT_CLICKED;
            case AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT:
                return AccessibilityEventType.ASSIST_READING_CONTEXT;
            default:
                return AccessibilityEventType.INVALID_ENUM_VALUE;
        }
    }

    // --- Window conversion ---

    private AccessibilityWindowInfoData convertWindow(
            AccessibilityWindowInfo window, int rootNodeId) {
        AccessibilityWindowInfoData data = new AccessibilityWindowInfoData();
        data.windowId = window.getId();
        data.rootNodeId = rootNodeId;
        data.windowType = toMojoWindowType(window.getType());

        Rect bounds = new Rect();
        window.getBoundsInScreen(bounds);
        bounds.offset(-mOriginX, -mOriginY);
        data.boundsInScreen = toMojoRect(bounds);

        Map<Integer, Boolean> boolProps = new HashMap<>();
        boolProps.put(AccessibilityWindowBooleanProperty.FOCUSED, window.isFocused());
        boolProps.put(AccessibilityWindowBooleanProperty.ACCESSIBILITY_FOCUSED,
                window.isAccessibilityFocused());
        boolProps.put(AccessibilityWindowBooleanProperty.IN_PICTURE_IN_PICTURE_MODE,
                window.isInPictureInPictureMode());
        boolProps.put(AccessibilityWindowBooleanProperty.WINDOW_ACTIVE, window.isActive());
        data.booleanProperties = boolProps;

        Map<Integer, String> stringProps = new HashMap<>();
        // A notification window's title carries the notification key, which is
        // plumbing, not something to read out. Suppress it so ChromeVox does not
        // announce "ArcNotif:0|com.example|1234|null|10048".
        if (notificationKeyOf(window) == null) {
            putIfPresent(stringProps, AccessibilityWindowStringProperty.TITLE,
                    window.getTitle());
        }
        if (!stringProps.isEmpty()) {
            data.stringProperties = stringProps;
        }

        Map<Integer, Integer> intProps = new HashMap<>();
        intProps.put(AccessibilityWindowIntProperty.LAYER_ORDER, window.getLayer());
        AccessibilityWindowInfo parent = window.getParent();
        if (parent != null) {
            intProps.put(AccessibilityWindowIntProperty.PARENT_WINDOW_ID, parent.getId());
        }
        AccessibilityNodeInfo anchor = window.getAnchor();
        if (anchor != null) {
            intProps.put(AccessibilityWindowIntProperty.ANCHOR_NODE_ID,
                    mIds.idFor(anchor.getWindowId(), anchor.getSourceNodeId()));
        }
        data.intProperties = intProps;

        int childCount = window.getChildCount();
        if (childCount > 0) {
            List<Integer> childIds = new ArrayList<>(childCount);
            for (int i = 0; i < childCount; i++) {
                AccessibilityWindowInfo child = window.getChild(i);
                if (child != null) {
                    childIds.add(Integer.valueOf(child.getId()));
                }
            }
            if (!childIds.isEmpty()) {
                Map<Integer, int[]> intListProps = new HashMap<>();
                intListProps.put(AccessibilityWindowIntListProperty.CHILD_WINDOW_IDS,
                        toIntArray(childIds));
                data.intListProperties = intListProps;
            }
        }

        return data;
    }

    private static int toMojoWindowType(int androidType) {
        switch (androidType) {
            case AccessibilityWindowInfo.TYPE_APPLICATION:
                return AccessibilityWindowType.TYPE_APPLICATION;
            case AccessibilityWindowInfo.TYPE_INPUT_METHOD:
                return AccessibilityWindowType.TYPE_INPUT_METHOD;
            case AccessibilityWindowInfo.TYPE_SYSTEM:
                return AccessibilityWindowType.TYPE_SYSTEM;
            case AccessibilityWindowInfo.TYPE_ACCESSIBILITY_OVERLAY:
                return AccessibilityWindowType.TYPE_ACCESSIBILITY_OVERLAY;
            case AccessibilityWindowInfo.TYPE_SPLIT_SCREEN_DIVIDER:
                return AccessibilityWindowType.TYPE_SPLIT_SCREEN_DIVIDER;
            default:
                return AccessibilityWindowType.INVALID_ENUM_VALUE;
        }
    }

    // --- Node conversion ---

    /** Depth-first walk adding {@code node} and all its descendants to {@code out}. */
    private void collectNodes(
            AccessibilityNodeInfo node, int windowId, List<AccessibilityNodeInfoData> out) {
        if (out.size() >= MAX_NODES) {
            if (out.size() == MAX_NODES) {
                Log.w(TAG, "node limit " + MAX_NODES + " reached; tree truncated");
                out.add(convertNode(node, windowId));
            }
            return;
        }
        out.add(convertNode(node, windowId));

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                collectNodes(child, windowId, out);
            }
        }
    }

    AccessibilityNodeInfoData convertNode(AccessibilityNodeInfo node, int windowId) {
        AccessibilityNodeInfoData data = new AccessibilityNodeInfoData();

        long sourceNodeId = node.getSourceNodeId();
        data.id = mIds.idFor(windowId, sourceNodeId);
        data.windowId = windowId;
        data.isVirtualNode = AccessibilityNodeInfo.getVirtualDescendantId(sourceNodeId)
                != AccessibilityNodeInfo.UNDEFINED_ITEM_ID;

        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        bounds.offset(-mOriginX, -mOriginY);
        data.boundsInScreen = toMojoRect(bounds);

        Map<Integer, Boolean> boolProps = new HashMap<>();
        boolProps.put(AccessibilityBooleanProperty.CHECKABLE, node.isCheckable());
        boolProps.put(AccessibilityBooleanProperty.CHECKED, node.isChecked());
        boolProps.put(AccessibilityBooleanProperty.FOCUSABLE, node.isFocusable());
        boolProps.put(AccessibilityBooleanProperty.FOCUSED, node.isFocused());
        boolProps.put(AccessibilityBooleanProperty.SELECTED, node.isSelected());
        boolProps.put(AccessibilityBooleanProperty.CLICKABLE, node.isClickable());
        boolProps.put(AccessibilityBooleanProperty.LONG_CLICKABLE, node.isLongClickable());
        boolProps.put(AccessibilityBooleanProperty.ENABLED, node.isEnabled());
        boolProps.put(AccessibilityBooleanProperty.PASSWORD, node.isPassword());
        boolProps.put(AccessibilityBooleanProperty.SCROLLABLE, node.isScrollable());
        boolProps.put(AccessibilityBooleanProperty.ACCESSIBILITY_FOCUSED,
                node.isAccessibilityFocused());
        boolProps.put(AccessibilityBooleanProperty.VISIBLE_TO_USER, node.isVisibleToUser());
        boolProps.put(AccessibilityBooleanProperty.EDITABLE, node.isEditable());
        boolProps.put(AccessibilityBooleanProperty.OPENS_POPUP, node.canOpenPopup());
        boolProps.put(AccessibilityBooleanProperty.DISMISSABLE, node.isDismissable());
        boolProps.put(AccessibilityBooleanProperty.MULTI_LINE, node.isMultiLine());
        boolProps.put(AccessibilityBooleanProperty.CONTENT_INVALID, node.isContentInvalid());
        boolProps.put(AccessibilityBooleanProperty.CONTEXT_CLICKABLE,
                node.isContextClickable());
        boolProps.put(AccessibilityBooleanProperty.IMPORTANCE,
                node.isImportantForAccessibility());
        boolProps.put(AccessibilityBooleanProperty.SCREEN_READER_FOCUSABLE,
                node.isScreenReaderFocusable());
        boolProps.put(AccessibilityBooleanProperty.SHOWING_HINT_TEXT, node.isShowingHintText());
        boolProps.put(AccessibilityBooleanProperty.HEADING, node.isHeading());
        boolProps.put(AccessibilityBooleanProperty.SUPPORTS_TEXT_LOCATION,
                node.getAvailableExtraData().contains(
                        AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY));
        data.booleanProperties = boolProps;

        Map<Integer, String> stringProps = new HashMap<>();
        putIfPresent(stringProps, AccessibilityStringProperty.PACKAGE_NAME,
                node.getPackageName());
        putIfPresent(stringProps, AccessibilityStringProperty.CLASS_NAME, node.getClassName());
        putIfPresent(stringProps, AccessibilityStringProperty.TEXT, node.getText());
        putIfPresent(stringProps, AccessibilityStringProperty.CONTENT_DESCRIPTION,
                node.getContentDescription());
        putIfPresent(stringProps, AccessibilityStringProperty.STATE_DESCRIPTION,
                node.getStateDescription());
        putIfPresent(stringProps, AccessibilityStringProperty.TOOLTIP, node.getTooltipText());
        putIfPresent(stringProps, AccessibilityStringProperty.PANE_TITLE, node.getPaneTitle());
        putIfPresent(stringProps, AccessibilityStringProperty.HINT_TEXT, node.getHintText());
        if (node.getViewIdResourceName() != null) {
            stringProps.put(AccessibilityStringProperty.VIEW_ID_RESOURCE_NAME,
                    node.getViewIdResourceName());
        }
        Bundle extras = node.getExtras();
        if (extras != null) {
            putIfPresent(stringProps, AccessibilityStringProperty.CHROME_ROLE,
                    extras.getCharSequence(EXTRAS_CHROME_ROLE));
            putIfPresent(stringProps, AccessibilityStringProperty.ROLE_DESCRIPTION,
                    extras.getCharSequence(EXTRAS_ROLE_DESCRIPTION));
        }
        data.stringProperties = stringProps;

        Map<Integer, Integer> intProps = new HashMap<>();
        intProps.put(AccessibilityIntProperty.TEXT_SELECTION_START,
                node.getTextSelectionStart());
        intProps.put(AccessibilityIntProperty.TEXT_SELECTION_END, node.getTextSelectionEnd());
        intProps.put(AccessibilityIntProperty.INPUT_TYPE, node.getInputType());
        intProps.put(AccessibilityIntProperty.LIVE_REGION,
                toMojoLiveRegion(node.getLiveRegion()));
        intProps.put(AccessibilityIntProperty.MAX_TEXT_LENGTH, node.getMaxTextLength());
        putNodeRef(intProps, AccessibilityIntProperty.LABEL_FOR, node.getLabelFor());
        putNodeRef(intProps, AccessibilityIntProperty.LABELED_BY, node.getLabeledBy());
        putNodeRef(intProps, AccessibilityIntProperty.TRAVERSAL_BEFORE,
                node.getTraversalBefore());
        putNodeRef(intProps, AccessibilityIntProperty.TRAVERSAL_AFTER,
                node.getTraversalAfter());
        data.intProperties = intProps;

        int childCount = node.getChildCount();
        if (childCount > 0) {
            List<Integer> childIds = new ArrayList<>(childCount);
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    childIds.add(Integer.valueOf(
                            mIds.idFor(windowId, child.getSourceNodeId())));
                }
            }
            if (!childIds.isEmpty()) {
                Map<Integer, int[]> intListProps = new HashMap<>();
                intListProps.put(AccessibilityIntListProperty.CHILD_NODE_IDS,
                        toIntArray(childIds));
                data.intListProperties = intListProps;
            }
        }

        data.collectionInfo = toMojoCollectionInfo(node.getCollectionInfo());
        data.collectionItemInfo = toMojoCollectionItemInfo(node.getCollectionItemInfo());
        data.rangeInfo = toMojoRangeInfo(node.getRangeInfo());
        populateActions(node, data);

        return data;
    }

    /**
     * Splits Android's action list into the framework actions Chrome knows by
     * enum value and the app-defined ones it exposes as custom actions.
     */
    private static void populateActions(
            AccessibilityNodeInfo node, AccessibilityNodeInfoData data) {
        List<AccessibilityNodeInfo.AccessibilityAction> actions = node.getActionList();
        if (actions == null || actions.isEmpty()) {
            return;
        }
        List<AccessibilityActionInAndroid> standard = new ArrayList<>();
        List<AccessibilityActionInAndroid> custom = new ArrayList<>();
        for (AccessibilityNodeInfo.AccessibilityAction action : actions) {
            Integer mojoType = ACTION_TO_MOJO.get(Integer.valueOf(action.getId()));
            AccessibilityActionInAndroid entry = new AccessibilityActionInAndroid();
            CharSequence label = action.getLabel();
            if (mojoType != null) {
                entry.id = mojoType.intValue();
                entry.label = label != null ? label.toString() : null;
                standard.add(entry);
            } else if (label != null) {
                // Chrome dereferences the label of every custom action, so an
                // unlabelled app-defined action must not be forwarded.
                entry.id = action.getId();
                entry.label = label.toString();
                custom.add(entry);
            }
        }
        if (!standard.isEmpty()) {
            data.standardActions = standard.toArray(new AccessibilityActionInAndroid[0]);
        }
        if (!custom.isEmpty()) {
            data.customActions = custom.toArray(new AccessibilityActionInAndroid[0]);
        }
    }

    private static AccessibilityCollectionInfoData toMojoCollectionInfo(
            AccessibilityNodeInfo.CollectionInfo info) {
        if (info == null) {
            return null;
        }
        AccessibilityCollectionInfoData data = new AccessibilityCollectionInfoData();
        data.rowCount = info.getRowCount();
        data.columnCount = info.getColumnCount();
        data.isHierarchical = info.isHierarchical();
        switch (info.getSelectionMode()) {
            case AccessibilityNodeInfo.CollectionInfo.SELECTION_MODE_SINGLE:
                data.selectionMode = AccessibilitySelectionMode.SINGLE;
                break;
            case AccessibilityNodeInfo.CollectionInfo.SELECTION_MODE_MULTIPLE:
                data.selectionMode = AccessibilitySelectionMode.MULTIPLE;
                break;
            default:
                data.selectionMode = AccessibilitySelectionMode.NONE;
                break;
        }
        return data;
    }

    private static AccessibilityCollectionItemInfoData toMojoCollectionItemInfo(
            AccessibilityNodeInfo.CollectionItemInfo info) {
        if (info == null) {
            return null;
        }
        AccessibilityCollectionItemInfoData data = new AccessibilityCollectionItemInfoData();
        data.rowIndex = info.getRowIndex();
        data.rowSpan = info.getRowSpan();
        data.columnIndex = info.getColumnIndex();
        data.columnSpan = info.getColumnSpan();
        data.isHeading = info.isHeading();
        data.isSelected = info.isSelected();
        return data;
    }

    private static AccessibilityRangeInfoData toMojoRangeInfo(
            AccessibilityNodeInfo.RangeInfo info) {
        if (info == null) {
            return null;
        }
        AccessibilityRangeInfoData data = new AccessibilityRangeInfoData();
        data.min = info.getMin();
        data.max = info.getMax();
        data.current = info.getCurrent();
        switch (info.getType()) {
            case AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_INT:
                data.rangeType = AccessibilityRangeType.INT;
                break;
            case AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_PERCENT:
                data.rangeType = AccessibilityRangeType.PERCENT;
                break;
            default:
                data.rangeType = AccessibilityRangeType.FLOAT;
                break;
        }
        return data;
    }

    private static int toMojoLiveRegion(int androidLiveRegion) {
        switch (androidLiveRegion) {
            case android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE:
                return AccessibilityLiveRegionType.POLITE;
            case android.view.View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE:
                return AccessibilityLiveRegionType.ASSERTIVE;
            default:
                return AccessibilityLiveRegionType.NONE;
        }
    }

    // --- Action id table ---

    /** Maps a Mojo {@code AccessibilityActionType} back to an Android action id. */
    static int toAndroidAction(int mojoActionType) {
        for (Map.Entry<Integer, Integer> entry : ACTION_TO_MOJO.entrySet()) {
            if (entry.getValue().intValue() == mojoActionType) {
                return entry.getKey().intValue();
            }
        }
        return 0;
    }

    private static Map<Integer, Integer> buildActionTable() {
        Map<Integer, Integer> map = new HashMap<>();
        // Action ids of the R.id.* form are resolved through AccessibilityAction
        // rather than hardcoded, so they track the platform.
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_FOCUS,
                org.chromium.ax.android.mojom.AccessibilityActionType.FOCUS);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_FOCUS,
                org.chromium.ax.android.mojom.AccessibilityActionType.CLEAR_FOCUS);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SELECT,
                org.chromium.ax.android.mojom.AccessibilityActionType.SELECT);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_SELECTION,
                org.chromium.ax.android.mojom.AccessibilityActionType.CLEAR_SELECTION);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK,
                org.chromium.ax.android.mojom.AccessibilityActionType.CLICK);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK,
                org.chromium.ax.android.mojom.AccessibilityActionType.LONG_CLICK);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS,
                org.chromium.ax.android.mojom.AccessibilityActionType.ACCESSIBILITY_FOCUS);
        addAction(map,
                AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS,
                org.chromium.ax.android.mojom.AccessibilityActionType
                        .CLEAR_ACCESSIBILITY_FOCUS);
        addAction(map,
                AccessibilityNodeInfo.AccessibilityAction
                        .ACTION_NEXT_AT_MOVEMENT_GRANULARITY,
                org.chromium.ax.android.mojom.AccessibilityActionType
                        .NEXT_AT_MOVEMENT_GRANULARITY);
        addAction(map,
                AccessibilityNodeInfo.AccessibilityAction
                        .ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                org.chromium.ax.android.mojom.AccessibilityActionType
                        .PREVIOUS_AT_MOVEMENT_GRANULARITY);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_NEXT_HTML_ELEMENT,
                org.chromium.ax.android.mojom.AccessibilityActionType.NEXT_HTML_ELEMENT);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_PREVIOUS_HTML_ELEMENT,
                org.chromium.ax.android.mojom.AccessibilityActionType.PREVIOUS_HTML_ELEMENT);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD,
                org.chromium.ax.android.mojom.AccessibilityActionType.SCROLL_FORWARD);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD,
                org.chromium.ax.android.mojom.AccessibilityActionType.SCROLL_BACKWARD);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_COPY,
                org.chromium.ax.android.mojom.AccessibilityActionType.COPY);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_PASTE,
                org.chromium.ax.android.mojom.AccessibilityActionType.PASTE);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_CUT,
                org.chromium.ax.android.mojom.AccessibilityActionType.CUT);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_SELECTION,
                org.chromium.ax.android.mojom.AccessibilityActionType.SET_SELECTION);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND,
                org.chromium.ax.android.mojom.AccessibilityActionType.EXPAND);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE,
                org.chromium.ax.android.mojom.AccessibilityActionType.COLLAPSE);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS,
                org.chromium.ax.android.mojom.AccessibilityActionType.DISMISS);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT,
                org.chromium.ax.android.mojom.AccessibilityActionType.SET_TEXT);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN,
                org.chromium.ax.android.mojom.AccessibilityActionType.SHOW_ON_SCREEN);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION,
                org.chromium.ax.android.mojom.AccessibilityActionType.SCROLL_TO_POSITION);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP,
                org.chromium.ax.android.mojom.AccessibilityActionType.SCROLL_UP);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT,
                org.chromium.ax.android.mojom.AccessibilityActionType.SCROLL_LEFT);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN,
                org.chromium.ax.android.mojom.AccessibilityActionType.SCROLL_DOWN);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT,
                org.chromium.ax.android.mojom.AccessibilityActionType.SCROLL_RIGHT);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK,
                org.chromium.ax.android.mojom.AccessibilityActionType.CONTEXT_CLICK);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS,
                org.chromium.ax.android.mojom.AccessibilityActionType.SET_PROGRESS);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_TOOLTIP,
                org.chromium.ax.android.mojom.AccessibilityActionType.SHOW_TOOLTIP);
        addAction(map, AccessibilityNodeInfo.AccessibilityAction.ACTION_HIDE_TOOLTIP,
                org.chromium.ax.android.mojom.AccessibilityActionType.HIDE_TOOLTIP);
        return map;
    }

    private static void addAction(Map<Integer, Integer> map,
            AccessibilityNodeInfo.AccessibilityAction action, int mojoType) {
        map.put(Integer.valueOf(action.getId()), Integer.valueOf(mojoType));
    }

    // --- Small helpers ---

    /**
     * Converts a rect, collapsing inverted edges.
     *
     * <p>Android hands out non-normalized bounds for views clipped fully out of
     * a scrolling parent, and Chrome's Rect mojom traits reject any rect whose
     * right is left of its left (or bottom above its top) with
     * VALIDATION_ERROR_DESERIALIZATION_FAILED — which drops the entire event,
     * not just the node. Collapse such a rect to zero area at its own origin,
     * which is what an off-screen node means anyway.
     */
    static org.chromium.ax.android.mojom.Rect toMojoRect(Rect rect) {
        org.chromium.ax.android.mojom.Rect out = new org.chromium.ax.android.mojom.Rect();
        out.left = rect.left;
        out.top = rect.top;
        out.right = Math.max(rect.left, rect.right);
        out.bottom = Math.max(rect.top, rect.bottom);
        return out;
    }

    private static void putIfPresent(Map<Integer, String> map, int key, CharSequence value) {
        if (value != null) {
            map.put(Integer.valueOf(key), value.toString());
        }
    }

    private void putNodeRef(Map<Integer, Integer> map, int key, AccessibilityNodeInfo node) {
        if (node != null) {
            map.put(Integer.valueOf(key),
                    Integer.valueOf(mIds.idFor(node.getWindowId(), node.getSourceNodeId())));
        }
    }

    private static int[] toIntArray(List<Integer> values) {
        int[] out = new int[values.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = values.get(i).intValue();
        }
        return out;
    }
}

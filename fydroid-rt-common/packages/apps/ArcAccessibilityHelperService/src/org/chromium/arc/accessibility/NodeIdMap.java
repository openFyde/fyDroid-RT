package org.chromium.arc.accessibility;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Allocates stable int32 identifiers for Android accessibility nodes.
 *
 * <p>Chrome's AXTreeSourceAndroid keeps windows and nodes in a single id space
 * (see {@code BuildNodeMap} in {@code ax_tree_source_android.cc}): window ids
 * come straight from {@code AccessibilityWindowInfo#getId()} and node ids come
 * from {@code AccessibilityNodeInfoData#id}. Two requirements follow:
 *
 * <ul>
 *   <li>node ids must never collide with an Android window id, so they are
 *       allocated from {@link #NODE_ID_BASE} upwards;
 *   <li>node ids must be stable across events, otherwise Chrome's AX tree
 *       serializer re-sends the whole tree and ChromeVox loses its focus every
 *       time an event arrives.
 * </ul>
 *
 * <p>An Android node is identified by its source node id — a long packing the
 * accessibility view id with the virtual descendant id. That value is only
 * unique within a window (each app process numbers its own views), so the map
 * is keyed by window id first.
 *
 * <p>Not thread safe; all access happens on the accessibility service's main
 * thread.
 */
class NodeIdMap {
    /**
     * First id handed out for a node. Android window ids are small, densely
     * allocated counters, so anything above this bound cannot collide with one.
     */
    static final int NODE_ID_BASE = 0x40000000;

    /** Identifies a live Android node behind an allocated id. */
    static final class NodeRef {
        final int windowId;
        final long sourceNodeId;

        NodeRef(int windowId, long sourceNodeId) {
            this.windowId = windowId;
            this.sourceNodeId = sourceNodeId;
        }
    }

    private final SparseArray<HashMap<Long, Integer>> mByWindow = new SparseArray<>();
    private final SparseArray<NodeRef> mById = new SparseArray<>();
    private int mNextId = NODE_ID_BASE;

    /**
     * Returns the stable id for {@code sourceNodeId} in {@code windowId},
     * allocating one on first sight.
     */
    int idFor(int windowId, long sourceNodeId) {
        HashMap<Long, Integer> window = mByWindow.get(windowId);
        if (window == null) {
            window = new HashMap<>();
            mByWindow.put(windowId, window);
        }
        Integer existing = window.get(sourceNodeId);
        if (existing != null) {
            return existing.intValue();
        }
        int id = mNextId++;
        window.put(sourceNodeId, Integer.valueOf(id));
        mById.put(id, new NodeRef(windowId, sourceNodeId));
        return id;
    }

    /** Returns the Android node behind an allocated id, or null if unknown. */
    NodeRef refFor(int id) {
        return mById.get(id);
    }

    /**
     * Drops the ids of windows that no longer exist. Called after every tree
     * walk so the map tracks the live window set rather than growing forever.
     */
    void retainWindows(Set<Integer> liveWindowIds) {
        for (int i = mByWindow.size() - 1; i >= 0; i--) {
            int windowId = mByWindow.keyAt(i);
            if (liveWindowIds.contains(Integer.valueOf(windowId))) {
                continue;
            }
            for (Map.Entry<Long, Integer> entry : mByWindow.valueAt(i).entrySet()) {
                mById.remove(entry.getValue().intValue());
            }
            mByWindow.removeAt(i);
        }
    }

    /** Forgets every allocation, e.g. when the Mojo connection drops. */
    void clear() {
        mByWindow.clear();
        mById.clear();
        mNextId = NODE_ID_BASE;
    }
}

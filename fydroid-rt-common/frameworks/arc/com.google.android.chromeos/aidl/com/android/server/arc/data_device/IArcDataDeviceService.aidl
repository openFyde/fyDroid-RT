/* Open source replacement for com.android.server.arc.data_device.IArcDataDeviceService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.data_device;

/**
 * Android-side sink for Chrome OS data_device (drag-and-drop + clipboard)
 * events pushed from wayland_service. Receives pointer motion, drop,
 * clipboard updates, and raw FDs for payload transport.
 *
 * One-way: every proprietary Proxy method calls
 * transact(..., null, FLAG_ONEWAY), and the Stub never writes a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..9:
 *   1 onEnter
 *   2 onLeave
 *   3 onMotion
 *   4 onDrop
 *   5 onRequestDragData
 *   6 onFinishDrag
 *   7 onClipboardUpdate
 *   8 onRequestClipboardData
 *   9 onCancelDrag
 */
oneway interface IArcDataDeviceService {
    // 1
    void onEnter(int taskId, float x, float y, in String[] mimeTypes, int dataId);
    // 2
    void onLeave();
    // 3
    void onMotion(float x, float y);
    // 4
    void onDrop(in String[] mimeTypes, int id);
    // 5
    void onRequestDragData(String mimeType, in FileDescriptor fd);
    // 6
    void onFinishDrag(boolean consumed);
    // 7
    void onClipboardUpdate(in String[] mimeTypes, int id);
    // 8
    void onRequestClipboardData(String mimeType, in FileDescriptor fd);
    // 9
    void onCancelDrag();
}

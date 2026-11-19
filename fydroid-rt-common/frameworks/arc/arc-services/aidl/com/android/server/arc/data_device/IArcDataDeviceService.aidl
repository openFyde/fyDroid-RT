package com.android.server.arc.data_device;

/**
 * ARC data device binder interface.
 * Receives drag-and-drop and clipboard events from the Wayland service.
 * All methods are oneway (asynchronous).
 * Methods ordered to match proprietary transaction IDs exactly.
 */
oneway interface IArcDataDeviceService {
    // 1
    void onEnter(int taskId, float x, float y, in String[] mimeTypes, int offerId);
    // 2
    void onLeave();
    // 3
    void onMotion(float x, float y);
    // 4
    void onDrop(in String[] mimeTypes, int offerId);
    // 5
    void onRequestDragData(String mimeType, in FileDescriptor fd);
    // 6
    void onFinishDrag(boolean success);
    // 7
    void onClipboardUpdate(in String[] mimeTypes, int offerId);
    // 8
    void onRequestClipboardData(String mimeType, in FileDescriptor fd);
    // 9
    void onCancelDrag();
}

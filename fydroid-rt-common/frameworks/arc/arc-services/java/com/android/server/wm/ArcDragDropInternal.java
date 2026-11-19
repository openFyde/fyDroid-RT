package com.android.server.wm;

import android.content.ClipData;

/**
 * Internal interface for ARC drag-and-drop integration.
 * Published via LocalServices by ArcDragDropService.
 * Consumed by ArcDataDeviceService to bridge drag events with Chrome OS.
 */
public interface ArcDragDropInternal {

    interface DataDevice {
        void dragRecipientEntered();
        void dragRecipientExited();
        void notifyDropResult(boolean consumed);
        boolean startDrag(int taskId);
    }

    ClipData getClipDataFromArc();
    void onCancelDrag();
    void onDrop(ClipData clipData);
    void onEnter(int taskId, float x, float y, ClipData clipData, int uid);
    void onFinishDrag(boolean success);
    void onLeave();
    void onMotion(float x, float y);
    void setDataDevice(DataDevice dataDevice);
}

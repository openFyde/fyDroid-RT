package com.android.server.clipboard;

import android.content.ClipData;

/**
 * Internal interface for ARC clipboard integration.
 * Published via LocalServices by ArcClipboard (in ClipboardService).
 * Consumed by ArcDataDeviceService to bridge clipboard events with Chrome OS.
 */
public interface ArcClipboardInternal {

    interface DataDevice {
        void setClipboard(ClipData clipData);
    }

    void onClipboardUpdate(ClipData clipData, int uid);
    void setDataDevice(DataDevice dataDevice);
}

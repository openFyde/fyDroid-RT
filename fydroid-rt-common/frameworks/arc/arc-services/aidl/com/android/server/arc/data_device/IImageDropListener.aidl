package com.android.server.arc.data_device;

/**
 * Callback for image drop events.
 * Registered by ArcInputMethodService to receive notifications when images are dropped.
 */
oneway interface IImageDropListener {
    // 1
    void onImageDrop(in String[] uris);
}

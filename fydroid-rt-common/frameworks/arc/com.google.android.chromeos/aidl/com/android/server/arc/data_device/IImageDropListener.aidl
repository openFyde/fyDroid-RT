/* Open source replacement for com.android.server.arc.data_device.IImageDropListener from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.data_device;

/**
 * Callback invoked when an image is dropped into an ARC window via
 * Chrome OS's data_device protocol.
 *
 * One-way: the proprietary Proxy calls transact(..., null, FLAG_ONEWAY).
 * Only one transaction code (1).
 */
oneway interface IImageDropListener {
    // 1
    void onImageDrop(in String[] uris);
}

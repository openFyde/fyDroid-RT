/* Open source replacement for com.android.server.arc.data_device.IArcDataDeviceProviderService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.data_device;

import android.os.SharedMemory;
import com.android.server.arc.data_device.IImageDropListener;

/**
 * Synchronous binder through which wayland_service / ArcSystemUI reads
 * content-provider data for clipboard / drag payloads, and subscribes
 * to image-drop notifications.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..6:
 *   1 getDataForContentProvider
 *   2 getMimeTypeForContentProvider
 *   3 isUriAccessedBy
 *   4 addImageDropListener
 *   5 removeImageDropListener
 *   6 notifyContentProviderAccess
 */
interface IArcDataDeviceProviderService {
    // 1
    SharedMemory getDataForContentProvider(String uri, String packageName);
    // 2
    String getMimeTypeForContentProvider(String uri);
    // 3
    boolean isUriAccessedBy(String uri, String packageName);
    // 4
    void addImageDropListener(IImageDropListener listener);
    // 5
    void removeImageDropListener(IImageDropListener listener);
    // 6
    void notifyContentProviderAccess(String uri, int packageUid);
}

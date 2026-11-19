package com.android.server.arc.data_device;

import android.os.SharedMemory;
import com.android.server.arc.data_device.IImageDropListener;

/**
 * ARC data device provider binder interface.
 * Provides content provider access for image clipboard/drag data.
 * Methods ordered to match proprietary transaction IDs exactly.
 */
interface IArcDataDeviceProviderService {
    // 1
    SharedMemory getDataForContentProvider(String uri, String callingPackage);
    // 2
    String getMimeTypeForContentProvider(String uri);
    // 3
    boolean isUriAccessedBy(String uri, String packageName);
    // 4
    void addImageDropListener(IImageDropListener listener);
    // 5
    void removeImageDropListener(IImageDropListener listener);
    // 6
    void notifyContentProviderAccess(String uri, int uid);
}

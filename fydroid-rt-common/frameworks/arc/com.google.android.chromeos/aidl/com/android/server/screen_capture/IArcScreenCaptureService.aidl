package com.android.server.screen_capture;

import com.android.server.screen_capture.IArcScreenCaptureServiceCallback;
import com.android.server.screen_capture.IArcScreenCapturePermissionCallback;

/**
 * Open source replacement for the proprietary
 * com.android.server.screen_capture.IArcScreenCaptureService binder
 * interface from the ARC++ com.google.android.chromeos jar.
 *
 * The interface is mixed: requestPermission, testModeAcceptPermission and
 * openSession are synchronous (transact with a reply parcel, flags=0) while
 * setOutputBuffer and closeSession are one-way (transact with null reply,
 * FLAG_ONEWAY). The individual methods are therefore marked oneway where
 * appropriate to preserve the proprietary wire format exactly.
 *
 * Method declaration order matches the proprietary transaction codes
 * (1..5) one-for-one:
 *   1 requestPermission
 *   2 testModeAcceptPermission
 *   3 openSession
 *   4 setOutputBuffer
 *   5 closeSession
 *
 * The raw FileDescriptor parameter of setOutputBuffer uses AIDL's native
 * FileDescriptor type, which serialises with writeRawFileDescriptor /
 * readRawFileDescriptor — matching the proprietary class byte-for-byte.
 */
interface IArcScreenCaptureService {
    // 1
    void requestPermission(String displayName, String packageName,
            IArcScreenCapturePermissionCallback callback);
    // 2
    void testModeAcceptPermission(String packageName);
    // 3
    int openSession(IArcScreenCaptureServiceCallback sessionCallback,
            String packageName, int width, int height);
    // 4
    oneway void setOutputBuffer(int sessionId, in FileDescriptor graphicsBuffer,
            int bufferFormat, long bufferFormatModifier, int stride);
    // 5
    oneway void closeSession(int sessionId);
}

package org.chromium.arc.bridge;

import android.util.Log;
import org.chromium.mojo.bindings.ExceptionHandler;

/**
 * Exception handler for Mojo binding errors in ARC services.
 * Logs errors as WTF (What a Terrible Failure) and returns true
 * to indicate the exception was handled.
 */
public class ArcExceptionHandler implements ExceptionHandler {
    private static final String TAG = "org.chromium.arc.bridge";

    private static class LazyHolder {
        static final ArcExceptionHandler INSTANCE = new ArcExceptionHandler();
    }

    private ArcExceptionHandler() {
    }

    public static ArcExceptionHandler getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public boolean handleException(RuntimeException e) {
        Log.wtf(TAG, "Unhandled exception", e);
        return true;
    }
}

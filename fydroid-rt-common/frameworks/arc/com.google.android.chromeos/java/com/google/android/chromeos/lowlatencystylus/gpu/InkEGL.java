/* Open source replacement for com.google.android.chromeos.lowlatencystylus.gpu.InkEGL
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.gpu;

import android.opengl.EGL14;
import android.opengl.EGLContext;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Package-private EGL helpers for single-buffered GL surfaces.
 */
class InkEGL {
    static final int EGL_FRONT_BUFFER_AUTO_REFRESH_ANDROID = 12620;
    static final int EGL_MUTABLE_RENDER_BUFFER_BIT_KHR = 4096;

    InkEGL() {
    }

    static int eglGetConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute) {
        int[] value = new int[1];
        if (!egl.eglGetConfigAttrib(display, config, attribute, value)) {
            throw new RuntimeException(String.format(
                    "eglGetConfigAttrib(attribute=0x%X) failed: EGL error 0x%X",
                    Integer.valueOf(attribute), Integer.valueOf(egl.eglGetError())));
        }
        return value[0];
    }

    static int eglQueryContext(android.opengl.EGLDisplay display, EGLContext context,
            int attribute) {
        int[] value = new int[1];
        if (!EGL14.eglQueryContext(display, context, attribute, value, 0)) {
            throw new RuntimeException(String.format(
                    "eglQueryContext(attribute=0x%X) failed: EGL error 0x%X",
                    Integer.valueOf(attribute), Integer.valueOf(EGL14.eglGetError())));
        }
        return value[0];
    }
}

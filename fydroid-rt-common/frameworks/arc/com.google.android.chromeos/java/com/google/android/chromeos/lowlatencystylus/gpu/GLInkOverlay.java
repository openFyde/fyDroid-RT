/* Open source replacement for com.google.android.chromeos.lowlatencystylus.gpu.GLInkOverlay
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.gpu;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.android.internal.util.Preconditions;
import com.google.android.chromeos.lowlatencystylus.LowLatencyInkUtils;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Hooks a {@link GLSurfaceView} for single-buffered (mutable render buffer)
 * rendering backed by a {@link GLInkRenderer}.
 */
public class GLInkOverlay {
    private GLSurfaceView mGLSurfaceView;
    private GLInkRenderer mRenderer;

    private class MutableRenderBufferConfigChooser implements GLSurfaceView.EGLConfigChooser {
        private MutableRenderBufferConfigChooser() {}

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            EGLConfig config = tryChooseMutableBufferConfig(egl, display);
            if (config != null) {
                Log.d("EGLConfig", "Mutable Render Buffer Config chosen.");
            }
            if (config == null) {
                config = fallbackConfigChooser(egl, display);
            }
            if (config == null) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            return config;
        }

        public EGLConfig tryChooseMutableBufferConfig(EGL10 egl, EGLDisplay display) {
            int[] configSpec = {
                    12320, 24,  // EGL_BUFFER_SIZE
                    12324, 8,   // EGL_RED_SIZE
                    12323, 8,   // EGL_GREEN_SIZE
                    12322, 8,   // EGL_BLUE_SIZE
                    12321, 0,   // EGL_ALPHA_SIZE
                    12325, 0,   // EGL_DEPTH_SIZE
                    12326, 0,   // EGL_STENCIL_SIZE
                    12352, 4,   // EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT
                    12339, 4100, // EGL_SURFACE_TYPE, WINDOW|MUTABLE_RENDER_BUFFER
                    12344       // EGL_NONE
            };
            return chooseConfigBySpec(egl, display, configSpec);
        }

        public EGLConfig fallbackConfigChooser(EGL10 egl, EGLDisplay display) {
            int[] configSpec = {
                    12320, 24,
                    12324, 8,
                    12323, 8,
                    12322, 8,
                    12321, 0,
                    12325, 0,
                    12326, 0,
                    12352, 4,
                    12344
            };
            return chooseConfigBySpec(egl, display, configSpec);
        }

        public EGLConfig chooseConfigBySpec(EGL10 egl, EGLDisplay display, int[] configSpec) {
            int[] numConfig = new int[1];
            if (!egl.eglChooseConfig(display, configSpec, null, 0, numConfig)) {
                return null;
            }
            int numConfigs = numConfig[0];
            if (numConfigs <= 0) {
                return null;
            }
            EGLConfig[] configs = new EGLConfig[numConfigs];
            if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, numConfig)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            return configs[0];
        }
    }

    private class SingleBufferWindowSurfaceFactory
            implements GLSurfaceView.EGLWindowSurfaceFactory {
        private SingleBufferWindowSurfaceFactory() {}

        @Override
        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config,
                Object nativeWindow) {
            int[] attribs = {12422, 12421, 12344};
            return egl.eglCreateWindowSurface(display, config, nativeWindow, attribs);
        }

        @Override
        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }

    public GLInkOverlay(GLSurfaceView surfaceView) {
        Preconditions.checkNotNull(surfaceView, "GLSurfaceView cannot be null");
        this.mGLSurfaceView = surfaceView;
        surfaceView.setEGLContextClientVersion(2);
        this.mGLSurfaceView.setEGLConfigChooser(new MutableRenderBufferConfigChooser());
        this.mGLSurfaceView.setEGLWindowSurfaceFactory(new SingleBufferWindowSurfaceFactory());
    }

    public void setRenderer(GLSurfaceView.Renderer renderer) {
        if (!(renderer instanceof GLInkRenderer)) {
            throw new UnsupportedOperationException("Please use GLInkRenderer");
        }
        LowLatencyInkUtils.reportApiUsage(2);
        this.mRenderer = (GLInkRenderer) renderer;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        int rotation = this.mGLSurfaceView.getDisplay().getRotation();
        if (rotation == 2) {
            rotation = 0;
        }
        if (rotation == 3) {
            rotation = 1;
        }
        final int finalRotation = rotation;
        LowLatencyInkUtils.setBufferRotation(this.mGLSurfaceView, finalRotation, w, h);
        this.mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.rotate(finalRotation);
            }
        });
    }

    public void setPredictionTargetMs(int predictionTargetMillis) {
        this.mRenderer.setPredictionTargetMs(predictionTargetMillis);
    }

    public int getPredictionTargetMs() {
        return this.mRenderer.getPredictionTargetMs();
    }

    public void onTouch(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            this.mGLSurfaceView.requestUnbufferedDispatch(event);
        }
        final MotionEvent motionEvent = MotionEvent.obtain(event);
        this.mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.processTouchEvent(motionEvent);
                motionEvent.recycle();
            }
        });
        this.mGLSurfaceView.requestRender();
    }
}

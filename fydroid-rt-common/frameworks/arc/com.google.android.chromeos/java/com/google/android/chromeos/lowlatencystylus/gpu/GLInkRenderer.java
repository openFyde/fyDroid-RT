/* Open source replacement for com.google.android.chromeos.lowlatencystylus.gpu.GLInkRenderer
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.gpu;

import android.graphics.Rect;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.google.android.chromeos.lowlatencystylus.LowLatencyInkUtils;
import com.google.android.chromeos.lowlatencystylus.predictor.InkPredictor;
import com.google.android.chromeos.lowlatencystylus.predictor.MultiPointerPredictor;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Abstract {@link GLSurfaceView.Renderer} base class for low-latency GL
 * ink rendering. Subclasses implement {@link #beforeDraw}, {@link #onDraw}
 * and {@link #clear} for the application-specific drawing logic.
 */
public abstract class GLInkRenderer implements GLSurfaceView.Renderer {
    private static final int DEFAULT_PREDICTION_REPORT_RATE_MS = 4;
    private static final int DEFAULT_PREDICTION_TARGET_MS = 25;

    private InkFrameBufferRenderer mFBRenderer;
    private final InkPredictor mPointerPredictor;
    private float[] mProjectionMatrix;
    private float[] mViewMatrix;
    private boolean mSharedBufferModeAvailable = false;
    private int mPreviousPredictionTarget = -1;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mRotation = 0;

    public abstract Rect beforeDraw(GL10 gl10, MotionEvent motionEvent);

    public abstract void clear();

    public abstract void onDraw(GL10 gl10, MotionEvent motionEvent);

    public GLInkRenderer() {
        MultiPointerPredictor multiPointerPredictor = new MultiPointerPredictor();
        this.mPointerPredictor = multiPointerPredictor;
        multiPointerPredictor.setPredictionTarget(DEFAULT_PREDICTION_TARGET_MS);
        this.mPointerPredictor.setReportRate(DEFAULT_PREDICTION_REPORT_RATE_MS);
        initMatrices();
    }

    public void setViewMatrix(float[] viewMatrix) {
        LowLatencyInkUtils.reportApiUsage(4);
        this.mViewMatrix = viewMatrix;
    }

    public float[] getViewMatrix() {
        return this.mViewMatrix;
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        LowLatencyInkUtils.reportApiUsage(3);
        this.mProjectionMatrix = projectionMatrix;
    }

    public float[] getProjectionMatrix() {
        return this.mProjectionMatrix;
    }

    public void enableSingleBuffer() {
        if (this.mSharedBufferModeAvailable) {
            EGLDisplay currentDisplay = EGL14.eglGetCurrentDisplay();
            EGLSurface currentSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
            // 12422 = EGL_RENDER_BUFFER, 12421 = EGL_SINGLE_BUFFER
            EGL14.eglSurfaceAttrib(currentDisplay, currentSurface, 12422, 12421);
            // 12620 = EGL_FRONT_BUFFER_AUTO_REFRESH_ANDROID
            EGL14.eglSurfaceAttrib(currentDisplay, currentSurface,
                    InkEGL.EGL_FRONT_BUFFER_AUTO_REFRESH_ANDROID, 1);
        }
    }

    public void disableSingleBuffer() {
        if (this.mSharedBufferModeAvailable) {
            EGLDisplay currentDisplay = EGL14.eglGetCurrentDisplay();
            EGLSurface currentSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
            // 12422 = EGL_RENDER_BUFFER, 12420 = EGL_BACK_BUFFER
            EGL14.eglSurfaceAttrib(currentDisplay, currentSurface, 12422, 12420);
            EGL14.eglSurfaceAttrib(currentDisplay, currentSurface,
                    InkEGL.EGL_FRONT_BUFFER_AUTO_REFRESH_ANDROID, 0);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        EGLDisplay display = EGL14.eglGetCurrentDisplay();
        EGLContext context = EGL14.eglGetCurrentContext();
        int renderBuffer = InkEGL.eglQueryContext(display, context,
                /* EGL_RENDER_BUFFER */ 12422);
        if (renderBuffer == /* EGL_SINGLE_BUFFER */ 12421) {
            drawFrameSingleBufferMode(gl);
        } else {
            drawFrameDoubleBufferMode(gl);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        EGL10 egl = (EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
        int surfaceType = InkEGL.eglGetConfigAttrib(egl, egl.eglGetCurrentDisplay(), config,
                /* EGL_SURFACE_TYPE */ 12339);
        this.mSharedBufferModeAvailable =
                (surfaceType & InkEGL.EGL_MUTABLE_RENDER_BUFFER_BIT_KHR) != 0;
        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        if (this.mWidth != width || this.mHeight != height) {
            clear();
        }
        this.mWidth = width;
        this.mHeight = height;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        updateMatrixAndFBO();
        enableSingleBuffer();
        onDraw(unused, null);
    }

    final void setPredictionTargetMs(int predictionTargetMs) {
        this.mPointerPredictor.setPredictionTarget(predictionTargetMs);
    }

    final int getPredictionTargetMs() {
        return this.mPointerPredictor.getPredictionTarget();
    }

    private MotionEvent predict() {
        int currentPredictionTarget = this.mPointerPredictor.getPredictionTarget();
        if (currentPredictionTarget != this.mPreviousPredictionTarget) {
            LowLatencyInkUtils.reportPredictionTargetUsage(2, currentPredictionTarget);
            this.mPreviousPredictionTarget = currentPredictionTarget;
        }
        if (currentPredictionTarget <= 0) {
            return null;
        }
        MotionEvent predictedMotionEvents = this.mPointerPredictor.predict();
        if (predictedMotionEvents == null) {
            return null;
        }
        return predictedMotionEvents;
    }

    private void drawFrameSingleBufferMode(GL10 gl) {
        MotionEvent predictMotionEvent = predict();
        this.mFBRenderer.useFrameBuffer(true);
        Rect scissorBox = beforeDraw(gl, predictMotionEvent);
        applyScissor(scissorBox);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        onDraw(gl, predictMotionEvent);
        this.mFBRenderer.useFrameBuffer(false);
        applyScissor(scissorBox);
        this.mFBRenderer.draw();
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        GLES20.glFlush();
        if (predictMotionEvent != null) {
            predictMotionEvent.recycle();
        }
    }

    private static void applyScissor(Rect scissorBox) {
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(scissorBox.left, scissorBox.top,
                scissorBox.right - scissorBox.left,
                scissorBox.bottom - scissorBox.top);
    }

    private void drawFrameDoubleBufferMode(GL10 gl) {
        MotionEvent predictMotionEvent = predict();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        onDraw(gl, predictMotionEvent);
        GLES20.glFlush();
    }

    private void updateMatrixAndFBO() {
        if (this.mRotation == 0) {
            this.mFBRenderer = new InkFrameBufferRenderer(this.mWidth, this.mHeight);
            Matrix.orthoM(this.mProjectionMatrix, 0, 0.0f, this.mWidth, 0.0f, this.mHeight,
                    -1.0f, 1.0f);
            Matrix.setIdentityM(this.mViewMatrix, 0);
            Matrix.translateM(this.mViewMatrix, 0, 0.0f, this.mHeight, 0.0f);
            Matrix.rotateM(this.mViewMatrix, 0, 180.0f, 1.0f, 0.0f, 0.0f);
            return;
        }
        if (this.mRotation == 1) {
            this.mFBRenderer = new InkFrameBufferRenderer(this.mHeight, this.mWidth);
            Matrix.orthoM(this.mProjectionMatrix, 0, 0.0f, this.mHeight, 0.0f, this.mWidth,
                    -1.0f, 1.0f);
            Matrix.setIdentityM(this.mViewMatrix, 0);
            Matrix.translateM(this.mViewMatrix, 0, this.mHeight, this.mWidth, 0.0f);
            Matrix.rotateM(this.mViewMatrix, 0, 180.0f, 0.0f, 1.0f, 0.0f);
            Matrix.rotateM(this.mViewMatrix, 0, -90.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    void rotate(int rotation) {
        if (this.mRotation != rotation) {
            clear();
        }
        this.mRotation = rotation;
    }

    final void processTouchEvent(MotionEvent event) {
        if (this.mPointerPredictor.getPredictionTarget() > 0) {
            this.mPointerPredictor.onTouchEvent(event);
        }
    }

    private void initMatrices() {
        this.mViewMatrix = new float[16];
        Matrix.setIdentityM(this.mViewMatrix, 0);
        this.mProjectionMatrix = new float[16];
        Matrix.setIdentityM(this.mProjectionMatrix, 0);
    }
}

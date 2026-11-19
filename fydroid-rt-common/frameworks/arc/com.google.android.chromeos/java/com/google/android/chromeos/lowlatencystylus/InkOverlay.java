/* Open source replacement for com.google.android.chromeos.lowlatencystylus.InkOverlay
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.internal.util.Preconditions;
import com.google.android.chromeos.lowlatencystylus.predictor.InkPredictor;
import com.google.android.chromeos.lowlatencystylus.predictor.MultiPointerPredictor;

import java.util.Locale;

/**
 * Low-latency ink overlay that routes draws from an {@link InkDelegate} to a
 * shared-buffer-mode {@link Surface} backed by a {@link SurfaceView}.
 */
public class InkOverlay {
    private static final String TAG = "InkOverlay";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LATENCY = false;
    private static final int DEFAULT_PREDICTION_REPORT_RATE_MS = 4;
    private static final int DEFAULT_PREDICTION_TARGET_MS = 25;

    private Bitmap mBackBufferBitmap;
    private Canvas mBackBufferCanvas;
    private Bitmap mCommittedBitmap;
    private Canvas mCommittedCanvas;
    private Paint mClearPaint;
    private Paint mImageBlitPaint;
    private InkDelegate mDelegate;
    private final Handler mHandler;
    private boolean mHasRotationSupport;
    private LatencyStats mLatencyStats;
    private InkPredictor mPointerPredictor;
    private SurfaceView mSurfaceView;
    private boolean mNeedsClear = true;
    private Rect mOverallDamageRect = new Rect();
    private Rect mPrevPredictionDamageRect = new Rect();
    private boolean mIsDebugPredictionEnabled = false;
    private int mPreviousPredictionTarget = -1;
    private int mDoRenderCounter = 0;
    private int mDisplayRotation = 0;
    private int mWidth = 0;
    private int mHeight = 0;

    private static class LatencyStats {
        long mCompleteRenderMs;
        long mLastInputEventMs;
        long mPostCommitNs;
        long mPostLockNs;
        long mPostRenderNs;
        long mPostUnlockNs;
        long mRenderScheduleTimeNs;
        long mStartRenderNs;

        private LatencyStats() {}

        void calculateAndReportLatencyStats() {
            long eventToPostMs = this.mCompleteRenderMs - this.mLastInputEventMs;
            float scheduleTimeMs = nanoToMillis(this.mStartRenderNs - this.mRenderScheduleTimeNs);
            float renderTimeMs = nanoToMillis(this.mPostRenderNs - this.mStartRenderNs);
            float lockTimeMs = nanoToMillis(this.mPostLockNs - this.mPostRenderNs);
            float commitTimeMs = nanoToMillis(this.mPostCommitNs - this.mPostLockNs);
            float unlockTimeMs = nanoToMillis(this.mPostUnlockNs - this.mPostCommitNs);
            long appTimeMs = (long) (scheduleTimeMs + renderTimeMs + lockTimeMs + commitTimeMs
                    + unlockTimeMs);
            Log.v(TAG, String.format(Locale.ROOT,
                    "Latency: %2dms (app: %dms) (schedule: %.2fms,  renderBackBuffer: %.2fms,  lock: %.2fms,  commit: %.2fms,  unlock: %.2fms)",
                    Long.valueOf(eventToPostMs), Long.valueOf(appTimeMs),
                    Float.valueOf(scheduleTimeMs), Float.valueOf(renderTimeMs),
                    Float.valueOf(lockTimeMs), Float.valueOf(commitTimeMs),
                    Float.valueOf(unlockTimeMs)));
        }

        private static float nanoToMillis(long nanoTime) {
            return nanoTime / 1000000.0f;
        }
    }

    public InkOverlay(SurfaceView surfaceView) {
        Preconditions.checkNotNull(surfaceView, "surfaceView cannot be null");
        this.mSurfaceView = surfaceView;
        this.mClearPaint = new Paint();
        this.mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        this.mClearPaint.setColor(0);
        this.mImageBlitPaint = new Paint();
        this.mImageBlitPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        this.mHandler = new Handler(Looper.myLooper());
        this.mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {}

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Surface surface = holder.getSurface();
                if (surface == null) {
                    return;
                }
                InkOverlay.this.mDoRenderCounter = 0;
                InkOverlay.this.mDisplayRotation = 0;
                LowLatencyInkUtils.setSharedBufferMode(surface, false);
                InkOverlay.this.mBackBufferBitmap =
                        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                InkOverlay.this.mBackBufferCanvas = new Canvas(InkOverlay.this.mBackBufferBitmap);
                InkOverlay.this.mBackBufferCanvas.drawColor(0, PorterDuff.Mode.SRC);
                InkOverlay.this.mCommittedBitmap =
                        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                InkOverlay.this.mCommittedCanvas = new Canvas(InkOverlay.this.mCommittedBitmap);
                InkOverlay.this.mCommittedCanvas.drawColor(0, PorterDuff.Mode.SRC);
                InkOverlay.this.mWidth = width;
                InkOverlay.this.mHeight = height;
                InkOverlay.this.scheduleRender();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });
        this.mSurfaceView.getHolder().setFormat(-3);
        this.mSurfaceView.setZOrderOnTop(true);
        this.mHasRotationSupport = LowLatencyInkUtils.hasRotationSupport();
        MultiPointerPredictor multiPointerPredictor = new MultiPointerPredictor();
        this.mPointerPredictor = multiPointerPredictor;
        multiPointerPredictor.setPredictionTarget(DEFAULT_PREDICTION_TARGET_MS);
        this.mPointerPredictor.setReportRate(DEFAULT_PREDICTION_REPORT_RATE_MS);
    }

    public void setInkDelegate(InkDelegate inkDelegate) {
        this.mDelegate = inkDelegate;
    }

    public void setPredictionTarget(int predictionTargetMillis) {
        this.mPointerPredictor.setPredictionTarget(predictionTargetMillis);
    }

    public int getPredictionTargetMs() {
        return this.mPointerPredictor.getPredictionTarget();
    }

    public void enableDebugPrediction(boolean enabled) {
        this.mIsDebugPredictionEnabled = enabled;
    }

    public void onTouch(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            this.mSurfaceView.requestUnbufferedDispatch(event);
        } else if (action == MotionEvent.ACTION_CANCEL) {
            this.mNeedsClear = true;
        }
        if (this.mPointerPredictor.getPredictionTarget() > 0) {
            this.mPointerPredictor.onTouchEvent(event);
        }
        InkDelegate inkDelegate = this.mDelegate;
        if (inkDelegate != null) {
            inkDelegate.onTouch(event);
            scheduleRender();
        }
    }

    private void scheduleRender() {
        if (!this.mHandler.hasMessages(0)) {
            Message m = Message.obtain(this.mHandler, new Runnable() {
                @Override
                public void run() {
                    doRender();
                }
            });
            m.what = 0;
            this.mHandler.sendMessage(m);
        }
    }

    private void doRender() {
        Surface surface = this.mSurfaceView.getHolder().getSurface();
        if (surface == null || !surface.isValid()) {
            return;
        }
        Rect renderedRect = renderBackBuffer();
        this.mDoRenderCounter++;
        if (this.mHasRotationSupport) {
            if (this.mDoRenderCounter == 2) {
                int displayRotation = LowLatencyInkUtils.getDisplayRotation(this.mSurfaceView);
                this.mDisplayRotation = displayRotation;
                LowLatencyInkUtils.setBufferRotation(this.mSurfaceView, displayRotation,
                        this.mWidth, this.mHeight);
                LowLatencyInkUtils.setSharedBufferMode(surface, true);
                LowLatencyInkUtils.reportApiUsage(1);
            }
        } else {
            LowLatencyInkUtils.setSharedBufferMode(surface, true);
        }
        Rect rotatedRect = rotateRect(renderedRect, this.mDisplayRotation,
                this.mSurfaceView.getWidth(), this.mSurfaceView.getHeight());
        Canvas canvas = surface.lockCanvas(rotatedRect);
        canvas.setMatrix(getRotationMatrix(this.mDisplayRotation,
                this.mSurfaceView.getWidth(), this.mSurfaceView.getHeight()));
        canvas.drawBitmap(this.mBackBufferBitmap, renderedRect, renderedRect, this.mImageBlitPaint);
        surface.unlockCanvasAndPost(canvas);
    }

    public void clear() {
        hide();
        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNeedsClear = true;
                scheduleRender();
                show();
            }
        }, 100L);
    }

    private void show() {
        this.mSurfaceView.setTranslationX(0.0f);
        this.mSurfaceView.setTranslationY(0.0f);
        this.mSurfaceView.requestLayout();
    }

    private void hide() {
        int[] locationOnScreen = new int[2];
        Point displaySize = new Point();
        this.mSurfaceView.getDisplay().getSize(displaySize);
        this.mSurfaceView.getLocationOnScreen(locationOnScreen);
        this.mSurfaceView.setTranslationX((displaySize.x - locationOnScreen[0]) - 1);
        this.mSurfaceView.setTranslationY((displaySize.y - locationOnScreen[1]) - 1);
    }

    private Rect renderBackBuffer() {
        if (this.mBackBufferCanvas == null) {
            throw new IllegalStateException("initialize has to be called before rendering.");
        }
        Rect damageRect = new Rect();
        if (this.mNeedsClear) {
            this.mNeedsClear = false;
            this.mCommittedCanvas.drawRect(this.mOverallDamageRect, this.mClearPaint);
            damageRect.union(this.mOverallDamageRect);
            damageRect.union(this.mPrevPredictionDamageRect);
            this.mPrevPredictionDamageRect.setEmpty();
            this.mOverallDamageRect.setEmpty();
            this.mBackBufferCanvas.drawRect(damageRect, this.mClearPaint);
            return damageRect;
        }
        InkDelegate inkDelegate = this.mDelegate;
        if (inkDelegate == null) {
            return damageRect;
        }
        Rect inkDamageRect = inkDelegate.onDraw(this.mCommittedCanvas);
        damageRect.union(inkDamageRect);
        this.mOverallDamageRect.union(inkDamageRect);
        damageRect.union(this.mPrevPredictionDamageRect);
        this.mPrevPredictionDamageRect.setEmpty();
        this.mBackBufferCanvas.drawBitmap(this.mCommittedBitmap, damageRect, damageRect,
                this.mImageBlitPaint);
        int currentPredictionTarget = this.mPointerPredictor.getPredictionTarget();
        if (currentPredictionTarget != this.mPreviousPredictionTarget) {
            LowLatencyInkUtils.reportPredictionTargetUsage(1, currentPredictionTarget);
            this.mPreviousPredictionTarget = currentPredictionTarget;
        }
        if (currentPredictionTarget > 0) {
            MotionEvent predictedEv = this.mPointerPredictor.predict();
            if (predictedEv != null) {
                this.mPrevPredictionDamageRect =
                        this.mDelegate.onDrawPrediction(this.mBackBufferCanvas, predictedEv);
                if (this.mIsDebugPredictionEnabled) {
                    Rect committedPredictionRect =
                            this.mDelegate.onDrawPrediction(this.mCommittedCanvas, predictedEv);
                    if (committedPredictionRect != null && !committedPredictionRect.isEmpty()) {
                        this.mOverallDamageRect.union(committedPredictionRect);
                    }
                }
                predictedEv.recycle();
                if (this.mPrevPredictionDamageRect != null
                        && !this.mPrevPredictionDamageRect.isEmpty()) {
                    damageRect.union(this.mPrevPredictionDamageRect);
                }
            }
        }
        return damageRect;
    }

    private Matrix getRotationMatrix(int displayRotation, int viewWidth, int viewHeight) {
        Matrix m = new Matrix();
        if (displayRotation == 1) {
            m.setRotate(90.0f);
            m.postTranslate(viewHeight, 0.0f);
        } else if (displayRotation == 2) {
            m.setRotate(180.0f);
            m.postTranslate(viewWidth, viewHeight);
        } else if (displayRotation == 3) {
            m.setRotate(270.0f);
            m.postTranslate(0.0f, viewWidth);
        } else if (displayRotation != 0) {
            Log.e(TAG, "Skipping getRotationMatrix. Unknown display rotation: " + displayRotation);
        }
        return m;
    }

    private Rect rotateRect(Rect rect, int displayRotation, int viewWidth, int viewHeight) {
        Matrix m = getRotationMatrix(displayRotation, viewWidth, viewHeight);
        RectF rotatedRectF = new RectF(rect);
        m.mapRect(rotatedRectF);
        Rect rotatedRect = new Rect();
        rotatedRectF.roundOut(rotatedRect);
        return rotatedRect;
    }
}

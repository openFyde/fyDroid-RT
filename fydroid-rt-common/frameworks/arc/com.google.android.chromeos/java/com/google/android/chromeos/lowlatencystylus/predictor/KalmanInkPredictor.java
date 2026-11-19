/* Open source replacement for com.google.android.chromeos.lowlatencystylus.predictor.KalmanInkPredictor
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.predictor;

import android.util.Log;
import android.view.MotionEvent;

import com.google.android.chromeos.lowlatencystylus.BatchedMotionEvent;
import com.google.android.chromeos.lowlatencystylus.filter.PenKalmanFilter;
import com.google.android.chromeos.lowlatencystylus.matrix.DVector2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Single-pointer ink predictor built on top of a {@link PenKalmanFilter}.
 * Projects the trajectory forward by the configured prediction target,
 * weighted by speed and jank confidence factors.
 */
public class KalmanInkPredictor implements InkPredictor {
    private static final String TAG = "KalmanInkPredictor";

    private static final float ACCELERATION_INFLUENCE = 0.5f;
    private static final int EVENT_TIME_IGNORED_THRESHOLD_MS = 20;
    private static final float HIGH_JANK = 0.2f;
    private static final float HIGH_SPEED = 2.0f;
    private static final float JANK_INFLUENCE = 0.1f;
    private static final float LOW_JANK = 0.02f;
    private static final float LOW_SPEED = 0.0f;
    private static final int MIN_KALMAN_FILTER_ITERATIONS = 4;
    private static final float VELOCITY_INFLUENCE = 1.0f;

    private long mPrevEventTime;
    private float mPredictionTargetMs = LOW_SPEED;
    private final PenKalmanFilter mKalman = new PenKalmanFilter(0.01d, 1.0d);
    private final DVector2 mLastPosition = new DVector2();
    private List<Float> mReportRates = new LinkedList<>();
    private int mExpectedPredictionSampleSize = -1;
    private float mReportRateMs = LOW_SPEED;
    private final DVector2 mPosition = new DVector2();
    private final DVector2 mVelocity = new DVector2();
    private final DVector2 mAcceleration = new DVector2();
    private final DVector2 mJank = new DVector2();
    private int mPointerId = 0;
    private double mPressure = 0.0d;

    public KalmanInkPredictor() {
        this.mKalman.reset();
        this.mPrevEventTime = 0L;
    }

    void initStrokePrediction(int pointerId) {
        this.mKalman.reset();
        this.mPrevEventTime = 0L;
        this.mPointerId = pointerId;
    }

    private void update(float x, float y, float pressure, long eventTime) {
        if (x == this.mLastPosition.a1 && y == this.mLastPosition.a2
                && eventTime <= this.mPrevEventTime + EVENT_TIME_IGNORED_THRESHOLD_MS) {
            return;
        }
        this.mKalman.update(x, y, pressure);
        this.mLastPosition.a1 = x;
        this.mLastPosition.a2 = y;
        if (this.mReportRates != null && this.mReportRates.size() < 20) {
            if (this.mPrevEventTime > 0) {
                float dt = eventTime - this.mPrevEventTime;
                this.mReportRates.add(Float.valueOf(dt));
                float sum = LOW_SPEED;
                Iterator<Float> it = this.mReportRates.iterator();
                while (it.hasNext()) {
                    sum += it.next().floatValue();
                }
                this.mReportRateMs = sum / this.mReportRates.size();
            }
        }
        this.mPrevEventTime = eventTime;
    }

    @Override
    public int getPredictionTarget() {
        return (int) this.mPredictionTargetMs;
    }

    @Override
    public void setPredictionTarget(int predictionTargetMillis) {
        if (predictionTargetMillis < 0) {
            predictionTargetMillis = 0;
        }
        this.mPredictionTargetMs = predictionTargetMillis;
        if (this.mReportRates == null) {
            this.mExpectedPredictionSampleSize = (int) Math.ceil(this.mPredictionTargetMs / this.mReportRateMs);
        }
    }

    @Override
    public void setReportRate(int reportRateMs) {
        if (reportRateMs <= 0) {
            throw new IllegalArgumentException(
                    "reportRateMs should always be a strictlypositive number");
        }
        this.mReportRateMs = reportRateMs;
        this.mReportRates = null;
        this.mExpectedPredictionSampleSize = (int) Math.ceil(this.mPredictionTargetMs / this.mReportRateMs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            this.mKalman.reset();
            this.mPrevEventTime = 0L;
            return false;
        }
        int pointerIndex = event.findPointerIndex(this.mPointerId);
        if (pointerIndex == -1) {
            Log.i(TAG, String.format(Locale.ROOT,
                    "onTouchEvent: Cannot find pointerId=%d in motionEvent=%s",
                    Integer.valueOf(this.mPointerId), event));
            return false;
        }
        for (BatchedMotionEvent ev : BatchedMotionEvent.iterate(event)) {
            MotionEvent.PointerCoords pointerCoords = ev.coords[pointerIndex];
            update(pointerCoords.x, pointerCoords.y, pointerCoords.pressure, ev.timeMs);
        }
        return true;
    }

    @Override
    public MotionEvent predict() {
        if (this.mExpectedPredictionSampleSize == -1
                && this.mKalman.getNumIterations() < MIN_KALMAN_FILTER_ITERATIONS) {
            return null;
        }
        this.mPosition.set(this.mLastPosition);
        this.mVelocity.set(this.mKalman.getVelocity());
        this.mAcceleration.set(this.mKalman.getAcceleration());
        this.mJank.set(this.mKalman.getJank());
        this.mPressure = this.mKalman.getPressure();
        double pressureChange = this.mKalman.getPressureChange();
        double speedAbs = this.mVelocity.magnitude() / ((double) this.mReportRateMs);
        double speedFactor = normalizeRange(speedAbs, 0.0d, 2.0d);
        double jankAbs = this.mJank.magnitude();
        double jankFactor = 1.0d - normalizeRange(jankAbs,
                0.019999999552965164d, 0.20000000298023224d);
        double confidenceFactor = speedFactor * jankFactor;

        MotionEvent predictedEvent = null;
        MotionEvent.PointerProperties[] pointerProperties = {new MotionEvent.PointerProperties()};
        pointerProperties[0].id = this.mPointerId;

        double predictionWindowSamples = this.mPredictionTargetMs / this.mReportRateMs;
        int predictionTargetInSamples = (int) Math.ceil(predictionWindowSamples * confidenceFactor);
        if (this.mExpectedPredictionSampleSize != -1
                && predictionTargetInSamples > this.mExpectedPredictionSampleSize) {
            predictionTargetInSamples = this.mExpectedPredictionSampleSize;
        }
        for (int i = 0; i < predictionTargetInSamples; i++) {
            this.mAcceleration.a1 += this.mJank.a1 * 0.10000000149011612d;
            this.mAcceleration.a2 += this.mJank.a2 * 0.10000000149011612d;
            this.mVelocity.a1 += this.mAcceleration.a1 * 0.5d;
            this.mVelocity.a2 += this.mAcceleration.a2 * 0.5d;
            this.mPosition.a1 += this.mVelocity.a1 * 1.0d;
            this.mPosition.a2 += this.mVelocity.a2 * 1.0d;
            double nextPressure = this.mPressure + pressureChange;
            this.mPressure = nextPressure;
            if (nextPressure < 0.1d) {
                break;
            }
            this.mPressure = Math.min(nextPressure, 1.0d);
            MotionEvent.PointerCoords[] coords = {new MotionEvent.PointerCoords()};
            coords[0].x = (float) this.mPosition.a1;
            coords[0].y = (float) this.mPosition.a2;
            coords[0].pressure = (float) this.mPressure;
            if (predictedEvent == null) {
                predictedEvent = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_MOVE, 1,
                        pointerProperties, coords, 0, 0, VELOCITY_INFLUENCE, VELOCITY_INFLUENCE,
                        0, 0, 0, 0);
            } else {
                predictedEvent.addBatch(0L, coords, 0);
            }
        }
        return predictedEvent;
    }

    private double normalizeRange(double x, double min, double max) {
        double normalized = (x - min) / (max - min);
        return Math.min(1.0d, Math.max(normalized, 0.0d));
    }

    protected MotionEvent appendPredictedEvent(MotionEvent predictedEvent) {
        int predictedEventSize = predictedEvent == null ? 0 : predictedEvent.getHistorySize();
        MotionEvent result = predictedEvent;
        for (int i = predictedEventSize; i < this.mExpectedPredictionSampleSize; i++) {
            MotionEvent.PointerCoords[] coords = {new MotionEvent.PointerCoords()};
            coords[0].x = (float) this.mPosition.a1;
            coords[0].y = (float) this.mPosition.a2;
            coords[0].pressure = (float) this.mPressure;
            if (result == null) {
                MotionEvent.PointerProperties[] pointerProperties = {new MotionEvent.PointerProperties()};
                pointerProperties[0].id = this.mPointerId;
                result = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_MOVE, 1,
                        pointerProperties, coords, 0, 0, VELOCITY_INFLUENCE, VELOCITY_INFLUENCE,
                        0, 0, 0, 0);
            } else {
                result.addBatch(0L, coords, 0);
            }
        }
        return result;
    }
}

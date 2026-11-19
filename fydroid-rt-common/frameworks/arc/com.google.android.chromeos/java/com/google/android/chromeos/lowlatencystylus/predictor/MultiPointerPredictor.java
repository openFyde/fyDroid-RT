/* Open source replacement for com.google.android.chromeos.lowlatencystylus.predictor.MultiPointerPredictor
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.predictor;

import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.google.android.chromeos.lowlatencystylus.BatchedMotionEvent;

import java.lang.reflect.Array;
import java.util.Locale;

/**
 * Dispatches a motion event to per-pointer {@link KalmanInkPredictor} instances
 * and merges the single-pointer predictions back into one multi-pointer
 * MotionEvent.
 */
public class MultiPointerPredictor implements InkPredictor {
    private static final String TAG = "MultiPointerPredictor";
    private static final boolean DEBUG_PREDICTION = Log.isLoggable(TAG, Log.DEBUG);

    private final SparseArray<KalmanInkPredictor> mPredictorMap = new SparseArray<>();
    private int mPredictionTargetMs = 0;
    private int mReportRateMs = 0;

    @Override
    public int getPredictionTarget() {
        return this.mPredictionTargetMs;
    }

    @Override
    public void setPredictionTarget(int predictionTargetMillis) {
        if (predictionTargetMillis < 0) {
            predictionTargetMillis = 0;
        }
        this.mPredictionTargetMs = predictionTargetMillis;
        for (int i = 0; i < this.mPredictorMap.size(); i++) {
            this.mPredictorMap.valueAt(i).setPredictionTarget(predictionTargetMillis);
        }
    }

    @Override
    public void setReportRate(int reportRateMs) {
        if (reportRateMs <= 0) {
            throw new IllegalArgumentException(
                    "reportRateMs should always be a strictlypositive number");
        }
        this.mReportRateMs = reportRateMs;
        for (int i = 0; i < this.mPredictorMap.size(); i++) {
            this.mPredictorMap.valueAt(i).setReportRate(this.mReportRateMs);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(event.getActionIndex());
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            KalmanInkPredictor predictor = new KalmanInkPredictor();
            predictor.setPredictionTarget(this.mPredictionTargetMs);
            predictor.setReportRate(this.mReportRateMs);
            predictor.initStrokePrediction(pointerId);
            predictor.onTouchEvent(event);
            this.mPredictorMap.put(pointerId, predictor);
        } else if (action == MotionEvent.ACTION_UP) {
            KalmanInkPredictor predictor = this.mPredictorMap.get(pointerId);
            if (predictor != null) {
                this.mPredictorMap.remove(pointerId);
                predictor.onTouchEvent(event);
            }
            this.mPredictorMap.clear();
        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            KalmanInkPredictor predictor = this.mPredictorMap.get(pointerId);
            if (predictor != null) {
                this.mPredictorMap.remove(pointerId);
                predictor.onTouchEvent(event);
            }
        } else if (action == MotionEvent.ACTION_CANCEL) {
            this.mPredictorMap.clear();
        } else if (action == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < this.mPredictorMap.size(); i++) {
                this.mPredictorMap.valueAt(i).onTouchEvent(event);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public MotionEvent predict() {
        int pointerCount = this.mPredictorMap.size();
        if (pointerCount == 0) {
            if (DEBUG_PREDICTION) {
                Log.d(TAG, "predict() -> null: no pointer on screen");
            }
            return null;
        }
        if (pointerCount == 1) {
            MotionEvent predictedEv = this.mPredictorMap.valueAt(0).predict();
            if (DEBUG_PREDICTION) {
                Log.d(TAG, "predict() -> MotionEvent: " + predictedEv);
            }
            return predictedEv;
        }

        int[] pointerIds = new int[pointerCount];
        MotionEvent[] singlePointerEvents = new MotionEvent[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerIds[i] = this.mPredictorMap.keyAt(i);
            KalmanInkPredictor predictor = this.mPredictorMap.valueAt(i);
            singlePointerEvents[i] = predictor.predict();
            singlePointerEvents[i] = predictor.appendPredictedEvent(singlePointerEvents[i]);
        }
        int minHistorySize = Integer.MAX_VALUE;
        for (MotionEvent ev : singlePointerEvents) {
            if (ev.getHistorySize() < minHistorySize) {
                minHistorySize = ev.getHistorySize();
            }
        }
        minHistorySize += 1;

        MotionEvent.PointerCoords[][] pointerCoords =
                (MotionEvent.PointerCoords[][]) Array.newInstance(
                        MotionEvent.PointerCoords.class, minHistorySize, pointerCount);
        for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++) {
            int historyIndex = 0;
            for (BatchedMotionEvent ev : BatchedMotionEvent.iterate(singlePointerEvents[pointerIndex])) {
                pointerCoords[historyIndex][pointerIndex] = ev.coords[0];
                historyIndex++;
                if (minHistorySize <= historyIndex) {
                    break;
                }
            }
        }
        for (MotionEvent ev : singlePointerEvents) {
            ev.recycle();
        }
        MotionEvent.PointerProperties[] pointerProperties =
                new MotionEvent.PointerProperties[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerProperties[i] = new MotionEvent.PointerProperties();
            pointerProperties[i].id = pointerIds[i];
        }
        MotionEvent multiPointerEvent = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_MOVE,
                pointerCount, pointerProperties, pointerCoords[0], 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
        for (int historyIndex = 1; historyIndex < minHistorySize; historyIndex++) {
            multiPointerEvent.addBatch(0L, pointerCoords[historyIndex], 0);
        }
        if (DEBUG_PREDICTION) {
            StringBuilder builder = new StringBuilder(String.format(Locale.ROOT,
                    "predict() -> MotionEvent: (pointerCount=%d, historySize=%d);",
                    Integer.valueOf(multiPointerEvent.getPointerCount()),
                    Integer.valueOf(multiPointerEvent.getHistorySize())));
            for (BatchedMotionEvent motionEvent : BatchedMotionEvent.iterate(multiPointerEvent)) {
                builder.append("      ");
                for (MotionEvent.PointerCoords coord : motionEvent.coords) {
                    builder.append(String.format(Locale.ROOT, "(%f, %f)",
                            Float.valueOf(coord.x), Float.valueOf(coord.y)));
                }
                builder.append("\n");
            }
            Log.d(TAG, builder.toString());
        }
        return multiPointerEvent;
    }
}

/* Open source replacement for com.google.android.chromeos.lowlatencystylus.predictor.InkPredictor
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.predictor;

import android.view.MotionEvent;

/**
 * Interface for motion-event predictors used by the low-latency stylus library.
 */
public interface InkPredictor {
    int getPredictionTarget();

    boolean onTouchEvent(MotionEvent motionEvent);

    MotionEvent predict();

    void setPredictionTarget(int i);

    void setReportRate(int i);
}

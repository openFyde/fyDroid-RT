/* Open source replacement for com.google.android.chromeos.lowlatencystylus.matrix.DVector2
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.matrix;

/**
 * Two-component double-precision vector used by the Kalman ink predictor.
 */
public class DVector2 {
    public double a1;
    public double a2;

    public double magnitude() {
        return Math.hypot(this.a1, this.a2);
    }

    public void set(DVector2 newValue) {
        this.a1 = newValue.a1;
        this.a2 = newValue.a2;
    }
}

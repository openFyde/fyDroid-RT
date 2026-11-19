/* Open source replacement for com.google.android.chromeos.lowlatencystylus.filter.KalmanFilter
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.filter;

import com.google.android.chromeos.lowlatencystylus.matrix.Matrix;

/**
 * Textbook linear Kalman filter. Public fields are the standard Kalman state
 * vector and matrices so callers (like {@link PenKalmanFilter}) can assign
 * the transition/observation/process-noise matrices directly.
 */
public class KalmanFilter {
    public Matrix F;
    public Matrix H;
    public Matrix K;
    public Matrix P;
    public Matrix Q;
    public Matrix R;
    public Matrix x;

    private int mXDim;
    private int mZDim;

    public KalmanFilter(int xDim, int zDim) {
        this.mXDim = xDim;
        this.mZDim = zDim;
        this.x = new Matrix(xDim, 1);
        this.P = Matrix.identity(xDim);
        this.Q = Matrix.identity(xDim);
        this.R = Matrix.identity(zDim);
        this.F = new Matrix(xDim, xDim);
        this.H = new Matrix(zDim, xDim);
        this.K = new Matrix(xDim, zDim);
    }

    public void reset() {
        this.x.fill(0.0d);
        Matrix.setIdentity(this.P);
        this.K.fill(0.0d);
    }

    public void predict() {
        this.x = this.F.dot(this.x);
        this.P = this.F.dot(this.P).dotTranspose(this.F).plus(this.Q);
    }

    public void update(Matrix z) {
        Matrix y = z.minus(this.H.dot(this.x));
        Matrix tS = this.H.dot(this.P).dotTranspose(this.H).plus(this.R);
        this.K = this.P.dotTranspose(this.H).dot(tS.inverse());
        this.x = this.x.plus(this.K.dot(y));
        this.P = this.P.minus(this.K.dot(this.H).dot(this.P));
    }
}

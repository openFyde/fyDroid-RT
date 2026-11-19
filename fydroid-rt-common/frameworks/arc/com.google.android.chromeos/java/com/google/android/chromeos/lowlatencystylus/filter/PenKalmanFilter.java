/* Open source replacement for com.google.android.chromeos.lowlatencystylus.filter.PenKalmanFilter
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.filter;

import com.google.android.chromeos.lowlatencystylus.matrix.DVector2;
import com.google.android.chromeos.lowlatencystylus.matrix.Matrix;

/**
 * A pen-trajectory Kalman filter that tracks the x/y position, velocity,
 * acceleration and jerk of the stylus, plus pressure and its first derivative.
 */
public class PenKalmanFilter {
    private final double mSigmaProcess;
    private final double mSigmaMeasurement;

    private final DVector2 mPosition = new DVector2();
    private final DVector2 mVelocity = new DVector2();
    private final DVector2 mAcceleration = new DVector2();
    private final DVector2 mJank = new DVector2();
    private double mPressure = 0.0d;
    private double mPressureChange = 0.0d;
    private int numIterations = 0;

    private final Matrix mNewX = new Matrix(1, 1);
    private final Matrix mNewY = new Matrix(1, 1);
    private final Matrix mNewP = new Matrix(1, 1);

    private final KalmanFilter mXKalman = createAxisKalmanFilter();
    private final KalmanFilter mYKalman = createAxisKalmanFilter();
    private final KalmanFilter mPKalman = createAxisKalmanFilter();

    public PenKalmanFilter(double sigmaProcess, double sigmaMeasurement) {
        this.mSigmaProcess = sigmaProcess;
        this.mSigmaMeasurement = sigmaMeasurement;
    }

    public void reset() {
        this.mXKalman.reset();
        this.mYKalman.reset();
        this.mPKalman.reset();
        this.numIterations = 0;
    }

    public void update(float x, float y, float pressure) {
        if (this.numIterations == 0) {
            this.mXKalman.x.put(0, 0, x);
            this.mYKalman.x.put(0, 0, y);
            this.mPKalman.x.put(0, 0, pressure);
        } else {
            this.mNewX.put(0, 0, x);
            this.mXKalman.predict();
            this.mXKalman.update(this.mNewX);
            this.mNewY.put(0, 0, y);
            this.mYKalman.predict();
            this.mYKalman.update(this.mNewY);
            this.mNewP.put(0, 0, pressure);
            this.mPKalman.predict();
            this.mPKalman.update(this.mNewP);
        }
        this.numIterations++;
        this.mPosition.a1 = this.mXKalman.x.get(0, 0);
        this.mPosition.a2 = this.mYKalman.x.get(0, 0);
        this.mVelocity.a1 = this.mXKalman.x.get(1, 0);
        this.mVelocity.a2 = this.mYKalman.x.get(1, 0);
        this.mAcceleration.a1 = this.mXKalman.x.get(2, 0);
        this.mAcceleration.a2 = this.mYKalman.x.get(2, 0);
        this.mJank.a1 = this.mXKalman.x.get(3, 0);
        this.mJank.a2 = this.mYKalman.x.get(3, 0);
        this.mPressure = this.mPKalman.x.get(0, 0);
        this.mPressureChange = this.mPKalman.x.get(1, 0);
    }

    public DVector2 getPosition() {
        return this.mPosition;
    }

    public DVector2 getVelocity() {
        return this.mVelocity;
    }

    public DVector2 getAcceleration() {
        return this.mAcceleration;
    }

    public DVector2 getJank() {
        return this.mJank;
    }

    public double getPressure() {
        return this.mPressure;
    }

    public double getPressureChange() {
        return this.mPressureChange;
    }

    public int getNumIterations() {
        return this.numIterations;
    }

    private KalmanFilter createAxisKalmanFilter() {
        KalmanFilter kalman = new KalmanFilter(4, 1);
        kalman.F = new Matrix(4, new double[]{
                1.0d, 1.0d, 0.5d, 0.16d,
                0.0d, 1.0d, 1.0d, 0.5d,
                0.0d, 0.0d, 1.0d, 1.0d,
                0.0d, 0.0d, 0.0d, 1.0d});
        Matrix G = new Matrix(1, new double[]{0.16d, 0.5d, 1.0d, 1.0d});
        G.dotTranspose(G, kalman.Q);
        kalman.Q.scale(this.mSigmaProcess);
        kalman.H = new Matrix(4, new double[]{1.0d, 0.0d, 0.0d, 0.0d});
        kalman.R.put(0, 0, this.mSigmaMeasurement);
        return kalman;
    }
}

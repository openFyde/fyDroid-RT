/* Open source replacement for com.google.android.chromeos.lowlatencystylus.matrix.Matrix
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.matrix;

import java.util.Arrays;
import java.util.Locale;

/**
 * Dense row-major double-precision matrix. Pure math data class — reproduces
 * the proprietary implementation line for line so the Kalman filter numerics
 * match exactly.
 */
public class Matrix {
    private final int mRows;
    private final int mCols;
    private final double[] mMem;

    public Matrix(int rows, int cols) {
        this.mRows = rows;
        this.mCols = cols;
        this.mMem = new double[rows * cols];
    }

    public Matrix(int stride, double[] values) {
        int rows = ((values.length + stride) - 1) / stride;
        this.mRows = rows;
        this.mCols = stride;
        this.mMem = values;
        if (values.length != rows * stride) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "Invalid number of elements in 'values' Expected:%d Actual:%d",
                    Integer.valueOf(this.mMem.length),
                    Integer.valueOf(this.mRows & this.mCols)));
        }
    }

    public Matrix(Matrix src) {
        this.mRows = src.mRows;
        this.mCols = src.mCols;
        this.mMem = new double[this.mRows * this.mCols];
        System.arraycopy(src.mMem, 0, this.mMem, 0, this.mMem.length);
    }

    public int getNumRows() {
        return this.mRows;
    }

    public int getNumCols() {
        return this.mCols;
    }

    public static Matrix identity(int width) {
        Matrix ret = new Matrix(width, width);
        setIdentity(ret);
        return ret;
    }

    public static void setIdentity(Matrix matrix) {
        Arrays.fill(matrix.mMem, 0.0d);
        int width = Math.min(matrix.mRows, matrix.mCols);
        for (int i = 0; i < width; i++) {
            matrix.put(i, i, 1.0d);
        }
    }

    public double get(int i, int j) {
        if (i < 0 || i >= this.mRows || j < 0 || j >= this.mCols) {
            throw new IndexOutOfBoundsException(String.format(Locale.ROOT,
                    "Invalid matrix index value. i:%d j:%d not available in %s",
                    Integer.valueOf(i), Integer.valueOf(j), shortString()));
        }
        return this.mMem[(this.mCols * i) + j];
    }

    public void put(int i, int j, double v) {
        if (i < 0 || i >= this.mRows || j < 0 || j >= this.mCols) {
            throw new IndexOutOfBoundsException(String.format(Locale.ROOT,
                    "Invalid matrix index value. i:%d j:%d not available in %s",
                    Integer.valueOf(i), Integer.valueOf(j), shortString()));
        }
        this.mMem[(this.mCols * i) + j] = v;
    }

    public void fill(double value) {
        Arrays.fill(this.mMem, value);
    }

    public void scale(double alpha) {
        int size = this.mRows * this.mCols;
        for (int i = 0; i < size; i++) {
            this.mMem[i] = this.mMem[i] * alpha;
        }
    }

    public Matrix plus(Matrix that) {
        if (this.mRows != that.mRows || this.mCols != that.mCols) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The matrix dimensions are not the same. this:%s that:%s",
                    shortString(), that.shortString()));
        }
        for (int i = 0; i < this.mMem.length; i++) {
            this.mMem[i] = this.mMem[i] + that.mMem[i];
        }
        return this;
    }

    public Matrix minus(Matrix that) {
        if (this.mRows != that.mRows || this.mCols != that.mCols) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The matrix dimensions are not the same. this:%s that:%s",
                    shortString(), that.shortString()));
        }
        for (int i = 0; i < this.mMem.length; i++) {
            this.mMem[i] = this.mMem[i] - that.mMem[i];
        }
        return this;
    }

    public Matrix dot(Matrix that) {
        try {
            return dot(that, new Matrix(this.mRows, that.mCols));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The matrices dimensions are not conformant for a dot matrix operation. this:%s that:%s",
                    shortString(), that.shortString()));
        }
    }

    public Matrix dot(Matrix that, Matrix result) {
        if (this.mRows != result.mRows || this.mCols != that.mRows || that.mCols != result.mCols) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The matrices dimensions are not conformant for a dot matrix operation. this:%s that:%s result:%s",
                    shortString(), that.shortString(), result.shortString()));
        }
        for (int i = 0; i < this.mRows; i++) {
            for (int j = 0; j < that.mCols; j++) {
                double s = 0.0d;
                for (int k = 0; k < this.mCols; k++) {
                    s += get(i, k) * that.get(k, j);
                }
                result.put(i, j, s);
            }
        }
        return result;
    }

    public Matrix inverse() {
        if (this.mRows != this.mCols) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The matrix is not square. this:%s", shortString()));
        }
        Matrix scratch = new Matrix(this.mRows, this.mCols * 2);
        for (int i = 0; i < this.mRows; i++) {
            for (int j = 0; j < this.mCols; j++) {
                scratch.put(i, j, get(i, j));
                scratch.put(i, this.mCols + j, i == j ? 1.0d : 0.0d);
            }
        }
        for (int i = 0; i < this.mRows; i++) {
            int ibest = i;
            double vbest = Math.abs(scratch.get(i, i));
            for (int ii = i + 1; ii < this.mRows; ii++) {
                double v = Math.abs(scratch.get(ii, i));
                if (v > vbest) {
                    ibest = ii;
                    vbest = v;
                }
            }
            if (ibest != i) {
                for (int j = 0; j < scratch.mCols; j++) {
                    double t = scratch.get(i, j);
                    scratch.put(i, j, scratch.get(ibest, j));
                    scratch.put(ibest, j, t);
                }
            }
            double d = scratch.get(i, i);
            if (d == 0.0d) {
                throw new ArithmeticException("Singular matrix");
            }
            for (int j = 0; j < scratch.mCols; j++) {
                scratch.put(i, j, scratch.get(i, j) / d);
            }
            for (int ii = i + 1; ii < this.mRows; ii++) {
                double d2 = scratch.get(ii, i);
                for (int j = 0; j < scratch.mCols; j++) {
                    scratch.put(ii, j, scratch.get(ii, j) - (scratch.get(i, j) * d2));
                }
            }
        }
        for (int i = this.mRows - 1; i >= 0; i--) {
            for (int ii = 0; ii < i; ii++) {
                double d = scratch.get(ii, i);
                for (int j = 0; j < scratch.mCols; j++) {
                    scratch.put(ii, j, scratch.get(ii, j) - (scratch.get(i, j) * d));
                }
            }
        }
        for (int i = 0; i < this.mRows; i++) {
            for (int j = 0; j < this.mCols; j++) {
                put(i, j, scratch.get(i, this.mCols + j));
            }
        }
        return this;
    }

    public Matrix dotTranspose(Matrix that) {
        try {
            return dotTranspose(that, new Matrix(this.mRows, that.mRows));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The matrices dimensions are not conformant for a transpose operation. this:%s that:%s",
                    shortString(), that.shortString()));
        }
    }

    public Matrix dotTranspose(Matrix that, Matrix result) {
        if (this.mRows != result.mRows || this.mCols != that.mCols || that.mRows != result.mCols) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The matrices dimensions are not conformant for a transpose operation. this:%s that:%s result:%s",
                    shortString(), that.shortString(), result.shortString()));
        }
        for (int i = 0; i < this.mRows; i++) {
            for (int j = 0; j < that.mRows; j++) {
                double s = 0.0d;
                for (int k = 0; k < this.mCols; k++) {
                    s += get(i, k) * that.get(j, k);
                }
                result.put(i, j, s);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof Matrix)) {
            return false;
        }
        Matrix other = (Matrix) that;
        if (this.mRows != other.mRows || this.mCols != other.mCols) {
            return false;
        }
        for (int i = 0; i < this.mMem.length; i++) {
            if (this.mMem[i] != other.mMem[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = (this.mRows * 101) + this.mCols;
        for (double m : this.mMem) {
            h = (h * 37) + Double.hashCode(m);
        }
        return h;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.mRows * this.mCols * 8);
        sb.append(this.mRows);
        sb.append("x");
        sb.append(this.mCols);
        sb.append(" [");
        for (int i = 0; i < this.mMem.length; i++) {
            if (i > 0) {
                sb.append(i % this.mCols == 0 ? "; " : ", ");
            }
            sb.append(this.mMem[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private String shortString() {
        return "(" + this.mRows + "x" + this.mCols + ")";
    }
}

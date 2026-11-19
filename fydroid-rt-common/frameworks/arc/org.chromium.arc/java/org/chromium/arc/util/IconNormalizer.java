package org.chromium.arc.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

class IconNormalizer {
    private AdaptiveIconDrawable mAdaptiveIconWrapper;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float[] mLeftBorder;
    private final Matrix mMatrix;
    private Paint mPaintMaskShape;
    private Paint mPaintMaskShapeOutline;
    private byte[] mPixels;
    private float[] mRightBorder;
    private final Path mShapePath;
    private int mMaxSize = 0;
    private final Map<Integer, Float> mAdaptiveIconScaleMap = new HashMap<>();
    private final Rect mBounds = new Rect();

    private static class FixedScaleDrawable extends DrawableWrapper {
        private static final float DEFAULT_VIEWPORT_SCALE;
        private static final float LEGACY_ICON_SCALE;
        private float mScaleX;
        private float mScaleY;

        static {
            float extraInsetFraction =
                    1.0f / ((AdaptiveIconDrawable.getExtraInsetFraction() * 2.0f) + 1.0f);
            DEFAULT_VIEWPORT_SCALE = extraInsetFraction;
            LEGACY_ICON_SCALE = extraInsetFraction * 0.7f;
        }

        FixedScaleDrawable() {
            super(new ColorDrawable());
            mScaleX = LEGACY_ICON_SCALE;
            mScaleY = LEGACY_ICON_SCALE;
        }

        @Override
        public void draw(Canvas canvas) {
            int save = canvas.save();
            canvas.scale(mScaleX, mScaleY,
                    getBounds().exactCenterX(), getBounds().exactCenterY());
            super.draw(canvas);
            canvas.restoreToCount(save);
        }

        @Override
        public int getIntrinsicHeight() {
            return (int) Math.ceil(super.getIntrinsicHeight() / LEGACY_ICON_SCALE);
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) Math.ceil(super.getIntrinsicWidth() / LEGACY_ICON_SCALE);
        }

        @Override
        public void inflate(Resources resources, XmlPullParser parser,
                AttributeSet attrs) {
        }

        @Override
        public void inflate(Resources resources, XmlPullParser parser,
                AttributeSet attrs, Resources.Theme theme) {
        }

        void setScale(float scale) {
            float intrinsicHeight = super.getIntrinsicHeight();
            float intrinsicWidth = super.getIntrinsicWidth();
            float scaledX = scale * LEGACY_ICON_SCALE;
            mScaleX = scaledX;
            mScaleY = scale * LEGACY_ICON_SCALE;
            if (intrinsicHeight > intrinsicWidth && intrinsicWidth > 0.0f) {
                mScaleX = (intrinsicWidth / intrinsicHeight) * scaledX;
            } else if (intrinsicWidth > intrinsicHeight && intrinsicHeight > 0.0f) {
                mScaleY = (intrinsicHeight / intrinsicWidth) * mScaleY;
            }
        }
    }

    IconNormalizer() {
        Paint paint = new Paint();
        mPaintMaskShape = paint;
        paint.setColor(-65536);
        mPaintMaskShape.setStyle(Paint.Style.FILL);
        mPaintMaskShape.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Paint paint2 = new Paint();
        mPaintMaskShapeOutline = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        mPaintMaskShapeOutline.setColor(-16777216);
        mPaintMaskShapeOutline.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mShapePath = new Path();
        mMatrix = new Matrix();
        mAdaptiveIconWrapper = new AdaptiveIconDrawable(
                new ColorDrawable(-1), new FixedScaleDrawable());
    }

    private static void convertToConvexArray(float[] arr, int direction,
            int startIdx, int endIdx) {
        float[] slopes = new float[arr.length - 1];
        int lastIdx = -1;
        float lastSlope = Float.MAX_VALUE;
        for (int i = startIdx + 1; i <= endIdx; i++) {
            if (arr[i] > -1.0f) {
                if (lastSlope == Float.MAX_VALUE) {
                    lastIdx = startIdx;
                } else {
                    float slopeDiff = ((arr[i] - arr[lastIdx]) / (i - lastIdx)) - lastSlope;
                    if (slopeDiff * direction < 0.0f) {
                        while (lastIdx > startIdx) {
                            lastIdx--;
                            if ((((arr[i] - arr[lastIdx]) / (i - lastIdx))
                                    - slopes[lastIdx]) * direction >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                lastSlope = (arr[i] - arr[lastIdx]) / (i - lastIdx);
                for (int j = lastIdx; j < i; j++) {
                    slopes[j] = lastSlope;
                    arr[j] = ((j - lastIdx) * lastSlope) + arr[lastIdx];
                }
                lastIdx = i;
            }
        }
    }

    private synchronized float getScale(Drawable drawable, Path path, boolean[] isShape) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            if (width <= 0 || width > mMaxSize) width = mMaxSize;
            if (height <= 0 || height > mMaxSize) height = mMaxSize;
        } else if (width > mMaxSize || height > mMaxSize) {
            int max = Math.max(width, height);
            width = (mMaxSize * width) / max;
            height = (mMaxSize * height) / max;
        }
        if ((drawable instanceof AdaptiveIconDrawable)
                && mAdaptiveIconScaleMap.containsKey(width)) {
            return mAdaptiveIconScaleMap.get(width);
        }
        mBitmap.eraseColor(0);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(mCanvas);
        ByteBuffer buffer = ByteBuffer.wrap(mPixels);
        buffer.rewind();
        mBitmap.copyPixelsToBuffer(buffer);
        int minLeft = mMaxSize + 1;
        int padding = mMaxSize - width;
        int pixelIdx = 0;
        int topRow = -1;
        int maxRight = -1;
        int bottomRow = -1;
        for (int row = 0; row < height; row++) {
            int leftEdge = -1;
            int rightEdge = -1;
            for (int col = 0; col < width; col++) {
                if ((mPixels[pixelIdx] & 255) > 40) {
                    if (leftEdge == -1) leftEdge = col;
                    rightEdge = col;
                }
                pixelIdx++;
            }
            pixelIdx += padding;
            mLeftBorder[row] = leftEdge;
            mRightBorder[row] = rightEdge;
            if (leftEdge != -1) {
                if (topRow == -1) topRow = row;
                minLeft = Math.min(minLeft, leftEdge);
                maxRight = Math.max(maxRight, rightEdge);
                bottomRow = row;
            }
        }
        if (topRow == -1 || maxRight == -1) {
            return 1.0f;
        }
        convertToConvexArray(mLeftBorder, 1, topRow, bottomRow);
        convertToConvexArray(mRightBorder, -1, topRow, bottomRow);
        float hullArea = 0.0f;
        for (int i = 0; i < height; i++) {
            if (mLeftBorder[i] > -1.0f) {
                hullArea += (mRightBorder[i] - mLeftBorder[i]) + 1.0f;
            }
        }
        float boundArea = ((maxRight + 1) - minLeft) * ((bottomRow + 1) - topRow);
        float hullRatio = hullArea / boundArea;
        float targetFill = hullRatio < 0.7853982f
                ? 0.6597222f : ((1.0f - hullRatio) * 0.040449437f) + 0.6510417f;
        mBounds.left = minLeft;
        mBounds.right = maxRight;
        mBounds.top = topRow;
        mBounds.bottom = bottomRow;
        if (isShape != null && isShape.length > 0) {
            isShape[0] = isShape(path);
        }
        float fillRatio = hullArea / (height * width);
        float scale = fillRatio > targetFill
                ? (float) Math.sqrt(targetFill / fillRatio) : 1.0f;
        if (drawable instanceof AdaptiveIconDrawable) {
            mAdaptiveIconScaleMap.put(width, scale);
        }
        return scale;
    }

    private boolean isShape(Path path) {
        if (Math.abs((mBounds.width() / (float) mBounds.height()) - 1.0f) > 0.05f) {
            return false;
        }
        mMatrix.reset();
        mMatrix.setScale(mBounds.width(), mBounds.height());
        mMatrix.postTranslate(mBounds.left, mBounds.top);
        path.transform(mMatrix, mShapePath);
        mCanvas.drawPath(mShapePath, mPaintMaskShape);
        mCanvas.drawPath(mShapePath, mPaintMaskShapeOutline);
        ByteBuffer buffer = ByteBuffer.wrap(mPixels);
        buffer.rewind();
        mBitmap.copyPixelsToBuffer(buffer);
        int startIdx = mBounds.top * mMaxSize;
        int rightPad = mMaxSize - mBounds.right;
        int diffPixels = 0;
        for (int row = mBounds.top; row < mBounds.bottom; row++) {
            int colIdx = startIdx + mBounds.left;
            for (int col = mBounds.left; col < mBounds.right; col++) {
                if ((mPixels[colIdx] & 255) > 40) {
                    diffPixels++;
                }
                colIdx++;
            }
            startIdx = colIdx + rightPad;
        }
        return ((float) diffPixels) / ((float) (mBounds.height() * mBounds.width())) < 0.01f;
    }

    Drawable normalizeIcon(Drawable drawable, int backgroundColor,
            int iconSize, float[] outScale) {
        float sizeF = iconSize;
        mPaintMaskShapeOutline.setStrokeWidth(Math.max(2.0f, sizeF / 24.0f));
        int doubleSize = iconSize * 2;
        if (doubleSize > mMaxSize) {
            int quadSize = doubleSize * 2;
            mMaxSize = quadSize;
            mBitmap = Bitmap.createBitmap(quadSize, quadSize, Bitmap.Config.ALPHA_8);
            mCanvas = new Canvas(mBitmap);
            mPixels = new byte[mMaxSize * mMaxSize];
            mLeftBorder = new float[mMaxSize];
            mRightBorder = new float[mMaxSize];
        }
        mAdaptiveIconWrapper.setBounds(0, 0, 1, 1);
        boolean[] isShapeArr = new boolean[1];
        float scale = getScale(drawable, mAdaptiveIconWrapper.getIconMask(), isShapeArr);
        if (!isShapeArr[0] && !(drawable instanceof AdaptiveIconDrawable)) {
            ((ColorDrawable) mAdaptiveIconWrapper.getBackground())
                    .setColor(backgroundColor);
            FixedScaleDrawable fg =
                    (FixedScaleDrawable) mAdaptiveIconWrapper.getForeground();
            fg.setDrawable(drawable);
            fg.setScale(scale);
            drawable = mAdaptiveIconWrapper;
            scale = getScale(drawable, null, null);
        }
        outScale[0] = (iconSize - ((((int) Math.floor((1.0f - scale) * sizeF)) / 2) * 2))
                / sizeF;
        return drawable;
    }
}

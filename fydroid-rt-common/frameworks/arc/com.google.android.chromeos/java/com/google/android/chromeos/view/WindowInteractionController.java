/* Open source replacement for com.google.android.chromeos.view.WindowInteractionController from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.view;

import android.app.Activity;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;

import com.google.android.chromeos.CompanionLibUtil;
import com.google.android.chromeos.IArcCompanionHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/**
 * Attaches a TouchDelegate to a View so that Chrome OS can turn raw
 * pointer events on a custom caption bar / grip into Android window
 * move-and-resize operations (via ViewRootImpl.startMovingTask and the
 * IArcCompanionHandler.startResizingTask binder).
 *
 * Reproduces the proprietary public API:
 *   - Integer HIT_TYPE_* constants.
 *   - Nested OnHitListener interface.
 *   - Nested HitType annotation interface.
 *   - Public constructor taking (View, OnHitListener).
 */
public class WindowInteractionController {
    public static final int HIT_TYPE_NONE = 0;
    public static final int HIT_TYPE_MOVE = 1;
    public static final int HIT_TYPE_RESIZE_DIAGONAL = 2;
    public static final int HIT_TYPE_RESIZE_HORIZONTAL = 3;
    public static final int HIT_TYPE_RESIZE_VERTICAL = 4;

    private static final int CTRL_TYPE_NONE = 0;
    private static final int CTRL_TYPE_LEFT = 1;
    private static final int CTRL_TYPE_RIGHT = 2;
    private static final int CTRL_TYPE_TOP = 4;
    private static final int CTRL_TYPE_BOTTOM = 8;

    private static final String TAG = "WindowIntController";

    private final View mView;
    private final Activity mActivity;
    private final OnHitListener mOnHitListener;
    private final IArcCompanionHandler mArcCompanionHandler;

    @Retention(RetentionPolicy.SOURCE)
    public @interface HitType {
    }

    public interface OnHitListener {
        int onHit(int x, int y);
    }

    public WindowInteractionController(View view, OnHitListener onHitListener) {
        Objects.requireNonNull(view);
        Objects.requireNonNull(onHitListener);
        this.mView = view;
        this.mActivity = (Activity) view.getContext();
        this.mOnHitListener = onHitListener;
        IArcCompanionHandler handler = CompanionLibUtil.getCompanionHandler(mActivity);
        this.mArcCompanionHandler = handler;
        if (handler == null) {
            Log.e(TAG, "Cannot connect to Companion Service");
        }
        setupViewTouchDelegate();
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_WINDOW_INTERACTION_CONTROLLER);
    }

    private void setupViewTouchDelegate() {
        Rect viewBounds = new Rect(0, 0, mView.getWidth(), mView.getHeight());
        mView.setTouchDelegate(new ViewTouchDelegate(viewBounds, mView));
    }

    class ViewTouchDelegate extends TouchDelegate {
        private final int mDragSlopSquared;
        private final int[] mTmpLocOnScreen = new int[2];
        private int mTouchDownX;
        private int mTouchDownY;
        private boolean mDragging = false;
        private boolean mCheckForDragging = false;
        private boolean mLeftMouseButtonReleased = false;

        ViewTouchDelegate(Rect bounds, View delegateView) {
            super(bounds, delegateView);
            int dragSlop = ViewConfiguration.get(
                    delegateView.getContext()).getScaledTouchSlop();
            this.mDragSlopSquared = dragSlop * dragSlop;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (action == MotionEvent.ACTION_DOWN) {
                if (event.getToolType(event.getActionIndex())
                                != MotionEvent.TOOL_TYPE_MOUSE
                        || (event.getButtonState() & MotionEvent.BUTTON_PRIMARY) != 0) {
                    mCheckForDragging = true;
                    mTouchDownX = x;
                    mTouchDownY = y;
                }
            } else if (action == MotionEvent.ACTION_UP) {
                mCheckForDragging = false;
                mDragging = false;
            } else if (action == MotionEvent.ACTION_CANCEL) {
                mCheckForDragging = false;
                mDragging = false;
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (!mDragging && mCheckForDragging && dragStarted(x, y)) {
                    mCheckForDragging = false;
                    mDragging = true;
                    mLeftMouseButtonReleased = false;
                    View rootView = mView.getRootView();
                    mView.getLocationOnScreen(mTmpLocOnScreen);
                    int absX = mTouchDownX + mTmpLocOnScreen[0];
                    int absY = mTouchDownY + mTmpLocOnScreen[1];
                    int hitType = mOnHitListener.onHit(absX, absY);
                    if (hitType == HIT_TYPE_NONE) {
                        Log.w(TAG, "No action needed at location (" + absX + ", " + absY + ")");
                    } else if (hitType == HIT_TYPE_MOVE) {
                        handleHitTypeMove(rootView, absX, absY);
                    } else if (hitType == HIT_TYPE_RESIZE_DIAGONAL
                            || hitType == HIT_TYPE_RESIZE_HORIZONTAL
                            || hitType == HIT_TYPE_RESIZE_VERTICAL) {
                        handleHitTypeResize(rootView, hitType, absX, absY);
                    } else {
                        Log.w(TAG, "Unknown hit type (" + hitType + ") at (" + absX + ", " + absY + ")");
                    }
                } else if (mDragging && !mLeftMouseButtonReleased) {
                    int toolType = event.getToolType(event.getActionIndex());
                    if (toolType == MotionEvent.TOOL_TYPE_MOUSE
                            && (event.getButtonState() & MotionEvent.BUTTON_PRIMARY) == 0) {
                        mLeftMouseButtonReleased = true;
                    }
                }
            }
            return true;
        }

        private boolean dragStarted(int x, int y) {
            int dx = x - mTouchDownX;
            int dy = y - mTouchDownY;
            return (dx * dx) + (dy * dy) > mDragSlopSquared;
        }

        private void handleHitTypeMove(View rootView, int absX, int absY) {
            if (rootView.startMovingTask(absX, absY)) {
                Log.d(TAG, "Successfully started moving task at (" + absX + ", " + absY + ")");
                return;
            }
            Log.w(TAG, "Unable to start moving task at (" + absX + ", " + absY + ")");
        }

        private void handleHitTypeResize(View rootView, int hitType, int absX, int absY) {
            int ctrlType = CTRL_TYPE_NONE;
            if (hitType == HIT_TYPE_RESIZE_DIAGONAL || hitType == HIT_TYPE_RESIZE_HORIZONTAL) {
                ctrlType |= (mTouchDownX < rootView.getWidth() / 2)
                        ? CTRL_TYPE_LEFT : CTRL_TYPE_RIGHT;
            }
            if (hitType == HIT_TYPE_RESIZE_DIAGONAL || hitType == HIT_TYPE_RESIZE_VERTICAL) {
                ctrlType |= (mTouchDownY >= rootView.getHeight() / 2)
                        ? CTRL_TYPE_BOTTOM : CTRL_TYPE_TOP;
            }
            Log.d(TAG, "About to start resizing. ctrlType: " + Integer.toHexString(ctrlType));
            if (mArcCompanionHandler == null) {
                Log.e(TAG, "Error on connect to Companion Service");
                return;
            }
            try {
                if (mArcCompanionHandler.startResizingTask(
                        mActivity.getTaskId(), ctrlType, absX, absY)) {
                    Log.i(TAG, "Successfully started resizing task");
                } else {
                    Log.w(TAG, "Unable to start resizing task");
                }
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to start resizing task", e);
            }
        }
    }
}

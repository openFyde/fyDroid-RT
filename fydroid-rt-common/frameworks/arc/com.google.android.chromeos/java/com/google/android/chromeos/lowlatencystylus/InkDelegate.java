/* Open source replacement for com.google.android.chromeos.lowlatencystylus.InkDelegate
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Callback interface used by {@link InkOverlay} to delegate drawing and input
 * forwarding to the client.
 */
public interface InkDelegate {
    Rect onDraw(Canvas canvas);

    Rect onDrawPrediction(Canvas canvas, MotionEvent motionEvent);

    void onTouch(MotionEvent motionEvent);
}

/* Open source replacement for com.google.android.chromeos.lowlatencystylus.BatchedMotionEvent
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus;

import android.view.MotionEvent;
import java.util.Iterator;

/**
 * Wraps a single historical sample of a {@link MotionEvent} so callers can
 * iterate over the whole history (historical samples plus the current sample)
 * as a simple {@link Iterable}.
 */
public class BatchedMotionEvent {
    public final MotionEvent.PointerCoords[] coords;
    public long timeMs;

    private BatchedMotionEvent(int pointerCount) {
        this.coords = new MotionEvent.PointerCoords[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            this.coords[i] = new MotionEvent.PointerCoords();
        }
    }

    public static IterableMotionEvent iterate(MotionEvent ev) {
        return new IterableMotionEvent(ev);
    }

    public static class IterableMotionEvent implements Iterable<BatchedMotionEvent> {
        private final MotionEvent mMotionEvent;
        private final int mPointerCount;

        IterableMotionEvent(MotionEvent motionEvent) {
            this.mMotionEvent = motionEvent;
            this.mPointerCount = motionEvent.getPointerCount();
        }

        @Override
        public Iterator<BatchedMotionEvent> iterator() {
            return new Iterator<BatchedMotionEvent>() {
                private int mHistoryId = 0;

                @Override
                public boolean hasNext() {
                    return this.mHistoryId < IterableMotionEvent.this.mMotionEvent.getHistorySize() + 1;
                }

                @Override
                public BatchedMotionEvent next() {
                    if (this.mHistoryId > IterableMotionEvent.this.mMotionEvent.getHistorySize()) {
                        return null;
                    }
                    BatchedMotionEvent batchedEvent = new BatchedMotionEvent(IterableMotionEvent.this.mPointerCount);
                    if (this.mHistoryId < IterableMotionEvent.this.mMotionEvent.getHistorySize()) {
                        for (int pointerId = 0; pointerId < IterableMotionEvent.this.mPointerCount; pointerId++) {
                            IterableMotionEvent.this.mMotionEvent.getHistoricalPointerCoords(
                                    pointerId, this.mHistoryId, batchedEvent.coords[pointerId]);
                        }
                        batchedEvent.timeMs = IterableMotionEvent.this.mMotionEvent.getHistoricalEventTime(this.mHistoryId);
                    } else {
                        for (int pointerId = 0; pointerId < IterableMotionEvent.this.mPointerCount; pointerId++) {
                            IterableMotionEvent.this.mMotionEvent.getPointerCoords(pointerId, batchedEvent.coords[pointerId]);
                        }
                        batchedEvent.timeMs = IterableMotionEvent.this.mMotionEvent.getEventTime();
                    }
                    this.mHistoryId++;
                    return batchedEvent;
                }
            };
        }
    }
}

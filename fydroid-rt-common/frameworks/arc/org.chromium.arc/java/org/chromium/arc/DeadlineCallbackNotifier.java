package org.chromium.arc;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DeadlineCallbackNotifier<T> implements Cancellable {
    private static final String TAG = "DeadlineCallbackNotifier";

    private final ExactlyOnceCallback<T> mCallback;
    private boolean mCallbackCompleted = false;
    private CountDownTimer mCallbackTimer;

    public DeadlineCallbackNotifier(ExactlyOnceCallback<T> callback) {
        this.mCallback = callback;
    }

    private synchronized boolean markCompleted() {
        if (this.mCallbackCompleted) {
            return false;
        }
        this.mCallbackCompleted = true;
        if (this.mCallbackTimer != null) {
            this.mCallbackTimer.cancel();
            this.mCallbackTimer = null;
        }
        return true;
    }

    private void setDeadlineOnLooper(long deadlineMillis) {
        if (this.mCallbackCompleted) {
            Log.e(TAG, "Cannot set timeout after being completed.");
            return;
        }
        if (this.mCallbackTimer != null) {
            Log.e(TAG, "Cannot set timeout twice.");
            return;
        }
        long remaining = deadlineMillis - System.currentTimeMillis();
        if (remaining <= 0) {
            completeExceptionally(new TimeoutException());
            return;
        }
        CountDownTimer timer = new CountDownTimer(remaining, remaining) {
            @Override
            public void onFinish() {
                DeadlineCallbackNotifier.this.completeExceptionally(new TimeoutException());
            }

            @Override
            public void onTick(long millisUntilFinished) {
            }
        };
        this.mCallbackTimer = timer;
        timer.start();
    }

    public void addCancellationRelay(CancellationRelay cancellationRelay) {
        if (!cancellationRelay.addListener(this)) {
            cancel(true);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completeExceptionally(new CancellationException());
    }

    public boolean complete(T value) {
        if (!markCompleted()) {
            return false;
        }
        this.mCallback.onSuccess(value);
        return true;
    }

    public boolean completeExceptionally(Throwable th) {
        if (!markCompleted()) {
            return false;
        }
        this.mCallback.onFailure(th);
        return true;
    }

    public void setDeadline(Handler handler, long timeout, TimeUnit unit) {
        final long deadlineMillis = unit.toMillis(timeout) + System.currentTimeMillis();
        if (Looper.myLooper() != handler.getLooper()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DeadlineCallbackNotifier.this.setDeadlineOnLooper(deadlineMillis);
                }
            });
        } else {
            setDeadlineOnLooper(deadlineMillis);
        }
    }
}

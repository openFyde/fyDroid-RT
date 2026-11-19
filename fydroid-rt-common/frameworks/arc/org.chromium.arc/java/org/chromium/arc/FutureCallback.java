package org.chromium.arc;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.chromium.mojo.bindings.Callbacks;

public class FutureCallback<V> implements Cancellable, Future<V>, Callbacks.Callback1<V> {
    private boolean mCancelled;
    private final Condition mCondition;
    private boolean mDone;
    private final ReentrantLock mLock;
    private V mValue = null;

    public FutureCallback(CancellationRelay cancellationRelay) {
        this.mDone = false;
        this.mCancelled = false;
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mLock = reentrantLock;
        this.mCondition = reentrantLock.newCondition();
        if (!cancellationRelay.addListener(this)) {
            this.mCancelled = true;
            this.mDone = true;
        }
    }

    public void call(V v) {
        set(v);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.mLock.lock();
        try {
            if (this.mDone) {
                return false;
            }
            this.mCancelled = true;
            this.mDone = true;
            this.mCondition.signalAll();
            return true;
        } finally {
            this.mLock.unlock();
        }
    }

    @Override
    public V get() throws ExecutionException, InterruptedException {
        this.mLock.lockInterruptibly();
        try {
            while (!this.mDone) {
                this.mCondition.await();
            }
            if (this.mCancelled) {
                throw new CancellationException();
            }
            return this.mValue;
        } finally {
            this.mLock.unlock();
        }
    }

    @Override
    public V get(long timeout, TimeUnit unit)
            throws ExecutionException, InterruptedException, TimeoutException {
        long nanos = unit.toNanos(timeout);
        this.mLock.lockInterruptibly();
        try {
            while (!this.mDone) {
                if (nanos > 0) {
                    nanos = this.mCondition.awaitNanos(nanos);
                } else {
                    throw new TimeoutException();
                }
            }
            if (this.mCancelled) {
                throw new CancellationException();
            }
            return this.mValue;
        } finally {
            this.mLock.unlock();
        }
    }

    @Override
    public boolean isCancelled() {
        this.mLock.lock();
        try {
            return this.mCancelled;
        } finally {
            this.mLock.unlock();
        }
    }

    @Override
    public boolean isDone() {
        this.mLock.lock();
        try {
            return this.mDone;
        } finally {
            this.mLock.unlock();
        }
    }

    public boolean set(V v) {
        this.mLock.lock();
        try {
            if (this.mDone) {
                return false;
            }
            this.mValue = v;
            this.mDone = true;
            this.mCondition.signalAll();
            return true;
        } finally {
            this.mLock.unlock();
        }
    }
}

package org.chromium.arc;

import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * Relays cancellation to all registered listeners.
 * Uses WeakHashMap so listeners are automatically removed when GC'd.
 */
public class CancellationRelay {
    private boolean mCancelled = false;
    private final WeakHashMap<Cancellable, Object> mListeners = new WeakHashMap<>();

    public boolean addListener(Cancellable cancellable) {
        synchronized (mListeners) {
            if (mCancelled) {
                return false;
            }
            mListeners.put(cancellable, null);
            return true;
        }
    }

    public void cancel(boolean mayInterruptIfRunning) {
        synchronized (mListeners) {
            mCancelled = true;
            Iterator<Cancellable> it = mListeners.keySet().iterator();
            while (it.hasNext()) {
                it.next().cancel(mayInterruptIfRunning);
            }
            mListeners.clear();
        }
    }

    public void removeListener(Cancellable cancellable) {
        synchronized (mListeners) {
            mListeners.remove(cancellable);
        }
    }
}

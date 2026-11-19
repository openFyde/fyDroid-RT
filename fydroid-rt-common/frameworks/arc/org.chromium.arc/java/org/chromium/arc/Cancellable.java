package org.chromium.arc;

/**
 * Interface for operations that can be cancelled.
 */
public interface Cancellable {
    boolean cancel(boolean mayInterruptIfRunning);
}

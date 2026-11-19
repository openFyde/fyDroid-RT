package org.chromium.arc;

public interface ExactlyOnceCallback<T> {
    void onFailure(Throwable th);
    void onSuccess(T t);
}

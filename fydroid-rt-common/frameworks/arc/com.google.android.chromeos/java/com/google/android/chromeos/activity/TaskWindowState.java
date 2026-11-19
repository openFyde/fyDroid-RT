/* Open source replacement for com.google.android.chromeos.activity.TaskWindowState from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.activity;

import com.google.android.chromeos.ChromeOsSharedLib;

/**
 * Value holder for a task window state int, returned by
 * {@link ChromeOsTaskManagement#getTaskWindowState()}. Wraps the raw int so
 * that callers can query it through {@link #getWindowState()} and get a
 * readable {@link #toString()}.
 *
 * Not Parcelable and not an annotation interface — it's a plain data class
 * in the proprietary jar, and we reproduce it verbatim.
 */
public class TaskWindowState {
    private int mWindowState;

    TaskWindowState(int windowState) {
        this.mWindowState = windowState;
    }

    public int getWindowState() {
        return this.mWindowState;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof TaskWindowState)) {
            return false;
        }
        TaskWindowState other = (TaskWindowState) otherObj;
        return other.mWindowState == this.mWindowState;
    }

    @Override
    public int hashCode() {
        return this.mWindowState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TaskWindowState{");
        sb.append("windowState=(");
        sb.append(this.mWindowState);
        sb.append(")");
        if (ChromeOsSharedLib.isFlagSet(
                this.mWindowState, ChromeOsTaskManagement.WINDOW_STATE_APP_MANAGED)) {
            sb.append("App Managed, ");
        }
        int masked = this.mWindowState & (-257);
        if (masked == ChromeOsTaskManagement.WINDOW_STATE_NORMAL) {
            sb.append("Normal");
        } else if (masked == ChromeOsTaskManagement.WINDOW_STATE_MAXIMIZED) {
            sb.append("Maximized");
        } else if (masked == ChromeOsTaskManagement.WINDOW_STATE_MINIMIZED) {
            sb.append("Minimized");
        } else if (masked == ChromeOsTaskManagement.WINDOW_STATE_ALWAYS_ON_TOP) {
            sb.append("Always On Top");
        }
        sb.append('}');
        return sb.toString();
    }
}

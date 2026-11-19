package com.android.server.wm;

oneway interface ITaskOrientationListener {
    void onTaskOrientationChanged(int taskId, int orientation);
}

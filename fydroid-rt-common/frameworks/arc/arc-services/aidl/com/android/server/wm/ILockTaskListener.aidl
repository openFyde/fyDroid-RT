package com.android.server.wm;

oneway interface ILockTaskListener {
    void onLockTaskModeChanged(int mode, int userId);
}

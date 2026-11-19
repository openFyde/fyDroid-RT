package com.android.server.arc.net;

interface IArcHostVpnServiceCallback {
    void onRevoke();
    void onDisconnect();
}

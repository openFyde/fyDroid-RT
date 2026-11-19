package com.android.server.arc.net;

import android.net.wifi.IWifiScanner;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ServiceManager;
import android.util.Slog;

import com.android.internal.util.AsyncChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal IWifiScanner stub for "wifiscanner" binder service.
 * Most scan scheduling is handled Chrome-side.
 */
class ArcWifiScannerService extends IWifiScanner.Stub {
    private static final String TAG = "ArcWifiScanner";

    private HandlerThread mThread;
    private Messenger mMessenger;

    void register() {
        // getMessenger() must never return null: WifiScanner's *constructor*
        // throws IllegalStateException("getMessenger() returned null!") on one,
        // so a null here kills every process that calls
        // getSystemService(WifiScanner.class) — it never gets the chance to
        // handle the null itself. GMS Nearby Connections does exactly that on
        // bind, which crash-looped com.google.android.gms.persistent a couple of
        // times a second and evicted the whole log buffer.
        mThread = new HandlerThread("ArcWifiScanner");
        mThread.start();
        mMessenger = new Messenger(new ScannerHandler(mThread.getLooper()));

        ServiceManager.addService("wifiscanner", this);
        Slog.i(TAG, "Registered 'wifiscanner' binder service");
    }

    @Override
    public Messenger getMessenger() {
        return mMessenger;
    }

    @Override
    public Bundle getAvailableChannels(int band, String packageName, String featureId) {
        // Return empty bundle — no channel info available
        return new Bundle();
    }

    @Override
    public boolean isScanning() {
        // fydroid-rt (AOSP-13): new IWifiScanner method — scans are Chrome-driven.
        return false;
    }

    /**
     * Answers the AsyncChannel handshake so WifiScanner can be constructed, and
     * drops every scan request. Scanning is Chrome-side, so there is nothing to
     * answer with.
     *
     * Ideally a request would be failed back with CMD_OP_FAILED so the caller's
     * ActionListener.onFailure() fires instead of never being called. That needs
     * WifiScanner.CMD_OP_FAILED and WifiScanner.OperationResult, both @hide and
     * only exported by framework-wifi-pre-jarjar, whose visibility allowlist does
     * not include device/fydeos — reachable only by patching the Wifi module.
     * Not worth it for a stub's error path: a dropped request means a listener
     * that is never called, which is survivable, unlike the crash this replaced.
     */
    private static final class ScannerHandler extends Handler {
        // One channel per connected client, keyed by its messenger — same shape as
        // WifiScanningServiceImpl.ClientHandler.mClients.
        private final Map<Messenger, AsyncChannel> mClients = new HashMap<>();

        ScannerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.replyTo == null) return;

            switch (msg.what) {
                case AsyncChannel.CMD_CHANNEL_FULL_CONNECTION: {
                    AsyncChannel channel = mClients.get(msg.replyTo);
                    if (channel != null) {
                        channel.replyToMessage(msg, AsyncChannel.CMD_CHANNEL_FULLY_CONNECTED,
                                AsyncChannel.STATUS_FULL_CONNECTION_REFUSED_ALREADY_CONNECTED);
                        return;
                    }
                    channel = new AsyncChannel();
                    channel.connected(null, this, msg.replyTo);
                    mClients.put(msg.replyTo, channel);
                    channel.replyToMessage(msg, AsyncChannel.CMD_CHANNEL_FULLY_CONNECTED,
                            AsyncChannel.STATUS_SUCCESSFUL);
                    return;
                }
                case AsyncChannel.CMD_CHANNEL_DISCONNECT:
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED:
                    mClients.remove(msg.replyTo);
                    return;
                default:
                    // Scan request — nothing to scan with. See the class comment.
                    return;
            }
        }
    }
}

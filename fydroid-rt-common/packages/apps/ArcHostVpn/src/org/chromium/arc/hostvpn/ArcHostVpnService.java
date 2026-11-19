package org.chromium.arc.hostvpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import com.android.server.arc.net.IArcHostVpnService;
import com.android.server.arc.net.IArcHostVpnServiceCallback;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public class ArcHostVpnService extends VpnService {
    private static final String TAG = "ArcHostVpnService";
    private static final int RECONNECT_DELAY_MS = 100;

    static {
        System.loadLibrary("archostvpn_jni");
    }

    private Handler mMainHandler;
    private HandlerThread mMainThread;
    private HandlerThread mTunToHostThread;
    private HandlerThread mHostToTunThread;

    private IArcHostVpnServiceCallback mCallback;
    private ParcelFileDescriptor mTunFd;
    private final Object mLock = new Object();
    private final AtomicInteger mForwardingThreadCount = new AtomicInteger(0);

    // Current connection parameters for reconnect
    private VpnConfig mCurrentConfig;
    private boolean mConnected;

    private static native void nativeForwardTunToHost(Object lock, int tunFd,
            int socketFd, int protocol, int ifindex, byte[] hostMacAddress);
    private static native void nativeForwardHostToTun(Object lock, int tunFd,
            int socketFd, int ifindex);
    private static native void nativeSignalBlockedThreads(Object lock);

    @Override
    public void onCreate() {
        super.onCreate();
        mMainThread = new HandlerThread("ArcHostVpn.Main");
        mMainThread.start();
        mMainHandler = new Handler(mMainThread.getLooper());

        mTunToHostThread = new HandlerThread("ArcHostVpn.TunToHost");
        mTunToHostThread.start();

        mHostToTunThread = new HandlerThread("ArcHostVpn.HostToTun");
        mHostToTunThread.start();

        Log.i(TAG, "ArcHostVpnService created");
    }

    @Override
    public void onDestroy() {
        handleDisconnect();
        mTunToHostThread.quitSafely();
        mHostToTunThread.quitSafely();
        mMainThread.quitSafely();
        super.onDestroy();
        Log.i(TAG, "ArcHostVpnService destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if ("android.net.VpnService".equals(intent.getAction())) {
            return mBinder;
        }
        return mBinder;
    }

    @Override
    public void onRevoke() {
        Log.i(TAG, "VPN revoked by system");
        mMainHandler.post(() -> {
            if (mCallback != null) {
                try {
                    mCallback.onRevoke();
                } catch (RemoteException e) {
                    Log.w(TAG, "Failed to notify callback onRevoke", e);
                }
            }
            handleDisconnect();
        });
    }

    private void handleConnect(VpnConfig config) {
        if (mConnected) {
            Log.i(TAG, "Reconnecting with delay");
            handleDisconnect();
            mCurrentConfig = config;
            mMainHandler.postDelayed(() -> handleSetUpVpnService(config),
                    RECONNECT_DELAY_MS);
            return;
        }
        mCurrentConfig = config;
        handleSetUpVpnService(config);
    }

    private void handleSetUpVpnService(VpnConfig config) {
        Builder builder = new Builder();

        // Add addresses
        for (String address : config.addresses) {
            try {
                String[] parts = address.split("/");
                InetAddress addr = InetAddress.getByName(parts[0]);
                int prefixLen = (parts.length > 1) ? Integer.parseInt(parts[1]) : 32;
                builder.addAddress(addr, prefixLen);
            } catch (UnknownHostException e) {
                Log.e(TAG, "Invalid address: " + address, e);
            }
        }

        // Add routes
        for (String route : config.routes) {
            try {
                String[] parts = route.split("/");
                InetAddress addr = InetAddress.getByName(parts[0]);
                int prefixLen = (parts.length > 1) ? Integer.parseInt(parts[1]) : 32;
                builder.addRoute(addr, prefixLen);
            } catch (UnknownHostException e) {
                Log.e(TAG, "Invalid route: " + route, e);
            }
        }

        // Add DNS servers
        for (String dns : config.dnsServers) {
            try {
                builder.addDnsServer(InetAddress.getByName(dns));
            } catch (UnknownHostException e) {
                Log.e(TAG, "Invalid DNS server: " + dns, e);
            }
        }

        if (config.mtu > 0) {
            builder.setMtu(config.mtu);
        }

        builder.setBlocking(true);

        mTunFd = builder.establish();
        if (mTunFd == null) {
            Log.e(TAG, "Failed to establish VPN");
            return;
        }

        mConnected = true;
        mForwardingThreadCount.set(2);

        final int tunFd = mTunFd.getFd();
        final int socketFd = config.socketFd;
        final int protocol = config.protocol;
        final int ifindex = config.ifindex;
        final byte[] hostMac = config.hostMacAddress;

        // Start TUN-to-host forwarding
        new Handler(mTunToHostThread.getLooper()).post(() -> {
            Log.i(TAG, "TUN→Host forwarding started");
            nativeForwardTunToHost(mLock, tunFd, socketFd, protocol, ifindex, hostMac);
            Log.i(TAG, "TUN→Host forwarding stopped");
            notifyForwardingStopped();
        });

        // Start host-to-TUN forwarding
        new Handler(mHostToTunThread.getLooper()).post(() -> {
            Log.i(TAG, "Host→TUN forwarding started");
            nativeForwardHostToTun(mLock, tunFd, socketFd, ifindex);
            Log.i(TAG, "Host→TUN forwarding stopped");
            notifyForwardingStopped();
        });

        Log.i(TAG, "VPN connected, forwarding active");
    }

    private void notifyForwardingStopped() {
        if (mForwardingThreadCount.decrementAndGet() == 0) {
            mMainHandler.post(() -> {
                Log.i(TAG, "Both forwarding threads stopped, cleaning up");
                handleDisconnect();
                if (mCallback != null) {
                    try {
                        mCallback.onDisconnect();
                    } catch (RemoteException e) {
                        Log.w(TAG, "Failed to notify callback onDisconnect", e);
                    }
                }
            });
        }
    }

    private void handleDisconnect() {
        if (!mConnected) {
            return;
        }
        mConnected = false;

        // Signal native threads to stop blocking reads
        nativeSignalBlockedThreads(mLock);

        // Close TUN fd
        if (mTunFd != null) {
            try {
                mTunFd.close();
            } catch (IOException e) {
                Log.w(TAG, "Error closing TUN fd", e);
            }
            mTunFd = null;
        }

        mCurrentConfig = null;
        Log.i(TAG, "VPN disconnected");
    }

    private final IArcHostVpnService.Stub mBinder = new IArcHostVpnService.Stub() {
        @Override
        public void connect(String[] addresses, String[] routes, String[] dnsServers,
                int mtu, int socketFd, byte[] hostMacAddress, int ifindex, int protocol) {
            VpnConfig config = new VpnConfig();
            config.addresses = addresses;
            config.routes = routes;
            config.dnsServers = dnsServers;
            config.mtu = mtu;
            config.socketFd = socketFd;
            config.hostMacAddress = hostMacAddress;
            config.ifindex = ifindex;
            config.protocol = protocol;

            mMainHandler.post(() -> handleConnect(config));
        }

        @Override
        public void disconnect() {
            mMainHandler.post(() -> handleDisconnect());
        }

        @Override
        public void registerCallback(IArcHostVpnServiceCallback callback) {
            mCallback = callback;
        }
    };

    private static class VpnConfig {
        String[] addresses;
        String[] routes;
        String[] dnsServers;
        int mtu;
        int socketFd;
        byte[] hostMacAddress;
        int ifindex;
        int protocol;
    }
}

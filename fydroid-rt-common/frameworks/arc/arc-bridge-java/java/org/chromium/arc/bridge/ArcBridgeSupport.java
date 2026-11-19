package org.chromium.arc.bridge;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.base.ContextUtils;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.bindings.ExceptionHandler;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.mojo.system.impl.CoreImpl;

/**
 * Bootstrap class for the ARC bridge Mojo connection.
 *
 * Loads libarcbridgeservice.so which connects to the arcbridge HIDL service,
 * bootstraps Mojo IPC, and delivers the ArcBridgeHost proxy to registered
 * ArcService instances.
 *
 * Singleton — use getInstance().
 *
 * Reconnect behavior: when the bridge host Mojo pipe drops (Chrome restart,
 * vendor service reconnect, etc.), the JNI side re-runs its bootstrap and
 * calls onMessagePipeReady() with a fresh pipe. Registered ArcServices keep
 * their registration across reconnects, so onBridgeReady is re-fired on each
 * fresh pipe — services that simply re-register their Mojo instance on
 * onBridgeReady (the normal pattern) heal automatically.
 */
public class ArcBridgeSupport {
    private static final String TAG = "ArcBridgeSupport";

    private final HashMap<ArcService, Integer> mServices = new HashMap<>();
    private ArcBridgeHost mBridgeHost = null;
    private int mBridgeHostVersion = -1;
    private boolean mContextInitialized = false;
    private final long mPtr;

    private static class LazyHolder {
        private static final ArcBridgeSupport INSTANCE = new ArcBridgeSupport();
    }

    static {
        System.loadLibrary("arcbridgeservice");
    }

    private ArcBridgeSupport() {
        ExceptionHandler.DefaultExceptionHandler.getInstance()
            .setDelegate(ArcExceptionHandler.getInstance());
        mPtr = nativeInit(this);
    }

    public static ArcBridgeSupport getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Registers a service to receive the ArcBridgeHost proxy.
     * If the bridge is already connected and the version is compatible,
     * onBridgeReady is called on the Mojo thread.
     */
    public void requestArcBridgeProxyAsync(Context context, ArcService service,
            int minVersion) {
        synchronized (this) {
            if (!mContextInitialized && context.getPackageManager() != null) {
                ContextUtils.initApplicationContext(context);
                nativeBuildInfoInit(mPtr);
                mContextInitialized = true;
            }
            if (mServices.containsKey(service)) {
                return;
            }
            mServices.put(service, Integer.valueOf(minVersion));
            if (mBridgeHost == null) {
                return;
            }
            runOnMojoThread(new Runnable() {
                @Override
                public void run() {
                    notifyServices();
                }
            });
        }
    }

    /**
     * Wraps a Mojo interface proxy as a thread-safe proxy and queries its version.
     * The BiConsumer receives the thread-safe proxy and its version.
     */
    @SuppressWarnings("unchecked")
    public static <HostT extends Interface> void buildThreadSafeProxy(
            HostT host, Interface.Manager manager,
            final ConnectionErrorHandler errorHandler,
            final BiConsumer<HostT, Integer> callback) {
        final Interface.Proxy proxy = manager.buildThreadSafeProxy(
                (Interface.Proxy) host);
        proxy.getProxyHandler().queryVersion(new Callbacks.Callback1<Integer>() {
            @Override
            public void call(Integer version) {
                if (errorHandler != null) {
                    proxy.getProxyHandler().setErrorHandler(errorHandler);
                }
                callback.accept((HostT) proxy, version);
            }
        });
    }

    /**
     * Instance method wrapper for buildThreadSafeProxy.
     * Called from native (libarcbridgeservice.so) via JNI.
     */
    @SuppressWarnings("unused") // Called from native
    public <HostT extends Interface> void buildThreadSafeProxyNonStatic(
            HostT host, Interface.Manager manager,
            ConnectionErrorHandler errorHandler,
            BiConsumer<HostT, Integer> callback) {
        buildThreadSafeProxy(host, manager, errorHandler, callback);
    }

    /**
     * Posts a Runnable to the Mojo IO thread.
     */
    public void runOnMojoThread(Runnable runnable) {
        nativeRunOnMojoThread(mPtr, runnable);
    }

    /**
     * Converts a file descriptor to a Mojo handle.
     */
    public UntypedHandle convertFileDescriptorToMojoHandle(int fd) {
        return CoreImpl.getInstance().acquireNativeHandle(
                nativeConvertFileDescriptorToMojoHandle(mPtr, fd));
    }

    /**
     * Converts a Mojo handle to a file descriptor.
     */
    public int convertMojoHandleToFileDescriptor(UntypedHandle handle) {
        return nativeConvertMojoHandleToFileDescriptor(mPtr,
                handle.releaseNativeHandle());
    }

    /**
     * Called from native (ArcBridgeSupportJni.cpp) when the Mojo bootstrap
     * completes and the ArcBridgeHost message pipe is ready.
     *
     * Note: an earlier iteration installed a ConnectionErrorHandler on the
     * bridge-host pipe to auto-re-bootstrap on disconnect. That looped:
     * `buildThreadSafeProxy` (or the initial Mojo handshake) generates a
     * transient "pipe replaced" close event on the original proxy soon
     * after it's wrapped, which the handler interpreted as a real
     * disconnect — triggering nativeBootstrap, a new pipe, another
     * transient close, and so on every ~10 ms while system_server's
     * `mServices` re-fired `onBridgeReady` for every consumer each round.
     * The vendor-side daemon already retries its own root_host connection
     * (jittered exp backoff + reconnect); individual Java services that
     * need to survive a bridge drop should re-register themselves from
     * their per-instance onConnectionError handlers.
     */
    @SuppressWarnings("unused") // Called from native
    private void onMessagePipeReady(MessagePipeHandle messagePipeHandle,
            final int version) {
        Interface.Manager<ArcBridgeHost, ArcBridgeHost.Proxy> manager =
                ArcBridgeHost.MANAGER;
        buildThreadSafeProxy(
                manager.attachProxy(messagePipeHandle, 0),
                manager, null,
                new BiConsumer<ArcBridgeHost, Integer>() {
                    @Override
                    public void accept(ArcBridgeHost host, Integer num) {
                        synchronized (ArcBridgeSupport.this) {
                            mBridgeHost = host;
                            mBridgeHostVersion = version;
                        }
                        notifyServices();
                    }
                });
    }

    private void notifyServices() {
        HashMap<ArcService, Integer> snapshot = new HashMap<>();
        synchronized (this) {
            snapshot.putAll(mServices);
            // Clear after snapshot — see onMessagePipeReady. Preserving
            // services across the call meant that any later
            // requestArcBridgeProxyAsync (e.g. a fresh app registering)
            // would re-fire onBridgeReady for every pre-existing service
            // and walk into the duplicate-registration warning path.
            mServices.clear();
        }
        for (Map.Entry<ArcService, Integer> entry : snapshot.entrySet()) {
            if (mBridgeHostVersion >= entry.getValue()) {
                entry.getKey().onBridgeReady(mBridgeHost);
            } else {
                entry.getKey().onBridgeVersionMismatch(mBridgeHostVersion);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            nativeDispose(mPtr);
        } finally {
            super.finalize();
        }
    }

    // Native methods — implemented in ArcBridgeSupportJni.cpp
    private static native long nativeInit(ArcBridgeSupport instance);
    private static native void nativeBuildInfoInit(long ptr);
    private static native int nativeConvertFileDescriptorToMojoHandle(long ptr, int fd);
    private static native int nativeConvertMojoHandleToFileDescriptor(long ptr, int handle);
    private static native void nativeDispose(long ptr);
    private static native void nativeRunOnMojoThread(long ptr, Runnable runnable);
    // Re-runs the HIDL getMojoBootstrapChannel + Mojo invitation accept path
    // on the Mojo thread. Called from the bridge-host pipe error handler
    // when the vendor service drops the root Mojo connection.
    private static native void nativeBootstrap(long ptr);
}

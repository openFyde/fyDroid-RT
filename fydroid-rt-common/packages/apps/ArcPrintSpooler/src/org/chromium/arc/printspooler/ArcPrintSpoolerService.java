package org.chromium.arc.printspooler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.internal.annotations.GuardedBy;

import org.chromium.arc.CancellationRelay;
import org.chromium.arc.FutureCallback;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.PrintSessionHost;
import org.chromium.arc.mojom.PrintSessionInstance;
import org.chromium.arc.mojom.PrintSpoolerHost;
import org.chromium.arc.mojom.PrintSpoolerInstance;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.UntypedHandle;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Binder for ArcPrintActivity, bound for the length of a print dialog, in front
 * of the process-wide PrintSpoolerInstance for Chrome.
 *
 * The instance registers with the ARC bridge the first time a dialog binds, and
 * Chrome's ArcPrintSpoolerBridge answers with Init(). It stays registered for
 * the life of the process: the Mojo bindings give no way to close an instance
 * pipe once it is passed to the bridge, and Chrome drops a second instance
 * while the first is still connected, so a per-dialog instance only worked for
 * the first print after the spooler process started. It registers again only
 * after Chrome closes the pipe (e.g. Chrome restarted).
 */
public class ArcPrintSpoolerService extends Service {
    private static final String TAG = ArcPrintSpoolerService.class.getSimpleName();

    public static final String ACTION_SERVICE_CONNECTED_TO_HOST =
            "org.chromium.arc.printspooler.SERVICE_CONNECTED_TO_HOST";

    private static final int MIN_BRIDGE_VERSION = 45;
    // PrintSpoolerHost.StartPrintInCustomTab.
    private static final int MIN_HOST_VERSION_CUSTOM_TAB = 4;
    // Chrome saves the document and opens the preview before it replies.
    private static final long START_PRINT_TIMEOUT_SECONDS = 20;

    private static final HostConnection sHostConnection = new HostConnection();

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public ArcPrintSpoolerService getService() {
            return ArcPrintSpoolerService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Starting service.");
        super.onCreate();
        sHostConnection.connect(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service.");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean isConnectedToHost() {
        return sHostConnection.isConnected();
    }

    /**
     * Opens the PDF behind |fd| in Chrome print preview over the ARC window of
     * |taskId|. Takes ownership of |fd|. Blocks until Chrome answers; returns
     * null if Chrome is not connected or could not open the preview.
     */
    public PrintSessionHost startPrintInCustomTab(int fd, int taskId,
            PrintSessionInstance printSession) {
        final PrintSpoolerHost host;
        final FutureCallback<PrintSessionHost> callback;
        synchronized (sHostConnection) {
            if (sHostConnection.mHost == null
                    || sHostConnection.mHostVersion < MIN_HOST_VERSION_CUSTOM_TAB) {
                Log.w(TAG, "Chrome cannot print in a custom tab, host version "
                        + sHostConnection.mHostVersion);
                return null;
            }
            host = sHostConnection.mHost;
            callback = new FutureCallback<>(sHostConnection.mCancellationRelay);
        }
        final UntypedHandle handle =
                ArcBridgeSupport.getInstance().convertFileDescriptorToMojoHandle(fd);
        ArcBridgeSupport.getInstance().runOnMojoThread(
                () -> host.startPrintInCustomTab(handle, taskId, printSession, callback::call));
        try {
            return callback.get(START_PRINT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | CancellationException | ExecutionException
                | TimeoutException e) {
            Log.e(TAG, "StartPrintInCustomTab failed.", e);
            return null;
        }
    }

    /** The PrintSpoolerInstance Chrome talks to; one per process. */
    private static final class HostConnection implements ArcService, PrintSpoolerInstance {
        private Context mContext;
        // Registered with the bridge and not yet closed by Chrome.
        @GuardedBy("this")
        private boolean mRegistered;
        @GuardedBy("this")
        PrintSpoolerHost mHost;
        @GuardedBy("this")
        int mHostVersion = -1;
        // Cancels calls still waiting on a host that went away. Replaced for
        // every new host, since a cancelled relay stays cancelled.
        @GuardedBy("this")
        CancellationRelay mCancellationRelay = new CancellationRelay();

        void connect(Context context) {
            synchronized (this) {
                if (mRegistered) {
                    return;
                }
                mRegistered = true;
                mContext = context;
            }
            ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                    context, this, MIN_BRIDGE_VERSION);
        }

        synchronized boolean isConnected() {
            return mHost != null;
        }

        // ArcService

        @Override
        public void onBridgeReady(ArcBridgeHost arcBridgeHost) {
            Log.i(TAG, "Bridge ready.");
            arcBridgeHost.onPrintSpoolerInstanceReady(this);
        }

        @Override
        public void onBridgeVersionMismatch(int version) {
            Log.e(TAG, "Bridge version mismatch: " + version);
            synchronized (this) {
                mRegistered = false;
            }
        }

        // PrintSpoolerInstance

        @Override
        public void init(PrintSpoolerHost printSpoolerHost, final InitResponse initResponse) {
            Log.i(TAG, "Host init.");
            ArcBridgeSupport.buildThreadSafeProxy(printSpoolerHost, PrintSpoolerHost.MANAGER, this,
                    (hostProxy, version) -> {
                        Log.i(TAG, "Host version: " + version);
                        synchronized (HostConnection.this) {
                            mHost = hostProxy;
                            mHostVersion = version;
                            mCancellationRelay = new CancellationRelay();
                        }
                        mContext.sendBroadcast(new Intent(ACTION_SERVICE_CONNECTED_TO_HOST)
                                .setPackage(mContext.getPackageName()));
                        initResponse.call();
                    });
        }

        @Override
        public void close() {
            Log.i(TAG, "Mojo connection closed.");
            onHostClosed();
        }

        @Override
        public void onConnectionError(MojoException e) {
            Log.e(TAG, "Mojo connection error.", e);
            onHostClosed();
        }

        // Chrome dropped the instance or the host; the next dialog registers
        // again.
        private void onHostClosed() {
            PrintSpoolerHost host;
            synchronized (this) {
                mCancellationRelay.cancel(false);
                host = mHost;
                mHost = null;
                mHostVersion = -1;
                mRegistered = false;
            }
            if (host != null) {
                host.close();
            }
        }
    }
}

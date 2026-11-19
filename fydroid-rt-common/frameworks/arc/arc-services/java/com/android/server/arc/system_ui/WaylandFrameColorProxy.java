package com.android.server.arc.system_ui;

import android.os.IBinder;
import android.os.Parcel;
import android.os.ServiceManager;
import android.util.Slog;

/**
 * Pushes the derived ChromeOS header colours to SurfaceFlinger's wayland
 * service, which forwards them to ash as zaura_surface.set_frame_colors so
 * every ARC caption is painted like a Chrome window's title bar.
 *
 * Only the two colours travel: the wayland service owns which surfaces get
 * them and when, because it is the only side that knows what windows exist.
 *
 * Transaction code matches WaylandBinderService.h on the native side.
 */
final class WaylandFrameColorProxy implements ArcSystemUiService.PaletteListener {
    private static final String TAG = "ArcFrameColor";
    private static final String SERVICE_NAME = "Wayland";
    private static final String DESCRIPTOR = "org.chromium.arc.wayland.IWaylandService";
    private static final int TRANSACTION_setFrameColors = 51;

    private IBinder mService;
    // Last pair actually sent, so a scheme flip that resolves to the same
    // colours (or Chrome re-sending its state on connect) costs nothing.
    private int mSentActive;
    private int mSentInactive;
    private boolean mHasSent;

    @Override
    public void onPaletteChanged() {
        push();
    }

    /**
     * Sends the current header colours. Called on the Mojo thread, so the
     * transaction is one-way: the wayland service hands the colours to its
     * event thread and applies them there, and nothing here needs the result.
     */
    synchronized void push() {
        final boolean dark = ArcSystemUiService.isDarkTheme();
        final int active = ArcSystemUiService.header(dark);
        final int inactive = ArcSystemUiService.headerUnfocused(dark);

        IBinder service = mService;
        if (service == null || !service.isBinderAlive()) {
            // checkService, not getService: this runs on the Mojo thread and
            // getService blocks for seconds while SurfaceFlinger is still
            // coming up. A miss is not fatal — Chrome pushes its palette again
            // on the next connect, and a window created later reads the
            // colours from the wayland service directly.
            service = ServiceManager.checkService(SERVICE_NAME);
            mService = service;
            if (service == null) {
                Slog.w(TAG, "Wayland service not available yet");
                return;
            }
            // A different SurfaceFlinger than the one we last sent to holds no
            // colours at all, so the dedupe below must not suppress this push:
            // it would leave every caption on ash's default for the rest of
            // the session, since nothing else re-sends them.
            mHasSent = false;
        }

        if (mHasSent && active == mSentActive && inactive == mSentInactive) {
            return;
        }

        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(active);
            data.writeInt(inactive);
            service.transact(TRANSACTION_setFrameColors, data, null,
                    IBinder.FLAG_ONEWAY);
            mSentActive = active;
            mSentInactive = inactive;
            mHasSent = true;
            Slog.i(TAG, String.format("Frame colours -> wayland: active=#%06X "
                    + "inactive=#%06X (%s)", active & 0xFFFFFF,
                    inactive & 0xFFFFFF, dark ? "dark" : "light"));
        } catch (Exception e) {
            // Drop the handle so the next push re-resolves it — this is what a
            // SurfaceFlinger restart looks like from here.
            mService = null;
            Slog.e(TAG, "Failed to push frame colours", e);
        } finally {
            data.recycle();
        }
    }
}

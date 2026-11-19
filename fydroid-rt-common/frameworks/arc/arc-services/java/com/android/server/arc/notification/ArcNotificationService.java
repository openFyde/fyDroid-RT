package com.android.server.arc.notification;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.util.Slog;

import com.android.server.SystemService;

import org.chromium.arc.mojom.NotificationsHost;

/**
 * System service that bridges Android notifications to Chrome OS notification center.
 * Orchestrates ArcNotifPresenter (notification listener), ArcNotificationInstance
 * (Mojo instance), and ArcNotificationMojoBridge (Mojo lifecycle).
 */
public class ArcNotificationService {
    private static final String TAG = "ArcNotifService";

    private final Context mContext;
    private ArcNotificationRenderer mRenderer;
    private ArcNotifPresenter mPresenter;
    private ArcNotificationInstance mInstance;
    private ArcNotificationMojoBridge mBridge;
    private volatile NotificationsHost mHost;

    public static class LifeCycle extends SystemService {
        private final ArcNotificationService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcNotificationService(context);
        }

        @Override
        public void onStart() {
            Slog.i(TAG, "ArcNotificationService starting");
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onBootCompleted();
            }
        }
    }

    private ArcNotificationService(Context context) {
        mContext = context;
    }

    private void onBootCompleted() {
        Slog.i(TAG, "Boot completed, initializing notification bridge");

        // Create and initialize the renderer (VirtualDisplay + render thread)
        mRenderer = new ArcNotificationRenderer(mContext);
        mRenderer.initialize();

        // Connect Wayland proxy and initialize shared notification display
        WaylandNotifSurfaceProxy waylandProxy = new WaylandNotifSurfaceProxy();
        if (waylandProxy.connect()) {
            mRenderer.setWaylandProxy(waylandProxy);
            mRenderer.initSharedNotifDisplay();
            Slog.i(TAG, "Wayland notification surface proxy connected, shared VD initialized");
        } else {
            Slog.w(TAG, "Wayland notification surface proxy not available");
        }

        // Create and register the notification listener
        mPresenter = new ArcNotifPresenter(this, mRenderer);
        try {
            mPresenter.registerAsSystemService(mContext,
                    new ComponentName(mContext, ArcNotifPresenter.class),
                    UserHandle.USER_ALL);
            Slog.i(TAG, "NotificationListenerService registered");
        } catch (Exception e) {
            Slog.e(TAG, "Failed to register notification listener", e);
            return;
        }

        // Wire expand toggle: surface chevron click → presenter toggles state
        mRenderer.setExpandToggleListener(key -> {
            mPresenter.handleNotificationEvent(key,
                    org.chromium.arc.mojom.ArcNotificationEvent.TOGGLE_EXPANSION);
        });

        // Wire body click: surface body tap → presenter fires contentIntent
        // (and honors FLAG_AUTO_CANCEL), matching real-device behavior.
        mRenderer.setBodyClickListener(key -> {
            mPresenter.handleNotificationEvent(key,
                    org.chromium.arc.mojom.ArcNotificationEvent.BODY_CLICKED);
        });

        // Wire group child count provider for group summary labels
        mRenderer.setGroupChildCountProvider(mPresenter::getGroupChildCount);

        // Repaint on a ChromeOS palette change. The card colours are derived
        // from Chrome's scheme and dynamic-colour seed and read at render time,
        // so without this a card already on screen keeps the palette it was
        // drawn with while the rest of the system repaints around it — the user
        // switches to dark mode or changes the wallpaper accent, and the ARC
        // cards stay in the old colours until their app happens to update them.
        com.android.server.arc.system_ui.ArcSystemUiService.addPaletteListener(
                mPresenter::repaintAll);

        // Create the Mojo instance handler
        mInstance = new ArcNotificationInstance(this, mPresenter);

        // Create and start the Mojo bridge
        mBridge = new ArcNotificationMojoBridge(mContext, this, mInstance);
        mBridge.start();
    }

    Context getContext() {
        return mContext;
    }

    ArcNotificationMojoBridge getBridge() {
        return mBridge;
    }

    void setHost(NotificationsHost host) {
        mHost = host;
    }

    NotificationsHost getHost() {
        return mHost;
    }

    ArcNotificationRenderer getRenderer() {
        return mRenderer;
    }
}

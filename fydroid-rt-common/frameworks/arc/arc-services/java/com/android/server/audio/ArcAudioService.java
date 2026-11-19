package com.android.server.audio;

import android.content.Context;
import android.media.AudioDeviceAttributes;
import android.media.AudioManager;
import android.media.VolumePolicy;
import android.util.Log;
import com.android.server.SystemService;
import java.util.function.BiConsumer;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.AudioHost;
import org.chromium.arc.mojom.AudioInstance;
import org.chromium.mojo.system.MojoException;

/**
 * Replaces stock AudioService with ARC++ audio bridge support.
 *
 * Extends AudioService so it IS the system audio service, registered via
 * config_deviceSpecificAudioService. Handles Chrome's headphone/mic switch,
 * output-device and spatial-audio notifications via Mojo.
 *
 * Volume is intentionally DECOUPLED from Chrome in both directions — Android and
 * Chrome OS keep independent volume state (they are different mixers sharing one
 * CRAS output). Chrome's NotifyVolumeState no longer drives the Android stream
 * indices or master mute, and Android volume changes are no longer forwarded
 * back to Chrome (onSystemVolumeUpdateRequest). Overall loudness is still set by
 * the Chrome OS slider through CRAS, which mixes both streams. The only surviving
 * cross-call is showVolumeControls(), which pops Chrome's volume UI on an Android
 * FLAG_SHOW_UI adjust.
 */
public class ArcAudioService extends AudioService implements AudioInstance, ArcService {
    private static final String TAG = "ArcAudioService";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 8;

    private final Context mContext;
    private AudioHost mAudioHost;
    private int mConnectedOutDevice;
    private int mConnectedInDevice;

    public static final class Lifecycle extends SystemService {
        private AudioService mService;

        public Lifecycle(Context context) {
            super(context);
            mService = new ArcAudioService(context);
        }

        @Override
        public void onStart() {
            publishBinderService(Context.AUDIO_SERVICE, mService);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == SystemService.PHASE_ACTIVITY_MANAGER_READY) {
                mService.systemReady();
            }
        }
    }

    public ArcAudioService(Context context) {
        super(context);
        mContext = context;
        mConnectedOutDevice = 0;
        mConnectedInDevice = 0;
    }

    @Override
    public void onSystemReady() {
        super.onSystemReady();
        Log.i(TAG, "onSystemReady");
        setVolumePolicy(new VolumePolicy(true, true, true, 400));
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- AudioInstance (Chrome calls these) ---

    @Override
    public void init(AudioHost host, InitResponse response) {
        ArcBridgeSupport.buildThreadSafeProxy(host, AudioHost.MANAGER, this,
                (BiConsumer<AudioHost, Integer>) (proxy, version) -> {
                    Log.i(TAG, "Host version " + version);
                    synchronized (ArcAudioService.this) {
                        mAudioHost = proxy;
                    }
                    response.call();
                });
    }

    @Override
    public void notifyVolumeState(int percent, boolean muted) {
        // Volume is decoupled from Chrome: ignore Chrome's volume/mute so it no
        // longer overrides the Android stream indices (which boot to max) or the
        // Android master mute. The Chrome OS slider still governs overall loudness
        // via CRAS on the shared output.
        Log.d(TAG, "notifyVolumeState ignored (decoupled): percent=" + percent
                + " muted=" + muted);
    }

    @Override
    public void notifySwitchState(int state) {
        int outDevice;
        int inDevice;
        switch (state) {
            case 0:  // nothing plugged
                outDevice = 0;
                inDevice = 0;
                break;
            case 4:  // headphone only (SW_HEADPHONE_INSERT)
                outDevice = 4;  // DEVICE_OUT_WIRED_HEADPHONE
                inDevice = 0;
                break;
            case 16: // mic only (SW_MICROPHONE_INSERT)
                outDevice = 0;
                inDevice = -2147483632; // DEVICE_IN_WIRED_HEADSET
                break;
            case 20: // both headphone and mic
                outDevice = 4;  // DEVICE_OUT_WIRED_HEADPHONE
                inDevice = -2147483632; // DEVICE_IN_WIRED_HEADSET
                break;
            default:
                Log.e(TAG, "Unsupported device!");
                return;
        }

        // fydroid-rt (AOSP-13): setWiredDeviceConnectionState now takes an
        // AudioDeviceAttributes (built from native device type + address) instead
        // of (int device, int state, String addr, String name, String caller).
        if (outDevice != mConnectedOutDevice) {
            if (mConnectedOutDevice != 0) {
                setWiredDeviceConnectionState(new AudioDeviceAttributes(mConnectedOutDevice, ""),
                        0, mContext.getOpPackageName());
            }
            if (outDevice != 0) {
                setWiredDeviceConnectionState(new AudioDeviceAttributes(outDevice, ""),
                        1, mContext.getOpPackageName());
            }
            mConnectedOutDevice = outDevice;
        }

        if (inDevice != mConnectedInDevice) {
            if (mConnectedInDevice != 0) {
                setWiredDeviceConnectionState(new AudioDeviceAttributes(mConnectedInDevice, ""),
                        0, mContext.getOpPackageName());
            }
            if (inDevice != 0) {
                setWiredDeviceConnectionState(new AudioDeviceAttributes(inDevice, ""),
                        1, mContext.getOpPackageName());
            }
            mConnectedInDevice = inDevice;
        }
    }

    @Override
    public void notifySpatialAudioState(boolean enabled) {
        Log.d(TAG, "notifySpatialAudioState: " + enabled);
    }

    @Override
    public void notifyOutputDeviceInfo(int deviceType) {
        Log.d(TAG, "notifyOutputDeviceInfo: " + deviceType);
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        onHostClosed();
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        onHostClosed();
    }

    // --- ArcService (bridge lifecycle) ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        host.onAudioInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "AudioInstance not supported on host bridge version " + version
                + ", need at least version " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- Chrome volume UI (decoupled: no volume-value sync) ---

    @Override
    protected void adjustStreamVolume(int streamType, int direction, int flags,
            String callingPackage, String caller, int uid, int pid, String attributionTag,
            boolean hasModifyAudioSettings, int keyEventMode) {
        // fydroid-rt (AOSP-13): adjustStreamVolume gained int pid + String attributionTag.
        super.adjustStreamVolume(streamType, direction, flags, callingPackage, caller,
                uid, pid, attributionTag, hasModifyAudioSettings, keyEventMode);
        if ((flags & AudioManager.FLAG_SHOW_UI) != 0) {
            synchronized (this) {
                if (mAudioHost != null) {
                    mAudioHost.showVolumeControls();
                }
            }
        }
    }

    @Override
    protected void sendVolumeUpdate(int streamType, int oldIndex, int index, int flags,
            int device) {
        // Suppress super.sendVolumeUpdate() — it would crash ArcSystemUI (null
        // StatusBar in VolumeDialogControllerImpl). Android volume is decoupled
        // from Chrome, so nothing is forwarded to the host here.
    }

    // --- Helpers ---

    private void onHostClosed() {
        synchronized (this) {
            mAudioHost = null;
        }
    }
}

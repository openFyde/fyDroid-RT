package com.android.server.arc.auth;

import android.content.Context;
import android.content.Intent;
import android.util.Slog;

import com.android.server.SystemService;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.AccountInfo;
import org.chromium.arc.mojom.ArcAccountInfo;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.ArcSignInAccount;
import org.chromium.arc.mojom.ArcSignInResult;
import org.chromium.arc.mojom.ArcSignInSuccess;
import org.chromium.arc.mojom.AuthHost;
import org.chromium.arc.mojom.AuthInstance;
import org.chromium.arc.util.SystemPropertyHelper;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;

import java.util.function.BiConsumer;

/**
 * ARC auth bridge: implements the Mojo {@link AuthInstance} endpoint so Chrome OS receives the
 * provisioning-complete signal it needs to release ARC's first-sign-in wait.
 *
 * Why this exists: in upstream ARC++, a GMS-backed provisioning flow eventually invokes
 * {@link AuthHost#onAuthorizationResult} on the Chrome side; fydroid has no GMS, so nobody fires
 * that callback and Chrome's {@code ArcSessionManager} sits in its 5-minute provisioning timer
 * before {@code arc_session_manager.cc:1047 "Timed out waiting for first sign in"} and a /data
 * wipe. The Chrome side already has a Fyde-profile shortcut in {@code ArcAuthService::
 * OnAuthorizationResult} that forces a SUCCESS result no matter what we pass — but the call still
 * has to happen.
 *
 * So we register an AuthInstance, and as soon as Chrome calls {@link #init} we fire
 * onAuthorizationResult once with a SUCCESS+initial-signin payload. The remaining AuthInstance
 * methods are stubs: fydroid does not manage Google accounts.
 *
 * We also use the same init handshake to fetch the primary AccountInfo from Chrome and mirror
 * the managed-state into system properties (persist.sys.arc.main_acc_managed et al.). Those
 * properties drive ArcPolicyService.isManaged() and decide whether FydroidPolicyManager gets
 * promoted to device owner. Doing it here means it runs the same way in regular sessions and
 * kiosks alike — both reach PHASE_BOOT_COMPLETED — and avoids the bridge-from-app hacks that an
 * ArcHome-side equivalent would need.
 */
public final class ArcAuthService
        implements AuthInstance, ArcService, ConnectionErrorHandler {
    private static final String TAG = "ArcAuth";

    // OnAuthInstanceReady predates v7, but using the same floor as ArcPolicyService keeps the
    // bridge-version handshake consistent across our system services.
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 7;

    // Fired once Chrome's primary AccountInfo has been mirrored into system properties (or
    // confirmed absent). ArcPolicyService listens for this before its first ensureDeviceOwner
    // call so isManaged() doesn't race with the prop write. Sent in both managed and unmanaged
    // branches so unmanaged sessions still reach markUserSetupComplete.
    private static final String ACTION_MANAGED_STATE_RESOLVED =
            "io.fydeos.arc.auth.action.MANAGED_STATE_RESOLVED";

    private final Context mContext;
    private AuthHost mAuthHost;

    public static class LifeCycle extends SystemService {
        private final ArcAuthService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcAuthService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
        }
    }

    private ArcAuthService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Slog.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- AuthInstance ---

    @Override
    public void init(AuthHost host, final InitResponse response) {
        Slog.i(TAG, "init: setting up thread-safe auth host proxy");
        ArcBridgeSupport.buildThreadSafeProxy(host, AuthHost.MANAGER, this,
                new BiConsumer<AuthHost, Integer>() {
                    @Override
                    public void accept(AuthHost proxy, Integer version) {
                        mAuthHost = proxy;
                        response.call();
                        Slog.i(TAG, "AuthHost ready, version=" + version
                                + ", reporting SUCCESS to unblock provisioning");
                        ArcSignInResult result = new ArcSignInResult();
                        result.setSuccess(ArcSignInSuccess.SUCCESS);
                        ArcSignInAccount account = new ArcSignInAccount();
                        // initial_signin is a placeholder union arm; the value is irrelevant.
                        account.setInitialSignin((byte) 1);
                        mAuthHost.onAuthorizationResult(result, account);

                        mAuthHost.requestPrimaryAccountInfo((status, info) ->
                                mirrorAccountProps(info));
                    }
                });
    }

    /**
     * Mirrors Chrome's view of the primary account into system properties so
     * {@link com.android.server.arc.policy.ArcPolicyService#isManaged()} (and other
     * SystemPropertyHelper consumers) can see the managed-state without going through Mojo
     * themselves. The enforce flag derives from authCode presence only — enrollment token
     * lives at a different field offset depending on which mojom jar wins the classpath race
     * and isn't needed to drive policy decisions on fydroid anyway.
     */
    private void mirrorAccountProps(AccountInfo info) {
        if (info == null) {
            Slog.w(TAG, "requestPrimaryAccountInfo returned null info, props unchanged");
        } else {
            SystemPropertyHelper.setLoginEnforced(info.authCode != null);
            SystemPropertyHelper.setMainAccountType(info.accountType);
            SystemPropertyHelper.setMainAccountManaged(info.isManaged);
            Slog.i(TAG, "Account props mirrored: managed=" + info.isManaged
                    + " type=" + info.accountType
                    + " enforced=" + (info.authCode != null));
        }
        mContext.sendBroadcast(new Intent(ACTION_MANAGED_STATE_RESOLVED));
    }

    @Override
    public void onAccountUpdated(String accountName, int updateType) {
        Slog.i(TAG, "onAccountUpdated (ignored): " + accountName + " type=" + updateType);
    }

    @Override
    public void getGoogleAccounts(GetGoogleAccountsResponse response) {
        // No Google accounts in fydroid.
        response.call(new ArcAccountInfo[0]);
    }

    @Override
    public void getMainAccountResolutionStatus(GetMainAccountResolutionStatusResponse response) {
        // Report 0 (NO_HASH_CODE_NO_ACCOUNT) — first enum value, harmless for UMA.
        response.call(0);
    }

    @Override
    public void setAccounts(ArcAccountInfo[] accounts) {
        Slog.i(TAG, "setAccounts (ignored): count=" + (accounts != null ? accounts.length : 0));
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mAuthHost = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mAuthHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering instance");
        host.onAuthInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

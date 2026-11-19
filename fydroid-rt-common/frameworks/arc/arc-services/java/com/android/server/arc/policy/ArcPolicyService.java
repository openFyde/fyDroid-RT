package com.android.server.arc.policy;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Slog;

import com.android.server.SystemService;
import com.android.server.pm.ArcInstallGate;

import io.fydeos.arc.installpolicy.AppInstallPolicy;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.CommandResultType;
import org.chromium.arc.mojom.PolicyHost;
import org.chromium.arc.mojom.PolicyInstance;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;

import java.util.function.BiConsumer;

/**
 * ARC policy bridge: implements the Mojo {@link PolicyInstance} endpoint so Chrome OS can push
 * enterprise ARC policy into Android. On connect / policy change it fetches the policy JSON from
 * Chrome ({@link PolicyHost#getPolicies}) and forwards it to the FydroidPolicyManager device-owner
 * app (which actually enforces it via DevicePolicyManager). Also ensures that app is the device
 * owner, since enforcement requires it.
 *
 * This replaces the previous temporary path where Chrome broadcast APPLY_POLICY directly.
 *
 * It is also the install-time half of app policy: it keeps the last policy it received and
 * answers {@link ArcInstallGate}, which PackageInstallerSession asks before installing anything,
 * so a BLOCKED app (or, in ALLOWLIST mode, an unlisted one) is refused before it is installed —
 * Google Play included. The device-owner app's after-install disable stays as a backstop. Both
 * resolve packages with the shared {@link AppInstallPolicy}, so they cannot disagree.
 */
public final class ArcPolicyService
        implements PolicyInstance, ArcService, ConnectionErrorHandler {
    private static final String TAG = "ArcPolicy";

    // OnPolicyInstanceReady@114 is [MinVersion=7] in arc_bridge.mojom.
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 7;
    // PolicyHost.ReportDPCVersion@7 is [MinVersion=6].
    private static final int POLICY_HOST_VERSION_REPORT_DPC = 6;

    private static final String DPC_PACKAGE = "io.fydeos.arc.policymanager";
    private static final String DPC_RECEIVER = DPC_PACKAGE + ".PolicyReceiver";
    private static final String DPC_ADMIN_RECEIVER = DPC_PACKAGE + ".ArcDeviceAdminReceiver";
    private static final String ACTION_APPLY_POLICY =
            "io.fydeos.arc.ArcGateway.action.APPLY_POLICY";
    private static final String EXTRA_POLICY_JSON =
            "io.fydeos.arc.policymanager.extra.POLICY_JSON";
    // Fired by FydroidPolicyManager.PolicyService.announcePolicyApplied() once a package's
    // install state and runtime-permission grants are all settled. We use this to defer the
    // PolicyHost.reportCompliance call to Chrome; reporting earlier races the still-in-progress
    // setPermissionGrantState loop (each call invalidates the package and AM kills any process
    // trying to start for it).
    private static final String ACTION_POLICY_APPLIED =
            "io.fydeos.arc.policymanager.action.POLICY_APPLIED";
    // Sent by FydroidPolicyManager's BootReceiver after BOOT_COMPLETED so the DPC can pull the
    // policy on its own boot. Avoids the prior race where our initial APPLY_POLICY broadcast
    // (during ArcPolicyService.init, before user unlock) was dropped because PolicyReceiver is
    // not directBootAware. Action is not a protected-broadcast since the DPC is not a system
    // app; the worst an unauthorized sender can do is trigger an extra getPolicies + idempotent
    // forward to the (genuine) DPC.
    private static final String ACTION_REQUEST_POLICY_REFRESH =
            "io.fydeos.arc.policy.action.REQUEST_REFRESH";
    // Fired by ArcAuthService.mirrorAccountProps once the managed-state props are written
    // (or confirmed unset). Gate ensureDeviceOwner / refreshPolicies on this so isManaged()
    // doesn't race with the prop write on a fresh-data boot — without the gate, ArcPolicy's
    // PHASE_BOOT_COMPLETED runs ~200ms before ArcAuth's mirrorAccountProps callback, hits the
    // "Device not managed, skipping" branch, prematurely calls markUserSetupComplete, and
    // closes the setDeviceOwner latch before ArcAuth's prop arrives.
    private static final String ACTION_MANAGED_STATE_RESOLVED =
            "io.fydeos.arc.auth.action.MANAGED_STATE_RESOLVED";
    private static final String DPC_VERSION = "fydroid-policy-1";
    private static final String DEVICE_OWNER_NAME = "FydeOS Policy Manager";

    // Managed-state signals Chrome OS pushes into the container (see ARC ProvisioningUtils /
    // SystemPropertyHelper). main_acc_managed is the steady-state "this account is managed" flag;
    // supervision.transition == 3 (UNMANAGED_TO_MANAGED) marks an in-progress transition.
    private static final String PROP_MAIN_ACCOUNT_MANAGED = "persist.sys.arc.main_acc_managed";
    private static final String PROP_SUPERVISION_TRANSITION = "ro.boot.supervision.transition";
    private static final int MANAGEMENT_TRANSITION_UNMANAGED_TO_MANAGED = 3;

    private final Context mContext;
    private final DevicePolicyManager mDpm;
    private PolicyHost mPolicyHost;
    private int mPolicyHostVersion = -1;
    private BroadcastReceiver mPolicyAppliedReceiver;
    private BroadcastReceiver mRefreshRequestReceiver;
    private BroadcastReceiver mManagedStateReceiver;
    private volatile boolean mManagedStateResolved;
    /**
     * The app-install policy last received from Chrome; null until the first one arrives and
     * while the device is unmanaged, which lets every install through.
     */
    private volatile AppInstallPolicy mInstallPolicy;

    /** {@link ArcInstallGate}: answered from memory, as the package manager requires. */
    private final ArcInstallGate mInstallGate = new ArcInstallGate() {
        @Override
        public String checkInstall(String packageName, String installerPackageName) {
            AppInstallPolicy policy = mInstallPolicy;
            if (policy == null) {
                return null;
            }
            // The device owner installs what policy asks for (FORCE_INSTALLED).
            if (DPC_PACKAGE.equals(installerPackageName)) {
                return null;
            }
            if (policy.isInstallAllowed(packageName)) {
                return null;
            }
            AppInstallPolicy.Entry entry = policy.resolve(packageName);
            return "blocked by ARC app policy ("
                    + (entry != null ? entry.toString() : policy.playStoreMode.name()) + ")";
        }
    };

    private final BroadcastReceiver mPolicyAppliedHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pkg = intent.getStringExtra("package");
            Slog.i(TAG, "POLICY_APPLIED for " + pkg + " - reporting compliance");
            reportCompliance();
        }
    };

    private final BroadcastReceiver mRefreshRequestHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Slog.i(TAG, "REQUEST_REFRESH from DPC - re-fetching policy");
            refreshPolicies();
        }
    };

    private final BroadcastReceiver mManagedStateHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Slog.i(TAG, "MANAGED_STATE_RESOLVED from ArcAuth");
            mManagedStateResolved = true;
            ensureDeviceOwner();
            // PolicyHost may already be ready (race winner); replay so a policy that arrived
            // before DO promotion gets re-forwarded to the now-promoted DPC.
            if (mPolicyHost != null) {
                refreshPolicies();
            }
        }
    };

    public static class LifeCycle extends SystemService {
        private final ArcPolicyService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcPolicyService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
            publishLocalService(ArcInstallGate.class, mService.mInstallGate);
        }
    }

    private ArcPolicyService(Context context) {
        mContext = context;
        mDpm = context.getSystemService(DevicePolicyManager.class);
    }

    void onSystemBooted() {
        Slog.i(TAG, "System booted, requesting ARC bridge proxy");
        registerDpcReceivers();
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    private void registerDpcReceivers() {
        if (mPolicyAppliedReceiver == null) {
            mPolicyAppliedReceiver = mPolicyAppliedHandler;
            mContext.registerReceiver(mPolicyAppliedReceiver,
                    new IntentFilter(ACTION_POLICY_APPLIED));
        }
        if (mRefreshRequestReceiver == null) {
            mRefreshRequestReceiver = mRefreshRequestHandler;
            mContext.registerReceiver(mRefreshRequestReceiver,
                    new IntentFilter(ACTION_REQUEST_POLICY_REFRESH));
        }
        if (mManagedStateReceiver == null) {
            mManagedStateReceiver = mManagedStateHandler;
            mContext.registerReceiver(mManagedStateReceiver,
                    new IntentFilter(ACTION_MANAGED_STATE_RESOLVED));
        }
    }

    // --- PolicyInstance ---

    @Override
    public void init(PolicyHost host, final InitResponse response) {
        Slog.i(TAG, "init: setting up thread-safe policy host proxy");
        ArcBridgeSupport.buildThreadSafeProxy(host, PolicyHost.MANAGER, this,
                new BiConsumer<PolicyHost, Integer>() {
                    @Override
                    public void accept(PolicyHost proxy, Integer version) {
                        mPolicyHost = proxy;
                        mPolicyHostVersion = version;
                        response.call();
                        Slog.i(TAG, "PolicyHost ready, version=" + version);
                        if (version >= POLICY_HOST_VERSION_REPORT_DPC) {
                            mPolicyHost.reportDpcVersion(DPC_VERSION);
                        }
                        if (mManagedStateResolved) {
                            refreshPolicies();
                        } else {
                            Slog.i(TAG, "Waiting for ArcAuth managed-state signal "
                                    + "before refreshPolicies");
                        }
                    }
                });
    }

    @Override
    public void onPolicyUpdated() {
        Slog.i(TAG, "onPolicyUpdated");
        refreshPolicies();
    }

    @Override
    public void onCommandReceived(String command, OnCommandReceivedResponse response) {
        // Remote enterprise commands are out of scope for app/permission policy; acknowledge as
        // ignored so Chrome doesn't treat it as a transport failure.
        Slog.i(TAG, "onCommandReceived (ignored): " + command);
        response.call(CommandResultType.IGNORED);
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mPolicyHost = null;
        mPolicyHostVersion = -1;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mPolicyHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering instance");
        host.onPolicyInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- Policy plumbing ---

    private void refreshPolicies() {
        if (mPolicyHost == null) {
            Slog.w(TAG, "refreshPolicies: no host");
            return;
        }
        // ensureDeviceOwner is owned by the MANAGED_STATE_RESOLVED handler now — calling it
        // here would race the ArcAuth prop write on cold boot. Subsequent refreshPolicies
        // callers (onPolicyUpdated, REQUEST_REFRESH, this method invoked from the broadcast
        // handler) all run after DO is settled.
        mPolicyHost.getPolicies(policies -> {
            if (policies == null || policies.isEmpty()) {
                Slog.i(TAG, "getPolicies: empty (device not managed)");
                mInstallPolicy = null;
                return;
            }
            updateInstallPolicy(policies);
            forwardToDpc(policies);
        });
    }

    private void updateInstallPolicy(String policiesJson) {
        try {
            AppInstallPolicy policy = AppInstallPolicy.parse(policiesJson);
            for (String why : policy.skipped) {
                Slog.w(TAG, "install policy: skip " + why);
            }
            mInstallPolicy = policy;
            Slog.i(TAG, "install gate: " + policy.playStoreMode + ", " + policy.entries.size()
                    + " entries " + policy.entries);
        } catch (Exception e) {
            // Keep the previous policy rather than opening the gate on a malformed update.
            Slog.e(TAG, "install policy: unparseable, keeping the previous one", e);
        }
    }

    /**
     * Tells Chrome we've ack'd the policy. The ArcvmKiosk launcher gates the app launch on a
     * non-null {@code compliance_report_received_} from {@code KioskArcvmAppService}; in upstream
     * ARC++ the cloud DPC calls {@code PolicyHost.reportCompliance} after enforcing the policy.
     * Fydroid's DPC has no Mojo channel of its own, so {@link ArcPolicyService} forwards the
     * signal — but only after the DPC broadcasts {@link #ACTION_POLICY_APPLIED}, which fires once
     * the package's install state and runtime-permission grants are settled. Reporting earlier
     * races the still-in-progress setPermissionGrantState loop: Chrome fires LaunchApp, AM starts
     * the kiosk process, then the next grant invalidates the package and AM kills the process
     * mid-start — splash screen sits on "Waiting for application window" forever.
     */
    private void reportCompliance() {
        if (mPolicyHost == null) {
            return;
        }
        final String complianceJson = "{\"nonComplianceDetails\":[]}";
        mPolicyHost.reportCompliance(complianceJson, response -> {
            Slog.i(TAG, "reportCompliance ack from Chrome: " + response);
        });
    }

    private void forwardToDpc(String policiesJson) {
        Slog.i(TAG, "forwarding policy to " + DPC_PACKAGE);
        Intent intent = new Intent(ACTION_APPLY_POLICY);
        intent.setComponent(new ComponentName(DPC_PACKAGE, DPC_RECEIVER));
        intent.putExtra(EXTRA_POLICY_JSON, policiesJson);
        // The app may never have been launched (stopped state), so force delivery to its receiver.
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // arc-services runs in system_server (uid system), which may send this protected broadcast.
        mContext.sendBroadcast(intent);
    }

    /** Mirrors ARC ProvisioningUtils: the device is managed per the properties Chrome sets. */
    private boolean isManaged() {
        return SystemProperties.getBoolean(PROP_MAIN_ACCOUNT_MANAGED, false)
                || SystemProperties.getInt(PROP_SUPERVISION_TRANSITION, 0)
                        == MANAGEMENT_TRANSITION_UNMANAGED_TO_MANAGED;
    }

    /**
     * Promotes the FydroidPolicyManager app to device owner so its DevicePolicyManager calls are
     * allowed. No-op if the device isn't managed or if it (or another admin) is already the owner.
     *
     * The Settings overlay seeds {@code user_setup_complete=false} (see product-overlay's
     * defaults.xml) so {@code setDeviceOwner} isn't rejected by
     * {@code enforceCanSetDeviceOwnerLocked}. Once the device-owner state is resolved here — set
     * by us, already set by someone else, or correctly absent because the device is unmanaged —
     * we flip {@code user_setup_complete} to 1 so the rest of Settings (search bar etc.) behaves.
     * The marker runs in every branch, including the {@code mDpm == null} and unmanaged paths,
     * so a fydroid device never gets stuck with an unfinished setup.
     */
    private void ensureDeviceOwner() {
        if (mDpm == null) {
            Slog.w(TAG, "No DevicePolicyManager, cannot ensure device owner");
        } else if (!isManaged()) {
            Slog.i(TAG, "Device not managed, skipping device-owner setup");
        } else {
            try {
                if (mDpm.isDeviceOwnerApp(DPC_PACKAGE)) {
                    Slog.i(TAG, DPC_PACKAGE + " is already device owner");
                } else {
                    ComponentName existing = mDpm.getDeviceOwnerComponentOnAnyUser();
                    if (existing != null) {
                        Slog.w(TAG, "Device owner already set to " + existing
                                + ", not overriding");
                    } else {
                        ComponentName admin =
                                new ComponentName(DPC_PACKAGE, DPC_ADMIN_RECEIVER);
                        if (!mDpm.isAdminActive(admin)) {
                            mDpm.setActiveAdmin(admin, false);
                        }
                        // fydroid-rt (AOSP-13): setDeviceOwner gained a userId arg.
                        boolean ok = mDpm.setDeviceOwner(admin, DEVICE_OWNER_NAME,
                                android.os.UserHandle.USER_SYSTEM);
                        Slog.i(TAG, "setDeviceOwner(" + admin + ") = " + ok);
                    }
                }
            } catch (Exception e) {
                Slog.e(TAG, "Failed to set device owner for " + DPC_PACKAGE
                        + " (is system setup already complete?)", e);
            }
        }
        markUserSetupComplete();
    }

    private void markUserSetupComplete() {
        try {
            Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.USER_SETUP_COMPLETE, 1);
        } catch (Exception e) {
            Slog.w(TAG, "Failed to mark user_setup_complete=1", e);
        }
    }
}

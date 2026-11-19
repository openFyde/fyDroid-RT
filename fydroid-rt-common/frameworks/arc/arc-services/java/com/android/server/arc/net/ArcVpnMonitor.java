package com.android.server.arc.net;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.IpPrefix;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;

import org.chromium.arc.mojom.AndroidVpnConfiguration;
import org.chromium.arc.mojom.ArcProxyInfo;
import org.chromium.arc.mojom.ManualProxyConfig;
import org.chromium.arc.mojom.NetHost;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Watches for an Android app establishing a VPN inside the container (e.g.
 * Clash Meta using {@link android.net.VpnService}) and reports it to Chrome
 * OS over the NetHost.androidVpn* mojo calls, so Chrome can register it as a
 * regular "arcvpn" shill service. Chrome / shill / patchpanel already fully
 * implement their side of this (arcbr0 + VPN routing) — this class is the
 * missing link that was never wired up on the Android side.
 *
 * <p>Chrome→ARC direction ({@code NetInstance.configureAndroidVpn} /
 * {@code disconnectAndroidVpn}) stays a stub in {@link ArcNetworkBridge}:
 * those exist for Chrome to ask ARC to reconnect/disconnect a VPN it already
 * knows about (e.g. after Always-On VPN policy changes), which this device
 * doesn't use yet.
 */
class ArcVpnMonitor {
    private static final String TAG = "ArcVpnMonitor";

    // Real ARC's convention for the host-facing bridge interface whose
    // address is the next hop for host-originated IPv4 traffic entering the
    // container (AndroidVpnConfiguration.ipv4Gateway). fydroid's networking
    // is a from-scratch reimplementation, not vendor/google_arc, so whether
    // this interface exists/is up and whether traffic arriving on it
    // actually reaches the VPN's tun device is unverified — this is a
    // best-effort mapping onto the one existing fydroid convention for this
    // interface name (see arc0 + adb-over-network in the device docs), not a
    // confirmed end-to-end path.
    private static final String ARC0_INTERFACE = "arc0";

    // patchpanel's fixed /30 for the arc0 <-> arcbr0 link (AddressManager
    // reserves exactly this for GuestType::kArc0; the same constants are
    // hardcoded host-side in datapath.cc kArcAddr, so they cannot drift).
    private static final String ARC0_SUBNET = "100.115.92.0/30";
    private static final String IPTABLES_PATH = "/system/bin/iptables";
    private static final String IP_FORWARD_PATH = "/proc/sys/net/ipv4/ip_forward";
    // netd's tethering chains. Its FORWARD chain ends in an unconditional
    // DROP while tethering is off, and its nat POSTROUTING chain is where a
    // masquerade for forwarded guest traffic belongs.
    private static final String TETHER_FORWARD_CHAIN = "tetherctrl_FORWARD";
    private static final String TETHER_NAT_CHAIN = "tetherctrl_nat_POSTROUTING";
    private static final int IPTABLES_TIMEOUT_SECONDS = 5;
    private static final String IP_PATH = "/system/bin/ip";
    // Above netd's EXPLICIT_NETWORK (16000) so packets forwarded in from the
    // host resolve against the VPN's own table before netd's per-network
    // rules (which only ever match traffic from local apps) get a look.
    private static final String ARC0_VPN_RULE_PREF = "15500";
    // netd derives a network's routing table id from the interface index
    // (RouteController.h: ROUTE_TABLE_OFFSET_FROM_INDEX). We compute it the
    // same way rather than passing the table *name*: names live in
    // /data/misc/net/rt_tables, and that directory is drwxr-x--- root:shell,
    // which system_server (user "system", not in group shell) cannot traverse
    // — so `ip ... lookup tun0` fails with "invalid table ID" from here even
    // though it works from a root shell.
    private static final int ROUTE_TABLE_OFFSET_FROM_INDEX = 1000;
    // The VPN's interface may not exist yet when the NetworkCallback fires.
    private static final int VPN_RULE_MAX_ATTEMPTS = 10;
    private static final long VPN_RULE_RETRY_DELAY_MS = 300;

    private final ArcNetworkService mService;
    private final ConnectivityManager mConnectivityManager;
    private final PackageManager mPackageManager;
    private final Handler mHandler;

    // The VPN network we last reported to Chrome, so onLost() only fires
    // AndroidVpnDisconnected for the network we actually reported, and so we
    // know whether to send Connected (new network) or Updated (same network,
    // changed properties).
    private Network mReportedNetwork;

    // Reflection handles for reading the active VPN's owning package straight
    // out of VpnManagerService, cached on first successful resolution. See
    // resolveActiveVpnPackage() for why this is necessary at all:
    // NetworkCapabilities.getOwnerUid() is unconditionally redacted to -1 for
    // any observer that isn't the VPN app itself — including system_server —
    // so there is no public API path to the owning package from the
    // NetworkCallback alone.
    private Object mVpnManagerService;
    private Field mVpnsField;
    private Method mGetPackageMethod;

    // VPN interface the forwarding rules currently point at, so teardown
    // removes exactly what setup added. Null when no rules are installed.
    private String mForwardingInterface;

    private final ConnectivityManager.NetworkCallback mCallback =
            new ConnectivityManager.NetworkCallback() {
                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities caps) {
                    handleVpnNetwork(network);
                }

                @Override
                public void onLinkPropertiesChanged(Network network, LinkProperties lp) {
                    handleVpnNetwork(network);
                }

                @Override
                public void onLost(Network network) {
                    if (network.equals(mReportedNetwork)) {
                        mReportedNetwork = null;
                        teardownHostForwarding();
                        SystemProperties.set("arc.vpn.active", "0");
                        Slog.i(TAG, "VPN network lost, reporting AndroidVpnDisconnected");
                        NetHost host = getNetHost();
                        if (host != null) {
                            host.androidVpnDisconnected();
                        }
                    }
                }
            };

    ArcVpnMonitor(Context context, ArcNetworkService service) {
        mService = service;
        mConnectivityManager = context.getSystemService(ConnectivityManager.class);
        mPackageManager = context.getPackageManager();
        mHandler = new Handler(Looper.getMainLooper());
    }

    void start() {
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                .build();
        mConnectivityManager.registerNetworkCallback(request, mCallback, mHandler);
        Slog.i(TAG, "Watching for in-container Android VPNs");
    }

    private void handleVpnNetwork(Network network) {
        LinkProperties lp = mConnectivityManager.getLinkProperties(network);
        if (lp == null) return;

        AndroidVpnConfiguration cfg = buildConfiguration(lp);
        if (cfg == null) return;

        NetHost host = getNetHost();
        if (host == null) {
            Slog.w(TAG, "NetHost not connected yet, dropping VPN notification for "
                    + cfg.appName);
            return;
        }

        boolean isNewNetwork = !network.equals(mReportedNetwork);
        mReportedNetwork = network;
        if (isNewNetwork) {
            // arc0 (the general host<->ARC bridge, distinct from the
            // per-CrOS-network veths ArcNetworkAgent manages) starts
            // administratively down — patchpanel deliberately leaves that to
            // Android (see fydroid-vpn-net.rc). Chrome/shill/patchpanel need
            // it up to route host traffic in via arcbr0.
            SystemProperties.set("arc.vpn.active", "1");
            setupHostForwarding(lp.getInterfaceName());
            Slog.i(TAG, "AndroidVpnConnected: " + cfg.appName);
            host.androidVpnConnected(cfg);
        } else {
            Slog.i(TAG, "AndroidVpnUpdated: " + cfg.appName);
            host.androidVpnUpdated(cfg);
        }
    }

    private NetHost getNetHost() {
        ArcNetworkBridge bridge = mService.getBridge();
        return bridge != null ? bridge.getNetHost() : null;
    }

    /**
     * Lets host traffic arriving on arc0 reach the VPN's tun device.
     *
     * <p>Registering the VPN with Chrome is not sufficient on its own: once
     * shill/patchpanel make the ARC VPN the host's default network, host
     * packets arrive on arc0 and are then dropped inside the container by
     * three separate things, none of which AOSP configures for this case
     * (upstream Android only ever routes its own apps' traffic into a VPN,
     * never traffic forwarded in from outside):
     *
     * <ul>
     *   <li>{@code ip_forward} is 0, so the kernel does not forward between
     *       interfaces at all.
     *   <li>netd's {@code tetherctrl_FORWARD} chain ends in an unconditional
     *       DROP whenever tethering is off, which is always here.
     *   <li>Without NAT the VPN app sees arc0's 100.115.92.x source address,
     *       which is not part of the tunnel's address space, so replies have
     *       nowhere to go.
     *   <li>Every netd routing rule that steers traffic into the VPN's table
     *       is qualified with {@code iif lo} (i.e. locally-originated only),
     *       so a packet arriving on arc0 matches none of them and falls
     *       through to {@code unreachable}. Hence the explicit
     *       {@code iif arc0 lookup <vpn table>} rule below.
     * </ul>
     *
     * <p>Rules are inserted at the head of the tether chain so they are
     * evaluated before its trailing DROP, and are removed again in
     * {@link #teardownHostForwarding()} so a later tethering session or a
     * different VPN does not inherit them.
     */
    private void setupHostForwarding(String vpnInterface) {
        if (vpnInterface == null || vpnInterface.isEmpty()) {
            Slog.w(TAG, "VPN has no interface name; not setting up host forwarding");
            return;
        }
        // Re-entrancy: a VPN reconnecting without an intervening onLost()
        // would otherwise stack duplicate rules.
        teardownHostForwarding();
        mForwardingInterface = vpnInterface;

        runIptables("-w", "-I", TETHER_FORWARD_CHAIN, "1",
                "-i", ARC0_INTERFACE, "-o", vpnInterface, "-j", "ACCEPT");
        runIptables("-w", "-I", TETHER_FORWARD_CHAIN, "2",
                "-i", vpnInterface, "-o", ARC0_INTERFACE, "-j", "ACCEPT");
        runIptables("-w", "-t", "nat", "-A", TETHER_NAT_CHAIN,
                "-s", ARC0_SUBNET, "-o", vpnInterface, "-j", "MASQUERADE");

        addVpnRoutingRule(vpnInterface, 0);

        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(IP_FORWARD_PATH),
                    "1\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
            Slog.e(TAG, "Failed to enable " + IP_FORWARD_PATH, e);
        }

        Slog.i(TAG, "Host forwarding enabled: " + ARC0_INTERFACE + " <-> " + vpnInterface);
    }

    private void teardownHostForwarding() {
        if (mForwardingInterface == null) return;
        String vpnInterface = mForwardingInterface;
        mForwardingInterface = null;

        runIptables("-w", "-D", TETHER_FORWARD_CHAIN,
                "-i", ARC0_INTERFACE, "-o", vpnInterface, "-j", "ACCEPT");
        runIptables("-w", "-D", TETHER_FORWARD_CHAIN,
                "-i", vpnInterface, "-o", ARC0_INTERFACE, "-j", "ACCEPT");
        runIptables("-w", "-t", "nat", "-D", TETHER_NAT_CHAIN,
                "-s", ARC0_SUBNET, "-o", vpnInterface, "-j", "MASQUERADE");

        // Delete by preference alone: by teardown time the VPN interface is
        // usually already gone, so its index (and thus the table id) can no
        // longer be resolved. The pref is unique to this rule.
        runIp("rule", "del", "pref", ARC0_VPN_RULE_PREF);

        // ip_forward is left on: it is a global toggle, and something else
        // (tethering, another VPN) may be relying on it by now.
        Slog.i(TAG, "Host forwarding disabled for " + vpnInterface);
    }

    /**
     * Adds the {@code iif arc0 lookup <vpn table>} rule, retrying while the
     * VPN's interface index is still unavailable (it may not have been created
     * yet when the NetworkCallback fired).
     */
    private void addVpnRoutingRule(String vpnInterface, int attempt) {
        // Bail if the VPN went away, or a different one took over, while we
        // were waiting to retry.
        if (!vpnInterface.equals(mForwardingInterface)) return;

        int table = routeTableForInterface(vpnInterface);
        if (table > 0 && runIp("rule", "add", "iif", ARC0_INTERFACE,
                "lookup", Integer.toString(table), "pref", ARC0_VPN_RULE_PREF)) {
            Slog.i(TAG, "Routing rule installed: iif " + ARC0_INTERFACE
                    + " lookup " + table + " (" + vpnInterface + ")");
            return;
        }
        if (attempt >= VPN_RULE_MAX_ATTEMPTS) {
            Slog.e(TAG, "Giving up installing the arc0 -> " + vpnInterface
                    + " routing rule; host traffic will not reach the VPN");
            return;
        }
        mHandler.postDelayed(() -> addVpnRoutingRule(vpnInterface, attempt + 1),
                VPN_RULE_RETRY_DELAY_MS);
    }

    /** netd's routing table id for an interface, or -1 if it has no index yet. */
    private int routeTableForInterface(String name) {
        try {
            NetworkInterface iface = NetworkInterface.getByName(name);
            if (iface == null) return -1;
            return iface.getIndex() + ROUTE_TABLE_OFFSET_FROM_INDEX;
        } catch (SocketException e) {
            Slog.w(TAG, "Failed to read the index of " + name, e);
            return -1;
        }
    }

    private void runIptables(String... args) {
        // A failing delete is normal (nothing there to remove).
        boolean isDelete = false;
        for (String arg : args) {
            if ("-D".equals(arg)) {
                isDelete = true;
                break;
            }
        }
        runCommand(IPTABLES_PATH, isDelete, args);
    }

    private boolean runIp(String... args) {
        return runCommand(IP_PATH, args.length > 1 && "del".equals(args[1]), args);
    }

    private boolean runCommand(String binary, boolean failureExpected, String... args) {
        List<String> cmd = new ArrayList<>();
        cmd.add(binary);
        for (String arg : args) {
            cmd.add(arg);
        }
        try {
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            if (!p.waitFor(IPTABLES_TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)) {
                p.destroyForcibly();
                Slog.e(TAG, "Timed out: " + String.join(" ", cmd));
                return false;
            }
            if (p.exitValue() != 0) {
                if (!failureExpected) {
                    String output = new String(p.getInputStream().readAllBytes(),
                            java.nio.charset.StandardCharsets.UTF_8).trim();
                    Slog.w(TAG, "Exit " + p.exitValue() + ": " + String.join(" ", cmd)
                            + (output.isEmpty() ? "" : " -- " + output));
                }
                return false;
            }
            return true;
        } catch (java.io.IOException | InterruptedException e) {
            Slog.e(TAG, "Failed to run: " + String.join(" ", cmd), e);
            return false;
        }
    }

    private AndroidVpnConfiguration buildConfiguration(LinkProperties lp) {
        String packageName = resolveActiveVpnPackage();
        if (packageName == null) {
            Slog.w(TAG, "Could not resolve the active VPN's package name");
            return null;
        }

        AndroidVpnConfiguration cfg = new AndroidVpnConfiguration();
        cfg.appName = packageName;
        cfg.appLabel = resolveAppLabel(packageName);
        cfg.sessionName = "";
        // The whole point of reporting this VPN to Chrome is to let the host
        // (and other system traffic) use it, not just apps inside ARC.
        cfg.tunnelChromeTraffic = true;
        cfg.ipv4Gateway = getArc0Address();

        List<String> includes = new ArrayList<>();
        List<String> excludes = new ArrayList<>();
        for (RouteInfo route : lp.getRoutes()) {
            IpPrefix dest = route.getDestination();
            if (dest == null) continue;
            String cidr = dest.getAddress().getHostAddress() + "/" + dest.getPrefixLength();
            if (route.getType() == RouteInfo.RTN_UNREACHABLE) {
                excludes.add(cidr);
            } else {
                includes.add(cidr);
            }
        }
        cfg.splitInclude = includes.toArray(new String[0]);
        cfg.splitExclude = excludes.toArray(new String[0]);

        List<String> nameservers = new ArrayList<>();
        for (InetAddress addr : lp.getDnsServers()) {
            nameservers.add(addr.getHostAddress());
        }
        cfg.nameservers = nameservers.toArray(new String[0]);

        String domains = lp.getDomains();
        cfg.domains = domains != null && !domains.isEmpty()
                ? domains.split(" ") : new String[0];

        cfg.mtu = lp.getMtu();

        ProxyInfo proxy = lp.getHttpProxy();
        if (proxy != null && proxy.getHost() != null && !proxy.getHost().isEmpty()) {
            ManualProxyConfig manual = new ManualProxyConfig();
            manual.host = proxy.getHost();
            manual.port = proxy.getPort();
            String[] exclusions = proxy.getExclusionList();
            manual.exclusionList = exclusions != null ? exclusions : new String[0];
            ArcProxyInfo arcProxy = new ArcProxyInfo();
            arcProxy.setManualProxy(manual);
            cfg.httpProxy = arcProxy;
        }

        return cfg;
    }

    /**
     * Reads the current user's active VPN package straight out of
     * VpnManagerService's internal state (mVpns SparseArray of Vpn, keyed by
     * user id), the same "in-process ServiceManager.getService() returns the
     * real object, not a Binder proxy" trick ArcClipboardService already
     * uses for ClipboardService. Vpn.getPackage() is a public method — only
     * getting the Vpn instance out of the private mVpns field needs
     * reflection.
     */
    private String resolveActiveVpnPackage() {
        try {
            if (mVpnManagerService == null) {
                mVpnManagerService = ServiceManager.getService(Context.VPN_MANAGEMENT_SERVICE);
                if (mVpnManagerService == null) return null;
            }
            if (mVpnsField == null) {
                mVpnsField = mVpnManagerService.getClass().getDeclaredField("mVpns");
                mVpnsField.setAccessible(true);
            }

            SparseArray<?> vpns = (SparseArray<?>) mVpnsField.get(mVpnManagerService);
            Object vpn = vpns != null ? vpns.get(UserHandle.USER_SYSTEM) : null;
            if (vpn == null) return null;

            if (mGetPackageMethod == null) {
                mGetPackageMethod = vpn.getClass().getMethod("getPackage");
            }
            Object result = mGetPackageMethod.invoke(vpn);
            return result != null ? result.toString() : null;
        } catch (ReflectiveOperationException | ClassCastException e) {
            Slog.e(TAG, "Failed to read the active VPN package from VpnManagerService", e);
            return null;
        }
    }

    private String resolveAppLabel(String packageName) {
        try {
            ApplicationInfo info = mPackageManager.getApplicationInfo(packageName, 0);
            return mPackageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    private String getArc0Address() {
        try {
            NetworkInterface iface = NetworkInterface.getByName(ARC0_INTERFACE);
            if (iface == null) return "";
            for (InetAddress addr : java.util.Collections.list(iface.getInetAddresses())) {
                if (addr instanceof java.net.Inet4Address) {
                    return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            Slog.w(TAG, "Failed to read " + ARC0_INTERFACE + " address", e);
        }
        return "";
    }
}

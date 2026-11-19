package com.android.server.arc.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkAgent;
import android.net.NetworkAgentConfig;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.os.Looper;
import android.util.Slog;

import org.chromium.arc.mojom.ConnectionStateType;
import org.chromium.arc.mojom.NetworkConfiguration;
import org.chromium.arc.mojom.NetworkType;

import android.os.INetworkManagementService;
import android.os.ServiceManager;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Wraps one Chrome OS network interface as an Android NetworkAgent.
 * ConnectivityService sees this as a real network (WiFi, Ethernet, etc.).
 */
class ArcNetworkAgent {
    private static final String TAG = "ArcNetworkAgent";
    private static final int NETWORK_SCORE = 100;

    private final Context mContext;
    private final String mInterface;
    private NetworkAgent mAgent;
    private NetworkConfiguration mConfig;

    ArcNetworkAgent(Context context, NetworkConfiguration config) {
        mContext = context;
        mInterface = config.arcNetworkInterface;
        mConfig = config;

        if (isConnected(config)) {
            createAgent(config);
        }
    }

    void update(NetworkConfiguration config) {
        mConfig = config;
        boolean connected = isConnected(config);

        if (mAgent != null && connected) {
            // Update existing agent
            Slog.i(TAG, "Updating agent for " + mInterface);
            mAgent.sendLinkProperties(buildLinkProperties(config));
            mAgent.sendNetworkCapabilities(buildCapabilities(config));
        } else if (mAgent == null && connected) {
            // Create new agent
            createAgent(config);
        } else if (mAgent != null && !connected) {
            // Disconnect
            disconnect();
        }
    }

    void disconnect() {
        if (mAgent != null) {
            Slog.i(TAG, "Disconnecting agent for " + mInterface);
            mAgent.unregister();
            mAgent = null;
        }
    }

    NetworkConfiguration getConfig() {
        return mConfig;
    }

    private void createAgent(NetworkConfiguration config) {
        // Bring the interface UP before registering — netd can't add routes
        // to a DOWN interface (proprietary code does this in ArcFactory.start())
        bringInterfaceUp(config.arcNetworkInterface);

        NetworkCapabilities nc = buildCapabilities(config);
        LinkProperties lp = buildLinkProperties(config);
        NetworkAgentConfig nac = buildAgentConfig(config);

        Slog.i(TAG, "Creating NetworkAgent for " + mInterface
                + " type=" + config.type + " default=" + config.isDefaultNetwork);

        mAgent = new NetworkAgent(mContext, Looper.getMainLooper(), TAG,
                nc, lp, NETWORK_SCORE, nac, null /* provider */) {
            @Override
            public void onNetworkUnwanted() {
                Slog.d(TAG, "onNetworkUnwanted for " + mInterface);
            }

            @Override
            public void onBandwidthUpdateRequested() {
                Slog.d(TAG, "onBandwidthUpdateRequested for " + mInterface);
            }
        };

        mAgent.register();
        mAgent.markConnected();
        Slog.i(TAG, "NetworkAgent registered and connected for " + mInterface);
    }

    private static void bringInterfaceUp(String iface) {
        try {
            INetworkManagementService nms = INetworkManagementService.Stub.asInterface(
                    ServiceManager.getService("network_management"));
            if (nms != null) {
                nms.setInterfaceUp(iface);
                Slog.i(TAG, "Brought interface " + iface + " UP");
            } else {
                Slog.w(TAG, "NetworkManagementService not available");
            }
        } catch (Exception e) {
            Slog.e(TAG, "Failed to bring " + iface + " up", e);
        }
    }

    private static NetworkCapabilities buildCapabilities(NetworkConfiguration config) {
        NetworkCapabilities.Builder builder = new NetworkCapabilities.Builder();

        // Transport type
        switch (config.type) {
            case NetworkType.WIFI:
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                break;
            case NetworkType.ETHERNET:
                builder.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);
                break;
            case NetworkType.CELLULAR:
                builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
                break;
            case NetworkType.VPN:
                builder.addTransportType(NetworkCapabilities.TRANSPORT_VPN);
                break;
            default:
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                break;
        }

        // Standard capabilities
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED);

        // NET_CAPABILITY_NOT_VCN_MANAGED is REQUIRED by every default network
        // request (ConnectivityService adds it to createDefaultInternetRequest*).
        // This build has no VcnManagementService to inject it onto agent
        // networks, and it is not part of NetworkCapabilities.DEFAULT_CAPABILITIES,
        // so the agent must declare it explicitly. Without it the agent satisfies
        // no default request, never clears its nascent inactivity timer, and is
        // reaped ~5s after connecting (handleLingerComplete) — tearing down the
        // routing table and leaving ARC with no default network.
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VCN_MANAGED);

        // NET_CAPABILITY_VALIDATED and NET_CAPABILITY_FOREGROUND are
        // ConnectivityService-managed capabilities; agents that set them
        // themselves trigger a CS "has CS-managed capability" BUG log and have
        // them stripped/re-derived anyway. Let CS run its own captive-portal/
        // validation probe and manage foreground state.

        if (!config.isMetered) {
            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
        }

        // Link speed from CrOS
        if (config.linkSpeed != null) {
            builder.setLinkDownstreamBandwidthKbps(config.linkSpeed.downlinkSpeedKbps);
            builder.setLinkUpstreamBandwidthKbps(config.linkSpeed.uplinkSpeedKbps);
        }

        return builder.build();
    }

    private static LinkProperties buildLinkProperties(NetworkConfiguration config) {
        LinkProperties lp = new LinkProperties();

        // Interface name
        if (config.arcNetworkInterface != null) {
            lp.setInterfaceName(config.arcNetworkInterface);
        }

        // MTU
        if (config.hostMtu > 0) {
            lp.setMtu(config.hostMtu);
        }

        // IPv4 address
        if (config.arcIpv4Address != null && !config.arcIpv4Address.isEmpty()) {
            try {
                int prefixLen = config.arcIpv4PrefixLength > 0 ? config.arcIpv4PrefixLength : 24;
                InetAddress addr = InetAddress.getByName(config.arcIpv4Address);
                lp.addLinkAddress(new LinkAddress(addr, prefixLen));
            } catch (UnknownHostException e) {
                Slog.w(TAG, "Invalid ARC IPv4 address: " + config.arcIpv4Address, e);
            }
        }

        // Subnet route (for on-link connectivity)
        if (config.arcIpv4Address != null && !config.arcIpv4Address.isEmpty()) {
            try {
                int prefixLen = config.arcIpv4PrefixLength > 0 ? config.arcIpv4PrefixLength : 24;
                InetAddress addr = InetAddress.getByName(config.arcIpv4Address);
                // Mask address to network prefix
                byte[] addrBytes = addr.getAddress();
                int mask = 0xFFFFFFFF << (32 - prefixLen);
                addrBytes[0] &= (byte) (mask >> 24);
                addrBytes[1] &= (byte) (mask >> 16);
                addrBytes[2] &= (byte) (mask >> 8);
                addrBytes[3] &= (byte) mask;
                InetAddress network = InetAddress.getByAddress(addrBytes);
                lp.addRoute(new RouteInfo(
                        new IpPrefix(network, prefixLen),
                        null, config.arcNetworkInterface, RouteInfo.RTN_UNICAST));
            } catch (UnknownHostException e) {
                Slog.w(TAG, "Failed to add subnet route", e);
            }
        }

        // IPv4 gateway / default route
        if (config.arcIpv4Gateway != null && !config.arcIpv4Gateway.isEmpty()) {
            try {
                InetAddress gateway = InetAddress.getByName(config.arcIpv4Gateway);
                lp.addRoute(new RouteInfo(
                        new IpPrefix(InetAddress.getByName("0.0.0.0"), 0),
                        gateway, config.arcNetworkInterface, RouteInfo.RTN_UNICAST));
            } catch (UnknownHostException e) {
                Slog.w(TAG, "Invalid ARC IPv4 gateway: " + config.arcIpv4Gateway, e);
            }
        }

        // DNS servers — use DNS proxy addresses if available, else host DNS.
        //
        // Skip any server whose address family this network has no address for.
        // Chrome hands us the host's full resolver list, which on an IPv6-capable
        // LAN includes the router's link-local address — but everything built
        // above is IPv4-only (arcIpv4Address / arcIpv4Gateway; no IPv6 address or
        // route is ever added), so netd's per-network table has no IPv6 route and
        // every connection to such a server fails: ENETUNREACH for a global v6
        // address, EINVAL for a link-local one. The link-local case cannot be
        // fixed by scoping either — LinkProperties has nowhere to carry the
        // sin6_scope_id, so netd only ever sees a bare "fe80::..." which is not a
        // valid connect() target.
        //
        // Left in the list, such a server answers no queries (netd reported
        // "<no data>" for it against 23/23 successes on the v4 server) and
        // opportunistic private DNS re-probes it on every network change:
        //     W resolv: Validating DnsTlsServer fe80::... with mark 0xf0064
        //     W resolv: Socket failed to connect: Invalid argument
        //     W resolv: TCP Handshake failed: 22
        // If ARC ever gets real IPv6 (an IPv6 LinkAddress plus routes), matching
        // servers start being accepted again with no further change here.
        boolean haveIpv4 = false;
        boolean haveIpv6 = false;
        for (LinkAddress la : lp.getLinkAddresses()) {
            if (la.getAddress() instanceof Inet4Address) {
                haveIpv4 = true;
            } else if (la.getAddress() instanceof Inet6Address) {
                haveIpv6 = true;
            }
        }
        // With no address at all there is nothing to judge reachability against,
        // so fall back to accepting whatever Chrome sent rather than dropping
        // every server and leaving the network with no resolver.
        boolean filterByFamily = haveIpv4 || haveIpv6;

        String[] dnsAddrs = config.dnsProxyAddresses;
        if (dnsAddrs == null || dnsAddrs.length == 0) {
            dnsAddrs = config.hostDnsAddresses;
        }
        if (dnsAddrs != null) {
            for (String dns : dnsAddrs) {
                if (dns != null && !dns.isEmpty()) {
                    try {
                        InetAddress dnsAddr = InetAddress.getByName(dns);
                        if (filterByFamily) {
                            boolean reachable = (dnsAddr instanceof Inet4Address)
                                    ? haveIpv4 : haveIpv6;
                            if (!reachable) {
                                Slog.i(TAG, "Skipping unreachable DNS server " + dns
                                        + " — " + config.arcNetworkInterface
                                        + " has no address of that family");
                                continue;
                            }
                        }
                        lp.addDnsServer(dnsAddr);
                    } catch (UnknownHostException e) {
                        Slog.w(TAG, "Invalid DNS server: " + dns, e);
                    }
                }
            }
        }

        // Search domains
        if (config.hostSearchDomains != null && config.hostSearchDomains.length > 0) {
            StringBuilder domains = new StringBuilder();
            for (String domain : config.hostSearchDomains) {
                if (domain != null && !domain.isEmpty()) {
                    if (domains.length() > 0) domains.append(",");
                    domains.append(domain);
                }
            }
            if (domains.length() > 0) {
                lp.setDomains(domains.toString());
            }
        }

        return lp;
    }

    private static NetworkAgentConfig buildAgentConfig(NetworkConfiguration config) {
        NetworkAgentConfig.Builder builder = new NetworkAgentConfig.Builder();

        switch (config.type) {
            case NetworkType.WIFI:
                builder.setLegacyType(ConnectivityManager.TYPE_WIFI);
                builder.setLegacyTypeName("WIFI");
                if (config.wifi != null && config.wifi.hexSsid != null) {
                    builder.setLegacyExtraInfo(config.wifi.hexSsid);
                }
                break;
            case NetworkType.ETHERNET:
                builder.setLegacyType(ConnectivityManager.TYPE_ETHERNET);
                builder.setLegacyTypeName("ETHERNET");
                break;
            case NetworkType.CELLULAR:
                builder.setLegacyType(ConnectivityManager.TYPE_MOBILE);
                builder.setLegacyTypeName("MOBILE");
                break;
            default:
                builder.setLegacyType(ConnectivityManager.TYPE_WIFI);
                builder.setLegacyTypeName("WIFI");
                break;
        }

        return builder.build();
    }

    private static boolean isConnected(NetworkConfiguration config) {
        return config.connectionState == ConnectionStateType.CONNECTED
                || config.connectionState == ConnectionStateType.ONLINE
                || config.connectionState == ConnectionStateType.PORTAL;
    }
}

package com.android.server.arc.net;

import com.android.server.arc.net.IArcHostVpnServiceCallback;

interface IArcHostVpnService {
    void connect(in String[] addresses, in String[] routes, in String[] dnsServers,
            int mtu, int socketFd, in byte[] hostMacAddress, int ifindex, int protocol);
    void disconnect();
    void registerCallback(IArcHostVpnServiceCallback callback);
}

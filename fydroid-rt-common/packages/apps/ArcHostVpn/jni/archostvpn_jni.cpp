#include <jni.h>
#include <errno.h>
#include <pthread.h>
#include <signal.h>
#include <string.h>
#include <unistd.h>

#include <arpa/inet.h>
#include <linux/if_ether.h>
#include <linux/if_packet.h>
#include <sys/socket.h>

#include <android/log.h>

#define LOG_TAG "ArcHostVpnJNI"
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static constexpr size_t kBufSize = 4096;

// Thread IDs for signal-based cancellation
static pthread_t sTunToHostThread = 0;
static pthread_t sHostToTunThread = 0;

static void signalHandler(int /* sig */) {
    // Empty handler — just need to interrupt blocking read/recvfrom
}

static void installSignalHandler() {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_handler = signalHandler;
    sa.sa_flags = 0;  // No SA_RESTART — we want reads to return EINTR
    sigaction(SIGUSR1, &sa, nullptr);
}

extern "C" JNIEXPORT void JNICALL
Java_org_chromium_arc_hostvpn_ArcHostVpnService_nativeForwardTunToHost(
        JNIEnv* env, jclass /* clazz */, jobject /* lock */,
        jint tunFd, jint socketFd, jint protocol, jint ifindex,
        jbyteArray hostMacAddress) {
    installSignalHandler();
    sTunToHostThread = pthread_self();

    jbyte* mac = env->GetByteArrayElements(hostMacAddress, nullptr);
    jsize macLen = env->GetArrayLength(hostMacAddress);

    uint8_t buf[kBufSize];

    // Prepare sockaddr_ll template
    struct sockaddr_ll addr;
    memset(&addr, 0, sizeof(addr));
    addr.sll_family = AF_PACKET;
    addr.sll_protocol = htons(protocol);
    addr.sll_ifindex = ifindex;
    addr.sll_halen = (macLen <= 8) ? macLen : 8;
    memcpy(addr.sll_addr, mac, addr.sll_halen);

    env->ReleaseByteArrayElements(hostMacAddress, mac, JNI_ABORT);

    ALOGI("TUN→Host: started (tunFd=%d, socketFd=%d, ifindex=%d)", tunFd, socketFd, ifindex);

    while (true) {
        ssize_t len = read(tunFd, buf, sizeof(buf));
        if (len < 0) {
            if (errno == EINTR) continue;
            ALOGE("TUN→Host: read error: %s", strerror(errno));
            break;
        }
        if (len == 0) {
            ALOGI("TUN→Host: TUN fd closed");
            break;
        }

        ssize_t sent = sendto(socketFd, buf, len, 0,
                reinterpret_cast<struct sockaddr*>(&addr), sizeof(addr));
        if (sent < 0) {
            if (errno == EINTR) continue;
            ALOGE("TUN→Host: sendto error: %s", strerror(errno));
            break;
        }
    }

    sTunToHostThread = 0;
    ALOGI("TUN→Host: exiting");
}

extern "C" JNIEXPORT void JNICALL
Java_org_chromium_arc_hostvpn_ArcHostVpnService_nativeForwardHostToTun(
        JNIEnv* /* env */, jclass /* clazz */, jobject /* lock */,
        jint tunFd, jint socketFd, jint ifindex) {
    installSignalHandler();
    sHostToTunThread = pthread_self();

    uint8_t buf[kBufSize];

    // Filter by ifindex
    struct sockaddr_ll srcAddr;
    socklen_t addrLen;

    ALOGI("Host→TUN: started (tunFd=%d, socketFd=%d, ifindex=%d)", tunFd, socketFd, ifindex);

    while (true) {
        addrLen = sizeof(srcAddr);
        memset(&srcAddr, 0, sizeof(srcAddr));

        ssize_t len = recvfrom(socketFd, buf, sizeof(buf), 0,
                reinterpret_cast<struct sockaddr*>(&srcAddr), &addrLen);
        if (len < 0) {
            if (errno == EINTR) continue;
            ALOGE("Host→TUN: recvfrom error: %s", strerror(errno));
            break;
        }
        if (len == 0) {
            ALOGI("Host→TUN: socket closed");
            break;
        }

        // Filter packets for our interface
        if (srcAddr.sll_ifindex != ifindex) {
            continue;
        }

        // Strip L2 ethernet header (14 bytes)
        if (len <= ETH_HLEN) {
            continue;
        }

        ssize_t written = write(tunFd, buf + ETH_HLEN, len - ETH_HLEN);
        if (written < 0) {
            if (errno == EINTR) continue;
            ALOGE("Host→TUN: write error: %s", strerror(errno));
            break;
        }
    }

    sHostToTunThread = 0;
    ALOGI("Host→TUN: exiting");
}

extern "C" JNIEXPORT void JNICALL
Java_org_chromium_arc_hostvpn_ArcHostVpnService_nativeSignalBlockedThreads(
        JNIEnv* /* env */, jclass /* clazz */, jobject /* lock */) {
    ALOGI("Signaling blocked forwarding threads");
    pthread_t t1 = sTunToHostThread;
    pthread_t t2 = sHostToTunThread;
    if (t1 != 0) {
        pthread_kill(t1, SIGUSR1);
    }
    if (t2 != 0) {
        pthread_kill(t2, SIGUSR1);
    }
}

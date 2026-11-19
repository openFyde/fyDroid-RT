#include <unistd.h>

#include <algorithm>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <mutex>
#include <random>
#include <thread>

#include <base/logging.h>
#include <hidl/HidlTransportSupport.h>

#include <arc/MojoProcessSupport.h>
#include "ArcBridgeInstance.h"
#include "ArcBridgeHidlService.h"

namespace {

// Retry parameters. The 4 s cap matches the Chrome-side reconnect cap so
// neither side waits much longer than the other after a stage-one
// disconnect. ±25% jitter on every sleep keeps the two sides from locking
// into the same retry phase — the in-container side and the Chrome side
// have independent RNGs, so even if they start the loop at the same
// instant they drift apart within a couple of attempts.
constexpr int kInitialDelayMs = 250;
constexpr int kMaxDelayMs = 4000;
constexpr double kJitterFraction = 0.25;
constexpr int kSilentStatusEveryAttempts = 60;  // ~1 hint per ~3 minutes at cap

// Apply ±jitter_fraction randomization to a base delay. Uses a thread-local
// random_device-seeded mt19937 so the daemon doesn't share rand() state with
// anything else and so multiple devices don't share a seed.
int jitter(int base_ms) {
    static thread_local std::mt19937 rng{std::random_device{}()};
    const int range = static_cast<int>(base_ms * kJitterFraction);
    if (range <= 0) {
        return base_ms;
    }
    std::uniform_int_distribution<int> dist(-range, range);
    return std::max(1, base_ms + dist(rng));
}

void sleepMs(int ms) {
    std::this_thread::sleep_for(std::chrono::milliseconds(ms));
}

// Retry connect() with capped exp backoff (kInitialDelayMs → kMaxDelayMs)
// plus jitter. Blocks until connect() returns true. Logs once at first
// failure and at sparse intervals after that — connect-refused while
// waiting for sign-in is normal, not an error to drown logcat with.
void connectWithBackoff(const android::sp<arc::ArcBridgeInstance>& instance,
                        const char* phase) {
    int attempt = 0;
    int delay_ms = kInitialDelayMs;
    while (!instance->connect()) {
        ++attempt;
        if (attempt == 1) {
            LOG(INFO) << phase << ": Chrome ARC bridge socket not ready; "
                      << "retrying with " << kInitialDelayMs << "ms→"
                      << kMaxDelayMs << "ms backoff (±" << int(kJitterFraction * 100)
                      << "% jitter)";
        } else if (attempt % kSilentStatusEveryAttempts == 0) {
            LOG(INFO) << phase << ": still waiting (" << attempt
                      << " attempts, current delay " << delay_ms << "ms)";
        }
        sleepMs(jitter(delay_ms));
        delay_ms = std::min(delay_ms * 2, kMaxDelayMs);
    }
    if (attempt == 0) {
        LOG(INFO) << phase << ": connected on first attempt";
    } else {
        LOG(INFO) << phase << ": connected after " << attempt
                  << " retr" << (attempt == 1 ? "y" : "ies");
    }
}

}  // namespace

int main(int /* argc */, char* /* argv */[]) {
    LOG(INFO) << "Starting ARC bridge service";

    // Step 1: Initialize Mojo via MojoProcessSupport singleton.
    arc::MojoProcessSupport* mojo_support = arc::MojoProcessSupport::GetInstance();
    arc::MojoThread* mojo_thread = mojo_support->getThread();

    // Step 2: Create ArcBridgeInstance and wire up the disconnect signal.
    //
    // Chrome OS opens the listening side of /var/run/chrome/arc_bridge.sock
    // only after a user signs in and session_manager finishes
    // UpgradeArcContainer. The Android container's init reaches the bridge
    // service much earlier (`class hal` during early boot), so we lose the
    // race on every boot. Worse, the user may stay at the Chrome OS login
    // screen for hours before signing in — connectWithBackoff() retries
    // forever, with capped exp backoff and jitter to avoid lock-stepping
    // with the Chrome-side accept loop.
    //
    // After a successful connect, the Mojo root_host_ may still disconnect
    // later: Chrome OS restarts, the container is upgrade-cycled, the user
    // signs out and back in. The bridge daemon must heal on its own —
    // tearing down all per-client forwarders and reconnecting — without
    // restarting the process, because HIDL registration would otherwise
    // need to be re-done and Android-side clients holding the
    // IArcBridgeService binder reference would see it disappear.
    //
    // Reconnect signaling: the Mojo error handler in ArcBridgeInstance
    // fires the setOnDisconnect callback on the Mojo thread, which sets
    // need_reconnect_ and notifies the main thread. The main thread is
    // parked on reconnect_cv_ for the lifetime of the process; it never
    // joins the HIDL threadpool (joinRpcThreadpool would otherwise block
    // forever and the wake-up path would be unreachable).
    android::sp<arc::ArcBridgeInstance> instance(
        new arc::ArcBridgeInstance(mojo_thread));

    std::mutex reconnect_mutex;
    std::condition_variable reconnect_cv;
    bool need_reconnect = false;

    instance->setOnDisconnect([&]() {
        {
            std::lock_guard<std::mutex> lock(reconnect_mutex);
            need_reconnect = true;
        }
        reconnect_cv.notify_all();
    });

    // Step 3: Initial connect (blocks until Chrome ready).
    connectWithBackoff(instance, "initial connect");

    // Step 4: Create the HIDL service wrapper. The wrapper holds a strong
    // pointer to `instance` so subsequent disconnect/connect cycles inside
    // `instance` are transparent to it.
    auto hidl_service = new vendor::google_arc::arcbridge::V1_0::
        implementation::ArcBridgeService(instance);

    // Configure threadpool. callerWillJoin=false so main thread is free to
    // run the reconnect monitor loop; the binder threads keep the process
    // alive on their own.
    android::hardware::configureRpcThreadpool(2, false /* callerWillJoin */);

    auto status = hidl_service->registerAsService("default");
    if (status != android::OK) {
        LOG(FATAL) << "Failed starting the ArcBridgeService HAL";
        return 1;
    }
    LOG(INFO) << "Registered IArcBridgeService/default";

    // Step 5: Reconnect monitor loop. Parks until the disconnect callback
    // sets need_reconnect, then drains the Mojo state and reconnects.
    while (true) {
        std::unique_lock<std::mutex> lock(reconnect_mutex);
        reconnect_cv.wait(lock, [&]{ return need_reconnect; });
        need_reconnect = false;
        lock.unlock();

        LOG(WARNING) << "Bridge dropped, tearing down forwarders and "
                     << "reconnecting";
        instance->disconnect();
        connectWithBackoff(instance, "reconnect");
        LOG(INFO) << "Bridge reconnected; existing in-container clients "
                  << "must re-request mojo bootstrap channels";
    }

    return 0;
}

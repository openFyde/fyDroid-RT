/*
 * Copyright (C) 2024 The FydeOS Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// WaylandDataDevice: Clipboard bridge between Chrome OS and Android.
//
// Chrome→Android flow:
//   1. data_offer event → store new wl_data_offer, add listener
//   2. offer event → check for text/plain mime
//   3. selection event → pipe read clipboard text → notify Java callback
//
// Android→Chrome flow:
//   1. Java calls binder transaction 30 with text
//   2. setClipboardText() → create wl_data_source → set_selection
//   3. Chrome reads via source.send callback → write text to fd

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandDataDevice"

#include "WaylandDataDevice.h"

#include <errno.h>
#include <unistd.h>

#include <binder/Parcel.h>
#include <log/log.h>
#include "WaylandLog.h"
#include <wayland-client.h>

namespace android {

WaylandDataDevice* WaylandDataDevice::sInstance = nullptr;

// --- wl_data_offer listener ---

static void dataOfferOffer(void* data, struct wl_data_offer* offer,
                           const char* mimeType) {
    static_cast<WaylandDataDevice*>(data)->onOfferMimeType(offer, mimeType);
}

static void dataOfferSourceActions(void*, struct wl_data_offer*, uint32_t) {}
static void dataOfferAction(void*, struct wl_data_offer*, uint32_t) {}

static const struct wl_data_offer_listener sDataOfferListener = {
    .offer = dataOfferOffer,
    .source_actions = dataOfferSourceActions,
    .action = dataOfferAction,
};

// --- wl_data_device listener ---

static void dataDeviceDataOffer(void* data, struct wl_data_device*,
                                struct wl_data_offer* offer) {
    static_cast<WaylandDataDevice*>(data)->onDataOffer(offer);
}

static void dataDeviceEnter(void*, struct wl_data_device*, uint32_t,
                            struct wl_surface*, wl_fixed_t, wl_fixed_t,
                            struct wl_data_offer*) {}
static void dataDeviceLeave(void*, struct wl_data_device*) {}
static void dataDeviceMotion(void*, struct wl_data_device*, uint32_t,
                             wl_fixed_t, wl_fixed_t) {}
static void dataDeviceDrop(void*, struct wl_data_device*) {}

static void dataDeviceSelection(void* data, struct wl_data_device*,
                                struct wl_data_offer* offer) {
    static_cast<WaylandDataDevice*>(data)->onSelection(offer);
}

static const struct wl_data_device_listener sDataDeviceListener = {
    .data_offer = dataDeviceDataOffer,
    .enter = dataDeviceEnter,
    .leave = dataDeviceLeave,
    .motion = dataDeviceMotion,
    .drop = dataDeviceDrop,
    .selection = dataDeviceSelection,
};

// --- wl_data_source listener ---

static void dataSourceTarget(void*, struct wl_data_source*, const char*) {}

static void dataSourceSend(void* data, struct wl_data_source*,
                           const char* mimeType, int32_t fd) {
    static_cast<WaylandDataDevice*>(data)->onSourceSend(mimeType, fd);
}

static void dataSourceCancelled(void* data, struct wl_data_source*) {
    static_cast<WaylandDataDevice*>(data)->onSourceCancelled();
}

static void dataSourceDndDropPerformed(void*, struct wl_data_source*) {}
static void dataSourceDndFinished(void*, struct wl_data_source*) {}
static void dataSourceAction(void*, struct wl_data_source*, uint32_t) {}

static const struct wl_data_source_listener sDataSourceListener = {
    .target = dataSourceTarget,
    .send = dataSourceSend,
    .cancelled = dataSourceCancelled,
    .dnd_drop_performed = dataSourceDndDropPerformed,
    .dnd_finished = dataSourceDndFinished,
    .action = dataSourceAction,
};

// --- WaylandDataDevice implementation ---

WaylandDataDevice::WaylandDataDevice() = default;

WaylandDataDevice::~WaylandDataDevice() {
    sInstance = nullptr;
    if (mSource) {
        wl_data_source_destroy(mSource);
    }
    if (mCurrentOffer) {
        wl_data_offer_destroy(mCurrentOffer);
    }
    if (mDevice) {
        wl_data_device_destroy(mDevice);
    }
}

WaylandDataDevice* WaylandDataDevice::instance() {
    return sInstance;
}

void WaylandDataDevice::init(wl_data_device_manager* manager, wl_seat* seat,
                              wl_display* display) {
    mDisplay = display;
    mManager = manager;
    mDevice = wl_data_device_manager_get_data_device(manager, seat);
    wl_data_device_add_listener(mDevice, &sDataDeviceListener, this);
    sInstance = this;
}

void WaylandDataDevice::setClipboardText(const std::string& text) {
    if (text.empty()) return;
    std::lock_guard<std::mutex> lock(mMutex);
    mPendingText = text;
}

void WaylandDataDevice::setClipboardMimes(const std::vector<std::string>& mimeTypes) {
    std::lock_guard<std::mutex> lock(mMutex);
    mPendingMimes = mimeTypes;
    mPendingMimesSet = true;
}

void WaylandDataDevice::processPending() {
    // Check for pending MIME types (from ArcDataDeviceService, rich clipboard)
    std::vector<std::string> mimes;
    std::string text;
    bool rich = false;
    {
        std::lock_guard<std::mutex> lock(mMutex);
        if (mPendingMimesSet) {
            rich = true;
            mimes = std::move(mPendingMimes);
            mPendingMimes.clear();
            mPendingMimesSet = false;
        } else if (!mPendingText.empty()) {
            text = std::move(mPendingText);
            mPendingText.clear();
        } else {
            return;
        }
    }

    if (!mDevice || !mDisplay || !mManager) {
        ALOGW("processPending: not initialized");
        return;
    }

    // Destroy previous source if any
    if (mSource) {
        wl_data_source_destroy(mSource);
        mSource = nullptr;
    }

    mSource = wl_data_device_manager_create_data_source(mManager);
    wl_data_source_add_listener(mSource, &sDataSourceListener, this);

    mSourceEmpty = rich && mimes.empty();
    if (mSourceEmpty) {
        // exo ignores a source with no MIME types (its clipboard writer skips
        // an empty write), leaving the previous copy in Chrome. Offer text and
        // send nothing, so Chrome holds an empty clipboard instead.
        wl_data_source_offer(mSource, "text/plain;charset=utf-8");
        ALOGI("processPending: clip has no type Chrome takes, clearing Chrome's clipboard");
    } else if (rich) {
        // Rich clipboard: offer all MIME types from ArcDataDeviceService
        for (const auto& mime : mimes) {
            wl_data_source_offer(mSource, mime.c_str());
        }
        ALOGI("processPending: created data source with %zu MIME types", mimes.size());
    } else {
        // Simple text clipboard
        std::lock_guard<std::mutex> lock(mMutex);
        mOutgoingText = text;
        mLastSentText = text;
        wl_data_source_offer(mSource, "text/plain;charset=utf-8");
        wl_data_source_offer(mSource, "text/plain");
        wl_data_source_offer(mSource, "UTF8_STRING");
    }

    // Arm the echo guard: the compositor sends this selection right back to us.
    {
        std::lock_guard<std::mutex> lock(mMutex);
        mSelfSelectionPending = true;
        mSelfSelectionTime = systemTime(SYSTEM_TIME_MONOTONIC);
    }

    wl_data_device_set_selection(mDevice, mSource, mLastSerial);
    wl_display_flush(mDisplay);
}

void WaylandDataDevice::updateSerial(uint32_t serial) {
    mLastSerial = serial;
}

void WaylandDataDevice::setClipboardCallback(const sp<IBinder>& callback) {
    std::lock_guard<std::mutex> lock(mMutex);
    mCallback = callback;
}

// --- Wayland event handlers ---

void WaylandDataDevice::onDataOffer(wl_data_offer* offer) {
    // New offer arriving — destroy any previous one we were tracking
    if (mCurrentOffer && mCurrentOffer != offer) {
        wl_data_offer_destroy(mCurrentOffer);
        mOfferMimeTypesMap.erase(mCurrentOffer);
    }
    mCurrentOffer = offer;
    mOfferHasText = false;
    mOfferMimeTypesMap[offer].clear();
    wl_data_offer_add_listener(offer, &sDataOfferListener, this);
}

void WaylandDataDevice::onOfferMimeType(wl_data_offer* offer, const char* mimeType) {
    if (offer != mCurrentOffer) return;
    // Track all MIME types for ArcDataDeviceService
    mOfferMimeTypesMap[offer].push_back(mimeType);
    // Also check for text (legacy flow)
    if (strcmp(mimeType, "text/plain;charset=utf-8") == 0 ||
        strcmp(mimeType, "text/plain") == 0 ||
        strcmp(mimeType, "UTF8_STRING") == 0) {
        mOfferHasText = true;
    }
}

void WaylandDataDevice::onSelection(wl_data_offer* offer) {
    if (!offer) {
        // Selection cleared
        return;
    }
    if (offer != mCurrentOffer) {
        ALOGW("onSelection: offer mismatch");
        return;
    }

    // Echo prevention: swallow the selection the compositor bounces back after
    // our own set_selection. The legacy text path below compares against
    // mLastSentText; the rich path has no content to compare (the data lives
    // behind the offer), so consume the one event our own set_selection caused.
    if (consumeSelfSelection()) {
        ALOGI("onSelection: echo of our own selection, skipping");
        wl_data_offer_destroy(offer);
        mOfferMimeTypesMap.erase(offer);
        mCurrentOffer = nullptr;
        return;
    }

    // Try the ArcDataDeviceService flow first (rich clipboard)
    sp<IBinder> service = getArcDataDeviceService();
    if (service != nullptr) {
        auto it = mOfferMimeTypesMap.find(offer);
        if (it != mOfferMimeTypesMap.end() && !it->second.empty()) {
            int offerId;
            {
                std::lock_guard<std::mutex> lock(mMutex);
                offerId = mNextOfferId++;
                mDataOffers[offerId] = offer;
            }
            callArcClipboardUpdate(it->second, offerId);
            // Don't destroy the offer — Java will call releaseDataOffer later
            mCurrentOffer = nullptr;
            return;
        }
    }

    // Legacy flow: read text directly
    if (!mOfferHasText) {
        ALOGVT("onSelection: no text mime offered");
        return;
    }
    readClipboardFromOffer();
}

void WaylandDataDevice::readClipboardFromOffer() {
    if (!mCurrentOffer || !mDisplay) return;

    int pipeFds[2];
    if (pipe(pipeFds) != 0) {
        ALOGE("readClipboardFromOffer: pipe() failed: %s", strerror(errno));
        return;
    }

    // Request the data — Chrome will write to pipeFds[1] (write end)
    // and we read from pipeFds[0] (read end).
    wl_data_offer_receive(mCurrentOffer, "text/plain;charset=utf-8", pipeFds[1]);
    close(pipeFds[1]);

    // Must flush so Chrome processes the receive request before we read
    wl_display_flush(mDisplay);

    // Read all data from the pipe
    std::string text;
    char buf[4096];
    while (true) {
        ssize_t n = read(pipeFds[0], buf, sizeof(buf));
        if (n > 0) {
            text.append(buf, n);
        } else if (n == 0) {
            break;  // EOF
        } else {
            if (errno == EINTR) continue;
            ALOGE("readClipboardFromOffer: read() failed: %s", strerror(errno));
            break;
        }
    }
    close(pipeFds[0]);

    // Destroy the offer — we're done with it
    wl_data_offer_destroy(mCurrentOffer);
    mCurrentOffer = nullptr;

    if (text.empty()) {
        ALOGVT("readClipboardFromOffer: empty text");
        return;
    }

    // Echo prevention: skip if this is text we just sent to Chrome
    {
        std::lock_guard<std::mutex> lock(mMutex);
        if (text == mLastSentText) {
            ALOGVT("readClipboardFromOffer: echo of our own text, skipping");
            return;
        }
    }

    notifyCallback(text);
}

void WaylandDataDevice::notifyCallback(const std::string& text) {
    sp<IBinder> callback;
    {
        std::lock_guard<std::mutex> lock(mMutex);
        callback = mCallback;
    }
    if (!callback) {
        ALOGW("notifyCallback: no callback registered");
        return;
    }

    // Send text to Java via binder transaction code 1
    Parcel data, reply;
    data.writeString16(String16(text.c_str(), text.size()));
    status_t err = callback->transact(1, data, &reply, IBinder::FLAG_ONEWAY);
    if (err != NO_ERROR) {
        ALOGE("notifyCallback: transact failed: %d", err);
    }
}

void WaylandDataDevice::onSourceSend(const char* mimeType, int fd) {
    if (mSourceEmpty) {
        close(fd);
        return;
    }

    // Try ArcDataDeviceService flow first (for rich clipboard)
    sp<IBinder> service = getArcDataDeviceService();
    if (service != nullptr) {
        // Call ArcDataDeviceService.onRequestClipboardData(mimeType, fd)
        // tx 8 = onRequestClipboardData, oneway
        Parcel data;
        data.writeInterfaceToken(
                String16("com.android.server.arc.data_device.IArcDataDeviceService"));
        data.writeString16(String16(mimeType));
        data.writeFileDescriptor(fd, false);  // don't take ownership yet
        status_t err = service->transact(8, data, nullptr, IBinder::FLAG_ONEWAY);
        close(fd);  // close our copy; kernel dup'd it for the receiver
        if (err == NO_ERROR) {
            return;
        }
        ALOGW("onSourceSend: transact to ArcDataDeviceService failed: %d, "
              "falling back to text", err);
    }

    // Legacy flow: write text directly
    std::string text;
    {
        std::lock_guard<std::mutex> lock(mMutex);
        text = mOutgoingText;
    }

    const char* ptr = text.c_str();
    size_t remaining = text.size();
    while (remaining > 0) {
        ssize_t n = write(fd, ptr, remaining);
        if (n > 0) {
            ptr += n;
            remaining -= n;
        } else if (n < 0) {
            if (errno == EINTR) continue;
            ALOGE("onSourceSend: write() failed: %s", strerror(errno));
            break;
        }
    }
    close(fd);
}

void WaylandDataDevice::onSourceCancelled() {
    // Chrome replaced our source with a new one (user copied something else).
    // Must destroy the source per protocol.
    if (mSource) {
        wl_data_source_destroy(mSource);
        mSource = nullptr;
    }
    // Our selection is gone, so anything that arrives next is genuinely
    // someone else's — don't let a stale guard swallow it.
    std::lock_guard<std::mutex> lock(mMutex);
    mSelfSelectionPending = false;
}

// Returns true if this selection event is the compositor echoing back the
// selection we just set, in which case the guard is consumed. The time bound
// makes a missed echo self-healing rather than permanently swallowing the next
// real clipboard change: in practice the echo lands within a few milliseconds.
bool WaylandDataDevice::consumeSelfSelection() {
    static constexpr nsecs_t kSelfSelectionWindowNs = 2 * 1000 * 1000 * 1000LL;  // 2s
    std::lock_guard<std::mutex> lock(mMutex);
    if (!mSelfSelectionPending) {
        return false;
    }
    mSelfSelectionPending = false;
    return systemTime(SYSTEM_TIME_MONOTONIC) - mSelfSelectionTime < kSelfSelectionWindowNs;
}

// --- Data offer management for ArcDataDeviceService ---

int WaylandDataDevice::receiveFromOffer(int offerId, const std::string& mimeType) {
    wl_data_offer* offer = nullptr;
    {
        std::lock_guard<std::mutex> lock(mMutex);
        auto it = mDataOffers.find(offerId);
        if (it == mDataOffers.end()) {
            ALOGE("receiveFromOffer: unknown offerId %d", offerId);
            return -1;
        }
        offer = it->second;
    }

    int pipeFds[2];
    if (pipe(pipeFds) != 0) {
        ALOGE("receiveFromOffer: pipe() failed: %s", strerror(errno));
        return -1;
    }

    // Request data from Chrome via the Wayland offer
    wl_data_offer_receive(offer, mimeType.c_str(), pipeFds[1]);
    close(pipeFds[1]);
    wl_display_flush(mDisplay);

    ALOGVT("receiveFromOffer: offerId=%d mime=%s readFd=%d",
          offerId, mimeType.c_str(), pipeFds[0]);
    return pipeFds[0];  // caller owns this fd
}

void WaylandDataDevice::releaseOffer(int offerId) {
    std::lock_guard<std::mutex> lock(mMutex);
    auto it = mDataOffers.find(offerId);
    if (it == mDataOffers.end()) {
        ALOGW("releaseOffer: unknown offerId %d", offerId);
        return;
    }
    wl_data_offer_destroy(it->second);
    mOfferMimeTypesMap.erase(it->second);
    mDataOffers.erase(it);
    ALOGVT("releaseOffer: offerId=%d", offerId);
}

sp<IBinder> WaylandDataDevice::getArcDataDeviceService() {
    std::lock_guard<std::mutex> lock(mMutex);
    if (mArcDataDeviceService == nullptr) {
        mArcDataDeviceService = defaultServiceManager()->checkService(
                String16("arc_data_device_service"));
    }
    return mArcDataDeviceService;
}

void WaylandDataDevice::callArcClipboardUpdate(
        const std::vector<std::string>& mimeTypes, int offerId) {
    sp<IBinder> service = getArcDataDeviceService();
    if (service == nullptr) {
        ALOGW("callArcClipboardUpdate: service not available");
        return;
    }

    // Call IArcDataDeviceService.onClipboardUpdate(String[] mimeTypes, int offerId)
    // tx 7 = onClipboardUpdate, oneway
    Parcel data;
    data.writeInterfaceToken(
            String16("com.android.server.arc.data_device.IArcDataDeviceService"));
    // Write String[] (count + individual strings)
    data.writeInt32(static_cast<int32_t>(mimeTypes.size()));
    for (const auto& mime : mimeTypes) {
        data.writeString16(String16(mime.c_str()));
    }
    data.writeInt32(offerId);
    status_t err = service->transact(7, data, nullptr, IBinder::FLAG_ONEWAY);
    if (err != NO_ERROR) {
        ALOGE("callArcClipboardUpdate: transact failed: %d", err);
    } else {
        ALOGI("callArcClipboardUpdate: sent %zu MIME types, offerId=%d",
              mimeTypes.size(), offerId);
    }
}

} // namespace android

#pragma clang diagnostic pop

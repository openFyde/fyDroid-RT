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

#pragma once

#include <map>
#include <mutex>
#include <string>
#include <vector>

#include <binder/IBinder.h>
#include <binder/IServiceManager.h>
#include <utils/RefBase.h>
#include <utils/Timers.h>

struct wl_data_device;
struct wl_data_device_manager;
struct wl_data_offer;
struct wl_data_source;
struct wl_display;
struct wl_seat;

namespace android {

// Bridges Wayland clipboard (wl_data_device_manager) with Android's
// ClipboardManager via binder IPC (ArcClipboardService / ArcDataDeviceService).
//
// Chrome→Android: wl_data_device selection events → read pipe → binder callback
// Android→Chrome: binder transaction → wl_data_source → set_selection
class WaylandDataDevice {
public:
    WaylandDataDevice();
    ~WaylandDataDevice();

    WaylandDataDevice(const WaylandDataDevice&) = delete;
    WaylandDataDevice& operator=(const WaylandDataDevice&) = delete;

    // Initialize with Wayland globals. Call after globals are finalized.
    void init(wl_data_device_manager* manager, wl_seat* seat, wl_display* display);

    // Queue clipboard text (Android→Chrome). Thread-safe; actual Wayland work
    // happens in processPending() on the event thread.
    void setClipboardText(const std::string& text);

    // Queue clipboard MIME types (Android→Chrome, from ArcDataDeviceService).
    // Creates a wl_data_source with these MIME types.
    void setClipboardMimes(const std::vector<std::string>& mimeTypes);

    // Process pending clipboard operations on the Wayland event thread.
    // Called from WaylandDisplay's event loop.
    void processPending();

    // --- Data offer management (for ArcDataDeviceService) ---

    // Receive data from a tracked offer. Creates a pipe, requests data via
    // wl_data_offer_receive, returns the read fd. Caller owns the fd.
    // Returns -1 on error.
    int receiveFromOffer(int offerId, const std::string& mimeType);

    // Release a tracked data offer.
    void releaseOffer(int offerId);

    // Register a binder callback for clipboard changes (Chrome→Android).
    // The callback receives transaction code 1 with a string parcel.
    void setClipboardCallback(const sp<IBinder>& callback);

    // Update the last known serial from input events (keyboard/pointer/touch).
    // Must be called from input handlers on the Wayland thread.
    void updateSerial(uint32_t serial);

    // Singleton accessor (same pattern as WaylandInputManager).
    static WaylandDataDevice* instance();

    // --- Wayland event handlers (called from static listeners) ---
    void onDataOffer(wl_data_offer* offer);
    void onSelection(wl_data_offer* offer);
    void onOfferMimeType(wl_data_offer* offer, const char* mimeType);
    void onSourceSend(const char* mimeType, int fd);
    void onSourceCancelled();

private:
    void readClipboardFromOffer();
    bool consumeSelfSelection();
    void notifyCallback(const std::string& text);
    void callArcClipboardUpdate(const std::vector<std::string>& mimeTypes, int offerId);
    sp<IBinder> getArcDataDeviceService();

    wl_display* mDisplay = nullptr;
    wl_data_device_manager* mManager = nullptr;
    wl_data_device* mDevice = nullptr;
    wl_data_source* mSource = nullptr;
    wl_data_offer* mCurrentOffer = nullptr;
    bool mOfferHasText = false;

    std::mutex mMutex;
    std::string mOutgoingText;  // text to send when Chrome asks (source.send)
    std::string mLastSentText;  // last text we set as selection (echo prevention)
    std::string mPendingText;   // queued from binder thread, processed on event thread
    std::vector<std::string> mPendingMimes;  // queued MIME types for data source
    // Set with mPendingMimes. An Android clip with no type Chrome can take
    // arrives as an empty list, which must still replace Chrome's selection.
    bool mPendingMimesSet = false;
    // Current source stands in for a clip Chrome can't take: it answers every
    // request with no data, so Chrome's clipboard ends up empty, not stale.
    bool mSourceEmpty = false;
    uint32_t mLastSerial = 0;   // last serial from input events for set_selection
    sp<IBinder> mCallback;

    // Echo prevention for the rich-clipboard path: the compositor hands our own
    // set_selection straight back to us as a selection event, which we would
    // otherwise forward to Android as if Chrome had copied something.
    // Armed by processPending(), consumed by the next selection event.
    bool mSelfSelectionPending = false;
    nsecs_t mSelfSelectionTime = 0;

    // Data offer tracking for ArcDataDeviceService
    int mNextOfferId = 1;
    std::map<int, wl_data_offer*> mDataOffers;  // offerId → offer
    std::map<wl_data_offer*, std::vector<std::string>> mOfferMimeTypesMap;
    sp<IBinder> mArcDataDeviceService;  // lazily resolved

    static WaylandDataDevice* sInstance;
};

} // namespace android

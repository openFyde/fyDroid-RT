/*
 * Copyright (C) 2026 Fyde Innovations
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

#include <chrono>
#include <memory>
#include <mutex>
#include <condition_variable>

#include <utils/StrongPointer.h>
#include <binder/IBinder.h>
#include <mojo/public/cpp/bindings/binding.h>

#include <arc/ArcService.h>
#include <arc/MojoProcessSupport.h>
#include <arc/ime.mojom.h>
#include <arc/arc_bridge.mojom.h>

namespace arc {

class ArcImeMojoService
        : public arc::ArcService,
          public arc::mojom::ImeInstance {
public:
    explicit ArcImeMojoService(arc::MojoProcessSupport* mojo_support);
    ~ArcImeMojoService() override;

    // --- arc::ArcService overrides ---
    void ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) override;
    void versionMismatch(unsigned int version) override;

    // --- arc::mojom::ImeInstance overrides ---
    void Init(arc::mojom::ImeHostPtr host_remote, const InitCallback& callback) override;
    void SetCompositionText(const std::string& text,
                            std::vector<arc::mojom::CompositionSegmentPtr> segments,
                            ::arc::mojom::RangePtr selection_range) override;
    void SetSelectionText(::arc::mojom::RangePtr selection) override;
    void ConfirmCompositionText() override;
    void InsertText(const std::string& text, int32_t new_cursor_position) override;
    void OnKeyboardAppearanceChanging(::arc::mojom::RectPtr new_bounds,
                                      bool is_available) override;
    void ExtendSelectionAndDelete(uint64_t before, uint64_t after) override;
    void SetComposingRegion(::arc::mojom::RangePtr range) override;

    // --- Called by ArcImeBinderService (Binder thread) ---

    // Blocks until Init() has been called and host_ is ready.
    void waitForHost();

    // Sends a key event to Chrome OS IME synchronously.
    // Posts to Mojo thread, blocks until result.
    bool sendKeyEvent(bool pressed, int32_t key_code,
                      bool is_shift_down, bool is_control_down,
                      bool is_alt_down, bool is_capslock_on,
                      uint32_t scan_code, bool is_alt_gr_down,
                      bool is_repeat);

    // Notifies Chrome that text input type changed (async, posts to Mojo thread).
    void sendTextInputTypeChanged(arc::mojom::TextInputType type,
                                  bool is_personalized_learning_allowed,
                                  int32_t flags);

    // Notifies Chrome that cursor rect changed.
    void sendCursorRectChanged(int32_t left, int32_t top,
                               int32_t right, int32_t bottom,
                               arc::mojom::CursorCoordinateSpace coord_space);

    // Notifies Chrome that the cursor rect changed, along with the text around
    // the cursor. Chrome's IME engine needs this to edit text it did not write
    // itself — without it, the virtual keyboard's backspace has nothing to
    // delete and only trims its own candidate buffer.
    void sendCursorRectChangedWithSurroundingText(
            int32_t left, int32_t top, int32_t right, int32_t bottom,
            uint32_t text_range_start, uint32_t text_range_end,
            const std::string& text_in_range,
            uint32_t selection_start, uint32_t selection_end,
            arc::mojom::CursorCoordinateSpace coord_space);

    // Notifies Chrome that composition was canceled.
    void sendCancelComposition();

    // Asks Chrome to show virtual keyboard if enabled.
    void sendShowVirtualKeyboard();

    // Set callback binder for forwarding ImeInstance calls to FydroidIME.
    void setCallback(android::sp<android::IBinder> callback);

private:
    arc::MojoProcessSupport* mojo_support_;

    mojo::InterfacePtr<arc::mojom::ImeHost> host_;
    mojo::Binding<arc::mojom::ImeInstance> binding_{this};

    std::mutex host_mutex_;
    std::condition_variable host_cv_;
    bool host_ready_ = false;

    // --- ImeHost reconnect. Mojo thread only. ---
    //
    // Chrome hands us an ImeHost exactly once, from Init(), in response to our
    // OnImeInstanceReady(). If that pipe ever drops there is no second Init and
    // nothing else re-establishes it, so the IME's whole Chrome path — keys,
    // composition, surrounding text — stays dead until ARC restarts. A dropped
    // pipe is not hypothetical: mojo closes the endpoint on any message it
    // fails to deserialise. Re-registering a fresh ImeInstance makes Chrome's
    // ConnectionHolder build a new ImeHost receiver and call Init() again,
    // turning a permanent failure into a brief one.
    //
    // The ArcBridgeHost we registered through. Owned by ArcBridgeSupport, which
    // main() heap-allocates and never frees, so it outlives this service.
    mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* bridge_host_ = nullptr;
    // Consecutive attempts without a connection that lasted; see
    // scheduleReconnect() for why a successful Init() alone does not reset it.
    int reconnect_attempts_ = 0;
    // When the current host_ was established, and whether there is one to
    // measure. Used to tell "recovered, then dropped again much later" (retry
    // promptly) from "keeps flapping" (back off).
    std::chrono::steady_clock::time_point host_connected_at_{};
    bool host_has_connected_ = false;
    // A re-registration is already queued; collapses the instance-side and
    // host-side error handlers both firing for one disconnect.
    bool reconnect_pending_ = false;

    // Binds a new ImeInstance endpoint and offers it to Chrome.
    void registerImeInstance();
    // Queues registerImeInstance() after a backoff.
    void scheduleReconnect();

    // Callback binder to FydroidIME (set via setCallback).
    std::mutex callback_mutex_;
    android::sp<android::IBinder> callback_;

    // Guards against double-committing text. sendKeyEvent() gives up waiting
    // for Chrome after kKeyEventTimeoutMs and tells FydroidIME "not consumed,"
    // which the IME takes as its cue to handle the key locally. But Chrome
    // may still be processing it, and InsertText/SetCompositionText/etc.
    // arrive on a separate Mojo call with no id tying them back to a specific
    // key — so a belated result lands with nothing to say "this is stale."
    // Armed on timeout, consumed by the next doc-mutating ImeInstance call
    // within the window (presumed to be that key's delayed effect, dropped
    // instead of forwarded), and disarmed early if Chrome's own late reply
    // says the key genuinely wasn't consumed. See sendKeyEvent().
    std::mutex timeout_guard_mutex_;
    bool timeout_guard_armed_ = false;
    std::chrono::steady_clock::time_point timeout_guard_armed_at_;

    // Arms the guard after a local sendKeyEvent() timeout.
    void armTimeoutGuard();
    // Clears the guard early once we learn for certain no InsertText follows.
    void disarmTimeoutGuard();
    // Consumes the guard if it is armed and still within its window. Callers
    // that get true must drop the call instead of forwarding it to FydroidIME.
    bool consumeTimeoutGuard();
};

}  // namespace arc

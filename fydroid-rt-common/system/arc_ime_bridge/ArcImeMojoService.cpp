#include "ArcImeMojoService.h"

#include <algorithm>
#include <chrono>
#include <mutex>
#include <condition_variable>

#include <base/bind.h>
#include <base/logging.h>
#include <base/task_runner.h>
#include <base/time/time.h>
#include <binder/Parcel.h>

namespace arc {

// Binder callback transaction codes (arc_ime_bridge → FydroidIME).
enum CallbackTransaction : uint32_t {
    CB_INSERT_TEXT = 1,
    CB_SET_COMPOSITION_TEXT = 2,
    CB_CONFIRM_COMPOSITION_TEXT = 3,
    CB_EXTEND_SELECTION_AND_DELETE = 4,
    CB_SET_SELECTION_TEXT = 5,
    CB_SET_COMPOSING_REGION = 6,
    CB_ON_KEYBOARD_APPEARANCE = 7,
};

// Every call from FydroidIME arrives on a binder thread while the IME's main
// thread blocks on the transaction, so nothing here may wait on Chrome
// indefinitely — a Chrome that never answers must degrade to local handling,
// not to a dead IME. Generous enough that a healthy round-trip never trips it.
constexpr int kKeyEventTimeoutMs = 200;
constexpr int kHostConnectTimeoutMs = 1000;

// How long after a sendKeyEvent() timeout a doc-mutating ImeInstance call is
// still presumed to be that key's delayed effect. Wider than the timeout
// itself to cover a Chrome that was merely slow, not hung; see
// timeout_guard_* in the header for why this exists at all.
constexpr int kTimeoutGuardWindowMs = 2 * kKeyEventTimeoutMs;

// ImeHost reconnect backoff: 500ms, doubling to an 8s ceiling.
constexpr int kReconnectBaseDelayMs = 500;
constexpr int kReconnectMaxBackoffShift = 4;
// How long a connection must survive to count as healthy, and so to earn the
// backoff a fresh start when it later drops. See scheduleReconnect().
constexpr int kReconnectStableMs = 5000;

// ---------------------------------------------------------------------------
// Constructor / Destructor
// ---------------------------------------------------------------------------

ArcImeMojoService::ArcImeMojoService(arc::MojoProcessSupport* mojo_support)
        : mojo_support_(mojo_support) {
}

ArcImeMojoService::~ArcImeMojoService() {
    if (binding_.is_bound()) {
        binding_.Close();
    }
}

// ---------------------------------------------------------------------------
// arc::ArcService overrides
// ---------------------------------------------------------------------------

void ArcImeMojoService::ready(
        mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) {
    LOG(INFO) << "ArcImeMojoService: ARC bridge ready";

    bridge_host_ = host;
    reconnect_attempts_ = 0;
    registerImeInstance();
}

void ArcImeMojoService::registerImeInstance() {
    if (!bridge_host_) return;
    reconnect_pending_ = false;

    // Re-registering: drop the previous endpoint first, since Bind() requires
    // an unbound Binding. Clear the error handler beforehand so our own Close()
    // is not mistaken for Chrome hanging up and does not queue another retry.
    if (binding_.is_bound()) {
        binding_.set_connection_error_handler(base::OnceClosure());
        binding_.Close();
    }

    mojo::InterfacePtr<arc::mojom::ImeInstance> instance_ptr;
    binding_.Bind(mojo::MakeRequest(&instance_ptr));

    binding_.set_connection_error_handler(base::BindOnce([](ArcImeMojoService* self) {
        LOG(WARNING) << "ArcImeMojoService: Mojo binding lost";
        {
            std::lock_guard<std::mutex> lock(self->host_mutex_);
            self->host_.reset();
            self->host_ready_ = false;
        }
        self->scheduleReconnect();
    }, this));

    // Chrome's ConnectionHolder treats this as a new instance: it drops the
    // stale connection, binds a fresh ImeHost receiver and calls Init() with
    // it. That Init is what revives host_.
    (*bridge_host_)->OnImeInstanceReady(std::move(instance_ptr));
    LOG(INFO) << "ArcImeMojoService: registered ImeInstance with Chrome OS";
}

void ArcImeMojoService::scheduleReconnect() {
    if (!bridge_host_ || reconnect_pending_) return;
    reconnect_pending_ = true;

    // Only a connection that LASTED earns a fresh backoff. Resetting on Init()
    // alone would keep retrying at the floor forever if something reclaims the
    // registration as fast as we take it back — two arc_ime_bridge processes
    // both recovering from each other do exactly that, and would ping-pong at
    // 2Hz for as long as both live. Measuring uptime instead lets a genuine
    // one-off drop retry promptly while a flap decays to the ceiling.
    if (host_has_connected_) {
        const auto uptime = std::chrono::steady_clock::now() - host_connected_at_;
        if (uptime >= std::chrono::milliseconds(kReconnectStableMs)) {
            reconnect_attempts_ = 0;
        }
        host_has_connected_ = false;
    }

    // Back off: a message Chrome refuses closes the endpoint the instant it is
    // sent, so retrying immediately and unconditionally could spin. Never give
    // up though — the alternative is an IME with no Chrome connection for the
    // rest of the session, which is the failure this exists to end.
    // No log line here: the retry is already legible as the gap between the
    // "ImeHost connection lost" warning and the "registered ImeInstance"
    // /"Init - ImeHost connected" pair that follow it.
    const int shift = std::min(reconnect_attempts_, kReconnectMaxBackoffShift);
    const int delay_ms = kReconnectBaseDelayMs << shift;
    ++reconnect_attempts_;

    mojo_support_->getThread()->getTaskRunner()->PostDelayedTask(
        FROM_HERE,
        base::BindOnce(&ArcImeMojoService::registerImeInstance,
                       base::Unretained(this)),
        base::TimeDelta::FromMilliseconds(delay_ms));
}

void ArcImeMojoService::versionMismatch(unsigned int version) {
    LOG(ERROR) << "ArcImeMojoService: version mismatch - Chrome OS supports up to v"
               << version << ", we require v" << arc::mojom::ImeInstance::Version_;
}

// ---------------------------------------------------------------------------
// arc::mojom::ImeInstance overrides
// ---------------------------------------------------------------------------

void ArcImeMojoService::Init(
        arc::mojom::ImeHostPtr host_remote,
        const InitCallback& callback) {
    LOG(INFO) << "ArcImeMojoService::Init - ImeHost connected";

    // Note when this connection began. Whether it earns a fresh backoff is
    // decided at disconnect time, by how long it lasted — see
    // scheduleReconnect().
    host_connected_at_ = std::chrono::steady_clock::now();
    host_has_connected_ = true;

    {
        std::lock_guard<std::mutex> lock(host_mutex_);
        host_ = std::move(host_remote);
        host_.set_connection_error_handler(base::BindOnce([](ArcImeMojoService* self) {
            LOG(WARNING) << "ArcImeMojoService: ImeHost connection lost";
            {
                std::lock_guard<std::mutex> lk(self->host_mutex_);
                self->host_.reset();
                self->host_ready_ = false;
            }
            self->scheduleReconnect();
        }, this));
        host_ready_ = true;
    }
    host_cv_.notify_all();
    callback.Run();
}

void ArcImeMojoService::InsertText(const std::string& text, int32_t new_cursor_position) {
    VLOG(1) << "ImeInstance::InsertText: \"" << text << "\" pos=" << new_cursor_position;

    if (consumeTimeoutGuard()) {
        LOG(WARNING) << "InsertText: dropped, likely the delayed effect of a "
                        "key event FydroidIME already handled locally after "
                        "a timeout";
        return;
    }

    android::sp<android::IBinder> cb;
    {
        std::lock_guard<std::mutex> lock(callback_mutex_);
        cb = callback_;
    }
    if (!cb) {
        LOG(WARNING) << "InsertText: no callback registered";
        return;
    }

    android::Parcel data, reply;
    data.writeString16(android::String16(text.c_str(), text.size()));
    data.writeInt32(new_cursor_position);
    cb->transact(CB_INSERT_TEXT, data, &reply, android::IBinder::FLAG_ONEWAY);
}

void ArcImeMojoService::SetCompositionText(
        const std::string& text,
        std::vector<arc::mojom::CompositionSegmentPtr> segments,
        ::arc::mojom::RangePtr selection_range) {
    VLOG(1) << "ImeInstance::SetCompositionText: \"" << text << "\"";

    if (consumeTimeoutGuard()) {
        LOG(WARNING) << "SetCompositionText: dropped, likely the delayed "
                        "effect of a key event FydroidIME already handled "
                        "locally after a timeout";
        return;
    }

    android::sp<android::IBinder> cb;
    {
        std::lock_guard<std::mutex> lock(callback_mutex_);
        cb = callback_;
    }
    if (!cb) return;

    android::Parcel data, reply;
    data.writeString16(android::String16(text.c_str(), text.size()));
    data.writeInt32(static_cast<int32_t>(segments.size()));
    for (const auto& seg : segments) {
        data.writeInt32(seg->start_offset);
        data.writeInt32(seg->end_offset);
        data.writeInt32(static_cast<int32_t>(seg->style));
    }
    bool has_selection = !selection_range.is_null();
    data.writeInt32(has_selection ? 1 : 0);
    if (has_selection) {
        data.writeInt32(selection_range->start);
        data.writeInt32(selection_range->end);
    }
    cb->transact(CB_SET_COMPOSITION_TEXT, data, &reply, android::IBinder::FLAG_ONEWAY);
}

void ArcImeMojoService::ConfirmCompositionText() {
    VLOG(1) << "ImeInstance::ConfirmCompositionText";

    if (consumeTimeoutGuard()) {
        LOG(WARNING) << "ConfirmCompositionText: dropped, likely the delayed "
                        "effect of a key event FydroidIME already handled "
                        "locally after a timeout";
        return;
    }

    android::sp<android::IBinder> cb;
    {
        std::lock_guard<std::mutex> lock(callback_mutex_);
        cb = callback_;
    }
    if (!cb) return;

    android::Parcel data, reply;
    cb->transact(CB_CONFIRM_COMPOSITION_TEXT, data, &reply, android::IBinder::FLAG_ONEWAY);
}

void ArcImeMojoService::ExtendSelectionAndDelete(uint64_t before, uint64_t after) {
    VLOG(1) << "ImeInstance::ExtendSelectionAndDelete: before=" << before << " after=" << after;

    if (consumeTimeoutGuard()) {
        LOG(WARNING) << "ExtendSelectionAndDelete: dropped, likely the "
                        "delayed effect of a key event FydroidIME already "
                        "handled locally after a timeout";
        return;
    }

    android::sp<android::IBinder> cb;
    {
        std::lock_guard<std::mutex> lock(callback_mutex_);
        cb = callback_;
    }
    if (!cb) return;

    android::Parcel data, reply;
    data.writeInt64(static_cast<int64_t>(before));
    data.writeInt64(static_cast<int64_t>(after));
    cb->transact(CB_EXTEND_SELECTION_AND_DELETE, data, &reply, android::IBinder::FLAG_ONEWAY);
}

void ArcImeMojoService::SetSelectionText(::arc::mojom::RangePtr selection) {
    VLOG(1) << "ImeInstance::SetSelectionText";

    if (consumeTimeoutGuard()) {
        LOG(WARNING) << "SetSelectionText: dropped, likely the delayed "
                        "effect of a key event FydroidIME already handled "
                        "locally after a timeout";
        return;
    }

    android::sp<android::IBinder> cb;
    {
        std::lock_guard<std::mutex> lock(callback_mutex_);
        cb = callback_;
    }
    if (!cb) return;

    android::Parcel data, reply;
    data.writeInt32(selection->start);
    data.writeInt32(selection->end);
    cb->transact(CB_SET_SELECTION_TEXT, data, &reply, android::IBinder::FLAG_ONEWAY);
}

void ArcImeMojoService::SetComposingRegion(::arc::mojom::RangePtr range) {
    VLOG(1) << "ImeInstance::SetComposingRegion";

    if (consumeTimeoutGuard()) {
        LOG(WARNING) << "SetComposingRegion: dropped, likely the delayed "
                        "effect of a key event FydroidIME already handled "
                        "locally after a timeout";
        return;
    }

    android::sp<android::IBinder> cb;
    {
        std::lock_guard<std::mutex> lock(callback_mutex_);
        cb = callback_;
    }
    if (!cb) return;

    android::Parcel data, reply;
    data.writeInt32(range->start);
    data.writeInt32(range->end);
    cb->transact(CB_SET_COMPOSING_REGION, data, &reply, android::IBinder::FLAG_ONEWAY);
}

void ArcImeMojoService::OnKeyboardAppearanceChanging(
        ::arc::mojom::RectPtr new_bounds, bool is_available) {
    VLOG(1) << "ImeInstance::OnKeyboardAppearanceChanging: available=" << is_available;

    android::sp<android::IBinder> cb;
    {
        std::lock_guard<std::mutex> lock(callback_mutex_);
        cb = callback_;
    }
    if (!cb) return;

    android::Parcel data, reply;
    data.writeInt32(new_bounds->left);
    data.writeInt32(new_bounds->top);
    data.writeInt32(new_bounds->right);
    data.writeInt32(new_bounds->bottom);
    data.writeInt32(is_available ? 1 : 0);
    cb->transact(CB_ON_KEYBOARD_APPEARANCE, data, &reply, android::IBinder::FLAG_ONEWAY);
}

// ---------------------------------------------------------------------------
// Called by ArcImeBinderService (Binder thread)
// ---------------------------------------------------------------------------

void ArcImeMojoService::waitForHost() {
    std::unique_lock<std::mutex> lock(host_mutex_);
    if (!host_cv_.wait_for(lock,
                           std::chrono::milliseconds(kHostConnectTimeoutMs),
                           [this] { return host_ready_; })) {
        LOG(WARNING) << "waitForHost: ImeHost still not connected after "
                     << kHostConnectTimeoutMs << "ms — proceeding";
    }
}

void ArcImeMojoService::setCallback(android::sp<android::IBinder> callback) {
    std::lock_guard<std::mutex> lock(callback_mutex_);
    callback_ = std::move(callback);
    LOG(INFO) << "ArcImeMojoService: callback registered from FydroidIME";
}

// ---------------------------------------------------------------------------
// sendKeyEvent — synchronous Mojo call via callHostSync pattern
// ---------------------------------------------------------------------------

bool ArcImeMojoService::sendKeyEvent(
        bool pressed, int32_t key_code,
        bool is_shift_down, bool is_control_down,
        bool is_alt_down, bool is_capslock_on,
        uint32_t scan_code, bool is_alt_gr_down,
        bool is_repeat) {
    // Shared state for cross-thread result transfer.
    auto result_mutex = std::make_shared<std::mutex>();
    auto result_cv = std::make_shared<std::condition_variable>();
    auto result_ready = std::make_shared<bool>(false);
    auto result_value = std::make_shared<bool>(false);

    auto on_result = base::BindRepeating(
        [](ArcImeMojoService* self,
           std::shared_ptr<std::mutex> mtx,
           std::shared_ptr<std::condition_variable> cv,
           std::shared_ptr<bool> ready,
           std::shared_ptr<bool> value,
           bool r) {
            {
                std::lock_guard<std::mutex> lock(*mtx);
                *value = r;
                *ready = true;
            }
            cv->notify_one();
            // A late "not consumed" tells us definitively that no InsertText
            // is coming for this key — disarm early instead of leaving the
            // window open to swallow some unrelated later key. (self outlives
            // this callback: it is the process-lifetime singleton owned by
            // main.cpp.) A no-op if the guard was never armed, i.e. this
            // reply beat the local timeout.
            if (!r) {
                self->disarmTimeoutGuard();
            }
        },
        this, result_mutex, result_cv, result_ready, result_value);

    auto task_runner = mojo_support_->getThread()->getTaskRunner();
    auto* host_ptr = &host_;

    // Build the KeyEventData on this thread, move it into the closure.
    auto key_event = arc::mojom::KeyEventData::New(
            pressed, key_code, is_shift_down, is_control_down,
            is_alt_down, is_capslock_on, scan_code, is_alt_gr_down,
            is_repeat);

    task_runner->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](mojo::InterfacePtr<arc::mojom::ImeHost>* host,
               arc::mojom::KeyEventDataPtr event,
               base::RepeatingCallback<void(bool)> cb) {
                if (!*host) {
                    LOG(ERROR) << "sendKeyEvent: ImeHost not connected";
                    cb.Run(false);
                    return;
                }
                (*host)->SendKeyEvent(std::move(event), cb);
            },
            host_ptr, std::move(key_event), std::move(on_result)));

    // Bounded wait. This runs on FydroidIME's main thread (a synchronous
    // binder transaction), so an unanswered SendKeyEvent would wedge the whole
    // IME: no keys, no keyboard, until the process is restarted. Chrome not
    // answering promptly is treated as "not consumed" — the IME then handles
    // the key locally, which is the right fallback for every key that matters
    // (backspace, enter, plain characters). The callback owns its state via
    // shared_ptr, so a late reply after this returns is harmless — but Chrome
    // may still separately be about to call InsertText/SetCompositionText/etc.
    // for this same key, which is NOT harmless: FydroidIME would apply it on
    // top of the local handling it just did, doubling the key's effect. Arm
    // the timeout guard so that arrival gets dropped instead of forwarded.
    std::unique_lock<std::mutex> lock(*result_mutex);
    if (!result_cv->wait_for(lock, std::chrono::milliseconds(kKeyEventTimeoutMs),
                             [&result_ready] { return *result_ready; })) {
        LOG(WARNING) << "sendKeyEvent: no reply from Chrome in "
                     << kKeyEventTimeoutMs << "ms, key_code=" << key_code
                     << " — handling locally";
        armTimeoutGuard();
        return false;
    }
    return *result_value;
}

void ArcImeMojoService::armTimeoutGuard() {
    std::lock_guard<std::mutex> lock(timeout_guard_mutex_);
    timeout_guard_armed_ = true;
    timeout_guard_armed_at_ = std::chrono::steady_clock::now();
}

void ArcImeMojoService::disarmTimeoutGuard() {
    std::lock_guard<std::mutex> lock(timeout_guard_mutex_);
    timeout_guard_armed_ = false;
}

bool ArcImeMojoService::consumeTimeoutGuard() {
    std::lock_guard<std::mutex> lock(timeout_guard_mutex_);
    if (!timeout_guard_armed_) return false;
    timeout_guard_armed_ = false;
    auto elapsed = std::chrono::steady_clock::now() - timeout_guard_armed_at_;
    return elapsed < std::chrono::milliseconds(kTimeoutGuardWindowMs);
}

// ---------------------------------------------------------------------------
// Async host calls (fire-and-forget, post to Mojo thread)
// ---------------------------------------------------------------------------

void ArcImeMojoService::sendTextInputTypeChanged(
        arc::mojom::TextInputType type,
        bool is_personalized_learning_allowed,
        int32_t flags) {
    auto task_runner = mojo_support_->getThread()->getTaskRunner();
    auto* host_ptr = &host_;

    task_runner->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](mojo::InterfacePtr<arc::mojom::ImeHost>* host,
               arc::mojom::TextInputType t, bool learning, int32_t f) {
                if (!*host) return;
                (*host)->OnTextInputTypeChanged(t, learning, f);
            },
            host_ptr, type, is_personalized_learning_allowed, flags));
}

void ArcImeMojoService::sendCursorRectChanged(
        int32_t left, int32_t top, int32_t right, int32_t bottom,
        arc::mojom::CursorCoordinateSpace coord_space) {
    auto task_runner = mojo_support_->getThread()->getTaskRunner();
    auto* host_ptr = &host_;

    task_runner->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](mojo::InterfacePtr<arc::mojom::ImeHost>* host,
               int32_t l, int32_t t, int32_t r, int32_t b,
               arc::mojom::CursorCoordinateSpace cs) {
                if (!*host) return;
                (*host)->OnCursorRectChanged(
                    arc::mojom::Rect::New(l, t, r, b), cs);
            },
            host_ptr, left, top, right, bottom, coord_space));
}

void ArcImeMojoService::sendCursorRectChangedWithSurroundingText(
        int32_t left, int32_t top, int32_t right, int32_t bottom,
        uint32_t text_range_start, uint32_t text_range_end,
        const std::string& text_in_range,
        uint32_t selection_start, uint32_t selection_end,
        arc::mojom::CursorCoordinateSpace coord_space) {
    auto task_runner = mojo_support_->getThread()->getTaskRunner();
    auto* host_ptr = &host_;

    task_runner->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](mojo::InterfacePtr<arc::mojom::ImeHost>* host,
               int32_t l, int32_t t, int32_t r, int32_t b,
               uint32_t tr_start, uint32_t tr_end, std::string text,
               uint32_t sel_start, uint32_t sel_end,
               arc::mojom::CursorCoordinateSpace cs) {
                if (!*host) {
                    LOG(WARNING) << "surroundingText: ImeHost not connected";
                    return;
                }
                (*host)->OnCursorRectChangedWithSurroundingText(
                    arc::mojom::Rect::New(l, t, r, b),
                    arc::mojom::Range::New(tr_start, tr_end),
                    text,
                    arc::mojom::Range::New(sel_start, sel_end),
                    cs);
            },
            host_ptr, left, top, right, bottom,
            text_range_start, text_range_end, text_in_range,
            selection_start, selection_end, coord_space));
}

void ArcImeMojoService::sendCancelComposition() {
    auto task_runner = mojo_support_->getThread()->getTaskRunner();
    auto* host_ptr = &host_;

    task_runner->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](mojo::InterfacePtr<arc::mojom::ImeHost>* host) {
                if (!*host) return;
                (*host)->OnCancelComposition();
            },
            host_ptr));
}

void ArcImeMojoService::sendShowVirtualKeyboard() {
    auto task_runner = mojo_support_->getThread()->getTaskRunner();
    auto* host_ptr = &host_;

    task_runner->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](mojo::InterfacePtr<arc::mojom::ImeHost>* host) {
                if (!*host) return;
                (*host)->ShowVirtualKeyboardIfEnabled();
            },
            host_ptr));
}

}  // namespace arc

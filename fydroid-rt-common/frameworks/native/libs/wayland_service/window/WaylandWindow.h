/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

#pragma once

#include <cstdint>

struct wl_output;
struct wl_surface;
struct zaura_surface;

namespace android {

// Abstract base for all Wayland window types (app, IME, toast, notification).
// WaylandLayer and WaylandLayerManager work with this interface polymorphically.
class WaylandWindow {
public:
    // Chrome's window state (from remote_surface state_type_changed)
    enum class WindowState : uint32_t {
        NORMAL = 1, MINIMIZED = 2, MAXIMIZED = 3, FULLSCREEN = 4,
        PINNED = 5, TRUSTED_PINNED = 6, MOVING = 7, RESIZING = 8,
        LEFT_SNAPPED = 9, RIGHT_SNAPPED = 10, PIP = 11,
    };

    // Callback for window events the manager cares about.
    struct EventListener {
        virtual ~EventListener() = default;
        virtual void onBoundsChanged(WaylandWindow* window,
                                     int32_t x, int32_t y,
                                     int32_t w, int32_t h,
                                     uint32_t reason) = 0;
        virtual void onGeometryChanged(WaylandWindow* window,
                                       int32_t x, int32_t y,
                                       int32_t w, int32_t h) = 0;
        virtual void onStateChanged(WaylandWindow* window,
                                    WindowState state) = 0;
        virtual void onWindowClosed(WaylandWindow* window) = 0;
        virtual void onOutputChanged(WaylandWindow* window,
                                     wl_output* output) = 0;
        // User started a drag operation. `direction` follows the
        // zcr_remote_surface_v2 resize_direction enum: 0 = move (no
        // resize), >0 = resize-from-this-edge. Default no-op so
        // non-resizing window types don't have to implement it.
        virtual void onDragStarted(WaylandWindow* /*window*/,
                                   uint32_t /*direction*/) {}

        // User finished drag-resizing the window (released the mouse).
        // Default no-op so non-resizing window types (toast, IME, etc.)
        // don't have to implement it.
        virtual void onDragFinished(WaylandWindow* /*window*/,
                                    int32_t /*x*/, int32_t /*y*/,
                                    bool /*cancelled*/) {}
    };

    virtual ~WaylandWindow() = default;

    virtual wl_surface* surface() const = 0;
    virtual void setBoundsInOutput(wl_output* output,
                                   int32_t x, int32_t y,
                                   int32_t w, int32_t h) = 0;
    virtual void setEventListener(EventListener* listener) = 0;

    // Request window state changes (sent to compositor)
    virtual void minimize() = 0;
    virtual void maximize() = 0;
    virtual void restore() = 0;
    virtual void fullscreen() = 0;

    virtual int32_t boundsX() const = 0;
    virtual int32_t boundsY() const = 0;
    virtual int32_t boundsWidth() const = 0;
    virtual int32_t boundsHeight() const = 0;
    // Android display frame position (for input coordinate mapping).
    // Defaults to boundsX/Y; overlay overrides to return the HWC frame
    // position which is unaffected by drag-to-move.
    virtual int32_t displayFrameX() const { return boundsX(); }
    virtual int32_t displayFrameY() const { return boundsY(); }

    // Move the window to a new position (output coordinates).
    // Default no-op; overridden by movable windows (e.g. overlay).
    virtual void moveTo(int32_t x, int32_t y) { (void)x; (void)y; }
    // Returns true for overlay windows that support drag-to-move and tap.
    virtual bool isOverlay() const { return false; }
    // Returns false for windows that must never receive pointer/touch/keyboard
    // input or take focus (a toast is a passive notice: it is not focusable or
    // touchable in Android either, and anything under it must stay clickable
    // while it is on screen). Such a window also declares an empty wl_surface
    // input region, so Chrome does not route input to it in the first place;
    // this is the second line of defence for the events we inject into Android.
    virtual bool acceptsInput() const { return true; }
    // Android display ID for input routing (0 = primary, >0 = VirtualDisplay).
    virtual int32_t displayId() const { return 0; }
    // Scale from surface-local (DIP) input coords to the window's Android
    // display pixel space. 1.0 for windows whose Android display matches the
    // wl_surface coordinate space; >1 for windows rendered at a higher
    // buffer_scale than their display (e.g. the notification VD, where the
    // buffer is 2× the DIP surface but display 2 is in pixels).
    virtual float inputScale() const { return 1.0f; }
    // Publish the Android accessibility window id of the window this surface
    // renders (zaura_surface.set_accessibility_id). Every window type has one:
    // a screen reader has to be able to read an IME, a toast, an overlay and a
    // notification card as much as an app, and this is the only channel that
    // carries an accessibility window id into Chrome. A negative id clears it.
    // See WaylandAccessibilityId.h.
    virtual void setAccessibilityId(int32_t id) = 0;
    // The window's zaura_surface, for windows that name it as their parent
    // (see WaylandModalWindow). nullptr when the window has none.
    virtual zaura_surface* auraSurface() const { return nullptr; }

    virtual WindowState windowState() const = 0;
    virtual bool isVisible() const = 0;
    virtual bool isClosed() const = 0;
};

} // namespace android

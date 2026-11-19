/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

#pragma once

#include <cstdint>
#include <mutex>
#include <unordered_map>

namespace android {

// Resolves the window_session_id for a given Android task ID.
//
// Session IDs are pushed from Java (ArcWindowManagerService) via Binder
// transaction 41 on the "Wayland" service. The Java side extracts the
// session ID from the task's base intent mIdentifier field
// ("window_session_id=NNNNNN") and calls registerTask().
//
// Chrome uses session IDs to match new Wayland surfaces to "ghost windows"
// (pre-created placeholders for apps from the previous session).
class WaylandSessionIdResolver {
public:
    // Returns the window_session_id for the given taskId, or 0 if not found.
    static int32_t resolve(int32_t taskId);

    // Register a task→sessionId mapping (called from WaylandBinderService).
    static void registerTask(int32_t taskId, int32_t sessionId);

    // Remove a task mapping (called when task is destroyed).
    static void unregisterTask(int32_t taskId);

private:
    static std::mutex sMutex;
    static std::unordered_map<int32_t, int32_t> sTaskSessionMap;
};

} // namespace android

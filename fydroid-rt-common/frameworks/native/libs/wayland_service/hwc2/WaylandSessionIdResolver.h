/*
 * Copyright (C) 2024 Fyde Innovations
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

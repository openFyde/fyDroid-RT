/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

#define LOG_TAG "WaylandSessionIdResolver"

#include "WaylandSessionIdResolver.h"

#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

std::mutex WaylandSessionIdResolver::sMutex;
std::unordered_map<int32_t, int32_t> WaylandSessionIdResolver::sTaskSessionMap;

int32_t WaylandSessionIdResolver::resolve(int32_t taskId) {
    if (taskId <= 0) {
        return 0;
    }

    std::lock_guard<std::mutex> lock(sMutex);
    auto it = sTaskSessionMap.find(taskId);
    if (it != sTaskSessionMap.end()) {
        if (it->second > 0) {
            ALOGVT("taskId=%d → session_id=%d", taskId, it->second);
        }
        return it->second;
    }

    ALOGVT("taskId=%d → no session_id registered", taskId);
    return 0;
}

void WaylandSessionIdResolver::registerTask(int32_t taskId, int32_t sessionId) {
    std::lock_guard<std::mutex> lock(sMutex);
    sTaskSessionMap[taskId] = sessionId;
    ALOGI("registerTask(taskId=%d, sessionId=%d)", taskId, sessionId);
}

void WaylandSessionIdResolver::unregisterTask(int32_t taskId) {
    std::lock_guard<std::mutex> lock(sMutex);
    sTaskSessionMap.erase(taskId);
    ALOGI("unregisterTask(taskId=%d)", taskId);
}

} // namespace android

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

// AIDL interface for the Android-side wake lock service.
// Package and descriptor confirmed from arcwlservice binary symbol:
//   IArcWakeLockService::descriptor @ BSS 0xa068
//   BnArcWakeLockService::onTransact dispatches acquirePartialWakeLock / releasePartialWakeLock
//
// Java PowerManager in system_server calls this Binder service (registered as
// "arc_wake_lock_service") when Android apps acquire/release PARTIAL_WAKE_LOCK.
package com.android.server.arc.power;

interface IArcWakeLockService {
    // Acquire a partial wake lock on Chrome OS. Returns true on success.
    boolean acquirePartialWakeLock();
    // Release the partial wake lock on Chrome OS. Returns true on success.
    boolean releasePartialWakeLock();
}

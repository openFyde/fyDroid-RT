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

#include <arc/MojoThread.h>

#include <base/threading/thread.h>

namespace arc {

MojoThread::MojoThread()
    : thread_(std::make_unique<base::Thread>("ArcMojoThread")) {}

MojoThread::~MojoThread() {
    if (thread_ && thread_->IsRunning()) {
        thread_->Stop();
    }
}

void MojoThread::start() {
    base::Thread::Options options;
    options.message_loop_type = base::MessageLoop::TYPE_IO;
    thread_->StartWithOptions(options);
}

void MojoThread::stop() {
    thread_->Stop();
}

void MojoThread::join() {
    // base::Thread::Stop() joins the thread internally.
    thread_->Stop();
}

bool MojoThread::isCurrentThread() {
    return thread_->task_runner()->BelongsToCurrentThread();
}

void MojoThread::waitForMessageLoop() {
    // base::Thread::Start/StartWithOptions already waits for the message loop
    // to be initialized before returning.
}

scoped_refptr<base::SingleThreadTaskRunner> MojoThread::getTaskRunner() {
    return thread_->task_runner();
}

}  // namespace arc

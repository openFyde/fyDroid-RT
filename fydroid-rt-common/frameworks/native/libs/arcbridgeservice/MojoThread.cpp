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

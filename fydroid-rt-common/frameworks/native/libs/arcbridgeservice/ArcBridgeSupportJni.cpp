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

// JNI native method implementations for org.chromium.arc.bridge.ArcBridgeSupport.
//
// The Java ArcBridgeSupport class (in org.chromium.arc.bridge.jar) is the Java-side
// bootstrap for the ARC bridge.  It loads libarcbridgeservice.so via System.loadLibrary
// and calls nativeInit() to initialize Mojo and the IPC thread.
//
// nativeInit() kicks off HIDL bootstrap asynchronously on the Mojo thread.
// When the ArcBridgeHost Mojo pipe is ready, we call Java's onMessagePipeReady()
// (@CalledByNative) to hand the pipe + version to the Java side.
//
// Native methods registered via JNI_OnLoad → RegisterNatives.

#include <jni.h>

#include <base/android/jni_android.h>
#include <base/bind.h>
#include <base/logging.h>

#include <arc/MojoProcessSupport.h>
#include <arc/MojoThread.h>

#include <mojo/public/c/system/core.h>
#include <mojo/public/c/system/platform_handle.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <mojo/public/cpp/platform/platform_channel.h>
#include <mojo/public/cpp/platform/platform_channel_endpoint.h>
#include <mojo/public/cpp/platform/platform_handle.h>
#include <mojo/public/cpp/system/invitation.h>

#include <vendor/google_arc/arcbridge/1.0/IArcBridgeService.h>

#include <arc/arc_bridge.mojom.h>

// Generated Mojo JNI registration — registers CoreImpl, BaseRunLoop, WatcherImpl
// native methods so Java Mojo bindings work when loaded via libarcbridgeservice.
#include "jni/libmojo_jni_registrations.h"

namespace {

JavaVM* g_jvm = nullptr;

// Cached during nativeInit (app thread) for use on the Mojo thread.
// The Mojo thread's boot classloader can't find Mojo Java classes,
// so we save the app classloader and use it to load classes later.
jobject g_classLoader = nullptr;
jmethodID g_loadClassMethod = nullptr;

struct NativeBridgeSupport {
    arc::MojoProcessSupport* mojoSupport;
    jobject javaRef;  // Global ref to Java ArcBridgeSupport instance
};

// Attach current thread to JVM and return JNIEnv.
static JNIEnv* getJNIEnv() {
    JNIEnv* env = nullptr;
    if (g_jvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        JavaVMAttachArgs args;
        args.version = JNI_VERSION_1_6;
        args.name = const_cast<char*>("MojoThread");
        args.group = nullptr;
        g_jvm->AttachCurrentThread(&env, &args);
    }
    return env;
}

// Find a class using the cached app classloader (works from any thread).
static jclass findClass(JNIEnv* env, const char* dotName) {
    jstring name = env->NewStringUTF(dotName);
    jclass cls = (jclass)env->CallObjectMethod(g_classLoader, g_loadClassMethod, name);
    env->DeleteLocalRef(name);
    if (env->ExceptionCheck()) {
        LOG(ERROR) << "ArcBridgeSupportJni: class not found: " << dotName;
        env->ExceptionDescribe();
        env->ExceptionClear();
        return nullptr;
    }
    return cls;
}

// Run a Java Runnable on the current thread (called from the Mojo IO thread).
static void runJavaRunnable(JavaVM* vm, jobject runnable) {
    JNIEnv* env = getJNIEnv();

    jclass cls = env->GetObjectClass(runnable);
    jmethodID mid = env->GetMethodID(cls, "run", "()V");
    env->CallVoidMethod(runnable, mid);

    if (env->ExceptionCheck()) {
        LOG(ERROR) << "ArcBridgeSupportJni: exception in Runnable on Mojo thread";
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    env->DeleteGlobalRef(runnable);
}

// Call Java ArcBridgeSupport.onMessagePipeReady(MessagePipeHandle, int) via JNI.
// Uses the cached classloader so it works from the Mojo thread.
static void callOnMessagePipeReady(jobject javaRef, MojoHandle rawHandle,
                                   int version) {
    JNIEnv* env = getJNIEnv();

    // CoreImpl.getInstance() — returns Core (interface), not CoreImpl
    jclass coreImplCls = findClass(env, "org.chromium.mojo.system.impl.CoreImpl");
    if (!coreImplCls) return;

    jmethodID getInst = env->GetStaticMethodID(
        coreImplCls, "getInstance",
        "()Lorg/chromium/mojo/system/Core;");
    jobject core = env->CallStaticObjectMethod(coreImplCls, getInst);
    if (env->ExceptionCheck()) {
        LOG(ERROR) << "ArcBridgeSupportJni: CoreImpl.getInstance() failed";
        env->ExceptionDescribe();
        env->ExceptionClear();
        env->DeleteLocalRef(coreImplCls);
        return;
    }

    // core.acquireNativeHandle(rawHandle) → UntypedHandle
    jmethodID acquire = env->GetMethodID(
        coreImplCls, "acquireNativeHandle",
        "(I)Lorg/chromium/mojo/system/UntypedHandle;");
    jobject untyped = env->CallObjectMethod(core, acquire, (jint)rawHandle);

    // untyped.toMessagePipeHandle() → MessagePipeHandle
    jclass untypedCls = env->GetObjectClass(untyped);
    jmethodID toMsgPipe = env->GetMethodID(
        untypedCls, "toMessagePipeHandle",
        "()Lorg/chromium/mojo/system/MessagePipeHandle;");
    jobject msgPipeHandle = env->CallObjectMethod(untyped, toMsgPipe);

    // ArcBridgeSupport.onMessagePipeReady(messagePipeHandle, version)
    jclass bridgeCls = env->GetObjectClass(javaRef);
    jmethodID onReady = env->GetMethodID(
        bridgeCls, "onMessagePipeReady",
        "(Lorg/chromium/mojo/system/MessagePipeHandle;I)V");
    env->CallVoidMethod(javaRef, onReady, msgPipeHandle, (jint)version);

    if (env->ExceptionCheck()) {
        LOG(ERROR) << "ArcBridgeSupportJni: exception in onMessagePipeReady";
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    env->DeleteLocalRef(bridgeCls);
    env->DeleteLocalRef(untypedCls);
    env->DeleteLocalRef(msgPipeHandle);
    env->DeleteLocalRef(untyped);
    env->DeleteLocalRef(core);
    env->DeleteLocalRef(coreImplCls);
}

// Bootstrap the HIDL/Mojo connection on the Mojo thread, then call back to Java.
static void bootstrapOnMojoThread(jobject javaRef) {
    LOG(INFO) << "ArcBridgeSupportJni: bootstrapping on Mojo thread";

    // Step 1: Get the HIDL service (may block waiting for it to register)
    auto hidl_service =
        ::vendor::google_arc::arcbridge::V1_0::IArcBridgeService::getService("default");
    if (!hidl_service) {
        LOG(ERROR) << "ArcBridgeSupportJni: IArcBridgeService not available";
        return;
    }
    LOG(INFO) << "ArcBridgeSupportJni: got IArcBridgeService";

    // Step 2: Get Mojo bootstrap channel (version 0 — our service ignores it)
    using MojoBootstrapChannel =
        ::vendor::google_arc::arcbridge::V1_0::MojoBootstrapChannel;
    android::hardware::hidl_handle channel_handle;
    std::vector<uint64_t> pipe_names;
    bool got_channel = false;

    auto ret = hidl_service->getMojoBootstrapChannel(0 /* version */,
        [&](const MojoBootstrapChannel& channel) {
            if (channel.channelHandle.getNativeHandle() &&
                channel.channelHandle.getNativeHandle()->numFds > 0) {
                channel_handle = channel.channelHandle;
                pipe_names.assign(channel.pipeNames.begin(),
                                  channel.pipeNames.end());
                got_channel = true;
            }
        });

    if (!ret.isOk() || !got_channel) {
        LOG(ERROR) << "ArcBridgeSupportJni: getMojoBootstrapChannel failed";
        return;
    }

    int fd = dup(channel_handle->data[0]);
    if (fd < 0) {
        PLOG(ERROR) << "ArcBridgeSupportJni: failed to dup channel fd";
        return;
    }
    LOG(INFO) << "ArcBridgeSupportJni: got channel fd=" << fd
              << " pipeNames=" << pipe_names.size();

    // Step 3: Accept the Mojo invitation
    mojo::IncomingInvitation invitation = mojo::IncomingInvitation::Accept(
        mojo::PlatformChannelEndpoint(
            mojo::PlatformHandle(base::ScopedFD(fd))));

    if (pipe_names.empty()) {
        LOG(ERROR) << "ArcBridgeSupportJni: no pipe names";
        return;
    }
    mojo::ScopedMessagePipeHandle pipe =
        invitation.ExtractMessagePipe(pipe_names[0]);
    if (!pipe.is_valid()) {
        LOG(ERROR) << "ArcBridgeSupportJni: failed to extract message pipe";
        return;
    }

    // Step 4: Pass the raw Mojo pipe handle to Java.
    // The version we report is ArcBridgeHost::Version_ from our generated mojom
    // (70, matching Chrome's ArcBridgeHost version).  The Java side will also
    // QueryVersion on the pipe, but stores our int for service version checks.
    MojoHandle raw_handle = pipe.release().value();
    int version = static_cast<int>(arc::mojom::ArcBridgeHost::Version_);

    LOG(INFO) << "ArcBridgeSupportJni: calling onMessagePipeReady, version="
              << version;
    callOnMessagePipeReady(javaRef, raw_handle, version);
}

// --- JNI method implementations ---

static jlong nativeInit(JNIEnv* env, jclass, jobject javaObj) {
    LOG(INFO) << "ArcBridgeSupportJni: nativeInit";

    // Initialize Mojo core, IPC thread, and ScopedIPCSupport.
    auto* mojoSupport = arc::MojoProcessSupport::GetInstance();

    auto* native = new NativeBridgeSupport();
    native->mojoSupport = mojoSupport;
    native->javaRef = env->NewGlobalRef(javaObj);

    // Cache the app classloader so the Mojo thread can find Mojo Java classes.
    // The Mojo thread's boot classloader doesn't have them.
    if (!g_classLoader) {
        jclass classClass = env->FindClass("java/lang/Class");
        jmethodID getClassLoader = env->GetMethodID(
            classClass, "getClassLoader", "()Ljava/lang/ClassLoader;");
        jclass bridgeCls = env->GetObjectClass(javaObj);
        jobject loader = env->CallObjectMethod(bridgeCls, getClassLoader);
        g_classLoader = env->NewGlobalRef(loader);

        jclass loaderCls = env->GetObjectClass(loader);
        g_loadClassMethod = env->GetMethodID(
            loaderCls, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");

        env->DeleteLocalRef(loaderCls);
        env->DeleteLocalRef(loader);
        env->DeleteLocalRef(bridgeCls);
        env->DeleteLocalRef(classClass);

        LOG(INFO) << "ArcBridgeSupportJni: classloader cached";
    }

    // Post bootstrap to Mojo thread.  When done, calls Java onMessagePipeReady().
    mojoSupport->getThread()->getTaskRunner()->PostTask(
        FROM_HERE,
        base::BindOnce(&bootstrapOnMojoThread, native->javaRef));

    return reinterpret_cast<jlong>(native);
}

static void nativeBuildInfoInit(JNIEnv*, jclass, jlong) {
    // No-op.  Chrome's BuildInfo is not needed in our open-source implementation.
}

static jint nativeConvertFileDescriptorToMojoHandle(JNIEnv*, jclass, jlong, jint fd) {
    MojoPlatformHandle platform_handle;
    platform_handle.struct_size = sizeof(MojoPlatformHandle);
    platform_handle.type = MOJO_PLATFORM_HANDLE_TYPE_FILE_DESCRIPTOR;
    platform_handle.value = static_cast<uint64_t>(fd);

    MojoHandle handle = MOJO_HANDLE_INVALID;
    MojoResult result = MojoWrapPlatformHandle(&platform_handle, nullptr, &handle);
    if (result != MOJO_RESULT_OK) {
        LOG(ERROR) << "ArcBridgeSupportJni: MojoWrapPlatformHandle failed: " << result;
        return static_cast<jint>(MOJO_HANDLE_INVALID);
    }
    return static_cast<jint>(handle);
}

static jint nativeConvertMojoHandleToFileDescriptor(JNIEnv*, jclass, jlong, jint mojoHandle) {
    MojoPlatformHandle platform_handle;
    platform_handle.struct_size = sizeof(MojoPlatformHandle);

    MojoResult result = MojoUnwrapPlatformHandle(
        static_cast<MojoHandle>(mojoHandle), nullptr, &platform_handle);
    if (result != MOJO_RESULT_OK ||
        platform_handle.type != MOJO_PLATFORM_HANDLE_TYPE_FILE_DESCRIPTOR) {
        LOG(ERROR) << "ArcBridgeSupportJni: MojoUnwrapPlatformHandle failed: " << result;
        return -1;
    }
    return static_cast<jint>(platform_handle.value);
}

static void nativeDispose(JNIEnv* env, jclass, jlong ptr) {
    auto* native = reinterpret_cast<NativeBridgeSupport*>(ptr);
    if (native) {
        if (native->javaRef) {
            env->DeleteGlobalRef(native->javaRef);
        }
        delete native;
    }
}

static void nativeRunOnMojoThread(JNIEnv* env, jclass, jlong ptr, jobject runnable) {
    auto* native = reinterpret_cast<NativeBridgeSupport*>(ptr);
    if (!native || !native->mojoSupport) {
        LOG(ERROR) << "ArcBridgeSupportJni: nativeRunOnMojoThread null native";
        return;
    }

    // Create a global ref so the Runnable survives the cross-thread post.
    jobject globalRunnable = env->NewGlobalRef(runnable);

    native->mojoSupport->getThread()->getTaskRunner()->PostTask(
        FROM_HERE,
        base::BindOnce(&runJavaRunnable, g_jvm, globalRunnable));
}

// Re-runs bootstrapOnMojoThread on the Mojo thread. Called from the Java
// bridge-host pipe error handler when the vendor service drops the root
// connection: vendor-side reconnect will eventually rebind the root_host_,
// and the HIDL getMojoBootstrapChannel() call inside bootstrapOnMojoThread
// will then succeed, delivering a fresh pipe to Java via onMessagePipeReady.
static void nativeBootstrap(JNIEnv*, jclass, jlong ptr) {
    auto* native = reinterpret_cast<NativeBridgeSupport*>(ptr);
    if (!native || !native->mojoSupport || !native->javaRef) {
        LOG(ERROR) << "ArcBridgeSupportJni: nativeBootstrap null native";
        return;
    }
    LOG(INFO) << "ArcBridgeSupportJni: nativeBootstrap — re-running bootstrap";
    native->mojoSupport->getThread()->getTaskRunner()->PostTask(
        FROM_HERE,
        base::BindOnce(&bootstrapOnMojoThread, native->javaRef));
}

// JNI method registration table.
static const JNINativeMethod kMethods[] = {
    {"nativeInit",
     "(Lorg/chromium/arc/bridge/ArcBridgeSupport;)J",
     reinterpret_cast<void*>(nativeInit)},
    {"nativeBuildInfoInit",
     "(J)V",
     reinterpret_cast<void*>(nativeBuildInfoInit)},
    {"nativeConvertFileDescriptorToMojoHandle",
     "(JI)I",
     reinterpret_cast<void*>(nativeConvertFileDescriptorToMojoHandle)},
    {"nativeConvertMojoHandleToFileDescriptor",
     "(JI)I",
     reinterpret_cast<void*>(nativeConvertMojoHandleToFileDescriptor)},
    {"nativeDispose",
     "(J)V",
     reinterpret_cast<void*>(nativeDispose)},
    {"nativeRunOnMojoThread",
     "(JLjava/lang/Runnable;)V",
     reinterpret_cast<void*>(nativeRunOnMojoThread)},
    {"nativeBootstrap",
     "(J)V",
     reinterpret_cast<void*>(nativeBootstrap)},
};

}  // anonymous namespace

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    g_jvm = vm;

    // Initialize libchrome's JavaVM so base::android::AttachCurrentThread() works.
    // Required before using generated JNI registration functions (LazyGetClass).
    base::android::InitVM(vm);

    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOG(ERROR) << "ArcBridgeSupportJni: JNI_OnLoad failed to get JNIEnv";
        return JNI_ERR;
    }

    jclass clazz = env->FindClass("org/chromium/arc/bridge/ArcBridgeSupport");
    if (!clazz) {
        LOG(ERROR) << "ArcBridgeSupportJni: ArcBridgeSupport class not found";
        return JNI_ERR;
    }

    if (env->RegisterNatives(clazz, kMethods,
                             sizeof(kMethods) / sizeof(kMethods[0])) < 0) {
        LOG(ERROR) << "ArcBridgeSupportJni: RegisterNatives failed";
        return JNI_ERR;
    }

    // Register Mojo Java system native methods (CoreImpl, BaseRunLoop, WatcherImpl).
    // The proprietary libarcbridgeservice bundled these so Java Mojo bindings work.
    // We call individual register functions, skipping JavaExceptionReporter which
    // is not present in the ARC classpath.
    if (!RegisterNative_org_chromium_mojo_system_impl_CoreImpl(env)) {
        LOG(ERROR) << "ArcBridgeSupportJni: RegisterNative CoreImpl failed";
        return JNI_ERR;
    }
    if (!RegisterNative_org_chromium_mojo_system_impl_BaseRunLoop(env)) {
        LOG(ERROR) << "ArcBridgeSupportJni: RegisterNative BaseRunLoop failed";
        return JNI_ERR;
    }
    if (!RegisterNative_org_chromium_mojo_system_impl_WatcherImpl(env)) {
        LOG(ERROR) << "ArcBridgeSupportJni: RegisterNative WatcherImpl failed";
        return JNI_ERR;
    }

    LOG(INFO) << "ArcBridgeSupportJni: JNI_OnLoad completed, "
              << sizeof(kMethods) / sizeof(kMethods[0]) << " methods registered"
              << " + Mojo system JNI methods";
    return JNI_VERSION_1_6;
}

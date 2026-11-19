#include "CameraHal.h"
#include "CameraInstanceImpl.h"
#include "CameraModuleDelegate.h"
#include "CameraModuleCallbacksImpl.h"
#include "VendorTagOpsDelegate.h"
#include "Camera3DeviceOpsDelegate.h"
#include "MojoCallWaiter.h"

#include <cstdio>

#include <base/bind.h>
#include <base/logging.h>
#include <base/threading/thread.h>
#include <arc/MojoProcessSupport.h>

#include <hardware/camera_common.h>
#include <hardware/hardware.h>

// ---------------------------------------------------------------------------
// C thunks for camera_module_t
// ---------------------------------------------------------------------------

extern "C" {
static int hal_open(const hw_module_t* module, const char* name,
                     hw_device_t** device);
static int hal_init();
static int hal_get_number_of_cameras();
static int hal_get_camera_info(int camera_id, struct camera_info* info);
static int hal_set_callbacks(const camera_module_callbacks_t* callbacks);
static int hal_set_torch_mode(const char* camera_id, bool enabled);
static void hal_get_vendor_tag_ops(vendor_tag_ops_t* ops);
}  // extern "C"

static hw_module_methods_t g_camera_module_methods = {
    .open = hal_open,
};

extern "C" camera_module_t HMI = {
    .common = {
        .tag = HARDWARE_MODULE_TAG,
        .module_api_version = CAMERA_MODULE_API_VERSION_2_5,
        .hal_api_version = HARDWARE_HAL_API_VERSION,
        .id = CAMERA_HARDWARE_MODULE_ID,
        .name = "ARC Camera HAL v3 (fydroid-rt)",
        .author = "The Android Open Source Project",
        .methods = &g_camera_module_methods,
        .dso = nullptr,
        .reserved = {0},
    },
    .get_number_of_cameras = hal_get_number_of_cameras,
    .get_camera_info = hal_get_camera_info,
    .set_callbacks = hal_set_callbacks,
    .get_vendor_tag_ops = hal_get_vendor_tag_ops,
    .open_legacy = nullptr,
    .set_torch_mode = hal_set_torch_mode,
    .init = hal_init,
    .get_physical_camera_info = nullptr,
    .is_stream_combination_supported = nullptr,
    .notify_device_state_change = nullptr,
    .reserved = {nullptr, nullptr},
};

// ---------------------------------------------------------------------------
// CameraHal singleton
// ---------------------------------------------------------------------------

namespace fydroid { namespace camera {

// "No vendor tags" ops table, used when the GetVendorTagOps handshake fails.
//
// The table must never be left zeroed: VendorTagDescriptor::createDescriptorFromOps
// calls get_tag_count() straight off the struct, so null pointers here would
// crash the framework rather than degrade to "this HAL has no vendor tags".
static int empty_vtag_get_tag_count(const vendor_tag_ops_t*) { return 0; }
static void empty_vtag_get_all_tags(const vendor_tag_ops_t*, uint32_t*) {}
static const char* empty_vtag_get_section_name(const vendor_tag_ops_t*, uint32_t) {
    return nullptr;
}
static const char* empty_vtag_get_tag_name(const vendor_tag_ops_t*, uint32_t) {
    return nullptr;
}
static int empty_vtag_get_tag_type(const vendor_tag_ops_t*, uint32_t) { return -1; }

static void FillEmptyVendorTagOps(vendor_tag_ops_t* ops) {
    ops->get_tag_count    = empty_vtag_get_tag_count;
    ops->get_all_tags     = empty_vtag_get_all_tags;
    ops->get_section_name = empty_vtag_get_section_name;
    ops->get_tag_name     = empty_vtag_get_tag_name;
    ops->get_tag_type     = empty_vtag_get_tag_type;
}

CameraHal& CameraHal::Instance() {
    static CameraHal instance;
    return instance;
}

int CameraHal::Init() {
    android::AutoMutex lock(lock_);
    if (initialized_) return 0;

    LOG(INFO) << "CameraHal::Init - starting Mojo bootstrap (initMojoIpc)";

    arc::MojoProcessSupport* mojo_support = arc::MojoProcessSupport::GetInstance();

    instance_ = android::sp<CameraInstanceImpl>(new CameraInstanceImpl(mojo_support));
    instance_->Bootstrap();

    LOG(INFO) << "CameraHal::Init - waiting for CameraModule from Chrome OS...";
    if (!instance_->WaitForModule()) {
        LOG(ERROR) << "CameraHal::Init - timed out waiting for CameraModule. "
                      "Failed to initialize Mojo IPC channels";
        instance_ = nullptr;
        return -ETIMEDOUT;
    }

    module_delegate_ = std::make_unique<CameraModuleDelegate>(
            mojo_support,
            std::move(instance_->module_ptr()));

    // cros_camera_service restarting (it aborts on some VM buffer paths) used
    // to leave the HAL failing every call until ARC restarted: Chrome re-runs
    // SetUpChannel with a fresh CameraModule, but nothing took it. Rebind the
    // delegate and re-register the hot-plug callbacks on the new pipe. Runs on
    // the Mojo thread; module_delegate_ and callbacks_impl_ outlive the HAL's
    // use of it (the HAL is never torn down).
    instance_->SetRechannelHandler(
            [this](mojo::InterfacePtr<cros::mojom::CameraModule> module) {
                module_delegate_->Rebind(std::move(module));
                if (callbacks_impl_) {
                    module_delegate_->RegisterCallbacksAssociated(callbacks_impl_.get());
                }
            });

    initialized_ = true;
    LOG(INFO) << "CameraHal::Init - done";
    return 0;
}

int CameraHal::GetNumberOfCameras() {
    if (!initialized_) return 0;
    return module_delegate_->GetNumberOfCameras();
}

int CameraHal::GetCameraInfo(int camera_id, camera_info* info) {
    if (!initialized_) return -ENODEV;
    return module_delegate_->GetCameraInfo(camera_id, info);
}

int CameraHal::SetCallbacks(const camera_module_callbacks_t* cb) {
    android::AutoMutex lock(lock_);
    module_callbacks_ = cb;

    // Register a CameraModuleCallbacks receiver with Chrome OS the first time
    // the framework hands us its callbacks. This is what surfaces hot-plug /
    // external cameras (e.g. USB webcams), whose IDs never come back through
    // GetNumberOfCameras and arrive only as CameraDeviceStatusChange events.
    //
    // The AOSP legacy provider calls init() -> getNumberOfCameras() ->
    // getCameraInfo() -> set_callbacks(), so module_delegate_ already exists.
    // RegisterCallbacksAssociated only posts to the Mojo thread, so calling it
    // while holding lock_ cannot deadlock.
    if (cb && !callbacks_registered_ && module_delegate_) {
        callback_thread_ = std::make_unique<base::Thread>("CameraHalCallbacks");
        callback_thread_->Start();
        callbacks_impl_ = std::make_unique<CameraModuleCallbacksImpl>(
                base::BindRepeating(&CameraHal::OnDeviceStatusChange,
                                    base::Unretained(this)),
                base::BindRepeating(&CameraHal::OnTorchModeStatusChange,
                                    base::Unretained(this)));
        module_delegate_->RegisterCallbacksAssociated(callbacks_impl_.get());
        callbacks_registered_ = true;
        LOG(INFO) << "CameraHal::SetCallbacks - registered hot-plug callbacks";
    }
    return 0;
}

void CameraHal::OnDeviceStatusChange(int camera_id, int new_status) {
    // Called on the Mojo thread. callback_thread_ is created before the
    // receiver is registered and is never stopped (the HAL is never torn
    // down), so Unretained(this) is safe.
    callback_thread_->task_runner()->PostTask(FROM_HERE,
            base::BindOnce(&CameraHal::DeliverDeviceStatusChange,
                           base::Unretained(this), camera_id, new_status));
}

void CameraHal::OnTorchModeStatusChange(int camera_id, int new_status) {
    callback_thread_->task_runner()->PostTask(FROM_HERE,
            base::BindOnce(&CameraHal::DeliverTorchModeStatusChange,
                           base::Unretained(this), camera_id, new_status));
}

void CameraHal::DeliverDeviceStatusChange(int camera_id, int new_status) {
    const camera_module_callbacks_t* cb;
    {
        android::AutoMutex lock(lock_);
        cb = module_callbacks_;
    }
    if (cb && cb->camera_device_status_change) {
        cb->camera_device_status_change(cb, camera_id, new_status);
    }
}

void CameraHal::DeliverTorchModeStatusChange(int camera_id, int new_status) {
    const camera_module_callbacks_t* cb;
    {
        android::AutoMutex lock(lock_);
        cb = module_callbacks_;
    }
    if (cb && cb->torch_mode_status_change) {
        // torch_mode_status_change takes the camera id as a string, unlike
        // camera_device_status_change which takes an int.
        char id[16];
        snprintf(id, sizeof(id), "%d", camera_id);
        cb->torch_mode_status_change(cb, id, new_status);
    }
}

int CameraHal::SetTorchMode(const char* camera_id, bool enabled) {
    if (!initialized_) return -ENODEV;
    // camera_id is a string like "0", "1" — convert to int.
    int id = atoi(camera_id);
    return module_delegate_->SetTorchMode(id, enabled);
}

int CameraHal::Open(const hw_module_t* module, const char* name,
                    hw_device_t** device) {
    if (!initialized_) return -ENODEV;
    int camera_id = atoi(name);

    Camera3DeviceOpsDelegate* delegate = nullptr;
    int result = module_delegate_->OpenDevice(camera_id, &delegate);
    if (result != 0 || !delegate) {
        return result ? result : -ENODEV;
    }

    camera3_device_t* cam_dev = delegate->GetCamera3Device();
    cam_dev->common.module = const_cast<hw_module_t*>(module);
    *device = &cam_dev->common;
    return 0;
}

void CameraHal::GetVendorTagOps(vendor_tag_ops_t* ops) {
    if (!initialized_ || !ops) return;

    if (!vendor_tag_delegate_) {
        // Fetch VendorTagOps from the module.
        arc::MojoProcessSupport* mojo_support = arc::MojoProcessSupport::GetInstance();
        auto runner = mojo_support->getThread()->getTaskRunner();

        auto pending = MakePendingReply<mojo::InterfacePtr<cros::mojom::VendorTagOps>>();

        runner->PostTask(FROM_HERE,
                base::BindOnce([](
                        cros::mojom::CameraModule* mod,
                        std::shared_ptr<PendingReply<
                                mojo::InterfacePtr<cros::mojom::VendorTagOps>>> p) {
                    mojo::InterfaceRequest<cros::mojom::VendorTagOps> req =
                            mojo::MakeRequest(&p->value);
                    mod->GetVendorTagOps(std::move(req),
                            base::BindRepeating([](std::shared_ptr<PendingReply<
                                    mojo::InterfacePtr<cros::mojom::VendorTagOps>>> p) {
                                p->event.Signal();
                            }, p));
                },
                module_delegate_->proxy(), pending));

        // Vendor tags are optional metadata. If Chrome never answers, ship an
        // ops table with no tags rather than blocking the caller — this runs
        // from CameraProviderManager setup, so hanging here stalls the whole
        // provider from ever coming up.
        if (!AwaitReply(pending, vendor_tag_pipe_, runner,
                        "CameraModule::GetVendorTagOps",
                        kMojoCallTimeoutMs, TimeoutPolicy::kFailCall)) {
            FillEmptyVendorTagOps(ops);
            return;
        }

        vendor_tag_delegate_ = std::make_unique<VendorTagOpsDelegate>(
                mojo_support, std::move(pending->value));
    }

    vendor_tag_delegate_->FillVendorTagOps(ops);
}

}} // namespace fydroid::camera

// ---------------------------------------------------------------------------
// C thunk implementations
// ---------------------------------------------------------------------------

extern "C" {

static int hal_open(const hw_module_t* module, const char* name,
                     hw_device_t** device) {
    return fydroid::camera::CameraHal::Instance().Open(module, name, device);
}

static int hal_init() {
    return fydroid::camera::CameraHal::Instance().Init();
}

static int hal_get_number_of_cameras() {
    return fydroid::camera::CameraHal::Instance().GetNumberOfCameras();
}

static int hal_get_camera_info(int camera_id, struct camera_info* info) {
    return fydroid::camera::CameraHal::Instance().GetCameraInfo(camera_id, info);
}

static int hal_set_callbacks(const camera_module_callbacks_t* callbacks) {
    return fydroid::camera::CameraHal::Instance().SetCallbacks(callbacks);
}

static int hal_set_torch_mode(const char* camera_id, bool enabled) {
    return fydroid::camera::CameraHal::Instance().SetTorchMode(camera_id, enabled);
}

static void hal_get_vendor_tag_ops(vendor_tag_ops_t* ops) {
    fydroid::camera::CameraHal::Instance().GetVendorTagOps(ops);
}

}  // extern "C"

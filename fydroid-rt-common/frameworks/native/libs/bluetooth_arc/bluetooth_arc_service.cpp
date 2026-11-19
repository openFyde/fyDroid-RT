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

// BluetoothArcService — Mojo bridge implementation.
//
// Bridges Android's bt_interface_t / bt_callbacks_t with Chrome OS's
// BluetoothHost / BluetoothInstance Mojo interfaces.

#include "bluetooth_arc_service.h"

#include <base/bind.h>
#include <base/logging.h>
#include <base/threading/thread.h>

#include <arc/ArcBridgeSupport.h>
#include <arc/MojoProcessSupport.h>

// Declared in bt_gatt_arc.cpp
extern const btgatt_interface_t* getGattInterface();

namespace arc {

// ---------------------------------------------------------------------------
// Singleton
// ---------------------------------------------------------------------------

BluetoothArcService* BluetoothArcService::GetInstance() {
    // The instance must live forever (JNI holds a raw pointer to bluetoothInterface).
    // Use a leaked sp<> to prevent RefBase destruction.
    static android::sp<BluetoothArcService> instance = new BluetoothArcService();
    return instance.get();
}

BluetoothArcService::BluetoothArcService() = default;
BluetoothArcService::~BluetoothArcService() = default;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

void BluetoothArcService::postToMojo(base::OnceClosure task) {
    if (!mojo_support_ || !mojo_support_->getThread()) {
        LOG(ERROR) << "BluetoothArc: Mojo not available";
        return;
    }
    mojo_support_->getThread()->getTaskRunner()->PostTask(
            FROM_HERE, std::move(task));
}

void BluetoothArcService::postToCallback(base::OnceClosure task) {
    if (callback_runner_) {
        callback_runner_->PostTask(FROM_HERE, std::move(task));
    } else {
        // Fallback: run inline (shouldn't happen after init).
        std::move(task).Run();
    }
}

RawAddress BluetoothArcService::toRawAddress(
        const arc::mojom::BluetoothAddressPtr& addr) {
    RawAddress raw;
    if (addr) {
        memcpy(raw.address, addr->address.data(), 6);
    } else {
        raw = RawAddress::kEmpty;
    }
    return raw;
}

arc::mojom::BluetoothAddressPtr BluetoothArcService::toMojoAddress(
        const RawAddress& addr) {
    auto mojo_addr = arc::mojom::BluetoothAddress::New();
    memcpy(mojo_addr->address.data(), addr.address, 6);
    return mojo_addr;
}

// Note: BluetoothUUID has a typemap to bluetooth::Uuid, so in generated
// C++ bindings, struct fields of type BluetoothUUID are bluetooth::Uuid.
// These helpers are no longer needed — UUID fields can be assigned directly.

// Convert Mojo BluetoothProperty to bt_property_t.
// Each property's val pointer points into out_storage entries.
void BluetoothArcService::convertProperties(
        const std::vector<arc::mojom::BluetoothPropertyPtr>& mojo_props,
        std::vector<bt_property_t>* out_props,
        std::vector<std::vector<uint8_t>>* out_storage) {
    out_props->clear();
    out_storage->clear();
    out_storage->reserve(mojo_props.size());

    for (const auto& prop : mojo_props) {
        bt_property_t bt_prop;
        std::vector<uint8_t> storage;

        if (prop->is_bdname()) {
            bt_prop.type = BT_PROPERTY_BDNAME;
            const std::string& name = prop->get_bdname();
            bt_bdname_t bdname = {};
            size_t copy_len = std::min(name.size(), sizeof(bdname.name) - 1);
            memcpy(bdname.name, name.data(), copy_len);
            bdname.name[copy_len] = '\0';
            storage.resize(sizeof(bt_bdname_t));
            memcpy(storage.data(), &bdname, sizeof(bt_bdname_t));
        } else if (prop->is_bdaddr()) {
            bt_prop.type = BT_PROPERTY_BDADDR;
            RawAddress raw = toRawAddress(prop->get_bdaddr());
            storage.resize(sizeof(RawAddress));
            memcpy(storage.data(), &raw, sizeof(RawAddress));
        } else if (prop->is_uuids()) {
            bt_prop.type = BT_PROPERTY_UUIDS;
            const auto& uuids = prop->get_uuids();
            storage.resize(uuids.size() * sizeof(bluetooth::Uuid));
            for (size_t i = 0; i < uuids.size(); i++) {
                // Typemap already converts BluetoothUUID → bluetooth::Uuid
                memcpy(storage.data() + i * sizeof(bluetooth::Uuid),
                       &uuids[i], sizeof(bluetooth::Uuid));
            }
        } else if (prop->is_device_class()) {
            bt_prop.type = BT_PROPERTY_CLASS_OF_DEVICE;
            uint32_t cls = prop->get_device_class();
            storage.resize(sizeof(uint32_t));
            memcpy(storage.data(), &cls, sizeof(uint32_t));
        } else if (prop->is_device_type()) {
            bt_prop.type = BT_PROPERTY_TYPE_OF_DEVICE;
            bt_device_type_t type = static_cast<bt_device_type_t>(
                    prop->get_device_type());
            storage.resize(sizeof(bt_device_type_t));
            memcpy(storage.data(), &type, sizeof(bt_device_type_t));
        } else if (prop->is_service_record()) {
            bt_prop.type = BT_PROPERTY_SERVICE_RECORD;
            const auto& rec = prop->get_service_record();
            bt_service_record_t sr = {};
            sr.uuid = rec->uuid;  // Typemap already converted to bluetooth::Uuid
            sr.channel = rec->channel;
            size_t name_len = std::min(rec->name.size(), sizeof(sr.name) - 1);
            memcpy(sr.name, rec->name.data(), name_len);
            sr.name[name_len] = '\0';
            storage.resize(sizeof(bt_service_record_t));
            memcpy(storage.data(), &sr, sizeof(bt_service_record_t));
        } else if (prop->is_adapter_scan_mode()) {
            bt_prop.type = BT_PROPERTY_ADAPTER_SCAN_MODE;
            bt_scan_mode_t mode = static_cast<bt_scan_mode_t>(
                    prop->get_adapter_scan_mode());
            storage.resize(sizeof(bt_scan_mode_t));
            memcpy(storage.data(), &mode, sizeof(bt_scan_mode_t));
        } else if (prop->is_bonded_devices()) {
            bt_prop.type = BT_PROPERTY_ADAPTER_BONDED_DEVICES;
            const auto& devs = prop->get_bonded_devices();
            storage.resize(devs.size() * sizeof(RawAddress));
            for (size_t i = 0; i < devs.size(); i++) {
                RawAddress raw = toRawAddress(devs[i]);
                memcpy(storage.data() + i * sizeof(RawAddress),
                       &raw, sizeof(RawAddress));
            }
        } else if (prop->is_discovery_timeout()) {
            bt_prop.type = BT_PROPERTY_ADAPTER_DISCOVERABLE_TIMEOUT;
            uint32_t timeout = prop->get_discovery_timeout();
            storage.resize(sizeof(uint32_t));
            memcpy(storage.data(), &timeout, sizeof(uint32_t));
        } else if (prop->is_remote_friendly_name()) {
            bt_prop.type = BT_PROPERTY_REMOTE_FRIENDLY_NAME;
            const std::string& name = prop->get_remote_friendly_name();
            bt_bdname_t bdname = {};
            size_t copy_len = std::min(name.size(), sizeof(bdname.name) - 1);
            memcpy(bdname.name, name.data(), copy_len);
            bdname.name[copy_len] = '\0';
            storage.resize(sizeof(bt_bdname_t));
            memcpy(storage.data(), &bdname, sizeof(bt_bdname_t));
        } else if (prop->is_remote_rssi()) {
            bt_prop.type = BT_PROPERTY_REMOTE_RSSI;
            int8_t rssi = static_cast<int8_t>(prop->get_remote_rssi());
            storage.resize(sizeof(int8_t));
            memcpy(storage.data(), &rssi, sizeof(int8_t));
        } else if (prop->is_remote_version()) {
            bt_prop.type = BT_PROPERTY_REMOTE_VERSION_INFO;
            const auto& ver = prop->get_remote_version();
            bt_remote_version_t rv;
            rv.version = ver->version;
            rv.sub_ver = ver->sub_ver;
            rv.manufacturer = ver->manufacturer;
            storage.resize(sizeof(bt_remote_version_t));
            memcpy(storage.data(), &rv, sizeof(bt_remote_version_t));
        } else if (prop->is_local_le_features()) {
            bt_prop.type = BT_PROPERTY_LOCAL_LE_FEATURES;
            const auto& feat = prop->get_local_le_features();
            bt_local_le_features_t lf = {};
            lf.version_supported = feat->version_supported;
            lf.local_privacy_enabled = feat->local_privacy_enabled;
            lf.max_adv_instance = feat->max_adv_instance;
            lf.rpa_offload_supported = feat->rpa_offload_supported;
            lf.max_irk_list_size = feat->max_irk_list_size;
            lf.max_adv_filter_supported = feat->max_adv_filter_supported;
            lf.activity_energy_info_supported = feat->activity_energy_info_supported;
            lf.scan_result_storage_size = feat->scan_result_storage_size;
            lf.total_trackable_advertisers = feat->total_trackable_advertisers;
            lf.extended_scan_support = feat->extended_scan_support;
            lf.debug_logging_supported = feat->debug_logging_supported;
            storage.resize(sizeof(bt_local_le_features_t));
            memcpy(storage.data(), &lf, sizeof(bt_local_le_features_t));
        } else {
            LOG(WARNING) << "BluetoothArc: unknown property union tag, skipping";
            continue;
        }

        bt_prop.len = storage.size();
        out_storage->push_back(std::move(storage));
        bt_prop.val = out_storage->back().data();
        out_props->push_back(bt_prop);
    }
}

// ---------------------------------------------------------------------------
// GATT DB helpers
// ---------------------------------------------------------------------------

const BluetoothArcService::GattDbEntry*
BluetoothArcService::lookupGattEntry(const RawAddress& addr, uint16_t handle) {
    // Must be called under gatt_mutex_ or from a context where the cache
    // is stable (e.g. callback thread after population).
    auto it = gatt_db_cache_.find(addr);
    if (it == gatt_db_cache_.end()) return nullptr;
    for (const auto& entry : it->second) {
        if (entry.attribute_handle == handle) return &entry;
    }
    return nullptr;
}

arc::mojom::BluetoothGattServiceIDPtr
BluetoothArcService::buildServiceId(const GattDbEntry& entry) {
    auto svc_id = arc::mojom::BluetoothGattServiceID::New();
    svc_id->id = arc::mojom::BluetoothGattID::New();
    svc_id->id->uuid = entry.uuid;  // typemap: BluetoothUUID == bluetooth::Uuid
    svc_id->id->deprecated_inst_id = static_cast<uint8_t>(entry.attribute_handle);
    svc_id->id->instance_id = entry.attribute_handle;
    svc_id->is_primary = entry.is_primary ? 1 : 0;
    return svc_id;
}

arc::mojom::BluetoothGattIDPtr
BluetoothArcService::buildGattId(const GattDbEntry& entry) {
    auto gatt_id = arc::mojom::BluetoothGattID::New();
    gatt_id->uuid = entry.uuid;  // typemap: BluetoothUUID == bluetooth::Uuid
    gatt_id->deprecated_inst_id = static_cast<uint8_t>(entry.attribute_handle);
    gatt_id->instance_id = entry.attribute_handle;
    return gatt_id;
}

int BluetoothArcService::getOrCreateConnId(const RawAddress& addr,
                                            int client_if) {
    std::lock_guard<std::mutex> lock(gatt_mutex_);
    auto it = addr_to_conn_id_.find(addr);
    if (it != addr_to_conn_id_.end()) return it->second;
    int conn_id = next_conn_id_++;
    conn_id_to_addr_[conn_id] = addr;
    addr_to_conn_id_[addr] = conn_id;
    conn_id_to_client_if_[conn_id] = client_if;
    return conn_id;
}

// ---------------------------------------------------------------------------
// bt_interface_t init / cleanup
// ---------------------------------------------------------------------------

int BluetoothArcService::initialize(bt_callbacks_t* callbacks) {
    LOG(INFO) << "BluetoothArc: initialize";

    callbacks_ = callbacks;

    // Start the callback thread. The JNI layer will attach it to the JVM
    // when it receives ASSOCIATE_JVM.
    callback_base_thread_ = std::make_unique<base::Thread>("bt_arc_callback");
    base::Thread::Options options;
    options.message_loop_type = base::MessageLoop::TYPE_DEFAULT;
    callback_base_thread_->StartWithOptions(options);
    callback_runner_ = callback_base_thread_->task_runner();

    // Fire ASSOCIATE_JVM on the callback thread so JNI can attach it.
    postToCallback(base::BindOnce([](bt_callbacks_t* cb) {
        if (cb && cb->thread_evt_cb) {
            cb->thread_evt_cb(ASSOCIATE_JVM);
        }
    }, callbacks_));

    // Initialize Mojo and connect to Chrome OS.
    mojo_support_ = arc::MojoProcessSupport::GetInstance();
    bridge_support_ = new arc::ArcBridgeSupport(mojo_support_);
    bridge_support_->requestArcBridgeProxyAsync(
            android::wp<arc::ArcService>(this),
            arc::mojom::BluetoothInstance::Version_);

    LOG(INFO) << "BluetoothArc: Mojo bootstrap initiated, version="
              << arc::mojom::BluetoothInstance::Version_;

    return BT_STATUS_SUCCESS;
}

void BluetoothArcService::cleanup() {
    LOG(INFO) << "BluetoothArc: cleanup";

    // Fire DISASSOCIATE_JVM on the callback thread.
    if (callback_runner_ && callbacks_ && callbacks_->thread_evt_cb) {
        postToCallback(base::BindOnce([](bt_callbacks_t* cb) {
            if (cb && cb->thread_evt_cb) {
                cb->thread_evt_cb(DISASSOCIATE_JVM);
            }
        }, callbacks_));
    }

    postToMojo(base::BindOnce([](BluetoothArcService* self) {
        if (self->binding_.is_bound()) {
            self->binding_.Close();
        }
        self->host_.reset();
    }, this));

    {
        std::lock_guard<std::mutex> lock(host_mutex_);
        host_ready_ = false;
    }

    if (callback_base_thread_) {
        callback_base_thread_->Stop();
        callback_base_thread_.reset();
    }
    callback_runner_ = nullptr;
    callbacks_ = nullptr;
}

// ---------------------------------------------------------------------------
// arc::ArcService overrides
// ---------------------------------------------------------------------------

void BluetoothArcService::ready(
        mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) {
    LOG(INFO) << "BluetoothArc: ARC bridge ready";

    // Register our BluetoothInstance endpoint with Chrome OS.
    mojo::InterfacePtr<arc::mojom::BluetoothInstance> instance_ptr;
    binding_.Bind(mojo::MakeRequest(&instance_ptr));

    binding_.set_connection_error_handler(base::BindOnce([](BluetoothArcService* self) {
        LOG(WARNING) << "BluetoothArc: Mojo binding lost";
        std::lock_guard<std::mutex> lock(self->host_mutex_);
        self->host_.reset();
        self->host_ready_ = false;
    }, this));

    (*host)->OnBluetoothInstanceReady(std::move(instance_ptr));
    LOG(INFO) << "BluetoothArc: registered BluetoothInstance with Chrome OS";
}

void BluetoothArcService::versionMismatch(unsigned int version) {
    LOG(ERROR) << "BluetoothArc: version mismatch - Chrome supports v"
               << version << ", we need v"
               << arc::mojom::BluetoothInstance::Version_;
}

// ---------------------------------------------------------------------------
// arc::mojom::BluetoothInstance::Init
// ---------------------------------------------------------------------------

void BluetoothArcService::Init(
        mojo::InterfacePtr<arc::mojom::BluetoothHost> host,
        const InitCallback& callback) {
    LOG(INFO) << "BluetoothArc: Init - BluetoothHost connected";

    {
        std::lock_guard<std::mutex> lock(host_mutex_);
        host_ = std::move(host);
        host_.set_connection_error_handler(base::BindOnce([](BluetoothArcService* self) {
            LOG(WARNING) << "BluetoothArc: BluetoothHost connection lost";
            std::lock_guard<std::mutex> lk(self->host_mutex_);
            self->host_.reset();
            self->host_ready_ = false;
        }, this));
        host_ready_ = true;
    }
    host_cv_.notify_all();
    callback.Run();
}

// ---------------------------------------------------------------------------
// bt_interface_t operations → BluetoothHost Mojo calls
// ---------------------------------------------------------------------------

int BluetoothArcService::enable() {
    LOG(INFO) << "BluetoothArc: enable";
    postToMojo(base::BindOnce([](BluetoothArcService* self) {
        if (!self->host_) {
            LOG(ERROR) << "BluetoothArc: enable - host not ready";
            return;
        }
        self->host_->EnableAdapter(base::BindRepeating(
            [](BluetoothArcService* s, arc::mojom::BluetoothAdapterState state) {
                LOG(INFO) << "BluetoothArc: EnableAdapter result: " << (int)state;
                if (s->callbacks_ && s->callbacks_->adapter_state_changed_cb) {
                    bt_state_t bt_state = (state == arc::mojom::BluetoothAdapterState::ON)
                            ? BT_STATE_ON : BT_STATE_OFF;
                    s->postToCallback(base::BindOnce(
                        [](bt_callbacks_t* cb, bt_state_t st) {
                            cb->adapter_state_changed_cb(st);
                        }, s->callbacks_, bt_state));
                }
            }, self));
    }, this));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::disable() {
    LOG(INFO) << "BluetoothArc: disable";
    postToMojo(base::BindOnce([](BluetoothArcService* self) {
        if (!self->host_) {
            LOG(ERROR) << "BluetoothArc: disable - host not ready";
            return;
        }
        self->host_->DisableAdapter(base::BindRepeating(
            [](BluetoothArcService* s, arc::mojom::BluetoothAdapterState state) {
                LOG(INFO) << "BluetoothArc: DisableAdapter result: " << (int)state;
                if (s->callbacks_ && s->callbacks_->adapter_state_changed_cb) {
                    bt_state_t bt_state = (state == arc::mojom::BluetoothAdapterState::ON)
                            ? BT_STATE_ON : BT_STATE_OFF;
                    s->postToCallback(base::BindOnce(
                        [](bt_callbacks_t* cb, bt_state_t st) {
                            cb->adapter_state_changed_cb(st);
                        }, s->callbacks_, bt_state));
                }
            }, self));
    }, this));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::getAdapterProperties() {
    LOG(INFO) << "BluetoothArc: getAdapterProperties";
    // Request each property type individually.
    static const bt_property_type_t types[] = {
        BT_PROPERTY_BDNAME,
        BT_PROPERTY_BDADDR,
        BT_PROPERTY_UUIDS,
        BT_PROPERTY_CLASS_OF_DEVICE,
        BT_PROPERTY_TYPE_OF_DEVICE,
        BT_PROPERTY_ADAPTER_SCAN_MODE,
        BT_PROPERTY_ADAPTER_BONDED_DEVICES,
        BT_PROPERTY_LOCAL_LE_FEATURES,
    };
    for (auto type : types) {
        getAdapterProperty(type);
    }
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::getAdapterProperty(bt_property_type_t type) {
    postToMojo(base::BindOnce([](BluetoothArcService* self, bt_property_type_t t) {
        if (!self->host_) {
            LOG(ERROR) << "BluetoothArc: getAdapterProperty - host not ready";
            return;
        }
        // Map bt_property_type_t to BluetoothPropertyType (values match).
        self->host_->GetAdapterProperty(
                static_cast<arc::mojom::BluetoothPropertyType>(t));
    }, this, type));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::setAdapterProperty(const bt_property_t* property) {
    if (!property) return BT_STATUS_PARM_INVALID;

    // Build a Mojo BluetoothProperty from the bt_property_t.
    auto mojo_prop = arc::mojom::BluetoothProperty::New();
    switch (property->type) {
        case BT_PROPERTY_BDNAME: {
            auto* name = reinterpret_cast<const bt_bdname_t*>(property->val);
            mojo_prop->set_bdname(std::string(reinterpret_cast<const char*>(name->name)));
            break;
        }
        case BT_PROPERTY_ADAPTER_SCAN_MODE: {
            auto* mode = reinterpret_cast<const bt_scan_mode_t*>(property->val);
            mojo_prop->set_adapter_scan_mode(
                    static_cast<arc::mojom::BluetoothScanMode>(*mode));
            break;
        }
        case BT_PROPERTY_ADAPTER_DISCOVERABLE_TIMEOUT: {
            auto* timeout = reinterpret_cast<const uint32_t*>(property->val);
            mojo_prop->set_discovery_timeout(*timeout);
            break;
        }
        default:
            LOG(WARNING) << "BluetoothArc: setAdapterProperty unsupported type="
                         << property->type;
            return BT_STATUS_UNSUPPORTED;
    }

    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothPropertyPtr prop) {
        if (!self->host_) return;
        self->host_->SetAdapterProperty(std::move(prop));
    }, this, std::move(mojo_prop)));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::getRemoteDeviceProperties(RawAddress* addr) {
    // Not directly supported via Mojo — Chrome sends these proactively.
    LOG(INFO) << "BluetoothArc: getRemoteDeviceProperties (no-op)";
    return BT_STATUS_UNSUPPORTED;
}

int BluetoothArcService::getRemoteDeviceProperty(RawAddress* addr,
                                                   bt_property_type_t type) {
    LOG(INFO) << "BluetoothArc: getRemoteDeviceProperty (no-op)";
    return BT_STATUS_UNSUPPORTED;
}

int BluetoothArcService::startDiscovery() {
    LOG(INFO) << "BluetoothArc: startDiscovery";
    postToMojo(base::BindOnce([](BluetoothArcService* self) {
        if (!self->host_) return;
        self->host_->StartDiscovery();
    }, this));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::cancelDiscovery() {
    LOG(INFO) << "BluetoothArc: cancelDiscovery";
    postToMojo(base::BindOnce([](BluetoothArcService* self) {
        if (!self->host_) return;
        self->host_->CancelDiscovery();
    }, this));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::createBond(const RawAddress* addr, int transport) {
    if (!addr) return BT_STATUS_PARM_INVALID;
    LOG(INFO) << "BluetoothArc: createBond " << addr->ToString()
              << " transport=" << transport;
    auto mojo_addr = toMojoAddress(*addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a, int t) {
        if (!self->host_) return;
        self->host_->CreateBond(std::move(a), t);
    }, this, std::move(mojo_addr), transport));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::removeBond(const RawAddress* addr) {
    if (!addr) return BT_STATUS_PARM_INVALID;
    LOG(INFO) << "BluetoothArc: removeBond " << addr->ToString();
    auto mojo_addr = toMojoAddress(*addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->RemoveBond(std::move(a));
    }, this, std::move(mojo_addr)));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::cancelBond(const RawAddress* addr) {
    if (!addr) return BT_STATUS_PARM_INVALID;
    LOG(INFO) << "BluetoothArc: cancelBond " << addr->ToString();
    auto mojo_addr = toMojoAddress(*addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->CancelBond(std::move(a));
    }, this, std::move(mojo_addr)));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::getConnectionState(const RawAddress* addr) {
    // This is synchronous in bt_interface_t but async in Mojo.
    // Return 0 (disconnected) for now — the real state comes via callbacks.
    return 0;
}

int BluetoothArcService::pinReply(const RawAddress* addr, uint8_t accept,
                                   uint8_t pin_len, bt_pin_code_t* pin_code) {
    LOG(INFO) << "BluetoothArc: pinReply (not implemented via Mojo)";
    return BT_STATUS_UNSUPPORTED;
}

int BluetoothArcService::sspReply(const RawAddress* addr,
                                   bt_ssp_variant_t variant,
                                   uint8_t accept, uint32_t passkey) {
    LOG(INFO) << "BluetoothArc: sspReply (not implemented via Mojo)";
    return BT_STATUS_UNSUPPORTED;
}

const void* BluetoothArcService::getProfileInterface(const char* profile_id) {
    if (!profile_id) return nullptr;

    LOG(INFO) << "BluetoothArc: getProfileInterface(" << profile_id << ")";

    if (strcmp(profile_id, BT_PROFILE_GATT_ID) == 0) {
        return getGattInterface();
    }

    LOG(INFO) << "BluetoothArc: profile " << profile_id << " not supported";
    return nullptr;
}

// ---------------------------------------------------------------------------
// BluetoothInstance Mojo callbacks → bt_callbacks_t
// ---------------------------------------------------------------------------

void BluetoothArcService::OnAdapterProperties(
        arc::mojom::BluetoothStatus status,
        std::vector<arc::mojom::BluetoothPropertyPtr> properties) {
    LOG(INFO) << "BluetoothArc: OnAdapterProperties status=" << (int)status
              << " count=" << properties.size();

    if (!callbacks_ || !callbacks_->adapter_properties_cb) return;

    auto props = std::make_shared<std::vector<bt_property_t>>();
    auto storage = std::make_shared<std::vector<std::vector<uint8_t>>>();
    convertProperties(properties, props.get(), storage.get());

    bt_status_t bt_status = static_cast<bt_status_t>(status);
    postToCallback(base::BindOnce(
        [](bt_callbacks_t* cb, bt_status_t s,
           std::shared_ptr<std::vector<bt_property_t>> p,
           std::shared_ptr<std::vector<std::vector<uint8_t>>> st) {
            cb->adapter_properties_cb(s, p->size(), p->data());
        }, callbacks_, bt_status, props, storage));
}

void BluetoothArcService::OnDeviceFound(
        std::vector<arc::mojom::BluetoothPropertyPtr> properties) {
    LOG(INFO) << "BluetoothArc: OnDeviceFound props=" << properties.size();

    if (!callbacks_ || !callbacks_->device_found_cb) return;

    auto props = std::make_shared<std::vector<bt_property_t>>();
    auto storage = std::make_shared<std::vector<std::vector<uint8_t>>>();
    convertProperties(properties, props.get(), storage.get());

    postToCallback(base::BindOnce(
        [](bt_callbacks_t* cb,
           std::shared_ptr<std::vector<bt_property_t>> p,
           std::shared_ptr<std::vector<std::vector<uint8_t>>> st) {
            cb->device_found_cb(p->size(), p->data());
        }, callbacks_, props, storage));
}

void BluetoothArcService::OnDevicePropertiesChanged(
        arc::mojom::BluetoothAddressPtr remote_addr,
        std::vector<arc::mojom::BluetoothPropertyPtr> properties) {
    LOG(INFO) << "BluetoothArc: OnDevicePropertiesChanged";

    if (!callbacks_ || !callbacks_->remote_device_properties_cb) return;

    auto raw_addr = std::make_shared<RawAddress>(toRawAddress(remote_addr));
    auto props = std::make_shared<std::vector<bt_property_t>>();
    auto storage = std::make_shared<std::vector<std::vector<uint8_t>>>();
    convertProperties(properties, props.get(), storage.get());

    postToCallback(base::BindOnce(
        [](bt_callbacks_t* cb,
           std::shared_ptr<RawAddress> addr,
           std::shared_ptr<std::vector<bt_property_t>> p,
           std::shared_ptr<std::vector<std::vector<uint8_t>>> st) {
            cb->remote_device_properties_cb(BT_STATUS_SUCCESS, addr.get(),
                                            p->size(), p->data());
        }, callbacks_, raw_addr, props, storage));
}

void BluetoothArcService::OnDiscoveryStateChanged(
        arc::mojom::BluetoothDiscoveryState state) {
    LOG(INFO) << "BluetoothArc: OnDiscoveryStateChanged state=" << (int)state;

    if (!callbacks_ || !callbacks_->discovery_state_changed_cb) return;

    bt_discovery_state_t bt_state =
            (state == arc::mojom::BluetoothDiscoveryState::STARTED)
            ? BT_DISCOVERY_STARTED : BT_DISCOVERY_STOPPED;

    postToCallback(base::BindOnce(
        [](bt_callbacks_t* cb, bt_discovery_state_t s) {
            cb->discovery_state_changed_cb(s);
        }, callbacks_, bt_state));
}

void BluetoothArcService::OnBondStateChanged(
        arc::mojom::BluetoothStatus status,
        arc::mojom::BluetoothAddressPtr remote_addr,
        arc::mojom::BluetoothBondState state) {
    LOG(INFO) << "BluetoothArc: OnBondStateChanged status=" << (int)status
              << " state=" << (int)state;

    if (!callbacks_ || !callbacks_->bond_state_changed_cb) return;

    auto raw_addr = std::make_shared<RawAddress>(toRawAddress(remote_addr));
    bt_status_t bt_status = static_cast<bt_status_t>(status);
    bt_bond_state_t bt_bond = static_cast<bt_bond_state_t>(state);

    postToCallback(base::BindOnce(
        [](bt_callbacks_t* cb, bt_status_t s,
           std::shared_ptr<RawAddress> addr, bt_bond_state_t b) {
            // 13 added a trailing fail_reason (HCI reason for a failed bond).
            cb->bond_state_changed_cb(s, addr.get(), b, 0 /* fail_reason */);
        }, callbacks_, bt_status, raw_addr, bt_bond));
}

void BluetoothArcService::OnConnectionStateChanged(
        arc::mojom::BluetoothAddressPtr remote_addr,
        bt_device_type_t device_type,
        bool connected) {
    LOG(INFO) << "BluetoothArc: OnConnectionStateChanged connected=" << connected;

    if (!callbacks_ || !callbacks_->acl_state_changed_cb) return;

    auto raw_addr = std::make_shared<RawAddress>(toRawAddress(remote_addr));
    bt_acl_state_t acl_state = connected
            ? BT_ACL_STATE_CONNECTED : BT_ACL_STATE_DISCONNECTED;

    postToCallback(base::BindOnce(
        [](bt_callbacks_t* cb,
           std::shared_ptr<RawAddress> addr, bt_acl_state_t s) {
            // 13 added transport_link_type + hci_reason trailing args.
            cb->acl_state_changed_cb(BT_STATUS_SUCCESS, addr.get(), s,
                                     0 /* transport_link_type: BR/EDR */,
                                     (bt_hci_error_code_t)0 /* hci_reason */);
        }, callbacks_, raw_addr, acl_state));
}

// ---------------------------------------------------------------------------
// GATT init/cleanup
// ---------------------------------------------------------------------------

bt_status_t BluetoothArcService::initGatt(const btgatt_callbacks_t* callbacks) {
    LOG(INFO) << "BluetoothArc: initGatt";
    if (!callbacks) return BT_STATUS_PARM_INVALID;
    gatt_client_cbs_ = callbacks->client;
    gatt_server_cbs_ = callbacks->server;
    gatt_scanner_cbs_ = callbacks->scanner;
    return BT_STATUS_SUCCESS;
}

void BluetoothArcService::cleanupGatt() {
    LOG(INFO) << "BluetoothArc: cleanupGatt";
    std::lock_guard<std::mutex> lock(gatt_mutex_);
    gatt_client_cbs_ = nullptr;
    gatt_server_cbs_ = nullptr;
    gatt_scanner_cbs_ = nullptr;
    client_if_to_uuid_.clear();
    server_if_to_uuid_.clear();
    conn_id_to_addr_.clear();
    addr_to_conn_id_.clear();
    conn_id_to_client_if_.clear();
    gatt_db_cache_.clear();
    pending_reads_.clear();
    pending_writes_.clear();
    pending_exec_writes_.clear();
}

// ---------------------------------------------------------------------------
// GATT Client operations
// ---------------------------------------------------------------------------

bt_status_t BluetoothArcService::gattRegisterClient(
        const bluetooth::Uuid& uuid) {
    int client_if;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        client_if = next_client_if_++;
        client_if_to_uuid_[client_if] = uuid;
    }
    LOG(INFO) << "BluetoothArc: gattRegisterClient if=" << client_if;

    auto uuid_copy = uuid;
    postToCallback(base::BindOnce(
        [](const btgatt_client_callbacks_t* cbs, int cif, bluetooth::Uuid u) {
            if (cbs && cbs->register_client_cb)
                cbs->register_client_cb(0 /* success */, cif, u);
        }, gatt_client_cbs_, client_if, uuid_copy));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattUnregisterClient(int client_if) {
    LOG(INFO) << "BluetoothArc: gattUnregisterClient if=" << client_if;
    std::lock_guard<std::mutex> lock(gatt_mutex_);
    client_if_to_uuid_.erase(client_if);
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattConnect(
        int client_if, const RawAddress& bd_addr, bool is_direct,
        int transport, bool opportunistic, int initiating_phys) {
    LOG(INFO) << "BluetoothArc: gattConnect if=" << client_if
              << " addr=" << bd_addr.ToString();

    auto mojo_addr = toMojoAddress(bd_addr);
    RawAddress addr_copy = bd_addr;
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 RawAddress addr, int cif) {
        if (!self->host_) return;
        self->host_->ConnectLEDevice(std::move(a));
        // OnLEConnectionStateChange will fire the open_cb
    }, this, std::move(mojo_addr), addr_copy, client_if));

    // Store the client_if so OnLEConnectionStateChange can use it.
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        // Preemptively associate this address with this client_if.
        // conn_id will be created when the connection callback arrives.
        auto it = addr_to_conn_id_.find(bd_addr);
        if (it != addr_to_conn_id_.end()) {
            conn_id_to_client_if_[it->second] = client_if;
        }
    }
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattDisconnect(
        int client_if, const RawAddress& bd_addr, int conn_id) {
    LOG(INFO) << "BluetoothArc: gattDisconnect if=" << client_if
              << " conn_id=" << conn_id;

    auto mojo_addr = toMojoAddress(bd_addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->DisconnectLEDevice(std::move(a));
    }, this, std::move(mojo_addr)));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattRefresh(int client_if,
                                              const RawAddress& bd_addr) {
    LOG(INFO) << "BluetoothArc: gattRefresh";
    std::lock_guard<std::mutex> lock(gatt_mutex_);
    gatt_db_cache_.erase(bd_addr);
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattSearchService(
        int conn_id, const bluetooth::Uuid* filter_uuid) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }
    LOG(INFO) << "BluetoothArc: gattSearchService conn_id=" << conn_id;

    auto mojo_addr = toMojoAddress(addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->SearchService(std::move(a));
    }, this, std::move(mojo_addr)));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattGetGattDb(int conn_id) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }
    LOG(INFO) << "BluetoothArc: gattGetGattDb conn_id=" << conn_id;

    auto mojo_addr = toMojoAddress(addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->GetGattDB(std::move(a));
    }, this, std::move(mojo_addr)));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattReadCharacteristic(
        int conn_id, uint16_t handle, int auth_req) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }

    const GattDbEntry* entry = lookupGattEntry(addr, handle);
    if (!entry) {
        LOG(ERROR) << "BluetoothArc: gattReadCharacteristic handle " << handle
                   << " not found in DB";
        return BT_STATUS_FAIL;
    }

    const GattDbEntry* svc_entry = lookupGattEntry(addr,
                                                    entry->parent_service_handle);
    if (!svc_entry) return BT_STATUS_FAIL;

    auto svc_id = buildServiceId(*svc_entry);
    auto char_id = buildGattId(*entry);
    auto mojo_addr = toMojoAddress(addr);

    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 arc::mojom::BluetoothGattServiceIDPtr sid,
                                 arc::mojom::BluetoothGattIDPtr cid,
                                 int cid_val, uint16_t h) {
        if (!self->host_) return;
        self->host_->ReadGattCharacteristic(
                std::move(a), std::move(sid), std::move(cid),
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  uint16_t handle,
                                  arc::mojom::BluetoothGattValuePtr val) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->read_characteristic_cb)
                        return;
                    btgatt_read_params_t params = {};
                    params.handle = handle;
                    params.status = static_cast<uint8_t>(val->status);
                    size_t len = std::min(val->value.size(),
                                         (size_t)BTGATT_MAX_ATTR_LEN);
                    memcpy(params.value.value, val->value.data(), len);
                    params.value.len = len;
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int c,
                           btgatt_read_params_t p) {
                            cbs->read_characteristic_cb(c, p.status, &p);
                        }, s->gatt_client_cbs_, conn, params));
                }, self, cid_val, h));
    }, this, std::move(mojo_addr), std::move(svc_id), std::move(char_id),
       conn_id, handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattWriteCharacteristic(
        int conn_id, uint16_t handle, int write_type, int auth_req,
        std::vector<uint8_t> value) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }

    const GattDbEntry* entry = lookupGattEntry(addr, handle);
    if (!entry) return BT_STATUS_FAIL;
    const GattDbEntry* svc_entry = lookupGattEntry(addr,
                                                    entry->parent_service_handle);
    if (!svc_entry) return BT_STATUS_FAIL;

    auto svc_id = buildServiceId(*svc_entry);
    auto char_id = buildGattId(*entry);
    auto mojo_addr = toMojoAddress(addr);

    // Build BluetoothGattValue
    auto gatt_val = arc::mojom::BluetoothGattValue::New();
    gatt_val->status = arc::mojom::BluetoothGattStatus::GATT_SUCCESS;
    gatt_val->value = std::move(value);

    // write_type 3 = GATT_WRITE_PREPARE
    bool prepare = (write_type == 3);

    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 arc::mojom::BluetoothGattServiceIDPtr sid,
                                 arc::mojom::BluetoothGattIDPtr cid,
                                 arc::mojom::BluetoothGattValuePtr val,
                                 bool prep, int conn, uint16_t h) {
        if (!self->host_) return;
        self->host_->WriteGattCharacteristic(
                std::move(a), std::move(sid), std::move(cid),
                std::move(val), prep,
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  uint16_t handle,
                                  arc::mojom::BluetoothGattStatus status) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->write_characteristic_cb)
                        return;
                    int st = static_cast<int>(status);
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int c,
                           int status, uint16_t h) {
                            // 13 added len + value (echo of written data).
                            cbs->write_characteristic_cb(c, status, h,
                                                         0 /* len */, nullptr);
                        }, s->gatt_client_cbs_, conn, st, handle));
                }, self, conn, h));
    }, this, std::move(mojo_addr), std::move(svc_id), std::move(char_id),
       std::move(gatt_val), prepare, conn_id, handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattReadDescriptor(
        int conn_id, uint16_t handle, int auth_req) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }

    const GattDbEntry* entry = lookupGattEntry(addr, handle);
    if (!entry || entry->type != BTGATT_DB_DESCRIPTOR) return BT_STATUS_FAIL;
    const GattDbEntry* char_entry = lookupGattEntry(addr,
                                                     entry->parent_char_handle);
    if (!char_entry) return BT_STATUS_FAIL;
    const GattDbEntry* svc_entry = lookupGattEntry(addr,
                                                    entry->parent_service_handle);
    if (!svc_entry) return BT_STATUS_FAIL;

    auto svc_id = buildServiceId(*svc_entry);
    auto char_id = buildGattId(*char_entry);
    auto desc_id = buildGattId(*entry);
    auto mojo_addr = toMojoAddress(addr);

    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 arc::mojom::BluetoothGattServiceIDPtr sid,
                                 arc::mojom::BluetoothGattIDPtr cid,
                                 arc::mojom::BluetoothGattIDPtr did,
                                 int conn, uint16_t h) {
        if (!self->host_) return;
        self->host_->ReadGattDescriptor(
                std::move(a), std::move(sid), std::move(cid), std::move(did),
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  uint16_t handle,
                                  arc::mojom::BluetoothGattValuePtr val) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->read_descriptor_cb)
                        return;
                    btgatt_read_params_t params = {};
                    params.handle = handle;
                    params.status = static_cast<uint8_t>(val->status);
                    size_t len = std::min(val->value.size(),
                                         (size_t)BTGATT_MAX_ATTR_LEN);
                    memcpy(params.value.value, val->value.data(), len);
                    params.value.len = len;
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int c,
                           btgatt_read_params_t p) {
                            cbs->read_descriptor_cb(c, p.status, p);
                        }, s->gatt_client_cbs_, conn, params));
                }, self, conn, h));
    }, this, std::move(mojo_addr), std::move(svc_id), std::move(char_id),
       std::move(desc_id), conn_id, handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattWriteDescriptor(
        int conn_id, uint16_t handle, int auth_req,
        std::vector<uint8_t> value) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }

    const GattDbEntry* entry = lookupGattEntry(addr, handle);
    if (!entry || entry->type != BTGATT_DB_DESCRIPTOR) return BT_STATUS_FAIL;
    const GattDbEntry* char_entry = lookupGattEntry(addr,
                                                     entry->parent_char_handle);
    if (!char_entry) return BT_STATUS_FAIL;
    const GattDbEntry* svc_entry = lookupGattEntry(addr,
                                                    entry->parent_service_handle);
    if (!svc_entry) return BT_STATUS_FAIL;

    auto svc_id = buildServiceId(*svc_entry);
    auto char_id = buildGattId(*char_entry);
    auto desc_id = buildGattId(*entry);
    auto mojo_addr = toMojoAddress(addr);

    auto gatt_val = arc::mojom::BluetoothGattValue::New();
    gatt_val->status = arc::mojom::BluetoothGattStatus::GATT_SUCCESS;
    gatt_val->value = std::move(value);

    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 arc::mojom::BluetoothGattServiceIDPtr sid,
                                 arc::mojom::BluetoothGattIDPtr cid,
                                 arc::mojom::BluetoothGattIDPtr did,
                                 arc::mojom::BluetoothGattValuePtr val,
                                 int conn, uint16_t h) {
        if (!self->host_) return;
        self->host_->WriteGattDescriptor(
                std::move(a), std::move(sid), std::move(cid),
                std::move(did), std::move(val),
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  uint16_t handle,
                                  arc::mojom::BluetoothGattStatus status) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->write_descriptor_cb)
                        return;
                    int st = static_cast<int>(status);
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int c,
                           int status, uint16_t h) {
                            // 13 added len + value (echo of written data).
                            cbs->write_descriptor_cb(c, status, h,
                                                     0 /* len */, nullptr);
                        }, s->gatt_client_cbs_, conn, st, handle));
                }, self, conn, h));
    }, this, std::move(mojo_addr), std::move(svc_id), std::move(char_id),
       std::move(desc_id), std::move(gatt_val), conn_id, handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattExecuteWrite(int conn_id, int execute) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }

    auto mojo_addr = toMojoAddress(addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 bool exec, int conn) {
        if (!self->host_) return;
        self->host_->ExecuteWrite(
                std::move(a), exec,
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  arc::mojom::BluetoothGattStatus status) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->execute_write_cb)
                        return;
                    int st = static_cast<int>(status);
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int c,
                           int status) {
                            cbs->execute_write_cb(c, status);
                        }, s->gatt_client_cbs_, conn, st));
                }, self, conn));
    }, this, std::move(mojo_addr), execute != 0, conn_id));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattRegisterForNotification(
        int client_if, const RawAddress& bd_addr, uint16_t handle) {
    LOG(INFO) << "BluetoothArc: gattRegisterForNotification handle=" << handle;

    const GattDbEntry* entry = lookupGattEntry(bd_addr, handle);
    if (!entry) return BT_STATUS_FAIL;
    const GattDbEntry* svc_entry = lookupGattEntry(bd_addr,
                                                    entry->parent_service_handle);
    if (!svc_entry) return BT_STATUS_FAIL;

    auto svc_id = buildServiceId(*svc_entry);
    auto char_id = buildGattId(*entry);
    auto mojo_addr = toMojoAddress(bd_addr);

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(bd_addr);
        conn_id = (it != addr_to_conn_id_.end()) ? it->second : 0;
    }

    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 arc::mojom::BluetoothGattServiceIDPtr sid,
                                 arc::mojom::BluetoothGattIDPtr cid,
                                 int conn, uint16_t h) {
        if (!self->host_) return;
        self->host_->RegisterForGattNotification(
                std::move(a), std::move(sid), std::move(cid),
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  uint16_t handle,
                                  arc::mojom::BluetoothGattStatus status) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->register_for_notification_cb)
                        return;
                    int st = static_cast<int>(status);
                    int registered = (status == arc::mojom::BluetoothGattStatus::GATT_SUCCESS) ? 1 : 0;
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int c,
                           int reg, int status, uint16_t h) {
                            cbs->register_for_notification_cb(c, reg, status, h);
                        }, s->gatt_client_cbs_, conn, registered, st, handle));
                }, self, conn, h));
    }, this, std::move(mojo_addr), std::move(svc_id), std::move(char_id),
       conn_id, handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattDeregisterForNotification(
        int client_if, const RawAddress& bd_addr, uint16_t handle) {
    LOG(INFO) << "BluetoothArc: gattDeregisterForNotification handle=" << handle;

    const GattDbEntry* entry = lookupGattEntry(bd_addr, handle);
    if (!entry) return BT_STATUS_FAIL;
    const GattDbEntry* svc_entry = lookupGattEntry(bd_addr,
                                                    entry->parent_service_handle);
    if (!svc_entry) return BT_STATUS_FAIL;

    auto svc_id = buildServiceId(*svc_entry);
    auto char_id = buildGattId(*entry);
    auto mojo_addr = toMojoAddress(bd_addr);

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(bd_addr);
        conn_id = (it != addr_to_conn_id_.end()) ? it->second : 0;
    }

    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 arc::mojom::BluetoothGattServiceIDPtr sid,
                                 arc::mojom::BluetoothGattIDPtr cid,
                                 int conn, uint16_t h) {
        if (!self->host_) return;
        self->host_->DeregisterForGattNotification(
                std::move(a), std::move(sid), std::move(cid),
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  uint16_t handle,
                                  arc::mojom::BluetoothGattStatus status) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->register_for_notification_cb)
                        return;
                    int st = static_cast<int>(status);
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int c,
                           int reg, int status, uint16_t h) {
                            cbs->register_for_notification_cb(c, reg, status, h);
                        }, s->gatt_client_cbs_, conn, 0 /* unregistered */,
                        st, handle));
                }, self, conn, h));
    }, this, std::move(mojo_addr), std::move(svc_id), std::move(char_id),
       conn_id, handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattReadRemoteRssi(
        int client_if, const RawAddress& bd_addr) {
    LOG(INFO) << "BluetoothArc: gattReadRemoteRssi";

    auto mojo_addr = toMojoAddress(bd_addr);
    RawAddress addr_copy = bd_addr;
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a,
                                 RawAddress addr, int cif) {
        if (!self->host_) return;
        self->host_->ReadRemoteRssi(
                std::move(a),
                base::BindRepeating([](BluetoothArcService* s, RawAddress addr,
                                  int cif, int32_t rssi) {
                    if (!s->gatt_client_cbs_ ||
                        !s->gatt_client_cbs_->read_remote_rssi_cb)
                        return;
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_client_callbacks_t* cbs, int cif,
                           RawAddress addr, int rssi) {
                            cbs->read_remote_rssi_cb(cif, addr, rssi,
                                                     0 /* success */);
                        }, s->gatt_client_cbs_, cif, addr, (int)rssi));
                }, self, addr, cif));
    }, this, std::move(mojo_addr), addr_copy, client_if));
    return BT_STATUS_SUCCESS;
}

int BluetoothArcService::gattGetDeviceType(const RawAddress& bd_addr) {
    return BT_DEVICE_DEVTYPE_BLE;
}

bt_status_t BluetoothArcService::gattConfigureMtu(int conn_id, int mtu) {
    LOG(INFO) << "BluetoothArc: gattConfigureMtu conn_id=" << conn_id
              << " mtu=" << mtu;
    // Chrome manages MTU — report success with 512.
    postToCallback(base::BindOnce(
        [](const btgatt_client_callbacks_t* cbs, int conn) {
            if (cbs && cbs->configure_mtu_cb)
                cbs->configure_mtu_cb(conn, 0 /* success */, 512);
        }, gatt_client_cbs_, conn_id));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattConnParameterUpdate(
        const RawAddress& bd_addr, int min_interval, int max_interval,
        int latency, int timeout, uint16_t min_ce_len, uint16_t max_ce_len) {
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattSetPreferredPhy(
        const RawAddress& bd_addr, uint8_t tx_phy, uint8_t rx_phy,
        uint16_t phy_options) {
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattReadPhy(
        const RawAddress& bd_addr,
        base::Callback<void(uint8_t, uint8_t, uint8_t)> cb) {
    // Default: 1M PHY
    cb.Run(1 /* tx_phy */, 1 /* rx_phy */, 0 /* status */);
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattTestCommand(
        int command, const btgatt_test_params_t& params) {
    return BT_STATUS_UNSUPPORTED;
}

bt_status_t BluetoothArcService::gattReadUsingCharUuid(
        int conn_id, const bluetooth::Uuid& uuid, uint16_t s_handle,
        uint16_t e_handle, int auth_req) {
    return BT_STATUS_UNSUPPORTED;
}

void BluetoothArcService::gattDiscoverServiceByUuid(
        int conn_id, const bluetooth::Uuid& uuid) {
    // PTS only — no-op
}

// BLE Scanner
void BluetoothArcService::startLEScan(bool start) {
    LOG(INFO) << "BluetoothArc: startLEScan start=" << start;
    postToMojo(base::BindOnce([](BluetoothArcService* self, bool s) {
        if (!self->host_) return;
        if (s)
            self->host_->StartLEScan();
        else
            self->host_->StopLEScan();
    }, this, start));
}

// ---------------------------------------------------------------------------
// GATT Server operations
// ---------------------------------------------------------------------------

bt_status_t BluetoothArcService::gattRegisterServer(
        const bluetooth::Uuid& uuid) {
    int server_if;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        server_if = next_server_if_++;
        server_if_to_uuid_[server_if] = uuid;
    }
    LOG(INFO) << "BluetoothArc: gattRegisterServer if=" << server_if;

    auto uuid_copy = uuid;
    postToCallback(base::BindOnce(
        [](const btgatt_server_callbacks_t* cbs, int sif, bluetooth::Uuid u) {
            if (cbs && cbs->register_server_cb)
                cbs->register_server_cb(0 /* success */, sif, u);
        }, gatt_server_cbs_, server_if, uuid_copy));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattUnregisterServer(int server_if) {
    LOG(INFO) << "BluetoothArc: gattUnregisterServer if=" << server_if;
    std::lock_guard<std::mutex> lock(gatt_mutex_);
    server_if_to_uuid_.erase(server_if);
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattServerConnect(
        int server_if, const RawAddress& bd_addr, bool is_direct,
        int transport) {
    LOG(INFO) << "BluetoothArc: gattServerConnect";
    auto mojo_addr = toMojoAddress(bd_addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->ConnectLEDevice(std::move(a));
    }, this, std::move(mojo_addr)));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattServerDisconnect(
        int server_if, const RawAddress& bd_addr, int conn_id) {
    LOG(INFO) << "BluetoothArc: gattServerDisconnect";
    auto mojo_addr = toMojoAddress(bd_addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->DisconnectLEDevice(std::move(a));
    }, this, std::move(mojo_addr)));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattAddService(
        int server_if, std::vector<btgatt_db_element_t> service) {
    if (service.empty()) return BT_STATUS_PARM_INVALID;

    LOG(INFO) << "BluetoothArc: gattAddService elements=" << service.size();

    // The first element must be a service. We need to add:
    // 1. AddService (the service itself)
    // 2. AddCharacteristic for each characteristic
    // 3. AddDescriptor for each descriptor
    // 4. StartService
    // Chain these sequentially on the Mojo thread.

    auto svc_copy = std::make_shared<std::vector<btgatt_db_element_t>>(
            std::move(service));

    postToMojo(base::BindOnce([](BluetoothArcService* self, int sif,
                                 std::shared_ptr<std::vector<btgatt_db_element_t>> svc) {
        if (!self->host_ || svc->empty()) return;

        const auto& first = (*svc)[0];
        bool is_primary = (first.type == BTGATT_DB_PRIMARY_SERVICE);

        auto svc_id = arc::mojom::BluetoothGattServiceID::New();
        svc_id->id = arc::mojom::BluetoothGattID::New();
        svc_id->id->uuid = first.uuid;
        svc_id->id->deprecated_inst_id = 0;
        svc_id->id->instance_id = 0;
        svc_id->is_primary = is_primary ? 1 : 0;

        int num_handles = svc->size();

        self->host_->AddService(
                std::move(svc_id), num_handles,
                base::BindRepeating([](BluetoothArcService* s, int sif,
                                  std::shared_ptr<std::vector<btgatt_db_element_t>> svc,
                                  int32_t service_handle) {
                    if (service_handle < 0) {
                        LOG(ERROR) << "BluetoothArc: AddService failed";
                        if (s->gatt_server_cbs_ && s->gatt_server_cbs_->service_added_cb) {
                            s->postToCallback(base::BindOnce(
                                [](const btgatt_server_callbacks_t* cbs, int sif,
                                   std::vector<btgatt_db_element_t> svc) {
                                    // 13: service list is now ptr + count.
                                    cbs->service_added_cb(
                                        BT_STATUS_FAIL, sif, svc.data(),
                                        svc.size());
                                }, s->gatt_server_cbs_, sif, *svc));
                        }
                        return;
                    }

                    // Walk remaining elements and add chars/descriptors
                    // For simplicity, add them sequentially via recursive lambda
                    (*svc)[0].attribute_handle = service_handle;
                    (*svc)[0].start_handle = service_handle;

                    // Helper: add elements starting at index i
                    struct AddHelper {
                        static void addNext(BluetoothArcService* s, int sif,
                                           std::shared_ptr<std::vector<btgatt_db_element_t>> svc,
                                           int32_t service_handle, size_t i) {
                            if (i >= svc->size()) {
                                // All elements added; start the service
                                s->host_->StartService(
                                    service_handle,
                                    base::BindRepeating([](BluetoothArcService* s, int sif,
                                                     std::shared_ptr<std::vector<btgatt_db_element_t>> svc,
                                                     int32_t sh,
                                                     arc::mojom::BluetoothGattStatus status) {
                                        (*svc)[0].end_handle = sh + svc->size() - 1;
                                        if (s->gatt_server_cbs_ && s->gatt_server_cbs_->service_added_cb) {
                                            int st = (status == arc::mojom::BluetoothGattStatus::GATT_SUCCESS)
                                                     ? BT_STATUS_SUCCESS : BT_STATUS_FAIL;
                                            s->postToCallback(base::BindOnce(
                                                [](const btgatt_server_callbacks_t* cbs, int st,
                                                   int sif, std::vector<btgatt_db_element_t> svc) {
                                                    // 13: service list is now ptr + count.
                                                    cbs->service_added_cb(
                                                        (bt_status_t)st, sif,
                                                        svc.data(), svc.size());
                                                }, s->gatt_server_cbs_, st, sif, *svc));
                                        }
                                    }, s, sif, svc, service_handle));
                                return;
                            }

                            auto& elem = (*svc)[i];
                            if (elem.type == BTGATT_DB_CHARACTERISTIC) {
                                s->host_->AddCharacteristic(
                                    service_handle, elem.uuid,
                                    elem.properties, elem.permissions,
                                    base::BindRepeating([](BluetoothArcService* s, int sif,
                                                     std::shared_ptr<std::vector<btgatt_db_element_t>> svc,
                                                     int32_t service_handle, size_t i,
                                                     int32_t char_handle) {
                                        (*svc)[i].attribute_handle = char_handle;
                                        addNext(s, sif, svc, service_handle, i + 1);
                                    }, s, sif, svc, service_handle, i));
                            } else if (elem.type == BTGATT_DB_DESCRIPTOR) {
                                s->host_->AddDescriptor(
                                    service_handle, elem.uuid,
                                    elem.permissions,
                                    base::BindRepeating([](BluetoothArcService* s, int sif,
                                                     std::shared_ptr<std::vector<btgatt_db_element_t>> svc,
                                                     int32_t service_handle, size_t i,
                                                     int32_t desc_handle) {
                                        (*svc)[i].attribute_handle = desc_handle;
                                        addNext(s, sif, svc, service_handle, i + 1);
                                    }, s, sif, svc, service_handle, i));
                            } else {
                                // Skip unknown element types
                                addNext(s, sif, svc, service_handle, i + 1);
                            }
                        }
                    };

                    AddHelper::addNext(s, sif, svc, service_handle, 1);
                }, self, sif, svc));
    }, this, server_if, svc_copy));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattStopService(int server_if,
                                                  int service_handle) {
    LOG(INFO) << "BluetoothArc: gattStopService handle=" << service_handle;
    postToMojo(base::BindOnce([](BluetoothArcService* self, int sif, int sh) {
        if (!self->host_) return;
        self->host_->StopService(sh,
            base::BindRepeating([](BluetoothArcService* s, int sif, int sh,
                              arc::mojom::BluetoothGattStatus status) {
                if (s->gatt_server_cbs_ && s->gatt_server_cbs_->service_stopped_cb) {
                    int st = (status == arc::mojom::BluetoothGattStatus::GATT_SUCCESS)
                             ? BT_STATUS_SUCCESS : BT_STATUS_FAIL;
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_server_callbacks_t* cbs, int st, int sif,
                           int sh) {
                            cbs->service_stopped_cb(st, sif, sh);
                        }, s->gatt_server_cbs_, st, sif, sh));
                }
            }, self, sif, sh));
    }, this, server_if, service_handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattDeleteService(int server_if,
                                                    int service_handle) {
    LOG(INFO) << "BluetoothArc: gattDeleteService handle=" << service_handle;
    postToMojo(base::BindOnce([](BluetoothArcService* self, int sif, int sh) {
        if (!self->host_) return;
        self->host_->DeleteService(sh,
            base::BindRepeating([](BluetoothArcService* s, int sif, int sh,
                              arc::mojom::BluetoothGattStatus status) {
                if (s->gatt_server_cbs_ && s->gatt_server_cbs_->service_deleted_cb) {
                    int st = (status == arc::mojom::BluetoothGattStatus::GATT_SUCCESS)
                             ? BT_STATUS_SUCCESS : BT_STATUS_FAIL;
                    s->postToCallback(base::BindOnce(
                        [](const btgatt_server_callbacks_t* cbs, int st, int sif,
                           int sh) {
                            cbs->service_deleted_cb(st, sif, sh);
                        }, s->gatt_server_cbs_, st, sif, sh));
                }
            }, self, sif, sh));
    }, this, server_if, service_handle));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattSendIndication(
        int server_if, int attribute_handle, int conn_id, int confirm,
        std::vector<uint8_t> value) {
    RawAddress addr;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = conn_id_to_addr_.find(conn_id);
        if (it == conn_id_to_addr_.end()) return BT_STATUS_FAIL;
        addr = it->second;
    }

    auto mojo_addr = toMojoAddress(addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 int attr_handle,
                                 arc::mojom::BluetoothAddressPtr a,
                                 bool confirm, std::vector<uint8_t> val,
                                 int conn) {
        if (!self->host_) return;
        self->host_->SendIndication(
                attr_handle, std::move(a), confirm, std::move(val),
                base::BindRepeating([](BluetoothArcService* s, int conn,
                                  arc::mojom::BluetoothGattStatus status) {
                    if (s->gatt_server_cbs_ &&
                        s->gatt_server_cbs_->indication_sent_cb) {
                        int st = static_cast<int>(status);
                        s->postToCallback(base::BindOnce(
                            [](const btgatt_server_callbacks_t* cbs, int c,
                               int st) {
                                cbs->indication_sent_cb(c, st);
                            }, s->gatt_server_cbs_, conn, st));
                    }
                }, self, conn));
    }, this, attribute_handle, std::move(mojo_addr), confirm != 0,
       std::move(value), conn_id));
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattSendResponse(
        int conn_id, int trans_id, int status,
        const btgatt_response_t& response) {
    LOG(INFO) << "BluetoothArc: gattSendResponse trans_id=" << trans_id
              << " status=" << status;

    std::lock_guard<std::mutex> lock(gatt_mutex_);

    auto gatt_status = static_cast<arc::mojom::BluetoothGattStatus>(status);

    // Check pending reads
    auto read_it = pending_reads_.find(trans_id);
    if (read_it != pending_reads_.end()) {
        std::vector<uint8_t> value;
        if (status == 0) {
            const auto& attr = response.attr_value;
            value.assign(attr.value, attr.value + attr.len);
        }
        read_it->second.Run(gatt_status, std::move(value));
        pending_reads_.erase(read_it);
        return BT_STATUS_SUCCESS;
    }

    // Check pending writes
    auto write_it = pending_writes_.find(trans_id);
    if (write_it != pending_writes_.end()) {
        write_it->second.Run(gatt_status);
        pending_writes_.erase(write_it);
        return BT_STATUS_SUCCESS;
    }

    // Check pending execute writes
    auto exec_it = pending_exec_writes_.find(trans_id);
    if (exec_it != pending_exec_writes_.end()) {
        exec_it->second.Run(gatt_status);
        pending_exec_writes_.erase(exec_it);
        return BT_STATUS_SUCCESS;
    }

    LOG(WARNING) << "BluetoothArc: gattSendResponse - unknown trans_id "
                 << trans_id;
    return BT_STATUS_FAIL;
}

bt_status_t BluetoothArcService::gattServerSetPreferredPhy(
        const RawAddress& bd_addr, uint8_t tx_phy, uint8_t rx_phy,
        uint16_t phy_options) {
    return BT_STATUS_SUCCESS;
}

bt_status_t BluetoothArcService::gattServerReadPhy(
        const RawAddress& bd_addr,
        base::Callback<void(uint8_t, uint8_t, uint8_t)> cb) {
    cb.Run(1, 1, 0);
    return BT_STATUS_SUCCESS;
}

// ---------------------------------------------------------------------------
// GATT Mojo callbacks → Android callbacks
// ---------------------------------------------------------------------------

void BluetoothArcService::OnLEDeviceFound(
        arc::mojom::BluetoothAddressPtr addr,
        int32_t rssi,
        const std::vector<uint8_t>& eir) {
    if (!gatt_scanner_cbs_ || !gatt_scanner_cbs_->scan_result_cb) return;

    auto raw_addr = std::make_shared<RawAddress>(toRawAddress(addr));
    auto eir_copy = std::make_shared<std::vector<uint8_t>>(eir);

    postToCallback(base::BindOnce(
        [](const btgatt_scanner_callbacks_t* cbs,
           std::shared_ptr<RawAddress> addr,
           int8_t rssi,
           std::shared_ptr<std::vector<uint8_t>> eir_data) {
            // Synthesize legacy ADV_IND event
            cbs->scan_result_cb(
                0x13,           // event_type: legacy ADV_IND
                0,              // addr_type: public
                addr.get(),
                1,              // primary_phy: 1M
                0,              // secondary_phy: none
                0xFF,           // advertising_sid: not available
                127,            // tx_power: unknown
                rssi,
                0,              // periodic_adv_int: none
                *eir_data,
                addr.get());    // 13: original_bda (pre-resolution address)
        }, gatt_scanner_cbs_, raw_addr, static_cast<int8_t>(rssi), eir_copy));
}

void BluetoothArcService::OnLEConnectionStateChange(
        arc::mojom::BluetoothAddressPtr remote_addr,
        bool connected) {
    RawAddress addr = toRawAddress(remote_addr);
    LOG(INFO) << "BluetoothArc: OnLEConnectionStateChange addr="
              << addr.ToString() << " connected=" << connected;

    if (connected) {
        int conn_id;
        int client_if;
        {
            std::lock_guard<std::mutex> lock(gatt_mutex_);
            auto it = addr_to_conn_id_.find(addr);
            if (it != addr_to_conn_id_.end()) {
                conn_id = it->second;
            } else {
                conn_id = next_conn_id_++;
                conn_id_to_addr_[conn_id] = addr;
                addr_to_conn_id_[addr] = conn_id;
            }
            auto cif_it = conn_id_to_client_if_.find(conn_id);
            client_if = (cif_it != conn_id_to_client_if_.end())
                        ? cif_it->second : 1;
        }

        if (gatt_client_cbs_ && gatt_client_cbs_->open_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_client_callbacks_t* cbs, int conn_id,
                   int client_if, RawAddress addr) {
                    cbs->open_cb(conn_id, 0 /* success */, client_if, addr);
                }, gatt_client_cbs_, conn_id, client_if, addr));
        }

        // Also notify server callbacks
        if (gatt_server_cbs_ && gatt_server_cbs_->connection_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_server_callbacks_t* cbs, int conn_id,
                   RawAddress addr) {
                    cbs->connection_cb(conn_id, 1 /* server_if */,
                                      1 /* connected */, addr);
                }, gatt_server_cbs_, conn_id, addr));
        }
    } else {
        int conn_id = 0;
        int client_if = 1;
        {
            std::lock_guard<std::mutex> lock(gatt_mutex_);
            auto it = addr_to_conn_id_.find(addr);
            if (it != addr_to_conn_id_.end()) {
                conn_id = it->second;
                auto cif_it = conn_id_to_client_if_.find(conn_id);
                if (cif_it != conn_id_to_client_if_.end())
                    client_if = cif_it->second;
                conn_id_to_addr_.erase(conn_id);
                conn_id_to_client_if_.erase(conn_id);
                addr_to_conn_id_.erase(it);
            }
            gatt_db_cache_.erase(addr);
        }

        if (conn_id > 0 && gatt_client_cbs_ && gatt_client_cbs_->close_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_client_callbacks_t* cbs, int conn_id,
                   int client_if, RawAddress addr) {
                    cbs->close_cb(conn_id, 0 /* success */, client_if, addr);
                }, gatt_client_cbs_, conn_id, client_if, addr));
        }

        if (conn_id > 0 && gatt_server_cbs_ &&
            gatt_server_cbs_->connection_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_server_callbacks_t* cbs, int conn_id,
                   RawAddress addr) {
                    cbs->connection_cb(conn_id, 1, 0 /* disconnected */, addr);
                }, gatt_server_cbs_, conn_id, addr));
        }
    }
}

void BluetoothArcService::OnLEDeviceAddressChange(
        arc::mojom::BluetoothAddressPtr old_addr,
        arc::mojom::BluetoothAddressPtr new_addr) {
    RawAddress old_raw = toRawAddress(old_addr);
    RawAddress new_raw = toRawAddress(new_addr);
    LOG(INFO) << "BluetoothArc: OnLEDeviceAddressChange "
              << old_raw.ToString() << " -> " << new_raw.ToString();

    std::lock_guard<std::mutex> lock(gatt_mutex_);
    auto it = addr_to_conn_id_.find(old_raw);
    if (it != addr_to_conn_id_.end()) {
        int conn_id = it->second;
        addr_to_conn_id_.erase(it);
        addr_to_conn_id_[new_raw] = conn_id;
        conn_id_to_addr_[conn_id] = new_raw;
    }

    auto db_it = gatt_db_cache_.find(old_raw);
    if (db_it != gatt_db_cache_.end()) {
        gatt_db_cache_[new_raw] = std::move(db_it->second);
        gatt_db_cache_.erase(db_it);
    }
}

void BluetoothArcService::OnSearchComplete(
        arc::mojom::BluetoothAddressPtr remote_addr,
        arc::mojom::BluetoothGattStatus status) {
    RawAddress addr = toRawAddress(remote_addr);
    LOG(INFO) << "BluetoothArc: OnSearchComplete addr=" << addr.ToString()
              << " status=" << (int)status;

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) return;
        conn_id = it->second;
    }

    if (gatt_client_cbs_ && gatt_client_cbs_->search_complete_cb) {
        int st = static_cast<int>(status);
        postToCallback(base::BindOnce(
            [](const btgatt_client_callbacks_t* cbs, int conn, int status) {
                cbs->search_complete_cb(conn, status);
            }, gatt_client_cbs_, conn_id, st));
    }

    // Auto-fetch the GATT DB
    auto mojo_addr2 = toMojoAddress(addr);
    postToMojo(base::BindOnce([](BluetoothArcService* self,
                                 arc::mojom::BluetoothAddressPtr a) {
        if (!self->host_) return;
        self->host_->GetGattDB(std::move(a));
    }, this, std::move(mojo_addr2)));
}

void BluetoothArcService::OnGetGattDB(
        arc::mojom::BluetoothAddressPtr remote_addr,
        std::vector<arc::mojom::BluetoothGattDBElementPtr> db) {
    RawAddress addr = toRawAddress(remote_addr);
    LOG(INFO) << "BluetoothArc: OnGetGattDB addr=" << addr.ToString()
              << " elements=" << db.size();

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) return;
        conn_id = it->second;
    }

    // Build the cache and the Android btgatt_db_element_t array.
    std::vector<GattDbEntry> cache;
    cache.reserve(db.size());

    uint16_t current_service_handle = 0;
    uint16_t current_char_handle = 0;

    for (const auto& elem : db) {
        GattDbEntry entry = {};
        entry.uuid = elem->uuid;  // typemap: BluetoothUUID == bluetooth::Uuid
        entry.type = static_cast<bt_gatt_db_attribute_type_t>(elem->type);
        entry.attribute_handle = elem->attribute_handle;
        entry.start_handle = elem->start_handle;
        entry.end_handle = elem->end_handle;
        entry.properties = elem->properties;

        switch (entry.type) {
            case BTGATT_DB_PRIMARY_SERVICE:
                current_service_handle = entry.attribute_handle;
                current_char_handle = 0;
                entry.is_primary = true;
                entry.parent_service_handle = entry.attribute_handle;
                break;
            case BTGATT_DB_SECONDARY_SERVICE:
                current_service_handle = entry.attribute_handle;
                current_char_handle = 0;
                entry.is_primary = false;
                entry.parent_service_handle = entry.attribute_handle;
                break;
            case BTGATT_DB_INCLUDED_SERVICE:
                entry.parent_service_handle = current_service_handle;
                break;
            case BTGATT_DB_CHARACTERISTIC:
                current_char_handle = entry.attribute_handle;
                entry.parent_service_handle = current_service_handle;
                entry.parent_char_handle = current_char_handle;
                break;
            case BTGATT_DB_DESCRIPTOR:
                entry.parent_service_handle = current_service_handle;
                entry.parent_char_handle = current_char_handle;
                break;
        }

        cache.push_back(entry);
    }

    // Build Android btgatt_db_element_t array.
    auto android_db = std::make_shared<std::vector<btgatt_db_element_t>>();
    android_db->reserve(cache.size());
    for (const auto& entry : cache) {
        btgatt_db_element_t elem = {};
        elem.id = entry.attribute_handle;
        elem.uuid = entry.uuid;
        elem.type = entry.type;
        elem.attribute_handle = entry.attribute_handle;
        elem.start_handle = entry.start_handle;
        elem.end_handle = entry.end_handle;
        elem.properties = entry.properties;
        android_db->push_back(elem);
    }

    // Store in cache
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        gatt_db_cache_[addr] = std::move(cache);
    }

    // Fire callback
    if (gatt_client_cbs_ && gatt_client_cbs_->get_gatt_db_cb) {
        postToCallback(base::BindOnce(
            [](const btgatt_client_callbacks_t* cbs, int conn,
               std::shared_ptr<std::vector<btgatt_db_element_t>> db) {
                cbs->get_gatt_db_cb(conn, db->data(), db->size());
            }, gatt_client_cbs_, conn_id, android_db));
    }
}

void BluetoothArcService::OnGattNotify(
        arc::mojom::BluetoothAddressPtr remote_addr,
        arc::mojom::BluetoothGattServiceIDPtr service_id,
        arc::mojom::BluetoothGattIDPtr char_id,
        bool is_notify,
        const std::vector<uint8_t>& value) {
    if (!gatt_client_cbs_ || !gatt_client_cbs_->notify_cb) return;

    RawAddress addr = toRawAddress(remote_addr);
    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) return;
        conn_id = it->second;
    }

    btgatt_notify_params_t params = {};
    params.bda = addr;
    // Use the char_id's instance_id as the attribute handle
    params.handle = char_id->instance_id;
    params.is_notify = is_notify ? 1 : 0;
    size_t len = std::min(value.size(), (size_t)BTGATT_MAX_ATTR_LEN);
    memcpy(params.value, value.data(), len);
    params.len = len;

    postToCallback(base::BindOnce(
        [](const btgatt_client_callbacks_t* cbs, int conn,
           btgatt_notify_params_t p) {
            cbs->notify_cb(conn, p);
        }, gatt_client_cbs_, conn_id, params));
}

void BluetoothArcService::OnServiceChanged(
        arc::mojom::BluetoothAddressPtr remote_addr) {
    RawAddress addr = toRawAddress(remote_addr);
    LOG(INFO) << "BluetoothArc: OnServiceChanged addr=" << addr.ToString();

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        gatt_db_cache_.erase(addr);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) return;
        conn_id = it->second;
    }

    if (gatt_client_cbs_ && gatt_client_cbs_->services_removed_cb) {
        postToCallback(base::BindOnce(
            [](const btgatt_client_callbacks_t* cbs, int conn) {
                cbs->services_removed_cb(conn, 0x0001, 0xFFFF);
            }, gatt_client_cbs_, conn_id));
    }
}

// ---------------------------------------------------------------------------
// GATT Server Mojo callbacks
// ---------------------------------------------------------------------------

void BluetoothArcService::RequestGattRead(
        arc::mojom::BluetoothAddressPtr address,
        int32_t attribute_handle,
        int32_t offset,
        bool is_long,
        arc::mojom::BluetoothGattDBAttributeType attribute_type,
        const RequestGattReadCallback& callback) {
    RawAddress addr = toRawAddress(address);

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) {
            callback.Run(arc::mojom::BluetoothGattStatus::GATT_FAILURE, {});
            return;
        }
        conn_id = it->second;
    }

    int trans_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        trans_id = next_trans_id_++;
        pending_reads_[trans_id] = callback;
    }

    bool is_char = (attribute_type ==
                    arc::mojom::BluetoothGattDBAttributeType::BTGATT_DB_CHARACTERISTIC);

    if (is_char) {
        if (gatt_server_cbs_ && gatt_server_cbs_->request_read_characteristic_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_server_callbacks_t* cbs, int conn, int tid,
                   RawAddress addr, int handle, int offset, bool is_long) {
                    cbs->request_read_characteristic_cb(
                        conn, tid, addr, handle, offset, is_long);
                }, gatt_server_cbs_, conn_id, trans_id, addr,
                (int)attribute_handle, (int)offset, is_long));
        }
    } else {
        if (gatt_server_cbs_ && gatt_server_cbs_->request_read_descriptor_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_server_callbacks_t* cbs, int conn, int tid,
                   RawAddress addr, int handle, int offset, bool is_long) {
                    cbs->request_read_descriptor_cb(
                        conn, tid, addr, handle, offset, is_long);
                }, gatt_server_cbs_, conn_id, trans_id, addr,
                (int)attribute_handle, (int)offset, is_long));
        }
    }
}

void BluetoothArcService::RequestGattWrite(
        arc::mojom::BluetoothAddressPtr address,
        int32_t attribute_handle,
        int32_t offset,
        const std::vector<uint8_t>& value,
        arc::mojom::BluetoothGattDBAttributeType attribute_type,
        bool is_prepare,
        const RequestGattWriteCallback& callback) {
    RawAddress addr = toRawAddress(address);

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) {
            callback.Run(arc::mojom::BluetoothGattStatus::GATT_FAILURE);
            return;
        }
        conn_id = it->second;
    }

    int trans_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        trans_id = next_trans_id_++;
        pending_writes_[trans_id] = callback;
    }

    bool is_char = (attribute_type ==
                    arc::mojom::BluetoothGattDBAttributeType::BTGATT_DB_CHARACTERISTIC);
    auto val_copy = value;

    if (is_char) {
        if (gatt_server_cbs_ && gatt_server_cbs_->request_write_characteristic_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_server_callbacks_t* cbs, int conn, int tid,
                   RawAddress addr, int handle, int offset, bool need_rsp,
                   bool is_prep, std::vector<uint8_t> val) {
                    // 13: write value is now ptr + length.
                    cbs->request_write_characteristic_cb(
                        conn, tid, addr, handle, offset, need_rsp, is_prep,
                        val.data(), val.size());
                }, gatt_server_cbs_, conn_id, trans_id, addr,
                (int)attribute_handle, (int)offset, true /* need_rsp */,
                is_prepare, std::move(val_copy)));
        }
    } else {
        if (gatt_server_cbs_ && gatt_server_cbs_->request_write_descriptor_cb) {
            postToCallback(base::BindOnce(
                [](const btgatt_server_callbacks_t* cbs, int conn, int tid,
                   RawAddress addr, int handle, int offset, bool need_rsp,
                   bool is_prep, std::vector<uint8_t> val) {
                    // 13: write value is now ptr + length.
                    cbs->request_write_descriptor_cb(
                        conn, tid, addr, handle, offset, need_rsp, is_prep,
                        val.data(), val.size());
                }, gatt_server_cbs_, conn_id, trans_id, addr,
                (int)attribute_handle, (int)offset, true /* need_rsp */,
                is_prepare, std::move(val_copy)));
        }
    }
}

void BluetoothArcService::RequestGattExecuteWrite(
        arc::mojom::BluetoothAddressPtr address,
        bool execute,
        const RequestGattExecuteWriteCallback& callback) {
    RawAddress addr = toRawAddress(address);

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) {
            callback.Run(arc::mojom::BluetoothGattStatus::GATT_FAILURE);
            return;
        }
        conn_id = it->second;
    }

    int trans_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        trans_id = next_trans_id_++;
        pending_exec_writes_[trans_id] = callback;
    }

    if (gatt_server_cbs_ && gatt_server_cbs_->request_exec_write_cb) {
        postToCallback(base::BindOnce(
            [](const btgatt_server_callbacks_t* cbs, int conn, int tid,
               RawAddress addr, int exec) {
                cbs->request_exec_write_cb(conn, tid, addr, exec);
            }, gatt_server_cbs_, conn_id, trans_id, addr,
            execute ? 1 : 0));
    }
}

void BluetoothArcService::OnMTUReceived(
        arc::mojom::BluetoothAddressPtr remote_addr,
        uint16_t mtu) {
    RawAddress addr = toRawAddress(remote_addr);
    LOG(INFO) << "BluetoothArc: OnMTUReceived addr=" << addr.ToString()
              << " mtu=" << mtu;

    int conn_id;
    {
        std::lock_guard<std::mutex> lock(gatt_mutex_);
        auto it = addr_to_conn_id_.find(addr);
        if (it == addr_to_conn_id_.end()) return;
        conn_id = it->second;
    }

    if (gatt_client_cbs_ && gatt_client_cbs_->configure_mtu_cb) {
        postToCallback(base::BindOnce(
            [](const btgatt_client_callbacks_t* cbs, int conn, int mtu) {
                cbs->configure_mtu_cb(conn, 0 /* success */, mtu);
            }, gatt_client_cbs_, conn_id, (int)mtu));
    }

    if (gatt_server_cbs_ && gatt_server_cbs_->mtu_changed_cb) {
        postToCallback(base::BindOnce(
            [](const btgatt_server_callbacks_t* cbs, int conn, int mtu) {
                cbs->mtu_changed_cb(conn, mtu);
            }, gatt_server_cbs_, conn_id, (int)mtu));
    }
}

void BluetoothArcService::OnGetSdpRecords(
        arc::mojom::BluetoothStatus status,
        arc::mojom::BluetoothAddressPtr remote_addr,
        const bluetooth::Uuid& target_uuid,
        std::vector<arc::mojom::BluetoothSdpRecordPtr> records) {
    // Phase 4: SDP
}

}  // namespace arc

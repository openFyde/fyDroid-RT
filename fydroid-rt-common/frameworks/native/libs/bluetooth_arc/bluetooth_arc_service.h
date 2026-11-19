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

// BluetoothArcService — Mojo bridge between Android bt_interface_t and Chrome OS.
//
// Inherits arc::ArcService for ARC bridge bootstrap, and arc::mojom::BluetoothInstance
// for receiving callbacks from Chrome OS. Provides BluetoothHost proxy for making
// calls to Chrome OS Bluetooth.
#pragma once

#include <atomic>
#include <map>
#include <memory>
#include <mutex>
#include <condition_variable>
#include <thread>
#include <vector>

#include <hardware/bluetooth.h>
#include <hardware/bt_gatt.h>
#include <hardware/bt_gatt_client.h>
#include <hardware/bt_gatt_server.h>
#include <hardware/ble_scanner.h>
#include <utils/StrongPointer.h>
#include <mojo/public/cpp/bindings/binding.h>

#include <arc/ArcService.h>
#include <arc/MojoProcessSupport.h>
#include <arc/bluetooth.mojom.h>
#include <arc/arc_bridge.mojom.h>

namespace arc {

class BluetoothArcService
        : public arc::ArcService,
          public arc::mojom::BluetoothInstance {
public:
    static BluetoothArcService* GetInstance();

    // Called from bt_interface_t init() to store JNI callbacks and start Mojo.
    int initialize(bt_callbacks_t* callbacks);
    void cleanup();

    // --- bt_interface_t operations (called from JNI thread) ---
    int enable();
    int disable();
    int getAdapterProperties();
    int getAdapterProperty(bt_property_type_t type);
    int setAdapterProperty(const bt_property_t* property);
    int getRemoteDeviceProperties(RawAddress* addr);
    int getRemoteDeviceProperty(RawAddress* addr, bt_property_type_t type);
    int startDiscovery();
    int cancelDiscovery();
    int createBond(const RawAddress* addr, int transport);
    int removeBond(const RawAddress* addr);
    int cancelBond(const RawAddress* addr);
    int getConnectionState(const RawAddress* addr);
    int pinReply(const RawAddress* addr, uint8_t accept, uint8_t pin_len,
                 bt_pin_code_t* pin_code);
    int sspReply(const RawAddress* addr, bt_ssp_variant_t variant,
                 uint8_t accept, uint32_t passkey);
    const void* getProfileInterface(const char* profile_id);

    // --- GATT interface ---
    bt_status_t initGatt(const btgatt_callbacks_t* callbacks);
    void cleanupGatt();

    // GATT Client operations
    bt_status_t gattRegisterClient(const bluetooth::Uuid& uuid);
    bt_status_t gattUnregisterClient(int client_if);
    bt_status_t gattConnect(int client_if, const RawAddress& bd_addr,
                            bool is_direct, int transport, bool opportunistic,
                            int initiating_phys);
    bt_status_t gattDisconnect(int client_if, const RawAddress& bd_addr,
                               int conn_id);
    bt_status_t gattRefresh(int client_if, const RawAddress& bd_addr);
    bt_status_t gattSearchService(int conn_id,
                                  const bluetooth::Uuid* filter_uuid);
    bt_status_t gattReadCharacteristic(int conn_id, uint16_t handle,
                                       int auth_req);
    bt_status_t gattReadUsingCharUuid(int conn_id, const bluetooth::Uuid& uuid,
                                      uint16_t s_handle, uint16_t e_handle,
                                      int auth_req);
    bt_status_t gattWriteCharacteristic(int conn_id, uint16_t handle,
                                        int write_type, int auth_req,
                                        std::vector<uint8_t> value);
    bt_status_t gattReadDescriptor(int conn_id, uint16_t handle, int auth_req);
    bt_status_t gattWriteDescriptor(int conn_id, uint16_t handle, int auth_req,
                                    std::vector<uint8_t> value);
    bt_status_t gattExecuteWrite(int conn_id, int execute);
    bt_status_t gattRegisterForNotification(int client_if,
                                            const RawAddress& bd_addr,
                                            uint16_t handle);
    bt_status_t gattDeregisterForNotification(int client_if,
                                              const RawAddress& bd_addr,
                                              uint16_t handle);
    bt_status_t gattReadRemoteRssi(int client_if, const RawAddress& bd_addr);
    int gattGetDeviceType(const RawAddress& bd_addr);
    bt_status_t gattConfigureMtu(int conn_id, int mtu);
    bt_status_t gattConnParameterUpdate(const RawAddress& bd_addr,
                                        int min_interval, int max_interval,
                                        int latency, int timeout,
                                        uint16_t min_ce_len,
                                        uint16_t max_ce_len);
    bt_status_t gattSetPreferredPhy(const RawAddress& bd_addr, uint8_t tx_phy,
                                    uint8_t rx_phy, uint16_t phy_options);
    bt_status_t gattReadPhy(
            const RawAddress& bd_addr,
            base::Callback<void(uint8_t, uint8_t, uint8_t)> cb);
    bt_status_t gattTestCommand(int command, const btgatt_test_params_t& params);
    bt_status_t gattGetGattDb(int conn_id);

    // GATT Server operations
    bt_status_t gattRegisterServer(const bluetooth::Uuid& uuid);
    bt_status_t gattUnregisterServer(int server_if);
    bt_status_t gattServerConnect(int server_if, const RawAddress& bd_addr,
                                  bool is_direct, int transport);
    bt_status_t gattServerDisconnect(int server_if, const RawAddress& bd_addr,
                                     int conn_id);
    bt_status_t gattAddService(int server_if,
                               std::vector<btgatt_db_element_t> service);
    bt_status_t gattStopService(int server_if, int service_handle);
    bt_status_t gattDeleteService(int server_if, int service_handle);
    bt_status_t gattSendIndication(int server_if, int attribute_handle,
                                   int conn_id, int confirm,
                                   std::vector<uint8_t> value);
    bt_status_t gattSendResponse(int conn_id, int trans_id, int status,
                                 const btgatt_response_t& response);
    bt_status_t gattServerSetPreferredPhy(const RawAddress& bd_addr,
                                          uint8_t tx_phy, uint8_t rx_phy,
                                          uint16_t phy_options);
    bt_status_t gattServerReadPhy(
            const RawAddress& bd_addr,
            base::Callback<void(uint8_t, uint8_t, uint8_t)> cb);

    // BLE Scanner operations
    void startLEScan(bool start);

    // Discover service by UUID (PTS only)
    void gattDiscoverServiceByUuid(int conn_id, const bluetooth::Uuid& uuid);

    // --- arc::ArcService overrides ---
    void ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) override;
    void versionMismatch(unsigned int version) override;

    // --- arc::mojom::BluetoothInstance overrides ---
    void Init(mojo::InterfacePtr<arc::mojom::BluetoothHost> host,
              const InitCallback& callback) override;
    void OnAdapterProperties(
            arc::mojom::BluetoothStatus status,
            std::vector<arc::mojom::BluetoothPropertyPtr> properties) override;
    void OnDeviceFound(
            std::vector<arc::mojom::BluetoothPropertyPtr> properties) override;
    void OnDevicePropertiesChanged(
            arc::mojom::BluetoothAddressPtr remote_addr,
            std::vector<arc::mojom::BluetoothPropertyPtr> properties) override;
    void OnDiscoveryStateChanged(
            arc::mojom::BluetoothDiscoveryState state) override;
    void OnBondStateChanged(
            arc::mojom::BluetoothStatus status,
            arc::mojom::BluetoothAddressPtr remote_addr,
            arc::mojom::BluetoothBondState state) override;
    void OnConnectionStateChanged(
            arc::mojom::BluetoothAddressPtr remote_addr,
            bt_device_type_t device_type,
            bool connected) override;

    // GATT client callbacks
    void OnLEDeviceFound(
            arc::mojom::BluetoothAddressPtr addr,
            int32_t rssi,
            const std::vector<uint8_t>& eir) override;
    void OnLEConnectionStateChange(
            arc::mojom::BluetoothAddressPtr remote_addr,
            bool connected) override;
    void OnLEDeviceAddressChange(
            arc::mojom::BluetoothAddressPtr old_addr,
            arc::mojom::BluetoothAddressPtr new_addr) override;
    void OnSearchComplete(
            arc::mojom::BluetoothAddressPtr remote_addr,
            arc::mojom::BluetoothGattStatus status) override;
    void OnGetGattDB(
            arc::mojom::BluetoothAddressPtr remote_addr,
            std::vector<arc::mojom::BluetoothGattDBElementPtr> db) override;
    void OnGattNotify(
            arc::mojom::BluetoothAddressPtr remote_addr,
            arc::mojom::BluetoothGattServiceIDPtr service_id,
            arc::mojom::BluetoothGattIDPtr char_id,
            bool is_notify,
            const std::vector<uint8_t>& value) override;
    void OnServiceChanged(
            arc::mojom::BluetoothAddressPtr remote_addr) override;

    // GATT server callbacks
    void RequestGattRead(
            arc::mojom::BluetoothAddressPtr address,
            int32_t attribute_handle,
            int32_t offset,
            bool is_long,
            arc::mojom::BluetoothGattDBAttributeType attribute_type,
            const RequestGattReadCallback& callback) override;
    void RequestGattWrite(
            arc::mojom::BluetoothAddressPtr address,
            int32_t attribute_handle,
            int32_t offset,
            const std::vector<uint8_t>& value,
            arc::mojom::BluetoothGattDBAttributeType attribute_type,
            bool is_prepare,
            const RequestGattWriteCallback& callback) override;
    void RequestGattExecuteWrite(
            arc::mojom::BluetoothAddressPtr address,
            bool execute,
            const RequestGattExecuteWriteCallback& callback) override;
    void OnMTUReceived(
            arc::mojom::BluetoothAddressPtr remote_addr,
            uint16_t mtu) override;

    // SDP callback
    void OnGetSdpRecords(
            arc::mojom::BluetoothStatus status,
            arc::mojom::BluetoothAddressPtr remote_addr,
            const bluetooth::Uuid& target_uuid,
            std::vector<arc::mojom::BluetoothSdpRecordPtr> records) override;

private:
    BluetoothArcService();
    ~BluetoothArcService() override;

    // Post a task to the Mojo thread.
    void postToMojo(base::OnceClosure task);

    // Fire a callback on the callback thread.
    void postToCallback(base::OnceClosure task);

    // Convert Mojo BluetoothAddress to RawAddress.
    static RawAddress toRawAddress(const arc::mojom::BluetoothAddressPtr& addr);
    static arc::mojom::BluetoothAddressPtr toMojoAddress(const RawAddress& addr);

    // Convert Mojo properties to bt_property_t array.
    // Returns heap-allocated properties; caller must free with freeProperties().
    static void convertProperties(
            const std::vector<arc::mojom::BluetoothPropertyPtr>& mojo_props,
            std::vector<bt_property_t>* out_props,
            std::vector<std::vector<uint8_t>>* out_storage);

    // GATT DB cache entry — annotated with parent handles for Mojo ID lookup.
    struct GattDbEntry {
        bluetooth::Uuid uuid;
        bt_gatt_db_attribute_type_t type;
        uint16_t attribute_handle;
        uint16_t start_handle;
        uint16_t end_handle;
        uint8_t properties;
        uint16_t parent_service_handle;
        uint16_t parent_char_handle;
        bool is_primary;
    };

    // Look up a GATT DB entry by address and attribute handle.
    const GattDbEntry* lookupGattEntry(const RawAddress& addr, uint16_t handle);

    // Build Mojo GATT ID types from cached DB entries.
    arc::mojom::BluetoothGattServiceIDPtr buildServiceId(const GattDbEntry& entry);
    arc::mojom::BluetoothGattIDPtr buildGattId(const GattDbEntry& entry);

    // Get conn_id for an address, creating one if it doesn't exist.
    int getOrCreateConnId(const RawAddress& addr, int client_if);

    arc::MojoProcessSupport* mojo_support_ = nullptr;
    arc::ArcBridgeSupport* bridge_support_ = nullptr;

    // Mojo host proxy (Chrome OS side).
    mojo::InterfacePtr<arc::mojom::BluetoothHost> host_;
    // Mojo binding for receiving callbacks from Chrome OS.
    mojo::Binding<arc::mojom::BluetoothInstance> binding_{this};

    // Guards host_ready_.
    std::mutex host_mutex_;
    std::condition_variable host_cv_;
    bool host_ready_ = false;

    // JNI callbacks.
    bt_callbacks_t* callbacks_ = nullptr;

    // Callback thread — the JNI layer expects callbacks on a dedicated thread
    // that's been attached to the JVM.
    std::unique_ptr<std::thread> callback_thread_;
    scoped_refptr<base::SingleThreadTaskRunner> callback_runner_;
    std::unique_ptr<base::Thread> callback_base_thread_;

    // --- GATT state ---
    const btgatt_client_callbacks_t* gatt_client_cbs_ = nullptr;
    const btgatt_server_callbacks_t* gatt_server_cbs_ = nullptr;
    const btgatt_scanner_callbacks_t* gatt_scanner_cbs_ = nullptr;

    // client_if / server_if tracking (local-only, auto-incrementing)
    std::mutex gatt_mutex_;
    int next_client_if_ = 1;
    int next_server_if_ = 1;
    std::map<int, bluetooth::Uuid> client_if_to_uuid_;
    std::map<int, bluetooth::Uuid> server_if_to_uuid_;

    // conn_id ↔ address mapping
    int next_conn_id_ = 1;
    std::map<int, RawAddress> conn_id_to_addr_;
    std::map<RawAddress, int> addr_to_conn_id_;
    std::map<int, int> conn_id_to_client_if_;

    // Per-device GATT DB cache (populated by OnGetGattDB)
    std::map<RawAddress, std::vector<GattDbEntry>> gatt_db_cache_;

    // GATT server: trans_id for request/response pairing
    int next_trans_id_ = 1;
    std::map<int, RequestGattReadCallback> pending_reads_;
    std::map<int, RequestGattWriteCallback> pending_writes_;
    std::map<int, RequestGattExecuteWriteCallback> pending_exec_writes_;
};

}  // namespace arc

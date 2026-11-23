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

// bt_gatt_arc.cpp — GATT interface for the ARC Bluetooth Mojo bridge.
//
// Exports getGattInterface() returning a btgatt_interface_t* with client,
// server, scanner, and advertiser interfaces. All calls delegate to
// BluetoothArcService::GetInstance().

#include <hardware/bt_gatt.h>
#include <hardware/bt_gatt_client.h>
#include <hardware/bt_gatt_server.h>
#include <hardware/ble_scanner.h>
#include <hardware/ble_advertiser.h>
#include <base/logging.h>

#include "bluetooth_arc_service.h"

using arc::BluetoothArcService;

// ---------------------------------------------------------------------------
// BLE Scanner — minimal implementation
// ---------------------------------------------------------------------------

class BleScannerArc : public BleScannerInterface {
public:
    void RegisterScanner(const bluetooth::Uuid& app_uuid,
                         RegisterCallback cb) override {
        uint8_t scanner_id = next_scanner_id_++;
        LOG(INFO) << "BluetoothArc: RegisterScanner id=" << (int)scanner_id;
        cb.Run(scanner_id, 0 /* success */);
    }

    void Unregister(int scanner_id) override {
        LOG(INFO) << "BluetoothArc: Unregister scanner id=" << scanner_id;
    }

    void Scan(bool start) override {
        LOG(INFO) << "BluetoothArc: Scan start=" << start;
        BluetoothArcService::GetInstance()->startLEScan(start);
    }

    void ScanFilterParamSetup(
            uint8_t client_if, uint8_t action, uint8_t filt_index,
            std::unique_ptr<btgatt_filt_param_setup_t> filt_param,
            FilterParamSetupCallback cb) override {
        cb.Run(0 /* avbl_space */, action, 0 /* success */);
    }

    void ScanFilterAdd(int filter_index, std::vector<ApcfCommand> filters,
                       FilterConfigCallback cb) override {
        cb.Run(0 /* filt_type */, 0 /* avbl_space */, 0 /* action */,
               0 /* success */);
    }

    void ScanFilterClear(int filt_index, FilterConfigCallback cb) override {
        cb.Run(0, 0, 0, 0);
    }

    void ScanFilterEnable(bool enable, EnableCallback cb) override {
        cb.Run(enable ? 1 : 0, 0 /* success */);
    }

    void SetScanParameters(int scanner_id, int scan_interval, int scan_window,
                           Callback cb) override {
        cb.Run(0 /* success */);
    }

    void BatchscanConfigStorage(int client_if, int batch_scan_full_max,
                                int batch_scan_trunc_max,
                                int batch_scan_notify_threshold,
                                Callback cb) override {
        cb.Run(0);
    }

    void BatchscanEnable(int scan_mode, int scan_interval,
                         int scan_window, int addr_type, int discard_rule,
                         Callback cb) override {
        cb.Run(0);
    }

    void BatchscanDisable(Callback cb) override {
        cb.Run(0);
    }

    void BatchscanReadReports(int client_if, int scan_mode) override {}

    // Android 13 replaced the per-call StartSync callbacks (StartSyncCb/
    // SyncReportCb/SyncLostCb) with a reg_id keyed into the global
    // ScanningCallbacks registered via RegisterCallbacks().
    void StartSync(uint8_t sid, RawAddress address, uint16_t skip,
                   uint16_t timeout, int reg_id) override {}

    void StopSync(uint16_t handle) override {}

    // New BleScannerInterface pure-virtuals in 13 — periodic-advertising sync
    // transfer (PAST) + the global scanning-callbacks registration. ARC drives
    // scanning through Chrome OS, so these are no-op stubs.
    void RegisterCallbacks(ScanningCallbacks* callbacks) override {}

    void CancelCreateSync(uint8_t sid, RawAddress address) override {}

    void TransferSync(RawAddress address, uint16_t service_data,
                      uint16_t sync_handle, int pa_source) override {}

    void TransferSetInfo(RawAddress address, uint16_t service_data,
                         uint8_t adv_handle, int pa_source) override {}

    void SyncTxParameters(RawAddress addr, uint8_t mode, uint16_t skip,
                          uint16_t timeout, int reg_id) override {}

private:
    uint8_t next_scanner_id_ = 1;
};

static BleScannerArc sBleScannerArc;

// ---------------------------------------------------------------------------
// GATT Client interface
// ---------------------------------------------------------------------------

static bt_status_t arc_gatt_client_register_client(
        const bluetooth::Uuid& uuid, bool eatt_support) {
    return BluetoothArcService::GetInstance()->gattRegisterClient(uuid);
}

static bt_status_t arc_gatt_client_unregister_client(int client_if) {
    return BluetoothArcService::GetInstance()->gattUnregisterClient(client_if);
}

static bt_status_t arc_gatt_client_connect(
        int client_if, const RawAddress& bd_addr, bool is_direct,
        int transport, bool opportunistic, int initiating_phys) {
    return BluetoothArcService::GetInstance()->gattConnect(
            client_if, bd_addr, is_direct, transport, opportunistic,
            initiating_phys);
}

static bt_status_t arc_gatt_client_disconnect(
        int client_if, const RawAddress& bd_addr, int conn_id) {
    return BluetoothArcService::GetInstance()->gattDisconnect(
            client_if, bd_addr, conn_id);
}

static bt_status_t arc_gatt_client_refresh(
        int client_if, const RawAddress& bd_addr) {
    return BluetoothArcService::GetInstance()->gattRefresh(client_if, bd_addr);
}

static bt_status_t arc_gatt_client_search_service(
        int conn_id, const bluetooth::Uuid* filter_uuid) {
    return BluetoothArcService::GetInstance()->gattSearchService(
            conn_id, filter_uuid);
}

static void arc_gatt_client_discover_service_by_uuid(
        int conn_id, const bluetooth::Uuid& uuid) {
    BluetoothArcService::GetInstance()->gattDiscoverServiceByUuid(conn_id, uuid);
}

static bt_status_t arc_gatt_client_read_characteristic(
        int conn_id, uint16_t handle, int auth_req) {
    return BluetoothArcService::GetInstance()->gattReadCharacteristic(
            conn_id, handle, auth_req);
}

static bt_status_t arc_gatt_client_read_using_characteristic_uuid(
        int conn_id, const bluetooth::Uuid& uuid,
        uint16_t s_handle, uint16_t e_handle, int auth_req) {
    return BluetoothArcService::GetInstance()->gattReadUsingCharUuid(
            conn_id, uuid, s_handle, e_handle, auth_req);
}

static bt_status_t arc_gatt_client_write_characteristic(
        int conn_id, uint16_t handle, int write_type, int auth_req,
        const uint8_t* value, size_t length) {
    return BluetoothArcService::GetInstance()->gattWriteCharacteristic(
            conn_id, handle, write_type, auth_req,
            std::vector<uint8_t>(value, value + length));
}

static bt_status_t arc_gatt_client_read_descriptor(
        int conn_id, uint16_t handle, int auth_req) {
    return BluetoothArcService::GetInstance()->gattReadDescriptor(
            conn_id, handle, auth_req);
}

static bt_status_t arc_gatt_client_write_descriptor(
        int conn_id, uint16_t handle, int auth_req,
        const uint8_t* value, size_t length) {
    return BluetoothArcService::GetInstance()->gattWriteDescriptor(
            conn_id, handle, auth_req,
            std::vector<uint8_t>(value, value + length));
}

static bt_status_t arc_gatt_client_execute_write(int conn_id, int execute) {
    return BluetoothArcService::GetInstance()->gattExecuteWrite(
            conn_id, execute);
}

static bt_status_t arc_gatt_client_register_for_notification(
        int client_if, const RawAddress& bd_addr, uint16_t handle) {
    return BluetoothArcService::GetInstance()->gattRegisterForNotification(
            client_if, bd_addr, handle);
}

static bt_status_t arc_gatt_client_deregister_for_notification(
        int client_if, const RawAddress& bd_addr, uint16_t handle) {
    return BluetoothArcService::GetInstance()->gattDeregisterForNotification(
            client_if, bd_addr, handle);
}

static bt_status_t arc_gatt_client_read_remote_rssi(
        int client_if, const RawAddress& bd_addr) {
    return BluetoothArcService::GetInstance()->gattReadRemoteRssi(
            client_if, bd_addr);
}

static int arc_gatt_client_get_device_type(const RawAddress& bd_addr) {
    return BluetoothArcService::GetInstance()->gattGetDeviceType(bd_addr);
}

static bt_status_t arc_gatt_client_configure_mtu(int conn_id, int mtu) {
    return BluetoothArcService::GetInstance()->gattConfigureMtu(conn_id, mtu);
}

static bt_status_t arc_gatt_client_conn_parameter_update(
        const RawAddress& bd_addr, int min_interval, int max_interval,
        int latency, int timeout, uint16_t min_ce_len, uint16_t max_ce_len) {
    return BluetoothArcService::GetInstance()->gattConnParameterUpdate(
            bd_addr, min_interval, max_interval, latency, timeout,
            min_ce_len, max_ce_len);
}

static bt_status_t arc_gatt_client_set_preferred_phy(
        const RawAddress& bd_addr, uint8_t tx_phy, uint8_t rx_phy,
        uint16_t phy_options) {
    return BluetoothArcService::GetInstance()->gattSetPreferredPhy(
            bd_addr, tx_phy, rx_phy, phy_options);
}

static bt_status_t arc_gatt_client_read_phy(
        const RawAddress& bd_addr,
        base::Callback<void(uint8_t, uint8_t, uint8_t)> cb) {
    return BluetoothArcService::GetInstance()->gattReadPhy(bd_addr, cb);
}

static bt_status_t arc_gatt_client_test_command(
        int command, const btgatt_test_params_t& params) {
    return BluetoothArcService::GetInstance()->gattTestCommand(command, params);
}

static bt_status_t arc_gatt_client_get_gatt_db(int conn_id) {
    return BluetoothArcService::GetInstance()->gattGetGattDb(conn_id);
}

static const btgatt_client_interface_t sGattClientInterface = {
    .register_client = arc_gatt_client_register_client,
    .unregister_client = arc_gatt_client_unregister_client,
    .connect = arc_gatt_client_connect,
    .disconnect = arc_gatt_client_disconnect,
    .refresh = arc_gatt_client_refresh,
    .search_service = arc_gatt_client_search_service,
    .btif_gattc_discover_service_by_uuid =
            arc_gatt_client_discover_service_by_uuid,
    .read_characteristic = arc_gatt_client_read_characteristic,
    .read_using_characteristic_uuid =
            arc_gatt_client_read_using_characteristic_uuid,
    .write_characteristic = arc_gatt_client_write_characteristic,
    .read_descriptor = arc_gatt_client_read_descriptor,
    .write_descriptor = arc_gatt_client_write_descriptor,
    .execute_write = arc_gatt_client_execute_write,
    .register_for_notification = arc_gatt_client_register_for_notification,
    .deregister_for_notification = arc_gatt_client_deregister_for_notification,
    .read_remote_rssi = arc_gatt_client_read_remote_rssi,
    .get_device_type = arc_gatt_client_get_device_type,
    .configure_mtu = arc_gatt_client_configure_mtu,
    .conn_parameter_update = arc_gatt_client_conn_parameter_update,
    .set_preferred_phy = arc_gatt_client_set_preferred_phy,
    .read_phy = arc_gatt_client_read_phy,
    .test_command = arc_gatt_client_test_command,
    .get_gatt_db = arc_gatt_client_get_gatt_db,
};

// ---------------------------------------------------------------------------
// GATT Server interface
// ---------------------------------------------------------------------------

static bt_status_t arc_gatt_server_register_server(
        const bluetooth::Uuid& uuid, bool eatt_support) {
    return BluetoothArcService::GetInstance()->gattRegisterServer(uuid);
}

static bt_status_t arc_gatt_server_unregister_server(int server_if) {
    return BluetoothArcService::GetInstance()->gattUnregisterServer(server_if);
}

static bt_status_t arc_gatt_server_connect(
        int server_if, const RawAddress& bd_addr, bool is_direct,
        int transport) {
    return BluetoothArcService::GetInstance()->gattServerConnect(
            server_if, bd_addr, is_direct, transport);
}

static bt_status_t arc_gatt_server_disconnect(
        int server_if, const RawAddress& bd_addr, int conn_id) {
    return BluetoothArcService::GetInstance()->gattServerDisconnect(
            server_if, bd_addr, conn_id);
}

static bt_status_t arc_gatt_server_add_service(
        int server_if, const btgatt_db_element_t* service,
        size_t service_count) {
    return BluetoothArcService::GetInstance()->gattAddService(
            server_if,
            std::vector<btgatt_db_element_t>(service, service + service_count));
}

static bt_status_t arc_gatt_server_stop_service(
        int server_if, int service_handle) {
    return BluetoothArcService::GetInstance()->gattStopService(
            server_if, service_handle);
}

static bt_status_t arc_gatt_server_delete_service(
        int server_if, int service_handle) {
    return BluetoothArcService::GetInstance()->gattDeleteService(
            server_if, service_handle);
}

static bt_status_t arc_gatt_server_send_indication(
        int server_if, int attribute_handle, int conn_id, int confirm,
        const uint8_t* value, size_t length) {
    return BluetoothArcService::GetInstance()->gattSendIndication(
            server_if, attribute_handle, conn_id, confirm,
            std::vector<uint8_t>(value, value + length));
}

static bt_status_t arc_gatt_server_send_response(
        int conn_id, int trans_id, int status,
        const btgatt_response_t& response) {
    return BluetoothArcService::GetInstance()->gattSendResponse(
            conn_id, trans_id, status, response);
}

static bt_status_t arc_gatt_server_set_preferred_phy(
        const RawAddress& bd_addr, uint8_t tx_phy, uint8_t rx_phy,
        uint16_t phy_options) {
    return BluetoothArcService::GetInstance()->gattServerSetPreferredPhy(
            bd_addr, tx_phy, rx_phy, phy_options);
}

static bt_status_t arc_gatt_server_read_phy(
        const RawAddress& bd_addr,
        base::Callback<void(uint8_t, uint8_t, uint8_t)> cb) {
    return BluetoothArcService::GetInstance()->gattServerReadPhy(
            bd_addr, std::move(cb));
}

static const btgatt_server_interface_t sGattServerInterface = {
    .register_server = arc_gatt_server_register_server,
    .unregister_server = arc_gatt_server_unregister_server,
    .connect = arc_gatt_server_connect,
    .disconnect = arc_gatt_server_disconnect,
    .add_service = arc_gatt_server_add_service,
    .stop_service = arc_gatt_server_stop_service,
    .delete_service = arc_gatt_server_delete_service,
    .send_indication = arc_gatt_server_send_indication,
    .send_response = arc_gatt_server_send_response,
    .set_preferred_phy = arc_gatt_server_set_preferred_phy,
    .read_phy = arc_gatt_server_read_phy,
};

// ---------------------------------------------------------------------------
// BLE Advertiser — no-op stub
//
// ARC drives advertising through Chrome OS, so this is not implemented yet, but
// the interface MUST be non-null: the AOSP-13 GATT JNI initializeNative()
// unconditionally calls sGattIf->advertiser->RegisterCallbacks(), so a null
// advertiser SIGSEGVs com.android.bluetooth during GattService.start(). The
// methods other than RegisterCallbacks() are only reached when an app advertises
// (not during adapter bring-up); they no-op until ARC advertising is wired up.
// ---------------------------------------------------------------------------

class BleAdvertiserArc : public BleAdvertiserInterface {
public:
    void RegisterAdvertiser(IdStatusCallback /*cb*/) override {}
    void GetOwnAddress(uint8_t /*advertiser_id*/, GetAddressCallback /*cb*/) override {}
    void SetParameters(uint8_t /*advertiser_id*/, AdvertiseParameters /*params*/,
                       ParametersCallback /*cb*/) override {}
    void SetData(int /*advertiser_id*/, bool /*set_scan_rsp*/,
                 std::vector<uint8_t> /*data*/, StatusCallback /*cb*/) override {}
    void Enable(uint8_t /*advertiser_id*/, bool /*enable*/, StatusCallback /*cb*/,
                uint16_t /*duration*/, uint8_t /*maxExtAdvEvents*/,
                StatusCallback /*timeout_cb*/) override {}
    void Unregister(uint8_t /*advertiser_id*/) override {}
    void StartAdvertising(uint8_t /*advertiser_id*/, StatusCallback /*cb*/,
                          AdvertiseParameters /*params*/,
                          std::vector<uint8_t> /*advertise_data*/,
                          std::vector<uint8_t> /*scan_response_data*/,
                          int /*timeout_s*/, StatusCallback /*timeout_cb*/) override {}
    void StartAdvertisingSet(int /*reg_id*/, IdTxPowerStatusCallback /*register_cb*/,
                             AdvertiseParameters /*params*/,
                             std::vector<uint8_t> /*advertise_data*/,
                             std::vector<uint8_t> /*scan_response_data*/,
                             PeriodicAdvertisingParameters /*periodic_params*/,
                             std::vector<uint8_t> /*periodic_data*/,
                             uint16_t /*duration*/, uint8_t /*maxExtAdvEvents*/,
                             IdStatusCallback /*timeout_cb*/) override {}
    void SetPeriodicAdvertisingParameters(int /*advertiser_id*/,
                                          PeriodicAdvertisingParameters /*parameters*/,
                                          StatusCallback /*cb*/) override {}
    void SetPeriodicAdvertisingData(int /*advertiser_id*/, std::vector<uint8_t> /*data*/,
                                    StatusCallback /*cb*/) override {}
    void SetPeriodicAdvertisingEnable(int /*advertiser_id*/, bool /*enable*/,
                                      StatusCallback /*cb*/) override {}
    void RegisterCallbacks(AdvertisingCallbacks* /*callbacks*/) override {}
};

static BleAdvertiserArc sBleAdvertiserArc;

// ---------------------------------------------------------------------------
// GATT top-level interface
// ---------------------------------------------------------------------------

static bt_status_t arc_gatt_init(const btgatt_callbacks_t* callbacks) {
    return BluetoothArcService::GetInstance()->initGatt(callbacks);
}

static void arc_gatt_cleanup() {
    BluetoothArcService::GetInstance()->cleanupGatt();
}

static btgatt_interface_t sGattInterface = {
    .size = sizeof(btgatt_interface_t),
    .init = arc_gatt_init,
    .cleanup = arc_gatt_cleanup,
    .client = &sGattClientInterface,
    .server = &sGattServerInterface,
    .scanner = &sBleScannerArc,
    .advertiser = &sBleAdvertiserArc,
};

const btgatt_interface_t* getGattInterface() {
    return &sGattInterface;
}

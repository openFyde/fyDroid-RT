// bt_interface_arc.cpp — Exports the `bluetoothInterface` symbol.
//
// libbluetooth_jni.so does dlopen("libbluetooth.so") + dlsym("bluetoothInterface")
// to get a bt_interface_t*. This file provides that symbol, backed by
// BluetoothArcService (Mojo bridge to Chrome OS) instead of the Fluoride stack.

#include <hardware/bluetooth.h>
#include <base/logging.h>

#include "bluetooth_arc_service.h"

// ---------------------------------------------------------------------------
// bt_interface_t function implementations
// ---------------------------------------------------------------------------

static int arc_init(bt_callbacks_t* callbacks, bool guest_mode,
                    bool is_niap_mode, int config_compare_result,
                    const char** init_flags, bool is_atv,
                    const char* user_data_directory) {
    return arc::BluetoothArcService::GetInstance()->initialize(callbacks);
}

static int arc_enable() {
    return arc::BluetoothArcService::GetInstance()->enable();
}

static int arc_disable() {
    return arc::BluetoothArcService::GetInstance()->disable();
}

static void arc_cleanup() {
    arc::BluetoothArcService::GetInstance()->cleanup();
}

static int arc_get_adapter_properties() {
    return arc::BluetoothArcService::GetInstance()->getAdapterProperties();
}

static int arc_get_adapter_property(bt_property_type_t type) {
    return arc::BluetoothArcService::GetInstance()->getAdapterProperty(type);
}

static int arc_set_adapter_property(const bt_property_t* property) {
    return arc::BluetoothArcService::GetInstance()->setAdapterProperty(property);
}

static int arc_get_remote_device_properties(RawAddress* remote_addr) {
    return arc::BluetoothArcService::GetInstance()->getRemoteDeviceProperties(remote_addr);
}

static int arc_get_remote_device_property(RawAddress* remote_addr,
                                          bt_property_type_t type) {
    return arc::BluetoothArcService::GetInstance()->getRemoteDeviceProperty(remote_addr, type);
}

static int arc_set_remote_device_property(RawAddress* remote_addr,
                                          const bt_property_t* property) {
    return BT_STATUS_UNSUPPORTED;
}

static int arc_get_remote_service_record(const RawAddress& remote_addr,
                                         const bluetooth::Uuid& uuid) {
    return BT_STATUS_UNSUPPORTED;
}

static int arc_get_remote_services(RawAddress* remote_addr, int transport) {
    return BT_STATUS_UNSUPPORTED;
}

static int arc_start_discovery() {
    return arc::BluetoothArcService::GetInstance()->startDiscovery();
}

static int arc_cancel_discovery() {
    return arc::BluetoothArcService::GetInstance()->cancelDiscovery();
}

static int arc_create_bond(const RawAddress* bd_addr, int transport) {
    return arc::BluetoothArcService::GetInstance()->createBond(bd_addr, transport);
}

static int arc_create_bond_out_of_band(const RawAddress* bd_addr, int transport,
                                       const bt_oob_data_t* p192_data,
                                       const bt_oob_data_t* p256_data) {
    return BT_STATUS_UNSUPPORTED;
}

static int arc_remove_bond(const RawAddress* bd_addr) {
    return arc::BluetoothArcService::GetInstance()->removeBond(bd_addr);
}

static int arc_cancel_bond(const RawAddress* bd_addr) {
    return arc::BluetoothArcService::GetInstance()->cancelBond(bd_addr);
}

static int arc_get_connection_state(const RawAddress* bd_addr) {
    return arc::BluetoothArcService::GetInstance()->getConnectionState(bd_addr);
}

static int arc_pin_reply(const RawAddress* bd_addr, uint8_t accept,
                         uint8_t pin_len, bt_pin_code_t* pin_code) {
    return arc::BluetoothArcService::GetInstance()->pinReply(
            bd_addr, accept, pin_len, pin_code);
}

static int arc_ssp_reply(const RawAddress* bd_addr, bt_ssp_variant_t variant,
                         uint8_t accept, uint32_t passkey) {
    return arc::BluetoothArcService::GetInstance()->sspReply(
            bd_addr, variant, accept, passkey);
}

static const void* arc_get_profile_interface(const char* profile_id) {
    return arc::BluetoothArcService::GetInstance()->getProfileInterface(profile_id);
}

static int arc_dut_mode_configure(uint8_t enable) {
    return BT_STATUS_UNSUPPORTED;
}

static int arc_dut_mode_send(uint16_t opcode, uint8_t* buf, uint8_t len) {
    return BT_STATUS_UNSUPPORTED;
}

static int arc_le_test_mode(uint16_t opcode, uint8_t* buf, uint8_t len) {
    return BT_STATUS_UNSUPPORTED;
}

static int arc_set_os_callouts(bt_os_callouts_t* callouts) {
    // OS callouts are not needed — Chrome OS handles wake locks.
    return BT_STATUS_SUCCESS;
}

static int arc_read_energy_info() {
    return BT_STATUS_UNSUPPORTED;
}

static void arc_dump(int fd, const char** arguments) {
    dprintf(fd, "BluetoothArc: Mojo bridge to Chrome OS\n");
}

static void arc_dump_metrics(std::string* output) {
    if (output) *output = "";
}

static int arc_config_clear() {
    return BT_STATUS_SUCCESS;
}

static void arc_interop_database_clear() {}

static void arc_interop_database_add(uint16_t feature, const RawAddress* addr,
                                     size_t len) {}

static bluetooth::avrcp::ServiceInterface* arc_get_avrcp_service() {
    return nullptr;
}

static std::string arc_obfuscate_address(const RawAddress& address) {
    return address.ToString();
}

static int arc_get_metric_id(const RawAddress& address) {
    return 0;
}

// ---------------------------------------------------------------------------
// Exported symbol: bluetoothInterface
// ---------------------------------------------------------------------------

__attribute__((visibility("default")))
bt_interface_t bluetoothInterface = {
    .size = sizeof(bt_interface_t),
    .init = arc_init,
    .enable = arc_enable,
    .disable = arc_disable,
    .cleanup = arc_cleanup,
    .get_adapter_properties = arc_get_adapter_properties,
    .get_adapter_property = arc_get_adapter_property,
    .set_adapter_property = arc_set_adapter_property,
    .get_remote_device_properties = arc_get_remote_device_properties,
    .get_remote_device_property = arc_get_remote_device_property,
    .set_remote_device_property = arc_set_remote_device_property,
    .get_remote_service_record = arc_get_remote_service_record,
    .get_remote_services = arc_get_remote_services,
    .start_discovery = arc_start_discovery,
    .cancel_discovery = arc_cancel_discovery,
    .create_bond = arc_create_bond,
    .create_bond_out_of_band = arc_create_bond_out_of_band,
    .remove_bond = arc_remove_bond,
    .cancel_bond = arc_cancel_bond,
    .get_connection_state = arc_get_connection_state,
    .pin_reply = arc_pin_reply,
    .ssp_reply = arc_ssp_reply,
    .get_profile_interface = arc_get_profile_interface,
    .dut_mode_configure = arc_dut_mode_configure,
    .dut_mode_send = arc_dut_mode_send,
    .le_test_mode = arc_le_test_mode,
    .set_os_callouts = arc_set_os_callouts,
    .read_energy_info = arc_read_energy_info,
    .dump = arc_dump,
    .dumpMetrics = arc_dump_metrics,
    .config_clear = arc_config_clear,
    .interop_database_clear = arc_interop_database_clear,
    .interop_database_add = arc_interop_database_add,
    .get_avrcp_service = arc_get_avrcp_service,
    .obfuscate_address = arc_obfuscate_address,
    .get_metric_id = arc_get_metric_id,
};

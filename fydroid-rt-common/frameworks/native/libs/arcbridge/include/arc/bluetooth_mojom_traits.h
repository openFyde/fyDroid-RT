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

// StructTraits and EnumTraits for mapping arc.mojom Bluetooth types
// to Android-native bluetooth types.
//
// BluetoothUUID       → bluetooth::Uuid      (16-byte Big Endian UUID)
// BluetoothDeviceType → bt_device_type_t     (BREDR/BLE/DUAL enum)

#ifndef ARC_BLUETOOTH_MOJOM_TRAITS_H_
#define ARC_BLUETOOTH_MOJOM_TRAITS_H_

#include <bluetooth/uuid.h>
#include <hardware/bluetooth.h>

#include <base/containers/span.h>
#include <mojo/public/cpp/bindings/array_data_view.h>
#include <mojo/public/cpp/bindings/enum_traits.h>
#include <mojo/public/cpp/bindings/struct_traits.h>

#include "arc/bluetooth.mojom-shared.h"

namespace mojo {

// --- BluetoothUUID ↔ bluetooth::Uuid ---

template <>
struct StructTraits<arc::mojom::BluetoothUUIDDataView, bluetooth::Uuid> {
  static base::span<const uint8_t> uuid(const bluetooth::Uuid& input) {
    const auto& bytes = input.To128BitBE();
    return base::make_span(bytes.data(), bytes.size());
  }

  static bool Read(arc::mojom::BluetoothUUIDDataView data,
                   bluetooth::Uuid* out) {
    mojo::ArrayDataView<uint8_t> uuid_bytes;
    data.GetUuidDataView(&uuid_bytes);
    if (uuid_bytes.is_null() || uuid_bytes.size() != 16)
      return false;
    *out = bluetooth::Uuid::From128BitBE(uuid_bytes.data());
    return true;
  }
};

// --- BluetoothDeviceType ↔ bt_device_type_t ---
// Values match exactly: BREDR=1, BLE=2, DUAL=3.

template <>
struct EnumTraits<arc::mojom::BluetoothDeviceType, bt_device_type_t> {
  static arc::mojom::BluetoothDeviceType ToMojom(bt_device_type_t input) {
    return static_cast<arc::mojom::BluetoothDeviceType>(input);
  }

  static bool FromMojom(arc::mojom::BluetoothDeviceType input,
                        bt_device_type_t* output) {
    switch (input) {
      case arc::mojom::BluetoothDeviceType::BREDR:
        *output = BT_DEVICE_DEVTYPE_BREDR;
        return true;
      case arc::mojom::BluetoothDeviceType::BLE:
        *output = BT_DEVICE_DEVTYPE_BLE;
        return true;
      case arc::mojom::BluetoothDeviceType::DUAL:
        *output = BT_DEVICE_DEVTYPE_DUAL;
        return true;
    }
    return false;
  }
};

}  // namespace mojo

#endif  // ARC_BLUETOOTH_MOJOM_TRAITS_H_

/*
 * Copyright (C) 2026 The FydeOS Authors
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
 */

package com.android.server.arc.usb;

import android.hardware.usb.IUsbSerialReader;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Slog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds {@link UsbDevice} entries for USB input devices by reading sysfs
 * directly, bypassing the Chrome USB host bridge.
 *
 * Chrome never announces a keyboard, mouse or gamepad over that bridge.
 * {@code ArcUsbHostBridge::OnDeviceChecked} silently drops any device
 * permission_broker refuses, and permission_broker's
 * {@code AllowConformingUsbDeviceRule} refuses a device whose interfaces are
 * claimed by a kernel driver unless every claimed interface is usb-serial or
 * mass storage. A HID interface bound to usbhid is neither, so it is denied and
 * {@code onDeviceAdded} never fires -- leaving {@code getDeviceList()} empty for
 * apps that enumerate controllers that way.
 *
 * The container mounts a full sysfs at /sys, so the descriptors are readable
 * even though the matching usbfs node is not openable. Devices reported here are
 * therefore enumeration-only: they carry no Chrome guid and
 * {@link ArcUsbHostManager#openDevice} cannot hand out an fd for them.
 */
final class ArcUsbSysfsScanner {
    private static final String TAG = "ArcUsbSysfsScanner";

    private static final File SYSFS_USB_DEVICES = new File("/sys/bus/usb/devices");

    private static final int USB_CLASS_HID = 0x03;
    // Fallbacks matching the values the Chrome bridge path synthesizes.
    private static final int USB_BM_ATTRIBUTES = 128;
    private static final int USB_MAX_POWER = 500;
    private static final int U8 = 0xFF;

    private static final Comparator<File> BY_NAME = new Comparator<File>() {
        @Override
        public int compare(File a, File b) {
            return a.getName().compareTo(b.getName());
        }
    };

    private ArcUsbSysfsScanner() {
    }

    /**
     * Returns the USB input devices currently present, keyed by the usbfs device
     * name Android identifies them with. Never null; empty if sysfs is
     * unreadable.
     */
    static Map<String, UsbDevice> scanInputDevices() {
        LinkedHashMap<String, UsbDevice> devices = new LinkedHashMap<>();
        File[] entries = SYSFS_USB_DEVICES.listFiles();
        if (entries == null) {
            Slog.w(TAG, "Cannot list " + SYSFS_USB_DEVICES + ", reporting no USB input devices");
            return devices;
        }
        Arrays.sort(entries, BY_NAME);
        for (File entry : entries) {
            String name = entry.getName();
            // The directory lists interfaces ("1-1:1.0") next to their device
            // ("1-1"), plus a root hub per bus ("usb1"). Only real devices are
            // worth descending into.
            if (name.indexOf(':') >= 0 || name.startsWith("usb")) {
                continue;
            }
            try {
                UsbDevice device = buildDevice(entry);
                if (device != null) {
                    devices.put(device.getDeviceName(), device);
                }
            } catch (Throwable t) {
                // A malformed descriptor, or a device unplugged mid-scan, must
                // never take down the enumeration (or system_server with it).
                Slog.w(TAG, "Skipping USB device " + name, t);
            }
        }
        return devices;
    }

    /** Returns null unless the device exposes at least one input interface. */
    private static UsbDevice buildDevice(File deviceDir) {
        List<UsbInterface> interfaces = readInputInterfaces(deviceDir);
        if (interfaces.isEmpty()) {
            return null;
        }

        // Without bus/device numbers there is no name to key the device on, and
        // without the ids no app can match it.
        int busNumber = readDec(deviceDir, "busnum", -1);
        int deviceNumber = readDec(deviceDir, "devnum", -1);
        int vendorId = readHex(deviceDir, "idVendor", -1);
        int productId = readHex(deviceDir, "idProduct", -1);
        if (busNumber < 0 || deviceNumber < 0 || vendorId < 0 || productId < 0) {
            Slog.w(TAG, "Incomplete descriptor for " + deviceDir.getName());
            return null;
        }

        // sysfs only describes the active configuration, which is the one an app
        // would talk to anyway.
        UsbConfiguration configuration = new UsbConfiguration(
                readDec(deviceDir, "bConfigurationValue", 1),
                readString(deviceDir, "configuration"),
                readHex(deviceDir, "bmAttributes", USB_BM_ATTRIBUTES),
                readMilliamps(deviceDir, "bMaxPower", USB_MAX_POWER));
        configuration.setInterfaces(interfaces.toArray(new UsbInterface[0]));

        final String serial = readString(deviceDir, "serial");
        UsbDevice.Builder builder = new UsbDevice.Builder(
                String.format("/dev/bus/usb/%03d/%03d", busNumber, deviceNumber),
                vendorId, productId,
                readHex(deviceDir, "bDeviceClass", 0),
                readHex(deviceDir, "bDeviceSubClass", 0),
                readHex(deviceDir, "bDeviceProtocol", 0),
                readString(deviceDir, "manufacturer"),
                readString(deviceDir, "product"),
                versionOf(readHex(deviceDir, "bcdDevice", 0)),
                new UsbConfiguration[] { configuration },
                serial,
                false /* hasAudioPlayback */, false /* hasAudioCapture */,
                false /* hasMidi */, false /* hasVideoPlayback */,
                false /* hasVideoCapture */);
        return builder.build(new IUsbSerialReader.Stub() {
            @Override
            public String getSerial(String packageName) {
                return serial;
            }
        });
    }

    private static List<UsbInterface> readInputInterfaces(File deviceDir) {
        ArrayList<UsbInterface> interfaces = new ArrayList<>();
        File[] children = deviceDir.listFiles();
        if (children == null) {
            return interfaces;
        }
        Arrays.sort(children, BY_NAME);
        // Interfaces are children named "<device>:<config>.<interface>".
        String prefix = deviceDir.getName() + ":";
        for (File child : children) {
            if (!child.getName().startsWith(prefix) || !isInputInterface(child)) {
                continue;
            }
            UsbInterface intf = new UsbInterface(
                    readHex(child, "bInterfaceNumber", 0),
                    readDec(child, "bAlternateSetting", 0),
                    readString(child, "interface"),
                    readHex(child, "bInterfaceClass", 0),
                    readHex(child, "bInterfaceSubClass", 0),
                    readHex(child, "bInterfaceProtocol", 0));
            intf.setEndpoints(readEndpoints(child));
            interfaces.add(intf);
        }
        return interfaces;
    }

    /**
     * An interface counts as input if it declares the HID class, or if the
     * kernel bound it to a driver that published an evdev node (an "input*"
     * child). The second test keeps vendor-class controllers -- Xbox pads on
     * xpad declare class 0xFF but are input devices.
     */
    private static boolean isInputInterface(File interfaceDir) {
        if (readHex(interfaceDir, "bInterfaceClass", -1) == USB_CLASS_HID) {
            return true;
        }
        File[] children = interfaceDir.listFiles();
        if (children == null) {
            return false;
        }
        for (File child : children) {
            if (child.getName().startsWith("input")) {
                return true;
            }
        }
        return false;
    }

    private static UsbEndpoint[] readEndpoints(File interfaceDir) {
        ArrayList<UsbEndpoint> endpoints = new ArrayList<>();
        File[] children = interfaceDir.listFiles();
        if (children != null) {
            Arrays.sort(children, BY_NAME);
            for (File child : children) {
                if (!child.getName().startsWith("ep_")) {
                    continue;
                }
                // bEndpointAddress already carries the direction bit, so unlike
                // the mojo path there is nothing to reassemble here.
                endpoints.add(new UsbEndpoint(
                        readHex(child, "bEndpointAddress", 0),
                        readHex(child, "bmAttributes", 0),
                        readHex(child, "wMaxPacketSize", 0),
                        readHex(child, "bInterval", 0)));
            }
        }
        return endpoints.toArray(new UsbEndpoint[0]);
    }

    /** Renders bcdDevice (0x0110) the way {@link UsbDevice#getVersion} expects ("1.10"). */
    private static String versionOf(int bcdDevice) {
        return String.format("%d.%d%d", (bcdDevice >> 8) & U8,
                (bcdDevice >> 4) & 0x0F, bcdDevice & 0x0F);
    }

    // --- sysfs attribute readers ---
    //
    // Numeric formats follow drivers/usb/core/{sysfs,endpoint}.c: device ids,
    // bcdDevice, the bDevice*/bInterface* class triplets, bNumEndpoints and every
    // endpoint attribute print as hex; busnum, devnum, bConfigurationValue and
    // bAlternateSetting print as decimal.

    private static String readString(File dir, String name) {
        File file = new File(dir, name);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8), 128)) {
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            // bAlternateSetting is space-padded ("%2d"); everything is newline
            // terminated.
            line = line.trim();
            return line.isEmpty() ? null : line;
        } catch (IOException e) {
            // Absent or unreadable attributes are normal: optional string
            // descriptors are simply missing on many devices.
            return null;
        }
    }

    private static int readHex(File dir, String name, int defaultValue) {
        return parse(readString(dir, name), 16, defaultValue);
    }

    private static int readDec(File dir, String name, int defaultValue) {
        return parse(readString(dir, name), 10, defaultValue);
    }

    /** Reads a "500mA" style attribute, in milliamps. */
    private static int readMilliamps(File dir, String name, int defaultValue) {
        String value = readString(dir, name);
        if (value == null) {
            return defaultValue;
        }
        int end = 0;
        while (end < value.length() && Character.isDigit(value.charAt(end))) {
            end++;
        }
        return end == 0 ? defaultValue : parse(value.substring(0, end), 10, defaultValue);
    }

    private static int parse(String value, int radix, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value, radix);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

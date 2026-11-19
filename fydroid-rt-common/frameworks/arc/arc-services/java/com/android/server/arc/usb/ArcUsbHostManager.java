/*
 * Copyright (C) 2026 Fyde Innovations
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

package com.android.server.arc.usb;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.hardware.usb.IUsbSerialReader;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.util.Pair;
import android.util.Slog;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import org.chromium.arc.CancellationRelay;
import org.chromium.arc.FutureCallback;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.UsbHostHost;
import org.chromium.arc.mojom.UsbHostInstance;
import org.chromium.device.mojom.UsbAlternateInterfaceInfo;
import org.chromium.device.mojom.UsbConfigurationInfo;
import org.chromium.device.mojom.UsbDeviceInfo;
import org.chromium.device.mojom.UsbEndpointInfo;
import org.chromium.device.mojom.UsbInterfaceInfo;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.mojo_base.mojom.String16;

/**
 * Android-side ARC USB host bridge. Receives device add/remove notifications
 * from Chrome's ArcUsbHostBridge over the {@code UsbHostInstance} mojo interface,
 * exposes them to {@link ArcUsbService} (the IUsbManager binder), and opens
 * devices by requesting an fd from Chrome via {@code UsbHostHost.openDevice}.
 *
 * Chrome only ever announces devices permission_broker is willing to release,
 * which excludes every input device, so the enumeration is topped up from sysfs
 * via {@link ArcUsbSysfsScanner}. Those entries are enumeration-only: they have
 * no guid and cannot be opened.
 */
public final class ArcUsbHostManager
        implements UsbHostInstance, ArcService, ConnectionErrorHandler {
    private static final String TAG = "ArcUsbHostManager";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 33;
    private static final int ARC_USB_HOST_OPEN_DEVICE_VERSION = 3;
    private static final int USB_BM_ATTRIBUTES = 128;
    private static final int USB_EP_INTERVAL = 255;
    private static final int USB_MAX_POWER = 500;
    private static final int U8 = 0xFF;

    private final Context mContext;
    private final Object mLock = new Object();
    // deviceName -> (guid, UsbDevice)
    private final HashMap<String, Pair<String, UsbDevice>> mDevices = new HashMap<>();
    // deviceName -> UsbDevice, for input devices read straight from sysfs because
    // Chrome will not announce them. See ArcUsbSysfsScanner. Replaced wholesale on
    // each refresh; guarded by mLock.
    private Map<String, UsbDevice> mSysfsDevices = new LinkedHashMap<>();
    private CancellationRelay mCancellationRelay = new CancellationRelay();
    private UsbHostHost mUsbHost = null;
    private int mUsbHostVersion = -1;

    public ArcUsbHostManager(Context context) {
        mContext = context;
    }

    public void systemReady() {
        Slog.i(TAG, "Init ARC USB host service");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering UsbHostInstance");
        host.onUsbHostInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch with host version " + version);
    }

    // --- UsbHostInstance ---

    @Override
    public void init(UsbHostHost host, final InitResponse response) {
        ArcBridgeSupport.buildThreadSafeProxy(host, UsbHostHost.MANAGER, this,
                new BiConsumer<UsbHostHost, Integer>() {
                    @Override
                    public void accept(UsbHostHost proxy, Integer version) {
                        synchronized (mLock) {
                            mUsbHost = proxy;
                            mUsbHostVersion = version;
                        }
                        response.call();
                        Slog.i(TAG, "UsbHostHost ready, version=" + version);
                    }
                });
    }

    @Override
    public void onDeviceAdded(String guid, String[] packages) {
        Slog.d(TAG, "onDeviceAdded: " + guid);
        getDeviceInfo(guid, packages);
    }

    @Override
    public void onDeviceRemoved(String guid, String[] packages) {
        synchronized (mLock) {
            Iterator<Map.Entry<String, Pair<String, UsbDevice>>> it =
                    mDevices.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Pair<String, UsbDevice>> entry = it.next();
                if (entry.getValue().first.equals(guid)) {
                    Slog.d(TAG, "onDeviceRemoved: " + entry.getKey());
                    broadcastUsbEvent(entry.getValue().second, false, packages);
                    it.remove();
                }
            }
        }
    }

    // --- ConnectionErrorHandler / mojo close ---

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        closeAndRetry();
    }

    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        closeAndRetry();
    }

    private void closeAndRetry() {
        synchronized (mLock) {
            mUsbHost = null;
            mUsbHostVersion = -1;
            mCancellationRelay.cancel(false);
        }
        mCancellationRelay = new CancellationRelay();
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- IUsbManager surface (called by ArcUsbService) ---

    public void getDeviceList(Bundle bundle, String packageName) {
        UsbHostHost host;
        synchronized (mLock) {
            host = mUsbHost;
        }
        if (host == null) {
            Slog.w(TAG, "UsbHost not available");
            return;
        }
        // Enumeration is not gated on a user prompt, matching stock Android
        // (and ARCVM, which registers the stock UsbService): getDeviceList()
        // needs no permission there, only openDevice() does. Asking Chrome
        // here raised the scan-device-list dialog on an app's first call, even
        // with nothing attached. Opening a device still requires per-device
        // consent; see openDevice().
        refreshSysfsDevices();
        synchronized (mLock) {
            // sysfs first, so that a device Chrome did announce -- and
            // which can therefore actually be opened -- wins a
            // deviceName collision.
            for (Map.Entry<String, UsbDevice> entry : mSysfsDevices.entrySet()) {
                bundle.putParcelable(entry.getKey(), entry.getValue());
            }
            for (String name : mDevices.keySet()) {
                bundle.putParcelable(name, mDevices.get(name).second);
            }
        }
    }

    public ParcelFileDescriptor openDevice(String deviceName, String packageName) {
        Slog.d(TAG, "openDevice: " + deviceName);
        synchronized (mLock) {
            if (!checkUsbHostLocked(ARC_USB_HOST_OPEN_DEVICE_VERSION)) {
                return null;
            }
            String guid = getDeviceGuidLocked(deviceName);
            if (guid.isEmpty()) {
                if (mSysfsDevices.containsKey(deviceName)) {
                    // Found via sysfs: a kernel driver owns the interface, which
                    // is exactly why permission_broker withheld it from Chrome,
                    // so there is no fd to hand out. Enumerable, not claimable.
                    Slog.w(TAG, "openDevice: " + deviceName
                            + " is claimed by a kernel driver, cannot be opened");
                    return null;
                }
                throw new IllegalArgumentException(
                        "device " + deviceName + " does not exist or is restricted");
            }
            Slog.d(TAG, "openDevice: " + deviceName + " => " + guid);
            return getDeviceFd(guid, packageName);
        }
    }

    public boolean hasPermission(UsbDevice device, String packageName) {
        Slog.d(TAG, "hasDevicePermission: " + device.getDeviceName());
        UsbHostHost host;
        String guid;
        synchronized (mLock) {
            host = mUsbHost;
            guid = getDeviceGuidLocked(device.getDeviceName());
            if (guid.isEmpty() && mSysfsDevices.containsKey(device.getDeviceName())) {
                // Enumeration-only device. The caller already cleared the
                // scan-device-list permission to have seen it at all, and an
                // empty guid would route this non-interactive query into
                // Chrome's interactive scan-list prompt. Answer it here.
                return true;
            }
        }
        if (host == null) {
            Slog.w(TAG, "UsbHost not available");
            return false;
        }
        final FutureCallback futureCallback = new FutureCallback(mCancellationRelay);
        host.requestPermission(guid, packageName, false,
                new UsbHostHost.RequestPermissionResponse() {
                    @Override
                    public void call(Boolean authorized) {
                        futureCallback.set(authorized);
                    }
                });
        try {
            Boolean authorized = (Boolean) futureCallback.get();
            Slog.d(TAG, "hasDevicePermission " + device.getDeviceName() + " = " + authorized);
            return authorized != null && authorized;
        } catch (InterruptedException | ExecutionException e) {
            Slog.e(TAG, "Interrupted while reading permission", e);
            return false;
        }
    }

    public void requestPermission(final UsbDevice device, String packageName,
            final PendingIntent pi) {
        Slog.d(TAG, "requestDevicePermission: " + device.getDeviceName() + " from:" + packageName);
        final Context userContext;
        try {
            userContext = mContext.createPackageContextAsUser("android", 0,
                    new UserHandle(UserHandle.getCallingUserId()));
        } catch (PackageManager.NameNotFoundException e) {
            Slog.d(TAG, "requestPermission cannot find 'android' package");
            return;
        }
        UsbHostHost host;
        String guid;
        synchronized (mLock) {
            host = mUsbHost;
            guid = getDeviceGuidLocked(device.getDeviceName());
        }
        if (host == null) {
            Slog.w(TAG, "UsbHost not available");
            return;
        }
        host.requestPermission(guid, packageName, true,
                new UsbHostHost.RequestPermissionResponse() {
                    @Override
                    public void call(Boolean authorized) {
                        onPermissionReceived(userContext, pi, device, authorized);
                    }
                });
    }

    // --- internals ---

    private boolean checkUsbHostLocked(int minVersion) {
        if (mUsbHost == null) {
            Slog.e(TAG, "USB Host unavailable");
            return false;
        }
        if (mUsbHostVersion >= minVersion) {
            return true;
        }
        Slog.e(TAG, "USB Host version is old: " + mUsbHostVersion + " (" + minVersion + ").");
        return false;
    }

    private String getDeviceGuidLocked(String deviceName) {
        Pair<String, UsbDevice> entry = mDevices.get(deviceName);
        return entry == null ? "" : entry.first;
    }

    /**
     * Re-reads the USB input devices from sysfs. Chrome only announces devices
     * permission_broker releases, which never includes a keyboard, mouse or
     * gamepad, so these have to be discovered rather than pushed to us.
     *
     * This is pull-only, refreshed whenever the list is asked for. There is no
     * attach/detach broadcast for these devices: the container cannot be told
     * about hotplug. Its network namespace lives inside a user namespace, and
     * uevent_net_init() (kernel lib/kobject_uevent.c) only puts a namespace on
     * the uevent broadcast list when it is owned by the initial user namespace,
     * so a NETLINK_KOBJECT_UEVENT socket here binds and subscribes but never
     * receives anything.
     */
    private void refreshSysfsDevices() {
        // Scanned outside mLock: it is file I/O, and nothing else needs the lock
        // held across it.
        Map<String, UsbDevice> scanned = ArcUsbSysfsScanner.scanInputDevices();
        synchronized (mLock) {
            mSysfsDevices = scanned;
        }
    }

    private void getDeviceInfo(final String guid, final String[] packages) {
        UsbHostHost host;
        synchronized (mLock) {
            host = mUsbHost;
        }
        if (host == null) {
            Slog.w(TAG, "UsbHost not available");
            return;
        }
        Slog.d(TAG, "USB device " + guid);
        host.getDeviceInfo(guid, new UsbHostHost.GetDeviceInfoResponse() {
            @Override
            public void call(String deviceName, UsbDeviceInfo info) {
                addUsbDevice(guid, packages, deviceName, info);
            }
        });
    }

    private void addUsbDevice(String guid, String[] packages, final String deviceName,
            UsbDeviceInfo info) {
        try {
            Slog.d(TAG, String.format("Adding device %s => %s VID:PID %04X:%04X",
                    deviceName, guid, info.vendorId & 0xFFFF, info.productId & 0xFFFF));

            String version = String.format("%d.%d%d",
                    info.deviceVersionMajor & U8, info.deviceVersionMinor & U8,
                    info.deviceVersionSubminor & U8);

            ArrayList<UsbConfiguration> configs =
                    new ArrayList<>(info.configurations.length);
            for (UsbConfigurationInfo ci : info.configurations) {
                UsbConfiguration config = new UsbConfiguration(ci.configurationValue & U8,
                        fromString16(ci.configurationName), USB_BM_ATTRIBUTES, USB_MAX_POWER);
                ArrayList<UsbInterface> interfaces = new ArrayList<>();
                for (UsbInterfaceInfo ii : ci.interfaces) {
                    for (UsbAlternateInterfaceInfo ai : ii.alternates) {
                        UsbInterface intf = new UsbInterface(ii.interfaceNumber & U8,
                                ai.alternateSetting & U8, fromString16(ai.interfaceName),
                                ai.classCode & U8, ai.subclassCode & U8, ai.protocolCode & U8);
                        ArrayList<UsbEndpoint> endpoints =
                                new ArrayList<>(ai.endpoints.length);
                        for (UsbEndpointInfo ei : ai.endpoints) {
                            int address = (ei.endpointNumber & U8)
                                    | (ei.direction == 0 ? USB_BM_ATTRIBUTES : 0);
                            endpoints.add(new UsbEndpoint(
                                    address, ei.type, ei.packetSize, USB_EP_INTERVAL));
                        }
                        intf.setEndpoints(endpoints.toArray(new UsbEndpoint[0]));
                        interfaces.add(intf);
                    }
                }
                config.setInterfaces(interfaces.toArray(new UsbInterface[0]));
                configs.add(config);
            }

            final String serial = fromString16(info.serialNumber);
            UsbDevice.Builder builder = new UsbDevice.Builder(deviceName,
                    info.vendorId & 0xFFFF, info.productId & 0xFFFF,
                    info.classCode & U8, info.subclassCode & U8, info.protocolCode & U8,
                    fromString16(info.manufacturerName), fromString16(info.productName),
                    version, configs.toArray(new UsbConfiguration[0]), serial,
                    false, false, false, false, false);
            UsbDevice device = builder.build(new IUsbSerialReader.Stub() {
                @Override
                public String getSerial(String packageName) {
                    return serial;
                }
            });

            synchronized (mLock) {
                mDevices.put(deviceName, Pair.create(guid, device));
            }
            broadcastUsbEvent(device, true, packages);
        } catch (Throwable t) {
            // A malformed descriptor must never crash system_server.
            Slog.e(TAG, "Failed to add USB device " + deviceName, t);
        }
    }

    private static String fromString16(String16 s) {
        if (s == null || s.data == null) {
            return null;
        }
        char[] chars = new char[s.data.length];
        for (int i = 0; i < s.data.length; i++) {
            chars[i] = (char) s.data[i];
        }
        return new String(chars);
    }

    private ParcelFileDescriptor getDeviceFd(String guid, String packageName) {
        // Caller holds mLock.
        if (mUsbHost == null) {
            Slog.w(TAG, "UsbHost not available");
            return null;
        }
        final FutureCallback futureCallback = new FutureCallback(mCancellationRelay);
        mUsbHost.openDevice(guid, packageName, new UsbHostHost.OpenDeviceResponse() {
            @Override
            public void call(UntypedHandle handle) {
                futureCallback.set(handle);
            }
        });
        try {
            int fd = ArcBridgeSupport.getInstance()
                    .convertMojoHandleToFileDescriptor((UntypedHandle) futureCallback.get());
            Slog.d(TAG, "getDeviceFd " + guid + " FD=" + fd);
            return fd < 0 ? null : ParcelFileDescriptor.adoptFd(fd);
        } catch (InterruptedException | ExecutionException e) {
            Slog.e(TAG, "Interrupted while trying to get open FD", e);
            return null;
        }
    }

    private void onPermissionReceived(Context context, PendingIntent pi, UsbDevice device,
            boolean granted) {
        Intent intent = new Intent();
        intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, granted);
        try {
            pi.send(context, 0, intent);
        } catch (PendingIntent.CanceledException e) {
            Slog.d(TAG, "requestPermission PendingIntent was cancelled");
        }
    }

    private void broadcastUsbEvent(UsbDevice device, boolean attached, String[] packages) {
        for (String pkg : packages) {
            Intent intent = new Intent(attached
                    ? UsbManager.ACTION_USB_DEVICE_ATTACHED
                    : UsbManager.ACTION_USB_DEVICE_DETACHED);
            intent.putExtra(UsbManager.EXTRA_DEVICE, device);
            intent.setPackage(pkg);
            if (attached) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    public void dump(PrintWriter pw) {
        refreshSysfsDevices();
        synchronized (mLock) {
            pw.println("ArcUsbHostManager: host=" + (mUsbHost != null)
                    + " version=" + mUsbHostVersion + " devices=" + mDevices.size()
                    + " sysfsInputDevices=" + mSysfsDevices.size());
            for (String name : mDevices.keySet()) {
                pw.println("  " + name + " => " + mDevices.get(name).first);
            }
            for (Map.Entry<String, UsbDevice> entry : mSysfsDevices.entrySet()) {
                UsbDevice device = entry.getValue();
                pw.println(String.format("  %s => sysfs VID:PID %04X:%04X %s",
                        entry.getKey(), device.getVendorId(), device.getProductId(),
                        device.getProductName()));
            }
        }
    }
}

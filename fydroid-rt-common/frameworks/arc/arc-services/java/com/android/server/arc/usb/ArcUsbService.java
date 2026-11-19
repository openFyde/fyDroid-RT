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

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbOperationInternal;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbOperationInternal;
import android.hardware.usb.UsbPortStatus;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.util.Slog;

import com.android.server.SystemService;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/**
 * ARC IUsbManager. Replaces the AOSP UsbService binder ("usb") so app
 * {@link android.hardware.usb.UsbManager} calls reach the ARC host bridge.
 * The host (enumeration/open/permission) calls delegate to
 * {@link ArcUsbHostManager}; gadget/accessory functions are not supported in
 * the container and are stubbed.
 */
public final class ArcUsbService extends IUsbManager.Stub {
    private static final String TAG = "ArcUsbService";

    private final Context mContext;
    private final ArcUsbHostManager mArcHostManager;

    public static class LifeCycle extends SystemService {
        private final ArcUsbService mArcUsbService;

        public LifeCycle(Context context) {
            super(context);
            mArcUsbService = new ArcUsbService(context);
        }

        @Override
        public void onStart() {
            publishBinderService("usb", mArcUsbService);
            Slog.d(TAG, "ARC USB service started");
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == SystemService.PHASE_ACTIVITY_MANAGER_READY) {
                mArcUsbService.systemReady();
            }
        }
    }

    private ArcUsbService(Context context) {
        mContext = context;
        mArcHostManager = new ArcUsbHostManager(context);
    }

    private void systemReady() {
        mArcHostManager.systemReady();
    }

    private String getCallingPackageName() {
        String[] packages = mContext.getPackageManager()
                .getPackagesForUid(Binder.getCallingUid());
        return (packages != null && packages.length > 0) ? packages[0] : "";
    }

    // --- USB host: delegate to ArcUsbHostManager ---

    @Override
    public void getDeviceList(Bundle devices) {
        mArcHostManager.getDeviceList(devices, getCallingPackageName());
    }

    @Override
    public ParcelFileDescriptor openDevice(String deviceName, String packageName) {
        return mArcHostManager.openDevice(deviceName, packageName);
    }

    @Override
    public boolean hasDevicePermission(UsbDevice device, String packageName) {
        return mArcHostManager.hasPermission(device, packageName);
    }

    @Override
    public void requestDevicePermission(UsbDevice device, String packageName, PendingIntent pi) {
        mArcHostManager.requestPermission(device, packageName, pi);
    }

    // --- USB accessory / gadget functions: unsupported in the container ---

    @Override
    public UsbAccessory getCurrentAccessory() {
        return null;
    }

    @Override
    public ParcelFileDescriptor openAccessory(UsbAccessory accessory) {
        return null;
    }

    @Override
    public ParcelFileDescriptor getControlFd(long function) {
        return null;
    }

    @Override
    public void setDevicePackage(UsbDevice device, String packageName, int userId) {
    }

    @Override
    public void setAccessoryPackage(UsbAccessory accessory, String packageName, int userId) {
    }

    @Override
    public void addDevicePackagesToPreferenceDenied(UsbDevice device, String[] packageNames,
            UserHandle user) {
    }

    @Override
    public void addAccessoryPackagesToPreferenceDenied(UsbAccessory accessory,
            String[] packageNames, UserHandle user) {
    }

    @Override
    public void removeDevicePackagesFromPreferenceDenied(UsbDevice device, String[] packageNames,
            UserHandle user) {
    }

    @Override
    public void removeAccessoryPackagesFromPreferenceDenied(UsbAccessory accessory,
            String[] packageNames, UserHandle user) {
    }

    @Override
    public void setDevicePersistentPermission(UsbDevice device, int uid, UserHandle user,
            boolean shouldBeGranted) {
    }

    @Override
    public void setAccessoryPersistentPermission(UsbAccessory accessory, int uid, UserHandle user,
            boolean shouldBeGranted) {
    }

    @Override
    public boolean hasAccessoryPermission(UsbAccessory accessory) {
        return false;
    }

    @Override
    public void requestAccessoryPermission(UsbAccessory accessory, String packageName,
            PendingIntent pi) {
    }

    @Override
    public void grantDevicePermission(UsbDevice device, int uid) {
    }

    @Override
    public void grantAccessoryPermission(UsbAccessory accessory, int uid) {
    }

    @Override
    public boolean hasDefaults(String packageName, int userId) {
        return false;
    }

    @Override
    public void clearDefaults(String packageName, int userId) {
    }

    @Override
    public boolean isFunctionEnabled(String function) {
        return false;
    }

    @Override
    public void setCurrentFunctions(long functions) {
        mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    }

    @Override
    public void setCurrentFunction(String function, boolean usbDataUnlocked) {
        mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    }

    @Override
    public long getCurrentFunctions() {
        mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        return 0L;
    }

    @Override
    public void setScreenUnlockedFunctions(long functions) {
    }

    @Override
    public long getScreenUnlockedFunctions() {
        return 0L;
    }

    @Override
    public void resetUsbGadget() {
    }

    @Override
    public List getPorts() {
        return null;
    }

    @Override
    public UsbPortStatus getPortStatus(String portId) {
        return null;
    }

    @Override
    public void setPortRoles(String portId, int powerRole, int dataRole) {
    }

    @Override
    public void enableContaminantDetection(String portId, boolean enable) {
    }

    @Override
    public void setUsbDeviceConnectionHandler(ComponentName usbDeviceConnectionHandler) {
    }

    // AOSP 13 port/gadget additions. getPorts() reports no ports, so these are
    // unreachable; complete the callback anyway so no caller can block on it.
    @Override
    public int getCurrentUsbSpeed() {
        return UsbManager.USB_DATA_TRANSFER_RATE_UNKNOWN;
    }

    @Override
    public int getGadgetHalVersion() {
        return UsbManager.GADGET_HAL_NOT_SUPPORTED;
    }

    @Override
    public int getUsbHalVersion() {
        return UsbManager.USB_HAL_NOT_SUPPORTED;
    }

    @Override
    public void resetUsbPort(String portId, int operationId, IUsbOperationInternal callback) {
        completeOperation(callback);
    }

    @Override
    public boolean enableUsbData(String portId, boolean enable, int operationId,
            IUsbOperationInternal callback) {
        completeOperation(callback);
        return false;
    }

    @Override
    public void enableUsbDataWhileDocked(String portId, int operationId,
            IUsbOperationInternal callback) {
        completeOperation(callback);
    }

    @Override
    public void enableLimitPowerTransfer(String portId, boolean limit, int operationId,
            IUsbOperationInternal callback) {
        completeOperation(callback);
    }

    private static void completeOperation(IUsbOperationInternal callback) {
        try {
            if (callback != null) {
                callback.onOperationComplete(
                        UsbOperationInternal.USB_OPERATION_ERROR_PORT_MISMATCH);
            }
        } catch (android.os.RemoteException e) {
        }
    }

    @Override
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        mArcHostManager.dump(pw);
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.device.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface UsbDevice extends Interface {
    public static final Interface.Manager<UsbDevice, Proxy> MANAGER = UsbDevice_Internal.MANAGER;

    public interface ClaimInterfaceResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface ClearHaltResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface CloseResponse extends Callbacks.Callback0 {
    }

    public interface ControlTransferInResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface ControlTransferOutResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GenericTransferInResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GenericTransferOutResponse extends Callbacks.Callback1<Integer> {
    }

    public interface IsochronousTransferInResponse extends Callbacks.Callback2<byte[], UsbIsochronousPacket[]> {
    }

    public interface IsochronousTransferOutResponse extends Callbacks.Callback1<UsbIsochronousPacket[]> {
    }

    public interface OpenResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends UsbDevice, Interface.Proxy {
    }

    public interface ReleaseInterfaceResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface ResetResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface SetConfigurationResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface SetInterfaceAlternateSettingResponse extends Callbacks.Callback1<Boolean> {
    }

    void claimInterface(byte b, ClaimInterfaceResponse claimInterfaceResponse);

    void clearHalt(byte b, ClearHaltResponse clearHaltResponse);

    void close(CloseResponse closeResponse);

    void controlTransferIn(UsbControlTransferParams usbControlTransferParams, int i, int i2, ControlTransferInResponse controlTransferInResponse);

    void controlTransferOut(UsbControlTransferParams usbControlTransferParams, byte[] bArr, int i, ControlTransferOutResponse controlTransferOutResponse);

    void genericTransferIn(byte b, int i, int i2, GenericTransferInResponse genericTransferInResponse);

    void genericTransferOut(byte b, byte[] bArr, int i, GenericTransferOutResponse genericTransferOutResponse);

    void isochronousTransferIn(byte b, int[] iArr, int i, IsochronousTransferInResponse isochronousTransferInResponse);

    void isochronousTransferOut(byte b, byte[] bArr, int[] iArr, int i, IsochronousTransferOutResponse isochronousTransferOutResponse);

    void open(OpenResponse openResponse);

    void releaseInterface(byte b, ReleaseInterfaceResponse releaseInterfaceResponse);

    void reset(ResetResponse resetResponse);

    void setConfiguration(byte b, SetConfigurationResponse setConfigurationResponse);

    void setInterfaceAlternateSetting(byte b, byte b2, SetInterfaceAlternateSettingResponse setInterfaceAlternateSettingResponse);
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface SensorDevice extends Interface {
    public static final Interface.Manager<SensorDevice, Proxy> MANAGER = SensorDevice_Internal.MANAGER;

    public interface GetAllChannelIdsResponse extends Callbacks.Callback1<String[]> {
    }

    public interface GetAttributesResponse extends Callbacks.Callback1<String[]> {
    }

    public interface GetChannelsAttributesResponse extends Callbacks.Callback1<String[]> {
    }

    public interface GetChannelsEnabledResponse extends Callbacks.Callback1<boolean[]> {
    }

    public interface Proxy extends SensorDevice, Interface.Proxy {
    }

    public interface SetChannelsEnabledResponse extends Callbacks.Callback1<int[]> {
    }

    public interface SetFrequencyResponse extends Callbacks.Callback1<Double> {
    }

    void getAllChannelIds(GetAllChannelIdsResponse getAllChannelIdsResponse);

    void getAttributes(String[] strArr, GetAttributesResponse getAttributesResponse);

    void getChannelsAttributes(int[] iArr, String str, GetChannelsAttributesResponse getChannelsAttributesResponse);

    void getChannelsEnabled(int[] iArr, GetChannelsEnabledResponse getChannelsEnabledResponse);

    void setChannelsEnabled(int[] iArr, boolean z, SetChannelsEnabledResponse setChannelsEnabledResponse);

    void setFrequency(double d, SetFrequencyResponse setFrequencyResponse);

    void setTimeout(int i);

    void startReadingSamples(SensorDeviceSamplesObserver sensorDeviceSamplesObserver);

    void stopReadingSamples();
}

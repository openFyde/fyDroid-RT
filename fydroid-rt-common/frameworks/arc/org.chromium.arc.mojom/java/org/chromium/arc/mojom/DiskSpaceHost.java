// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface DiskSpaceHost extends Interface {
    public static final Interface.Manager<DiskSpaceHost, Proxy> MANAGER = DiskSpaceHost_Internal.MANAGER;

    public interface GetFreeDiskSpaceResponse extends Callbacks.Callback1<DiskSpace> {
    }

    public interface GetQuotaCurrentSpaceForGidResponse extends Callbacks.Callback1<Long> {
    }

    public interface GetQuotaCurrentSpaceForProjectIdResponse extends Callbacks.Callback1<Long> {
    }

    public interface GetQuotaCurrentSpaceForUidResponse extends Callbacks.Callback1<Long> {
    }

    public interface GetQuotaCurrentSpacesForIdsResponse extends Callbacks.Callback1<QuotaSpaces> {
    }

    public interface IsQuotaSupportedResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends DiskSpaceHost, Interface.Proxy {
    }

    void getFreeDiskSpace(GetFreeDiskSpaceResponse getFreeDiskSpaceResponse);

    void getQuotaCurrentSpaceForGid(int i, GetQuotaCurrentSpaceForGidResponse getQuotaCurrentSpaceForGidResponse);

    void getQuotaCurrentSpaceForProjectId(int i, GetQuotaCurrentSpaceForProjectIdResponse getQuotaCurrentSpaceForProjectIdResponse);

    void getQuotaCurrentSpaceForUid(int i, GetQuotaCurrentSpaceForUidResponse getQuotaCurrentSpaceForUidResponse);

    void getQuotaCurrentSpacesForIds(int[] iArr, int[] iArr2, int[] iArr3, GetQuotaCurrentSpacesForIdsResponse getQuotaCurrentSpacesForIdsResponse);

    void isQuotaSupported(IsQuotaSupportedResponse isQuotaSupportedResponse);
}

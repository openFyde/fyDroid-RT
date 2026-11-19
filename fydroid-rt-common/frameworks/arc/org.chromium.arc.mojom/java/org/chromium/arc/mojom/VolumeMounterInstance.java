// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.FilePath;

public interface VolumeMounterInstance extends Interface {
    public static final Interface.Manager<VolumeMounterInstance, Proxy> MANAGER = VolumeMounterInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface PrepareForRemovableMediaUnmountResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends VolumeMounterInstance, Interface.Proxy {
    }

    void init(VolumeMounterHost volumeMounterHost, InitResponse initResponse);

    void onMountEvent(MountPointInfo mountPointInfo);

    void prepareForRemovableMediaUnmount(FilePath filePath, PrepareForRemovableMediaUnmountResponse prepareForRemovableMediaUnmountResponse);
}

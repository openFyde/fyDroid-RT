// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface WallpaperHost extends Interface {
    public static final Interface.Manager<WallpaperHost, Proxy> MANAGER = WallpaperHost_Internal.MANAGER;

    public interface GetWallpaperResponse extends Callbacks.Callback1<byte[]> {
    }

    public interface Proxy extends WallpaperHost, Interface.Proxy {
    }

    void getWallpaper(GetWallpaperResponse getWallpaperResponse);

    void setDefaultWallpaper();

    void setWallpaper(byte[] bArr, int i);
}

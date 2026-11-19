// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.payments.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface DigitalGoodsFactory extends Interface {
    public static final Interface.Manager<DigitalGoodsFactory, Proxy> MANAGER = DigitalGoodsFactory_Internal.MANAGER;

    public interface CreateDigitalGoodsResponse extends Callbacks.Callback2<Integer, DigitalGoods> {
    }

    public interface Proxy extends DigitalGoodsFactory, Interface.Proxy {
    }

    void createDigitalGoods(String str, CreateDigitalGoodsResponse createDigitalGoodsResponse);
}

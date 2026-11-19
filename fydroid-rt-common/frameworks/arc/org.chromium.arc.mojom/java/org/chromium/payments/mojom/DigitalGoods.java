// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.payments.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface DigitalGoods extends Interface {
    public static final Interface.Manager<DigitalGoods, Proxy> MANAGER = DigitalGoods_Internal.MANAGER;

    public interface ConsumeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetDetailsResponse extends Callbacks.Callback2<Integer, ItemDetails[]> {
    }

    public interface ListPurchaseHistoryResponse extends Callbacks.Callback2<Integer, PurchaseReference[]> {
    }

    public interface ListPurchasesResponse extends Callbacks.Callback2<Integer, PurchaseReference[]> {
    }

    public interface Proxy extends DigitalGoods, Interface.Proxy {
    }

    void consume(String str, ConsumeResponse consumeResponse);

    void getDetails(String[] strArr, GetDetailsResponse getDetailsResponse);

    void listPurchaseHistory(ListPurchaseHistoryResponse listPurchaseHistoryResponse);

    void listPurchases(ListPurchasesResponse listPurchasesResponse);
}

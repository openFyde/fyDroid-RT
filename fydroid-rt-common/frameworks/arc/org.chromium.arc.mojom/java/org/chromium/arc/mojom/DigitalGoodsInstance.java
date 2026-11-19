package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.payments.mojom.ItemDetails;
import org.chromium.payments.mojom.PurchaseDetails;
import org.chromium.payments.mojom.PurchaseReference;

public interface DigitalGoodsInstance extends Interface {
    public static final Interface.Manager<DigitalGoodsInstance, Proxy> MANAGER = DigitalGoodsInstance_Internal.MANAGER;

    public interface AcknowledgeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ConsumeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DeprecatedListPurchasesResponse extends Callbacks.Callback2<Integer, PurchaseDetails[]> {
    }

    public interface GetDetailsResponse extends Callbacks.Callback2<Integer, ItemDetails[]> {
    }

    public interface ListPurchaseHistoryResponse extends Callbacks.Callback2<Integer, PurchaseReference[]> {
    }

    public interface ListPurchasesResponse extends Callbacks.Callback2<Integer, PurchaseReference[]> {
    }

    public interface Proxy extends DigitalGoodsInstance, Interface.Proxy {
    }

    void acknowledge(String str, String str2, String str3, boolean z, AcknowledgeResponse acknowledgeResponse);

    void consume(String str, String str2, String str3, ConsumeResponse consumeResponse);

    void deprecatedListPurchases(String str, String str2, DeprecatedListPurchasesResponse deprecatedListPurchasesResponse);

    void getDetails(String str, String str2, String[] strArr, GetDetailsResponse getDetailsResponse);

    void listPurchaseHistory(String str, String str2, ListPurchaseHistoryResponse listPurchaseHistoryResponse);

    void listPurchases(String str, String str2, ListPurchasesResponse listPurchasesResponse);
}

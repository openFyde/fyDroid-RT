package org.chromium.chromeos.payments.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface PaymentAppInstance extends Interface {
    public static final Interface.Manager<PaymentAppInstance, Proxy> MANAGER = PaymentAppInstance_Internal.MANAGER;

    public interface AbortPaymentAppResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface InvokePaymentAppResponse extends Callbacks.Callback1<InvokePaymentAppResult> {
    }

    public interface IsPaymentImplementedResponse extends Callbacks.Callback1<IsPaymentImplementedResult> {
    }

    public interface IsReadyToPayResponse extends Callbacks.Callback1<IsReadyToPayResult> {
    }

    public interface Proxy extends PaymentAppInstance, Interface.Proxy {
    }

    void abortPaymentApp(String str, AbortPaymentAppResponse abortPaymentAppResponse);

    void invokePaymentApp(PaymentParameters paymentParameters, InvokePaymentAppResponse invokePaymentAppResponse);

    void isPaymentImplemented(String str, IsPaymentImplementedResponse isPaymentImplementedResponse);

    void isReadyToPay(PaymentParameters paymentParameters, IsReadyToPayResponse isReadyToPayResponse);
}

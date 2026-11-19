package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface PrintSessionInstance extends Interface {
    public static final Interface.Manager<PrintSessionInstance, Proxy> MANAGER = PrintSessionInstance_Internal.MANAGER;

    public interface CreatePreviewDocumentResponse extends Callbacks.Callback2<UntypedHandle, Long> {
    }

    public interface Proxy extends PrintSessionInstance, Interface.Proxy {
    }

    void createPreviewDocument(PrintDocumentRequest printDocumentRequest, CreatePreviewDocumentResponse createPreviewDocumentResponse);

    void onPrintPreviewClosed();
}

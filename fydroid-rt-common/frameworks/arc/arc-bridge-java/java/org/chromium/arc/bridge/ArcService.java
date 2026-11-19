package org.chromium.arc.bridge;

import org.chromium.arc.mojom.ArcBridgeHost;

/**
 * Interface for services that connect to the ARC bridge.
 * Services implement this to receive the ArcBridgeHost Mojo proxy
 * when the bridge connection is established.
 */
public interface ArcService {
    /**
     * Called when the ARC bridge connection is ready and the host version
     * meets the minimum version requirement.
     *
     * @param arcBridgeHost The Mojo proxy for communicating with Chrome OS
     */
    void onBridgeReady(ArcBridgeHost arcBridgeHost);

    /**
     * Called when the ARC bridge host version is lower than the minimum
     * version requested by this service.
     *
     * @param version The actual host version
     */
    void onBridgeVersionMismatch(int version);
}

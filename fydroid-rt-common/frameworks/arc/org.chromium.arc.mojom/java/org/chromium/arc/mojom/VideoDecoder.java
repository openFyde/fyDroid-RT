package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.AssociatedInterfaceRequestNotSupported;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface VideoDecoder extends Interface {
    public static final Interface.Manager<VideoDecoder, Proxy> MANAGER = VideoDecoder_Internal.MANAGER;

    public interface DecodeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface InitializeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends VideoDecoder, Interface.Proxy {
    }

    public interface ResetResponse extends Callbacks.Callback0 {
    }

    void decode(DecoderBuffer decoderBuffer, DecodeResponse decodeResponse);

    void initialize(VideoDecoderConfig videoDecoderConfig, VideoDecoderClient videoDecoderClient, AssociatedInterfaceRequestNotSupported associatedInterfaceRequestNotSupported, InitializeResponse initializeResponse);

    void releaseVideoFrame(int i);

    void reset(ResetResponse resetResponse);
}

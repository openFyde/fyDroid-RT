// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface VideoDecodeAccelerator extends Interface {
    public static final Interface.Manager<VideoDecodeAccelerator, Proxy> MANAGER = VideoDecodeAccelerator_Internal.MANAGER;

    public interface FlushResponse extends Callbacks.Callback1<Integer> {
    }

    public interface InitializeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends VideoDecodeAccelerator, Interface.Proxy {
    }

    public interface ResetResponse extends Callbacks.Callback1<Integer> {
    }

    public static final class Result {
        public static final int CANCELLED = 6;
        public static final int ILLEGAL_STATE = 1;
        public static final int INSUFFICIENT_RESOURCES = 5;
        public static final int INVALID_ARGUMENT = 2;
        public static final int PLATFORM_FAILURE = 4;
        public static final int SUCCESS = 0;
        public static final int UNREADABLE_INPUT = 3;

        private Result() {
        }

        public static boolean isKnownValue(int i) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    return true;
                default:
                    return false;
            }
        }

        public static void validate(int i) {
        }
    }

    void assignPictureBuffers(int i);

    void decode(BitstreamBuffer bitstreamBuffer);

    void flush(FlushResponse flushResponse);

    void importBufferForPicture(int i, int i2, UntypedHandle untypedHandle, VideoFramePlane[] videoFramePlaneArr, BufferModifier bufferModifier);

    void initialize(VideoDecodeAcceleratorConfig videoDecodeAcceleratorConfig, VideoDecodeClient videoDecodeClient, InitializeResponse initializeResponse);

    void reset(ResetResponse resetResponse);

    void reusePictureBuffer(int i);
}

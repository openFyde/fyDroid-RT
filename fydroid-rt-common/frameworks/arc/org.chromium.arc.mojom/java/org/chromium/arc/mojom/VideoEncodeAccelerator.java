// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface VideoEncodeAccelerator extends Interface {
    public static final Interface.Manager<VideoEncodeAccelerator, Proxy> MANAGER = VideoEncodeAccelerator_Internal.MANAGER;

    public interface EncodeResponse extends Callbacks.Callback0 {
    }

    public static final class Error {
        public static final int ERROR_MAX = 2;
        public static final int ILLEGAL_STATE_ERROR = 0;
        public static final int INVALID_ARGUMENT_ERROR = 1;
        public static final int PLATFORM_FAILURE_ERROR = 2;

        private Error() {
        }

        public static boolean isKnownValue(int i) {
            return i == 0 || i == 1 || i == 2;
        }

        public static void validate(int i) {
        }
    }

    public interface FlushResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface GetSupportedProfilesResponse extends Callbacks.Callback1<VideoEncodeProfile[]> {
    }

    public interface InitializeDeprecatedResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface InitializeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends VideoEncodeAccelerator, Interface.Proxy {
    }

    public static final class Result {
        public static final int ILLEGAL_STATE_ERROR = 1;
        public static final int INSUFFICIENT_RESOURCES_ERROR = 4;
        public static final int INVALID_ARGUMENT_ERROR = 2;
        public static final int PLATFORM_FAILURE_ERROR = 3;
        public static final int SUCCESS = 0;

        private Result() {
        }

        public static boolean isKnownValue(int i) {
            return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
        }

        public static void validate(int i) {
            if (!isKnownValue(i)) {
                throw new DeserializationException("Invalid enum value.");
            }
        }
    }

    public interface UseBitstreamBufferResponse extends Callbacks.Callback3<Integer, Boolean, Long> {
    }

    void encode(int i, UntypedHandle untypedHandle, VideoFramePlane[] videoFramePlaneArr, long j, boolean z, EncodeResponse encodeResponse);

    void flush(FlushResponse flushResponse);

    void getSupportedProfiles(GetSupportedProfilesResponse getSupportedProfilesResponse);

    void initialize(VideoEncodeAcceleratorConfig videoEncodeAcceleratorConfig, VideoEncodeClient videoEncodeClient, InitializeResponse initializeResponse);

    void initializeDeprecated(VideoEncodeAcceleratorConfig videoEncodeAcceleratorConfig, VideoEncodeClient videoEncodeClient, InitializeDeprecatedResponse initializeDeprecatedResponse);

    void requestEncodingParametersChange(int i, int i2);

    void useBitstreamBuffer(UntypedHandle untypedHandle, int i, int i2, UseBitstreamBufferResponse useBitstreamBufferResponse);
}

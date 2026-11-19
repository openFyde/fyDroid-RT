package org.chromium.cros.mojom;

import java.util.Map;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface Camera3DeviceOps extends Interface {
    public static final Interface.Manager<Camera3DeviceOps, Proxy> MANAGER = Camera3DeviceOps_Internal.MANAGER;

    public static final class BufferType {
        public static final int DMABUF = 0;
        public static final int SHM = 1;

        private BufferType() {
        }

        public static boolean isKnownValue(int i) {
            return i == 0 || i == 1;
        }

        public static void validate(int i) {
            if (!isKnownValue(i)) {
                throw new DeserializationException("Invalid enum value.");
            }
        }
    }

    public interface CloseResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ConfigureStreamsAndGetAllocatedBuffersResponse extends Callbacks.Callback3<Integer, Camera3StreamConfiguration, Map<Long, Camera3StreamBuffer[]>> {
    }

    public interface ConfigureStreamsResponse extends Callbacks.Callback2<Integer, Camera3StreamConfiguration> {
    }

    public interface ConstructDefaultRequestSettingsResponse extends Callbacks.Callback1<CameraMetadata> {
    }

    public interface FlushResponse extends Callbacks.Callback1<Integer> {
    }

    public interface InitializeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ProcessCaptureRequestResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends Camera3DeviceOps, Interface.Proxy {
    }

    public interface RegisterBufferResponse extends Callbacks.Callback1<Integer> {
    }

    void close(CloseResponse closeResponse);

    void configureStreams(Camera3StreamConfiguration camera3StreamConfiguration, ConfigureStreamsResponse configureStreamsResponse);

    void configureStreamsAndGetAllocatedBuffers(Camera3StreamConfiguration camera3StreamConfiguration, ConfigureStreamsAndGetAllocatedBuffersResponse configureStreamsAndGetAllocatedBuffersResponse);

    void constructDefaultRequestSettings(int i, ConstructDefaultRequestSettingsResponse constructDefaultRequestSettingsResponse);

    void dump(UntypedHandle untypedHandle);

    void flush(FlushResponse flushResponse);

    void initialize(Camera3CallbackOps camera3CallbackOps, InitializeResponse initializeResponse);

    void processCaptureRequest(Camera3CaptureRequest camera3CaptureRequest, ProcessCaptureRequestResponse processCaptureRequestResponse);

    void registerBuffer(long j, int i, UntypedHandle[] untypedHandleArr, int i2, int i3, int i4, int i5, int[] iArr, int[] iArr2, RegisterBufferResponse registerBufferResponse);

    void signalStreamFlush(long[] jArr);
}

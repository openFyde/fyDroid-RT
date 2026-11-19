package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.Time;
import org.chromium.mojo_base.mojom.TimeTicks;

public interface TimerHost extends Interface {
    public static final Interface.Manager<TimerHost, Proxy> MANAGER = TimerHost_Internal.MANAGER;

    public interface CreateTimersResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends TimerHost, Interface.Proxy {
    }

    public interface SetTimeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StartTimerResponse extends Callbacks.Callback1<Integer> {
    }

    void createTimers(CreateTimerRequest[] createTimerRequestArr, CreateTimersResponse createTimersResponse);

    void setTime(Time time, SetTimeResponse setTimeResponse);

    void startTimer(int i, TimeTicks timeTicks, StartTimerResponse startTimerResponse);
}

package com.android.server.wm;

import android.content.ClipData;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Pair;
import android.util.Slog;
import android.view.Display;
import android.view.IWindow;
import android.view.IWindowSession;
import android.view.InputChannel;
import android.view.SurfaceControl;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.input.InputManagerService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Drag-and-drop service integrating Android DnD with Chrome OS.
 * Intercepts WMS drag operations via IDragDropCallback and bridges them
 * to/from Chrome OS via ArcDragDropInternal.
 */
public class ArcDragDropService extends SystemService {
    private static final String APPLAUNCHER_PACKAGE = "org.chromium.arc.applauncher";
    private static final int CALLBACK_RESULT_CONTINUE = 0;
    private static final int CALLBACK_RESULT_STOP = 1;
    private static final int CALLBACK_RESULT_CONTINUE_WITHOUT_INTEGRATION = 2;
    private static final List<String> CTS_PACKAGE_NAMES = Arrays.asList(
            "android.server.wm.cts",
            "android.server.wm.dndsourceapp",
            "android.view.cts");
    private static final int FIRST_NOTIFY_MOVE_DELAY_MS = 100;
    private static final String PLACEHOLDER_WINDOW_TAG =
            "org.chromium.arc.applauncher/org.chromium.arc.applauncher.PlaceholderActivity";
    private static final String TAG = "ArcDragDropService";
    private static final int TASK_NOT_FOUND = -1;

    private final AtomicReference<ArcDragDropInternal.DataDevice> mDataDevice =
            new AtomicReference<>();
    private final Delegate mDelegate;
    private DisplayHelper mDisplayHelper = new DisplayHelper();
    private DragOperation mOperation;
    private final Object mWindowLock;

    // --- Delegate: abstracts WMS access for testability ---

    interface Delegate {
        // fydroid-rt (AOSP-13): WindowManagerService.getInstance() was removed.
        // The WMS is a local system_server binder; fetch + cast it (the canonical
        // 12+ pattern, see GameServiceProviderInstanceFactoryImpl).
        static WindowManagerService getWms() {
            return (WindowManagerService) ServiceManager.getService(Context.WINDOW_SERVICE);
        }

        default SurfaceControl.Builder buildSurfaceLocked() {
            return Delegate.getWms()
                    .getDefaultDisplayContentLocked().makeOverlay();
        }

        default Pair<IWindowSession, IWindow> findPlaceholderWindowLocked() {
            for (WindowState ws : Delegate.getWms()
                    .mWindowMap.values()) {
                if (APPLAUNCHER_PACKAGE.equals(ws.getOwningPackage())
                        && PLACEHOLDER_WINDOW_TAG.equals(ws.getWindowTag())) {
                    return new Pair<>(ws.mSession, ws.mClient);
                }
            }
            return null;
        }

        default DisplayContent getDefaultDisplayContentLocked() {
            return Delegate.getWms()
                    .getDefaultDisplayContentLocked();
        }

        default DisplayContent getDisplayContentForTask(int taskId) {
            Task task = Delegate.getWms().mRoot
                    .anyTaskForId(taskId, 0);
            return task != null ? task.getDisplayContent() : null;
        }

        default Handler getHandler() {
            return Delegate.getWms().mH;
        }

        default int getTaskIdForWindowLocked(IWindow window) {
            WindowState ws = Delegate.getWms().mWindowMap
                    .get(window.asBinder());
            if (ws == null || ws.getTask() == null) return TASK_NOT_FOUND;
            return ws.getTask().mTaskId;
        }

        default Object getWindowLock() {
            return Delegate.getWms().mGlobalLock;
        }

        default boolean isCtsWindowLocked(IWindow window) {
            WindowState ws = Delegate.getWms().mWindowMap
                    .get(window.asBinder());
            if (ws == null) return false;
            return CTS_PACKAGE_NAMES.contains(ws.getOwningPackage());
        }
    }

    // --- DragOperation: active drag state machine ---

    interface DragOperation {
        void onCancel();
        void onDrop(ClipData clipData);
        void onMotion(float x, float y);
        int registerInputChannel(DragState state, Display display,
                InputManagerService ims, InputChannel channel);
    }

    // --- DisplayHelper: tracks display for drag events ---

    class DisplayHelper {
        private DisplayContent mCurrentDisplayContent;

        DisplayContent getCurrentDisplayContentOrDefault() {
            return mCurrentDisplayContent != null
                    ? mCurrentDisplayContent
                    : mDelegate.getDefaultDisplayContentLocked();
        }

        void updateCurrentDisplay(DisplayContent dc) {
            mCurrentDisplayContent = dc;
        }
    }

    // --- ArcInitiatedOperation: drag started from an Android app ---

    class ArcInitiatedOperation implements DragOperation {
        private ClipData mClipData;
        private DragState mState;
        private int mTaskId;

        ClipData getClipData() {
            return mClipData;
        }

        boolean prePerformDrag(IWindow window, ClipData clipData) {
            mClipData = clipData;
            mTaskId = mDelegate.getTaskIdForWindowLocked(window);
            mDisplayHelper.updateCurrentDisplay(
                    mDelegate.getDisplayContentForTask(mTaskId));
            return true;
        }

        boolean postPerformDrag() {
            if (mState == null) {
                Slog.e(TAG, "postPerformDrag called without init");
                return false;
            }
            mDelegate.getHandler().postDelayed(() -> {
                synchronized (mWindowLock) {
                    if (mOperation == this && !mState.isClosing()) {
                        notifyMoveLocked(mState, 0.0f, 0.0f);
                    }
                }
            }, FIRST_NOTIFY_MOVE_DELAY_MS);
            return true;
        }

        @Override
        public int registerInputChannel(DragState state, Display display,
                InputManagerService ims, InputChannel channel) {
            Preconditions.checkState(mState == null,
                    "trying to initialize ArcInitiatedOperation twice");
            mState = state;
            if ((state.mFlags & 256) == 0) {
                return CALLBACK_RESULT_CONTINUE_WITHOUT_INTEGRATION;
            }
            if (mTaskId == TASK_NOT_FOUND) {
                Slog.w(TAG, "The drag origin is not a task");
                return CALLBACK_RESULT_CONTINUE_WITHOUT_INTEGRATION;
            }
            ArcDragDropInternal.DataDevice dd = mDataDevice.get();
            if (dd == null || !dd.startDrag(mTaskId)) {
                return CALLBACK_RESULT_STOP;
            }
            state.mTransaction.setMetadata(state.mSurfaceControl,
                    1073741833, 1);
            state.register(display);
            // fydroid-rt (AOSP-13): transferTouchFocus(InputChannel,InputChannel)
            // gained an isDragDrop arg in 12; pass true since this is a drag.
            return ims.transferTouchFocus(channel, state.getInputChannel(), true /* isDragDrop */)
                    ? CALLBACK_RESULT_CONTINUE
                    : CALLBACK_RESULT_STOP;
        }

        @Override
        public void onMotion(float x, float y) {
            Preconditions.checkNotNull(mState,
                    "uninitialized ArcInitiatedOperation");
            notifyMoveLocked(mState, x, y);
        }

        @Override
        public void onDrop(ClipData clipData) {
            Preconditions.checkNotNull(mState,
                    "uninitialized ArcInitiatedOperation");
            notifyDropLocked(mState);
        }

        @Override
        public void onCancel() {
            Pair<IWindowSession, IWindow> placeholder =
                    mDelegate.findPlaceholderWindowLocked();
            if (placeholder == null) {
                Slog.e(TAG, "Cannot find drag and drop placeholder window");
                return;
            }
            IWindowSession session = placeholder.first;
            IWindow window = placeholder.second;
            long ident = Binder.clearCallingIdentity();
            try {
                mState.mToken = window.asBinder();
                session.reportDropResult(window, false);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to invoke reportDropResult", e);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        void onFinishDrag(boolean success) {
            Pair<IWindowSession, IWindow> placeholder =
                    mDelegate.findPlaceholderWindowLocked();
            if (placeholder == null) {
                Slog.e(TAG, "Cannot find drag and drop placeholder window");
                return;
            }
            IWindowSession session = placeholder.first;
            IWindow window = placeholder.second;
            long ident = Binder.clearCallingIdentity();
            try {
                mState.mToken = window.asBinder();
                session.reportDropResult(window, success);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to invoke reportDropResult", e);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    // --- ChromeInitiatedOperation: drag started from Chrome OS ---

    class ChromeInitiatedOperation implements DragOperation {
        private IWindowSession mSession;
        private DragState mState;
        private IBinder mToken;

        boolean onEnter(int taskId, float x, float y, ClipData clipData,
                int uid) {
            Pair<IWindowSession, IWindow> placeholder =
                    mDelegate.findPlaceholderWindowLocked();
            if (placeholder == null) {
                Slog.e(TAG, "Cannot find drag and drop placeholder window");
                return false;
            }
            SurfaceControl surface = mDelegate.buildSurfaceLocked()
                    .setName("drag surface")
                    .setBufferSize(1, 1)
                    .setOpaque(true)
                    .build();
            IWindowSession session = placeholder.first;
            IWindow window = placeholder.second;
            mSession = session;
            long ident = Binder.clearCallingIdentity();
            try {
                mToken = session.performDrag(window, 769, surface, 0,
                        x, y, x, y, clipData);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to invoke performDrag", e);
                Binder.restoreCallingIdentity(ident);
                return false;
            }
            Binder.restoreCallingIdentity(ident);
            if (mToken == null) {
                Slog.e(TAG, "Cannot perform drag from Chrome");
                return false;
            }
            Preconditions.checkNotNull(mState);
            mState.mUid = uid;
            return true;
        }

        void onLeave() {
            long ident = Binder.clearCallingIdentity();
            try {
                mSession.cancelDragAndDrop(mToken, true);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to invoke cancelDragAndDrop", e);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public int registerInputChannel(DragState state, Display display,
                InputManagerService ims, InputChannel channel) {
            Preconditions.checkState(mState == null,
                    "trying to initialize ChromeInitiatedOperation twice");
            mState = state;
            state.register(display);
            return CALLBACK_RESULT_CONTINUE;
        }

        @Override
        public void onMotion(float x, float y) {
            Preconditions.checkNotNull(mState,
                    "uninitialized ChromeInitiatedOperation");
            notifyMoveLocked(mState, x, y);
        }

        @Override
        public void onDrop(ClipData clipData) {
            Preconditions.checkNotNull(mState,
                    "uninitialized ChromeInitiatedOperation");
            mState.mData = clipData;
            mState.mDataDescription =
                    clipData != null ? clipData.getDescription() : null;
            notifyDropLocked(mState);
        }

        @Override
        public void onCancel() {
            Preconditions.checkNotNull(mSession,
                    "WindowSession not initialized");
            long ident = Binder.clearCallingIdentity();
            try {
                mSession.cancelDragAndDrop(mToken, true);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to invoke cancelDragAndDrop", e);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    // --- IDragDropCallback: intercepts WMS drag operations ---

    class Callback implements WindowManagerInternal.IDragDropCallback {
        @Override
        public boolean prePerformDrag(IWindow window, IBinder dragToken,
                int touchSource, float touchX, float touchY,
                float thumbCenterX, float thumbCenterY, ClipData data) {
            synchronized (mWindowLock) {
                if (mOperation instanceof ChromeInitiatedOperation) {
                    return true;
                }
                if (mOperation instanceof ArcInitiatedOperation) {
                    Slog.e(TAG, "previous operation hasn't been completed");
                    return false;
                }
                Preconditions.checkState(mOperation == null);
                if (mDelegate.isCtsWindowLocked(window)) {
                    return true;
                }
                mOperation = new ArcInitiatedOperation();
                boolean ok = ((ArcInitiatedOperation) mOperation)
                        .prePerformDrag(window, data);
                if (!ok) reset();
                return ok;
            }
        }

        @Override
        public void postPerformDrag() {
            synchronized (mWindowLock) {
                if (mOperation instanceof ArcInitiatedOperation) {
                    if (!((ArcInitiatedOperation) mOperation).postPerformDrag()) {
                        reset();
                    }
                }
            }
        }

        @Override
        public boolean registerInputChannel(DragState state, Display display,
                InputManagerService service, InputChannel source) {
            synchronized (mWindowLock) {
                if (mOperation != null) {
                    int result = mOperation.registerInputChannel(
                            state, display, service, source);
                    if (result == CALLBACK_RESULT_CONTINUE) {
                        return true;
                    }
                    if (result == CALLBACK_RESULT_STOP) {
                        reset();
                        return false;
                    }
                    // CALLBACK_RESULT_CONTINUE_WITHOUT_INTEGRATION
                    reset();
                }
                // Fall through to default behavior
                state.register(display);
                return service.transferTouchFocus(source,
                        state.getInputChannel(), true /* isDragDrop */);
            }
        }

        @Override
        public void preReportDropResult(IWindow window, boolean consumed) {
            ArcDragDropInternal.DataDevice dd = mDataDevice.get();
            if (dd != null) {
                dd.notifyDropResult(consumed);
            }
        }
    }

    // --- LocalService: ArcDragDropInternal published via LocalServices ---

    class LocalService implements ArcDragDropInternal {
        @Override
        public ClipData getClipDataFromArc() {
            synchronized (mWindowLock) {
                if (mOperation instanceof ArcInitiatedOperation) {
                    return ((ArcInitiatedOperation) mOperation).getClipData();
                }
                return null;
            }
        }

        @Override
        public void onCancelDrag() {
            synchronized (mWindowLock) {
                if (mOperation == null) return;
                mOperation.onCancel();
                reset();
            }
        }

        @Override
        public void onDrop(ClipData clipData) {
            synchronized (mWindowLock) {
                if (mOperation != null) {
                    mOperation.onDrop(clipData);
                }
                reset();
            }
        }

        @Override
        public void onEnter(int taskId, float x, float y, ClipData clipData,
                int uid) {
            synchronized (mWindowLock) {
                mDisplayHelper.updateCurrentDisplay(
                        mDelegate.getDisplayContentForTask(taskId));
                if (mOperation instanceof ArcInitiatedOperation) {
                    return;
                }
                if (mOperation instanceof ChromeInitiatedOperation) {
                    Slog.e(TAG, "previous operation hasn't been completed");
                    return;
                }
                Preconditions.checkState(mOperation == null);
                mOperation = new ChromeInitiatedOperation();
                if (!((ChromeInitiatedOperation) mOperation)
                        .onEnter(taskId, x, y, clipData, uid)) {
                    reset();
                }
            }
        }

        @Override
        public void onFinishDrag(boolean success) {
            synchronized (mWindowLock) {
                if (mOperation instanceof ArcInitiatedOperation) {
                    ((ArcInitiatedOperation) mOperation).onFinishDrag(success);
                    reset();
                }
            }
        }

        @Override
        public void onLeave() {
            synchronized (mWindowLock) {
                if (mOperation instanceof ChromeInitiatedOperation) {
                    ((ChromeInitiatedOperation) mOperation).onLeave();
                    reset();
                }
                mDisplayHelper.updateCurrentDisplay(null);
            }
        }

        @Override
        public void onMotion(float x, float y) {
            synchronized (mWindowLock) {
                if (mOperation != null) {
                    mOperation.onMotion(x, y);
                }
            }
        }

        @Override
        public void setDataDevice(ArcDragDropInternal.DataDevice dataDevice) {
            Preconditions.checkState(mDataDevice.compareAndSet(null, dataDevice),
                    "data device is already set");
        }
    }

    // --- Constructor and lifecycle ---

    public ArcDragDropService(Context context) {
        this(context, new Delegate() {});
    }

    ArcDragDropService(Context context, Delegate delegate) {
        super(context);
        mDelegate = delegate;
        mWindowLock = delegate.getWindowLock();
    }

    @Override
    public void onStart() {
        LocalServices.addService(ArcDragDropInternal.class, new LocalService());
        LocalServices.getService(WindowManagerInternal.class)
                .registerDragDropControllerCallback(new Callback());
    }

    // --- Internal helpers ---

    private void reset() {
        mOperation = null;
        mDisplayHelper.updateCurrentDisplay(null);
    }

    void notifyDropLocked(DragState state) {
        final DisplayContent dc = mDisplayHelper.getCurrentDisplayContentOrDefault();
        state.mDisplayContent = dc;
        // fydroid-rt (AOSP-13): DragState.notifyDropLocked(x,y) was replaced by
        // reportDropWindowLock(token, x, y) — the drop target is now identified by
        // the touched window's input-channel token (via WMS.mInputToWindowMap),
        // not by re-resolving the point internally. Find the window under the drop
        // point and pass its token; a null token reproduces the old "drop outside a
        // valid window" path (endDragLocked).
        final float x = state.mCurrentX;
        final float y = state.mCurrentY;
        final WindowState touched =
                (dc != null) ? dc.getTouchableWinAtPointLocked(x, y) : null;
        final IBinder token = (touched != null) ? touched.mInputChannelToken : null;
        state.reportDropWindowLock(token, x, y);
    }

    void notifyMoveLocked(DragState state, float x, float y) {
        state.mDisplayContent = mDisplayHelper.getCurrentDisplayContentOrDefault();
        // fydroid-rt (AOSP-13): notifyMoveLocked(x,y) was replaced by
        // updateDragSurfaceLocked(keepHandling, x, y), which moves the drag surface.
        // NOTE: DRAG_LOCATION/DRAG_EXITED dispatch to the target window moved out of
        // WindowManager into the native InputDispatcher in 12+, so a synthetic
        // (Chrome-driven) drag no longer pushes location events from Java. TODO:
        // inject drag motion natively if target-window drag highlighting regresses.
        state.updateDragSurfaceLocked(true /* keepHandling */, x, y);
    }
}

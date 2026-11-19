/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Hand-written drop-in replacement for org.chromium.arc.wayland.IWaylandService
 * from the proprietary com.google.android.chromeos jar.
 *
 * Why hand-written instead of AIDL?
 *   The proprietary IWaylandService has gappy transaction codes (1..6, 8..10,
 *   12..13, 15..16, 19..21, 24..40, 42, 1001..1003): methods that were
 *   removed left holes in the numbering, and the debug methods jump to 1001+.
 *   AIDL assigns codes sequentially in declaration order with no way to
 *   specify a code, so we'd never reproduce the original wire format from
 *   an .aidl source. This file is structured to look as close as possible
 *   to AIDL-generated output so the diff against the proprietary jar is
 *   trivial.
 *
 * Wire compatibility: byte-for-byte identical to the proprietary stub for
 * every method (descriptor, parameter ordering, parcelable wrapping rules,
 * Binder.transact flag values).
 */
package org.chromium.arc.wayland;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.FileDescriptor;

import org.chromium.arc.display.ArcDisplayLayout;

public interface IWaylandService extends IInterface {
    void ackKeyboardKey(int serial, boolean handled) throws RemoteException;

    void cancelVibrateGamepad(int gamepadId) throws RemoteException;

    void clearTaskIdNamespace() throws RemoteException;

    void dbgForceAllDisplaysToRefresh() throws RemoteException;

    void dbgForceInvalidateBufferCache() throws RemoteException;

    void dbgForceWaylandProtocolError() throws RemoteException;

    void dragRecipientEntered() throws RemoteException;

    void dragRecipientExited() throws RemoteException;

    ArcDisplayLayout getDisplayLayout() throws RemoteException;

    void kcmGenerated(String layoutName) throws RemoteException;

    void notifyDropResult(boolean consumed) throws RemoteException;

    void notifyInputBridgePipeOpening(FileDescriptor pipeFd) throws RemoteException;

    void registerOrientationChangeListener(IWaylandOrientationChangeListener listener)
            throws RemoteException;

    void registerWaylandShellHandler(IWaylandShellHandler waylandShell) throws RemoteException;

    void releaseClipboardData(int id) throws RemoteException;

    FileDescriptor requestClipboardData(String mimeType, int id) throws RemoteException;

    FileDescriptor requestDragData(String mimeType, int id) throws RemoteException;

    void requestPointerCapture(boolean enable) throws RemoteException;

    void setClipboard(String[] mimeType) throws RemoteException;

    void setNotificationDisplayId(int displayId) throws RemoteException;

    void setTaskIdTrustedWebAppPlayBillingId(int taskId, String billingId) throws RemoteException;

    void setTracingBufferSize(int enabled) throws RemoteException;

    void setTracingEnabled(int enabled) throws RemoteException;

    void startDrag(int taskId, String[] mimeTypes) throws RemoteException;

    void taskIdCaptionColorChanged(int taskId, int frameColor) throws RemoteException;

    void taskIdCreated(int taskId, String packageName, String SactivityLabel,
            String activityName, int windowSessionId) throws RemoteException;

    void taskIdDestroyed(int taskId) throws RemoteException;

    void taskIdFinishing(int taskId) throws RemoteException;

    void taskIdMovedToFront(int taskId) throws RemoteException;

    void taskIdRequestPin(int taskId, boolean enabled, boolean trusted) throws RemoteException;

    void taskIdSetAutoHideShelf(int taskId, boolean autoHide) throws RemoteException;

    void taskIdSetLayersKept(int taskId) throws RemoteException;

    void taskIdSetOrientationLock(int taskId, int orientationLock) throws RemoteException;

    void taskIdStartMove(int taskId, int x, int y) throws RemoteException;

    void taskIdStartResize(int taskId, int ctrlType, int x, int y) throws RemoteException;

    long translateDisplayId(long displayId) throws RemoteException;

    void vibrateGamepad(int gamepadId, long[] timings, byte[] amplitudes, int repeat)
            throws RemoteException;

    /** No-op default implementation, used by Stub.setDefaultImpl(). */
    public static class Default implements IWaylandService {
        @Override
        public void registerOrientationChangeListener(IWaylandOrientationChangeListener listener)
                throws RemoteException {}

        @Override
        public void taskIdCreated(int taskId, String packageName, String SactivityLabel,
                String activityName, int windowSessionId) throws RemoteException {}

        @Override
        public void taskIdDestroyed(int taskId) throws RemoteException {}

        @Override
        public void taskIdMovedToFront(int taskId) throws RemoteException {}

        @Override
        public void taskIdRequestPin(int taskId, boolean enabled, boolean trusted)
                throws RemoteException {}

        @Override
        public ArcDisplayLayout getDisplayLayout() throws RemoteException {
            return null;
        }

        @Override
        public void clearTaskIdNamespace() throws RemoteException {}

        @Override
        public void taskIdFinishing(int taskId) throws RemoteException {}

        @Override
        public void taskIdSetAutoHideShelf(int taskId, boolean autoHide) throws RemoteException {}

        @Override
        public long translateDisplayId(long displayId) throws RemoteException {
            return 0L;
        }

        @Override
        public void ackKeyboardKey(int serial, boolean handled) throws RemoteException {}

        @Override
        public void taskIdSetLayersKept(int taskId) throws RemoteException {}

        @Override
        public void taskIdStartMove(int taskId, int x, int y) throws RemoteException {}

        @Override
        public void taskIdStartResize(int taskId, int ctrlType, int x, int y)
                throws RemoteException {}

        @Override
        public void taskIdSetOrientationLock(int taskId, int orientationLock)
                throws RemoteException {}

        @Override
        public void taskIdCaptionColorChanged(int taskId, int frameColor) throws RemoteException {}

        @Override
        public void requestPointerCapture(boolean enable) throws RemoteException {}

        @Override
        public void registerWaylandShellHandler(IWaylandShellHandler waylandShell)
                throws RemoteException {}

        @Override
        public void setTracingEnabled(int enabled) throws RemoteException {}

        @Override
        public void setTracingBufferSize(int enabled) throws RemoteException {}

        @Override
        public void setNotificationDisplayId(int displayId) throws RemoteException {}

        @Override
        public void notifyInputBridgePipeOpening(FileDescriptor pipeFd) throws RemoteException {}

        @Override
        public void vibrateGamepad(int gamepadId, long[] timings, byte[] amplitudes, int repeat)
                throws RemoteException {}

        @Override
        public void cancelVibrateGamepad(int gamepadId) throws RemoteException {}

        @Override
        public void startDrag(int taskId, String[] mimeTypes) throws RemoteException {}

        @Override
        public void notifyDropResult(boolean consumed) throws RemoteException {}

        @Override
        public void setClipboard(String[] mimeType) throws RemoteException {}

        @Override
        public void setTaskIdTrustedWebAppPlayBillingId(int taskId, String billingId)
                throws RemoteException {}

        @Override
        public FileDescriptor requestDragData(String mimeType, int id) throws RemoteException {
            return null;
        }

        @Override
        public FileDescriptor requestClipboardData(String mimeType, int id)
                throws RemoteException {
            return null;
        }

        @Override
        public void releaseClipboardData(int id) throws RemoteException {}

        @Override
        public void dragRecipientEntered() throws RemoteException {}

        @Override
        public void dragRecipientExited() throws RemoteException {}

        @Override
        public void kcmGenerated(String layoutName) throws RemoteException {}

        @Override
        public void dbgForceWaylandProtocolError() throws RemoteException {}

        @Override
        public void dbgForceInvalidateBufferCache() throws RemoteException {}

        @Override
        public void dbgForceAllDisplaysToRefresh() throws RemoteException {}

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IWaylandService {
        private static final String DESCRIPTOR = "org.chromium.arc.wayland.IWaylandService";

        // Transaction codes — copied verbatim from the proprietary stub.
        // The numbering has gaps (7, 11, 14, 17, 18, 22, 23, 41): methods
        // that were removed but whose codes were never reused.
        static final int TRANSACTION_registerOrientationChangeListener = 1;
        static final int TRANSACTION_taskIdCreated = 2;
        static final int TRANSACTION_taskIdDestroyed = 3;
        static final int TRANSACTION_taskIdMovedToFront = 4;
        static final int TRANSACTION_taskIdRequestPin = 5;
        static final int TRANSACTION_getDisplayLayout = 6;
        static final int TRANSACTION_clearTaskIdNamespace = 8;
        static final int TRANSACTION_taskIdFinishing = 9;
        static final int TRANSACTION_taskIdSetAutoHideShelf = 10;
        static final int TRANSACTION_translateDisplayId = 12;
        static final int TRANSACTION_ackKeyboardKey = 13;
        static final int TRANSACTION_taskIdSetLayersKept = 15;
        static final int TRANSACTION_taskIdStartMove = 16;
        static final int TRANSACTION_taskIdStartResize = 19;
        static final int TRANSACTION_taskIdSetOrientationLock = 20;
        static final int TRANSACTION_taskIdCaptionColorChanged = 21;
        static final int TRANSACTION_requestPointerCapture = 24;
        static final int TRANSACTION_registerWaylandShellHandler = 25;
        static final int TRANSACTION_setTracingEnabled = 26;
        static final int TRANSACTION_setTracingBufferSize = 27;
        static final int TRANSACTION_setNotificationDisplayId = 28;
        static final int TRANSACTION_notifyInputBridgePipeOpening = 29;
        static final int TRANSACTION_vibrateGamepad = 30;
        static final int TRANSACTION_cancelVibrateGamepad = 31;
        static final int TRANSACTION_startDrag = 32;
        static final int TRANSACTION_notifyDropResult = 33;
        static final int TRANSACTION_setClipboard = 34;
        static final int TRANSACTION_setTaskIdTrustedWebAppPlayBillingId = 35;
        static final int TRANSACTION_requestDragData = 36;
        static final int TRANSACTION_requestClipboardData = 37;
        static final int TRANSACTION_releaseClipboardData = 38;
        static final int TRANSACTION_dragRecipientEntered = 39;
        static final int TRANSACTION_dragRecipientExited = 40;
        static final int TRANSACTION_kcmGenerated = 42;
        static final int TRANSACTION_dbgForceWaylandProtocolError = 1001;
        static final int TRANSACTION_dbgForceInvalidateBufferCache = 1002;
        static final int TRANSACTION_dbgForceAllDisplaysToRefresh = 1003;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWaylandService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWaylandService)) {
                return (IWaylandService) iin;
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case TRANSACTION_registerOrientationChangeListener: {
                    data.enforceInterface(DESCRIPTOR);
                    IWaylandOrientationChangeListener _arg0 =
                            IWaylandOrientationChangeListener.Stub.asInterface(
                                    data.readStrongBinder());
                    registerOrientationChangeListener(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdCreated: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    String _arg1 = data.readString();
                    String _arg2 = data.readString();
                    String _arg3 = data.readString();
                    int _arg4 = data.readInt();
                    taskIdCreated(_arg0, _arg1, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdDestroyed: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    taskIdDestroyed(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdMovedToFront: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    taskIdMovedToFront(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdRequestPin: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    boolean _arg1 = data.readInt() != 0;
                    boolean _arg2 = data.readInt() != 0;
                    taskIdRequestPin(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getDisplayLayout: {
                    data.enforceInterface(DESCRIPTOR);
                    ArcDisplayLayout _result = getDisplayLayout();
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, Parcelable_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_clearTaskIdNamespace: {
                    data.enforceInterface(DESCRIPTOR);
                    clearTaskIdNamespace();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdFinishing: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    taskIdFinishing(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdSetAutoHideShelf: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    boolean _arg1 = data.readInt() != 0;
                    taskIdSetAutoHideShelf(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_translateDisplayId: {
                    data.enforceInterface(DESCRIPTOR);
                    long _arg0 = data.readLong();
                    long _result = translateDisplayId(_arg0);
                    reply.writeNoException();
                    reply.writeLong(_result);
                    return true;
                }
                case TRANSACTION_ackKeyboardKey: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    boolean _arg1 = data.readInt() != 0;
                    ackKeyboardKey(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdSetLayersKept: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    taskIdSetLayersKept(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdStartMove: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    int _arg2 = data.readInt();
                    taskIdStartMove(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdStartResize: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    taskIdStartResize(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdSetOrientationLock: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    taskIdSetOrientationLock(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_taskIdCaptionColorChanged: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    taskIdCaptionColorChanged(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_requestPointerCapture: {
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg0 = data.readInt() != 0;
                    requestPointerCapture(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_registerWaylandShellHandler: {
                    data.enforceInterface(DESCRIPTOR);
                    IWaylandShellHandler _arg0 =
                            IWaylandShellHandler.Stub.asInterface(data.readStrongBinder());
                    registerWaylandShellHandler(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setTracingEnabled: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    setTracingEnabled(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setTracingBufferSize: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    setTracingBufferSize(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setNotificationDisplayId: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    setNotificationDisplayId(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_notifyInputBridgePipeOpening: {
                    data.enforceInterface(DESCRIPTOR);
                    FileDescriptor _arg0 = data.readRawFileDescriptor();
                    notifyInputBridgePipeOpening(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_vibrateGamepad: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    long[] _arg1 = data.createLongArray();
                    byte[] _arg2 = data.createByteArray();
                    int _arg3 = data.readInt();
                    vibrateGamepad(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_cancelVibrateGamepad: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    cancelVibrateGamepad(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_startDrag: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    String[] _arg1 = data.createStringArray();
                    startDrag(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_notifyDropResult: {
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg0 = data.readInt() != 0;
                    notifyDropResult(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setClipboard: {
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg0 = data.createStringArray();
                    setClipboard(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setTaskIdTrustedWebAppPlayBillingId: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    String _arg1 = data.readString();
                    setTaskIdTrustedWebAppPlayBillingId(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_requestDragData: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    int _arg1 = data.readInt();
                    FileDescriptor _result = requestDragData(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeRawFileDescriptor(_result);
                    return true;
                }
                case TRANSACTION_requestClipboardData: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    int _arg1 = data.readInt();
                    FileDescriptor _result = requestClipboardData(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeRawFileDescriptor(_result);
                    return true;
                }
                case TRANSACTION_releaseClipboardData: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    releaseClipboardData(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dragRecipientEntered: {
                    data.enforceInterface(DESCRIPTOR);
                    dragRecipientEntered();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dragRecipientExited: {
                    data.enforceInterface(DESCRIPTOR);
                    dragRecipientExited();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_kcmGenerated: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    kcmGenerated(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dbgForceWaylandProtocolError: {
                    data.enforceInterface(DESCRIPTOR);
                    dbgForceWaylandProtocolError();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dbgForceInvalidateBufferCache: {
                    data.enforceInterface(DESCRIPTOR);
                    dbgForceInvalidateBufferCache();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dbgForceAllDisplaysToRefresh: {
                    data.enforceInterface(DESCRIPTOR);
                    dbgForceAllDisplaysToRefresh();
                    reply.writeNoException();
                    return true;
                }
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        // Same numeric value as android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE.
        // Inlined here to avoid pulling in the constant via reflection at boot.
        private static final int Parcelable_WRITE_RETURN_VALUE = 1;

        private static class Proxy implements IWaylandService {
            public static IWaylandService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public void registerOrientationChangeListener(
                    IWaylandOrientationChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = mRemote.transact(
                            TRANSACTION_registerOrientationChangeListener, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().registerOrientationChangeListener(listener);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdCreated(int taskId, String packageName, String SactivityLabel,
                    String activityName, int windowSessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeString(packageName);
                    _data.writeString(SactivityLabel);
                    _data.writeString(activityName);
                    _data.writeInt(windowSessionId);
                    boolean _status = mRemote.transact(TRANSACTION_taskIdCreated, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdCreated(taskId, packageName, SactivityLabel,
                                activityName, windowSessionId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdDestroyed(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdDestroyed, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdDestroyed(taskId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdMovedToFront(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdMovedToFront, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdMovedToFront(taskId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdRequestPin(int taskId, boolean enabled, boolean trusted)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(enabled ? 1 : 0);
                    _data.writeInt(trusted ? 1 : 0);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdRequestPin, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdRequestPin(taskId, enabled, trusted);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public ArcDisplayLayout getDisplayLayout() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status =
                            mRemote.transact(TRANSACTION_getDisplayLayout, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getDisplayLayout();
                    }
                    _reply.readException();
                    ArcDisplayLayout _result;
                    if (_reply.readInt() != 0) {
                        _result = ArcDisplayLayout.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void clearTaskIdNamespace() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status =
                            mRemote.transact(TRANSACTION_clearTaskIdNamespace, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().clearTaskIdNamespace();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdFinishing(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdFinishing, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdFinishing(taskId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdSetAutoHideShelf(int taskId, boolean autoHide)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(autoHide ? 1 : 0);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdSetAutoHideShelf, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdSetAutoHideShelf(taskId, autoHide);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public long translateDisplayId(long displayId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(displayId);
                    boolean _status =
                            mRemote.transact(TRANSACTION_translateDisplayId, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().translateDisplayId(displayId);
                    }
                    _reply.readException();
                    return _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void ackKeyboardKey(int serial, boolean handled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(handled ? 1 : 0);
                    boolean _status =
                            mRemote.transact(TRANSACTION_ackKeyboardKey, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().ackKeyboardKey(serial, handled);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdSetLayersKept(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdSetLayersKept, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdSetLayersKept(taskId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdStartMove(int taskId, int x, int y) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdStartMove, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdStartMove(taskId, x, y);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdStartResize(int taskId, int ctrlType, int x, int y)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(ctrlType);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    boolean _status =
                            mRemote.transact(TRANSACTION_taskIdStartResize, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdStartResize(taskId, ctrlType, x, y);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdSetOrientationLock(int taskId, int orientationLock)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(orientationLock);
                    boolean _status = mRemote.transact(
                            TRANSACTION_taskIdSetOrientationLock, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdSetOrientationLock(taskId, orientationLock);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void taskIdCaptionColorChanged(int taskId, int frameColor)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(frameColor);
                    boolean _status = mRemote.transact(
                            TRANSACTION_taskIdCaptionColorChanged, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().taskIdCaptionColorChanged(taskId, frameColor);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void requestPointerCapture(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    boolean _status =
                            mRemote.transact(TRANSACTION_requestPointerCapture, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().requestPointerCapture(enable);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void registerWaylandShellHandler(IWaylandShellHandler waylandShell)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder(waylandShell != null ? waylandShell.asBinder() : null);
                    boolean _status = mRemote.transact(
                            TRANSACTION_registerWaylandShellHandler, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().registerWaylandShellHandler(waylandShell);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setTracingEnabled(int enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(enabled);
                    boolean _status =
                            mRemote.transact(TRANSACTION_setTracingEnabled, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().setTracingEnabled(enabled);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setTracingBufferSize(int enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(enabled);
                    boolean _status =
                            mRemote.transact(TRANSACTION_setTracingBufferSize, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().setTracingBufferSize(enabled);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setNotificationDisplayId(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(displayId);
                    boolean _status = mRemote.transact(
                            TRANSACTION_setNotificationDisplayId, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().setNotificationDisplayId(displayId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void notifyInputBridgePipeOpening(FileDescriptor pipeFd)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeRawFileDescriptor(pipeFd);
                    boolean _status = mRemote.transact(
                            TRANSACTION_notifyInputBridgePipeOpening, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().notifyInputBridgePipeOpening(pipeFd);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void vibrateGamepad(int gamepadId, long[] timings, byte[] amplitudes,
                    int repeat) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(gamepadId);
                    _data.writeLongArray(timings);
                    _data.writeByteArray(amplitudes);
                    _data.writeInt(repeat);
                    boolean _status =
                            mRemote.transact(TRANSACTION_vibrateGamepad, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().vibrateGamepad(gamepadId, timings, amplitudes, repeat);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void cancelVibrateGamepad(int gamepadId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(gamepadId);
                    boolean _status =
                            mRemote.transact(TRANSACTION_cancelVibrateGamepad, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().cancelVibrateGamepad(gamepadId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void startDrag(int taskId, String[] mimeTypes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeStringArray(mimeTypes);
                    boolean _status = mRemote.transact(TRANSACTION_startDrag, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().startDrag(taskId, mimeTypes);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void notifyDropResult(boolean consumed) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(consumed ? 1 : 0);
                    boolean _status =
                            mRemote.transact(TRANSACTION_notifyDropResult, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().notifyDropResult(consumed);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setClipboard(String[] mimeType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStringArray(mimeType);
                    boolean _status =
                            mRemote.transact(TRANSACTION_setClipboard, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().setClipboard(mimeType);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setTaskIdTrustedWebAppPlayBillingId(int taskId, String billingId)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeString(billingId);
                    boolean _status = mRemote.transact(
                            TRANSACTION_setTaskIdTrustedWebAppPlayBillingId, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().setTaskIdTrustedWebAppPlayBillingId(taskId, billingId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public FileDescriptor requestDragData(String mimeType, int id)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(mimeType);
                    _data.writeInt(id);
                    boolean _status =
                            mRemote.transact(TRANSACTION_requestDragData, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().requestDragData(mimeType, id);
                    }
                    _reply.readException();
                    return _reply.readRawFileDescriptor();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public FileDescriptor requestClipboardData(String mimeType, int id)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(mimeType);
                    _data.writeInt(id);
                    boolean _status =
                            mRemote.transact(TRANSACTION_requestClipboardData, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().requestClipboardData(mimeType, id);
                    }
                    _reply.readException();
                    return _reply.readRawFileDescriptor();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void releaseClipboardData(int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(id);
                    boolean _status =
                            mRemote.transact(TRANSACTION_releaseClipboardData, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().releaseClipboardData(id);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void dragRecipientEntered() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status =
                            mRemote.transact(TRANSACTION_dragRecipientEntered, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().dragRecipientEntered();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void dragRecipientExited() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status =
                            mRemote.transact(TRANSACTION_dragRecipientExited, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().dragRecipientExited();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void kcmGenerated(String layoutName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(layoutName);
                    boolean _status =
                            mRemote.transact(TRANSACTION_kcmGenerated, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().kcmGenerated(layoutName);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void dbgForceWaylandProtocolError() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(
                            TRANSACTION_dbgForceWaylandProtocolError, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().dbgForceWaylandProtocolError();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void dbgForceInvalidateBufferCache() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(
                            TRANSACTION_dbgForceInvalidateBufferCache, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().dbgForceInvalidateBufferCache();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void dbgForceAllDisplaysToRefresh() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(
                            TRANSACTION_dbgForceAllDisplaysToRefresh, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().dbgForceAllDisplaysToRefresh();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IWaylandService impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IWaylandService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}

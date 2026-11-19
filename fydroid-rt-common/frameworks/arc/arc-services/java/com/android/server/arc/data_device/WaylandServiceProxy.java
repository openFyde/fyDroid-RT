package com.android.server.arc.data_device;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.FileDescriptor;

/**
 * Java proxy for the Wayland binder service (org.chromium.arc.wayland.IWaylandService)
 * for data device operations (clipboard and drag-and-drop data transfer).
 * Transaction codes match the proprietary ARC++ runtime.
 */
class WaylandServiceProxy {
    private static final String DESCRIPTOR = "org.chromium.arc.wayland.IWaylandService";

    // Transaction codes for data device operations
    private static final int TRANSACTION_startDrag = 32;
    private static final int TRANSACTION_notifyDropResult = 33;
    private static final int TRANSACTION_setClipboardMimes = 34;
    private static final int TRANSACTION_receiveDragData = 36;
    private static final int TRANSACTION_receiveClipboardData = 37;
    private static final int TRANSACTION_releaseDataOffer = 38;
    private static final int TRANSACTION_dragRecipientEntered = 39;
    private static final int TRANSACTION_dragRecipientExited = 40;

    private final IBinder mRemote;

    WaylandServiceProxy(IBinder remote) {
        mRemote = remote;
    }

    static WaylandServiceProxy fromBinder(IBinder binder) {
        if (binder == null) return null;
        return new WaylandServiceProxy(binder);
    }

    void startDrag(int taskId, String[] mimeTypes) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(taskId);
            data.writeStringArray(mimeTypes);
            mRemote.transact(TRANSACTION_startDrag, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    void notifyDropResult(boolean consumed) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(consumed ? 1 : 0);
            mRemote.transact(TRANSACTION_notifyDropResult, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    void setClipboardMimes(String[] mimeTypes) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeStringArray(mimeTypes);
            mRemote.transact(TRANSACTION_setClipboardMimes, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    FileDescriptor receiveDragData(String mimeType, int offerId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeString(mimeType);
            data.writeInt(offerId);
            mRemote.transact(TRANSACTION_receiveDragData, data, reply, 0);
            reply.readException();
            return reply.readRawFileDescriptor();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    FileDescriptor receiveClipboardData(String mimeType, int offerId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeString(mimeType);
            data.writeInt(offerId);
            mRemote.transact(TRANSACTION_receiveClipboardData, data, reply, 0);
            reply.readException();
            return reply.readRawFileDescriptor();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    void releaseDataOffer(int offerId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(offerId);
            mRemote.transact(TRANSACTION_releaseDataOffer, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    void dragRecipientEntered() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mRemote.transact(TRANSACTION_dragRecipientEntered, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    void dragRecipientExited() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            mRemote.transact(TRANSACTION_dragRecipientExited, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }
}

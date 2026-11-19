package com.android.server.arc.data_device;

import android.app.IUriGrantsManager;
import android.app.UriGrantsManager;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SharedMemory;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.clipboard.ArcClipboardInternal;
import com.android.server.uri.UriGrantsManagerInternal;
import com.android.server.wm.ArcDragDropInternal;
import java.io.FileDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import libcore.io.IoUtils;

/**
 * Data device service handling clipboard and drag-and-drop data transfer
 * between Chrome OS and Android via Wayland data_device protocol.
 *
 * Publishes two binder services:
 * - "arc_data_device_service" (IArcDataDeviceService): receives drag/clipboard events from Wayland
 * - "arc_data_device_provider_service" (IArcDataDeviceProviderService): image data access for
 *   ContentProvider
 *
 * Also creates a DataDevice that implements both ArcDragDropInternal.DataDevice and
 * ArcClipboardInternal.DataDevice, bridging Android drag/clipboard operations to Chrome OS
 * via the Wayland binder service.
 */
public final class ArcDataDeviceService extends SystemService {
    private static final String ARC_DOCUMENTS_UI_PACKAGE_NAME = "com.android.documentsui";
    private static final String ARC_INPUT_METHOD_SERVICE_PACKAGE_NAME = "org.chromium.arc.ime";
    private static final String BINDER_SERVICE_NAME = "arc_data_device_service";
    private static final String DATA_DEVICE_PROVIDER_PACKAGE_NAME = "org.chromium.arc.data_device";
    private static final int DRAG_IMAGE_TIMEOUT_MILLIS = 20000;
    private static final int FILE_PROVIDER_ACCESS_TIMEOUT_MILLIS = 5000;
    private static final String NOTIFY_CONTENT_PROVIDER_ACCESS_PERMISSION =
            "org.chromium.arc.data_device.NOTIFY_CONTENT_PROVIDER_ACCESS";
    private static final String PROVIDER_BINDER_SERVICE_NAME = "arc_data_device_provider_service";
    private static final String TAG = "ArcDataDeviceService";
    private static final String WAYLAND_SERVICE_NAME = "Wayland";

    private final ClipDataConverter mClipDataConverter;
    private final AtomicReference<ClipData> mClipboardDataFromArc = new AtomicReference<>();
    private final AtomicReference<ClipDataConverter.ImageData> mClipboardImageData =
            new AtomicReference<>();
    private final AtomicReference<ClipDataConverter.ImageData> mDragImageData =
            new AtomicReference<>();
    private final AtomicReference<Runnable> mDragImageTimeout = new AtomicReference<>();
    private final AtomicReference<List<Uri>> mDroppedImageUris = new AtomicReference<>();
    private final Handler mHandler;
    private final RemoteCallbackList<IImageDropListener> mImageDropListeners =
            new RemoteCallbackList<>();

    public ArcDataDeviceService(Context context) {
        super(context);
        mHandler = new Handler(Looper.getMainLooper());
        mClipDataConverter = new ClipDataConverter(context, mHandler);
    }

    @Override
    public void onStart() {
        ArcDragDropInternal dragDrop =
                (ArcDragDropInternal) LocalServices.getService(ArcDragDropInternal.class);
        if (dragDrop == null) {
            Slog.w(TAG, "ArcDragDropInternal not available, publishing placeholder binders");
            publishBinderService(BINDER_SERVICE_NAME, new Binder());
            publishBinderService(PROVIDER_BINDER_SERVICE_NAME, new Binder());
            return;
        }

        WaylandServiceProxy wayland = WaylandServiceProxy.fromBinder(
                ServiceManager.getService(WAYLAND_SERVICE_NAME));
        if (wayland == null) {
            Slog.w(TAG, "Wayland service not available, publishing placeholder binders");
            publishBinderService(BINDER_SERVICE_NAME, new Binder());
            publishBinderService(PROVIDER_BINDER_SERVICE_NAME, new Binder());
            return;
        }

        DataDevice dataDevice = new DataDevice(wayland, dragDrop);
        dragDrop.setDataDevice(dataDevice);

        ArcClipboardInternal clipboard =
                (ArcClipboardInternal) LocalServices.getService(ArcClipboardInternal.class);
        if (clipboard != null) {
            clipboard.setDataDevice(dataDevice);
        } else {
            Slog.w(TAG, "ArcClipboardInternal not available, clipboard bridging disabled");
        }

        publishBinderService(BINDER_SERVICE_NAME,
                new BinderService(wayland, dragDrop, clipboard));
        publishBinderService(PROVIDER_BINDER_SERVICE_NAME, new ProviderBinderService());
        Slog.i(TAG, "Started with Wayland and DragDrop services");
    }

    // --- Helpers ---

    int getBinderCallingUid() {
        return Binder.getCallingUid();
    }

    int getPackageUid(String packageName) {
        try {
            return getContext().getPackageManager().getPackageUid(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.w(TAG, "cannot get package uid for " + packageName);
            return -1;
        }
    }

    int getPackageUidFromUri(Uri uri) {
        String authority = uri.getAuthority();
        if (ClipDataConverter.FILE_PROVIDER_AUTHORITY.equals(authority)
                || ClipDataConverter.VOLUME_PROVIDER_AUTHORITY.equals(authority)
                || ClipDataConverter.CHROME_CONTENT_PROVIDER_AUTHORITY.equals(authority)) {
            return getPackageUid(ARC_DOCUMENTS_UI_PACKAGE_NAME);
        }
        if (ClipDataConverter.DATA_DEVICE_PROVIDER_AUTHORITY.equals(authority)) {
            return getPackageUid(DATA_DEVICE_PROVIDER_PACKAGE_NAME);
        }
        return -1;
    }

    int getProperUidForClipData(ClipData clipData) {
        if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                if (uri != null) {
                    int uid = getPackageUidFromUri(uri);
                    if (uid != -1) return uid;
                }
            }
        }
        return getPackageUid(DATA_DEVICE_PROVIDER_PACKAGE_NAME);
    }

    IUriGrantsManager getUriGrantsManagerService() {
        return UriGrantsManager.getService();
    }

    IBinder newUriPermissionOwner() {
        return ((UriGrantsManagerInternal) LocalServices.getService(
                UriGrantsManagerInternal.class)).newUriPermissionOwner("data_device_service");
    }

    // --- BinderService (IArcDataDeviceService) ---

    class BinderService extends IArcDataDeviceService.Stub {
        private final ArcClipboardInternal mClipboardService;
        private final ArcDragDropInternal mDragDropService;
        private final WaylandServiceProxy mWaylandService;

        BinderService(WaylandServiceProxy wayland, ArcDragDropInternal dragDrop,
                ArcClipboardInternal clipboard) {
            mWaylandService = wayland;
            mDragDropService = dragDrop;
            mClipboardService = clipboard;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            try {
                return super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                Slog.e(TAG, "ArcDataDeviceService crash", e);
                throw e;
            }
        }

        @Override
        public void onEnter(int taskId, float x, float y, String[] mimeTypes, int offerId) {
            ClipData placeholder = mClipDataConverter.createPlaceholderClipData(mimeTypes,
                    new DragDataOffer(mWaylandService, offerId));
            mDragDropService.onEnter(taskId, x, y, placeholder,
                    getProperUidForClipData(placeholder));
        }

        @Override
        public void onLeave() {
            mDragDropService.onLeave();
        }

        @Override
        public void onMotion(float x, float y) {
            mDragDropService.onMotion(x, y);
        }

        @Override
        public void onDrop(String[] mimeTypes, int offerId) {
            ClipDataConverter.Result result = mClipDataConverter.createClipData(mimeTypes,
                    new DragDataOffer(mWaylandService, offerId));
            ClipData clipData = result != null ? result.getClipData() : null;

            // Cancel any previous drag image timeout
            Runnable oldTimeout = mDragImageTimeout.getAndSet(null);
            if (oldTimeout != null) {
                mHandler.removeCallbacks(oldTimeout);
            }

            // Update drag image data
            ClipDataConverter.ImageData imageData =
                    result != null ? result.getImageData() : null;
            IoUtils.closeQuietly(mDragImageData.getAndSet(imageData));
            if (imageData != null) {
                Runnable timeout = () -> {
                    Slog.w(TAG, "Drag image data removed from ContentProvider due to timeout. "
                            + "Apps are encouraged to save the dragged image right after "
                            + "the ContentProvider URI becomes available.");
                    IoUtils.closeQuietly(mDragImageData.getAndSet(null));
                };
                mDragImageTimeout.set(timeout);
                mHandler.postDelayed(timeout, DRAG_IMAGE_TIMEOUT_MILLIS);
            }

            mDroppedImageUris.set(result != null ? result.getImageUris(getContext()) : null);
            mDragDropService.onDrop(clipData);
        }

        @Override
        public void onRequestDragData(String mimeType, FileDescriptor fd) {
            try {
                ClipData clipData = mDragDropService.getClipDataFromArc();
                if (clipData == null) {
                    Slog.e(TAG, "No clip data available from ARC");
                } else {
                    mClipDataConverter.writeClipData(clipData, mimeType, fd);
                }
            } finally {
                IoUtils.closeQuietly(fd);
            }
        }

        @Override
        public void onFinishDrag(boolean success) {
            mDragDropService.onFinishDrag(success);
        }

        @Override
        public void onClipboardUpdate(String[] mimeTypes, int offerId) {
            if (mClipboardService == null) {
                Slog.w(TAG, "onClipboardUpdate: clipboard service not available");
                return;
            }
            ClipDataConverter.Result result = mClipDataConverter.createClipData(mimeTypes,
                    new ClipboardDataOffer(mWaylandService, offerId));
            ClipData clipData = result != null ? result.getClipData() : null;
            IoUtils.closeQuietly(mClipboardImageData.getAndSet(
                    result != null ? result.getImageData() : null));
            mClipboardService.onClipboardUpdate(clipData, getProperUidForClipData(clipData));
        }

        @Override
        public void onRequestClipboardData(String mimeType, FileDescriptor fd) {
            try {
                ClipData clipData = mClipboardDataFromArc.get();
                if (clipData == null) {
                    Slog.e(TAG, "No clip data available from ARC");
                } else {
                    mClipDataConverter.writeClipData(clipData, mimeType, fd);
                }
            } finally {
                IoUtils.closeQuietly(fd);
            }
        }

        @Override
        public void onCancelDrag() {
            mDragDropService.onCancelDrag();
        }
    }

    // --- DataDevice (bridges to Wayland) ---

    class DataDevice implements ArcDragDropInternal.DataDevice, ArcClipboardInternal.DataDevice {
        private final ArcDragDropInternal mDragDropService;
        private final IBinder mPermissionOwner;
        private final IUriGrantsManager mUgm;
        private final WaylandServiceProxy mWaylandService;

        DataDevice(WaylandServiceProxy wayland, ArcDragDropInternal dragDrop) {
            mPermissionOwner = newUriPermissionOwner();
            mUgm = getUriGrantsManagerService();
            mWaylandService = wayland;
            mDragDropService = dragDrop;
        }

        @Override
        public void dragRecipientEntered() {
            try {
                mWaylandService.dragRecipientEntered();
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to notify Wayland service it entered a view accepting drop");
            }
        }

        @Override
        public void dragRecipientExited() {
            try {
                mWaylandService.dragRecipientExited();
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to notify Wayland service it exited a view accepting drop");
            }
        }

        @Override
        public void notifyDropResult(boolean consumed) {
            try {
                mWaylandService.notifyDropResult(consumed);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to notify Wayland service about the drop result");
            }
            List<Uri> uris = mDroppedImageUris.getAndSet(null);
            if (consumed && uris != null) {
                notifyImageDropListeners(uris);
            }
        }

        @Override
        public boolean startDrag(int taskId) {
            try {
                mWaylandService.startDrag(taskId,
                        ClipDataConverter.getSupportedMimeTypes(
                                mDragDropService.getClipDataFromArc()));
                return true;
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to notify Wayland service to start drag");
                return false;
            }
        }

        @Override
        public void setClipboard(ClipData clipData) {
            mClipboardDataFromArc.set(clipData);
            try {
                mWaylandService.setClipboardMimes(
                        ClipDataConverter.getSupportedMimeTypes(clipData));
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to notify Wayland service to notify clipboard change");
            }
        }

        private void notifyImageDropListeners(List<Uri> uris) {
            // Grant URI permissions to ArcInputMethodService
            for (Uri uri : uris) {
                int sourceUid = getPackageUidFromUri(uri);
                try {
                    mUgm.grantUriPermissionFromOwner(mPermissionOwner, sourceUid,
                            ARC_INPUT_METHOD_SERVICE_PACKAGE_NAME, uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                            UserHandle.getUserId(sourceUid),
                            UserHandle.getUserId(getPackageUid(
                                    ARC_INPUT_METHOD_SERVICE_PACKAGE_NAME)));
                } catch (RemoteException e) {
                    Slog.e(TAG, "Failed to call grantUriPermissionFromOwner", e);
                    return;
                }
            }

            // Revoke after timeout
            mHandler.postDelayed(() -> {
                for (Uri uri : uris) {
                    ((UriGrantsManagerInternal) LocalServices.getService(
                            UriGrantsManagerInternal.class))
                            .revokeUriPermissionFromOwner(mPermissionOwner, uri,
                                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                                    UserHandle.getUserId(getPackageUidFromUri(uri)));
                }
            }, DRAG_IMAGE_TIMEOUT_MILLIS);

            // Broadcast to listeners
            String[] uriStrings = uris.stream().map(Uri::toString).toArray(String[]::new);
            int count = mImageDropListeners.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    mImageDropListeners.getBroadcastItem(i).onImageDrop(uriStrings);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to call onImageDrop", e);
                }
            }
            mImageDropListeners.finishBroadcast();
        }
    }

    // --- DataOffer delegates ---

    class ClipboardDataOffer implements ClipDataConverter.DataOfferDelegate {
        private final int mId;
        private final WaylandServiceProxy mWaylandService;

        ClipboardDataOffer(WaylandServiceProxy wayland, int id) {
            mWaylandService = wayland;
            mId = id;
        }

        @Override
        public void close() {
            try {
                mWaylandService.releaseDataOffer(mId);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to release clipboard / drag data", e);
            }
        }

        @Override
        public FileDescriptor receive(String mimeType) throws RemoteException {
            return mWaylandService.receiveClipboardData(mimeType, mId);
        }
    }

    class DragDataOffer implements ClipDataConverter.DataOfferDelegate {
        private final int mId;
        private final WaylandServiceProxy mWaylandService;

        DragDataOffer(WaylandServiceProxy wayland, int id) {
            mWaylandService = wayland;
            mId = id;
        }

        @Override
        public void close() {
            // Drag data offers are not released (owned by the Wayland service)
        }

        @Override
        public FileDescriptor receive(String mimeType) throws RemoteException {
            return mWaylandService.receiveDragData(mimeType, mId);
        }
    }

    // --- ProviderBinderService (IArcDataDeviceProviderService) ---

    class ProviderBinderService extends IArcDataDeviceProviderService.Stub {
        final Set<Pair<String, String>> mContentProviderClients =
                Collections.synchronizedSet(new ArraySet<>());

        private ClipDataConverter.ImageData getImageData(String uri) {
            ClipDataConverter.ImageData clipboard = mClipboardImageData.get();
            ClipDataConverter.ImageData drag = mDragImageData.get();
            if (clipboard == null && drag == null) {
                Slog.e(TAG, "no image data available");
                return null;
            }
            if (clipboard != null && clipboard.getUri().equals(uri)) {
                return clipboard;
            }
            if (drag != null && drag.getUri().equals(uri)) {
                return drag;
            }
            Slog.e(TAG, "invalid ContentProvider uri " + uri);
            return null;
        }

        @Override
        public SharedMemory getDataForContentProvider(String uri, String callingPackage) {
            if (getBinderCallingUid() != getPackageUid(DATA_DEVICE_PROVIDER_PACKAGE_NAME)) {
                throw new SecurityException(
                        "only ArcDataDeviceProvider is allowed to call this method");
            }
            ClipDataConverter.ImageData imageData = getImageData(uri);
            if (imageData == null) return null;
            imageData.addAccessingPackage(callingPackage);
            return imageData.getSharedMemory();
        }

        @Override
        public String getMimeTypeForContentProvider(String uri) {
            if (getBinderCallingUid() != getPackageUid(DATA_DEVICE_PROVIDER_PACKAGE_NAME)) {
                throw new SecurityException(
                        "only ArcDataDeviceProvider is allowed to call this method");
            }
            ClipDataConverter.ImageData imageData = getImageData(uri);
            return imageData != null ? imageData.getMimeType() : null;
        }

        @Override
        public boolean isUriAccessedBy(String uri, String packageName) {
            if (getBinderCallingUid() != getPackageUid(ARC_INPUT_METHOD_SERVICE_PACKAGE_NAME)) {
                throw new SecurityException(
                        "only ArcInputMethodService is allowed to call this method");
            }
            if (mContentProviderClients.contains(new Pair<>(uri, packageName))) {
                return true;
            }
            ClipDataConverter.ImageData imageData = getImageData(uri);
            return imageData != null && imageData.isAccessedBy(packageName);
        }

        @Override
        public void addImageDropListener(IImageDropListener listener) {
            if (getBinderCallingUid() != getPackageUid(ARC_INPUT_METHOD_SERVICE_PACKAGE_NAME)) {
                throw new SecurityException(
                        "only ArcInputMethodService is allowed to call this method");
            }
            mImageDropListeners.register(listener);
        }

        @Override
        public void removeImageDropListener(IImageDropListener listener) {
            if (getBinderCallingUid() != getPackageUid(ARC_INPUT_METHOD_SERVICE_PACKAGE_NAME)) {
                throw new SecurityException(
                        "only ArcInputMethodService is allowed to call this method");
            }
            mImageDropListeners.unregister(listener);
        }

        @Override
        public void notifyContentProviderAccess(String uri, int uid) {
            if (getContext().checkCallingPermission(
                    NOTIFY_CONTENT_PROVIDER_ACCESS_PERMISSION) != 0) {
                throw new SecurityException(
                        "only allowed ContentProviders can call this method");
            }
            String authority = Uri.parse(uri).getAuthority();
            if (!ClipDataConverter.FILE_PROVIDER_AUTHORITY.equals(authority)
                    && !ClipDataConverter.VOLUME_PROVIDER_AUTHORITY.equals(authority)
                    && !ClipDataConverter.CHROME_CONTENT_PROVIDER_AUTHORITY.equals(authority)) {
                throw new SecurityException(
                        "The URI does not have the authority of an allowed ContentProvider");
            }
            String packageName = getContext().getPackageManager().getNameForUid(uid);
            Pair<String, String> entry = new Pair<>(uri, packageName);
            mContentProviderClients.add(entry);
            mHandler.postDelayed(() -> mContentProviderClients.remove(entry),
                    FILE_PROVIDER_ACCESS_TIMEOUT_MILLIS);
        }
    }
}

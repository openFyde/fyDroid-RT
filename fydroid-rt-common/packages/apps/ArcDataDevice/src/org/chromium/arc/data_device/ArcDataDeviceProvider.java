/*
 * Copyright (C) 2024 The FydeOS Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chromium.arc.data_device;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SharedMemory;
import android.provider.OpenableColumns;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.util.Log;

import com.android.server.arc.data_device.IArcDataDeviceProviderService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ContentProvider wrapping the arc_data_device_provider_service binder.
 * Provides clipboard/drag image data to apps via content:// URIs.
 */
public class ArcDataDeviceProvider extends ContentProvider {
    private static final String TAG = "ArcDataDeviceProvider";

    private final AtomicReference<IArcDataDeviceProviderService> mDataDeviceProviderService =
            new AtomicReference<>();

    @Override
    public boolean onCreate() {
        mDataDeviceProviderService.set(IArcDataDeviceProviderService.Stub.asInterface(
                ServiceManager.getService("arc_data_device_provider_service")));
        return true;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        SharedMemory srcMemory;
        try {
            srcMemory = mDataDeviceProviderService.get().getDataForContentProvider(
                    uri.toString(),
                    getContext().getPackageManager().getNameForUid(Binder.getCallingUid()));
        } catch (RemoteException e) {
            throw new FileNotFoundException("failed to get the data for content provider");
        }

        if (srcMemory == null) {
            Log.w(TAG, "openAssetFile " + uri + ": no data");
            throw new FileNotFoundException("failed to get data for content provider");
        }

        try (SharedMemory src = srcMemory) {
            SharedMemory dst = SharedMemory.create("image clipboard data", src.getSize());
            try {
                ByteBuffer srcBuf = src.mapReadOnly();
                ByteBuffer dstBuf = dst.mapReadWrite();
                dstBuf.put(srcBuf);
                SharedMemory.unmap(dstBuf);
                SharedMemory.unmap(srcBuf);
                dst.setProtect(OsConstants.PROT_READ);
                AssetFileDescriptor afd = new AssetFileDescriptor(
                        ParcelFileDescriptor.dup(dst.getFileDescriptor()), 0L, src.getSize());
                dst.close();
                return afd;
            } catch (Throwable t) {
                dst.close();
                throw t;
            }
        } catch (ErrnoException | IOException e) {
            throw new FileNotFoundException("failed to copy the shared memory");
        }
    }

    @Override
    public String getType(Uri uri) {
        try {
            return mDataDeviceProviderService.get().getMimeTypeForContentProvider(uri.toString());
        } catch (RemoteException e) {
            Log.e(TAG, "failed to get the MIME type for content provider", e);
            return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // Apps pasting an image commonly ask for its display name (and size)
        // before opening it, and give up on a null cursor. The size isn't
        // known without fetching the data, and the column allows null.
        if (projection == null) {
            projection = new String[] {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};
        }
        MatrixCursor cursor = new MatrixCursor(projection, 1);
        MatrixCursor.RowBuilder row = cursor.newRow();
        for (String column : projection) {
            row.add(OpenableColumns.DISPLAY_NAME.equals(column)
                    ? uri.getLastPathSegment() : null);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }
}

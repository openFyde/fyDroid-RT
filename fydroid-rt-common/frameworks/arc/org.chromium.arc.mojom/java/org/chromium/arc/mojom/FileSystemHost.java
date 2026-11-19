package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.mojo_base.mojom.Time;
import org.chromium.url.mojom.Url;

public interface FileSystemHost extends Interface {
    public static final Interface.Manager<FileSystemHost, Proxy> MANAGER = FileSystemHost_Internal.MANAGER;

    public interface GetFileNameResponse extends Callbacks.Callback1<String> {
    }

    public interface GetFileSelectorElementsResponse extends Callbacks.Callback1<FileSelectorElements> {
    }

    public interface GetFileSizeResponse extends Callbacks.Callback1<Long> {
    }

    public interface GetFileTypeResponse extends Callbacks.Callback1<String> {
    }

    public interface GetLastModifiedResponse extends Callbacks.Callback1<Time> {
    }

    public interface GetVirtualFileIdResponse extends Callbacks.Callback1<String> {
    }

    public interface HandleIdReleasedResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface OnFileSelectorEventResponse extends Callbacks.Callback0 {
    }

    public interface OpenFileToReadResponse extends Callbacks.Callback1<UntypedHandle> {
    }

    public interface Proxy extends FileSystemHost, Interface.Proxy {
    }

    public interface SelectFilesResponse extends Callbacks.Callback1<SelectFilesResult> {
    }

    void getFileName(String str, GetFileNameResponse getFileNameResponse);

    void getFileSelectorElements(GetFileSelectorElementsRequest getFileSelectorElementsRequest, GetFileSelectorElementsResponse getFileSelectorElementsResponse);

    void getFileSize(String str, GetFileSizeResponse getFileSizeResponse);

    void getFileType(String str, GetFileTypeResponse getFileTypeResponse);

    void getLastModified(Url url, GetLastModifiedResponse getLastModifiedResponse);

    void getVirtualFileId(String str, GetVirtualFileIdResponse getVirtualFileIdResponse);

    void handleIdReleased(String str, HandleIdReleasedResponse handleIdReleasedResponse);

    void onDocumentChanged(long j, int i);

    void onFileSelectorEvent(FileSelectorEvent fileSelectorEvent, OnFileSelectorEventResponse onFileSelectorEventResponse);

    void onMediaStoreUriAdded(Url url, MediaStoreMetadata mediaStoreMetadata);

    void onRootsChanged();

    void openFileToRead(String str, OpenFileToReadResponse openFileToReadResponse);

    void selectFiles(SelectFilesRequest selectFilesRequest, SelectFilesResponse selectFilesResponse);
}

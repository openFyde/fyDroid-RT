package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.url.mojom.Url;

public interface FileSystemInstance extends Interface {
    public static final Interface.Manager<FileSystemInstance, Proxy> MANAGER = FileSystemInstance_Internal.MANAGER;

    public interface AddWatcherResponse extends Callbacks.Callback1<Long> {
    }

    public interface CopyDocumentResponse extends Callbacks.Callback1<Document> {
    }

    public interface CreateDocumentResponse extends Callbacks.Callback1<Document> {
    }

    public interface DeleteDocumentResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface GetChildDocumentsResponse extends Callbacks.Callback1<Document[]> {
    }

    public interface GetDocumentResponse extends Callbacks.Callback1<Document> {
    }

    public interface GetFileSizeResponse extends Callbacks.Callback1<Long> {
    }

    public interface GetMimeTypeResponse extends Callbacks.Callback1<String> {
    }

    public interface GetRecentDocumentsResponse extends Callbacks.Callback1<Document[]> {
    }

    public interface GetRootSizeResponse extends Callbacks.Callback1<RootSize> {
    }

    public interface GetRootsResponse extends Callbacks.Callback1<Root[]> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface MoveDocumentResponse extends Callbacks.Callback1<Document> {
    }

    public interface OpenFileSessionToReadResponse extends Callbacks.Callback1<FileSession> {
    }

    public interface OpenFileSessionToWriteResponse extends Callbacks.Callback1<FileSession> {
    }

    public interface OpenThumbnailResponse extends Callbacks.Callback1<UntypedHandle> {
    }

    public interface OpenUrlsWithPermissionAndWindowInfoResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends FileSystemInstance, Interface.Proxy {
    }

    public interface RemoveWatcherResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface RenameDocumentResponse extends Callbacks.Callback1<Document> {
    }

    void addWatcher(String str, String str2, AddWatcherResponse addWatcherResponse);

    void closeFileSession(String str, String str2);

    void copyDocument(String str, String str2, String str3, CopyDocumentResponse copyDocumentResponse);

    void createDocument(String str, String str2, String str3, String str4, CreateDocumentResponse createDocumentResponse);

    void deleteDocument(String str, String str2, DeleteDocumentResponse deleteDocumentResponse);

    void getChildDocuments(String str, String str2, GetChildDocumentsResponse getChildDocumentsResponse);

    void getDocument(String str, String str2, GetDocumentResponse getDocumentResponse);

    void getFileSize(String str, GetFileSizeResponse getFileSizeResponse);

    void getMimeType(String str, GetMimeTypeResponse getMimeTypeResponse);

    void getRecentDocuments(String str, String str2, GetRecentDocumentsResponse getRecentDocumentsResponse);

    void getRootSize(String str, String str2, GetRootSizeResponse getRootSizeResponse);

    void getRoots(GetRootsResponse getRootsResponse);

    void init(FileSystemHost fileSystemHost, InitResponse initResponse);

    void moveDocument(String str, String str2, String str3, String str4, MoveDocumentResponse moveDocumentResponse);

    void openFileSessionToRead(Url url, OpenFileSessionToReadResponse openFileSessionToReadResponse);

    void openFileSessionToWrite(Url url, OpenFileSessionToWriteResponse openFileSessionToWriteResponse);

    void openThumbnail(String str, Size size, OpenThumbnailResponse openThumbnailResponse);

    void openUrlsWithPermissionAndWindowInfo(OpenUrlsRequest openUrlsRequest, WindowInfo windowInfo, OpenUrlsWithPermissionAndWindowInfoResponse openUrlsWithPermissionAndWindowInfoResponse);

    void reindexDirectory(String str);

    void removeWatcher(long j, RemoveWatcherResponse removeWatcherResponse);

    void renameDocument(String str, String str2, String str3, RenameDocumentResponse renameDocumentResponse);

    void requestFileRemovalScan(String[] strArr);

    void requestMediaScan(String[] strArr);
}

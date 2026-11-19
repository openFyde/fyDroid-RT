package org.chromium.arc.printspooler.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.print.IPrintDocumentAdapter;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentInfo;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.util.ArraySet;
import android.util.Log;
import android.view.KeyEvent;

import com.android.printspooler.R;
import com.android.printspooler.model.MutexFileProvider;
import com.android.printspooler.model.PrintSpoolerProvider;
import com.android.printspooler.model.PrintSpoolerService;
import com.android.printspooler.model.RemotePrintDocument;
import com.android.printspooler.renderer.IPdfEditor;
import com.android.printspooler.renderer.PdfManipulationService;
import com.android.printspooler.ui.PrintActivity;
import com.android.printspooler.util.PageRangeUtils;

import libcore.io.IoUtils;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.mojom.PrintDocumentRequest;
import org.chromium.arc.mojom.PrintMediaSize;
import org.chromium.arc.mojom.PrintPageRange;
import org.chromium.arc.mojom.PrintSessionHost;
import org.chromium.arc.mojom.PrintSessionInstance;
import org.chromium.arc.printspooler.ArcPrintSpoolerService;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.UntypedHandle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Print dialog for ARC, a port of stock ARC's ArcPrintActivity.
 *
 * Instead of the stock dialog and Android print services, the app's document is
 * rendered to PDF here and shown in Chrome print preview over the app's window,
 * so it prints to the printers set up on the ChromeOS host. Chrome asks for a
 * new preview (CreatePreviewDocument) whenever the user changes page size,
 * color, duplex or pages; the document is re-laid out by the app and trimmed
 * to the selected pages before it goes back. Chrome prints the last preview
 * itself, so the Android print job only tracks the dialog and ends canceled
 * when the preview closes.
 *
 * Falls back to the stock PrintActivity if Chrome's print spooler does not
 * connect in time.
 */
public class ArcPrintActivity extends Activity
        implements PrintSessionInstance, RemotePrintDocument.UpdateResultCallbacks {
    private static final String TAG = "ArcPrintActivity";

    private static final long HOST_CONNECT_TIMEOUT_MS = 5000;

    // Same values as PrintActivity's states.
    private static final int STATE_INITIALIZING = 0;
    private static final int STATE_CONFIGURING = 1;
    private static final int STATE_PRINT_CONFIRMED = 2;
    private static final int STATE_PRINT_CANCELED = 3;
    private static final int STATE_UPDATE_FAILED = 4;
    private static final int STATE_CREATE_FILE_FAILED = 5;
    private static final int STATE_PRINT_COMPLETED = 8;

    private static final PrintAttributes.MediaSize DEFAULT_MEDIA_SIZE =
            PrintAttributes.MediaSize.NA_LETTER;
    private static final PrintAttributes.Resolution DEFAULT_RESOLUTION =
            new PrintAttributes.Resolution("300x300", "300x300", 300, 300);
    private static final PrintAttributes.Margins DEFAULT_MARGINS =
            new PrintAttributes.Margins(0, 0, 0, 0);

    private final Handler mHandler = new Handler();

    private PrintJobInfo mPrintJob;
    // Package of the app that is printing.
    private String mCallingPackageName;
    private IBinder mDocumentAdapter;
    private boolean mRecreated;

    private ArcPrintSpoolerService mService;
    private boolean mBound;
    private boolean mStarted;
    private BroadcastReceiver mReceiver;

    private PrintSpoolerProvider mSpoolerProvider;
    private MutexFileProvider mFileProvider;
    private MutexFileProvider mTransformableFileProvider;
    private RemotePrintDocument mPrintedDocument;
    private PageRange[] mSelectedPages = new PageRange[] {PageRange.ALL_PAGES};
    private int mState = STATE_INITIALIZING;
    private boolean mIsFinishing;

    // Only touched on the main thread.
    private PrintSessionHost mPrintSessionHost;
    private CreatePreviewDocumentResponse mCurrentCallback;
    private CreatePreviewDocumentResponse mPendingCallback;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mService = ((ArcPrintSpoolerService.LocalBinder) binder).getService();
            maybeStart();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private final Runnable mHostConnectTimeout = () -> {
        if (!mStarted) {
            Log.w(TAG, "Chrome print spooler not connected, using the Android print dialog.");
            fallbackToStandardPrint();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecreated = savedInstanceState != null;

        Bundle extras = getIntent().getExtras();
        mPrintJob = extras == null ? null
                : extras.getParcelable(PrintManager.EXTRA_PRINT_JOB, PrintJobInfo.class);
        mCallingPackageName = extras == null ? null
                : extras.getString(Intent.EXTRA_PACKAGE_NAME);
        mDocumentAdapter = extras == null ? null
                : extras.getBinder(PrintManager.EXTRA_PRINT_DOCUMENT_ADAPTER);
        if (mPrintJob == null || mDocumentAdapter == null) {
            Log.e(TAG, "Print dialog started without a print job or document adapter.");
            finish();
            return;
        }
        checkAndSetPrintAttributes();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                maybeStart();
            }
        };
        registerReceiver(mReceiver,
                new IntentFilter(ArcPrintSpoolerService.ACTION_SERVICE_CONNECTED_TO_HOST),
                Context.RECEIVER_NOT_EXPORTED);
        mBound = bindService(new Intent(this, ArcPrintSpoolerService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mHandler.postDelayed(mHostConnectTimeout, HOST_CONNECT_TIMEOUT_MS);
    }

    /** Starts the print job once Chrome's print spooler is connected. */
    private void maybeStart() {
        if (mStarted || mService == null || !mService.isConnectedToHost()
                || isFinishing() || isDestroyed()) {
            return;
        }
        mStarted = true;
        mHandler.removeCallbacks(mHostConnectTimeout);
        mSpoolerProvider = new PrintSpoolerProvider(this, this::onConnectedToPrintSpooler);
    }

    private void onConnectedToPrintSpooler() {
        if (isFinishing() || isDestroyed()) {
            if (mRecreated) {
                mSpoolerProvider.getSpooler().setPrintJobState(mPrintJob.getId(),
                        PrintJobInfo.STATE_CANCELED, null);
            }
            return;
        }
        if (!mRecreated) {
            mSpoolerProvider.getSpooler().createPrintJob(mPrintJob);
        }

        setContentView(R.layout.arc_print_activity);
        try {
            mFileProvider = new MutexFileProvider(
                    PrintSpoolerService.generateFileForPrintJob(this, mPrintJob.getId()));
            mTransformableFileProvider = new MutexFileProvider(
                    new File(getFilesDir(), mPrintJob.getId() + "_arc.pdf"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create print job file.", e);
        }
        mPrintedDocument = new RemotePrintDocument(this,
                IPrintDocumentAdapter.Stub.asInterface(mDocumentAdapter), mFileProvider,
                () -> {
                    Log.w(TAG, "Printing app died unexpectedly.");
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    if (!isFinalState(mState) || mPrintedDocument.isUpdating()) {
                        setState(STATE_PRINT_CANCELED);
                        mPrintedDocument.cancel(true);
                        doFinish();
                    }
                }, this);
        mPrintedDocument.start();
        setState(STATE_CONFIGURING);
        updateDocument();
    }

    private void fallbackToStandardPrint() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Intent intent = new Intent(getIntent()).setClass(this, PrintActivity.class);
        startActivity(intent);
        // No spooler job was created here, so there is nothing to cancel.
        mPrintJob = null;
        finish();
    }

    @Override
    protected void onPause() {
        PrintSpoolerService spooler =
                mSpoolerProvider == null ? null : mSpoolerProvider.getSpooler();
        if (isFinishing() && spooler != null && mPrintJob != null) {
            if (mState == STATE_INITIALIZING) {
                spooler.setPrintJobState(mPrintJob.getId(), PrintJobInfo.STATE_CANCELED, null);
            } else {
                spooler.updatePrintJobUserConfigurableOptionsNoPersistence(mPrintJob);
                if (mState == STATE_CREATE_FILE_FAILED) {
                    spooler.setPrintJobState(mPrintJob.getId(), PrintJobInfo.STATE_FAILED,
                            getString(R.string.print_write_error_message));
                } else if (mState == STATE_PRINT_COMPLETED) {
                    spooler.setPrintJobState(mPrintJob.getId(),
                            PrintJobInfo.STATE_COMPLETED, null);
                } else {
                    spooler.setPrintJobState(mPrintJob.getId(),
                            PrintJobInfo.STATE_CANCELED, null);
                }
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mHostConnectTimeout);
        if (mPrintedDocument != null) {
            mPrintedDocument.cancel(true);
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        closeSession();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        doFinish();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mState == STATE_INITIALIZING) {
            doFinish();
            return true;
        }
        if (mState == STATE_PRINT_CANCELED || mState == STATE_PRINT_CONFIRMED
                || mState == STATE_PRINT_COMPLETED) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
            cancelPrint();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    // PrintSessionInstance, called on the Mojo thread.

    @Override
    public void onPrintPreviewClosed() {
        Log.i(TAG, "Print preview closed.");
        mHandler.post(this::cancelPrint);
    }

    @Override
    public void createPreviewDocument(PrintDocumentRequest request,
            CreatePreviewDocumentResponse callback) {
        mHandler.post(() -> onCreatePreviewDocument(request, callback));
    }

    @Override
    public void close() {
        Log.i(TAG, "Print session closed.");
        mHandler.post(() -> mPrintSessionHost = null);
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Print session connection error.", e);
        close();
    }

    private void onCreatePreviewDocument(PrintDocumentRequest request,
            CreatePreviewDocumentResponse callback) {
        if (mPrintedDocument == null || isFinalState(mState)) {
            replyPreview(callback, null, 0);
            return;
        }
        PageRange[] pages;
        if (request.pages.length == 0) {
            pages = new PageRange[] {PageRange.ALL_PAGES};
        } else {
            pages = new PageRange[request.pages.length];
            for (int i = 0; i < request.pages.length; i++) {
                PrintPageRange range = request.pages[i];
                pages[i] = new PageRange(range.start, range.end);
            }
        }
        mSelectedPages = PageRangeUtils.normalize(pages);

        org.chromium.arc.mojom.PrintAttributes requested = request.attributes;
        PrintAttributes attributes = mPrintJob.getAttributes();
        PrintMediaSize mediaSize = requested.mediaSize;
        if (mediaSize != null) {
            attributes.setMediaSize(new PrintAttributes.MediaSize(mediaSize.id,
                    mediaSize.label, mediaSize.widthMils, mediaSize.heightMils));
        }
        // PrintColorMode and PrintDuplexMode use Android's values.
        attributes.setColorMode(requested.colorMode);
        attributes.setDuplexMode(requested.duplexMode);

        if (mCurrentCallback == null) {
            mCurrentCallback = callback;
            updateOrTransformDocument();
        } else {
            // Only the latest settings matter; drop the one already waiting.
            if (mPendingCallback != null) {
                replyPreview(mPendingCallback, null, 0);
            }
            mPendingCallback = callback;
        }
    }

    // RemotePrintDocument.UpdateResultCallbacks

    @Override
    public void onUpdateCanceled() {
        if (isFinalState(mState) || mState == STATE_PRINT_CONFIRMED) {
            doFinish();
        }
    }

    @Override
    public void onUpdateCompleted(RemotePrintDocument.RemotePrintDocumentInfo document) {
        PrintDocumentInfo info = document.info;
        if (info != null) {
            PrintDocumentInfo adjustedInfo = new PrintDocumentInfo.Builder(info.getName())
                    .setContentType(info.getContentType())
                    .setPageCount(PageRangeUtils.getNormalizedPageCount(
                            document.pagesWrittenToFile, info.getPageCount()))
                    .build();
            try {
                adjustedInfo.setDataSize(mFileProvider.acquireFile(null).length());
            } finally {
                mFileProvider.releaseFile();
            }
            mPrintJob.setDocumentInfo(adjustedInfo);
            mPrintJob.setPages(document.pagesInFileToPrint);
            if (document.pagesWrittenToFile == null) {
                // The app wrote nothing for the requested pages yet.
                updateDocument();
                return;
            }
        }

        if (isFinalState(mState) || mState == STATE_PRINT_CONFIRMED) {
            doFinish();
            return;
        }
        if (mPrintSessionHost == null) {
            startPrintInCustomTab();
        } else {
            transformAndSendPreviewDocument();
        }
        setState(STATE_CONFIGURING);
    }

    @Override
    public void onUpdateFailed(CharSequence error) {
        Log.e(TAG, "Document update failed: " + error);
        if (isFinalState(mState)) {
            doFinish();
        }
        setState(STATE_UPDATE_FAILED);
        if (mCurrentCallback != null) {
            replyPreview(mCurrentCallback, null, 0);
            mCurrentCallback = null;
        }
        if (mPrintSessionHost == null) {
            // Chrome never got a document, so there is no preview to close.
            doFinish();
        }
    }

    private void startPrintInCustomTab() {
        PrintSessionHost host = null;
        File titled = new File(getCacheDir(), mPrintJob.getId() + "_titled.pdf");
        try {
            File document = mFileProvider.acquireFile(null);
            // Chrome names the print job after the document title; give it the
            // app's name instead of the temporary file Chrome saves it to.
            if (PdfTitleStamper.stamp(document, titled, getPrintingAppName())) {
                document = titled;
            }
            int fd = ParcelFileDescriptor.open(document,
                    ParcelFileDescriptor.MODE_READ_ONLY).detachFd();
            host = mService == null ? null : mService.startPrintInCustomTab(fd, getTaskId(), this);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to open the print document.", e);
        } finally {
            mFileProvider.releaseFile();
            // Chrome read it through the fd, which outlives the name.
            titled.delete();
        }
        if (host == null) {
            Log.e(TAG, "Chrome could not open print preview for task " + getTaskId());
            setState(STATE_PRINT_CANCELED);
            doFinish();
            return;
        }
        mPrintSessionHost = host;
    }

    /** The printing app's label, else the print job's own name. */
    private String getPrintingAppName() {
        if (mCallingPackageName != null) {
            try {
                PackageManager pm = getPackageManager();
                CharSequence label = pm.getApplicationLabel(
                        pm.getApplicationInfo(mCallingPackageName, 0));
                if (label != null && label.length() > 0) {
                    return label.toString();
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Printing app " + mCallingPackageName + " not found.");
            }
        }
        return mPrintJob.getLabel();
    }

    private void transformAndSendPreviewDocument() {
        new DocumentTransformer(this, mFileProvider, mTransformableFileProvider, mSelectedPages,
                mPrintJob.getAttributes(), this::onDocumentTransformed).transform();
    }

    private void onDocumentTransformed(String error) {
        if (error != null) {
            Log.e(TAG, "Failed to make the preview document: " + error);
            setState(STATE_UPDATE_FAILED);
            mPrintedDocument.kill(error);
            if (mCurrentCallback != null) {
                replyPreview(mCurrentCallback, null, 0);
                mCurrentCallback = null;
            }
            return;
        }
        if (mPendingCallback != null) {
            // Settings changed while this one was rendered; the preview Chrome
            // is waiting for now is the pending one.
            replyPreview(mCurrentCallback, null, 0);
            handlePendingCallback();
            return;
        }
        try {
            File file = mTransformableFileProvider.acquireFile(null);
            int fd = ParcelFileDescriptor.open(file,
                    ParcelFileDescriptor.MODE_READ_ONLY).detachFd();
            replyPreview(mCurrentCallback,
                    ArcBridgeSupport.getInstance().convertFileDescriptorToMojoHandle(fd),
                    file.length());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to send the preview document to Chrome.", e);
            replyPreview(mCurrentCallback, null, 0);
        } finally {
            mTransformableFileProvider.releaseFile();
        }
        mCurrentCallback = null;
    }

    private void handlePendingCallback() {
        mCurrentCallback = mPendingCallback;
        mPendingCallback = null;
        updateOrTransformDocument();
    }

    private static void replyPreview(CreatePreviewDocumentResponse callback,
            UntypedHandle document, long size) {
        if (callback == null) {
            return;
        }
        ArcBridgeSupport.getInstance().runOnMojoThread(() -> callback.call(document, size));
    }

    private void closeSession() {
        final PrintSessionHost host = mPrintSessionHost;
        mPrintSessionHost = null;
        if (host != null) {
            ArcBridgeSupport.getInstance().runOnMojoThread(host::close);
        }
        if (mCurrentCallback != null) {
            replyPreview(mCurrentCallback, null, 0);
            mCurrentCallback = null;
        }
        if (mPendingCallback != null) {
            replyPreview(mPendingCallback, null, 0);
            mPendingCallback = null;
        }
    }

    private void cancelPrint() {
        setState(STATE_PRINT_CANCELED);
        if (mPrintedDocument != null) {
            mPrintedDocument.cancel(true);
        }
        doFinish();
    }

    private void doFinish() {
        if ((mPrintedDocument != null && mPrintedDocument.isUpdating()) || mIsFinishing) {
            return;
        }
        mIsFinishing = true;
        closeSession();
        if (mSpoolerProvider != null) {
            mSpoolerProvider.destroy();
        }
        if (mTransformableFileProvider != null) {
            File file = mTransformableFileProvider.acquireFile(null);
            if (file.exists()) {
                file.delete();
            }
            mTransformableFileProvider.releaseFile();
        }
        if (mPrintedDocument != null) {
            mPrintedDocument.finish();
            mPrintedDocument.destroy();
        }
        finish();
    }

    private static boolean isFinalState(int state) {
        return state == STATE_PRINT_CANCELED || state == STATE_PRINT_COMPLETED
                || state == STATE_CREATE_FILE_FAILED;
    }

    private void setState(int state) {
        if (!isFinalState(mState) || isFinalState(state)) {
            mState = state;
        }
    }

    /** Lays the document out for the current attributes, all pages. */
    private boolean updateDocument() {
        if (mPrintedDocument.hasUpdateError()) {
            return false;
        }
        PrintDocumentInfo info = mPrintedDocument.getDocumentInfo().info;
        PageRange[] pages = null;
        if (info != null) {
            int pageCount = info.getPageCount();
            pages = pageCount == PrintDocumentInfo.PAGE_COUNT_UNKNOWN
                    ? new PageRange[] {PageRange.ALL_PAGES}
                    : new PageRange[] {new PageRange(0, pageCount - 1)};
        }
        return mPrintedDocument.update(mPrintJob.getAttributes(), pages,
                mState != STATE_PRINT_CONFIRMED);
    }

    /**
     * Asks the app for a new layout if the attributes changed; otherwise the
     * file is current and only needs trimming to the selected pages.
     */
    private void updateOrTransformDocument() {
        if (!updateDocument()) {
            transformAndSendPreviewDocument();
        }
    }

    private void checkAndSetPrintAttributes() {
        PrintAttributes attributes = mPrintJob.getAttributes();
        if (attributes == null) {
            mPrintJob.setAttributes(new PrintAttributes.Builder()
                    .setMediaSize(DEFAULT_MEDIA_SIZE)
                    .setResolution(DEFAULT_RESOLUTION)
                    .setMinMargins(DEFAULT_MARGINS)
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                    .setDuplexMode(PrintAttributes.DUPLEX_MODE_NONE)
                    .build());
            return;
        }
        PrintAttributes.MediaSize mediaSize = attributes.getMediaSize();
        if (mediaSize == null) {
            attributes.setMediaSize(DEFAULT_MEDIA_SIZE);
        } else {
            ArraySet<PrintAttributes.MediaSize> predefined =
                    PrintAttributes.MediaSize.getAllPredefinedSizes();
            if (!predefined.contains(mediaSize.asPortrait())
                    && !predefined.contains(mediaSize.asLandscape())) {
                attributes.setMediaSize(mediaSize.isPortrait()
                        ? DEFAULT_MEDIA_SIZE.asPortrait() : DEFAULT_MEDIA_SIZE.asLandscape());
            }
        }
        if (attributes.getResolution() == null) {
            attributes.setResolution(DEFAULT_RESOLUTION);
        }
        attributes.setMinMargins(DEFAULT_MARGINS);
        if (attributes.getColorMode() == 0) {
            attributes.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        }
        if (attributes.getDuplexMode() == 0) {
            attributes.setDuplexMode(PrintAttributes.DUPLEX_MODE_NONE);
        }
    }

    /**
     * Drops the unselected pages and applies the print attributes to a copy of
     * the document, in the spooler's isolated PDF renderer.
     */
    private static final class DocumentTransformer implements ServiceConnection {
        private final Context mContext;
        private final MutexFileProvider mInputFileProvider;
        private final MutexFileProvider mOutputFileProvider;
        private final PageRange[] mPagesToShred;
        private final PrintAttributes mAttributesToApply;
        private final Consumer<String> mCallback;
        private boolean mIsTransformationStarted;

        DocumentTransformer(Context context, MutexFileProvider inputFileProvider,
                MutexFileProvider outputFileProvider, PageRange[] selectedPages,
                PrintAttributes attributesToApply, Consumer<String> callback) {
            mContext = context;
            mInputFileProvider = inputFileProvider;
            mOutputFileProvider = outputFileProvider;
            mPagesToShred = computePagesToShred(selectedPages);
            mAttributesToApply = attributesToApply;
            mCallback = callback;
        }

        void transform() {
            Intent intent = new Intent(PdfManipulationService.ACTION_GET_EDITOR);
            intent.setClass(mContext, PdfManipulationService.class);
            mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (mIsTransformationStarted) {
                return;
            }
            mIsTransformationStarted = true;
            final IPdfEditor editor = IPdfEditor.Stub.asInterface(service);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        doTransform(editor);
                        return null;
                    } catch (RemoteException | IOException | IllegalStateException e) {
                        return e.toString();
                    }
                }

                @Override
                protected void onPostExecute(String error) {
                    mContext.unbindService(DocumentTransformer.this);
                    mCallback.accept(error);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        private void doTransform(IPdfEditor editor) throws RemoteException, IOException {
            ParcelFileDescriptor source = null;
            ParcelFileDescriptor destination = null;
            try {
                source = ParcelFileDescriptor.open(mInputFileProvider.acquireFile(null),
                        ParcelFileDescriptor.MODE_READ_WRITE);
                editor.openDocument(source);
                source.close();
                editor.removePages(mPagesToShred);
                if (mAttributesToApply != null) {
                    editor.applyPrintAttributes(mAttributesToApply);
                }
                destination = ParcelFileDescriptor.open(mOutputFileProvider.acquireFile(null),
                        ParcelFileDescriptor.MODE_READ_WRITE
                                | ParcelFileDescriptor.MODE_CREATE
                                | ParcelFileDescriptor.MODE_TRUNCATE);
                editor.write(destination);
                destination.close();
                editor.closeDocument();
            } finally {
                IoUtils.closeQuietly(source);
                IoUtils.closeQuietly(destination);
                mInputFileProvider.releaseFile();
                mOutputFileProvider.releaseFile();
            }
        }

        /** The complement of |selectedPages|, which are normalized. */
        private static PageRange[] computePagesToShred(PageRange[] selectedPages) {
            ArrayList<PageRange> pagesToShred = new ArrayList<>();
            PageRange previous = null;
            for (int i = 0; i < selectedPages.length; i++) {
                PageRange current = selectedPages[i];
                if (previous == null) {
                    int end = current.getStart() - 1;
                    if (end >= 0) {
                        pagesToShred.add(new PageRange(0, end));
                    }
                } else {
                    int start = previous.getEnd() + 1;
                    int end = current.getStart() - 1;
                    if (start <= end) {
                        pagesToShred.add(new PageRange(start, end));
                    }
                }
                if (i == selectedPages.length - 1 && current.getEnd() != Integer.MAX_VALUE) {
                    pagesToShred.add(new PageRange(current.getEnd() + 1, Integer.MAX_VALUE));
                }
                previous = current;
            }
            return pagesToShred.toArray(new PageRange[0]);
        }
    }
}

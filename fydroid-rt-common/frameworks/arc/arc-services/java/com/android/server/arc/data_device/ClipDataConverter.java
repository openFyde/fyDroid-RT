package com.android.server.arc.data_device;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentProvider;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.text.Html;
import android.text.Spanned;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import libcore.io.IoUtils;
import libcore.io.Streams;

/**
 * Converts MIME-typed data between Chrome OS Wayland clipboard/drag format
 * and Android's ClipData. Supports text, HTML, URI lists, and images.
 */
class ClipDataConverter {
    static final String CHROME_CONTENT_PROVIDER_AUTHORITY = "org.chromium.arc.chromecontentprovider";
    static final String CONTENT_PROVIDER_BASE_URI = "content://org.chromium.arc.data_device.provider/";
    static final String DATA_DEVICE_PROVIDER_AUTHORITY = "org.chromium.arc.data_device.provider";
    static final String FILE_PROVIDER_AUTHORITY = "org.chromium.arc.file_system.fileprovider";
    static final String VOLUME_PROVIDER_AUTHORITY = "org.chromium.arc.volumeprovider";

    private static final int MAX_BINDER_PARCEL_SIZE_BYTES = 818688;
    private static final String MIMETYPE_APPLICATION_OCTET_STREAM_PREFIX = "application/octet-stream;name=";
    static final String MIMETYPE_APPLICATION_X_ARC_URILIST = "application/x-arc-uri-list";
    static final String MIMETYPE_IMAGE_BMP = "image/bmp";
    static final String MIMETYPE_IMAGE_GIF = "image/gif";
    static final String MIMETYPE_IMAGE_HEIF = "image/heif";
    static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";
    static final String MIMETYPE_IMAGE_PNG = "image/png";
    static final String MIMETYPE_IMAGE_WEBP = "image/webp";
    static final String MIMETYPE_TEXT_HTML = "text/html";
    static final String MIMETYPE_TEXT_HTML_UTF16 = "text/html;charset=utf-16";
    static final String MIMETYPE_TEXT_HTML_UTF8 = "text/html;charset=utf-8";
    static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    static final String MIMETYPE_TEXT_PLAIN_UTF16 = "text/plain;charset=utf-16";
    static final String MIMETYPE_TEXT_PLAIN_UTF8 = "text/plain;charset=utf-8";
    static final String MIMETYPE_TEXT_URILIST = "text/uri-list";
    // Office's clipboard drawing: an OOXML package with the picture under */media/.
    // WPS copies images only in this form, with no image/* type at all.
    static final String MIMETYPE_OFFICE_DRAWING =
            "application/vnd.openxmlformats-officedocument.clipboard.drawing";
    // X11's name for UTF-8 text; a Wayland compositor may ask for it by this name.
    static final String MIMETYPE_UTF8_STRING = "UTF8_STRING";
    private static final int PROVIDER_ANR_TIMEOUT_MILLIS = 10000;
    // Office drawings are read whole into system_server; refuse anything bigger.
    private static final int MAX_DRAWING_BYTES = 64 * 1024 * 1024;
    // Image types Chrome's clipboard decoder (exo → data_decoder) understands.
    private static final List<String> PASSTHROUGH_IMAGE_TYPES = Arrays.asList(
            MIMETYPE_IMAGE_PNG, MIMETYPE_IMAGE_JPEG, MIMETYPE_IMAGE_GIF, MIMETYPE_IMAGE_WEBP,
            MIMETYPE_IMAGE_BMP);
    private static final String TAG = "ClipDataConverter";
    private static final ClipData.Item sPlaceholderItem = new ClipData.Item("");

    private final Context mContext;
    private final Handler mHandler;
    private final List<DataOfferConverter> mConverters = new ArrayList<>();
    private final AtomicInteger mSequenceNumber = new AtomicInteger();

    interface DataOfferDelegate extends AutoCloseable {
        FileDescriptor receive(String mimeType) throws RemoteException;
        @Override void close();
    }

    ClipDataConverter(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        mConverters.add(new ImageConverter(MIMETYPE_IMAGE_PNG));
        mConverters.add(new OctetStreamConverter());
        mConverters.add(new UriListConverter(MIMETYPE_TEXT_URILIST));
        mConverters.add(new UriListConverter(MIMETYPE_APPLICATION_X_ARC_URILIST));
        mConverters.add(new HtmlConverter(MIMETYPE_TEXT_HTML_UTF16, StandardCharsets.UTF_16LE));
        mConverters.add(new HtmlConverter(MIMETYPE_TEXT_HTML_UTF8, StandardCharsets.UTF_8));
        mConverters.add(new HtmlConverter(MIMETYPE_TEXT_HTML, StandardCharsets.US_ASCII));
        mConverters.add(new TextConverter(MIMETYPE_TEXT_PLAIN_UTF16, StandardCharsets.UTF_16LE));
        mConverters.add(new TextConverter(MIMETYPE_TEXT_PLAIN_UTF8, StandardCharsets.UTF_8));
        mConverters.add(new TextConverter(MIMETYPE_TEXT_PLAIN, StandardCharsets.US_ASCII));
    }

    // --- Public API ---

    Result createClipData(String[] mimeTypes, DataOfferDelegate delegate) {
        return createClipData(mimeTypes, delegate, false, mSequenceNumber.getAndIncrement());
    }

    ClipData createPlaceholderClipData(String[] mimeTypes, DataOfferDelegate delegate) {
        Result result = createClipData(mimeTypes, delegate, true, mSequenceNumber.get());
        return result != null ? result.getClipData() : null;
    }

    static String[] getSupportedMimeTypes(ClipData clipData) {
        if (clipData == null || clipData.getDescription() == null) {
            return new String[0];
        }
        ArrayList<String> types = new ArrayList<>();
        ClipDescription desc = clipData.getDescription();
        if (desc.hasMimeType(MIMETYPE_TEXT_HTML)) {
            types.add(MIMETYPE_TEXT_PLAIN_UTF8);
            types.add(MIMETYPE_TEXT_HTML_UTF8);
        } else if (desc.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            types.add(MIMETYPE_TEXT_PLAIN_UTF8);
        }
        for (int i = 0; i < desc.getMimeTypeCount(); i++) {
            String type = desc.getMimeType(i);
            if (!ClipDescription.compareMimeTypes(type, "image/*")) {
                continue;
            }
            // Formats exo's decoder reads go through as-is; anything else
            // (HEIF, a bare image/*) is offered as PNG and transcoded on send.
            String offered = PASSTHROUGH_IMAGE_TYPES.contains(type) ? type : MIMETYPE_IMAGE_PNG;
            if (!types.contains(offered)) {
                types.add(offered);
            }
        }
        if (desc.hasMimeType(MIMETYPE_OFFICE_DRAWING) && !types.contains(MIMETYPE_IMAGE_PNG)) {
            // The picture is extracted from the package on send.
            types.add(MIMETYPE_IMAGE_PNG);
        }
        return types.toArray(new String[0]);
    }

    void writeClipData(ClipData clipData, String mimeType, FileDescriptor fd) {
        try {
            if (MIMETYPE_TEXT_PLAIN_UTF8.equals(mimeType) || MIMETYPE_TEXT_PLAIN.equals(mimeType)
                    || MIMETYPE_UTF8_STRING.equals(mimeType)) {
                writeTextClipData(clipData, fd);
            } else if (MIMETYPE_TEXT_HTML_UTF8.equals(mimeType)
                    || MIMETYPE_TEXT_HTML.equals(mimeType)) {
                writeHtmlClipData(clipData, fd);
            } else if (ClipDescription.compareMimeTypes(mimeType, "image/*")) {
                writeImageClipData(clipData, mimeType, fd);
            } else {
                Slog.e(TAG, "Unsupported clip data type " + mimeType);
            }
        } catch (IOException e) {
            Slog.e(TAG, "cannot write to FD");
        }
    }

    // --- Internal ---

    private Result createClipData(String[] mimeTypes, DataOfferDelegate delegate,
            boolean placeholder, int seqNum) {
        try {
            for (DataOfferConverter converter : mConverters) {
                for (String mimeType : mimeTypes) {
                    if (converter.match(mimeType)) {
                        Result result = converter.convert(mimeType, delegate, placeholder, seqNum);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        } catch (RemoteException | IOException e) {
            Slog.e(TAG, "Cannot read FD providing clipboard / drag data", e);
        } catch (OutOfMemoryError e) {
            Slog.e(TAG, "Out of memory providing clipboard / drag data", e);
        }
        IoUtils.closeQuietly(delegate);
        return null;
    }

    ContentResolver getContentResolver() {
        return mContext.getContentResolver();
    }

    static String readString(String mimeType, DataOfferDelegate delegate, Charset charset)
            throws IOException, RemoteException {
        FileDescriptor fd = delegate.receive(mimeType);
        if (fd == null) {
            throw new IOException("cannot get fd");
        }
        try {
            return Streams.readFully(new InputStreamReader(new FileInputStream(fd), charset));
        } finally {
            IoUtils.closeQuietly(fd);
        }
    }

    private void writeTextClipData(ClipData clipData, FileDescriptor fd) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clipData.getItemCount(); i++) {
            String text = getItemText(clipData.getItemAt(i));
            if (text != null) {
                sb.append(text);
            }
        }
        writeString(fd, StandardCharsets.UTF_8, sb.toString());
    }

    private void writeHtmlClipData(ClipData clipData, FileDescriptor fd) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clipData.getItemCount(); i++) {
            String html = getItemHtml(clipData.getItemAt(i));
            if (html != null) {
                sb.append(html);
            }
        }
        writeString(fd, StandardCharsets.UTF_8, sb.toString());
    }

    // An item's content need not be inline: office apps (WPS, for one) copy a
    // content:// URI and a ClipDescription saying text/html + text/plain, and
    // serve the actual text from their provider. Reading only getText() /
    // getHtmlText() hands Chrome empty strings, so nothing pastes.

    private String getItemText(ClipData.Item item) {
        CharSequence text = item.getText();
        if (text != null) {
            return text.toString();
        }
        String html = item.getHtmlText();
        if (html == null) {
            String[] types = getContentStreamTypes(item.getUri(), "text/*");
            if (types == null) {
                // Intent or non-text URI: Android's own paste falls back to its string form.
                return item.coerceToText(mContext).toString();
            }
            if (hasStreamType(types, MIMETYPE_TEXT_PLAIN)) {
                return readContentText(item.getUri(), MIMETYPE_TEXT_PLAIN);
            }
            html = readContentText(item.getUri(), MIMETYPE_TEXT_HTML);
            if (html == null) {
                return null;
            }
        }
        return Html.fromHtml(stripHtmlClipboardHeader(html), Html.FROM_HTML_MODE_LEGACY)
                .toString();
    }

    private String getItemHtml(ClipData.Item item) {
        String html = item.getHtmlText();
        if (html == null && item.getText() == null
                && hasStreamType(getContentStreamTypes(item.getUri(), "text/*"), MIMETYPE_TEXT_HTML)) {
            html = readContentText(item.getUri(), MIMETYPE_TEXT_HTML);
        }
        return html != null ? stripHtmlClipboardHeader(html) : null;
    }

    // Returns the stream types matching mimeTypeFilter a content:// URI can serve,
    // or null if none.
    private String[] getContentStreamTypes(Uri uri, String mimeTypeFilter) {
        if (uri == null || !ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            return null;
        }
        AnrTimeout timeout = setAnrTimeout(uri);
        try {
            return getContentResolver().getStreamTypes(uri, mimeTypeFilter);
        } catch (RuntimeException e) {
            Slog.w(TAG, "Cannot query stream types of " + uri, e);
            return null;
        } finally {
            timeout.close();
        }
    }

    private static boolean hasStreamType(String[] types, String mimeType) {
        if (types != null) {
            for (String type : types) {
                if (ClipDescription.compareMimeTypes(type, mimeType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String readContentText(Uri uri, String mimeType) {
        AnrTimeout timeout = setAnrTimeout(uri);
        try (InputStream is = getContentResolver()
                .openTypedAssetFileDescriptor(uri, mimeType, null).createInputStream()) {
            return Streams.readFully(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException e) {
            Slog.w(TAG, "Cannot read " + mimeType + " from " + uri, e);
            return null;
        } finally {
            timeout.close();
        }
    }

    // Windows' CF_HTML clipboard format prefixes the markup with a plain-text
    // header ("Version:1.0\r\nStartHTML:..."); WPS writes it verbatim. Chrome
    // would paste those header lines as visible text, so drop everything
    // before the first tag.
    private static String stripHtmlClipboardHeader(String html) {
        if (html.startsWith("Version:")) {
            int start = html.indexOf('<');
            if (start > 0) {
                return html.substring(start);
            }
        }
        return html;
    }

    private void writeImageClipData(ClipData clipData, String mimeType, FileDescriptor fd) {
        Pair<Uri, String> image = getImageUriFromClipData(clipData);
        if (image == null) {
            Uri drawing = getOfficeDrawingUri(clipData);
            if (drawing != null) {
                writeOfficeDrawingImage(drawing, fd);
            } else {
                Slog.e(TAG, "ClipData does not contain a valid URI.");
            }
            return;
        }
        Uri imageUri = image.first;
        AnrTimeout timeout = setAnrTimeout(imageUri);
        try (FileOutputStream fos = new FileOutputStream(fd)) {
            if (mimeType.equals(image.second)) {
                try (InputStream is = getContentResolver().openInputStream(imageUri)) {
                    Streams.copy(is, fos);
                }
            } else {
                // Offered as PNG because Chrome can't decode the source format.
                Bitmap bitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(getContentResolver(), imageUri),
                        (decoder, info, source) ->
                                decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE));
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } finally {
                    bitmap.recycle();
                }
            }
        } catch (IOException e) {
            Slog.e(TAG, "Failed to write image clip data", e);
        } catch (OutOfMemoryError e) {
            Slog.e(TAG, "Out of memory transcoding image clip data", e);
        } finally {
            timeout.close();
        }
    }

    private Uri getOfficeDrawingUri(ClipData clipData) {
        if (!clipData.getDescription().hasMimeType(MIMETYPE_OFFICE_DRAWING)) {
            return null;
        }
        for (int i = 0; i < clipData.getItemCount(); i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            if (getContentStreamTypes(uri, MIMETYPE_OFFICE_DRAWING) != null) {
                return uri;
            }
        }
        return null;
    }

    // Sends the first picture in the drawing package. Formats Chrome decodes go
    // through as-is (exo sniffs the bytes, not the offered type); others are
    // transcoded to PNG. Vector media (EMF/WMF) can't be rendered here.
    //
    // Not ZipInputStream: WPS stores the picture uncompressed with its size
    // deferred to a data descriptor (and a zero CRC), and a streaming reader
    // can't find the end of such an entry — Android's reads it as 0 bytes.
    // The central directory has the real sizes, so read the whole package.
    private void writeOfficeDrawingImage(Uri uri, FileDescriptor fd) {
        AnrTimeout timeout = setAnrTimeout(uri);
        try (FileOutputStream fos = new FileOutputStream(fd);
             InputStream is = getContentResolver()
                     .openTypedAssetFileDescriptor(uri, MIMETYPE_OFFICE_DRAWING, null)
                     .createInputStream()) {
            Pair<String, byte[]> picture = extractZipMedia(readBounded(is, MAX_DRAWING_BYTES));
            if (picture == null) {
                Slog.w(TAG, "No raster picture in the Office drawing from " + uri);
                return;
            }
            if (PASSTHROUGH_IMAGE_TYPES.contains(picture.first)) {
                fos.write(picture.second);
            } else {
                Bitmap bitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(ByteBuffer.wrap(picture.second)),
                        (decoder, info, source) ->
                                decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE));
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } finally {
                    bitmap.recycle();
                }
            }
        } catch (IOException | RuntimeException | DataFormatException e) {
            Slog.e(TAG, "Failed to extract the picture from " + uri, e);
        } catch (OutOfMemoryError e) {
            Slog.e(TAG, "Out of memory extracting the picture from " + uri, e);
        } finally {
            timeout.close();
        }
    }

    private static byte[] readBounded(InputStream is, int limit) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[64 * 1024];
        for (int n; (n = is.read(buf)) != -1; ) {
            if (out.size() + n > limit) {
                throw new IOException("Office drawing larger than " + limit + " bytes");
            }
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }

    // Returns (image type, bytes) of the first */media/ raster entry, located
    // through the central directory.
    private static Pair<String, byte[]> extractZipMedia(byte[] zip)
            throws IOException, DataFormatException {
        ByteBuffer bb = ByteBuffer.wrap(zip).order(ByteOrder.LITTLE_ENDIAN);
        int eocd = -1;
        for (int i = zip.length - 22; i >= Math.max(0, zip.length - 22 - 0xffff); i--) {
            if (bb.getInt(i) == 0x06054b50) {
                eocd = i;
                break;
            }
        }
        if (eocd < 0) {
            throw new IOException("Office drawing is not a zip package");
        }
        int count = bb.getShort(eocd + 10) & 0xffff;
        int pos = bb.getInt(eocd + 16);
        for (int i = 0; i < count; i++) {
            if (bb.getInt(pos) != 0x02014b50) {
                throw new IOException("Bad zip central directory");
            }
            int method = bb.getShort(pos + 10) & 0xffff;
            int csize = bb.getInt(pos + 20);
            int size = bb.getInt(pos + 24);
            int nameLen = bb.getShort(pos + 28) & 0xffff;
            int extraLen = bb.getShort(pos + 30) & 0xffff;
            int commentLen = bb.getShort(pos + 32) & 0xffff;
            int local = bb.getInt(pos + 42);
            String name = new String(zip, pos + 46, nameLen, StandardCharsets.UTF_8)
                    .toLowerCase(Locale.ROOT);
            pos += 46 + nameLen + extraLen + commentLen;

            String type = getImageTypeFromFilename(name);
            if (!name.contains("/media/") || type == null) {
                continue;
            }
            int data = local + 30 + (bb.getShort(local + 26) & 0xffff)
                    + (bb.getShort(local + 28) & 0xffff);
            if (method == 0) {
                return new Pair<>(type, Arrays.copyOfRange(zip, data, data + csize));
            }
            if (method == 8) {
                Inflater inflater = new Inflater(true /* nowrap */);
                try {
                    inflater.setInput(zip, data, csize);
                    byte[] out = new byte[size];
                    int n = 0;
                    while (n < size && !inflater.finished()) {
                        int r = inflater.inflate(out, n, size - n);
                        if (r == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
                            break;
                        }
                        n += r;
                    }
                    return new Pair<>(type, n == size ? out : Arrays.copyOf(out, n));
                } finally {
                    inflater.end();
                }
            }
            Slog.w(TAG, "Unsupported zip method " + method + " for " + name);
        }
        return null;
    }

    static String getImageTypeFromFilename(String filename) {
        if (filename.endsWith(".png")) return MIMETYPE_IMAGE_PNG;
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return MIMETYPE_IMAGE_JPEG;
        if (filename.endsWith(".gif")) return MIMETYPE_IMAGE_GIF;
        if (filename.endsWith(".bmp")) return MIMETYPE_IMAGE_BMP;
        if (filename.endsWith(".webp")) return MIMETYPE_IMAGE_WEBP;
        if (filename.endsWith(".heic") || filename.endsWith(".heif")) return MIMETYPE_IMAGE_HEIF;
        return null;
    }

    private static void writeString(FileDescriptor fd, Charset charset, String str)
            throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fd), charset)) {
            writer.write(str);
        }
    }

    // First content:// item the provider reports as an image, with that type.
    private Pair<Uri, String> getImageUriFromClipData(ClipData clipData) {
        ContentResolver cr = getContentResolver();
        for (int i = 0; i < clipData.getItemCount(); i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            if (uri == null) {
                continue;
            }
            if (!ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                Slog.w(TAG, "Not a ContentProvider URI: " + uri);
                continue;
            }
            AnrTimeout timeout = setAnrTimeout(uri);
            try {
                String type = cr.getType(uri);
                if (type != null && ClipDescription.compareMimeTypes(type, "image/*")) {
                    return new Pair<>(uri, type);
                }
                Slog.w(TAG, "Expected an image for " + uri + "; got " + type);
            } finally {
                timeout.close();
            }
        }
        return null;
    }

    AnrTimeout setAnrTimeout(Uri uri) {
        Preconditions.checkState(!mHandler.getLooper().isCurrentThread());
        AnrTimeout timeout = new AnrTimeout(uri);
        timeout.start();
        return timeout;
    }

    // --- Converter hierarchy ---

    abstract class DataOfferConverter {
        private final String mMimeType;

        protected DataOfferConverter(String mimeType) {
            mMimeType = mimeType;
        }

        boolean match(String mimeType) {
            return mMimeType.equals(mimeType);
        }

        abstract Result convert(String mimeType, DataOfferDelegate delegate,
                boolean placeholder, int seqNum) throws IOException, RemoteException;
    }

    class TextConverter extends DataOfferConverter {
        final Charset mCharset;

        TextConverter(String mimeType, Charset charset) {
            super(mimeType);
            mCharset = charset;
        }

        @Override
        Result convert(String mimeType, DataOfferDelegate delegate,
                boolean placeholder, int seqNum) throws IOException, RemoteException {
            if (placeholder) {
                return new Result(new ClipData("", new String[]{MIMETYPE_TEXT_PLAIN},
                        sPlaceholderItem), null);
            }
            String text = readString(mimeType, delegate, mCharset);
            if (text.length() * 2 >= MAX_BINDER_PARCEL_SIZE_BYTES) {
                Slog.e(TAG, "clipboard content too long; returning an empty clipboard");
                return null;
            }
            IoUtils.closeQuietly(delegate);
            return new Result(ClipData.newPlainText("", text), null);
        }
    }

    class HtmlConverter extends DataOfferConverter {
        final Charset mCharset;

        HtmlConverter(String mimeType, Charset charset) {
            super(mimeType);
            mCharset = charset;
        }

        @Override
        Result convert(String mimeType, DataOfferDelegate delegate,
                boolean placeholder, int seqNum) throws IOException, RemoteException {
            if (placeholder) {
                return new Result(new ClipData("", new String[]{MIMETYPE_TEXT_HTML},
                        sPlaceholderItem), null);
            }
            String html = readString(mimeType, delegate, mCharset);
            String cleanHtml = html.replaceAll("<!--.*?-->", "");
            Spanned spanned = Html.fromHtml(cleanHtml);
            if ((spanned.length() + html.length()) * 2 >= MAX_BINDER_PARCEL_SIZE_BYTES) {
                Slog.e(TAG, "clipboard content too long; returning an empty clipboard");
                return null;
            }
            IoUtils.closeQuietly(delegate);
            // Not ClipData.newHtmlText(): that factory declares ONLY text/html
            // in the ClipDescription, even though the Item it builds carries
            // this same plain-text rendering (for coerceToText()). Chrome
            // offers text/html for anything from a rich text selection to a
            // plain "copy link" on a URL, so this is the common case, not an
            // edge case. A consumer that gates on
            // ClipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN) rather than
            // reading the item directly — e.g. an app deciding whether to
            // offer to auto-fill a pasted link — never sees the clip at all.
            // The plain-text rendering is already right there; declare it.
            //
            // text is spanned.toString(), not the Spanned itself: a styled
            // CharSequence makes Editor.paste() take the coerceToStyledText()
            // path (Android's default "Paste", vs. "Paste as plain text"),
            // which hands the receiving app process the Spanned as-is —
            // spans and all — across the Binder trip out of system_server.
            // Nothing here needs the styling; a plain String is what
            // coerceToText()'s own fallback would already produce, and it
            // carries no span-marshalling risk into whatever the target app
            // does with it.
            ClipData.Item item = new ClipData.Item(spanned.toString(), html);
            ClipData clipData = new ClipData("",
                    new String[]{MIMETYPE_TEXT_HTML, MIMETYPE_TEXT_PLAIN}, item);
            return new Result(clipData, null);
        }
    }

    class ImageConverter extends DataOfferConverter {
        private final ExecutorService mExecutorService;

        ImageConverter(String mimeType) {
            super(mimeType);
            mExecutorService = Executors.newSingleThreadExecutor();
        }

        @Override
        Result convert(String mimeType, DataOfferDelegate delegate,
                boolean placeholder, int seqNum) throws IOException, RemoteException {
            return convert(mimeType, delegate, mimeType, placeholder, seqNum);
        }

        protected Result convert(String clipMimeType, DataOfferDelegate delegate,
                String receiveMimeType, boolean placeholder, int seqNum) {
            if (placeholder) {
                return new Result(new ClipData("", new String[]{clipMimeType},
                        sPlaceholderItem), null);
            }
            ImageData imageData = ImageData.read(seqNum, clipMimeType, delegate,
                    receiveMimeType, mExecutorService);
            if (imageData == null) {
                return null;
            }
            return new Result(new ClipData("image", new String[]{clipMimeType},
                    new ClipData.Item(Uri.parse(imageData.getUri()))), imageData);
        }
    }

    class OctetStreamConverter extends ImageConverter {
        OctetStreamConverter() {
            super(null);
        }

        @Override
        boolean match(String mimeType) {
            return mimeType.startsWith(MIMETYPE_APPLICATION_OCTET_STREAM_PREFIX);
        }

        @Override
        Result convert(String mimeType, DataOfferDelegate delegate,
                boolean placeholder, int seqNum) throws IOException, RemoteException {
            String sniffed = getSniffedMimeType(mimeType);
            if (sniffed == null) {
                return null;
            }
            return super.convert(sniffed, delegate, mimeType, placeholder, seqNum);
        }

        private String getSniffedMimeType(String mimeType) {
            if (!mimeType.startsWith(MIMETYPE_APPLICATION_OCTET_STREAM_PREFIX)) {
                return null;
            }
            String name = mimeType.substring(MIMETYPE_APPLICATION_OCTET_STREAM_PREFIX.length());
            if (name.length() < 2 || name.charAt(0) != '\"'
                    || name.charAt(name.length() - 1) != '\"') {
                return null;
            }
            String filename = name.substring(1, name.length() - 1).replaceAll("\\\\\"", "\"");
            return getImageTypeFromFilename(filename);
        }
    }

    class UriListConverter extends DataOfferConverter {
        UriListConverter(String mimeType) {
            super(mimeType);
        }

        @Override
        Result convert(String mimeType, DataOfferDelegate delegate,
                boolean placeholder, int seqNum) throws IOException, RemoteException {
            String text = readString(mimeType, delegate, StandardCharsets.UTF_16LE);
            ClipData clipData = null;
            for (String line : text.split("\\r?\\n")) {
                if (!line.isEmpty()) {
                    Uri uri = Uri.parse(line);
                    if (clipData == null) {
                        clipData = ClipData.newUri(getContentResolver(), "", uri);
                    } else {
                        clipData.addItem(getContentResolver(), new ClipData.Item(uri));
                    }
                }
            }
            IoUtils.closeQuietly(delegate);
            if (clipData == null) {
                return null;
            }
            return new Result(clipData, null);
        }
    }

    // --- Result ---

    class Result {
        private final ClipData mClipData;
        private final ImageData mImageData;

        Result(ClipData clipData, ImageData imageData) {
            mClipData = clipData;
            mImageData = imageData;
        }

        ClipData getClipData() {
            return mClipData;
        }

        ImageData getImageData() {
            return mImageData;
        }

        List<Uri> getImageUris(Context context) {
            if (mImageData != null) {
                return Arrays.asList(Uri.parse(mImageData.getUri()));
            }
            ArrayList<Uri> allUris = new ArrayList<>();
            mClipData.collectUris(allUris);
            ArrayList<Uri> imageUris = new ArrayList<>();
            for (Uri uri : allUris) {
                String authority = uri.getAuthority();
                if (FILE_PROVIDER_AUTHORITY.equals(authority)
                        || VOLUME_PROVIDER_AUTHORITY.equals(authority)
                        || CHROME_CONTENT_PROVIDER_AUTHORITY.equals(authority)) {
                    String type = context.getContentResolver().getType(uri);
                    if (MIMETYPE_IMAGE_JPEG.equals(type) || MIMETYPE_IMAGE_PNG.equals(type)
                            || MIMETYPE_IMAGE_GIF.equals(type) || MIMETYPE_IMAGE_BMP.equals(type)
                            || MIMETYPE_IMAGE_WEBP.equals(type)
                            || MIMETYPE_IMAGE_HEIF.equals(type)) {
                        imageUris.add(uri);
                    }
                }
            }
            return imageUris.isEmpty() ? null : imageUris;
        }
    }

    // --- ImageData ---

    static class ImageData implements Closeable {
        private static final int MAXIMUM_IMAGE_SIZE = 5242880; // 5 MB
        private final DataOfferDelegate mDataOffer;
        private FileDescriptor mImageFd;
        private final String mMimeType;
        private final int mSequenceNumber;
        private SharedMemory mSharedMemory = null;
        private LoadingState mImageLoadingState = LoadingState.NOT_LOADED;
        private final Set<String> mPackageNames = Collections.synchronizedSet(new ArraySet<>());

        enum LoadingState {
            NOT_LOADED, LOADED_SUCCESS, FAILED, CLOSED
        }

        private ImageData(int seqNum, String mimeType, FileDescriptor fd,
                DataOfferDelegate dataOffer, ExecutorService executor) {
            mSequenceNumber = seqNum;
            mMimeType = mimeType;
            mImageFd = fd;
            mDataOffer = dataOffer;
            executor.execute(this::loadImageDataIntoSharedMemoryIfNeeded);
        }

        static ImageData read(int seqNum, String mimeType, DataOfferDelegate delegate,
                String receiveMimeType, ExecutorService executor) {
            try {
                FileDescriptor fd = delegate.receive(receiveMimeType);
                if (fd == null) {
                    Slog.e(TAG, "received null fd for image data");
                    return null;
                }
                return new ImageData(seqNum, mimeType, fd, delegate, executor);
            } catch (RemoteException e) {
                Slog.e(TAG, "failed to get clipboard image fd", e);
                return null;
            }
        }

        String getUri() {
            return CONTENT_PROVIDER_BASE_URI + mSequenceNumber
                    + (MIMETYPE_IMAGE_PNG.equals(mMimeType) ? ".png" : ".jpg");
        }

        String getMimeType() {
            return mMimeType;
        }

        synchronized SharedMemory getSharedMemory() {
            loadImageDataIntoSharedMemoryIfNeeded();
            return mSharedMemory;
        }

        void addAccessingPackage(String packageName) {
            mPackageNames.add(packageName);
        }

        boolean isAccessedBy(String packageName) {
            return mPackageNames.contains(packageName);
        }

        @Override
        public synchronized void close() {
            if (mSharedMemory != null) {
                mSharedMemory.close();
            }
            mImageLoadingState = LoadingState.CLOSED;
        }

        private synchronized void loadImageDataIntoSharedMemoryIfNeeded() {
            if (mImageLoadingState == LoadingState.NOT_LOADED) {
                mImageLoadingState = loadImageDataIntoSharedMemoryLocked()
                        ? LoadingState.LOADED_SUCCESS : LoadingState.FAILED;
            }
        }

        private boolean loadImageDataIntoSharedMemoryLocked() {
            Slog.i(TAG, "ClipDataConverter.ImageData: start loading the image");
            try (FileInputStream fis = new FileInputStream(mImageFd)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int total = 0;
                int n;
                while ((n = fis.read(buf)) != -1) {
                    baos.write(buf, 0, n);
                    total += n;
                    if (total > MAXIMUM_IMAGE_SIZE) {
                        Slog.e(TAG, "Image is too big to load. Giving up");
                        return false;
                    }
                }
                if (total <= 0) {
                    Slog.e(TAG, "Received image is empty");
                    return false;
                }
                SharedMemory shm = SharedMemory.create("image clipboard data", total);
                mSharedMemory = shm;
                ByteBuffer bb = shm.mapReadWrite();
                bb.put(baos.toByteArray());
                SharedMemory.unmap(bb);
                mSharedMemory.setProtect(OsConstants.PROT_READ);
                return true;
            } catch (ErrnoException | IOException e) {
                Slog.e(TAG, "Failed to read a clipboard image", e);
                return false;
            } finally {
                IoUtils.closeQuietly(mImageFd);
                IoUtils.closeQuietly(mDataOffer);
            }
        }
    }

    // --- ANR timeout ---

    class AnrTimeout implements Runnable, AutoCloseable {
        final Uri mUri;

        AnrTimeout(Uri uri) {
            mUri = uri;
        }

        void start() {
            mHandler.postDelayed(this, PROVIDER_ANR_TIMEOUT_MILLIS);
        }

        @Override
        public void close() {
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            ContentResolver cr = getContentResolver();
            IContentProvider provider = cr.acquireExistingProvider(mUri);
            if (provider == null) return;
            try {
                cr.appNotRespondingViaProvider(provider);
            } finally {
                cr.releaseProvider(provider);
            }
            start(); // restart for next interval
        }
    }
}

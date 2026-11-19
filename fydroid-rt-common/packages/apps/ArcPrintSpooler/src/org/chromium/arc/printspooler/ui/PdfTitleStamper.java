package org.chromium.arc.printspooler.ui;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sets the /Title of a PDF by appending an incremental update (a new Info
 * dictionary plus a cross-reference section chained to the old one), so the
 * original bytes are untouched.
 *
 * Chrome saves the print document under a temporary name and its PDF viewer
 * titles the tab with the document's /Title, falling back to the file name;
 * that title becomes the print job name. Apps rarely set a title, so without
 * this the job shows up as ".org.chromium.Chromium.XXXXXX".
 */
final class PdfTitleStamper {
    private static final String TAG = "PdfTitleStamper";

    // startxref, the trailer and the xref stream dictionary all sit near the
    // offsets we look at; these bound how much is read.
    private static final int TAIL_SIZE = 4096;
    private static final int XREF_DICT_SIZE = 4096;

    private static final Pattern STARTXREF = Pattern.compile("startxref\\s+(\\d+)");
    private static final Pattern ROOT = Pattern.compile("/Root\\s+(\\d+\\s+\\d+)\\s+R");
    private static final Pattern SIZE = Pattern.compile("/Size\\s+(\\d+)");

    private PdfTitleStamper() {}

    /**
     * Writes |source| with |title| as its /Title to |dest|. Returns false, with
     * |dest| unspecified, if the document cannot be updated this way
     * (encrypted, or a layout this does not understand).
     */
    static boolean stamp(File source, File dest, String title) {
        try {
            Trailer trailer = readTrailer(source);
            if (trailer == null) {
                return false;
            }
            Files.copy(source.toPath(), dest.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            try (FileOutputStream out = new FileOutputStream(dest, true)) {
                out.write(buildUpdate(source.length(), trailer, title));
            }
            return true;
        } catch (IOException e) {
            Log.w(TAG, "Cannot set the document title.", e);
            return false;
        }
    }

    private static final class Trailer {
        long startXref;
        boolean isStream;
        String root;
        int size;
    }

    private static Trailer readTrailer(File file) throws IOException {
        long length = file.length();
        String tail = read(file, Math.max(0, length - TAIL_SIZE), TAIL_SIZE);
        Matcher m = STARTXREF.matcher(tail);
        String startXref = null;
        while (m.find()) {
            startXref = m.group(1);
        }
        if (startXref == null) {
            Log.w(TAG, "No startxref.");
            return null;
        }
        Trailer trailer = new Trailer();
        trailer.startXref = Long.parseLong(startXref);
        if (trailer.startXref <= 0 || trailer.startXref >= length) {
            Log.w(TAG, "Bad startxref " + startXref);
            return null;
        }

        String dict;
        String atXref = read(file, trailer.startXref, XREF_DICT_SIZE);
        if (atXref.trim().startsWith("xref")) {
            // Classic table: the trailer follows it, just before startxref.
            int at = tail.lastIndexOf("trailer");
            if (at < 0) {
                Log.w(TAG, "No trailer.");
                return null;
            }
            dict = tail.substring(at, tail.lastIndexOf("startxref"));
        } else {
            // Cross-reference stream: its dictionary is the trailer.
            int end = atXref.indexOf("stream");
            if (end < 0 || !atXref.contains("/XRef")) {
                Log.w(TAG, "No cross-reference at startxref.");
                return null;
            }
            trailer.isStream = true;
            dict = atXref.substring(0, end);
        }
        if (dict.contains("/Encrypt")) {
            // The new Info strings would have to be encrypted too.
            return null;
        }
        Matcher root = ROOT.matcher(dict);
        Matcher size = SIZE.matcher(dict);
        if (!root.find() || !size.find()) {
            Log.w(TAG, "Trailer without /Root or /Size.");
            return null;
        }
        trailer.root = root.group(1).replaceAll("\\s+", " ");
        trailer.size = Integer.parseInt(size.group(1));
        return trailer;
    }

    private static byte[] buildUpdate(long offset, Trailer trailer, String title) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out, "\n");
        int info = trailer.size;
        long infoOffset = offset + out.size();
        write(out, info + " 0 obj\n<< /Title " + toPdfTextString(title) + " >>\nendobj\n");
        long xrefOffset = offset + out.size();
        if (!trailer.isStream) {
            write(out, "xref\n" + info + " 1\n" + String.format("%010d 00000 n\r\n", infoOffset)
                    + "trailer\n<< /Size " + (info + 1) + " /Root " + trailer.root + " R /Info "
                    + info + " 0 R /Prev " + trailer.startXref + " >>\n");
        } else {
            // A stream-based file must keep using streams for its updates.
            int xref = info + 1;
            byte[] entries = new byte[14];
            putEntry(entries, 0, infoOffset);
            putEntry(entries, 7, xrefOffset);
            write(out, xref + " 0 obj\n<< /Type /XRef /Size " + (xref + 1) + " /Root "
                    + trailer.root + " R /Info " + info + " 0 R /Prev " + trailer.startXref
                    + " /Index [" + info + " 2] /W [1 4 2] /Length " + entries.length
                    + " >>\nstream\n");
            out.write(entries, 0, entries.length);
            write(out, "\nendstream\nendobj\n");
        }
        write(out, "startxref\n" + xrefOffset + "\n%%EOF\n");
        return out.toByteArray();
    }

    // Type 1 (in use) entry: 1 byte type, 4 byte offset, 2 byte generation 0.
    private static void putEntry(byte[] entries, int at, long offset) {
        entries[at] = 1;
        for (int i = 0; i < 4; i++) {
            entries[at + 1 + i] = (byte) (offset >>> (8 * (3 - i)));
        }
    }

    // UTF-16BE with a byte order mark, as a hex string: works for any app
    // name, CJK included, with nothing to escape.
    private static String toPdfTextString(String text) {
        StringBuilder hex = new StringBuilder("<FEFF");
        for (byte b : text.getBytes(StandardCharsets.UTF_16BE)) {
            hex.append(String.format("%02X", b & 0xff));
        }
        return hex.append('>').toString();
    }

    private static String read(File file, long offset, int max) throws IOException {
        try (RandomAccessFile in = new RandomAccessFile(file, "r")) {
            int length = (int) Math.min(max, in.length() - offset);
            byte[] bytes = new byte[Math.max(0, length)];
            in.seek(offset);
            in.readFully(bytes);
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }
    }

    private static void write(ByteArrayOutputStream out, String text) {
        byte[] bytes = text.getBytes(StandardCharsets.ISO_8859_1);
        out.write(bytes, 0, bytes.length);
    }
}

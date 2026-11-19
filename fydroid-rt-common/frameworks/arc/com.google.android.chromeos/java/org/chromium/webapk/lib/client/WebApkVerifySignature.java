/* Open source replacement for org.chromium.webapk.lib.client.WebApkVerifySignature from the proprietary com.google.android.chromeos jar (originally from Chromium). */
package org.chromium.webapk.lib.client;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a WebAPK zip and verifies its comment-embedded ECDSA signature.
 * Port of the open-source Chromium class of the same name.
 */
public class WebApkVerifySignature {
    private static final long CD_SIG = 33639248L;
    private static final long DATA_DESCRIPTOR_SIG = 134695760L;
    private static final long EOCD_SIG = 101010256L;

    public static final int ERROR_OK = 0;
    public static final int ERROR_BAD_APK = 1;
    public static final int ERROR_EXTRA_FIELD_TOO_LARGE = 2;
    public static final int ERROR_FILE_COMMENT_TOO_LARGE = 3;
    public static final int ERROR_INCORRECT_SIGNATURE = 4;
    public static final int ERROR_SIGNATURE_NOT_FOUND = 5;
    public static final int ERROR_TOO_MANY_META_INF_FILES = 6;
    public static final int ERROR_BAD_BLANK_SPACE = 7;
    public static final int ERROR_BAD_V2_SIGNING_BLOCK = 8;

    private static final long LFH_SIG = 67324752L;
    private static final int MAX_EOCD_SIZE = 65536;
    private static final int MAX_EXTRA_LENGTH = 4160;
    private static final int MAX_FILE_COMMENT_LENGTH = 0;
    private static final int MAX_META_INF_FILES = 5;
    private static final int MAX_V2_SIGNING_BLOCK_SIZE = 24576;
    private static final int MIN_EOCD_SIZE = 22;
    private static final String SIGNING_ALGORITHM = "SHA256withECDSA";
    private static final String TAG = "WebApkVerifySignature";
    private static final String V2_SIGNING_MAGIC = "APK Sig Block 42";
    private static final Pattern WEBAPK_COMMENT_PATTERN =
            Pattern.compile("webapk:\\d+:([a-fA-F0-9]+)");

    private ArrayList<Block> mBlocks;
    private final ByteBuffer mBuffer;
    private int mCentralDirOffset;
    private String mComment;
    private int mEndOfCentralDirOffset;
    private int mRecordCount;

    /** Marker annotation for the integer error codes returned by this class. */
    public @interface Error {
    }

    /**
     * Represents a single central-directory record. Sort by filename for
     * signing; sort by {@link #positionComparator} to walk the file in
     * order.
     */
    private static class Block implements Comparable<Block> {
        public static Comparator<Block> positionComparator = new Comparator<Block>() {
            @Override
            public int compare(Block b1, Block b2) {
                return b1.mPosition - b2.mPosition;
            }
        };

        int mCompressedSize;
        String mFilename;
        int mHeaderSize = 0;
        int mPosition;

        Block(String filename, int position, int compressedSize) {
            this.mFilename = filename;
            this.mPosition = position;
            this.mCompressedSize = compressedSize;
        }

        @Override
        public int compareTo(Block o) {
            return this.mFilename.compareTo(o.mFilename);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Block) {
                return this.mFilename.equals(((Block) o).mFilename);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.mFilename.hashCode();
        }
    }

    public WebApkVerifySignature(ByteBuffer buffer) {
        this.mBuffer = buffer;
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Parses the ZIP structure and caches the central directory / comment.
     * Returns an {@code ERROR_*} code, or {@link #ERROR_OK} on success.
     */
    public int read() {
        try {
            int err = readEOCD();
            if (err != 0) {
                return err;
            }
            if (parseCommentSignature(this.mComment) == null) {
                return ERROR_SIGNATURE_NOT_FOUND;
            }
            int err2 = readDirectory();
            if (err2 != 0) {
                return err2;
            }
            return ERROR_OK;
        } catch (Exception e) {
            return ERROR_BAD_APK;
        }
    }

    /**
     * After a successful {@link #read()}, verifies the embedded signature
     * against the given public key.
     */
    public int verifySignature(PublicKey pub) {
        byte[] sig = parseCommentSignature(this.mComment);
        if (sig == null || sig.length == 0) {
            return ERROR_SIGNATURE_NOT_FOUND;
        }
        try {
            Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
            signature.initVerify(pub);
            int err = calculateHash(signature);
            if (err != 0) {
                return err;
            }
            return signature.verify(sig) ? ERROR_OK : ERROR_INCORRECT_SIGNATURE;
        } catch (Exception e) {
            Log.e(TAG, "Exception calculating signature", e);
            return ERROR_INCORRECT_SIGNATURE;
        }
    }

    /**
     * Feeds all non-META-INF file names and compressed contents into
     * {@code sig}, in filename order. Returns {@link #ERROR_OK} on success
     * or an {@code ERROR_*} code otherwise.
     */
    public int calculateHash(Signature sig) throws Exception {
        Collections.sort(this.mBlocks);
        int metaInfCount = 0;
        for (Block block : this.mBlocks) {
            if (block.mFilename.indexOf("META-INF/") == 0) {
                metaInfCount++;
                if (metaInfCount > MAX_META_INF_FILES) {
                    return ERROR_TOO_MANY_META_INF_FILES;
                }
            } else {
                byte[] filename = block.mFilename.getBytes();
                sig.update(intToLittleEndian(filename.length));
                sig.update(filename);
                sig.update(intToLittleEndian(block.mCompressedSize));
                seek(block.mPosition + block.mHeaderSize);
                ByteBuffer slice = this.mBuffer.slice();
                slice.limit(block.mCompressedSize);
                sig.update(slice);
            }
        }
        return ERROR_OK;
    }

    private byte[] intToLittleEndian(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        return buffer.array();
    }

    /**
     * Extracts the hex-encoded signature bytes embedded in a ZIP comment of
     * the form {@code "webapk:<version>:<hex>"}. Returns {@code null} if the
     * comment does not match.
     */
    static byte[] parseCommentSignature(String comment) {
        Matcher m = WEBAPK_COMMENT_PATTERN.matcher(comment);
        if (!m.find()) {
            return null;
        }
        String s = m.group(1);
        return hexToBytes(s);
    }

    private int readEOCD() {
        int start = findEOCDStart();
        if (start < 0) {
            return ERROR_BAD_APK;
        }
        this.mEndOfCentralDirOffset = start;
        seek(start + 10);
        this.mRecordCount = read2();
        seekDelta(4);
        this.mCentralDirOffset = read4();
        int commentLength = read2();
        this.mComment = readString(commentLength);
        if (this.mBuffer.position() < this.mBuffer.limit()) {
            return ERROR_BAD_BLANK_SPACE;
        }
        return ERROR_OK;
    }

    int readDirectory() {
        this.mBlocks = new ArrayList<>(this.mRecordCount);
        seek(this.mCentralDirOffset);
        for (int i = 0; i < this.mRecordCount; i++) {
            int signature = read4();
            if (signature != CD_SIG) {
                Log.d(TAG, "Missing Central Directory Signature");
                return ERROR_BAD_APK;
            }
            seekDelta(16);
            int compressedSize = read4();
            seekDelta(4);
            int fileNameLength = read2();
            int extraLen = read2();
            int fileCommentLength = read2();
            seekDelta(8);
            int offset = read4();
            String filename = readString(fileNameLength);
            seekDelta(extraLen + fileCommentLength);
            if (extraLen > MAX_EXTRA_LENGTH) {
                return ERROR_EXTRA_FIELD_TOO_LARGE;
            }
            if (fileCommentLength > MAX_FILE_COMMENT_LENGTH) {
                return ERROR_FILE_COMMENT_TOO_LARGE;
            }
            this.mBlocks.add(new Block(filename, offset, compressedSize));
        }
        if (this.mBuffer.position() != this.mEndOfCentralDirOffset) {
            return ERROR_BAD_BLANK_SPACE;
        }
        Collections.sort(this.mBlocks, Block.positionComparator);
        int lastByte = 0;
        for (Block block : this.mBlocks) {
            if (block.mPosition != lastByte) {
                return ERROR_BAD_BLANK_SPACE;
            }
            seek(block.mPosition);
            int lfhSignature = read4();
            if (lfhSignature != LFH_SIG) {
                Log.d(TAG, "LFH Signature missing");
                return ERROR_BAD_APK;
            }
            seekDelta(2);
            int flags = read2();
            seekDelta(18);
            int fileNameLength = read2();
            int extraFieldLength = read2();
            if (extraFieldLength > MAX_EXTRA_LENGTH) {
                return ERROR_EXTRA_FIELD_TOO_LARGE;
            }
            block.mHeaderSize =
                    (this.mBuffer.position() - block.mPosition) + fileNameLength + extraFieldLength;
            int nextByte = block.mPosition + block.mHeaderSize + block.mCompressedSize;
            if ((flags & 8) != 0) {
                seek(nextByte);
                if (read4() == DATA_DESCRIPTOR_SIG) {
                    nextByte += 16;
                } else {
                    nextByte += 12;
                }
            }
            lastByte = nextByte;
        }
        if (lastByte != this.mCentralDirOffset) {
            seek(this.mCentralDirOffset - V2_SIGNING_MAGIC.length());
            String magic = readString(V2_SIGNING_MAGIC.length());
            if (V2_SIGNING_MAGIC.equals(magic)) {
                return this.mCentralDirOffset - lastByte > MAX_V2_SIGNING_BLOCK_SIZE
                        ? ERROR_BAD_V2_SIGNING_BLOCK
                        : ERROR_OK;
            }
            return ERROR_BAD_BLANK_SPACE;
        }
        return ERROR_OK;
    }

    private int findEOCDStart() {
        int offset = this.mBuffer.limit() - MIN_EOCD_SIZE;
        int minSearchOffset = Math.max(0, offset - MAX_EOCD_SIZE);
        while (offset >= minSearchOffset) {
            seek(offset);
            if (read4() == EOCD_SIG) {
                return offset;
            }
            offset--;
        }
        return -1;
    }

    private void seek(int offset) {
        this.mBuffer.position(offset);
    }

    private void seekDelta(int delta) {
        ByteBuffer byteBuffer = this.mBuffer;
        byteBuffer.position(byteBuffer.position() + delta);
    }

    private int read2() {
        return this.mBuffer.getShort();
    }

    private int read4() {
        return this.mBuffer.getInt();
    }

    private String readString(int length) {
        if (length <= 0) {
            return "";
        }
        byte[] bytes = new byte[length];
        this.mBuffer.get(bytes);
        return new String(bytes);
    }

    /**
     * Decodes a hex string of even length into its raw byte form. Returns
     * {@code null} if the length is odd.
     */
    static byte[] hexToBytes(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            return null;
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}

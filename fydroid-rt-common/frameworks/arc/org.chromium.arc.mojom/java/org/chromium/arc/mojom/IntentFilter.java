package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class IntentFilter extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] actions;
    public String activityLabel;
    public String activityName;
    public String[] categories;
    public AuthorityEntry[] dataAuthorities;
    public PatternMatcher[] dataPaths;
    public String[] dataSchemes;
    public PatternMatcher[] deprecatedDataSchemeSpecificParts;
    public String[] mimeTypes;
    public String packageName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(56, 10), new DataHeader(64, 21), new DataHeader(72, 38), new DataHeader(80, 39), new DataHeader(88, 41)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[5];
    }

    public IntentFilter() {
        super(88, 41);
    }

    public static IntentFilter decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            IntentFilter intentFilter = new IntentFilter(i);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            intentFilter.actions = new String[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                intentFilter.actions[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
            }
            Decoder pointer2 = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            intentFilter.categories = new String[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                intentFilter.categories[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer2, false);
            }
            Decoder pointer3 = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray3 = pointer3.readDataHeaderForPointerArray(-1);
            intentFilter.dataSchemes = new String[dataHeaderForPointerArray3.elementsOrVersion];
            for (int i4 = 0; i4 < dataHeaderForPointerArray3.elementsOrVersion; i4++) {
                intentFilter.dataSchemes[i4] = GeneratedOutlineSupport.outline1(i4, 8, 8, pointer3, false);
            }
            if (i >= 10) {
                Decoder pointer4 = decoder.readPointer(32, true);
                if (pointer4 == null) {
                    intentFilter.dataAuthorities = null;
                } else {
                    DataHeader dataHeaderForPointerArray4 = pointer4.readDataHeaderForPointerArray(-1);
                    intentFilter.dataAuthorities = new AuthorityEntry[dataHeaderForPointerArray4.elementsOrVersion];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray4.elementsOrVersion; i5++) {
                        intentFilter.dataAuthorities[i5] = AuthorityEntry.decode(GeneratedOutlineSupport.outline2(i5, 8, 8, pointer4, false));
                    }
                }
                Decoder pointer5 = decoder.readPointer(40, true);
                if (pointer5 == null) {
                    intentFilter.dataPaths = null;
                } else {
                    DataHeader dataHeaderForPointerArray5 = pointer5.readDataHeaderForPointerArray(-1);
                    intentFilter.dataPaths = new PatternMatcher[dataHeaderForPointerArray5.elementsOrVersion];
                    for (int i6 = 0; i6 < dataHeaderForPointerArray5.elementsOrVersion; i6++) {
                        intentFilter.dataPaths[i6] = PatternMatcher.decode(GeneratedOutlineSupport.outline2(i6, 8, 8, pointer5, false));
                    }
                }
                Decoder pointer6 = decoder.readPointer(48, true);
                if (pointer6 == null) {
                    intentFilter.deprecatedDataSchemeSpecificParts = null;
                } else {
                    DataHeader dataHeaderForPointerArray6 = pointer6.readDataHeaderForPointerArray(-1);
                    intentFilter.deprecatedDataSchemeSpecificParts = new PatternMatcher[dataHeaderForPointerArray6.elementsOrVersion];
                    for (int i7 = 0; i7 < dataHeaderForPointerArray6.elementsOrVersion; i7++) {
                        intentFilter.deprecatedDataSchemeSpecificParts[i7] = PatternMatcher.decode(GeneratedOutlineSupport.outline2(i7, 8, 8, pointer6, false));
                    }
                }
            }
            if (i >= 21) {
                intentFilter.packageName = decoder.readString(56, true);
            }
            if (i >= 38) {
                Decoder pointer7 = decoder.readPointer(64, true);
                if (pointer7 == null) {
                    intentFilter.mimeTypes = null;
                } else {
                    DataHeader dataHeaderForPointerArray7 = pointer7.readDataHeaderForPointerArray(-1);
                    intentFilter.mimeTypes = new String[dataHeaderForPointerArray7.elementsOrVersion];
                    for (int i8 = 0; i8 < dataHeaderForPointerArray7.elementsOrVersion; i8++) {
                        intentFilter.mimeTypes[i8] = GeneratedOutlineSupport.outline1(i8, 8, 8, pointer7, false);
                    }
                }
            }
            if (i >= 39) {
                intentFilter.activityName = decoder.readString(72, true);
            }
            if (i >= 41) {
                intentFilter.activityLabel = decoder.readString(80, true);
            }
            return intentFilter;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static IntentFilter deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        String[] strArr = this.actions;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.actions;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        String[] strArr3 = this.categories;
        if (strArr3 != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr3.length, 16, -1);
            int iOutline02 = 0;
            while (true) {
                String[] strArr4 = this.categories;
                if (iOutline02 >= strArr4.length) {
                    break;
                }
                iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr4[iOutline02], false, iOutline02, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        String[] strArr5 = this.dataSchemes;
        if (strArr5 != null) {
            Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(strArr5.length, 24, -1);
            int iOutline03 = 0;
            while (true) {
                String[] strArr6 = this.dataSchemes;
                if (iOutline03 >= strArr6.length) {
                    break;
                }
                iOutline03 = GeneratedOutlineSupport.outline0(iOutline03, 8, 8, encoderEncodePointerArray3, strArr6[iOutline03], false, iOutline03, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, false);
        }
        AuthorityEntry[] authorityEntryArr = this.dataAuthorities;
        if (authorityEntryArr != null) {
            Encoder encoderEncodePointerArray4 = encoderAtDataOffset.encodePointerArray(authorityEntryArr.length, 32, -1);
            int i = 0;
            while (true) {
                AuthorityEntry[] authorityEntryArr2 = this.dataAuthorities;
                if (i >= authorityEntryArr2.length) {
                    break;
                }
                encoderEncodePointerArray4.encode((Struct) authorityEntryArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(32, true);
        }
        PatternMatcher[] patternMatcherArr = this.dataPaths;
        if (patternMatcherArr != null) {
            Encoder encoderEncodePointerArray5 = encoderAtDataOffset.encodePointerArray(patternMatcherArr.length, 40, -1);
            int i2 = 0;
            while (true) {
                PatternMatcher[] patternMatcherArr2 = this.dataPaths;
                if (i2 >= patternMatcherArr2.length) {
                    break;
                }
                encoderEncodePointerArray5.encode((Struct) patternMatcherArr2[i2], (i2 * 8) + 8, false);
                i2++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(40, true);
        }
        PatternMatcher[] patternMatcherArr3 = this.deprecatedDataSchemeSpecificParts;
        if (patternMatcherArr3 != null) {
            Encoder encoderEncodePointerArray6 = encoderAtDataOffset.encodePointerArray(patternMatcherArr3.length, 48, -1);
            int i3 = 0;
            while (true) {
                PatternMatcher[] patternMatcherArr4 = this.deprecatedDataSchemeSpecificParts;
                if (i3 >= patternMatcherArr4.length) {
                    break;
                }
                encoderEncodePointerArray6.encode((Struct) patternMatcherArr4[i3], (i3 * 8) + 8, false);
                i3++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(48, true);
        }
        encoderAtDataOffset.encode(this.packageName, 56, true);
        String[] strArr7 = this.mimeTypes;
        if (strArr7 != null) {
            Encoder encoderEncodePointerArray7 = encoderAtDataOffset.encodePointerArray(strArr7.length, 64, -1);
            int iOutline04 = 0;
            while (true) {
                String[] strArr8 = this.mimeTypes;
                if (iOutline04 >= strArr8.length) {
                    break;
                }
                iOutline04 = GeneratedOutlineSupport.outline0(iOutline04, 8, 8, encoderEncodePointerArray7, strArr8[iOutline04], false, iOutline04, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(64, true);
        }
        encoderAtDataOffset.encode(this.activityName, 72, true);
        encoderAtDataOffset.encode(this.activityLabel, 80, true);
    }

    private IntentFilter(int i) {
        super(88, i);
    }

    public static IntentFilter deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}

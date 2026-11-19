/*
 * Open source replacement for com.android.server.arc.input_method_manager.TextInputState
 * from the proprietary com.google.android.chromeos jar.
 *
 * The proprietary class is a hand-written Parcelable (not a stable AIDL
 * parcelable), so the wire format has no size prefix. The writeToParcel /
 * readFromParcel order below matches the proprietary class field-for-field
 * so that binder traffic between this open-source build and the
 * proprietary wayland_service (or vice versa) stays interoperable.
 */
package com.android.server.arc.input_method_manager;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

public final class TextInputState implements Parcelable {
    // Flag values match the ARC++ wire format. Kept private because
    // they are only consumed by setEditorInfo() below.
    private static final int TEXT_INPUT_FLAG_NONE = 0;
    private static final int TEXT_INPUT_FLAG_AUTOCOMPLETE_ON = 1;
    private static final int TEXT_INPUT_FLAG_AUTOCOMPLETE_OFF = 2;
    private static final int TEXT_INPUT_FLAG_AUTOCORRECT_ON = 4;
    private static final int TEXT_INPUT_FLAG_AUTOCORRECT_OFF = 8;
    private static final int TEXT_INPUT_FLAG_SPELLCHECK_ON = 16;
    private static final int TEXT_INPUT_FLAG_SPELLCHECK_OFF = 32;
    private static final int TEXT_INPUT_FLAG_AUTOCAPITALIZE_NONE = 64;
    private static final int TEXT_INPUT_FLAG_AUTOCAPITALIZE_CHARACTERS = 128;
    private static final int TEXT_INPUT_FLAG_AUTOCAPITALIZE_WORDS = 256;
    private static final int TEXT_INPUT_FLAG_AUTOCAPITALIZE_SENTENCES = 512;

    public int mCursorPos;
    public CharSequence mText;
    public int mTextStart;
    public int mTextEnd;
    public int mSelectionStart;
    public int mSelectionEnd;
    public int mTextInputType;
    public boolean mShouldDoLearning;
    public int mFlags;
    public boolean mFirstUpdateAfterOperation;
    public int mCompositionStart;
    public int mCompositionEnd;

    public static final Parcelable.Creator<TextInputState> CREATOR =
            new Parcelable.Creator<TextInputState>() {
        @Override
        public TextInputState createFromParcel(Parcel in) {
            return new TextInputState(in);
        }

        @Override
        public TextInputState[] newArray(int size) {
            return new TextInputState[size];
        }
    };

    public TextInputState() {
    }

    public TextInputState(TextInputState state) {
        if (state != null) {
            this.mCursorPos = state.mCursorPos;
            this.mText = state.mText;
            this.mTextStart = state.mTextStart;
            this.mTextEnd = state.mTextEnd;
            this.mSelectionStart = state.mSelectionStart;
            this.mSelectionEnd = state.mSelectionEnd;
            this.mTextInputType = state.mTextInputType;
            this.mShouldDoLearning = state.mShouldDoLearning;
            this.mFlags = state.mFlags;
            this.mFirstUpdateAfterOperation = state.mFirstUpdateAfterOperation;
            this.mCompositionStart = state.mCompositionStart;
            this.mCompositionEnd = state.mCompositionEnd;
        }
    }

    private TextInputState(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Wire format (must match the proprietary class):
     *   int  mCursorPos
     *   String mText.toString()
     *   int  mTextStart
     *   int  mTextEnd
     *   int  mSelectionStart
     *   int  mSelectionEnd
     *   int  mTextInputType
     *   bool mShouldDoLearning
     *   int  mFlags
     *   bool mFirstUpdateAfterOperation
     *   int  mCompositionStart
     *   int  mCompositionEnd
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mCursorPos);
        out.writeString(this.mText.toString());
        out.writeInt(this.mTextStart);
        out.writeInt(this.mTextEnd);
        out.writeInt(this.mSelectionStart);
        out.writeInt(this.mSelectionEnd);
        out.writeInt(this.mTextInputType);
        out.writeBoolean(this.mShouldDoLearning);
        out.writeInt(this.mFlags);
        out.writeBoolean(this.mFirstUpdateAfterOperation);
        out.writeInt(this.mCompositionStart);
        out.writeInt(this.mCompositionEnd);
    }

    public void readFromParcel(Parcel in) {
        this.mCursorPos = in.readInt();
        this.mText = in.readString();
        this.mTextStart = in.readInt();
        this.mTextEnd = in.readInt();
        this.mSelectionStart = in.readInt();
        this.mSelectionEnd = in.readInt();
        this.mTextInputType = in.readInt();
        this.mShouldDoLearning = in.readBoolean();
        this.mFlags = in.readInt();
        this.mFirstUpdateAfterOperation = in.readBoolean();
        this.mCompositionStart = in.readInt();
        this.mCompositionEnd = in.readInt();
    }

    public void setEditorInfo(EditorInfo outAttrs) {
        outAttrs.inputType = this.mTextInputType;
        outAttrs.initialSelStart = this.mSelectionStart;
        outAttrs.initialSelEnd = this.mSelectionEnd;
        if (!this.mShouldDoLearning) {
            outAttrs.imeOptions |= 16777216; // IME_FLAG_NO_PERSONALIZED_LEARNING
        }
        outAttrs.contentMimeTypes = null;
        setCapsMode(outAttrs);
        if (this.mTextInputType == 1) {
            setSuggestionsMode(outAttrs);
        }
        outAttrs.imeOptions |= 33554432; // IME_FLAG_FORCE_ASCII
    }

    public ExtractedText generateExtractedText() {
        ExtractedText et = new ExtractedText();
        et.partialStartOffset = -1;
        et.partialEndOffset = -1;
        et.startOffset = this.mTextStart;
        et.selectionStart = this.mSelectionStart - this.mTextStart;
        et.selectionEnd = this.mSelectionEnd - this.mTextStart;
        et.text = this.mText;
        et.flags = 0;
        if (et.selectionStart != et.selectionEnd) {
            et.flags |= 2; // FLAG_SELECTING
        }
        return et;
    }

    private void setCapsMode(EditorInfo outAttrs) {
        int i = this.mFlags;
        if ((i & TEXT_INPUT_FLAG_AUTOCAPITALIZE_CHARACTERS) != 0) {
            outAttrs.initialCapsMode = 4096; // TextUtils.CAP_MODE_CHARACTERS
        } else if ((i & TEXT_INPUT_FLAG_AUTOCAPITALIZE_WORDS) != 0) {
            outAttrs.initialCapsMode = 8192; // TextUtils.CAP_MODE_WORDS
        } else if ((i & TEXT_INPUT_FLAG_AUTOCAPITALIZE_SENTENCES) != 0) {
            outAttrs.initialCapsMode = 16384; // TextUtils.CAP_MODE_SENTENCES
        }
    }

    private void setSuggestionsMode(EditorInfo outAttrs) {
        int i = this.mFlags;
        if ((i & TEXT_INPUT_FLAG_AUTOCORRECT_OFF) == 0
                && (i & TEXT_INPUT_FLAG_AUTOCORRECT_ON) != 0) {
            outAttrs.inputType |= 32768; // TYPE_TEXT_FLAG_AUTO_CORRECT
        }
        int i2 = this.mFlags;
        if ((i2 & TEXT_INPUT_FLAG_AUTOCOMPLETE_OFF) == 0
                && (i2 & TEXT_INPUT_FLAG_AUTOCOMPLETE_ON) != 0) {
            outAttrs.inputType |= 65536; // TYPE_TEXT_FLAG_AUTO_COMPLETE
        }
        if ((this.mFlags & TEXT_INPUT_FLAG_SPELLCHECK_OFF) != 0) {
            outAttrs.inputType |= 524288; // TYPE_TEXT_FLAG_NO_SUGGESTIONS
        }
    }
}

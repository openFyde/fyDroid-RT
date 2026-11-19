package org.chromium.arc.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.util.Log;

public final class InputMethodSettingsResultReceiver extends ResultReceiver {
    public static final String IME_ID_INTENT_EXTRA_KEY = "imeId";
    public static final String RESULT_RECEIVER_INTENT_EXTRA_KEY = "resultReceiver";
    private final OnReceiveResultListener mOnReceiveResultListener;

    public interface OnReceiveResultListener {
        void onReceiveInputMethodSettingsResult(String imeId, boolean enabled);
    }

    public InputMethodSettingsResultReceiver(OnReceiveResultListener listener) {
        super(null);
        this.mOnReceiveResultListener = listener;
    }

    public ResultReceiver getResultReceiverForIntent() {
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiver = ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultData == null) {
            Log.e("InputMethodSettingsResultReceiver", "onReceiveResult: Bundle is null.");
            return;
        }
        String imeId = resultData.getString(IME_ID_INTENT_EXTRA_KEY);
        if (imeId == null) {
            Log.e("InputMethodSettingsResultReceiver",
                    "onReceiveResult: imeId is not set in Bundle.");
        } else {
            this.mOnReceiveResultListener.onReceiveInputMethodSettingsResult(
                    imeId, resultCode == 1);
        }
    }
}

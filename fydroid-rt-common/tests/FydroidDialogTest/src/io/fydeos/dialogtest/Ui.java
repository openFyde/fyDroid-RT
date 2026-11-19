package io.fydeos.dialogtest;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;

final class Ui {
    static final String TAG = "FydroidDialogTest";

    static void touch(String who, MotionEvent ev) {
        if (ev.getActionMasked() != MotionEvent.ACTION_MOVE
                && ev.getActionMasked() != MotionEvent.ACTION_HOVER_MOVE) {
            Log.i(TAG, who + " touch " + MotionEvent.actionToString(ev.getAction())
                    + " @" + (int) ev.getRawX() + "," + (int) ev.getRawY());
        }
    }

    static void key(String who, KeyEvent ev) {
        Log.i(TAG, who + " key " + KeyEvent.keyCodeToString(ev.getKeyCode())
                + (ev.getAction() == KeyEvent.ACTION_DOWN ? " down" : " up"));
    }

    static Button button(Context c, LinearLayout parent, String label, Runnable r) {
        Button b = new Button(c);
        b.setText(label);
        b.setOnClickListener(v -> {
            Log.i(TAG, "clicked: " + label);
            r.run();
        });
        parent.addView(b);
        return b;
    }

    static LinearLayout column(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(48, 48, 48, 48);
        return l;
    }
}

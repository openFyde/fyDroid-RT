package io.fydeos.dialogtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class PageActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setTitle(getClass().getSimpleName());
        LinearLayout l = Ui.column(this);
        Ui.button(this, l, "Close page", this::finish);
        setContentView(l);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Ui.touch(getClass().getSimpleName(), ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        Ui.key(getClass().getSimpleName(), ev);
        return super.dispatchKeyEvent(ev);
    }
}

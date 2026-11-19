package io.fydeos.dialogtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout l = Ui.column(this);
        Ui.button(this, l, "Notification permission", this::perm);
        Ui.button(this, l, "AlertDialog", this::alert);
        Ui.button(this, l, "Dialog activity", this::dialogAct);
        // Hardware-key typing targets: plain, password and autocorrect fields.
        l.addView(field("text", InputType.TYPE_CLASS_TEXT));
        l.addView(field("password",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
        l.addView(field("autocorrect", InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT));
        setContentView(l);
        run(getIntent());
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        run(i);
    }

    private void run(Intent i) {
        String mode = i.getStringExtra("mode");
        if ("perm".equals(mode)) perm();
        else if ("alert".equals(mode)) alert();
        else if ("dialogact".equals(mode)) dialogAct();
        else if ("translucentweb".equals(mode)) {
            startActivity(new Intent(this, TranslucentWebActivity.class));
        }
    }

    private EditText field(String hint, int type) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setInputType(type);
        e.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence c, int a, int b, int d) {}
            public void onTextChanged(CharSequence c, int a, int b, int d) {}
            public void afterTextChanged(android.text.Editable ed) {
                android.util.Log.i(Ui.TAG, hint + " text now \"" + ed + "\"");
            }
        });
        return e;
    }

    private void perm() {
        requestPermissions(new String[] {"android.permission.POST_NOTIFICATIONS"}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] p, int[] r) {
        Log.i(Ui.TAG, "permission result " + (r.length > 0 ? r[0] : -99));
    }

    private void alert() {
        new AlertDialog.Builder(this)
                .setTitle("AlertDialog")
                .setMessage("Click OK or Cancel")
                .setPositiveButton("OK", (d, w) -> Log.i(Ui.TAG, "alert OK"))
                .setNegativeButton("Cancel", (d, w) -> Log.i(Ui.TAG, "alert Cancel"))
                .show();
    }

    private void dialogAct() {
        startActivity(new Intent(this, DialogActivity.class));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Ui.touch("main", ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        Ui.key("main", ev);
        return super.dispatchKeyEvent(ev);
    }
}

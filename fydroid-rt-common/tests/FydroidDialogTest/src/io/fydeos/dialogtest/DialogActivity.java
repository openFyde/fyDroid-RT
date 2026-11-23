/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fydeos.dialogtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class DialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setTitle("Log in with TapTap?");
        LinearLayout l = Ui.column(this);
        Ui.button(this, l, "Use (same task)",
                () -> startActivity(new Intent(this, PageActivity.class)));
        Ui.button(this, l, "Use (new task)",
                () -> startActivity(new Intent(this, TaskPageActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
        Ui.button(this, l, "Close", this::finish);
        setContentView(l);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Ui.touch("dialog", ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        Ui.key("dialog", ev);
        return super.dispatchKeyEvent(ev);
    }
}

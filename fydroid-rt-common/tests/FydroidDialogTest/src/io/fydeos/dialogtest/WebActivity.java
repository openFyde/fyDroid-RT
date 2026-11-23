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
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * A WebView with a text, a password and an autocorrect/autocapitalize input;
 * each logs its value on every change.
 */
public class WebActivity extends Activity {
    private static final String PAGE = "<html><body style='font-size:24px'>"
            + "<input id=t placeholder=text style='font-size:24px'><br><br>"
            + "<input id=p type=password placeholder=password style='font-size:24px'><br><br>"
            + "<input id=a autocapitalize=sentences autocorrect=on spellcheck=true"
            + " placeholder=autocorrect style='font-size:24px'>"
            + "<script>for (const id of ['t','p','a']) document.getElementById(id)"
            + ".addEventListener('input', e => console.log(id + ' value now \"'"
            + " + e.target.value + '\" (' + e.inputType + ')'));</script>"
            + "</body></html>";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        WebView w = new WebView(this);
        w.getSettings().setJavaScriptEnabled(true);
        w.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage m) {
                Log.i(Ui.TAG, "web " + m.message());
                return true;
            }
        });
        w.loadData(PAGE, "text/html", "utf-8");
        setContentView(w);
    }
}

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

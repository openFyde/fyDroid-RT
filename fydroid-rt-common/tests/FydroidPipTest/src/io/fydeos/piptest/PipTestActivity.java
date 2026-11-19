/*
 * Copyright (C) 2026 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */
package io.fydeos.piptest;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Rational;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A Picture-in-Picture client with no UI to drive: it enters PIP by itself and
 * logs every state change under the "PipTest" tag, so the whole transition can
 * be read out of logcat over ssh.
 *
 * Everything interesting is a log line, on purpose — the point is to verify the
 * platform's PIP plumbing, and a test that needs a mouse cannot be run on a
 * headless board.
 */
public class PipTestActivity extends Activity {
    private static final String TAG = "PipTest";
    private static final long AUTO_PIP_DELAY_MS = 3000;

    private TextView mText;
    private Button mPipButton;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * The auto-PIP timer is armed once per process, not once per intent.
     * Expanding a PIP window re-launches the task, which delivers a new intent
     * — so re-arming here would drop the window straight back into PIP three
     * seconds after every expand, and there would be no windowed moment in
     * which to press the button.
     */
    private boolean mAutoPipArmed;

    // A media session so the PIP menu's transport row has something real to
    // drive. The PIP menu looks up the active session belonging to the task's
    // package and dims its play/prev/next buttons when there is none, so
    // without this the transport half of the menu cannot be exercised at all.
    private MediaSession mSession;
    private boolean mPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mText = new TextView(this);
        mText.setTextSize(28);
        mText.setGravity(Gravity.CENTER);
        mText.setBackgroundColor(Color.parseColor("#1A73E8"));
        mText.setTextColor(Color.WHITE);
        mText.setText("PIP test");

        // Enter PIP on demand. The auto timer is for scripted runs; this is for
        // driving the PIP window by hand — expand it, press again, as often as
        // you like.
        mPipButton = new Button(this);
        mPipButton.setText("Enter PIP");
        mPipButton.setAllCaps(false);
        mPipButton.setTextSize(20);
        mPipButton.setOnClickListener(v -> enterPip("button"));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(mText, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(mPipButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(root);

        Log.i(TAG, "onCreate: isInPictureInPictureMode=" + isInPictureInPictureMode()
                + " taskId=" + getTaskId());

        startFakeSession();

        // "now" enters PIP from onCreate, "off" stays windowed, anything else
        // (including no extra) enters after a short delay so the window has been
        // mapped and drawn first — that is the realistic case, and the one that
        // exercises a live window changing state.
        applyMode(getIntent(), "onCreate");
    }

    /**
     * A second `am start` on a running instance delivers a new intent instead of
     * re-creating the activity, so the mode extra has to be honoured here too —
     * otherwise repeated test cycles silently do nothing.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        applyMode(intent, "onNewIntent");
    }

    private void applyMode(Intent intent, String from) {
        String mode = intent != null ? intent.getStringExtra("pip") : null;
        Log.i(TAG, from + ": pip=" + mode);
        if ("now".equals(mode)) {
            enterPip("intent extra pip=now (" + from + ")");
        } else if ("off".equals(mode)) {
            Log.i(TAG, "pip=off — staying windowed");
        } else if (mAutoPipArmed) {
            Log.i(TAG, "auto-pip already used this run — press the button instead");
        } else {
            mAutoPipArmed = true;
            mHandler.postDelayed(() -> enterPip("auto after " + AUTO_PIP_DELAY_MS + "ms"),
                    AUTO_PIP_DELAY_MS);
        }
    }

    private void enterPip(String why) {
        if (isInPictureInPictureMode()) {
            Log.i(TAG, "enterPip(" + why + "): already in PIP, skipping");
            return;
        }
        PictureInPictureParams params = new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(16, 9))
                .build();
        boolean ok = enterPictureInPictureMode(params);
        Log.i(TAG, "enterPip(" + why + "): enterPictureInPictureMode returned " + ok
                + " taskId=" + getTaskId());
    }

    /**
     * The signal Phase A is really about: this only fires if the ACTIVITY (not
     * just its task) resolved to WINDOWING_MODE_PINNED.
     */
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode,
            Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        // windowConfiguration (bounds + windowing mode) is @hide, so this stays
        // on public API and reports the size the app actually sees. The
        // authoritative windowing mode comes from
        // `dumpsys activity activities | grep -A2 piptest`.
        Log.i(TAG, "onPictureInPictureModeChanged: inPip=" + isInPictureInPictureMode
                + " screen=" + newConfig.screenWidthDp + "x" + newConfig.screenHeightDp + "dp"
                + " orientation=" + newConfig.orientation);
        if (mText != null) {
            mText.setText(isInPictureInPictureMode ? "in PIP" : "windowed");
        }
        // The button is for the windowed state only: a PIP window should be
        // content, and it is already in PIP anyway.
        if (mPipButton != null) {
            mPipButton.setVisibility(isInPictureInPictureMode ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onPictureInPictureRequested() {
        Log.i(TAG, "onPictureInPictureRequested");
        enterPip("onPictureInPictureRequested");
        return true;
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        // The auto-PIP trigger a phone would use. fydroid never pauses a window
        // on focus loss, so this is expected NOT to fire — phase C replaces it
        // with an occlusion signal. Logged so that assumption stays testable.
        Log.i(TAG, "onUserLeaveHint");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: isInPictureInPictureMode=" + isInPictureInPictureMode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: isInPictureInPictureMode=" + isInPictureInPictureMode());
    }

    /** Publish a playing media session and log every transport command. */
    private void startFakeSession() {
        mSession = new MediaSession(this, "PipTest");
        mSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                mPlaying = true;
                Log.i(TAG, "media: play");
                publishState();
            }

            @Override
            public void onPause() {
                mPlaying = false;
                Log.i(TAG, "media: pause");
                publishState();
            }

            @Override
            public void onSkipToNext() {
                Log.i(TAG, "media: next");
            }

            @Override
            public void onSkipToPrevious() {
                Log.i(TAG, "media: previous");
            }
        });
        mSession.setActive(true);
        publishState();
    }

    private void publishState() {
        if (mSession == null) return;
        mSession.setPlaybackState(new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE
                        | PlaybackState.ACTION_SKIP_TO_NEXT
                        | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(mPlaying ? PlaybackState.STATE_PLAYING
                                   : PlaybackState.STATE_PAUSED, 0, 1.0f)
                .build());
    }

    @Override
    protected void onDestroy() {
        if (mSession != null) {
            mSession.release();
            mSession = null;
        }
        super.onDestroy();
    }
}

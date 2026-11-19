package com.android.server.arc.tts;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import com.android.server.SystemService;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.TtsHost;
import org.chromium.arc.mojom.TtsInstance;
import org.chromium.arc.mojom.TtsUtterance;
import org.chromium.arc.mojom.TtsVoice;
import org.chromium.mojo.system.MojoException;

import java.util.ArrayList;
import java.util.Set;

/**
 * Bridges text-to-speech between Chrome OS and Android's TTS engine.
 *
 * Chrome sends speak/stop requests; we forward to Android's TextToSpeech
 * engine and relay progress events back via TtsHost.
 */
class ArcTtsService implements TtsInstance, ArcService {
    private static final String TAG = "ArcTts";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    // TTS event types matching Chrome's expectations
    private static final int TTS_EVENT_START = 0;
    private static final int TTS_EVENT_END = 1;
    private static final int TTS_EVENT_ERROR = 3;
    private static final int TTS_EVENT_INTERRUPTED = 4;

    private final Context mContext;
    private TtsHost mHost;
    private TextToSpeech mTts;
    private boolean mTtsReady;

    public static class LifeCycle extends SystemService {
        private final ArcTtsService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcTtsService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
        }
    }

    ArcTtsService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- TtsInstance ---

    @Override
    public void init(TtsHost host, InitResponse response) {
        Log.i(TAG, "init: Chrome connected");
        mHost = host;
        response.call();

        // Initialize TTS engine
        initTtsEngine();
    }

    @Override
    public void speak(TtsUtterance utterance) {
        if (!mTtsReady || mTts == null) {
            Log.w(TAG, "TTS not ready, dropping speak request");
            TtsHost host = mHost;
            if (host != null) {
                host.onTtsEvent(utterance.utteranceId, TTS_EVENT_ERROR,
                        0, 0, "TTS engine not ready");
            }
            return;
        }

        Bundle params = new Bundle();
        if (utterance.rate > 0) {
            mTts.setSpeechRate((float) utterance.rate);
        }
        if (utterance.pitch > 0) {
            mTts.setPitch((float) utterance.pitch);
        }

        String utteranceId = String.valueOf(utterance.utteranceId);
        int result = mTts.speak(utterance.text, TextToSpeech.QUEUE_FLUSH,
                params, utteranceId);

        if (result != TextToSpeech.SUCCESS) {
            Log.e(TAG, "TTS speak failed: " + result);
            TtsHost host = mHost;
            if (host != null) {
                host.onTtsEvent(utterance.utteranceId, TTS_EVENT_ERROR,
                        0, 0, "TTS speak failed");
            }
        }
    }

    @Override
    public void stop() {
        if (mTts != null) {
            mTts.stop();
        }
    }

    @Override
    public void refreshVoices() {
        if (!mTtsReady || mTts == null) return;

        TtsHost host = mHost;
        if (host == null) return;

        try {
            Set<Voice> voices = mTts.getVoices();
            if (voices == null) {
                host.onVoicesChanged(new TtsVoice[0]);
                return;
            }

            ArrayList<TtsVoice> voiceList = new ArrayList<>();
            int id = 0;
            for (Voice voice : voices) {
                TtsVoice tv = new TtsVoice();
                tv.id = id++;
                tv.name = voice.getName();
                tv.locale = voice.getLocale().toLanguageTag();
                tv.isNetworkConnectionRequired =
                        voice.isNetworkConnectionRequired();
                voiceList.add(tv);
            }

            host.onVoicesChanged(voiceList.toArray(new TtsVoice[0]));
            Log.i(TAG, "Reported " + voiceList.size() + " voices");
        } catch (Exception e) {
            Log.e(TAG, "Failed to refresh voices", e);
        }
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        mHost = null;
        shutdownTts();
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        mHost = null;
        shutdownTts();
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering TtsInstance");
        host.onTtsInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version);
    }

    // --- TTS engine management ---

    private void initTtsEngine() {
        mTts = new TextToSpeech(mContext, status -> {
            if (status == TextToSpeech.SUCCESS) {
                mTtsReady = true;
                Log.i(TAG, "TTS engine initialized");

                mTts.setOnUtteranceProgressListener(
                        new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        reportEvent(utteranceId, TTS_EVENT_START, 0, 0, "");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        reportEvent(utteranceId, TTS_EVENT_END, 0, 0, "");
                    }

                    @Override
                    public void onError(String utteranceId) {
                        reportEvent(utteranceId, TTS_EVENT_ERROR, 0, 0,
                                "TTS error");
                    }

                    @Override
                    public void onStop(String utteranceId,
                            boolean interrupted) {
                        reportEvent(utteranceId, TTS_EVENT_INTERRUPTED,
                                0, 0, "");
                    }

                    @Override
                    public void onRangeStart(String utteranceId, int start,
                            int end, int frame) {
                        // Chrome doesn't use range events
                    }
                });

                // Send initial voice list
                refreshVoices();
            } else {
                Log.e(TAG, "TTS engine init failed: " + status);
                mTtsReady = false;
            }
        });
    }

    private void reportEvent(String utteranceId, int eventType,
            int charIndex, int length, String errorMsg) {
        TtsHost host = mHost;
        if (host == null) return;

        try {
            int id = Integer.parseInt(utteranceId);
            host.onTtsEvent(id, eventType, charIndex, length, errorMsg);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Invalid utterance ID: " + utteranceId);
        }
    }

    private void shutdownTts() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            mTts = null;
            mTtsReady = false;
        }
    }
}

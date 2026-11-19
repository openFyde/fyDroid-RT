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

package com.fydeos.wvprobe;

import android.media.MediaDrm;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.KeyStatus;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Drives the fydroid Widevine bridge end to end and says which link breaks.
 *
 * Streaming apps are a poor test: Spotify plays unencrypted MP3 and never
 * touches MediaDrm, so nothing exercises the path. This does, deterministically:
 *
 *   isCryptoSchemeSupported  does the framework enumerate our AIDL
 *                            IDrmFactory/widevine at all?
 *   new MediaDrm             DrmFactory.createDrmPlugin
 *   getPropertyString        DrmPlugin properties (L3, HDCP_NONE, ...)
 *   openSession              a local session id; no CDM session yet
 *   getKeyRequest            CdmBridge.ensureCdm -> Chrome CreateCdm ->
 *                            CreateSessionAndGenerateRequest -> OnSessionMessage
 *   provideKeyResponse       Cdm::UpdateSession
 *   getKeyRequest (again)    the buffered-message path: after the service
 *                            certificate lands, the CDM emits the real license
 *                            request through OnSessionMessage and the HAL hands
 *                            it over from its per-session queue
 *   onKeysChange             Cdm client OnSessionKeysChange -> IDrmPluginListener
 *
 * With a real license server this completes an actual Widevine key exchange, so
 * a run that reaches "KEYS USABLE" has proven key delivery, not just plumbing.
 *
 * Read the result with:  logcat -s wvprobe:V
 */
public final class WidevineProbe {

    static final String TAG = "wvprobe";

    private static final UUID WIDEVINE =
            new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
    private static final UUID CLEARKEY =
            new UUID(0xE2719D58A985B3C9L, 0x781AB030AF78D30EL);

    /** Shaka's public unauthenticated Widevine test proxy. */
    private static final String LICENSE_SERVER =
            "https://cwip-shaka-proxy.appspot.com/no_auth";

    /**
     * The cenc PSSH from Shaka's "Angel One" Widevine test asset
     * (shaka-demo-assets/angel-one-widevine/dash.mpd). Carries
     * content_id "shaka_cec2f64aa7890a11", which the proxy above will issue
     * licenses for — a synthetic key id would just be rejected.
     */
    private static final String ANGEL_ONE_PSSH_B64 =
            "AAAAPnBzc2gAAAAA7e+LqXnWSs6jyCfc1R0h7QAAAB4iFnNoYWthX2NlYzJmNjRh"
            + "YTc4OTBhMTFI49yVmwY=";

    /** Rounds of getKeyRequest/provideKeyResponse before giving up. */
    private static final int MAX_ROUNDS = 3;

    private WidevineProbe() {}

    public static void run() {
        say("================ widevine probe ================");
        boolean wv = MediaDrm.isCryptoSchemeSupported(WIDEVINE);
        boolean ck = MediaDrm.isCryptoSchemeSupported(CLEARKEY);
        say("isCryptoSchemeSupported(widevine) = " + wv);
        say("isCryptoSchemeSupported(clearkey) = " + ck + "   (control)");
        if (!wv) {
            say("STOP: the framework does not enumerate the Widevine scheme.");
            say("      The HAL registers and servicemanager finds it in the");
            say("      VINTF manifest, so this would mean libmediadrm is not");
            say("      picking up the AIDL factory.");
            return;
        }

        HandlerThread cbThread = new HandlerThread("wvprobe-cb");
        cbThread.start();
        MediaDrm drm = null;
        byte[] session = null;
        try {
            drm = new MediaDrm(WIDEVINE);
            say("MediaDrm constructed  (DrmFactory.createDrmPlugin OK)");

            for (String p : new String[] {
                    "vendor", "version", "description", "algorithms",
                    "securityLevel", "systemId", "hdcpLevel" }) {
                try {
                    say("  property " + p + " = \"" + drm.getPropertyString(p) + "\"");
                } catch (Throwable t) {
                    say("  property " + p + " -> " + t);
                }
            }

            // Byte-array properties. Netflix reads deviceUniqueId to build its ESN,
            // and reports a failure here as StatusCode -1033
            // (WIDEVINE_INITIAL_PROVISIONING_CHECK_FAILED), which it shows to the
            // user as "could not reach the Netflix service".
            for (String p : new String[] {
                    "deviceUniqueId", "provisioningUniqueId", "serviceCertificate",
                    "metrics" }) {
                try {
                    byte[] v = drm.getPropertyByteArray(p);
                    say("  byte[] " + p + " = " + (v == null ? "null" : v.length + " bytes"));
                } catch (Throwable t) {
                    say("  byte[] " + p + " -> " + t);
                }
            }

            // OnSessionKeysChange -> IDrmPluginListener::onKeysChange -> here.
            final CountDownLatch keysLatch = new CountDownLatch(1);
            final AtomicBoolean usable = new AtomicBoolean(false);
            drm.setOnKeyStatusChangeListener(
                    (MediaDrm md, byte[] sid, List<KeyStatus> keys, boolean hasNewUsableKey) -> {
                        say("onKeysChange: " + keys.size() + " key(s), hasNewUsableKey="
                                + hasNewUsableKey);
                        for (KeyStatus k : keys) {
                            say("    key " + hex(k.getKeyId()) + " status=" + k.getStatusCode());
                            if (k.getStatusCode() == KeyStatus.STATUS_USABLE) usable.set(true);
                        }
                        keysLatch.countDown();
                    }, new Handler(cbThread.getLooper()));

            session = drm.openSession();
            say("openSession -> " + hex(session) + "  (" + session.length + " bytes)");

            byte[] initData = Base64.decode(ANGEL_ONE_PSSH_B64, Base64.DEFAULT);
            say("init data: Angel One cenc PSSH, " + initData.length + " bytes");
            say("license server: " + LICENSE_SERVER);

            boolean done = false;
            for (int round = 1; round <= MAX_ROUNDS && !done; round++) {
                say("---- round " + round + " ----");
                KeyRequest req = drm.getKeyRequest(session, initData, "video/mp4",
                        MediaDrm.KEY_TYPE_STREAMING, new HashMap<String, String>());
                byte[] reqData = req.getData();
                say("getKeyRequest -> " + reqData.length + " bytes"
                        + ", requestType=" + req.getRequestType()
                        + ", first bytes " + hex(head(reqData, 8))
                        + (reqData.length <= 4 ? "  (service certificate request)"
                                              : "  (license request)"));

                byte[] resp = post(LICENSE_SERVER, reqData);
                if (resp == null) {
                    say("STOP: no response from the license server. The bridge is");
                    say("      fine up to here; this is a network problem.");
                    break;
                }
                say("license server -> " + resp.length + " bytes");

                byte[] keySetId = drm.provideKeyResponse(session, resp);
                say("provideKeyResponse OK, keySetId = "
                        + (keySetId == null ? "null"
                                            : "\"" + new String(keySetId) + "\""));

                // Keys only arrive on the license round, not the certificate one.
                if (keysLatch.await(3, TimeUnit.SECONDS)) {
                    done = true;
                }
            }

            if (usable.get()) {
                say("KEYS USABLE — a real Widevine license was fetched and");
                say("installed. Key delivery through the bridge is proven.");
            } else if (done) {
                say("keys changed but none usable — see the statuses above.");
            } else {
                say("no usable keys after " + MAX_ROUNDS + " rounds.");
            }
        } catch (Throwable t) {
            say("FAILED: " + t);
            Throwable c = t.getCause();
            while (c != null) { say("  caused by: " + c); c = c.getCause(); }
            Log.e(TAG, "probe failed", t);
        } finally {
            if (drm != null) {
                try { if (session != null) drm.closeSession(session); } catch (Throwable ignored) {}
                try { drm.close(); } catch (Throwable ignored) {}
            }
            cbThread.quitSafely();
            say("================ probe done ================");
        }
    }

    /** POST raw bytes, return the raw body. null on any failure (logged). */
    private static byte[] post(String url, byte[] body) {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            c.setConnectTimeout(15000);
            c.setReadTimeout(15000);
            c.setRequestProperty("Content-Type", "application/octet-stream");
            try (OutputStream os = c.getOutputStream()) {
                os.write(body);
            }
            int code = c.getResponseCode();
            InputStream is = (code >= 200 && code < 300)
                    ? c.getInputStream() : c.getErrorStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while (is != null && (n = is.read(buf)) > 0) out.write(buf, 0, n);
            byte[] resp = out.toByteArray();
            if (code < 200 || code >= 300) {
                say("  HTTP " + code + ": "
                        + new String(head(resp, 200)));
                return null;
            }
            return resp;
        } catch (Throwable t) {
            say("  POST failed: " + t);
            return null;
        } finally {
            if (c != null) c.disconnect();
        }
    }

    private static byte[] head(byte[] b, int n) {
        int len = Math.min(n, b.length);
        byte[] out = new byte[len];
        System.arraycopy(b, 0, out, 0, len);
        return out;
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private static void say(String s) {
        Log.i(TAG, s);
    }
}

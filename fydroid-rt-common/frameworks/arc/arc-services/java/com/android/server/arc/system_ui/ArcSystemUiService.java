package com.android.server.arc.system_ui;

import android.app.UiModeManager;
import android.content.Context;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.internal.graphics.cam.Cam;
import com.android.server.SystemService;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.SystemUiInstance;
import org.chromium.mojo.system.MojoException;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Receives ChromeOS system UI state over Mojo.
 *
 * Chrome has always pushed its colour scheme at us — ArcSystemUiBridge calls
 * SetDarkThemeStatus whenever the scheme changes and once on connect — but
 * nothing on this side implemented SystemUiInstance, so the instance was never
 * registered and the push had nowhere to land. Anything that wanted to match
 * Chrome's scheme (the notification card, to begin with) had to hardcode one.
 *
 * The status is published as a system property rather than kept in a field:
 * it is read from the notification render path, it is worth being able to see
 * with `getprop` when a card comes out the wrong colour, and it matches how
 * ArcChromeFeatureFlagsService publishes the flags it receives. Not persisted —
 * Chrome re-sends on every connect, and a stale value across a reboot would
 * paint the first cards wrong.
 */
public final class ArcSystemUiService implements SystemUiInstance, ArcService {
    private static final String TAG = "ArcSystemUi";

    // OnSystemUiInstanceReady is MinVersion=62 in arc_bridge.mojom, comfortably
    // below Chrome's v70 on r150, so this needs no forwarder version gate.
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 62;

    /** "1" when ChromeOS is in dark mode. Empty until Chrome first reports. */
    public static final String PROP_DARK_THEME = "sys.fydroid.dark_theme";

    private final Context mContext;
    private final WaylandFrameColorProxy mFrameColorProxy = new WaylandFrameColorProxy();

    public static class LifeCycle extends SystemService {
        private final ArcSystemUiService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcSystemUiService(context);
        }

        @Override
        public void onStart() {
            mService.initialize();
        }

        @Override
        public void onBootPhase(int phase) {
            // UiModeManagerService publishes before PHASE_SYSTEM_SERVICES_READY.
            // This service registers after that phase has been dispatched, so
            // the first one it actually receives is DEVICE_SPECIFIC (520) —
            // hence >= rather than ==, or the callback would never fire.
            if (phase >= SystemService.PHASE_SYSTEM_SERVICES_READY) {
                mService.onSystemServicesReady();
            }
        }
    }

    ArcSystemUiService(Context context) {
        mContext = context;
    }

    /** True when ChromeOS is in dark mode. Defaults to light before Chrome reports. */
    public static boolean isDarkTheme() {
        return SystemProperties.getBoolean(PROP_DARK_THEME, false);
    }

    private void initialize() {
        Slog.i(TAG, "Initializing, requesting ARC bridge proxy");
        // Window captions are painted by ash, not by us, so the derived header
        // has to be handed to the wayland service to reach them. Registered as
        // a plain listener so it inherits the same "only on a real change"
        // gating the notification repaint uses.
        addPaletteListener(mFrameColorProxy);
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- SystemUiInstance ---

    @Override
    public void setDarkThemeStatus(boolean active) {
        Slog.i(TAG, "Chrome colour scheme: " + (active ? "dark" : "light"));
        SystemProperties.set(PROP_DARK_THEME, active ? "1" : "0");
        if (active != sDark) {
            sDark = active;
            notifyPaletteChanged();
        }
        // Deliberately outside the change check, unlike the palette above.
        // Android's night mode is persisted per user, so it survives a reboot
        // independently of anything Chrome told us: a board left dark
        // yesterday comes back with UI_MODE_NIGHT_YES, and if ChromeOS is now
        // light then sDark already matches (both false) and a change-gated
        // call would never correct it. setNightMode is a no-op when the mode
        // is already right, so re-asserting costs nothing.
        applyNightMode();
    }

    // --- Android night mode ---------------------------------------------
    //
    // Separate concern from the palette below, and the reason ARC Settings
    // rendered light on a dark board: the palette only colours what we draw
    // ourselves, whereas every ARC app themes off Configuration's
    // UI_MODE_NIGHT_*. Nothing was driving that from ChromeOS's scheme, so
    // Android sat in day mode permanently. Chrome hands us the scheme, so it
    // is the natural thing to drive it from.

    /** True once the framework services this needs are up. */
    private volatile boolean mSystemServicesReady;
    /** A scheme arrived before the framework was ready; apply it when it is. */
    private volatile boolean mNightModePending;

    void onSystemServicesReady() {
        if (mSystemServicesReady) return;
        mSystemServicesReady = true;
        if (mNightModePending) {
            applyNightMode();
        }
    }

    private void applyNightMode() {
        // Chrome pushes the scheme the moment the bridge is up, which on this
        // board is ~100ms into system_server and well before UiModeManager is
        // usable. Hold the value rather than dropping it.
        if (!mSystemServicesReady) {
            mNightModePending = true;
            return;
        }
        mNightModePending = false;
        final UiModeManager uiMode = mContext.getSystemService(UiModeManager.class);
        if (uiMode == null) {
            Slog.w(TAG, "UiModeManager unavailable; night mode not applied");
            return;
        }
        final boolean dark = sDark;
        try {
            uiMode.setNightMode(dark ? UiModeManager.MODE_NIGHT_YES
                                     : UiModeManager.MODE_NIGHT_NO);
            Slog.i(TAG, "Android night mode -> " + (dark ? "yes" : "no"));
        } catch (Exception e) {
            Slog.e(TAG, "Failed to set Android night mode", e);
        }
    }

    // --- Palette-change listeners -----------------------------------------
    //
    // Anything drawn with these colours has to be redrawn when they change:
    // ChromeOS repaints itself the moment the scheme or the wallpaper accent
    // changes, and a consumer that only reads the palette when it happens to
    // draw is left in the old one. The notification renderer reads per render,
    // so cards already on screen are exactly that case.

    /** Notified after the derived palette changes. Called on the Mojo thread. */
    public interface PaletteListener {
        void onPaletteChanged();
    }

    private static final CopyOnWriteArrayList<PaletteListener> sListeners =
            new CopyOnWriteArrayList<>();
    private static volatile boolean sDark = false;

    public static void addPaletteListener(PaletteListener listener) {
        sListeners.addIfAbsent(listener);
    }

    public static void removePaletteListener(PaletteListener listener) {
        sListeners.remove(listener);
    }

    private static void notifyPaletteChanged() {
        for (PaletteListener l : sListeners) {
            try {
                l.onPaletteChanged();
            } catch (Exception e) {
                Slog.e(TAG, "Palette listener failed", e);
            }
        }
    }

    @Override
    public void setOverlayColor(int sourceColor, int themeStyle) {
        Slog.i(TAG, "Overlay colour: source=" + Integer.toHexString(sourceColor)
                + " style=" + themeStyle);
        final float hue = Cam.fromInt(sourceColor).getHue();
        final boolean changed =
                !sHasSeed || hue != sSeedHue || themeStyle != sThemeStyle;
        sThemeStyle = themeStyle;
        sSeedHue = hue;
        sHasSeed = true;
        if (themeStyle != THEME_STYLE_TONAL_SPOT) {
            // Only TONAL_SPOT's chroma config is reproduced below. The others
            // differ (VIBRANT drives primary chroma to 200, NEUTRAL uses a
            // hue-dependent transform), so the palette will be approximate.
            // Still far closer than the static fallback, which is a fixed blue
            // and would be visibly wrong against any non-blue wallpaper.
            Slog.w(TAG, "Theme style " + themeStyle + " is not TONAL_SPOT; "
                    + "derived palette is approximate");
        }
        Slog.i(TAG, String.format(
                "Derived palette: hue=%.1f surface3 light=#%06X dark=#%06X "
                + "on-surface light=#%06X dark=#%06X",
                sSeedHue, surface3(false) & 0xFFFFFF, surface3(true) & 0xFFFFFF,
                onSurface(false) & 0xFFFFFF, onSurface(true) & 0xFFFFFF));
        Slog.i(TAG, String.format(
                "Derived header: light=#%06X/#%06X dark=#%06X/#%06X "
                + "(active/unfocused)",
                header(false) & 0xFFFFFF, headerUnfocused(false) & 0xFFFFFF,
                header(true) & 0xFFFFFF, headerUnfocused(true) & 0xFFFFFF));
        // Chrome re-sends both messages on every connect, so only a real change
        // repaints — otherwise every ARC restart would redraw every card for
        // nothing.
        if (changed) {
            notifyPaletteChanged();
        }
    }

    // --- Dynamic palette ---------------------------------------------------
    //
    // ChromeOS themes itself from the wallpaper, so the static cros_ref_colors
    // palette is only what it falls back to when dynamic colour is off. Chrome
    // hands us the same seed it themes itself with, so the palette can be
    // rebuilt here rather than guessed, and it tracks the wallpaper for free.
    //
    // Both sides run the same algorithm: Chrome uses material_color_utilities'
    // HCT via ui/color/dynamic_color/palette_factory.cc, and the framework
    // ships the same solver at com.android.internal.graphics.cam. A tonal
    // palette is the seed's HUE at a fixed CHROMA, sampled at a tone; the
    // chroma per palette comes from palette_factory.cc's kTonalSpot config.

    private static final int THEME_STYLE_TONAL_SPOT = 0;
    private static final float CHROMA_PRIMARY = 40.0f;
    private static final float CHROMA_SECONDARY = 16.0f;
    private static final float CHROMA_NEUTRAL = 6.0f;
    private static final float CHROMA_NEUTRAL_VARIANT = 8.0f;

    private static volatile boolean sHasSeed = false;
    private static volatile float sSeedHue = 0.0f;
    private static volatile int sThemeStyle = THEME_STYLE_TONAL_SPOT;

    /** True once Chrome has sent a dynamic-colour seed. */
    public static boolean hasSeed() {
        return sHasSeed;
    }

    private static int primary(float tone) {
        return Cam.getInt(sSeedHue, CHROMA_PRIMARY, tone);
    }

    private static int secondary(float tone) {
        return Cam.getInt(sSeedHue, CHROMA_SECONDARY, tone);
    }

    private static int neutral(float tone) {
        return Cam.getInt(sSeedHue, CHROMA_NEUTRAL, tone);
    }

    private static int neutralVariant(float tone) {
        return Cam.getInt(sSeedHue, CHROMA_NEUTRAL_VARIANT, tone);
    }

    /** Alpha-blend {@code fg} at {@code alpha} over opaque {@code bg}. */
    private static int blend(int fg, float alpha, int bg) {
        int r = Math.round(alpha * ((fg >> 16) & 0xFF) + (1 - alpha) * ((bg >> 16) & 0xFF));
        int g = Math.round(alpha * ((fg >> 8) & 0xFF) + (1 - alpha) * ((bg >> 8) & 0xFF));
        int b = Math.round(alpha * (fg & 0xFF) + (1 - alpha) * (bg & 0xFF));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /**
     * cros.sys.surface3 — the colour ash paints a notification popup with
     * (kCrosSysSystemBaseElevatedOpaque, and the 80%-alpha blurred variant
     * kColorAshShieldAndBase80, are both this hue).
     *
     *   light = blend(rgba(primary40, 0.11), neutral99)
     *   dark  = blend(rgba(primary80, 0.11), neutral10)
     */
    public static int surface3(boolean dark) {
        if (!sHasSeed) return dark ? FALLBACK_SURFACE3_DARK : FALLBACK_SURFACE3_LIGHT;
        return dark ? blend(primary(80.0f), 0.11f, neutral(10.0f))
                    : blend(primary(40.0f), 0.11f, neutral(99.0f));
    }

    /** cros.sys.on-surface — primary text on that surface. */
    public static int onSurface(boolean dark) {
        if (!sHasSeed) return dark ? FALLBACK_ON_SURFACE_DARK : FALLBACK_ON_SURFACE_LIGHT;
        return dark ? neutral(90.0f) : neutral(10.0f);
    }

    /** cros.sys.on-surface-variant — secondary text and icons. */
    public static int onSurfaceVariant(boolean dark) {
        if (!sHasSeed) {
            return dark ? FALLBACK_ON_SURFACE_VARIANT_DARK
                        : FALLBACK_ON_SURFACE_VARIANT_LIGHT;
        }
        return dark ? neutralVariant(80.0f) : neutralVariant(30.0f);
    }

    /**
     * cros.sys.header — the colour Chrome paints its own window title bar.
     *
     * ash_color_mixer.cc maps ui::kColorFrameActive onto kCrosSysHeader, so
     * this is literally the browser frame, and cros_sys_colors.json5 defines
     * it straight off the secondary tonal palette:
     *
     *   light = secondary90
     *   dark  = secondary12
     *
     * The frame is not one of the surface tones: it comes from SECONDARY,
     * chroma 16 in kTonalSpot, so it carries noticeably more of the wallpaper
     * accent than the notification card's surface3 does. That is why an ARC
     * caption left on ash's default (cros.dialog_title_bar_color, blended out
     * of the legacy static cros_colors.json5 and blind to the seed) reads as
     * grey next to a Chrome window on a themed board.
     */
    public static int header(boolean dark) {
        if (!sHasSeed) return dark ? REF_SECONDARY_12 : REF_SECONDARY_90;
        return dark ? secondary(12.0f) : secondary(90.0f);
    }

    /**
     * cros.sys.header-unfocused — ui::kColorFrameInactive, the same title bar
     * on a window that does not have focus. Chrome washes the header out
     * towards a neutral rather than dimming it:
     *
     *   light = blend(rgba(secondary90, 0.28), neutralvariant90)
     *   dark  = blend(rgba(secondary12, 0.6),  neutral25)
     */
    public static int headerUnfocused(boolean dark) {
        return unfocusedFrom(header(dark), dark);
    }

    /**
     * The unfocused counterpart of an arbitrary frame colour.
     *
     * Split out of {@link #headerUnfocused} so an app-coloured caption gets
     * the same focus treatment ChromeOS gives its own: the header-unfocused
     * token is not a dimmed header, it is the header washed towards the
     * neutral, and running an app's colour through the identical blend keeps
     * focus legible on a window we did not choose the colour of.
     */
    public static int unfocusedFrom(int active, boolean dark) {
        return dark ? blend(active, 0.6f, neutral25())
                    : blend(active, 0.28f, neutralVariant90());
    }

    /** Blend targets for the above, falling back to the static ref tones. */
    private static int neutral25() {
        return sHasSeed ? neutral(25.0f) : REF_NEUTRAL_25;
    }

    private static int neutralVariant90() {
        return sHasSeed ? neutralVariant(90.0f) : REF_NEUTRAL_VARIANT_90;
    }

    // Static cros_ref_colors.json5 values, used until a seed arrives (and on a
    // board with dynamic colour off, where they are what ChromeOS itself uses).
    private static final int FALLBACK_SURFACE3_LIGHT = 0xFFE2EAF6;
    private static final int FALLBACK_SURFACE3_DARK = 0xFF2E3137;
    private static final int FALLBACK_ON_SURFACE_LIGHT = 0xFF1F1F1F;
    private static final int FALLBACK_ON_SURFACE_DARK = 0xFFE3E3E3;
    private static final int FALLBACK_ON_SURFACE_VARIANT_LIGHT = 0xFF444746;
    private static final int FALLBACK_ON_SURFACE_VARIANT_DARK = 0xFFC4C7C5;

    // Ref tones the header is built from, for the same no-seed case. Kept as
    // ref values rather than pre-blended results so the seedless path runs the
    // identical blend the dynamic one does — and these ARE what Chrome frames
    // its own windows with when dynamic colour is off, so matching is right.
    private static final int REF_SECONDARY_90 = 0xFFC2E7FF;
    private static final int REF_SECONDARY_12 = 0xFF002238;
    private static final int REF_NEUTRAL_VARIANT_90 = 0xFFE1E3E1;
    private static final int REF_NEUTRAL_25 = 0xFF3C3C3C;

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering instance");
        host.onSystemUiInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

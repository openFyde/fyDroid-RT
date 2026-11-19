package com.android.server.arc.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Slog;
import com.android.server.arc.system_ui.ArcSystemUiService;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.chromium.arc.mojom.ArcBitmap;
import org.chromium.arc.mojom.ArcNotificationButton;
import org.chromium.arc.mojom.ArcNotificationData;
import org.chromium.arc.mojom.ArcNotificationExpandState;
import org.chromium.arc.mojom.ArcNotificationFlags;
import org.chromium.arc.mojom.ArcNotificationMessage;
import org.chromium.arc.mojom.ArcNotificationPriority;
import org.chromium.arc.mojom.ArcNotificationRemoteInputState;
import org.chromium.arc.mojom.ArcNotificationShownContents;
import org.chromium.arc.mojom.ArcNotificationStyle;
import org.chromium.arc.mojom.ArcNotificationType;
import org.chromium.arc.mojom.NotificationsHost;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Manages a shared VirtualDisplay for all notification content rendering.
 * Each notification gets its own window at an x-offset slot on the shared VD.
 * Surface metadata (METADATA_KEY_NOTIFICATION) maps each SurfaceControl to
 * a notification key, which flows through SF → HWC2 → Wayland as
 * zcr_notification_surface_v2.
 *
 * Also renders bitmap snapshots for sending to Chrome via Mojo.
 *
 * All rendering happens on a dedicated HandlerThread to avoid blocking
 * system_server binder threads.
 */
class ArcNotificationRenderer {
    private static final String TAG = "ArcNotifRender";
    private static final int ICON_SIZE_PX = 48;
    private static final int NOTIFICATION_WIDTH_DP = 360;
    // Upper bound for the snapshot bitmap sent to Chrome over Mojo. Chrome
    // renders the message-center popup from this snapshot, so it must be at
    // least the render width (NOTIFICATION_WIDTH_DP × density) — otherwise the
    // snapshot is downsampled and Chrome upscales it back into the column,
    // softening the content ("low density"/blurry notifications). 1280 covers
    // the 720px render at 2.0× (and 1080px at 3.0×) without any width
    // downscale, while still capping pathologically tall expanded views.
    private static final int MAX_SNAPSHOT_SIZE = 1280;
    // DESTROY_CONTENT_ON_REMOVAL | SUPPORTS_TOUCH | OWN_CONTENT_ONLY, plus
    // PUBLIC (1).
    //
    // PUBLIC is what makes notification content readable by a screen reader.
    // A virtual display is private unless asked otherwise, and
    // AccessibilityManagerService.isValidDisplay() drops every private virtual
    // display whose owner uid is not SystemUI's — the exemption written for
    // bubbles. Here the VD is owned by system_server (uid 1000) while fydroid's
    // SystemUI runs as an ordinary app (uid 10033), so the exemption misses and
    // AccessibilityWindowManager never tracks the display: the helper cannot see
    // a single notification window, and ChromeVox has nothing to read.
    //
    // The proprietary ArcSystemUI used 328 (private); it did not need this
    // because its notification surfaces were reachable another way.
    private static final int VIRTUAL_DISPLAY_FLAGS = 328 | 1;

    // Card palette, following Chrome's colour scheme.
    //
    // Chrome paints its own notification card and we draw ours on top of it, so
    // a card that does not use Chrome's colours reads as a foreign rectangle
    // sitting inside the message centre. This was plain white, which nothing in
    // ChromeOS uses in either scheme; the text tones below are the fix, and
    // cardBackgroundColor() explains why the background is now Chrome's own.
    //
    // The palette is DERIVED, not hardcoded: this board themes ChromeOS from
    // the wallpaper, so the static cros_ref_colors values are only a fallback.
    // Chrome hands us its dynamic-colour seed over the same Mojo interface that
    // reports the scheme, and ArcSystemUiService rebuilds the tonal palette
    // from it — see the derivation there. The fallbacks are the STATIC palette,
    // whose primary is blue; on a pink-seeded board they are visibly wrong,
    // which is exactly why the seed is used.
    /**
     * Chrome's current scheme, pushed over Mojo by ArcSystemUiService. Read per
     * render rather than latched: a card rendered after the user flips the
     * scheme gets the new colours. Cards already on screen keep the scheme they
     * were rendered under until the app updates them.
     */
    private static boolean isDarkScheme() {
        return ArcSystemUiService.isDarkTheme();
    }

    private int cardBackgroundColor() {
        // Draw nothing and let Chrome's own card background show through.
        //
        // Chrome already paints the right background under this surface, per
        // context: ArcNotificationView::UpdateBackgroundPainter uses
        // kCrosSysSystemBaseElevated for a popup and kCrosSysSystemOnBase in
        // the tray. With system blur on — which is "more than 4GiB of RAM", so
        // yes on this board — both are TRANSLUCENT (90% and 100% of their tone
        // over a blurred backdrop), so their rendered colour depends on
        // whatever is behind the popup. No opaque bitmap can reproduce that:
        // painting kCrosSysSystemBaseElevatedOpaque got the tone right and
        // still read as a foreign rectangle, visibly lighter than the strip of
        // Chrome's own panel exposed below the card (#F3E7EB vs #E1D5DB on a
        // pink-seeded board, the difference being 10% of the wallpaper).
        //
        // The window is PixelFormat.TRANSLUCENT, so leaving these regions
        // transparent composites onto Chrome's panel and matches it exactly —
        // in the tray as well as in the popup, and it keeps matching when a
        // popup expires and the same card settles into the list, which a baked
        // colour cannot do.
        return Color.TRANSPARENT;
    }

    private static int cardTextColor() {
        return ArcSystemUiService.onSurface(isDarkScheme());
    }

    private static int cardSecondaryTextColor() {
        return ArcSystemUiService.onSurfaceVariant(isDarkScheme());
    }

    private static int cardIconTint() {
        return ArcSystemUiService.onSurfaceVariant(isDarkScheme());
    }

    /**
     * Theme the card content is inflated under. The app's own notification
     * RemoteViews are inflated against this, so it — not just our background —
     * is what decides whether the body text comes out readable.
     */
    private static int cardTheme() {
        return isDarkScheme() ? android.R.style.Theme_Material
                              : android.R.style.Theme_Material_Light;
    }

    // Surface metadata key for notification — matches ARC++ ArcNotificationMetadata
    static final int METADATA_KEY_NOTIFICATION = 0x40000000;

    private final Context mContext;
    private HandlerThread mRenderThread;
    private Handler mRenderHandler;
    private DisplayManager mDisplayManager;
    private Context mDisplayContext;
    private int mRenderWidth;
    private int mDensityDpi;
    private float mDensity;

    // Wayland proxy for notification display setup
    private WaylandNotifSurfaceProxy mWaylandProxy;

    // Shared notification VirtualDisplay
    private VirtualDisplay mSharedNotifVD;
    private ImageReader mNotifSink;  // VD output sink — definite-size Surface
    private HandlerThread mSinkDrainThread;  // drains mNotifSink so SF never stalls
    private Context mSharedNotifContext;
    private WindowManager mSharedNotifWM;
    private int mNotifSlotWidth;

    // Per-notification window tracking
    private final Map<String, NotifWindow> mNotifWindows = new HashMap<>();
    private final TreeSet<Integer> mFreeSlots = new TreeSet<>();
    private int mNextSlot = 0;
    private int mMaxNotifHeight = 1024;  // tall enough for expanded notifications

    static class NotifWindow {
        FrameLayout container;  // stable window root — SurfaceControl persists
        View contentView;       // swappable notification content
        int slot;
        String key;
        volatile boolean metadataSet;
    }

    // Callback for expand/collapse toggle from surface touch
    interface ExpandToggleListener {
        void onExpandToggle(String key);
    }
    private ExpandToggleListener mExpandToggleListener;

    void setExpandToggleListener(ExpandToggleListener listener) {
        mExpandToggleListener = listener;
    }

    // Callback for a body tap on the surface-rendered card. The notification's
    // contentIntent is not part of the inflated RemoteViews (stock Android wires
    // it on ExpandableNotificationRow, not here), so the body has no click
    // handler of its own — fire it via the presenter's BODY_CLICKED path, which
    // sends contentIntent and honors FLAG_AUTO_CANCEL.
    interface BodyClickListener {
        void onBodyClick(String key);
    }
    private BodyClickListener mBodyClickListener;

    void setBodyClickListener(BodyClickListener listener) {
        mBodyClickListener = listener;
    }

    // Provides the number of child notifications for a given group summary.
    interface GroupChildCountProvider {
        int getGroupChildCount(StatusBarNotification summary);
    }
    private GroupChildCountProvider mGroupChildCountProvider;

    void setGroupChildCountProvider(GroupChildCountProvider provider) {
        mGroupChildCountProvider = provider;
    }

    private int getGroupChildCount(StatusBarNotification sbn) {
        GroupChildCountProvider p = mGroupChildCountProvider;
        return p != null ? p.getGroupChildCount(sbn) : 0;
    }

    ArcNotificationRenderer(Context context) {
        mContext = context;
    }

    void initialize() {
        // Enable HWUI in the system process. ThreadedRenderer.initForSystemProcess()
        // sets sRendererEnabled=false on boards where ActivityManager.isHighEndGfx()
        // is false, which makes ViewRootImpl.enableHardwareAcceleration() refuse to
        // create a ThreadedRenderer even for a window that sets
        // FLAG_HARDWARE_ACCELERATED. We GPU-render the per-notification windows (so
        // draws run on HWUI's RenderThread instead of blocking the shared
        // render+input thread in drawSoftware()/lockCanvas() — the inline-reply
        // typing lag), so flip it back on. Only windows that explicitly request
        // FLAG_HARDWARE_ACCELERATED are affected; other system_server windows are
        // unchanged.
        android.view.ThreadedRenderer.sRendererEnabled = true;

        // Create render thread
        mRenderThread = new HandlerThread("ArcNotifRenderer");
        mRenderThread.start();
        mRenderHandler = new Handler(mRenderThread.getLooper());

        // Get default display metrics
        mDisplayManager = mContext.getSystemService(DisplayManager.class);
        Display defaultDisplay = mDisplayManager.getDisplay(Display.DEFAULT_DISPLAY);
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(metrics);
        // Round physical DPI up to the next density bucket (multiple of 160)
        // for crisper notification rendering. E.g. 240 xdpi → 320dpi (xhdpi).
        mDensityDpi = (int) Math.ceil(metrics.xdpi / 160.0f) * 160;
        mDensity = mDensityDpi / 160.0f;

        // Use system context for snapshot rendering — no need for a separate
        // renderer VD. The shared notification VD (created later in
        // initSharedNotifDisplay) must be the first VD so it correctly gets
        // the notification display flag in HWC2.
        mDisplayContext = mContext;

        mRenderWidth = (int) (NOTIFICATION_WIDTH_DP * mDensity);
        Slog.i(TAG, "Initialized, renderWidth=" + mRenderWidth + "px, density=" + mDensity);
    }

    /**
     * Renders a notification snapshot and sends it to Chrome via the host.
     * Posted to the render thread for async execution.
     */
    void renderAndSend(StatusBarNotification sbn, Ranking ranking,
            boolean isUpdate, boolean expanded, NotificationsHost host) {
        mRenderHandler.post(() -> {
            try {
                doRenderAndSend(sbn, ranking, isUpdate, expanded, host);
            } catch (Exception e) {
                Slog.e(TAG, "renderAndSend failed for " + sbn.getKey(), e);
            }
        });
    }

    private void doRenderAndSend(StatusBarNotification sbn, Ranking ranking,
            boolean isUpdate, boolean expanded, NotificationsHost host) {
        String key = sbn.getKey();

        ArcNotificationData data = buildNotificationData(sbn, ranking, expanded);
        if (data == null) {
            Slog.e(TAG, "Failed to build notification data for " + key);
            return;
        }

        try {
            if (isUpdate) {
                host.onNotificationUpdated(data);
            } else {
                host.onNotificationPosted(data);
            }

            // If a live notification window exists, update its View
            NotifWindow nw;
            synchronized (mNotifWindows) {
                nw = mNotifWindows.get(key);
            }
            if (nw != null) {
                updateNotificationView(nw, sbn, expanded);
            }
            Slog.d(TAG, (isUpdate ? "Updated" : "Posted") + ": " + key
                    + " pkg=" + sbn.getPackageName()
                    + " buttons=" + (data.buttons != null ? data.buttons.length : 0)
                    + " actionsEnabled=" + data.isActionEnabled
                    + " expandState=" + data.expandState
                    // Priority decides whether Chrome pops the card, and a
                    // re-render that loses the Ranking silently demotes it —
                    // worth seeing on every render.
                    + " priority=" + data.priority
                    + " renderOnChrome=" + data.renderOnChrome
                    + " snapshot=" + (data.snapshotImage != null
                            ? data.snapshotImage.width + "x" + data.snapshotImage.height : "null")
                    + " style=" + data.style);
        } catch (Exception e) {
            Slog.e(TAG, "Failed to send notification to Chrome: " + key, e);
        }
    }

    // --- Build ArcNotificationData with snapshot ---

    private ArcNotificationData buildNotificationData(StatusBarNotification sbn,
            Ranking ranking, boolean expanded) {
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        ArcNotificationData data = new ArcNotificationData();

        // Core fields
        data.key = sbn.getKey();
        data.packageName = sbn.getPackageName();
        data.time = notification.when;

        // Text content
        CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        data.title = title != null ? title.toString() : "";
        data.message = text != null ? text.toString() : "";

        // BigTextStyle overrides message
        CharSequence bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        if (bigText != null && bigText.length() > 0) {
            data.message = bigText.toString();
        }

        // Notification type
        boolean isGroupSummary = (notification.flags & Notification.FLAG_GROUP_SUMMARY) != 0;
        data.type = isGroupSummary ? ArcNotificationType.BUNDLED : ArcNotificationType.SIMPLE;

        // Style
        data.style = mapNotificationStyle(extras);

        // Priority from ranking importance
        data.priority = convertImportance(ranking.getImportance());

        // Flags
        data.noClear = (notification.flags & Notification.FLAG_NO_CLEAR) != 0;
        data.ongoingEvent = (notification.flags & Notification.FLAG_ONGOING_EVENT) != 0;
        data.isCustomNotification = isCustomNotification(data.style);

        // Media
        data.isMediaNotification = data.style == ArcNotificationStyle.MEDIA_STYLE
                || data.style == ArcNotificationStyle.DECORATED_MEDIA_CUSTOM_VIEW_STYLE
                || extras.getParcelable(Notification.EXTRA_MEDIA_SESSION) != null;

        // Group
        data.groupKey = notification.getGroup();

        // Actions / buttons — send via Mojo so Chrome handles them
        // (including inline reply UI). Surface buttons are hidden to avoid
        // direct PendingIntent fire bypassing the inline reply flow.
        Notification.Action[] actions = notification.actions;
        data.isActionEnabled = actions != null && actions.length > 0;
        data.replyButtonIndex = findReplyButtonIndex(actions);
        data.isInlineReplyEnabled = data.replyButtonIndex >= 0;
        data.buttons = convertActions(actions);

        // Small icon
        Icon smallIcon = notification.getSmallIcon();
        if (smallIcon != null) {
            data.smallIcon = convertIconToBitmap(smallIcon, mDisplayContext);
        }

        // Progress
        data.progressMax = extras.getInt(Notification.EXTRA_PROGRESS_MAX, 0);
        data.progressCurrent = extras.getInt(Notification.EXTRA_PROGRESS, 0);
        data.indeterminateProgress =
                extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, false);

        // Style-specific content
        populateStyleContent(data, extras, mDisplayContext);

        // Expand / display state — check if notification has big content view
        boolean isExpandable = false;
        try {
            Context appCtx;
            try {
                appCtx = mDisplayContext.createPackageContext(sbn.getPackageName(),
                        Context.CONTEXT_RESTRICTED);
            } catch (PackageManager.NameNotFoundException e) {
                appCtx = mDisplayContext;
            }
            Context themed = new ContextThemeWrapper(appCtx,
                    cardTheme());
            Notification.Builder b = Notification.Builder.recoverBuilder(
                    themed, notification);
            isExpandable = b.createBigContentView() != null;
        } catch (Exception e) {
            // ignore
        }
        if (expanded) {
            data.expandState = ArcNotificationExpandState.EXPANDED;
        } else if (isExpandable) {
            data.expandState = ArcNotificationExpandState.COLLAPSED;
        } else {
            data.expandState = ArcNotificationExpandState.FIXED_SIZE;
        }
        data.shownContents = ArcNotificationShownContents.CONTENTS_SHOWN;
        data.remoteInputState = ArcNotificationRemoteInputState.CLOSED;

        // Flags (SUPPORT_SNOOZE)
        data.flags = new ArcNotificationFlags();
        data.flags.value = ArcNotificationFlags.SUPPORT_SNOOZE;

        // Full-surface notifications: the fixed notification VirtualDisplay is
        // deterministically HWC-routed, so the live zcr_notification_surface
        // always presents and Chrome renders it directly. Suppress the snapshot
        // so body taps / action buttons hit the live surface instead of a
        // static bitmap.
        data.snapshotImage = null;
        data.snapshotImagePublic = null;
        data.snapshotImageScale = 1.0f;
        // Chrome v27 doesn't support render_on_chrome (MinVersion=28) and
        // always treats ARC notifications as NOTIFICATION_TYPE_CUSTOM.
        // CUSTOM type requires a Wayland surface for expand/collapse UI.
        data.renderOnChrome = false;

        // App display name
        data.appDisplayName = loadAppName(sbn.getPackageName(), mDisplayContext);

        // Auto-group summaries have empty title/message — use app name and
        // child count so the Mojo side has a label to show.
        if (isGroupSummary) {
            int count = getGroupChildCount(sbn);
            if (count > 0) {
                data.title = data.appDisplayName + " \u00B7 " + count
                        + (count == 1 ? " notification" : " notifications");
            } else if (data.title.isEmpty()) {
                data.title = data.appDisplayName;
            }
        }

        // Accessible name
        StringBuilder accessible = new StringBuilder();
        if (data.title.length() > 0) accessible.append(data.title);
        if (data.message.length() > 0) {
            if (accessible.length() > 0) accessible.append(' ');
            accessible.append(data.message);
        }
        data.accessibleName = accessible.toString();

        // Unused/null fields
        data.deprecatedIconData = new byte[0];
        data.swipeInputRect = null;
        data.childrenData = null;

        return data;
    }

    // --- Snapshot rendering ---

    private ArcBitmap renderSnapshot(StatusBarNotification sbn, boolean expanded) {
        Bitmap bitmap = null;
        try {
            Notification notification = sbn.getNotification();

            View view;
            if (isGroupSummary(sbn)) {
                // Group summary: minimal icon + app name card
                view = inflateGroupSummaryView(sbn, mDisplayContext);
            } else {
                // Create a context for the notification's package
                Context appContext;
                try {
                    appContext = mDisplayContext.createPackageContext(sbn.getPackageName(),
                            Context.CONTEXT_RESTRICTED);
                } catch (PackageManager.NameNotFoundException e) {
                    appContext = mDisplayContext;
                }

                // Wrap with a notification-appropriate theme
                Context themedContext = new ContextThemeWrapper(appContext,
                        cardTheme());

                // Recover the builder and create the content view
                Notification.Builder builder = Notification.Builder.recoverBuilder(
                        themedContext, notification);

                RemoteViews contentView;
                if (expanded) {
                    contentView = builder.createBigContentView();
                    if (contentView == null) {
                        contentView = builder.createContentView();
                    }
                } else {
                    contentView = builder.createContentView();
                }

                if (contentView == null) {
                    Slog.w(TAG, "No content view for " + sbn.getKey());
                    return null;
                }

                // Inflate the RemoteViews into a real View
                view = contentView.apply(themedContext, null);
            }

            // Measure and layout
            int widthSpec = View.MeasureSpec.makeMeasureSpec(mRenderWidth,
                    View.MeasureSpec.EXACTLY);
            int heightSpec = notifHeightSpec();
            view.measure(widthSpec, heightSpec);

            int measuredHeight = view.getMeasuredHeight();
            if (measuredHeight <= 0) {
                measuredHeight = (int) (64 * mDensity);
            }
            view.layout(0, 0, mRenderWidth, measuredHeight);

            // Draw to bitmap
            bitmap = Bitmap.createBitmap(mRenderWidth, measuredHeight,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            Slog.d(TAG, "Rendered snapshot " + mRenderWidth + "x" + measuredHeight
                    + " for " + sbn.getKey() + " expanded=" + expanded);

            // Scale down if exceeds max size
            Bitmap scaled = scaleBitmap(bitmap, MAX_SNAPSHOT_SIZE);
            ArcBitmap result = bitmapToArcBitmap(scaled);
            if (scaled != bitmap) {
                scaled.recycle();
            }
            return result;
        } catch (Exception e) {
            Slog.w(TAG, "Failed to render snapshot for " + sbn.getKey(), e);
            return null;
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    /**
     * Scales a bitmap to fit within maxSize x maxSize, preserving aspect ratio.
     * Returns the original bitmap if already within bounds.
     */
    private static Bitmap scaleBitmap(Bitmap bitmap, int maxSize) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (w <= maxSize && h <= maxSize) {
            return bitmap;
        }
        float scale = Math.min((float) maxSize / w, (float) maxSize / h);
        int newW = Math.max(1, (int) (w * scale));
        int newH = Math.max(1, (int) (h * scale));
        return Bitmap.createScaledBitmap(bitmap, newW, newH, true);
    }

    /**
     * Wraps a content view with action buttons below it, matching the standard
     * Android notification action row appearance.
     */
    private View wrapWithActionButtons(View contentView, Notification notification,
            Context themedContext) {
        LinearLayout container = new LinearLayout(themedContext);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(contentView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout actionRow = new LinearLayout(themedContext);
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        int padH = (int) (4 * mDensity);
        int padV = (int) (4 * mDensity);
        actionRow.setPadding(padH, 0, padH, padV);

        int maxButtons = Math.min(notification.actions.length, 3);
        for (int i = 0; i < maxButtons; i++) {
            Notification.Action action = notification.actions[i];
            if (action.title == null) continue;

            Button button = new Button(themedContext, null,
                    android.R.attr.borderlessButtonStyle);
            button.setText(action.title);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            button.setSingleLine(true);
            button.setEllipsize(TextUtils.TruncateAt.END);

            // Use notification accent color for button text
            int color = notification.color;
            if (color != 0 && color != Notification.COLOR_DEFAULT) {
                button.setTextColor(color);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            actionRow.addView(button, lp);
        }

        container.addView(actionRow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return container;
    }

    // --- Style content ---

    private void populateStyleContent(ArcNotificationData data, Bundle extras,
            Context context) {
        // InboxStyle: text lines
        if (data.style == ArcNotificationStyle.INBOX_STYLE) {
            CharSequence[] textLines = extras.getCharSequenceArray(
                    Notification.EXTRA_TEXT_LINES);
            if (textLines != null) {
                data.texts = new String[textLines.length];
                for (int i = 0; i < textLines.length; i++) {
                    data.texts[i] = textLines[i] != null ? textLines[i].toString() : "";
                }
            }
        }

        // BigPictureStyle: big picture bitmap
        if (data.style == ArcNotificationStyle.BIG_PICTURE_STYLE) {
            Bitmap picture = extras.getParcelable(Notification.EXTRA_PICTURE);
            if (picture != null) {
                data.bigPicture = convertBitmap(picture);
            }
        }

        // MessagingStyle: messages
        if (data.style == ArcNotificationStyle.MESSAGING_STYLE) {
            Parcelable[] msgParcelables = extras.getParcelableArray(
                    Notification.EXTRA_MESSAGES);
            if (msgParcelables != null) {
                List<Notification.MessagingStyle.Message> msgs =
                        Notification.MessagingStyle.Message
                                .getMessagesFromBundleArray(msgParcelables);
                if (msgs != null && !msgs.isEmpty()) {
                    data.messages = new ArcNotificationMessage[msgs.size()];
                    for (int i = 0; i < msgs.size(); i++) {
                        Notification.MessagingStyle.Message msg = msgs.get(i);
                        ArcNotificationMessage arcMsg = new ArcNotificationMessage();
                        CharSequence msgText = msg.getText();
                        arcMsg.message = msgText != null ? msgText.toString() : "";
                        if (msg.getSenderPerson() != null) {
                            CharSequence name = msg.getSenderPerson().getName();
                            arcMsg.senderName = name != null ? name.toString() : "";
                            Icon senderIcon = msg.getSenderPerson().getIcon();
                            if (senderIcon != null) {
                                arcMsg.senderIcon = convertIconToBitmap(senderIcon,
                                        context);
                            }
                        } else {
                            arcMsg.senderName = "";
                        }
                        data.messages[i] = arcMsg;
                    }
                }
            }
        }
    }

    // --- Conversion helpers ---

    static int mapNotificationStyle(Bundle extras) {
        String template = extras.getString(Notification.EXTRA_TEMPLATE);
        if (template == null) return ArcNotificationStyle.NO_STYLE;

        switch (template) {
            case "android.app.Notification$BigPictureStyle":
                return ArcNotificationStyle.BIG_PICTURE_STYLE;
            case "android.app.Notification$BigTextStyle":
                return ArcNotificationStyle.BIG_TEXT_STYLE;
            case "android.app.Notification$MessagingStyle":
                return ArcNotificationStyle.MESSAGING_STYLE;
            case "android.app.Notification$InboxStyle":
                return ArcNotificationStyle.INBOX_STYLE;
            case "android.app.Notification$MediaStyle":
                return ArcNotificationStyle.MEDIA_STYLE;
            case "android.app.Notification$DecoratedCustomViewStyle":
                return ArcNotificationStyle.DECORATED_CUSTOM_VIEW_STYLE;
            case "android.app.Notification$DecoratedMediaCustomViewStyle":
                return ArcNotificationStyle.DECORATED_MEDIA_CUSTOM_VIEW_STYLE;
            default:
                return ArcNotificationStyle.NO_STYLE;
        }
    }

    static boolean isCustomNotification(int style) {
        return style == ArcNotificationStyle.DECORATED_CUSTOM_VIEW_STYLE
                || style == ArcNotificationStyle.DECORATED_MEDIA_CUSTOM_VIEW_STYLE;
    }

    static int convertImportance(int importance) {
        switch (importance) {
            case 0: return ArcNotificationPriority.NONE;
            case 1: return ArcNotificationPriority.MIN;
            case 2: return ArcNotificationPriority.LOW;
            case 4: return ArcNotificationPriority.HIGH;
            case 5: return ArcNotificationPriority.MAX;
            default: return ArcNotificationPriority.DEFAULT;
        }
    }

    static ArcNotificationButton[] convertActions(Notification.Action[] actions) {
        if (actions == null || actions.length == 0) {
            return new ArcNotificationButton[0];
        }
        ArcNotificationButton[] buttons = new ArcNotificationButton[actions.length];
        for (int i = 0; i < actions.length; i++) {
            ArcNotificationButton button = new ArcNotificationButton();
            button.label = actions[i].title != null ? actions[i].title.toString() : "";
            button.buttonPlaceholder = "";
            buttons[i] = button;
        }
        return buttons;
    }

    static int findReplyButtonIndex(Notification.Action[] actions) {
        if (actions == null) return -1;
        for (int i = 0; i < actions.length; i++) {
            RemoteInput[] remoteInputs = actions[i].getRemoteInputs();
            if (remoteInputs != null) {
                for (RemoteInput ri : remoteInputs) {
                    if (ri.getAllowFreeFormInput()) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    static ArcBitmap convertIconToBitmap(Icon icon, Context context) {
        try {
            Drawable d = icon.loadDrawable(context);
            if (d == null) return null;

            Bitmap bmp = Bitmap.createBitmap(ICON_SIZE_PX, ICON_SIZE_PX,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            d.setBounds(0, 0, ICON_SIZE_PX, ICON_SIZE_PX);
            d.draw(canvas);

            ArcBitmap result = bitmapToArcBitmap(bmp);
            bmp.recycle();
            return result;
        } catch (Exception e) {
            Slog.w(TAG, "Failed to convert icon", e);
            return null;
        }
    }

    static ArcBitmap convertBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        try {
            Bitmap argb;
            if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                argb = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            } else {
                argb = bitmap;
            }
            return bitmapToArcBitmap(argb);
        } catch (Exception e) {
            Slog.w(TAG, "Failed to convert bitmap", e);
            return null;
        }
    }

    static ArcBitmap bitmapToArcBitmap(Bitmap bmp) {
        ArcBitmap ab = new ArcBitmap();
        ab.width = bmp.getWidth();
        ab.height = bmp.getHeight();
        ByteBuffer buf = ByteBuffer.allocate(bmp.getByteCount());
        bmp.copyPixelsToBuffer(buf);
        ab.pixelData = buf.array();
        return ab;
    }

    static String loadAppName(String packageName, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            CharSequence label = pm.getApplicationLabel(info);
            return label != null ? label.toString() : packageName;
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    static Drawable loadAppIcon(String packageName, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    static boolean isGroupSummary(StatusBarNotification sbn) {
        return (sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0;
    }

    /**
     * Minimal group summary view: app icon + app display name.
     * Used for auto-group summaries (FLAG_GROUP_SUMMARY) which have no
     * meaningful content of their own.
     */
    private View inflateGroupSummaryView(StatusBarNotification sbn, Context displayCtx) {
        Context themedContext = new ContextThemeWrapper(displayCtx,
                cardTheme());

        LinearLayout row = new LinearLayout(themedContext);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(cardBackgroundColor());
        int pad = (int) (16 * mDensity);
        row.setPadding(pad, pad, pad, pad);

        String pkg = sbn.getPackageName();
        Drawable icon = loadAppIcon(pkg, mDisplayContext);
        if (icon != null) {
            ImageView iv = new ImageView(themedContext);
            iv.setImageDrawable(icon);
            int iconSize = (int) (32 * mDensity);
            LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
                    iconSize, iconSize);
            ivLp.setMarginEnd((int) (12 * mDensity));
            row.addView(iv, ivLp);
        }

        TextView label = new TextView(themedContext);
        String appName = loadAppName(pkg, mDisplayContext);
        int count = getGroupChildCount(sbn);
        CharSequence labelText = appName;
        if (count > 0) {
            labelText = appName + " \u00B7 " + count
                    + (count == 1 ? " notification" : " notifications");
        }
        label.setText(labelText);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        label.setTextColor(cardTextColor());
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(label, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        return row;
    }

    // --- Shared notification VirtualDisplay management ---

    void setWaylandProxy(WaylandNotifSurfaceProxy proxy) {
        mWaylandProxy = proxy;
    }

    /**
     * Creates the shared notification VirtualDisplay. Called once during boot.
     * Must be called AFTER setWaylandProxy().
     */
    void initSharedNotifDisplay() {
        if (mWaylandProxy == null) {
            Slog.e(TAG, "initSharedNotifDisplay: no Wayland proxy");
            return;
        }

        mNotifSlotWidth = mRenderWidth;

        // 1. Arm the notification-display flag with the VD's pixel size BEFORE
        //    creating it, so the composer marks the matching VD as the
        //    notification display (deterministic — a task VD created in the same
        //    window can't steal a plain "next VD" flag).
        mWaylandProxy.setNotificationDisplayId(0, mNotifSlotWidth, mMaxNotifHeight);

        // 2. Create shared VirtualDisplay (initial size = one slot).
        // Use an ImageReader (not SurfaceTexture + setDefaultBufferSize) for the
        // output sink: ImageReader's BufferQueue is configured with the pixel
        // size at construction, so SurfaceFlinger's query(NATIVE_WINDOW_WIDTH/
        // HEIGHT) on the producer always reads back 720x1024. The old
        // setDefaultBufferSize path set the size on the consumer side and raced
        // SF's producer-side query → the VD sometimes came up 0x0 (a 0-pixel
        // DisplayDevice that composited nothing → blank cards).
        //
        // The ImageReader output MUST be drained even though we don't use the
        // composited frames: SurfaceFlinger is the producer into this queue, and
        // if its buffers are never released it stalls after maxImages frames.
        // That back-pressure propagates to the per-notification window surfaces —
        // ViewRootImpl.drawSoftware()'s lockCanvas() then blocks ~200ms waiting
        // for a free buffer, and because rendering shares mRenderThread with the
        // reply field's input handling, every keystroke was serialized behind a
        // blocked draw (the "typing lag"). Drain on a dedicated thread so SF
        // keeps compositing and releases buffers promptly.
        mNotifSink = ImageReader.newInstance(mNotifSlotWidth, mMaxNotifHeight,
                PixelFormat.RGBA_8888, 3);
        mSinkDrainThread = new HandlerThread("ArcNotifSinkDrain");
        mSinkDrainThread.start();
        Handler drainHandler = new Handler(mSinkDrainThread.getLooper());
        mNotifSink.setOnImageAvailableListener(reader -> {
            try {
                android.media.Image img = reader.acquireLatestImage();
                if (img != null) img.close();
            } catch (Exception e) {
                // Ignore — draining is best-effort buffer recycling.
            }
        }, drainHandler);
        Surface surface = mNotifSink.getSurface();

        mSharedNotifVD = mDisplayManager.createVirtualDisplay(
                "ArcNotificationVirtualDisplay",
                mNotifSlotWidth, mMaxNotifHeight, mDensityDpi, surface,
                VIRTUAL_DISPLAY_FLAGS);

        if (mSharedNotifVD == null) {
            Slog.e(TAG, "initSharedNotifDisplay: failed to create VD");
            mNotifSink.close();
            mNotifSink = null;
            return;
        }

        int displayId = mSharedNotifVD.getDisplay().getDisplayId();
        // Update with real displayId (first call used placeholder 0 for flag only)
        mWaylandProxy.setNotificationDisplayId(displayId);
        Slog.i(TAG, "initSharedNotifDisplay: displayId=" + displayId);

        mSharedNotifContext = mContext.createDisplayContext(mSharedNotifVD.getDisplay());
        mSharedNotifWM = mSharedNotifContext.getSystemService(WindowManager.class);
    }

    /**
     * Creates a per-notification window on the shared VD. The window's
     * SurfaceControl gets metadata mapping it to the notification key.
     */
    void createNotificationDisplay(String key, StatusBarNotification sbn, boolean expanded) {
        if (mSharedNotifWM == null) {
            Slog.e(TAG, "createNotificationDisplay: shared VD not initialized");
            return;
        }

        synchronized (mNotifWindows) {
            if (mNotifWindows.containsKey(key)) {
                Slog.d(TAG, "createNotificationDisplay: already exists " + key);
                return;
            }
        }

        mRenderHandler.post(() -> {
            try {
                doCreateNotificationDisplay(key, sbn, expanded);
            } catch (Exception e) {
                Slog.e(TAG, "createNotificationDisplay failed for " + key, e);
            }
        });
    }

    private void doCreateNotificationDisplay(String key, StatusBarNotification sbn,
            boolean expanded) {
        // 1. Allocate slot
        int slot = mFreeSlots.isEmpty() ? mNextSlot++ : mFreeSlots.pollFirst();

        // 2. Create notification content view (pass key for clickable chevrons)
        View contentView = inflateNotificationView(sbn, expanded, mSharedNotifContext, key);
        if (contentView == null) {
            Slog.e(TAG, "Failed to inflate notification view for " + key);
            mFreeSlots.add(slot);
            return;
        }
        // The window is software-rendered (see note below); drawSoftware()
        // crashes on hardware bitmaps, so sanitize the tree first.
        convertHwBitmapsToSoftware(contentView);

        // 3. Pre-measure content to get exact height. Avoids "tall then short"
        //    visual glitch and ensures Chrome popup starts at correct size.
        int widthSpec = View.MeasureSpec.makeMeasureSpec(mNotifSlotWidth,
                View.MeasureSpec.EXACTLY);
        int heightSpec = notifHeightSpec();
        contentView.measure(widthSpec, heightSpec);
        int measuredHeight = alignHeightToScale(
                Math.max(contentView.getMeasuredHeight(), (int) (48 * mDensity)));

        // 4. Wrap in stable FrameLayout — its SurfaceControl persists across updates
        FrameLayout container = new FrameLayout(mSharedNotifContext);
        container.addView(contentView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        // 5. Add to shared VD's WindowManager at x-offset with explicit height.
        // The window is HARDWARE-accelerated: HWUI draws on its own RenderThread
        // instead of blocking the shared render+input thread in
        // ViewRootImpl.drawSoftware()/lockCanvas() (the inline-reply typing lag).
        // The system process disables HWUI by default (see sRendererEnabled flip
        // in initialize()); FLAG_HARDWARE_ACCELERATED + the force private flag
        // make ViewRootImpl create a ThreadedRenderer for this window.
        // (Earlier this looked impossible — the notif layer "stayed empty" — but
        // that was the isOutputBacked metadata short-circuit on the composer
        // side, since fixed; the GPU buffer presents fine once the NotificationKey
        // binds.)
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                mNotifSlotWidth,
                measuredHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);
        lp.privateFlags |=
                WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED;
        lp.x = slot * mNotifSlotWidth;
        lp.y = 0;
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.setTitle("ArcNotif:" + key);
        // setTitle above is only the debug/surface name; AccessibilityWindowInfo
        // .getTitle() reads accessibilityTitle, and without it the window comes
        // through to the accessibility service as title=null. That title is how
        // the ARC accessibility helper recovers which notification a window
        // belongs to, so that Chrome can key the tree by notification_key —
        // without it ChromeVox announces the host card's role ("Dialog") and no
        // content. The helper strips this prefix and does not forward it as the
        // spoken window title.
        lp.accessibilityTitle = "ArcNotif:" + key;

        // 6. Track (before addView so attach listener can find it)
        NotifWindow nw = new NotifWindow();
        nw.container = container;
        nw.contentView = contentView;
        nw.slot = slot;
        nw.key = key;
        synchronized (mNotifWindows) {
            mNotifWindows.put(key, nw);
        }

        // 7. Set surface metadata once after surface is created.
        // Post to let the ViewRootImpl create the SurfaceControl first.
        container.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (!nw.metadataSet) {
                    v.post(() -> {
                        if (!nw.metadataSet) {
                            setNotifSurfaceMetadata(v, key);
                            nw.metadataSet = true;
                        }
                    });
                }
            }
            @Override
            public void onViewDetachedFromWindow(View v) {}
        });

        mSharedNotifWM.addView(container, lp);

        // 8. Resize shared VD
        resizeSharedNotifDisplay();

        Slog.i(TAG, "createNotificationDisplay: " + key + " slot=" + slot
                + " height=" + measuredHeight);
    }

    /**
     * ArcNotificationMetadata — matches ARC++ org.chromium.arc.surfaceflinger.ArcNotificationMetadata.
     * Parceled as writeString(notificationId).
     */
    private void setNotifSurfaceMetadata(View v, String key) {
        ViewRootImpl vri = v.getViewRootImpl();
        if (vri == null) return;
        SurfaceControl sc = vri.getSurfaceControl();
        if (sc == null || !sc.isValid()) return;

        Parcel p = Parcel.obtain();
        p.writeString(key);
        try {
            new SurfaceControl.Transaction()
                .setMetadata(sc, METADATA_KEY_NOTIFICATION, p)
                .apply();
            Slog.d(TAG, "setNotifSurfaceMetadata: key=" + key);
        } finally {
            p.recycle();
        }
    }

    void closeNotificationDisplay(String key) {
        mRenderHandler.post(() -> {
            NotifWindow nw;
            synchronized (mNotifWindows) {
                nw = mNotifWindows.remove(key);
            }
            if (nw == null) return;
            try {
                mSharedNotifWM.removeView(nw.container);
            } catch (Exception e) {
                Slog.w(TAG, "closeNotificationDisplay: removeView failed", e);
            }
            mFreeSlots.add(nw.slot);
            resizeSharedNotifDisplay();
            Slog.i(TAG, "closeNotificationDisplay: " + key);
        });
    }

    private void updateNotificationView(NotifWindow nw, StatusBarNotification sbn,
            boolean expanded) {
        try {
            View newContent = inflateNotificationView(sbn, expanded, mSharedNotifContext, nw.key);
            if (newContent == null) return;
            // Software-rendered window: sanitize hardware bitmaps before draw.
            convertHwBitmapsToSoftware(newContent);

            // Pre-measure the new content to get its height.
            // Chrome uses surface_->GetSize() to size the popup, so the Wayland
            // surface dimensions must change for expand/collapse to work visually.
            int widthSpec = View.MeasureSpec.makeMeasureSpec(mNotifSlotWidth,
                    View.MeasureSpec.EXACTLY);
            int heightSpec = notifHeightSpec();
            newContent.measure(widthSpec, heightSpec);
            int measuredHeight = alignHeightToScale(
                    Math.max(newContent.getMeasuredHeight(), (int) (48 * mDensity)));

            // Swap child within the stable FrameLayout container.
            // The container's SurfaceControl (and metadata) stays alive — no layer churn.
            nw.container.removeAllViews();
            nw.container.addView(newContent, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));
            nw.contentView = newContent;

            // Set explicit pixel height so WindowManager sees a real size change.
            // WRAP_CONTENT → same WRAP_CONTENT is a no-op; explicit height forces
            // window resize → new buffer dimensions → new Wayland surface size →
            // Chrome resizes popup via surface_->GetSize().
            WindowManager.LayoutParams lp =
                    (WindowManager.LayoutParams) nw.container.getLayoutParams();
            if (lp != null) {
                lp.height = measuredHeight;
                mSharedNotifWM.updateViewLayout(nw.container, lp);
            }

            Slog.d(TAG, "updateNotificationView: " + nw.key + " expanded=" + expanded
                    + " height=" + measuredHeight);
        } catch (Exception e) {
            Slog.w(TAG, "updateNotificationView failed for " + nw.key, e);
        }
    }

    /**
     * Recursively replaces hardware bitmaps (Bitmap.Config.HARDWARE) in a view
     * tree with software (ARGB_8888) copies. The notification window is
     * software-rendered (system_server can't drive a hardware ThreadedRenderer
     * for this offscreen VirtualDisplay window — it produces no buffer), and
     * ViewRootImpl.drawSoftware() throws "Software rendering doesn't support
     * hardware bitmaps", crashing system_server. Notification images
     * (large icons, BigPicture) are commonly decoded as HARDWARE bitmaps, so
     * the tree must be sanitized before it is drawn.
     */
    private void convertHwBitmapsToSoftware(View root) {
        if (root == null) return;
        Drawable swSrc = softwareCopyIfHardware(
                (root instanceof ImageView) ? ((ImageView) root).getDrawable() : null);
        if (swSrc != null) {
            ((ImageView) root).setImageDrawable(swSrc);
        }
        Drawable swBg = softwareCopyIfHardware(root.getBackground());
        if (swBg != null) {
            root.setBackground(swBg);
        }
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                convertHwBitmapsToSoftware(vg.getChildAt(i));
            }
        }
    }

    /**
     * Returns a software-backed copy of {@code d} if it is a BitmapDrawable
     * wrapping a hardware bitmap, else null (no replacement needed).
     */
    private Drawable softwareCopyIfHardware(Drawable d) {
        if (!(d instanceof BitmapDrawable)) return null;
        Bitmap bmp = ((BitmapDrawable) d).getBitmap();
        if (bmp == null || bmp.getConfig() != Bitmap.Config.HARDWARE) return null;
        Bitmap soft = bmp.copy(Bitmap.Config.ARGB_8888, false);
        if (soft == null) return null;
        return new BitmapDrawable(mSharedNotifContext.getResources(), soft);
    }

    /**
     * Loads the stock notification expand/collapse chevron. These are the same
     * crisp VectorDrawables the framework uses for the notification expand
     * button ({@code ic_expand_notification} / {@code ic_collapse_notification}),
     * tinted and sized for our surface. They replace the soft, blurry
     * {@code arrow_*_float} bitmaps that read as a fuzzy triangle.
     *
     * @param up true for the collapse chevron ("^"), false for expand ("v")
     */
    /**
     * Wires the expand button that comes with the platform notification
     * template, and reports whether there was one.
     *
     * notification_template_header.xml always includes
     * notification_expand_button and Notification.Builder binds it, so every
     * applied contentView/bigContentView carries one, already pointing the
     * right way — the builder calls setExpanded(false) on the collapsed view
     * and setExpanded(true) on the big one. It is styled as the platform pill,
     * tinted from the notification's own colours, which is what these cards
     * should look like.
     *
     * What it does not have outside SystemUI is a click handler:
     * NotificationContentView normally both manages its visibility and drives
     * the expand. Applied standalone, it just sits there — which is why this
     * used to overlay a chevron of its own, and why a card briefly showed two
     * of them once the card background went transparent and stopped masking
     * the platform pill.
     *
     * wireBodyClick only listens on the root, so this child consumes its own
     * taps before the body-click fires.
     */
    private boolean wireTemplateExpandButton(View root, String notifKey) {
        View expandButton = root.findViewById(com.android.internal.R.id.expand_button);
        if (expandButton == null || notifKey == null) {
            return false;
        }
        expandButton.setOnClickListener(v -> {
            Slog.d(TAG, "Template expand button clicked: " + notifKey);
            if (mExpandToggleListener != null) {
                mExpandToggleListener.onExpandToggle(notifKey);
            }
        });
        return true;
    }

    private Drawable loadChevron(Context ctx, boolean up) {
        Drawable d = ctx.getDrawable(up
                ? com.android.internal.R.drawable.ic_collapse_notification
                : com.android.internal.R.drawable.ic_expand_notification);
        if (d != null) {
            d = d.mutate();
            d.setTint(cardIconTint());
            int sz = (int) (18 * mDensity);
            d.setBounds(0, 0, sz, sz);
        }
        return d;
    }

    /**
     * Rounds a window pixel height up to a multiple of the render scale
     * (mDensityDpi / 160). The notification wl_surface declares
     * buffer_scale = scale on the native side, and Wayland requires the buffer
     * dimensions to be an integer multiple of the buffer scale — an odd height
     * would otherwise be rejected at commit. The slot width (360dp × scale) is
     * always divisible, so only the height needs aligning.
     */
    /**
     * Height spec for measuring a notification view: at most the tallest
     * notification window. Not UNSPECIFIED: ImageFloatingTextView (the
     * BigTextStyle body) takes the spec's size as its available height even
     * then, so a 0 size cut big text to one line and expanded cards never grew.
     */
    private int notifHeightSpec() {
        return View.MeasureSpec.makeMeasureSpec(Math.max(1, mMaxNotifHeight),
                View.MeasureSpec.AT_MOST);
    }

    private int alignHeightToScale(int height) {
        int scale = Math.max(1, mDensityDpi / 160);
        return ((height + scale - 1) / scale) * scale;
    }

    private void resizeSharedNotifDisplay() {
        if (mSharedNotifVD == null) return;
        int slotCount = Math.max(1, mNextSlot);
        int width = slotCount * mNotifSlotWidth;
        int height = Math.max(1, mMaxNotifHeight);
        mSharedNotifVD.resize(width, height, mDensityDpi);
    }

    /**
     * Inflates a notification View for use on a VirtualDisplay.
     *
     * Collapsed: standard createContentView() + inline chevron (keeps
     *            default notification height so Chrome doesn't re-layout).
     * Expanded:  full bigContentView + action buttons + chevron.
     */
    private View inflateNotificationView(StatusBarNotification sbn, boolean expanded,
            Context displayCtx) {
        return inflateNotificationView(sbn, expanded, displayCtx, null);
    }

    private View inflateNotificationView(StatusBarNotification sbn, boolean expanded,
            Context displayCtx, String notifKey) {
        try {
            Notification notification = sbn.getNotification();

            // Group summary: minimal icon + app name card
            if (isGroupSummary(sbn)) {
                return inflateGroupSummaryView(sbn, displayCtx);
            }

            Context appContext;
            try {
                appContext = displayCtx.createPackageContext(sbn.getPackageName(),
                        Context.CONTEXT_RESTRICTED);
            } catch (PackageManager.NameNotFoundException e) {
                appContext = displayCtx;
            }

            Context themedContext = new ContextThemeWrapper(appContext,
                    cardTheme());

            Notification.Builder builder = Notification.Builder.recoverBuilder(
                    themedContext, notification);

            boolean hasBigView = builder.createBigContentView() != null;

            if (!expanded) {
                // --- Collapsed: standard content view + inline chevron ---
                RemoteViews contentView = builder.createContentView();
                if (contentView == null) return null;
                View bodyView = contentView.apply(themedContext, null,
                        makeNotifClickHandler(notification, notifKey));
                boolean templateExpander = wireTemplateExpandButton(bodyView, notifKey);
                wireBodyClick(bodyView, notifKey);

                if (hasBigView && !templateExpander) {
                    // Fallback for a layout with no platform expand button
                    // (custom RemoteViews): overlay our own chevron at
                    // top-right so the body can't cover it.
                    FrameLayout frame = new FrameLayout(displayCtx);
                    frame.setBackgroundColor(cardBackgroundColor());

                    frame.addView(bodyView, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));

                    TextView chevron = new TextView(displayCtx);
                    Drawable chevronIcon = loadChevron(displayCtx, false);
                    if (chevronIcon != null) {
                        chevron.setCompoundDrawables(
                                null, null, null, chevronIcon);
                    }
                    chevron.setTextColor(cardSecondaryTextColor());
                    chevron.setGravity(Gravity.CENTER);
                    int pad = (int) (16 * mDensity);
                    chevron.setPadding(pad, pad, pad, pad);
                    int minTouch = (int) (48 * mDensity);
                    chevron.setMinimumWidth(minTouch);
                    chevron.setMinimumHeight(minTouch);
                    chevron.setBackgroundColor(cardBackgroundColor());
                    if (notifKey != null) {
                        chevron.setClickable(true);
                        chevron.setOnClickListener(v -> {
                            Slog.d(TAG, "Chevron expand clicked: " + notifKey);
                            if (mExpandToggleListener != null) {
                                mExpandToggleListener.onExpandToggle(notifKey);
                            }
                        });
                    }
                    FrameLayout.LayoutParams chevronLp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.TOP | Gravity.END);
                    frame.addView(chevron, chevronLp);

                    return frame;
                }
                return bodyView;
            }

            // --- Expanded: full content + action buttons + chevron ---
            RemoteViews bigView = builder.createBigContentView();
            if (bigView == null) {
                bigView = builder.createContentView();
            }
            if (bigView == null) return null;

            View bodyView = bigView.apply(themedContext, null,
                    makeNotifClickHandler(notification, notifKey));
            boolean templateExpander = wireTemplateExpandButton(bodyView, notifKey);
            wireBodyClick(bodyView, notifKey);
            bodyView.setBackgroundColor(cardBackgroundColor());
            // Override action list background to match panel
            View actionsContainer = bodyView.findViewById(
                    com.android.internal.R.id.actions_container_layout);
            if (actionsContainer != null) {
                actionsContainer.setBackgroundColor(cardBackgroundColor());
            }
            View actions = bodyView.findViewById(
                    com.android.internal.R.id.actions);
            if (actions != null) {
                actions.setBackgroundColor(cardBackgroundColor());
            }

            // Vertical stack so inline reply UI can be appended below body
            LinearLayout verticalStack = new LinearLayout(displayCtx);
            verticalStack.setOrientation(LinearLayout.VERTICAL);
            verticalStack.setBackgroundColor(cardBackgroundColor());
            verticalStack.setTag("notif_stack");
            verticalStack.addView(bodyView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            FrameLayout container = new FrameLayout(displayCtx);
            container.setBackgroundColor(cardBackgroundColor());

            container.addView(verticalStack, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));

            // Same fallback as the collapsed path: only overlay a collapse
            // chevron when the template had no expand button of its own.
            if (hasBigView && !templateExpander) {
                TextView chevron = new TextView(displayCtx);
                Drawable chevronIcon = loadChevron(displayCtx, true);
                if (chevronIcon != null) {
                    chevron.setCompoundDrawables(
                            null, null, null, chevronIcon);
                }
                chevron.setTextColor(cardSecondaryTextColor());
                chevron.setGravity(Gravity.CENTER);
                int pad = (int) (16 * mDensity);
                chevron.setPadding(pad, pad, pad, pad);
                int minTouch = (int) (48 * mDensity);
                chevron.setMinimumWidth(minTouch);
                chevron.setMinimumHeight(minTouch);
                chevron.setBackgroundColor(cardBackgroundColor());
                if (notifKey != null) {
                    chevron.setClickable(true);
                    chevron.setOnClickListener(v -> {
                        Slog.d(TAG, "Chevron collapse clicked: " + notifKey);
                        if (mExpandToggleListener != null) {
                            mExpandToggleListener.onExpandToggle(notifKey);
                        }
                    });
                }
                FrameLayout.LayoutParams chevronLp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP | Gravity.END);
                container.addView(chevron, chevronLp);
            }

            return container;
        } catch (Exception e) {
            Slog.w(TAG, "inflateNotificationView failed for " + sbn.getKey(), e);
            return null;
        }
    }

    /**
     * Creates an OnClickHandler for RemoteViews that intercepts RemoteInput
     * action buttons. Instead of firing the PendingIntent directly (which
     * sends null text), it shows an inline EditText for the user to type.
     * Non-RemoteInput buttons fire normally.
     */
    // fydroid-rt (AOSP-13): RemoteViews.OnClickHandler was renamed to
    // InteractionHandler in 12 (onClickHandler -> onInteraction, same 3-arg
    // lambda: View, PendingIntent, RemoteResponse -> boolean).
    /**
     * Makes the inflated notification body fire its contentIntent on tap.
     *
     * createContentView()/createBigContentView() RemoteViews carry only the
     * app's own setOnClickPendingIntent targets (action buttons), not the
     * notification's contentIntent — stock Android wires that on the host
     * ExpandableNotificationRow. On the surface-rendered card there is no such
     * host, so without this the body tap is delivered to the view tree and
     * silently dropped. Route it through the presenter's BODY_CLICKED path so
     * contentIntent + FLAG_AUTO_CANCEL behave exactly as on a real device.
     *
     * Set on the body root only: child clickable views (action buttons, inline
     * reply EditText) consume their own touches first, so they keep working.
     */
    private void wireBodyClick(View bodyView, String notifKey) {
        if (bodyView == null || notifKey == null) return;
        bodyView.setOnClickListener(v -> {
            if (mBodyClickListener != null) {
                mBodyClickListener.onBodyClick(notifKey);
            }
        });
    }

    private RemoteViews.InteractionHandler makeNotifClickHandler(
            Notification notification, String notifKey) {
        return (view, pendingIntent, response) -> {
            // Check if this PendingIntent belongs to a RemoteInput action
            if (notification.actions != null) {
                for (Notification.Action action : notification.actions) {
                    if (action.actionIntent != null
                            && action.actionIntent.equals(pendingIntent)
                            && action.getRemoteInputs() != null) {
                        for (RemoteInput ri : action.getRemoteInputs()) {
                            if (ri.getAllowFreeFormInput()) {
                                Slog.d(TAG, "Intercepted RemoteInput click: "
                                        + notifKey);
                                showInlineReply(view, action, notifKey);
                                return true;
                            }
                        }
                    }
                }
            }
            // Non-RemoteInput: fire PendingIntent normally
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Slog.w(TAG, "PendingIntent canceled for " + notifKey);
            }
            return true;
        };
    }

    /**
     * Shows an inline reply EditText below the action buttons.
     * Keyboard input routed via Wayland keyboard focus on the notification surface.
     */
    private void showInlineReply(View anchorView, Notification.Action action,
            String notifKey) {
        NotifWindow nw;
        synchronized (mNotifWindows) {
            nw = mNotifWindows.get(notifKey);
        }
        if (nw == null) {
            Slog.w(TAG, "showInlineReply: no window for " + notifKey);
            return;
        }

        // Find the vertical stack tagged by inflateNotificationView (expanded)
        View foundView = nw.container.findViewWithTag("notif_stack");
        if (!(foundView instanceof LinearLayout)) {
            Slog.w(TAG, "showInlineReply: notif_stack not found for " + notifKey);
            return;
        }
        LinearLayout foundParent = (LinearLayout) foundView;
        if (foundParent.findViewWithTag("inline_reply") != null) return;
        final LinearLayout parent = foundParent;

        Context ctx = anchorView.getContext();
        LinearLayout replyRow = new LinearLayout(ctx);
        replyRow.setOrientation(LinearLayout.HORIZONTAL);
        replyRow.setGravity(Gravity.CENTER_VERTICAL);
        replyRow.setTag("inline_reply");
        int pad = (int) (8 * mDensity);
        replyRow.setPadding(pad, pad, pad, pad);
        replyRow.setBackgroundColor(cardBackgroundColor());

        EditText editText = new EditText(ctx);
        RemoteInput[] remoteInputs = action.getRemoteInputs();
        CharSequence hint = null;
        if (remoteInputs != null) {
            for (RemoteInput ri : remoteInputs) {
                if (ri.getAllowFreeFormInput() && ri.getLabel() != null) {
                    hint = ri.getLabel();
                    break;
                }
            }
        }
        editText.setHint(hint != null ? hint : "Reply...");
        editText.setSingleLine(true);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        // Center the single line vertically and keep vertical padding small. The
        // field's height is constrained by the expanded body's share of the card
        // (diagnostic: only ~59px tall), so 20+20px padding left just 19px for a
        // ~38px text line → the glyph tops were clipped. Small padding + a fixed,
        // adequate field height (set on the LayoutParams below) lets the whole
        // line fit and center.
        editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        int evpad = (int) (4 * mDensity);
        editText.setPadding(editText.getPaddingLeft(), evpad,
                editText.getPaddingRight(), evpad);

        Button sendBtn = new Button(ctx, null, android.R.attr.borderlessButtonStyle);
        // Use the standard AOSP send icon (framework-res). The previous
        // Unicode arrow renders as a missing-glyph cross when the chosen
        // typeface does not carry the symbol — which is what users were
        // seeing on the inline reply Send button.
        Drawable sendIcon = ctx.getDrawable(android.R.drawable.ic_menu_send);
        if (sendIcon != null) {
            int sendSize = (int) (24 * mDensity);
            sendIcon.setBounds(0, 0, sendSize, sendSize);
            sendBtn.setCompoundDrawables(sendIcon, null, null, null);
        } else {
            sendBtn.setText("\u27A4");
            sendBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }

        View.OnClickListener sendAction = v -> {
            String replyText = editText.getText().toString();
            if (replyText.isEmpty()) return;
            fireReplyIntent(action, replyText, notifKey);
            parent.removeView(replyRow);
            // Restore FLAG_NOT_FOCUSABLE after reply is done.
            WindowManager.LayoutParams rlp =
                    (WindowManager.LayoutParams) nw.container.getLayoutParams();
            if (rlp != null) {
                rlp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mSharedNotifWM.updateViewLayout(nw.container, rlp);
            }
        };

        sendBtn.setOnClickListener(sendAction);
        editText.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendAction.onClick(tv);
                return true;
            }
            return false;
        });

        replyRow.addView(editText, new LinearLayout.LayoutParams(
                0, (int) (48 * mDensity), 1.0f));
        replyRow.addView(sendBtn, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Pin the expanded body to its current laid-out height before appending
        // the reply row. The big content view is "greedy": given a taller window
        // it expands past its natural height (diagnostic: 315 → 353px), stealing
        // the reply row's vertical space so the row gets squeezed and the field's
        // underline falls below the visible window. Fixing the body's height
        // forces the grown window's extra space into the reply row instead.
        if (parent.getChildCount() > 0) {
            View body = parent.getChildAt(0);
            int bodyH = body.getHeight();
            if (bodyH > 0) {
                LinearLayout.LayoutParams blp =
                        (LinearLayout.LayoutParams) body.getLayoutParams();
                blp.height = bodyH;
                body.setLayoutParams(blp);
            }
        }

        parent.addView(replyRow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Clear FLAG_NOT_FOCUSABLE so the EditText can take focus and bring up
        // the IME (the on-screen keyboard) for the reply. Use ADJUST_NOTHING so
        // WindowManager does NOT pan/resize the card's window when the keyboard
        // shows — that pan was what shifted the reply row up and clipped it; the
        // keyboard is a separate surface and Chrome places the card popup, so the
        // card itself must stay put.
        WindowManager.LayoutParams wlp =
                (WindowManager.LayoutParams) nw.container.getLayoutParams();
        if (wlp != null) {
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wlp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
            mSharedNotifWM.updateViewLayout(nw.container, wlp);
        }

        editText.requestFocus();

        // Make the notification VirtualDisplay the top-focused Android display
        // NOW, before the user types. The on-screen keyboard lives on display 0
        // while the reply field is on the notif VD (display 2); without this the
        // first key targets a non-focused display, and PhoneWindowManager spends
        // it on moveDisplayToTop() (WM log: "Moving non-focused display N to top
        // because a key is targeting it") — so the first character is swallowed
        // even though the caret is already blinking. Pre-focusing the display
        // makes that first key land on the field. moveDisplayToTop() is a
        // WindowManagerFuncs method, not on IWindowManager; in system_server the
        // "window" service binder IS the local WindowManagerService, so cast to it.
        focusNotifDisplay();
        Slog.d(TAG, "showInlineReply: " + notifKey);

        // Grow the window to fit the reply field. A single post()-measure runs
        // before the EditText has fully laid out and under-counts the row,
        // clipping the input frame. Instead measure the *settled* layout: grow
        // the window whenever the natural content height exceeds the current
        // window height, and keep watching until it stabilises (the reply row,
        // hint, and underline are all laid out).
        final Runnable resizeToContent = () -> {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(mNotifSlotWidth,
                    View.MeasureSpec.EXACTLY);
            int heightSpec = notifHeightSpec();
            nw.container.measure(widthSpec, heightSpec);
            int measured = nw.container.getMeasuredHeight();
            int h = alignHeightToScale(measured);
            WindowManager.LayoutParams lp2 =
                    (WindowManager.LayoutParams) nw.container.getLayoutParams();
            if (lp2 != null && lp2.height != h) {
                Slog.d(TAG, "showInlineReply resize: measured=" + measured
                        + " aligned=" + h + " prevWindowH=" + lp2.height);
                lp2.height = h;
                mSharedNotifWM.updateViewLayout(nw.container, lp2);
            }
        };
        // Re-measure across the next couple of layout passes so the final
        // height accounts for the fully-laid-out reply row.
        final ViewTreeObserver vto = nw.container.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int passes = 0;
            @Override
            public void onGlobalLayout() {
                resizeToContent.run();
                if (++passes >= 3) {
                    ViewTreeObserver o = nw.container.getViewTreeObserver();
                    if (o.isAlive()) o.removeOnGlobalLayoutListener(this);
                }
            }
        });
        mRenderHandler.post(resizeToContent);
    }

    /**
     * Moves the notification VirtualDisplay to the top of the Android display
     * focus order so key events land on the reply field instead of being spent
     * moving the display to top (see showInlineReply). No-op if anything is
     * unavailable; never throws into the caller.
     */
    private void focusNotifDisplay() {
        try {
            if (mSharedNotifVD == null) return;
            int displayId = mSharedNotifVD.getDisplay().getDisplayId();
            android.os.IBinder b = android.os.ServiceManager.getService("window");
            if (b instanceof com.android.server.wm.WindowManagerService) {
                ((com.android.server.wm.WindowManagerService) b)
                        .moveDisplayToTop(displayId);
                Slog.d(TAG, "focusNotifDisplay: moved display " + displayId + " to top");
            } else {
                Slog.w(TAG, "focusNotifDisplay: 'window' service is not local WMS");
            }
        } catch (Throwable t) {
            Slog.w(TAG, "focusNotifDisplay failed", t);
        }
    }

    private void fireReplyIntent(Notification.Action action, String replyText,
            String notifKey) {
        if (action.actionIntent == null || action.getRemoteInputs() == null) return;
        try {
            Intent fillIn = new Intent();
            Bundle results = new Bundle();
            for (RemoteInput ri : action.getRemoteInputs()) {
                if (ri.getAllowFreeFormInput()) {
                    results.putString(ri.getResultKey(), replyText);
                }
            }
            RemoteInput.addResultsToIntent(action.getRemoteInputs(), fillIn, results);
            action.actionIntent.send(mContext, 0, fillIn);
            Slog.d(TAG, "Inline reply sent: " + notifKey + " text=" + replyText);
        } catch (PendingIntent.CanceledException e) {
            Slog.w(TAG, "Reply intent canceled: " + notifKey);
        }
    }
}

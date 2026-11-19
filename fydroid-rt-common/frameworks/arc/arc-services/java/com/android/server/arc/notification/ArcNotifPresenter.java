package com.android.server.arc.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Slog;

import org.chromium.arc.mojom.ArcNotificationEvent;
import org.chromium.arc.mojom.NotificationsHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listens for Android notifications and delegates rendering/sending to
 * ArcNotificationRenderer. Handles inbound events from Chrome (click, close,
 * button clicks, expansion toggle).
 */
class ArcNotifPresenter extends NotificationListenerService {
    private static final String TAG = "ArcNotifPresenter";

    private final ArcNotificationService mService;
    private final ArcNotificationRenderer mRenderer;
    private final Map<String, StatusBarNotification> mActiveNotifications = new HashMap<>();
    private final Map<String, Boolean> mExpandedState = new HashMap<>();
    /**
     * Last real Ranking seen for each key.
     *
     * Every re-render path below (group-summary refresh, palette repaint,
     * expansion toggle) used to pass a bare {@code new Ranking()}, whose
     * mImportance is left at 0 — IMPORTANCE_NONE, since the field is only ever
     * assigned by readFromParcel/populate. That converts to
     * ArcNotificationPriority.NONE and Chrome maps it to MIN_PRIORITY, which is
     * below the popup threshold in NotificationList::GetPopupNotifications():
     * the card drops out of the popup set and MessagePopupCollection closes a
     * heads-up that is still on screen. Toggling expansion on a popup dismissed
     * it; a palette change dismissed every popup at once. The demotion also
     * stuck, sinking the card to the bottom of the tray.
     */
    private final Map<String, Ranking> mRankings = new HashMap<>();

    ArcNotifPresenter(ArcNotificationService service, ArcNotificationRenderer renderer) {
        mService = service;
        mRenderer = renderer;
    }

    // --- NotificationListenerService callbacks ---

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        NotificationsHost host = mService.getHost();
        if (host == null) {
            return;
        }

        String key = sbn.getKey();
        Ranking ranking = new Ranking();
        if (rankingMap != null && rankingMap.getRanking(key, ranking)) {
            synchronized (mRankings) {
                mRankings.put(key, ranking);
            }
        } else {
            // No ranking for this post — keep whatever we last knew rather than
            // sending the empty one's IMPORTANCE_NONE.
            ranking = rankingFor(key);
        }

        // Suppress the input-method / on-screen-keyboard notification. When the
        // soft keyboard is up (e.g. for the inline reply field) the IME / system
        // posts a "keyboard" notification; it shouldn't appear as a notification
        // card. Drop it here so it never reaches Chrome.
        if (isInputMethodNotification(sbn, ranking)) {
            Slog.d(TAG, "Suppressed input-method notification: " + key
                    + " pkg=" + sbn.getPackageName());
            return;
        }

        boolean isUpdate;
        synchronized (mActiveNotifications) {
            isUpdate = mActiveNotifications.containsKey(key);
            mActiveNotifications.put(key, sbn);
        }

        boolean expanded;
        synchronized (mExpandedState) {
            expanded = mExpandedState.getOrDefault(key, false);
        }

        mRenderer.renderAndSend(sbn, ranking, isUpdate, expanded, host);

        // If this is a non-summary child, refresh any existing group summary
        // for the same package so its child count label stays accurate.
        if (!isGroupSummary(sbn)) {
            refreshGroupSummaryForPackage(sbn.getPackageName(), host);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap,
            int reason) {
        String key = sbn.getKey();
        boolean wasSummary = isGroupSummary(sbn);
        synchronized (mActiveNotifications) {
            mActiveNotifications.remove(key);
        }
        synchronized (mExpandedState) {
            mExpandedState.remove(key);
        }
        synchronized (mRankings) {
            mRankings.remove(key);
        }

        // Tear down the display-2 card surface. Android-side removals (app
        // cancel, body-tap auto-cancel, timeout) otherwise only notify the
        // Chrome host — closeNotificationDisplay is only reached via the
        // Chrome→Android CLOSED path, so without this the rendered card leaks
        // as a ghost window. Idempotent: no-ops if the window is already gone
        // (e.g. a subsequent CLOSED event for the same key).
        mRenderer.closeNotificationDisplay(key);

        NotificationsHost host = mService.getHost();
        if (host == null) {
            return;
        }

        try {
            host.onNotificationRemoved(key);
            Slog.d(TAG, "Removed: " + key + " reason=" + reason);
        } catch (Exception e) {
            Slog.e(TAG, "Failed to send removal to Chrome: " + key, e);
        }

        // Refresh any remaining summary for the same package.
        if (!wasSummary) {
            refreshGroupSummaryForPackage(sbn.getPackageName(), host);
        }
    }

    /**
     * Ranking changes arrive without a re-post (e.g. the user raises or lowers
     * the channel's importance in Settings), so refresh the cache here or a
     * later re-render would ship a stale priority to Chrome.
     */
    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        if (rankingMap == null) return;
        List<String> keys;
        synchronized (mActiveNotifications) {
            keys = new ArrayList<>(mActiveNotifications.keySet());
        }
        for (String key : keys) {
            Ranking ranking = new Ranking();
            if (rankingMap.getRanking(key, ranking)) {
                synchronized (mRankings) {
                    mRankings.put(key, ranking);
                }
            }
        }
    }

    /**
     * Last known Ranking for a key, for re-render paths that have no RankingMap
     * of their own. Falls back to an empty Ranking only when we never saw one.
     */
    private Ranking rankingFor(String key) {
        synchronized (mRankings) {
            Ranking ranking = mRankings.get(key);
            return ranking != null ? ranking : new Ranking();
        }
    }

    private static boolean isGroupSummary(StatusBarNotification sbn) {
        return (sbn.getNotification().flags
                & android.app.Notification.FLAG_GROUP_SUMMARY) != 0;
    }

    /**
     * True for the on-screen-keyboard / input-method notification, which we
     * never want to render as a card. Matched either by the posting package
     * being the active IME, or by the system keyboard notification channels.
     */
    private boolean isInputMethodNotification(StatusBarNotification sbn, Ranking ranking) {
        String pkg = sbn.getPackageName();
        String imePkg = getImePackage();
        if (imePkg != null && imePkg.equals(pkg)) {
            return true;
        }
        NotificationChannel ch = ranking != null ? ranking.getChannel() : null;
        if (ch != null) {
            String id = ch.getId();
            if ("VIRTUAL_KEYBOARD".equals(id) || "PHYSICAL_KEYBOARD".equals(id)) {
                return true;
            }
        }
        return false;
    }

    /** Package of the currently-selected input method, or null. */
    private String getImePackage() {
        try {
            // NB: this is registered via registerAsSystemService(), which sets
            // mSystemContext but never attaches the ContextWrapper base context,
            // so this.getContentResolver() NPEs. Use the service's real context.
            String s = Settings.Secure.getString(
                    mService.getContext().getContentResolver(),
                    Settings.Secure.DEFAULT_INPUT_METHOD);
            if (!TextUtils.isEmpty(s)) {
                int slash = s.indexOf('/');
                return slash > 0 ? s.substring(0, slash) : s;
            }
        } catch (Exception e) {
            Slog.w(TAG, "getImePackage failed", e);
        }
        return null;
    }

    private void refreshGroupSummaryForPackage(String pkg, NotificationsHost host) {
        StatusBarNotification summary = null;
        synchronized (mActiveNotifications) {
            for (StatusBarNotification other : mActiveNotifications.values()) {
                if (other != null && pkg.equals(other.getPackageName())
                        && isGroupSummary(other)) {
                    summary = other;
                    break;
                }
            }
        }
        if (summary == null) return;
        boolean expanded;
        synchronized (mExpandedState) {
            expanded = mExpandedState.getOrDefault(summary.getKey(), false);
        }
        mRenderer.renderAndSend(summary, rankingFor(summary.getKey()), true, expanded, host);
    }

    // --- Accessors for notification state ---

    StatusBarNotification getActiveNotification(String key) {
        synchronized (mActiveNotifications) {
            return mActiveNotifications.get(key);
        }
    }

    boolean isExpanded(String key) {
        synchronized (mExpandedState) {
            return mExpandedState.getOrDefault(key, false);
        }
    }

    void resetExpandedState(String key) {
        synchronized (mExpandedState) {
            mExpandedState.remove(key);
        }
    }

    /**
     * Counts sibling notifications for a group summary. All non-summary
     * notifications from the same package are considered children,
     * regardless of ranker group assignment.
     */
    int getGroupChildCount(StatusBarNotification summary) {
        if (summary == null) return 0;
        String summaryKey = summary.getKey();
        String pkg = summary.getPackageName();
        int count = 0;
        synchronized (mActiveNotifications) {
            for (StatusBarNotification other : mActiveNotifications.values()) {
                if (other == null) continue;
                if (other.getKey().equals(summaryKey)) continue;
                if (!pkg.equals(other.getPackageName())) continue;
                if ((other.getNotification().flags
                        & android.app.Notification.FLAG_GROUP_SUMMARY) != 0) {
                    continue;
                }
                count++;
            }
        }
        return count;
    }

    // --- Inbound events from Chrome (via ArcNotificationInstance) ---

    /**
     * Re-render every notification currently on screen.
     *
     * The card palette is read at render time, so a card already showing keeps
     * whatever colours it was drawn with — flipping the ChromeOS scheme or
     * changing the wallpaper accent left the existing cards in the old palette
     * until their app happened to update them. Called from the palette listener
     * wired in ArcNotificationService.
     *
     * Each card goes through the same path an app update takes (isUpdate=true,
     * preserving its expanded state), and renderAndSend posts to the render
     * thread, so this is safe to call from the Mojo thread that delivers the
     * palette change.
     */
    void repaintAll() {
        NotificationsHost host = mService.getHost();
        if (host == null) return;

        List<StatusBarNotification> active;
        synchronized (mActiveNotifications) {
            active = new ArrayList<>(mActiveNotifications.values());
        }
        if (active.isEmpty()) return;

        Slog.i(TAG, "Palette changed — repainting " + active.size()
                + " notification(s)");
        for (StatusBarNotification sbn : active) {
            boolean expanded;
            synchronized (mExpandedState) {
                expanded = mExpandedState.getOrDefault(sbn.getKey(), false);
            }
            mRenderer.renderAndSend(sbn, rankingFor(sbn.getKey()), true, expanded, host);
        }
    }

    void handleNotificationEvent(String key, int eventType) {
        StatusBarNotification sbn;
        synchronized (mActiveNotifications) {
            sbn = mActiveNotifications.get(key);
        }

        switch (eventType) {
            case ArcNotificationEvent.BODY_CLICKED:
                if (sbn != null) {
                    Notification n = sbn.getNotification();
                    PendingIntent contentIntent = n.contentIntent;
                    boolean autoCancel = (n.flags & Notification.FLAG_AUTO_CANCEL) != 0;
                    if (contentIntent != null) {
                        Slog.i(TAG, "BODY_CLICKED " + key + " isActivity="
                                + contentIntent.isActivity() + " creator="
                                + contentIntent.getCreatorPackage() + " flags=0x"
                                + Integer.toHexString(n.flags));
                        try {
                            contentIntent.send();
                            Slog.i(TAG, "Fired contentIntent for " + key);
                        } catch (PendingIntent.CanceledException e) {
                            Slog.w(TAG, "contentIntent CANCELED for " + key, e);
                        }
                    } else {
                        Slog.i(TAG, "BODY_CLICKED " + key + " has no contentIntent"
                                + " (autoCancel=" + autoCancel + ")");
                    }
                    // Honor FLAG_AUTO_CANCEL — clicking the body dismisses it,
                    // matching standard Android notification behavior.
                    if (autoCancel) {
                        cancelNotification(key);
                        Slog.i(TAG, "Auto-canceled notification " + key);
                    }
                }
                break;

            case ArcNotificationEvent.CLOSED:
                try {
                    cancelNotification(key);
                    Slog.d(TAG, "Canceled notification: " + key);
                } catch (Exception e) {
                    Slog.w(TAG, "Failed to cancel notification: " + key, e);
                }
                break;

            case ArcNotificationEvent.TOGGLE_EXPANSION:
                boolean expanded;
                synchronized (mExpandedState) {
                    boolean wasExpanded = mExpandedState.getOrDefault(key, false);
                    expanded = !wasExpanded;
                    mExpandedState.put(key, expanded);
                }
                if (sbn != null) {
                    NotificationsHost host = mService.getHost();
                    if (host != null) {
                        mRenderer.renderAndSend(sbn, rankingFor(key), true, expanded, host);
                        Slog.d(TAG, "Toggle expansion for " + key
                                + " expanded=" + expanded);
                    }
                }
                break;

            case ArcNotificationEvent.ACTIVATED:
                Slog.d(TAG, "Notification activated: " + key);
                break;

            case ArcNotificationEvent.DEACTIVATED:
                Slog.d(TAG, "Notification deactivated: " + key);
                break;

            default:
                Slog.d(TAG, "Unhandled event " + eventType + " for " + key);
                break;
        }
    }

    void handleButtonClick(String key, int buttonIndex, String replyText) {
        StatusBarNotification sbn;
        synchronized (mActiveNotifications) {
            sbn = mActiveNotifications.get(key);
        }
        if (sbn == null) {
            Slog.w(TAG, "handleButtonClick: unknown notification " + key);
            return;
        }

        Notification.Action[] actions = sbn.getNotification().actions;
        if (actions == null || buttonIndex < 0 || buttonIndex >= actions.length) {
            Slog.w(TAG, "handleButtonClick: invalid button index " + buttonIndex
                    + " for " + key);
            return;
        }

        Notification.Action action = actions[buttonIndex];
        PendingIntent actionIntent = action.actionIntent;
        if (actionIntent == null) {
            Slog.w(TAG, "handleButtonClick: no actionIntent for button " + buttonIndex);
            return;
        }

        try {
            if (replyText != null && !replyText.isEmpty()
                    && action.getRemoteInputs() != null) {
                // Inline reply: add RemoteInput result to fill-in Intent
                Intent fillInIntent = new Intent();
                Bundle resultsBundle = new Bundle();
                for (RemoteInput ri : action.getRemoteInputs()) {
                    if (ri.getAllowFreeFormInput()) {
                        resultsBundle.putString(ri.getResultKey(), replyText);
                    }
                }
                RemoteInput.addResultsToIntent(action.getRemoteInputs(),
                        fillInIntent, resultsBundle);
                actionIntent.send(mService.getContext(), 0, fillInIntent);

                // Notify Chrome of inline reply
                NotificationsHost host = mService.getHost();
                if (host != null) {
                    host.logInlineReplySent(key);
                }
            } else {
                actionIntent.send();
            }
            Slog.d(TAG, "Fired action " + buttonIndex + " for " + key);
        } catch (PendingIntent.CanceledException e) {
            Slog.w(TAG, "actionIntent canceled for button " + buttonIndex
                    + " of " + key);
        }
    }
}

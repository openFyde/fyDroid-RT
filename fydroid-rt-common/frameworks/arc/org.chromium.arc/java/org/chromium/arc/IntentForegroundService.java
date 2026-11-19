package org.chromium.arc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public abstract class IntentForegroundService extends Service {
    private final String mNotificationChannelId;
    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;

    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            IntentForegroundService.this.onHandleIntent((Intent) message.obj);
            IntentForegroundService.this.stopSelf(message.arg1);
        }
    }

    public IntentForegroundService(String notificationChannelId) {
        this.mNotificationChannelId = notificationChannelId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread(
                "IntentForegroundService[" + this.mNotificationChannelId + "]");
        handlerThread.start();
        this.mServiceLooper = handlerThread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
    }

    @Override
    public void onDestroy() {
        this.mServiceLooper.quit();
    }

    protected abstract void onHandleIntent(Intent intent);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager nm = (NotificationManager) getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(
                mNotificationChannelId, mNotificationChannelId, 0));
        startForeground(1, new Notification.Builder(this, mNotificationChannelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info).build());
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return START_REDELIVER_INTENT;
    }
}
